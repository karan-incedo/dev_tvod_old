package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/26/17.
 */

public class DotSelectorView extends FrameLayout implements OnInternalEvent {
    private final int selectedColor;
    private final int deselectedColor;
    private LinearLayout childrenContainer;
    private int selectedViewIndex;
    private List<View> childViews;
    private List<OnInternalEvent> internalEventReceivers;

    public DotSelectorView(Context context,
                           int selectedColor,
                           int deselectedColor) {
        super(context);
        this.selectedColor = selectedColor;
        this.deselectedColor = deselectedColor;
        this.selectedViewIndex = 0;
        init(context);
    }

    private void init(Context context) {
        childViews = new ArrayList<>();
        RelativeLayout carouselView = new RelativeLayout(context);
        LayoutParams carouselLayoutParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        carouselView.setLayoutParams(carouselLayoutParams);
        childrenContainer = new LinearLayout(context);
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.MATCH_PARENT;
        RelativeLayout.LayoutParams childrenLayoutParams =
                new RelativeLayout.LayoutParams(width, height);
        childrenLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        childrenLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        childrenContainer.setLayoutParams(childrenLayoutParams);
        childrenContainer.setOrientation(LinearLayout.HORIZONTAL);
        childrenContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        internalEventReceivers = new ArrayList<>();
        carouselView.addView(childrenContainer);
        addView(carouselView);
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
        childrenContainer.addView(dotView);
        childViews.add(dotImageView);
        final int index = childViews.size() - 1;
        dotView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deselect(selectedViewIndex);
                select(index);
                sendEvent(new InternalEvent<>(index));
            }
        });
        if (index == 0) {
            select(0);
        }
    }

    public void select(int index) {
        if (0 <= index && index < childViews.size()) {
            ((GradientDrawable) childViews.get(index).getBackground()).setColor(selectedColor);
            selectedViewIndex = index;
        }
    }

    public void deselect(int index) {
        if (0 <= index && index < childViews.size()) {
            ((GradientDrawable) childViews.get(index).getBackground()).setColor(deselectedColor);
        }
    }

    private ImageView createDotImageView(Context context) {
        ImageView dotImageView = new ImageView(context);
        dotImageView.setBackgroundResource(R.drawable.tab_indicator_default);
        ((GradientDrawable) dotImageView.getBackground()).setColor(deselectedColor);
        int imageWidth = (int) context.getResources().getDimension(R.dimen.dot_selector_width);
        int imageHeight = (int) context.getResources().getDimension(R.dimen.dot_selector_height);
        LayoutParams dotSelectorLayoutParams =
                new LayoutParams(imageWidth, imageHeight);
        dotSelectorLayoutParams.gravity = Gravity.CENTER;
        dotImageView.setLayoutParams(dotSelectorLayoutParams);
        return dotImageView;
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

    @Override
    public void addReceiver(OnInternalEvent e) {
        internalEventReceivers.add(e);
        getParent().bringChildToFront(this);
    }

    @Override
    public void sendEvent(InternalEvent<?> event) {
        for (OnInternalEvent receiver : internalEventReceivers) {
            receiver.receiveEvent(event);
        }
    }

    @Override
    public void receiveEvent(InternalEvent<?> event) {
        if (event.getEventData() instanceof Integer && childViews.size() > 0) {
            int index = (Integer) event.getEventData() % childViews.size();
            deselect(selectedViewIndex);
            select(index);
        }
    }
}
