package com.viewlift.views.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.activity.AppCMSPlayVideoActivity;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.components.AppCMSViewComponent;
import com.viewlift.views.components.DaggerAppCMSViewComponent;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.CustomVideoPlayerView;
import com.viewlift.views.customviews.FullPlayerView;
import com.viewlift.views.customviews.MiniPlayerView;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.customviews.VideoPlayerView;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.modules.AppCMSPageViewModule;

import java.lang.ref.SoftReference;
import java.util.List;


/**
 * Created by viewlift on 5/3/17.
 */

public class AppCMSPageFragment extends Fragment {
    private static final String TAG = "AppCMSPageFragment";
    private final String FIREBASE_SCREEN_VIEW_EVENT = "screen_view";
    private final String LOGIN_STATUS_KEY = "logged_in_status";
    private final String LOGIN_STATUS_LOGGED_IN = "logged_in";
    private final String LOGIN_STATUS_LOGGED_OUT = "not_logged_in";
    private AppCMSViewComponent appCMSViewComponent;
    private OnPageCreation onPageCreation;
    private static AppCMSPresenter appCMSPresenter;
    private AppCMSBinder appCMSBinder;
    private PageView pageView;
    private String videoPageName = "Video Page";
    private String authentication_screen_name = "Authentication Screen";


    private boolean shouldSendFirebaseViewItemEvent;
    private ViewGroup pageViewGroup;

    private OnScrollGlobalLayoutListener onScrollGlobalLayoutListener;

    public static AppCMSPageFragment newInstance(Context context, AppCMSBinder appCMSBinder) {
        AppCMSPageFragment fragment = new AppCMSPageFragment();
        fragment.shouldSendFirebaseViewItemEvent = false;
        Bundle args = new Bundle();
        args.putBinder(context.getString(R.string.fragment_page_bundle_key), appCMSBinder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnPageCreation) {
            try {
                onPageCreation = (OnPageCreation) context;

                appCMSBinder =
                        ((AppCMSBinder) getArguments().getBinder(context.getString(R.string.fragment_page_bundle_key)));

                appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                        .getAppCMSPresenterComponent()
                        .appCMSPresenter();
                new SoftReference<Object>(appCMSBinder, appCMSPresenter.getSoftReferenceQueue());
                appCMSViewComponent = buildAppCMSViewComponent();

                shouldSendFirebaseViewItemEvent = true;
            } catch (ClassCastException e) {
                //Log.e(TAG, "Could not attach fragment: " + e.toString());
            } catch (NullPointerException e) {
                //Log.e(TAG, "Could not attach fragment: " + e.toString());
            }
        } else {
            throw new RuntimeException("Attached context must implement " +
                    OnPageCreation.class.getCanonicalName());
        }
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (appCMSViewComponent == null && appCMSBinder != null) {
            try {
                appCMSViewComponent = buildAppCMSViewComponent();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        if (appCMSViewComponent != null) {
            pageView = appCMSViewComponent.appCMSPageView();
        } else {
            pageView = null;
            //Log.e(TAG, "AppCMS page creation error");
            onPageCreation.onError(appCMSBinder);
        }

        if (pageView != null) {
            if (pageView.getParent() != null) {
                ((ViewGroup) pageView.getParent()).removeAllViews();
            }
            onPageCreation.onSuccess(appCMSBinder);

        } else {
            onPageCreation.onError(appCMSBinder);
        }

        if (container != null) {
            container.removeAllViews();
            pageViewGroup = container;
        }

        /*
         * Here we are sending analytics for the screen views. Here we will log the events for
         * the Screen which will come on AppCMSPageActivity.
         */
        if (shouldSendFirebaseViewItemEvent) {
            sendFirebaseAnalyticsEvents(appCMSBinder);
            shouldSendFirebaseViewItemEvent = false;
        }
        if (pageView != null) {
            pageView.setOnScrollChangeListener(new PageView.OnScrollChangeListener() {
                @Override
                public void onScroll(int dx, int dy) {
                    if (appCMSBinder != null) {
                        appCMSBinder.setxScroll(appCMSBinder.getxScroll() + dx);
                        appCMSBinder.setyScroll(appCMSBinder.getyScroll() + dy);
                    }
                }

                @Override
                public void setCurrentPosition(int position) {
                    if (appCMSBinder != null) {
                        appCMSBinder.setCurrentScrollPosition(position);
                    }
                }
            });

            if (onScrollGlobalLayoutListener != null) {
                pageView.getViewTreeObserver().removeOnGlobalLayoutListener(onScrollGlobalLayoutListener);
                onScrollGlobalLayoutListener.appCMSBinder = appCMSBinder;
                onScrollGlobalLayoutListener.pageView = pageView;
            } else {
                onScrollGlobalLayoutListener = new OnScrollGlobalLayoutListener(appCMSBinder,
                        pageView);
            }

            pageView.getViewTreeObserver().addOnGlobalLayoutListener(onScrollGlobalLayoutListener);
        }

        Log.d(TAG, "PageView created");

        return pageView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                appCMSBinder = (AppCMSBinder)
                        savedInstanceState.getBinder(getString(R.string.app_cms_binder_key));
            } catch (ClassCastException e) {
                //Log.e(TAG, "Could not attach fragment: " + e.toString());
            }
        }
    }

    private void sendFirebaseAnalyticsEvents(AppCMSBinder appCMSVideoPageBinder) {
        if (appCMSPresenter == null || appCMSVideoPageBinder == null)
            return;
        if (appCMSPresenter.getmFireBaseAnalytics() == null)
            return;

        if (appCMSVideoPageBinder.getScreenName() == null ||
                appCMSVideoPageBinder.getScreenName().equalsIgnoreCase(authentication_screen_name))
            return;

        Bundle bundle = new Bundle();
        if (!appCMSVideoPageBinder.isUserLoggedIn()) {
            appCMSPresenter.getmFireBaseAnalytics().setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_OUT);

            bundle.putString(FIREBASE_SCREEN_VIEW_EVENT, appCMSVideoPageBinder.getScreenName());
        } else {
            appCMSPresenter.getmFireBaseAnalytics().setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_IN);

            if (!TextUtils.isEmpty(appCMSVideoPageBinder.getScreenName()) && appCMSVideoPageBinder.getScreenName().matches(videoPageName))
                bundle.putString(FIREBASE_SCREEN_VIEW_EVENT, appCMSVideoPageBinder.getScreenName() + "-" + appCMSVideoPageBinder.getPageName());
            else
                bundle.putString(FIREBASE_SCREEN_VIEW_EVENT, appCMSVideoPageBinder.getScreenName());
        }

        //Logs an app event.
        appCMSPresenter.getmFireBaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
        //Sets whether analytics collection is enabled for this app on this device.
        appCMSPresenter.getmFireBaseAnalytics().setAnalyticsCollectionEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PageView.isTablet(getContext()) || (appCMSBinder != null && appCMSBinder.isFullScreenEnabled())) {
            handleOrientation(getActivity().getResources().getConfiguration().orientation);
        }
        updateDataLists();

        if (pageView != null && pageView.findChildViewById(R.id.video_player_id) != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    setPageOriantationForVideoPage();

                }
            }, 3000);
            View nextChild = (pageView.findChildViewById(R.id.video_player_id));
            ViewGroup group = (ViewGroup) nextChild;
            if ((group.getChildAt(0)) != null) {
                ((CustomVideoPlayerView) group.getChildAt(0)).requestAudioFocus();
                appCMSPresenter.videoPlayerView = ((CustomVideoPlayerView) group.getChildAt(0));
            }
        } else if (!BaseView.isTablet(getContext()) && appCMSPresenter != null) {
            appCMSPresenter.restrictPortraitOnly();
        }
        setMiniPlayer();

        if (pageView != null &&
                appCMSPresenter.videoPlayerView != null) {
            appCMSPresenter.videoPlayerView.requestAudioFocus();
        }

        try {
            CastServiceProvider.getInstance(getActivity()).setCastCallBackListener(castCallBackListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private CastServiceProvider.CastCallBackListener castCallBackListener = new CastServiceProvider.CastCallBackListener() {
        @Override
        public void onCastStatusUpdate() {
            if (pageView != null && pageView.findChildViewById(R.id.video_player_id) != null) {
                if (pageView.findChildViewById(R.id.video_player_id) instanceof FrameLayout) {

                    FrameLayout rootView = (FrameLayout) pageView.findChildViewById(R.id.video_player_id);
                    if (rootView.getChildAt(0) instanceof CustomVideoPlayerView)
                        ((CustomVideoPlayerView) rootView.getChildAt(0)).showOverlayWhenCastingConnected();
                }
            }
            if (appCMSPresenter.videoPlayerView != null) {
                appCMSPresenter.videoPlayerView.showOverlayWhenCastingConnected();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        updateDataLists();

        if (pageView != null && pageView.findChildViewById(R.id.video_player_id) != null) {
            View nextChild = (pageView.findChildViewById(R.id.video_player_id));
            ViewGroup group = (ViewGroup) nextChild;
            if (group.getChildAt(0) != null) {
                ((VideoPlayerView) group.getChildAt(0)).pausePlayer();
            }
        }

        if (appCMSPresenter != null) {
            if (appCMSPresenter.videoPlayerView != null && appCMSPresenter.videoPlayerView.getPlayer() != null) {
                appCMSPresenter.videoPlayerView.pausePlayer();
            }
            appCMSPresenter.dismissPopupWindowPlayer(false);
        }
    }

    public void updateDataLists() {
        if (pageView != null) {
            pageView.notifyAdaptersOfUpdate();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (appCMSPresenter != null) {
            appCMSPresenter.closeSoftKeyboard();
        }
        appCMSBinder = null;
        pageView = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pageViewGroup != null) {
            pageViewGroup.removeAllViews();
        }

        if (pageView != null && pageView.findChildViewById(R.id.video_player_id) != null) {
            View playerParent = (pageView.findChildViewById(R.id.video_player_id));
            ViewGroup group = (ViewGroup) playerParent;
            if (group.getChildAt(0) != null)
                ((VideoPlayerView) group.getChildAt(0)).pausePlayer();

            if (group.getChildAt(0) != null && ((CustomVideoPlayerView) group.getChildAt(0)).entitlementCheckTimer != null) {
                ((CustomVideoPlayerView) group.getChildAt(0)).entitlementCheckTimer.cancel();
                ((CustomVideoPlayerView) group.getChildAt(0)).entitlementCheckTimer = null;

            }
        }
        try {
            CastServiceProvider.getInstance(getActivity()).setCastCallBackListener(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBinder(getString(R.string.app_cms_binder_key), appCMSBinder);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        if (appCMSPresenter != null) {
            appCMSPresenter.isconfig = true;

            if (appCMSPresenter.isAutoRotate()) {
                if (pageView != null && pageView.findChildViewById(R.id.video_player_id) != null &&
                        !BaseView.isTablet(getActivity())) {

                    View nextChild = (pageView.findChildViewById(R.id.video_player_id));
                    ViewGroup group = (ViewGroup) nextChild;
                    if ((group.getChildAt(0)) == null &&
                            newConfig.orientation == Configuration.ORIENTATION_PORTRAIT &&
                            AppCMSPresenter.isFullScreenVisible) {
                        appCMSPresenter.videoPlayerView.updateFullscreenButtonState(Configuration.ORIENTATION_PORTRAIT);

                    } else if ((group.getChildAt(0)) != null &&
                            !(group instanceof FullPlayerView) &&
                            newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

                        appCMSPresenter.videoPlayerView = ((CustomVideoPlayerView) group.getChildAt(0));
                        appCMSPresenter.videoPlayerView.updateFullscreenButtonState(Configuration.ORIENTATION_LANDSCAPE);
                    }
                }
            }
            // if (!appCMSPresenter.isFullScreenVisible) {
            handleOrientation(newConfig.orientation);
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

    public AppCMSViewComponent buildAppCMSViewComponent() throws NullPointerException {
        String screenName = appCMSBinder.getScreenName();
        /* if (!appCMSPresenter.isPageAVideoPage(screenName)) {
            screenName = appCMSBinder.getPageId();
        }*/

        return DaggerAppCMSViewComponent.builder()
                .appCMSPageViewModule(new AppCMSPageViewModule(getContext(),
                        appCMSBinder.getAppCMSPageUI(),
                        appCMSBinder.getAppCMSPageAPI(),
                        appCMSPresenter.getAppCMSAndroidModules(),
                        screenName,
                        appCMSBinder.getJsonValueKeyMap(),
                        appCMSPresenter))
                .build();
    }

    public ViewCreator getViewCreator() {
        if (appCMSViewComponent != null) {
            return appCMSViewComponent.viewCreator();
        }
        return null;
    }

    public List<String> getModulesToIgnore() {
        if (appCMSViewComponent != null) {
            return appCMSViewComponent.modulesToIgnore();
        }
        return null;
    }

    public void refreshView(AppCMSBinder appCMSBinder) {
        sendFirebaseAnalyticsEvents(appCMSBinder);
        this.appCMSBinder = appCMSBinder;
        ViewCreator viewCreator = getViewCreator();
        viewCreator.setIgnoreBinderUpdate(true);
        List<String> modulesToIgnore = getModulesToIgnore();

        if (viewCreator != null && modulesToIgnore != null) {
            boolean updatePage = false;
            if (pageView != null) {
                updatePage = pageView.getParent() != null;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setPageOriantationForVideoPage();

                    }
                }, 1000);
            }

            try {
                String screenName = appCMSBinder.getScreenName();
                if (!appCMSPresenter.isPageAVideoPage(screenName)) {
                    screenName = appCMSBinder.getPageId();
                }

                pageView = viewCreator.generatePage(getContext(),
                        appCMSBinder.getAppCMSPageUI(),
                        appCMSBinder.getAppCMSPageAPI(),
                        appCMSPresenter.getAppCMSAndroidModules(),
                        screenName,
                        appCMSBinder.getJsonValueKeyMap(),
                        appCMSPresenter,
                        modulesToIgnore);

                if (pageViewGroup != null &&
                        pageView != null &&
                        pageView.getParent() == null) {
                    if (pageViewGroup.getChildCount() > 0) {
                        pageViewGroup.removeAllViews();
                    }
                    pageViewGroup.addView(pageView);
                    if (updatePage) {
                        updateAllViews(pageViewGroup);
                    }
                }
                if (updatePage) {
                    updateAllViews(pageViewGroup);
                    pageView.notifyAdaptersOfUpdate();
                }

                if (pageView != null) {
                    pageView.setOnScrollChangeListener(new PageView.OnScrollChangeListener() {
                        @Override
                        public void onScroll(int dx, int dy) {
                            appCMSBinder.setxScroll(appCMSBinder.getxScroll() + dx);
                            appCMSBinder.setyScroll(appCMSBinder.getyScroll() + dy);
                        }

                        @Override
                        public void setCurrentPosition(int position) {
                            appCMSBinder.setCurrentScrollPosition(position);
                        }
                    });

                    if (onScrollGlobalLayoutListener != null) {
                        pageView.getViewTreeObserver().removeOnGlobalLayoutListener(onScrollGlobalLayoutListener);
                        onScrollGlobalLayoutListener.appCMSBinder = appCMSBinder;
                        onScrollGlobalLayoutListener.pageView = pageView;
                    } else {
                        onScrollGlobalLayoutListener = new OnScrollGlobalLayoutListener(appCMSBinder,
                                pageView);
                    }

                    pageView.getViewTreeObserver().addOnGlobalLayoutListener(onScrollGlobalLayoutListener);
                }

                if (pageViewGroup != null) {
                    pageViewGroup.requestLayout();
                }
            } catch (Exception e) {
                //
                e.printStackTrace();
            }
        }
        setMiniPlayer();

    }

    private void updateAllViews(ViewGroup pageViewGroup) {
        if (pageViewGroup.getVisibility() == View.VISIBLE) {
            pageViewGroup.setVisibility(View.GONE);
            pageViewGroup.setVisibility(View.VISIBLE);
        }
        pageViewGroup.requestLayout();
        for (int i = 0; i < pageViewGroup.getChildCount(); i++) {
            View child = pageViewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                updateAllViews((ViewGroup) child);
            } else {
                if (child.getVisibility() == View.VISIBLE) {
                    child.setVisibility(View.GONE);
                    child.setVisibility(View.VISIBLE);
                }
                child.requestLayout();
            }
        }
    }

    public synchronized void setPageOriantationForVideoPage() {
        /**
         * if current activity is video player then restrict to landscape only
         */
        if (appCMSPresenter.getCurrentActivity() instanceof AppCMSPlayVideoActivity) {
            appCMSPresenter.restrictLandscapeOnly();
            return;
        }
        if (pageView != null && pageView.findChildViewById(R.id.video_player_id) != null &&
                appCMSPresenter.isAutoRotate()) {
            appCMSPresenter.unrestrictPortraitOnly();
        } else if (!BaseView.isTablet(getContext())) {
            System.out.println("config from setPageOriantationForVideoPage fragment");

            appCMSPresenter.restrictPortraitOnly();
        } else if (BaseView.isTablet(getContext())) {
            appCMSPresenter.unrestrictPortraitOnly();
        }
    }

    RecyclerView.OnScrollListener scrollListenerForMiniPlayer = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView v, int newState) {
            super.onScrollStateChanged(v, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    synchronized (v) {
                        int videoPlayerModulePostition = 0;
                        if (v.getLayoutManager() != null &&
                                (v.getLayoutManager()) instanceof LinearLayoutManager) {
                            int firstVisibleIndex = ((LinearLayoutManager) v.getLayoutManager()).findFirstVisibleItemPosition();
                            ModuleList singleVideoUI = null;
                            if (pageView != null && pageView.getAppCMSPageUI() != null && pageView.getAppCMSPageUI().getModuleList() != null) {
                                singleVideoUI = appCMSPresenter.getModuleListByName(pageView.getAppCMSPageUI().getModuleList(), getString(R.string.app_cms_page_video_player_module_key));

                                if (singleVideoUI != null) {
                                    videoPlayerModulePostition = singleVideoUI.getModulePosition();
                                }
                                if (firstVisibleIndex >= videoPlayerModulePostition && singleVideoUI != null &&
                                        singleVideoUI.getSettings().isShowPIP()) {
                                    if (appCMSPresenter.relativeLayoutPIP == null) {
                                        appCMSPresenter.relativeLayoutPIP = new MiniPlayerView(getActivity(), appCMSPresenter, v);
                                    }
                                    View nextChild = (pageView.findChildViewById(R.id.video_player_id));
                                    ViewGroup group = (ViewGroup) nextChild;
                                    if (group != null && appCMSPresenter.videoPlayerView != null && !appCMSPresenter.pipPlayerVisible) {
                                        appCMSPresenter.showPopupWindowPlayer(v, group);
                                    }

                                } else {
                                    View nextChild = (pageView.findChildViewById(R.id.video_player_id));
                                    ViewGroup group = (ViewGroup) nextChild;
                                    if (group != null && appCMSPresenter.videoPlayerView != null) {
                                        if (appCMSPresenter.videoPlayerView != null && !appCMSPresenter.videoPlayerView.hideMiniPlayer) {
                                            appCMSPresenter.videoPlayerView.resumePlayerLastState();
                                        }
                                        appCMSPresenter.unrestrictPortraitOnly();
                                        appCMSPresenter.dismissPopupWindowPlayer(false);
                                    }

                                }
                            }
                        }
                    }
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:

                    break;
                default:
                    break;
            }
        }
    };

    int firstVisibleIndex = -1;
    int videoPlayerModulePostition = 0;

    public void setMiniPlayer() {
        if ((pageView != null && pageView.getAppCMSPageUI() != null && pageView.findViewById(R.id.home_nested_scroll_view) != null) && pageView.findViewById(R.id.home_nested_scroll_view) instanceof RecyclerView) {
            RecyclerView nestedScrollView = pageView.findViewById(R.id.home_nested_scroll_view);
//            if (appCMSPresenter.relativeLayoutPIP == null) {
//                appCMSPresenter.relativeLayoutPIP = new MiniPlayerView(getActivity(), appCMSPresenter, nestedScrollView);
//            }
            nestedScrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nestedScrollView.addOnScrollListener(scrollListenerForMiniPlayer);
                    nestedScrollView.refreshDrawableState();
                    nestedScrollView.getAdapter().notifyDataSetChanged();
                    firstVisibleIndex = ((LinearLayoutManager) nestedScrollView.getLayoutManager()).findFirstVisibleItemPosition();

                    if (pageView != null) {
                        ModuleList singleVideoUI = appCMSPresenter.getModuleListByName(pageView.getAppCMSPageUI().getModuleList(), getString(R.string.app_cms_page_video_player_module_key));
                        if (singleVideoUI != null) {
                            videoPlayerModulePostition = singleVideoUI.getModulePosition();
                        }

                        if (firstVisibleIndex >= 0) {
                            if (firstVisibleIndex >= videoPlayerModulePostition && singleVideoUI != null &&
                                    singleVideoUI.getSettings().isShowPIP()) {
                                if (appCMSPresenter.isPagePrimary(appCMSBinder.getScreenName()) || appCMSPresenter.isPagePrimary(appCMSBinder.getPageId())) {
                                    if (appCMSPresenter.relativeLayoutPIP == null) {
                                        appCMSPresenter.relativeLayoutPIP = new MiniPlayerView(getActivity(), appCMSPresenter, nestedScrollView);
                                    }
                                    View nextChild = (pageView.findChildViewById(R.id.video_player_id));
                                    ViewGroup group = (ViewGroup) nextChild;
                                    if (nextChild != null && appCMSPresenter.videoPlayerView != null) {
                                        appCMSPresenter.videoPlayerView.requestAudioFocus();
                                        appCMSPresenter.showPopupWindowPlayer(nestedScrollView, group);
                                    }
                                }
                            } else {
                                appCMSPresenter.dismissPopupWindowPlayer(false);
                            }
                        } else {
                            appCMSPresenter.dismissPopupWindowPlayer(false);
                        }
                    }
                }
            }, 10);
        }
    }

    private void removeAllViews(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                removeAllViews(((ViewGroup) viewGroup.getChildAt(i)));
            }
        }
        viewGroup.removeAllViews();
    }

    public interface OnPageCreation {
        void onSuccess(AppCMSBinder appCMSBinder);

        void onError(AppCMSBinder appCMSBinder);

        // void enterFullScreenVideoPlayer();

        // void exitFullScreenVideoPlayer(boolean exitFullScreenVideoPlayer);
    }

    private static class OnScrollGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private AppCMSBinder appCMSBinder;
        private PageView pageView;

        public OnScrollGlobalLayoutListener(AppCMSBinder appCMSBinder,
                                            PageView pageView) {
            this.appCMSBinder = appCMSBinder;
            this.pageView = pageView;
        }

        @Override
        public void onGlobalLayout() {
            if (pageView != null) {
                if (appCMSBinder != null && appCMSBinder.getPageId() != null) {

                    if (appCMSBinder.isScrollOnLandscape() != BaseView.isLandscape(pageView.getContext())) {
                        appCMSBinder.setxScroll(0);
                        appCMSBinder.setyScroll(0);
                        pageView.scrollToPosition(appCMSBinder.getCurrentScrollPosition());
                    } else {
                        if (!appCMSPresenter.isViewPlanPage(appCMSBinder.getPageId())) {
                            int x = appCMSBinder.getxScroll();
                            int y = appCMSBinder.getyScroll();
                            pageView.scrollToPosition(-x, -y);
                            pageView.scrollToPosition(x, y);
                        }
                    }
                    appCMSBinder.setScrollOnLandscape(BaseView.isLandscape(pageView.getContext()));
                }
                pageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }

        public AppCMSBinder getAppCMSBinder() {
            return appCMSBinder;
        }

        public void setAppCMSBinder(AppCMSBinder appCMSBinder) {
            this.appCMSBinder = appCMSBinder;
        }

        public PageView getPageView() {
            return pageView;
        }

        public void setPageView(PageView pageView) {
            this.pageView = pageView;
        }
    }

}

