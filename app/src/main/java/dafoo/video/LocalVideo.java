package dafoo.video;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.activity.BaseFragmentActivity;
import com.darfoo.mvplayer.VideoPlayerActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

public class LocalVideo extends BaseFragmentActivity {

	private DBOperation mDBOperation;
	private Cursor mCursor;
	private GridView mGridView;
	private LocalCursorAdapter mLocalCursorAdapter;
	private ImageButton mButton;
	private int mButtonFlag;
    private VideoEditAdapter mVideoEditAdapter;
    private Intent intent;
   /* String[] mediaColumns = new String[]{
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.ARTIST,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATA};*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mButtonFlag=1;
		setContentView(R.layout.layout_local_video);
		mGridView = (GridView) findViewById(R.id.local_video_girdview);
       /* mCursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null,null);*/
		mDBOperation = new DBOperation(this);
		Log.i("rykdatabase", "1");
		
		mDBOperation.openDatabase();
		Log.i("rykdatabase", "2");
		
		/*mDBOperation.insert(1, "http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg", "1videotest", 20140907);
		mDBOperation.insert(2, "http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg", "2videotest", 20140907);
		mDBOperation.insert(3, "http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg", "3videotest", 20140907);
		mDBOperation.insert(4, "http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg", "4videotest", 20140907);
		mDBOperation.insert(5, "http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg", "5videotest", 20140907);
		mDBOperation.insert(6, "http://img2.cache.netease.com/game/2013/11/25/2013112515552161561.jpg", "6videotest", 20140907);*/
		//Log.i("rykdatabase", "3");
		mCursor = mDBOperation.selectall();
        mCursor.moveToFirst();
		//Log.i("rykdatabase", "4");

		//Log.i("rykdatabase", mCursor.getString(mCursor.getColumnIndex("title")));
		
		/*mCursorAdapter = new SimpleCursorAdapter(this, R.layout.video_display,
				mCursor, from, to);*/
		mLocalCursorAdapter=new LocalCursorAdapter(this, mCursor);
		//Log.i("rykdatabase", "5");
		mGridView.setAdapter(mLocalCursorAdapter);		
		mGridView.setOnItemClickListener(new ItemClick());
		//Log.i("rykdatabase", "6");
		mButton=(ImageButton)findViewById(R.id.local_button);
        if(mCursor.getCount()>0){
            mButton.setVisibility(View.VISIBLE);
            intent = new Intent(this, Upload.class);
            startService(intent);
		}
        else mButton.setVisibility(View.INVISIBLE);
		mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*Intent _Intent=new Intent(LocalVideo.this, VideoEdit.class);
				startActivity(_Intent);
				finish();*/
				if(mButtonFlag==1)
				{
					mButton.setImageResource(R.drawable.save);
					mButtonFlag=2;
					mVideoEditAdapter=new VideoEditAdapter(LocalVideo.this, mCursor);
					mGridView.setAdapter(mVideoEditAdapter);
				}else 
					if(mButtonFlag==2)
					{
						mButton.setImageResource(R.drawable.edit_button);
						mButtonFlag=1;
                        mCursor=mDBOperation.selectall();
                      /*  mCursor =LocalVideo.this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null,null);*/
						mCursor.moveToFirst();
						mLocalCursorAdapter=new LocalCursorAdapter(LocalVideo.this, mCursor);
						mGridView.setAdapter(mLocalCursorAdapter);
					}
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
			Intent _Intent=new Intent(LocalVideo.this, VideoPlayerActivity.class);
			Bundle _Bundle=new Bundle();
			Log.i("ryk1", ""+mCursor.getInt(mCursor.getColumnIndex("id")));
			_Bundle.putInt("status", 1);
			_Bundle.putInt("id",mCursor.getInt(mCursor.getColumnIndex("id")));
			if(mCursor.getColumnIndex("title")!=0)
			{
				_Title=mCursor.getString(mCursor.getColumnIndex("title"));
			}
			else {
				_Title="untitle";
			}
			_Bundle.putString("title", _Title);
            _Bundle.putString("author", mCursor.getString(mCursor.getColumnIndex("author")));
            _Bundle.putInt("type",mCursor.getInt(mCursor.getColumnIndex("type")));
			_Intent.putExtra("message", _Bundle);
			startActivity(_Intent);
			Log.i("rykdatabase", mCursor.getString(mCursor.getColumnIndex("title"))+"qq ");
		}
		
	}

}
