package com.viewlift.views.customviews;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;

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
    private final boolean forceMaxLines;
    private final int moreBackgroundColor;

    public ViewCreatorMultiLineLayoutListener(TextView textView,
                                              String title,
                                              String fullText,
                                              AppCMSPresenter appCMSPresenter,
                                              boolean forceMaxLines,
                                              int moreBackgroundColor) {
        this.textView = textView;
        this.title = title;
        this.fullText = fullText;
        this.appCMSPresenter = appCMSPresenter;
        this.forceMaxLines = forceMaxLines;
        this.moreBackgroundColor = moreBackgroundColor;
    }

    @Override
    public void onGlobalLayout() {
        int linesCompletelyVisible = (textView.getHeight() -
                textView.getPaddingTop() -
                textView.getPaddingBottom()) /
                textView.getLineHeight();
        if (!forceMaxLines) {
            Rect bounds = new Rect();
            Paint textPaint = textView.getPaint();
            textPaint.getTextBounds(textView.getText().toString(),
                    0,
                    textView.getText().length(),
                    bounds);
            if (bounds.height() < linesCompletelyVisible * textView.getLineHeight()
                    && appCMSPresenter.getPlatformType()
                    .equals(AppCMSPresenter.PlatformType.ANDROID)) {
                linesCompletelyVisible--;
            }
            if (linesCompletelyVisible < textView.getLineCount() &&
                    textView.getLayout() != null &&
                    appCMSPresenter != null) {
                int lineEnd = textView.getLayout().getLineEnd(linesCompletelyVisible - 1) - EXTRA_TRUNC_CHARS;
                if (0 < lineEnd) {
                    SpannableString spannableTextWithMore =
                            new SpannableString(textView.getContext().getString(R.string.string_with_ellipse_and_more,
                                    textView.getText().subSequence(0, lineEnd)));
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            appCMSPresenter.showMoreDialog(title, fullText);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            if(appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV){
                                ds.setUnderlineText(false);
                                ds.setColor(ContextCompat.getColor(textView.getContext() , android.R.color.white));
                            } else {
                                super.updateDrawState(ds);
                                ds.setColor(moreBackgroundColor);
                            }
                        }
                    };
                    spannableTextWithMore.setSpan(clickableSpan,
                            spannableTextWithMore.length() - CLICKABLE_CHAR_COUNT,
                            spannableTextWithMore.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(spannableTextWithMore);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        } else if (forceMaxLines) {
            textView.setMaxLines(linesCompletelyVisible);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }
        textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }


    /**
     * this method will set span on SpnabbleString i.e om MORE when there will be a focus on description text.
     * This Methos is for TV specific.
     * @param textView
     * @param hasFocus
     * @param textColor
     */
    public void setSpanOnFocus(TextView textView, boolean hasFocus , int textColor){
        Spannable wordToSpan = new SpannableString(textView.getText().toString());
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(
                Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor())
        );

        int length = wordToSpan.length();
        if (hasFocus) {
            wordToSpan.setSpan(new StyleSpan(Typeface.BOLD), length - CLICKABLE_CHAR_COUNT, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordToSpan.setSpan(foregroundColorSpan, length - CLICKABLE_CHAR_COUNT, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } else {
            wordToSpan.setSpan(new StyleSpan(Typeface.NORMAL), length - CLICKABLE_CHAR_COUNT, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordToSpan.setSpan(new ForegroundColorSpan(textColor), length - CLICKABLE_CHAR_COUNT, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(wordToSpan);
    }
}
