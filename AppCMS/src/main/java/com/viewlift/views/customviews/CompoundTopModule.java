package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.main.Content;
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
    ){
        super(context, moduleInfo, false);

        this.jsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
        this.moduleViewList = moduleViewList;
        this.mContext = context;
        init();
    }
    public void init(){
        try {
            ViewGroup chieldContainer = getChildrenContainer();
            LinearLayout topComponent = new LinearLayout(mContext);

            if (!BaseView.isTablet(mContext)) {
                topComponent.setOrientation(LinearLayout.VERTICAL);
            }

            for (ModuleView moduleView:moduleViewList) {

                topComponent.addView(moduleView);

            }

            chieldContainer.addView(topComponent);

        }catch (Exception e){
            System.out.println("======="+e.getMessage());
            e.printStackTrace();
        }
    }
}
