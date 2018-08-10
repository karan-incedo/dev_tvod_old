package com.viewlift.models.data.appcms.downloads;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.viewlift.models.data.appcms.api.SubscriptionPlan;
import com.viewlift.models.data.appcms.beacon.OfflineBeaconData;
import com.viewlift.models.data.appcms.subscriptions.UserSubscriptionPlan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.RealmQuery;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by sandeep.singh on 7/18/2017.
 */

public class RealmController {
    private static RealmController instance;
    private Realm realm;

    private static final String TAG = "RealmController";

    public RealmController() {
        realm = Realm.getDefaultInstance();
    }

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null && activity != null && activity.getApplication() != null) {
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
        try {
            realm.beginTransaction();
            realm.delete(DownloadVideoRealm.class);
            realm.commitTransaction();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to clear all downloads: " + e.getMessage());
        }
    }

    public RealmResults<DownloadVideoRealm> getDownloads() {
        try {
            return realm.where(DownloadVideoRealm.class).findAllAsync();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get downloads: " + e.getMessage());
        }
        return null;
    }

    public RealmResults<DownloadVideoRealm> getDownloadsByUserIdAndMedia(String userId, String contentType) {
        try {
            return realm.where(DownloadVideoRealm.class)
                    .equalTo("userId", userId)
                    .equalTo("contentType", contentType)
                    .findAll();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get downloads by user ID: " + e.getMessage());
        }
        return null;
    }

    public boolean getDownloadMediaType(String contentType) {
        try {
            return realm.where(DownloadVideoRealm.class)
                    .equalTo("contentType", contentType)
                    .findAll().size() > 0 ? true : false;

        } catch (Exception e) {
            //Log.e(TAG, "Failed to get downloads by user ID: " + e.getMessage());
        }
        return false;
    }

    public RealmResults<DownloadVideoRealm> getDownloadesByUserId(String userId) {
        try {
            Log.e("RealmController", "LoggedIn user ID :" + userId);
            return realm.where(DownloadVideoRealm.class).equalTo("userId", userId).findAllAsync();
        } catch (Exception e) {
            realm = Realm.getDefaultInstance();
            getDownloadesByUserId(userId);
            e.printStackTrace();
        }
        return null;
    }

    public RealmResults<DownloadVideoRealm> getAllUnSyncedWithServer(String userId) {
        try {
            return realm.where(DownloadVideoRealm.class).equalTo("isSyncedWithServer", false)
                    .equalTo("userId", userId).greaterThanOrEqualTo("watchedTime",30).notEqualTo("mediaType", "AUDIO", Case.INSENSITIVE).findAll();
        } catch (NullPointerException e) {
            //Log.e(TAG, "Failed to get server sync status: " + e.getMessage());
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get server sync status: " + e.getMessage());
        }
        return null;
    }

    public RealmResults<DownloadVideoRealm> getAllUnfinishedDownloades(String userId) {
        try {
            String[] status = {String.valueOf(DownloadStatus.STATUS_FAILED),
                    String.valueOf(DownloadStatus.STATUS_PAUSED),
                    String.valueOf(DownloadStatus.STATUS_PENDING),
                    String.valueOf(DownloadStatus.STATUS_RUNNING)};
            return realm.where(DownloadVideoRealm.class).in("downloadStatus", status)
                    .equalTo("userId", userId).findAll();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get all unfinished downloads: " + e.getMessage());
        }
        return null;
    }

    public RealmResults<DownloadVideoRealm> getDownloadesByStatus(String status) {
        try {
            return realm.where(DownloadVideoRealm.class).contains("downloadStatus", status).findAll();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get downloads by status: " + e.getMessage());
        }
        return null;
    }

    public DownloadVideoRealm getDownloadById(String videoId) {
        try {
            return realm.where(DownloadVideoRealm.class).equalTo("videoId", videoId).findFirst();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get download by ID " + videoId + ": " + e.getMessage());
        }
        return null;
    }

    public DownloadClosedCaptionRealm getDownloadedByIdCCFile(String gistId){
        try {
            return realm.where(DownloadClosedCaptionRealm.class).equalTo("gistId", gistId).findFirst();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get download by ID " + videoId + ": " + e.getMessage());
        }
        return null;
    }

    public DownloadVideoRealm getDownloadByDMId(long dm_id) {
        try {
            return realm.where(DownloadVideoRealm.class).equalTo("videoId_DM", dm_id).findFirst();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get download by ID " + videoId + ": " + e.getMessage());
        }
        return null;
    }

    public RealmResults<DownloadVideoRealm> getDownloadsById(String videoId) {
        try {
            return realm.where(DownloadVideoRealm.class).equalTo("videoId", videoId).findAll();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get download by ID " + videoId + ": " + e.getMessage());
        }
        return null;
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
        try {
            DownloadVideoRealm downloadById = getDownloadById(videoId);
            return downloadById != null && getDownloadById(videoId).getDownloadStatus()
                    .equals(DownloadStatus.STATUS_SUCCESSFUL);
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get video ready to play offline: " + e.getMessage());
        }
        return false;
    }

    public boolean isCCFileAvailableForOffLine(String gistId){
        if (TextUtils.isEmpty(gistId)) {
            return false;
        }
        try {
            DownloadClosedCaptionRealm downloadById = getDownloadedByIdCCFile(gistId);
            return downloadById != null && getDownloadById(gistId).getDownloadStatus()
                    .equals(DownloadStatus.STATUS_SUCCESSFUL);
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get video ready to play offline: " + e.getMessage());
        }
        return false;
    }

    @WorkerThread
    public DownloadVideoRealm getDownloadByIdBelongstoUser(String videoId, String userId) {
        try {
            return realm.where(DownloadVideoRealm.class)
                    .beginGroup()
                    .equalTo("videoId", videoId)
                    .equalTo("userId", userId)
                    .endGroup()
                    .findFirst();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get download by ID belonging to user " + userId + " " + videoId + ": " + e.getMessage());
        }
        return null;
    }

    public void addCurrentDownloadTitle(CurrentDownloadingVideo currentDownloadingVideo) {
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(currentDownloadingVideo);
            realm.commitTransaction();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to add current download video: " + e.getMessage());
        }
    }

    public CurrentDownloadingVideo getCurrentDownloadTitle() {
        try {
            return realm.where(CurrentDownloadingVideo.class)
                    .findFirst();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get current current download title: " + e.getMessage());
        }
        return null;
    }

    public void removeCurrentDownloadTitle() {
        try {
            realm.beginTransaction();
            realm.delete(CurrentDownloadingVideo.class);
            realm.commitTransaction();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to remove current download title: " + e.getMessage());
        }
    }

    public void addDownload(DownloadVideoRealm downloadVideoRealm) {
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(downloadVideoRealm);
            realm.commitTransaction();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to add download: " + e.getMessage());
        }

    }

    public void addDownloadedCCFile(DownloadClosedCaptionRealm downloadClosedCaptionRealm){
        try{
            realm.beginTransaction();
            realm.insert(downloadClosedCaptionRealm);
            realm.commitTransaction();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public RealmResults<DownloadClosedCaptionRealm> getAllDownloadedCCFiles(String gistId) {
        try {
            if (realm.where(DownloadClosedCaptionRealm.class).count() > 0) {
                return realm.where(DownloadClosedCaptionRealm.class).equalTo("gistId",gistId).findAll();
            }
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get all subscription plans: " + e.getMessage());
        }
        return null;
    }

    public void updateDownload(DownloadVideoRealm downloadVideoRealm) {
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(downloadVideoRealm);
            realm.commitTransaction();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to update download: " + e.getMessage());
        }
    }

    public void addSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(subscriptionPlan);
            realm.commitTransaction();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to add subscription plan: " + e.getMessage());
        }
    }

    public void deleteSubscriptionPlans() {
        try {
            realm.beginTransaction();
            realm.delete(SubscriptionPlan.class);
            realm.commitTransaction();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to delete subscription plans: " + e.getMessage());
        }
    }

    public RealmResults<SubscriptionPlan> getAllSubscriptionPlans() {
        try {
            if (realm.where(SubscriptionPlan.class).count() > 0) {
                return realm.where(SubscriptionPlan.class).findAll();
            }
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get all subscription plans: " + e.getMessage());
        }
        return null;
    }

    public void addUserSubscriptionPlan(UserSubscriptionPlan userSubscriptionPlan) {
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(userSubscriptionPlan);
            realm.commitTransaction();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to add user subscription plan: " + e.getMessage());
        }
    }

    public RealmResults<UserSubscriptionPlan> getUserSubscriptionPlan(String userId) {
        try {
            if (realm.where(UserSubscriptionPlan.class).equalTo("userId", userId).count() > 0) {
                return realm.where(UserSubscriptionPlan.class).equalTo("userId", userId).distinct("userId").findAll();
            }
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get user subscription plan " + userId + ": " + e.getMessage());
        }
        return null;
    }

    public void updateDownloadInfo(String videoId, String filmUrl, String thumbUrl, String posterUrl,
                                   String subtitlesUrl, long totalSize, DownloadStatus status) {

        try {
            DownloadVideoRealm toEdit = realm.where(DownloadVideoRealm.class)
                    .equalTo("videoId", videoId).findFirst();

            if (!realm.isInTransaction())
                realm.beginTransaction();

            toEdit.setVideoSize(totalSize);
            toEdit.setVideoFileURL(thumbUrl);
            toEdit.setVideoImageUrl(thumbUrl);
            toEdit.setPosterFileURL(posterUrl);
            toEdit.setSubtitlesFileURL(subtitlesUrl);
            toEdit.setLocalURI(filmUrl);
            toEdit.setDownloadStatus(status);

            realm.copyToRealmOrUpdate(toEdit);
            realm.commitTransaction();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to update download info: " + e.getMessage());
        }
    }

    /**
     * This may be usefull in future when we try to implement "downloadedSoFar" value also
     *
     * @param videoId
     * @param filmUrl
     * @param thumbUrl
     * @param totalSize
     * @param downloadedSoFar
     * @param status
     */
    public void updateDownloadInfo(String videoId, String filmUrl, String thumbUrl, long totalSize,
                                   long downloadedSoFar, DownloadStatus status) {
        try {
            DownloadVideoRealm toEdit = realm.where(DownloadVideoRealm.class)
                    .equalTo("videoId", videoId).findFirst();

            if (!realm.isInTransaction())
                realm.beginTransaction();

            toEdit.setVideoSize(totalSize);
            toEdit.setVideo_Downloaded_so_far(downloadedSoFar);
            toEdit.setVideoFileURL(thumbUrl);
            toEdit.setVideoImageUrl(thumbUrl);
            toEdit.setLocalURI(filmUrl);
            toEdit.setDownloadStatus(status);

            realm.copyToRealmOrUpdate(toEdit);
            realm.commitTransaction();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to update download info: " + e.getMessage());
        }
    }

    public void removeFromDB(DownloadVideoRealm downloadVideoRealm,Action1 ccAction) {
        try {
            DownloadVideoRealm toEdit = realm.where(DownloadVideoRealm.class)
                    .beginGroup()
                    .equalTo("videoId", downloadVideoRealm.getVideoId())
                    .equalTo("userId", downloadVideoRealm.getUserId())
                    .endGroup()
                    .findFirst();

            if (!realm.isInTransaction())
                realm.beginTransaction();

            deleteVideoCCFiles(downloadVideoRealm.getVideoId(),ccAction);
            toEdit.deleteFromRealm();

            realm.commitTransaction();

        } catch (Exception e) {
            Log.e(TAG, "Failed to to remove video from database: " + e.getMessage());
        }
    }

    private void deleteVideoCCFiles(String gistId,Action1 ccAction){

        try {
            RealmResults<DownloadClosedCaptionRealm> all = realm.where(DownloadClosedCaptionRealm.class)
                    .equalTo("gistId", gistId)
                    .findAll();
            List<Long> enquelistData = new ArrayList<>();
            for(DownloadClosedCaptionRealm dc : all)
                enquelistData.add(dc.getCcFileEnqueueId());

            all.deleteAllFromRealm();
            Observable.just(enquelistData).doOnError(null).subscribe(ccAction);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void addOfflineBeaconData(OfflineBeaconData offlineBeaconData) {
        try {
            if (!realm.isInTransaction()) {
                realm.beginTransaction();
            }
            realm.insert(offlineBeaconData);
            realm.commitTransaction();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to add offline beacon data: " + e.getMessage());
        }
    }

    public RealmResults<OfflineBeaconData> getOfflineBeaconDataListByUser(String userId) {
        try {
            if (realm.where(OfflineBeaconData.class).equalTo("uid", userId).count() > 0) {
                return realm.where(OfflineBeaconData.class).equalTo("uid", userId).findAll();
            }
        } catch (Exception e) {
            //Log.e(TAG, "Failed to get offline beacond data list by user " + userId + ": " + e.getMessage());
        }
        return null;
    }

    public void deleteOfflineBeaconDataByUser(String userId) {
        try {
            if (!realm.isInTransaction()) {
                realm.beginTransaction();
            }
            RealmResults<OfflineBeaconData> resultsToDel = realm.where(OfflineBeaconData.class).equalTo("uid", userId).findAll();
            resultsToDel.deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            //Log.e(TAG, "Failed to delete offline beacon user data " + userId + " : " + e.getMessage());
        }
    }


    public void closeRealm() {
        realm.close();
        instance = null;
    }
}
