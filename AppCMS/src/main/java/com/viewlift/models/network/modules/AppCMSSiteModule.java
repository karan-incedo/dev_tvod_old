package com.viewlift.models.network.modules;

import com.google.gson.Gson;
import com.viewlift.models.network.rest.AppCMSSiteCall;
import com.viewlift.models.network.rest.AppCMSSiteRest;
import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by viewlift on 6/15/17.
 */

@Module(includes={AppCMSUIModule.class})
public class AppCMSSiteModule {
    @Provides
    @Singleton
    public AppCMSSiteRest providesAppCMSSiteRest(Retrofit retrofit) {
        return retrofit.create(AppCMSSiteRest.class);
    }

    @Provides
    @Singleton
    public AppCMSSiteCall providesAppCMSSiteCall(AppCMSSiteRest appCMSSiteRest,
                                                 Gson gson,
                                                 File storageDirectory) {
        return new AppCMSSiteCall(appCMSSiteRest, gson, storageDirectory);
    }
}
