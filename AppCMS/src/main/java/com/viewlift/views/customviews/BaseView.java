package com.viewlift.views.customviews;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Mobile;
import com.viewlift.models.data.appcms.ui.page.TabletLandscape;
import com.viewlift.models.data.appcms.ui.page.TabletPortrait;

import java.util.Map;

/**
 * Created by viewlift on 5/17/17.
 */

public abstract class BaseView extends FrameLayout {
    public static final int STANDARD_MOBILE_WIDTH_PX = 375;
    public static final int STANDARD_MOBILE_HEIGHT_PX = 667;
    public static final int STANDARD_TABLET_WIDTH_PX = 768;
    public static final int STANDARD_TABLET_HEIGHT_PX = 1024;
    protected static final float TABLET_LANDSCAPE_HEIGHT_SCALE = 1.06f;
    protected static int DEVICE_WIDTH;
    protected static int DEVICE_HEIGHT;

    protected ViewGroup childrenContainer;
    protected boolean[] componentHasViewList;

    public BaseView(Context context) {
        super(context);
        DEVICE_WIDTH = getContext().getResources().getDisplayMetrics().widthPixels;
        DEVICE_HEIGHT = getContext().getResources().getDisplayMetrics().heightPixels;
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
    }

    public static float convertDpToPixel(float dp, Context context) {
        if (context != null) {
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        }
        return 0.0f;
    }

    public static float convertWidthDpToPixel(Layout layout, Context context) {
        if (BaseView.isTablet(context)) {
            if (BaseView.isLandscape(context)) {
                return convertDpToPixel(layout.getTabletLandscape().getWidth(), context);
            } else {
                return convertDpToPixel(layout.getTabletPortrait().getWidth(), context);
            }
        } else {
            return convertDpToPixel(layout.getMobile().getWidth(), context);
        }
    }

    public static float convertHeightDpToPixel(Layout layout, Context context) {
        if (BaseView.isTablet(context)) {
            if (BaseView.isLandscape(context)) {
                return convertDpToPixel(layout.getTabletLandscape().getHeight(), context);
            } else {
                return convertDpToPixel(layout.getTabletPortrait().getHeight(), context);
            }
        } else {
            return convertDpToPixel(layout.getMobile().getHeight(), context);
        }
    }

    public static float convertPixelsToDp(float px, Context context) {
        if (context != null) {
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            return dp;
        }
        return 0.0f;
    }

    public static boolean isTablet(Context context) {
        if (context != null) {
            int largeScreenLayout =
                    (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
            int xLargeScreenLayout =
                    (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);

            if (largeScreenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                    xLargeScreenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE) {

                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                float yInches = metrics.heightPixels / metrics.ydpi;
                float xInches = metrics.widthPixels / metrics.xdpi;
                double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);

                if (diagonalInches >= 6.5) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isLandscape(Context context) {
        return context != null
                && context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static float getLeftDrawableHeight(Context context,
                                              Layout layout,
                                              float defaultValue) {
        if (BaseView.isTablet(context)) {
            if (BaseView.isLandscape(context)) {
                if (0 < layout.getTabletLandscape().getLeftDrawableHeight()) {
                    return convertVerticalValue(context, layout.getTabletLandscape().getLeftDrawableHeight());
                }
            } else {
                if (0 < layout.getTabletPortrait().getLeftDrawableHeight()) {
                    return convertVerticalValue(context, layout.getTabletPortrait().getLeftDrawableHeight());
                }
            }
        } else {
            if (0 < layout.getMobile().getLeftDrawableHeight()) {
                return convertVerticalValue(context, layout.getMobile().getLeftDrawableHeight());
            }
        }
        return defaultValue;
    }

    public static float getLeftDrawableWidth(Context context,
                                              Layout layout,
                                              float defaultValue) {
        if (BaseView.isTablet(context)) {
            if (BaseView.isLandscape(context)) {
                if (0 < layout.getTabletLandscape().getLeftDrawableWidth()) {
                    return convertVerticalValue(context, layout.getTabletLandscape().getLeftDrawableWidth());
                }
            } else {
                if (0 < layout.getTabletPortrait().getLeftDrawableWidth()) {
                    return convertVerticalValue(context, layout.getTabletPortrait().getLeftDrawableWidth());
                }
            }
        } else {
            if (0 < layout.getMobile().getLeftDrawableWidth()) {
                return convertVerticalValue(context, layout.getMobile().getLeftDrawableWidth());
            }
        }
        return defaultValue;
    }

    public static float convertHorizontalValue(Context context, float value) {
        if (context != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    return DEVICE_WIDTH * (value / STANDARD_TABLET_HEIGHT_PX);
                } else {
                    return DEVICE_WIDTH * (value / STANDARD_TABLET_WIDTH_PX);
                }
            }
            return DEVICE_WIDTH * (value / STANDARD_MOBILE_WIDTH_PX);
        }
        return 0.0f;
    }

    public static float convertVerticalValue(Context context, float value) {
        if (context != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    return DEVICE_HEIGHT * (value / STANDARD_TABLET_WIDTH_PX);
                } else {
                    return DEVICE_HEIGHT * (value / STANDARD_TABLET_HEIGHT_PX);
                }
            }
            return DEVICE_HEIGHT * (value / STANDARD_MOBILE_HEIGHT_PX);
        }
        return 0.0f;
    }

    public static float getScaledHorizontalValue(Context context, float value) {
        if (context != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    return DEVICE_WIDTH * (value / STANDARD_TABLET_HEIGHT_PX);
                } else {
                    return DEVICE_WIDTH * (value / STANDARD_TABLET_WIDTH_PX);
                }
            } else {
                return DEVICE_WIDTH * (value / STANDARD_MOBILE_WIDTH_PX);
            }
        }

        return -1.0f;
    }

    public static float getHorizontalMargin(Context context, Layout layout) {
        if (context != null && layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    if (tabletLandscape.getLeftMargin() != 0f) {
                        return DEVICE_WIDTH * (tabletLandscape.getLeftMargin() / STANDARD_TABLET_HEIGHT_PX);
                    } else if (tabletLandscape.getRightMargin() != 0f) {
                        return DEVICE_WIDTH * (tabletLandscape.getRightMargin() / STANDARD_TABLET_HEIGHT_PX);
                    } else {
                        return -1.0f;
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    if (tabletPortrait.getLeftMargin() != 0f) {
                        return DEVICE_WIDTH * (tabletPortrait.getLeftMargin() / STANDARD_TABLET_WIDTH_PX);
                    } else if (tabletPortrait.getRightMargin() != 0f) {
                        return DEVICE_WIDTH * (tabletPortrait.getRightMargin() / STANDARD_TABLET_WIDTH_PX);
                    } else {
                        return -1.0f;
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                if (mobile.getLeftMargin() != 0f) {
                    return DEVICE_WIDTH * (mobile.getLeftMargin() / STANDARD_MOBILE_WIDTH_PX);
                } else if (mobile.getRightMargin() != 0f) {
                    return DEVICE_WIDTH * (mobile.getRightMargin() / STANDARD_MOBILE_WIDTH_PX);
                } else {
                    return -1.0f;
                }
            }
        }
        return -1.0f;
    }

    public static float getVerticalMargin(Context context, Layout layout, int viewHeight, int parentViewHeight) {
        if (context != null && layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    if (tabletLandscape.getTopMargin() != 0f) {
                        return DEVICE_HEIGHT * (tabletLandscape.getTopMargin() / STANDARD_TABLET_WIDTH_PX);
                    } else if (tabletLandscape.getBottomMargin() != 0f) {
                        float bm = DEVICE_HEIGHT * (tabletLandscape.getBottomMargin() / STANDARD_TABLET_WIDTH_PX);
                        int tmDiff = viewHeight;
                        if (tmDiff < 0) {
                            tmDiff = 0;
                        }
                        return parentViewHeight - bm - tmDiff;
                    } else {
                        return -1.0f;
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    if (tabletPortrait.getTopMargin() != 0f) {
                        return DEVICE_HEIGHT * (tabletPortrait.getTopMargin() / STANDARD_TABLET_HEIGHT_PX);
                    } else if (tabletPortrait.getBottomMargin() != 0f) {
                        float bm = DEVICE_HEIGHT * (tabletPortrait.getBottomMargin() / STANDARD_TABLET_HEIGHT_PX);
                        int tmDiff = viewHeight;
                        if (tmDiff < 0) {
                            tmDiff = 0;
                        }
                        return parentViewHeight - bm - tmDiff;
                    } else {
                        return -1.0f;
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                if (mobile.getTopMargin() != 0f) {
                    return DEVICE_HEIGHT * (mobile.getTopMargin() / STANDARD_MOBILE_HEIGHT_PX);
                } else if (mobile.getBottomMargin() != 0f) {
                    float bm = DEVICE_HEIGHT * (mobile.getBottomMargin() / STANDARD_MOBILE_HEIGHT_PX);
                    int tmDiff = viewHeight;
                    if (tmDiff < 0) {
                        tmDiff = 0;
                    }
                    return parentViewHeight - bm - tmDiff;
                } else {
                    return -1.0f;
                }
            }
        }
        return -1.0f;
    }

    public static void setViewWidth(Context context, Layout layout, float width) {
        if (context != null && layout != null) {
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
        if (context != null && layout != null) {
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

    public static float getYAxis(Context context, Layout layout, float defaultYValue) {
        if (context != null && layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    if (layout.getTabletLandscape() != null) {
                        return layout.getTabletLandscape().getYAxis();
                    }
                } else {
                    if (layout.getTabletPortrait() != null) {
                        return layout.getTabletPortrait().getYAxis();
                    }
                }
            } else {
                if (layout.getMobile() != null) {
                    return layout.getMobile().getYAxis();
                }
            }
        }
        return defaultYValue;
    }

    public static void setYAxis(Context context, Layout layout, float yValue) {
        if (context != null && layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    if (layout.getTabletLandscape() != null) {
                        layout.getTabletLandscape().setYAxis(yValue);
                    }
                } else {
                    if (layout.getTabletPortrait() != null) {
                        layout.getTabletPortrait().setYAxis(yValue);
                    }
                }
            } else {
                if (layout.getMobile() != null) {
                    layout.getMobile().setYAxis(yValue);
                }
            }
        }
    }

    public static float getSavedViewWidth(Context context, Layout layout, float defaultWidth) {
        if (context != null && layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    float width = 0.0f;
                    if (tabletLandscape != null) {
                        width = tabletLandscape.getSavedWidth();
                    }
                    if (width != -1.0f) {
                        return DEVICE_WIDTH * (width / STANDARD_TABLET_HEIGHT_PX);
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    float width = 0.0f;
                    if (tabletPortrait != null) {
                        width = tabletPortrait.getSavedWidth();
                    }
                    if (width != -1.0f) {
                        return DEVICE_WIDTH * (width / STANDARD_TABLET_WIDTH_PX);
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                float width = 0.0f;
                if (mobile != null) {
                    width = mobile.getSavedWidth();
                }
                if (width != -1.0f) {
                    return DEVICE_WIDTH * (width / STANDARD_MOBILE_WIDTH_PX);
                }
            }
        }
        return defaultWidth;
    }

    public static float getViewWidth(Context context, Layout layout, float defaultWidth) {
        if (context != null && layout != null) {
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

    public static float getThumbnailWidth(Context context, Layout layout, float defaultWidth) {
        if (context != null && layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    float width = getThumbnailWidth(tabletLandscape);
                    if (width != -1.0f) {
                        return DEVICE_WIDTH * (width / STANDARD_TABLET_HEIGHT_PX);
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    float width = getThumbnailWidth(tabletPortrait);
                    if (width != -1.0f) {
                        return DEVICE_WIDTH * (width / STANDARD_TABLET_WIDTH_PX);
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                float width = getThumbnailWidth(mobile);
                if (width != -1.0f) {
                    return DEVICE_WIDTH * (width / STANDARD_MOBILE_WIDTH_PX);
                }
            }
        }
        return defaultWidth;
    }

    public static float getViewMaximumWidth(Context context, Layout layout, float defaultMaximumWidth) {
        if (context != null && layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    float maximumWidth = getViewMaximumWidth(layout.getTabletLandscape());
                    if (maximumWidth >= 0.0f) {
                        return DEVICE_WIDTH * (maximumWidth / STANDARD_TABLET_HEIGHT_PX);
                    }
                } else {
                    float maximumWidth = getViewMaximumWidth(layout.getTabletPortrait());
                    if (maximumWidth >= 0.0f) {
                        return DEVICE_WIDTH * (maximumWidth / STANDARD_TABLET_WIDTH_PX);
                    }
                }
            } else {
                float maximumWidth = getViewMaximumWidth(layout.getMobile());
                if (maximumWidth >= 0.0f) {
                    return DEVICE_WIDTH * (maximumWidth / STANDARD_TABLET_WIDTH_PX);
                }
            }
        }
        return defaultMaximumWidth;
    }

    public static float getThumbnailHeight(Context context, Layout layout, float defaultHeight) {
        if (context != null && layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    float height = getThumbnailHeight(tabletLandscape);
                    if (height >= 0.0f) {
                        return DEVICE_HEIGHT * (height / STANDARD_TABLET_WIDTH_PX);
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    float height = getThumbnailHeight(tabletPortrait);
                    if (height >= 0.0f) {
                        return DEVICE_HEIGHT * (height / STANDARD_TABLET_HEIGHT_PX);
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                float height = getThumbnailHeight(mobile);
                if (height >= 0.0f) {
                    return DEVICE_HEIGHT * (height / STANDARD_MOBILE_HEIGHT_PX);
                }
            }
        }
        return defaultHeight;
    }

    public static float getViewHeight(Context context, Layout layout, float defaultHeight) {
        if (context != null && layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    float height = getViewHeight(tabletLandscape);
                    if (height >= 0.0f) {
                        return DEVICE_HEIGHT * (height / STANDARD_TABLET_WIDTH_PX);
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    float height = getViewHeight(tabletPortrait);
                    if (height >= 0.0f) {
                        return DEVICE_HEIGHT * (height / STANDARD_TABLET_HEIGHT_PX);
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                float height = getViewHeight(mobile);
                if (height >= 0.0f) {
                    return DEVICE_HEIGHT * (height / STANDARD_MOBILE_HEIGHT_PX);
                }
            }
        }
        return defaultHeight;
    }

    public static float getFontSize(Context context, Layout layout) {
        if (context != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    if (layout.getTabletLandscape().getFontSize() != 0f) {
                        return layout.getTabletLandscape().getFontSize();
                    }
                } else {
                    if (layout.getTabletLandscape().getFontSize() != 0f) {
                        return layout.getTabletLandscape().getFontSize();
                    }
                }
            } else {
                if (layout.getMobile().getFontSize() != 0f) {
                    return layout.getMobile().getFontSize();
                }
            }
        }

        return -1.0f;
    }

    public static float getFontSizeKey(Context context, Layout layout) {
        if (context != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    if (layout.getTabletLandscape().getFontSizeKey() != 0f) {
                        return layout.getTabletLandscape().getFontSizeKey();
                    }
                } else {
                    if (layout.getTabletPortrait().getFontSizeKey() != 0f) {
                        return layout.getTabletPortrait().getFontSizeKey();
                    }
                }
            } else {
                if (layout.getMobile().getFontSizeKey() != 0f) {
                    return layout.getMobile().getFontSizeKey();
                }
            }
        }
        return -1.0f;
    }

    public static float getFontSizeValue(Context context, Layout layout) {
        if (context != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    if (layout.getTabletLandscape().getFontSizeValue() != 0f) {
                        return layout.getTabletLandscape().getFontSizeValue();
                    }
                } else {
                    if (layout.getTabletPortrait().getFontSizeValue() != 0f) {
                        return layout.getTabletPortrait().getFontSizeValue();
                    }
                }
            } else {
                if (layout.getMobile().getFontSizeValue() != 0f) {
                    layout.getMobile().getFontSizeValue();
                }
            }
        }
        return -1.0f;
    }

    public static int getDeviceWidth() {
        return DEVICE_WIDTH;
    }

    public static void setDeviceWidth(int deviceWidth) {
        DEVICE_WIDTH = deviceWidth;
    }

    public static int getDeviceHeight() {
        return DEVICE_HEIGHT;
    }

    public static void setDeviceHeight(int deviceHeight) {
        DEVICE_HEIGHT = deviceHeight;
    }

    protected static float getViewWidth(TabletLandscape tabletLandscape) {
        if (tabletLandscape != null) {
            if (tabletLandscape.getWidth() != 0f) {
                return tabletLandscape.getWidth();
            }
        }
        return -1.0f;
    }

    protected static float getThumbnailWidth(TabletLandscape tabletLandscape) {
        if (tabletLandscape != null) {
            if (tabletLandscape.getThumbnailWidth() != 0f) {
                return tabletLandscape.getThumbnailWidth();
            }
        }
        return -1.0f;
    }

    private static float getViewMaximumWidth(TabletLandscape tabletLandscape) {
        if (tabletLandscape != null) {
            if (tabletLandscape.getMaximumWidth() != 0f) {
                return tabletLandscape.getMaximumWidth();
            }
        }
        return -1.0f;
    }

    protected static float getViewHeight(TabletLandscape tabletLandscape) {
        if (tabletLandscape != null) {
            if (tabletLandscape.getHeight() != 0f) {
                return tabletLandscape.getHeight();
            }
        }
        return -1.0f;
    }

    protected static float getThumbnailHeight(TabletLandscape tabletLandscape) {
        if (tabletLandscape != null) {
            if (tabletLandscape.getThumbnailHeight() != 0f) {
                return tabletLandscape.getThumbnailHeight();
            }
        }
        return -1.0f;
    }

    protected static float getViewWidth(TabletPortrait tabletPortrait) {
        if (tabletPortrait != null) {
            if (tabletPortrait.getWidth() != 0f) {
                return tabletPortrait.getWidth();
            }
        }
        return -1.0f;
    }

    protected static float getThumbnailWidth(TabletPortrait tabletPortrait) {
        if (tabletPortrait != null) {
            if (tabletPortrait.getThumbnailWidth() != 0f) {
                return tabletPortrait.getThumbnailWidth();
            }
        }
        return -1.0f;
    }

    private static float getViewMaximumWidth(TabletPortrait tabletPortrait) {
        if (tabletPortrait != null) {
            if (tabletPortrait.getMaximumWidth() != 0f) {
                return tabletPortrait.getMaximumWidth();
            }
        }
        return -1.0f;
    }

    protected static float getViewHeight(TabletPortrait tabletPortrait) {
        if (tabletPortrait != null) {
            if (tabletPortrait.getHeight() != 0f) {
                return tabletPortrait.getHeight();
            }
        }
        return -1.0f;
    }

    protected static float getThumbnailHeight(TabletPortrait tabletPortrait) {
        if (tabletPortrait != null) {
            if (tabletPortrait.getThumbnailHeight() != 0f) {
                return tabletPortrait.getThumbnailHeight();
            }
        }
        return -1.0f;
    }

    protected static float getViewWidth(Mobile mobile) {
        if (mobile != null) {
            if (mobile.getWidth() != 0f) {
                return mobile.getWidth();
            }
        }
        return -1.0f;
    }

    protected static float getThumbnailWidth(Mobile mobile) {
        if (mobile != null) {
            if (mobile.getThumbnailWidth() != 0f) {
                return mobile.getThumbnailWidth();
            }
        }
        return -1.0f;
    }

    private static float getViewMaximumWidth(Mobile mobile) {
        if (mobile != null) {
            if (mobile.getMaximumWidth() != 0f) {
                return mobile.getMaximumWidth();
            }
        }
        return -1.0f;
    }

    protected static float getViewHeight(Mobile mobile) {
        if (mobile != null) {
            if (mobile.getHeight() != 0f) {
                return mobile.getHeight();
            }
        }
        return -1.0f;
    }

    protected static float getThumbnailHeight(Mobile mobile) {
        if (mobile != null) {
            if (mobile.getThumbnailHeight() != 0f) {
                return mobile.getThumbnailHeight();
            }
        }
        return -1.0f;
    }

    public abstract void init();

    protected abstract Component getChildComponent(int index);

    protected abstract Layout getLayout();

    public ViewGroup getChildrenContainer() {
        if (childrenContainer == null) {
            return createChildrenContainer();
        }
        return childrenContainer;
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
                                            boolean useWidthOfScreen,
                                            String viewType) {
        Layout layout = childComponent.getLayout();

        if (!shouldShowView(childComponent)) {
            view.setVisibility(GONE);
            return;
        }

        view.setPadding(0, 0, 0, 0);

        int lm = 0, tm = 0, rm = 0, bm = 0;
        int deviceHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        int deviceWidth = getContext().getResources().getDisplayMetrics().widthPixels;

        int viewWidth = (int) getViewWidth(getContext(), layout, LayoutParams.MATCH_PARENT);
        int viewHeight = (int) getViewHeight(getContext(), layout, LayoutParams.WRAP_CONTENT);

        AppCMSUIKeyType componentType = jsonValueKeyMap.get(childComponent.getType());
        if (componentType == null) {
            componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY ||
                componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY) {
            viewWidth = (int) convertWidthDpToPixel(layout, getContext());
            if (viewWidth == 0) {
                viewWidth = LayoutParams.MATCH_PARENT;
            }
            viewHeight = (int) convertHeightDpToPixel(layout, getContext());
            if (viewHeight == 0) {
                viewHeight = LayoutParams.WRAP_CONTENT;
            }
        }

        int parentViewWidth = (int) getViewWidth(getContext(),
                parentLayout,
                parentView.getMeasuredWidth());
        int parentViewHeight = (int) getViewHeight(getContext(),
                parentLayout,
                parentView.getMeasuredHeight());
        int maxViewWidth = (int) getViewMaximumWidth(getContext(), layout, -1);
        int measuredHeight = parentViewHeight != 0 ? parentViewHeight : deviceHeight;
        int measuredWidth = parentViewWidth != 0 ? parentViewWidth : deviceWidth;
        int gravity = Gravity.NO_GRAVITY;

        if (isTablet(getContext())) {
            if (isLandscape(getContext())) {
                TabletLandscape tabletLandscape = layout.getTabletLandscape();
                if (tabletLandscape != null) {
                    if (tabletLandscape.getXAxis() != 0f) {
                        float scaledX = DEVICE_WIDTH * (tabletLandscape.getXAxis() / STANDARD_TABLET_HEIGHT_PX);
                        lm = Math.round(scaledX);
                    }

                    if (tabletLandscape.getYAxis() != 0f) {
                        float scaledY = DEVICE_HEIGHT * (tabletLandscape.getYAxis() / STANDARD_TABLET_WIDTH_PX);
                        tm = Math.round(scaledY);
                    }

                    if (getViewWidth(tabletLandscape) == -1f && tabletLandscape.getXAxis() != 0f) {
                        float scaledX = DEVICE_WIDTH * (tabletLandscape.getXAxis() / STANDARD_TABLET_HEIGHT_PX);
                        lm = Math.round(scaledX);
                    } else if (tabletLandscape.getLeftMargin() != 0f) {
                        lm = Math.round(DEVICE_WIDTH * (tabletLandscape.getLeftMargin() / STANDARD_TABLET_HEIGHT_PX));
                    } else if (tabletLandscape.getRightMargin() != 0f) {
                        int lmDiff = viewWidth;
                        if (lmDiff < 0) {
                            lmDiff = 0;
                        }

                        if (parentViewWidth > 0) {
                            lm = parentViewWidth -
                                    (int) (DEVICE_WIDTH * (tabletLandscape.getRightMargin() / STANDARD_TABLET_HEIGHT_PX)) -
                                    lmDiff;
                        } else {
                            gravity = Gravity.END;
                        }
                    }
                    if (useMarginsAsPercentages) {
                        if (tabletLandscape.getTopMargin() != 0f) {
                            tm = Math.round((tabletLandscape.getTopMargin() / 100.0f) * measuredHeight);
                        } else if (tabletLandscape.getBottomMargin() != 0f) {
                            int marginDiff = viewHeight;
                            if (marginDiff < 0) {
                                marginDiff = 0;
                            }
                            tm = Math.round(((100.0f - tabletLandscape.getBottomMargin()) / 100.0f) * measuredHeight) -
                                    Math.round(convertDpToPixel(marginDiff, getContext()));
                        }
                    } else {
                        if (tabletLandscape.getTopMargin() != 0f) {
                            tm = (int) (DEVICE_HEIGHT * (tabletLandscape.getTopMargin() / STANDARD_TABLET_WIDTH_PX));
                        } else if (tabletLandscape.getBottomMargin() != 0f && tabletLandscape.getBottomMargin() > 0) {
                            int tmDiff = viewHeight;
                            if (tmDiff < 0) {
                                tmDiff = 0;
                            }

                            if (parentViewHeight > 0) {
                                tm = parentViewHeight -
                                        (int) (DEVICE_HEIGHT * (tabletLandscape.getBottomMargin() / STANDARD_TABLET_WIDTH_PX)) -
                                        tmDiff;
                            } else {
                                gravity = Gravity.BOTTOM;
                                bm = Math.round(DEVICE_HEIGHT * (tabletLandscape.getBottomMargin() / STANDARD_TABLET_WIDTH_PX));
                            }
                        }
                    }
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                if (tabletPortrait != null) {
                    if (tabletPortrait.getXAxis() != 0f) {
                        float scaledX = DEVICE_WIDTH * (tabletPortrait.getXAxis() / STANDARD_TABLET_WIDTH_PX);
                        lm = Math.round(scaledX);
                    }

                    if (tabletPortrait.getYAxis() != 0f) {
                        float scaledY = DEVICE_HEIGHT * (tabletPortrait.getYAxis() / STANDARD_TABLET_HEIGHT_PX);
                        tm = Math.round(scaledY);
                    }

                    if (getViewWidth(tabletPortrait) == -1 && tabletPortrait.getXAxis() != 0f) {
                        float scaledX = DEVICE_WIDTH * (tabletPortrait.getXAxis() / STANDARD_TABLET_WIDTH_PX);
                        lm = Math.round(scaledX);
                    } else if (tabletPortrait.getLeftMargin() != 0f) {
                        lm = Math.round(DEVICE_WIDTH * (tabletPortrait.getLeftMargin() / STANDARD_TABLET_WIDTH_PX));
                    } else if (tabletPortrait.getRightMargin() != 0f) {
                        int lmDiff = viewWidth;
                        if (lmDiff < 0) {
                            lmDiff = 0;
                        }

                        if (parentViewWidth > 0) {
                            lm = parentViewWidth -
                                    Math.round(DEVICE_WIDTH * (tabletPortrait.getRightMargin() / STANDARD_TABLET_WIDTH_PX)) -
                                    lmDiff;
                        } else {
                            gravity = Gravity.END;
                            rm = Math.round(DEVICE_WIDTH * (tabletPortrait.getRightMargin() / STANDARD_TABLET_WIDTH_PX));
                        }
                    }
                    if (useMarginsAsPercentages) {
                        if (tabletPortrait.getTopMargin() != 0f) {
                            tm = Math.round((tabletPortrait.getTopMargin() / 100.0f) * measuredHeight);
                        } else if (tabletPortrait.getBottomMargin() != 0f) {
                            int marginDiff = viewHeight;
                            if (marginDiff < 0) {
                                marginDiff = 0;
                            }
                            tm = Math.round(((100.0f - tabletPortrait.getBottomMargin()) / 100.0f) * measuredHeight) -
                                    Math.round(convertDpToPixel(marginDiff, getContext()));
                        }
                    } else {
                        if (tabletPortrait.getTopMargin() != 0f) {
                            tm = (int) (DEVICE_HEIGHT * (tabletPortrait.getTopMargin() / STANDARD_TABLET_HEIGHT_PX));
                        } else if (tabletPortrait.getBottomMargin() != 0f && tabletPortrait.getBottomMargin() > 0) {
                            int tmDiff = viewHeight;
                            if (tmDiff < 0) {
                                tmDiff = 0;
                            }
                            if (parentViewHeight > 0) {
                                tm = parentViewHeight -
                                        (int) (DEVICE_HEIGHT * (tabletPortrait.getBottomMargin() / STANDARD_TABLET_HEIGHT_PX)) -
                                        tmDiff;
                            } else {
                                gravity = Gravity.BOTTOM;
                                bm = Math.round(DEVICE_HEIGHT * (tabletPortrait.getBottomMargin() / STANDARD_TABLET_HEIGHT_PX));
                            }
                        }
                    }
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            if (mobile != null) {
                if (mobile.getXAxis() != 0f) {
                    float scaledX = DEVICE_WIDTH * (mobile.getXAxis() / STANDARD_MOBILE_WIDTH_PX);
                    lm = Math.round(scaledX);
                }

                if (mobile.getYAxis() != 0f) {
                    float scaledY = DEVICE_HEIGHT * (mobile.getYAxis() / STANDARD_MOBILE_HEIGHT_PX);
                    tm = Math.round(scaledY);
                }

                if (getViewWidth(mobile) == -1 && mobile.getXAxis() != 0f) {
                    float scaledX = DEVICE_WIDTH * (mobile.getXAxis() / STANDARD_MOBILE_WIDTH_PX);
                    lm = Math.round(scaledX);
                } else if (mobile.getLeftMargin() != 0f) {
                    float scaledLm = DEVICE_WIDTH * (mobile.getLeftMargin() / STANDARD_MOBILE_WIDTH_PX);
                    lm = Math.round(scaledLm);
                    if (mobile.getRightMargin() != 0f) {
                        float scaledRm = DEVICE_WIDTH * (mobile.getRightMargin() / STANDARD_MOBILE_WIDTH_PX);
                        rm = Math.round(scaledRm);
                    }
                } else if (mobile.getRightMargin() != 0f) {
                    int lmDiff = viewWidth;
                    if (lmDiff < 0) {
                        lmDiff = 0;
                    }

                    if (parentViewWidth > 0) {
                        float scaledRm = DEVICE_WIDTH * (mobile.getRightMargin() / STANDARD_MOBILE_WIDTH_PX);
                        lm = parentViewWidth - Math.round(scaledRm) - lmDiff;
                    } else {
                        gravity = Gravity.END;
                        rm = Math.round(DEVICE_WIDTH * (mobile.getRightMargin() / STANDARD_MOBILE_WIDTH_PX));
                    }
                }
                if (useMarginsAsPercentages) {
                    if (mobile.getTopMargin() != 0f) {
                        tm = Math.round((mobile.getTopMargin() / 100.0f) * measuredHeight);
                    } else if (mobile.getBottomMargin() != 0f) {
                        int marginDiff = viewHeight;
                        if (marginDiff < 0) {
                            marginDiff = 0;
                        }
                        tm = Math.round(((100.0f - mobile.getBottomMargin()) / 100.0f) * measuredHeight) -
                                Math.round(convertDpToPixel(marginDiff, getContext()));
                    }
                } else {
                    if (mobile.getTopMargin() != 0f) {
                        tm = Math.round(DEVICE_HEIGHT * (mobile.getTopMargin() / STANDARD_TABLET_HEIGHT_PX));
                    } else if (mobile.getBottomMargin() != 0f && mobile.getBottomMargin() > 0) {
                        int tmDiff = viewHeight;
                        if (tmDiff < 0) {
                            tmDiff = 0;
                        }
                        if (parentViewHeight > 0) {
                            tm = parentViewHeight -
                                    (int) (DEVICE_HEIGHT * (mobile.getBottomMargin() / STANDARD_TABLET_HEIGHT_PX)) -
                                    tmDiff;
                        } else {
                            gravity = Gravity.BOTTOM;
                            bm = Math.round(DEVICE_HEIGHT * (mobile.getBottomMargin() / STANDARD_TABLET_HEIGHT_PX));
                        }
                    }
                }
            }
        }

        AppCMSUIKeyType componentKey = jsonValueKeyMap.get(childComponent.getKey());
        if (componentKey == null) {
            componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY ||
                componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY) {

            if (viewWidth < 0) {
                viewWidth = LayoutParams.MATCH_PARENT;
            }

            if (jsonValueKeyMap.get(childComponent.getTextAlignment()) == AppCMSUIKeyType.PAGE_TEXTALIGNMENT_CENTER_KEY) {
                ((TextView) view).setGravity(Gravity.CENTER);
            }

            if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY &&
                    view instanceof Spinner) {
                viewHeight = LayoutParams.WRAP_CONTENT;
                viewWidth = LayoutParams.WRAP_CONTENT;
            }

            AppCMSUIKeyType componentViewType = jsonValueKeyMap.get(viewType);
            if (componentViewType == null) {
                componentViewType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }

            switch (componentKey) {
                case PAGE_TRAY_TITLE_KEY:
                    if (isTablet(getContext())) {
                        if (isLandscape(getContext())) {
                            tm -= viewHeight / 6;
                            viewHeight *= 1.5;
                        }
                    }
                    break;

                case PAGE_VIDEO_PLAY_BUTTON_KEY:
                    if (jsonValueKeyMap.get(viewType) != AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                        lm -= 8;
                        rm -= 8;
                        bm -= 8;
                        tm -= 8;
                    }
                    break;

                case PAGE_PLAY_IMAGE_KEY:
                    if (AppCMSUIKeyType.PAGE_HISTORY_MODULE_KEY != jsonValueKeyMap.get(viewType)
                            && AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY != jsonValueKeyMap.get(viewType)
                            && AppCMSUIKeyType.PAGE_WATCHLIST_MODULE_KEY != jsonValueKeyMap.get(viewType)
                            && componentViewType != AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                        gravity = Gravity.CENTER;
                        lm -= 40;
                        rm -= 40;
                        bm -= 40;
                        tm -= 40;
                    }
                    break;

                case PAGE_VIDEO_CLOSE_KEY:
                    lm -= 8;
                    bm -= 8;
                    break;

                case PAGE_DOWNLOAD_SETTING_TITLE_KEY:
                    gravity = Gravity.CENTER_HORIZONTAL;
                    bm = viewHeight / 2;
                    break;

                case PAGE_CAROUSEL_TITLE_KEY:
                    gravity = Gravity.CENTER_HORIZONTAL;
                    if (isLandscape(getContext()) || !isTablet(getContext())) {
                        if (isLandscape(getContext())) {
                            tm -= viewHeight * 5;
                        } else {
                            tm -= viewHeight * 2;
                        }
                        viewHeight *= 2;
                    } else {
                        tm -= viewHeight * 3;
                        viewHeight *= 2;
                    }
                    lm = maxViewWidth / 2 - viewWidth / 2;
                    break;

                case PAGE_CAROUSEL_INFO_KEY:
                    gravity = Gravity.CENTER_HORIZONTAL;
                    if (isTablet(getContext())) {
                        if (isLandscape(getContext())) {
                            tm -= viewHeight * 9;
                        } else {
                            tm -= viewHeight * 5;
                        }
                    } else {
                        tm -= viewHeight * 2;
                    }
                    viewHeight *= 2;
                    lm = maxViewWidth / 2 - viewWidth / 2;
                    break;

                case PAGE_WATCH_VIDEO_KEY:
                    gravity = Gravity.CENTER_HORIZONTAL;
                    lm = maxViewWidth / 2;
                    if (isTablet(getContext()) && !isLandscape(getContext())) {
                        tm -= viewHeight / 2;
                    } else if (isLandscape(getContext())) {
                        tm -= viewHeight * 2;
                    }
                    break;

                case PAGE_VIDEO_SHARE_KEY:
                    if (isTablet(getContext())) {
                        lm -= viewWidth / 2;
                        tm -= (int) (viewWidth * 0.25);
                        viewHeight = (int) (viewWidth * 1.25);
                    } else {
                        lm -= viewWidth / 3;
                    }
                    break;

                case PAGE_API_TITLE:
                    viewHeight *= 1.1;
                    break;

                case PAGE_ADD_TO_WATCHLIST_KEY:
                    if (isTablet(getContext())) {
                        lm -= viewWidth * 0.5;
                    }
                    gravity = Gravity.TOP;
                    break;

                case PAGE_VIDEO_DOWNLOAD_BUTTON_KEY:
                    if (isTablet(getContext())) {
                        lm -= viewWidth * 0.7;
                    }
                    viewWidth = viewHeight;
                    break;

                case PAGE_PLAN_TITLE_KEY:
                    if (childComponent.getTextAlignment().equalsIgnoreCase("right")) {
                        gravity = Gravity.END;
                        rm += convertDpToPixel(8, getContext());
                        view.setTextAlignment(TEXT_ALIGNMENT_TEXT_END);
                    } else {
                        gravity = Gravity.START;
                    }
                    break;

                case PAGE_PLAN_PRICEINFO_KEY:
                    lm += convertDpToPixel(8, getContext());
                    break;

                case PAGE_SETTINGS_PLAN_VALUE_KEY:
                    if (isTablet(getContext())) {
                        if (isLandscape(getContext())) {
                            rm = Math.round(DEVICE_HEIGHT * (childComponent.getLayout().getTabletLandscape().getRightMargin() /
                                    STANDARD_MOBILE_WIDTH_PX));
                        } else {
                            rm = Math.round(DEVICE_WIDTH * (childComponent.getLayout().getTabletPortrait().getRightMargin() /
                                    STANDARD_MOBILE_WIDTH_PX));
                        }
                    } else {
                        rm = Math.round(DEVICE_WIDTH * (childComponent.getLayout().getMobile().getRightMargin() /
                                STANDARD_MOBILE_WIDTH_PX));
                    }

                    if (0 < lm && 0 < rm) {
                        viewWidth = measuredWidth - rm - lm;
                    }
                    break;

                case PAGE_THUMBNAIL_TITLE_KEY:
                    if (jsonValueKeyMap.get(viewType) != null &&
                            (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_CONTINUE_WATCHING_MODULE_KEY ||
                                    jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_TRAY_MODULE_KEY)) {
                        int thumbnailWidth = (int) getThumbnailWidth(getContext(), layout, LayoutParams.MATCH_PARENT);
                        int thumbnailHeight = (int) getThumbnailHeight(getContext(), layout, LayoutParams.WRAP_CONTENT);
                        if (0 < thumbnailHeight && 0 < thumbnailWidth) {
                            if (thumbnailHeight < thumbnailWidth) {
                                int heightByRatio = (int) ((float) thumbnailWidth * 9.0f / 16.0f);
                                tm = heightByRatio + 4;
                            } else {
                                int heightByRatio = (int) ((float) thumbnailWidth * 4.0f / 3.0f);
                                tm = heightByRatio + 4;
                            }
                        }
                    }
                    break;

                case PAGE_VIDEO_AGE_LABEL_KEY:
                    viewWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
                    view.setPadding(4, 0, 4, 0);
                    break;

                default:
                    break;
            }

            if (maxViewWidth != -1) {
                ((TextView) view).setMaxWidth(maxViewWidth);
            }
        } else if (componentType == AppCMSUIKeyType.PAGE_TEXTFIELD_KEY) {
            viewHeight *= 1.2;
        } else if (componentType == AppCMSUIKeyType.PAGE_PROGRESS_VIEW_KEY) {
            if (jsonValueKeyMap.get(viewType) != null) {
                if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_CONTINUE_WATCHING_MODULE_KEY ||
                        jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                    int thumbnailWidth = (int) getThumbnailWidth(getContext(), layout, LayoutParams.MATCH_PARENT);
                    int thumbnailHeight = (int) getThumbnailHeight(getContext(), layout, LayoutParams.WRAP_CONTENT);
                    if (0 < thumbnailHeight && 0 < thumbnailWidth) {
                        int heightByRatio = (int) ((float) thumbnailWidth * 4.0f / 3.0f);
                        if (thumbnailHeight < thumbnailWidth) {
                            heightByRatio = (int) ((float) thumbnailWidth * 9.0f / 16.0f);
                        }

                        tm = heightByRatio - viewHeight;
                    }
                } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_VIDEO_DETAILS_KEY) {
                    viewWidth = (int) getThumbnailWidth(getContext(), layout, LayoutParams.MATCH_PARENT);
                    gravity = Gravity.CENTER_HORIZONTAL;
                }
            }
        } else if (componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY) {
            if (componentKey == AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY) {
                viewWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
                viewHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
            } else if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY) {
                if (getResources().getBoolean(R.bool.video_detail_page_plays_video)) {
                    if (!BaseView.isTablet(getContext())) {
                        if (BaseView.isLandscape(getContext())) {
                            viewWidth = viewHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                        }
                    } else {
                        if (ViewCreator.playerViewFullScreenEnabled()) {
                            viewWidth = viewHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                        }
                    }
                }
            } else if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY ||
                    componentKey == AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY) {
                if (0 < viewWidth && 0 < viewHeight) {
                    if (viewWidth < viewHeight) {
                        viewHeight = (int) ((float) viewWidth * 4.0f / 3.0f);
                    } else {
                        viewHeight = (int) ((float) viewWidth * 9.0f / 16.0f);
                    }
                }
            }
        }

        if (useWidthOfScreen) {
            viewWidth = DEVICE_WIDTH;
        }

        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(viewWidth, viewHeight);
        marginLayoutParams.setMargins(lm, tm, rm, bm);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(marginLayoutParams);
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;

        if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY ||
                componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY ||
                componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY) {
            layoutParams.gravity = gravity;
        }

        if (componentType == AppCMSUIKeyType.PAGE_COLLECTIONGRID_KEY) {
            if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY) {
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                layoutParams.setMargins(0, tm, 0, bm);
            } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_CONTINUE_WATCHING_MODULE_KEY) {
                tm *= +1.6;
                layoutParams.setMargins(lm, tm, rm, bm);
            }
        }

        view.setLayoutParams(layoutParams);
    }

    protected void initializeComponentHasViewList(int size) {
        componentHasViewList = new boolean[size];
    }

    protected ViewGroup createChildrenContainer() {
        childrenContainer = new FrameLayout(getContext());
        int viewWidth = (int) getViewWidth(getContext(), getLayout(), (float) LayoutParams.MATCH_PARENT);
        int viewHeight = (int) getViewHeight(getContext(), getLayout(), (float) LayoutParams.MATCH_PARENT);
        if (isLandscape(getContext())) {
            viewHeight *= TABLET_LANDSCAPE_HEIGHT_SCALE;
        }
        FrameLayout.LayoutParams childContainerLayoutParams =
                new FrameLayout.LayoutParams(viewWidth, viewHeight);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
        addView(childrenContainer);
        return childrenContainer;
    }

    protected int getFontsize(Context context, Component component) {
        if (context != null && component != null) {
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
        }
        return 0;
    }

    protected boolean shouldShowView(Component component) {
        if (component != null) {
            if (isTablet(getContext())) {
                if (!component.isVisibleForTablet() && component.isVisibleForPhone()) {
                    return false;
                }
            } else {
                if (component.isVisibleForTablet() && !component.isVisibleForPhone()) {
                    return false;
                }
            }
        }
        return true;
    }

    protected float getGridWidth(Context context, Layout layout, int defaultWidth) {
        if (context != null && layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    float width = tabletLandscape.getGridWidth() != 0f ? tabletLandscape.getGridWidth() : -1.0f;
                    if (width != -1.0f) {
                        return DEVICE_WIDTH * (width / STANDARD_TABLET_HEIGHT_PX);
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    float width = tabletPortrait.getGridWidth() != 0f ? tabletPortrait.getGridWidth() : -1.0f;
                    if (width != -1.0f) {
                        return DEVICE_WIDTH * (width / STANDARD_TABLET_WIDTH_PX);
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                float width = mobile.getGridWidth() != 0f ? mobile.getGridWidth() : -1.0f;
                if (width != -1.0f) {
                    return DEVICE_WIDTH * (width / STANDARD_MOBILE_WIDTH_PX);
                }
            }
        }
        return defaultWidth;
    }

    protected float getGridHeight(Context context, Layout layout, int defaultHeight) {
        if (context != null && layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    float height = tabletLandscape.getGridHeight() != 0f ? tabletLandscape.getGridHeight() : -1.0f;
                    if (height != -1.0f) {
                        return DEVICE_HEIGHT * (height / STANDARD_TABLET_WIDTH_PX);
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    float height = tabletPortrait.getGridHeight() != 0f ? tabletPortrait.getGridHeight() : -1.0f;
                    if (height != -1) {
                        return DEVICE_HEIGHT * (height / STANDARD_TABLET_HEIGHT_PX);
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                float height = mobile.getGridHeight() != 0f ? mobile.getGridHeight() : -1.0f;
                if (height != -1.0f) {
                    return DEVICE_HEIGHT * (height / STANDARD_MOBILE_HEIGHT_PX);
                }
            }
        }
        return defaultHeight;
    }

    protected float getTrayPadding(Context context, Layout layout) {
        if (context != null && layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    if (layout.getTabletLandscape().getTrayPadding() != 0f) {
                        return layout.getTabletLandscape().getTrayPadding();
                    }
                } else {
                    if (layout.getTabletPortrait().getTrayPadding() != 0f) {
                        return layout.getTabletPortrait().getTrayPadding();
                    }
                }
            } else {
                if (layout.getMobile().getTrayPadding() != 0f) {
                    return layout.getMobile().getTrayPadding();
                }
            }
        }
        return -1.0f;
    }
}
