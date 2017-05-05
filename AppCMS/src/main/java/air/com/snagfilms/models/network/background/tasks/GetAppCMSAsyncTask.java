package air.com.snagfilms.models.network.background.tasks;

import android.os.AsyncTask;

import java.io.IOException;

import air.com.snagfilms.models.network.rest.AppCMSCall;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/4/17.
 */

public class GetAppCMSAsyncTask<R> extends AsyncTask<String, Integer, R> {
    private final AppCMSCall<R> call;
    private final Action1<R> readyAction;

    public GetAppCMSAsyncTask(AppCMSCall<R> call, Action1<R> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected R doInBackground(String... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0]);
            } catch (IOException e) {

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(R result) {
        Observable.just(result).subscribe(readyAction);
    }
}
