package com.viewlift.models.data.appcms.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.CreditBlock;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.ImageGist;
import com.vimeo.stag.UseStag;

import java.util.List;

/**
 * Created by wishy.gupta on 09-01-2018.
 */
@UseStag
public class AudioList {
    @SerializedName("gist")
    @Expose
    Gist gist;


    @SerializedName("creditBlocks")
    @Expose
    List<CreditBlock> creditBlocks = null;

    public List<CreditBlock> getCreditBlocks() {
        return creditBlocks;
    }

    public void setCreditBlocks(List<CreditBlock> creditBlocks) {
        this.creditBlocks = creditBlocks;
    }


    public Gist getGist() {
        return gist;
    }

    public void setGist(Gist gist) {
        this.gist = gist;
    }

    public ContentDatum convertToContentDatum() {
        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setCreditBlocks(this.creditBlocks);
        contentDatum.setGist(this.gist);
        return contentDatum;
    }

}
