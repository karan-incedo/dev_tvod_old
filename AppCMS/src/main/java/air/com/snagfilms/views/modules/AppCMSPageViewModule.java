package air.com.snagfilms.views.modules;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.Map;

import javax.inject.Singleton;

import air.com.snagfilms.models.data.appcms.AppCMSKeyType;
import air.com.snagfilms.models.data.appcms.page.Page;
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
    private final Page page;
    private final Map<AppCMSKeyType, String> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;

    public AppCMSPageViewModule(Context context,
                                Page page,
                                Map<AppCMSKeyType, String> jsonValueKeyMap,
                                AppCMSPresenter appCMSPresenter) {
        this.context = context;
        this.page = page;
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
        return viewCreator.generatePage(context, page, jsonValueKeyMap, appCMSPresenter);
    }
}
