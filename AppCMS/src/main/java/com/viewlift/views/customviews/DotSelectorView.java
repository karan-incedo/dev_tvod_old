package com.viewlift.views.customviews;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/26/17.
 */

public class DotSelectorView extends LinearLayout {
    List<View> childViews;

    public DotSelectorView(Context context, ViewGroup.LayoutParams layoutParams) {
        super(context);
        init(layoutParams);
    }

    private void init(ViewGroup.LayoutParams layoutParams) {
        childViews = new ArrayList<>();
        layoutParams.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height =
                (int) getContext().getResources().getDimension(R.dimen.dot_selector_listview_height);
        this.setLayoutParams(layoutParams);
        this.setOrientation(HORIZONTAL);
    }

    public void addDots(int size) {
        for (int i = 0; i < size; i++) {
            addDot();
        }
    }

    public void addDot() {
        FrameLayout dotView = createDotView(getContext());
        ImageView dotImageView = createDotImageView(getContext());
        dotView.addView(dotImageView);
        addView(dotView);
        childViews.add(dotImageView);
    }

    public void select(int index) {
        if (0 <= index && index < childViews.size()) {
            childViews.get(index).setBackgroundResource(R.drawable.tab_indicator_selected);
        }
    }

    public void deselect(int index) {
        if (0 <= index && index < childViews.size()) {
            childViews.get(index).setBackgroundResource(R.drawable.tab_indicator_default);
        }
    }

    private ImageView createDotImageView(Context context) {
        ImageView defaultDotImageView = new ImageView(context);
        defaultDotImageView.setBackgroundResource(R.drawable.tab_indicator_default);
        int imageWidth = (int) context.getResources().getDimension(R.dimen.dot_selector_width);
        int imageHeight = (int) context.getResources().getDimension(R.dimen.dot_selector_height);
        FrameLayout.LayoutParams dotSelectorLayoutParams =
                new FrameLayout.LayoutParams(imageWidth, imageHeight);
        dotSelectorLayoutParams.gravity = Gravity.CENTER;
        defaultDotImageView.setLayoutParams(dotSelectorLayoutParams);
        return defaultDotImageView;
    }

    private FrameLayout createDotView(Context context) {
        FrameLayout dotSelectorView = new FrameLayout(context);
        int viewWidth =
                (int) context.getResources().getDimension(R.dimen.dot_selector_item_width);
        int viewHeight =
                (int) context.getResources().getDimension(R.dimen.dot_selector_item_height);
        MarginLayoutParams marginLayoutParams =
                new MarginLayoutParams(viewWidth, viewHeight);
        marginLayoutParams.leftMargin =
                (int) context.getResources().getDimension(R.dimen.dot_selector_margin_left);
        marginLayoutParams.rightMargin =
                (int) context.getResources().getDimension(R.dimen.dot_selector_margin_right);
        LayoutParams viewLayoutParams =
                new LayoutParams(marginLayoutParams);
        dotSelectorView.setLayoutParams(viewLayoutParams);
        dotSelectorView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        return dotSelectorView;
    }
}
