package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
import com.viewlift.utils.LocaleUtils;

import java.util.Locale;
import java.util.Map;

/**
 * Created by anas.azeem on 9/4/2017.
 * Owned by ViewLift, NYC
 */

public class ToggleSwitchView extends TVBaseView {
    private Context mContext;
    private Component mComponent;
    private int DEFAULT_HEIGHT = LayoutParams.WRAP_CONTENT;
    private int DEFAULT_WIDTH = LayoutParams.MATCH_PARENT;
    private Map<String, AppCMSUIKeyType> mJsonValueKeyMap;
    private TextView textView;
    private ImageView imageView;
    boolean isEnabled = false;
    AppCMSPresenter appCMSPresenter;

    public TextView getTextView() {
        return textView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public ToggleSwitchView(Context context, Component component,
                            Map<String, AppCMSUIKeyType> jsonValueKeyMap, AppCMSPresenter appCMSPresenter) {
        super(context);
        mContext = context;
        mComponent = component;
        mJsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
        /*isEnabled = ((AppCMSApplication) mContext.getApplicationContext()).
                getAppCMSPresenterComponent().appCMSPresenter()
                .getAutoplayEnabledUserPref(mContext);*/
        init();
        initComponent();
        setFocusable(true);
    }

    @Override
    public void init() {
        float viewHeight = Utils.getViewHeight(mContext, mComponent.getLayout(), DEFAULT_HEIGHT);
        float viewWidth = Utils.getViewWidth(mContext, mComponent.getLayout(), DEFAULT_WIDTH);
        LayoutParams layoutParams = new LayoutParams((int) viewWidth, (int) viewHeight);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        setLayoutParams(layoutParams);
    }

    private void initComponent() {
        if (null != mComponent) {
            addView(createTextView(mComponent));
            addView(createToggleView(mComponent));
        }
    }

    private View createToggleView(Component mComponent) {
        ImageView componentView = new ImageView(mContext);
        if (mJsonValueKeyMap.get(mComponent.getKey()) == AppCMSUIKeyType.PAGE_SETTING_AUTOPLAY_TOGGLE_SWITCH_KEY) {
            isEnabled = ((AppCMSApplication) mContext.getApplicationContext()).
                    getAppCMSPresenterComponent().appCMSPresenter()
                    .getAutoplayEnabledUserPref(mContext);
            setOnClickListener(v -> {
                if (isEnabled) {
                    isEnabled = false;
                    componentView.setImageResource(R.drawable.focused_off);
                 } else {
                    isEnabled = true;
                    componentView.setImageResource(R.drawable.focused_on);
                 }
                ((AppCMSApplication) mContext.getApplicationContext()).getAppCMSPresenterComponent()
                        .appCMSPresenter().setAutoplayEnabledUserPref(mContext, isEnabled);
            });
        } else if (mJsonValueKeyMap.get(mComponent.getKey()) == AppCMSUIKeyType.PAGE_SETTING_CLOSED_CAPTION_TOGGLE_SWITCH_KEY) {
            isEnabled = ((AppCMSApplication) mContext.getApplicationContext()).
                    getAppCMSPresenterComponent().appCMSPresenter()
                    .getClosedCaptionPreference();
            setOnClickListener(v -> {
                if (isEnabled) {
                    isEnabled = false;
                    componentView.setImageResource(R.drawable.focused_off);
                } else {
                    isEnabled = true;
                    componentView.setImageResource(R.drawable.focused_on);
                }
                ((AppCMSApplication) mContext.getApplicationContext()).getAppCMSPresenterComponent()
                        .appCMSPresenter().setClosedCaptionPreference(isEnabled);
            });
        }
        if (isEnabled) {
            componentView.setImageResource(R.drawable.unfocused_on);
        } else {
            componentView.setImageResource(R.drawable.unfocused_off);
        }
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        componentView.setLayoutParams(params);

        this.setOnFocusChangeListener((v, hasFocus) -> {

            if(hasFocus && null != appCMSPresenter
                    && null != appCMSPresenter.getCurrentActivity()
                    && appCMSPresenter.getCurrentActivity() instanceof AppCmsHomeActivity){
                ((AppCmsHomeActivity) appCMSPresenter.getCurrentActivity()).shouldShowSubLeftNavigation((hasFocus));
            }

            if (hasFocus) {
                if (isEnabled) {
                    componentView.setImageResource(R.drawable.focused_on);
                } else {
                    componentView.setImageResource(R.drawable.focused_off);
                }
            } else {
                if (isEnabled) {
                    componentView.setImageResource(R.drawable.unfocused_on);
                } else {
                    componentView.setImageResource(R.drawable.unfocused_off);
                }
            }
        });

        imageView = componentView;
        return componentView;
    }

    private View createTextView(Component component) {
        View componentView = new TextView(mContext);
        int textColor = ContextCompat.getColor(mContext, R.color.colorAccent);
        if (!TextUtils.isEmpty(component.getTextColor())) {
            textColor = Color.parseColor(getColor(mContext, component.getTextColor()));
        } else if (component.getStyles() != null) {
            if (!TextUtils.isEmpty(component.getStyles().getColor())) {
                textColor = Color.parseColor(getColor(mContext, component.getStyles().getColor()));
            } else if (!TextUtils.isEmpty(component.getStyles().getTextColor())) {
                textColor = Color.parseColor(getColor(mContext, component.getStyles().getTextColor()));
            }
        }
        ((TextView) componentView).setTextColor(textColor);
        Typeface typeface = Utils.getTypeFace(mContext, mJsonValueKeyMap, component);
        if (null != typeface) {
            ((TextView) componentView).setTypeface(typeface);
        }
        ((TextView) componentView).setText(component.getText());
        textView = (TextView) componentView;
        return componentView;
    }

    private String getColor(Context context, String color) {
        if (color.indexOf(context.getString(R.string.color_hash_prefix)) != 0) {
            return context.getString(R.string.color_hash_prefix) + color;
        }
        return color;
    }

    @Override
    protected Component getChildComponent(int index) {
        return null;
    }

    @Override
    protected Layout getLayout() {
        return null;
    }
}
