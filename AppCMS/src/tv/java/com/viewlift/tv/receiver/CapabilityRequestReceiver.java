package com.viewlift.tv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.viewlift.tv.utility.Utils;

/**
 * Created by anas.azeem on 4/3/2018.
 * Owned by ViewLift, NYC
 */

public class CapabilityRequestReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CapabilityRequest", "broadcastCapabilities");

        Utils.broadcastCapabilities(context); //the method you use to broadcast your app's information to the launcher
    }
}