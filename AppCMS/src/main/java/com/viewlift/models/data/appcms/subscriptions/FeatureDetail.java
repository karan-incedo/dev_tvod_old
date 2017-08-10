package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/19/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class FeatureDetail {
    @SerializedName("textToDisplay")
    @Expose
    private String textToDisplay;

    @SerializedName("value")
    @Expose
    private String value;

    @SerializedName("valueType")
    @Expose
    private String valueType;

    public String getTextToDisplay() {
        return textToDisplay;
    }

    public void setTextToDisplay(String textToDisplay) {
        this.textToDisplay = textToDisplay;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }
}
