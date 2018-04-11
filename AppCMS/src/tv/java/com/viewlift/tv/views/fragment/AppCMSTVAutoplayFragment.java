package com.viewlift.tv.views.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Season_;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.component.AppCMSTVViewComponent;
import com.viewlift.tv.views.component.DaggerAppCMSTVViewComponent;
import com.viewlift.tv.views.customviews.AppCMSTVAutoplayCustomLoader;
import com.viewlift.tv.views.customviews.TVPageView;
import com.viewlift.tv.views.module.AppCMSTVPageViewModule;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.CustomTypefaceSpan;
import com.viewlift.views.customviews.PageView;

import java.security.MessageDigest;

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
    private TextView countdownCancelledTextView;

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
            tvCountdown.setTextColor(Color.parseColor(appCMSPresenter.getAppTextColor()));
            ImageView movieImage = (ImageView) pageView.findViewById(R.id.autoplay_play_movie_image);
            cancelCountdownButton = (Button) pageView.findViewById(R.id.autoplay_cancel_countdown_button);
            TextView finishedMovieTitle = (TextView) pageView.findViewById(R.id.autoplay_finished_movie_title);
            ImageView finishedMovieImage = (ImageView) pageView.findViewById(R.id.autoplay_finished_movie_image);
            TextView upNextMovieTitle = (TextView) pageView.findViewById(R.id.autoplay_up_next_movie_title);
            upNextTextView = (TextView) pageView.findViewById(R.id.up_next_text_view_id);
            countdownCancelledTextView = (TextView) pageView.findViewById(R.id.countdown_cancelled_text_view_id);
            appCMSTVAutoplayCustomLoader = (AppCMSTVAutoplayCustomLoader) pageView.findViewById(R.id.autoplay_rotating_loader_view_id);

            if (movieImage != null) {
                movieImage.setOnClickListener(v -> {
                    if (isAdded() && isVisible()) {
                        stopCountdown();
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
                        if (countdownCancelledTextView != null) {
                            countdownCancelledTextView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        fragmentInteractionListener.closeActivity();
                    }
                });
            }
            Typeface font = Typeface.createFromAsset(getResources().getAssets(), "fonts/OpenSans-ExtraBold.ttf");
            String mediaType = binder.getContentData().getGist().getMediaType();
            if (finishedMovieTitle != null) {
                if (mediaType != null && mediaType.equalsIgnoreCase("episodic")){
                    String seasonAndEpisodeNumber = getSeasonAndEpisodeNumber(binder.getContentData(),
                            binder.getCurrentMovieId());
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(seasonAndEpisodeNumber);
                    spannableStringBuilder.append(" ").append(binder.getCurrentMovieName());
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#7b7b7b")), 0, seasonAndEpisodeNumber.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), 0, seasonAndEpisodeNumber.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    finishedMovieTitle.setText(spannableStringBuilder);
                } else {
                    finishedMovieTitle.setText(binder.getCurrentMovieName());
                }
            }

            if (upNextMovieTitle != null) {
                if (mediaType != null && mediaType.equalsIgnoreCase("episodic")){
                    int episodeNumber = getEpisodeNumber(binder.getContentData(),
                            binder.getContentData().getGist().getId());
                    String text = upNextMovieTitle.getText().toString();
                    String episodeNumberStr = Integer.toString(episodeNumber);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(episodeNumberStr);
                    spannableStringBuilder.append(" ").append(text);
                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#7b7b7b")), 0, episodeNumberStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), 0, episodeNumberStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    upNextMovieTitle.setText(spannableStringBuilder);
                }
            }

            if (finishedMovieImage != null) {
                try {
                    Glide.with(context)
                            .load(binder.getCurrentMovieImageUrl())
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .error(ContextCompat.getDrawable(context, R.drawable.video_image_placeholder))
                                    .placeholder(ContextCompat.getDrawable(context, R.drawable.video_image_placeholder)))
                            .into(finishedMovieImage);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (pageView.getChildAt(0) != null) {
                pageView.getChildAt(0).setBackgroundResource(R.drawable.autoplay_overlay);
                pageView.getChildAt(0).getBackground().setTint(appCMSPresenter.getGeneralBackgroundColor());
            }
            String imageUrl;
            if (BaseView.isTablet(context) && BaseView.isLandscape(context)) {
                imageUrl = binder.getContentData().getGist().getVideoImageUrl();
            } else {
                imageUrl = binder.getContentData().getGist().getPosterImageUrl();
            }

            try {
                Glide.with(context)
                        .load(imageUrl)
                        .apply(new RequestOptions().transform(new BlurTransformation(context, 10){
                            @Override
                            public void updateDiskCacheKey(MessageDigest messageDigest) {
                            }

                            @Override
                            public Resource<Bitmap> transform(Context context, Resource<Bitmap> resource, int outWidth, int outHeight) {
                                return super.transform(resource,outWidth,outHeight);
                            }
                        }))
                        .into(new SimpleTarget<Drawable>(Utils.getDeviceWidth(context),
                                Utils.getDeviceHeight(context)) {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                if (isAdded() && isVisible()) {
                                    pageView.setBackground(resource);
                                }
                            }
                        });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return pageView;
    }

    private String getSeasonAndEpisodeNumber(ContentDatum mainContentData, String id) {
        String returnVal = "";
        if (mainContentData.getSeason() != null) {
            for (int seasonNumber = 0; seasonNumber < mainContentData.getSeason().size(); seasonNumber++) {
                Season_ season = mainContentData.getSeason().get(seasonNumber);
                for (int episodeNumber = 0; episodeNumber < season.getEpisodes().size(); episodeNumber++) {
                    ContentDatum contentDatum = season.getEpisodes().get(episodeNumber);
                    if (contentDatum.getGist().getId().equalsIgnoreCase(id)) {
                        returnVal = getString(R.string.season_episode_placeholders, seasonNumber + 1, episodeNumber + 1);
                        break;
                    }
                }
                if (returnVal.length() > 0) break;
            }
        }
        return returnVal;
    }


    private int getEpisodeNumber(ContentDatum mainContentData, String id) {
        int returnVal = 0;
        if (mainContentData.getSeason() != null) {
            for (int seasonNumber = 0; seasonNumber < mainContentData.getSeason().size(); seasonNumber++) {
                Season_ season = mainContentData.getSeason().get(seasonNumber);
                for (int episodeNumber = 0; episodeNumber < season.getEpisodes().size(); episodeNumber++) {
                    ContentDatum contentDatum = season.getEpisodes().get(episodeNumber);
                    if (contentDatum.getGist().getId().equalsIgnoreCase(id)) {
                        returnVal = episodeNumber + 1;
                        break;
                    }
                }
                if (returnVal > 0) break;
            }
        }

        return returnVal;
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
            if (countdownCancelledTextView != null) {
                countdownCancelledTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (binder != null && appCMSViewComponent.tvviewCreator() != null) {
            appCMSPresenter.removeLruCacheItem(context, binder.getPageID());
        }
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }
        binder = null;
        pageView = null;
        super.onDestroy();
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
