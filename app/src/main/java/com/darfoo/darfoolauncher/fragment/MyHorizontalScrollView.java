package com.darfoo.darfoolauncher.fragment;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.darfoo.darfoolauncher.R;

public class MyHorizontalScrollView extends HorizontalScrollView implements OnClickListener
{
    //图片滚动时的回调接口
    public interface CurrentImageChangeListener{
    }
    //条目点击时的回调
    public interface OnItemClickListener{
        void onClick(View view, int pos);
    }

    private OnItemClickListener mOnClickListener;
    private static final String TAG = "MyHorizontalScrollView";
    //HorizontalListView中的LinearLayout
    private LinearLayout mContainer;
    //子元素的宽度
    private int mChildWidth;
    //子元素的高度
    private int mChildHeight;
    //当前最后一张图片的index
    private int mCurrentIndex;
    //当前第一张图片的下标
    private int mFristIndex;
    private HorizontalScrollViewAdapter mAdapter;
    //每屏幕最多显示的个数
    private int mCountOneScreen;
    //屏幕的宽度
    private int mScreenWitdh;
    //保存View与位置的键值对
    private Map<View, Integer> mViewPos = new HashMap<View, Integer>();
    //是否是推荐视频
    private boolean recommend = false;

    public MyHorizontalScrollView(Context context, AttributeSet attrs){
        super(context, attrs);
        // 获得屏幕宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWitdh = outMetrics.widthPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mContainer = (LinearLayout) getChildAt(0);
    }
    //设置推荐值 如果是推荐 有图形变换
    public void setRecommend(boolean recommend){
        this.recommend = recommend;
    }
    //加载下一张图片
    protected void loadNextImg(){
        // 数组边界值计算
        if (mCurrentIndex == mAdapter.getCount() - 1){
            mCurrentIndex = -1;
        }
        //移除第一张图片，且将水平滚动位置置0
        scrollTo(0, 0);
        mViewPos.remove(mContainer.getChildAt(0));
        mContainer.removeViewAt(0);
        //获取下一张图片，并且设置onclick事件，且加入容器中
        View view = mAdapter.getView(++mCurrentIndex, null, mContainer);
        view.setOnClickListener(this);
        mContainer.addView(view);
        mViewPos.put(view, mCurrentIndex);
        //当前第一张图片小标
        mFristIndex++;
        mFristIndex = mFristIndex % mAdapter.getCount();
    }
    //加载前一张图片
    protected void loadPreImg(){
        //如果当前已经是第一张，则
        if (mFristIndex == 0)
            mFristIndex = mAdapter.getCount();
        //获得当前应该显示为第一张图片的下标
        int index = mCurrentIndex - mCountOneScreen;
        if (index < 0){
            index = index + mAdapter.getCount();
        }
        //移除最后一张
        int oldViewPos = mContainer.getChildCount() - 1;
        mViewPos.remove(mContainer.getChildAt(oldViewPos));
        mContainer.removeViewAt(oldViewPos);
        //将此View放入第一个位置
        View view = mAdapter.getView(index, null, mContainer);
        mViewPos.put(view, index);
        mContainer.addView(view, 0);
        view.setOnClickListener(this);
        //水平滚动位置向左移动view的宽度个像素
        scrollTo(mChildWidth, 0);
        //当前位置--，当前第一个显示的下标--
        mCurrentIndex--;
        mFristIndex--;
        if(mCurrentIndex<0){
            mCurrentIndex = mCurrentIndex + mAdapter.getCount();
        }
        if(mFristIndex<0){
            mFristIndex = mFristIndex + mAdapter.getCount();
        }
    }

    //滑动时的回调
    public void notifyCurrentImgChanged(){
        //先清除所有的背景色，点击时会设置为蓝色
        for (int i = 0; i < mContainer.getChildCount(); i++){
            //mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);
        }
    }

    //初始化数据，设置数据适配器
    public void initDatas(HorizontalScrollViewAdapter mAdapter){
        this.mAdapter = mAdapter;
        mContainer = (LinearLayout) getChildAt(0);
        // 获得适配器中第一个View
        final View view = mAdapter.getView(0, null, mContainer);
        mContainer.addView(view);
        // 强制计算当前View的宽和高
        if (mChildWidth == 0 && mChildHeight == 0){
            int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            view.measure(w, h);
            mChildHeight = view.getMeasuredHeight();
            mChildWidth = view.getMeasuredWidth();
            Log.e(TAG, view.getMeasuredWidth() + "," + view.getMeasuredHeight());
            mChildHeight = view.getMeasuredHeight();
            // 计算每次加载多少个View;
            //mCountOneScreen = mScreenWitdh / mChildWidth;
            mCountOneScreen = (mScreenWitdh / mChildWidth == 0)?mScreenWitdh / mChildWidth+1:mScreenWitdh / mChildWidth+2;
            Log.e(TAG, "mCountOneScreen = " + mCountOneScreen
                    + " ,mChildWidth = " + mChildWidth);
        }
        if(recommend){
            mCountOneScreen = 1;
        }
        //初始化第一屏幕的元素
        initFirstScreenChildren(mCountOneScreen);
    }

    //加载第一屏的View
    public void initFirstScreenChildren(int mCountOneScreen)
    {
        mContainer = (LinearLayout) getChildAt(0);
        mContainer.removeAllViews();
        mViewPos.clear();
        for (int i = 0; i < mCountOneScreen; i++)
        {
            View view = mAdapter.getView(i, null, mContainer);
            view.setOnClickListener(this);
            mContainer.addView(view);
            mViewPos.put(view, i);
            mCurrentIndex = i;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                int scrollX = getScrollX();
                // 如果当前scrollX为view的宽度，加载下一张，移除第一张
                if (scrollX >= mChildWidth){
                    loadNextImg();
                }
                // 如果当前scrollX = 0， 往前设置一张，移除最后一张
                if (scrollX == 0){
                    loadPreImg();
                }
                notifyCurrentImgChanged();
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void onClick(View v){
        if (mOnClickListener != null){
            if(this.recommend) {
                View turn_left = v.findViewById(R.id.ic_left);
                turn_left.setOnClickListener(new Button.OnClickListener(){
                    public void onClick(View v) {
                        loadPreImg();
                    }});
                View turn_right = v.findViewById(R.id.ic_right);
                turn_right.setOnClickListener(new Button.OnClickListener(){
                    public void onClick(View v) {
                        loadNextImg();
                    }});
                mOnClickListener.onClick(v, mViewPos.get(v));
            }else {
                mOnClickListener.onClick(v, mViewPos.get(v));
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnClickListener){
        this.mOnClickListener = mOnClickListener;
    }

    public void setCurrentImageChangeListener(){
    }
}