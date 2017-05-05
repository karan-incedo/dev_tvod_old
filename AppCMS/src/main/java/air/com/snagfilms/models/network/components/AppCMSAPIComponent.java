package air.com.snagfilms.models.network.components;

import javax.inject.Singleton;

import air.com.snagfilms.models.data.appcms.android.Android;
import air.com.snagfilms.models.data.appcms.main.Main;
import air.com.snagfilms.models.data.appcms.page.Page;
import air.com.snagfilms.models.network.modules.AppCMSAPIModule;
import air.com.snagfilms.models.network.rest.AppCMSCall;
import dagger.Component;

/**
 * Created by viewlift on 5/4/17.
 */

@Singleton
@Component(modules={AppCMSAPIModule.class})
public interface AppCMSAPIComponent {
    AppCMSCall<Main> appCMSMainCall();
    AppCMSCall<Android> appCMSAndroidCall();
    AppCMSCall<Page> appCMSPageCall();
}
