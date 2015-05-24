package com.darfoo.darfoolauncher.fragment;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.support.Utils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Future;

public class HorizontalScrollViewAdapter
{
    private LayoutInflater mInflater;
    private List<String> images;
    private List<String> names;
    private boolean recommend_video;
    private boolean latest_video;
    private boolean famous_dance_group;
    private Context context;


    public HorizontalScrollViewAdapter(Context context, List<String> images, List<String> names, boolean recommend_video, boolean latest_video, boolean famous_dance_group)
    {
        mInflater = LayoutInflater.from(context);
        this.images = images;
        this.names = names;
        this.recommend_video = recommend_video;
        this.latest_video = latest_video;
        this.famous_dance_group = famous_dance_group;
        this.context = context;
    }

    public int getCount(){
        return images.size();
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            if(this.recommend_video){
                convertView = mInflater.inflate(R.layout.fragment_recommendation_recommend_video_item, parent, false);
                viewHolder.mImg = (ImageView) convertView.findViewById(R.id.fragment_recommendation_recommend_video_item_image);
                viewHolder.mText = (TextView) convertView.findViewById(R.id.fragment_recommendation_recommend_video_item_text);
                init_viewGroup(convertView, position, images.size(),this.context);
            }else if(this.latest_video){
                convertView = mInflater.inflate(R.layout.fragment_recommendation_latest_video_item, parent, false);
                viewHolder.mImg = (ImageView) convertView.findViewById(R.id.fragment_recommendation_latest_video_item_image);
                viewHolder.mText = (TextView) convertView.findViewById(R.id.fragment_recommendation_latest_video_item_text);
            }else{
                convertView = mInflater.inflate(R.layout.fragment_recommendation_famous_dance_group_item, parent, false);
                viewHolder.mImg = (ImageView) convertView.findViewById(R.id.fragment_recommendation_famous_dance_group_item_image);
                viewHolder.mText = (TextView) convertView.findViewById(R.id.fragment_recommendation_famous_dance_group_item_text);
            }
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        position = position % images.size();
        if (position < 0) position = 0;

        Log.d("view adapter", position + "");
        Future<Bitmap> future =  Ion.with(this.context).load(images.get(position)).asBitmap().setCallback(new FutureCallback<Bitmap>() {
            @Override
            public void onCompleted(Exception e, Bitmap result) {
                if(e!=null){
                    viewHolder.mImg.setImageResource(R.drawable.ic_famous_dancegroup);
                    result = ((BitmapDrawable)viewHolder.mImg.getDrawable()).getBitmap();
                }
                if(famous_dance_group) {
                    result = Utils.getRoundedCornerBitmap(result);
                }
                viewHolder.mImg.setImageBitmap(result);
            }
        });
        viewHolder.mText.setText(names.get(position));
        return convertView;
    }

    private class ViewHolder{
        ImageView mImg;
        TextView mText;
    }

    public static void init_viewGroup(View v, int position, int Length, Context context){
        ViewGroup viewGroup=(ViewGroup) v.findViewById(R.id.viewgroup);
        ImageView[] imageViews;
        imageViews=new ImageView[Length];
        for(int i=0;i<imageViews.length;i++) {
            imageViews[i] = new ImageView(context);
            if (i == position) {
                imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                imageViews[i].setBackgroundResource(R.drawable.page_indicator);
            }
            viewGroup.addView(imageViews[i]);
        }
    }

}
