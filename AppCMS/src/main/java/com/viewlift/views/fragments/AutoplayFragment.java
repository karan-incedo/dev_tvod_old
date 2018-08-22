package com.viewlift.views.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.AppCMSTransactionDataValue;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.components.AppCMSViewComponent;
import com.viewlift.views.components.DaggerAppCMSViewComponent;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.modules.AppCMSPageViewModule;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * This fragment is the manifestation of the autoplay screen which opens whenever a movie gets
 * completed and a new movie is to be played
 */
public class AutoplayFragment extends Fragment {
    //private static final String TAG = "AutoplayFragment";
    private static final int TOTAL_COUNTDOWN_IN_MILLIS = 13000;
    private static final int COUNTDOWN_INTERVAL_IN_MILLIS = 1000;
    private static final String TAG = "AutoplayFragment";
    private int totalCountdownInMillis;
    private FragmentInteractionListener fragmentInteractionListener;
    private AppCMSVideoPageBinder binder;
    private AppCMSPresenter appCMSPresenter;
    private AppCMSViewComponent appCMSViewComponent;
    private PageView pageView;
    private OnPageCreation onPageCreation;
    private CountDownTimer countdownTimer;
    private TextView tvCountdown;
    Context mContext;

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
        mContext = context;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
                playButton.setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
                playButton.setOnClickListener(v -> {
                    checkForTvodContent();

                });
            }

            Button cancelButton = (Button) pageView.findChildViewById(R.id.autoplay_cancel_button);

            if (cancelButton != null) {
                cancelButton.setOnClickListener(v -> {
                    fragmentInteractionListener.cancelCountdown();
                });
            }

            if (pageView.getChildAt(0) != null) {
                pageView.getChildAt(0)
                        .setBackgroundColor(Color.parseColor(
                                appCMSPresenter.getAppBackgroundColor()
                                        .replace("#", "#DD")));
            }

            String imageUrl = null;
            Uri imageURI = null;
            boolean loadImageFromLocalSystem;

            // TODO: getVideoImageUrl & getPosterImageUrl to be replaced with imageGist.

            if (BaseView.isTablet(getContext()) && BaseView.isLandscape(getContext())) {
                if (URLUtil.isFileUrl(binder.getContentData().getGist().getVideoImageUrl())) {
                    loadImageFromLocalSystem = true;
                    imageURI = Uri.parse(binder.getContentData().getGist().getVideoImageUrl());
                } else if (binder.getContentData().getGist().getImageGist() != null &&
                        !TextUtils.isEmpty(binder.getContentData().getGist().getImageGist().get_16x9())) {
                    loadImageFromLocalSystem = false;
                    imageUrl = binder.getContentData().getGist().getImageGist().get_16x9();
                } else {
                    loadImageFromLocalSystem = false;
                    imageUrl = binder.getContentData().getGist().getVideoImageUrl();
                }
            } else {
                if (URLUtil.isFileUrl(binder.getContentData().getGist().getPosterImageUrl())) {
                    loadImageFromLocalSystem = true;
                    imageURI = Uri.parse(binder.getContentData().getGist().getPosterImageUrl());
                } else if (binder.getContentData().getGist().getImageGist() != null &&
                        !TextUtils.isEmpty(binder.getContentData().getGist().getImageGist().get_16x9())) {
                    loadImageFromLocalSystem = false;
                    imageUrl = binder.getContentData().getGist().getImageGist().get_16x9();
                } else {
                    loadImageFromLocalSystem = false;
                    imageUrl = binder.getContentData().getGist().getPosterImageUrl();
                    if (imageUrl == null)
                        imageUrl = binder.getContentData().getGist().getVideoImageUrl();
                }
            }

            if (loadImageFromLocalSystem) {
                RequestOptions requestOptions = new RequestOptions()
                        .transform(new AutoplayBlurTransformation(getContext(), imageURI.toString()));
                Glide.with(getContext()).load(imageURI)
                        .apply(requestOptions)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                if (isAdded() && isVisible()) {
                                    pageView.setBackground(resource);
                                }
                            }
                        });
            } else {
                RequestOptions requestOptions = new RequestOptions()
                        .transform(new AutoplayBlurTransformation(getContext(), imageUrl));
                Glide.with(getContext()).load(imageUrl)
                        .apply(requestOptions)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                if (isAdded() && isVisible()) {
                                    pageView.setBackground(resource);
                                }
                            }
                        });
            }
        }
        if (isPurchaseDialog) {
            checkForTvodContent();
        }
        return pageView;
    }

    boolean isPurchaseDialog = false;

    /**
     * check If content is TVOD /PPV or SVOD or FREE
     * If content is TVOD than show purchase dialog
     */
    private void checkForTvodContent() {
        if ((binder.getContentData() != null && binder.getContentData().getPricing() != null && (binder.getContentData().getPricing().getType().equalsIgnoreCase("TVOD") ||
                binder.getContentData().getPricing().getType().equalsIgnoreCase("PPV")))) {
            appCMSPresenter.getTransactionData(binder.getContentData().getGist().getId(), updatedContentDatum -> {
                boolean isPlayable = false;
                if (updatedContentDatum != null &&
                        updatedContentDatum.size() > 0 &&
                        updatedContentDatum.get(0).size() > 0) {
                    AppCMSTransactionDataValue objTransactionData = updatedContentDatum.get(0).get(binder.getContentData().getGist().getId());
                    long expirationDate = objTransactionData.getTransactionEndDate();
                    long remainingTime = appCMSPresenter.getTimeIntervalForEvent(expirationDate, "EEE MMM dd HH:mm:ss");

                    long scheduleStartDate = 0;
                    long scheduleEndDate = 0;
                    long scheduleRemainTime = 0;

                    if (binder.getContentData().getGist() != null && binder.getContentData().getGist().getScheduleStartDate() > 0) {
                        scheduleStartDate = binder.getContentData().getGist().getScheduleStartDate();
                        scheduleRemainTime = appCMSPresenter.getTimeIntervalForEvent(scheduleStartDate, "YYYY MMM dd HH:mm:ss");

                    }

                    if (scheduleRemainTime > 0) {
                        String scheduleRemianTime = (AppCMSPresenter.geTimeFormat(scheduleRemainTime));

                        appCMSPresenter.showRentTimeDialog(retry -> {
                            if (retry) {

                                getActivity().finish();
                            }
                        }, mContext.getString(R.string.content_time_available_in_for_rent,
                                scheduleRemianTime), true);
                        return;
                    }

                    if (remainingTime < 0 && expirationDate > 0) {
                        isPlayable = false;
                    } else {
                        isPlayable = true;
                    }

                }

                if (isPlayable) {
                    callCountDownFinished();
                } else {
                    isPurchaseDialog = true;
                    appCMSPresenter.showNoPurchaseDialog(mContext.getString(R.string.rental_title), mContext.getString(R.string.rental_description));

                }
            }, null, false);
        } else {
            callCountDownFinished();
        }
    }

    private void startCountdown() {
        totalCountdownInMillis = Integer.valueOf(appCMSPresenter.getCurrentActivity().getResources()
                .getString(R.string.app_cms_autoplay_countdown_timer));
        countdownTimer = new CountDownTimer(totalCountdownInMillis, COUNTDOWN_INTERVAL_IN_MILLIS) {

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

                checkForTvodContent();
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


    private void callCountDownFinished() {
        if (isAdded() && isVisible()) {
            fragmentInteractionListener.onCountdownFinished();
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

        void cancelCountdown();
    }

    private static class AutoplayBlurTransformation extends BlurTransformation {
        private final String ID;

        public AutoplayBlurTransformation(Context context, String imageUrl) {
            super(context);
            this.ID = imageUrl;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof AutoplayBlurTransformation;
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            try {
                byte[] ID_BYTES = ID.getBytes(STRING_CHARSET_NAME);
                messageDigest.update(ID_BYTES);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "Could not update disk cache key: " + e.getMessage());
            }
        }

        @Override
        public int hashCode() {
            return ID.hashCode();
        }

        @NonNull
        @Override
        public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {
            return super.transform(resource, outWidth, outHeight);
        }
    }
}
