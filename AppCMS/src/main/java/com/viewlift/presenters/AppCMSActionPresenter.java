package com.viewlift.presenters;

import com.viewlift.presenters.AppCMSPresenter.ExtraScreenType;

public class AppCMSActionPresenter {
    private String action;
    private ExtraScreenType extraScreenType;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public AppCMSPresenter.ExtraScreenType getExtraScreenType() {
        return extraScreenType;
    }

    public void setExtraScreenType(AppCMSPresenter.ExtraScreenType extraScreenType) {
        this.extraScreenType = extraScreenType;
    }
}
