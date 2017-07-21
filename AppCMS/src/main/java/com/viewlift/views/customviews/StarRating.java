package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.viewlift.R;

/**
 * Created by viewlift on 6/9/17.
 */

public class StarRating extends LinearLayout {
    private final int starBorderColor;
    private final int starFillColor;
    private final float rating;
    private final VectorDrawable starDrawableNoFill;
    private final VectorDrawable starDrawable;

    private static class StarViewGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        final ImageView imageToFill;
        final int borderColor;
        final int fillColor;
        final float percentageToFill;
        final VectorDrawable starDrawableNoFill;
        final VectorDrawable starDrawable;

        public StarViewGlobalLayoutListener(ImageView imageToFill,
                                            int borderColor,
                                            int fillColor,
                                            float percentageToFill,
                                            VectorDrawable starDrawableNoFill,
                                            VectorDrawable starDrawable) {
            this.imageToFill = imageToFill;
            this.borderColor = borderColor;
            this.fillColor = fillColor;
            this.starDrawableNoFill = starDrawableNoFill;
            this.percentageToFill = percentageToFill;
            this.starDrawable = starDrawable;
        }

        @Override
        public void onGlobalLayout() {
            int imageWidth = imageToFill.getWidth();
            int imageHeight = imageToFill.getHeight();
            int drawableWidth = starDrawable.getIntrinsicWidth();
            int drawableHeight = starDrawable.getIntrinsicHeight();

            if (imageHeight > 0 && imageWidth > 0) {

                Bitmap starViewCropped = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(starViewCropped);
                starDrawable.setBounds(0, 0, imageWidth, imageHeight);
                starDrawable.setColorFilter(new PorterDuffColorFilter(fillColor, PorterDuff.Mode.MULTIPLY));
                starDrawable.draw(canvas);

                Paint paint = new Paint();
                paint.setColor(borderColor);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                Rect starFilledCroppedRect = new Rect((int) (drawableWidth * percentageToFill),
                        0,
                        drawableWidth,
                        drawableHeight);
                canvas.drawRect(starFilledCroppedRect, paint);
                paint = null;

                starDrawableNoFill.setBounds(0, 0, imageWidth, imageHeight);
                starDrawableNoFill.setColorFilter(new PorterDuffColorFilter(borderColor, PorterDuff.Mode.MULTIPLY));
                starDrawableNoFill.draw(canvas);

                imageToFill.setImageDrawable(new BitmapDrawable(imageToFill.getContext().getResources(), starViewCropped));
            }
        }
    }

    public StarRating(Context context, int starBorderColor, int starFillColor, float rating) {
        super(context);
        this.starBorderColor = starBorderColor;
        this.starFillColor = starFillColor;
        this.rating = rating;
        this.starDrawableNoFill = (VectorDrawable) ContextCompat.getDrawable(getContext(), R.drawable.star_icon_no_fill);
        this.starDrawable = (VectorDrawable) ContextCompat.getDrawable(getContext(), R.drawable.star_icon);
        init();
    }

    public void init() {
        setOrientation(HORIZONTAL);

        float ratingRemainder = rating;
        for (int i = 0; i < 5; i++) {
            LinearLayout.LayoutParams starLayoutParams =
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            starLayoutParams.weight = 1;
            starLayoutParams.gravity = Gravity.CENTER;
            ImageView starView = new ImageView(getContext());
            starView.setLayoutParams(starLayoutParams);
            float fillRatio = ratingRemainder >= 1.0f ? 1.0f : ratingRemainder % 1.0f;
            starView.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new StarViewGlobalLayoutListener(starView,
                            starBorderColor,
                            starFillColor,
                            fillRatio,
                            starDrawableNoFill,
                            starDrawable));
            addView(starView);
            ratingRemainder -= 1.0f;
        }
    }
}
