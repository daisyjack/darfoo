package com.darfoo.darfoolauncher.activity;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.support.UmengUtils;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends BaseFragmentActivity {

    private ListView mListView;

    private String[] mTitles;

    private int[] mImageRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mListView = (ListView) findViewById(R.id.listview_settings);

        mTitles = getResources().getStringArray(R.array.settings_titles);
        mImageRes = new int[mTitles.length];
        mImageRes[0] = R.drawable.ic_account;
        mImageRes[1] = R.drawable.ic_wifi;
        mImageRes[2] = R.drawable.ic_time;
        mImageRes[3] = R.drawable.ic_display;
        mImageRes[4] = R.drawable.ic_vote;
        mImageRes[5] = R.drawable.ic_hotline;
        mImageRes[6] = R.drawable.ic_help;
        mImageRes[7] = R.drawable.ic_about;
        ArrayList<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < mTitles.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", mImageRes[i]);
            map.put("title", mTitles[i]);
            listData.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, listData, R.layout.list_item_settings,
                new String[]{"image", "title"},
                new int[]{R.id.imageview_settings_item, R.id.textview_settings_item});
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new OnSettingsItemClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengUtils.EVENT_SETTINGS);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengUtils.EVENT_SETTINGS);
        MobclickAgent.onPause(this);
    }

    private class OnSettingsItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, String> map = new HashMap<>(1);
            map.put(UmengUtils.ITEM_TITLE, mTitles[position]);
            MobclickAgent.onEvent(SettingsActivity.this, UmengUtils.EVENT_SETTINGS, map);

            Intent intent = null;
            switch (position) {
                case 0: // account
                    intent = new Intent(SettingsActivity.this, UserLoginFragment.class);
                    break;
                case 1: // wifi
                    intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    break;
                case 2: // time
                    intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                    break;
                case 3: // brightness
                    showDisplaySettings();
                    break;
                case 4: // vote

                    break;
                case 5: // mall
                    intent = new Intent(SettingsActivity.this, SingleImageActivity.class);
                    intent.putExtra(SingleImageActivity.IMG_RES, R.drawable.darfoo_mall);
                    break;
                case 6: // help

                    break;
                case 7: // about
                    intent = new Intent(SettingsActivity.this, SingleImageActivity.class);
                    intent.putExtra(SingleImageActivity.IMG_RES, R.drawable.about_darfoo);
                    break;
            }
            if (intent != null) {
                startActivity(intent);
            }
        }
    }

    private void showDisplaySettings() {
        View view = getLayoutInflater().inflate(R.layout.dialog_display_settings, null);
        Switch switchAuto = (Switch) view.findViewById(R.id.switch_auto_brightness);
        final SeekBar seekBarBrightness = (SeekBar) view.findViewById(R.id.seekbar_brightness);

        boolean isAuto = false;
        try {
            isAuto = Settings.System
                    .getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE)
                    == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        switchAuto.setChecked(isAuto);
        switchAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int mode = isChecked ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC :
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
                Settings.System
                        .putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
                seekBarBrightness.setVisibility(isChecked ? View.INVISIBLE : View.VISIBLE);
                seekBarBrightness.setProgress(getBrightness());
            }
        });
        seekBarBrightness.setVisibility(isAuto ? View.INVISIBLE : View.VISIBLE);
        seekBarBrightness.setProgress(getBrightness());
        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
                Settings.System
                        .putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                getContentResolver().notifyChange(uri, null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.brightness);
        builder.setView(view);
        builder.create().show();
    }

    private int getBrightness() {
        try {
            return Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return 128;
    }
}
