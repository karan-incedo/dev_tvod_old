package com.viewlift.tv.views.fragment;

import android.content.Context;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.GsonBuilder;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.tv.model.BrowseCompnentModule;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.CustomHeaderItem;
import com.viewlift.tv.views.presenter.AppCmsListRowPresenter;
import com.viewlift.tv.views.presenter.CardPresenter;
import com.viewlift.tv.views.presenter.CustomHeaderItemPresenter;
import com.viewlift.tv.views.presenter.JumbotronPresenter;

import java.util.Collections;
import java.util.List;

import snagfilms.com.air.appcms.R;

/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class AppCmsBrowseFragment extends BaseBrowseFragment {

    private static List<BrowseCompnentModule> mBrowseCompnentModules;
    private ArrayObjectAdapter mRowsAdapter;
    private final String TAG = AppCmsBrowseFragment.class.getName();

    public static AppCmsBrowseFragment newInstance(Context context, List<BrowseCompnentModule> browseCompnentModuleList){
        AppCmsBrowseFragment appCmsBrowseFragment = new AppCmsBrowseFragment();
        mBrowseCompnentModules = browseCompnentModuleList;
        Log.d("" , "appcmsBrowseFragment newInstance");
        return appCmsBrowseFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG , "appcmsBrowseFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG , "appcmsBrowseFragment onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG , "appcmsBrowseFragment onActivityCreated");
        createRows();
    }

    private void createRows(){
        int i = 0;
        AppCmsListRowPresenter listRowPresenter = new AppCmsListRowPresenter(getActivity());
        mRowsAdapter = new ArrayObjectAdapter(listRowPresenter);

        for (BrowseCompnentModule browseCompnentModule : mBrowseCompnentModules) {
            Module moduleData = browseCompnentModule.moduleData;
            ModuleList moduleUI = browseCompnentModule.moduleUI;

            int position = browseCompnentModule.position;
            CustomHeaderItem header = null;
            ArrayObjectAdapter listRowAdapter = null ;
            if (moduleUI.getView().equalsIgnoreCase(getResources().getString(R.string.carousel_nodule))) {
                ModuleList moduleList = new GsonBuilder().create().fromJson(Utils.loadJsonFromAssets(getActivity(), "carousel_ftv_component.json"), ModuleList.class);
                moduleUI = moduleList; //TODO : change it when it comes from server.
                CardPresenter cardPresenter = new JumbotronPresenter(getActivity(), (int) getResources().getDimension(R.dimen.jumbotron_height)
                        /*moduleUI.getLayout().getMobile().getHeight()*/, (int) getResources().getDimension(R.dimen.jumbotron_weidth));

                header = new CustomHeaderItem(getActivity(), i, moduleData.getTitle());
                header.setmIsCarousal(true);
                header.setmListRowLeftMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                header.setmListRowRightMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                header.setmBackGroundColor(moduleUI.getLayout().getTv().getBackgroundColor());
                header.setmListRowHeight(Integer.valueOf(moduleUI.getLayout().getTv().getHeight()));
                listRowAdapter = new ArrayObjectAdapter(cardPresenter);

            } else {
                CardPresenter cardPresenter = new CardPresenter(getActivity(),
                        (int) getResources().getDimension(R.dimen.card_height)
                        , (int) getResources().getDimension(R.dimen.card_width));
                header = new CustomHeaderItem(getActivity(), i, moduleData.getTitle());
                header.setmIsCarousal(false);
                header.setmListRowHeight(450/*Integer.valueOf(moduleUI.getLayout().getTv().getHeight())*/);
                listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            }


            Log.d(TAG , "NITS header = "+header.getName());
            if (moduleData.getContentData() != null && moduleData.getContentData().size() > 0) {
                List<ContentDatum> contentData1 = moduleData.getContentData();
                for (ContentDatum contentData : contentData1) {
                    BrowseFragmentRowData rowData = new BrowseFragmentRowData();
                    rowData.contentData = contentData;
                    listRowAdapter.add(rowData);
                    Log.d(TAG , "NITS header Items ===== "+rowData.contentData.getGist().getTitle());
                }
                mRowsAdapter.add(new ListRow(header, listRowAdapter));
                i++;
            }
        }
        setAdapter(mRowsAdapter);
    }
}
