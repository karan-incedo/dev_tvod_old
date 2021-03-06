package com.viewlift.tv.views.activity;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.component.AppCMSTVViewComponent;
import com.viewlift.tv.views.component.DaggerAppCMSTVViewComponent;
import com.viewlift.tv.views.customviews.AppCMSTVTrayAdapter;
import com.viewlift.tv.views.customviews.TVModuleView;
import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.fragment.AppCmsNavigationFragment;
import com.viewlift.tv.views.fragment.AppCmsSubNavigationFragment;
import com.viewlift.tv.views.module.AppCMSTVPageViewModule;
import com.viewlift.views.binders.AppCMSBinder;


public class AppCmsMyProfileFragment extends Fragment implements AppCmsSubNavigationFragment.OnNavigationVisibilityListener {

    private AppCmsSubNavigationFragment appCmsSubNavigationFragment;
    private AppCMSBinder mAppCMSBinder;
    private AppCMSPresenter appCMSPresenter;
    private AppCMSTVViewComponent appCmsViewComponent;
    private TVPageView tvPageView;

    public static AppCmsMyProfileFragment newInstance(Context context, AppCMSBinder appCMSBinder) {
        AppCmsMyProfileFragment appCmsTVPageFragment = new AppCmsMyProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putBinder("app_cms_binder", appCMSBinder);
        //appCmsTVPageFragment.mPageId = appCMSBinder.getScreenName();
        appCmsTVPageFragment.setArguments(bundle);
        return appCmsTVPageFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        mAppCMSBinder = (AppCMSBinder) bundle.getBinder("app_cms_binder");
        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        if (appCmsViewComponent == null && mAppCMSBinder != null) {
            appCmsViewComponent = buildAppCMSViewComponent();
        }

        if (appCmsViewComponent != null) {
            tvPageView = appCmsViewComponent.appCMSTVPageView();
        } else {
            tvPageView = null;
        }

        if (tvPageView != null) {
            if (tvPageView.getParent() != null) {
                ((ViewGroup) tvPageView.getParent()).removeAllViews();
            }
        }
        if (container != null) {
            container.removeAllViews();
        }


        View view = inflater.inflate(R.layout.app_cms_my_profile_fragment, null);
        setSubNavigationFragment();

        FrameLayout pageHolder = (FrameLayout) view.findViewById(R.id.profile_placeholder);
        pageHolder.addView(tvPageView);
        return view;
    }


    public void updateAdapterData(AppCMSBinder appCmsBinder) {
        try {
            TVModuleView tvModuleView = (TVModuleView) tvPageView.getChildrenContainer().getChildAt(0);
            int childCount = tvModuleView.getChildrenContainer().getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (null != tvModuleView.getChildrenContainer().getChildAt(i)
                        && tvModuleView.getChildrenContainer().getChildAt(i) instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) tvModuleView.getChildrenContainer().getChildAt(i);
                    ((AppCMSTVTrayAdapter) recyclerView.getAdapter()).setContentData(appCmsBinder.getAppCMSPageAPI().getModules().get(0).getContentData());

                }
            }
        } catch (Exception e) {

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (null != appCMSPresenter)
            appCMSPresenter.sendStopLoadingPageAction(false,null);
    }

    private void setSubNavigationFragment() {
        appCmsSubNavigationFragment = AppCmsSubNavigationFragment.newInstance(getActivity(), this);
        Bundle bundle = new Bundle();
        bundle.putBinder("app_cms_binder", mAppCMSBinder);
        appCmsSubNavigationFragment.setArguments(bundle);
        appCmsSubNavigationFragment.setSelectedPageId(mAppCMSBinder.getPageId());
        getChildFragmentManager().beginTransaction().replace(R.id.sub_navigation_placholder, appCmsSubNavigationFragment).commitAllowingStateLoss();
    }

    @Override
    public void showNavigation(boolean shouldShow) {

    }

    public AppCMSTVViewComponent buildAppCMSViewComponent() {
        return DaggerAppCMSTVViewComponent.builder()
                .appCMSTVPageViewModule(new AppCMSTVPageViewModule(getActivity(),
                        mAppCMSBinder.getAppCMSPageUI(),
                        mAppCMSBinder.getAppCMSPageAPI(),
                        mAppCMSBinder.getJsonValueKeyMap(),
                        appCMSPresenter
                ))
                .build();
    }

}
