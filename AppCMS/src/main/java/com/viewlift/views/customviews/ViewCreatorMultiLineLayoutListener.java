package com.viewlift.views.customviews;

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
import android.text.style.TypefaceSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;

/**
 * Created by viewlift on 6/7/17.
 */

public class ViewCreatorMultiLineLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final int EXTRA_TRUNC_CHARS = 9;
    private static final int CLICKABLE_CHAR_COUNT = 4;

    private final TextView textView;
    private final AppCMSPresenter appCMSPresenter;
    private final String title;
    private String fullText;
    private boolean forceMaxLines;
    private final int moreForegroundColor;
    private final int generalForegroundColor;
    private final boolean useItalics;

    public ViewCreatorMultiLineLayoutListener(TextView textView,
                                              String title,
                                              String fullText,
                                              AppCMSPresenter appCMSPresenter,
                                              boolean forceMaxLines,
                                              int moreForegroundColor,
                                              int generalForegroundColor,
                                              boolean useItalics) {
        this.textView = textView;
        this.title = title;
        this.fullText = fullText;
        this.appCMSPresenter = appCMSPresenter;
        this.forceMaxLines = forceMaxLines;
        this.moreForegroundColor = moreForegroundColor;
        this.generalForegroundColor = generalForegroundColor;
        this.useItalics = useItalics;
    }

    @Override
    public void onGlobalLayout() {
        fullText = fullText == null ? "" : fullText;
        int linesCompletelyVisible = textView.getHeight() /
                textView.getLineHeight();
        if (textView.getLineCount() < linesCompletelyVisible) {
            linesCompletelyVisible = textView.getLineCount();
            //Resolved AF-11
            if(appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV){
                forceMaxLines = true;
            }
        }
        if (!forceMaxLines && textView.getLayout() != null) {
            int lineEnd = textView.getLayout().getLineVisibleEnd(linesCompletelyVisible - 1) -
                    EXTRA_TRUNC_CHARS;
            if (0 <= lineEnd &&
                    lineEnd + EXTRA_TRUNC_CHARS < fullText.length() &&
                    appCMSPresenter != null) {
                if (0 < lineEnd) {
                    SpannableString spannableTextWithMore =
                            new SpannableString(textView.getContext().getString(R.string.string_with_ellipse_and_more,
                                    textView.getText().subSequence(0, lineEnd)));
                    spannableTextWithMore.setSpan(new ForegroundColorSpan(generalForegroundColor), 0, lineEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                                ds.setColor(moreForegroundColor);
                            }
                        }
                    };
                    spannableTextWithMore.setSpan(clickableSpan,
                            spannableTextWithMore.length() - CLICKABLE_CHAR_COUNT,
                            spannableTextWithMore.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (useItalics) {
                        TypefaceSpan typefaceSpan = new ItalicTypefaceSpan("sans-serif");
                        spannableTextWithMore.setSpan(typefaceSpan,
                                spannableTextWithMore.length() - CLICKABLE_CHAR_COUNT,
                                spannableTextWithMore.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    textView.setText(spannableTextWithMore);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    textView.setFocusable(true);
                }
            }
        } else if (forceMaxLines) {
            textView.setMaxLines(linesCompletelyVisible);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setFocusable(false);
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
                appCMSPresenter.getBrandPrimaryCtaColor()
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

    private static class ItalicTypefaceSpan extends TypefaceSpan {
        private Typeface italicTypeface;

        public ItalicTypefaceSpan(String family) {
            super(family);
            italicTypeface = Typeface.create(family, Typeface.ITALIC);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyItalicTypeface(ds);
            super.updateDrawState(ds);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyItalicTypeface(paint);
            super.updateMeasureState(paint);
        }

        private void applyItalicTypeface(TextPaint paint) {
            paint.setTypeface(italicTypeface);
        }
    }
}
