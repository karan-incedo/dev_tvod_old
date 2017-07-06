package com.viewlift.models.network.background.tasks;

import android.os.AsyncTask;

import com.viewlift.models.data.appcms.ui.authentication.SignInResponse;
import com.viewlift.models.network.rest.AppCMSSignInCall;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 7/5/17.
 */

public class PostAppCMSLoginRequestAsyncTask extends AsyncTask<PostAppCMSLoginRequestAsyncTask.Params, Integer, SignInResponse> {
    private final AppCMSSignInCall call;
    private final Action1<SignInResponse> readyAction;

    public static class Params {
        String url;
        String email;
        String password;
        public static class Builder {
            private Params params;
            public Builder() {
                this.params = new Params();
            }
            public Builder url(String url) {
                params.url = url;
                return this;
            }
            public Builder email(String email) {
                params.email = email;
                return this;
            }
            public Builder password(String password) {
                params.password = password;
                return this;
            }
            public Params build() {
                return params;
            }
        }
    }

    public PostAppCMSLoginRequestAsyncTask(AppCMSSignInCall call,
                                           Action1<SignInResponse> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected SignInResponse doInBackground(Params... params) {
        if (params.length > 0) {
            return call.call(params[0].url, params[0].email, params[0].password);
        }
        return null;
    }

    @Override
    protected void onPostExecute(SignInResponse signInResponse) {
        Observable.just(signInResponse).subscribe(readyAction);
    }
}
