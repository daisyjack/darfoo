package dafoo.video;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.darfoo.darfoolauncher.support.QiniuUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/1/21 0021.
 */
public class Upload extends Service {

    private DBOperation mDBOperation;
    private static final String TAG = "MyService";
    public static final String VIDEODIR = Environment.getExternalStorageDirectory().getPath()
            + File.separator + "Darfoo" + File.separator + "video" + File.separator;
    public Upload() {
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    public void onCreate() {
        super.onCreate();
        mDBOperation = new DBOperation(this);
        System.out.println("service");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        mDBOperation.openDatabase();
        Cursor mCursor = mDBOperation.selectall();
        while (mCursor.moveToNext()){
            if(Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("id")))<0){
                String mId = mCursor.getString(mCursor.getColumnIndex("id"));
                String title = mCursor.getString(mCursor.getColumnIndex("title"));
                int type =  mCursor.getInt(mCursor.getColumnIndex("type"));
                int epoch =  mCursor.getInt(mCursor.getColumnIndex("update_timestamp"));
                String houzhui="";
                if(type==1)houzhui=".mp4";
                else if(type==2)houzhui=".flv";
                String mPath =  VIDEODIR + title + "_" +  mId + houzhui;
                UploadTask task = new UploadTask(mPath);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,houzhui,title,epoch+"",mPath );
            }
        };
        mDBOperation.closeDatabase();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private class UploadTask extends AsyncTask<String, Void, Void> {

        private String temppath;


        UploadTask(String path) {
            temppath = path;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(),  "开始上传", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            QiniuUtils.uploadResource(getApplicationContext(),params[0],params[1],Integer.parseInt(params[2]),params[3]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(Upload.this, "上传完成", Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
        }

    }
}
