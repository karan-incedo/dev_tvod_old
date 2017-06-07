package com.viewlift.views.customviews;

import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by viewlift on 6/7/17.
 */

public class ViewCreatorLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private final int viewHeight;
    private final TextView textView;

    public ViewCreatorLayoutListener(int viewHeight, TextView textView) {
        this.viewHeight = viewHeight;
        this.textView = textView;
    }

    @Override
    public void onGlobalLayout() {
        int linesCompletelyVisible = viewHeight / textView.getLineHeight();
        int textStart = textView.getLayout().getLineStart(0);
        int textEnd = textView.getLayout().getLineEnd(linesCompletelyVisible - 1);
        StringBuffer sb = new StringBuffer();
        sb.append(textView.getText().subSequence(textStart, textEnd - 3));
        sb.append("...");
        textView.setText(sb.toString());
    }
}
