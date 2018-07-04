package com.viewlift.tv.adm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amazon.device.messaging.ADMConstants;
import com.amazon.device.messaging.ADMMessageHandlerBase;
import com.amazon.device.messaging.ADMMessageReceiver;
import com.viewlift.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by anas.azeem on 3/22/2018.
 * Owned by ViewLift, NYC
 */

public class AppCMSADMMessageHandler extends ADMMessageHandlerBase {

    private static final String TAG = "AppCMSADMMessageHandler";

    /**
     * Class constructor.
     */
    public AppCMSADMMessageHandler() {
        super(AppCMSADMMessageHandler.class.getName());
    }

    /**
     * Class constructor, including the className argument.
     *
     * @param className The name of the class.
     */
    public AppCMSADMMessageHandler(String className) {
        super(className);
    }

    public class AppCMSADMMessageAlertReceiver extends ADMMessageReceiver {
        public AppCMSADMMessageAlertReceiver()
        {
            super(AppCMSADMMessageHandler.class);
        }
    }
    @Override
    protected void onMessage(Intent intent) {
        Log.i(TAG, "SampleADMMessageHandler:onMessage");

        /* String to access message field from data JSON. */
        final String msgKey = getString(R.string.json_data_msg_key);

        /* String to access timeStamp field from data JSON. */
        final String timeKey = getString(R.string.json_data_time_key);

        final String contentIdKey = getString(R.string.json_content_id_key);

        final String seekDeltaKey = getString(R.string.json_seek_delta_key);

        final String searchStringKey = getString(R.string.json_seek_search_string_key);

        final String dataTypeKey = getString(R.string.json_data_type_key);

        /* Intent action that will be triggered in onMessage() callback. */
        final String intentAction = getString(R.string.intent_msg_action);

        /* Extras that were included in the intent. */
        final Bundle extras = intent.getExtras();

        verifyMD5Checksum(extras);

        /* Extract message from the extras in the intent. */
        final String msg = extras.getString("myDirective");
        final String time = extras.getString(timeKey);
        final String contentId = extras.getString("myContentId");
        final String searchString = extras.getString("searchString");
        final long seekDelta = Long.parseLong(extras.getString("seekDelta") != null ? extras.getString("seekDelta") : "0");
        String dataType = extras.getString("dataType");
        dataType = "SERIES";

        if (msg == null || time == null) {
            Log.w(TAG, "SampleADMMessageHandler:onMessage Unable to extract message data." +
                    "Make sure that msgKey and timeKey values match data elements of your JSON message");
        }

        /* Create a notification with message data. */
        /* This is required to test cases where the app or device may be off. */
//        postNotification(msgKey, timeKey, intentAction, msg, time);

        /* Intent category that will be triggered in onMessage() callback. */
        final String msgCategory = getString(R.string.intent_msg_category);

        /* Broadcast an intent to update the app UI with the message. */
        /* The broadcast receiver will only catch this intent if the app is within the onResume state of its lifecycle. */
        /* User will see a notification otherwise. */
        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(intentAction);

//        broadcastIntent.addCategory(msgCategory);
        broadcastIntent.putExtra(msgKey, msg);
        broadcastIntent.putExtra(timeKey, time);
        broadcastIntent.putExtra(contentIdKey, contentId);
        broadcastIntent.putExtra(seekDeltaKey, seekDelta);
        broadcastIntent.putExtra(searchStringKey, searchString);
        broadcastIntent.putExtra(dataTypeKey, dataType);
        sendBroadcast(broadcastIntent);

        /*final Intent notificationIntent = new Intent(this, AppCmsTVSplashActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(notificationIntent);*/

        Log.d(TAG, "Piyush ADM Message: " + msg);

    }

    @Override
    protected void onRegistrationError(String s) {

    }

    @Override
    protected void onRegistered(String registrationId) {
        Log.i(TAG, "SampleADMMessageHandler:onRegistered");
        Log.i(TAG, registrationId);

        /* Register the app instance's registration ID with your server. */
        final AppCMSADMServerMsgHandler srv = new AppCMSADMServerMsgHandler();
        srv.registerAppInstance(getApplicationContext(), registrationId);
    }

    @Override
    protected void onUnregistered(String s) {

    }

    private void verifyMD5Checksum(final Bundle extras) {
        /* String to access consolidation key field from data JSON. */
        final String consolidationKey = getString(R.string.json_data_consolidation_key);

        final Set<String> extrasKeySet = extras.keySet();
        final Map<String, String> extrasHashMap = new HashMap<String, String>();
        for (String key : extrasKeySet) {
            if (!key.equals(ADMConstants.EXTRA_MD5) && !key.equals(consolidationKey)) {
                extrasHashMap.put(key, extras.getString(key));
            }
        }
        final String md5 = ADMSampleMD5ChecksumCalculator.calculateChecksum(extrasHashMap);
        Log.i(TAG, "SampleADMMessageHandler:onMessage App md5: " + md5);

        /* Extract md5 from the extras in the intent. */
        final String admMd5 = extras.getString(ADMConstants.EXTRA_MD5);
        Log.i(TAG, "SampleADMMessageHandler:onMessage ADM md5: " + admMd5);

        /* Data integrity check. */
        if (!admMd5.trim().equals(md5.trim())) {
            Log.w(TAG, "SampleADMMessageHandler:onMessage MD5 checksum verification failure. " +
                    "Message received with errors");
        }
    }
}
