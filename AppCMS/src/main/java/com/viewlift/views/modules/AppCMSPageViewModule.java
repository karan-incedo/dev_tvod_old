package com.viewlift.views.modules;

import android.content.Context;
import android.support.annotation.Nullable;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.customviews.ViewCreator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/5/17.
 */

@Module
public class AppCMSPageViewModule {
    private final Context context;
    private final AppCMSPageUI appCMSPageUI;
    private final AppCMSPageAPI appCMSPageAPI;
    private final String screenName;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final List<String> modulesToIgnoreList;
    private ViewCreator viewCreator;

    public AppCMSPageViewModule(Context context,
                                AppCMSPageUI appCMSPageUI,
                                AppCMSPageAPI appCMSPageAPI,
                                String screeeName,
                                Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                AppCMSPresenter appCMSPresenter) {
        this.context = context;
        this.appCMSPageUI = appCMSPageUI;
        this.appCMSPageAPI = appCMSPageAPI;
        this.screenName = screeeName;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
        this.modulesToIgnoreList =
                Arrays.asList(context.getResources().getStringArray(R.array.app_cms_modules_to_ignore));
    }

    @Provides
    @Singleton
    public ViewCreator providesViewCreator() {
        if (viewCreator == null) {
            viewCreator = new ViewCreator();
        }
        return viewCreator;
    }

    @Provides
    @Singleton
    @Nullable
    public PageView providesViewFromPage(ViewCreator viewCreator) {
        return viewCreator.generatePage(context,
                appCMSPageUI,
                appCMSPageAPI,
                screenName,
                jsonValueKeyMap,
                appCMSPresenter,
                modulesToIgnoreList);
    }
}
