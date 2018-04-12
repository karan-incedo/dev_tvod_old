package com.viewlift.kiswe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.kiswe.kmsdkcorekit.KMSDKCoreKit;
import com.kiswe.kmsdkcorekit.reports.Report;
import com.kiswe.kmsdkcorekit.reports.ReportSubscriber;
import com.kiswe.kmsdkcorekit.reports.Reports;
import com.viewlift.R;

/**
 * Created by viewlift on 2/20/18.
 */

public class LaunchKisweReceiver extends BroadcastReceiver {
    private static final String TAG = "KisweRegReceiver";

    private ReportSubscriber reportSubscriber = new ReportSubscriber() {
        @Override
        public void handleReport(Report report) {

            if (!Reports.STATUS_SOURCE_PLAYER.equals(report.getString(Reports.FIELD_STATUS_SOURCE))) {
                return;
            }

            String eventId = report.getString(Reports.FIELD_STATUS_EVENT_ID, "unknown");
            String msg = report.getString(Reports.FIELD_STATUS_MESSAGE, "unknown status");
            int code = report.getInt(Reports.FIELD_STATUS_CODE, -1);

            Log.i(TAG, "(handleReport) Status (" + code + "): " + msg + " [" + eventId + "]");
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create an instance of the Kiswe View Player
        if (context != null && intent != null) {
            String eventId = intent.getStringExtra("KISWE_EVENT_ID");
            String userName = intent.getStringExtra("KISWE_USERNAME");
            if (!TextUtils.isEmpty(eventId) &&
                    !TextUtils.isEmpty(userName)) {
                launchKiswePlayer(context,
                        eventId,
                        userName);
            }
        }
    }

    public void launchKiswePlayer(Context context,
                                  String eventId,
                                  String userName) {
        KMSDKCoreKit.initialize(context);
        KMSDKCoreKit mKit = KMSDKCoreKit.getInstance()
                .addReportSubscriber(Reports.TYPE_STATUS, reportSubscriber)
                .setLogLevel(KMSDKCoreKit.DEBUG);
        mKit.setApiKey(context.getResources().getString(R.string.KISWE_PLAYER_API_KEY));
        mKit.configUser(userName,
                context.getResources().getString(R.string.KISWE_PLAYER_API_KEY));
        mKit.startKiswePlayerActivity(context, eventId);
    }
}
