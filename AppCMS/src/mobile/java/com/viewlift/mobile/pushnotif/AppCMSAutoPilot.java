package com.viewlift.mobile.pushnotif;

import android.support.annotation.NonNull;

import com.urbanairship.Autopilot;
import com.urbanairship.UAirship;

public class AppCMSAutoPilot extends Autopilot {
    private String prodAppKey;
    private String prodSecret;
    private String devAppKey;
    private String devSecret;
    private String senderId;

    @Override
    public void onAirshipReady(@NonNull UAirship airship) {
        airship.getPushManager().setUserNotificationsEnabled(true);
    }
}
