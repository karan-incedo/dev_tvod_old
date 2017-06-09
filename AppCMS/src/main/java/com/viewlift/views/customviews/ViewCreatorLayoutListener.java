package com.viewlift.views.customviews;

import android.text.TextUtils;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 6/7/17.
 */

public class ViewCreatorLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private final TextView textView;

    public ViewCreatorLayoutListener(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void onGlobalLayout() {
        int linesCompletelyVisible = (textView.getHeight() - textView.getPaddingTop() - textView.getPaddingBottom()) / textView.getLineHeight();
        textView.setMaxLines(linesCompletelyVisible);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }
}
