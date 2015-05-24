package dafoo.video;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.support.Cryptor;
import com.koushikdutta.ion.Ion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class VideoEditAdapter extends CursorAdapter {

    private Context mContext;
    public static final String VIDEODIR = Environment.getExternalStorageDirectory().getPath()+ File.separator+"Darfoo"+File.separator
    		+"video"+File.separator+File.separator;

    public VideoEditAdapter(Context context, Cursor c) {
        super(context, c);
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    public class DeleteOnclickListener implements View.OnClickListener {
        private int mId;
        private Cursor mCursor;
        public String mVideoPath;

        public DeleteOnclickListener(int id ) {
            // TODO Auto-generated constructor stub
            mId = id;
         }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            /*Log.i("rykdatabase","click"+mId );*/
            AlertDialog.Builder _Builder=new AlertDialog.Builder(mContext);
            _Builder.setMessage("是否要删除？");
            //_Builder.setTitle("提示");
            _Builder.setNegativeButton("是", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                    DBOperation _DbOperation=new DBOperation(mContext);
                    _DbOperation.openDatabase();
                    mCursor=_DbOperation.select(mId);
                    mCursor.moveToFirst();
                    mVideoPath = VIDEODIR + mCursor.getString(mCursor.getColumnIndex("title")) + "_" + mCursor.getInt(mCursor.getColumnIndex("id"));
                    File file = new File(mVideoPath);
                    if(file.exists())
                    {
                    file.delete();
                    Log.i("adapter", "delete");
                    }else {
						Log.i("adapter", "not delete"+"  "+mVideoPath);
					}
                    _DbOperation.delete(mId);
                    changeCursor(mCursor);
                }
            });
            _Builder.setPositiveButton("否", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                }
            });
            _Builder.create().show();

        }
    }

    public class ExchangeOnclickListener implements View.OnClickListener {
        private Integer mId1;
        private Integer mId2;

        public ExchangeOnclickListener(Integer mId1, Integer mId2) {
            super();
            this.mId1 = mId1;
            this.mId2 = mId2;

        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int _Key1;
            int _Key2;
            Cursor _Cursor;
            if (mId1 != null) {

                DBOperation _DbOperation = new DBOperation(mContext);
                _DbOperation.openDatabase();
                Log.i("rykdatabase", "Idone" + mId1.intValue());
                _Cursor = _DbOperation.select(mId1.intValue());
                _Cursor.moveToFirst();
                _Key1 = _Cursor.getInt(_Cursor.getColumnIndex("key"));
                Log.i("rykdatabase", "keyone" + _Key1);
                Log.i("rykdatabase", "Idtwo" + mId2.intValue());
                _Cursor = _DbOperation.select(mId2.intValue());
                _Cursor.moveToFirst();
                _Key2 = _Cursor.getInt(_Cursor.getColumnIndex("key"));
                Log.i("rykdatabase", "keytwo" + _Key2);
                _DbOperation.update(mId1.intValue(), _Key2);
                _DbOperation.update(mId2.intValue(), _Key1);
                changeCursor(_DbOperation.selectall());
            }

        }

    }

    @Override
    public void bindView(View arg0, Context arg1, Cursor arg2) {
        // TODO Auto-generated method stub
        Integer _UpId;
        Integer _MidId;
        Integer _DownId;
        TextView _TextView = (TextView) arg0.findViewById(R.id.video_edit_title);
        _TextView.setText(arg2.getString(arg2.getColumnIndex("title")));
		ImageView _ImageView=(ImageView)arg0.findViewById(R.id.video_edit_cover);
        String imageUrl = arg2.getString(arg2.getColumnIndex("image_url"));
        if(imageUrl != null) {
            imageUrl = Cryptor.decryptQiniuUrl(imageUrl);
        }
		Ion.with(arg1)
		.load(imageUrl)
		.withBitmap()
		.placeholder(R.drawable.wait)
		.error(R.drawable.wait)
		.intoImageView(_ImageView);
        /*_ImageView.setImageBitmap(getVideoThumbnail(arg2.getString(4), 230, 126,
                MediaStore.Images.Thumbnails.MINI_KIND));*/
        Button _DeleteButton = (Button) arg0.findViewById(R.id.video_edit_delete_button);
        _DeleteButton.setOnClickListener(new DeleteOnclickListener(arg2.getInt(arg2.getColumnIndex("id"))));
        /*_MidId = new Integer(arg2.getInt(arg2.getColumnIndex("id")));
        if (arg2.moveToPrevious()) {
            _UpId = new Integer(arg2.getInt(arg2.getColumnIndex("id")));
            arg2.moveToNext();
        } else {
            arg2.moveToNext();
            _UpId = null;
        }
        if (arg2.moveToNext()) {
            _DownId = Integer.valueOf(arg2.getInt(arg2.getColumnIndex("id")));
            arg2.moveToPrevious();
        } else {
            _DownId = null;
        }

        Button _UpButton = (Button) arg0.findViewById(R.id.up_button);
        _UpButton
                .setOnClickListener(new ExchangeOnclickListener(_UpId, _MidId));
        Button _DownButton = (Button) arg0.findViewById(R.id.down_button);
        _DownButton.setOnClickListener(new ExchangeOnclickListener(_DownId,
                _MidId));*/
    }
   /* private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        System.out.println("w"+bitmap.getWidth());
        System.out.println("h"+bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }*/
    @Override
    public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        LayoutInflater _Inflater = LayoutInflater.from(arg0);
        return _Inflater.inflate(R.layout.video_edit_item, arg2, false);
    }

}
