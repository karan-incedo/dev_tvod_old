package com.viewlift.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.Audio.AudioServiceHelper;
import com.viewlift.Audio.playback.AudioPlaylistHelper;
import com.viewlift.R;
import com.viewlift.casting.CastHelper;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.audio.AppCMSAudioDetailResult;
import com.viewlift.models.data.appcms.downloads.UserVideoDownloadStatus;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.fragments.AppCMSPlayAudioFragment;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class AppCMSPlayAudioActivity extends AppCompatActivity implements View.OnClickListener, AppCMSPlayAudioFragment.OnUpdateMetaChange {
    @BindView(R.id.media_route_button)
    ImageButton casting;
    @BindView(R.id.add_to_playlist)
    ImageView addToPlaylist;
    @BindView(R.id.download_audio)
    ImageButton downloadAudio;
    @BindView(R.id.share_audio)
    ImageView shareAudio;
    @BindView(R.id.ll_cross_icon)
    LinearLayout ll_cross_icon;
    AppCMSPlayAudioFragment appCMSPlayAudioFragment;
    private AppCMSPresenter appCMSPresenter;
    private CastServiceProvider castProvider;
    ContentDatum currentAudio;
    public static boolean isDownloading = true;
    private BroadcastReceiver networkConnectedReceiver;
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cmsplay_audio);
        ButterKnife.bind(this);
        if (appCMSPresenter == null) {
            appCMSPresenter = ((AppCMSApplication) getApplication())
                    .getAppCMSPresenterComponent()
                    .appCMSPresenter();
        }


        casting.setOnClickListener(this);
        addToPlaylist.setOnClickListener(this);
        downloadAudio.setOnClickListener(this);
        shareAudio.setOnClickListener(this);
        ll_cross_icon.setOnClickListener(this);

        launchAudioPlayer();
        setCasting();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;

        if (connectivityManager != null) {
            activeNetwork = connectivityManager.getActiveNetworkInfo();
        }

        networkConnectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                String pageId = "";
                if ((((appCMSPresenter.getCurrentActivity() instanceof AppCMSPlayVideoActivity)) || ((appCMSPresenter.getCurrentActivity() instanceof AppCMSPlayAudioActivity))) && appCMSPresenter.getCurrentPlayingVideo() != null && appCMSPresenter.isVideoDownloaded(appCMSPresenter.getCurrentPlayingVideo()) ) {
                    return;
                }
                if (appCMSPresenter.getNetworkConnectedState() && !isConnected) {
                    appCMSPresenter.setShowNetworkConnectivity(true);
                    appCMSPresenter.showNoNetworkConnectivityToast();
                } else {
                    appCMSPresenter.setShowNetworkConnectivity(false);
                    appCMSPresenter.cancelAlertDialog();
                }

            }
        };
    }

    private void setCasting() {
        castProvider = CastServiceProvider.getInstance(this);
        castProvider.setActivityInstance(this, casting);
        castProvider.onActivityResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkConnectedReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkConnectedReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        if (!BaseView.isTablet(this)) {
            appCMSPresenter.restrictPortraitOnly();
        } else {
            appCMSPresenter.unrestrictPortraitOnly();
        }
        checkAudioDownloadStatus();
    }

    private void checkAudioDownloadStatus(){
        currentAudio = AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData();
        if (currentAudio != null &&
                currentAudio.getGist() != null &&
                currentAudio.getGist().getId() != null &&
                appCMSPresenter.isVideoDownloaded(currentAudio.getGist().getId())) {
            downloadAudio.setImageResource(R.drawable.ic_downloaded_big);
            downloadAudio.setOnClickListener(null);
        } else {
            updateDownloadImageAndStartDownloadProcess(currentAudio, downloadAudio);

        }
    }

    private void launchAudioPlayer() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            appCMSPlayAudioFragment =
                    AppCMSPlayAudioFragment.newInstance(this);
            fragmentTransaction.add(R.id.app_cms_play_audio_page_container,
                    appCMSPlayAudioFragment,
                    getString(R.string.audio_fragment_tag_key));
           // fragmentTransaction.addToBackStack(getString(R.string.audio_fragment_tag_key));
            fragmentTransaction.commit();
        } catch (Exception e) {

        }
    }

    public void startDownloadPlaylist() {
        appCMSPresenter.askForPermissionToDownloadForPlaylist(true, new Action1<Boolean>() {
            @Override
            public void call(Boolean isStartDownload) {
                if (isStartDownload) {
                    isDownloading = true;
                    downloadAudio.setTag(true);
                    audioDownload(downloadAudio, currentAudio);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == casting) {

        }
        if (view == ll_cross_icon) {
            finish();
        }
        if (view == addToPlaylist) {
        }
        if (view == downloadAudio) {
            startDownloadPlaylist();
        }
        if (view == shareAudio) {
            AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
            ContentDatum currentAudio = AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData();
            if (appCMSMain != null &&
                    currentAudio != null &&
                    currentAudio.getGist() != null &&
                    currentAudio.getGist().getTitle() != null &&
                    currentAudio.getGist().getPermalink() != null) {
                StringBuilder audioUrl = new StringBuilder();
                audioUrl.append(appCMSMain.getDomainName());
                audioUrl.append(currentAudio.getGist().getPermalink());
                String[] extraData = new String[1];
                extraData[0] = audioUrl.toString();
                appCMSPresenter.launchButtonSelectedAction(currentAudio.getGist().getPermalink(),
                        getString(R.string.app_cms_action_share_key),
                        currentAudio.getGist().getTitle(),
                        extraData,
                        currentAudio,
                        false,
                        0,
                        null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // finish();
    }


    @Override
    public void updateMetaData(MediaMetadataCompat metadata) {
        String audioData = "" + metadata.getString(AudioPlaylistHelper.CUSTOM_METADATA_TRACK_PARAM_LINK);// metadata.getDescription().getTitle();
        checkAudioDownloadStatus();
    }


    void audioDownload(ImageButton download, ContentDatum data) {
        appCMSPresenter.getAudioDetail(data.getGist().getId(),
                0, null, false, false, 0,
                new AppCMSPresenter.AppCMSAudioDetailAPIAction(false,
                        false,
                        false,
                        null,
                        data.getGist().getId(),
                        data.getGist().getId(),
                        null,
                        data.getGist().getId(),
                        false, null) {
                    @Override
                    public void call(AppCMSAudioDetailResult appCMSAudioDetailResult) {
                        AppCMSPageAPI audioApiDetail = appCMSAudioDetailResult.convertToAppCMSPageAPI(data.getGist().getId());
                        updateDownloadImageAndStartDownloadProcess(audioApiDetail.getModules().get(0).getContentData().get(0), download);
                        download.performClick();
                        /*if ((boolean) download.getTag()) {
                            isDownloading = false;
                            download.setTag(false);
                            download.performClick();
                        }*/
                    }
                });


    }

    void updateDownloadImageAndStartDownloadProcess(ContentDatum contentDatum, ImageButton downloadView) {
        String userId = appCMSPresenter.getLoggedInUser();
        Map<String, ViewCreator.UpdateDownloadImageIconAction> updateDownloadImageIconActionMap =
                appCMSPresenter.getUpdateDownloadImageIconActionMap();
        try {
            int radiusDifference = 5;
            if (BaseView.isTablet(getApplicationContext())) {
                radiusDifference = 2;
            }
            ViewCreator.UpdateDownloadImageIconAction updateDownloadImageIconAction =
                    updateDownloadImageIconActionMap.get(contentDatum.getGist().getId());
            if (updateDownloadImageIconAction == null) {
                updateDownloadImageIconAction = new ViewCreator.UpdateDownloadImageIconAction(downloadView, appCMSPresenter,
                        contentDatum, userId, radiusDifference, userId);
                updateDownloadImageIconActionMap.put(contentDatum.getGist().getId(), updateDownloadImageIconAction);
            }

            downloadView.setTag(contentDatum.getGist().getId());

            updateDownloadImageIconAction.updateDownloadImageButton(downloadView);
            updateDownloadImageIconAction.updateContentData(contentDatum);

            appCMSPresenter.getUserVideoDownloadStatus(
                    contentDatum.getGist().getId(), updateDownloadImageIconAction, userId);
        } catch (Exception e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppCMSPresenter.REQUEST_WRITE_EXTERNAL_STORAGE_FOR_DOWNLOADS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    appCMSPresenter.resumeDownloadAfterPermissionGranted();
                }
                break;

            default:
                break;
        }
    }

}
