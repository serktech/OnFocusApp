package com.serktech.onfocus.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.serktech.onfocus.R;
import com.serktech.onfocus.server.MusicService;
import com.serktech.onfocus.ui.widget.MyScrollView;
import com.serktech.onfocus.ui.widget.TranslucentListener;

/**
 * todo-list:Priority 2: no transparency at the top, custom sliding effects fine-tuning
 * todo-list:Priority 3: bottom navigation color, location
 * todo-list:Priority 4: navBt full-screen click event
 */
public class AboutActivity extends AppCompatActivity implements TranslucentListener {


    private Toolbar mToolbar;
    private MyScrollView mScrollView;
    private MusicService mMusicService;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMusicService = ((MusicService.MyBinder)iBinder).getMusicService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicService = null;
        }
    };

    private void bindServiceConnection() {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("position", MainActivity.mPosition);
        startService(intent);
        bindService(intent, conn, this.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_2);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Back");
//        mToolbar.setAlpha(0f);
        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mScrollView = (MyScrollView) findViewById(R.id.scrollView);
        mScrollView.setTranslucentListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onTranlucent(float alpha,int color, int textColor) {
        mToolbar.setAlpha(2-alpha);
        mToolbar.setBackgroundColor(color);
        mToolbar.setTitleTextColor(textColor);
    }

}
