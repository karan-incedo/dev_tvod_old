package com.viewlift.models.network.modules;

import com.viewlift.models.network.rest.AppCMSSearchCall;
import com.viewlift.models.network.rest.AppCMSSearchRest;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by viewlift on 6/12/17.
 */

@Module(includes = {AppCMSSiteModule.class})
public class AppCMSSearchModule {
    @Provides
    @Singleton
    public AppCMSSearchRest providesAppCMSSearchRest(Retrofit retrofit) {
        return retrofit.create(AppCMSSearchRest.class);
    }

    @Provides
    @Singleton
    public AppCMSSearchCall providesAppCMSSearchCall(AppCMSSearchRest appCMSSearchRest) {
        return new AppCMSSearchCall(appCMSSearchRest);
    }
}
