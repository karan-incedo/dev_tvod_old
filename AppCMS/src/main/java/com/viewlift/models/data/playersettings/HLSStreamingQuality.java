package com.viewlift.models.data.playersettings;

/**
 * Class is used to store the index and value (resolution eg. 360p) of a particular track of
 * an HLS stream.
 */
public class HLSStreamingQuality {

    int index;
    String value;

    public HLSStreamingQuality(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
