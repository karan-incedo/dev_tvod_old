package com.viewlift.models.data.appcms.subscriptions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 8/23/17.
 */

@UseStag
public class Receipt {
    @SerializedName("orderId")
    @Expose
    String orderId;

    @SerializedName("packageName")
    @Expose
    String packageName;

    @SerializedName("productId")
    @Expose
    String productId;

    @SerializedName("purchaseTime")
    @Expose
    long purchaseTime;

    @SerializedName("purchaseState")
    @Expose
    int purchaseState;

    @SerializedName("purchaseToken")
    @Expose
    String purchaseToken;

    @SerializedName("autoRenewing")
    @Expose
    boolean autoRenewing;

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

    public int getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(int purchaseState) {
        this.purchaseState = purchaseState;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public boolean isAutoRenewing() {
        return autoRenewing;
    }

    public void setAutoRenewing(boolean autoRenewing) {
        this.autoRenewing = autoRenewing;
    }
}
