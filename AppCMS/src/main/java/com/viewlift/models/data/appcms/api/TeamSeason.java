package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class TeamSeason {

    @SerializedName("seasonid")
    @Expose
   String seasonid ;

    @SerializedName("seasonname")
    @Expose
    String seasonname ;
}
