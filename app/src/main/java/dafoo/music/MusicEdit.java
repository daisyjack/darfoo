package dafoo.music;

import com.darfoo.darfoolauncher.R;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.darfoo.darfoolauncher.activity.BaseFragmentActivity;
import com.darfoo.mvplayer.MusicPlayerActivity;

public class MusicEdit extends BaseFragmentActivity {

	private ListView mListView;
	private MusicDBOp mDBOperation;
	private Cursor mCursor;
	private EditAdapter mEditAdapter;
	private Button mButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_edit_page);

        Log.d("musicedit","ajkasdfklajdf");
        mListView=(ListView)findViewById(R.id.music_edit_page_listView);
		mDBOperation=new MusicDBOp(this);
		mDBOperation.openDatabase();		
		mCursor = mDBOperation.selectall();
		mCursor.moveToFirst();
		mEditAdapter=new EditAdapter(this, mCursor);
		mListView.setAdapter(mEditAdapter);
		mButton=(Button)findViewById(R.id.music_edit_page_button);
		mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent _Intent=new Intent(MusicEdit.this,MusicPlayerActivity.class);
				startActivity(_Intent);
				finish();
			}
		});
	}

	
}
