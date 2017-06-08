package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.TextView;

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

    public CastView(Context context,
                    String fontFamilyKey,
                    int fontFamilyKeyType,
                    String fontFamilyValue,
                    int fontFamilyValueType,
                    String directorListTitle,
                    String directorList,
                    String starringListTitle,
                    String starringList,
                    int textColor) {
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
        init();
    }

    private void init() {
        setColumnCount(2);

        Typeface keyTypeFace = Typeface.create(fontFamilyKey, fontFamilyKeyType);

        TextView directorListTitleView = new TextView(getContext());
        directorListTitleView.setText(directorListTitle);
        directorListTitleView.setTypeface(keyTypeFace);
        directorListTitleView.setTextColor(textColor);
        ViewTreeObserver directorListTitleVto = directorListTitleView.getViewTreeObserver();
        directorListTitleVto.addOnGlobalLayoutListener(new ViewCreatorLayoutListener(directorListTitleView));

        TextView starringListTitleView = new TextView(getContext());
        starringListTitleView.setText(starringListTitle);
        starringListTitleView.setTypeface(keyTypeFace);
        starringListTitleView.setTextColor(textColor);
        ViewTreeObserver starringListTitleVto = starringListTitleView.getViewTreeObserver();
        starringListTitleVto.addOnGlobalLayoutListener(new ViewCreatorLayoutListener(starringListTitleView));

        Typeface valueTypeFace = Typeface.create(fontFamilyValue, fontFamilyValueType);

        TextView directorListView = new TextView(getContext());
        directorListView.setText(directorList);
        directorListView.setTypeface(valueTypeFace);
        directorListView.setTextColor(textColor);
        directorListView.setPadding(24, 0, 0, 0);
        ViewTreeObserver directorListVto = directorListView.getViewTreeObserver();
        directorListVto.addOnGlobalLayoutListener(new ViewCreatorLayoutListener(directorListView));

        TextView starringListView = new TextView(getContext());
        starringListView.setText(starringList);
        starringListView.setTypeface(valueTypeFace);
        starringListView.setTextColor(textColor);
        starringListView.setPadding(24, 0, 0, 0);
        ViewTreeObserver starringListVto = starringListView.getViewTreeObserver();
        starringListVto.addOnGlobalLayoutListener(new ViewCreatorLayoutListener(starringListView));

        addView(directorListTitleView);
        addView(directorListView);
        addView(starringListTitleView);
        addView(starringListView);

        GradientDrawable border = new GradientDrawable();
        border.setShape(GradientDrawable.RECTANGLE);
        border.setStroke(1,
                ContextCompat.getColor(getContext(), android.R.color.white));
        border.setColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        setBackground(border);
    }
}
