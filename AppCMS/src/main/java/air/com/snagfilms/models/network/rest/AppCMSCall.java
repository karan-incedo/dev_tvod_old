package air.com.snagfilms.models.network.rest;

import android.support.annotation.WorkerThread;

import java.io.IOException;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSCall<R> {
    private AppCMSAPI<R> appCMSMainAPI;

    public AppCMSCall(AppCMSAPI<R> appCMSCallAPI) {
        this.appCMSMainAPI = appCMSCallAPI;
    }

    @WorkerThread
    public R call(String url) throws IOException {
        return appCMSMainAPI.get(url).execute().body();
    }
}
