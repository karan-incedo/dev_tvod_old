package com.viewlift.views.utilities;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by viewlift on 11/6/17.
 */

public class ImageUtils {
    private static ImageLoader registeredImageLoader;

    public static void registerImageLoader(ImageLoader imageLoader) {
        registeredImageLoader = imageLoader;
    }

    public static ImageView createImageView(Context context) {
        if (registeredImageLoader != null) {
            return registeredImageLoader.createImageView(context);
        }
        return null;
    }

    public static boolean loadImage(ImageView view,
                                    String url,
                                    ImageLoader.ScaleType scaleType) {
        if (registeredImageLoader != null) {
            registeredImageLoader.loadImage(view, url, scaleType);
            return true;
        }
        return false;
    }

    public static boolean loadImageWithLinearGradient(ImageView view,
                                                   String url,
                                                   int imageWidth,
                                                   int imageHeight) {
        if (registeredImageLoader != null) {
            registeredImageLoader.loadImageWithLinearGradient(view,
                    url,
                    imageWidth,
                    imageHeight);
            return true;
        }
        return false;
    }
}
