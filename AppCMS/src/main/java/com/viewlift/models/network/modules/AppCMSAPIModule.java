package com.viewlift.models.network.modules;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viewlift.models.network.rest.AppCMSFloodLightRest;
import com.viewlift.models.network.rest.AppCMSPageAPICall;
import com.viewlift.models.network.rest.AppCMSPageAPIRest;
import com.viewlift.models.network.rest.AppCMSStreamingInfoCall;
import com.viewlift.models.network.rest.AppCMSStreamingInfoRest;
import com.viewlift.models.network.rest.AppCMSVideoDetailCall;
import com.viewlift.models.network.rest.AppCMSVideoDetailRest;
import com.viewlift.models.network.rest.UANamedUserEventCall;
import com.viewlift.models.network.rest.UANamedUserEventRest;
import com.viewlift.presenters.UrbanAirshipEventPresenter;
import com.viewlift.stag.generated.Stag;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.viewlift.R;

/**
 * Created by viewlift on 5/9/17.
 */

@Module
public class AppCMSAPIModule {
    private final String baseUrl;
    private final String apiKey;
    private final File storageDirectory;
    private final long defaultConnectionTimeout;

    private final String loggedInStatusGroup;
    private final String loggedInStatusTag;
    private final String loggedOutStatusTag;
    private final String subscriptionStatusGroup;
    private final String subscribedTag;
    private final String subscriptionAboutToExpireTag;
    private final String subscriptionEndDateGroup;
    private final String subscriptionPlanGroup;
    private final String unsubscribedTag;
    private final int daysBeforeSubscriptionEndForNotification;

    public AppCMSAPIModule(Context context, String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.storageDirectory = context.getFilesDir();
        this.defaultConnectionTimeout =
                context.getResources().getInteger(R.integer.app_cms_default_connection_timeout_msec);

        this.loggedInStatusGroup =  context.getString(R.string.ua_user_logged_in_status_group);
        this.loggedInStatusTag = context.getString(R.string.ua_user_logged_in_tag);
        this.loggedOutStatusTag = context.getString(R.string.ua_user_logged_out_tag);
        this.subscriptionStatusGroup = context.getString(R.string.ua_user_subscription_status_group);
        this.subscribedTag = context.getString(R.string.ua_user_subscribed_tag);
        this.subscriptionAboutToExpireTag = context.getString(R.string.ua_user_subscription_about_expire_tag);
        this.unsubscribedTag = context.getString(R.string.ua_user_unsubscribed_tag);
        this.subscriptionEndDateGroup = "";
        this.subscriptionPlanGroup = "";
        this.daysBeforeSubscriptionEndForNotification = context.getResources().getInteger(R.integer.ua_days_before_subscription_end_notification_tag);
    }

    @Provides
    @Singleton
    public Gson providesGson() {
//        return new Gson();
        return new GsonBuilder().registerTypeAdapterFactory(new Stag.Factory())
                .create();
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofit(Gson gson) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(defaultConnectionTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(defaultConnectionTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(defaultConnectionTimeout, TimeUnit.MILLISECONDS)
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
    public AppCMSVideoDetailRest providesAppCMSVideoDetailRest(Retrofit retrofit) {
        return retrofit.create(AppCMSVideoDetailRest.class);
    }

    @Provides
    @Singleton
    public UANamedUserEventRest providesUANamedUserEventRest(Retrofit retrofit) {
        return retrofit.create(UANamedUserEventRest.class);
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

    @Provides
    @Singleton
    public AppCMSVideoDetailCall providesAppCMSVideoDetailCall(AppCMSVideoDetailRest appCMSVideoDetailRest){
        return new AppCMSVideoDetailCall(appCMSVideoDetailRest);
    }

    @Provides
    @Singleton
    public AppCMSFloodLightRest appCMSFloodLightRest(Retrofit retrofit) {
        return retrofit.create(AppCMSFloodLightRest.class);
    }

    public UANamedUserEventCall providesUANamedUserEventCall(UANamedUserEventRest uaNamedUserEventRest,
                                                             Gson gson) {
        return new UANamedUserEventCall(uaNamedUserEventRest, gson);
    }

    @Provides
    @Singleton
    public UrbanAirshipEventPresenter providesUrbanAirshipEventPresenter() {
        return new UrbanAirshipEventPresenter(loggedInStatusGroup,
                loggedInStatusTag,
                loggedOutStatusTag,
                subscriptionStatusGroup,
                subscribedTag,
                subscriptionAboutToExpireTag,
                unsubscribedTag,
                subscriptionEndDateGroup,
                subscriptionPlanGroup,
                daysBeforeSubscriptionEndForNotification);
    }
}
