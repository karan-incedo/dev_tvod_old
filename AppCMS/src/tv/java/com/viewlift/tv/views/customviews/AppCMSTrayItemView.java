package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;

import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.tv.utility.Utils;

import java.util.Map;

/**
 * Created by anas.azeem on 9/8/2017.
 * Owned by ViewLift, NYC
 */

public class AppCMSTrayItemView extends TVBaseView {

    private final Context mContext;
    private final Component mComponent;
    private final Map<String, AppCMSUIKeyType> mJsonValueKeyMap;
    private int DEFAULT_HEIGHT = LayoutParams.WRAP_CONTENT;
    private int DEFAULT_WIDTH = LayoutParams.MATCH_PARENT;

    public AppCMSTrayItemView(@NonNull Context context, Component component,
                              Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
        super(context);
        mContext = context;
        mComponent = component;
        mJsonValueKeyMap = jsonValueKeyMap;
        init();
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

    @Override
    protected Component getChildComponent(int index) {
        if (mComponent.getComponents() != null &&
                0 <= index &&
                index < mComponent.getComponents().size()) {
            return mComponent.getComponents().get(index);
        }
        return null;
    }

    @Override
    protected Layout getLayout() {
        return mComponent.getLayout();
    }
}
