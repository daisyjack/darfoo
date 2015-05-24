package dafoo.music;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.activity.BaseFragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import dafoo.video.OnlineVideo;

public class LocalMusic extends BaseFragmentActivity {

	private MusicDBOp mDBOperation;
	private Cursor mCursor;
	//public static SimpleCursorAdapter mCursorAdapter;
	private GridView mGridView;
	private TextView mTextView;
	private LocalMusicAdapter mLocalMusicAdapter;
	private Button mButton;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_local_video);
		
		mGridView = (GridView) findViewById(R.id.local_video_girdview);
		mTextView=(TextView)findViewById(R.id.local_textView);
		mTextView.setText("本地音频");
		mDBOperation = new MusicDBOp(this);
		mDBOperation.openDatabase();
		//Log.i("rykdatabase", "music table open");
		
		/*mDBOperation.insert(1, "http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg", "1musictest", 20140907);
		mDBOperation.insert(2, "http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg", "2musictest", 20140907);
		mDBOperation.insert(3, "http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg", "3musictest", 20140907);
		mDBOperation.insert(4, "http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg", "4musictest", 20140907);
		mDBOperation.insert(5, "http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg", "5musictest", 20140907);
		mDBOperation.insert(6, "http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg", "6musictest", 20140907);*/
		
		//Log.i("rykdatabase", "insert 6 record");
		mCursor = mDBOperation.selectall();
		//Log.i("rykdatabase", "select all");
		mCursor.moveToFirst();
		//Log.i("rykdatabase", mCursor.getString(mCursor.getColumnIndex("title")));
		mLocalMusicAdapter=new LocalMusicAdapter(this, mCursor);
		//Log.i("rykdatabase", "adapter created");
		mGridView.setAdapter(mLocalMusicAdapter);
		//Log.i("rykdatabase", "adapter set");
		mGridView.setOnItemClickListener(new ItemClick());
		//Log.i("rykdatabase", "onclick set");
		mButton=(Button)findViewById(R.id.local_button);
		mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent _mIntent=new Intent(LocalMusic.this,MusicEdit.class);
				startActivity(_mIntent);
				finish();
			}
		});
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mDBOperation.closeDatabase();
	}


	public class ItemClick implements AdapterView.OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			String _Title;
			mCursor.moveToPosition(position);
			Intent _Intent=new Intent(LocalMusic.this, OnlineVideo.class);
			Bundle _Bundle=new Bundle();
			_Bundle.putInt("status", 1);
			_Bundle.putInt("id", mCursor.getInt(mCursor.getColumnIndex("id")));
			if(mCursor.getColumnIndex("title")!=0)
			{
				_Title=mCursor.getString(mCursor.getColumnIndex("title"));
			}
			else {
				_Title="untitle";
			}
			_Bundle.putString("title", _Title);
			_Intent.putExtra("message", _Bundle);
			startActivity(_Intent);
			Log.i("rykdatabase", mCursor.getString(mCursor.getColumnIndex("title"))+"qq ");
			
		}
		
	}
}
