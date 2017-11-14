package com.viewlift.models.data.appcms.sites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 11/9/17.
 */

@UseStag
public class Notifications {
    @SerializedName("apiKey")
    @Expose
    String apiKey;

    @SerializedName("mailChimp")
    @Expose
    MailChimp mailChimp;

    @SerializedName("mandrill")
    @Expose
    Mandrill mandrill;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public MailChimp getMailChimp() {
        return mailChimp;
    }

    public void setMailChimp(MailChimp mailChimp) {
        this.mailChimp = mailChimp;
    }

    public Mandrill getMandrill() {
        return mandrill;
    }

    public void setMandrill(Mandrill mandrill) {
        this.mandrill = mandrill;
    }
}
