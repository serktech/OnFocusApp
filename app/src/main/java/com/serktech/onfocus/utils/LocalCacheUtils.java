package com.serktech.onfocus.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static android.media.tv.TvContract.Programs.Genres.encode;

/**
 * Cache.
 */

public class LocalCacheUtils {
    private static final String CACHE_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/WerbNews";

    /**
     * Read the image locally
     * @param url
     */
    public Bitmap getBitmapFromLocal(String url){
        String fileName = null;//The image url as a file name, and MD5 encryption
        try {
            //fileName = MD5Encoder.encode(url);
            File file=new File(CACHE_PATH,fileName);

            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * After obtaining the picture from the network, save to the local cache
     * @param url
     * @param bitmap
     */
    public void setBitmapToLocal(String url,Bitmap bitmap){
        try {
            //String fileName = MD5Encoder.encode(url);//The image url as a file name, and MD5 encryption
            String fileName = encode(url);
            File file=new File(CACHE_PATH,fileName);

            //By getting the file's parent file, to determine whether the parent file exists
            File parentFile = file.getParentFile();
            if (!parentFile.exists()){
                parentFile.mkdirs();
            }

            //Save the picture locally
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}