package com.viewlift.tv.views.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseCompnentModule;
import com.viewlift.tv.views.component.AppCMSTVViewComponent;
import com.viewlift.tv.views.component.DaggerAppCMSTVViewComponent;
import com.viewlift.tv.views.customviews.TVModuleView;
import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.module.AppCMSTVPageViewModule;
import com.viewlift.views.binders.AppCMSBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.viewlift.R;

/**
 * Created by nitin.tyagi on 6/28/2017.
 */

public class AppCmsTVPageFragment extends Fragment {

    private FrameLayout pageContainer;
    private AppCMSBinder mAppCMSBinder;
    private AppCMSPresenter appCMSPresenter;
    private AppCMSTVViewComponent appCmsViewComponent;
    private TVPageView tvPageView;
    public String mPageId ;

    public static AppCmsTVPageFragment newInstance(Context context , AppCMSBinder appCMSBinder){
        AppCmsTVPageFragment appCmsTVPageFragment = new AppCmsTVPageFragment();
        Bundle bundle = new Bundle();
        bundle.putBinder("app_cms_binder" , appCMSBinder);
        appCmsTVPageFragment.mPageId = appCMSBinder.getScreenName();
        appCmsTVPageFragment.setArguments(bundle);
        return appCmsTVPageFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        mAppCMSBinder = (AppCMSBinder)bundle.getBinder("app_cms_binder");

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        //clear the Adapter.
        if(null != appCmsViewComponent && null != appCmsViewComponent.tvviewCreator()
                && null != appCmsViewComponent.tvviewCreator().mRowsAdapter){
               appCmsViewComponent.tvviewCreator().mRowsAdapter.clear();
        }

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
            //onPageCreation.onSuccess(appCMSBinder);
        }
        if (container != null) {
            container.removeAllViews();
        }

        if((tvPageView.getChildrenContainer()).findViewById(R.id.appcms_browsefragment) != null
                && getChildFragmentManager().findFragmentByTag(mAppCMSBinder.getScreenName()) == null){
            AppCmsBrowseFragment browseFragment = AppCmsBrowseFragment.newInstance(getActivity());
            browseFragment.setAdapter(appCmsViewComponent.tvviewCreator().mRowsAdapter);
            getChildFragmentManager().beginTransaction().replace(R.id.appcms_browsefragment ,browseFragment ,mAppCMSBinder.getScreenName()).commitAllowingStateLoss();
        }
        return tvPageView;
    }


    @Override
    public void onResume() {
        super.onResume();
        requestFocus();
        if(null != appCMSPresenter)
        appCMSPresenter.sendStopLoadingPageAction();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    public void requestFocus(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewGroup ChildContaineer = (ViewGroup)(tvPageView.getChildrenContainer());
                int childcount = 0;
                if(null != ChildContaineer){
                    childcount = ChildContaineer.getChildCount() ;
                }
                for(int i =0 ; i<childcount; i++){
                    if(ChildContaineer.getChildAt(0) instanceof  TVModuleView){
                        TVModuleView tvModuleView = (TVModuleView)ChildContaineer.getChildAt(0);
                        ViewGroup moduleChildContaineer = tvModuleView.getChildrenContainer();
                        int moduleChild = moduleChildContaineer.getChildCount();

                        for(int j = 0; j < moduleChild; j++){
                            View view = moduleChildContaineer.getChildAt(j);
                            if(null != view){
                                System.out.println("View isFocusable == "+view.isFocusable() + "TAG =  = == " + (view.getTag() != null ? view.getTag().toString() : null));
                                if (null != view.getTag() &&
                                        view.getTag().toString().equalsIgnoreCase(getString(R.string.video_image_key))){
                                    ((FrameLayout)view).getChildAt(0).requestFocus();
                                    break;
                                }
                                else if(view.isFocusable()){
                                    view.requestFocus();
                                    break;
                                }else{
                                    view.clearFocus();
                                }
                            }
                        }
                    }
                }
            }
        } , 10);
    }

    @Override
    public void onDestroyView() {
        if(tvPageView != null)
            tvPageView.setBackground(null);
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
