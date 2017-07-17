package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/12/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class Data {

    @SerializedName("items")
    @Expose
    List<Item> items = null;

    @SerializedName("count")
    @Expose
    int count;

    @SerializedName("type")
    @Expose
    String type;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
