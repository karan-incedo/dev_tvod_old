package air.com.snagfilms.models.network.modules;

import com.google.gson.Gson;

import javax.inject.Singleton;

import air.com.snagfilms.models.data.appcms.android.Android;
import air.com.snagfilms.models.data.appcms.android.Page;
import air.com.snagfilms.models.data.appcms.main.Main;
import air.com.snagfilms.models.network.rest.AppCMSAndroidAPI;
import air.com.snagfilms.models.network.rest.AppCMSMainAPI;
import air.com.snagfilms.models.network.rest.AppCMSCall;
import air.com.snagfilms.models.network.rest.AppCMSAPI;
import air.com.snagfilms.models.network.rest.AppCMSPageAPI;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by viewlift on 5/4/17.
 */

@Module
public class AppCMSAPIModule {
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
    public AppCMSCall<Main> providesAppCMSMainCall(AppCMSMainAPI appCMSMainAPI) {
        return new AppCMSCall<>(appCMSMainAPI);
    }

    @Provides
    @Singleton
    public AppCMSCall<Android> providesAppCMSAndroidCall(AppCMSAndroidAPI appCMSAndroidAPI) {
        return new AppCMSCall<>(appCMSAndroidAPI);
    }

    @Provides
    @Singleton
    public AppCMSCall<Page> providesAppCMSPageCall(AppCMSPageAPI appCMSPageAPI) {
        return new AppCMSCall<>(appCMSPageAPI);
    }
}
