package com.viewlift.views.customviews;

import android.graphics.Paint;
import android.graphics.Rect;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.viewlift.R;

/*
 * Created by viewlift on 6/15/17.
 */

public class ViewCreatorTitleLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final float MAX_WIDTH_RATIO = 0.8f;
    private final TextView textView;
    private float specifiedMaxWidthRatio;

    public ViewCreatorTitleLayoutListener(TextView textView) {
        this.textView = textView;
        this.specifiedMaxWidthRatio = MAX_WIDTH_RATIO;
    }

    @Override
    public void onGlobalLayout() {
        float maxAllowedWidth = textView.getRootView().getWidth() * specifiedMaxWidthRatio;
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(textView.getText().toString(),
                0,
                textView.getText().length(),
                bounds);
        if (bounds.width() > maxAllowedWidth) {
            float resizeRatio = maxAllowedWidth / bounds.width();
            int subStringLength = (int) (((float) textView.getText().length()) * resizeRatio);
            if (subStringLength > 0) {
                textView.setText(textView.getContext().getString(R.string.string_with_ellipse,
                        textView.getText().subSequence(0, subStringLength - 3)));
                textView.requestLayout();
            }
        }

        textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    public void setSpecifiedMaxWidthRatio(float specifiedMaxWidthRatio) {
        this.specifiedMaxWidthRatio = specifiedMaxWidthRatio;
    }
}
