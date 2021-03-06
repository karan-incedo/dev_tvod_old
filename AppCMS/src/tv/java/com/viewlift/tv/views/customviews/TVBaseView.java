package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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

import static com.viewlift.tv.utility.Utils.getViewHeight;
import static com.viewlift.tv.utility.Utils.getViewWidth;


/**
 * Created by nitin.tyagi on 7/12/2017.
 */

public abstract class TVBaseView extends FrameLayout {


    protected static int DEVICE_WIDTH;
    protected static int DEVICE_HEIGHT;
    public static final int STANDARD_MOBILE_WIDTH_PX = 1920;
    public static final int STANDARD_MOBILE_HEIGHT_PX = 1080;
    private static float LETTER_SPACING = 0.17f;
    private ViewGroup childrenContainer;
    protected boolean[] componentHasViewList;

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
        boolean appendFirstSep = runtime >= 0 &&
                (!TextUtils.isEmpty(year) || !TextUtils.isEmpty(primaryCategory));
        boolean appendSecondSep = (runtime >= 0 || !TextUtils.isEmpty(year)) &&
                !TextUtils.isEmpty(primaryCategory);
        StringBuffer infoText = new StringBuffer();
        if (runtime >= 0) {
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
    protected ViewGroup createChildrenContainer() {
        childrenContainer = new FrameLayout(getContext());
        int viewWidth = (int) getViewWidth(getContext(), getLayout(), (float) LayoutParams.MATCH_PARENT);
        int viewHeight = (int) getViewHeight(getContext(), getLayout(), (float) LayoutParams.MATCH_PARENT);
        FrameLayout.LayoutParams childContainerLayoutParams =
                new FrameLayout.LayoutParams(viewWidth, viewHeight);
        childrenContainer.setLayoutParams(childContainerLayoutParams);
        addView(childrenContainer);
        return childrenContainer;
    }
    public void setViewMarginsFromComponent(Component childComponent,
                                            View view,
                                            Layout parentLayout,
                                            View parentView,
                                            Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                            boolean useMarginsAsPercentages,
                                            boolean useWidthOfScreen,
                                            String viewType) {
        Layout layout = childComponent.getLayout();

        view.setPadding(0, 0, 0, 0);

        int lm = 0, tm = 0, rm = 0, bm = 0;
        int deviceHeight =    getContext().getResources().getDisplayMetrics().heightPixels;
        int viewWidth = (int) getViewWidth(getContext(), layout, FrameLayout.LayoutParams.MATCH_PARENT);
        int viewHeight = (int) getViewHeight(getContext(), layout, FrameLayout.LayoutParams.WRAP_CONTENT);

        int parentViewWidth = (int) getViewWidth(getContext(),
                parentLayout,
                parentView.getMeasuredWidth());
        int parentViewHeight = (int) getViewHeight(getContext(),
                parentLayout,
                parentView.getMeasuredHeight());
      //  int maxViewWidth = (int) getViewMaximumWidth(getContext(), layout, -1);
        int measuredHeight = parentViewHeight != 0 ? parentViewHeight : deviceHeight;
        int gravity = Gravity.NO_GRAVITY;

            FireTV mobile = layout.getTv();
            if (mobile != null) {
                if (getViewWidth(mobile) != -1) {
                    if (mobile.getXAxis() != null) {
                        float scaledX = DEVICE_WIDTH * (Float.valueOf(mobile.getXAxis()) / STANDARD_MOBILE_WIDTH_PX);
                        lm = Math.round(scaledX);
                    }
                }

                if (getViewHeight(mobile) != -1) {
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
                            gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                        }
                        break;
                    case PAGE_TEXTALIGNMENT_RIGHT_KEY:
                        gravity = Gravity.RIGHT ;
                        if(componentKey == AppCMSUIKeyType.PAGE_VIDEO_SUBTITLE_KEY){
                            gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                        }
                        break;
                    case PAGE_TEXTALIGNMENT_CENTER_KEY:
                        gravity = Gravity.CENTER;
                        if(componentKey == AppCMSUIKeyType.PAGE_SETTINGS_USER_EMAIL_LABEL_KEY){
                            gravity = Gravity.CENTER_HORIZONTAL;
                        }
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
                    if (AppCMSUIKeyType.PAGE_HISTORY_MODULE_KEY != jsonValueKeyMap.get(viewType)
                            && AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY != jsonValueKeyMap.get(viewType)
                            && AppCMSUIKeyType.PAGE_WATCHLIST_MODULE_KEY != jsonValueKeyMap.get(viewType)) {
                        gravity = Gravity.CENTER;
                        tm = 0;
                        lm = 0;
                    }
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
                    int padding = Utils.getViewXAxisAsPerScreen(getContext(),childComponent.getPadding());
                    view.setPadding(padding,padding,padding,padding);
                    break;
                case PAGE_VIDEO_TITLE_KEY:
                    viewWidth = DEVICE_WIDTH/2 - Utils.getViewXAxisAsPerScreen(getContext() , 150);
                    break;
                case PAGE_VIDEO_SUBTITLE_KEY:
                    viewWidth = DEVICE_WIDTH/2;
                    break;
                case PAGE_AUTOPLAY_FINISHED_UP_TITLE_KEY:
                case PAGE_AUTOPLAY_FINISHED_MOVIE_TITLE_KEY:
                    gravity = Gravity.NO_GRAVITY;
                break;
            }

            int fontsize = getFontsize(getContext(), childComponent);
            if (fontsize > 0) {
                ((TextView) view).setTextSize((float) fontsize);
            }
        } else if (componentType == AppCMSUIKeyType.PAGE_TEXTFIELD_KEY) {
            viewHeight *= 1.2;
        } else if (componentType == AppCMSUIKeyType.PAGE_TABLE_VIEW_KEY) {
            viewHeight = (int) (viewHeight / 1.15);
        } else if (componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY
            && componentKey == AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_IMAGE_KEY) {
                int imagePadding = Integer.valueOf(
                        childComponent.getLayout().getTv().getPadding() != null
                                ? childComponent.getLayout().getTv().getPadding()
                                : "0");
                view.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
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

    protected void initializeComponentHasViewList(int size) {
        componentHasViewList = new boolean[size];
    }

    protected void setTypeFace(Context context,
                             Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                             Component component,
                             TextView textView) {
        if (jsonValueKeyMap.get(component.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY) {
            AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(component.getFontWeight());
            if (fontWeight == null) {
                fontWeight = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            Typeface face = null;
            switch (fontWeight) {
                case PAGE_TEXT_BOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_bold_ttf));
                    break;
                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_semibold_ttf));
                    break;
                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_extrabold_ttf));
                    break;
                default:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_regular_ttf));
            }
            textView.setTypeface(face);
        }
    }
}
