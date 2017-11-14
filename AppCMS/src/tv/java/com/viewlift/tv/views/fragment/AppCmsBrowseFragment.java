package com.viewlift.tv.views.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
import com.viewlift.tv.views.customviews.TVPageView;

/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class AppCmsBrowseFragment extends BaseBrowseFragment {
    private ArrayObjectAdapter mRowsAdapter;
    private final String TAG = AppCmsBrowseFragment.class.getName();
    private View view;
    private TVPageView pageView;


    public static AppCmsBrowseFragment newInstance(Context context){
        AppCmsBrowseFragment appCmsBrowseFragment = new AppCmsBrowseFragment();
        //Log.d("" , "appcmsBrowseFragment newInstance");
        return appCmsBrowseFragment;
    }

    public void setPageView(TVPageView tvPageView) {
        pageView = tvPageView;
    }

    public void setmRowsAdapter(ArrayObjectAdapter rowsAdapter) {
        this.mRowsAdapter = rowsAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void requestFocus(boolean requestFocus) {
        if (null != view) {
            if (requestFocus)
                view.requestFocus();
            else
                view.clearFocus();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.d(TAG , "appcmsBrowseFragment onActivityCreated");
        if (null != mRowsAdapter) {
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
    long clickedTime;

    public void pushedPlayKey() {
        if (null != rowData) {
            Utils.pageLoading(true, getActivity());
            String filmId = rowData.contentData.getGist().getId();
            String permaLink = rowData.contentData.getGist().getPermalink();
            String title = rowData.contentData.getGist().getTitle();

            long diff = System.currentTimeMillis() - clickedTime;
            if (diff > 2000) {
                clickedTime = System.currentTimeMillis();
                appCMSPresenter.launchTVVideoPlayer(rowData.contentData,
                        -1,
                        null,
                        rowData.contentData.getGist().getWatchedTime());
            } else {
                appCMSPresenter.showLoadingDialog(false);
            }
        }
    }

    public boolean hasFocus() {
        return (null != view && view.hasFocus());
    }

    private class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (null != item && item instanceof BrowseFragmentRowData) {
                BrowseFragmentRowData rowData = (BrowseFragmentRowData) item;
                ContentDatum data = rowData.contentData;

                String action = /*"play"*/rowData.action;
                if (action.equalsIgnoreCase(getString(R.string.app_cms_action_watchvideo_key))) {
                    pushedPlayKey();
                } else {
                    String permalink = data.getGist().getPermalink();
                    String title = data.getGist().getTitle();
                    String hlsUrl = getHlsUrl(data);
                    String[] extraData = new String[4];
                    extraData[0] = permalink;
                    extraData[1] = hlsUrl;
                    extraData[2] = data.getGist().getId();
                    if (data.getContentDetails() != null &&
                            data.getContentDetails().getClosedCaptions() != null) {
                        for (ClosedCaptions closedCaption :
                                data.getContentDetails().getClosedCaptions()) {
                            if (closedCaption.getFormat().equalsIgnoreCase("SRT")) {
                                extraData[3] = closedCaption.getUrl();
                                break;
                            }
                        }
                    }
                    if (!appCMSPresenter.launchTVButtonSelectedAction(permalink,
                            action,
                            title,
                            extraData,
                            data,
                            false, -1, null)) {

                    }
                }
                itemViewHolder.view.setClickable(false);
                new Handler().postDelayed(() -> itemViewHolder.view.setClickable(true), 3000);
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

    boolean isPlayerComponentSelected = false;
    private class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (null != item && item instanceof BrowseFragmentRowData) {
                isPlayerComponentSelected = false;
                rowData = (BrowseFragmentRowData) item;
                if (rowData != null)
                    data = rowData.contentData;
                Utils.setBrowseFragmentViewParameters(view,
                        (int) getResources().getDimension(R.dimen.browse_fragment_margin_left),
                        (int) getResources().getDimension(R.dimen.browse_fragment_margin_top));

            }else {
                if(pageView.isStandAlonePlayerEnabled()) {
                    isPlayerComponentSelected = true;
                    Utils.setBrowseFragmentViewParameters(view,
                            -40,
                            (int) getResources().getDimension(R.dimen.browse_fragment_margin_top_for_player));
                }
            }

        }
    }
}
