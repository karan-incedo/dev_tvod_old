package com.viewlift.views.adapters;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.ViewCreator;

import java.util.Map;

public class AppCMSSeasonTabPagerAdapter extends PagerAdapter {

    int mCurrentPosition = -1;
    Component subComponent;
    AppCMSAndroidModules appCMSAndroidModules;
    private final ModuleWithComponents moduleInfo;
    private final Module moduleAPI;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final ViewCreator viewCreator;

    public AppCMSSeasonTabPagerAdapter(Component subComponent,
                                       ViewCreator viewCreator,
                                       Module moduleAPI,
                                       AppCMSAndroidModules appCMSAndroidModules,
                                       Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                       AppCMSPresenter appCMSPresenter,
                                       ModuleWithComponents moduleInfo) {
        this.subComponent = subComponent;
        this.viewCreator = viewCreator;
        this.moduleAPI = moduleAPI;
        this.appCMSAndroidModules = appCMSAndroidModules;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
        this.moduleInfo = moduleInfo;
    }


    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        viewCreator.createComponentView(collection.getContext(),
                subComponent,
                subComponent.getLayout(),
                moduleAPI,
                appCMSAndroidModules,
                null,
                moduleInfo.getSettings(),
                jsonValueKeyMap,
                appCMSPresenter,
                false,
                moduleInfo.getView(),
                moduleInfo.getId());
        View componentView = viewCreator.getComponentViewResult().componentView;
        collection.addView(componentView);
        return componentView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }


    @Override
    public int getCount() {
        if (moduleAPI != null &&
                moduleAPI.getContentData() != null &&
                moduleAPI.getContentData().get(0) != null &&
                moduleAPI.getContentData().get(0).getSeason() != null)
            return moduleAPI.getContentData().get(0).getSeason().size();
        else
            return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (moduleAPI != null &&
                moduleAPI.getContentData() != null &&
                moduleAPI.getContentData().get(0) != null &&
                moduleAPI.getContentData().get(0).getSeason() != null)
            return moduleAPI.getContentData().get(0).getSeason().get(position).getTitle();
        else
            return "";
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);

    }



}
