package dafoo.video;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class DBOperation {

    private Context mContext;
    private SQLiteDatabase mSqLiteDatabase;
    private String mDBName = "dafoo.db";
    private String mLocalVideoTable = "local_video";
    private static long VIDEO_NUM;

    public DBOperation(Context context) {
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
                String sql = "CREATE TABLE IF NOT EXISTS " + mLocalVideoTable
                        + "( " + Video._id + " int PRIMARY_KEY," + Video.key
                        + " int," + Video.id + " int," + Video.image_url
                        + " varchar(100)," + Video.title + " varchar(50),"
                        + Video.update_timestamp + " int,"
                        + Video.type+ " int,"	+ "author varchar(100))";
                mSqLiteDatabase.execSQL(sql);

                Cursor _Cursor=mSqLiteDatabase.rawQuery("select * from "+mLocalVideoTable, null);
                _Cursor.moveToFirst();
                VIDEO_NUM=_Cursor.getCount();
                _Cursor.close();
                Log.i("rykdatabase", "num"+String.valueOf(VIDEO_NUM));

            } catch (Exception e) {
                // TODO: handle exception
                Log.i("rykdatabase", "db table exit");
                Toast.makeText(mContext, "视频获取失败", Toast.LENGTH_LONG).show();
            }
        }

        Log.i("rykdatabase", "insert");
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
        return mSqLiteDatabase.query(mLocalVideoTable, new String[] { "_id","key",
                        "id", "image_url", "title", "update_timestamp","type" ,"author"}, null, null,
                null, null, "key");
    }

    /**
     * 根据id查出一条记录
     * @param id
     * @return
     */
    public Cursor select(int id) {
        if (mSqLiteDatabase != null) {
            return mSqLiteDatabase.query(mLocalVideoTable, new String[] {
                            "_id", "key","id", "image_url", "title", "update_timestamp","type","author" },
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
    public long insert(int id, String image_url, String title,
            int update_timestamp,String author,int type) {
        VIDEO_NUM++;
        long _Status;
        if (mSqLiteDatabase != null) {
            ContentValues values = new ContentValues();
            values.put("key", VIDEO_NUM);
            values.put("id", id);
            values.put("image_url", image_url);
            values.put("title", title);
            values.put("update_timestamp", update_timestamp);
            values.put("author", author);
            values.put("type",type);
            _Status = mSqLiteDatabase.insert(mLocalVideoTable, "", values);
            Log.i("rykdatabase", "insert"+String.valueOf(VIDEO_NUM));
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
            int _Status = mSqLiteDatabase.delete(mLocalVideoTable, "id="
                    + String.valueOf(id), null);
            for (int _iKey=_Key+1;_iKey<=VIDEO_NUM;_iKey++)
            {
            	ContentValues _ContentValues=new ContentValues();
            	_ContentValues.put("key", _iKey-1);
            	mSqLiteDatabase.update(mLocalVideoTable, _ContentValues, "key="+_iKey, null);
            }
            VIDEO_NUM -= _Status;
            Log.i("rykdatabase", "delete"+id);
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
        mSqLiteDatabase.update(mLocalVideoTable, _ContentValues, "id="+id, null);

    }

}