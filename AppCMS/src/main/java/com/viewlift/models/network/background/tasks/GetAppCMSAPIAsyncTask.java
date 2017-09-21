package com.viewlift.models.network.background.tasks;

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
        String urlWithContent;
        String authToken;
        String pageId;
        public static class Builder {
            private Params params;
            public Builder() {
                params = new Params();
            }
            public Builder context() {
                return this;
            }
            public Builder urlWithContent(String urlWithContent) {
                params.urlWithContent = urlWithContent;
                return this;
            }
            public Builder pageId(String pageId) {
                params.pageId = pageId;
                return this;
            }
            public Builder authToken(String authToken) {
                params.authToken = authToken;
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
                return call.call(params[0].urlWithContent,
                        params[0].authToken,
                        params[0].pageId,
                        0);
            } catch (IOException e) {
                Log.e(TAG, "DialogType retrieving page API data: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(AppCMSPageAPI result) {
        Observable.just(result).subscribe(readyAction);
    }
}
