
package com.viewlift.models.data.appcms.ui.page;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModuleList implements Module {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("layout")
    @Expose
    private Layout layout;
    @SerializedName("settings")
    @Expose
    private Settings settings;
    @SerializedName("view")
    @Expose
    private String view;
    @SerializedName("components")
    @Expose
    private List<Component> components  ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

}
