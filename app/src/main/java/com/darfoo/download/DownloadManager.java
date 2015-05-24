
package com.darfoo.download;


import com.darfoo.darfoolauncher.R;
import com.darfoo.download.db.DownloadMusicDBOperation;
import com.darfoo.download.db.DownloadVideoDBOperation;
import com.darfoo.download.error.FileAlreadyExistException;
import com.darfoo.download.utils.StorageUtils;
import com.darfoo.download.utils.TaskUtils;
import com.darfoo.mvplayer.VideoPlayerActivity;
import com.darfoo.mvplayer.utils.FileDetail;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dafoo.music.MusicDBOp;
import dafoo.video.DBOperation;


public class DownloadManager extends Thread {

    private static final int MAX_TASK_COUNT = 100;
    private static final int MAX_DOWNLOAD_THREAD_COUNT = 3;

    private Context mContext;

    private TaskQueue mTaskQueue;
    private List<DownloadTask> mDownloadingTasks;
    private List<DownloadTask> mPausingTasks;

    private Boolean isRunning = false;

    public DownloadManager(Context context) {

        mContext = context;
        mTaskQueue = new TaskQueue();
        mDownloadingTasks = new ArrayList<DownloadTask>();
        mPausingTasks = new ArrayList<DownloadTask>();
    }

    public void startManage() {

        isRunning = true;
        this.start();
    }

    public void close() {

        isRunning = false;
        pauseAllTask();
        this.stop();
    }

    public boolean isRunning() {

        return isRunning;
    }

    @Override
    public void run() {

        super.run();
        while (isRunning) {
            DownloadTask task = mTaskQueue.poll();
            mDownloadingTasks.add(task);
            if(task.isVideoFile()) {
                DownloadVideoDBOperation downloadVideoDB = new DownloadVideoDBOperation(mContext);
                downloadVideoDB.openDatabase();
                downloadVideoDB.update(task.getDetail().getId(), TaskUtils.DOWNLOADING);
                downloadVideoDB.closeDatabase();
            } else {
                DownloadMusicDBOperation downloadMusicDBO = new DownloadMusicDBOperation(mContext);
                downloadMusicDBO.openDatabase();
                downloadMusicDBO.update(task.getDetail().getId(), TaskUtils.DOWNLOADING);
                downloadMusicDBO.closeDatabase();
            }
            refreshDownloadList(task.isVideoFile());
            task.execute();
        }
    }

    public void addTask(String url, String path, String filename, FileDetail detail) {

        if (!StorageUtils.isSDCardPresent()) {
            Toast.makeText(mContext, "未发现SD卡", Toast.LENGTH_LONG).show();
            return;
        }

        if (!StorageUtils.isSdCardWrittenable()) {
            Toast.makeText(mContext, "SD卡不能读写", Toast.LENGTH_LONG).show();
            return;
        }

        if (getTotalTaskCount() >= MAX_TASK_COUNT) {
            Toast.makeText(mContext, "任务列表已满", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            addTask(newDownloadTask(url, path, filename, detail));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void addTask(DownloadTask task) {

        mTaskQueue.offer(task);
        if(task.isVideoFile()) {
            DownloadVideoDBOperation downloadVideoDB = new DownloadVideoDBOperation(mContext);
            DBOperation dBOperation = new DBOperation(mContext);
            downloadVideoDB.openDatabase();
            dBOperation.openDatabase();
            FileDetail detail = task.getDetail();
            if (downloadVideoDB.select(detail.getId()).getCount() == 0 &&
                    dBOperation.select(detail.getId()).getCount() == 0) {
                downloadVideoDB.insert(detail.getId(), detail.getUrl(), detail.getImageUrl(),
                        detail.getTitle(), detail.getUpdateTimeStamp(), detail.getAuthor(), 100, TaskUtils.WAITING);

                    Toast.makeText(mContext, task.getDetail().getTitle() + "开始下载", Toast.LENGTH_SHORT).show();


            }
            dBOperation.closeDatabase();
            downloadVideoDB.closeDatabase();
        } else {

            DownloadMusicDBOperation downloadMusicDB = new DownloadMusicDBOperation(mContext);
            MusicDBOp musicDBOp = new MusicDBOp(mContext);
            downloadMusicDB.openDatabase();
            musicDBOp.openDatabase();
            FileDetail detail = task.getDetail();
            if (downloadMusicDB.select(detail.getId()).getCount() == 0 &&
                    musicDBOp.select(detail.getId()).getCount() == 0) {
                downloadMusicDB.insert(detail.getId(), detail.getUrl(), detail.getImageUrl(), detail.getTitle(),
                        detail.getUpdateTimeStamp(), detail.getAuthor(), 0, detail.getDuration(), TaskUtils.WAITING);

                Toast.makeText(mContext, task.getDetail().getTitle() + "开始下载", Toast.LENGTH_SHORT).show();

            }
            downloadMusicDB.closeDatabase();
            musicDBOp.closeDatabase();
        }


        if (!this.isAlive()) {
            this.startManage();
        }
    }



    public boolean hasTask(String url) {

        if (!this.isAlive()) {
            this.startManage();
        }

        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task.getUrl().equals(url)) {
            	Log.d("DownloadManager", "下载中列表，任务已经存在" + task.getFilename());
                Toast.makeText(mContext, task.getDetail().getTitle() + "正在下载中哦~", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        for (int i = 0; i < mPausingTasks.size(); i++) {
            task = mPausingTasks.get(i);
            if (task.getUrl().equals(url)) {
                Log.d("DownloadManager", "下载暂停列表，任务已经存在" + task.getFilename());
                Toast.makeText(mContext, task.getDetail().getTitle() + "继续下载", Toast.LENGTH_SHORT).show();
                //为了实现加入任务之后，自动开始下载
                mTaskQueue.offer(task);
                mPausingTasks.remove(task);
                return true;
            }
        }

        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
            if (task.getUrl().equals(url)) {
                Log.d("DownloadManager", "下载队列，任务已经存在" + task.getFilename());
                Toast.makeText(mContext, task.getDetail().getTitle() + "正在下载中哦~", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    public DownloadTask getTask(int position) {

        if (position >= mDownloadingTasks.size()) {
            return mTaskQueue.get(position - mDownloadingTasks.size());
        } else {
            return mDownloadingTasks.get(position);
        }
    }

    public int getQueueTaskCount() {

        return mTaskQueue.size();
    }

    public int getDownloadingTaskCount() {

        return mDownloadingTasks.size();
    }

    public int getPausingTaskCount() {

        return mPausingTasks.size();
    }

    public int getTotalTaskCount() {

        return getQueueTaskCount() + getDownloadingTaskCount() + getPausingTaskCount();
    }


    public void addUncompleteTask(DownloadTask task) {
        mPausingTasks.add(task);
    }
    public synchronized void pauseTask(String url) {

        DownloadTask task;
    	Log.d("DownloadManager", mDownloadingTasks.size() + "个任务暂停，当前文件" + url);

        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task != null && task.getUrl().equals(url)) {
                pauseTask(task);
            }
        }
    }

    public synchronized void pauseAllTask() {

        DownloadTask task;

        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
            mTaskQueue.remove(task);
            mPausingTasks.add(task);
        }

        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task != null) {
                pauseTask(task);
            }
        }
    }

    public synchronized void deleteTask(String url) {

    	Log.d("DownloadManager", mDownloadingTasks.size() + "当前删除文件" + url);
        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);

            if (task != null && task.getUrl().equals(url)) {
                File file = new File(task.getPath(), task.getFilename());
                if (file.exists()) {
                    file.delete();
                    Log.d("DownloadManager", "删除文件" + file.getPath());
                }
                
                file = new File(task.getPath(), task.getFilename() + DownloadTask.TEMP_SUFFIX);
                if (file.exists()) {
                    file.delete();
                    Log.d("DownloadManager", "删除临时文件" + file.getPath());

                }
                
                task.onCancelled();
                completeTask(task);
                return;
            }
        }
        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
            if (task != null && task.getUrl().equals(url)) {
                mTaskQueue.remove(task);
            }
        }
        for (int i = 0; i < mPausingTasks.size(); i++) {
            task = mPausingTasks.get(i);
            if (task != null && task.getUrl().equals(url)) {
                mPausingTasks.remove(task);
                File file = new File(task.getPath(), task.getFilename());
                if (file.exists()) {
                    file.delete();
                    Log.d("DownloadManager", "删除文件" + file.getPath());
                }
                
                file = new File(task.getPath(), task.getFilename() + DownloadTask.TEMP_SUFFIX);
                if (file.exists()) {
                    file.delete();
                    Log.d("DownloadManager", "删除临时文件" + file.getPath());

                }
            }
        }
    }

    public synchronized void continueTask(String url) {

        DownloadTask task;
        for (int i = 0; i < mPausingTasks.size(); i++) {
            task = mPausingTasks.get(i);
            if (task != null && task.getUrl().equals(url)) {
                continueTask(task);
            }

        }
    }

    public synchronized void pauseTask(DownloadTask task) {

        if (task != null) {
            task.onCancelled();

            // move to pausing list
            String url = task.getUrl();
            String path = task.getPath();
            String filename = task.getFilename();
            FileDetail detail = task.getDetail();
            try {
                mDownloadingTasks.remove(task);
                task = newDownloadTask(url, path, filename, detail);
                mPausingTasks.add(task);
                if(task.isVideoFile()) {
                    DownloadVideoDBOperation downloadVideoDB = new DownloadVideoDBOperation(mContext);
                    downloadVideoDB.openDatabase();
                    downloadVideoDB.update(task.getDetail().getId(), TaskUtils.FAILED);
                    downloadVideoDB.closeDatabase();
                } else {
                    DownloadMusicDBOperation downloadMusicDBO = new DownloadMusicDBOperation(mContext);
                    downloadMusicDBO.openDatabase();
                    downloadMusicDBO.update(task.getDetail().getId(), TaskUtils.FAILED);
                    downloadMusicDBO.closeDatabase();
                }

                refreshDownloadList(task.isVideoFile());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }

    public synchronized void continueTask(DownloadTask task) {

        if (task != null) {
            mPausingTasks.remove(task);
            mTaskQueue.offer(task);
            if(task.isVideoFile()) {
                DownloadVideoDBOperation downloadVideoDB = new DownloadVideoDBOperation(mContext);
                downloadVideoDB.openDatabase();
                downloadVideoDB.update(task.getDetail().getId(), TaskUtils.WAITING);
                downloadVideoDB.closeDatabase();
            } else {
                DownloadMusicDBOperation downloadMusicDBO = new DownloadMusicDBOperation(mContext);
                downloadMusicDBO.openDatabase();
                downloadMusicDBO.update(task.getDetail().getId(), TaskUtils.WAITING);
                downloadMusicDBO.closeDatabase();
            }

            Toast.makeText(mContext, task.getDetail().getTitle() + "继续下载", Toast.LENGTH_SHORT).show();
            refreshDownloadList(task.isVideoFile());

        }

        if (!this.isAlive()) {
            this.startManage();
        }
    }

    public synchronized void completeTask(DownloadTask task) {

        if (mDownloadingTasks.contains(task)) {
            mDownloadingTasks.remove(task);

            Log.d("DownloadManager", "排除下载中列表的任务");

        }
    }


    public void refreshDownloadList(boolean isVideo) {
        Intent intent = new Intent();
        if(isVideo) {
            intent.setAction(mContext.getString(R.string.download_video_action));
        } else {
            intent.setAction(mContext.getString(R.string.download_music_action));
        }
        mContext.sendBroadcast(intent);
    }
    /**
     * Create a new download task with default config
     * 
     * @param url
     * @return
     * @throws java.net.MalformedURLException
     */
    public DownloadTask newDownloadTask(String url, String path, String filename, final FileDetail detail) throws MalformedURLException {

        DownloadTaskListener taskListener = new DownloadTaskListener() {

            @Override
            public void updateProcess(DownloadTask task) {


            }

            @Override
            public void preDownload(DownloadTask task) {

            }

            @Override
            public void finishDownload(DownloadTask task) {

            	Log.d("DownloadManager", "完成下载任务，进行队列的处理");
                completeTask(task);
                Log.d("DownloadManager", "完成下载任务，插入数据库");
                Intent intent = new Intent();
                if(task.getPath().equals(VideoPlayerActivity.VIDEODIR)) {
                    DBOperation dbOperation = new DBOperation(mContext);
                    dbOperation.openDatabase();

                    FileDetail tempDetail = task.getDetail();
                    int count = dbOperation.select(tempDetail.getId()).getCount();

                    if(count == 0) {
                        dbOperation.insert(tempDetail.getId(), tempDetail.getImageUrl(), tempDetail.getTitle(), tempDetail.getUpdateTimeStamp(), tempDetail.getAuthor(),100);
                    }

                    dbOperation.closeDatabase();

                    //删除下载中的任务记录
                    DownloadVideoDBOperation downloadVideoDB = new DownloadVideoDBOperation(mContext);
                    downloadVideoDB.openDatabase();
                    downloadVideoDB.delete(detail.getId());
                    downloadVideoDB.closeDatabase();

                    intent.setAction(mContext.getString(R.string.download_video_action));

                } else {
                    MusicDBOp musicDBOp = new MusicDBOp(mContext);
                    musicDBOp.openDatabase();
                    FileDetail tempDetail = task.getDetail();
                    int count = musicDBOp.select(tempDetail.getId()).getCount();
                    if(count == 0) {
                        musicDBOp.insert(tempDetail.getId(), tempDetail.getImageUrl(),
                                tempDetail.getTitle(), tempDetail.getUpdateTimeStamp(), tempDetail.getAuthor(),
                                0, tempDetail.getDuration());
                    }
                    musicDBOp.closeDatabase();

                    //删除下载中的任务记录
                    DownloadMusicDBOperation downloadMusicDB = new DownloadMusicDBOperation(mContext);
                    downloadMusicDB.openDatabase();
                    downloadMusicDB.delete(detail.getId());
                    downloadMusicDB.closeDatabase();

                    intent.setAction(mContext.getString(R.string.download_music_action));

                }

                mContext.sendBroadcast(intent);
                Toast.makeText(mContext, task.getDetail().getTitle() + "下载完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void errorDownload(DownloadTask task, Throwable error) {

                if (error != null) {
                	if(error instanceof FileAlreadyExistException) {
                        finishDownload(task);
                	} else {
                        pauseTask(task);

                    }


                    if(error instanceof SocketException) {
                        Toast.makeText(mContext, "网络不佳，请重新下载。", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }

                	Log.d("DownloadManager", "下载出错 " + error.getMessage());
                } else {
                    pauseTask(task);
                }


            }
        };
        return new DownloadTask(mContext, url, path, filename, detail, taskListener);
    }

    /**
     * 下载任务队列，控制同时下载任务数
     */
    private class TaskQueue {
        private Queue<DownloadTask> taskQueue;

        public TaskQueue() {

            taskQueue = new LinkedList<DownloadTask>();
        }

        public void offer(DownloadTask task) {

            taskQueue.offer(task);
        }

        public DownloadTask poll() {

            DownloadTask task = null;
            while (mDownloadingTasks.size() >= MAX_DOWNLOAD_THREAD_COUNT
                    || (task = taskQueue.poll()) == null) {
                try {
                    Thread.sleep(1000); // sleep
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return task;
        }

        public DownloadTask get(int position) {

            if (position >= size()) {
                return null;
            }
            return ((LinkedList<DownloadTask>) taskQueue).get(position);
        }

        public int size() {

            return taskQueue.size();
        }

        @SuppressWarnings("unused")
        public boolean remove(int position) {

            return taskQueue.remove(get(position));
        }

        public boolean remove(DownloadTask task) {

            return taskQueue.remove(task);
        }
    }

}
