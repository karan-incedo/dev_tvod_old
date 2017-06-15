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

    public static final int STANDARD_MOBILE_WIDTH_PX = 375;
    public static final int STANDARD_MOBILE_HEIGHT_PX = 667;

    public static final int STANDARD_TABLET_WIDTH_PX = 768;
    public static final int STANDARD_TABLET_HEIGHT_PX = 1024;

    protected static int DEVICE_WIDTH;
    protected static int DEVICE_HEIGHT;

    protected ViewGroup childrenContainer;
    protected boolean[] componentHasViewList;
    protected Action1<Boolean> onOrientationChangeHandler;
    protected Action1<LifecycleStatus> onLifecycleChangeHandler;

    public BaseView(Context context) {
        super(context);
        DEVICE_WIDTH = getContext().getResources().getDisplayMetrics().widthPixels;
        DEVICE_HEIGHT = getContext().getResources().getDisplayMetrics().heightPixels;
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
                    // NO-OP
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
                                            Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                            boolean useMarginsAsPercentages,
                                            boolean useWidthOfScreen) {
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
                        viewWidth = Math.round(DEVICE_WIDTH * (getViewWidth(tabletLandscape) / STANDARD_TABLET_WIDTH_PX));
                        if (tabletLandscape.getXAxis() != null) {
                            float scaledX = DEVICE_WIDTH * (tabletLandscape.getXAxis() / STANDARD_TABLET_WIDTH_PX);
                            lm = Math.round(scaledX);
                        }
                    }
                    if (getViewHeight(tabletLandscape) != -1) {
                        viewHeight = (int) convertDpToPixel(getViewHeight(tabletLandscape), getContext());
                        if (tabletLandscape.getYAxis() != null) {
                            float scaledY = DEVICE_HEIGHT * (tabletLandscape.getYAxis() / STANDARD_TABLET_HEIGHT_PX);
                            tm = Math.round(scaledY);
                        }
                    }
                    if (getViewWidth(tabletLandscape) == -1 && tabletLandscape.getXAxis() != null) {
                        float scaledX = DEVICE_WIDTH * (tabletLandscape.getXAxis() / STANDARD_TABLET_WIDTH_PX);
                        lm = Math.round(scaledX);
                    } else if (tabletLandscape.getLeftMargin() != null) {
                        lm = Math.round(convertDpToPixel(tabletLandscape.getLeftMargin(), getContext()));
                    } else if (tabletLandscape.getRightMargin() != null) {
                        int lmDiff = viewWidth;
                        if (lmDiff < 0) {
                            lmDiff = 0;
                        }
                        if (parentViewHeight > 0) {
                            lm = parentViewHeight -
                                    Math.round(convertDpToPixel(tabletLandscape.getRightMargin(), getContext())) -
                                    lmDiff;
                        } else {
                            // TODO: Correct for measurements when parent view width is not available
                        }
                    }
                    if (useMarginsAsPercentages) {
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
                    } else {
                        if (tabletLandscape.getTopMargin() != null) {
                            tm = Math.round(convertDpToPixel(tabletLandscape.getTopMargin(), getContext()));
                        } else if (tabletLandscape.getBottomMargin() != null && tabletLandscape.getBottomMargin() > 0) {
                            int tmDiff = viewHeight;
                            if (tmDiff < 0) {
                                tmDiff = 0;
                            }
                            if (parentViewHeight > 0) {
                                tm = parentViewHeight -
                                        Math.round(convertDpToPixel(tabletLandscape.getBottomMargin(), getContext())) -
                                        tmDiff;
                            } else {
                                // TODO: Correct for measurements when parent view height is not available
                            }
                        }
                    }
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                if (tabletPortrait != null) {
                    if (getViewWidth(tabletPortrait) != -1) {
                        viewWidth = Math.round(DEVICE_WIDTH * (getViewWidth(tabletPortrait) / STANDARD_TABLET_WIDTH_PX));
                        if (tabletPortrait.getXAxis() != null) {
                            float scaledX = DEVICE_WIDTH * (tabletPortrait.getXAxis() / STANDARD_TABLET_WIDTH_PX);
                            lm = Math.round(scaledX);
                        }
                    }
                    if (getViewHeight(tabletPortrait) != -1) {
                        viewHeight = (int) convertDpToPixel(getViewHeight(tabletPortrait), getContext());
                        if (tabletPortrait.getYAxis() != null) {
                            float scaledY = DEVICE_HEIGHT * (tabletPortrait.getYAxis() / STANDARD_TABLET_HEIGHT_PX);
                            tm = Math.round(scaledY);
                        }
                    }
                    if (getViewWidth(tabletPortrait) == -1 && tabletPortrait.getXAxis() != null) {
                        float scaledX = DEVICE_WIDTH * (tabletPortrait.getXAxis() / STANDARD_TABLET_WIDTH_PX);
                        lm = Math.round(scaledX);
                    } else if (tabletPortrait.getLeftMargin() != null) {
                        lm = Math.round(convertDpToPixel(tabletPortrait.getLeftMargin(), getContext()));
                    } else if (tabletPortrait.getRightMargin() != null) {
                        int lmDiff = viewWidth;
                        if (lmDiff < 0) {
                            lmDiff = 0;
                        }
                        if (parentViewHeight > 0) {
                            lm = parentViewHeight -
                                    Math.round(convertDpToPixel(tabletPortrait.getRightMargin(), getContext())) -
                                    lmDiff;
                        } else {
                            // TODO: Correct for measurements when parent view width is not available
                        }
                    }
                    if (useMarginsAsPercentages) {
                        if (tabletPortrait.getTopMargin() != null) {
                            tm = Math.round((tabletPortrait.getTopMargin() / 100.0f) * measuredHeight);
                        } else if (tabletPortrait.getBottomMargin() != null) {
                            int marginDiff = viewHeight;
                            if (marginDiff < 0) {
                                marginDiff = 0;
                            }
                            tm = Math.round(((100.0f - tabletPortrait.getBottomMargin()) / 100.0f) * measuredHeight) -
                                    Math.round(convertDpToPixel(marginDiff, getContext()));
                        }
                    } else {
                        if (tabletPortrait.getTopMargin() != null) {
                            tm = Math.round(convertDpToPixel(tabletPortrait.getTopMargin(), getContext()));
                        } else if (tabletPortrait.getBottomMargin() != null && tabletPortrait.getBottomMargin() > 0) {
                            int tmDiff = viewHeight;
                            if (tmDiff < 0) {
                                tmDiff = 0;
                            }
                            if (parentViewHeight > 0) {
                                tm = parentViewHeight -
                                        Math.round(convertDpToPixel(tabletPortrait.getBottomMargin(), getContext())) -
                                        tmDiff;
                            } else {
                                // TODO: Correct for measurements when parent view height is not available
                            }
                        }
                    }
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            if (mobile != null) {
                if (getViewWidth(mobile) != -1) {
                    viewWidth = Math.round(DEVICE_WIDTH * (getViewWidth(mobile) / STANDARD_MOBILE_WIDTH_PX));
                    if (mobile.getXAxis() != null) {
                        float scaledX = DEVICE_WIDTH * (mobile.getXAxis() / STANDARD_MOBILE_WIDTH_PX);
                        lm = Math.round(scaledX);
                    }
                }
                if (getViewHeight(mobile) != -1) {
                    viewHeight = (int) convertDpToPixel(getViewHeight(mobile), getContext());
                    if (mobile.getYAxis() != null) {
                        float scaledY = DEVICE_HEIGHT * (mobile.getYAxis() / STANDARD_MOBILE_HEIGHT_PX);
                        tm = Math.round(scaledY);
                    }
                }
                if (getViewWidth(mobile) == -1 && mobile.getXAxis() != null) {
                    float scaledX = DEVICE_WIDTH * (mobile.getXAxis() / STANDARD_MOBILE_WIDTH_PX);
                    lm = Math.round(scaledX);
                } else if (mobile.getLeftMargin() != null) {
                    float scaledLm = DEVICE_WIDTH * (mobile.getLeftMargin() / STANDARD_MOBILE_WIDTH_PX);
                    lm = Math.round(scaledLm);
                } else if (mobile.getRightMargin() != null) {
                    int lmDiff = viewWidth;
                    if (lmDiff < 0) {
                        lmDiff = 0;
                    }
                    if (parentViewHeight > 0) {
                        float scaledRm = DEVICE_WIDTH * (mobile.getRightMargin() / STANDARD_MOBILE_WIDTH_PX);
                        lm = parentViewHeight - Math.round(scaledRm) - lmDiff;
                    } else {
                        // TODO: Correct for measurements when parent view width is not available
                    }
                }
                if (mobile.getTopMargin() != null) {
                    float scaledTm = DEVICE_HEIGHT * (mobile.getTopMargin() / STANDARD_MOBILE_WIDTH_PX);
                    tm = Math.round(scaledTm);
                } else if (mobile.getBottomMargin() != null && mobile.getBottomMargin() > 0) {
                    int tmDiff = viewHeight;
                    if (tmDiff < 0) {
                        tmDiff = 0;
                    }
                    if (parentViewHeight > 0) {
                        float scaledBm = DEVICE_HEIGHT * (mobile.getBottomMargin() / STANDARD_MOBILE_HEIGHT_PX);
                        tm = parentViewHeight - Math.round(scaledBm) - tmDiff;
                    } else {
                        // TODO: Correct for measurements when parent view height is not available
                    }
                }
            }
        }

        int gravity = Gravity.NO_GRAVITY;
        AppCMSUIKeyType componentType = jsonValueKeyMap.get(childComponent.getType());
        if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY ||
                componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY) {
            if (viewWidth < 0) {
                viewWidth = LayoutParams.MATCH_PARENT;
            }
            if (jsonValueKeyMap.get(childComponent.getTextAlignment()) == AppCMSUIKeyType.PAGE_TEXTALIGNMENT_CENTER_KEY) {
                ((TextView) view).setGravity(Gravity.CENTER_HORIZONTAL);
            }
            AppCMSUIKeyType componentKey = jsonValueKeyMap.get(childComponent.getKey());
            if (componentKey == null) {
                componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            switch (componentKey) {
                case PAGE_PLAY_IMAGE_KEY:
                    gravity = Gravity.CENTER;
                    tm = 0;
                    lm = 0;
                    break;
                case PAGE_CAROUSEL_TITLE_KEY:
                case PAGE_CAROUSEL_INFO_KEY:
                    tm -= viewHeight;
                    viewHeight *= 2;
                    break;
                case PAGE_THUMBNAIL_TITLE_KEY:
                    tm -= viewHeight / 2;
                    viewHeight *= 1.5;
                    break;
                default:
            }

            int fontsize = getFontsize(getContext(), childComponent);
            if (fontsize > 0) {
                ((TextView) view).setTextSize((float) fontsize);
            }
        }

        if (useWidthOfScreen) {
            viewWidth = DEVICE_WIDTH;
        }

        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(viewWidth, viewHeight);
        marginLayoutParams.setMargins(lm, tm, 0, 0);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(marginLayoutParams);
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
        if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY ||
                componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY) {
            layoutParams.gravity = gravity;
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

    public static boolean isTablet(Context context) {
        int largeScreenLayout =
                (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        int xLargeScreenLayout =
                (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);

        return (largeScreenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                xLargeScreenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE);
    }

    public static boolean isLandscape(Context context) {
        int layoutDirection = context.getResources().getConfiguration().getLayoutDirection();
        return layoutDirection == Configuration.ORIENTATION_LANDSCAPE;
    }

    public Action1<LifecycleStatus> getOnLifecycleChangeHandler() {
        return onLifecycleChangeHandler;
    }

    public static void setViewWidth(Context context, Layout layout, float width) {
        if (layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    if (layout.getTabletLandscape() != null) {
                        layout.getTabletLandscape().setWidth(width);
                    }
                } else {
                    if (layout.getTabletPortrait() != null) {
                        layout.getTabletPortrait().setWidth(width);
                    }
                }
            } else {
                if (layout.getMobile() != null) {
                    layout.getMobile().setWidth(width);
                }
            }
        }
    }

    public static void setViewHeight(Context context, Layout layout, float height) {
        if (layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    if (layout.getTabletLandscape() != null) {
                        layout.getTabletLandscape().setHeight(height);
                    }
                } else {
                    if (layout.getTabletPortrait() != null) {
                        layout.getTabletPortrait().setHeight(height);
                    }
                }
            } else {
                if (layout.getMobile() != null) {
                    layout.getMobile().setHeight(height);
                }
            }
        }
    }

    public static float getViewWidth(Context context, Layout layout, float defaultWidth) {
        if (layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    float width = getViewWidth(tabletLandscape);
                    if (width != -1.0f) {
                        return DEVICE_WIDTH * (width / STANDARD_TABLET_HEIGHT_PX);
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    float width = getViewWidth(tabletPortrait);
                    if (width != -1.0f) {
                        return DEVICE_WIDTH * (width / STANDARD_TABLET_WIDTH_PX);
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                float width = getViewWidth(mobile);
                if (width != -1.0f) {
                    return DEVICE_WIDTH * (width / STANDARD_MOBILE_WIDTH_PX);
                }
            }
        }
        return defaultWidth;
    }

    public static float getViewHeight(Context context, Layout layout, float defaultHeight) {
        if (layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    float height = getViewHeight(tabletLandscape);
                    if (height != -1.0f) {
                        return DEVICE_HEIGHT * (height / STANDARD_TABLET_WIDTH_PX);
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    float height = getViewHeight(tabletPortrait);
                    if (height != -1.0f) {
                        return DEVICE_HEIGHT * (height / STANDARD_TABLET_HEIGHT_PX);
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                float height = getViewHeight(mobile);
                if (height != -1.0f) {
                    return DEVICE_HEIGHT * (height / STANDARD_MOBILE_HEIGHT_PX);
                }
            }
        }
        return defaultHeight;
    }

    public static float getFontSizeKey(Context context, Layout layout) {
        if (isTablet(context)) {
            if (isLandscape(context)) {
                if (layout.getTabletLandscape().getFontSizeKey() != null) {
                    return layout.getTabletLandscape().getFontSizeKey();
                }
            } else {
                if (layout.getTabletPortrait().getFontSizeKey() != null) {
                    return layout.getTabletPortrait().getFontSizeKey();
                }
            }
        } else {
            if (layout.getMobile().getFontSizeKey() != null) {
                return layout.getMobile().getFontSizeKey();
            }
        }
        return -1.0f;
    }

    public static float getFontSizeValue(Context context, Layout layout) {
        if (isTablet(context)) {
            if (isLandscape(context)) {
                if (layout.getTabletLandscape().getFontSizeValue() != null) {
                    return layout.getTabletLandscape().getFontSizeValue();
                }
            } else {
                if (layout.getTabletPortrait().getFontSizeValue() != null) {
                    return layout.getTabletPortrait().getFontSizeValue();
                }
            }
        } else {
            if (layout.getMobile().getFontSizeValue() != null) {
                layout.getMobile().getFontSizeValue();
            }
        }
        return -1.0f;
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

    protected static float getViewWidth(TabletLandscape tabletLandscape) {
        if (tabletLandscape != null) {
            if (tabletLandscape.getWidth() != null) {
                return tabletLandscape.getWidth();
            }
        }
        return -1.0f;
    }

    protected static float getViewHeight(TabletLandscape tabletLandscape) {
        if (tabletLandscape != null) {
            if (tabletLandscape.getHeight() != null) {
                return tabletLandscape.getHeight();
            }
        }
        return -1.0f;
    }

    protected static float getViewWidth(TabletPortrait tabletPortrait) {
        if (tabletPortrait != null) {
            if (tabletPortrait.getWidth() != null) {
                return tabletPortrait.getWidth();
            }
        }
        return -1.0f;
    }

    protected static float getViewHeight(TabletPortrait tabletPortrait) {
        if (tabletPortrait != null) {
            if (tabletPortrait.getHeight() != null) {
                return tabletPortrait.getHeight();
            }
        }
        return -1.0f;
    }

    protected static float getViewWidth(Mobile mobile) {
        if (mobile != null) {
            if (mobile.getWidth() != null) {
                return mobile.getWidth();
            }
        }
        return -1.0f;
    }

    protected static float getViewHeight(Mobile mobile) {
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
