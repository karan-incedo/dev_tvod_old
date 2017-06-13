package com.viewlift.views.customviews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/26/17.
 */

public class NavBarItemView extends LinearLayout {
    private static final String TAG = "NavBarItemView";
    private ImageView navImage;
    private TextView navLabel;
    private String tag;
    private boolean hasFocus;

    public NavBarItemView(Context context) {
        super(context);
        init();
    }

    public NavBarItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavBarItemView(Context context,
                          @Nullable AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NavBarItemView(Context context,
                          AttributeSet attrs,
                          int defStyleAttr,
                          int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        hasFocus = false;
        setOrientation(VERTICAL);
        createChildren(getContext());
    }

    public void select(boolean hasFocus) {
        this.hasFocus = hasFocus;
        int color = ContextCompat.getColor(getContext(), R.color.colorNavBarText);
        if (hasFocus) {
            color = ContextCompat.getColor(getContext(), R.color.colorAccent);
        }
        applyTintToDrawable(navImage.getDrawable(), color);
        navLabel.setTextColor(color);
    }

    public void createChildren(Context context) {
        navImage = new ImageView(context);
        LinearLayout.LayoutParams navImageLayoutParams =
                new LinearLayout.LayoutParams((int) BaseView.convertDpToPixel(getContext().getResources().getDimension(R.dimen.nav_image_width), getContext()),
                        (int) BaseView.convertDpToPixel(getContext().getResources().getDimension(R.dimen.nav_image_height), getContext()));
        navImageLayoutParams.gravity = Gravity.CENTER;
        navImage.setLayoutParams(navImageLayoutParams);

        navLabel = new TextView(context);
        LinearLayout.LayoutParams navLabelLayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        navLabelLayoutParams.gravity = Gravity.CENTER;
        navLabel.setLayoutParams(navLabelLayoutParams);
        navLabel.setTextColor(ContextCompat.getColor(context, R.color.colorNavBarText));

        addView(navImage);
        addView(navLabel);
    }

    public void setImage(String drawableName) {
        Resources resources = getResources();
        int drawableId = resources.getIdentifier(drawableName,
                "drawable",
                getContext().getPackageName());
        navImage.setImageDrawable(ContextCompat.getDrawable(getContext(), drawableId));
    }

    public void setLabel(String label) {
        navLabel.setText(label);
    }

    public void hideLabel() {
        navLabel.setVisibility(GONE);
    }

    public boolean isItemSelected() {
        return hasFocus;
    }

    private void applyTintToDrawable(@Nullable Drawable drawable, int color) {
        if (drawable != null) {
            drawable.setTint(color);
            drawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        }
    }
}
