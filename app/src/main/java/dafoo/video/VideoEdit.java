package dafoo.video;

import com.darfoo.darfoolauncher.R;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.darfoo.darfoolauncher.activity.BaseFragmentActivity;

public class VideoEdit extends BaseFragmentActivity {

	private ListView mListView;
	private DBOperation mDBOperation;
	private Cursor mCursor;
	private VideoEditAdapter mEditAdapter;
	private Button mButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_editpage);
		mListView=(ListView)findViewById(R.id.edit_page_listView);
		mDBOperation=new DBOperation(this);
		mDBOperation.openDatabase();
		mCursor = mDBOperation.selectall();
		mCursor.moveToFirst();
		mEditAdapter=new VideoEditAdapter(this, mCursor);
		mListView.setAdapter(mEditAdapter);
		mButton=(Button)findViewById(R.id.edit_page_button);
		mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent _Intent=new Intent(VideoEdit.this,LocalVideo.class);
				startActivity(_Intent);
				finish();
			}
		});
	}

	
}
