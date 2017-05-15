package air.com.snagfilms.models.network.modules;

import com.google.gson.Gson;

import java.io.File;

import javax.inject.Singleton;

import air.com.snagfilms.models.network.rest.AppCMSAndroidAPI;
import air.com.snagfilms.models.network.rest.AppCMSAndroidCall;
import air.com.snagfilms.models.network.rest.AppCMSMainAPI;
import air.com.snagfilms.models.network.rest.AppCMSMainCall;
import air.com.snagfilms.models.network.rest.AppCMSPageAPI;
import air.com.snagfilms.models.network.rest.AppCMSPageCall;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by viewlift on 5/4/17.
 */

@Module
public class AppCMSAPIModule {
    private final String baseUrl;
    private final File storageDirectory;

    public AppCMSAPIModule(String baseUrl, File storageDirectory) {
        this.baseUrl = baseUrl;
        this.storageDirectory = storageDirectory;
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofit(Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .build();
    }

    @Provides
    @Singleton
    public AppCMSMainAPI providesAppCMSMainAPI(Retrofit retrofit) {
        return retrofit.create(AppCMSMainAPI.class);
    }

    @Provides
    @Singleton
    public AppCMSAndroidAPI providesAppCMSAndroidAPI(Retrofit retrofit) {
        return retrofit.create(AppCMSAndroidAPI.class);
    }

    @Provides
    @Singleton
    public AppCMSPageAPI providesAppCMSPageAPI(Retrofit retrofit) {
        return retrofit.create(AppCMSPageAPI.class);
    }

    @Provides
    @Singleton
    public AppCMSMainCall providesAppCMSMainCall(AppCMSMainAPI appCMSAPI, Gson gson) {
        return new AppCMSMainCall(appCMSAPI, gson, storageDirectory);
    }

    @Provides
    @Singleton
    public AppCMSAndroidCall providesAppCMSAndroidCall(AppCMSAndroidAPI appCMSAPI, Gson gson) {
        return new AppCMSAndroidCall(appCMSAPI, gson, storageDirectory);
    }

    @Provides
    @Singleton
    public AppCMSPageCall providesAppCMSPageCall(AppCMSPageAPI appCMSAPI, Gson gson) {
        return new AppCMSPageCall(appCMSAPI, gson, storageDirectory);
    }
}
