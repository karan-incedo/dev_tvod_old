package com.viewlift.presenters;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentManager;
import android.content.Context;

import com.viewlift.presenters.bitmap.ImageCache;

/**
 * Created by viewlift on 1/9/18.
 */

public class BitmapCachePresenter {
    private ImageCache mImageCache;
    private ImageCache.ImageCacheParams mCacheParams;

    public BitmapCachePresenter(Context context,
                                FragmentManager fragmentManager) {
        mCacheParams = new ImageCache.ImageCacheParams(context,
                context.getCacheDir().getPath());
        mImageCache = ImageCache.getInstance(fragmentManager,
                mCacheParams);
    }

    public void addBitmapToCache(Context context, String data, Bitmap value) {
        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), value);
        mImageCache.addBitmapToCache(data, drawable);
    }

    public Bitmap getBitmapFromMemCache(String data) {
        return mImageCache.getBitmapFromDiskCache(data);
    }
}
