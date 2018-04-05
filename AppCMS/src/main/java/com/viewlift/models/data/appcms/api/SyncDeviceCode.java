package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class SyncDeviceCode {

    private String picture;

    private String email;

    private String name;

    private String userId;

    private String provider;

    private String authorizationToken;

    private boolean isSubscribed;

    private String refreshToken;

    private String status;

    private String errorMessage;

    public String getPicture ()
    {
        return picture;
    }

    public void setPicture (String picture)
    {
        this.picture = picture;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getUserId ()
    {
        return userId;
    }

    public void setUserId (String userId)
    {
        this.userId = userId;
    }

    public String getProvider ()
    {
        return provider;
    }

    public void setProvider (String provider)
    {
        this.provider = provider;
    }

    public String getAuthorizationToken ()
    {
        return authorizationToken;
    }

    public void setAuthorizationToken (String authorizationToken)
    {
        this.authorizationToken = authorizationToken;
    }

    public boolean getIsSubscribed ()
    {
        return isSubscribed;
    }

    public void setIsSubscribed (boolean isSubscribed)
    {
        this.isSubscribed = isSubscribed;
    }

    public String getRefreshToken ()
    {
        return refreshToken;
    }

    public void setRefreshToken (String refreshToken)
    {
        this.refreshToken = refreshToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    @Override
    public String toString()
    {
        return "ClassPojo [picture = "+picture+", email = "+email+", name = "+name+", userId = "+userId+", provider = "+provider+", authorizationToken = "+authorizationToken+", isSubscribed = "+isSubscribed+", refreshToken = "+refreshToken+"]";
    }
}
