package com.viewlift.views.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.Mpeg;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSDownloadQualityAdapter;
import com.viewlift.views.adapters.AppCMSDownloadRadioAdapter;
import com.viewlift.views.binders.AppCMSDownloadQualityBinder;
import com.viewlift.views.components.AppCMSViewComponent;
import com.viewlift.views.components.DaggerAppCMSViewComponent;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.modules.AppCMSPageViewModule;

/**
 * Created by sandeep.singh on 7/28/2017.
 */

public class AppCMSDownloadQualityFragment extends Fragment
        implements AppCMSDownloadRadioAdapter.ItemClickListener<Mpeg> {

    private static final String TAG = AppCMSDownloadQualityFragment.class.getSimpleName();
    private AppCMSDownloadQualityBinder binder;
    private AppCMSPresenter appCMSPresenter;
    private AppCMSViewComponent appCMSViewComponent;
    private PageView pageView;
    private String downloadQuality;

    public AppCMSDownloadQualityFragment() {
        // Required empty public constructor
    }

    public static AppCMSDownloadQualityFragment newInstance(Context context, AppCMSDownloadQualityBinder binder) {
        AppCMSDownloadQualityFragment fragment = new AppCMSDownloadQualityFragment();
        Bundle args = new Bundle();
        args.putBinder(context.getString(R.string.app_cms_download_setting_binder_key), binder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        try {
            super.onAttach(context);

            binder = (AppCMSDownloadQualityBinder)
                    getArguments().getBinder(context.getString(R.string.app_cms_download_setting_binder_key));

            appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                    .getAppCMSPresenterComponent()
                    .appCMSPresenter();
            appCMSViewComponent = buildAppCMSViewComponent();
        } catch (ClassCastException e) {
            //Log.e(TAG, "Could not attach fragment: " + e.toString());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (appCMSViewComponent == null && binder != null) {
            appCMSViewComponent = buildAppCMSViewComponent();
        }

        if (appCMSViewComponent != null) {
            pageView = appCMSViewComponent.appCMSPageView();
        } else {
            pageView = null;
        }

        if (pageView != null) {
            if (pageView.getParent() != null) {
                ((ViewGroup) pageView.getParent()).removeAllViews();
            }

            try {
                if (!BaseView.isTablet(getContext())) {
                    appCMSPresenter.restrictPortraitOnly();
                } else {
                    appCMSPresenter.unrestrictPortraitOnly();
                }
            } catch (Exception e) {

            }
        }

        if (container != null) {
            container.removeAllViews();
        }

        if (pageView != null) {

            RecyclerView listDownloadQuality = (RecyclerView) pageView.findChildViewById(R.id.download_quality_selection_list);
            Button continueButton = (Button) pageView.findChildViewById(R.id.download_quality_continue_button);
            Button cancelButton = (Button) pageView.findChildViewById(R.id.download_quality_cancel_button);

            ((AppCMSDownloadQualityAdapter) listDownloadQuality.getAdapter()).setItemClickListener(this);

            continueButton.setOnClickListener(v -> {
                appCMSPresenter.setUserDownloadQualityPref(downloadQuality);
                if (binder != null &&
                        binder.getContentDatum() != null &&
                        binder.getResultAction1() != null) {
                    appCMSPresenter.editDownload(binder.getContentDatum(), binder.getResultAction1(), true,null);

                }
                getActivity().finish();
            });

            appCMSPresenter.setDownloadQualityScreenShowBefore(true);
            cancelButton.setOnClickListener(v -> getActivity().finish());
            pageView.setBackgroundColor(Color.TRANSPARENT);
        }
        return pageView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                binder = (AppCMSDownloadQualityBinder) savedInstanceState.getBinder(getString(R.string.app_cms_download_setting_binder_key));
            } catch (ClassCastException e) {
                //Log.e(TAG, "Could not attach fragment: " + e.toString());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PageView.isTablet(getContext()) || (binder != null && binder.isFullScreenEnabled())) {
            handleOrientation(getActivity().getResources().getConfiguration().orientation);
        }

        if (pageView == null) {
            //Log.e(TAG, "AppCMS page creation error");
        } else {

            getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pageView.notifyAdaptersOfUpdate();
        }

        if (appCMSPresenter != null) {
            appCMSPresenter.dismissOpenDialogs(null);
        }
    }

    private void handleOrientation(int orientation) {
        if (appCMSPresenter != null) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                appCMSPresenter.onOrientationChange(true);
            } else {
                appCMSPresenter.onOrientationChange(false);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (binder != null && appCMSViewComponent.viewCreator() != null) {
            appCMSPresenter.removeLruCacheItem(getContext(), binder.getPageId());
        }

        binder = null;
        pageView = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBinder(getString(R.string.app_cms_binder_key), binder);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        handleOrientation(newConfig.orientation);
    }

    public AppCMSViewComponent buildAppCMSViewComponent() {
        try {
            return DaggerAppCMSViewComponent.builder()
                    .appCMSPageViewModule(new AppCMSPageViewModule(getContext(),
                            binder.getAppCMSPageUI(),
                            binder.getAppCMSPageAPI(),
                            appCMSPresenter.getAppCMSAndroidModules(),
                            binder.getScreenName(),
                            binder.getJsonValueKeyMap(),
                            appCMSPresenter))
                    .build();
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    public void onItemClick(Mpeg item) {
        this.downloadQuality = item.getRenditionValue();
    }
}
