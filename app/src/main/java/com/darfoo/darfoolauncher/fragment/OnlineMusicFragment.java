package com.darfoo.darfoolauncher.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.support.OnMusicClickListener;
import com.darfoo.darfoolauncher.support.OnVideoClickListener;
import com.darfoo.darfoolauncher.support.Utils;
import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import dafoo.music.OnlineMusicAdapter;
import dafoo.video.OnlineJsonAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnlineMusicFragment extends Fragment {


    public static final String ARG_CONTENT_TYPE = "index";

    public static final String ARG_TITLE_LIST = "title_list";

    public static final String ARG_ARRAY_LIST = "array_list";

    public static final int TYPE_TEACH = 0;

    public static final int TYPE_VIDEO = 1;

    public static final int TYPE_MUSIC = 2;

    private int mContentType;

//    TextView mTitleView;

    private LinearLayout mCategoriesLayout;

    private GridView mGridView;

    private ProgressBar mProgressBar;

    private ArrayList<Integer> titleResIds, arrayResIds;

    private ArrayList<Integer> selectionList;

    private String requestUrl;


    public OnlineMusicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mContentType = bundle.getInt(ARG_CONTENT_TYPE);
            switch (mContentType) {
                case TYPE_TEACH:
                    requestUrl = Utils.BASE_URL + "/resources/tutorial/category/";
                    break;
                case TYPE_VIDEO:
                    requestUrl = Utils.BASE_URL + "/resources/video/category/";
                    break;
                case TYPE_MUSIC:
                    requestUrl = Utils.BASE_URL + "/resources/music/category/";
                    break;
            }
            titleResIds = bundle.getIntegerArrayList(ARG_TITLE_LIST);
            arrayResIds = bundle.getIntegerArrayList(ARG_ARRAY_LIST);
            selectionList = new ArrayList<Integer>(titleResIds.size());
            for (int i = 0; i < titleResIds.size(); i++) {
                selectionList.add(0);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_music, container, false);
//        mTitleView = (TextView) view.findViewById(R.id.textview_content_title);//隐藏分类选项
        mCategoriesLayout = (LinearLayout) view.findViewById(R.id.information_layout);
        mCategoriesLayout.setVisibility(View.INVISIBLE);
        mGridView = (GridView) view.findViewById(R.id.gridview_music);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar_loading);

        if (savedInstanceState == null) {
//            mTitleView.setText(R.string.title_teaching);
            addCategories();
            updateContentList();
            //mGridView.setNumColumns(mContentType == TYPE_MUSIC ? 6 : 4);
        }
        return view;
    }

    private void addCategories() {
        for (int i = 0; i < titleResIds.size(); i++) {
            int titleResId = titleResIds.get(i);
            int arrayResId = arrayResIds.get(i);

            LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity())
                    .inflate(R.layout.content_category_item, null);
            TextView titleView = (TextView) layout.findViewById(R.id.textview_category_title);
            Spinner spinner = (Spinner) layout.findViewById(R.id.spinner_category);
            spinner.setVisibility(View.INVISIBLE);//隐藏分类选项
            titleView.setText(titleResId);
            spinner.setId(i);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter
                    .createFromResource(getActivity(), arrayResId, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new OnCategorySelectListener());
            mCategoriesLayout.addView(layout);
        }
    }

    private class OnCategorySelectListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectionList.set(parent.getId(), position);
            updateContentList();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            parent.setSelection(0);
        }
    }

    private void updateContentList() {
        mGridView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        StringBuilder builder = new StringBuilder();
        builder.append(selectionList.get(0));
        for (int i = 1; i < selectionList.size(); i++) {
            builder.append("-");
            builder.append(selectionList.get(i));
        }
        if (mContentType != TYPE_TEACH) {
            builder.append('-');
            builder.append(0);
        }
        String url = requestUrl + builder.toString();

        Ion.with(this).load(url).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                //int[] colors = new int[]{0x30F2F2F2, 0x30E2E2E2};
                if (e != null) {
                    e.printStackTrace();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mGridView.setVisibility(View.VISIBLE);
                    return;
                }

                Log.i("Base", result.toString());
                /*String str = "[{'id':1,'image_url':'http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg','author_name':'jack','title':'videoone','update_timestamp':140908},"
                        + "{'id':2,'image_url':'http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg','author_name':'jack','title':'videotwo','update_timestamp':140908},"
        				+ "{'id':3,'image_url':'http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg','author_name':'jack','title':'videothree','update_timestamp':140908}]";
                JsonElement _Element=new Gson().fromJson(str, JsonElement.class);
        		result=_Element.getAsJsonArray();*/
                OnlineMusicAdapter _OnlineMusicAdapter = new OnlineMusicAdapter(getActivity(), result,0);
                mGridView.setAdapter(_OnlineMusicAdapter);
               // int colorPos = position%colors.length;
               // mGridView.setBackgroundColor(colors[colorPos]);
                mGridView.setOnItemClickListener(new OnMusicClickListener(getActivity()));
                mProgressBar.setVisibility(View.INVISIBLE);
                mGridView.setVisibility(View.VISIBLE);
            }
        });
    }
}
