package air.com.snagfilms.views.modules;

import android.content.Context;
import android.support.annotation.Nullable;

import javax.inject.Singleton;

import air.com.snagfilms.models.data.appcms.page.Page;
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

    public AppCMSPageViewModule(Context context, Page page) {
        this.context = context;
        this.page = page;
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
        return viewCreator.generatePage(context, page);
    }
}
