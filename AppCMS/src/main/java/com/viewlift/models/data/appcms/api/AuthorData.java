package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by ram.kailash on 2/14/2018.
 */
@UseStag
public class AuthorData implements Serializable{

    private String site;
    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("publishDate")
    @Expose
    Long publishDate;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("permalink")
    @Expose
    String permalink;

    @SerializedName("siteOwner")
    @Expose
    String siteOwner;

    @SerializedName("registeredDate")
    @Expose
    Long registeredDate;

    @SerializedName("originRegion")
    @Expose
    String originRegion;

    @SerializedName("firstName")
    @Expose
    String firstName;

    @SerializedName("lastName")
    @Expose
    String lastName;

    @SerializedName("displayName")
    @Expose
    String displayName;

    @SerializedName("email")
    @Expose
    String email;

    @SerializedName("image")
    @Expose
    String image;

    @SerializedName("caption")
    @Expose
    String caption;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("birthPlace")
    @Expose
    String birthPlace;

    @SerializedName("height")
    @Expose
    String height;

    @SerializedName("hairColor")
    @Expose
    String hairColor;

    @SerializedName("eyeColor")
    @Expose
    String eyeColor;

    @SerializedName("education")
    @Expose
    String education;

    @SerializedName("awards")
    @Expose
    String awards;

    @SerializedName("summary")
    @Expose
    String summary;

    @SerializedName("bio")
    @Expose
    String bio;

    @SerializedName("twitterPage")
    @Expose
    String twitterPage;

    @SerializedName("twitterHandle")
    @Expose
    String twitterHandle;

    @SerializedName("facebookPage")
    @Expose
    String facebookPage;

    @SerializedName("webSite")
    @Expose
    String webSite;

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

    public Long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Long publishDate) {
        this.publishDate = publishDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Long registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getOriginRegion() {
        return originRegion;
    }

    public void setOriginRegion(String originRegion) {
        this.originRegion = originRegion;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getTwitterPage() {
        return twitterPage;
    }

    public void setTwitterPage(String twitterPage) {
        this.twitterPage = twitterPage;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public void setTwitterHandle(String twitterHandle) {
        this.twitterHandle = twitterHandle;
    }

    public String getFacebookPage() {
        return facebookPage;
    }

    public void setFacebookPage(String facebookPage) {
        this.facebookPage = facebookPage;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }
}
