package com.viewlift.casting.roku;

import java.net.URL;


public class RokuDevice {
    private URL appRunUrl;
    private URL url;
    private String rokuDeviceName;
    private String rokuAppId;

    RokuDevice(URL url, String rokuDeviceName, URL appRunUrl) {
        this.url = url;
        this.rokuDeviceName = rokuDeviceName;
        this.appRunUrl = appRunUrl;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getRokuDeviceName() {
        return rokuDeviceName;
    }

    public void setRokuDeviceName(String rokuDeviceName) {
        this.rokuDeviceName = rokuDeviceName;
    }

    public URL getAppRunUrl() {
        return appRunUrl;
    }

    public void setAppRunUrl(URL appRunUrl) {
        this.appRunUrl = appRunUrl;
    }

    public void setRokuAppId(String rokuAppId) {
        this.rokuAppId = rokuAppId;
    }

    public String getRokuAppId() {
        return rokuAppId;
    }

    @Override
    public String toString() {
        return "\n " + rokuDeviceName;
    }
}
