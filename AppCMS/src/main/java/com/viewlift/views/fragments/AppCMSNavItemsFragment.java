package com.viewlift.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSNavItemsAdapter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.customviews.BaseView;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/30/17.
 */

public class AppCMSNavItemsFragment extends DialogFragment {
    private AppCMSPresenter appCMSPresenter;

    public static AppCMSNavItemsFragment newInstance(Context context,
                                                     AppCMSBinder appCMSBinder,
                                                     int textColor,
                                                     int bgColor,
                                                     int borderColor) {
        AppCMSNavItemsFragment fragment = new AppCMSNavItemsFragment();
        Bundle args = new Bundle();
        args.putBinder(context.getString(R.string.fragment_page_bundle_key), appCMSBinder);
        args.putInt(context.getString(R.string.app_cms_text_color_key), textColor);
        args.putInt(context.getString(R.string.app_cms_bg_color_key), bgColor);
        args.putInt(context.getString(R.string.app_cms_border_color_key), borderColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();

        int textColor = args.getInt(getContext().getString(R.string.app_cms_text_color_key));
        int bgColor = args.getInt(getContext().getString(R.string.app_cms_bg_color_key));
        int borderColor = args.getInt(getContext().getString(R.string.app_cms_border_color_key));

        final AppCMSBinder appCMSBinder =
                ((AppCMSBinder) args.getBinder(getContext().getString(R.string.fragment_page_bundle_key)));
        View view = inflater.inflate(R.layout.fragment_menu_nav, container, false);
        RecyclerView navItemsList = (RecyclerView) view.findViewById(R.id.nav_items_list);
        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
        AppCMSNavItemsAdapter appCMSNavItemsAdapter = new AppCMSNavItemsAdapter(appCMSBinder.getNavigation(),
                appCMSBinder.isUserLoggedIn(),
                appCMSPresenter,
                textColor);
        navItemsList.setAdapter(appCMSNavItemsAdapter);
        if (!BaseView.isTablet(getContext())) {
            appCMSPresenter.restrictPortraitOnly();
        }

        LinearLayout appCMSNavLoginContainer = (LinearLayout) view.findViewById(R.id.app_cms_nav_login_container);
        if (appCMSPresenter.isUserLoggedIn(getContext())) {
            appCMSNavLoginContainer.setVisibility(View.GONE);
        } else {
            appCMSNavLoginContainer.setVisibility(View.VISIBLE);
            View appCMSNavItemsSeparatorView = view.findViewById(R.id.app_cms_nav_items_separator_view);
            appCMSNavItemsSeparatorView.setBackgroundColor(textColor);
            TextView appCMSNavItemsLoggedOutMessage = (TextView) view.findViewById(R.id.app_cms_nav_items_logged_out_message);
            appCMSNavItemsLoggedOutMessage.setTextColor(textColor);
            Button appCMSNavLoginButton = (Button) view.findViewById(R.id.app_cms_nav_login_button);
            appCMSNavLoginButton.setTextColor(textColor);
            appCMSNavLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appCMSPresenter.navigateToLoginPage();
                    appCMSPresenter.navigateToLoginPage();
                    dismiss();
                }
            });
            GradientDrawable loginBorder = new GradientDrawable();
            loginBorder.setShape(GradientDrawable.RECTANGLE);
            loginBorder.setStroke(getContext().getResources().getInteger(R.integer.app_cms_border_stroke_width), borderColor);
            loginBorder.setColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            appCMSNavLoginButton.setBackground(loginBorder);
        }

        ImageButton closeButton = (ImageButton) view.findViewById(R.id.app_cms_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appCMSPresenter != null) {
                    appCMSPresenter.setNavItemToCurrentAction(getActivity());
                }
                dismiss();
            }
        });

        setBgColor(bgColor);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setWindow();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
        appCMSPresenter.setNavItemToCurrentAction(getActivity());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setWindow();
    }

    private void setBgColor(int bgColor) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(bgColor));
            }
        }
    }

    private void setWindow() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(width, height);
                window.setGravity(Gravity.START);
            }
        }
    }
}
