
package com.viewlift.models.data.appcms.sites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gist {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("companyName")
    @Expose
    private String companyName;
    @SerializedName("serviceType")
    @Expose
    private String serviceType;
    @SerializedName("domainName")
    @Expose
    private String domainName;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("siteInternalName")
    @Expose
    private String siteInternalName;
    @SerializedName("appAccess")
    @Expose
    private AppAccess appAccess;
    @SerializedName("updateDate")
    @Expose
    private Long updateDate;
    @SerializedName("addedDate")
    @Expose
    private Long addedDate;
    @SerializedName("productionMode")
    @Expose
    private Boolean productionMode;

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

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public Long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Long addedDate) {
        this.addedDate = addedDate;
    }

    public Boolean getProductionMode() {
        return productionMode;
    }

    public void setProductionMode(Boolean productionMode) {
        this.productionMode = productionMode;
    }

}
