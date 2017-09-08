package com.viewlift.models.network.background.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.network.rest.AppCMSMainUICall;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSMainUIAsyncTask extends AsyncTask<GetAppCMSMainUIAsyncTask.Params, Integer, AppCMSMain> {
    private static final String TAG = "GetAppCMSMainAsyncTask";

    private final AppCMSMainUICall call;
    private final Action1<AppCMSMain> readyAction;

    public GetAppCMSMainUIAsyncTask(AppCMSMainUICall call,
                                    Action1<AppCMSMain> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected AppCMSMain doInBackground(GetAppCMSMainUIAsyncTask.Params... params) {
        if (params.length > 0) {
            return call.call(params[0].context, params[0].siteId, 0);
        }
        return null;
    }

    @Override
    protected void onPostExecute(AppCMSMain result) {
        Observable.just(result).subscribe(readyAction);
    }

    public static class Params {
        Context context;
        String siteId;

        public static class Builder {
            Params params;

            public Builder() {
                params = new Params();
            }

            public Builder context(Context context) {
                params.context = context;
                return this;
            }

            public Builder siteId(String siteId) {
                params.siteId = siteId;
                return this;
            }

            public Params build() {
                return params;
            }
        }
    }
}
