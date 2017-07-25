
package com.viewlift.models.billing.appcms.subscriptions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InAppPurchaseData {

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
    private Integer purchaseTime;
    @SerializedName("purchaseState")
    @Expose
    private Integer purchaseState;
    @SerializedName("developerPayload")
    @Expose
    private String developerPayload;
    @SerializedName("purchaseToken")
    @Expose
    private String purchaseToken;

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

    public Integer getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Integer purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public Integer getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(Integer purchaseState) {
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
