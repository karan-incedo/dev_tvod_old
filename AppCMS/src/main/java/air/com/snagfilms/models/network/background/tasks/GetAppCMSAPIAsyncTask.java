package air.com.snagfilms.models.network.background.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import air.com.snagfilms.models.data.appcms.api.AppCMSPageAPI;
import air.com.snagfilms.models.network.rest.AppCMSPageAPICall;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSAPIAsyncTask extends AsyncTask<GetAppCMSAPIAsyncTask.Params, Integer, AppCMSPageAPI> {
    private final AppCMSPageAPICall call;
    private final Action1<AppCMSPageAPI> readyAction;

    public static class Params {
        Context context;
        String url;
        public static class Builder {
            private Params params;
            public Builder() {
                params = new Params();
            }
            public Builder context(Context context) {
                params.context = context;
                return this;
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

    public GetAppCMSAPIAsyncTask(AppCMSPageAPICall call, Action1<AppCMSPageAPI> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected AppCMSPageAPI doInBackground(GetAppCMSAPIAsyncTask.Params... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0].context, params[0].url);
            } catch (IOException e) {

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(AppCMSPageAPI jsonElement) {
        Observable.just(jsonElement).subscribe(readyAction);
    }
}
