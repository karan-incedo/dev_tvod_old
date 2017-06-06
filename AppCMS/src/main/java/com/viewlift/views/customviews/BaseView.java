package com.viewlift.views.customviews;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Mobile;
import com.viewlift.models.data.appcms.ui.page.TabletLandscape;
import com.viewlift.models.data.appcms.ui.page.TabletPortrait;

import java.util.Map;

import rx.functions.Action1;

/**
 * Created by viewlift on 5/17/17.
 */

public abstract class BaseView extends FrameLayout {
    private static final String TAG = "BaseView";

    protected ViewGroup childrenContainer;
    protected boolean[] componentHasViewList;
    protected boolean hideOnFullscreenLandscape;
    protected Action1<Boolean> onOrientationChangeHandler;
    protected Action1<LifecycleStatus> onLifecycleChangeHandler;

    public BaseView(Context context) {
        super(context);
        hideOnFullscreenLandscape = false;
    }

    protected abstract void init();

    protected abstract Component getChildComponent(int index);

    protected abstract Layout getLayout();

    public ViewGroup getChildrenContainer() {
        if (childrenContainer == null) {
            return createChildrenContainer();
        }
        return childrenContainer;
    }

    public Action1<Boolean> getOrientationChangeHandler() {
        if (onOrientationChangeHandler == null) {
            onOrientationChangeHandler = new Action1<Boolean>() {
                @Override
                public void call(Boolean isLandscape) {
                    if (hideOnFullscreenLandscape && isLandscape) {
                        setVisibility(GONE);
                    } else if (!isLandscape) {
                        setVisibility(VISIBLE);
                    }
                }
            };
        }
        return onOrientationChangeHandler;
    }

    public void setComponentHasView(int index, boolean hasView) {
        if (componentHasViewList != null) {
            componentHasViewList[index] = hasView;
        }
    }

    public void setViewMarginsFromComponent(Component childComponent,
                                            View view,
                                            Layout parentLayout,
                                            View parentView,
                                            boolean firstMeasurement,
                                            Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                                            boolean useMarginsAsPercentages) {
        Layout layout = childComponent.getLayout();

        if (!shouldShowView(childComponent)) {
            view.setVisibility(GONE);
            return;
        }

        int lm = 0, tm = 0, rm = 0, bm = 0;
        int deviceWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        int deviceHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        int viewWidth = LayoutParams.MATCH_PARENT;
        int viewHeight = LayoutParams.WRAP_CONTENT;
        int parentViewWidth = (int) getViewWidth(getContext(),
                parentLayout,
                parentView.getMeasuredWidth());
        int parentViewHeight = (int) getViewHeight(getContext(),
                parentLayout,
                parentView.getMeasuredHeight());
        int measuredWidth = firstMeasurement ? deviceWidth : parentViewWidth;
        int measuredHeight = firstMeasurement ? deviceHeight : parentViewHeight;

        if (isTablet(getContext())) {
            if (isLandscape(getContext())) {
                TabletLandscape tabletLandscape = layout.getTabletLandscape();
                if (tabletLandscape != null) {
                    if (getViewWidth(tabletLandscape) != -1) {
                        viewWidth = (int) getViewWidth(tabletLandscape);
                        if (tabletLandscape.getXAxis() != null) {
                            lm = (int) convertDpToPixel(tabletLandscape.getXAxis(), getContext());
                        }
                    }
                    if (getViewHeight(tabletLandscape) != -1) {
                        viewHeight = (int) getViewHeight(tabletLandscape);
                        if (tabletLandscape.getYAxis() != null) {
                            tm = Math.round((tabletLandscape.getXAxis()));
                        }
                    }
                    if (tabletLandscape.getLeftMargin() != null) {
                        lm = Math.round((tabletLandscape.getLeftMargin() / 100.0f) * measuredWidth);
                    }
                    if (tabletLandscape.getRightMargin() != null) {
                        rm = Math.round((tabletLandscape.getRightMargin() / 100.0f) * measuredWidth);
                    }
                    if (tabletLandscape.getTopMargin() != null) {
                        tm = Math.round((tabletLandscape.getTopMargin() / 100.0f) * measuredHeight);
                    } else if (tabletLandscape.getBottomMargin() != null) {
                        int marginDiff = viewHeight;
                        if (marginDiff < 0) {
                            marginDiff = 0;
                        }
                        tm = Math.round(((100.0f - tabletLandscape.getBottomMargin()) / 100.0f) * measuredHeight) -
                                Math.round(convertDpToPixel(marginDiff, getContext()));
                    }
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                if (tabletPortrait != null) {
                    if (getViewWidth(tabletPortrait) != -1) {
                        viewWidth = (int) getViewWidth(tabletPortrait);
                        if (tabletPortrait.getXAxis() != null) {
                            lm = Math.round(convertDpToPixel(tabletPortrait.getXAxis(), getContext()));
                        }
                    }
                    if (getViewHeight(tabletPortrait) != -1) {
                        viewHeight = (int) getViewHeight(tabletPortrait);
                        if (tabletPortrait.getYAxis() != null) {
                            tm = Math.round((tabletPortrait.getXAxis()));
                        }
                    }
                    if (tabletPortrait.getLeftMargin() != null) {
                        lm = Math.round((tabletPortrait.getLeftMargin() / 100.0f) * measuredWidth);
                    }
                    if (tabletPortrait.getRightMargin() != null) {
                        rm = Math.round((tabletPortrait.getRightMargin() / 100.0f) * measuredWidth);
                    }
                    if (tabletPortrait.getTopMargin() != null) {
                        tm = Math.round((tabletPortrait.getTopMargin() / 100.0f) * measuredHeight);
                    }
                    if (tabletPortrait.getBottomMargin() != null) {
                        int marginDiff = viewHeight;
                        if (marginDiff < 0) {
                            marginDiff = 0;
                        }
                        tm = Math.round(((100.0f - tabletPortrait.getBottomMargin()) / 100.0f) * measuredHeight) -
                                Math.round(convertDpToPixel(marginDiff, getContext()));
                    }
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            if (mobile != null) {
                if (getViewWidth(mobile) != -1) {
                    viewWidth = Math.round(convertDpToPixel(getViewWidth(mobile), getContext()));
                    if (mobile.getXAxis() != null) {
                        lm = Math.round(convertDpToPixel(mobile.getXAxis(), getContext()));
                    }
                }
                if (getViewHeight(mobile) != -1) {
                    viewHeight = (int) convertDpToPixel(getViewHeight(mobile), getContext());
                    if (mobile.getYAxis() != null) {
                        tm = Math.round(convertDpToPixel(mobile.getYAxis(), getContext()));
                    }
                }
                if (mobile.getLeftMargin() != null) {
                    lm = Math.round(convertDpToPixel(mobile.getLeftMargin(), getContext()));
                } else if (mobile.getRightMargin() != null) {
                    int lmDiff = viewWidth;
                    if (lmDiff < 0) {
                        lmDiff = 0;
                    }
                    if (parentViewHeight > 0) {
                        lm = parentViewHeight -
                                Math.round(convertDpToPixel(mobile.getRightMargin(), getContext())) -
                                lmDiff;
                    } else {
                        // TODO: Correct for measurements when parent view width is not available
                    }
                }
                if (useMarginsAsPercentages) {
                    if (mobile.getTopMargin() != null) {
                        tm = Math.round((mobile.getTopMargin() / 100.0f) * measuredHeight);
                    } else if (mobile.getBottomMargin() != null) {
                        int marginDiff = viewHeight;
                        if (marginDiff < 0) {
                            marginDiff = 0;
                        }
                        tm = Math.round(((100.0f - mobile.getBottomMargin()) / 100.0f) * measuredHeight) -
                                Math.round(convertDpToPixel(marginDiff, getContext()));
                    }
                } else {
                    if (mobile.getTopMargin() != null) {
                        tm = Math.round(convertDpToPixel(mobile.getTopMargin(), getContext()));
                    } else if (mobile.getBottomMargin() != null && mobile.getBottomMargin() > 0) {
                        int tmDiff = viewHeight;
                        if (tmDiff < 0) {
                            tmDiff = 0;
                        }
                        if (parentViewHeight > 0) {
                            tm = parentViewHeight -
                                    Math.round(convertDpToPixel(mobile.getBottomMargin(), getContext())) -
                                    tmDiff;
                        } else {
                            // TODO: Correct for measurements when parent view height is not available
                        }
                    }
                }
            }
        }

        int gravity = Gravity.NO_GRAVITY;
        if (view instanceof TextView) {
            if (viewWidth < 0) {
                viewWidth = LayoutParams.MATCH_PARENT;
            }
            if (!TextUtils.isEmpty(childComponent.getTextAlignment()) &&
                    childComponent.getTextAlignment()
                        .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_TEXTALIGNMENT_CENTER_KEY))) {
                ((TextView) view).setGravity(Gravity.CENTER_HORIZONTAL);
            }
            if (childComponent.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_PLAY_IMAGE_KEY))) {
                gravity = Gravity.CENTER;
                tm = 0;
                lm = 0;
            } else if (childComponent.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_CAROUSEL_TITLE_KEY)) ||
                    childComponent.getKey()
                            .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_CAROUSEL_INFO_KEY))) {
                tm -= viewHeight;
                viewHeight *= 2;
            } else if (childComponent.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_THUMBNAIL_TITLE_KEY))) {
                tm -= viewHeight/2;
                viewHeight *= 1.5;
            }
        }
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(viewWidth, viewHeight);
        marginLayoutParams.setMargins(lm, tm, 0, 0);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(marginLayoutParams);
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
        if (view instanceof TextView) {
            layoutParams.gravity = gravity;
            int fontsize = getFontsize(getContext(), childComponent);
            if (fontsize > 0) {
                ((TextView) view).setTextSize((float) fontsize);
            }
        }
        Log.d(TAG, "View params key: " +
                childComponent.getKey() +
                " width: " +
                viewWidth +
                " height: " +
                viewHeight +
                " top margin: " +
                tm +
                " left margin: " +
                lm +
                " parent width: " +
                parentViewWidth +
                " parent height: " +
                parentViewHeight);
        view.setLayoutParams(layoutParams);
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public boolean isTablet(Context context) {
        int largeScreenLayout =
                (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        int xLargeScreenLayout =
                (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);

        return (largeScreenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                xLargeScreenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE);
    }

    public boolean isLandscape(Context context) {
        int layoutDirection = context.getResources().getConfiguration().getLayoutDirection();
        return layoutDirection == Configuration.ORIENTATION_LANDSCAPE;
    }

    public boolean shouldHideOnFullScreenLandscape() {
        return hideOnFullscreenLandscape;
    }

    public void setHideOnFullscreenLandscape(boolean hideOnFullscreenLandscape) {
        this.hideOnFullscreenLandscape = hideOnFullscreenLandscape;
    }

    public Action1<LifecycleStatus> getOnLifecycleChangeHandler() {
        return onLifecycleChangeHandler;
    }

    protected void initializeComponentHasViewList(int size) {
        componentHasViewList = new boolean[size];
    }

    protected ViewGroup createChildrenContainer() {
        childrenContainer = new FrameLayout(getContext());
        int viewWidth = (int) getViewWidth(getContext(), getLayout(), (float) LayoutParams.MATCH_PARENT);
        int viewHeight = (int) getViewHeight(getContext(), getLayout(), (float) LayoutParams.MATCH_PARENT);
        FrameLayout.LayoutParams childContainerLayoutParams =
                new FrameLayout.LayoutParams(viewWidth, viewHeight);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
        this.addView(childrenContainer);
        return childrenContainer;
    }

    protected float getViewWidth(Context context, Layout layout, float defaultWidth) {
        if (layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    float width = getViewWidth(tabletLandscape);
                    if (width != -1.0f) {
                        return convertDpToPixel(width, context);
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    float width = getViewHeight(tabletPortrait);
                    if (width != -1.0f) {
                        return convertDpToPixel(width, context);
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                float width = getViewWidth(mobile);
                if (width != -1.0f) {
                    return convertDpToPixel(width, context);
                }
            }
        }
        return defaultWidth;
    }

    protected float getViewHeight(Context context, Layout layout, float defaultHeight) {
        if (layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    float height = getViewHeight(tabletLandscape);
                    if (height != -1.0f) {
                        return convertDpToPixel(height, context);
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    float height = getViewHeight(tabletPortrait);
                    if (height != -1.0f) {
                        return convertDpToPixel(height, context);
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                float height = getViewHeight(mobile);
                if (height != -1.0f) {
                    return convertDpToPixel(height, context);
                }
            }
        }
        return defaultHeight;
    }

    protected float getViewWidth(TabletLandscape tabletLandscape) {
        if (tabletLandscape != null) {
            if (tabletLandscape.getWidth() != null) {
                return tabletLandscape.getWidth();
            }
        }
        return -1.0f;
    }

    protected float getViewHeight(TabletLandscape tabletLandscape) {
        if (tabletLandscape != null) {
            if (tabletLandscape.getHeight() != null) {
                return tabletLandscape.getHeight();
            }
        }
        return -1.0f;
    }

    protected float getViewWidth(TabletPortrait tabletPortrait) {
        if (tabletPortrait != null) {
            if (tabletPortrait.getWidth() != null) {
                return tabletPortrait.getWidth();
            }
        }
        return -1.0f;
    }

    protected float getViewHeight(TabletPortrait tabletPortrait) {
        if (tabletPortrait != null) {
            if (tabletPortrait.getHeight() != null) {
                return tabletPortrait.getHeight();
            }
        }
        return -1.0f;
    }

    protected float getViewWidth(Mobile mobile) {
        if (mobile != null) {
            if (mobile.getWidth() != null) {
                return mobile.getWidth();
            }
        }
        return -1.0f;
    }

    protected float getViewHeight(Mobile mobile) {
        if (mobile != null) {
            if (mobile.getHeight() != null) {
                return mobile.getHeight();
            }
        }
        return -1.0f;
    }

    protected int getFontsize(Context context, Component component) {
        if (component.getFontSize() > 0) {
            return component.getFontSize();
        }
        if (isTablet(context)) {
            if (isLandscape(context)) {
                if (component.getLayout().getTabletLandscape().getFontSize() > 0) {
                    return component.getLayout().getTabletLandscape().getFontSize();
                }
            } else {
                if (component.getLayout().getTabletPortrait().getFontSize() > 0) {
                    return component.getLayout().getTabletPortrait().getFontSize();
                }
            }
        } else {
            if (component.getLayout().getMobile().getFontSize() > 0) {
                return component.getLayout().getMobile().getFontSize();
            }
        }
        return 0;
    }

    protected boolean shouldShowView(Component component) {
        if (isTablet(getContext())) {
            if (!component.isVisibleForTablet() && component.isVisibleForPhone()) {
                return false;
            }
        } else {
            if (component.isVisibleForTablet() && !component.isVisibleForPhone()) {
                return false;
            }
        }
        return true;
    }
}
