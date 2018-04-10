package com.viewlift.models.data.appcms.subscribeForLatestNewsPojo;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class SubscribeGoRequest{

    @SerializedName("email_address")
    private String emailAddress;

    @SerializedName("status")
    private String status;

    public void setEmailAddress(String emailAddress){
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress(){
        return emailAddress;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }

    @Override
    public String toString(){
        return
                "SubscribeGoRequest{" +
                        "email_address = '" + emailAddress + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}