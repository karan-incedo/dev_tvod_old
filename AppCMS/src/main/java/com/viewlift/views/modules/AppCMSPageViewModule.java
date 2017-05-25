package com.viewlift.views.modules;

import android.content.Context;
import android.support.annotation.Nullable;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.Map;

import javax.inject.Singleton;

import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.customviews.ViewCreator;
import dagger.Module;
import dagger.Provides;

/**
 * Created by viewlift on 5/5/17.
 */

@Module
public class AppCMSPageViewModule {
    private final Context context;
    private final AppCMSPageUI appCMSPageUI;
    private final AppCMSPageAPI appCMSPageAPI;
    private final Map<AppCMSUIKeyType, String> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;

    public AppCMSPageViewModule(Context context,
                                AppCMSPageUI appCMSPageUI,
                                AppCMSPageAPI appCMSPageAPI,
                                Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                                AppCMSPresenter appCMSPresenter) {
        this.context = context;
        this.appCMSPageUI = appCMSPageUI;
        this.appCMSPageAPI = appCMSPageAPI;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
    }

    @Provides
    @Singleton
    public ViewCreator providesViewCreator() {
        return new ViewCreator();
    }

    @Provides
    @Singleton
    @Nullable
    public PageView providesViewFromPage(ViewCreator viewCreator) {
        return viewCreator.generatePage(context,
                appCMSPageUI,
                appCMSPageAPI,
                jsonValueKeyMap,
                appCMSPresenter);
    }
}
