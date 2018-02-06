package com.viewlift.views.adapters;

/*
 * Created by Viewlift on 7/11/17.
 */

import android.support.v7.widget.RecyclerView;

import com.viewlift.models.data.appcms.api.ContentDatum;

import java.util.List;

public interface AppCMSBaseAdapter {
    void resetData(RecyclerView listView);

    void updateData(RecyclerView listView, List<ContentDatum> contentData);

    void setClickable(boolean clickable);
}
