package com.viewlift.models.network.modules;

import com.viewlift.models.network.rest.AppCMSSearchCall;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by viewlift on 6/12/17.
 */

@Module
public class AppCMSSearchUrlModule {
    private final String baseUrl;
    private final String siteName;
    private final String apiKey;
    private final AppCMSSearchCall appCMSSearchCall;

    public AppCMSSearchUrlModule(String baseUrl,
                                 String siteName,
                                 String apiKey,
                                 AppCMSSearchCall appCMSSearchCall) {
        this.baseUrl = baseUrl;
        this.siteName = siteName;
        this.apiKey = apiKey;
        this.appCMSSearchCall = appCMSSearchCall;
    }

    @Provides
    @Singleton
    public AppCMSSearchUrlData providesSearchInitializer() {
        return new AppCMSSearchUrlData(baseUrl, siteName, apiKey);
    }

    @Provides
    @Singleton
    public AppCMSSearchCall providesAppCMSSearchCall() {
        return appCMSSearchCall;
    }
}
