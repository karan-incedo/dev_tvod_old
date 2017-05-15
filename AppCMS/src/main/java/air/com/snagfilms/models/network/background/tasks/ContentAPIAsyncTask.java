package air.com.snagfilms.models.network.background.tasks;

import android.os.AsyncTask;

import com.google.gson.JsonElement;

import java.io.IOException;

import air.com.snagfilms.models.network.rest.ContentAPICall;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/9/17.
 */

public class ContentAPIAsyncTask extends AsyncTask<String, Integer, JsonElement> {
    private final ContentAPICall call;
    private final Action1<JsonElement> readyAction;

    public ContentAPIAsyncTask(ContentAPICall call, Action1<JsonElement> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected JsonElement doInBackground(String... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0]);
            } catch (IOException e) {

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JsonElement jsonElement) {
        Observable.just(jsonElement).subscribe(readyAction);
    }
}
