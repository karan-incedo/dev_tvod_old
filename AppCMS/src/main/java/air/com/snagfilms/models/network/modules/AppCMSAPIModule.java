package air.com.snagfilms.models.network.modules;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import air.com.snagfilms.models.data.appcms.AppCMSKeyType;
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
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/4/17.
 */

@Module
public class AppCMSAPIModule {
    private final String baseUrl;
    private final File storageDirectory;
    private final Map<AppCMSKeyType, String> jsonValueKeyMap;

    public AppCMSAPIModule(String baseUrl, File storageDirectory, Context context) {
        this.baseUrl = baseUrl;
        this.storageDirectory = storageDirectory;

        this.jsonValueKeyMap = new HashMap<>();
        this.jsonValueKeyMap.put(AppCMSKeyType.MAIN_VERSION_KEY,
                context.getString(R.string.app_cms_main_version_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.MAIN_OLD_VERSION_KEY,
                context.getString(R.string.app_cms_main_old_version_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.MAIN_ANDROID_KEY,
                context.getString(R.string.app_cms_main_android_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.ANDROID_SPLASH_SCREEN_KEY,
                context.getString(R.string.app_cms_android_splash_screen_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.PAGE_BUTTON_KEY,
                context.getString(R.string.app_cms_page_button_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.PAGE_LABEL_KEY,
                context.getString(R.string.app_cms_page_label_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.PAGE_COLLECTIONGRID_KEY,
                context.getString(R.string.app_cms_page_collection_grid_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.PAGE_IMAGE_KEY,
                context.getString(R.string.app_cms_page_image_key));
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
        return new AppCMSMainCall(appCMSAPI,
                gson,
                storageDirectory,
                jsonValueKeyMap.get(AppCMSKeyType.MAIN_VERSION_KEY),
                jsonValueKeyMap.get(AppCMSKeyType.MAIN_OLD_VERSION_KEY));
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

    @Provides
    @Singleton
    public Map<AppCMSKeyType, String> providesJsonValueKeyMap() {
        return jsonValueKeyMap;
    }
}
