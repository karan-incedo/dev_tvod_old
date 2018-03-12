package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.ArrayList;

@UseStag
public class ModuleList implements ModuleWithComponents, Serializable {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("layout")
    @Expose
    Layout layout;

    @SerializedName("settings")
    @Expose
    Settings settings;

    @SerializedName("view")
    @Expose
    String view;

    @SerializedName("components")
    @JsonAdapter(ComponentListSerializerDeserializer.class)
    @Expose
    ArrayList<Component> components;

    @SerializedName("key")
    @Expose
    String key;

    @SerializedName("value")
    @Expose
    int value;


    @SerializedName("alpha")
    @Expose
    float alpha;

    @SerializedName("type")
    @Expose
    String type;

    @SerializedName("svod")
    @Expose
    boolean svod;

    @SerializedName("blockName")
    @Expose
    String blockName;


    @SerializedName("isTabSeparator")
    @Expose
    boolean isTabSeparator;

    @SerializedName("tabSeparator-color")
    @Expose
    String tabSeparator_color;

    @SerializedName("isBackgroundSelectable")
    @Expose
    boolean isBackgroundSelectable;


    @SerializedName("isSelectable")
    @Expose
    boolean isSelectable;

    public int getModulePosition() {
        return modulePosition;
    }

    public void setModulePosition(int modulePosition) {
        this.modulePosition = modulePosition;
    }

    int modulePosition;

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

    public ArrayList<Component> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<Component> components) {
        this.components = components;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean isSvod() {
        return svod;
    }

    public void setSvod(boolean svod) {
        this.svod = svod;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public boolean isTabSeparator() {
        return isTabSeparator;
    }

    public void setTabSeparator(boolean tabSeparator) {
        isTabSeparator = tabSeparator;
    }

    public String getTabSeparator_color() {
        return tabSeparator_color;
    }

    public void setTabSeparator_color(String tabSeparator_color) {
        this.tabSeparator_color = tabSeparator_color;
    }

    public boolean isBackgroundSelectable() {
        return isBackgroundSelectable;
    }

    public void setBackgroundSelectable(boolean backgroundSelectable) {
        isBackgroundSelectable = backgroundSelectable;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
    }
}
