package com.viewlift.views.customviews;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.viewlift.presenters.AppCMSPresenter;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 6/7/17.
 */

public class ViewCreatorMultiLineLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final int EXTRA_TRUNC_CHARS = 10;
    private static final int CLICKABLE_CHAR_COUNT = 4;

    private final TextView textView;
    private final AppCMSPresenter appCMSPresenter;
    private final String title;
    private final String fullText;
    private final int textColor;
    private final boolean forceMaxLines;

    public ViewCreatorMultiLineLayoutListener(TextView textView,
                                              String title,
                                              String fullText,
                                              int textColor,
                                              AppCMSPresenter appCMSPresenter,
                                              boolean forceMaxLines) {
        this.textView = textView;
        this.title = title;
        this.fullText = fullText;
        this.textColor = textColor;
        this.appCMSPresenter = appCMSPresenter;
        this.forceMaxLines = forceMaxLines;
    }

    @Override
    public void onGlobalLayout() {
        int linesCompletelyVisible = (textView.getHeight() - textView.getPaddingTop() - textView.getPaddingBottom()) / textView.getLineHeight();
        if (linesCompletelyVisible < textView.getLineCount() &&
                textView.getLayout() != null &&
                appCMSPresenter != null) {
            int lineEnd = textView.getLayout().getLineEnd(linesCompletelyVisible - 1) - EXTRA_TRUNC_CHARS;
            SpannableString spannableTextWithMore =
                    new SpannableString(textView.getContext().getString(R.string.string_with_ellipse_and_more,
                            textView.getText().subSequence(0, lineEnd)));
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    appCMSPresenter.showMoreDialog(title, fullText, textColor);
                }
            };
            spannableTextWithMore.setSpan(clickableSpan,
                    spannableTextWithMore.length() - CLICKABLE_CHAR_COUNT,
                    spannableTextWithMore.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableTextWithMore);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        if (forceMaxLines) {
            textView.setMaxLines(linesCompletelyVisible);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }
        textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }
}
