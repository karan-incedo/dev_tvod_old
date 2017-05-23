package air.com.snagfilms.models.network.background.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;

import air.com.snagfilms.models.data.appcms.page.Page;
import air.com.snagfilms.models.network.rest.AppCMSPageUICall;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSPageUIAsyncTask extends AsyncTask<GetAppCMSPageUIAsyncTask.RunOptions, Integer, Page> {
    private final AppCMSPageUICall call;
    private final Action1<Page> readyAction;

    public static class RunOptions {
        public Uri dataUri;
        public boolean loadFromFile;
    }

    public GetAppCMSPageUIAsyncTask(AppCMSPageUICall call, Action1<Page> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected Page doInBackground(GetAppCMSPageUIAsyncTask.RunOptions... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0].dataUri, params[0].loadFromFile);
            } catch (IOException e) {

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Page page) {
        Observable.just(page).subscribe(readyAction);
    }
}
