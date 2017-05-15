package air.com.snagfilms.models.network.rest;

import android.support.annotation.WorkerThread;

import com.google.gson.JsonElement;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by viewlift on 5/9/17.
 */

public class ContentAPICall {
    private ContentAPI contentAPI;

    @Inject
    public ContentAPICall(ContentAPI contentAPI) {
        this.contentAPI = contentAPI;
    }

    @WorkerThread
    public JsonElement call(String url) throws IOException {
        return contentAPI.get(url).execute().body();
    }
}
