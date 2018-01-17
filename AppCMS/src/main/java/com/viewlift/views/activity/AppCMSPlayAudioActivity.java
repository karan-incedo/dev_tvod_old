package com.viewlift.views.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.fragments.AppCMSPlayAudioFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppCMSPlayAudioActivity extends AppCompatActivity implements View.OnClickListener, AppCMSPlayAudioFragment.OnUpdateMetaChange {
    @BindView(R.id.audio_player_back)
    LinearLayout activityBack;
    @BindView(R.id.media_route_button)
    ImageButton casting;
    @BindView(R.id.add_to_playlist)
    ImageView addToPlaylist;
    @BindView(R.id.download_audio)
    ImageView downloadAudio;
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
        activityBack.setOnClickListener(this);
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
        if (view == activityBack) {
        }
        if (view == casting) {
        }
        if (view == addToPlaylist) {
        }
        if (view == downloadAudio) {
        }
        if (view == shareAudio) {
            shareAction(audioData);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void shareAction(String shareData) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareData);
        sendIntent.setType(getString(R.string.text_plain_mime_type));
        Intent chooserIntent = Intent.createChooser(sendIntent,
                getResources().getText(R.string.send_to));
        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(chooserIntent);

    }

    @Override
    public void updateMetaData(MediaMetadataCompat metadata) {
        audioData = "" + metadata.getDescription().getTitle();
    }
}
