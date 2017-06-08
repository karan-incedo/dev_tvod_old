package com.viewlift.views.customviews;

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
        int textStart = textView.getLayout().getLineStart(0);
            if (linesCompletelyVisible < textView.getLayout().getLineCount()) {
            int textEnd = textView.getLayout().getLineEnd(linesCompletelyVisible - 1);
            if (textEnd < textView.getText().length() - 1) {
                StringBuffer sb = new StringBuffer();
                sb.append(textView.getText().subSequence(textStart, textEnd - 3));
                sb.append(textView.getContext().getString(R.string.ellipse_char));
                textView.setText(sb.toString());
            }
        }
    }
}
