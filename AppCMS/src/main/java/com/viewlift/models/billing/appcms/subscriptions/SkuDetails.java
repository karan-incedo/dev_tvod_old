package com.viewlift.models.billing.appcms.subscriptions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 8/15/17.
 */

@UseStag
public class SkuDetails {

    @SerializedName("productId")
    @Expose
    String productId;

    @SerializedName("type")
    @Expose
    String type;

    @SerializedName("price")
    @Expose
    String price;

    @SerializedName("price_amount_micros")
    @Expose
    String priceAmountMicros;

    @SerializedName("price_currency_code")
    @Expose
    String priceCurrencyCode;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("subscriptionPeriod")
    @Expose
    String subscriptionPeriod;

    @SerializedName("freeTrialPeriod")
    @Expose
    String freeTrialPeriod;

    @SerializedName("introductoryPrice")
    @Expose
    String introductoryPrice;

    @SerializedName("introductoryPriceAmountMicros")
    @Expose
    String introductoryPriceAmountMicros;

    @SerializedName("introductoryPricePeriod")
    @Expose
    String introductoryPricePeriod;

    @SerializedName("introductoryPriceCycles")
    @Expose
    String introductoryPriceCycles;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceAmountMicros() {
        return priceAmountMicros;
    }

    public void setPriceAmountMicros(String priceAmountMicros) {
        this.priceAmountMicros = priceAmountMicros;
    }

    public String getPriceCurrencyCode() {
        return priceCurrencyCode;
    }

    public void setPriceCurrencyCode(String priceCurrencyCode) {
        this.priceCurrencyCode = priceCurrencyCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubscriptionPeriod() {
        return subscriptionPeriod;
    }

    public void setSubscriptionPeriod(String subscriptionPeriod) {
        this.subscriptionPeriod = subscriptionPeriod;
    }

    public String getFreeTrialPeriod() {
        return freeTrialPeriod;
    }

    public void setFreeTrialPeriod(String freeTrialPeriod) {
        this.freeTrialPeriod = freeTrialPeriod;
    }

    public String getIntroductoryPrice() {
        return introductoryPrice;
    }

    public void setIntroductoryPrice(String introductoryPrice) {
        this.introductoryPrice = introductoryPrice;
    }

    public String getIntroductoryPriceAmountMicros() {
        return introductoryPriceAmountMicros;
    }

    public void setIntroductoryPriceAmountMicros(String introductoryPriceAmountMicros) {
        this.introductoryPriceAmountMicros = introductoryPriceAmountMicros;
    }

    public String getIntroductoryPricePeriod() {
        return introductoryPricePeriod;
    }

    public void setIntroductoryPricePeriod(String introductoryPricePeriod) {
        this.introductoryPricePeriod = introductoryPricePeriod;
    }

    public String getIntroductoryPriceCycles() {
        return introductoryPriceCycles;
    }

    public void setIntroductoryPriceCycles(String introductoryPriceCycles) {
        this.introductoryPriceCycles = introductoryPriceCycles;
    }
}
