package air.com.snagfilms.models.network.rest;

import android.content.Context;
import android.support.annotation.WorkerThread;

import java.io.IOException;

import javax.inject.Inject;

import air.com.snagfilms.models.data.appcms.api.AppCMSPageAPI;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/9/17.
 */

public class AppCMSPageAPICall {
    private final AppCMSPageAPIRest appCMSPageAPIRest;
    private final String apiKey;

    @Inject
    public AppCMSPageAPICall(AppCMSPageAPIRest appCMSPageAPIRest, String apiKey) {
        this.appCMSPageAPIRest = appCMSPageAPIRest;
        this.apiKey = apiKey;
    }

    @WorkerThread
    public AppCMSPageAPI call(Context context, String url) throws IOException {
        String urlWithContent = context.getString(R.string.app_cms_page_api_url, url);
        return appCMSPageAPIRest.get(apiKey, urlWithContent).execute().body();
    }
}
