package com.viewlift.models.data.appcms.sites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Gist {

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("companyName")
    @Expose
    String companyName;

    @SerializedName("serviceType")
    @Expose
    String serviceType;

    @SerializedName("domainName")
    @Expose
    String domainName;

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("siteInternalName")
    @Expose
    String siteInternalName;

    @SerializedName("appAccess")
    @Expose
    AppAccess appAccess;

    @SerializedName("updateDate")
    @Expose
    long updateDate;

    @SerializedName("addedDate")
    @Expose
    long addedDate;

    @SerializedName("productionMode")
    @Expose
    boolean productionMode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteInternalName() {
        return siteInternalName;
    }

    public void setSiteInternalName(String siteInternalName) {
        this.siteInternalName = siteInternalName;
    }

    public AppAccess getAppAccess() {
        return appAccess;
    }

    public void setAppAccess(AppAccess appAccess) {
        this.appAccess = appAccess;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public boolean getProductionMode() {
        return productionMode;
    }

    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
    }
}
