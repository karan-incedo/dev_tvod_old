package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.tv.FireTV;
import com.viewlift.tv.utility.Utils;

import java.util.Map;



/**
 * Created by nitin.tyagi on 7/12/2017.
 */

public abstract class TVBaseView extends FrameLayout {


    protected static int DEVICE_WIDTH;
    protected static int DEVICE_HEIGHT;
    public static final int STANDARD_MOBILE_WIDTH_PX = 1920;
    public static final int STANDARD_MOBILE_HEIGHT_PX = 1080;
    private static float LETTER_SPACING = 0.17f;

    public TVBaseView(@NonNull Context context) {
        super(context);
        DEVICE_WIDTH = getContext().getResources().getDisplayMetrics().widthPixels;
        DEVICE_HEIGHT = getContext().getResources().getDisplayMetrics().heightPixels;
    }



    public abstract void init();

    protected abstract Component getChildComponent(int index);

    protected abstract Layout getLayout();


    public static void setViewWithSubtitle(Context context, ContentDatum data, View view) {
        long runtime = (data.getGist().getRuntime() / 60);
        String year = data.getGist().getYear();
        String primaryCategory =
                data.getGist().getPrimaryCategory() != null ?
                        data.getGist().getPrimaryCategory().getTitle() :
                        null;
        boolean appendFirstSep = runtime > 0 &&
                (!TextUtils.isEmpty(year) || !TextUtils.isEmpty(primaryCategory));
        boolean appendSecondSep = (runtime > 0 || !TextUtils.isEmpty(year)) &&
                !TextUtils.isEmpty(primaryCategory);
        StringBuffer infoText = new StringBuffer();
        if (runtime > 0) {
            infoText.append(runtime + " " + context.getResources().getQuantityString(R.plurals.mins_abbreviation , (int)runtime));
        }
        if (appendFirstSep) {
            infoText.append(context.getString(R.string.text_separator));
        }
        if (!TextUtils.isEmpty(year)) {
            infoText.append(year);
        }
        if (appendSecondSep) {
            infoText.append(context.getString(R.string.text_separator));
        }
        if (!TextUtils.isEmpty(primaryCategory)) {
            infoText.append(primaryCategory.toUpperCase());
        }
        ((TextView) view).setText(infoText.toString());
         view.setAlpha(0.6f);
        ((TextView) view).setLetterSpacing(LETTER_SPACING);

    }


    public void setViewMarginsFromComponent(Component childComponent,
                                            View view,
                                            Layout parentLayout,
                                            View parentView,
                                            Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                            boolean useMarginsAsPercentages,
                                            boolean useWidthOfScreen) {
        Layout layout = childComponent.getLayout();

        view.setPadding(0, 0, 0, 0);

        int lm = 0, tm = 0, rm = 0, bm = 0;
        int deviceHeight =    getContext().getResources().getDisplayMetrics().heightPixels;
        int viewWidth = (int) Utils.getViewWidth(getContext(), layout, FrameLayout.LayoutParams.MATCH_PARENT);
        int viewHeight = (int) Utils.getViewHeight(getContext(), layout, FrameLayout.LayoutParams.WRAP_CONTENT);

        int parentViewWidth = (int) Utils.getViewWidth(getContext(),
                parentLayout,
                parentView.getMeasuredWidth());
        int parentViewHeight = (int) Utils.getViewHeight(getContext(),
                parentLayout,
                parentView.getMeasuredHeight());
      //  int maxViewWidth = (int) getViewMaximumWidth(getContext(), layout, -1);
        int measuredHeight = parentViewHeight != 0 ? parentViewHeight : deviceHeight;
        int gravity = Gravity.NO_GRAVITY;

            FireTV mobile = layout.getTv();
            if (mobile != null) {
                if (Utils.getViewWidth(mobile) != -1) {
                    if (mobile.getXAxis() != null) {
                        float scaledX = DEVICE_WIDTH * (Float.valueOf(mobile.getXAxis()) / STANDARD_MOBILE_WIDTH_PX);
                        lm = Math.round(scaledX);
                    }
                }

                if (Utils.getViewHeight(mobile) != -1) {
                    if (mobile.getYAxis() != null) {
                        float scaledY = DEVICE_HEIGHT * ((Float.valueOf(mobile.getYAxis()) / STANDARD_MOBILE_HEIGHT_PX));
                        tm = Math.round(scaledY);
                    }
                }

                if (mobile.getLeftMargin() != null && (Float.valueOf(mobile.getLeftMargin()) != 0)) {
                    float scaledLm = DEVICE_WIDTH * ((Float.valueOf(mobile.getLeftMargin()) / STANDARD_MOBILE_WIDTH_PX));
                    lm = Math.round(scaledLm);
                }

                if(mobile.getTopMargin() != null && (Float.valueOf(mobile.getTopMargin())) != 0){
                    float scaledLm = DEVICE_HEIGHT * ((Float.valueOf(mobile.getTopMargin()) / STANDARD_MOBILE_HEIGHT_PX));
                    tm = Math.round(scaledLm);
                }

                if(mobile.getRightMargin() != null && (Float.valueOf(mobile.getRightMargin())) != 0){
                    float scaledLm = DEVICE_WIDTH * ((Float.valueOf(mobile.getRightMargin()) / STANDARD_MOBILE_WIDTH_PX));
                    rm = Math.round(scaledLm);
                }

            }


        AppCMSUIKeyType componentType = jsonValueKeyMap.get(childComponent.getType());
        AppCMSUIKeyType componentKey = jsonValueKeyMap.get(childComponent.getKey());

        if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY ||
                componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY ) {
            if (viewWidth < 0) {
                viewWidth = FrameLayout.LayoutParams.MATCH_PARENT;
            }

            if(childComponent.getTextAlignment() != null){
                AppCMSUIKeyType textAlignment = jsonValueKeyMap.get(childComponent.getTextAlignment());
                switch(textAlignment){
                    case PAGE_TEXTALIGNMENT_LEFT_KEY:
                        gravity = Gravity.LEFT ;
                        if(componentKey == AppCMSUIKeyType.PAGE_VIDEO_TITLE_KEY){
                            gravity = Gravity.LEFT | Gravity.CENTER;
                        }
                        break;
                    case PAGE_TEXTALIGNMENT_RIGHT_KEY:
                        gravity = Gravity.RIGHT ;
                        if(componentKey == AppCMSUIKeyType.PAGE_VIDEO_SUBTITLE_KEY){
                            gravity = Gravity.RIGHT | Gravity.CENTER;
                        }
                        break;
                    case PAGE_TEXTALIGNMENT_CENTER_KEY:
                        gravity = Gravity.CENTER;
                        break;
                }
                ((TextView) view).setGravity(gravity);
            }


            if (componentKey == null) {
                componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            switch (componentKey) {
                case PAGE_TRAY_TITLE_KEY:
                    break;
                case PAGE_PLAY_IMAGE_KEY:
                    gravity = Gravity.CENTER;
                    tm = 0;
                    lm = 0;
                    break;
                case PAGE_THUMBNAIL_TITLE_KEY:
                 {
                        tm -= viewHeight / 2;
                        viewHeight *= 1.5;
                    }
                    break;
                case PAGE_WATCH_VIDEO_KEY:
                    gravity = Gravity.CENTER_HORIZONTAL;
                    break;
                case PAGE_VIDEO_SHARE_KEY:
                    break;
                case PAGE_API_TITLE:
                    viewHeight *= 1.5;
                    break;
                case PAGE_ADD_TO_WATCHLIST_KEY:
                case PAGE_VIDEO_WATCH_TRAILER_KEY:
                    //viewWidth = FrameLayout.LayoutParams.WRAP_CONTENT;
                    int padding = childComponent.getPadding();
                    view.setPadding(padding,padding,padding,padding);
                    break;
                case PAGE_VIDEO_TITLE_KEY:
                case PAGE_VIDEO_SUBTITLE_KEY:
                    viewWidth = DEVICE_WIDTH/2;
                    break;
                default:
            }

            int fontsize = getFontsize(getContext(), childComponent);
            if (fontsize > 0) {
                ((TextView) view).setTextSize((float) fontsize);
            }
        } else if (componentType == AppCMSUIKeyType.PAGE_TEXTFIELD_KEY) {
            viewHeight *= 1.2;
        }

        if (useWidthOfScreen) {
            viewWidth = DEVICE_WIDTH;
        }

        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(viewWidth, viewHeight);
        marginLayoutParams.setMargins(lm, tm, rm, bm);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(marginLayoutParams);
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
        if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY ||
                componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY ||
                componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY) {
            layoutParams.gravity = gravity;
        }
        view.setLayoutParams(layoutParams);
    }


    protected int getFontsize(Context context, Component component) {
        if (component.getFontSize() > 0) {
            return component.getFontSize();
        }
        if (component.getLayout().getTv().getFontSize() > 0) {
                return component.getLayout().getTv().getFontSize();
        }

        return 0;
    }


}
