
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SocialMedia {

    @SerializedName("facebook")
    @Expose
    private Facebook facebook;
    @SerializedName("instagram")
    @Expose
    private Instagram instagram;
    @SerializedName("twitter")
    @Expose
    private Twitter twitter;
    @SerializedName("vimeo")
    @Expose
    private Vimeo vimeo;
    @SerializedName("youtube")
    @Expose
    private Youtube youtube;

    public Facebook getFacebook() {
        return facebook;
    }

    public void setFacebook(Facebook facebook) {
        this.facebook = facebook;
    }

    public Instagram getInstagram() {
        return instagram;
    }

    public void setInstagram(Instagram instagram) {
        this.instagram = instagram;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public Vimeo getVimeo() {
        return vimeo;
    }

    public void setVimeo(Vimeo vimeo) {
        this.vimeo = vimeo;
    }

    public Youtube getYoutube() {
        return youtube;
    }

    public void setYoutube(Youtube youtube) {
        this.youtube = youtube;
    }

}
