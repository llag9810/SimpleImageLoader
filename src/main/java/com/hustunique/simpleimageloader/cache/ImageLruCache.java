package com.hustunique.simpleimageloader.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.hustunique.simpleimageloader.utils.MD5Utils;

import java.lang.ref.SoftReference;

/**
 * Created by yifan on 4/3/17.
 */

public class ImageLruCache {

    private static LruCache<String, SoftReference<Bitmap>> cache;

    private static int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
    private static int cacheSize = maxMemory / 8;

    static {
        cache = new LruCache<>(cacheSize);
    }

    public Bitmap get(String url) {
        String key = MD5Utils.hashKeyFromUrl(url);
        SoftReference<Bitmap> ref = cache.get(key);
        return ref == null ? null : ref.get();
    }

    public Bitmap put(String url, Bitmap bitmap) {
        String key = MD5Utils.hashKeyFromUrl(url);
        cache.put(key, new SoftReference<Bitmap>(bitmap));
        return bitmap;
    }

}
