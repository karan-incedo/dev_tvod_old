package com.viewlift.views.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.components.AppCMSViewComponent;
import com.viewlift.views.components.DaggerAppCMSViewComponent;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.customviews.VideoPlayerView;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.modules.AppCMSPageViewModule;

import java.util.List;

/**
 * Created by viewlift on 5/3/17.
 */

public class AppCMSPageFragment extends Fragment {
    private static final String TAG = "AppCMSPageFragment";

    private AppCMSViewComponent appCMSViewComponent;
    private OnPageCreation onPageCreation;
    private AppCMSPresenter appCMSPresenter;
    private AppCMSBinder appCMSBinder;
    private PageView pageView;
    private String videoPageName = "Video Page";
    private String authentication_screen_name = "Authentication Screen";

    private final String FIREBASE_SCREEN_VIEW_EVENT = "screen_view";

    private final String LOGIN_STATUS_KEY = "logged_in_status";
    private final String LOGIN_STATUS_LOGGED_IN = "logged_in";
    private final String LOGIN_STATUS_LOGGED_OUT = "not_logged_in";

    private boolean shouldSendFirebaseViewItemEvent;
    private ViewGroup pageViewGroup;
    private VideoPlayerView videoPlayerView;
    private Button playLiveImageView;

    public interface OnPageCreation {
        void onSuccess(AppCMSBinder appCMSBinder);

        void onError(AppCMSBinder appCMSBinder);
    }

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
            videoPlayerView = (VideoPlayerView) pageView.findViewById(R.id.video_player_id);
            playLiveImageView = (Button) pageView.findViewById(R.id.play_live_image_id);

        } else {
            //Log.e(TAG, "AppCMS page creation error");
            onPageCreation.onError(appCMSBinder);
        }


        if (container != null) {
            container.removeAllViews();
            pageViewGroup = container;
        }
        /**
         * Here we are sending analytics for the screen views. Here we will log the events for
         * the Screen which will come on AppCMSPageActivity.
         */
        if (shouldSendFirebaseViewItemEvent) {
            sendFirebaseAnalyticsEvents(appCMSBinder);
            shouldSendFirebaseViewItemEvent = false;
        }
        if (pageView!=null) {
        if (pageView.findViewById(R.id.home_nested_scroll_view) instanceof NestedScrollView &&
                appCMSBinder.getAppCMSPageUI().getModuleList() != null &&
                appCMSBinder.getAppCMSPageUI().getModuleList().size() >= 2 &&
                appCMSBinder.getAppCMSPageUI().getModuleList().get(1) != null &&
                appCMSBinder.getAppCMSPageUI().getModuleList().get(1).getSettings() != null) {
            NestedScrollView nestedScrollView = (NestedScrollView) pageView.findViewById(R.id.home_nested_scroll_view);


            //System.out.println(positionToScroll+ " positionToScroll "+holder.getChildCount() );
            if (appCMSBinder.getAppCMSPageUI().getModuleList().get(1).getSettings().isShowPIP()) {
                Toast.makeText(getContext(), "Created Scroll Event listener  ", Toast.LENGTH_SHORT).show();
                nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {


                        if (appCMSPresenter.getFirstVisibleChildPosition(v) == 0) {
                            appCMSPresenter.pipPlayerVisible = false;
                            appCMSPresenter.dismissPopupWindowPlayer();
                             resumePlayer(true);
                        } else if (!appCMSPresenter.pipPlayerVisible) {
                            appCMSPresenter.showPopupWindowPlayer(v);
                             resumePlayer(false);
                        }

                    }
                });

                if (appCMSPresenter.getFirstVisibleChildPosition(nestedScrollView) > 0 &&
                        !appCMSPresenter.pipPlayerVisible) {
                    appCMSPresenter.showPopupWindowPlayer(nestedScrollView);
                } else if (appCMSPresenter.getFirstVisibleChildPosition(nestedScrollView) == 0) {
                    appCMSPresenter.dismissPopupWindowPlayer();
                }
            } else {
                appCMSPresenter.dismissPopupWindowPlayer();
            }

        } else if (appCMSPresenter.pipPlayerVisible) {
            appCMSPresenter.dismissPopupWindowPlayer();
            }
        }

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
        if (appCMSVideoPageBinder == null)
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
        if(videoPlayerView != null) {
            videoPlayerView.requestAudioFocus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        updateDataLists();
        resumePlayer(false);
    }

    public void updateDataLists() {
        if (pageView != null) {
            pageView.notifyAdaptersOfUpdate();
            if (!appCMSPresenter.pipPlayerVisible) {
                resumePlayer(true);
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
        return DaggerAppCMSViewComponent.builder()
                .appCMSPageViewModule(new AppCMSPageViewModule(getContext(),
                        appCMSBinder.getAppCMSPageUI(),
                        appCMSBinder.getAppCMSPageAPI(),
                        appCMSPresenter.getAppCMSAndroidModules(),
                        appCMSBinder.getScreenName(),
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
        List<String> modulesToIgnore = getModulesToIgnore();
        if (viewCreator != null && modulesToIgnore != null) {
            boolean updatePage = false;
            if (pageView != null) {
                updatePage = pageView.getParent() != null;
            }

            try {
            pageView = viewCreator.generatePage(getContext(),
                    appCMSBinder.getAppCMSPageUI(),
                    appCMSBinder.getAppCMSPageAPI(),
                    appCMSPresenter.getAppCMSAndroidModules(),
                    appCMSBinder.getScreenName(),
                    appCMSBinder.getJsonValueKeyMap(),
                    appCMSPresenter,
                    modulesToIgnore);

            if (pageViewGroup != null &&
                    pageView != null &&
                    pageView.getParent() == null) {
                removeAllViews(pageViewGroup);
                pageViewGroup.addView(pageView);
                if (updatePage) {
                    updateAllViews(pageViewGroup);
                }
                }
            } catch (Exception e) {
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

    private void removeAllViews(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                removeAllViews(((ViewGroup) viewGroup.getChildAt(i)));
            }
        }
        viewGroup.removeAllViews();
    }

    private void resumePlayer(boolean playerState){
        if (videoPlayerView != null && playLiveImageView != null) {
            if (appCMSPresenter.isUserLoggedIn() && appCMSPresenter.isAppSVOD() && playerState) {
                    playLiveImageView.setVisibility(View.GONE);
                    videoPlayerView.startPlayer();
            } else {
                playLiveImageView.setVisibility(View.VISIBLE);
                videoPlayerView.pausePlayer();
            }
        }
    }
}
