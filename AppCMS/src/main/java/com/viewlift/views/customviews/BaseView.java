package com.viewlift.views.customviews;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Mobile;
import com.viewlift.models.data.appcms.ui.page.TabletLandscape;
import com.viewlift.models.data.appcms.ui.page.TabletPortrait;

/**
 * Created by viewlift on 5/17/17.
 */

public abstract class BaseView extends FrameLayout {
    private static final String TAG = "BaseView";

    protected ViewGroup childrenContainer;
    protected boolean[] componentHasViewList;

    public BaseView(Context context) {
        super(context);
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

    protected void initializeComponentHasViewList(int size) {
        componentHasViewList = new boolean[size];
    }

    public void setComponentHasView(int index, boolean hasView) {
        if (componentHasViewList != null) {
            componentHasViewList[index] = hasView;
        }
    }

    protected ViewGroup createChildrenContainer() {
        childrenContainer = new FrameLayout(getContext());
        int viewWidth = getViewWidth(getContext(), getLayout(), LayoutParams.MATCH_PARENT);
        int viewHeight = getViewHeight(getContext(), getLayout(), LayoutParams.MATCH_PARENT);
        FrameLayout.LayoutParams childContainerLayoutParams =
                new FrameLayout.LayoutParams(viewWidth, viewHeight);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
        this.addView(childrenContainer);
        return childrenContainer;
    }

    public void setViewMarginsFromComponent(Layout layout,
                                            View view,
                                            Layout parentLayout,
                                            View parentView,
                                            boolean firstMeasurement) {
        int lm = 0, tm = 0, rm = 0, bm = 0;
        int deviceWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        int deviceHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        int viewWidth = LayoutParams.MATCH_PARENT;
        int viewHeight = LayoutParams.WRAP_CONTENT;
        int parentViewWidth = getViewWidth(getContext(),
                parentLayout,
                parentView.getMeasuredWidth());
        int parentViewHeight = getViewHeight(getContext(),
                parentLayout,
                parentView.getMeasuredHeight());
        int measuredWidth = firstMeasurement ? deviceWidth : parentViewWidth;
        int measuredHeight = firstMeasurement ? deviceHeight : parentViewHeight;

        if (isTablet(getContext())) {
            if (isLandscape(getContext())) {
                TabletLandscape tabletLandscape = layout.getTabletLandscape();
                if (tabletLandscape != null) {
                    if (getViewWidth(tabletLandscape) != -1) {
                        viewWidth = getViewWidth(tabletLandscape);
                        if (tabletLandscape.getXAxis() != null) {
                            lm = (int) convertDpToPixel(tabletLandscape.getXAxis(), getContext());
                        }
                    }
                    if (getViewHeight(tabletLandscape) != -1) {
                        viewHeight = getViewHeight(tabletLandscape);
                        if (tabletLandscape.getYAxis() != null) {
                            tm = tabletLandscape.getXAxis();
                        }
                    }
                    if (tabletLandscape.getLeftMargin() != null) {
                        lm = ((int) ((float) tabletLandscape.getLeftMargin() / 100.0f)) * measuredWidth;
                    }
                    if (tabletLandscape.getRightMargin() != null) {
                        rm = ((int) (tabletLandscape.getRightMargin() / 100.0f)) * measuredWidth;
                    }
                    if (tabletLandscape.getTopMargin() != null) {
                        tm = (int) ((tabletLandscape.getTopMargin() / 100.0f) * measuredHeight);
                    } else if (tabletLandscape.getBottomMargin() != null) {
                        int marginDiff = viewHeight;
                        if (marginDiff < 0) {
                            marginDiff = 0;
                        }
                        tm = (int) (((100.0f - tabletLandscape.getBottomMargin()) / 100.0f) * measuredHeight) -
                                (int) convertDpToPixel(marginDiff, getContext());
                    }
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                if (tabletPortrait != null) {
                    if (getViewWidth(tabletPortrait) != -1) {
                        viewWidth = getViewWidth(tabletPortrait);
                        if (tabletPortrait.getXAxis() != null) {
                            lm = (int) convertDpToPixel(tabletPortrait.getXAxis(), getContext());
                        }
                    }
                    if (getViewHeight(tabletPortrait) != -1) {
                        viewHeight = getViewHeight(tabletPortrait);
                        if (tabletPortrait.getYAxis() != null) {
                            tm = tabletPortrait.getXAxis();
                        }
                    }
                    if (tabletPortrait.getLeftMargin() != null) {
                        lm = ((int) ((float) tabletPortrait.getLeftMargin() / 100.0f)) * measuredWidth;
                    }
                    if (tabletPortrait.getRightMargin() != null) {
                        rm = ((int) ((float) tabletPortrait.getRightMargin() / 100.0f)) * measuredWidth;
                    }
                    if (tabletPortrait.getTopMargin() != null) {
                        tm = (int) ((tabletPortrait.getTopMargin() / 100.0f) * measuredHeight);
                    } else if (tabletPortrait.getBottomMargin() != null) {
                        int marginDiff = viewHeight;
                        if (marginDiff < 0) {
                            marginDiff = 0;
                        }
                        tm = (int) (((100.0f - tabletPortrait.getBottomMargin()) / 100.0f) * measuredHeight) -
                                (int) convertDpToPixel(marginDiff, getContext());
                    }
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            if (mobile != null) {
                if (getViewWidth(mobile) != -1) {
                    viewWidth = (int) convertDpToPixel(getViewWidth(mobile), getContext());
                    if (mobile.getXAxis() != null) {
                        Log.d(TAG, "x-axis: " + mobile.getXAxis());
                        lm = (int) convertDpToPixel(mobile.getXAxis(), getContext());
                    }
                }
                if (getViewHeight(mobile) != -1) {
                    viewHeight = (int) convertDpToPixel(getViewHeight(mobile), getContext());
                    if (mobile.getYAxis() != null) {
                        Log.d(TAG, "y-axis: " + mobile.getYAxis());
                        tm = (int) convertDpToPixel(mobile.getYAxis(), getContext());
                    }
                }
                if (mobile.getLeftMargin() != null) {
                    lm = ((int) ((float) mobile.getLeftMargin() / 100.0f)) * measuredWidth;
                }
                if (mobile.getRightMargin() != null) {
                    rm = ((int) ((float) mobile.getRightMargin() / 100.0f)) * measuredWidth;
                }
                if (mobile.getTopMargin() != null) {
                    tm = (int) ((mobile.getTopMargin() / 100.0f) * measuredHeight);
                } else if (mobile.getBottomMargin() != null) {
                    int marginDiff = viewHeight;
                    if (marginDiff < 0) {
                        marginDiff = 0;
                    }
                    tm = (int) (((100.0f - mobile.getBottomMargin()) / 100.0f) * measuredHeight) -
                            (int) convertDpToPixel(marginDiff, getContext());
                }
            }
        }
        Log.d(TAG, "firstMeasurement: " + firstMeasurement);
        Log.d(TAG, "deviceWidth: " + deviceWidth + " deviceHeight: " + deviceHeight);
        Log.d(TAG, "viewWidth: " + viewWidth + " viewHeight: " + viewHeight);
        Log.d(TAG, "parentViewWidth: " + parentViewWidth + " parentViewHeight: " + parentViewHeight);
        Log.d(TAG, "lm: " + lm + " tm: " + tm + " rm: " + rm + " bm: " + bm);
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(viewWidth, viewHeight);
        marginLayoutParams.topMargin = tm;
        marginLayoutParams.leftMargin = lm;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(marginLayoutParams);
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
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

    protected int getViewWidth(Context context, Layout layout, int defaultWidth) {
        if (layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    int width = getViewWidth(tabletLandscape);
                    if (width != -1) {
                        return (int) convertDpToPixel(width, context);
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    int width = getViewHeight(tabletPortrait);
                    if (width != -1) {
                        return (int) convertDpToPixel(width, context);
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                int width = getViewWidth(mobile);
                if (width != -1) {
                    return (int) convertDpToPixel(width, context);
                }
            }
        }
        return defaultWidth;
    }

    protected int getViewHeight(Context context, Layout layout, int defaultHeight) {
        if (layout != null) {
            if (isTablet(context)) {
                if (isLandscape(context)) {
                    TabletLandscape tabletLandscape = layout.getTabletLandscape();
                    int height = getViewHeight(tabletLandscape);
                    if (height != -1) {
                        return (int) convertDpToPixel(height, context);
                    }
                } else {
                    TabletPortrait tabletPortrait = layout.getTabletPortrait();
                    int height = getViewHeight(tabletPortrait);
                    if (height != -1) {
                        return (int) convertDpToPixel(height, context);
                    }
                }
            } else {
                Mobile mobile = layout.getMobile();
                int height = getViewHeight(mobile);
                if (height != -1) {
                    return (int) convertDpToPixel(height, context);
                }
            }
        }
        return defaultHeight;
    }

    protected int getViewWidth(TabletLandscape tabletLandscape) {
        if (tabletLandscape != null) {
            if (tabletLandscape.getWidth() != null) {
                return tabletLandscape.getWidth();
            }
        }
        return -1;
    }

    protected int getViewHeight(TabletLandscape tabletLandscape) {
        if (tabletLandscape != null) {
            if (tabletLandscape.getHeight() != null) {
                return tabletLandscape.getHeight();
            }
        }
        return -1;
    }

    protected int getViewWidth(TabletPortrait tabletPortrait) {
        if (tabletPortrait != null) {
            if (tabletPortrait.getWidth() != null) {
                return tabletPortrait.getWidth();
            }
        }
        return -1;
    }

    protected int getViewHeight(TabletPortrait tabletPortrait) {
        if (tabletPortrait != null) {
            if (tabletPortrait.getHeight() != null) {
                return tabletPortrait.getHeight();
            }
        }
        return -1;
    }

    protected int getViewWidth(Mobile mobile) {
        if (mobile != null) {
            if (mobile.getWidth() != null) {
                return mobile.getWidth();
            }
        }
        return -1;
    }

    protected int getViewHeight(Mobile mobile) {
        if (mobile != null) {
            if (mobile.getHeight() != null) {
                return mobile.getHeight();
            }
        }
        return -1;
    }
}
