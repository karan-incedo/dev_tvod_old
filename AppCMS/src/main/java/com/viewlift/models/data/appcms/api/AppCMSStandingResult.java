package com.viewlift.models.data.appcms.api;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.playlist.AudioList;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

@UseStag
public class AppCMSStandingResult {

    @SerializedName("seasonData")
    @Expose
    SeasonData seasonData;

}
