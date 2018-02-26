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

import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import com.viewlift.R;

/**
 * Created by viewlift on 5/26/17.
 */

public class DotSelectorView extends BaseView implements OnInternalEvent {
    private static final String TAG = "DotSelectorView";

    private Component component;
    private final int selectedColor;
    private final int deselectedColor;
    private List<View> childViews;
    private List<OnInternalEvent> internalEventReceivers;
    private volatile int selectedViewIndex;
    private volatile  boolean cancelled;

    private String moduleId;

    public DotSelectorView(Context context,
                           Component component,
                           int selectedColor,
                           int deselectedColor) {
        super(context);
        this.component = component;
        this.selectedColor = selectedColor;
        this.deselectedColor = deselectedColor;
        this.selectedViewIndex = 0;
        this.cancelled = false;
        init();
    }

    @Override
    public void init() {
        Context context = getContext();
        childrenContainer = new LinearLayout(context);
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        RelativeLayout.LayoutParams childrenLayoutParams =
                new RelativeLayout.LayoutParams(width, height);
        childrenLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        childrenLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        childrenContainer.setLayoutParams(childrenLayoutParams);
        ((LinearLayout) childrenContainer).setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout carouselView = new RelativeLayout(context);
        LayoutParams carouselLayoutParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        carouselView.setLayoutParams(carouselLayoutParams);

        carouselView.addView(childrenContainer);

        addView(carouselView);

        childViews = new ArrayList<>();
        internalEventReceivers = new ArrayList<>();
    }

    public boolean dotsInitialized() {
        try {
            return childViews.size() > 0;
        } catch (Exception e) {

        }
        return false;
    }

    public void addDots(int size) {
        if (childrenContainer != null) {
            childrenContainer.removeAllViews();
        }
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
        dotView.setOnClickListener(v -> {
            deselect(selectedViewIndex);
            select(index);
            sendEvent(new InternalEvent<>(index));
        });
    }

    public void select(int index) {
        if (0 <= index && index < childViews.size()) {
            ((GradientDrawable) childViews.get(index).getBackground()).setColor(selectedColor);
            selectedViewIndex = index;
        }
    }

    public void deSelectAll() {
        for (int i = 0; i < childViews.size(); i++) {
            deselect(i);
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

        int imageWidth = (int) getViewWidth(context,
                component.getLayout(),
                (int) context.getResources().getDimension(R.dimen.dot_selector_width));
        int imageHeight = (int) getViewHeight(context,
                component.getLayout(),
                (int) context.getResources().getDimension(R.dimen.dot_selector_height));
        LayoutParams dotSelectorLayoutParams =
                new LayoutParams(imageWidth, imageHeight);
        dotSelectorLayoutParams.gravity = Gravity.CENTER;
        dotImageView.setLayoutParams(dotSelectorLayoutParams);
        return dotImageView;
    }

    private FrameLayout createDotView(Context context) {
        FrameLayout dotSelectorView = new FrameLayout(context);
        int viewWidth = (int) getViewWidth(context,
                component.getLayout(),
                (int) context.getResources().getDimension(R.dimen.dot_selector_item_width));
        int viewHeight = (int) getViewHeight(context,
                component.getLayout(),
                (int) context.getResources().getDimension(R.dimen.dot_selector_item_height));
        LayoutParams viewLayoutParams =
                new LayoutParams(viewWidth, viewHeight);
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
        if (!cancelled) {
            if (event.getEventData() instanceof Integer && childViews.size() > 0) {
                int index = (Integer) event.getEventData() % childViews.size();
                deselect(selectedViewIndex);
                select(index);
            }
        }
    }

    @Override
    public void cancel(boolean cancel) {
        this.cancelled = cancel;
        if (this.cancelled) {
            deselect(selectedViewIndex);
            selectedViewIndex = 0;
            select(selectedViewIndex);
        }
    }

    @Override
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public String getModuleId() {
        return moduleId;
    }

    @Override
    protected Component getChildComponent(int index) {
        return null;
    }

    @Override
    protected Layout getLayout() {
        return component.getLayout();
    }
}
