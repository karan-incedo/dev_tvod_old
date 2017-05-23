package air.com.snagfilms.models.network.background.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;

import air.com.snagfilms.models.data.appcms.android.Android;
import air.com.snagfilms.models.network.rest.AppCMSAndroidUICall;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/4/17.
 */

public class GetAppCMSAndroidUIAsyncTask extends AsyncTask<GetAppCMSAndroidUIAsyncTask.RunOptions, Integer, Android> {
    private final AppCMSAndroidUICall call;
    private final Action1<Android> readyAction;

    public static class RunOptions {
        public Uri dataUri;
        public boolean loadFromFile;
    }

    public GetAppCMSAndroidUIAsyncTask(AppCMSAndroidUICall call, Action1<Android> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected Android doInBackground(RunOptions... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0].dataUri, params[0].loadFromFile);
            } catch (IOException e) {

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Android result) {
        Observable.just(result).subscribe(readyAction);
    }
}
