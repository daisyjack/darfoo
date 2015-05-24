package dafoo.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darfoo.darfoolauncher.R;
import com.darfoo.download.db.DownloadVideoDBOperation;
import com.darfoo.download.utils.CommandUtils;
import com.darfoo.download.widgets.DownloadVideoAdapter;
import com.darfoo.mvplayer.VideoPlayerActivity;

public class LocalVideoFragment extends Fragment {

    private static final String TAG = "LocalVideoFragment";
	private DBOperation mDBOperation;
    private DownloadVideoDBOperation mDownloadDBOperation;
	private Cursor mCursor;
    private Cursor mDownloadCursor;
    private DownloadVideoReceiver mReceiver;
	private TextView mTextView;
	private GridView mGridView;
	private DownloadVideoAdapter mDownloadVideoAdapter;
	private Button mButton;
	private LinearLayout mLinearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent(getActivity(), com.darfoo.download.DownloadService.class);
        serviceIntent.putExtra(CommandUtils.COMMAND, CommandUtils.START_MANAGER);
        getActivity().startService(serviceIntent);

    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLinearLayout=(LinearLayout)inflater.inflate(R.layout.layout_local_video, null);
		mLinearLayout.removeView(mLinearLayout.findViewById(R.id.local_relativelayout));
		
		mGridView = (GridView)mLinearLayout.findViewById(R.id.local_video_girdview);
		mDBOperation = new DBOperation(getActivity());
        mDownloadDBOperation = new DownloadVideoDBOperation(getActivity());
		mDBOperation.openDatabase();
        mDownloadDBOperation.openDatabase();

		mCursor = mDBOperation.selectall();
		mCursor.moveToFirst();
        mDownloadCursor = mDownloadDBOperation.selectall();
        mDownloadCursor.moveToFirst();

        mDownloadVideoAdapter=new DownloadVideoAdapter(getActivity(), mDownloadCursor, mCursor);
		mGridView.setAdapter(mDownloadVideoAdapter);
		mGridView.setOnItemClickListener(new ItemClick());

        mReceiver = new DownloadVideoReceiver();
        IntentFilter filter = new IntentFilter(getString(R.string.download_video_action));
        getActivity().registerReceiver(mReceiver, filter);
		return mLinearLayout;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
		mDBOperation.closeDatabase();
        mDownloadDBOperation.closeDatabase();
	}
	public class ItemClick implements AdapterView.OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

            if(position < mDownloadCursor.getCount()){
                return;
            }

			String _Title;
			mCursor.moveToPosition(position - mDownloadCursor.getCount());
			Intent _Intent=new Intent(getActivity(), VideoPlayerActivity.class);
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
		}
		
	}

    private class DownloadVideoReceiver extends BroadcastReceiver {


        public DownloadVideoReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "通知videoadapter", Toast.LENGTH_SHORT).show();
            mDownloadVideoAdapter.updateList();
        }
    }
	
}
