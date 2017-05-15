package air.com.snagfilms.views.components;

import android.support.annotation.Nullable;

import javax.inject.Singleton;

import air.com.snagfilms.views.customviews.ComponentView;
import air.com.snagfilms.views.customviews.PageView;
import air.com.snagfilms.views.customviews.ViewCreator;
import air.com.snagfilms.views.modules.AppCMSPageViewModule;
import dagger.Component;

/**
 * Created by viewlift on 5/5/17.
 */

@Singleton
@Component(modules={AppCMSPageViewModule.class})
public interface AppCMSViewComponent {
    ViewCreator viewCreator();
    @Nullable PageView appCMSPageView();
}
