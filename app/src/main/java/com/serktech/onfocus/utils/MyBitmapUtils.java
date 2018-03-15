package com.serktech.onfocus.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * bitmap
 */

public class MyBitmapUtils {

    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    public MyBitmapUtils(){
        mMemoryCacheUtils=new MemoryCacheUtils();
        mLocalCacheUtils=new LocalCacheUtils();
        mNetCacheUtils=new NetCacheUtils(mLocalCacheUtils,mMemoryCacheUtils);
    }

    public void disPlay(ImageView ivPic, String url) {
        //ivPic.setImageResource(R.mipmap.test);
        Bitmap bitmap;
        //Memory cache
        bitmap=mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap!=null){
            ivPic.setImageBitmap(bitmap);
            System.out.println("Get pictures from memory.....");
            return;
        }

        //Local cache
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if(bitmap !=null){
            ivPic.setImageBitmap(bitmap);
            System.out.println("Get the image locally.....");
            //After getting the image locally, save to memory
            mMemoryCacheUtils.setBitmapToMemory(url,bitmap);
            return;
        }
        //Network cache
        mNetCacheUtils.getBitmapFromNet(ivPic,url);
    }
}
