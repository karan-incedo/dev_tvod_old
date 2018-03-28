package com.viewlift.views.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.components.AppCMSViewComponent;
import com.viewlift.views.components.DaggerAppCMSViewComponent;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.customviews.VideoPlayerView;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.modules.AppCMSPageViewModule;

import java.lang.ref.SoftReference;
import java.util.List;

/*
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
    private AppCMSPresenter appCMSPresenter;
    private AppCMSBinder appCMSBinder;
    private PageView pageView;
    private String videoPageName = "Video Page";
    private String authentication_screen_name = "Authentication Screen";
    private boolean shouldSendFirebaseViewItemEvent;
    private ViewGroup pageViewGroup;
    private VideoPlayerView videoPlayerView;

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
            appCMSViewComponent = buildAppCMSViewComponent();
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
            //Log.e(TAG, "AppCMS page creation error");
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
            if (pageView.findViewById(R.id.home_nested_scroll_view) instanceof NestedScrollView &&
                    appCMSBinder != null &&
                    appCMSBinder.getAppCMSPageUI() != null &&
                    appCMSBinder.getAppCMSPageUI().getModuleList() != null &&
                    appCMSBinder.getAppCMSPageUI().getModuleList().size() >= 2 &&
                    appCMSBinder.getAppCMSPageUI().getModuleList().get(1) != null &&
                    appCMSBinder.getAppCMSPageUI().getModuleList().get(1).getSettings() != null) {
                NestedScrollView nestedScrollView = (NestedScrollView)
                        pageView.findViewById(R.id.home_nested_scroll_view);

                //System.out.println(positionToScroll+ " positionToScroll "+holder.getChildCount() );
                if (appCMSBinder.getAppCMSPageUI().getModuleList().get(1).getSettings().isShowPIP()) {
                    Toast.makeText(getContext(), "Created Scroll Event listener  ", Toast.LENGTH_SHORT).show();
                    nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                            (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                                if (appCMSPresenter.getFirstVisibleChildPosition(v) == 0) {
                                    appCMSPresenter.pipPlayerVisible = false;
                                    appCMSPresenter.dismissPopupWindowPlayer();

                                    if (videoPlayerView != null) {
                                        videoPlayerView.startPlayer();
                                    }

                                } else if (!appCMSPresenter.pipPlayerVisible) {
                                    appCMSPresenter.showPopupWindowPlayer(v,
                                            0);
                                    if (videoPlayerView != null) {
                                        videoPlayerView.pausePlayer();
                                    }
                                }
                            });

                    if (appCMSPresenter.getFirstVisibleChildPosition(nestedScrollView) > 0 &&
                            !appCMSPresenter.pipPlayerVisible) {
                        appCMSPresenter.showPopupWindowPlayer(nestedScrollView, 0);
                    } else if (appCMSPresenter.getFirstVisibleChildPosition(nestedScrollView) == 0) {
                        appCMSPresenter.dismissPopupWindowPlayer();
                    }
                } else {
                    appCMSPresenter.dismissPopupWindowPlayer();
                }

            } else if (appCMSPresenter.pipPlayerVisible) {
                appCMSPresenter.dismissPopupWindowPlayer();
            }

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
    }

    @Override
    public void onPause() {
        super.onPause();
        updateDataLists();
    }

    public void updateDataLists() {
        if (pageView != null) {
            pageView.notifyAdaptersOfUpdate();
            if (videoPlayerView != null && !appCMSPresenter.pipPlayerVisible) {
                videoPlayerView.startPlayer();
            }
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBinder(getString(R.string.app_cms_binder_key), appCMSBinder);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        handleOrientation(newConfig.orientation);
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

    public AppCMSViewComponent buildAppCMSViewComponent() {
        String screenName = appCMSBinder.getScreenName();
        if (!appCMSPresenter.isPageAVideoPage(screenName)) {
            screenName = appCMSBinder.getPageId();
        }
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

        if (modulesToIgnore != null) {
            boolean updatePage = false;
            if (pageView != null) {
                updatePage = pageView.getParent() != null;
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
            }
        }
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

    public interface OnPageCreation {
        void onSuccess(AppCMSBinder appCMSBinder);

        void onError(AppCMSBinder appCMSBinder);

        void enterFullScreenVideoPlayer();

        void exitFullScreenVideoPlayer(boolean exitFullScreenVideoPlayer);
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
                if (appCMSBinder != null) {

                    if (appCMSBinder.isScrollOnLandscape() != BaseView.isLandscape(pageView.getContext())) {
                        appCMSBinder.setxScroll(0);
                        appCMSBinder.setyScroll(0);
                        pageView.scrollToPosition(appCMSBinder.getCurrentScrollPosition());
                    } else {

                        int x = appCMSBinder.getxScroll();
                        int y = appCMSBinder.getyScroll();
                        pageView.scrollToPosition(-x, -y);
                        pageView.scrollToPosition(x, y);
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
