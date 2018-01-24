package com.viewlift.views.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.viewlift.AppCMSApplication;
import com.viewlift.Audio.playback.AudioPlaylistHelper;
import com.viewlift.R;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.fragments.AppCMSPlayAudioFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppCMSPlayAudioActivity extends AppCompatActivity implements View.OnClickListener, AppCMSPlayAudioFragment.OnUpdateMetaChange {
    @BindView(R.id.media_route_button)
    ImageButton casting;
    @BindView(R.id.add_to_playlist)
    ImageView addToPlaylist;
    @BindView(R.id.download_audio)
    ImageButton downloadAudio;
    @BindView(R.id.share_audio)
    ImageView shareAudio;

    private AppCMSPresenter appCMSPresenter;
    private String audioData = "";
    private CastServiceProvider castProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_cmsplay_audio);
        ButterKnife.bind(this);
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
    protected void onResume() {
        super.onResume();

        if (appCMSPresenter == null) {
            appCMSPresenter = ((AppCMSApplication) getApplication())
                    .getAppCMSPresenterComponent()
                    .appCMSPresenter();
        }

        if (!BaseView.isTablet(this)) {
            appCMSPresenter.restrictPortraitOnly();
        } else {
            appCMSPresenter.unrestrictPortraitOnly();
        }
        appCMSPresenter.updateDownloadImageAndStartDownloadProcess(AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData(), downloadAudio,false);

    }

    private void launchAudioPlayer() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            final AppCMSPlayAudioFragment appCMSPlayAudioFragment =
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
            appCMSPresenter.getAudioDetail(AudioPlaylistHelper.getInstance().getCurrentMediaId(),
                    0, null, false, downloadAudio,false);
        }
        if (view == shareAudio) {
            AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
            ContentDatum currentAudio = AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData();
            if (appCMSMain != null &&
                    currentAudio != null &&
                    currentAudio.getAudioGist() != null &&
                    currentAudio.getAudioGist().getTitle() != null &&
                    currentAudio.getAudioGist().getPermalink() != null) {
                StringBuilder audioUrl = new StringBuilder();
                audioUrl.append(appCMSMain.getDomainName());
                audioUrl.append(currentAudio.getAudioGist().getPermalink());
                String[] extraData = new String[1];
                extraData[0] = audioUrl.toString();
                appCMSPresenter.launchButtonSelectedAction(currentAudio.getAudioGist().getPermalink(),
                        getString(R.string.app_cms_action_share_key),
                        currentAudio.getAudioGist().getTitle(),
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
        audioData = "" +metadata.getString(AudioPlaylistHelper.CUSTOM_METADATA_TRACK_PARAM_LINK);// metadata.getDescription().getTitle();
    }
}
