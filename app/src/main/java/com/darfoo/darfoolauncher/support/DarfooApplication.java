package com.darfoo.darfoolauncher.support;

import com.darfoo.darfoolauncher.activity.LockScreenActivity;
import com.darfoo.darfoolauncher.activity.MainActivity;
import com.darfoo.darfoolauncher.activity.SdManagerActivity;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

/**
 * Created by jewelzqiu on 12/9/14.
 */
public class DarfooApplication extends Application {

    private ScreenStatusReceiver mReceiver;
    private SdStatusReceiver _mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver = new ScreenStatusReceiver();
        _mReceiver = new SdStatusReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        registerReceiver(mReceiver, filter);
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter1.addAction(Intent.ACTION_MEDIA_EJECT);
        filter1.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter1.addDataScheme("file");
        registerReceiver(_mReceiver, filter1);
    }

    private class ScreenStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                Intent intent1 = new Intent(DarfooApplication.this, LockScreenActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            } /*else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                startActivity(new Intent(DarfooApplication.this, LockScreenActivity.class));
        }*/
        }
    }


   private class SdStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
           if  (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
                Intent intent2 = new Intent(DarfooApplication.this, SdManagerActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
            }
            else if (Intent.ACTION_MEDIA_REMOVED.equals(intent.getAction()) || Intent.ACTION_MEDIA_EJECT.equals(intent.getAction())) {
               Intent intent3 = new Intent(DarfooApplication.this, MainActivity.class);
               intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent3);
               Toast.makeText(DarfooApplication.this, "SD卡已卸载！！！", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
