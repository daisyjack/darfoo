package com.darfoo.darfoolauncher.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by 余晓飞 on 2015/1/7.
 */
public class MultiGridView extends GridView{
    public MultiGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiGridView(Context context) {
        super(context);
    }

    public MultiGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
