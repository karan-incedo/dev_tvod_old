package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.ui.android.AccessLevels;
import com.viewlift.models.data.appcms.ui.android.Platforms;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Items implements Serializable {

    @SerializedName("imageUrl")
    @Expose
    String imageUrl;

    @SerializedName("title")
    @Expose
    String title;

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }
}
