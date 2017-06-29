package com.viewlift.models.network.modules;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.viewlift.models.network.rest.AppCMSWatchlistCall;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppCMSWatchlistUrlModule {
    private final String baseUrl;
    private final String siteName;
    private final AppCMSWatchlistCall appCMSWatchlistCall;

    public AppCMSWatchlistUrlModule(String baseUrl, String siteName,
                                    AppCMSWatchlistCall appCMSWatchlistCall) {
        this.baseUrl = baseUrl;
        this.siteName = siteName;
        this.appCMSWatchlistCall = appCMSWatchlistCall;
    }

    @Provides
    @Singleton
    public AppCMSWatchlistUrlData providesWatchlistInitializer() {
        return new AppCMSWatchlistUrlData(baseUrl, siteName);
    }

    @Provides
    @Singleton
    public AppCMSWatchlistCall providesAppCMSWatchlistCall() {
        return appCMSWatchlistCall;
    }
}
