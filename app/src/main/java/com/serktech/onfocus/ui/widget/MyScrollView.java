package com.serktech.onfocus.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * scroll
 */

public class MyScrollView extends ScrollView  {

    private TranslucentListener listener;

    public void setTranslucentListener(TranslucentListener listener) {
        this.listener = listener;
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener != null) {
            int scrollY = getScrollY();
            int screen_height = getContext().getResources().getDisplayMetrics().heightPixels;
            if (scrollY <= screen_height / 3f) {//0~1f,The transparency should be 1 ~ 0f
                listener.onTranlucent( 1 - scrollY / (screen_height / 3f), Color.GRAY,Color.WHITE);//alpha=Slide out of the height/(screen_height/3f)
            }
            if(scrollY == 1) {
                listener.onTranlucent(0f,Color.TRANSPARENT,Color.TRANSPARENT);
            }
        }
    }
}