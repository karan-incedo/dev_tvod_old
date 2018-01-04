package com.viewlift.views.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by viewlift on 10/2/17.
 */

public class AppCMSUpgradeFragment extends Fragment {
    public static AppCMSUpgradeFragment newInstance() {
        return new AppCMSUpgradeFragment();
    }

    @BindView(R.id.app_cms_upgrade_textview)
    TextView upgradeTextView;

    @BindView(R.id.app_cms_upgrade_button)
    Button upgradeButton;

    private AppCMSPresenter appCMSPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upgrade_page, container, false);

        ButterKnife.bind(this, view);

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        if (appCMSPresenter != null) {
            int textColor = Color.parseColor(appCMSPresenter.getAppTextColor());
            int bgColor = Color.parseColor(appCMSPresenter.getAppBackgroundColor());
            upgradeTextView.setTextColor(textColor);
            upgradeButton.setTextColor(textColor);
            upgradeButton.setBackgroundColor(bgColor);

            upgradeTextView.setText(getString(R.string.app_cms_upgrade_textview_text,
                    getString(R.string.app_cms_app_version),
                    appCMSPresenter.getGooglePlayAppStoreVersion()));
        }

        upgradeButton.setOnClickListener((v) -> {
            Intent googlePlayStoreUpgradeAppIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.google_play_store_upgrade_app_url,
                            getString(R.string.package_name))));
            startActivity(googlePlayStoreUpgradeAppIntent);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (appCMSPresenter != null) {
            upgradeTextView.setText(getString(R.string.app_cms_upgrade_textview_text,
                    getString(R.string.app_cms_app_version),
                    appCMSPresenter.getGooglePlayAppStoreVersion()));
        }
    }
}
