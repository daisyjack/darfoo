package com.darfoo.download.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import dafoo.video.Video;

public class DownloadVideoDBOperation {

    private Context mContext;
    private SQLiteDatabase mSqLiteDatabase;
    private String mDBName = "dafoo.db";
    private String mLocalVideoTable = "downloading_video";
    private static long VIDEO_NUM;

    public DownloadVideoDBOperation(Context context) {
        mContext = context;
    }

    /**
     * 连接或创建数据库
     * taskFlag: -1 下载失败 0 下载中 1 等待下载
     */
    public void openDatabase() {

        mSqLiteDatabase = mContext.openOrCreateDatabase(mDBName,
                Context.MODE_PRIVATE, null);

        if (mSqLiteDatabase != null) {

            try {
                String sql = "CREATE TABLE IF NOT EXISTS " + mLocalVideoTable
                        + "( "
                        + Video._id + " int PRIMARY_KEY,"
                        + Video.id + " int,"
                        + Video.url + " varchar(100),"
                        + Video.image_url + " varchar(100),"
                        + Video.title + " varchar(50),"
                        + Video.update_timestamp + " int,"
                        + Video.type+ " int,"
                        + Video.author + " varchar(100),"
                        + Video.taskFlag + " int"
                        + ")";
                mSqLiteDatabase.execSQL(sql);

                Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + mLocalVideoTable, null);
                cursor.moveToFirst();
                VIDEO_NUM = cursor.getCount();
                cursor.close();
            } catch (Exception e) {
                Toast.makeText(mContext, "视频获取失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 断开数据库
     */
    public void closeDatabase() {
        mSqLiteDatabase.close();
    }

    /**
     * 按key升序查出所有数据
     * @return
     */
    public Cursor selectall() {
        return mSqLiteDatabase.query(mLocalVideoTable, new String[] { "_id",
                        "id", Video.url, "image_url", "title", "update_timestamp","type" ,"author", Video.taskFlag}, null, null,
                null, null, null);
    }

    /**
     * 根据id查出一条记录
     * @param id
     * @return
     */
    public Cursor select(int id) {
        if (mSqLiteDatabase != null) {
            return mSqLiteDatabase.query(mLocalVideoTable, new String[] {
                            "_id", "id", Video.url, "image_url", "title", "update_timestamp","type","author", Video.taskFlag },
                    "id=" + String.valueOf(id), null, null, null, null);
        } else {
            return null;
        }
    }

    /**
     * 插入一条video数据
     *
     * @param id
     * @param image_url
     * @param title
     * @param update_timestamp
     * @return
     */
    public long insert(int id, String url, String image_url, String title,
            int update_timestamp, String author, int type, int taskFlag) {
        long status;
        if (mSqLiteDatabase != null) {
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put(Video.url, url);
            values.put("image_url", image_url);
            values.put("title", title);
            values.put("update_timestamp", update_timestamp);
            values.put("author", author);
            values.put("type",type);
            values.put(Video.taskFlag, taskFlag);
            status = mSqLiteDatabase.insert(mLocalVideoTable, "", values);
            return status;
        } else
            return -1;
    }

    /**
     * 根据id删除一条记录
     *
     * @param id
     * @return
     */
    public int delete(int id) {
        if (mSqLiteDatabase != null) {
        	Cursor cursor = select(id);
            cursor.moveToFirst();
            int status = mSqLiteDatabase.delete(mLocalVideoTable, "id="
                    + String.valueOf(id), null);
            return status;
        } else {
            return -1;
        }
    }
    /**
     * 更新特定id数据的key
     * @param id
     * @param taskFlag
     */
    public void update(int id, int taskFlag)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Video.taskFlag, taskFlag);
        mSqLiteDatabase.update(mLocalVideoTable, contentValues, "id="+id, null);

    }

}