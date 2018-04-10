package com.viewlift.models.network.background.tasks;

import android.os.AsyncTask;
import com.viewlift.models.data.appcms.subscribeForLatestNewsPojo.ResponsePojo;
import com.viewlift.models.network.rest.AppCMSSubscribeForLatestNewsCall;
import com.viewlift.presenters.AppCMSPresenter;

/**
 * Created by artanelezaj on 4/10/18.
 */

public class StartEmailSubscripctionAsyncTask extends AsyncTask<String, Void, ResponsePojo> {
    private AppCMSPresenter appCMSPresenter;
    private AppCMSSubscribeForLatestNewsCall appCMSSubscribeForLatestNewsCall;

    public StartEmailSubscripctionAsyncTask(AppCMSPresenter appCMSPresenter, AppCMSSubscribeForLatestNewsCall appCMSSubscribeForLatestNewsCall) {
        this.appCMSPresenter = appCMSPresenter;
        this.appCMSSubscribeForLatestNewsCall = appCMSSubscribeForLatestNewsCall;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ResponsePojo doInBackground(String... params) {
        return appCMSSubscribeForLatestNewsCall.call(params[0]);
    }

    @Override
    protected void onPostExecute(ResponsePojo result) {
        appCMSPresenter.emailSubscriptionResponse(result);
    }
}