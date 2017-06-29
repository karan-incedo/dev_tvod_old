package com.viewlift.models.network.modules;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.google.gson.Gson;
import com.viewlift.models.network.rest.AppCMSWatchlistCall;
import com.viewlift.models.network.rest.AppCMSWatchlistRest;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module(includes = {AppCMSSiteModule.class})
public class AppCMSWatchlistModule {

    @Provides
    @Singleton
    public AppCMSWatchlistRest providesAppCMSWatchlistRest(Retrofit retrofit) {
        return retrofit.create(AppCMSWatchlistRest.class);
    }

    @Provides
    @Singleton
    public AppCMSWatchlistCall providesAppCMSWatchlistCall(AppCMSWatchlistRest appCMSWatchlistRest,
                                                           Gson gson) {
        return new AppCMSWatchlistCall(appCMSWatchlistRest, gson);
    }
}
