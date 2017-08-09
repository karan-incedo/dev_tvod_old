
package com.viewlift.models.billing.appcms.subscriptions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InAppPurchaseData {

    @SerializedName("autoRenewing")
    @Expose
    private boolean autoRenewing;
    @SerializedName("orderId")
    @Expose
    private String orderId;
    @SerializedName("packageName")
    @Expose
    private String packageName;
    @SerializedName("productId")
    @Expose
    private String productId;
    @SerializedName("purchaseTime")
    @Expose
    private long purchaseTime;
    @SerializedName("purchaseState")
    @Expose
    private long purchaseState;
    @SerializedName("developerPayload")
    @Expose
    private String developerPayload;
    @SerializedName("purchaseToken")
    @Expose
    private String purchaseToken;

    public boolean isAutoRenewing() {
        return autoRenewing;
    }

    public void setAutoRenewing(boolean autoRenewing) {
        this.autoRenewing = autoRenewing;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public long getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(long purchaseState) {
        this.purchaseState = purchaseState;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

}
