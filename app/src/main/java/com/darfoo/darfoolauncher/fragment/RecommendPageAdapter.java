package com.darfoo.darfoolauncher.fragment;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.darfoo.darfoolauncher.R;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YuXiaofei on 2014/12/23.
 */
public class RecommendPageAdapter extends PagerAdapter{

    private List<ImageView> mImageViewList;

    private List<String> mTitleList;

    private Context mContext;

    public RecommendPageAdapter(Context context, List<ImageView> imageViewList, List<String> titleList) {
        mContext = context;
        mImageViewList = imageViewList;
        mTitleList = titleList;
    }

    @Override
    public int getCount() {
//        return mImageViewList.size();
        return mImageViewList.size() * 1000;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Log.d("viewpager", position + "");
        ImageView imageView = mImageViewList.get(position % mImageViewList.size());

        /*if(imageView.getParent() != null) {
            container.removeView(imageView);
        }*/
        if(imageView.getParent() == null) {
            container.addView(imageView, 0);
        }
        return mImageViewList.get(position % mImageViewList.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        /*if(mImageViewList.get(position % mImageViewList.size()).getParent() != null) {
            ((ViewPager)container).removeView(mImageViewList.get(position % mImageViewList.size()));

        }*/


    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }
}
