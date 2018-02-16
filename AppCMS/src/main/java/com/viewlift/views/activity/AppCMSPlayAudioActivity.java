package com.viewlift.views.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.viewlift.AppCMSApplication;
import com.viewlift.Audio.playback.AudioPlaylistHelper;
import com.viewlift.R;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.mobile.AppCMSLaunchActivity;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.audio.AppCMSAudioDetailResult;
import com.viewlift.models.data.appcms.downloads.UserVideoDownloadStatus;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.fragments.AppCMSPlayAudioFragment;

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
    AppCMSPlayAudioFragment appCMSPlayAudioFragment;
    private AppCMSPresenter appCMSPresenter;
    private String audioData = "";
    private CastServiceProvider castProvider;
    ContentDatum currentAudio;
    public static boolean isDownloading = true;

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
        launchAudioPlayer();
        setCasting();
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
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("on destroy audio player");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!BaseView.isTablet(this)) {
            appCMSPresenter.restrictPortraitOnly();
        } else {
            appCMSPresenter.unrestrictPortraitOnly();
        }
        currentAudio = AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData();
        if (appCMSPresenter.isVideoDownloaded(currentAudio.getGist().getId())) {
            downloadAudio.setImageResource(R.drawable.ic_downloaded);
            downloadAudio.setOnClickListener(null);
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
            fragmentTransaction.addToBackStack(getString(R.string.audio_fragment_tag_key));
            fragmentTransaction.commit();
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View view) {
        if (view == casting) {

        }
        if (view == addToPlaylist) {
        }
        if (view == downloadAudio) {
            isDownloading=true;
            audioDownload(downloadAudio, currentAudio);
            appCMSPresenter.getAudioDetail(AudioPlaylistHelper.getInstance().getCurrentMediaId(),
                    0, null, false, false, null);
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
        finish();
    }


    @Override
    public void updateMetaData(MediaMetadataCompat metadata) {
        audioData = "" + metadata.getString(AudioPlaylistHelper.CUSTOM_METADATA_TRACK_PARAM_LINK);// metadata.getDescription().getTitle();
    }


    void audioDownload(ImageButton download, ContentDatum data) {
        appCMSPresenter.getAudioDetail(data.getGist().getId(),
                0, null, false, false,
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
                    }
                });


    }

    void updateDownloadImageAndStartDownloadProcess(ContentDatum contentDatum, ImageButton downloadView) {
        String userId = appCMSPresenter.getLoggedInUser();
        appCMSPresenter.getUserVideoDownloadStatus(
                contentDatum.getGist().getId(),
                new UpdateDownloadImageIconAction(downloadView,
                        appCMSPresenter,
                        contentDatum, userId), userId);
    }

    /**
     * This class has been created to updated the Download Image Action and Status
     */
    private static class UpdateDownloadImageIconAction implements Action1<UserVideoDownloadStatus> {
        private final AppCMSPresenter appCMSPresenter;
        private final ContentDatum contentDatum;
        private final String userId;
        private ImageButton imageButton;
        private View.OnClickListener addClickListener;

        UpdateDownloadImageIconAction(ImageButton imageButton, AppCMSPresenter presenter,
                                      ContentDatum contentDatum, String userId) {
            this.imageButton = imageButton;
            this.appCMSPresenter = presenter;
            this.contentDatum = contentDatum;
            this.userId = userId;

            addClickListener = v -> {
                if (!appCMSPresenter.isNetworkConnected()) {
                    if (!appCMSPresenter.isUserLoggedIn()) {
                        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK, null, false,
                                appCMSPresenter::launchBlankPage,
                                null);
                        return;
                    }
                    appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                            appCMSPresenter.getNetworkConnectivityDownloadErrorMsg(),
                            true,
                            () -> appCMSPresenter.navigateToDownloadPage(appCMSPresenter.getDownloadPageId(),
                                    null, null, false),
                            null);
                    return;
                }
                if ((appCMSPresenter.isUserSubscribed()) &&
                        appCMSPresenter.isUserLoggedIn()) {
                    appCMSPresenter.editDownload(UpdateDownloadImageIconAction.this.contentDatum, UpdateDownloadImageIconAction.this, true);
                } else {
                    appCMSPresenter.setAudioPlayerOpen(true);
                    if (appCMSPresenter.isUserLoggedIn()) {
                        appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.SUBSCRIPTION_REQUIRED_AUDIO,
                                () -> {
                                    appCMSPresenter.setAfterLoginAction(() -> {
                                        System.out.println("After login action");

                                    });
                                });
                    } else {
                        appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED_AUDIO,
                                () -> {
                                    appCMSPresenter.setAfterLoginAction(() -> {

                                    });
                                });
                    }
                }
                imageButton.setOnClickListener(null);
            }

            ;
        }


        @Override
        public void call(UserVideoDownloadStatus userVideoDownloadStatus) {
            if (userVideoDownloadStatus != null) {

                switch (userVideoDownloadStatus.getDownloadStatus()) {
                    case STATUS_FAILED:
                        appCMSPresenter.setDownloadInProgress(false);
                        appCMSPresenter.startNextDownload();
                        break;

                    case STATUS_PAUSED:
                        //
                        break;

                    case STATUS_PENDING:
                        appCMSPresenter.setDownloadInProgress(false);
                        appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false);
                        imageButton.setOnClickListener(null);
                        break;

                    case STATUS_RUNNING:
                        appCMSPresenter.setDownloadInProgress(true);
                        appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false);
                        imageButton.setOnClickListener(null);
                        break;

                    case STATUS_SUCCESSFUL:
                        appCMSPresenter.setDownloadInProgress(false);
                        appCMSPresenter.cancelDownloadIconTimerTask(contentDatum.getGist().getId());
                        imageButton.setImageResource(R.drawable.ic_downloaded);
                        imageButton.setOnClickListener(null);
                        appCMSPresenter.notifyDownloadHasCompleted();
                        break;

                    case STATUS_INTERRUPTED:
                        appCMSPresenter.setDownloadInProgress(false);
                        imageButton.setImageResource(android.R.drawable.stat_sys_warning);
                        imageButton.setOnClickListener(null);
                        break;

                    default:
                        //Log.d(TAG, "No download Status available ");
                        break;
                }

            } else {
                appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                        UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false);
                imageButton.setImageResource(R.drawable.ic_download);
                imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                int fillColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor());
                imageButton.getDrawable().setColorFilter(new PorterDuffColorFilter(fillColor, PorterDuff.Mode.MULTIPLY));
//                imageButton.setOnClickListener(addClickListener);
                if (isDownloading) {
                    isDownloading = false;
                    addClickListener.onClick(imageButton);
                }
            }
        }

        public void updateDownloadImageButton(ImageButton imageButton) {
            this.imageButton = imageButton;
        }

    }
}
