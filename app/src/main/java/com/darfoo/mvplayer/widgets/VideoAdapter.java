package com.darfoo.mvplayer.widgets;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.support.Cryptor;
import com.darfoo.mvplayer.utils.FileDetail;
import com.koushikdutta.ion.Ion;

import java.util.List;

/**
 * Created by YuXiaofei on 2014/11/24.
 */
public class VideoAdapter extends BaseAdapter {

    private Context mContext;
    private List<FileDetail> details;

    public VideoAdapter(Context context, List<FileDetail> details) {
        super();
        mContext = context;
        this.details = details;

    }

    @Override
    public int getCount() {
        return details.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.video_card_item, null);
        }

        ImageView cover = (ImageView)convertView.findViewById(R.id.video_cover);
        TextView title = (TextView)convertView.findViewById(R.id.video_title);
//        cover.setImageResource(R.drawable.video_cover);

        String imageUrl = details.get(position).getImageUrl();
        if(imageUrl != null) {
            imageUrl = Cryptor.decryptQiniuUrl(imageUrl);
        }
        Ion.with(mContext)
                .load(imageUrl)
                .withBitmap()
                .placeholder(R.drawable.wait)
                .error(R.drawable.wait)
                .intoImageView(cover);

        Log.d("VideoPlayer", details.get(position).getImageUrl());
        title.setText(details.get(position).getTitle());
        return convertView;

    }

}
