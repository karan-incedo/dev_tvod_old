package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.ui.authentication.UserIdentity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 7/6/17.
 */

public class AppCMSUserIdentityCall {
    private final AppCMSUserIdentityRest appCMSUserIdentityRest;
    private final Map<String, String> authHeaders;

    @Inject
    public AppCMSUserIdentityCall(AppCMSUserIdentityRest appCMSUserIdentityRest) {
        this.appCMSUserIdentityRest = appCMSUserIdentityRest;
        this.authHeaders = new HashMap<>();
    }

    public void callGet(String url, String authToken, final Action1<UserIdentity> userIdentityAction) {
        authHeaders.put("Authorization", authToken);
        appCMSUserIdentityRest.get(url, authHeaders).enqueue(new Callback<UserIdentity>() {
            @Override
            public void onResponse(Call<UserIdentity> call, Response<UserIdentity> response) {
                Observable.just(response.body()).subscribe(userIdentityAction);
            }

            @Override
            public void onFailure(Call<UserIdentity> call, Throwable t) {
                Observable.just((UserIdentity) null).subscribe(userIdentityAction);
            }
        });
    }

    public void callPost(String url,
                         String authToken,
                         UserIdentity userIdentity,
                         final Action1<UserIdentity> userIdentityAction) {
        authHeaders.put("Authorization", authToken);
        appCMSUserIdentityRest.post(url, authHeaders, userIdentity).enqueue(new Callback<UserIdentity>() {
            @Override
            public void onResponse(Call<UserIdentity> call, Response<UserIdentity> response) {
                Observable.just(response.body()).subscribe(userIdentityAction);
            }

            @Override
            public void onFailure(Call<UserIdentity> call, Throwable t) {
                Observable.just((UserIdentity) null).subscribe(userIdentityAction);
            }
        });
    }
}
