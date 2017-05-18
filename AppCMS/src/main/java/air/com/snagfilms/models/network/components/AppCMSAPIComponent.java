package air.com.snagfilms.models.network.components;

import java.util.Map;

import javax.inject.Singleton;

import air.com.snagfilms.models.data.appcms.AppCMSKeyType;
import air.com.snagfilms.models.network.modules.AppCMSAPIModule;
import air.com.snagfilms.models.network.rest.AppCMSAndroidCall;
import air.com.snagfilms.models.network.rest.AppCMSMainCall;
import air.com.snagfilms.models.network.rest.AppCMSPageCall;
import dagger.Component;

/**
 * Created by viewlift on 5/4/17.
 */

@Singleton
@Component(modules={AppCMSAPIModule.class})
public interface AppCMSAPIComponent {
    AppCMSMainCall appCMSMainCall();
    AppCMSAndroidCall appCMSAndroidCall();
    AppCMSPageCall appCMSPageCall();
    Map<AppCMSKeyType, String> jsonValueKeyMap();
}
