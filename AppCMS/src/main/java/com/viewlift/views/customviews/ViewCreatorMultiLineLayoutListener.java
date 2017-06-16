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
    private final TextView textView;
    private final AppCMSPresenter appCMSPresenter;
    private final String fullText;
    private final String title;
    private final boolean forceMaxLines;

    public ViewCreatorMultiLineLayoutListener(TextView textView,
                                              String fullText,
                                              AppCMSPresenter appCMSPresenter,
                                              String title,
                                              boolean forceMaxLines) {
        this.textView = textView;
        this.fullText = fullText;
        this.appCMSPresenter = appCMSPresenter;
        this.title = title;
        this.forceMaxLines = forceMaxLines;
    }

    @Override
    public void onGlobalLayout() {
        int linesCompletelyVisible = (textView.getHeight() - textView.getPaddingTop() - textView.getPaddingBottom()) / textView.getLineHeight();
        if (linesCompletelyVisible < textView.getLineCount() &&
                textView.getLayout() != null &&
                appCMSPresenter != null) {
            int lineEnd = textView.getLayout().getLineEnd(linesCompletelyVisible - 1) - 7;
            SpannableString spannableTextWithMore =
                    new SpannableString(textView.getContext().getString(R.string.string_with_ellipse_and_more,
                            textView.getText().subSequence(0, lineEnd)));
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    appCMSPresenter.showMoreDialog(title, fullText);
                }
            };
            spannableTextWithMore.setSpan(clickableSpan,
                    spannableTextWithMore.length() - 4,
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
