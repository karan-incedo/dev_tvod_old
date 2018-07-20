package com.viewlift.models.data.appcms.ui.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class UserIdentity {

    @SerializedName("_raw")
    @Expose
    Raw raw;
    @SerializedName("phone")
    @Expose
    Phone phone;

    @SerializedName("registerdVia")
    @Expose
    String registerdVia;

    @SerializedName("at_hash")
    @Expose
    String atHash;

    @SerializedName("registeredOn")
    @Expose
    String registeredOn;

    @SerializedName("userId")
    @Expose
    String userId;

    @SerializedName("provider")
    @Expose
    String provider;

    @SerializedName("site")
    @Expose
    String site;

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("email")
    @Expose
    String email;

    @SerializedName("picture")
    @Expose
    String picture;

    @SerializedName("country")
    @Expose
    String country;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("password")
    @Expose
    String password;

    @SerializedName("authorizationToken")
    @Expose
    String authorizationToken;

    @SerializedName("refreshToken")
    @Expose
    String refreshToken;

    @SerializedName("isSubscribed")
    @Expose
    boolean isSubscribed;

    @SerializedName("error")
    @Expose
    String error;

    public Raw getRaw() {
        return raw;
    }

    public void setRaw(Raw raw) {
        this.raw = raw;
    }

    public String getRegisterdVia() {
        return registerdVia;
    }

    public void setRegisterdVia(String registerdVia) {
        this.registerdVia = registerdVia;
    }

    public String getAtHash() {
        return atHash;
    }

    public void setAtHash(String atHash) {
        this.atHash = atHash;
    }

    public String getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(String registeredOn) {
        this.registeredOn = registeredOn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Phone getPhone() {
        return phone;
    }
}
