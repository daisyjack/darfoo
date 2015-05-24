package dafoo.video;

import com.darfoo.darfoolauncher.support.Cryptor;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.*;
import com.koushikdutta.ion.Ion;

import com.darfoo.darfoolauncher.R;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList; 


public class OnlineJsonAdapter extends BaseAdapter {

	private JsonArray mJsonArray;
	private Context mContext;
	public Gson mGson;
	public static List<VideoFromCategory> mList;
	
	public OnlineJsonAdapter(Context context, JsonArray jsonArray)
	{
		this.mContext=context;
		this.mJsonArray=jsonArray;
		mGson=new Gson();
		mList =mGson.fromJson(mJsonArray, new TypeToken<List<VideoFromCategory>>(){}.getType());
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
		Log.i("rykdatabase", "OnlineJsonAdapter getView");
		LayoutInflater _Inflater=LayoutInflater.from(mContext);
		RelativeLayout _RelativeLayout=(RelativeLayout)_Inflater.inflate(R.layout.video_item, parent, false);
		ImageView _ImageView=(ImageView)_RelativeLayout.findViewById(R.id.video_cover);
		TextView _TextView=(TextView)_RelativeLayout.findViewById(R.id.video_title);
		Log.i("rykdatabase", "5");
		VideoFromCategory _VideoFromCategory=mList.get(position);
		Log.i("rykdatabase", "6");
		_TextView.setText(_VideoFromCategory.getTitle());
		Log.i("rykdatabase", "7"+_VideoFromCategory.getTitle());
		String _Url= Cryptor.decryptQiniuUrl(_VideoFromCategory.getImage_url());
		Log.i("rykdatabase", "8"+_VideoFromCategory.getImage_url());
        Log.i("aaa", _Url);
		Ion.with(mContext)
		.load(_Url)
		.withBitmap()
		.placeholder(R.drawable.wait)
		.error(R.drawable.wait)
		.intoImageView(_ImageView);
		Log.i("rykdatabase", "9");
		return _RelativeLayout;
	}

}
