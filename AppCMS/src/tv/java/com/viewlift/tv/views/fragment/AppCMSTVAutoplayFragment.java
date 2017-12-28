package com.viewlift.tv.views.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.component.AppCMSTVViewComponent;
import com.viewlift.tv.views.component.DaggerAppCMSTVViewComponent;
import com.viewlift.tv.views.customviews.AppCMSTVAutoplayCustomLoader;
import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.module.AppCMSTVPageViewModule;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.PageView;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class AppCMSTVAutoplayFragment extends Fragment {

    private static final String TAG = "AutoplayFragment";
    private FragmentInteractionListener fragmentInteractionListener;
    private AppCMSVideoPageBinder binder;
    private AppCMSPresenter appCMSPresenter;
    private AppCMSTVViewComponent appCMSViewComponent;
    private TVPageView pageView;
    private OnPageCreation onPageCreation;
    private CountDownTimer countdownTimer;

    private final int totalCountdownInMillis = 5000;
    private final int countDownIntervalInMillis = 1000;
    private TextView tvCountdown;
    private Context context;
    private boolean cancelCountdown = true;
    private Button cancelCountdownButton;
    private AppCMSTVAutoplayCustomLoader appCMSTVAutoplayCustomLoader;
    private TextView upNextTextView;

    public interface OnPageCreation {
        void onSuccess(AppCMSVideoPageBinder binder);

        void onError(AppCMSVideoPageBinder binder);
    }

    public AppCMSTVAutoplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnPageCreation) {
            try {
                onPageCreation = (OnPageCreation) context;
                this.fragmentInteractionListener = (FragmentInteractionListener) getActivity();
                binder = (AppCMSVideoPageBinder)
                        getArguments().getBinder(context.getString(R.string.fragment_page_bundle_key));
                appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                        .getAppCMSPresenterComponent()
                        .appCMSPresenter();
                appCMSViewComponent = buildAppCMSViewComponent();
            } catch (ClassCastException e) {
                Log.e(TAG, "Could not attach fragment: " + e.toString());
            }
        } else {
            throw new RuntimeException("Attached context must implement " +
                    OnPageCreation.class.getCanonicalName());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        if (activity instanceof OnPageCreation) {
            try {
                onPageCreation = (OnPageCreation) activity;
                this.fragmentInteractionListener = (FragmentInteractionListener) getActivity();
                binder = (AppCMSVideoPageBinder)
                        getArguments().getBinder(activity.getString(R.string.fragment_page_bundle_key));
                appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                        .getAppCMSPresenterComponent()
                        .appCMSPresenter();
                appCMSViewComponent = buildAppCMSViewComponent();
            } catch (ClassCastException e) {
                Log.e(TAG, "Could not attach fragment: " + e.toString());
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
            pageView = appCMSViewComponent.appCMSTVPageView();
        } else {
            pageView = null;
        }

        if (pageView != null) {
            if (pageView.getParent() != null) {
                ((ViewGroup) pageView.getParent()).removeAllViews();
            }
            if (!BaseView.isTablet(context)) {
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
            tvCountdown = (TextView) pageView.findViewById(R.id.countdown_id);
            ImageView movieImage = (ImageView) pageView.findViewById(R.id.autoplay_play_movie_image);
            cancelCountdownButton = (Button) pageView.findViewById(R.id.autoplay_cancel_countdown_button);
            TextView finishedMovieTitle = (TextView) pageView.findViewById(R.id.autoplay_finished_movie_title);
            upNextTextView = (TextView) pageView.findViewById(R.id.up_next_text_view_id);
            appCMSTVAutoplayCustomLoader = (AppCMSTVAutoplayCustomLoader) pageView.findViewById(R.id.autoplay_rotating_loader_view_id);

            if (movieImage != null) {
                movieImage.setOnClickListener(v -> {
                    if (isAdded() && isVisible()) {
                        fragmentInteractionListener.onCountdownFinished();
                    }
                });
            }

            if (cancelCountdownButton != null) {
                cancelCountdownButton.requestFocus();
                cancelCountdownButton.setOnClickListener(v -> {
                    if (cancelCountdown) {
                        stopCountdown();
                        cancelCountdownButton.setText(getString(R.string.back));
                        cancelCountdown = false;
                        if (appCMSTVAutoplayCustomLoader != null) {
                            appCMSTVAutoplayCustomLoader.setVisibility(View.GONE);
                        }
                        if (upNextTextView != null) {
                            upNextTextView.setVisibility(View.GONE);
                        }
                    } else {
                        fragmentInteractionListener.closeActivity();
                    }
                });
            }

            if (finishedMovieTitle != null) {
                finishedMovieTitle.setText(binder.getCurrentMovieName());
            }
            if (pageView.getChildAt(0) != null) {
                pageView.getChildAt(0).setBackgroundResource(R.drawable.autoplay_overlay);
            }
            String imageUrl;
            if (BaseView.isTablet(context) && BaseView.isLandscape(context)) {
                imageUrl = binder.getContentData().getGist().getVideoImageUrl();
            } else {
                imageUrl = binder.getContentData().getGist().getPosterImageUrl();
            }

            Glide.with(context).load(imageUrl)
                    .bitmapTransform(new BlurTransformation(context, 5))
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
        return pageView;
    }

    private void startCountdown() {
        countdownTimer = new CountDownTimer(totalCountdownInMillis, countDownIntervalInMillis) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (isAdded() && isVisible() && tvCountdown != null) {
                    int quantity = (int) (millisUntilFinished / 1000);
                    tvCountdown.setText(Integer.toString(quantity));
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

    private void stopCountdown() {
        countdownTimer.cancel();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                binder = (AppCMSVideoPageBinder) savedInstanceState.getBinder(getString(R.string.app_cms_video_player_binder_key));
            } catch (ClassCastException e) {
                Log.e(TAG, "Could not attach fragment: " + e.toString());
            }
        }
        if (countdownTimer == null) {
            startCountdown();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PageView.isTablet(context) || (binder != null && binder.isFullscreenEnabled())) {
            handleOrientation(getActivity().getResources().getConfiguration().orientation);
        }

        if (pageView == null) {
            Log.e(TAG, "AppCMS page creation error");
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
    public void onStop() {
        super.onStop();
        if (cancelCountdown) {
            stopCountdown();
            if (cancelCountdownButton != null) {
                cancelCountdownButton.setText(getString(R.string.back));
            }
            cancelCountdown = false;
            if (appCMSTVAutoplayCustomLoader != null) {
                appCMSTVAutoplayCustomLoader.setVisibility(View.GONE);
            }
            if (upNextTextView != null) {
                upNextTextView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (binder != null && appCMSViewComponent.tvviewCreator() != null) {
            appCMSPresenter.removeLruCacheItem(context, binder.getPageID());
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

    public static AppCMSTVAutoplayFragment newInstance(Context context, AppCMSVideoPageBinder binder) {
        AppCMSTVAutoplayFragment fragment = new AppCMSTVAutoplayFragment();
        Bundle args = new Bundle();
        args.putBinder(context.getString(R.string.fragment_page_bundle_key), binder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    public interface FragmentInteractionListener {
        void onCountdownFinished();

        void closeActivity();
    }

    public AppCMSTVViewComponent buildAppCMSViewComponent() {
        return DaggerAppCMSTVViewComponent.builder()
                .appCMSTVPageViewModule(new AppCMSTVPageViewModule(context,
                        binder.getAppCMSPageUI(),
                        binder.getAppCMSPageAPI(),
                        binder.getJsonValueKeyMap(),
                        appCMSPresenter))
                .build();
    }
}
