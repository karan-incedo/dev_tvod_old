package com.viewlift.views.fragments;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.components.AppCMSViewComponent;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.modules.AppCMSPageViewModule;
import com.viewlift.views.binders.AppCMSBinder;

import com.viewlift.views.components.DaggerAppCMSViewComponent;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/3/17.
 */

public class AppCMSPageFragment extends Fragment {
    private static final String TAG = "AppCMSPageFragment";

    private AppCMSViewComponent appCMSViewComponent;
    private OnPageCreationError onPageCreationError;
    private AppCMSPresenter appCMSPresenter;
    private AppCMSBinder appCMSBinder;
    private PageView pageView;

    public interface OnPageCreationError {
        void onError();
    }

    public static AppCMSPageFragment newInstance(Context context, AppCMSBinder appCMSBinder) {
        AppCMSPageFragment fragment = new AppCMSPageFragment();
        Bundle args = new Bundle();
        args.putBinder(context.getString(R.string.fragment_page_bundle_key), appCMSBinder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnPageCreationError){
            onPageCreationError = (OnPageCreationError) context;
            appCMSBinder =
                    ((AppCMSBinder) getArguments().getBinder(context.getString(R.string.fragment_page_bundle_key)));
            appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                    .getAppCMSPresenterComponent()
                    .appCMSPresenter();
            appCMSViewComponent = DaggerAppCMSViewComponent
                    .builder()
                    .appCMSPageViewModule(new AppCMSPageViewModule(context,
                            appCMSBinder.getAppCMSPageUI(),
                            appCMSBinder.getAppCMSPageAPI(),
                            appCMSBinder.getJsonValueKeyMap(),
                            appCMSPresenter))
                    .build();
        } else {
            throw new RuntimeException("Attached context must implement " +
                OnPageCreationError.class.getCanonicalName());
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        pageView = appCMSViewComponent.appCMSPageView();
        if (pageView == null) {
            Log.e(TAG, "AppCMS page creation error");
            onPageCreationError.onError();
        } else {
            if (!pageView.isTablet(getContext()) && !appCMSBinder.isFullScreenEnabled()) {
                appCMSPresenter.restrictPortraitOnly();
            } else {
                appCMSPresenter.unrestrictPortraitOnly();
            }
        }
        if (container != null) {
            container.removeAllViews();
        }
        return pageView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pageView != null && (pageView.isTablet(getContext()) || appCMSBinder.isFullScreenEnabled())) {
            handleOrientation(getActivity().getResources().getConfiguration().orientation);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        handleOrientation(newConfig.orientation);
    }

    private void handleOrientation(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            appCMSPresenter.onOrientationChange(true);
        } else {
            appCMSPresenter.onOrientationChange(false);
        }
    }
}
