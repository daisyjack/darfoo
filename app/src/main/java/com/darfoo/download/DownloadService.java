package com.darfoo.download;

import com.darfoo.darfoolauncher.support.Cryptor;
import com.darfoo.download.db.DownloadMusicDBOperation;
import com.darfoo.download.db.DownloadVideoDBOperation;
import com.darfoo.download.utils.CommandUtils;
import com.darfoo.download.utils.TaskUtils;
import com.darfoo.mvplayer.MusicPlayerActivity;
import com.darfoo.mvplayer.VideoPlayerActivity;
import com.darfoo.mvplayer.utils.FileDetail;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.net.MalformedURLException;

import dafoo.music.Music;
import dafoo.video.Video;


public class DownloadService extends Service {

	private DownloadManager mDownloadManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mDownloadManager = new DownloadManager(this);
        reloadUncompeleteVideos();
        reloadUncompeleteMusics();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
		String command = intent.getStringExtra(CommandUtils.COMMAND);


		String url;
		String path;
		String filename = "unknown file";
		switch (command) {
		case CommandUtils.START_MANAGER:
			if (!mDownloadManager.isRunning()) {
                mDownloadManager.startManage();
            }
			break;
        case CommandUtils.ADD_FILE:
        	Log.d("DownloadService", "add the task");
            Bundle bundle = intent.getBundleExtra("FileMessage");
            FileDetail detail = (FileDetail)bundle.getSerializable("FileInfo");
        	url = Cryptor.decryptQiniuUrl(intent.getStringExtra("URL"));
			path = intent.getStringExtra("Path");
            if(path.equals(VideoPlayerActivity.VIDEODIR)) {
                filename = detail.getTitle() + "_" + detail.getId();
            } else if(path.equals(MusicPlayerActivity.MUSICDIR)) {
                filename = detail.getTitle() + "_" +detail.getId() + ".mp3";
            }
            if (!TextUtils.isEmpty(url) && !mDownloadManager.hasTask(url)) {
                mDownloadManager.addTask(url, path, filename, detail);
            }
            break;
        case CommandUtils.RESUME_TASK:
            url = Cryptor.decryptQiniuUrl(intent.getStringExtra("URL"));
            Log.d("DownloadService", "resume the task" + url);

            if (!TextUtils.isEmpty(url)) {
                mDownloadManager.continueTask(url);
            }
            break;
        case CommandUtils.DELETE_FILE:
            url = Cryptor.decryptQiniuUrl(intent.getStringExtra("URL"));
        	Log.d("DownloadService", "开始删除任务");
            if (!TextUtils.isEmpty(url)) {
                mDownloadManager.deleteTask(url);
            }
            break;
        case CommandUtils.PAUSE_TASK:
            url = Cryptor.decryptQiniuUrl(intent.getStringExtra("URL"));
            if (!TextUtils.isEmpty(url)) {
                mDownloadManager.pauseTask(url);
            }
            break;
        case CommandUtils.STOP_MANAGER:
            mDownloadManager.close();
            // mDownloadManager = null;
            break;

        default:
            break;
		
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

    private void reloadUncompeleteMusics() {
        DownloadMusicDBOperation downloadMusicDBO = new DownloadMusicDBOperation(this);
        downloadMusicDBO.openDatabase();
        Cursor musicCursor = downloadMusicDBO.selectall();

        while(musicCursor.moveToNext()) {
            /**
             * 在因为关机等不可预知的情况下，无法catch到Exception，此时无法对数据库做出更新，因此在service再次重启时，统一将
             * 原下载中的任务，都设为failed，便于用户重新继续下载
             */
            if(musicCursor.getInt(musicCursor.getColumnIndex(Music.taskFlag)) != TaskUtils.FAILED) {
                downloadMusicDBO.update(musicCursor.getInt(musicCursor.getColumnIndex(Music.id)), TaskUtils.FAILED);
            }
            FileDetail detail = new FileDetail(musicCursor.getInt(musicCursor.getColumnIndex(Music.id)),
                    musicCursor.getString(musicCursor.getColumnIndex(Music.url)),
                    musicCursor.getString(musicCursor.getColumnIndex(Music.title)),
                    musicCursor.getString(musicCursor.getColumnIndex(Music.image_url)),
                    musicCursor.getInt(musicCursor.getColumnIndex(Music.update_timestamp)),
                    musicCursor.getString(musicCursor.getColumnIndex(Music.author)),
                    musicCursor.getLong(musicCursor.getColumnIndex(Music.stop_pos)),
                    -1);

            try {
                DownloadTask task = mDownloadManager.newDownloadTask(Cryptor.decryptQiniuUrl(detail.getUrl()),
                        MusicPlayerActivity.MUSICDIR,
                        detail.getTitle() + "_" + detail.getId() + ".mp3",
                        detail);
                mDownloadManager.addUncompleteTask(task);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
        mDownloadManager.refreshDownloadList(false);
        downloadMusicDBO.closeDatabase();
    }

    private void reloadUncompeleteVideos() {
        DownloadVideoDBOperation downloadVideoDBO = new DownloadVideoDBOperation(this);
        downloadVideoDBO.openDatabase();

        Cursor videoCursor = downloadVideoDBO.selectall();
        while(videoCursor.moveToNext()) {
            /**
             * 在因为关机等不可预知的情况下，无法catch到Exception，此时无法对数据库做出更新，因此在service再次重启时，统一将
             * 原下载中的任务，都设为failed，便于用户重新继续下载
             */
            if(videoCursor.getInt(videoCursor.getColumnIndex(Video.taskFlag)) != TaskUtils.FAILED) {
                downloadVideoDBO.update(videoCursor.getInt(videoCursor.getColumnIndex(Video.id)), TaskUtils.FAILED);
            }
            FileDetail detail = new FileDetail(videoCursor.getInt(videoCursor.getColumnIndex(Video.id)),
                    videoCursor.getString(videoCursor.getColumnIndex(Video.url)),
                    videoCursor.getString(videoCursor.getColumnIndex(Video.title)),
                    videoCursor.getString(videoCursor.getColumnIndex(Video.image_url)),
                    videoCursor.getInt(videoCursor.getColumnIndex(Video.update_timestamp)),
                    videoCursor.getString(videoCursor.getColumnIndex(Video.author)),
                    videoCursor.getInt(videoCursor.getColumnIndex(Video.type)));

            try {
                DownloadTask task = mDownloadManager.newDownloadTask(Cryptor.decryptQiniuUrl(detail.getUrl()),
                        VideoPlayerActivity.VIDEODIR,
                        detail.getTitle() + "_" + detail.getId(),
                        detail);
                mDownloadManager.addUncompleteTask(task);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
        mDownloadManager.refreshDownloadList(true);

        downloadVideoDBO.closeDatabase();

    }
	
}
