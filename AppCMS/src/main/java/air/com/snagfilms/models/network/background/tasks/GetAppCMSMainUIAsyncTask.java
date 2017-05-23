package air.com.snagfilms.models.network.background.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;

import java.io.IOException;

import air.com.snagfilms.models.network.rest.AppCMSMainUICall;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSMainUIAsyncTask extends AsyncTask<String, Integer, JsonElement> {
    private static final String TAG = "";

    private final Context context;
    private final AppCMSMainUICall call;
    private final Action1<JsonElement> readyAction;

    public GetAppCMSMainUIAsyncTask(Context context,
                                    AppCMSMainUICall call,
                                    Action1<JsonElement> readyAction) {
        this.context = context;
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected JsonElement doInBackground(String... params) {
        if (params.length > 0) {
            try {
                return call.call(context, params[0]);
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
