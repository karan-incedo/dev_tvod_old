package com.viewlift.views.fragments;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.page.Links;
import com.viewlift.models.data.appcms.ui.page.SocialLinks;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.MoreMenuDialogAdapter;

import java.util.ArrayList;

/**
 * Created by ram.kailash on 10/10/2017.
 */

public class AppCMSMoreMenuDialogFragment extends DialogFragment implements View.OnClickListener {

    private AppCMSPresenter appCMSPresenter;
    ArrayList<Links> links;

    public static AppCMSMoreMenuDialogFragment newInstance(ArrayList<Links> links) {
        AppCMSMoreMenuDialogFragment appCMSMoreMenuDialogFragment = new AppCMSMoreMenuDialogFragment();
        appCMSMoreMenuDialogFragment.links = links;
        return appCMSMoreMenuDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.more_menu_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView dialogList = (ListView) view.findViewById(R.id.list_view_dialog);
        Button closeBtn = (Button) view.findViewById(R.id.close_button);
        dialogList.setAdapter(new MoreMenuDialogAdapter(appCMSPresenter, links));

        closeBtn.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                .getCta().getPrimary().getTextColor()));
        closeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }


}
