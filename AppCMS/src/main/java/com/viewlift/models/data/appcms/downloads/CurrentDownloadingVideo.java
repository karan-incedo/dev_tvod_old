package com.viewlift.models.data.appcms.downloads;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by viewlift on 8/22/17.
 */

public class CurrentDownloadingVideo extends RealmObject {
    @PrimaryKey
    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
