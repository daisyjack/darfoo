package com.darfoo.darfoolauncher.fragment;

import com.darfoo.darfoolauncher.support.Cryptor;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.darfoo.darfoolauncher.R;
import com.darfoo.darfoolauncher.support.OnVideoClickListener;
import com.darfoo.darfoolauncher.support.Utils;
import com.darfoo.mvplayer.VideoPlayerActivity;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import dafoo.video.OnlineJsonAdapter;

public class RecommendationFragment extends Fragment {

    private ViewPager mViewpager;
    private ImageButton mLeftPagerBtn;
    private ImageButton mRightPagerBtn;
    private ProgressBar mProgressBar;
    private ScrollView mScrollView;
    private MultiGridView mGridView;
    private ViewGroup mIndicatorGroup;
    private List<ImageView> mImageViewList;
    private List<ImageView> mIndicatorList;
    private android.os.Handler handler;
    private Runnable mNextPageRunnable;

    public static final long AUTO_SCROLL_TIME = 5000;
    private int mCurrentItemPos = 0;

    public RecommendationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.d("RecommendationFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_recommendation, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar_loading);
        mScrollView = (ScrollView) view.findViewById(R.id.recommend_scrollView);
        mScrollView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

        initRecommendedVideo(view);

        initLatestVideo(view);

        return view;
    }

    private void initRecommendedVideo(View view){
        String requestUrl = Utils.BASE_URL + "/resources/video/recommend";
        final View this_view = view;
        final RecommendationFragment this_content = this;
        Ion.with(this).load(requestUrl).asJsonArray().setCallback(new FutureCallback<JsonArray>(){
            List<String> names = new ArrayList<String>();
            List<String> image_urls = new ArrayList<String>();
            List<String> authornames = new ArrayList<String>();
            List<String> video_urls = new ArrayList<String>();
            List<Integer> ids = new ArrayList<Integer>();
            List<Integer> update_timestamps = new ArrayList<Integer>();
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                if (e != null) {return;}
                mProgressBar.setVisibility(View.INVISIBLE);
                mScrollView.setVisibility(View.VISIBLE);
                for (int i = 0; i < result.size(); i++){
                    JsonObject temp = (JsonObject) result.get(i);
                    String title = temp.get("title").getAsString();
                    String image_url = temp.get("image_url").getAsString();
                    String authorname = temp.get("authorname").getAsString();
                    String video_url = temp.get("video_url").getAsString();
                    Integer id = temp.get("id").getAsInt();
                    Integer update_timestamp = temp.get("update_timestamp").getAsInt();
                    names.add(title);
                    image_urls.add(image_url);
                    video_urls.add(video_url);
                    authornames.add(authorname);
                    ids.add(id);
                    update_timestamps.add(update_timestamp);
                }

                //获取需要显示的封面
                mImageViewList = new ArrayList<ImageView>();
                for (int i = 0; i < image_urls.size(); i++) {

                    String viewPagerImageUrl = image_urls.get(i);
                    if(viewPagerImageUrl != null) {
                        viewPagerImageUrl = Cryptor.decryptQiniuUrl(viewPagerImageUrl);
                    }
                    ImageView imageView = new ImageView(getActivity());
                    Ion.with(getActivity())
                            .load(viewPagerImageUrl)
                            .withBitmap()
                            .placeholder(R.drawable.wait)
                            .error(R.drawable.wait)
                            .intoImageView(imageView);

                    if((i == 0 || i == 2)&& image_urls.size() >= 3) {
                        imageView.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    }
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    mImageViewList.add(imageView);
                }

                //获取指示器小圆点
                mIndicatorList = new ArrayList<ImageView>();
                mIndicatorGroup= (ViewGroup)this_view.findViewById(R.id.indicator_group);
                LinearLayout.LayoutParams llp =  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                llp.setMargins(0, 0, 5, 0);
                
                for(int i = 0; i < mImageViewList.size(); i++) {
                    ImageView imageView = new ImageView(this_content.getActivity());
                    imageView.setImageResource(R.drawable.page_indicator);
                    imageView.setLayoutParams(llp);
                    mIndicatorGroup.addView(imageView);
                    mIndicatorList.add(imageView);
                }


                for(int i = 0; i < mImageViewList.size(); i ++) {
                    mImageViewList.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = mImageViewList.indexOf(v);
                            Intent intent = new Intent(this_content.getActivity(), VideoPlayerActivity.class);
                            int BundleSize = 5;
                            String[] ImageUrlArray = new String[BundleSize];
                            String[] VideoUrlArray = new String[BundleSize];
                            String[] TitleArray = new String[BundleSize];
                            String[] AuthorNamesArray = new String[BundleSize];
                            int[] Ids = new int[BundleSize];
                            int[] UpdateTimeStamps = new int[BundleSize];
                            for (int i = 0; i < BundleSize; i++) {
                                TitleArray[i] = names.get((position + i - 2 + names.size()) % names.size());
                                ImageUrlArray[i] = image_urls.get((position + i - 2 + image_urls.size()) % image_urls.size());
                                VideoUrlArray[i] = video_urls.get((position + i - 2 + video_urls.size()) % video_urls.size());
                                AuthorNamesArray[i] = authornames.get((position + i - 2 + authornames.size()) % authornames.size());
                                Ids[i] = ids.get((position + i - 2 + ids.size()) % ids.size());
                                UpdateTimeStamps[i] = update_timestamps.get((position + i - 2 + update_timestamps.size()) % update_timestamps.size());
                            }
                            Bundle bundle = new Bundle();
                            bundle.putInt("status", 2);
                            bundle.putIntArray("IdArray", Ids);
                            bundle.putIntArray("TypeArray", new int[] {1, 1, 1, 1, 1, 1, 1});
                            bundle.putStringArray("ImageArray", ImageUrlArray);
                            bundle.putStringArray("UrlArray", VideoUrlArray);
                            bundle.putStringArray("TitleArray", TitleArray);
                            bundle.putStringArray("AuthorArray", AuthorNamesArray);
                            bundle.putIntArray("UpdateArray", UpdateTimeStamps);
                            intent.putExtra("message", bundle);
                            startActivity(intent);
                        }
                    });
                }
                mViewpager = (ViewPager)this_view.findViewById(R.id.recommend_viewpager);
                mLeftPagerBtn = (ImageButton)this_view.findViewById(R.id.recommend_left_button);
                mRightPagerBtn = (ImageButton)this_view.findViewById(R.id.recommend_right_button);

                final RecommendPageAdapter adapter = new RecommendPageAdapter(this_content.getActivity(), mImageViewList, names);
                mViewpager.setAdapter(adapter);
                mViewpager.setOffscreenPageLimit(3);
                mViewpager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.viewpager_page_margin));
                mCurrentItemPos = mImageViewList.size() * 300 + 1;
                mViewpager.setCurrentItem(mImageViewList.size() * 300 + 1);

//                mViewpager.setPageTransformer(false, new ZoomOutPageTransformer());
                mViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i2) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        mCurrentItemPos = i;
                        for(int j = 0; j < mIndicatorList.size(); j++) {
                            if (j == (i % mImageViewList.size())) {
                                mIndicatorList.get(j).setImageResource(R.drawable.page_indicator_focused);
                            } else {
                                mIndicatorList.get(j).setImageResource(R.drawable.page_indicator);
                            }
                        }


                        Drawable drawable;
                        if(i - 1 >= 0) {
                             drawable = mImageViewList.get((i - 1) % mImageViewList.size()).getDrawable();
                            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                            mImageViewList.get((i - 1) % mImageViewList.size()).setImageDrawable(drawable);
                            mImageViewList.get((i - 1) % mImageViewList.size()).invalidate();
                        }


                             drawable = mImageViewList.get((i + 1) % mImageViewList.size()).getDrawable();
                            drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                            mImageViewList.get((i + 1) % mImageViewList.size()).setImageDrawable(drawable);
                            mImageViewList.get((i + 1) % mImageViewList.size()).invalidate();


                         drawable = mImageViewList.get(i % mImageViewList.size()).getDrawable();
                        drawable.clearColorFilter();
                        mImageViewList.get(i % mImageViewList.size()).setImageDrawable(drawable);
                        mImageViewList.get(i % mImageViewList.size()).invalidate();

                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {


                    }
                });

                mIndicatorList.get(1).setImageResource(R.drawable.page_indicator_focused);
                mLeftPagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIndicatorList.get(mCurrentItemPos % mImageViewList.size()).setImageResource(R.drawable.page_indicator);
                        mCurrentItemPos = mViewpager.getCurrentItem() - 1;
                        if(mCurrentItemPos < 0) {
                            mCurrentItemPos = mImageViewList.size() - 1;
                        }
                        mViewpager.setCurrentItem(mCurrentItemPos, true);
                        mIndicatorList.get(mCurrentItemPos % mImageViewList.size()).setImageResource(R.drawable.page_indicator_focused);
                    }
                });

                mRightPagerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIndicatorList.get(mCurrentItemPos % mImageViewList.size()).setImageResource(R.drawable.page_indicator);
                        mCurrentItemPos = (mViewpager.getCurrentItem() + 1) ;
                        /*if(mCurrentItemPos >= mImageViewList.size()) {
                            mCurrentItemPos = 0;
                        }*/
                        mViewpager.setCurrentItem(mCurrentItemPos, true);
                        mIndicatorList.get(mCurrentItemPos % mImageViewList.size()).setImageResource(R.drawable.page_indicator_focused);
                    }
                });

                handler = new android.os.Handler();
                mNextPageRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if(mImageViewList != null && mCurrentItemPos + 1 < mImageViewList.size() * 1000 ) {
                            mViewpager.setCurrentItem(mCurrentItemPos + 1, true);
                        } else {
                            mViewpager.setCurrentItem(mImageViewList.size() * 300, true);
                        }

                        handler.postDelayed(this, AUTO_SCROLL_TIME);

                    }
                };
                //viewpager图片轮播
                handler.post(mNextPageRunnable);

            }

        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(handler != null) {

            handler.removeCallbacks(mNextPageRunnable);
        }
        Log.d("RecommendationFragment", "onDestroyView ");

    }



    public void initLatestVideo(View view){
        String requestUrl = Utils.BASE_URL + "/resources/video/index";
        mGridView = (MultiGridView)view.findViewById(R.id.gridview_content);
        Ion.with(this).load(requestUrl).asJsonArray().setCallback(new FutureCallback<JsonArray>() {

            @Override
            public void onCompleted(Exception e, JsonArray result) {
                if (e != null) {
                    e.printStackTrace();
                    mGridView.setVisibility(View.VISIBLE);
                    return;
                }

                OnlineJsonAdapter onlineJsonAdapter = new OnlineJsonAdapter(getActivity(),result);
                mGridView.setAdapter(onlineJsonAdapter);
                mGridView.setOnItemClickListener(new OnVideoClickListener(getActivity()));

            }
        });
    }


}
