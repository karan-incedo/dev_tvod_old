package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewlift.R;

/**
 * Created by viewlift on 6/7/17.
 */

public class CreditBlocksView extends RelativeLayout {
    private final String fontFamilyKey;
    private final int fontFamilyKeyType;
    private final String fontFamilyValue;
    private final int fontFamilyValueType;
    private final String directorListTitle;
    private final String directorList;
    private final String starringListTitle;
    private final String starringList;
    private final int textColor;
    private final int moreBackgroundColor;
    private final float fontsizeKey;
    private final float fontsizeValue;

    private TextView directorListTitleView;
    private TextView directorListView;
    private TextView starringListTitleView;
    private TextView starringListView;

    public CreditBlocksView(Context context,
                            String fontFamilyKey,
                            int fontFamilyKeyType,
                            String fontFamilyValue,
                            int fontFamilyValueType,
                            String directorListTitle,
                            String directorList,
                            String starringListTitle,
                            String starringList,
                            int textColor,
                            int moreBackgroundColor,
                            float fontsizeKey,
                            float fontsizeValue) {
        super(context);
        this.fontFamilyKey = fontFamilyKey;
        this.fontFamilyKeyType = fontFamilyKeyType;
        this.fontFamilyValue = fontFamilyValue;
        this.fontFamilyValueType = fontFamilyValueType;
        this.directorListTitle = directorListTitle;
        this.directorList = directorList;
        this.starringList = starringList;
        this.starringListTitle = starringListTitle;
        this.textColor = textColor;
        this.moreBackgroundColor = moreBackgroundColor;
        this.fontsizeKey = fontsizeKey;
        this.fontsizeValue = fontsizeValue;
        init();
    }

    private void init() {
        Typeface keyTypeFace = Typeface.create(fontFamilyKey, fontFamilyKeyType);
        Typeface valueTypeFace = Typeface.create(fontFamilyValue, fontFamilyValueType);

        int directorListTitleViewId = View.generateViewId();
        int directorListViewId = View.generateViewId();
        int starringListTitleViewId = View.generateViewId();
        int starringListViewId = View.generateViewId();

        directorListTitleView = new TextView(getContext());
        directorListTitleView.setTypeface(Typeface.DEFAULT_BOLD);
        directorListTitleView.setTextColor(textColor);
        if (fontsizeKey != -1.0f) {
            directorListTitleView.setTextSize(fontsizeKey);
        }
        LayoutParams directorListTitleLayoutParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        directorListTitleLayoutParams.addRule(ALIGN_PARENT_START);
        directorListTitleView.setLayoutParams(directorListTitleLayoutParams);
        directorListTitleView.setSingleLine(true);
        directorListTitleView.setId(directorListTitleViewId);
        addView(directorListTitleView);

        directorListView = new TextView(getContext());
        directorListView.setTypeface(valueTypeFace);
        directorListView.setTextColor(textColor);
        directorListView.setPadding((int) getContext().getResources().getDimension(R.dimen.castview_padding),
                0,
                0,
                0);
        if (fontsizeValue != -1.0f) {
            directorListView.setTextSize(fontsizeValue);
        }
        LayoutParams directorListLayoutParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        directorListLayoutParams.addRule(ALIGN_PARENT_END);
        directorListLayoutParams.addRule(END_OF, directorListTitleViewId);
        directorListView.setLayoutParams(directorListLayoutParams);
        directorListView.setId(directorListViewId);
        addView(directorListView);

        starringListTitleView = new TextView(getContext());
        starringListTitleView.setTypeface(Typeface.DEFAULT_BOLD);
        starringListTitleView.setTextColor(textColor);
        if (fontsizeKey != -1.0f) {
            starringListTitleView.setTextSize(fontsizeKey);
        }
        LayoutParams starringListTitleLayoutParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        starringListTitleLayoutParams.addRule(ALIGN_PARENT_START);
        starringListTitleLayoutParams.addRule(BELOW, directorListTitleViewId);
        starringListTitleView.setLayoutParams(starringListTitleLayoutParams);
        starringListTitleView.setSingleLine(true);
        starringListTitleView.setId(starringListTitleViewId);
        addView(starringListTitleView);

        starringListView = new TextView(getContext());
        starringListView.setTypeface(valueTypeFace);
        starringListView.setTextColor(textColor);
        starringListView.setPadding((int) getContext().getResources().getDimension(R.dimen.castview_padding),
                0,
                0,
                0);
        if (fontsizeValue != -1.0f) {
            starringListView.setTextSize(fontsizeValue);
        }

        LayoutParams starringListLayoutParams =
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        starringListLayoutParams.addRule(ALIGN_PARENT_END);
        starringListLayoutParams.addRule(END_OF, starringListTitleViewId);
        starringListLayoutParams.addRule(ALIGN_START, directorListViewId);
        starringListLayoutParams.addRule(BELOW, directorListViewId);
        starringListView.setLayoutParams(starringListLayoutParams);
        starringListView.setId(starringListViewId);
        addView(starringListView);

        updateText(directorListTitle, directorList, starringListTitle, starringList);
    }

    public void updateText(String directorListTitle,
                           String directorList,
                           String starringListTitle,
                           String starringList) {
        if (!TextUtils.isEmpty(directorListTitle) && !TextUtils.isEmpty(directorList) &&
                directorListTitleView != null &&
                directorListView != null) {
            directorListTitleView.setText(directorListTitle);
            directorListView.setText(directorList);

            ViewTreeObserver directorListVto = directorListView.getViewTreeObserver();
            directorListVto.addOnGlobalLayoutListener(new ViewCreatorMultiLineLayoutListener(directorListView,
                    null,
                    directorList,
                    null,
                    true,
                    moreBackgroundColor,
                    true));
        }

        if (!TextUtils.isEmpty(starringListTitle) && !TextUtils.isEmpty(starringList) &&
                starringListTitleView != null &&
                starringListView != null) {
            starringListTitleView.setText(starringListTitle);
            starringListView.setText(starringList);

            ViewTreeObserver starringListVto = starringListView.getViewTreeObserver();
            starringListVto.addOnGlobalLayoutListener(new ViewCreatorMultiLineLayoutListener(starringListView,
                    null,
                    starringList,
                    null,
                    true,
                    moreBackgroundColor,
                    true));
        }
    }
}
