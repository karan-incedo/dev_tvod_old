package air.com.snagfilms.views.modules;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.Map;

import javax.inject.Singleton;

import air.com.snagfilms.models.data.appcms.ui.AppCMSUIKeyType;
import air.com.snagfilms.models.data.appcms.ui.page.AppCMSPageUI;
import air.com.snagfilms.presenters.AppCMSPresenter;
import air.com.snagfilms.views.customviews.PageView;
import air.com.snagfilms.views.customviews.ViewCreator;
import dagger.Module;
import dagger.Provides;

/**
 * Created by viewlift on 5/5/17.
 */

@Module
public class AppCMSPageViewModule {
    private final Context context;
    private final AppCMSPageUI appCMSPageUI;
    private final Map<AppCMSUIKeyType, String> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;

    public AppCMSPageViewModule(Context context,
                                AppCMSPageUI appCMSPageUI,
                                Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                                AppCMSPresenter appCMSPresenter) {
        this.context = context;
        this.appCMSPageUI = appCMSPageUI;
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
        return viewCreator.generatePage(context, appCMSPageUI, jsonValueKeyMap, appCMSPresenter);
    }
}
