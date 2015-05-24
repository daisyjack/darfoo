package dafoo.music;

import com.koushikdutta.ion.Ion;

import com.darfoo.darfoolauncher.R;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LocalMusicAdapter extends CursorAdapter {

	public LocalMusicAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub
		TextView _TextView=(TextView)arg0.findViewById(R.id.video_title);
		ImageView _ImageView=(ImageView)arg0.findViewById(R.id.video_cover);
		
		Ion.with(arg1)
		.load(arg2.getString(arg2.getColumnIndex("image_url")))
		.withBitmap()
		.placeholder(R.drawable.wait)
		.error(R.drawable.wait)
		.intoImageView(_ImageView);
		_TextView.setText(arg2.getString(arg2.getColumnIndex("title")));
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		LayoutInflater _Inflater=LayoutInflater.from(arg0);
		
		return _Inflater.inflate(R.layout.video_item, arg2, false);
		
	}

}
