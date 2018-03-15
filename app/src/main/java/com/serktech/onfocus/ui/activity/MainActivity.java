package com.serktech.onfocus.ui.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.serktech.onfocus.R;
import com.serktech.onfocus.server.MusicService;
import com.serktech.onfocus.ui.adapter.ViewFragmentAdapter;
import com.serktech.onfocus.ui.fragment.ClassicFragment;
import com.serktech.onfocus.ui.fragment.DynamicFragment;
import com.serktech.onfocus.ui.fragment.ForestFragment;
import com.serktech.onfocus.ui.fragment.RainFragment;
import com.serktech.onfocus.ui.fragment.WaveFragment;
import com.serktech.onfocus.ui.widget.MyCircleProgress;
import com.serktech.onfocus.ui.widget.MyNumberPicker;
import com.serktech.onfocus.ui.widget.MyTimeCountDown;
import com.serktech.onfocus.ui.widget.MyViewPager;
import com.serktech.onfocus.ui.widget.OnTimeCompleteListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * todo-list: Large picture OOM && button animation
 * todo-list:BUG：Service life cycle problems Description: When the AC destroyed, the service continues to run
 */

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, NoticeImp, OnTimeCompleteListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.viewpager)
    MyViewPager mViewpager;
    @BindView(R.id.bt_play)
    Button mBtPlay;
    @BindView(R.id.bt_pause)
    Button mBtPause;
    @BindView(R.id.bt_continute)
    Button mBtContinute;
    @BindView(R.id.bt_giveup)
    Button mBtGiveup;
    @BindView(R.id.daily_text)
    TextView mDayilText;
    @BindView(R.id.circleProgress)
    MyCircleProgress mCircleProgress;
    @BindView(R.id.mid_text)
    TextView mMidText;
    @BindView(R.id.num)
    MyNumberPicker mNumPick;
    @BindView(R.id.timer)
    MyTimeCountDown mTimer;

    private ViewFragmentAdapter mAdapter;
    private List<Fragment> mFragments;
    private MusicService mMusicService;
    private Animation mAnimation;
    private String[] mSentenceArrays;
    private String[] mMidTextArrays;
    private String daily_sentece = null;

    public static int mPosition;
    private static final int NO_f = 0x1;
    public static final int PROGRESS_CIRCLE_STARTING = 0x110;
    private final String[] times = {"05","10","15","20","25","30",
            "35","45","50","55","60"};
    private int progress;
    private float countTime;
    private float t1;
    private float t2;
    private boolean isPause = false;

//    private NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PROGRESS_CIRCLE_STARTING:
                    progress = mCircleProgress.getProgress();
                    mCircleProgress.setProgress(++progress);
                    if(progress >= 100){
                        handler.removeMessages(PROGRESS_CIRCLE_STARTING);
                        progress = 0;
                        mCircleProgress.setProgress(0);
                        mCircleProgress.setStatus(MyCircleProgress.Status.End);
                    }else{
                        handler.sendEmptyMessageDelayed(PROGRESS_CIRCLE_STARTING, (long)(t1 * 1000));
                    }
                    break;
            }
        }
    };


    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMusicService = ((MusicService.MyBinder) iBinder).getMusicService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicService = null;
        }
    };

    private void bindServiceConnection() {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, conn, this.BIND_AUTO_CREATE);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        initBtn();
        initFragments();
        initTextView();
        initNumberPicker();
        mTimer.setOnTimeCompleteListener(this);
        mCircleProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mMidText.setVisibility(View.GONE);
                    mTimer.setVisibility(View.GONE);
                    mNumPick.setVisibility(View.VISIBLE);
            }
        });
        mMusicService = new MusicService();
        bindServiceConnection();
        //TODO:OOM TEST
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.d("TAG", "Max memory is " + maxMemory + "KB");
    }

    private void initNumberPicker() {
        mNumPick.setDisplayedValues(times);
        mNumPick.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);//Not clickable in the middle
        mNumPick.setMaxValue(times.length-1);
        mNumPick.setWrapSelectorWheel(false);
        mTimer.initTime(300);
        t1 = 300/100;
        mNumPick.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mNumPick.setValue(newVal);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMidText.setVisibility(View.GONE);
                        mNumPick.setVisibility(View.VISIBLE);
                        mBtPlay.setClickable(true);
                    }
                },1350);
                mBtPlay.setClickable(false);
                countTime = Integer.parseInt(times[newVal]);
                t1 = ((60*countTime)/100);
                t2 = 60*countTime;
                mTimer.initTime((long) t2);
            }
        });
    }

    private void initBtn() {
        mBtPlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                    v.setBackgroundResource(R.drawable.shape_play_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE) {
                    v.setBackgroundResource(R.drawable.shape_play);
                }
                return false;
            }
        });

        mBtPause.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                    v.setBackgroundResource(R.drawable.shape_pause_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE) {
                    v.setBackgroundResource(R.drawable.shape_pause);
                }
                return false;
            }
        });
        mBtContinute.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                    v.setBackgroundResource(R.drawable.shape_continute_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE) {
                    v.setBackgroundResource(R.drawable.shape_continute);
                }
                return false;
            }
        });
        mBtGiveup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                    v.setBackgroundResource(R.drawable.shape_pause_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE) {
                    v.setBackgroundResource(R.drawable.shape_pause);
                }
                return false;
            }
        });
    }

    private void initFragments() {
        mFragments = new ArrayList<Fragment>();
        mFragments.add(new DynamicFragment());
        mFragments.add(new RainFragment());
        mFragments.add(new ForestFragment());
        mFragments.add(new WaveFragment());
        mFragments.add(new ClassicFragment());
        mAdapter = new ViewFragmentAdapter(getSupportFragmentManager(), mFragments);
        mViewpager.setAdapter(mAdapter);
        mViewpager.addOnPageChangeListener(this);
        //TODO:set bigger pic
        mViewpager.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.a3));
    }

    private void initTextView() {
        Typeface mTypeFace = Typeface.createFromAsset(getAssets(),
                "Roboto-Light.ttf");
        mDayilText.setTypeface(mTypeFace);
        mMidText.setTypeface(mTypeFace);
        mTimer.setTypeface(mTypeFace);
        mSentenceArrays = this.getResources().getStringArray(R.array.daily_sentence);
        int id = (int) (Math.random() * (mSentenceArrays.length - 1));
        daily_sentece = mSentenceArrays[id];
        mDayilText.setText(daily_sentece);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.mPosition = position;
        mMidTextArrays = this.getResources().getStringArray(R.array.midText);
        switch (mPosition){
            case 0:
                mMidText.setText(mMidTextArrays[0]);
                break;
            case 1:
                mMidText.setText(mMidTextArrays[1]);
                break;
            case 2:
                mMidText.setText(mMidTextArrays[2]);
                break;
            case 3:
                mMidText.setText(mMidTextArrays[3]);
                break;
            case 4:
                mMidText.setText(mMidTextArrays[4]);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @OnClick({R.id.bt_play, R.id.bt_pause, R.id.bt_continute, R.id.bt_giveup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_play:
//                mAnimation = AnimationUtils.loadAnimation(this,R.anim.play_bt);
//                mBtPlay.startAnimation(mAnimation);
                NoticePlay();
                mMusicService.play();
                mBtPlay.setVisibility(View.GONE);
                mBtPause.setVisibility(View.VISIBLE);
                mBtContinute.setVisibility(View.GONE);
                mBtGiveup.setVisibility(View.GONE);
                mCircleProgress.setStatus(MyCircleProgress.Status.Starting);
                mCircleProgress.setClickable(true);
                Message message = Message.obtain();
                message.what = PROGRESS_CIRCLE_STARTING;
                handler.sendMessage(message);
                mTimer.setVisibility(View.VISIBLE);
                mMidText.setVisibility(View.GONE);
                mNumPick.setVisibility(View.GONE);
                if(isPause == true){
                    mTimer.TimeContinute();
                }else {
                    mTimer.reStart();
                }
                break;
            case R.id.bt_pause:
                NoticePause();
                //mAnimation = AnimationUtils.loadAnimation(this,R.anim.pause_left);
                //mBtPause.startAnimation(mAnimation);
                mMusicService.pause();
                mBtPlay.setVisibility(View.GONE);
                mBtPause.setVisibility(View.GONE);
                mBtContinute.setVisibility(View.VISIBLE);
                mBtGiveup.setVisibility(View.VISIBLE);
                mCircleProgress.setClickable(false);
                if(mCircleProgress.getStatus() == MyCircleProgress.Status.Starting) {
                    mCircleProgress.setStatus(MyCircleProgress.Status.End);
                    handler.removeMessages(PROGRESS_CIRCLE_STARTING);
                    mTimer.TimePause();
                    isPause = true;
                    mTimer.setVisibility(View.VISIBLE);
                    mMidText.setVisibility(View.GONE);
                    mNumPick.setVisibility(View.GONE);
                }
                break;
            case R.id.bt_continute:
                NoticePlay();
//                mAnimation = AnimationUtils.loadAnimation(this,R.anim.pause_left);
//                mBtContinute.startAnimation(mAnimation);
                mMusicService.play();
                mBtPlay.setVisibility(View.GONE);
                mBtPause.setVisibility(View.VISIBLE);
                mBtContinute.setVisibility(View.GONE);
                mBtGiveup.setVisibility(View.GONE);
                mCircleProgress.setStatus(MyCircleProgress.Status.Starting);
                mCircleProgress.setClickable(false);
                Message message1 = Message.obtain();
                message1.what = PROGRESS_CIRCLE_STARTING;
                handler.sendMessage(message1);
                mTimer.setVisibility(View.VISIBLE);
                mMidText.setVisibility(View.GONE);
                mNumPick.setVisibility(View.GONE);
                if(isPause == true){
                    mTimer.TimeContinute();
                }else {
                    mTimer.reStart();
                }
                break;
            case R.id.bt_giveup:
                NoticeCancel();
//                mAnimation = AnimationUtils.loadAnimation(this,R.anim.pause_right);
//                mBtGiveup.startAnimation(mAnimation);
                mMusicService.pause();
                mBtPlay.setVisibility(View.VISIBLE);
                mBtPause.setVisibility(View.GONE);
                mBtContinute.setVisibility(View.GONE);
                mBtGiveup.setVisibility(View.GONE);
                mCircleProgress.setProgress(0);
                mCircleProgress.setClickable(true);
                mTimer.setVisibility(View.GONE);
                mNumPick.setVisibility(View.GONE);
                mMidText.setVisibility(View.VISIBLE);
                mTimer.stop();
                mTimer.initTime(300);
                t1 = 3;
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//        manager.cancelAll();
    }

    @Override
    public void NoticePlay() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pi);
//        builder.setSmallIcon(R.drawable.ic_notify_icon_red);
//        builder.setContentTitle("onFocus");
//        builder.setContentText("Concentrate");
//        builder.setSmallIcon(R.drawable.ic_notify_icon_red);
//        Notification n = builder.build();
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.notify(NO_f, n);
    }

    @Override
    public void NoticePause() {
//        builder.setSmallIcon(R.drawable.ic_notify_icon_red);
//        builder.setContentTitle("onFocus");
//        builder.setContentText("Focus has been suspended");
//        builder.setSmallIcon(R.drawable.ic_notify_icon_red);
//        Notification n = builder.build();
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.notify(NO_f, n);
    }

    @Override
    public void NoticeCancel() {
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.cancelAll();
    }

    @Override
    public void onTimeComplete() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pi);
//        builder.setSmallIcon(R.drawable.ic_notify_icon_red);
//        builder.setContentTitle("onFocus");
//        builder.setContentText("Focus end");
//        builder.setSmallIcon(R.drawable.ic_notify_icon_red);
//        Notification n = builder.build();
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//        manager.notify(NO_f, n);

        mMusicService.pause();
        mBtPlay.setVisibility(View.VISIBLE);
        mBtPause.setVisibility(View.GONE);
        mBtContinute.setVisibility(View.GONE);
        mBtGiveup.setVisibility(View.GONE);
        mCircleProgress.setProgress(0);
        mCircleProgress.setClickable(true);
        mTimer.setVisibility(View.GONE);
        mNumPick.setVisibility(View.GONE);
        mMidText.setVisibility(View.VISIBLE);
        mTimer.stop();
        mTimer.initTime(300);
        t1 = 3;
    }
}
