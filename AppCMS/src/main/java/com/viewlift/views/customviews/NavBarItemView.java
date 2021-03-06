package com.viewlift.views.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewlift.R;

/**
 * Created by viewlift on 5/26/17.
 */

public class NavBarItemView extends LinearLayout {
    private static final String TAG = "NavBarItemView";
    private ImageView navImage;
    private TextView navLabel;
    private String tag;
    private boolean hasFocus;
    private int highlightColor;

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
        setPadding(0, 0, 0, 0);
        setOrientation(VERTICAL);
        createChildren(getContext());
    }

    public void select(boolean hasFocus) {
        this.hasFocus = hasFocus;
        int color = ContextCompat.getColor(getContext(), R.color.colorNavBarText);
        if (hasFocus) {
            color = highlightColor;
        }
        applyTintToDrawable(navImage.getDrawable(), color);
        navLabel.setTextColor(color);
    }

    public void createChildren(Context context) {
        navImage = new ImageView(context);

        int navImageWidth =
                (int) BaseView.convertDpToPixel(getContext().getResources().getDimension(R.dimen.nav_image_width), getContext());
        int navImageHeight =
                (int) BaseView.convertDpToPixel(getContext().getResources().getDimension(R.dimen.nav_image_height), getContext());

        if (BaseView.isTablet(getContext())) {
            navImageWidth =
                    (int) BaseView.convertDpToPixel(getContext().getResources().getDimension(R.dimen.nav_item_large_width), getContext());
            navImageHeight =
                    (int) BaseView.convertDpToPixel(getContext().getResources().getDimension(R.dimen.nav_item_large_height), getContext());
        }

        LinearLayout.LayoutParams navImageLayoutParams =
                new LinearLayout.LayoutParams(navImageWidth, navImageHeight);
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
        navLabel.setClickable(true);
        addView(navLabel);
    }

    public void setImage(String drawableName) {
        Resources resources = getResources();
        try {
            int drawableId = resources.getIdentifier(drawableName,
                    "drawable",
                    getContext().getPackageName());
            navImage.setImageDrawable(ContextCompat.getDrawable(getContext(), drawableId));
        } catch (Exception e) {
            Log.e(TAG, "Missing tab icon image resource: " + drawableName);
        }
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

    public void setHighlightColor(int highlightColor) {
        this.highlightColor = highlightColor;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        navImage.setOnClickListener(l);
        navLabel.setOnClickListener(l);
    }

    private void applyTintToDrawable(@Nullable Drawable drawable, int color) {
        if (drawable != null) {
            drawable.setTint(color);
            drawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        }
    }
}
