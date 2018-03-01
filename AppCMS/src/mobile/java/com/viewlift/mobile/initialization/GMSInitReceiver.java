package com.viewlift.mobile.initialization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.iid.InstanceID;

/**
 * Created by viewlift on 2/28/18.
 */

public class GMSInitReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getStringExtra("init_action");
            switch (action) {
                case "init":
                    sendGMSInstanceId(context.getApplicationContext());
                    break;
                default:
            }
        }
    }

    private void sendGMSInstanceId(Context context) {
        String instanceId = InstanceID.getInstance(context).getId();
        Intent intent = new Intent("receive_gms_instance_id");
        intent.putExtra("gms_instance_id", instanceId);
        context.sendBroadcast(intent);
    }
}
