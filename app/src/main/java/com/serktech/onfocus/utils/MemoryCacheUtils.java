package com.serktech.onfocus.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Memory on 2017/11/20.
 */

public class MemoryCacheUtils {
    // private HashMap<String,Bitmap> mMemoryCache=new HashMap<>();//1.Because of strong references, which can easily cause memory overflows, consider using the weakly quoted method below
    // private HashMap<String, SoftReference<Bitmap>> mMemoryCache = new HashMap<>();//2.Because Android2.3 +, the system will give priority to the recovery of weak reference object, the official proposed the use of LruCache
    private LruCache<String,Bitmap> mMemoryCache;

    public MemoryCacheUtils(){
        long maxMemory = Runtime.getRuntime().maxMemory()/8;//Get the maximum allowable memory of the phone 1/8, that is, exceed the specified memory, then began to recover
        //Need to pass in the maximum allowable memory, virtual machine default memory 16M, real machine may not be the same
        mMemoryCache=new LruCache<String,Bitmap>((int) maxMemory){
            //Used to calculate the size of each entry
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int byteCount = value.getByteCount();
                return byteCount;
            }
        };

    }

    /**
     * Read picture from memory
     * @param url
     */
    public Bitmap getBitmapFromMemory(String url) {
        //Bitmap bitmap = mMemoryCache.get(url);//1.Strong reference method
            /*2.Weak reference method
            SoftReference<Bitmap> bitmapSoftReference = mMemoryCache.get(url);
            if (bitmapSoftReference != null) {
                Bitmap bitmap = bitmapSoftReference.get();
                return bitmap;
            }
            */
        Bitmap bitmap = mMemoryCache.get(url);
        return bitmap;

    }

    /**
     * Write pictures to memory
     * @param url
     * @param bitmap
     */
    public void setBitmapToMemory(String url, Bitmap bitmap) {
        //mMemoryCache.put(url, bitmap);//1.Strong reference method
            /*2.Weak reference method
            mMemoryCache.put(url, new SoftReference<>(bitmap));
            */
        mMemoryCache.put(url,bitmap);
    }
}

