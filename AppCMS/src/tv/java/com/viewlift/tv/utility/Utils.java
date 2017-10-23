package com.viewlift.tv.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.tv.FireTV;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.activity.AppCmsBaseActivity;
import com.viewlift.tv.views.fragment.ClearDialogFragment;

import java.io.InputStream;
import java.util.Map;

import static com.viewlift.tv.views.activity.AppCmsHomeActivity.DIALOG_FRAGMENT_TAG;

/**
 * Created by nitin.tyagi on 7/3/2017.
 */

public class Utils {

    private static final int DEAFULT_PADDING = 0;
    public static final int STANDARD_TABLET_HEIGHT_PX = 1080;
    public static final int STANDARD_TABLET_WIDTH_PX = 1920;


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


    public static float getViewHeight(Context context, Layout layout, float defaultHeight) {
        if (layout != null) {
            FireTV fireTV = layout.getTv();
            float height = getViewHeight(fireTV);
            if (height != -1.0f) {
                return getViewYAxisAsPerScreen(context,(int)height);
            }
        }
        return defaultHeight;
    }


    public static float getViewWidth(Context context, Layout layout, float defaultWidth) {
        if (layout != null) {
            FireTV fireTV = layout.getTv();
            float width = getViewWidth(fireTV);
            if (width != -1.0f) {
                return getViewXAxisAsPerScreen(context,(int)width);
               // return width;
            }
        }
        return defaultWidth;
    }

    public static float getItemViewHeight(Context context, Layout layout, float defaultHeight) {
        if (layout != null) {
            FireTV fireTV = layout.getTv();
            float height = getItemViewHeight(fireTV);
            if (height != -1.0f) {
                return getViewYAxisAsPerScreen(context,(int)height);
            }
        }
        return defaultHeight;
    }


    public static float getItemViewWidth(Context context, Layout layout, float defaultWidth) {
        if (layout != null) {
            FireTV fireTV = layout.getTv();
            float width = getItemViewWidth(fireTV);
            if (width != -1.0f) {
                return getViewXAxisAsPerScreen(context,(int)width);
               // return width;
            }
        }
        return defaultWidth;
    }


    public static int getViewXAxisAsPerScreen(Context context , int dimension){
        float dim  = context.getResources().getDisplayMetrics().widthPixels
                * ((float)dimension / STANDARD_TABLET_WIDTH_PX);
        return Math.round(dim);
    }


    public static int getViewYAxisAsPerScreen(Context context , int dimension){
        float dim  = context.getResources().getDisplayMetrics().heightPixels
                * ((float)dimension / STANDARD_TABLET_HEIGHT_PX);
        return Math.round(dim);
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


    public static float getItemViewHeight(FireTV fireTV) {
        if (fireTV != null) {
            if (fireTV.getHeight() != null) {
                return Float.valueOf(fireTV.getItemHeight());
            }
        }
        return -1.0f;
    }


    public static float getItemViewWidth(FireTV fireTV) {
        if (fireTV != null) {
            if (fireTV.getWidth() != null) {
                return Float.valueOf(fireTV.getItemHeight());
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

    public static StateListDrawable getNavigationSelector(Context context , AppCMSPresenter appCMSPresenter , boolean isSubNavigation){
        StateListDrawable res = new StateListDrawable();
        res.addState(new int[]{android.R.attr.state_focused}, getNavigationSelectedState(context ,appCMSPresenter , isSubNavigation));
        res.addState(new int[]{android.R.attr.state_pressed}, getNavigationSelectedState(context , appCMSPresenter , isSubNavigation));
        res.addState(new int[]{android.R.attr.state_selected},getNavigationSelectedState(context , appCMSPresenter , isSubNavigation));
        res.addState(new int[]{}, new ColorDrawable(ContextCompat.getColor(context,android.R.color.transparent)));
        return res;
    }

    public static Drawable getProgressDrawable(Context context , String unProgressColor , AppCMSPresenter appCMSPresenter) {
        ShapeDrawable shape = new ShapeDrawable();
        shape.getPaint().setStyle(Paint.Style.FILL);
        shape.getPaint().setColor(Color.parseColor(getColor(context,unProgressColor)));
        ShapeDrawable shapeD = new ShapeDrawable();
        shapeD.getPaint().setStyle(Paint.Style.FILL);
        shapeD.getPaint().setColor(
                Color.parseColor(getFocusColor(context,appCMSPresenter)));
        ClipDrawable clipDrawable = new ClipDrawable(shapeD, Gravity.LEFT,
                ClipDrawable.HORIZONTAL);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                 shape , clipDrawable});
        return layerDrawable;
    }


    public static LayerDrawable getNavigationSelectedState(Context context , AppCMSPresenter appCMSPresenter , boolean isSubNavigation){
        GradientDrawable focusedLayer = new GradientDrawable();
        focusedLayer.setShape(GradientDrawable.RECTANGLE);
        focusedLayer.setColor(Color.parseColor(getFocusColor(context,appCMSPresenter)));

        GradientDrawable transparentLayer = new GradientDrawable();
        transparentLayer.setShape(GradientDrawable.RECTANGLE);
        if(isSubNavigation){
            transparentLayer.setColor(ContextCompat.getColor(context , R.color.appcms_sub_nav_background));
        }else{
            transparentLayer.setColor(ContextCompat.getColor(context , R.color.appcms_nav_background)/*Color.parseColor(getFocusColor(appCMSPresenter))*/);
        }

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                focusedLayer,
                transparentLayer
        });

        if(isSubNavigation){
            layerDrawable.setLayerInset(1,0,0,0,5);
        }else{
            layerDrawable.setLayerInset(1,0,5,0,0);
        }
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
        boolean isEditText = false;
        if(null != component){
            isEditText = component.getType().equalsIgnoreCase(context.getString(R.string.app_cms_page_textfield_key));
        }

        StateListDrawable res = new StateListDrawable();
        res.addState(new int[]{android.R.attr.state_focused}, getBorder(context,selectedColor,isEditText , component,false));
        res.addState(new int[]{android.R.attr.state_pressed}, getBorder(context,selectedColor,isEditText , component,false));
        res.addState(new int[]{android.R.attr.state_selected}, getBorder(context,selectedColor,isEditText, component , false));
        if(isEditText)
        res.addState(new int[]{} ,getBorder(context,selectedColor,isEditText, component , true) );
        else
        res.addState(new int[]{}, new ColorDrawable(ContextCompat.getColor(
                context,
                android.R.color.transparent
        )));
        return res;
    }

    private static GradientDrawable getBorder(Context context , String borderColor , boolean isEditText , Component component , boolean isNormalState){
        GradientDrawable ageBorder = new GradientDrawable();
        ageBorder.setShape(GradientDrawable.RECTANGLE);

        if(isEditText)
        ageBorder.setCornerRadius(component.getCornerRadius());

        if(!isNormalState)
        ageBorder.setStroke(6,Color.parseColor(borderColor));

        ageBorder.setColor(ContextCompat.getColor(
                context,
                isEditText ? android.R.color.white : android.R.color.transparent
        ));
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
                    //Log.d("" , "setTypeFace===Opensans_Bold" + " text = "+ ( ( component != null && component.getKey() != null ) ? component.getKey().toString() : null ) );
                    break;
                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_semibold_ttf));
                    //Log.d("" , "setTypeFace===Opensans_SemiBold" + " text = "+ ( ( component != null && component.getKey() != null ) ? component.getKey().toString() : null ) );
                    break;
                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_extrabold_ttf));
                    //Log.d("" , "setTypeFace===Opensans_ExtraBold" + " text = "+ ( ( component != null && component.getKey() != null ) ? component.getKey().toString() : null ) );
                    break;
                default:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_regular_ttf));
                    //Log.d("" , "setTypeFace===Opensans_RegularBold" + " text = "+ ( ( component != null && component.getKey() != null ) ? component.getKey().toString() : null ) );
            }
        }
        return face;
    }


    public static String getTextColor(Context context , AppCMSPresenter appCMSPresenter){
        String color  = getColor(context,Integer.toHexString(ContextCompat.getColor(context , android.R.color.white)));
        //Log.d("Utils.java" , "getTextColor = "+color);
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
        //Log.d("Utils.java" , "getTitleColor = "+color);
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
        //Log.d("Utils.java" , "getBackGroundColor = "+color);
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
        //Log.d("Utils.java" , "getFocusColor = "+color);
        if(null != appCMSPresenter && null != appCMSPresenter.getAppCMSMain()
                && null != appCMSPresenter.getAppCMSMain().getBrand()
                && null != appCMSPresenter.getAppCMSMain().getBrand().getCta()
                && null != appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary()){
            color =  appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor();
        }
        return color;
    }

    public static double getPercentage(long runtime , long watchedTime){
        double percentage = 0;
        percentage = ((double)watchedTime / (double) runtime ) * 100;
        return percentage;
    }


    @NonNull
    public static ClearDialogFragment getClearDialogFragment(Context context,
                                                       AppCMSPresenter appCMSPresenter,
                                                       int dialogWidth,
                                                       int dialogHeight,
                                                       String dialogTitle,
                                                       String dialogMessage,
                                                       String positiveButtonText,
                                                       String negativeButtonText,
                                                       float messageSize) {
        Bundle bundle = new Bundle();
        bundle.putInt(ClearDialogFragment.DIALOG_WIDTH_KEY, dialogWidth);
        bundle.putInt(ClearDialogFragment.DIALOG_HEIGHT_KEY, dialogHeight);
        bundle.putFloat(ClearDialogFragment.DIALOG_MESSAGE__SIZE_KEY, messageSize);
        bundle.putString(ClearDialogFragment.DIALOG_MESSAGE_TEXT_COLOR_KEY,
                Utils.getTextColor(context, appCMSPresenter));
        bundle.putString(ClearDialogFragment.DIALOG_TITLE_KEY, dialogTitle);
        bundle.putString(ClearDialogFragment.DIALOG_MESSAGE_KEY, dialogMessage);
        bundle.putString(ClearDialogFragment.DIALOG_POSITIVE_BUTTON_TEXT_KEY,
                positiveButtonText);
        bundle.putString(ClearDialogFragment.DIALOG_NEGATIVE_BUTTON_TEXT_KEY,
                negativeButtonText);
        Intent args = new Intent(AppCMSPresenter.PRESENTER_DIALOG_ACTION);
        args.putExtra(context.getString(R.string.dialog_item_key), bundle);
        android.app.FragmentTransaction ft = appCMSPresenter
                .getCurrentActivity().getFragmentManager()
                .beginTransaction();
        ClearDialogFragment newFragment =
                ClearDialogFragment.newInstance(bundle);
        newFragment.show(ft, DIALOG_FRAGMENT_TAG);
        return newFragment;
    }


    public static void pageLoading(final boolean shouldShowProgress , Activity activity){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(shouldShowProgress){
                    CustomProgressBar.getInstance(activity).showProgressDialog(activity,"Loading...");
                }else{
                    CustomProgressBar.getInstance(activity).dismissProgressDialog();
                }
            }
        });
    }


}
