package com.viewlift.pushnotif;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Autopilot;
import com.urbanairship.UAirship;
import com.viewlift.BuildConfig;
import com.viewlift.R;

public class AppCMSAutoPilot extends Autopilot {
    @Override
    public void onAirshipReady(@NonNull UAirship airship) {
        airship.getPushManager().setUserNotificationsEnabled(true);
    }
}
