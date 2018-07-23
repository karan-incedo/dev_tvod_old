package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Language implements Serializable,BaseInterface {

    @SerializedName("code")
    @Expose
    String languageCode;

    @SerializedName("name")
    @Expose
    String languageName;

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }


    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }


    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if(obj != null && obj instanceof Language){
            isEqual = ((Language) obj).languageCode.equalsIgnoreCase(this.getLanguageCode());
        }
        return isEqual;
    }

    public ContentDatum convertToContentDatum(){
        ContentDatum contentDatum = new ContentDatum();
        Gist gist = new Gist();
        gist.setTitle(languageName);
        gist.setDataId(languageCode);
        contentDatum.setGist(gist);
        return contentDatum;
    }
}
