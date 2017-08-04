
package com.viewlift.models.data.appcms.subscriptions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class AppCMSUserSubscriptionPlanInfoResult {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("siteId")
    @Expose
    private String siteId;
    @SerializedName("planId")
    @Expose
    private String planId;
    @SerializedName("subscriptionStatus")
    @Expose
    private String subscriptionStatus;
    @SerializedName("subscriptionStartDate")
    @Expose
    private long subscriptionStartDate;
    @SerializedName("subscriptionEndDate")
    @Expose
    private long subscriptionEndDate;
    @SerializedName("paymentInitiatedDate")
    @Expose
    private long paymentInitiatedDate;
    @SerializedName("paymentToBeReInitiatedDate")
    @Expose
    private long paymentToBeReInitiatedDate;
    @SerializedName("siteOwner")
    @Expose
    private String siteOwner;
    @SerializedName("paymentHandler")
    @Expose
    private String paymentHandler;
    @SerializedName("platform")
    @Expose
    private String platform;
    @SerializedName("paymentUniqueId")
    @Expose
    private String paymentUniqueId;
    @SerializedName("vlTransactionId")
    @Expose
    private String vlTransactionId;
    @SerializedName("receipt")
    @Expose
    private String receipt;
    @SerializedName("preTaxAmount")
    @Expose
    private double preTaxAmount;
    @SerializedName("taxAmount")
    @Expose
    private double taxAmount;
    @SerializedName("taxStatus")
    @Expose
    private double taxStatus;
    @SerializedName("totalAmount")
    @Expose
    private double totalAmount;
    @SerializedName("paymentTransactionId")
    @Expose
    private String paymentTransactionId;
    @SerializedName("paymentTransactionReferenceId")
    @Expose
    private String paymentTransactionReferenceId;
    @SerializedName("paymentInstrumentId")
    @Expose
    private String paymentInstrumentId;
    @SerializedName("gatewayChargeId")
    @Expose
    private String gatewayChargeId;
    @SerializedName("gatewayRefundId")
    @Expose
    private String gatewayRefundId;
    @SerializedName("addedDate")
    @Expose
    private long addedDate;
    @SerializedName("updateDate")
    @Expose
    private long updateDate;
    @SerializedName("debugInfo")
    @Expose
    private DebugInfo debugInfo;
    @SerializedName("identifier")
    @Expose
    private String identifier;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;
    @SerializedName("freeTrial")
    @Expose
    private boolean freeTrial;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("zip")
    @Expose
    private int zip;
    @SerializedName("description")
    @Expose
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public long getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public void setSubscriptionStartDate(long subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }

    public long getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(long subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public long getPaymentInitiatedDate() {
        return paymentInitiatedDate;
    }

    public void setPaymentInitiatedDate(long paymentInitiatedDate) {
        this.paymentInitiatedDate = paymentInitiatedDate;
    }

    public long getPaymentToBeReInitiatedDate() {
        return paymentToBeReInitiatedDate;
    }

    public void setPaymentToBeReInitiatedDate(long paymentToBeReInitiatedDate) {
        this.paymentToBeReInitiatedDate = paymentToBeReInitiatedDate;
    }

    public String getSiteOwner() {
        return siteOwner;
    }

    public void setSiteOwner(String siteOwner) {
        this.siteOwner = siteOwner;
    }

    public String getPaymentHandler() {
        return paymentHandler;
    }

    public void setPaymentHandler(String paymentHandler) {
        this.paymentHandler = paymentHandler;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPaymentUniqueId() {
        return paymentUniqueId;
    }

    public void setPaymentUniqueId(String paymentUniqueId) {
        this.paymentUniqueId = paymentUniqueId;
    }

    public String getVlTransactionId() {
        return vlTransactionId;
    }

    public void setVlTransactionId(String vlTransactionId) {
        this.vlTransactionId = vlTransactionId;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public double getPreTaxAmount() {
        return preTaxAmount;
    }

    public void setPreTaxAmount(double preTaxAmount) {
        this.preTaxAmount = preTaxAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getTaxStatus() {
        return taxStatus;
    }

    public void setTaxStatus(double taxStatus) {
        this.taxStatus = taxStatus;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public String getPaymentTransactionReferenceId() {
        return paymentTransactionReferenceId;
    }

    public void setPaymentTransactionReferenceId(String paymentTransactionReferenceId) {
        this.paymentTransactionReferenceId = paymentTransactionReferenceId;
    }

    public String getPaymentInstrumentId() {
        return paymentInstrumentId;
    }

    public void setPaymentInstrumentId(String paymentInstrumentId) {
        this.paymentInstrumentId = paymentInstrumentId;
    }

    public String getGatewayChargeId() {
        return gatewayChargeId;
    }

    public void setGatewayChargeId(String gatewayChargeId) {
        this.gatewayChargeId = gatewayChargeId;
    }

    public String getGatewayRefundId() {
        return gatewayRefundId;
    }

    public void setGatewayRefundId(String gatewayRefundId) {
        this.gatewayRefundId = gatewayRefundId;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public DebugInfo getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(DebugInfo debugInfo) {
        this.debugInfo = debugInfo;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public boolean getFreeTrial() {
        return freeTrial;
    }

    public void setFreeTrial(boolean freeTrial) {
        this.freeTrial = freeTrial;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
