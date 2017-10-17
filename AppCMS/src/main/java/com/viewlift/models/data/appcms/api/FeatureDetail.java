package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

import io.realm.annotations.PrimaryKey;

@UseStag
public class FeatureDetail implements Serializable {

    @SerializedName("textToDisplay")
    @Expose
    private String textToDisplay;

    @PrimaryKey
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
