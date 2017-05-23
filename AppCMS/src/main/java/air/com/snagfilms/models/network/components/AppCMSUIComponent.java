package air.com.snagfilms.models.network.components;

import javax.inject.Singleton;

import air.com.snagfilms.models.network.modules.AppCMSUIModule;
import air.com.snagfilms.models.network.rest.AppCMSAndroidUICall;
import air.com.snagfilms.models.network.rest.AppCMSMainUICall;
import air.com.snagfilms.models.network.rest.AppCMSPageUICall;
import dagger.Component;

/**
 * Created by viewlift on 5/4/17.
 */

@Singleton
@Component(modules={AppCMSUIModule.class})
public interface AppCMSUIComponent {
    AppCMSMainUICall appCMSMainCall();
    AppCMSAndroidUICall appCMSAndroidCall();
    AppCMSPageUICall appCMSPageCall();
}
