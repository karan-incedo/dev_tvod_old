package air.com.snagfilms.views.modules;

import java.util.Map;

import javax.inject.Singleton;

import air.com.snagfilms.models.data.appcms.AppCMSActionType;
import air.com.snagfilms.models.data.appcms.AppCMSKeyType;
import air.com.snagfilms.models.data.appcms.page.Page;
import air.com.snagfilms.models.network.modules.AppCMSUIModule;
import air.com.snagfilms.models.network.rest.AppCMSAndroidUICall;
import air.com.snagfilms.models.network.rest.AppCMSMainUICall;
import air.com.snagfilms.models.network.rest.AppCMSPageUICall;
import air.com.snagfilms.presenters.AppCMSPresenter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by viewlift on 5/22/17.
 */
@Module(includes={AppCMSUIModule.class})
public class AppCMSPresenterModule {

    @Provides
    @Singleton
    public AppCMSPresenter providesAppCMSPresenter(AppCMSMainUICall appCMSMainUICall,
                                                   AppCMSAndroidUICall appCMSAndroidUICall,
                                                   AppCMSPageUICall appCMSPageUICall,
                                                   Map<AppCMSKeyType, String> jsonValueKeyMap,
                                                   Map<String, String> pageNameToActionMap,
                                                   Map<String, Page> actionToPageMap,
                                                   Map<String, AppCMSActionType> actionToActionTypeMap) {
        return new AppCMSPresenter(appCMSMainUICall,
                appCMSAndroidUICall,
                appCMSPageUICall,
                jsonValueKeyMap,
                pageNameToActionMap,
                actionToPageMap,
                actionToActionTypeMap);
    }
}
