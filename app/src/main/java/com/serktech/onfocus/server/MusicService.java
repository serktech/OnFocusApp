package com.serktech.onfocus.server;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.serktech.onfocus.ui.activity.MainActivity;

/**
 * music
 */

public class MusicService extends Service {

    public MainActivity mActivity = new MainActivity();

    public final Binder mBinder = new MyBinder();

    public final Uri uri0 = null;
    public final Uri uri1 = Uri.parse("android.resource://com.serktech.onfocus/raw/rain");
    public final Uri uri2 = Uri.parse("android.resource://com.serktech.onfocus/raw/forest");
    public final Uri uri3 = Uri.parse("android.resource://com.serktech.onfocus/raw/wave");
    public final Uri uri4 = Uri.parse("android.resource://com.serktech.onfocus/raw/classic");

    public MediaPlayer mp = new MediaPlayer();

    public class MyBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Play in the UI continues and starts directly
     * @param
     */
    public void play() {
        switch (mActivity.mPosition) {
            case 0:
                break;
            case 1:
                mp = MediaPlayer.create(this,uri1);
                mp.start();
                break;
            case 2:
                mp = MediaPlayer.create(this,uri2);
                mp.start();
                break;
            case 3:
                mp = MediaPlayer.create(this,uri3);
                mp.start();
                break;
            case 4:
                mp = MediaPlayer.create(this,uri4);
                mp.start();
                break;
            default:
        }
    }

    /**
     * Pause, cancel
     */
    public void pause() {
        mp.stop();
    }

}
