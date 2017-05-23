package air.com.snagfilms.views.modules;

import java.util.Map;

import javax.inject.Singleton;

import air.com.snagfilms.models.data.appcms.ui.page.AppCMSPageUI;
import air.com.snagfilms.models.network.modules.AppCMSAPIModule;
import air.com.snagfilms.models.network.rest.AppCMSPageAPICall;
import air.com.snagfilms.presenters.AppCMSActionType;
import air.com.snagfilms.models.data.appcms.ui.AppCMSUIKeyType;
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
@Module(includes={AppCMSAPIModule.class})
public class AppCMSPresenterModule {

    @Provides
    @Singleton
    public AppCMSPresenter providesAppCMSPresenter(AppCMSMainUICall appCMSMainUICall,
                                                   AppCMSAndroidUICall appCMSAndroidUICall,
                                                   AppCMSPageUICall appCMSPageUICall,
                                                   AppCMSPageAPICall appCMSPageAPICall,
                                                   Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                                                   Map<String, String> pageNameToActionMap,
                                                   Map<String, AppCMSPageUI> actionToPageMap,
                                                   Map<String, AppCMSActionType> actionToActionTypeMap) {
        return new AppCMSPresenter(appCMSMainUICall,
                appCMSAndroidUICall,
                appCMSPageUICall,
                appCMSPageAPICall,
                jsonValueKeyMap,
                pageNameToActionMap,
                actionToPageMap,
                actionToActionTypeMap);
    }
}
