package com.viewlift.models.data.appcms.downloads;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sandeep.singh on 7/18/2017.
 */

public class RealmController {
    private static RealmController instance;
    private final Realm realm;

    public RealmController() {
        realm = Realm.getDefaultInstance();
    }

    public RealmController(Application application) {
        Realm.init(application.getApplicationContext());
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {
        if (instance == null) {
            instance = new RealmController();
        }
        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    public void refresh() {
        realm.refresh();
    }

    public void clearAllDownloads() {
        realm.beginTransaction();
        realm.delete(DownloadVideoRealm.class);
        realm.commitTransaction();
    }

    public RealmResults<DownloadVideoRealm> getDownloades() {

        return realm.where(DownloadVideoRealm.class).findAll();
    }

    public RealmResults<DownloadVideoRealm> getDownloadesByStatus(String status) {

        return realm.where(DownloadVideoRealm.class).contains("downloadStatus", status).findAll();
    }

    public DownloadVideoRealm getDownloadById(String videoId) {

        return realm.where(DownloadVideoRealm.class).equalTo("videoId", videoId).findFirst();
    }

    public void addDownload(DownloadVideoRealm downloadVideoRealm){

        realm.beginTransaction();
        realm.copyToRealm(downloadVideoRealm);
        realm.commitTransaction();
    }

    public void editDownloadVideoImageURL(DownloadVideoRealm downloadVideoRealm, String url){

        DownloadVideoRealm toEdit = realm.where(DownloadVideoRealm.class)
                .equalTo("videoId", downloadVideoRealm.getVideoId()).findFirst();

        if (!realm.isInTransaction())
            realm.beginTransaction();

        toEdit.setVideoId_DM(downloadVideoRealm.getVideoId_DM());

        toEdit.setVideoTitle(downloadVideoRealm.getVideoTitle());
        toEdit.setVideoDescription(downloadVideoRealm.getVideoDescription());
        toEdit.setLocalURI(downloadVideoRealm.getLocalURI());
        toEdit.setVideoWebURL(downloadVideoRealm.getVideoWebURL());
        toEdit.setPermalink(downloadVideoRealm.getPermalink());
        toEdit.setVideoFileURL(url);
        toEdit.setPosterImageUrl(url);

        realm.copyToRealmOrUpdate(toEdit);
        realm.commitTransaction();
    }

    public void editDownloadVideoUrl(DownloadVideoRealm downloadVideoRealm, String url){
        DownloadVideoRealm toEdit = realm.where(DownloadVideoRealm.class)
                .equalTo("videoId", downloadVideoRealm.getVideoId()).findFirst();

        if (!realm.isInTransaction())
                realm.beginTransaction();



        toEdit.setVideoId_DM(downloadVideoRealm.getVideoId_DM());

        toEdit.setVideoTitle(downloadVideoRealm.getVideoTitle());
        toEdit.setVideoDescription(downloadVideoRealm.getVideoDescription());

        toEdit.setVideoWebURL(downloadVideoRealm.getVideoWebURL());
        toEdit.setPermalink(downloadVideoRealm.getPermalink());
        toEdit.setVideoFileURL(downloadVideoRealm.getVideoFileURL());
        toEdit.setPosterImageUrl(downloadVideoRealm.getPosterImageUrl());

        toEdit.setLocalURI(url);

        realm.copyToRealmOrUpdate(toEdit);
        realm.commitTransaction();
    }
}