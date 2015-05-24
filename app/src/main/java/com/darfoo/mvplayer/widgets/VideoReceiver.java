package com.darfoo.mvplayer.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.darfoo.mvplayer.VideoPlayerActivity;

/**
 * 接收来自videoview的广播，对播放器UI进行控制
 * Created by YuXiaofei on 2014/11/24.
 */
public class VideoReceiver extends BroadcastReceiver {

    private Context mContext;
    private VideoPlayerActivity act;
    private boolean listIsGone = false;
    private boolean isDownloading = false;
    private String mActionStr;

    public VideoReceiver(Context context) {
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        mActionStr = intent.getAction();
        act = (VideoPlayerActivity)context;

        switch(mActionStr) {

            case "full_screen":
//                Toast.makeText(context, "全屏播放", Toast.LENGTH_SHORT).show();
                /*if(!listIsGone) {
                   act.convertToFullscreen();
                    listIsGone = true;
                } else {
                    act.cancelFromFullscreen();
                    listIsGone = false;
                }*/
                break;

            case "download_file":
                /*Intent serviceIntent = new Intent(context, DownloadService.class);
                String currentURL = act.mVideoPath;
                String downloadPath = act.mDownloadPath;
                Bundle bundle = new Bundle();
                bundle.putSerializable("VideoInfo", act.detail);

                serviceIntent.putExtra("URL", currentURL);
                serviceIntent.putExtra("Path", downloadPath);
                serviceIntent.putExtra("VideoMessage", bundle);



                context.startService(serviceIntent);*/
               /* String currentURL = act.mVideoPath;
                String downloadPath = act.mDownloadPath;
                if(act.isOnlineVideo) {
                    act.checkLocalVideo();
                }

                HashMap<String, AsyncTask<String, Void, Void>> mCheckMap = act.mCheckMap;
                //暂时使用url作为HashMap的Key
                if(mCheckMap.get(currentURL) == null && !act.isDownFinished) {
                    DownloadTask task = new DownloadTask(act.detail);
                    mCheckMap.put(currentURL, task);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentURL, downloadPath, VideoPlayerActivity.VIDEODIR);
                    isDownloading = true;
                } else if(isDownloading){
                    Toast.makeText(mContext, act.mTitle.getText() + "正在下载..", Toast.LENGTH_SHORT).show();
                } else if(!isDownloading && act.isDownFinished){

                    Toast.makeText(mContext, act.mTitle.getText() + "已下载", Toast.LENGTH_SHORT).show();
                }*/
                break;

            case "show_error":
//                act.showPlayErrorMessage();
                break;

            case "share_video":

                break;

            default:
                break;

        }

    }



}

