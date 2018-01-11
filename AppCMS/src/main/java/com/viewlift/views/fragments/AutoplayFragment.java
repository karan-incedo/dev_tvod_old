package com.viewlift.views.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.components.AppCMSViewComponent;
import com.viewlift.views.components.DaggerAppCMSViewComponent;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.modules.AppCMSPageViewModule;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * This fragment is the manifestation of the autoplay screen which opens whenever a movie gets
 * completed and a new movie is to be played
 */
public class AutoplayFragment extends Fragment {
    //private static final String TAG = "AutoplayFragment";
    private static final int TOTAL_COUNTDOWN_IN_MILLIS = 13000;
    private static final int COUNTDOWN_INTERVAL_IN_MILLIS = 1000;
    private FragmentInteractionListener fragmentInteractionListener;
    private AppCMSVideoPageBinder binder;
    private AppCMSPresenter appCMSPresenter;
    private AppCMSViewComponent appCMSViewComponent;
    private PageView pageView;
    private OnPageCreation onPageCreation;
    private CountDownTimer countdownTimer;
    private TextView tvCountdown;

    public AutoplayFragment() {
        // Required empty public constructor
    }

    public static AutoplayFragment newInstance(Context context, AppCMSVideoPageBinder binder) {
        AutoplayFragment fragment = new AutoplayFragment();
        Bundle args = new Bundle();
        args.putBinder(context.getString(R.string.fragment_page_bundle_key), binder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnPageCreation) {
            try {
                onPageCreation = (OnPageCreation) context;
                super.onAttach(context);
                this.fragmentInteractionListener = (FragmentInteractionListener) getActivity();
                binder = (AppCMSVideoPageBinder)
                        getArguments().getBinder(context.getString(R.string.fragment_page_bundle_key));
                appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                        .getAppCMSPresenterComponent()
                        .appCMSPresenter();
                appCMSViewComponent = buildAppCMSViewComponent();
            } catch (ClassCastException e) {
                //Log.e(TAG, "Could not attach fragment: " + e.toString());
            }
        } else {
            throw new RuntimeException("Attached context must implement " +
                    OnPageCreation.class.getCanonicalName());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (appCMSViewComponent == null && binder != null) {
            appCMSViewComponent = buildAppCMSViewComponent();
        }

        if (appCMSViewComponent != null) {
            pageView = appCMSViewComponent.appCMSPageView();
        } else {
            pageView = null;
        }

        if (pageView != null) {
            if (pageView.getParent() != null) {
                ((ViewGroup) pageView.getParent()).removeAllViews();
            }

            if (!BaseView.isTablet(getContext())) {
                appCMSPresenter.restrictPortraitOnly();
            } else {
                appCMSPresenter.unrestrictPortraitOnly();
            }
            onPageCreation.onSuccess(binder);
        }
        if (container != null) {
            container.removeAllViews();
        }

        if (pageView != null) {
            tvCountdown = (TextView) pageView.findChildViewById(R.id.countdown_id);
            Button playButton = (Button) pageView.findChildViewById(R.id.autoplay_play_button);

            if (playButton != null) {
                playButton.setOnClickListener(v -> {
                    if (isAdded() && isVisible()) {
                        fragmentInteractionListener.onCountdownFinished();
                    }
                });
            }
            if (pageView.getChildAt(0) != null) {
                pageView.getChildAt(0)
                        .setBackgroundColor(Color.parseColor(
                                appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                                        .getBackgroundColor().replace("#", "#DD")));
            }

            String imageUrl = null;
            Uri imageURI = null;
            boolean loadImageFromLocalSystem;

            // TODO: Nov 15, '17 getVideoImageUrl & getPosterImageUrl will be replaced with imageGist.

            if (BaseView.isTablet(getContext()) && BaseView.isLandscape(getContext())) {
                if (URLUtil.isFileUrl(binder.getContentData().getGist().getVideoImageUrl())) {
                    loadImageFromLocalSystem = true;
                    imageURI = Uri.parse(binder.getContentData().getGist().getVideoImageUrl());
                } else {
                    loadImageFromLocalSystem = false;
                    imageUrl = binder.getContentData().getGist().getVideoImageUrl();
                }
            } else {
                if (URLUtil.isFileUrl(binder.getContentData().getGist().getPosterImageUrl())) {
                    loadImageFromLocalSystem = true;
                    imageURI = Uri.parse(binder.getContentData().getGist().getPosterImageUrl());
                } else {
                    loadImageFromLocalSystem = false;
                    imageUrl = binder.getContentData().getGist().getPosterImageUrl();
                    if(imageUrl == null)
                     imageUrl = binder.getContentData().getGist().getVideoImageUrl();
                }
            }

            if (loadImageFromLocalSystem) {
                Glide.with(getContext()).load(imageURI)
                        .bitmapTransform(new BlurTransformation(getContext()))
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource,
                                                        GlideAnimation<? super GlideDrawable>
                                                                glideAnimation) {
                                if (isAdded() && isVisible()) {
                                    pageView.setBackground(resource);
                                }
                            }
                        });
            } else {
                Glide.with(getContext()).load(imageUrl)
                        .bitmapTransform(new BlurTransformation(getContext()))
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource,
                                                        GlideAnimation<? super GlideDrawable>
                                                                glideAnimation) {
                                if (isAdded() && isVisible()) {
                                    pageView.setBackground(resource);
                                }
                            }
                        });
            }
        }
        return pageView;
    }

    private void startCountdown() {
        countdownTimer = new CountDownTimer(TOTAL_COUNTDOWN_IN_MILLIS, COUNTDOWN_INTERVAL_IN_MILLIS) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (isAdded() && isVisible() && tvCountdown != null) {
                    int quantity = (int) (millisUntilFinished / 1000);
                    tvCountdown.setText(getResources().getQuantityString(R.plurals.countdown_seconds,
                            quantity, quantity));
                }
            }

            @Override
            public void onFinish() {
                if (isAdded() && isVisible()) {
                    fragmentInteractionListener.onCountdownFinished();
                }
            }
        }.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                binder = (AppCMSVideoPageBinder) savedInstanceState.getBinder(getString(R.string.app_cms_video_player_binder_key));
            } catch (ClassCastException e) {
                //Log.e(TAG, "Could not attach fragment: " + e.toString());
            }
        }

        if (countdownTimer == null) {
            startCountdown();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PageView.isTablet(getContext()) || (binder != null && binder.isFullscreenEnabled())) {
            handleOrientation(getActivity().getResources().getConfiguration().orientation);
        }

        if (pageView == null) {
            //Log.e(TAG, "AppCMS page creation error");
            onPageCreation.onError(binder);
        } else {
            pageView.notifyAdaptersOfUpdate();
        }

        if (appCMSPresenter != null) {
            appCMSPresenter.dismissOpenDialogs(null);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (binder != null && appCMSViewComponent.viewCreator() != null) {
            appCMSPresenter.removeLruCacheItem(getContext(), binder.getPageID());
        }

        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }

        binder = null;
        pageView = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBinder(getString(R.string.app_cms_binder_key), binder);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        handleOrientation(newConfig.orientation);
    }

    public AppCMSViewComponent buildAppCMSViewComponent() {
        return DaggerAppCMSViewComponent.builder()
                .appCMSPageViewModule(new AppCMSPageViewModule(getContext(),
                        binder.getAppCMSPageUI(),
                        binder.getAppCMSPageAPI(),
                        appCMSPresenter.getAppCMSAndroidModules(),
                        binder.getScreenName(),
                        binder.getJsonValueKeyMap(),
                        appCMSPresenter))
                .build();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    public interface OnPageCreation {
        void onSuccess(AppCMSVideoPageBinder binder);

        void onError(AppCMSVideoPageBinder binder);
    }

    public interface FragmentInteractionListener {
        void onCountdownFinished();
    }
}
