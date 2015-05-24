package com.darfoo.download.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import dafoo.music.Music;

public class DownloadMusicDBOperation {

    private Context mContext;
    private SQLiteDatabase mSqLiteDatabase;
    private String mDBName = "dafoo.db";
    private String mLocalMusicTable = "downloading_music";
    private static long MUSIC_NUM;

    public DownloadMusicDBOperation(Context context) {
        mContext = context;
    }

    /**
     * taskFlag: -1 下载失败 0 下载中 1 等待下载
     * 连接或创建数据库
     */
    public void openDatabase() {

        mSqLiteDatabase = mContext.openOrCreateDatabase(mDBName,
                Context.MODE_PRIVATE, null);

        if (mSqLiteDatabase != null) {

            try {
                String sql = "CREATE TABLE IF NOT EXISTS " + mLocalMusicTable
                        + "( "
                        + Music._id + " int PRIMARY_KEY,"
                        + Music.id + " int,"
                        + Music.url + " varchar(100),"
                        + Music.image_url + " varchar(100),"
                        + Music.title + " varchar(50),"
                        + Music.update_timestamp + " int,"
                        + Music.author + " varchar(100),"
                        + Music.start_pos + " long,"
                        + Music.stop_pos + " long,"
                        + Music.taskFlag + " int"
                        + ")";
                mSqLiteDatabase.execSQL(sql);

                Cursor cursor = mSqLiteDatabase.rawQuery("select * from "+ mLocalMusicTable, null);
                cursor.moveToFirst();
                MUSIC_NUM = cursor.getCount();
                cursor.close();
            } catch (Exception e) {
                Toast.makeText(mContext, "音乐获取失败", Toast.LENGTH_LONG).show();
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

        return mSqLiteDatabase.query(mLocalMusicTable, new String[] { "_id", "id", Music.url,
                        "image_url", "title", "update_timestamp",
                        "author", Music.start_pos, Music.stop_pos, Music.taskFlag }, null, null, null, null, null);

    }

    /**
     * 根据id查出一条记录
     * @param id
     * @return
     */
    public Cursor select(int id) {
        if (mSqLiteDatabase != null) {
            return mSqLiteDatabase.query(mLocalMusicTable, new String[] {
                            "_id", "id", Music.url, "image_url", "title",
                            "update_timestamp","author", Music.start_pos, Music.stop_pos, Music.taskFlag},
                    "id=" + String.valueOf(id), null, null, null, null);
        } else {
            return null;
        }
    }

    /**
     * 插入一条music数据
     *
     * @param id
     * @param image_url
     * @param title
     * @param update_timestamp
     * @return
     */
    public long insert(int id, String url, String image_url, String title,
            int update_timestamp,String author, long startPos, long stopPos, int taskFlag) {
        long status;
        if (mSqLiteDatabase != null) {
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put(Music.url, url);
            values.put("image_url", image_url);
            values.put("title", title);
            values.put("update_timestamp", update_timestamp);
            values.put("author", author);
            values.put(Music.start_pos, startPos);
            values.put(Music.stop_pos, stopPos);
            values.put(Music.taskFlag, taskFlag);

            status = mSqLiteDatabase.insert(mLocalMusicTable, "", values);
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
            int status = mSqLiteDatabase.delete(mLocalMusicTable, "id="
                    + String.valueOf(id), null);        
            return status;
        } else {
            return -1;
        }
    }

    /**
     * 更新特定id数据的taskFlag
     * @param id
     * @param taskFlag
     */
    public void update(int id, int taskFlag)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Music.taskFlag, taskFlag);
        mSqLiteDatabase.update(mLocalMusicTable, contentValues, "id="+id, null);

    }

    /**
     * 更新特定id数据的startPos和stopPos
     * @param id
     * @param startPos
     * @param stopPos
     */
    public void updateDuration(int id, long startPos, long stopPos)
    {
        ContentValues _ContentValues=new ContentValues();
        _ContentValues.put("start_pos", startPos);
        _ContentValues.put("stop_pos", stopPos);

        mSqLiteDatabase.update(mLocalMusicTable, _ContentValues, "id="+id, null);

    }

}