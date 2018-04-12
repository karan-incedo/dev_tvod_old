package com.viewlift.tv.views.module;

import android.content.Context;
import android.support.annotation.Nullable;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.customviews.TVViewCreator;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.customviews.ViewCreator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import com.viewlift.R;

/**
 * Created by viewlift on 5/5/17.
 */

@Module
public class AppCMSTVPageViewModule {
    private final Context context;
    private final AppCMSPageUI appCMSPageUI;
    private final AppCMSPageAPI appCMSPageAPI;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final List<String> modulesToIgnoreList;
    private TVViewCreator viewCreator;
    private boolean isFromLoginDialog;

    public AppCMSTVPageViewModule(Context context,
                                  AppCMSPageUI appCMSPageUI,
                                  AppCMSPageAPI appCMSPageAPI,
                                  Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                  AppCMSPresenter appCMSPresenter) {
        this.context = context;
        this.appCMSPageUI = appCMSPageUI;
        this.appCMSPageAPI = appCMSPageAPI;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
        this.modulesToIgnoreList =
                Arrays.asList(context.getResources().getStringArray(R.array.app_cms_modules_to_ignore_tv));
    }


    public AppCMSTVPageViewModule(Context context,
                                  AppCMSPageUI appCMSPageUI,
                                  AppCMSPageAPI appCMSPageAPI,
                                  Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                  AppCMSPresenter appCMSPresenter,
                                  boolean isFromLoginDialog) {
        this.context = context;
        this.appCMSPageUI = appCMSPageUI;
        this.appCMSPageAPI = appCMSPageAPI;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
        this.modulesToIgnoreList =
                Arrays.asList(context.getResources().getStringArray(R.array.app_cms_modules_to_ignore));
        this.isFromLoginDialog = isFromLoginDialog;
    }



    @Provides
    @Singleton
    public TVViewCreator providesViewCreator() {
        if (viewCreator == null) {
            viewCreator = new TVViewCreator();
        }
        return viewCreator;
    }

    @Provides
    @Nullable
    public TVPageView providesViewFromPage(TVViewCreator viewCreator) {
        return viewCreator.generatePage(context,
                appCMSPageUI,
                appCMSPageAPI,
                jsonValueKeyMap,
                appCMSPresenter,
                modulesToIgnoreList,
                isFromLoginDialog);
    }
}
