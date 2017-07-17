package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/12/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class RecurringPayment {

    @SerializedName("preTax")
    @Expose
    double preTax;

    @SerializedName("tax")
    @Expose
    int tax;

    @SerializedName("currencyCode")
    @Expose
    String currencyCode;

    @SerializedName("total")
    @Expose
    double total;

    public double getPreTax() {
        return preTax;
    }

    public void setPreTax(double preTax) {
        this.preTax = preTax;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
