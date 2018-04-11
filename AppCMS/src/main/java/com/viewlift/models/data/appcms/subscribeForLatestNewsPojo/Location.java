package com.viewlift.models.data.appcms.subscribeForLatestNewsPojo;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Location{

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("dstoff")
    private int dstoff;

    @SerializedName("timezone")
    private String timezone;

    @SerializedName("latitude")
    private int latitude;

    @SerializedName("gmtoff")
    private int gmtoff;

    @SerializedName("longitude")
    private int longitude;

    public void setCountryCode(String countryCode){
        this.countryCode = countryCode;
    }

    public String getCountryCode(){
        return countryCode;
    }

    public void setDstoff(int dstoff){
        this.dstoff = dstoff;
    }

    public int getDstoff(){
        return dstoff;
    }

    public void setTimezone(String timezone){
        this.timezone = timezone;
    }

    public String getTimezone(){
        return timezone;
    }

    public void setLatitude(int latitude){
        this.latitude = latitude;
    }

    public int getLatitude(){
        return latitude;
    }

    public void setGmtoff(int gmtoff){
        this.gmtoff = gmtoff;
    }

    public int getGmtoff(){
        return gmtoff;
    }

    public void setLongitude(int longitude){
        this.longitude = longitude;
    }

    public int getLongitude(){
        return longitude;
    }

    @Override
    public String toString(){
        return
                "Location{" +
                        "country_code = '" + countryCode + '\'' +
                        ",dstoff = '" + dstoff + '\'' +
                        ",timezone = '" + timezone + '\'' +
                        ",latitude = '" + latitude + '\'' +
                        ",gmtoff = '" + gmtoff + '\'' +
                        ",longitude = '" + longitude + '\'' +
                        "}";
    }
}