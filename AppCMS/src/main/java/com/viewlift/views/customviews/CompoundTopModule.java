package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;
import java.util.Map;

/**
 * Created by sandeep on 14/02/18.
 */

public class CompoundTopModule extends ModuleView {

    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final List<ModuleView> moduleViewList;
    private final Context mContext;

    public CompoundTopModule(Context context,
                             ModuleWithComponents moduleInfo,
                             Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                             AppCMSPresenter appCMSPresenter,
                             List<ModuleView> moduleViewList
    ) {
        super(context, moduleInfo, false);

        this.jsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
        this.moduleViewList = moduleViewList;
        this.mContext = context;
        init();
    }

    public void init() {
        try {
            ViewGroup chieldContainer = getChildrenContainer();
            if (BaseView.isTablet(mContext)) {
                addComponentTab(chieldContainer);

            } else {
                addComponentMobile(chieldContainer);
            }

        } catch (Exception e) {

        }
    }

    private void addComponentMobile(final ViewGroup parentView) {
        LinearLayout topComponent = new LinearLayout(mContext);
        topComponent.setOrientation(LinearLayout.VERTICAL);

        for (ModuleView moduleView : moduleViewList) {

            if (jsonValueKeyMap.get(moduleView.getModule().getType())==AppCMSUIKeyType.PAGE_MEDIAM_RECTANGLE_AD_MODULE_KEY){
                LinearLayout linearLayout=new LinearLayout(mContext);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.addView(moduleView);
                topComponent.addView(linearLayout);
                continue;
            }
            topComponent.addView(moduleView);
        }

        parentView.addView(topComponent);
        setBackgroundColor(Color.GRAY);
    }

    private void addComponentTab(final ViewGroup parent) {
        LinearLayout topComponent = new LinearLayout(mContext);
        topComponent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        LinearLayout layoutHeadline =new LinearLayout(mContext);

        LinearLayout.LayoutParams crosualLP, topHeadlineLP;

        if (isLandscape(mContext)){
            topComponent.setWeightSum(100);
            crosualLP = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            topHeadlineLP = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            crosualLP.weight = 70;
            topHeadlineLP.weight = 30;
            layoutHeadline.setOrientation(LinearLayout.VERTICAL);
            topComponent.setOrientation(LinearLayout.HORIZONTAL);
        }else {
            crosualLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            topHeadlineLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutHeadline.setOrientation(LinearLayout.HORIZONTAL);
            topComponent.setOrientation(LinearLayout.VERTICAL);

        }

        layoutHeadline.setLayoutParams(topHeadlineLP);


        for (ModuleView moduleView : moduleViewList) {
            switch (jsonValueKeyMap.get(moduleView.getModule().getType())) {
                case PAGE_EVENT_CAROUSEL_MODULE_KEY:
                    topComponent.addView(moduleView,crosualLP);
                    break;
                case PAGE_LIST_MODULE_KEY:
                    if (isLandscape(mContext)) {
                        layoutHeadline.addView(moduleView);
                    }else{
                        LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                        lp.weight=50;
                        layoutHeadline.addView(moduleView,lp);
                    }
                    break;
                case PAGE_MEDIAM_RECTANGLE_AD_MODULE_KEY:
                    if (isLandscape(mContext)) {
                        layoutHeadline.addView(moduleView);
                    }else{
                        LinearLayout.LayoutParams lp= new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                        lp.weight=50;
                        layoutHeadline.addView(moduleView,lp);
                    }
                    break;
            }

        }
        topComponent.addView(layoutHeadline);
        parent.addView(topComponent);
        setBackgroundColor(Color.GRAY);
    }
}
