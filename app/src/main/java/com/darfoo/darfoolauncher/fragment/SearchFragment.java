package com.darfoo.darfoolauncher.fragment;


import com.google.gson.JsonArray;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.support.OnMusicClickListener;
import com.darfoo.darfoolauncher.support.OnVideoClickListener;
import com.darfoo.darfoolauncher.support.Utils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import dafoo.music.OnlineMusicAdapter;
import dafoo.video.OnlineJsonAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private static final int TYPE_VIDEO = 0;

    private static final int TYPE_MUSIC = 1;

    private TabHost mTabHost;

    private String[] mTabTitles;

    private GridView mGridView;

    private ProgressBar mProgressBar;

    private EditText mSearchText;

    private ImageButton mSearchButton;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        mGridView = (GridView) view.findViewById(R.id.gridview_search);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mSearchText = (EditText) view.findViewById(R.id.edittext_search);
        mSearchButton = (ImageButton) view.findViewById(R.id.button_search);

        mTabHost.setup();
        mTabTitles = getResources().getStringArray(R.array.downloads_tab_titles);
        TextView tabView;
        TabHost.TabSpec tabSpec;
        for (int i = 0; i < mTabTitles.length; i++) {
            String title = mTabTitles[i];
            int backgroundRes = 0;
            switch (i) {
                case 0:
                    backgroundRes = R.drawable.bg_top_tab_video;
                    break;
                case 1:
                    backgroundRes = R.drawable.bg_top_tab_music;
                    break;
            }
            tabView = (TextView) inflater.inflate(R.layout.tab_item_downloads, null);
            tabView.setBackgroundResource(backgroundRes);
            tabSpec = mTabHost.newTabSpec(title).setIndicator(tabView)
                    .setContent(R.id.gridview_search);
            mTabHost.addTab(tabSpec);
        }
        mSearchButton.setOnClickListener(new OnSearchPressListener());

        return view;
    }

    private class OnSearchPressListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Editable editable = mSearchText.getText();
            if (editable == null || editable.length() == 0) {
                Toast.makeText(getActivity(), "请输入搜索关键词", Toast.LENGTH_LONG).show();
                return;
            }

            mGridView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

            String requestURL;
            try {
                requestURL = Utils.BASE_URL + "/resources/" +
                        (mTabHost.getCurrentTab() == TYPE_VIDEO ? "video" : "music") +
                        "/search/"+ URLEncoder.encode(editable.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }
            final int mContentType = mTabHost.getCurrentTab();
            Ion.with(getActivity()).load(requestURL).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
                @Override
                public void onCompleted(Exception e, JsonArray result) {

                    if (e != null) {
                        e.printStackTrace();
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mGridView.removeAllViewsInLayout();
                        mGridView.setVisibility(View.VISIBLE);
                        return;
                    }

                    if(result.size() == 0) {
                        ImageView imageView = new ImageView(getActivity());
                        imageView.setImageResource(R.drawable.search_result_empty);
                        Toast toast = new Toast(getActivity());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(imageView);
                        toast.show();
                    }

                    if (mContentType == TYPE_MUSIC) {
                        OnlineMusicAdapter _OnlineMusicAdapter = new OnlineMusicAdapter(
                                getActivity(), result,1);
                        mGridView.setNumColumns(2);
                        mGridView.setVerticalSpacing(0);
                        mGridView.setAdapter(_OnlineMusicAdapter);
                        mGridView.setOnItemClickListener(new OnMusicClickListener(getActivity()));
                    } else {
                        OnlineJsonAdapter _OnlineJsonAdapter = new OnlineJsonAdapter(getActivity(),
                                result);
                        mGridView.setNumColumns(4);
                        mGridView.setAdapter(_OnlineJsonAdapter);
                        mGridView.setOnItemClickListener(new OnVideoClickListener(getActivity()));
                    }
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mGridView.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
