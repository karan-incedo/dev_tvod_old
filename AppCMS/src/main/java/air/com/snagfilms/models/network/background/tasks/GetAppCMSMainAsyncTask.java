package air.com.snagfilms.models.network.background.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;

import java.io.IOException;

import air.com.snagfilms.models.network.rest.AppCMSMainCall;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSMainAsyncTask extends AsyncTask<Uri, Integer, JsonElement> {
    private static final String TAG = "";

    private final AppCMSMainCall call;
    private final Action1<JsonElement> readyAction;

    public GetAppCMSMainAsyncTask(AppCMSMainCall call, Action1<JsonElement> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected JsonElement doInBackground(Uri... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0]);
            } catch (IOException e) {
                Log.e(TAG, "Could not retrieve data: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JsonElement main) {
        Observable.just(main).subscribe(readyAction);
    }
}
