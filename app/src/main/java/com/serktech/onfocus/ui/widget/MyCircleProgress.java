package com.serktech.onfocus.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.serktech.onfocus.R;

/**
 * circle
 */

public class MyCircleProgress extends ProgressBar {

    //Define some property constants
    private static final int PROGRESS_DEFAULT_COLOR = 0xCCd3d6da;//the color of the default circle (border)
    private static final int PROGRESS_REACHED_COLOR = 0XFFFFFFFF;//progress bar color
    private static final int PROGRESS_REACHED_HEIGHT = 3;//The height of the progress bar
    private static final int PROGRESS_DEFAULT_HEIGHT = 3;//Default height of the circle
    private static final int PROGRESS_RADIUS = 125;//The radius of the circle

    //Text
    private static final int TEXT_COLOR = Color.parseColor("#CCd3d6da");
    private static final int TEXT_SIZE = 48;

    //View The current state, the default is not started
    private Status mStatus = Status.End;
    //brush
    private Paint mPaint;
    //Text brush
    private Paint mTextPaint;
    //The color of the progress bar
    private int mReachedColor = PROGRESS_REACHED_COLOR;
    //The height of the progress bar
    private int mReachedHeight = dp2px(PROGRESS_REACHED_HEIGHT);
    //The color of the default circle (border)
    private int mDefaultColor = PROGRESS_DEFAULT_COLOR;
    //The height of the default circle (border)
    private int mDefaultHeight = dp2px(PROGRESS_DEFAULT_HEIGHT);
    //The radius of the circle
    private int mRadius = dp2px(PROGRESS_RADIUS);
    //The default text color
    private int textColor = TEXT_COLOR;
    //The default text size
    private int textSize = sp2px(TEXT_SIZE);

    public MyCircleProgress(Context context) {
        this(context,null);
    }

    public MyCircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyCircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //Gets the value of the custom property
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CustomCircleProgress);
        //The default color of the circle
        mDefaultColor = array.getColor(R.styleable.CustomCircleProgress_progress_default_color, PROGRESS_DEFAULT_COLOR);
        //The color of the progress bar
        mReachedColor = array.getColor(R.styleable.CustomCircleProgress_progress_reached_color, PROGRESS_REACHED_COLOR);
        //The height of the default circle
        mDefaultHeight = (int) array.getDimension(R.styleable.CustomCircleProgress_progress_default_height, mDefaultHeight);
        //The height of the progress bar
        mReachedHeight = (int) array.getDimension(R.styleable.CustomCircleProgress_progress_reached_height, mReachedHeight);
        //The radius of the circle
        mRadius = (int) array.getDimension(R.styleable.CustomCircleProgress_circle_radius, mRadius);
        //Finally do not forget to recycle TypedArray
        array.recycle();

        //Set the brush (new brush operation generally not onDraw method, because the onDraw method will be called multiple times in the drawing process)
        setPaint();
    }

    private void setPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //Text brush
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
    }

    /**
     * The onMeasure method is because some of our custom circular View attributes (such as: progress bar width, etc.) are given to the user to customize, so we need to measure
     * See if it meets the requirements
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int paintHeight = Math.max(mReachedHeight, mDefaultHeight);//Compare the two numbers, whichever is greater

        if(heightMode != MeasureSpec.EXACTLY){
            //If the user does not pinpoint the width and height, we measure the height of the entire View. Assign the upper and lower padding defined by the custom Circle View + the diameter of the circular view + the height of the circular stroke border
            int exceptHeight = getPaddingTop() + getPaddingBottom() + mRadius*2 + paintHeight;
            //measured value as the exact value passed to the parent class, tell him I need so much space, you give me the right allocation
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(exceptHeight, MeasureSpec.EXACTLY);
        }
        if(widthMode != MeasureSpec.EXACTLY){
            //custom properties does not set the width of the circular border, so here directly with the height of the
            int exceptWidth = getPaddingLeft() + getPaddingRight() + mRadius*2 + paintHeight;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(exceptWidth, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getPaddingLeft(),getPaddingTop());
        mPaint.setStyle(Paint.Style.STROKE);
        //Draw some settings for the default circle (border)
        mPaint.setColor(mDefaultColor);
        mPaint.setStrokeWidth(mDefaultHeight);
        canvas.drawCircle(mRadius,mRadius,mRadius,mPaint);
        //Draw some of the progress bar settings
        mPaint.setColor(mReachedColor);
        mPaint.setStrokeWidth(mReachedHeight);
        //Draw an arc according to the progress
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius *2), -90, sweepAngle, false, mPaint);
        canvas.restore();

    }

    protected int dp2px(int dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    protected int sp2px(int spVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }

    //The current view shows the status
    public enum Status{
        End,
        Starting
    }

    public Status getStatus(){
        return mStatus;
    }
    public void setStatus(Status status){
        this.mStatus = status;
        invalidate();
    }
}
