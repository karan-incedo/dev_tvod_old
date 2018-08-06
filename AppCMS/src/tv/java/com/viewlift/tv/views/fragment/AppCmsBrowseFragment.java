package com.viewlift.tv.views.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
import com.viewlift.tv.views.customviews.CustomTVVideoPlayerView;
import com.viewlift.tv.views.customviews.TVPageView;

/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class AppCmsBrowseFragment extends BaseBrowseFragment {
    private ArrayObjectAdapter mRowsAdapter;
    private final String TAG = AppCmsBrowseFragment.class.getName();
    private View view;
    private TVPageView pageView;
    private String screenName;


    public static AppCmsBrowseFragment newInstance(Context context){
        AppCmsBrowseFragment appCmsBrowseFragment = new AppCmsBrowseFragment();
        return appCmsBrowseFragment;
    }

    public void setPageView(TVPageView tvPageView) {
        pageView = tvPageView;
    }

    public void setmRowsAdapter(ArrayObjectAdapter rowsAdapter) {
        this.mRowsAdapter = rowsAdapter;
        //mRowsAdapter.notifyItemRangeChanged(0, rowsAdapter.size());
        // todo check anas azeem
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCmsHomeActivity activity = (AppCmsHomeActivity) getActivity();

        new Handler().postDelayed(() -> {
            if(null != customVideoVideoPlayerView){
                if (activity.isNavigationVisible() /*|| activity.isSubNavigationVisible()*/) {
                } else {
                    if(activity.isActive) {
                        customVideoVideoPlayerView.resumePlayer();
                    }
                }
            }
        },50);

        /* if the current screen in a video page, then the layout containing the BrowseFragment
        * is given a height based on the row height we get from Page UI json, instead of match parent*/
        if (screenName.toLowerCase().contains("video page")) {
            FrameLayout browseFrame = getActivity().findViewById(R.id.appcms_browsefragment);
            ViewGroup.LayoutParams layoutParams = browseFrame.getLayoutParams();
            int rowHeight = Integer.parseInt(((BrowseFragmentRowData) ((ListRow) mRowsAdapter.get(0)).getAdapter().get(0)).uiComponentList.get(0).getLayout().getTv().getHeight());
            // this 160 value includes header and the empty space other than the actual row item
            layoutParams.height = 160 + rowHeight;
            browseFrame.setLayoutParams(layoutParams);

        }
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
        if (null != rowData && !rowData.isPlayerComponent) {
            Utils.pageLoading(true, getActivity());
            String filmId = rowData.contentData.getGist().getId();
            String permaLink = rowData.contentData.getGist().getPermalink();
            String title = rowData.contentData.getGist().getTitle();

            long diff = System.currentTimeMillis() - clickedTime;
            if (diff > 2000) {
                clickedTime = System.currentTimeMillis();
                if (null != rowData.contentData.getGist().getContentType() &&
                        rowData.contentData.getGist().getContentType().equalsIgnoreCase("SERIES")) {
                    appCMSPresenter.launchTVButtonSelectedAction(
                            rowData.contentData.getGist().getPermalink(),
                            "showDetailPage",
                            rowData.contentData.getGist().getTitle(),
                            null,
                            rowData.contentData,
                            false,
                            -1,
                            null,
                            null);
                } else {
                    appCMSPresenter.launchTVVideoPlayer(rowData.contentData,
                            0,
                            rowData.relatedVideoIds,
                            rowData.contentData.getGist().getWatchedTime(),
                            null);
                }
            } else {
                appCMSPresenter.showLoadingDialog(false);
            }
        }
    }

    public boolean hasFocus() {
        return (null != view && view.hasFocus());
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    private class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if(AppCMSPresenter.isFullScreenVisible){
                return;
            }
            if (null != item && item instanceof BrowseFragmentRowData) {
                BrowseFragmentRowData rowData = (BrowseFragmentRowData) item;
                if(rowData.isPlayerComponent){
                    if(customVideoVideoPlayerView.isLoginButtonVisible()){
                        if(!appCMSPresenter.isUserLoggedIn()) {
                            customVideoVideoPlayerView.performLoginButtonClick();
                        }
                        else{
                            customVideoVideoPlayerView.showRestrictMessage(getString(R.string.reload_page_from_menu));
                            customVideoVideoPlayerView.toggleLoginButtonVisibility(false);
                        }
                        return;
                    }
                    appCMSPresenter.setTVVideoPlayerView(null);
                    appCMSPresenter.setTVVideoPlayerView(customVideoVideoPlayerView);
                    appCMSPresenter.tvVideoPlayerView.getPlayerView().setUseController(true);
                    customVideoVideoPlayerView.hideControlsForLiveStream();
                    appCMSPresenter.showFullScreenTVPlayer();
                    appCMSPresenter.tvVideoPlayerView.getPlayerView().getPlayer().seekTo(appCMSPresenter.tvVideoPlayerView.getPlayer().getContentPosition() + 1000);
                    return;
                }
                ContentDatum data = rowData.contentData;

                String action = rowData.action;

                if(appCMSPresenter.getAppCMSMain().getFeatures().isTrickPlay()){
                    action = getString(R.string.app_cms_action_watchvideo_key);
                }

                if (action.equalsIgnoreCase(getString(R.string.app_cms_action_watchvideo_key))) {
                    pushedPlayKey();
                } else {
                    if (null != rowData.contentData.getGist().getContentType() &&
                            rowData.contentData.getGist().getContentType().equalsIgnoreCase("SERIES")){
                        action = "showDetailPage";
                    }
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
                            if ("SRT".equalsIgnoreCase(closedCaption.getFormat())) {
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
                            false,
                            -1,
                            null,
                            null)) {

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
    CustomTVVideoPlayerView customVideoVideoPlayerView;
    private class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if(AppCMSPresenter.isFullScreenVisible){
                return;
            }

            if (null != item && item instanceof BrowseFragmentRowData) {
                isPlayerComponentSelected = false;
                rowData = (BrowseFragmentRowData) item;
                if (rowData != null) {
                    if(getActivity() instanceof AppCmsHomeActivity){
                        ((AppCmsHomeActivity) getActivity()).shouldShowLeftNavigation(rowData.itemPosition == 0);
                    }
                    data = rowData.contentData;
                    if(rowData.isPlayerComponent){
                        if( null != itemViewHolder && null != itemViewHolder.view
                                && ((FrameLayout) itemViewHolder.view).getChildAt(0) instanceof CustomTVVideoPlayerView){
                            customVideoVideoPlayerView  =  (CustomTVVideoPlayerView)((FrameLayout) itemViewHolder.view).getChildAt(0);
                            if(customVideoVideoPlayerView.isLoginButtonVisible() && appCMSPresenter.isUserLoggedIn()){
                                customVideoVideoPlayerView.showRestrictMessage(getString(R.string.reload_page_from_menu));
                                customVideoVideoPlayerView.toggleLoginButtonVisibility(false);
                            }
                        }
                        Utils.setBrowseFragmentViewParameters(view,
                                Utils.getViewYAxisAsPerScreen(getActivity() , (int) getResources().getDimension(R.dimen.browse_fragment_margin_left)),
                                (int) getResources().getDimension(R.dimen.browse_fragment_margin_top_for_player));
                        isPlayerComponentSelected = true;
                        showMoreContentIcon();
                    } else if(rowData.isSearchPage){
                        new Handler().postDelayed(() -> Utils.setBrowseFragmentViewParameters(view,
                                (int) getResources().getDimension(R.dimen.grid_browse_fragment_margin_left),
                                (int) getResources().getDimension(R.dimen.browse_fragment_margin_top)), 0);
                    } else if (null != rowData.blockName && rowData.blockName.equalsIgnoreCase("showDetail01")){
                        new Handler().postDelayed(() -> Utils.setBrowseFragmentViewParameters(view,
                                (int) getResources().getDimension(R.dimen.browse_fragment_show_season_margin_left),
                                (int) getResources().getDimension(R.dimen.browse_fragment_margin_top)), 0);
                    }else{
                        Utils.setBrowseFragmentViewParameters(view,
                                (int) getResources().getDimension(R.dimen.browse_fragment_margin_left),
                                (int) getResources().getDimension(R.dimen.browse_fragment_margin_top));
                        hideController();
                    }
                   /* Utils.setBrowseFragmentViewParameters(view,
                            (int) getResources().getDimension(R.dimen.browse_fragment_margin_left),
                            (int) getResources().getDimension(R.dimen.browse_fragment_margin_top));*/
                }
            }

        }
    }


    boolean isFirstTime = true;
    private void showMoreContentIcon(){
        if(isPlayerComponentSelected && isFirstTime && mRowsAdapter != null && mRowsAdapter.size() > 1){
            isFirstTime = false;
            if(appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS){
                getActivity().findViewById(R.id.press_down_button).setVisibility(View.VISIBLE);
            } else {
                getActivity().findViewById(R.id.press_down_button).setVisibility(View.INVISIBLE);
            }
        }
        hideFooterControls();
    }

    private void hideFooterControls(){
        new Handler().postDelayed(() -> hideController(),8000);
    }

    private void hideController() {
        if(appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS){
            try {
                getActivity().findViewById(R.id.press_up_button).setVisibility(View.INVISIBLE);
                getActivity().findViewById(R.id.press_down_button).setVisibility(View.INVISIBLE);
                getActivity().findViewById(R.id.top_logo).setVisibility(View.INVISIBLE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        new Handler().postDelayed(() -> {
            if(null != customVideoVideoPlayerView){
                customVideoVideoPlayerView.pausePlayer();
            }
        },51);

        super.onPause();
    }

    public CustomTVVideoPlayerView getCustomVideoVideoPlayerView(){
        return customVideoVideoPlayerView;
    }

}
