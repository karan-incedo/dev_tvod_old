package com.viewlift.models.data.appcms.downloads;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.viewlift.models.data.appcms.api.SubscriptionPlan;
import com.viewlift.models.data.appcms.subscriptions.AppCMSUserSubscriptionPlanResult;
import com.viewlift.models.data.appcms.subscriptions.UserSubscriptionPlan;

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

    public RealmResults<DownloadVideoRealm> getAllUnfinishedDownloades(String userId) {

        String[] status = {String.valueOf(DownloadStatus.STATUS_FAILED),
                String.valueOf(DownloadStatus.STATUS_PAUSED),
                String.valueOf(DownloadStatus.STATUS_PENDING),
                String.valueOf(DownloadStatus.STATUS_RUNNING)};
        return realm.where(DownloadVideoRealm.class).in("downloadStatus", status)
                .equalTo("userId",userId).findAll();

    }

    public RealmResults<DownloadVideoRealm> getDownloadesByStatus(String status) {
        return realm.where(DownloadVideoRealm.class).contains("downloadStatus", status).findAll();
    }

    public DownloadVideoRealm getDownloadById(String videoId) {

        return realm.where(DownloadVideoRealm.class).equalTo("videoId", videoId).findFirst();
    }

    /**
     * Use this method to know if a video is available and completely downloaded.
     *
     * @param videoId id of the video you need to get information about
     * @return true if the video is available and ready to play, false otherwise
     */
    public boolean isVideoReadyToPlayOffline(String videoId) {
        if (TextUtils.isEmpty(videoId)) {
            return false;
        }
        DownloadVideoRealm downloadById = getDownloadById(videoId);
        return downloadById != null && getDownloadById(videoId).getDownloadStatus()
                .equals(DownloadStatus.STATUS_SUCCESSFUL);
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

    public void updateDownload(DownloadVideoRealm downloadVideoRealm) {
        realm.beginTransaction();
        realm.insertOrUpdate(downloadVideoRealm);
        realm.commitTransaction();
    }

    public void addSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        realm.beginTransaction();
        realm.insertOrUpdate(subscriptionPlan);
        realm.commitTransaction();
    }

    public RealmResults<SubscriptionPlan> getAllSubscriptionPlans() {
        if (realm.where(SubscriptionPlan.class).count() > 0) {
            return realm.where(SubscriptionPlan.class).findAll();
        }
        return null;
    }

    public void addUserSubscriptionPlan(UserSubscriptionPlan userSubscriptionPlan) {
        realm.beginTransaction();
        realm.insertOrUpdate(userSubscriptionPlan);
        realm.commitTransaction();
    }

    public RealmResults<UserSubscriptionPlan> getUserSubscriptionPlan(String userId) {
        if (realm.where(UserSubscriptionPlan.class).equalTo("userId", userId).count() > 0) {
            return realm.where(UserSubscriptionPlan.class).equalTo("userId", userId).distinct("userId");
        }
        return null;
    }

    public void updateDownloadInfo(String videoId, String filmUrl, String thumbUrl, String posterUrl, String subtitlesUrl, long totlsize, DownloadStatus status) {
        DownloadVideoRealm toEdit = realm.where(DownloadVideoRealm.class)
                .equalTo("videoId", videoId).findFirst();

        if (!realm.isInTransaction())
            realm.beginTransaction();

        toEdit.setVideoSize(totlsize);
        toEdit.setVideoFileURL(thumbUrl);
        toEdit.setVideoImageUrl(thumbUrl);
        toEdit.setPosterFileURL(posterUrl);
        toEdit.setSubtitlesFileURL(subtitlesUrl);
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
        toEdit.setVideoImageUrl(thumbUrl);
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
        instance = null;
    }
}
