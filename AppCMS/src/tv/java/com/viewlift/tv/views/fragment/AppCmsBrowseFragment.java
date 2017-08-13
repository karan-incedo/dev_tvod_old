package com.viewlift.tv.views.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;

import java.util.List;

import com.viewlift.R;

/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class AppCmsBrowseFragment extends BaseBrowseFragment {
    private ArrayObjectAdapter mRowsAdapter;
    private final String TAG = AppCmsBrowseFragment.class.getName();
    private View view;



    public static AppCmsBrowseFragment newInstance(Context context){
        AppCmsBrowseFragment appCmsBrowseFragment = new AppCmsBrowseFragment();
        Log.d("" , "appcmsBrowseFragment newInstance");
        return appCmsBrowseFragment;
    }

    public void setmRowsAdapter(ArrayObjectAdapter rowsAdapter){
        this.mRowsAdapter = rowsAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }


    public void requestFocus(boolean requestFocus){
        if(null != view){
            if(requestFocus)
                view.requestFocus();
            else
                view.clearFocus();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG , "appcmsBrowseFragment onActivityCreated");
        if(null != mRowsAdapter){
            setAdapter(mRowsAdapter);
        }

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }



    AppCMSPresenter appCMSPresenter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
    }

    ContentDatum data = null;
    BrowseFragmentRowData rowData = null;
    public void pushedPlayKey() {
        if (null != rowData) {
            ((AppCmsHomeActivity)getActivity()).pageLoading(true);
            String filmId = rowData.contentData.getGist().getId();
            String permaLink = rowData.contentData.getGist().getPermalink();
            String title = rowData.contentData.getGist().getTitle();

            if (!appCMSPresenter.launchTVVideoPlayer(filmId, permaLink, title , rowData.contentData)) {
                ((AppCmsHomeActivity)getActivity()).pageLoading(false);
                Log.e(TAG, "Could not launch play action: " +
                        " filmId: " +
                        filmId +
                        " permaLink: " +
                        permaLink +
                        " title: " +
                        title);
            }

         /*   if (!appCMSPresenter.launchVideoPlayer(rowData.contentData , -1 , null , 0)) {
                ((AppCmsHomeActivity)getActivity()).pageLoading(false);
                Log.e(TAG, "Could not launch play action: " +
                        " filmId: " +
                        filmId +
                        " permaLink: " +
                        permaLink +
                        " title: " +
                        title);
            }*/

        }
    }

    public boolean hasFocus() {
        return (null != view && view.hasFocus());
    }

    private class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            BrowseFragmentRowData rowData = (BrowseFragmentRowData)item;
            ContentDatum data = rowData.contentData;
            //String hls = rowData.contentData.getStreamingInfo().getVideoAssets().getHls();
            Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());
            String permalink = data.getGist().getPermalink();
            String action = "play";
            String title = data.getGist().getTitle();
            String hlsUrl = getHlsUrl(data);
            String[] extraData = new String[3];
            extraData[0] = permalink;
            extraData[1] = hlsUrl;
            extraData[2] = data.getGist().getId();
            Log.d(TAG, "Launching " + permalink + ": " + action);
            if (!appCMSPresenter.launchTVButtonSelectedAction(permalink,
                    action,
                    title,
                    extraData,
                    false)) {
                Log.e(TAG, "Could not launch action: " +
                        " permalink: " +
                        permalink +
                        " action: " +
                        action +
                        " hlsUrl: " +
                        hlsUrl);
            }
        }
    }

    private String getHlsUrl(ContentDatum data) {
        if (data.getStreamingInfo() != null &&
                data.getStreamingInfo().getVideoAssets() != null &&
                data.getStreamingInfo().getVideoAssets().getHls() != null) {
            return data.getStreamingInfo().getVideoAssets().getHls();
        }
        return null;
    }

    private class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {


            rowData = (BrowseFragmentRowData)item;
            if(rowData != null)
             data = rowData.contentData;
            //Log.d(TAG , "Clicked StreamInfo = " + rowData.contentData.getGist().getTitle());
        }
    }



}
