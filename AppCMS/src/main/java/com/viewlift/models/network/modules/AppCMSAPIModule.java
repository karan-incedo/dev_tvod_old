package com.viewlift.models.network.modules;

import android.content.Context;

import com.google.gson.Gson;
import com.viewlift.models.network.rest.AppCMSPageAPICall;
import com.viewlift.models.network.rest.AppCMSPageAPIRest;
import com.viewlift.models.network.rest.AppCMSStreamingInfoCall;
import com.viewlift.models.network.rest.AppCMSStreamingInfoRest;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by viewlift on 5/9/17.
 */

@Module
public class AppCMSAPIModule {
    private final String baseUrl;
    private final String apiKey;
    private final File storageDirectory;

    public AppCMSAPIModule(Context context, String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.storageDirectory = context.getFilesDir();;
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofit(Gson gson) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    public File providesStorageDirectory() {
        return storageDirectory;
    }

    @Provides
    @Singleton
    public AppCMSPageAPIRest providesAppCMSPageAPIRest(Retrofit retrofit) {
        return retrofit.create(AppCMSPageAPIRest.class);
    }

    @Provides
    @Singleton
    public AppCMSStreamingInfoRest providesAppCMSStreamingInfoRest(Retrofit retrofit) {
        return retrofit.create(AppCMSStreamingInfoRest.class);
    }

    @Provides
    @Singleton
    public AppCMSPageAPICall providesAppCMSPageAPICall(AppCMSPageAPIRest appCMSPageAPI,
                                                       Gson gson,
                                                       File storageDirectory) {
        return new AppCMSPageAPICall(appCMSPageAPI, apiKey, gson, storageDirectory);
    }

    @Provides
    @Singleton
    public AppCMSStreamingInfoCall providesAppCMSStreamingInfoCall(AppCMSStreamingInfoRest appCMSStreamingInfoRest) {
        return new AppCMSStreamingInfoCall(appCMSStreamingInfoRest);
    }
}
