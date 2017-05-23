package air.com.snagfilms.views.customviews;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import air.com.snagfilms.models.data.appcms.page.Layout;
import air.com.snagfilms.models.data.appcms.page.Mobile;
import air.com.snagfilms.models.data.appcms.page.TabletLandscape;
import air.com.snagfilms.models.data.appcms.page.TabletPortrait;

/**
 * Created by viewlift on 5/17/17.
 */

public abstract class BaseView extends FrameLayout {
    private static final String TAG = "BaseView";

    protected ViewGroup childrenContainer;

    public BaseView(Context context) {
        super(context);
    }

    public ViewGroup getChildrenContainer(Context context, int orientation) {
        if (childrenContainer == null) {
            return createChildrenContainer(context, orientation);
        }
        return childrenContainer;
    }

    protected ViewGroup createChildrenContainer(Context context, int orientation) {
        childrenContainer = new FrameLayout(context);
        FrameLayout.LayoutParams childContainerLayoutParams =
                new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
        NestedScrollView nestedScrollView = new NestedScrollView(context);
        NestedScrollView.LayoutParams nestedScrollViewLayoutParams =
                new NestedScrollView.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
        nestedScrollView.setLayoutParams(nestedScrollViewLayoutParams);
        nestedScrollView.addView(childrenContainer);
        this.addView(nestedScrollView);
        return childrenContainer;
    }

    protected abstract void init();

    protected void setViewMarginsFromComponent(Context context,
                                               Layout layout,
                                               View view,
                                               View parentView,
                                               int measurementCount) {
        int lm = 0, tm = 0, rm = 0, bm = 0;
        int deviceWidth = context.getResources().getDisplayMetrics().widthPixels;
        int deviceHeight = context.getResources().getDisplayMetrics().heightPixels;
        int viewWidth = LayoutParams.MATCH_PARENT;
        int viewHeight = LayoutParams.WRAP_CONTENT;
        int parentViewWidth = parentView.getMeasuredWidth();
        int parentViewHeight = parentView.getMeasuredHeight();
        int measuredWidth = measurementCount == 0 ? deviceWidth : parentViewWidth;
        int measuredHeight = measurementCount == 0 ? deviceHeight : parentViewHeight;
        if (isTablet(context)) {
            if (isLandscape(context)) {
                TabletLandscape tabletLandscape = layout.getTabletLandscape();
                if (tabletLandscape != null) {
                    if (tabletLandscape.getWidth() != null) {
                        viewWidth = tabletLandscape.getWidth();
                    }
                    if (tabletLandscape.getHeight() != null) {
                        viewHeight = tabletLandscape.getHeight();
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
                                (int) convertDpToPixel(marginDiff, context);
                    }
                }
            } else {
                TabletPortrait tabletPortrait = layout.getTabletPortrait();
                if (tabletPortrait != null) {
                    if (tabletPortrait.getWidth() != null) {
                        viewWidth = tabletPortrait.getWidth();
                    }
                    if (tabletPortrait.getHeight() != null) {
                        viewHeight = tabletPortrait.getHeight();
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
                                (int) convertDpToPixel(marginDiff, context);
                    }
                }
            }
        } else {
            Mobile mobile = layout.getMobile();
            if (mobile != null) {
                if (mobile.getWidth() != null) {
                    viewWidth = (int) convertDpToPixel(mobile.getWidth(), context);
                }
                if (mobile.getHeight() != null) {
                    viewHeight = (int) convertDpToPixel(mobile.getHeight(), context);
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
                            (int) convertDpToPixel(marginDiff, context);
                }
            }
        }
        Log.d(TAG, "firstMeasurement: " + measurementCount);
        Log.d(TAG, "deviceWidth: " + deviceWidth + " deviceHeight: " + deviceHeight);
        Log.d(TAG, "viewWidth: " + viewWidth + " viewHeight: " + viewHeight);
        Log.d(TAG, "parentViewWidth: " + parentViewWidth + " parentViewHeight: " + parentViewHeight);
        Log.d(TAG, "lm: " + lm + " tm: " + tm + " rm: " + rm + " bm: " + bm);
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(viewWidth, viewHeight);
        marginLayoutParams.topMargin = tm;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(marginLayoutParams);
        view.setLayoutParams(layoutParams);
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
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
}
