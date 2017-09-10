package com.viewlift.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.customviews.BaseView;

/**
 * Created by viewlift on 9/10/17.
 */

public class AppCMSCCAvenueFragment extends DialogFragment {
    private AppCMSPresenter appCMSPresenter;

    public static AppCMSCCAvenueFragment newInstance(Context context,
                                                     AppCMSBinder appCMSBinder,
                                                     int textColor,
                                                     int bgColor,
                                                     int borderColor,
                                                     int buttonColor) {
        AppCMSCCAvenueFragment fragment = new AppCMSCCAvenueFragment();
        Bundle args = new Bundle();
        args.putBinder(context.getString(R.string.fragment_page_bundle_key), appCMSBinder);
        args.putInt(context.getString(R.string.app_cms_text_color_key), textColor);
        args.putInt(context.getString(R.string.app_cms_bg_color_key), bgColor);
        args.putInt(context.getString(R.string.app_cms_border_color_key), borderColor);
        args.putInt(context.getString(R.string.app_cms_button_color_key), buttonColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        Bundle args = getArguments();

        // These variables are used to apply the AppCMS theme
        int textColor = args.getInt(getContext().getString(R.string.app_cms_text_color_key));
        int bgColor = args.getInt(getContext().getString(R.string.app_cms_bg_color_key));
        int borderColor = args.getInt(getContext().getString(R.string.app_cms_border_color_key));
        int buttonColor = args.getInt(getContext().getString(R.string.app_cms_button_color_key));

        // Create your view here
        View view = inflater.inflate(R.layout.fragment_ccavenue, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!BaseView.isTablet(getContext())) {
            appCMSPresenter.unrestrictPortraitOnly();
        }
    }

    private void setBgColor(int bgColor, View view) {
        RelativeLayout appCMSNavigationMenuMainLayout =
                view.findViewById(R.id.app_cms_navigation_menu_main_layout);
        appCMSNavigationMenuMainLayout.setBackgroundColor(bgColor);
    }
}
