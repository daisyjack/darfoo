package dafoo.music;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class MusicDBOp {

    private Context mContext;
    private SQLiteDatabase mSqLiteDatabase;
    private String mDBName = "dafoo.db";
    private String mLocalMusicTable = "local_music";
    private static long MUSIC_NUM;

    public MusicDBOp(Context context) {
        mContext = context;
    }

    /**
     * 连接或创建数据库
     */
    public void openDatabase() {

        mSqLiteDatabase = mContext.openOrCreateDatabase(mDBName,
                Context.MODE_PRIVATE, null);

        if (mSqLiteDatabase != null) {

            try {
                String sql = "CREATE TABLE IF NOT EXISTS " + mLocalMusicTable
                        + "( " + Music._id + " int PRIMARY_KEY," + Music.key
                        + " int," + Music.id + " int," + Music.image_url
                        + " varchar(100)," + Music.title + " varchar(50),"
                        + Music.update_timestamp + " int," + "author varchar(100),"
                        + Music.start_pos + " long,"
                        + Music.stop_pos + " long" + ")";
                mSqLiteDatabase.execSQL(sql);

                Cursor _Cursor=mSqLiteDatabase.rawQuery("select * from "+mLocalMusicTable, null);
                _Cursor.moveToFirst();
                MUSIC_NUM=_Cursor.getCount();
                _Cursor.close();
                Log.i("rykdatabase", "num"+String.valueOf(MUSIC_NUM));
            } catch (Exception e) {
                // TODO: handle exception
                Log.i("rykdatabase", "db table exit");
                Toast.makeText(mContext, "音乐获取失败", Toast.LENGTH_LONG).show();
            }
        }



        Log.i("rykdatabase", "db open");
    }

    /**
     * 断开数据库
     */
    public void closeDatabase() {
        mSqLiteDatabase.close();
        Log.i("rykdatabase", "db close");
    }

    /**
     * 按key升序查出所有数据
     * @return
     */
    public Cursor selectall() {

        return mSqLiteDatabase.query(mLocalMusicTable, new String[] { "_id","key",
                        "id", "image_url", "title", "update_timestamp","author", Music.start_pos, Music.stop_pos }, null, null,
                null, null, "key");

    }

    /**
     * 根据id查出一条记录
     * @param id
     * @return
     */
    public Cursor select(int id) {
        if (mSqLiteDatabase != null) {
            return mSqLiteDatabase.query(mLocalMusicTable, new String[] {
                            "_id", "key","id", "image_url", "title", "update_timestamp","author", Music.start_pos, Music.stop_pos },
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
    public long insert(int id, String image_url, String title,
            int update_timestamp,String author, long startPos, long stopPos) {
        MUSIC_NUM++;
        long _Status;
        if (mSqLiteDatabase != null) {
            ContentValues values = new ContentValues();
            values.put("key", MUSIC_NUM);
            values.put("id", id);
            values.put("image_url", image_url);
            values.put("title", title);
            values.put("update_timestamp", update_timestamp);
            values.put("author", author);
            values.put(Music.start_pos, startPos);
            values.put(Music.stop_pos, stopPos);
            _Status = mSqLiteDatabase.insert(mLocalMusicTable, "", values);
            Log.i("rykdatabase", "insert"+String.valueOf(MUSIC_NUM));
            return _Status;
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
        	Cursor _Cursor=select(id);
            _Cursor.moveToFirst();
            int _Key= _Cursor.getInt(_Cursor.getColumnIndex("key"));
            int _Status = mSqLiteDatabase.delete(mLocalMusicTable, "id="
                    + String.valueOf(id), null);        
            for (int _iKey=_Key+1;_iKey<=MUSIC_NUM;_iKey++)
            {
            	ContentValues _ContentValues=new ContentValues();
            	_ContentValues.put("key", _iKey-1);
            	mSqLiteDatabase.update(mLocalMusicTable, _ContentValues, "key="+_iKey, null);
            }
            Log.i("rykdatabase", "delete"+id);
            MUSIC_NUM -= _Status;
            return _Status;
        } else {
            return -1;
        }
    }

    /**
     * 更新特定id数据的key
     * @param id
     * @param key
     */
    public void update(int id, int key)
    {
        ContentValues _ContentValues=new ContentValues();
        _ContentValues.put("key", key);
        mSqLiteDatabase.update(mLocalMusicTable, _ContentValues, "id="+id, null);

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