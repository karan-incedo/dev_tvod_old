package com.viewlift.mobile.pushnotif;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.apptentive.android.sdk.Apptentive;
import com.urbanairship.AirshipReceiver;
import com.urbanairship.push.PushMessage;
import com.viewlift.R;

/**
 * Created by viewlift on 1/16/18.
 */

public class AppCMSAirshipReceiver extends AirshipReceiver {
    @Override
    protected void onChannelUpdated(@NonNull Context context, @NonNull String channelId) {
        super.onChannelUpdated(context, channelId);
        Apptentive.setPushNotificationIntegration(Apptentive.PUSH_PROVIDER_URBAN_AIRSHIP, channelId);
    }

    @Override
    protected void onChannelCreated(@NonNull Context context, @NonNull String channelId) {
        super.onChannelCreated(context, channelId);
        Apptentive.setPushNotificationIntegration(Apptentive.PUSH_PROVIDER_URBAN_AIRSHIP, channelId);
    }

    @Override
    protected void onPushReceived(@NonNull Context context, @NonNull PushMessage message, boolean notificationPosted) {
        Bundle pushBundle = message.getPushBundle();

        if (Apptentive.isApptentivePushNotification(pushBundle)) {
            PendingIntent pendingIntent = Apptentive.buildPendingIntentFromPushNotification(pushBundle);
            if (pendingIntent != null) {
                String title = Apptentive.getTitleFromApptentivePush(pushBundle);
                String body = Apptentive.getBodyFromApptentivePush(pushBundle);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                int color = Build.VERSION.SDK_INT >= 23 ? context.getResources().getColor(R.color.colorAccent, null) : context.getResources().getColor(R.color.colorAccent);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_skylight_notification)
                        .setColor(color)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());
            } else {
                // This push came from Apptentive, but it's not for the active conversation.
            }
        } else {
            // This push didn't come from Apptentive.
            super.onPushReceived(context, message, notificationPosted);
        }
    }
}
