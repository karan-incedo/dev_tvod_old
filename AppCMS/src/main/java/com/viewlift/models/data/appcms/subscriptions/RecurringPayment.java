package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/12/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecurringPayment {

    @SerializedName("preTax")
    @Expose
    private Double preTax;

    @SerializedName("tax")
    @Expose
    private Integer tax;

    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;

    @SerializedName("total")
    @Expose
    private Double total;

    public Double getPreTax() {
        return preTax;
    }

    public void setPreTax(Double preTax) {
        this.preTax = preTax;
    }

    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
