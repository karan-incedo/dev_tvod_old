package com.viewlift.models.data.appcms.downloads;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by sandeep.singh on 7/18/2017.
 */

public class DownloadedVideos extends RealmObject {
    private RealmList<DownloadVideoRealm> videoList;

    public RealmList<DownloadVideoRealm> getVideoList() {
        return videoList;
    }
}
