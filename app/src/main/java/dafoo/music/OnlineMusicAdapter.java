package dafoo.music;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.darfoo.darfoolauncher.R;

import dafoo.video.VideoFromCategory;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OnlineMusicAdapter extends BaseAdapter {

	private JsonArray mJsonArray;
	private Context mContext;
	public Gson mGson;
	public static List<MusicFromCategory> mList;
    int mflag;
	
	public OnlineMusicAdapter(Context context, JsonArray jsonArray,int flag)
	{
		this.mContext=context;
		this.mJsonArray=jsonArray;
        this.mflag = flag;
		mGson=new Gson();
		mList =mGson.fromJson(mJsonArray, new TypeToken<List<MusicFromCategory>>(){}.getType());
		Log.i("rykdatabase", "4");
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		//return 1;
		return mJsonArray.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mJsonArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
        int[] colors = new int[]{0xF2F2F2,0xF2F2F2, 0x50D2D2D2, 0x50D2D2D2};
        LayoutInflater _Inflater=LayoutInflater.from(mContext);
		LinearLayout _LinearLayout=(LinearLayout)_Inflater.inflate(R.layout.online_music_item, parent, false);
		//ImageView _ImageView=(ImageView)_RelativeLayout.findViewById(R.id.online_music_cover);
		TextView _TextView=(TextView)_LinearLayout.findViewById(R.id.online_music_title);
		TextView _TextView2=(TextView)_LinearLayout.findViewById(R.id.online_music_author);
        TextView _TextView3=(TextView)_LinearLayout.findViewById(R.id.music_num);
        ImageView myimage = (ImageView)_LinearLayout.findViewById(R.id.fire);
		MusicFromCategory _MusicFromCategory=mList.get(position);
		_TextView.setText(_MusicFromCategory.getTitle());
        _TextView2.setText(_MusicFromCategory.getAuthorname());
		//String _Url=_MusicFromCategory.getImage_url();	
		/*Ion.with(mContext)
		.load(_Url)
		.withBitmap()
		.placeholder(R.drawable.wait)
		.error(R.drawable.wait)
		.intoImageView(_ImageView);*/
        String mynum = position+1+"";
        _TextView3.setText(mynum);
       if(position<3 && mflag == 0) myimage.setImageResource(R.drawable.fire);
        else myimage = null;
        int colorPos = position%colors.length;
        _LinearLayout.setBackgroundColor(colors[colorPos]);
        return _LinearLayout;
	}
}
