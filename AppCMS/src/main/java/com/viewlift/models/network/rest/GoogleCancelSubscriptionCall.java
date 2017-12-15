package com.viewlift.models.network.rest;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by viewlift on 7/25/17.
 */

public class GoogleCancelSubscriptionCall {
    private static final String TAG = "GoogleCancelSubs";

    private final GoogleCancelSubscriptionRest googleCancelSubscriptionRest;

    private Map<String, String> authHeaders;

    @Inject
    public GoogleCancelSubscriptionCall(GoogleCancelSubscriptionRest googleCancelSubscriptionRest) {
        this.googleCancelSubscriptionRest = googleCancelSubscriptionRest;
        this.authHeaders = new HashMap<>();
    }

    public void cancelSubscription(String url, String accessToken) {
        //Log.d(TAG, "Cancelling subscription: " + url);
        authHeaders.clear();
        authHeaders.put("Authorization", accessToken);
        googleCancelSubscriptionRest.sendCancelSubscription(url, authHeaders);
    }
}
