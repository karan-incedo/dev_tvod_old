package com.viewlift.tv.views.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BrowseFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseCompnentModule;
import com.viewlift.tv.views.component.AppCMSTVViewComponent;
import com.viewlift.tv.views.component.DaggerAppCMSTVViewComponent;
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

    public static AppCmsTVPageFragment newInstance(Context context , AppCMSBinder appCMSBinder){
        AppCmsTVPageFragment appCmsTVPageFragment = new AppCmsTVPageFragment();
        Bundle bundle = new Bundle();
        bundle.putBinder("app_cms_binder" , appCMSBinder);
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
       // super.onCreateView(inflater, container, savedInstanceState);
        View _rootView = inflater.inflate(R.layout.appcms_tv_page_fragment ,null);
        pageContainer = (FrameLayout)_rootView.findViewById(R.id.page_container);

        Bundle bundle = getArguments();
        mAppCMSBinder = (AppCMSBinder)bundle.getBinder("app_cms_binder");

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

       /* if (tvPageView != null) {
            if (tvPageView.getParent() != null) {
                ((ViewGroup) tvPageView.getParent()).removeAllViews();
            }
            //onPageCreation.onSuccess(appCMSBinder);
        }*/
        if (container != null) {
            container.removeAllViews();
        }


        if((tvPageView.getChildrenContainer()).findViewById(R.id.appcms_browsefragment) != null){
            AppCmsBrowseFragment browseFragment = AppCmsBrowseFragment.newInstance(getActivity() ,
                    appCmsViewComponent.tvviewCreator().mRowsAdapter);
            getChildFragmentManager().beginTransaction().replace(R.id.appcms_browsefragment ,browseFragment , "frag").commit();
        }
        return tvPageView;


        /*listModule();

        if(null != traymoduleLists){
           AppCmsBrowseFragment browseFragment = AppCmsBrowseFragment.newInstance(getActivity() , traymoduleLists);
           getChildFragmentManager().beginTransaction().replace(R.id.page_container,browseFragment,"browse").commit();
        }*/

       // return _rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        appCMSPresenter.sendStopLoadingPageAction();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    List<BrowseCompnentModule> traymoduleLists;
    private void listModule(){
        AppCMSPageUI appCMSPageUI = mAppCMSBinder.getAppCMSPageUI();
        AppCMSPageAPI appCMSPageAPI = mAppCMSBinder.getAppCMSPageAPI();
        List<String> moduleToIgnore = Arrays.asList(getResources().getStringArray(R.array.app_cms_modules_to_ignore));
        List<String> trayModuleList = Arrays.asList(getResources().getStringArray(R.array.app_cms_tray_modules));
        List<String> detailModuleList = Arrays.asList(getResources().getStringArray(R.array.app_cms_detail_module));
        traymoduleLists = new ArrayList<>();
        List<BrowseCompnentModule> detailModuleLists = new ArrayList<>();


        for(int i = 0 ; i <appCMSPageUI.getModuleList().size() ; i++){
            //ModuleList is the UI of Single ModuleWithComponents like tray module or Crausol ModuleWithComponents.
            ModuleList module = appCMSPageUI.getModuleList().get(i);
            if(trayModuleList.contains(module.getView())){
                BrowseCompnentModule browseCompnentModule = new BrowseCompnentModule();
                browseCompnentModule.position = i;
                browseCompnentModule.moduleUI = module;

                for(int j=0 ; j < appCMSPageAPI.getModules().size(); j++){
                    if(appCMSPageAPI.getModules().get(j).getId().equalsIgnoreCase(module.getId())){
                        browseCompnentModule.moduleData = appCMSPageAPI.getModules().get(j);// assign module data.
                        traymoduleLists.add(browseCompnentModule);
                        break;
                    }
                }

            }else if(detailModuleList.contains(module.getView())){
                BrowseCompnentModule browseCompnentModule = new BrowseCompnentModule();
                browseCompnentModule.position = i;
                browseCompnentModule.moduleUI = module;

                for(int j = 0 ; j <appCMSPageAPI.getModules().size() ; j++){
                    if(appCMSPageAPI.getModules().get(j).getId().equalsIgnoreCase(module.getId())){
                        browseCompnentModule.moduleData = appCMSPageAPI.getModules().get(j);// assign module data.
                        detailModuleLists.add(browseCompnentModule);
                    }
                }
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle bundle = getArguments();
        mAppCMSBinder = (AppCMSBinder)bundle.getBinder("app_cms_binder");

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();


        appCmsViewComponent = buildAppCMSViewComponent();
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
