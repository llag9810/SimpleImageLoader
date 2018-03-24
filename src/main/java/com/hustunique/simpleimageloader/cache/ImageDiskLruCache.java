package com.hustunique.simpleimageloader.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hustunique.simpleimageloader.utils.BitmapUtils;
import com.hustunique.simpleimageloader.utils.MD5Utils;
import com.hustunique.simpleimageloader.utils.NetworkUtils;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;

/**
 * Created by yifan on 3/6/17.
 */

public class ImageDiskLruCache {
    private DiskLruCache diskLruCache;
    private boolean created;
    private SoftReference<Context> context;
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50; // 50MB

    public ImageDiskLruCache(Context context) {
        this.context = new SoftReference<Context>(context);
        File diskCacheDir = getCachePath(context, "bitmap");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }

        if (getAvailableSize(diskCacheDir) > DISK_CACHE_SIZE) {
            try {
                diskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                created = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            created = false;
        }
    }

    public Bitmap get(String url) {
        try {
            return get(url, 0, 0);
        } catch (IOException e) {
            return null;
        }
    }

    public Bitmap get(String url, int reqWidth, int reqHeight) throws IOException {
        Bitmap bitmap = null;
        String key = MD5Utils.hashKeyFromUrl(url);
        DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
        if (snapshot != null) {
            FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(0);
            if (reqWidth <= 0 || reqHeight <= 0) {
                bitmap = BitmapFactory.decodeFileDescriptor(fileInputStream.getFD());
            } else {
                bitmap = BitmapUtils.getCompressedBitmap(fileInputStream.getFD(), reqWidth, reqHeight);
            }
        }
        return bitmap;
    }

    public boolean put(String urlString) {
        String key = MD5Utils.hashKeyFromUrl(urlString);
        try {
            DiskLruCache.Editor editor = diskLruCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (NetworkUtils.downloadUrlToStream(urlString, outputStream)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
                diskLruCache.flush();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private File getCachePath(Context context, String name) {
        final String cachePath = context.getCacheDir().getPath();
        return new File(cachePath + File.separator + name);
    }

    public long getAvailableSize() {
        return getAvailableSize(getCachePath(context.get(), "bitmap"));
    }

    public long getAvailableSize(File path) {
        return path.getUsableSpace();
    }
}
