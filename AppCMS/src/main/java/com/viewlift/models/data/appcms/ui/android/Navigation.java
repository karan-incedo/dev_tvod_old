
package com.viewlift.models.data.appcms.ui.android;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Navigation {

    @SerializedName("primary")
    @Expose
    private List<Primary> primary = null;
    @SerializedName("user")
    @Expose
    private List<User> user = null;
    @SerializedName("footer")
    @Expose
    private List<Footer> footer = null;

    public List<Primary> getPrimary() {
        return primary;
    }

    public void setPrimary(List<Primary> primary) {
        this.primary = primary;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    public List<Footer> getFooter() {
        return footer;
    }

    public void setFooter(List<Footer> footer) {
        this.footer = footer;
    }
}
