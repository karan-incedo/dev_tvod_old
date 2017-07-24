package com.viewlift.models.network.rest;

import android.app.DownloadManager;
import android.database.Cursor;

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
    @Inject
    public AppCMSUserDownloadVideoStatusCall() {

    }

    public void call(String videoId, AppCMSPresenter appCMSPresenter,
                     final Action1<UserVideoDownloadStatus> readyAction1) {
        try {
            DownloadVideoRealm downloadVideoRealm = appCMSPresenter.getRealmController().getDownloadById(videoId);
            if (downloadVideoRealm == null) {

                Observable.just((UserVideoDownloadStatus) null).subscribe(readyAction1);
                return;
            }

            DownloadManager downloadManager = appCMSPresenter.getDownloadManager();
            UserVideoDownloadStatus statusResponse = new UserVideoDownloadStatus();

            DownloadManager.Query query = new DownloadManager.Query();
            statusResponse.setVideoId_DM(downloadVideoRealm.getVideoId_DM());
            statusResponse.setVideoId(videoId);


            query.setFilterById(downloadVideoRealm.getVideoId_DM());

            Cursor cursor = downloadManager.query(query);
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
                        if (!downloadVideoRealm.getVideoFileURL().contains("file:///")
                                || !downloadVideoRealm.getLocalURI().contains("file:///")  ) {  // Checking if it is already updated condition will minimize updation in realm DB
                            String uriVideo = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            appCMSPresenter.getRealmController().editDownloadVideoUrl(downloadVideoRealm, uriVideo);        // Updating local mp4 URL by video Id
                            appCMSPresenter.updateDownloadedImage(videoId);                                                 // Updating local image URL by video Id
                        }

                        statusResponse.setDownloadStatus(DownloadStatus.STATUS_SUCCESSFUL);
                        break;
                    default:
                }

                Observable.just(statusResponse).subscribe(readyAction1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Observable.just((UserVideoDownloadStatus) null).subscribe(readyAction1);
        }
    }
}
