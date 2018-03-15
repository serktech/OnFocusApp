package com.serktech.onfocus.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Pager
 */

public class MyViewPager extends ViewPager {

    private Bitmap mBitmap;
    private Paint mPaint = new Paint(1);

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(this.mBitmap != null) {
            int width = this.mBitmap.getWidth();
            int height = this.mBitmap.getHeight();
            int count = getAdapter().getCount();
            int x = getScrollX();
            int n =height * getWidth() /getHeight();

            /**
             * (width - n) / (count - 1) indicates the width of the background image to be displayed by the remaining ViewPager, excluding the background width used for displaying the first ViewPager page.
                           * getWidth () is equal to the width of a ViewPager page, that is, the width of the phone screen. This calculation can be interpreted as sliding a ViewPager page need to slide the pixel value.
                           * ((width - n) / (count - 1)) / getWidth () also means the width of the background image when the ViewPager slides by one pixel.
                           * x * ((width - n) / (count - 1)) / getWidth () also means the width of the background image when the ViewPager slides x pixels.
                           * The width of the background picture sliding width can be understood as the position of the background picture sliding arrival.
             */

            int w = x * ((width - n) / (count - 1)) / getWidth();
            canvas.drawBitmap(this.mBitmap, new Rect(w, 0, n + w, height),
                    new Rect( x, 0, x + getWidth(), getHeight()), this.mPaint);
        }
        super.dispatchDraw(canvas);
    }

    public void setBackground(Bitmap paramBitmap) {
        this.mBitmap = paramBitmap;
        this.mPaint.setFilterBitmap(true);
    }

}
