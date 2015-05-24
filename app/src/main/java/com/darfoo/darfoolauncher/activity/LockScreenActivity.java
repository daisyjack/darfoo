package com.darfoo.darfoolauncher.activity;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.support.Utils;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

public class LockScreenActivity extends BaseFragmentActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new ViewPagerAdapter());
        mViewPager.setCurrentItem(1);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    SharedPreferences preferences = PreferenceManager
                            .getDefaultSharedPreferences(LockScreenActivity.this);
                    boolean needUpdate = preferences.getBoolean(Utils.NEED_UPDATE, false);
                    if (needUpdate) {
                        int version = preferences.getInt(Utils.NEW_VERSION, -1);
                        if (version == -1) {
                            return;
                        }
                        String filePath = getExternalCacheDir() + "/" + version + ".apk";
                        File file = new File(filePath);
                        if (!file.exists()) {
                            return;
                        }
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + filePath),
                                "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    finish();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Utils.checkAppUpdate(this);
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView textView = (TextView) LayoutInflater.from(LockScreenActivity.this)
                    .inflate(R.layout.fragment_lock, null);
            if (position == 1) {
                textView.setText(R.string.slide_to_unlock);
            }
            container.addView(textView, 0);
            return textView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
