package com.viewlift.models.network.background.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.network.rest.AppCMSPageAPICall;

import java.io.IOException;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSAPIAsyncTask extends AsyncTask<GetAppCMSAPIAsyncTask.Params, Integer, AppCMSPageAPI> {
    private static final String TAG = "GetAppCMSAPIAsyncTask";

    private final AppCMSPageAPICall call;
    private final Action1<AppCMSPageAPI> readyAction;

    public static class Params {
        Context context;
        String baseUrl;
        String endpoint;
        String siteId;
        boolean usePageIdQueryParam;
        String pageId;
        public static class Builder {
            private Params params;
            public Builder() {
                params = new Params();
            }
            public Builder context(Context context) {
                params.context = context;
                return this;
            }
            public Builder baseUrl(String baseUrl) {
                params.baseUrl = baseUrl;
                return this;
            }
            public Builder endpoint(String endpoint) {
                params.endpoint = endpoint;
                return this;
            }
            public Builder siteId(String siteId) {
                params.siteId = siteId;
                return this;
            }
            public Builder usePageIdQueryParam(boolean usePageIdQueryParam) {
                params.usePageIdQueryParam = usePageIdQueryParam;
                return this;
            }
            public Builder pageId(String pageId) {
                params.pageId = pageId;
                return this;
            }
            public Params build() {
                return params;
            }
        }
    }

    public GetAppCMSAPIAsyncTask(AppCMSPageAPICall call, Action1<AppCMSPageAPI> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected AppCMSPageAPI doInBackground(GetAppCMSAPIAsyncTask.Params... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0].context,
                        params[0].baseUrl,
                        params[0].endpoint,
                        params[0].siteId,
                        params[0].usePageIdQueryParam,
                        params[0].pageId);
            } catch (IOException e) {
                Log.e(TAG, "Error retrieving page API data: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(AppCMSPageAPI result) {
        Observable.just(result).subscribe(readyAction);
    }
}
