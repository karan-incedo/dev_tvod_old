package com.viewlift.views.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.viewlift.R;

/**
 * Created by viewlift on 10/2/17.
 */

public class AppCMSUpgradeFragment extends Fragment {
    public static AppCMSUpgradeFragment newInstance() {
        return new AppCMSUpgradeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upgrade_page, container, false);
        Button upgradeButton = view.findViewById(R.id.app_cms_upgrade_button);
        upgradeButton.setOnClickListener((v) -> {
            Intent googlePlayStoreUpgradeAppIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.google_play_store_upgrade_app_url,
                            getString(R.string.package_name))));
            startActivity(googlePlayStoreUpgradeAppIntent);
        });
        return view;
    }
}
