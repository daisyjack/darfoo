package dafoo.music;

import com.darfoo.darfoolauncher.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Environment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class EditAdapter extends CursorAdapter {

	private Context mContext;
	public static final String MUSICDIR = Environment.getExternalStorageDirectory().getPath()+File.separator+"Darfoo"+File.separator
    		+"music"+File.separator+ File.separator;

	public EditAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub

		mContext = context;
	}

	public class DeleteOnclickListener implements View.OnClickListener {
		private int mId;
		private Cursor mCursor;
        public String mMusicPath;

		public DeleteOnclickListener(int id) {
			// TODO Auto-generated constructor stub
			mId = id;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.i("rykdatabase","click"+mId );
			AlertDialog.Builder _Builder=new AlertDialog.Builder(mContext);
			_Builder.setMessage("是否要删除？");
			//_Builder.setTitle("��ʾ");
			_Builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					MusicDBOp _MusicDBOp=new MusicDBOp(mContext);
					_MusicDBOp.openDatabase();
					mCursor=_MusicDBOp.select(mId);
                    mCursor.moveToFirst();
                    mMusicPath = MUSICDIR + mCursor.getString(mCursor.getColumnIndex("title")) + "_" + mCursor.getInt(mCursor.getColumnIndex("id"));
                    File file = new File(mMusicPath);
                    if(file.exists())
                    {
                    file.delete();
                    Log.i("adapter", "delete");
                    }else {
						Log.i("adapter", "not delete"+"  "+mMusicPath);
					}
					_MusicDBOp.delete(mId);
					changeCursor(_MusicDBOp.selectall());
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

				MusicDBOp _MusicDBOp = new MusicDBOp(mContext);
				_MusicDBOp.openDatabase();

				Log.i("rykdatabase", "Idone" + mId1.intValue());
				_Cursor = _MusicDBOp.select(mId1.intValue());
				_Cursor.moveToFirst();
				_Key1 = _Cursor.getInt(_Cursor.getColumnIndex("key"));
				Log.i("rykdatabase", "keyone" + _Key1);

				Log.i("rykdatabase", "Idtwo" + mId2.intValue());
				_Cursor = _MusicDBOp.select(mId2.intValue());
				_Cursor.moveToFirst();
				_Key2 = _Cursor.getInt(_Cursor.getColumnIndex("key"));

				Log.i("rykdatabase", "keytwo" + _Key2);
				_MusicDBOp.update(mId1.intValue(), _Key2);
				_MusicDBOp.update(mId2.intValue(), _Key1);

				changeCursor(_MusicDBOp.selectall());
			}

		}

	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub
		Integer _UpId;
		Integer _MidId;
		Integer _DownId;

		TextView _TextView = (TextView) arg0.findViewById(R.id.music_edit_textView);
		_TextView.setText(arg2.getString(arg2.getColumnIndex("title")));
		TextView _TextView2=(TextView)arg0.findViewById(R.id.edit_key);
		_TextView2.setText(""+arg2.getInt(arg2.getColumnIndex("key")));
		TextView _TextView3=(TextView) arg0.findViewById(R.id.textView3);
		_TextView3.setText(arg2.getString(arg2.getColumnIndex("author")));
		Button _DeleteButton = (Button) arg0.findViewById(R.id.music_delete_button);
		_DeleteButton.setOnClickListener(new DeleteOnclickListener(arg2
				.getInt(arg2.getColumnIndex("id"))));

		_MidId = new Integer(arg2.getInt(arg2.getColumnIndex("id")));
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

		Button _UpButton = (Button) arg0.findViewById(R.id.music_up_button);
		_UpButton.setOnClickListener(new ExchangeOnclickListener(_UpId, _MidId));

		Button _DownButton = (Button) arg0.findViewById(R.id.music_down_button);
		_DownButton.setOnClickListener(new ExchangeOnclickListener(_DownId,
				_MidId));
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		LayoutInflater _Inflater = LayoutInflater.from(arg0);
		return _Inflater.inflate(R.layout.music_edit_item, arg2, false);
	}

}
