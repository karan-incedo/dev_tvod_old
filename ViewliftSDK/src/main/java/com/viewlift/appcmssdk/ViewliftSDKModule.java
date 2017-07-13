package com.viewlift.appcmssdk;

import android.content.Context;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by viewlift on 7/13/17.
 */

@Module
public class ViewliftSDKModule {
    private Context context;

    private final long defaultConnectionTimeout;
    private final String baseUrl;

    public ViewliftSDKModule(Context context, String baseUrl) {
        this.context = context;
        this.baseUrl = baseUrl;
        this.defaultConnectionTimeout =
                context.getResources().getInteger(R.integer.default_connection_timeout_msec);
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public OkHttpClient providesOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(defaultConnectionTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(defaultConnectionTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(defaultConnectionTimeout, TimeUnit.MILLISECONDS)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofit(OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    public ViewliftSDK providesAppCMSSDK() {
        return new ViewliftSDK(context);
    }
}
