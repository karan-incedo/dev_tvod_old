package com.viewlift.pushnotif;

import android.support.annotation.NonNull;

import com.urbanairship.Autopilot;
import com.urbanairship.UAirship;

public class AppCMSAutoPilot extends Autopilot {
    @Override
    public void onAirshipReady(@NonNull UAirship airship) {
        airship.getPushManager().setUserNotificationsEnabled(true);
    }
}
