package com.viewlift.views.customviews;

import android.content.Context;
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
            System.out.println("=======" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addComponentMobile(final ViewGroup parentView) {
        LinearLayout topComponent = new LinearLayout(mContext);
        topComponent.setOrientation(LinearLayout.VERTICAL);

        for (ModuleView moduleView : moduleViewList) {

            topComponent.addView(moduleView);
        }

        parentView.addView(topComponent);
    }

    private void addComponentTab(final ViewGroup parent) {
        LinearLayout topComponent = new LinearLayout(mContext);
        topComponent.setWeightSum(100);

        LinearLayout.LayoutParams crosualLP = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams topHeadlineLP = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (isLandscape(mContext)){
            crosualLP.weight = 70;
            topHeadlineLP.weight = 30;
        }else {
            crosualLP.weight = 60;
            topHeadlineLP.weight = 40;
        }
        LinearLayout layoutHeadline =new LinearLayout(mContext);

        topComponent.setOrientation(LinearLayout.HORIZONTAL);
        layoutHeadline.setOrientation(LinearLayout.VERTICAL);

        layoutHeadline.setLayoutParams(topHeadlineLP);


        for (ModuleView moduleView : moduleViewList) {
            switch (jsonValueKeyMap.get(moduleView.getModule().getType())) {
                case PAGE_EVENT_CAROUSEL_MODULE_KEY:
                    moduleView.setLayoutParams(crosualLP);
                    topComponent.addView(moduleView);
                    break;
                case PAGE_LIST_MODULE_KEY:
                    layoutHeadline.addView(moduleView);
                    break;
                case PAGE_MEDIAM_RECTANGLE_AD_MODULE_KEY:
                    layoutHeadline.addView(moduleView);
                    break;
            }

        }
        topComponent.addView(layoutHeadline);
        parent.addView(topComponent);
    }
}
