package com.viewlift.appcmssdk;

import android.content.Context;

import com.viewlift.models.network.modules.AppCMSUIModule;
import com.viewlift.models.network.rest.AppCMSFilmRecordsCall;
import com.viewlift.models.network.rest.AppCMSMainUICall;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by viewlift on 7/13/17.
 */

@Module(includes = {AppCMSUIModule.class})
public class AppCMSSDKModule {
    private Context context;
    private String appCMSBaseURL;
    private String appCMSSiteId;

    public AppCMSSDKModule(Context context,
                           String appCMSBaseURL,
                           String appCMSSiteId) {
        this.context = context;
        this.appCMSBaseURL = appCMSBaseURL;
        this.appCMSSiteId = appCMSSiteId;
    }

    @Provides
    @Singleton
    public AppCMSSDK providesAppCMSSDK(AppCMSMainUICall appCMSMainUICall,
                                       AppCMSFilmRecordsCall appCMSFilmRecordsCall) {
        return new AppCMSSDK(context,
                appCMSBaseURL,
                appCMSSiteId,
                appCMSMainUICall,
                appCMSFilmRecordsCall);
    }
}
