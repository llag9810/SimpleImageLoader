package com.hustunique.simpleimageloader.thread;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by yifan on 3/24/17.
 */

public class TaskResult {
    public ImageView imageView;
    public Bitmap bitmap;

    public TaskResult(ImageView imageView, Bitmap bitmap) {
        this.imageView = imageView;
        this.bitmap = bitmap;

    }
}
