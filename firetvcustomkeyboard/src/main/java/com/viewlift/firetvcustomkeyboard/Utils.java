package com.viewlift.firetvcustomkeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;

class Utils {

    /**
     * This method converts dp unit to equivalent pixels, depending on device
     * density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need
     *                to convert into pixels
     * @param context Context to get resources and device specific display
     *                metrics
     * @return A float value to represent px equivalent to dp depending on
     * device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent
     * pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display
     *                metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /**
     * this method is use for setting the button background selector.
     * @param context
     * @param selectedColor
     * @return
     */
    public static StateListDrawable getButtonBackgroundSelector(Context context , int selectedColor){
        StateListDrawable res = new StateListDrawable();
        res.addState(new int[]{android.R.attr.state_focused}, getButtonFocusedState(context , selectedColor));
        res.addState(new int[]{android.R.attr.state_selected}, getButtonFocusedState(context , selectedColor));
        res.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(ContextCompat.getColor(context,android.R.color.transparent)));
        res.addState(new int[]{}, new ColorDrawable(ContextCompat.getColor(context,android.R.color.transparent)) );
        return res;
    }

    private static GradientDrawable getButtonFocusedState(Context context , int selectedColor){
        GradientDrawable ageBorder = new GradientDrawable();
        ageBorder.setShape(GradientDrawable.RECTANGLE);
        ageBorder.setColor(selectedColor);
        return ageBorder;
    }

}
