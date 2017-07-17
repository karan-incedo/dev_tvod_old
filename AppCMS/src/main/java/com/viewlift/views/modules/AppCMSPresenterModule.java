package com.viewlift.views.modules;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.network.modules.AppCMSSearchModule;
import com.viewlift.models.network.rest.AppCMSAddToWatchlistCall;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;
import com.viewlift.models.network.rest.AppCMSBeaconRest;
import com.viewlift.models.network.rest.AppCMSDeleteHistoryCall;
import com.viewlift.models.network.rest.AppCMSFacebookLoginCall;
import com.viewlift.models.network.rest.AppCMSHistoryCall;
import com.viewlift.models.network.rest.AppCMSMainUICall;
import com.viewlift.models.network.rest.AppCMSPageUICall;
import com.viewlift.models.network.rest.AppCMSRefreshIdentityCall;
import com.viewlift.models.network.rest.AppCMSResetPasswordCall;
import com.viewlift.models.network.rest.AppCMSSearchCall;
import com.viewlift.models.network.rest.AppCMSSignInCall;
import com.viewlift.models.network.rest.AppCMSSiteCall;
import com.viewlift.models.network.rest.AppCMSSubscriptionCall;
import com.viewlift.models.network.rest.AppCMSUpdateWatchHistoryCall;
import com.viewlift.models.network.rest.AppCMSUserIdentityCall;
import com.viewlift.models.network.rest.AppCMSUserVideoStatusCall;
import com.viewlift.models.network.rest.AppCMSWatchlistCall;
import com.viewlift.presenters.AppCMSActionType;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by viewlift on 5/22/17.
 */

@Module(includes = {AppCMSSearchModule.class})
public class AppCMSPresenterModule {

    @Provides
    @Singleton
    public AppCMSPresenter providesAppCMSPresenter(AppCMSMainUICall appCMSMainUICall,
                                                   AppCMSAndroidUICall appCMSAndroidUICall,
                                                   AppCMSPageUICall appCMSPageUICall,
                                                   AppCMSSiteCall appCMSSiteCall,
                                                   AppCMSSearchCall appCMSSearchCall,

                                                   AppCMSWatchlistCall appCMSWatchlistCall,
                                                   AppCMSHistoryCall appCMSHistoryCall,

                                                   AppCMSDeleteHistoryCall appCMSDeleteHistoryCall,
                                                   AppCMSSubscriptionCall appCMSSubscriptionCall,

                                                   AppCMSBeaconRest appCMSBeaconRest,
                                                   AppCMSSignInCall appCMSSignInCall,
                                                   AppCMSRefreshIdentityCall appCMSRefreshIdentityCall,
                                                   AppCMSResetPasswordCall appCMSResetPasswordCall,
                                                   AppCMSFacebookLoginCall appCMSFacebookLoginCall,
                                                   AppCMSUserIdentityCall appCMSUserIdentityCall,
                                                   AppCMSAddToWatchlistCall appCMSAddToWatchlistCall,

                                                   AppCMSUpdateWatchHistoryCall appCMSUpdateWatchHistoryCall,
                                                   AppCMSUserVideoStatusCall appCMSUserVideoStatusCall,

                                                   Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                                   Map<String, String> pageNameToActionMap,
                                                   Map<String, AppCMSPageUI> actionToPageMap,
                                                   Map<String, AppCMSPageAPI> actionToPageAPIMap,
                                                   Map<String, AppCMSActionType> actionToActionTypeMap) {
        return new AppCMSPresenter(appCMSMainUICall,
                appCMSAndroidUICall,
                appCMSPageUICall,
                appCMSSiteCall,
                appCMSSearchCall,

                appCMSWatchlistCall,
                appCMSHistoryCall,

                appCMSDeleteHistoryCall,
                appCMSSubscriptionCall,

                appCMSBeaconRest,
                appCMSSignInCall,
                appCMSRefreshIdentityCall,
                appCMSResetPasswordCall,
                appCMSFacebookLoginCall,
                appCMSUserIdentityCall,

                appCMSUpdateWatchHistoryCall,
                appCMSUserVideoStatusCall,
                appCMSAddToWatchlistCall,

                jsonValueKeyMap,
                pageNameToActionMap,
                actionToPageMap,
                actionToPageAPIMap,
                actionToActionTypeMap);
    }
}
