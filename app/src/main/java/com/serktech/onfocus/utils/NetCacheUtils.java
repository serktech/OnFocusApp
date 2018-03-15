package com.serktech.onfocus.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * caches
 */

public class NetCacheUtils {
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;

    public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        mLocalCacheUtils = localCacheUtils;
        mMemoryCacheUtils = memoryCacheUtils;
    }

    /**
     * Download pictures from the web
     * @param ivPic Display pictures imageview
     * @param url   Download the picture of the network address
     */
    public void getBitmapFromNet(ImageView ivPic, String url) {
        new BitmapTask().execute(ivPic, url);//begin AsyncTask

    }

    /**
     * AsyncTask is the package handler and thread pool
           * The first generic: parameter type
           * The second generics: Generic update progress
           * The third generic: onPostExecute the return of results
     */
    class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

        private ImageView ivPic;
        private String url;

        /**
         * Time-consuming operation of the background exists in the sub-thread
         * @param params
         * @return
         */
        @Override
        protected Bitmap doInBackground(Object[] params) {
            ivPic = (ImageView) params[0];
            url = (String) params[1];

            return downLoadBitmap(url);
        }

        /**
         * Update progress, in the main thread
         * @param values
         */
        @Override
        protected void onProgressUpdate(Void[] values) {
            super.onProgressUpdate(values);
        }

        /**
         * After the time-consuming method to implement the method, the main thread
         * @param result
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                ivPic.setImageBitmap(result);
                System.out.println("从网络缓存图片啦.....");

                //After obtaining the picture from the network, save to the local cache
                mLocalCacheUtils.setBitmapToLocal(url, result);
                //Save to memory
                mMemoryCacheUtils.setBitmapToMemory(url, result);

            }
        }
    }

    /**
     * Network download pictures
     * @param url
     * @return
     */
    private Bitmap downLoadBitmap(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                //Image Compression
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize=2;//Wide and high compression of the original 1/2
                options.inPreferredConfig= Bitmap.Config.ARGB_4444;
                Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream(),null,options);
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return null;
    }
}
