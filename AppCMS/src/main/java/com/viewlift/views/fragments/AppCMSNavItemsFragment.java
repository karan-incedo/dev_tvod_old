package com.viewlift.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSNavItemsAdapter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.customviews.BaseView;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/30/17.
 */

public class AppCMSNavItemsFragment extends Fragment {
    private AppCMSNavItemsAdapter.OnCloseNavAction onCloseNavAction;

    public static AppCMSNavItemsFragment newInstance(Context context,
                                                     AppCMSBinder appCMSBinder) {
        AppCMSNavItemsFragment fragment = new AppCMSNavItemsFragment();
        Bundle args = new Bundle();
        args.putBinder(context.getString(R.string.fragment_page_bundle_key), appCMSBinder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof AppCMSNavItemsAdapter.OnCloseNavAction) {
            onCloseNavAction = (AppCMSNavItemsAdapter.OnCloseNavAction) context;
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        AppCMSBinder appCMSBinder =
                ((AppCMSBinder) getArguments().getBinder(getContext().getString(R.string.fragment_page_bundle_key)));
        View view = inflater.inflate(R.layout.fragment_menu_nav, container, false);
        RecyclerView navItemsList = (RecyclerView) view.findViewById(R.id.nav_items_list);
        AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
        AppCMSNavItemsAdapter appCMSNavItemsAdapter = new AppCMSNavItemsAdapter(onCloseNavAction,
                appCMSBinder.getNavigation(),
                appCMSBinder.isUserLoggedIn(),
                appCMSPresenter);
        navItemsList.setAdapter(appCMSNavItemsAdapter);
        if (!BaseView.isTablet(getContext())) {
            appCMSPresenter.restrictPortraitOnly();
        }
        return view;
    }
}
