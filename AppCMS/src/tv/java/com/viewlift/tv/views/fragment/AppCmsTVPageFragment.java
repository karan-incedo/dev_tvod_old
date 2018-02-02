package com.viewlift.tv.views.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.views.component.AppCMSTVViewComponent;
import com.viewlift.tv.views.component.DaggerAppCMSTVViewComponent;
import com.viewlift.tv.views.customviews.AppCMSTVTrayAdapter;
import com.viewlift.tv.views.customviews.CustomHeaderItem;
import com.viewlift.tv.views.customviews.TVModuleView;
import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.module.AppCMSTVPageViewModule;
import com.viewlift.tv.views.presenter.CardPresenter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.binders.AppCMSSwitchSeasonBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitin.tyagi on 6/28/2017.
 */

public class AppCmsTVPageFragment extends Fragment {

    private FrameLayout pageContainer;
    private AppCMSBinder mAppCMSBinder;
    private AppCMSPresenter appCMSPresenter;
    private AppCMSTVViewComponent appCmsViewComponent;
    private TVPageView tvPageView;
    public String mPageId;

    public static AppCmsTVPageFragment newInstance(Context context, AppCMSBinder appCMSBinder) {
        AppCmsTVPageFragment appCmsTVPageFragment = new AppCmsTVPageFragment();
        Bundle bundle = new Bundle();
        bundle.putBinder("app_cms_binder", appCMSBinder);
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
        mAppCMSBinder = (AppCMSBinder) bundle.getBinder("app_cms_binder");

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        //clear the Adapter.
        if (null != appCmsViewComponent && null != appCmsViewComponent.tvviewCreator()
                && null != appCmsViewComponent.tvviewCreator().mRowsAdapter) {
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

        if (null != tvPageView && (tvPageView.getChildrenContainer()).findViewById(R.id.appcms_browsefragment) != null) {
            if (getChildFragmentManager().findFragmentByTag(mAppCMSBinder.getScreenName()) == null) {
                AppCmsBrowseFragment browseFragment = AppCmsBrowseFragment.newInstance(getActivity());
                browseFragment.setPageView(tvPageView);
                browseFragment.setmRowsAdapter(appCmsViewComponent.tvviewCreator().mRowsAdapter);
                getChildFragmentManager().beginTransaction().replace(R.id.appcms_browsefragment, browseFragment, mAppCMSBinder.getScreenName()).commitAllowingStateLoss();
            } else {
               refreshBrowseFragment();
            }
        }
        return tvPageView;
    }


    @Override
    public void onResume() {
        super.onResume();
        requestFocus();
        if (null != appCMSPresenter)
            appCMSPresenter.sendStopLoadingPageAction(false,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    public void requestFocus() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != tvPageView) {
                    ViewGroup ChildContaineer = (ViewGroup) (tvPageView.getChildrenContainer());
                    int childcount = 0;
                    if (null != ChildContaineer) {
                        childcount = ChildContaineer.getChildCount();
                    }
                    for (int i = 0; i < childcount; i++) {
                        if (ChildContaineer.getChildAt(0) instanceof TVModuleView) {
                            TVModuleView tvModuleView = (TVModuleView) ChildContaineer.getChildAt(0);
                            ViewGroup moduleChildContaineer = tvModuleView.getChildrenContainer();
                            int moduleChild = moduleChildContaineer.getChildCount();

                            for (int j = 0; j < moduleChild; j++) {
                                View view = moduleChildContaineer.getChildAt(j);
                                if (null != view) {
                                    System.out.println("View isFocusable == " + view.isFocusable() + "TAG =  = == " + (view.getTag() != null ? view.getTag().toString() : null));
                                    if (null != view.getTag() &&
                                            view.getTag().toString().equalsIgnoreCase(getString(R.string.video_image_key))) {
                                        ((FrameLayout) view).getChildAt(0).requestFocus();
                                        break;
                                    } else if (view.isFocusable()) {
                                        view.requestFocus();
                                        break;
                                    } else {
                                        view.clearFocus();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, 10);
        }


    public void refreshBrowseFragment(){
        try {
            if (null != appCmsViewComponent.tvviewCreator() && null != appCmsViewComponent.tvviewCreator().mRowsAdapter) {
                int totalNumber = 0;
                for (int i = 0; i < appCmsViewComponent.tvviewCreator().mRowsAdapter.size(); i++) {
                    ListRow listRow = (ListRow) appCmsViewComponent.tvviewCreator().mRowsAdapter.get(i);
                    CustomHeaderItem customHeaderItem = (CustomHeaderItem) listRow.getHeaderItem();
                    for (Module module : mAppCMSBinder.getAppCMSPageAPI().getModules()) {
                        if (module.getId().equalsIgnoreCase(customHeaderItem.getmModuleId())) {
                            List<ContentDatum> contentData = module.getContentData();
                            for (int i1 = 0; i1 < contentData.size(); i1++) {
                                ContentDatum contentDatum = contentData.get(i1);
                                for (int j = 0; j < listRow.getAdapter().size(); j++) {
                                    if (((BrowseFragmentRowData) listRow.getAdapter().get(j)).contentData.getGist().getId().equalsIgnoreCase(contentDatum.getGist().getId())) {
                                        BrowseFragmentRowData rowData = (BrowseFragmentRowData) listRow.getAdapter().get(j);
                                        rowData.contentData = module.getContentData().get(i1);
                                        totalNumber++;
                                        break;
                                    }
                                }

                            }
                        }
                    }
                }
                appCmsViewComponent.tvviewCreator().mRowsAdapter.notifyArrayItemRangeChanged(0, totalNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroyView() {
        if (tvPageView != null)
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

    public void updateBinder(AppCMSBinder appCmsBinder) {
        mAppCMSBinder = appCmsBinder;
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

    public void updateSeasonData(AppCMSSwitchSeasonBinder appCMSSwitchSeasonBinder) {

        try {
            if (null != appCmsViewComponent.tvviewCreator() && null != appCmsViewComponent.tvviewCreator().mRowsAdapter) {

                ListRow listRow = (ListRow) appCmsViewComponent.tvviewCreator().mRowsAdapter.get(0);
                ArrayObjectAdapter  arrayObjectAdapter = (ArrayObjectAdapter) listRow.getAdapter();
                CardPresenter trayCardPresenter = (CardPresenter) arrayObjectAdapter.getPresenter(0);
                List<Component> uiComponentList = ((BrowseFragmentRowData) arrayObjectAdapter.get(0)).uiComponentList;
                String blockName = ((BrowseFragmentRowData) arrayObjectAdapter.get(0)).blockName;
                String action = ((BrowseFragmentRowData) arrayObjectAdapter.get(0)).action;
                arrayObjectAdapter.clear();

                ArrayObjectAdapter traylistRowAdapter = new ArrayObjectAdapter(trayCardPresenter);
                List<ContentDatum> episodes = appCMSSwitchSeasonBinder.getSeasonList().get(appCMSSwitchSeasonBinder.getSelectedSeasonIndex()).getEpisodes();
                for (int i = 0; i < episodes.size(); i++) {
                    List<String> relatedVids = new ArrayList<>();
                    for (int j = i + 1; j < episodes.size(); j++) {
                        ContentDatum contentDatum = episodes.get(j);
                        relatedVids.add(contentDatum.getGist().getId());
                    }
                    ContentDatum contentDatum = episodes.get(i);
                    BrowseFragmentRowData rowData = new BrowseFragmentRowData();
                    rowData.contentData = contentDatum;
                    rowData.relatedVideoIds = relatedVids;
                    rowData.uiComponentList = uiComponentList;
                    rowData.action = action;
                    rowData.blockName = blockName;
                    rowData.rowNumber = appCMSSwitchSeasonBinder.getTrayIndex();
                    traylistRowAdapter.add(rowData);
                }
                CustomHeaderItem customHeaderItem = (CustomHeaderItem) listRow.getHeaderItem();
                CustomHeaderItem header = new CustomHeaderItem(appCMSPresenter.getCurrentActivity(), 0, "Season " + (appCMSSwitchSeasonBinder.getSelectedSeasonIndex() + 1));
                header.setFontFamily(customHeaderItem.getFontFamily());
                header.setFontSize(customHeaderItem.getFontSize());
                header.setFontWeight(customHeaderItem.getFontWeight());
                header.setmBackGroundColor(customHeaderItem.getmBackGroundColor());
                header.setmIsCarousal(customHeaderItem.ismIsCarousal());
                header.setmIsLivePlayer(customHeaderItem.ismIsLivePlayer());
                header.setmListRowHeight(customHeaderItem.getmListRowHeight());
                header.setmListRowLeftMargin(customHeaderItem.getmListRowLeftMargin());
                header.setmListRowRightMargin(customHeaderItem.getmListRowRightMargin());
                header.setmListRowWidth(customHeaderItem.getmListRowWidth());
                header.setmModuleId(customHeaderItem.getmModuleId());
                header.setContentDescription(customHeaderItem.getContentDescription());
                header.setDescription(customHeaderItem.getDescription());
                appCmsViewComponent.tvviewCreator().mRowsAdapter.remove(appCmsViewComponent.tvviewCreator().mRowsAdapter.get(0));
                appCmsViewComponent.tvviewCreator().mRowsAdapter.add(new ListRow(header, traylistRowAdapter));
                appCmsViewComponent.tvviewCreator().mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
