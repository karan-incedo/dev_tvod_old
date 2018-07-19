package com.viewlift.models.data.appcms.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by anas.azeem on 7/26/2017.
 * Owned by ViewLift, NYC
 */

@UseStag
public class ClosedCaptions implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("publishDate")
    @Expose
    private String publishDate;

    @SerializedName("updateDate")
    @Expose
    private String updateDate;

    @SerializedName("autoGenerateRelated")
    @Expose
    private String addedDate;

    @SerializedName("permalink")
    @Expose
    private String permalink;

    @SerializedName("siteOwner")
    @Expose
    private String siteOwner;

    @SerializedName("registeredDate")
    @Expose
    private String registeredDate;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("format")
    @Expose
    private String format;

    @SerializedName("language")
    @Expose
    private String language;

    @SerializedName("size")
    @Expose
    private float size;

    public ClosedCaptions(Parcel in) {
        id = in.readString();
        publishDate = in.readString();
        updateDate = in.readString();
        addedDate = in.readString();
        permalink = in.readString();
        siteOwner = in.readString();
        registeredDate = in.readString();
        url = in.readString();
        format = in.readString();
        language = in.readString();
        size = in.readFloat();
    }

    public static final Creator<ClosedCaptions> CREATOR = new Creator<ClosedCaptions>() {
        @Override
        public ClosedCaptions createFromParcel(Parcel in) {
            return new ClosedCaptions(in);
        }

        @Override
        public ClosedCaptions[] newArray(int size) {
            return new ClosedCaptions[size];
        }
    };

    public ClosedCaptions() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getSiteOwner() {
        return siteOwner;
    }

    public void setSiteOwner(String siteOwner) {
        this.siteOwner = siteOwner;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(publishDate);
        dest.writeString(updateDate);
        dest.writeString(addedDate);
        dest.writeString(permalink);
        dest.writeString(siteOwner);
        dest.writeString(registeredDate);
        dest.writeString(url);
        dest.writeString(format);
        dest.writeString(language);
        dest.writeFloat(size);
    }
}
