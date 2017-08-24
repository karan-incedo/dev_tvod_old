package com.viewlift.tv.utility;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.nfc.Tag;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.tv.FireTV;
import com.viewlift.presenters.AppCMSPresenter;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by nitin.tyagi on 7/3/2017.
 */

public class Utils {

    private static final int DEAFULT_PADDING = 0;

    public static void setBrowseFragmentViewParameters(View browseFragmentView, int marginLeft,
                                                       int marginTop) {
        //View browseContainerDoc = browseFragmentView.findViewById(R.id.browse_container_dock);
        View browseContainerDoc = browseFragmentView.findViewById(R.id.browse_frame);

        if (null != browseContainerDoc) {
            browseContainerDoc.setBackgroundColor(Color.TRANSPARENT);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) browseContainerDoc
                    .getLayoutParams();
            params.leftMargin = marginLeft;// -80;
            params.topMargin = marginTop;// -225;
            params.bottomMargin = 0;
            browseContainerDoc.setLayoutParams(params);

        }

        View browseHeaders = browseFragmentView.findViewById(R.id.browse_headers);
        if (null != browseHeaders) {
            browseHeaders.setBackgroundColor(Color.TRANSPARENT);
        }

        View containerList = browseFragmentView.findViewById(R.id.container_list);
        if (null != containerList) {
            containerList.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    public static String loadJsonFromAssets(Context context , String fileName){
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static int convertPixelsToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = px / 2/*(metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)*/;
        return dp;
    }




    public static float getViewHeight(Context context, Layout layout, float defaultHeight) {
        if (layout != null) {
            FireTV fireTV = layout.getTv();
            float height = getViewHeight(fireTV);
            if (height != -1.0f) {
                return height;
            }
        }
        return defaultHeight;
    }


    public static float getViewWidth(Context context, Layout layout, float defaultWidth) {
        if (layout != null) {
            FireTV fireTV = layout.getTv();
            float width = getViewWidth(fireTV);
            if (width != -1.0f) {
                return width;
            }
        }
        return defaultWidth;
    }

    public static int getLeftPadding(Context context, Layout layout) {
        if (layout != null) {
            FireTV fireTV = layout.getTv();
            if(null != fireTV && null != fireTV.getLeftMargin()){
                return Integer.valueOf(layout.getTv().getLeftMargin());
            }else{
                return DEAFULT_PADDING;
            }
        }
        return DEAFULT_PADDING;
    }

    public static int getRightPadding(Context context, Layout layout) {
        if (layout != null) {
            FireTV fireTV = layout.getTv();
            if(null != fireTV && null != fireTV.getRightMargin()){
                return Integer.valueOf(layout.getTv().getRightMargin());
            }else{
                return DEAFULT_PADDING;
            }
        }
        return DEAFULT_PADDING;
    }

    public static int getTopPadding(Context context, Layout layout) {
        if (layout != null) {
            FireTV fireTV = layout.getTv();
            if(null != fireTV && null != fireTV.getTopMargin()){
                return Integer.valueOf(layout.getTv().getTopMargin());
            }else{
                return DEAFULT_PADDING;
            }
        }
        return DEAFULT_PADDING;
    }

    public static int getBottomPadding(Context context, Layout layout) {
        if (layout != null) {
            FireTV fireTV = layout.getTv();
            if(null != fireTV && null != fireTV.getBottomMargin()){
                return Integer.valueOf(layout.getTv().getBottomMargin());
            }else{
                return DEAFULT_PADDING;
            }
        }
        return DEAFULT_PADDING;
    }


    public static float getViewHeight(FireTV fireTV) {
        if (fireTV != null) {
            if (fireTV.getHeight() != null) {
                return Float.valueOf(fireTV.getHeight());
            }
        }
        return -1.0f;
    }


    public static float getViewWidth(FireTV fireTV) {
        if (fireTV != null) {
            if (fireTV.getWidth() != null) {
                return Float.valueOf(fireTV.getWidth());
            }
        }
        return -1.0f;
    }



    public static float getFontSizeKey(Context context, Layout layout) {
       {
            if (layout.getTv().getFontSizeKey() != null) {
                return layout.getTv().getFontSizeKey();
            }
        }
        return -1.0f;
    }


    public static float getFontSizeValue(Context context, Layout layout) {
            if (layout.getTv().getFontSizeValue() != null) {
                layout.getTv().getFontSizeValue();
            }
        return -1.0f;
    }

    public static StateListDrawable getNavigationSelector(Context context , AppCMSPresenter appCMSPresenter){
        StateListDrawable res = new StateListDrawable();
        res.addState(new int[]{android.R.attr.state_focused}, getNavigationSelectedState(context ,appCMSPresenter));
        res.addState(new int[]{android.R.attr.state_pressed}, getNavigationSelectedState(context , appCMSPresenter));
        res.addState(new int[]{android.R.attr.state_selected},getNavigationSelectedState(context , appCMSPresenter));
        res.addState(new int[]{}, new ColorDrawable(ContextCompat.getColor(context,android.R.color.transparent)));
        return res;
    }

    private static LayerDrawable getNavigationSelectedState(Context context , AppCMSPresenter appCMSPresenter){
        GradientDrawable focusedLayer = new GradientDrawable();
        focusedLayer.setShape(GradientDrawable.RECTANGLE);
        focusedLayer.setColor(Color.parseColor(getFocusColor(context,appCMSPresenter)));

        GradientDrawable transparentLayer = new GradientDrawable();
        transparentLayer.setShape(GradientDrawable.RECTANGLE);
        transparentLayer.setColor(ContextCompat.getColor(context , R.color.appcms_nav_background)/*Color.parseColor(getFocusColor(appCMSPresenter))*/);

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                focusedLayer,
                transparentLayer
        });

        layerDrawable.setLayerInset(1,0,5,0,0);
        return layerDrawable;
    }

    /**
     * this method is use for setting the tray border.
     * @param context
     * @param selectedColor
     * @param component
     * @return
     */
    public static StateListDrawable getTrayBorder(Context context , String selectedColor , Component component){
        StateListDrawable res = new StateListDrawable();
        res.addState(new int[]{android.R.attr.state_focused}, getBorder(context,selectedColor));
        res.addState(new int[]{android.R.attr.state_pressed}, getBorder(context,selectedColor));
        res.addState(new int[]{android.R.attr.state_selected}, getBorder(context,selectedColor));
        res.addState(new int[]{}, new ColorDrawable(ContextCompat.getColor(context,android.R.color.transparent)));
        return res;
    }

    private static GradientDrawable getBorder(Context context , String borderColor){
        GradientDrawable ageBorder = new GradientDrawable();
        ageBorder.setShape(GradientDrawable.RECTANGLE);
        ageBorder.setStroke(6,Color.parseColor(borderColor));
        ageBorder.setColor(ContextCompat.getColor(context, android.R.color.transparent));
        return ageBorder;
    }

    /**
     * this method is use for setting the button background selector.
     * @param context
     * @param selectedColor
     * @param component
     * @return
     */
    public static StateListDrawable setButtonBackgroundSelector(Context context , int selectedColor , Component component){
        StateListDrawable res = new StateListDrawable();

        res.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(selectedColor));
        res.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(selectedColor));
        res.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(selectedColor));

        GradientDrawable gradientDrawable = getButtonNormalState(context , component);
        if(null != gradientDrawable )
            res.addState(new int[]{}, gradientDrawable );
        return res;
    }

    private static GradientDrawable getButtonNormalState(Context context , Component component){
        GradientDrawable ageBorder = null;
        if (component.getBorderWidth() != 0 && component.getBorderColor() != null) {
            if (component.getBorderWidth() > 0 && !TextUtils.isEmpty(component.getBorderColor())) {
                ageBorder = new GradientDrawable();
                ageBorder.setShape(GradientDrawable.RECTANGLE);
                ageBorder.setStroke(component.getBorderWidth(),
                        Color.parseColor(getColor(context, component.getBorderColor())));
                ageBorder.setColor(ContextCompat.getColor(context, android.R.color.transparent));
            }
        }
        return ageBorder;
    }


    public static ColorStateList getButtonTextColorDrawable(String defaultColor , String focusedColor){
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_focused},
                new int[] {android.R.attr.state_selected},
                new int[] {android.R.attr.state_pressed},
                new int[] {}
        };
        int[] colors = new int[] {
                Color.parseColor(focusedColor),
                Color.parseColor(focusedColor),
                Color.parseColor(focusedColor),
                Color.parseColor(defaultColor)
        };
        ColorStateList myList = new ColorStateList(states, colors);
        return myList;
    }

    /**
     * this method is use for setting the textCoo
     * @param context
     * @param appCMSPresenter
     * @return
     */
    public static ColorStateList getTextColorDrawable(Context context , AppCMSPresenter appCMSPresenter){
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_focused},
                new int[] {android.R.attr.state_selected},
                new int[] {android.R.attr.state_pressed},
                new int[] {}
        };
        int[] colors = new int[] {
                Color.parseColor(getFocusColor(context,appCMSPresenter)),
                Color.parseColor(getFocusColor(context,appCMSPresenter)),
                Color.parseColor(getFocusColor(context,appCMSPresenter)),
                Color.parseColor(getTextColor(context,appCMSPresenter))
        };
        ColorStateList myList = new ColorStateList(states, colors);
        return myList;
    }

    public static String getColor(Context context, String color) {
        if (color.indexOf(context.getString(R.string.color_hash_prefix)) != 0) {
            return context.getString(R.string.color_hash_prefix) + color;
        }
        return color;
    }




    public static Typeface getTypeFace(Context context,
                            Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                            Component component) {
        Typeface face = null;
        if (jsonValueKeyMap.get(component.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY) {
            AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(component.getFontWeight());
            if (fontWeight == null) {
                fontWeight = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            switch (fontWeight) {
                case PAGE_TEXT_BOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_bold_ttf));
                    Log.d("" , "setTypeFace===Opensans_Bold" + " text = "+ ( ( component != null && component.getKey() != null ) ? component.getKey().toString() : null ) );
                    break;
                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_semibold_ttf));
                    Log.d("" , "setTypeFace===Opensans_SemiBold" + " text = "+ ( ( component != null && component.getKey() != null ) ? component.getKey().toString() : null ) );
                    break;
                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_extrabold_ttf));
                    Log.d("" , "setTypeFace===Opensans_ExtraBold" + " text = "+ ( ( component != null && component.getKey() != null ) ? component.getKey().toString() : null ) );
                    break;
                default:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_regular_ttf));
                    Log.d("" , "setTypeFace===Opensans_RegularBold" + " text = "+ ( ( component != null && component.getKey() != null ) ? component.getKey().toString() : null ) );
            }
        }
        return face;
    }


    public static String getTextColor(Context context , AppCMSPresenter appCMSPresenter){
        String color  = getColor(context,Integer.toHexString(ContextCompat.getColor(context , android.R.color.white)));
        Log.d("Utils.java" , "getTextColor = "+color);
        if(null != appCMSPresenter && null != appCMSPresenter.getAppCMSMain()
            && null != appCMSPresenter.getAppCMSMain().getBrand()
                && null != appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
       && null != appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()){
            color = appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor();
        }
        return color;
    }


    public static String getTitleColor(Context context , AppCMSPresenter appCMSPresenter){
        String color  = getColor(context,Integer.toHexString(ContextCompat.getColor(context , android.R.color.white)));
        Log.d("Utils.java" , "getTitleColor = "+color);
        if(null != appCMSPresenter && null != appCMSPresenter.getAppCMSMain()
                && null != appCMSPresenter.getAppCMSMain().getBrand()
                && null != appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                && null != appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()){
            color = appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getPageTitleColor();
        }
        return color;
    }

    public static String getBackGroundColor(Context context  ,AppCMSPresenter appCMSPresenter){
        String color  = getColor(context,Integer.toHexString(ContextCompat.getColor(context , R.color.dialog_bg_color)));
        Log.d("Utils.java" , "getBackGroundColor = "+color);
        if(null != appCMSPresenter && null != appCMSPresenter.getAppCMSMain()
                && null != appCMSPresenter.getAppCMSMain().getBrand()
                && null != appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                && null != appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()){
            color =  appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor();
        }
        return color;
    }

    public static String getFocusColor(Context context  , AppCMSPresenter appCMSPresenter){
        String color  = getColor(context,Integer.toHexString(ContextCompat.getColor(context , R.color.colorAccent)));
        Log.d("Utils.java" , "getFocusColor = "+color);
        if(null != appCMSPresenter && null != appCMSPresenter.getAppCMSMain()
                && null != appCMSPresenter.getAppCMSMain().getBrand()
                && null != appCMSPresenter.getAppCMSMain().getBrand().getCta()
                && null != appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary()){
            color =  appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor();
        }
        return color;
    }

}
