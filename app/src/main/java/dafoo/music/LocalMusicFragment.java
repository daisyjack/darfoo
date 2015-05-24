package dafoo.music;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.darfoo.darfoolauncher.R;
import com.darfoo.download.db.DownloadMusicDBOperation;
import com.darfoo.download.widgets.DownloadMusicAdapter;
import com.darfoo.mvplayer.MusicPlayerActivity;

public class LocalMusicFragment extends Fragment {

	private MusicDBOp mDBOperation;
    private DownloadMusicDBOperation mDownloadDBOperation;

    private Cursor mCursor;
    private Cursor mDownloadCursor;
    private DownloadMusicReceiver mReceiver;

    private GridView mGridView;
	private TextView mTextView;
	private DownloadMusicAdapter mDownloadMusicAdapter;
	private Button mButton;
	private ListView mListView;
	private LinearLayout mLinearLayout;
	
	public LocalMusicFragment() {
		
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLinearLayout=(LinearLayout)inflater.inflate(R.layout.download_music, null);
		mListView=(ListView)mLinearLayout.findViewById(R.id.download_music_list);
		mDBOperation = new MusicDBOp(getActivity());
        mDownloadDBOperation = new DownloadMusicDBOperation(getActivity());
		mDBOperation.openDatabase();
        mDownloadDBOperation.openDatabase();


		mCursor = mDBOperation.selectall();
		mCursor.moveToFirst();
        mDownloadCursor = mDownloadDBOperation.selectall();
        mDownloadCursor.moveToFirst();

        mDownloadMusicAdapter= new DownloadMusicAdapter(getActivity(), mDownloadCursor, mCursor);
		mListView.setAdapter(mDownloadMusicAdapter);
		mListView.setOnItemClickListener(new ItemClick());

        mReceiver = new DownloadMusicReceiver();
        IntentFilter filter = new IntentFilter(getString(R.string.download_music_action));
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
            Log.d("LocalMusicFragment", mDownloadCursor.getCount() + "");
            if(position < mDownloadCursor.getCount()){
                return;
            }

            String _Title;
			mCursor.moveToPosition(position - mDownloadCursor.getCount());
			Intent _Intent=new Intent(getActivity(), MusicPlayerActivity.class);
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

    private class DownloadMusicReceiver extends BroadcastReceiver {


        public DownloadMusicReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "通知musicadapter", Toast.LENGTH_SHORT).show();
            mDownloadMusicAdapter.updateList();
        }
    }}
