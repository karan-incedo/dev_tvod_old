package com.viewlift.models.network.background.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.viewlift.models.data.appcms.api.AppCMSVideoDetail;
import com.viewlift.models.network.rest.AppCMSVideoDetailCall;

import java.io.IOException;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by anas.azeem on 7/12/2017.
 * Owned by ViewLift, NYC
 */
public class GetAppCMSVideoDetailAsyncTask extends AsyncTask<GetAppCMSVideoDetailAsyncTask.Params, Integer, AppCMSVideoDetail> {
    private static final String TAG = "VideoDetailAsyncTask";

    private final AppCMSVideoDetailCall call;
    private final Action1<AppCMSVideoDetail> readyAction;

    public static class Params {
        String url;
        boolean loadFromFile;
        public static class Builder {
            private Params params;
            public Builder() {
                this.params = new Params();
            }
            public Builder url(String url) {
                params.url = url;
                return this;
            }
            public Builder loadFromFile(boolean loadFromFile) {
                params.loadFromFile = loadFromFile;
                return this;
            }
            public Params build() {
                return params;
            }
        }
    }

    public GetAppCMSVideoDetailAsyncTask(AppCMSVideoDetailCall call,
                                           Action1<AppCMSVideoDetail> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected AppCMSVideoDetail doInBackground(Params... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0].url);
            } catch (IOException e) {
                Log.e(TAG, "Could not retrieve Video Detail data - " + params[0] + ": " + e.toString());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(AppCMSVideoDetail appCMSVideoDetail) {
        Observable.just(appCMSVideoDetail).subscribe(readyAction);
    }
}