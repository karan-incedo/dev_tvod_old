package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/19/17.
 */

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.SubscriptionRequest;
import com.viewlift.models.data.appcms.subscriptions.AppCMSSubscriptionPlanResult;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSSubscriptionPlanCall {

    private static final String TAG = "SubscriptionPlanCall";
    private final AppCMSSubscriptionPlanRest appCMSSubscriptionPlanRest;
    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSSubscriptionPlanCall(AppCMSSubscriptionPlanRest appCMSSubscriptionPlanRest,
                                      Gson gson) {
        this.appCMSSubscriptionPlanRest = appCMSSubscriptionPlanRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url, int subscriptionCallType, SubscriptionRequest request,
                     final Action1<List<AppCMSSubscriptionPlanResult>> planResultAction1,
                     final Action1<AppCMSSubscriptionPlanResult> resultAction1)
            throws IOException {

        switch (subscriptionCallType) {

            case R.string.app_cms_subscription_plan_list_key:
                appCMSSubscriptionPlanRest.getPlanList(url)
                        .enqueue(new Callback<List<AppCMSSubscriptionPlanResult>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<AppCMSSubscriptionPlanResult>> call,
                                                   @NonNull Response<List<AppCMSSubscriptionPlanResult>> response) {
                                Observable.just(response.body()).subscribe(planResultAction1);
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<AppCMSSubscriptionPlanResult>> call,
                                                  @NonNull Throwable t) {
                                Log.e(TAG, "onFailure: " + t.getMessage());
                            }
                        });
                break;

            case R.string.app_cms_subscription_subscribed_plan_key:
                appCMSSubscriptionPlanRest.getSubscribedPlan(url).enqueue(new Callback<AppCMSSubscriptionPlanResult>() {
                    @Override
                    public void onResponse(Call<AppCMSSubscriptionPlanResult> call, Response<AppCMSSubscriptionPlanResult> response) {
                        Observable.just(response.body()).subscribe(resultAction1);
                    }

                    @Override
                    public void onFailure(Call<AppCMSSubscriptionPlanResult> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
                break;

            case R.string.app_cms_subscription_plan_create_key:
                appCMSSubscriptionPlanRest.createPlan(url, request)
                        .enqueue(new Callback<AppCMSSubscriptionPlanResult>() {
                            @Override
                            public void onResponse(@NonNull Call<AppCMSSubscriptionPlanResult> call,
                                                   @NonNull Response<AppCMSSubscriptionPlanResult> response) {
                                Observable.just(response.body()).subscribe(resultAction1);
                            }

                            @Override
                            public void onFailure(@NonNull Call<AppCMSSubscriptionPlanResult> call,
                                                  @NonNull Throwable t) {
                                Log.e(TAG, "onFailure: " + t.getMessage());
                            }
                        });
                break;

            case R.string.app_cms_subscription_plan_update_key:
                appCMSSubscriptionPlanRest.updatePlan(url, request)
                        .enqueue(new Callback<AppCMSSubscriptionPlanResult>() {
                            @Override
                            public void onResponse(@NonNull Call<AppCMSSubscriptionPlanResult> call,
                                                   @NonNull Response<AppCMSSubscriptionPlanResult> response) {
                                Observable.just(response.body()).subscribe(resultAction1);
                            }

                            @Override
                            public void onFailure(@NonNull Call<AppCMSSubscriptionPlanResult> call,
                                                  @NonNull Throwable t) {
                                Log.e(TAG, "onFailure: " + t.getMessage());
                            }
                        });
                break;

            case R.string.app_cms_subscription_plan_cancel_key:
                appCMSSubscriptionPlanRest.cancelPlan(url, request)
                        .enqueue(new Callback<AppCMSSubscriptionPlanResult>() {
                            @Override
                            public void onResponse(@NonNull Call<AppCMSSubscriptionPlanResult> call,
                                                   @NonNull Response<AppCMSSubscriptionPlanResult> response) {
                                Observable.just(response.body()).subscribe(resultAction1);
                            }

                            @Override
                            public void onFailure(@NonNull Call<AppCMSSubscriptionPlanResult> call,
                                                  @NonNull Throwable t) {
                                Log.e(TAG, "onFailure: " + t.getMessage());
                            }
                        });
                break;

            default:
                throw new RuntimeException("Invalid SubscriptionCallType: " + subscriptionCallType);
        }
    }
}
