package com.viewlift.models.network.background.tasks;

import com.viewlift.models.data.urbanairship.UAAssociateNamedUserRequest;
import com.viewlift.models.data.urbanairship.UANamedUserRequest;
import com.viewlift.models.network.rest.UANamedUserEventCall;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by viewlift on 12/21/17.
 */

public class PostUANamedUserEventAsyncTask {
    private final UANamedUserEventCall uaNamedUserEventCall;

    public static class Params {
        String accessKey;
        String authKey;
        public static class Builder {
            Params params;

            public Builder() {
                params = new Params();
            }

            public Builder accessKey(String accessKey) {
                params.accessKey = accessKey;
                return this;
            }

            public Builder authKey(String authKey) {
                params.authKey = authKey;
                return this;
            }

            public Params build() {
                return params;
            }
        }
    }

    public PostUANamedUserEventAsyncTask(UANamedUserEventCall uaNamedUserEventCall) {
        this.uaNamedUserEventCall = uaNamedUserEventCall;
    }

    public void execute(Params params,
                        UANamedUserRequest uaNamedUserRequest) {
        Observable
                .fromCallable(() -> {
                    if (params != null) {
                        try {
                            return uaNamedUserEventCall.call(params.accessKey,
                                    params.authKey,
                                    uaNamedUserRequest);
                        } catch (Exception e) {
                            //Log.e(TAG, "DialogType retrieving page API data: " + e.getMessage());
                        }
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> Observable.empty())
                .subscribe((result) -> {});
    }

    public void execute(Params params,
                        UAAssociateNamedUserRequest uaAssociateNamedUserRequest,
                        boolean associate) {
        Observable
                .fromCallable(() -> {
                    if (params != null) {
                        try {
                            if (associate) {
                                return uaNamedUserEventCall.sendLoginAssociation(params.accessKey,
                                        params.authKey,
                                        uaAssociateNamedUserRequest);
                            } else {
                                return uaNamedUserEventCall.sendLogoutDisassociation(params.accessKey,
                                        params.authKey,
                                        uaAssociateNamedUserRequest);
                            }
                        } catch (Exception e) {
                            //Log.e(TAG, "DialogType retrieving page API data: " + e.getMessage());
                        }
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> Observable.empty())
                .subscribe((result) -> {});
    }
}
