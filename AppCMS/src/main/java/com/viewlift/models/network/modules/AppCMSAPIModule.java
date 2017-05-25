package com.viewlift.models.network.modules;

import com.google.gson.Gson;
import com.viewlift.models.network.rest.AppCMSPageAPICall;
import com.viewlift.models.network.rest.AppCMSPageAPIRest;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by viewlift on 5/9/17.
 */

@Module(includes={AppCMSUIModule.class})
public class AppCMSAPIModule {
    private final String apiKey;

    public AppCMSAPIModule(String apiKey) {
        this.apiKey = apiKey;
    }

    @Provides
    @Singleton
    public AppCMSPageAPIRest providesAppCMSPageAPIRest(Retrofit retrofit) {
        return retrofit.create(AppCMSPageAPIRest.class);
    }

    @Provides
    @Singleton
    public AppCMSPageAPICall providesAppCMSPageAPICall(AppCMSPageAPIRest appCMSPageAPI,
                                                       Gson gson,
                                                       File storageDirectory) {
        return new AppCMSPageAPICall(appCMSPageAPI, apiKey, gson, storageDirectory);
    }
}
