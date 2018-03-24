package com.hustunique.simpleimageloader.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;

/**
 * Created by yifan on 4/3/17.
 */

public class BitmapUtils {
    private static final String TAG = "BitmapUtils";

    /**
     * Compute Sample size of a bitmap according to the requested width and height.
     *
     * @param options A BitmapFactory.Options is needed to get the original size of the bitmap.
     * @param requestWidth the width you requested to compress.
     * @param requestHeight the height you requested to compress.
     * @return Sample size
     */
    public static int getInSampleSize(BitmapFactory.Options options,
                                      int requestWidth, int requestHeight) {
        final int originalWidth = options.outWidth;
        final int originalHeight = options.outHeight;
        int inSampleSize = 1;
        if (originalHeight > requestHeight || originalWidth > requestWidth) {

            final int halfHeight = originalHeight / 2;
            final int halfWidth = originalWidth / 2;
            while ((halfHeight / inSampleSize) >= requestHeight
                    && (halfWidth / inSampleSize) >= requestWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * get the compressed bitmap of the given path and requested width and height.
     *
     * @param filePath the file path of the image
     * @param reqWidth requested width to compress
     * @param reqHeight requested height to compress
     * @return the compressed bitmap
     */
    public static Bitmap getCompressedBitmap(String filePath, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        if (reqHeight != 0 && reqWidth != 0) {
            // Calculate inSampleSize
            options.inSampleSize = getInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            //不需要缩放
            if (options.inSampleSize <= 1) {
                return BitmapFactory.decodeFile(filePath);
            }
        }
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * get the compressed bitmap of the given fd and requested width and height.
     *
     * @param fd the file descriptor of the image
     * @param reqWidth requested width to compress
     * @param reqHeight requested height to compress
     * @return the compressed bitmap
     */
    public static Bitmap getCompressedBitmap(FileDescriptor fd, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        // Calculate inSampleSize
        options.inSampleSize = getInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        //不需要缩放
        if (options.inSampleSize <= 1) {
            return BitmapFactory.decodeFileDescriptor(fd);
        }
        //inSampleSize！=1进行缩放
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }
}
