package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 6/15/17.
 */

public class ViewCreatorTitleLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private final TextView textView;

    private static final float MAX_WIDTH_RATIO = 8.0f / 10.0f;

    public ViewCreatorTitleLayoutListener(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void onGlobalLayout() {
        float maxAllowedWidth = textView.getRootView().getWidth() * MAX_WIDTH_RATIO;
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(textView.getText().toString(),
                0,
                textView.getText().length(),
                bounds);
        if (bounds.width() > maxAllowedWidth) {
            float resizeRatio = maxAllowedWidth / bounds.width();
            int subStringLength = (int) (((float) textView.getText().length()) * resizeRatio);
            textView.setText(textView.getContext().getString(R.string.string_with_ellipse, textView.getText().subSequence(0, subStringLength - 3)));
        }

        textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }
}
