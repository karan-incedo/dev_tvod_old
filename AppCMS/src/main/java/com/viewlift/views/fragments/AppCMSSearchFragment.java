package com.viewlift.views.fragments;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v4.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 6/20/17.
 */

public class AppCMSSearchFragment extends DialogFragment {
    private SearchView appCMSSearchView;
    private Button appCMSGoButton;

    public static AppCMSSearchFragment newInstance(Context context,
                                                   int bgColor,
                                                   int buttonColor,
                                                   int textColor) {
        Bundle args = new Bundle();
        args.putInt(context.getString(R.string.bg_color_key), bgColor);
        args.putInt(context.getString(R.string.button_color_key), buttonColor);
        args.putInt(context.getString(R.string.text_color_key), textColor);
        AppCMSSearchFragment appCMSSearchFragment = new AppCMSSearchFragment();
        appCMSSearchFragment.setArguments(args);
        return appCMSSearchFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Bundle args = getArguments();

        final AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        appCMSSearchView = (SearchView) view.findViewById(R.id.app_cms_search_view);
        appCMSSearchView.setQueryHint(getString(R.string.search_films));
        appCMSSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        appCMSSearchView.setIconifiedByDefault(false);
        appCMSSearchView.setFocusable(true);
        appCMSSearchView.requestFocus();

        appCMSGoButton = (Button) view.findViewById(R.id.app_cms_search_button);
        appCMSGoButton.setBackgroundColor(args.getInt(getString(R.string.button_color_key)));
        appCMSGoButton.setTextColor(args.getInt(getString(R.string.text_color_key)));
        appCMSGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCMSPresenter.launchSearchResultsPage(appCMSSearchView.getQuery().toString());
            }
        });

        ImageButton closeButton = (ImageButton) view.findViewById(R.id.app_cms_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setBgColor(args.getInt(getContext().getString(R.string.bg_color_key)));

        if (!BaseView.isTablet(getContext())) {
            appCMSPresenter.restrictPortraitOnly();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setWindow();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setWindow();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    private void setBgColor(int bgColor) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(bgColor));
        }
    }

    private void setWindow() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = dialog.getWindow();
            window.setLayout(width, height);
            window.setGravity(Gravity.START);
        }
    }
}
