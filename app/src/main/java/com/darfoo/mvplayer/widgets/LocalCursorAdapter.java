package com.darfoo.mvplayer.widgets;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.support.Cryptor;
import com.koushikdutta.ion.Ion;

public class LocalCursorAdapter extends CursorAdapter {

	
	public LocalCursorAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub
		TextView _TextView=(TextView)arg0.findViewById(R.id.video_title);
		ImageView _ImageView=(ImageView)arg0.findViewById(R.id.video_cover);
       /* _ImageView.setImageBitmap(getVideoThumbnail(arg2.getString(4), 230, 126,
                MediaStore.Images.Thumbnails.MINI_KIND));*/

        String imageUrl = arg2.getString(arg2.getColumnIndex("image_url"));
        if(imageUrl != null) {
            imageUrl = Cryptor.decryptQiniuUrl(imageUrl);
        }
		Ion.with(arg1)
		.load(imageUrl)
		.withBitmap()
		.placeholder(R.drawable.wait)
		.error(R.drawable.wait)
		.intoImageView(_ImageView);
		_TextView.setText(arg2.getString(arg2.getColumnIndex("title")));
	}
   /* private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        System.out.println("w"+bitmap.getWidth());
        System.out.println("h"+bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }*/
	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		LayoutInflater _Inflater=LayoutInflater.from(arg0);
		
		return _Inflater.inflate(R.layout.video_card_item, arg2, false);
		
	}

}
