package com.darfoo.darfoolauncher.fragment;


import com.darfoo.darfoolauncher.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import dafoo.music.LocalMusicFragment;
import dafoo.video.LocalVideoFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadsFragment extends Fragment {


    public DownloadsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_downloads, container, false);
        FragmentTabHost mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        String[] mTabTitles = getResources().getStringArray(R.array.downloads_tab_titles);
        TextView tabView;
        TabHost.TabSpec tabSpec;
        Class fragmentClass;
        for (int i = 0; i < mTabTitles.length; i++) {
            Bundle bundle = null;
            int backgroundRes = 0;
            switch (i) {
                case 0:
                    fragmentClass = LocalVideoFragment.class;
                    backgroundRes = R.drawable.bg_top_tab_video;
                    break;
                case 1:
                	fragmentClass = LocalMusicFragment.class;
                    backgroundRes = R.drawable.bg_top_tab_music;
                    break;
                default:
                    fragmentClass = BaseContentFragment.class;
            }
            String title = mTabTitles[i];
            tabView = (TextView) inflater.inflate(R.layout.tab_item_downloads, null);
            tabView.setBackgroundResource(backgroundRes);
            tabSpec = mTabHost.newTabSpec(title).setIndicator(tabView);
            mTabHost.addTab(tabSpec, fragmentClass, bundle);
        }
        return view;
    }


}
