package com.viewlift.models.network.background.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.viewlift.models.data.appcms.api.AppCMSStreamingInfo;
import com.viewlift.models.network.rest.AppCMSStreamingInfoCall;

import java.io.IOException;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 6/26/17.
 */

public class GetAppCMSStreamingInfoAsyncTask extends AsyncTask<GetAppCMSStreamingInfoAsyncTask.Params, Integer, AppCMSStreamingInfo> {
    private static final String TAG = "StreamingInfoTask";

    private final AppCMSStreamingInfoCall call;
    private final Action1<AppCMSStreamingInfo> readyAction;

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

    public GetAppCMSStreamingInfoAsyncTask(AppCMSStreamingInfoCall call,
                                           Action1<AppCMSStreamingInfo> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected AppCMSStreamingInfo doInBackground(Params... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0].url);
            } catch (IOException e) {
                Log.e(TAG, "Could not retrieve Streaming Info data - " + params[0] + ": " + e.toString());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(AppCMSStreamingInfo streamingInfo) {
        Observable.just(streamingInfo).subscribe(readyAction);
    }
}
