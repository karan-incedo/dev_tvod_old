package com.viewlift.models.network.background.tasks;

import android.os.AsyncTask;

import com.viewlift.models.data.appcms.ui.authentication.RefreshIdentityResponse;
import com.viewlift.models.network.rest.AppCMSRefreshIdentityCall;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 7/5/17.
 */

public class GetAppCMSRefreshIdentityAsyncTask extends AsyncTask<GetAppCMSRefreshIdentityAsyncTask.Params, Integer, RefreshIdentityResponse> {
    private final AppCMSRefreshIdentityCall call;
    private final Action1<RefreshIdentityResponse> readyAction;

    public static class Params {
        String url;
        public static class Builder {
            private Params params;
            public Builder() {
                this.params = new Params();
            }
            public Builder url(String url) {
                params.url = url;
                return this;
            }
            public Params build() {
                return params;
            }
        }
    }

    public GetAppCMSRefreshIdentityAsyncTask(AppCMSRefreshIdentityCall call,
                                             Action1<RefreshIdentityResponse> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected RefreshIdentityResponse doInBackground(Params... params) {
        if (params.length > 0) {
            return call.call(params[0].url);
        }
        return null;
    }

    @Override
    protected void onPostExecute(RefreshIdentityResponse refreshIdentityResponse) {
        Observable.just(refreshIdentityResponse).subscribe(readyAction);
    }
}
