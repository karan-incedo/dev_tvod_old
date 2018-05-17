package com.viewlift.tv.adm;

import com.amazon.device.messaging.ADMMessageReceiver;

/**
 * Created by anas.azeem on 3/22/2018.
 * Owned by ViewLift, NYC
 */

public class AppCMSADMMessageAlertReceiver extends ADMMessageReceiver {
    public AppCMSADMMessageAlertReceiver()
    {
        super(AppCMSADMMessageHandler.class);
    }
}
