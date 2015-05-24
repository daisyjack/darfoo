package com.darfoo.mvplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.darfoo.mvplayer.VideoPlayerActivity;
import com.darfoo.mvplayer.utils.FileDetail;
import com.darfoo.mvplayer.utils.FileDownloader;

import java.io.File;
import java.util.HashMap;

import dafoo.video.DBOperation;

public class DownloadService extends Service {

    private DBOperation mDBOperation;
    private HashMap<String, AsyncTask<String, Void, Void>> mCheckMap;
    public DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCheckMap = new HashMap<>();
        mDBOperation = new DBOperation(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String currentURL = intent.getStringExtra("URL");
        String downloadPath = intent.getStringExtra("Path");
        Bundle bundle = intent.getBundleExtra("VideoMessage");
        FileDetail detail = (FileDetail)bundle.getSerializable("VideoInfo");
        File file = new File(downloadPath);


        mDBOperation.openDatabase();
        int count = mDBOperation.select(detail.getId()).getCount();


        //暂时使用url作为HashMap的Key,用于检验下载任务的唯一性
        if(count == 0 && !mCheckMap.containsKey(currentURL)) {
            DownloadTask task = new DownloadTask(detail);
            mCheckMap.put(currentURL, task);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentURL, downloadPath, VideoPlayerActivity.VIDEODIR);

        } else if(count == 0 && file.exists() && mCheckMap.containsKey(currentURL)){
            Toast.makeText(getApplicationContext(), detail.getTitle() + "正在下载..", Toast.LENGTH_SHORT).show();
        } else if(count != 0 && file.exists()){

            Toast.makeText(getApplicationContext(), detail.getTitle() + "已下载", Toast.LENGTH_SHORT).show();
        }

        mDBOperation.closeDatabase();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private class DownloadTask extends AsyncTask<String, Void, Void> {

        private FileDetail tempDetail;
        private boolean isDownloadFinish = false;

        DownloadTask(FileDetail detail) {
            tempDetail = detail;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), tempDetail.getTitle() + "开始下载", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            FileDownloader loader = new FileDownloader(params[0], params[1], params[2]);
            if(loader.download() == 0){
                //下载完成后加入数据库
                isDownloadFinish = true;
                mDBOperation.openDatabase();
                if(tempDetail.getTitle() == null) {
                    tempDetail.setTitle("untitle");
                }
                Log.d("download video", tempDetail.getAuthor());
                mDBOperation.insert(tempDetail.getId(), tempDetail.getImageUrl(), tempDetail.getTitle(), tempDetail.getUpdateTimeStamp(), tempDetail.getAuthor(),100);
                mDBOperation.closeDatabase();
                mCheckMap.remove(params[0]);

            } else {
                File file = new File(params[1]);
                if(file.exists()) {
                    file.delete();
                }
                mCheckMap.remove(params[0]);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(isDownloadFinish) {
                Toast.makeText(getApplicationContext(), tempDetail.getTitle() + "下载完成", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getApplicationContext(), tempDetail.getTitle() + "下载失败", Toast.LENGTH_SHORT).show();

            }
        }

    }
}
