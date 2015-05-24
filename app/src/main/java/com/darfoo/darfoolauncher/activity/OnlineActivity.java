package com.darfoo.darfoolauncher.activity;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.fragment.BaseContentFragment;
import com.darfoo.darfoolauncher.fragment.DownloadsFragment;
import com.darfoo.darfoolauncher.fragment.OnlineMusicFragment;
import com.darfoo.darfoolauncher.fragment.RecommendationFragment;
import com.darfoo.darfoolauncher.fragment.SearchFragment;
import com.darfoo.darfoolauncher.fragment.TeamFragment;
import com.darfoo.darfoolauncher.support.UmengUtils;
import com.darfoo.darfoolauncher.support.Utils;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.HashMap;

public class OnlineActivity extends BaseFragmentActivity {

    private static FragmentTabHost mTabHost;

    private String[] mTabTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabTitles = getResources().getStringArray(R.array.main_tab_titles);
        LayoutInflater inflater = getLayoutInflater();
        ImageView tabView;
        TabHost.TabSpec tabSpec;
        Class fragmentClass;
        for (int i = 0; i < mTabTitles.length; i++) {
            Bundle bundle = null;
            ArrayList<Integer> titleResIds, arrayResIds;
            int sourceRes = 0;
            switch (i) {
                case 0: // recommendation
                    fragmentClass = RecommendationFragment.class;
                    sourceRes = R.drawable.ic_tab_recommendation;
                    break;
                case 1: // team
                    fragmentClass = TeamFragment.class;
                    sourceRes = R.drawable.ic_tab_team;
                    break;
                case 2: // teach
                    fragmentClass = BaseContentFragment.class;
                    bundle = new Bundle();
                    titleResIds = new ArrayList<Integer>(3);
                    titleResIds.add(R.string.by_speed);
                    titleResIds.add(R.string.by_level);
                    titleResIds.add(R.string.by_category);

                    arrayResIds = new ArrayList<Integer>(3);
                    arrayResIds.add(R.array.dance_speed);
                    arrayResIds.add(R.array.dance_level);
                    arrayResIds.add(R.array.teaching_category);

                    bundle.putIntegerArrayList(BaseContentFragment.ARG_TITLE_LIST, titleResIds);
                    bundle.putIntegerArrayList(BaseContentFragment.ARG_ARRAY_LIST, arrayResIds);
                    bundle.putInt(BaseContentFragment.ARG_CONTENT_TYPE,
                            BaseContentFragment.TYPE_TEACH);

                    sourceRes = R.drawable.ic_tab_teaching;
                    break;
                case 3: // video
                    fragmentClass = BaseContentFragment.class;
                    bundle = new Bundle();
                    titleResIds = new ArrayList<Integer>(3);
                    titleResIds.add(R.string.by_speed);
                    titleResIds.add(R.string.by_level);
                    titleResIds.add(R.string.by_style);

                    arrayResIds = new ArrayList<Integer>(3);
                    arrayResIds.add(R.array.dance_speed);
                    arrayResIds.add(R.array.dance_level);
                    arrayResIds.add(R.array.video_style);

                    bundle.putIntegerArrayList(BaseContentFragment.ARG_TITLE_LIST, titleResIds);
                    bundle.putIntegerArrayList(BaseContentFragment.ARG_ARRAY_LIST, arrayResIds);
                    bundle.putInt(BaseContentFragment.ARG_CONTENT_TYPE,
                            BaseContentFragment.TYPE_VIDEO);

                    sourceRes = R.drawable.ic_tab_video;
                    break;
                case 4: // music
                    fragmentClass = OnlineMusicFragment.class;
                    bundle = new Bundle();
                    titleResIds = new ArrayList<Integer>(2);
                    titleResIds.add(R.string.by_beat);
                    titleResIds.add(R.string.by_style);

                    arrayResIds = new ArrayList<Integer>(2);
                    arrayResIds.add(R.array.music_beat);
                    arrayResIds.add(R.array.music_style);

                    bundle.putIntegerArrayList(BaseContentFragment.ARG_TITLE_LIST, titleResIds);
                    bundle.putIntegerArrayList(BaseContentFragment.ARG_ARRAY_LIST, arrayResIds);
                    bundle.putInt(BaseContentFragment.ARG_CONTENT_TYPE,
                            BaseContentFragment.TYPE_MUSIC);

                    sourceRes = R.drawable.ic_tab_music;
                    break;
                case 5: // downloads
                    fragmentClass = DownloadsFragment.class;
                    sourceRes = R.drawable.ic_tab_download;
                    break;
                default: // search
                    fragmentClass = SearchFragment.class;
                    sourceRes = R.drawable.ic_tab_search;
                    break;
            }
            String title = mTabTitles[i];
            tabView = (ImageView) inflater.inflate(R.layout.tab_item_online, null);
            tabView.setImageResource(sourceRes);
            tabSpec = mTabHost.newTabSpec(title).setIndicator(tabView);
            mTabHost.addTab(tabSpec, fragmentClass, bundle);
        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                HashMap<String, String> map = new HashMap<>(1);
                map.put(UmengUtils.ITEM_TITLE, mTabTitles[mTabHost.getCurrentTab()]);
                MobclickAgent.onEvent(OnlineActivity.this, UmengUtils.EVENT_ONLINE_TABS, map);
                Utils.sendStatistics(OnlineActivity.this, "tab", mTabHost.getCurrentTab() + 1);
            }
        });

        HashMap<String, String> map = new HashMap<>(1);
        map.put(UmengUtils.ITEM_TITLE, mTabTitles[mTabHost.getCurrentTab()]);
        MobclickAgent.onEvent(OnlineActivity.this, UmengUtils.EVENT_ONLINE_TABS, map);
        Utils.sendStatistics(OnlineActivity.this, "tab", mTabHost.getCurrentTab() + 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengUtils.EVENT_ONLINE_TABS);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengUtils.EVENT_ONLINE_TABS);
        MobclickAgent.onPause(this);
    }
}
