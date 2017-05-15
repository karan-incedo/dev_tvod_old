package air.com.snagfilms.models.network.background.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;

import air.com.snagfilms.models.data.appcms.main.Main;
import air.com.snagfilms.models.network.rest.AppCMSMainCall;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSMainAsyncTask extends AsyncTask<Uri, Integer, Main> {
    private final AppCMSMainCall call;
    private final Action1<Main> readyAction;

    public GetAppCMSMainAsyncTask(AppCMSMainCall call, Action1<Main> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected Main doInBackground(Uri... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0]);
            } catch (IOException e) {

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Main main) {
        Observable.just(main).subscribe(readyAction);
    }
}
