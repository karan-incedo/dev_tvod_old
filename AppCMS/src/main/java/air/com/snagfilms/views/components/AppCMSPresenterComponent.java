package air.com.snagfilms.views.components;

import javax.inject.Singleton;

import air.com.snagfilms.presenters.AppCMSPresenter;
import air.com.snagfilms.views.modules.AppCMSPresenterModule;
import dagger.Component;

/**
 * Created by viewlift on 5/22/17.
 */
@Singleton
@Component(modules={AppCMSPresenterModule.class})
public interface AppCMSPresenterComponent {
    AppCMSPresenter appCMSPresenter();
}
