package com.viewlift.views.utilities;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by viewlift on 11/6/17.
 */

public interface ImageLoader {
    ImageView createImageView(Context context);
    void loadImage(ImageView view, String url, boolean shouldCenter);
    void loadImageWithLinearGradient(ImageView view,
                                     String url,
                                     int imageWidth,
                                     int imageHeight);
}
