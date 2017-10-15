package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 8/1/17.
 */

@UseStag
public class IPGeoLocatorResponse {
    @SerializedName("cityname")
    @Expose
    String cityname;
    @SerializedName("continent")
    @Expose
    String continent;
    @SerializedName("countryisocode")
    @Expose
    String countryisocode;
    @SerializedName("countryname")
    @Expose
    String countryname;
    @SerializedName("ispname")
    @Expose
    String ispname;
    @SerializedName("isporganization")
    @Expose
    String isporganization;
    @SerializedName("latitude")
    @Expose
    double latitude;
    @SerializedName("longitude")
    @Expose
    double longitude;
    @SerializedName("metrocode")
    @Expose
    int metrocode;
    @SerializedName("origip")
    @Expose
    String origip;
    @SerializedName("postalcode")
    @Expose
    String postalcode;
    @SerializedName("subdivision_en_name")
    @Expose
    String subdivision_en_name;
    @SerializedName("subdivision_iso_code")
    @Expose
    String subdivision_iso_code;
    @SerializedName("timezone")
    @Expose
    String timezone;

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountryisocode() {
        return countryisocode;
    }

    public void setCountryisocode(String countryisocode) {
        this.countryisocode = countryisocode;
    }

    public String getCountryname() {
        return countryname;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public String getIspname() {
        return ispname;
    }

    public void setIspname(String ispname) {
        this.ispname = ispname;
    }

    public String getIsporganization() {
        return isporganization;
    }

    public void setIsporganization(String isporganization) {
        this.isporganization = isporganization;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getMetrocode() {
        return metrocode;
    }

    public void setMetrocode(int metrocode) {
        this.metrocode = metrocode;
    }

    public String getOrigip() {
        return origip;
    }

    public void setOrigip(String origip) {
        this.origip = origip;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getSubdivision_en_name() {
        return subdivision_en_name;
    }

    public void setSubdivision_en_name(String subdivision_en_name) {
        this.subdivision_en_name = subdivision_en_name;
    }

    public String getSubdivision_iso_code() {
        return subdivision_iso_code;
    }

    public void setSubdivision_iso_code(String subdivision_iso_code) {
        this.subdivision_iso_code = subdivision_iso_code;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
