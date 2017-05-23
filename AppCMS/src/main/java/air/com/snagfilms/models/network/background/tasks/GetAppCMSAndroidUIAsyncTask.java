package air.com.snagfilms.models.network.background.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;

import air.com.snagfilms.models.data.appcms.ui.android.AppCMSAndroidUI;
import air.com.snagfilms.models.network.rest.AppCMSAndroidUICall;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/4/17.
 */

public class GetAppCMSAndroidUIAsyncTask extends AsyncTask<GetAppCMSAndroidUIAsyncTask.Params, Integer, AppCMSAndroidUI> {
    private final AppCMSAndroidUICall call;
    private final Action1<AppCMSAndroidUI> readyAction;

    public static class Params {
        String url;
        boolean loadFromFile;
        public static class Builder {
            private Params params;
            public Builder() {
                params = new Params();
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

    public GetAppCMSAndroidUIAsyncTask(AppCMSAndroidUICall call, Action1<AppCMSAndroidUI> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected AppCMSAndroidUI doInBackground(Params... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0].url, params[0].loadFromFile);
            } catch (IOException e) {

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(AppCMSAndroidUI result) {
        Observable.just(result).subscribe(readyAction);
    }
}
