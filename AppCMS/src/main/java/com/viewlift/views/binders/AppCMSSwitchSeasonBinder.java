package com.viewlift.views.binders;

import android.os.Binder;

import com.viewlift.models.data.appcms.api.Season_;
import com.viewlift.models.data.appcms.ui.page.Component;

import java.util.List;

/**
 * Created by anas.azeem on 1/4/2018.
 * Owned by ViewLift, NYC
 */

public class AppCMSSwitchSeasonBinder extends Binder {
    private List<Season_> seasonList;
    private List<String> relatedVideoList;
    private Component component;
    private String action;
    private String blockName;
    private int trayIndex;
    private int selectedSeasonIndex;

    public AppCMSSwitchSeasonBinder(List<Season_> contentDatumList,
                                    List<String> relatedVideoList,
                                    Component component,
                                    String action,
                                    String blockName,
                                    int trayIndex,
                                    int selectedSeasonIndex) {
        this.seasonList = contentDatumList;
        this.relatedVideoList = relatedVideoList;
        this.component = component;
        this.action = action;
        this.blockName = blockName;
        this.trayIndex = trayIndex;
        this.selectedSeasonIndex = selectedSeasonIndex;
    }

    public List<Season_> getSeasonList() {
        return seasonList;
    }

    public void setSeasonList(List<Season_> seasonList) {
        this.seasonList = seasonList;
    }

    public List<String> getRelatedVideoList() {
        return relatedVideoList;
    }

    public void setRelatedVideoList(List<String> relatedVideoList) {
        this.relatedVideoList = relatedVideoList;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public int getTrayIndex() {
        return trayIndex;
    }

    public void setTrayIndex(int trayIndex) {
        this.trayIndex = trayIndex;
    }

    public int getSelectedSeasonIndex() {
        return selectedSeasonIndex;
    }

    public void setSelectedSeasonIndex(int selectedSeasonIndex) {
        this.selectedSeasonIndex = selectedSeasonIndex;
    }
}
