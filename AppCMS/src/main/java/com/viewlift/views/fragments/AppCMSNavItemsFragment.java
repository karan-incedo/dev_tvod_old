package com.viewlift.views.fragments;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSNavItemsAdapter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.customviews.BaseView;

import com.viewlift.R;

/**
 * Created by viewlift on 5/30/17.
 */

public class AppCMSNavItemsFragment extends DialogFragment {
    private AppCMSPresenter appCMSPresenter;
    private AppCMSBinder appCMSBinder;
    private AppCMSNavItemsAdapter appCMSNavItemsAdapter;

    public static AppCMSNavItemsFragment newInstance(Context context,
                                                     AppCMSBinder appCMSBinder,
                                                     int textColor,
                                                     int bgColor,
                                                     int borderColor,
                                                     int buttonColor) {
        AppCMSNavItemsFragment fragment = new AppCMSNavItemsFragment();
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
        Bundle args = getArguments();

        int textColor = args.getInt(getContext().getString(R.string.app_cms_text_color_key));
        int bgColor = args.getInt(getContext().getString(R.string.app_cms_bg_color_key));
        int borderColor = args.getInt(getContext().getString(R.string.app_cms_border_color_key));
        int buttonColor = args.getInt(getContext().getString(R.string.app_cms_button_color_key));

        appCMSBinder =
                ((AppCMSBinder) args.getBinder(getContext().getString(R.string.fragment_page_bundle_key)));
        View view = inflater.inflate(R.layout.fragment_menu_nav, container, false);
        RecyclerView navItemsList = (RecyclerView) view.findViewById(R.id.nav_items_list);
        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
        appCMSNavItemsAdapter = new AppCMSNavItemsAdapter(appCMSBinder.getNavigation(),
                appCMSPresenter,
                appCMSBinder.getJsonValueKeyMap(),
                appCMSBinder.isUserLoggedIn(),
                appCMSBinder.isUserSubscribed(),
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
                    if (appCMSPresenter != null) {
                        appCMSPresenter.setLaunchType(AppCMSPresenter.LaunchType.LOGIN_AND_SIGNUP);
                        appCMSPresenter.navigateToLoginPage();
                    }
                }
            });
            GradientDrawable loginBorder = new GradientDrawable();
            loginBorder.setShape(GradientDrawable.RECTANGLE);
            loginBorder.setStroke(getContext().getResources().getInteger(R.integer.app_cms_border_stroke_width), borderColor);
            loginBorder.setColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            appCMSNavLoginButton.setBackground(loginBorder);

            Button appCMSNavFreeTrialButton = (Button) view.findViewById(R.id.app_cms_nav_free_trial_button);
            if (appCMSPresenter.getAppCMSMain()
                    .getServiceType()
                    .equals(getContext().getString(R.string.app_cms_main_svod_service_type_key))) {
                appCMSNavFreeTrialButton.setTextColor(textColor);
                appCMSNavFreeTrialButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (appCMSPresenter != null) {
                            appCMSPresenter.setLaunchType(AppCMSPresenter.LaunchType.SUBSCRIBE);
                            appCMSPresenter.navigateToSubscriptionPlansPage(appCMSBinder.getPageId(),
                                    appCMSBinder.getPageName());
                        }
                    }
                });
                appCMSNavFreeTrialButton.setBackgroundColor(buttonColor);
            } else {
                appCMSNavFreeTrialButton.setVisibility(View.INVISIBLE);
            }
        }

        setBgColor(bgColor, view);

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
                (RelativeLayout) view.findViewById(R.id.app_cms_navigation_menu_main_layout);
        appCMSNavigationMenuMainLayout.setBackgroundColor(bgColor);
    }
}
