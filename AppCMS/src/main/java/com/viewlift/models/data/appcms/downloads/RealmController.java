package com.viewlift.models.data.appcms.downloads;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.viewlift.models.data.appcms.api.SubscriptionPlan;

import io.realm.Realm;
import io.realm.RealmConfiguration;
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
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);
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

    public RealmResults<DownloadVideoRealm> getDownloads() {

        return realm.where(DownloadVideoRealm.class).findAll();
    }

    public RealmResults<DownloadVideoRealm> getDownloadesByUserId(String userId) {

        return realm.where(DownloadVideoRealm.class).contains("userId", userId).findAll();
    }

    public RealmResults<DownloadVideoRealm> getAllUnfinishedDownloades() {

        String[] status = {String.valueOf(DownloadStatus.STATUS_FAILED),
                String.valueOf(DownloadStatus.STATUS_PAUSED),
                String.valueOf(DownloadStatus.STATUS_PENDING),
                String.valueOf(DownloadStatus.STATUS_RUNNING)};
        return realm.where(DownloadVideoRealm.class).in("downloadStatus", status).findAll();

    }

    public RealmResults<DownloadVideoRealm> getDownloadesByStatus(String status) {
        return realm.where(DownloadVideoRealm.class).contains("downloadStatus", status).findAll();
    }

    public DownloadVideoRealm getDownloadById(String videoId) {

        return realm.where(DownloadVideoRealm.class).equalTo("videoId", videoId).findFirst();
    }

    public DownloadVideoRealm getDownloadByIdBelongstoUser(String videoId, String userId) {


        return realm.where(DownloadVideoRealm.class)
                .beginGroup()
                .equalTo("videoId", videoId)
                .equalTo("userId", userId)
                .endGroup()
                .findFirst();

    }

    public void addDownload(DownloadVideoRealm downloadVideoRealm) {

        realm.beginTransaction();
        realm.insert(downloadVideoRealm);
        realm.commitTransaction();
    }

    public void addSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        realm.beginTransaction();
        realm.insert(subscriptionPlan);
        realm.commitTransaction();
    }

    public RealmResults<SubscriptionPlan> getAllSubscriptionPlans() {
        return realm.where(SubscriptionPlan.class).findAll();
    }

    public void updateDownloadInfo(String videoId, String filmUrl, String thumbUrl, long totlsize, DownloadStatus status) {
        DownloadVideoRealm toEdit = realm.where(DownloadVideoRealm.class)
                .equalTo("videoId", videoId).findFirst();

        if (!realm.isInTransaction())
            realm.beginTransaction();

        toEdit.setVideoSize(totlsize);
        toEdit.setVideoFileURL(thumbUrl);
        toEdit.setPosterImageUrl(thumbUrl);
        toEdit.setLocalURI(filmUrl);
        toEdit.setDownloadStatus(status);

        realm.copyToRealmOrUpdate(toEdit);
        realm.commitTransaction();

    }

    /**
     * This may be usefull in future when we try to implement "downloadedSoFar" value also
     *
     * @param videoId
     * @param filmUrl
     * @param thumbUrl
     * @param totlsize
     * @param downloadedSoFar
     * @param status
     */
    public void updateDownloadInfo(String videoId, String filmUrl, String thumbUrl, long totlsize, long downloadedSoFar, DownloadStatus status) {
        DownloadVideoRealm toEdit = realm.where(DownloadVideoRealm.class)
                .equalTo("videoId", videoId).findFirst();

        if (!realm.isInTransaction())
            realm.beginTransaction();

        toEdit.setVideoSize(totlsize);
        toEdit.setVideo_Downloaded_so_far(downloadedSoFar);
        toEdit.setVideoFileURL(thumbUrl);
        toEdit.setPosterImageUrl(thumbUrl);
        toEdit.setLocalURI(filmUrl);
        toEdit.setDownloadStatus(status);

        realm.copyToRealmOrUpdate(toEdit);
        realm.commitTransaction();

    }

    public void removeFromDB(DownloadVideoRealm downloadVideoRealm) {

        DownloadVideoRealm toEdit = realm.where(DownloadVideoRealm.class)
                .equalTo("videoId", downloadVideoRealm.getVideoId()).findFirst();

        if (!realm.isInTransaction())
            realm.beginTransaction();

        toEdit.deleteFromRealm();

        realm.commitTransaction();

    }

    public void closeRealm() {
        realm.close();
    }
}
