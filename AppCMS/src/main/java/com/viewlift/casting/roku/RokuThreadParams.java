package com.viewlift.casting.roku;

import java.net.URL;

public class RokuThreadParams {
    private int action;
    private int int_data;
    private URL url;

    public RokuThreadParams() {
    }

    public int getAction() {
        return this.action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getIntData() {
        return this.int_data;
    }

    public void setIntData(int int_data) {
        this.int_data = int_data;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return this.url;
    }
}
