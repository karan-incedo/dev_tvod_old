package com.viewlift.models.network.rest;

import android.app.DownloadManager;
import android.database.Cursor;
import android.support.annotation.UiThread;
import android.util.Log;

import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.models.data.appcms.downloads.DownloadVideoRealm;
import com.viewlift.models.data.appcms.downloads.UserVideoDownloadStatus;
import com.viewlift.presenters.AppCMSPresenter;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by sandeep.singh on 7/18/2017.
 */

public class AppCMSUserDownloadVideoStatusCall {
    private static final String TAG = "DownloadStatusCallTAG_";

    @Inject
    public AppCMSUserDownloadVideoStatusCall() {
        //
    }

    @UiThread
    public void call(String videoId, AppCMSPresenter appCMSPresenter,
                     final Action1<UserVideoDownloadStatus> readyAction1, String userId) {
        DownloadVideoRealm downloadVideoRealm=null;
        UserVideoDownloadStatus statusResponse = new UserVideoDownloadStatus();

        Cursor cursor = null;

        try {
             downloadVideoRealm = appCMSPresenter.getRealmController()
                    .getDownloadByIdBelongstoUser(videoId, userId);
            if (downloadVideoRealm == null) {

                Observable.just((UserVideoDownloadStatus) null)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(readyAction1);
                return;
            }

            DownloadManager downloadManager = appCMSPresenter.getDownloadManager();


            statusResponse.setVideoId_DM(downloadVideoRealm.getVideoId_DM());
            statusResponse.setVideoId(videoId);

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadVideoRealm.getVideoId_DM());

            cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);

                int status = cursor.getInt(columnIndex);

                switch (status) {
                    case DownloadManager.STATUS_FAILED:
                        statusResponse.setDownloadStatus(DownloadStatus.STATUS_FAILED);
                        break;

                    case DownloadManager.STATUS_PAUSED:
                        statusResponse.setDownloadStatus(DownloadStatus.STATUS_PAUSED);
                        break;

                    case DownloadManager.STATUS_RUNNING:
                        statusResponse.setDownloadStatus(DownloadStatus.STATUS_RUNNING);
                        break;

                    case DownloadManager.STATUS_PENDING:
                        statusResponse.setDownloadStatus(DownloadStatus.STATUS_PENDING);
                        break;

                    case DownloadManager.STATUS_SUCCESSFUL:
                        String uriVideo = cursor.getString(cursor
                                .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        long totalSize = cursor.getLong(cursor
                                .getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                        appCMSPresenter.getRealmController().updateDownloadInfo(videoId,
                                uriVideo,
                                appCMSPresenter.downloadedMediaLocalURI(downloadVideoRealm.getVideoThumbId_DM()),
                                appCMSPresenter.downloadedMediaLocalURI(downloadVideoRealm.getPosterThumbId_DM()),
                                appCMSPresenter.downloadedMediaLocalURI(downloadVideoRealm.getSubtitlesId_DM()),
                                totalSize,
                                DownloadStatus.STATUS_SUCCESSFUL);

                        statusResponse.setDownloadStatus(DownloadStatus.STATUS_SUCCESSFUL);
                        statusResponse.setVideoSize(totalSize);
                        statusResponse.setThumbUri(appCMSPresenter
                                .downloadedMediaLocalURI(downloadVideoRealm.getVideoThumbId_DM()));
                        statusResponse.setVideoUri(appCMSPresenter
                                .downloadedMediaLocalURI(downloadVideoRealm.getVideoId_DM()));
                        statusResponse.setPosterUri(appCMSPresenter
                                .downloadedMediaLocalURI(downloadVideoRealm.getPosterThumbId_DM()));
                        statusResponse.setSubtitlesUri(appCMSPresenter
                                .downloadedMediaLocalURI(downloadVideoRealm.getSubtitlesId_DM()));
                        break;

                    default:
                        break;
                }

                Observable.just(statusResponse)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(readyAction1);
            }
            else{
                if (null != downloadVideoRealm ) { // fix of SVFA-1856
                    statusResponse.setDownloadStatus(DownloadStatus.STATUS_INTERRUPTED);
                    Observable.just(statusResponse)
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(readyAction1);
                    return;
                }
            }

        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
            Observable.just((UserVideoDownloadStatus) null)
                    .onErrorResumeNext(throwable -> Observable.empty())
                    .subscribe(readyAction1);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
