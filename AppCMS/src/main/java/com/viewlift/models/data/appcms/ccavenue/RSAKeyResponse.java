package com.viewlift.models.data.appcms.ccavenue;

import com.google.gson.annotations.SerializedName;

public class RSAKeyResponse {
    @SerializedName("status")
    String status;
    @SerializedName("accessCode")
    String accessCode;
    @SerializedName("orderId")
    String orderId;
    @SerializedName("merchantId")
    String merchantId;
    @SerializedName("redirectUrl")
    String redirectUrl;
    @SerializedName("cancelUrl")
    String cancelUrl;
    @SerializedName("rsaToken")
    String rsaToken;
    @SerializedName("referenceNo")
    String referenceNo;

    public String getStatus() {
        return status;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public String getRsaToken() {
        return rsaToken;
    }

    public String getReferenceNo() {
        return referenceNo;
    }
}
