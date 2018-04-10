package com.viewlift.models.data.appcms.subscribeForLatestNewsPojo;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class UserExist{

    @SerializedName("instance")
    private String instance;

    @SerializedName("detail")
    private String detail;

    @SerializedName("type")
    private String type;

    @SerializedName("title")
    private String title;

    @SerializedName("status")
    private int status;

    public void setInstance(String instance){
        this.instance = instance;
    }

    public String getInstance(){
        return instance;
    }

    public void setDetail(String detail){
        this.detail = detail;
    }

    public String getDetail(){
        return detail;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public int getStatus(){
        return status;
    }

    @Override
    public String toString(){
        return
                "UserExist{" +
                        "instance = '" + instance + '\'' +
                        ",detail = '" + detail + '\'' +
                        ",type = '" + type + '\'' +
                        ",title = '" + title + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}