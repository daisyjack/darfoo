package com.darfoo.darfoolauncher.activity;

import com.darfoo.darfoolauncher.R;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jewelzqiu on 11/16/14.
 *
 * All Activities should extend this Activity!
 */
public class BaseFragmentActivity extends FragmentActivity {

    private ImageButton mWifiButton;

    private TextView mTimeView;

    private TextView mBatteryView;

    private ImageView mBatteryImageView;

    private StatusChangeReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
                .inflate(R.layout.status_bar, null);

        final ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        mWifiButton = (ImageButton) actionBarLayout.findViewById(R.id.button_wifi);
        mTimeView = (TextView) actionBarLayout.findViewById(R.id.textview_time);
        mBatteryView = (TextView) actionBarLayout.findViewById(R.id.textview_battery);
        mBatteryImageView = (ImageView) actionBarLayout.findViewById(R.id.imageview_battery);

        updateStatusBar();

//        mWifiButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//            }
//        });

        mReceiver = new StatusChangeReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    private void updateStatusBar() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        mTimeView.setText(format.format(new Date()));

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            mWifiButton.setImageResource(R.drawable.ic_signal_wifi_off_black);
        } else {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getSSID() != null && !wifiInfo.getSSID().equals("0x")) {
                mWifiButton.setImageResource(R.drawable.ic_signal_wifi_4_bar_black);
            } else {
                mWifiButton.setImageResource(R.drawable.ic_signal_wifi_not_connected_black);
            }
        }
    }

    private class StatusChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }

            String action = intent.getAction();
            if (action.toLowerCase().contains("battery")) {
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status", -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = rawlevel * 100 / scale;
                }
                StringBuilder builder = new StringBuilder();
//                switch (status) {
//                    case BatteryManager.BATTERY_STATUS_CHARGING:
//                        builder.append(getString(R.string.battery_charging));
//                        break;
//                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
//                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
//                        builder.append(getString(R.string.battery_charging));
//                        break;
//                    case BatteryManager.BATTERY_STATUS_FULL:
//                        builder.append(getString(R.string.battery_charging));
//                        break;
//                    default:
//                        builder.append(getString(R.string.battery_charging));
//                        break;
//                }
                if (level >= 0) {
                    builder.append(level);
                    builder.append('%');
                }
                mBatteryView.setText(level + "%");
//                mBatteryView.setText(builder.toString());

                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    mBatteryImageView.setImageResource(R.drawable.ic_battery_charging);
                } else if (level >= 80) {
                    mBatteryImageView.setImageResource(R.drawable.ic_battery_100);
                } else if (level >= 60) {
                    mBatteryImageView.setImageResource(R.drawable.ic_battery_80);
                } else if (level >= 40) {
                    mBatteryImageView.setImageResource(R.drawable.ic_battery_60);
                } else if (level >= 20) {
                    mBatteryImageView.setImageResource(R.drawable.ic_battery_40);
                } else {
                    mBatteryImageView.setImageResource(R.drawable.ic_battery_20);
                }
            } else if (action.toLowerCase().contains("time")) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                mTimeView.setText(format.format(new Date()));
            } else if (action.toLowerCase().contains("wifi")) {
                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                if (!wifiManager.isWifiEnabled()) {
                    mWifiButton.setImageResource(R.drawable.ic_signal_wifi_off_black);
                } else {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo.getSSID() != null && !wifiInfo.getSSID().equals("0x")) {
                        mWifiButton.setImageResource(R.drawable.ic_signal_wifi_4_bar_black);
                    } else {
                        mWifiButton.setImageResource(R.drawable.ic_signal_wifi_not_connected_black);
                    }
                }
            }
        }
    }
}
