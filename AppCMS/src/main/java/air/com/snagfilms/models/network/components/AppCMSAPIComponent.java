package air.com.snagfilms.models.network.components;

import javax.inject.Singleton;

import air.com.snagfilms.models.network.modules.AppCMSAPIModule;
import air.com.snagfilms.models.network.rest.AppCMSPageAPICall;
import dagger.Component;

/**
 * Created by viewlift on 5/9/17.
 */

@Singleton
@Component(modules={AppCMSAPIModule.class})
public interface AppCMSAPIComponent {
    AppCMSPageAPICall appCMSPageAPICall();
}
