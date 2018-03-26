package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**

 */
@UseStag
public class SubscriptionAudioFlowContent implements Serializable {

    @SerializedName("overlay_message")
    @Expose
    String overlayMessage;

    @SerializedName("subscription_button_text")
    @Expose
    String subscriptionButtonText;

    @SerializedName("login_button_text")
    @Expose
    String loginButtonText;

    public String getOverlayMessage() {
        return overlayMessage;
    }

    public void setOverlayMessage(String overlayMessage) {
        this.overlayMessage = overlayMessage;
    }

    public String getSubscriptionButtonText() {
        return subscriptionButtonText;
    }

    public void setSubscriptionButtonText(String subscriptionButtonText) {
        this.subscriptionButtonText = subscriptionButtonText;
    }

    public String getLoginButtonText() {
        return loginButtonText;
    }

    public void setLoginButtonText(String loginButtonText) {
        this.loginButtonText = loginButtonText;
    }
}
