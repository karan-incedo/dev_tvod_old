package com.viewlift.views.modules;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.network.modules.AppCMSSearchModule;
import com.viewlift.models.network.rest.AppCMSAddToWatchlistCall;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;
import com.viewlift.models.network.rest.AppCMSAnonymousAuthTokenCall;
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
import com.viewlift.models.network.rest.AppCMSSubscriptionPlanCall;
import com.viewlift.models.network.rest.AppCMSUpdateWatchHistoryCall;
import com.viewlift.models.network.rest.AppCMSUserDownloadVideoStatusCall;
import com.viewlift.models.network.rest.AppCMSUserIdentityCall;
import com.viewlift.models.network.rest.AppCMSUserVideoStatusCall;
import com.viewlift.models.network.rest.AppCMSWatchlistCall;
import com.viewlift.models.network.rest.GoogleCancelSubscriptionCall;
import com.viewlift.models.network.rest.GoogleCancelSubscriptionRest;
import com.viewlift.models.network.rest.GoogleRefreshTokenCall;
import com.viewlift.models.network.rest.GoogleRefreshTokenRest;
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
    public AppCMSPresenter providesAppCMSPresenter(Gson gson,
                                                   AppCMSMainUICall appCMSMainUICall,
                                                   AppCMSAndroidUICall appCMSAndroidUICall,
                                                   AppCMSPageUICall appCMSPageUICall,
                                                   AppCMSSiteCall appCMSSiteCall,
                                                   AppCMSSearchCall appCMSSearchCall,

                                                   AppCMSWatchlistCall appCMSWatchlistCall,
                                                   AppCMSHistoryCall appCMSHistoryCall,

                                                   AppCMSDeleteHistoryCall appCMSDeleteHistoryCall,

                                                   AppCMSSubscriptionCall appCMSSubscriptionCall,
                                                   AppCMSSubscriptionPlanCall appCMSSubscriptionPlanCall,
                                                   AppCMSAnonymousAuthTokenCall appCMSAnonymousAuthTokenCall,

                                                   AppCMSBeaconRest appCMSBeaconRest,
                                                   AppCMSSignInCall appCMSSignInCall,
                                                   AppCMSRefreshIdentityCall appCMSRefreshIdentityCall,
                                                   AppCMSResetPasswordCall appCMSResetPasswordCall,
                                                   AppCMSFacebookLoginCall appCMSFacebookLoginCall,
                                                   AppCMSUserIdentityCall appCMSUserIdentityCall,
                                                   GoogleRefreshTokenCall googleRefreshTokenCall,
                                                   GoogleCancelSubscriptionCall googleCancelSubscriptionCall,
                                                   AppCMSAddToWatchlistCall appCMSAddToWatchlistCall,

                                                   AppCMSUpdateWatchHistoryCall appCMSUpdateWatchHistoryCall,
                                                   AppCMSUserVideoStatusCall appCMSUserVideoStatusCall,
                                                   AppCMSUserDownloadVideoStatusCall appCMSUserDownloadVideoStatusCall,

                                                   Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                                   Map<String, String> pageNameToActionMap,
                                                   Map<String, AppCMSPageUI> actionToPageMap,
                                                   Map<String, AppCMSPageAPI> actionToPageAPIMap,
                                                   Map<String, AppCMSActionType> actionToActionTypeMap) {
        return new AppCMSPresenter(gson,
                appCMSMainUICall,
                appCMSAndroidUICall,
                appCMSPageUICall,
                appCMSSiteCall,
                appCMSSearchCall,

                appCMSWatchlistCall,
                appCMSHistoryCall,

                appCMSDeleteHistoryCall,

                appCMSSubscriptionCall,
                appCMSSubscriptionPlanCall,
                appCMSAnonymousAuthTokenCall,

                appCMSBeaconRest,
                appCMSSignInCall,
                appCMSRefreshIdentityCall,
                appCMSResetPasswordCall,
                appCMSFacebookLoginCall,
                appCMSUserIdentityCall,
                googleRefreshTokenCall,
                googleCancelSubscriptionCall,

                appCMSUpdateWatchHistoryCall,
                appCMSUserVideoStatusCall,
                appCMSUserDownloadVideoStatusCall,

                appCMSAddToWatchlistCall,

                jsonValueKeyMap,
                pageNameToActionMap,
                actionToPageMap,
                actionToPageAPIMap,
                actionToActionTypeMap);
    }
}
