package com.hustunique.simpleimageloader.thread;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import com.hustunique.imageloader.ImageLoader;
import com.hustunique.simpleimageloader.utils.MD5Utils;
import com.hustunique.simpleimageloader.utils.NetworkUtils;

import java.io.IOException;

/**
 * Created by yifan on 2/24/17.
 */

public class LoadTaskRunnable implements Runnable {
    public static final int MESSAGE_POST_RESULT = 1;
    private boolean mIsDiskLruCacheCreated = false;
    private Context mContext;
    private String uri;
    private int reqWidth;
    private int reqHeight;
    private ImageView imageView;
    private ImageLoader.BitmapCallback callback;

    private Handler imageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_POST_RESULT) {
                TaskResult result = (TaskResult) msg.obj;
                ImageView imageView = result.imageView;

                if (result.bitmap != null) {
                    imageView.setImageBitmap(result.bitmap);
                }
            }
        }
    };

    public LoadTaskRunnable(Context context, ImageView imageview, String uri, int reqWidth, int reqHeight) {
        this.uri = uri;
        this.reqHeight = reqHeight;
        this.reqWidth = reqWidth;
        this.imageView = imageview;
        mContext = context.getApplicationContext();
    }

    public LoadTaskRunnable(Context context, ImageLoader.BitmapCallback callback, String uri, int reqWidth, int reqHeight) {
        this.callback = callback;
        this.uri = uri;
        this.reqHeight = reqHeight;
        this.reqWidth = reqWidth;
        mContext = context.getApplicationContext();
    }

    @Override
    public void run() {
        final Bitmap bitmap = loadBitmap(uri, reqWidth, reqHeight);

        TaskResult loaderResult = new TaskResult(imageView, bitmap);
        imageHandler.obtainMessage(MESSAGE_POST_RESULT, loaderResult).sendToTarget();

        if (callback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onResponse(bitmap);
                }
            });

        }
    }

    /**
     * get Bitmap from the local memory or Url
     *
     * @param uri       url
     * @param reqWidth  the requested width
     * @param reqHeight the requested height
     * @return
     */
    private Bitmap loadBitmap(String uri, int reqWidth, int reqHeight) {
        Bitmap bitmap = null;
        try {
            bitmap = ImageLoader.getImageDiskLruCache(mContext).get(uri, reqWidth, reqHeight);
            if (bitmap != null) {

                ImageLoader.getImageLruCache().put(MD5Utils.hashKeyFromUrl(uri), bitmap);
                return bitmap;
            } else {
                bitmap = loadBitmapFromHttp(uri, reqWidth, reqHeight);
                if (bitmap != null) {
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap == null && !mIsDiskLruCacheCreated) {
            bitmap = NetworkUtils.downloadBitmapFromUrl(uri);
        }
        return bitmap;
    }

    private Bitmap loadBitmapFromHttp(String url, int reqWidth, int reqHeight) throws IOException {
        ImageLoader.getImageDiskLruCache(mContext).get(url);
        return ImageLoader.getImageDiskLruCache(mContext).get(url, reqWidth, reqHeight);
    }

}
