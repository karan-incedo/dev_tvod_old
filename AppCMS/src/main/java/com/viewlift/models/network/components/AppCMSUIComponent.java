package com.viewlift.models.network.components;

import com.viewlift.models.network.modules.AppCMSUIModule;
import com.viewlift.models.network.rest.AppCMSAddToWatchlistCall;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;
import com.viewlift.models.network.rest.AppCMSBeaconRest;
import com.viewlift.models.network.rest.AppCMSFacebookLoginCall;
import com.viewlift.models.network.rest.AppCMSHistoryCall;
import com.viewlift.models.network.rest.AppCMSMainUICall;
import com.viewlift.models.network.rest.AppCMSPageUICall;
import com.viewlift.models.network.rest.AppCMSResetPasswordCall;
import com.viewlift.models.network.rest.AppCMSUpdateWatchHistoryCall;
import com.viewlift.models.network.rest.AppCMSUserIdentityCall;
import com.viewlift.models.network.rest.AppCMSUserVideoStatusCall;
import com.viewlift.models.network.rest.AppCMSWatchlistCall;
import com.viewlift.models.network.rest.AppCMSRefreshIdentityCall;
import com.viewlift.models.network.rest.AppCMSSignInCall;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by viewlift on 5/4/17.
 */

@Singleton
@Component(modules = {AppCMSUIModule.class})
public interface AppCMSUIComponent {
    AppCMSMainUICall appCMSMainCall();

    AppCMSAndroidUICall appCMSAndroidCall();

    AppCMSPageUICall appCMSPageCall();

    AppCMSBeaconRest appCMSBeaconRest();

    AppCMSWatchlistCall appCMSWatchlistCall();

    AppCMSHistoryCall appCMSHistoryCall();

    AppCMSSignInCall appCMSSignInCall();

    AppCMSRefreshIdentityCall appCMSRefreshIdentityCall();

    AppCMSResetPasswordCall appCMSPasswordCall();

    AppCMSFacebookLoginCall appCMSFacebookLoginCall();

    AppCMSUserIdentityCall appCMSUserIdentityCall();

    AppCMSUpdateWatchHistoryCall appCMSUpdateWatchHistoryCall();

    AppCMSUserVideoStatusCall appCMSUserVideoStatusCall();

    AppCMSAddToWatchlistCall appCMSAddToWatchlistCall();
}
