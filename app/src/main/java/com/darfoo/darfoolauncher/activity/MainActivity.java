package com.darfoo.darfoolauncher.activity;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.support.UmengUtils;
import com.darfoo.darfoolauncher.support.Utils;
import com.darfoo.mvplayer.MusicPlayerActivity;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;

import dafoo.music.MusicDBOp;
import dafoo.video.LocalVideo;


public class MainActivity extends BaseFragmentActivity {

    private static final int TYPE_NORMAL = 0;

    private static final int TYPE_CONNECT = 1;

    private static final int TYPE_ENTERTAIN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new MainAdapter(TYPE_NORMAL));

        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.setSessionContinueMillis(10000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengUtils.EVENT_MAIN);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengUtils.EVENT_MAIN);
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {

    }

    private class MainAdapter extends BaseAdapter {

        private int mType;

        private String[] mTitles;

        private int[] imageRes;

        public MainAdapter(int type) {
            mType = type;
            switch (type) {
                case TYPE_NORMAL:
                    mTitles = getResources().getStringArray(R.array.category_titles);
                    imageRes = new int[mTitles.length];
                    imageRes[0] = R.drawable.bg_main_internet;
                    imageRes[1] = R.drawable.bg_main_video;
                    imageRes[2] = R.drawable.bg_main_music;
                    imageRes[3] = R.drawable.bg_main_browser;
                    imageRes[4] = R.drawable.bg_main_settings;
                    imageRes[5] = R.drawable.bg_main_entertainment;
                    break;
                case TYPE_CONNECT:
                    mTitles = getResources().getStringArray(R.array.folder_connection_titles);
                    imageRes = new int[mTitles.length];
                    imageRes[0] = R.drawable.bg_folder_wifi;
                    imageRes[1] = R.drawable.bg_connection_instruction;
                    imageRes[2] = R.drawable.bg_folder_browser;
                    break;
                case TYPE_ENTERTAIN:
                    mTitles = getResources().getStringArray(R.array.folder_entertain_titles);
                    imageRes = new int[mTitles.length];
                    imageRes[0] = R.drawable.bg_tv_series;
                    imageRes[1] = R.drawable.bg_game;
                    break;
            }
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.grid_item_main, parent, false);
                holder = new ViewHolder();
                holder.button = (ImageButton) convertView.findViewById(R.id.imagebutton_category);
                holder.textView = (TextView) convertView.findViewById(R.id.textview_category);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.button.setImageResource(imageRes[position]);
            holder.textView.setText(mTitles[position]);
            holder.button.setOnClickListener(
                    new OnMainClickListener(mType, position, mTitles[position]));
            return convertView;
        }

        class ViewHolder {

            ImageButton button;

            TextView textView;
        }
    }

    private class OnMainClickListener implements View.OnClickListener {

        private int mType;

        private int mPosition;

        private String clickedTitle;

        public OnMainClickListener(int type, int position, String title) {
            mType = type;
            mPosition = position;
            clickedTitle = title;
        }

        @Override
        public void onClick(View v) {
            switch (mType) {
                case TYPE_NORMAL:
                    onMainItemClick(mPosition);
                    break;
                case TYPE_CONNECT:
                    onConnectItemClick(mPosition);
                    break;
                case TYPE_ENTERTAIN:
                    onEntertainItemClick(mPosition);
                    break;
            }

            HashMap<String, String> map = new HashMap<>(1);
            map.put(UmengUtils.ITEM_TITLE, clickedTitle);
            MobclickAgent.onEvent(MainActivity.this, UmengUtils.EVENT_MAIN, map);
        }
    }

    private void onMainItemClick(int position) {
        Intent intent = null;
        switch (position) {
            case 0: // internet
                intent = new Intent(MainActivity.this, OnlineActivity.class);
                break;
            case 1: // video
                intent = new Intent(MainActivity.this, LocalVideo.class);
                break;
            case 2: // music
                MusicDBOp mDBOperation = new MusicDBOp(MainActivity.this);
                mDBOperation.openDatabase();
                Cursor mCursor = mDBOperation.selectall();

                mCursor.moveToFirst();
                Bundle bundle = new Bundle();
                if (mCursor.getCount() != 0) {

                    bundle.putInt("status", 3);
                    bundle.putInt("id", mCursor.getInt(mCursor.getColumnIndex("id")));
                    bundle.putString("title",
                            mCursor.getString(mCursor.getColumnIndex("title")));

                } else {
                    bundle.putInt("status", -1);
                }
                intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
                intent.putExtra("message", bundle);
                mDBOperation.closeDatabase();

                break;
            case 3: // browser
                showConnectionDialog();
                break;
            case 4: // settings
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                break;
            case 5: // entertainment
                showEntertainDialog();
                break;
        }
        if (intent != null) {
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        Utils.sendStatistics(MainActivity.this, "menu", position + 1);
    }

    private void onConnectItemClick(int position) {
        Intent intent = null;
        switch (position) {
            case 0: // wifi connection
                intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                break;
            case 1: // connection instruction
                intent = new Intent(MainActivity.this, SingleImageActivity.class);
                break;
            case 2: // browser
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.darfoo.com"));
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    private void onEntertainItemClick(int position) {
        Intent intent = null;
        switch (position) {
            case 0: // tv series

                break;
            case 1: // game
                intent = new Intent(getString(R.string.game_action));
                break;
        }
        if (intent != null && intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showConnectionDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_folder, null);
        GridView gridView = (GridView) view.findViewById(R.id.gridview_folder);
        gridView.setAdapter(new MainAdapter(TYPE_CONNECT));
        gridView.setNumColumns(3);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow()
                .setLayout(getResources().getDimensionPixelSize(R.dimen.dialog_connection_width),
                        getResources().getDimensionPixelSize(R.dimen.dialog_height));
    }

    private void showEntertainDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_folder, null);
        GridView gridView = (GridView) view.findViewById(R.id.gridview_folder);
        gridView.setAdapter(new MainAdapter(TYPE_ENTERTAIN));
        gridView.setNumColumns(2);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow()
                .setLayout(getResources().getDimensionPixelSize(R.dimen.dialog_enterntain_width),
                        getResources().getDimensionPixelSize(R.dimen.dialog_height));
    }
}
