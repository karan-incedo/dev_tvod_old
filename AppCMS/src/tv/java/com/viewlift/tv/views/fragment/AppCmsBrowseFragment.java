package com.viewlift.tv.views.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
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
import com.viewlift.tv.views.customviews.CustomHeaderItem;
import com.viewlift.tv.views.presenter.AppCmsListRowPresenter;
import com.viewlift.tv.views.presenter.CardPresenter;
import com.viewlift.tv.views.presenter.JumbotronPresenter;

import java.util.List;

import com.viewlift.R;

/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class AppCmsBrowseFragment extends BaseBrowseFragment {

    private static List<BrowseCompnentModule> mBrowseCompnentModules;
    private static ArrayObjectAdapter mRowsAdapter;
    private final String TAG = AppCmsBrowseFragment.class.getName();

    public static AppCmsBrowseFragment newInstance(Context context, List<BrowseCompnentModule> browseCompnentModuleList){
        AppCmsBrowseFragment appCmsBrowseFragment = new AppCmsBrowseFragment();
        mBrowseCompnentModules = browseCompnentModuleList;
        Log.d("" , "appcmsBrowseFragment newInstance");
        return appCmsBrowseFragment;
    }

    public static AppCmsBrowseFragment newInstance(Context context, ArrayObjectAdapter rowsAdapter){
        AppCmsBrowseFragment appCmsBrowseFragment = new AppCmsBrowseFragment();
        mRowsAdapter = rowsAdapter;
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
        if(null != mRowsAdapter){
            setAdapter(mRowsAdapter);
        }
    }

}
