package com.viewlift.casting.roku;


public class RokuDiscoveryEventData {
    public RokuWrapper activity;
    public RokuDevice rokuDevice;
    public RokuDiscoveryEventData(RokuWrapper _activity, RokuDevice rokuDevice) {
        this.activity = _activity;
        this.rokuDevice = rokuDevice;
    }
}
