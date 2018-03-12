package com.viewlift.mobile.initialization;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.urbanairship.UAirship;
import com.viewlift.mobile.pushnotif.AppCMSAirshipReceiver;

/**
 * Created by viewlift on 2/28/18.
 */

public class UrbanAirshipInitReceiver extends BroadcastReceiver {
    private static AppCMSAirshipReceiver appCMSAirshipReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (appCMSAirshipReceiver == null) {
            appCMSAirshipReceiver = new AppCMSAirshipReceiver();
        }

        if (intent != null) {
            String action = intent.getStringExtra("init_action");
            switch (action) {
                case "init":
                    context.startService(new Intent(context, UAirshipInitService.class));
                    break;
                case "register_receiver":
                    registerReceiver(context.getApplicationContext());
                    break;
                case "unregister_receiver":
                    unregisterReciever(context.getApplicationContext());
                    break;
                case "send_channel_id":
                    sendChannelId(context.getApplicationContext());
                    break;
                case "send_app_key":
                    sendAppKey(context.getApplicationContext());
                    break;
            }
        }
    }

    public void registerReceiver(Context context) {
        context.registerReceiver(appCMSAirshipReceiver,
                new IntentFilter("com.urbanairship.push.CHANNEL_UPDATED"));
        context.registerReceiver(appCMSAirshipReceiver,
                new IntentFilter("com.urbanairship.push.OPENED"));
        context.registerReceiver(appCMSAirshipReceiver,
                new IntentFilter("com.urbanairship.push.RECEIVED"));
        context.registerReceiver(appCMSAirshipReceiver,
                new IntentFilter("com.urbanairship.push.DISMISSED"));
    }

    public void unregisterReciever(Context context) {
        context.unregisterReceiver(appCMSAirshipReceiver);
    }

    public void sendChannelId(Context context) {
        String channelId = UAirship.shared().getPushManager().getChannelId();
        Intent intent = new Intent("receive_ua_channel_id");
        intent.putExtra("channel_id", channelId);
        context.sendBroadcast(intent);
    }

    public void sendAppKey(Context context) {
        String appKey = UAirship.shared().getAirshipConfigOptions().getAppKey();
        Intent intent = new Intent("receive_ua_app_key");
        intent.putExtra("app_key", appKey);
        context.sendBroadcast(intent);
    }

    private static class UAirshipInitService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            UAirship.takeOff(getApplication());
            UAirship.shared().getPushManager().setUserNotificationsEnabled(true);

            return Service.START_STICKY_COMPATIBILITY;
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
