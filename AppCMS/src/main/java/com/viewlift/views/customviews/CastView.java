package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.support.v7.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 6/7/17.
 */

public class CastView extends GridLayout {
    private final String fontFamilyKey;
    private final int fontFamilyKeyType;
    private final String fontFamilyValue;
    private final int fontFamilyValueType;
    private final String directorListTitle;
    private final String directorList;
    private final String starringListTitle;
    private final String starringList;
    private final int textColor;
    private final float fontsizeKey;
    private final float fontsizeValue;

    public CastView(Context context,
                    String fontFamilyKey,
                    int fontFamilyKeyType,
                    String fontFamilyValue,
                    int fontFamilyValueType,
                    String directorListTitle,
                    String directorList,
                    String starringListTitle,
                    String starringList,
                    int textColor,
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
        this.fontsizeKey = fontsizeKey;
        this.fontsizeValue = fontsizeValue;
        init();
    }

    private void init() {
        setColumnCount(2);

        Typeface keyTypeFace = Typeface.create(fontFamilyKey, fontFamilyKeyType);
        Typeface valueTypeFace = Typeface.create(fontFamilyValue, fontFamilyValueType);

        if (!TextUtils.isEmpty(directorListTitle) && !TextUtils.isEmpty(directorList)) {
            TextView directorListTitleView = new TextView(getContext());
            directorListTitleView.setText(directorListTitle);
            directorListTitleView.setTypeface(keyTypeFace);
            directorListTitleView.setTextColor(textColor);
            if (fontsizeKey != -1.0f) {
                directorListTitleView.setTextSize(fontsizeKey);
            }
            ViewTreeObserver directorListTitleVto = directorListTitleView.getViewTreeObserver();
            directorListTitleVto.addOnGlobalLayoutListener(new ViewCreatorLayoutListener(directorListTitleView));
            addView(directorListTitleView);

            TextView directorListView = new TextView(getContext());
            directorListView.setText(directorList);
            directorListView.setTypeface(valueTypeFace);
            directorListView.setTextColor(textColor);
            directorListView.setPadding((int) getContext().getResources().getDimension(R.dimen.castview_padding),
                    0,
                    0,
                    0);
            if (fontsizeValue != -1.0f) {
                directorListView.setTextSize(fontsizeValue);
            }
            ViewTreeObserver directorListVto = directorListView.getViewTreeObserver();
            directorListVto.addOnGlobalLayoutListener(new ViewCreatorLayoutListener(directorListView));
            addView(directorListView);
        }

        if (!TextUtils.isEmpty(starringListTitle) && !TextUtils.isEmpty(starringList)) {
            TextView starringListTitleView = new TextView(getContext());
            starringListTitleView.setText(starringListTitle);
            starringListTitleView.setTypeface(keyTypeFace);
            starringListTitleView.setTextColor(textColor);
            if (fontsizeKey != -1.0f) {
                starringListTitleView.setTextSize(fontsizeKey);
            }
            ViewTreeObserver starringListTitleVto = starringListTitleView.getViewTreeObserver();
            starringListTitleVto.addOnGlobalLayoutListener(new ViewCreatorLayoutListener(starringListTitleView));
            addView(starringListTitleView);

            TextView starringListView = new TextView(getContext());
            starringListView.setText(starringList);
            starringListView.setTypeface(valueTypeFace);
            starringListView.setTextColor(textColor);
            starringListView.setPadding((int) getContext().getResources().getDimension(R.dimen.castview_padding),
                    0,
                    0,
                    0);
            if (fontsizeValue != -1.0f) {
                starringListView.setTextSize(fontsizeValue);
            }
            ViewTreeObserver starringListVto = starringListView.getViewTreeObserver();
            starringListVto.addOnGlobalLayoutListener(new ViewCreatorLayoutListener(starringListView));
            addView(starringListView);
        }
    }
}
