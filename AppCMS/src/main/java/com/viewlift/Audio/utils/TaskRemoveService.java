package com.viewlift.Audio.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.viewlift.Audio.AudioServiceHelper;

/**
 * in some  devices musicservicenot call on task removed when bluetooth connected
 * so this service stop audio playback on task removed
 */
public class TaskRemoveService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        Intent intent = new Intent();
        intent.setAction(AudioServiceHelper.APP_CMS_STOP_AUDIO_SERVICE_ACTION);
        intent.putExtra(AudioServiceHelper.APP_CMS_STOP_AUDIO_SERVICE_MESSAGE, true);
        getApplicationContext().sendBroadcast(intent);
        //Code here
        stopSelf();
    }
}