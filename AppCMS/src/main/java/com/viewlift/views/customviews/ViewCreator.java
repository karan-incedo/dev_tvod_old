package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.CreditBlock;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.Mpeg;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.models.data.appcms.downloads.UserVideoDownloadStatus;
import com.viewlift.models.data.appcms.history.UserVideoStatusResponse;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSCarouselItemAdapter;
import com.viewlift.views.adapters.AppCMSDownloadQualityAdapter;
import com.viewlift.views.adapters.AppCMSTrayItemAdapter;
import com.viewlift.views.adapters.AppCMSViewAdapter;

import net.nightwhistler.htmlspanner.HtmlSpanner;

import org.htmlcleaner.HtmlCleaner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

/**
 * Created by viewlift on 5/5/17.
 */

public class ViewCreator {
    private static final String TAG = "ViewCreator";
    ComponentViewResult componentViewResult;

    public static void setViewWithSubtitle(Context context, ContentDatum data, View view) {
        long runtime = (data.getGist().getRuntime() / 60L);
        String year = data.getGist().getYear();
        String primaryCategory =
                data.getGist().getPrimaryCategory() != null ?
                        data.getGist().getPrimaryCategory().getTitle() :
                        null;
        boolean appendFirstSep = runtime > 0
                && (!TextUtils.isEmpty(year) || !TextUtils.isEmpty(primaryCategory));
        boolean appendSecondSep = (runtime > 0 || !TextUtils.isEmpty(year))
                && !TextUtils.isEmpty(primaryCategory);
        StringBuffer infoText = new StringBuffer();
        if (runtime > 0) {
            infoText.append(runtime + context.getString(R.string.mins_abbreviation));
        }
        if (appendFirstSep) {
            infoText.append(context.getString(R.string.text_separator));
        }
        if (!TextUtils.isEmpty(year)) {
            infoText.append(year);
        }
        if (appendSecondSep) {
            infoText.append(context.getString(R.string.text_separator));
        }
        if (!TextUtils.isEmpty(primaryCategory)) {
            infoText.append(primaryCategory.toUpperCase());
        }
        ((TextView) view).setText(infoText.toString());
        view.setAlpha(0.6f);
    }

    public static long adjustColor1(long color1, long color2) {
        double ratio = (double) color1 / (double) color2;
        if (1.0 <= ratio && ratio <= 1.1) {
            color1 *= 0.8;
        }
        return color1;
    }

    public void refreshPageView(PageView pageView,
                                Context context,
                                AppCMSPageUI appCMSPageUI,
                                AppCMSPageAPI appCMSPageAPI,
                                Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                AppCMSPresenter appCMSPresenter,
                                List<String> modulesToIgnore) {
        for (ModuleList module : appCMSPageUI.getModuleList()) {
            boolean createModule = !modulesToIgnore.contains(module.getType()) && pageView != null;

            if (createModule && appCMSPresenter.isViewPlanPage(module.getId()) &&
                    (jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_CAROUSEL_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_TRAY_MODULE_KEY)) {
                createModule = false;
            }

            if (createModule) {
                ModuleView moduleView = pageView.getModuleViewWithModuleId(module.getId());
                boolean shouldHideModule = false;
                if (moduleView != null) {
                    moduleView.resetHeightAdjusters();

                    Module moduleAPI = matchModuleAPIToModuleUI(module, appCMSPageAPI, jsonValueKeyMap);

                    boolean shouldHideComponent;

                    if (moduleAPI != null) {

                        for (Component component : module.getComponents()) {
                            shouldHideComponent = false;

                            AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());

                            if (componentType == null) {
                                componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                            }

                            AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());

                            if (componentKey == null) {
                                componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                            }

                            View view = pageView.findViewFromComponentId(moduleAPI.getId()
                                    + component.getKey());

                            if (view != null) {

                                if (componentType == AppCMSUIKeyType.PAGE_TABLE_VIEW_KEY ||
                                        componentType == AppCMSUIKeyType.PAGE_COLLECTIONGRID_KEY ||
                                        componentType == AppCMSUIKeyType.PAGE_CAROUSEL_VIEW_KEY) {

                                    AppCMSUIKeyType moduleType = jsonValueKeyMap.get(module.getView());
                                    if (moduleType != AppCMSUIKeyType.PAGE_SUBSCRIPTION_IMAGEROW_KEY) {
                                        pageView.updateDataList(moduleAPI.getContentData(),
                                                moduleAPI.getId() + component.getKey());
                                        if ((moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty()) ||
                                                componentType == AppCMSUIKeyType.PAGE_TABLE_VIEW_KEY) {
                                            view.setVisibility(View.VISIBLE);
                                            moduleView.setVisibility(View.VISIBLE);
                                        } else {
                                            if (view != null) {
                                                view.setVisibility(View.GONE);
                                            }
                                            moduleView.setVisibility(View.GONE);
                                            shouldHideModule = true;
                                        }
                                    }
                                } else if (componentType == AppCMSUIKeyType.PAGE_PROGRESS_VIEW_KEY) {
                                    if (appCMSPresenter.isUserLoggedIn(context)) {
                                        ((ProgressBar) view).setMax(100);
                                        ((ProgressBar) view).setProgress(0);
                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0) != null &&
                                                moduleAPI.getContentData().get(0).getGist() != null) {
                                            if (moduleAPI.getContentData()
                                                    .get(0).getGist().getWatchedPercentage() > 0) {
                                                view.setVisibility(View.VISIBLE);
                                                ((ProgressBar) view)
                                                        .setProgress(moduleAPI.getContentData()
                                                                .get(0).getGist().getWatchedPercentage());
                                            } else {
                                                long watchedTime =
                                                        moduleAPI.getContentData().get(0).getGist().getWatchedTime();
                                                long runTime =
                                                        moduleAPI.getContentData().get(0).getGist().getRuntime();
                                                if (watchedTime > 0 && runTime > 0) {
                                                    long percentageWatched = watchedTime / runTime;
                                                    ((ProgressBar) view)
                                                            .setProgress((int) percentageWatched);
                                                    view.setVisibility(View.VISIBLE);
                                                } else {
                                                    view.setVisibility(View.INVISIBLE);
                                                    ((ProgressBar) view).setProgress(0);
                                                }
                                            }
                                        } else {
                                            view.setVisibility(View.INVISIBLE);
                                            ((ProgressBar) view).setProgress(0);
                                        }
                                    }
                                } else if (componentType == AppCMSUIKeyType.PAGE_BUTTON_KEY) {
                                    if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_WATCH_TRAILER_KEY) {
                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0).getContentDetails() != null &&
                                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers() != null &&
                                                !moduleAPI.getContentData().get(0).getContentDetails().getTrailers().isEmpty() &&
                                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0) != null &&
                                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink() != null &&
                                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getId() != null &&
                                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getVideoAssets() != null) {
                                            view.setVisibility(View.VISIBLE);
                                            view.setOnClickListener(v -> {
                                                String[] extraData = new String[3];
                                                extraData[0] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink();
                                                extraData[1] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getVideoAssets().getHls();
                                                extraData[2] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getId();
                                                if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                                        component.getAction(),
                                                        moduleAPI.getContentData().get(0).getGist().getTitle(),
                                                        extraData,
                                                        moduleAPI.getContentData().get(0),
                                                        false,
                                                        -1,
                                                        null)) {
                                                    Log.e(TAG, "Could not launch action: " +
                                                            " permalink: " +
                                                            moduleAPI.getContentData().get(0).getGist().getPermalink() +
                                                            " action: " +
                                                            component.getAction() +
                                                            " hls URL: " +
                                                            moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets().getHls());
                                                }
                                            });
                                        } else {
                                            shouldHideComponent = true;
                                            view.setVisibility(View.GONE);
                                        }
                                    } else if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_PLAY_BUTTON_KEY) {
                                        view.setOnClickListener(v -> {
                                            if (moduleAPI.getContentData() != null &&
                                                    !moduleAPI.getContentData().isEmpty() &&
                                                    moduleAPI.getContentData().get(0) != null &&
                                                    moduleAPI.getContentData().get(0).getStreamingInfo() != null &&
                                                    moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets() != null) {
                                                VideoAssets videoAssets = moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets();
                                                String videoUrl = videoAssets.getHls();
                                                if (TextUtils.isEmpty(videoUrl)) {
                                                    for (int i = 0; i < videoAssets.getMpeg().size() && TextUtils.isEmpty(videoUrl); i++) {
                                                        videoUrl = videoAssets.getMpeg().get(i).getUrl();
                                                    }
                                                }
                                                if (moduleAPI.getContentData().get(0).getGist() != null &&
                                                        moduleAPI.getContentData().get(0).getGist().getId() != null &&
                                                        moduleAPI.getContentData().get(0).getGist().getPermalink() != null) {
                                                    String[] extraData = new String[3];
                                                    extraData[0] = moduleAPI.getContentData().get(0).getGist().getPermalink();
                                                    extraData[1] = videoUrl;
                                                    extraData[2] = moduleAPI.getContentData().get(0).getGist().getId();
                                                    if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                                            component.getAction(),
                                                            moduleAPI.getContentData().get(0).getGist().getTitle(),
                                                            extraData,
                                                            moduleAPI.getContentData().get(0),
                                                            false,
                                                            -1,
                                                            moduleAPI.getContentData().get(0).getContentDetails().getRelatedVideoIds())) {
                                                        Log.e(TAG, "Could not launch action: " +
                                                                " permalink: " +
                                                                moduleAPI.getContentData().get(0).getGist().getPermalink() +
                                                                " action: " +
                                                                component.getAction() +
                                                                " video URL: " +
                                                                videoUrl);
                                                    }
                                                }
                                            }
                                        });
                                    } else if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_SHARE_KEY) {
                                        view.setOnClickListener(v -> {
                                            AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
                                            if (appCMSMain != null &&
                                                    moduleAPI.getContentData() != null &&
                                                    !moduleAPI.getContentData().isEmpty() &&
                                                    moduleAPI.getContentData().get(0) != null &&
                                                    moduleAPI.getContentData().get(0).getGist() != null &&
                                                    moduleAPI.getContentData().get(0).getGist().getTitle() != null &&
                                                    moduleAPI.getContentData().get(0).getGist().getPermalink() != null) {
                                                StringBuilder filmUrl = new StringBuilder();
                                                filmUrl.append(appCMSMain.getDomainName());
                                                filmUrl.append(moduleAPI.getContentData().get(0).getGist().getPermalink());
                                                String[] extraData = new String[1];
                                                extraData[0] = filmUrl.toString();
                                                if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                                        component.getAction(),
                                                        moduleAPI.getContentData().get(0).getGist().getTitle(),
                                                        extraData,
                                                        moduleAPI.getContentData().get(0),
                                                        false,
                                                        0,
                                                        null)) {
                                                    Log.e(TAG, "Could not launch action: " +
                                                            " permalink: " +
                                                            moduleAPI.getContentData().get(0).getGist().getPermalink() +
                                                            " action: " +
                                                            component.getAction() +
                                                            " film URL: " +
                                                            filmUrl.toString());
                                                }
                                            }
                                        });
                                    } else if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_DOWNLOAD_BUTTON_KEY
                                            && view != null) {
                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0).getGist() != null &&
                                                moduleAPI.getContentData().get(0).getGist().getId() != null) {
                                            String userId = appCMSPresenter.getLoggedInUser(context);
                                            appCMSPresenter.getUserVideoDownloadStatus(
                                                    moduleAPI.getContentData().get(0).getGist().getId(), new UpdateDownloadImageIconAction((ImageButton) view, appCMSPresenter,
                                                            moduleAPI.getContentData().get(0), userId), userId);

                                        }
                                        view.setVisibility(View.VISIBLE);
                                    } else if (componentKey == AppCMSUIKeyType.PAGE_ADD_TO_WATCHLIST_KEY
                                            && view != null) {
                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0).getGist() != null &&
                                                moduleAPI.getContentData().get(0).getGist().getId() != null) {
                                            appCMSPresenter.getUserVideoStatus(
                                                    moduleAPI.getContentData().get(0).getGist().getId(),
                                                    new UpdateImageIconAction((ImageButton) view, appCMSPresenter, moduleAPI.getContentData()
                                                            .get(0).getGist().getId()));
                                        }
                                        view.setVisibility(View.VISIBLE);
                                    }
                                } else if (componentType == AppCMSUIKeyType.PAGE_VIDEO_STARRATING_KEY ||
                                        componentType == AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_STAR_RATING_KEY) {
                                    float starRating = -1.0f;
                                    if (moduleAPI.getContentData() != null &&
                                            !moduleAPI.getContentData().isEmpty() &&
                                            moduleAPI.getContentData().get(0).getGist() != null) {
                                        if (moduleAPI.getContentData().get(0).getGist().getAverageStarRating() != 0f) {
                                            starRating = moduleAPI.getContentData().get(0).getGist().getAverageStarRating();
                                        }
                                        if (starRating >= 0) {
                                            ((StarRating) view).updateRating(starRating);
                                        }
                                    }
                                } else if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY) {
                                    if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_TITLE_KEY) {
                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0).getGist() != null &&
                                                moduleAPI.getContentData().get(0).getGist().getTitle() != null) {
                                            if (!TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getTitle())) {
                                                ((TextView) view).setText(moduleAPI.getContentData().get(0).getGist().getTitle());
                                            }
                                            ViewTreeObserver titleTextVto = view.getViewTreeObserver();
                                            ViewCreatorTitleLayoutListener viewCreatorTitleLayoutListener =
                                                    new ViewCreatorTitleLayoutListener((TextView) view);
                                            titleTextVto.addOnGlobalLayoutListener(viewCreatorTitleLayoutListener);
                                            ((TextView) view).setSingleLine(true);
                                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
                                        }
                                    } else if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_SUBTITLE_KEY) {
                                        if (moduleAPI.getContentData() != null) {
                                            setViewWithSubtitle(context,
                                                    moduleAPI.getContentData().get(0),
                                                    view);
                                        }
                                    } else if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_AGE_LABEL_KEY) {
                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0) != null &&
                                                moduleAPI.getContentData().get(0).getParentalRating() != null &&
                                                !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getParentalRating())) {
                                            String parentalRating = moduleAPI.getContentData().get(0).getParentalRating();
                                            String convertedRating = context.getString(R.string.age_rating_converted_default);
                                            if (parentalRating.contains(context.getString(R.string.age_rating_y7))) {
                                                convertedRating = context.getString(R.string.age_rating_converted_y7);
                                            } else if (parentalRating.contains(context.getString(R.string.age_rating_y))) {
                                                convertedRating = context.getString(R.string.age_rating_converted_y);
                                            } else if (parentalRating.contains(context.getString(R.string.age_rating_pg))) {
                                                convertedRating = context.getString(R.string.age_rating_converted_pg);
                                            } else if (parentalRating.contains(context.getString(R.string.age_rating_g))) {
                                                convertedRating = context.getString(R.string.age_rating_converted_g);
                                            } else if (parentalRating.contains(context.getString(R.string.age_rating_fourteen))) {
                                                convertedRating = context.getString(R.string.age_rating_converted_fourteen);
                                            } else if (parentalRating.contains(context.getString(R.string.age_rating_converted_default))) {
                                                convertedRating = context.getString(R.string.age_rating_converted_default);
                                            } else if (parentalRating.contains(context.getString(R.string.age_raging_r))) {
                                                convertedRating = context.getString(R.string.age_rating_converted_eighteen);
                                            }
                                            ((TextView) view).setText(convertedRating);
                                            ((TextView) view).setGravity(Gravity.CENTER);
                                            applyBorderToComponent(context,
                                                    view,
                                                    component,
                                                    -1);
                                        }
                                    } else if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_DESCRIPTION_KEY) {
                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0) != null &&
                                                moduleAPI.getContentData().get(0).getGist() != null &&
                                                moduleAPI.getContentData().get(0).getGist().getDescription() != null) {
                                            String videoDescription = moduleAPI.getContentData().get(0).getGist().getDescription();
                                            if (videoDescription != null) {
                                                videoDescription = videoDescription.trim();
                                            }
                                            if (!TextUtils.isEmpty(videoDescription)) {
                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                                    ((TextView) view).setText(Html.fromHtml(videoDescription));
                                                } else {
                                                    ((TextView) view).setText(Html.fromHtml(videoDescription, Html.FROM_HTML_MODE_COMPACT));
                                                }
                                                view.setVisibility(View.VISIBLE);
                                            } else if (!BaseView.isLandscape(context)) {
                                                shouldHideComponent = true;
                                                view.setVisibility(View.GONE);
                                            }
                                            ViewTreeObserver textVto = view.getViewTreeObserver();
                                            ViewCreatorMultiLineLayoutListener viewCreatorLayoutListener =
                                                    new ViewCreatorMultiLineLayoutListener(((TextView) view),
                                                            moduleAPI.getContentData().get(0).getGist().getTitle(),
                                                            videoDescription,
                                                            appCMSPresenter,
                                                            false);
                                            textVto.addOnGlobalLayoutListener(viewCreatorLayoutListener);
                                        }
                                    }
                                } else if (componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY) {
                                    if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY) {
                                        if (moduleAPI.getContentData() != null) {
                                            int viewWidth = view.getWidth();
                                            int viewHeight = view.getHeight();

                                            if (viewHeight > 0 && viewWidth > 0 && viewHeight > viewWidth) {
                                                String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                                        moduleAPI.getContentData().get(0).getGist().getPosterImageUrl(),
                                                        viewWidth,
                                                        viewHeight);
                                                Glide.with(context)
                                                        .load(imageUrl)
                                                        .override(viewWidth, viewHeight)
                                                        .centerCrop()
                                                        .into((ImageView) view);
                                            } else if (viewWidth > 0 && viewHeight > 0) {
                                                String videoImageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                                        moduleAPI.getContentData().get(0).getGist().getVideoImageUrl(),
                                                        viewWidth,
                                                        viewHeight);
                                                Glide.with(context)
                                                        .load(videoImageUrl)
                                                        .override(viewWidth, viewHeight)
                                                        .centerCrop()
                                                        .into((ImageView) view);
                                            } else if (viewHeight > 0) {
                                                Glide.with(context)
                                                        .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                                                        .override(Target.SIZE_ORIGINAL, viewHeight)
                                                        .centerCrop()
                                                        .into((ImageView) view);
                                            } else {
                                                Glide.with(context)
                                                        .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                                                        .into((ImageView) view);
                                            }
                                            view.forceLayout();
                                        }
                                    }
                                } else if (componentKey == AppCMSUIKeyType.PAGE_SETTINGS_EDIT_PROFILE_KEY) {
                                    if (!TextUtils.isEmpty(appCMSPresenter.getFacebookAccessToken(context))) {
                                        view.setVisibility(View.GONE);
                                    }

                                    if (!TextUtils.isEmpty(appCMSPresenter.getGoogleAccessToken(context))) {
                                        view.setVisibility(View.GONE);
                                    }
                                } else if (componentKey == AppCMSUIKeyType.PAGE_SETTINGS_CHANGE_PASSWORD_KEY) {
                                    if (!TextUtils.isEmpty(appCMSPresenter.getFacebookAccessToken(context))) {
                                        view.setVisibility(View.GONE);
                                    }

                                    if (!TextUtils.isEmpty(appCMSPresenter.getGoogleAccessToken(context))) {
                                        view.setVisibility(View.GONE);
                                    }
                                } else {
                                    if (componentType == AppCMSUIKeyType.PAGE_CASTVIEW_VIEW_KEY) {
                                        String directorTitle = null;
                                        StringBuffer directorListSb = new StringBuffer();
                                        String starringTitle = null;
                                        StringBuffer starringListSb = new StringBuffer();

                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0) != null &&
                                                moduleAPI.getContentData().get(0).getCreditBlocks() != null) {
                                            for (CreditBlock creditBlock : moduleAPI.getContentData().get(0).getCreditBlocks()) {
                                                AppCMSUIKeyType creditBlockType = jsonValueKeyMap.get(creditBlock.getTitle());
                                                if (creditBlockType != null &&
                                                        (creditBlockType == AppCMSUIKeyType.PAGE_VIDEO_CREDITS_DIRECTEDBY_KEY ||
                                                                creditBlockType == AppCMSUIKeyType.PAGE_VIDEO_CREDITS_DIRECTOR_KEY ||
                                                                creditBlockType == AppCMSUIKeyType.PAGE_VIDEO_CREDITS_DIRECTORS_KEY)) {
                                                    if (!TextUtils.isEmpty(creditBlock.getTitle())) {
                                                        directorTitle = creditBlock.getTitle().toUpperCase();
                                                    }
                                                    if (creditBlock != null && creditBlock.getCredits() != null) {
                                                        for (int j = 0; j < creditBlock.getCredits().size(); j++) {
                                                            directorListSb.append(creditBlock.getCredits().get(j).getTitle());
                                                            if (j < creditBlock.getCredits().size() - 1) {
                                                                directorListSb.append(", ");
                                                            }
                                                        }
                                                    }
                                                } else if (creditBlockType != null &&
                                                        creditBlockType == AppCMSUIKeyType.PAGE_VIDEO_CREDITS_STARRING_KEY) {
                                                    if (!TextUtils.isEmpty(creditBlock.getTitle())) {
                                                        starringTitle = creditBlock.getTitle().toUpperCase();
                                                    }
                                                    if (creditBlock != null && creditBlock.getCredits() != null) {
                                                        for (int j = 0; j < creditBlock.getCredits().size(); j++) {
                                                            starringListSb.append(creditBlock.getCredits().get(j).getTitle());
                                                            if (j < creditBlock.getCredits().size() - 1) {
                                                                starringListSb.append(", ");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (directorListSb.length() == 0 && starringListSb.length() == 0) {
                                            if (!BaseView.isLandscape(context)) {
                                                shouldHideComponent = true;
                                                view.setVisibility(View.GONE);
                                            }
                                        } else {
                                            ((CreditBlocksView) view).updateText(directorTitle,
                                                    directorListSb.toString(),
                                                    starringTitle,
                                                    starringListSb.toString());
                                            view.setVisibility(View.VISIBLE);
                                            view.forceLayout();
                                        }
                                    } else if (componentType == AppCMSUIKeyType.PAGE_SETTINGS_KEY) {
                                        appCMSPresenter.checkForExistingSubscription(false);
                                        for (Component settingsComponent : component.getComponents()) {
                                            shouldHideComponent = false;

                                            AppCMSUIKeyType settingsComponentKey = jsonValueKeyMap.get(settingsComponent.getKey());

                                            if (settingsComponentKey == null) {
                                                settingsComponentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                                            }

                                            View settingsView = pageView.findViewFromComponentId(module.getId()
                                                    + settingsComponent.getKey());

                                            String paymentProcessor = appCMSPresenter.getActiveSubscriptionProcessor(context);

                                            if (settingsView != null) {
                                                if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_NAME_VALUE_KEY) {
                                                    ((TextView) settingsView).setText(appCMSPresenter.getLoggedInUserName(context));
                                                } else if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_EMAIL_VALUE_KEY) {
                                                    ((TextView) settingsView).setText(appCMSPresenter.getLoggedInUserEmail(context));
                                                } else if (TextUtils.isEmpty(appCMSPresenter.getLoggedInUserEmail(context))) {
                                                    settingsView.setVisibility(View.GONE);
                                                } else {
                                                    if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_PLAN_PROCESSOR_TITLE_KEY) {
                                                        if (appCMSPresenter.isUserSubscribed(context) &&
                                                                !TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionPlanName(context))) {
                                                            settingsView.setVisibility(View.VISIBLE);
                                                        } else {
                                                            settingsView.setVisibility(View.GONE);
                                                            shouldHideComponent = true;
                                                        }
                                                    } else if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_PLAN_VALUE_KEY) {
                                                        if (appCMSPresenter.isUserSubscribed(context) &&
                                                                !TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionPlanName(context))) {
                                                            ((TextView) settingsView).setText(appCMSPresenter.getActiveSubscriptionPlanName(context));
                                                        } else {
                                                            ((TextView) settingsView).setText(context.getString(R.string.subscription_unsubscribed_plan_value));
                                                        }
                                                    } else if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_PLAN_PROCESSOR_VALUE_KEY) {
                                                        if (paymentProcessor != null) {
                                                            if (paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_ios_payment_processor)) ||
                                                                    paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_ios_payment_processor_friendly))) {
                                                                ((TextView) settingsView).setText(context.getString(R.string.subscription_ios_payment_processor_friendly));
                                                            } else if (paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_web_payment_processor_friendly))) {
                                                                ((TextView) settingsView).setText(context.getString(R.string.subscription_web_payment_processor_friendly));
                                                            } else if (paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_android_payment_processor)) ||
                                                                    paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_android_payment_processor_friendly))) {
                                                                ((TextView) settingsView).setText(context.getString(R.string.subscription_android_payment_processor_friendly));
                                                            } else if (paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_ccavenue_payment_processor))) {
                                                                ((TextView) settingsView).setText(context.getString(R.string.subscription_ccavenue_payment_processor_friendly));
                                                            } else {
                                                                ((TextView) settingsView).setText(context.getString(R.string.subscription_unknown_payment_processor_friendly));
                                                            }
                                                        } else {
                                                            ((TextView) settingsView).setText("");
                                                        }
                                                    } else if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_DOWNLOAD_QUALITY_PROFILE_KEY) {
                                                        ((TextView) settingsView).setText(appCMSPresenter.getUserDownloadQualityPref(context));
                                                    } else if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_UPGRADE_PLAN_PROFILE_KEY) {
                                                        if (!appCMSPresenter.isUserSubscribed(context)) {
                                                            ((TextView) settingsView).setText(context.getString(R.string.app_cms_page_upgrade_subscribe_button_text));
                                                        } else if (!TextUtils.isEmpty(component.getText())) {
                                                            ((TextView) settingsView).setText(component.getText());
                                                        }
                                                    } else if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_CANCEL_PLAN_PROFILE_KEY) {
                                                        if (appCMSPresenter.isUserSubscribed(context)) {
                                                            settingsView.setVisibility(View.VISIBLE);
                                                        } else {
                                                            settingsView.setVisibility(View.GONE);
                                                        }
                                                    }
                                                }
                                                settingsView.requestLayout();
                                            }
                                        }
                                    } else if (componentType == AppCMSUIKeyType.PAGE_TOGGLE_BUTTON_KEY) {
                                        switch (componentType) {
                                            case PAGE_AUTOPLAY_TOGGLE_BUTTON_KEY:
                                                ((Switch) view).setChecked(appCMSPresenter.getAutoplayEnabledUserPref(context));
                                                break;

                                            case PAGE_CLOSED_CAPTIONS_TOGGLE_BUTTON_KEY:
                                                ((Switch) view).setChecked(appCMSPresenter.getClosedCaptionPreference(context));
                                                break;

                                            default:
                                                break;
                                        }
                                    }
                                }

                                if (shouldHideComponent) {
                                    ModuleView.HeightLayoutAdjuster heightLayoutAdjuster =
                                            new ModuleView.HeightLayoutAdjuster();
                                    if (BaseView.isTablet(context)) {
                                        if (BaseView.isLandscape(context)) {
                                            heightLayoutAdjuster.heightAdjustment =
                                                    (int) component.getLayout().getTabletLandscape().getHeight();
                                            heightLayoutAdjuster.topMargin =
                                                    (int) component.getLayout().getTabletLandscape().getTopMargin();
                                            heightLayoutAdjuster.yAxis =
                                                    (int) component.getLayout().getTabletLandscape().getYAxis();
                                            heightLayoutAdjuster.component = component;
                                        } else {
                                            heightLayoutAdjuster.heightAdjustment =
                                                    (int) component.getLayout().getTabletPortrait().getHeight();
                                            heightLayoutAdjuster.topMargin =
                                                    (int) component.getLayout().getTabletPortrait().getTopMargin();
                                            heightLayoutAdjuster.yAxis =
                                                    (int) component.getLayout().getTabletPortrait().getYAxis();
                                            heightLayoutAdjuster.component = component;
                                        }
                                    } else {
                                        heightLayoutAdjuster.heightAdjustment =
                                                (int) component.getLayout().getMobile().getHeight();
                                        heightLayoutAdjuster.topMargin =
                                                (int) component.getLayout().getMobile().getTopMargin();
                                        heightLayoutAdjuster.yAxis =
                                                (int) component.getLayout().getMobile().getYAxis();
                                        heightLayoutAdjuster.component = component;
                                    }
                                    moduleView.addHeightAdjuster(heightLayoutAdjuster);
                                }
                            }
                        }
                    }

                    ViewGroup.LayoutParams moduleLayoutParams = moduleView.getLayoutParams();
                    moduleView.verifyHeightAdjustments();

                    for (int j = 0; j < moduleView.getHeightAdjusterListSize(); j++) {
                        ModuleView.HeightLayoutAdjuster heightLayoutAdjuster = moduleView.getHeightLayoutAdjuster(j);

                        if (heightLayoutAdjuster.reset) {
                            moduleLayoutParams.height += BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                        } else {
                            moduleLayoutParams.height -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                        }
                        List<ModuleView.ChildComponentAndView> childComponentAndViewList =
                                moduleView.getChildComponentAndViewList();

                        for (int k = 0; k < childComponentAndViewList.size(); k++) {
                            ModuleView.ChildComponentAndView childComponentAndView = childComponentAndViewList.get(k);

                            ViewGroup.MarginLayoutParams childLayoutParams =
                                    (ViewGroup.MarginLayoutParams) childComponentAndView.childView.getLayoutParams();
                            if (BaseView.isTablet(context)) {
                                if (BaseView.isLandscape(context)) {
                                    if (childComponentAndView.component.getLayout().getTabletLandscape().getYAxis() > 0 &&
                                            heightLayoutAdjuster.yAxis <
                                                    childComponentAndView.component.getLayout().getTabletLandscape().getYAxis()) {
                                        if (heightLayoutAdjuster.reset) {
                                            childLayoutParams.topMargin += BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                        } else {
                                            childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                        }
                                    }
                                    if (childComponentAndView.component.getLayout().getTabletLandscape().getTopMargin() > 0 &&
                                            heightLayoutAdjuster.topMargin <
                                                    childComponentAndView.component.getLayout().getTabletLandscape().getTopMargin()) {
                                        if (heightLayoutAdjuster.reset) {
                                            childLayoutParams.topMargin += BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                        } else {
                                            childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                        }
                                    }
                                } else {
                                    if (childComponentAndView.component.getLayout().getTabletPortrait().getYAxis() > 0 &&
                                            heightLayoutAdjuster.yAxis <
                                                    childComponentAndView.component.getLayout().getTabletPortrait().getYAxis()) {
                                        if (heightLayoutAdjuster.reset) {
                                            childLayoutParams.topMargin += BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                        } else {
                                            childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                        }
                                    }
                                    if (childComponentAndView.component.getLayout().getTabletPortrait().getTopMargin() > 0 &&
                                            heightLayoutAdjuster.topMargin <
                                                    childComponentAndView.component.getLayout().getTabletPortrait().getTopMargin()) {
                                        if (heightLayoutAdjuster.reset) {
                                            childLayoutParams.topMargin += BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                        } else {
                                            childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                        }
                                    }
                                }
                            } else {
                                if (childComponentAndView.component.getLayout().getMobile().getYAxis() > 0 &&
                                        heightLayoutAdjuster.yAxis <
                                                childComponentAndView.component.getLayout().getMobile().getYAxis()) {
                                    if (heightLayoutAdjuster.reset) {
                                        childLayoutParams.topMargin += BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                    } else {
                                        childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                    }
                                }
                                if (childComponentAndView.component.getLayout().getMobile().getTopMargin() > 0 &&
                                        heightLayoutAdjuster.topMargin <
                                                childComponentAndView.component.getLayout().getMobile().getTopMargin()) {
                                    if (heightLayoutAdjuster.reset) {
                                        childLayoutParams.topMargin += BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                    } else {
                                        childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                    }
                                }
                            }
                            childComponentAndView.childView.setLayoutParams(childLayoutParams);
                        }
                    }
                    moduleView.removeResetHeightAdjusters();

                    moduleView.setLayoutParams(moduleLayoutParams);

                    if (!shouldHideModule) {
                        moduleView.setVisibility(View.VISIBLE);
                    }

                    moduleView.requestLayout();
                }
            }
        }
    }

    public PageView generatePage(Context context,
                                 AppCMSPageUI appCMSPageUI,
                                 AppCMSPageAPI appCMSPageAPI,
                                 String screenName,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter,
                                 List<String> modulesToIgnore) {
        if (appCMSPageUI == null || appCMSPageAPI == null) {
            return null;
        }

        PageView pageView = appCMSPresenter.getPageViewLruCache().get(appCMSPageAPI.getId()
                + BaseView.isLandscape(context));
        if (appCMSPresenter.isPageAVideoPage(screenName)) {
            pageView = appCMSPresenter.getPageViewLruCache().get(screenName + BaseView.isLandscape(context));
        }

        boolean newView = false;
        if (pageView == null || pageView.getContext() != context) {
            pageView = new PageView(context, appCMSPageUI);
            pageView.setUserLoggedIn(appCMSPresenter.isUserLoggedIn(context));
            if (appCMSPresenter.isPageAVideoPage(screenName)) {
                appCMSPresenter.getPageViewLruCache().put(screenName + BaseView.isLandscape(context), pageView);
            } else {
                appCMSPresenter.getPageViewLruCache().put(appCMSPageAPI.getId()
                        + BaseView.isLandscape(context), pageView);
            }
            newView = true;
        }
        if (newView ||
                (!appCMSPresenter.isPagePrimary(appCMSPageAPI.getId()) && !appCMSPresenter.isPageAVideoPage(screenName)) ||
                appCMSPresenter.isUserLoggedIn(context) != pageView.isUserLoggedIn()) {
            pageView.setUserLoggedIn(appCMSPresenter.isUserLoggedIn(context));
            pageView.getChildrenContainer().removeAllViews();
            Runtime.getRuntime().gc();
            componentViewResult = new ComponentViewResult();
            createPageView(context,
                    appCMSPageUI,
                    appCMSPageAPI,
                    pageView,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    modulesToIgnore);
        } else {
            refreshPageView(pageView,
                    context,
                    appCMSPageUI,
                    appCMSPageAPI,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    modulesToIgnore);
        }
        pageView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        return pageView;
    }

    public ComponentViewResult getComponentViewResult() {
        return componentViewResult;
    }

    protected void createPageView(Context context,
                                  AppCMSPageUI appCMSPageUI,
                                  AppCMSPageAPI appCMSPageAPI,
                                  PageView pageView,
                                  Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                  AppCMSPresenter appCMSPresenter,
                                  List<String> modulesToIgnore) {
        appCMSPresenter.clearOnInternalEvents();
        pageView.clearExistingViewLists();
        List<ModuleList> modulesList = appCMSPageUI.getModuleList();
        ViewGroup childrenContainer = pageView.getChildrenContainer();
        for (ModuleList module : modulesList) {
            boolean createModule = !modulesToIgnore.contains(module.getType());

            if (createModule && appCMSPresenter.isViewPlanPage(appCMSPageAPI.getId()) &&
                    (jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_CAROUSEL_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_TRAY_MODULE_KEY)) {
                createModule = false;
            }

            if (createModule) {
                if (appCMSPresenter.isViewPlanPage(appCMSPageAPI.getId()) &&
                        jsonValueKeyMap.get(module.getType()) != AppCMSUIKeyType.PAGE_CAROUSEL_MODULE_KEY &&
                        jsonValueKeyMap.get(module.getType()) != AppCMSUIKeyType.PAGE_TRAY_MODULE_KEY) {

                }
                Module moduleAPI = matchModuleAPIToModuleUI(module, appCMSPageAPI, jsonValueKeyMap);
                View childView = createModuleView(context, module, moduleAPI, pageView,
                        jsonValueKeyMap,
                        appCMSPresenter);
                if (childView != null) {
                    childrenContainer.addView(childView);
                }
            }
        }

        List<OnInternalEvent> presenterOnInternalEvents = appCMSPresenter.getOnInternalEvents();
        if (presenterOnInternalEvents != null) {
            for (OnInternalEvent onInternalEvent : presenterOnInternalEvents) {
                for (OnInternalEvent receiverInternalEvent : presenterOnInternalEvents) {
                    if (receiverInternalEvent != onInternalEvent) {
                        onInternalEvent.addReceiver(receiverInternalEvent);
                    }
                }
            }
        }
    }

    public <T extends ModuleWithComponents> View createModuleView(final Context context,
                                                                  final T module,
                                                                  final Module moduleAPI,
                                                                  PageView pageView,
                                                                  Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                                                  AppCMSPresenter appCMSPresenter) {
        ModuleView moduleView = null;
        if (jsonValueKeyMap.get(module.getView()) == AppCMSUIKeyType.PAGE_AUTHENTICATION_MODULE_KEY) {
            moduleView = new LoginModule(context,
                    module,
                    moduleAPI,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    this);
            pageView.addModuleViewWithModuleId(module.getId(), moduleView);
        } else {
            moduleView = new ModuleView<>(context, module, true);
            ViewGroup childrenContainer = moduleView.getChildrenContainer();
            boolean hideModule = false;
            boolean modulesHasHiddenComponent = false;

            AdjustOtherState adjustOthers = AdjustOtherState.IGNORE;
            pageView.addModuleViewWithModuleId(module.getId(), moduleView);
            if (module.getComponents() != null) {
                for (int i = 0; i < module.getComponents().size(); i++) {
                    Component component = module.getComponents().get(i);

                    createComponentView(context,
                            component,
                            module.getLayout(),
                            moduleAPI,
                            pageView,
                            module.getSettings(),
                            jsonValueKeyMap,
                            appCMSPresenter,
                            false,
                            module.getView());

                    if (adjustOthers == AdjustOtherState.INITIATED) {
                        adjustOthers = AdjustOtherState.ADJUST_OTHERS;
                    }

                    if (!appCMSPresenter.isAppSVOD() && component.isSvod()) {
                        componentViewResult.shouldHideComponent = true;
                        componentViewResult.componentView.setVisibility(View.GONE);
                        adjustOthers = AdjustOtherState.INITIATED;
                    }

                    if (componentViewResult.shouldHideModule) {
                        hideModule = true;
                    }

                    if (componentViewResult.onInternalEvent != null) {
                        appCMSPresenter.addInternalEvent(componentViewResult.onInternalEvent);
                    }

                    if (componentViewResult.shouldHideComponent) {
                        ModuleView.HeightLayoutAdjuster heightLayoutAdjuster =
                                new ModuleView.HeightLayoutAdjuster();
                        modulesHasHiddenComponent = true;
                        if (BaseView.isTablet(context)) {
                            if (BaseView.isLandscape(context)) {
                                heightLayoutAdjuster.heightAdjustment =
                                        (int) component.getLayout().getTabletLandscape().getHeight();
                                heightLayoutAdjuster.topMargin =
                                        (int) component.getLayout().getTabletLandscape().getTopMargin();
                                heightLayoutAdjuster.yAxis =
                                        (int) component.getLayout().getTabletLandscape().getYAxis();
                                heightLayoutAdjuster.component = component;
                            } else {
                                heightLayoutAdjuster.heightAdjustment =
                                        (int) component.getLayout().getTabletPortrait().getHeight();
                                heightLayoutAdjuster.topMargin =
                                        (int) component.getLayout().getTabletPortrait().getTopMargin();
                                heightLayoutAdjuster.yAxis =
                                        (int) component.getLayout().getTabletPortrait().getYAxis();
                                heightLayoutAdjuster.component = component;
                            }
                        } else {
                            heightLayoutAdjuster.heightAdjustment =
                                    (int) component.getLayout().getMobile().getHeight();
                            heightLayoutAdjuster.topMargin =
                                    (int) component.getLayout().getMobile().getTopMargin();
                            heightLayoutAdjuster.yAxis =
                                    (int) component.getLayout().getMobile().getYAxis();
                            heightLayoutAdjuster.component = component;
                        }
                        moduleView.addHeightAdjuster(heightLayoutAdjuster);
                    }

                    View componentView = componentViewResult.componentView;

                    if (componentView != null) {
                        if (componentViewResult.addToPageView) {
                            pageView.addView(componentView);
                        } else {
                            childrenContainer.addView(componentView);
                            moduleView.setComponentHasView(i, true);
                            moduleView.setViewMarginsFromComponent(component,
                                    componentView,
                                    moduleView.getLayout(),
                                    childrenContainer,
                                    false,
                                    jsonValueKeyMap,
                                    componentViewResult.useMarginsAsPercentagesOverride,
                                    componentViewResult.useWidthOfScreen,
                                    module.getView());
                            if ((adjustOthers == AdjustOtherState.IGNORE && componentViewResult.shouldHideComponent) ||
                                    adjustOthers == AdjustOtherState.ADJUST_OTHERS) {
                                moduleView.addChildComponentAndView(component, componentView);
                            } else {
                                moduleView.setComponentHasView(i, false);
                            }
                        }
                    }
                }
            }

            if (hideModule) {
                moduleView.setVisibility(View.GONE);
            }

            if (modulesHasHiddenComponent) {
                moduleView.verifyHeightAdjustments();
                ViewGroup.LayoutParams moduleLayoutParams = moduleView.getLayoutParams();
                for (int i = 0; i < moduleView.getHeightAdjusterListSize(); i++) {
                    ModuleView.HeightLayoutAdjuster heightLayoutAdjuster = moduleView.getHeightLayoutAdjuster(i);

                    moduleLayoutParams.height -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                    List<ModuleView.ChildComponentAndView> childComponentAndViewList =
                            moduleView.getChildComponentAndViewList();

                    for (int j = 0; j < childComponentAndViewList.size(); j++) {
                        ModuleView.ChildComponentAndView childComponentAndView = childComponentAndViewList.get(j);

                        ViewGroup.MarginLayoutParams childLayoutParams =
                                (ViewGroup.MarginLayoutParams) childComponentAndView.childView.getLayoutParams();
                        if (BaseView.isTablet(context)) {
                            if (BaseView.isLandscape(context)) {
                                if (childComponentAndView.component.getLayout().getTabletLandscape().getYAxis() > 0 &&
                                        heightLayoutAdjuster.yAxis <
                                                childComponentAndView.component.getLayout().getTabletLandscape().getYAxis()) {
                                    childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                }
                                if (childComponentAndView.component.getLayout().getTabletLandscape().getTopMargin() > 0 &&
                                        heightLayoutAdjuster.topMargin <
                                                childComponentAndView.component.getLayout().getTabletLandscape().getTopMargin()) {
                                    childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                }
                            } else {
                                if (childComponentAndView.component.getLayout().getTabletPortrait().getYAxis() > 0 &&
                                        heightLayoutAdjuster.yAxis <
                                                childComponentAndView.component.getLayout().getTabletPortrait().getYAxis()) {
                                    childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                }
                                if (childComponentAndView.component.getLayout().getTabletPortrait().getTopMargin() > 0 &&
                                        heightLayoutAdjuster.topMargin <
                                                childComponentAndView.component.getLayout().getTabletPortrait().getTopMargin()) {
                                    childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                }
                            }
                        } else {
                            if (childComponentAndView.component.getLayout().getMobile().getYAxis() > 0 &&
                                    heightLayoutAdjuster.yAxis <
                                            childComponentAndView.component.getLayout().getMobile().getYAxis()) {
                                childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                            }
                            if (childComponentAndView.component.getLayout().getMobile().getTopMargin() > 0 &&
                                    heightLayoutAdjuster.topMargin <
                                            childComponentAndView.component.getLayout().getMobile().getTopMargin()) {
                                childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                            }
                        }
                        childComponentAndView.childView.setLayoutParams(childLayoutParams);
                    }
                }
                moduleView.setLayoutParams(moduleLayoutParams);
            }
        }
        moduleView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        return moduleView;
    }

    public CollectionGridItemView createCollectionGridItemView(final Context context,
                                                               final Layout parentLayout,
                                                               final boolean useParentLayout,
                                                               final Component component,
                                                               final AppCMSPresenter appCMSPresenter,
                                                               final Module moduleAPI,
                                                               Settings settings,
                                                               Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                                               int defaultWidth,
                                                               int defaultHeight,
                                                               boolean useMarginsAsPercentages,
                                                               boolean gridElement,
                                                               String viewType) {
        CollectionGridItemView collectionGridItemView = new CollectionGridItemView(context,
                parentLayout,
                useParentLayout,
                component,
                defaultWidth,
                defaultHeight);
        List<OnInternalEvent> onInternalEvents = new ArrayList<>();

        for (int i = 0; i < component.getComponents().size(); i++) {
            Component childComponent = component.getComponents().get(i);
            createComponentView(context,
                    childComponent,
                    parentLayout,
                    moduleAPI,
                    null,
                    settings,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    gridElement,
                    viewType);

            if (componentViewResult.onInternalEvent != null) {
                onInternalEvents.add(componentViewResult.onInternalEvent);
            }

            View componentView = componentViewResult.componentView;
            if (componentView != null) {
                CollectionGridItemView.ItemContainer itemContainer =
                        new CollectionGridItemView.ItemContainer.Builder()
                                .childView(componentView)
                                .component(childComponent)
                                .build();
                collectionGridItemView.addChild(itemContainer);
                collectionGridItemView.setComponentHasView(i, true);
                collectionGridItemView.setViewMarginsFromComponent(childComponent,
                        componentView,
                        collectionGridItemView.getLayout(),
                        collectionGridItemView.getChildrenContainer(),
                        false,
                        jsonValueKeyMap,
                        useMarginsAsPercentages,
                        componentViewResult.useWidthOfScreen,
                        viewType);
            } else {
                collectionGridItemView.setComponentHasView(i, false);
            }
        }

        return collectionGridItemView;
    }

    public void createComponentView(final Context context,
                                    final Component component,
                                    final Layout parentLayout,
                                    final Module moduleAPI,
                                    @Nullable final PageView pageView,
                                    final Settings settings,
                                    Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                    final AppCMSPresenter appCMSPresenter,
                                    boolean gridElement,
                                    final String viewType) {
        componentViewResult.componentView = null;
        componentViewResult.useMarginsAsPercentagesOverride = true;
        componentViewResult.useWidthOfScreen = false;
        componentViewResult.shouldHideModule = false;
        componentViewResult.addToPageView = false;
        componentViewResult.shouldHideComponent = false;
        componentViewResult.onInternalEvent = null;

        if (moduleAPI == null) {
            return;
        }

        AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());

        if (componentType == null) {
            componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());

        if (componentKey == null) {
            componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        String paymentProcessor = appCMSPresenter.getActiveSubscriptionProcessor(context);

        AppCMSUIKeyType moduleType = jsonValueKeyMap.get(viewType);

        int tintColor = Color.parseColor(getColor(context,
                appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()));

        switch (componentType) {
            case PAGE_TABLE_VIEW_KEY:
                if (moduleType == AppCMSUIKeyType.PAGE_DOWNLOAD_SETTING_MODULE_KEY) {

                    componentViewResult.componentView = new RecyclerView(context);

                    ((RecyclerView) componentViewResult.componentView)
                            .setLayoutManager(new LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL,
                                    false));

                    List<Mpeg> mpegs;
                    if (moduleAPI.getContentData() != null &&
                            !moduleAPI.getContentData().isEmpty() &&
                            moduleAPI.getContentData().get(0) != null &&
                            moduleAPI.getContentData().get(0).getStreamingInfo() != null &&
                            moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets() != null &&
                            moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets().getMpeg() != null) {
                        mpegs = moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets().getMpeg();
                    } else {
                        mpegs = new ArrayList<>();
                    }

                    List<Component> components;
                    if (component.getComponents() != null) {
                        components = component.getComponents();
                    } else {
                        components = new ArrayList<>();
                    }

                    AppCMSDownloadQualityAdapter radioAdapter = new AppCMSDownloadQualityAdapter(context,
                            mpegs,
                            components,
                            appCMSPresenter,
                            jsonValueKeyMap);

                    ((RecyclerView) componentViewResult.componentView).setAdapter(radioAdapter);
                    componentViewResult.componentView.setId(R.id.download_quality_selection_list);

                    if (pageView != null) {
                        pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                .adapter(radioAdapter)
                                .listview((RecyclerView) componentViewResult.componentView)
                                .id(moduleAPI.getId() + component.getKey())
                                .build());
                    }
                } else {

                    componentViewResult.componentView = new RecyclerView(context);

                    ((RecyclerView) componentViewResult.componentView)
                            .setLayoutManager(new LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL,
                                    false));
                    AppCMSTrayItemAdapter appCMSTrayItemAdapter = new AppCMSTrayItemAdapter(context,
                            moduleAPI.getContentData(),
                            component.getComponents(),
                            appCMSPresenter,
                            jsonValueKeyMap,
                            viewType);
                    ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSTrayItemAdapter);
                    componentViewResult.onInternalEvent = appCMSTrayItemAdapter;

                    if (pageView != null) {
                        pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                .adapter(appCMSTrayItemAdapter)
                                .listview((RecyclerView) componentViewResult.componentView)
                                .id(moduleAPI.getId() + component.getKey())
                                .build());
                    }
                }

                break;

            case PAGE_COLLECTIONGRID_KEY:

                if (moduleType == null) {
                    moduleType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                }

                if (moduleType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_IMAGEROW_KEY) {
                    componentViewResult.componentView = new ImageView(context);
                    if (BaseView.isTablet(context)) {
                        ((ImageView) componentViewResult.componentView).setImageResource(R.drawable.features_tablet);
                    } else {
                        ((ImageView) componentViewResult.componentView).setImageResource(R.drawable.features_mobile);
                    }
                } else {
                    componentViewResult.componentView = new RecyclerView(context);

                    AppCMSViewAdapter appCMSViewAdapter;
                    if (moduleType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY) {
                        if (!BaseView.isTablet(context)) {
                            ((RecyclerView) componentViewResult.componentView)
                                    .setLayoutManager(new LinearLayoutManager(context,
                                            LinearLayoutManager.VERTICAL,
                                            false));
                        } else {
                            ((RecyclerView) componentViewResult.componentView)
                                    .setLayoutManager(new LinearLayoutManager(context,
                                            LinearLayoutManager.HORIZONTAL,
                                            false));
                        }

                        appCMSViewAdapter = new AppCMSViewAdapter(context,
                                this,
                                appCMSPresenter,
                                settings,
                                component.getLayout(),
                                false,
                                component,
                                jsonValueKeyMap,
                                moduleAPI,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                viewType);

                        if (!BaseView.isTablet(context)) {
                            componentViewResult.useWidthOfScreen = true;
                        }
                    } else {
                        ((RecyclerView) componentViewResult.componentView)
                                .setLayoutManager(new LinearLayoutManager(context,
                                        LinearLayoutManager.HORIZONTAL,
                                        false));
                        appCMSViewAdapter = new AppCMSViewAdapter(context,
                                this,
                                appCMSPresenter,
                                settings,
                                parentLayout,
                                false,
                                component,
                                jsonValueKeyMap,
                                moduleAPI,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                viewType);
                        componentViewResult.useWidthOfScreen = true;
                    }

                    ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSViewAdapter);

                    if (pageView != null) {
                        pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                .adapter(appCMSViewAdapter)
                                .listview((RecyclerView) componentViewResult.componentView)
                                .id(moduleAPI.getId() + component.getKey())
                                .build());
                    }

                    if (moduleAPI.getContentData() == null ||
                            moduleAPI.getContentData().isEmpty()) {
                        componentViewResult.shouldHideModule = true;
                    }
                }

                break;

            case PAGE_CAROUSEL_VIEW_KEY:
                componentViewResult.componentView = new RecyclerView(context);
                ((RecyclerView) componentViewResult.componentView)
                        .setLayoutManager(new LinearLayoutManager(context,
                                LinearLayoutManager.HORIZONTAL,
                                false));
                boolean loop = false;
                if (settings.getLoop()) {
                    loop = settings.getLoop();
                }
                AppCMSCarouselItemAdapter appCMSCarouselItemAdapter =
                        new AppCMSCarouselItemAdapter(context,
                                this,
                                appCMSPresenter,
                                settings,
                                parentLayout,
                                component,
                                jsonValueKeyMap,
                                moduleAPI,
                                (RecyclerView) componentViewResult.componentView,
                                loop);
                ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSCarouselItemAdapter);
                if (pageView != null) {
                    pageView.addListWithAdapter(new ListWithAdapter.Builder()
                            .adapter(appCMSCarouselItemAdapter)
                            .listview((RecyclerView) componentViewResult.componentView)
                            .id(moduleAPI.getId() + component.getKey())
                            .build());
                }
                componentViewResult.onInternalEvent = appCMSCarouselItemAdapter;
                break;

            case PAGE_PAGE_CONTROL_VIEW_KEY:
                long selectedColor = Long.parseLong(appCMSPresenter.getAppCMSMain().getBrand()
                                .getGeneral()
                                .getBlockTitleColor().replace("#", ""),
                        16);
                long deselectedColor = component.getUnSelectedColor() != null ?
                        Long.valueOf(component.getUnSelectedColor(), 16) : 0L;

                deselectedColor = adjustColor1(deselectedColor, selectedColor);
                componentViewResult.componentView = new DotSelectorView(context,
                        component,
                        0xff000000 + (int) selectedColor,
                        0xff000000 + (int) deselectedColor);
                int numDots = moduleAPI.getContentData() != null ? moduleAPI.getContentData().size() : 0;
                ((DotSelectorView) componentViewResult.componentView).addDots(numDots);
                componentViewResult.onInternalEvent = (DotSelectorView) componentViewResult.componentView;
                componentViewResult.useMarginsAsPercentagesOverride = false;
                break;

            case PAGE_BUTTON_KEY:
                if (componentKey != AppCMSUIKeyType.PAGE_VIDEO_CLOSE_KEY &&
                        componentKey != AppCMSUIKeyType.PAGE_VIDEO_DOWNLOAD_BUTTON_KEY &&
                        componentKey != AppCMSUIKeyType.PAGE_BUTTON_SWITCH_KEY &&
                        componentKey != AppCMSUIKeyType.PAGE_ADD_TO_WATCHLIST_KEY) {
                    componentViewResult.componentView = new Button(context);
                } else if (componentKey == AppCMSUIKeyType.PAGE_BUTTON_SWITCH_KEY) {
                    componentViewResult.componentView = new Switch(context);
                } else {
                    componentViewResult.componentView = new ImageButton(context);
                }

                if (!gridElement) {
                    if (!TextUtils.isEmpty(component.getText()) && componentKey != AppCMSUIKeyType.PAGE_PLAY_KEY) {
                        ((TextView) componentViewResult.componentView).setText(component.getText());
                    } else if (moduleAPI.getSettings() != null &&
                            !moduleAPI.getSettings().getHideTitle() &&
                            !TextUtils.isEmpty(moduleAPI.getTitle()) &&
                            componentKey != AppCMSUIKeyType.PAGE_BUTTON_SWITCH_KEY &&
                            componentKey != AppCMSUIKeyType.PAGE_VIDEO_CLOSE_KEY) {
                        ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle());
                    }
                }

                if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand()
                        .getCta().getPrimary().getTextColor())) {
                    if (componentViewResult.componentView instanceof TextView) {
                        ((TextView) componentViewResult.componentView).setTextColor(
                                Color.parseColor(getColor(context, appCMSPresenter.getAppCMSMain()
                                        .getBrand().getCta().getPrimary().getTextColor())));
                    }
                }

                if (appCMSPresenter.isActionFacebook(component.getAction())) {
                    applyBorderToComponent(context, componentViewResult.componentView, component,
                            ContextCompat.getColor(context, R.color.facebookBlue));
                } else if (appCMSPresenter.isActionGoogle(component.getAction())) {
                    if (appCMSPresenter.getAppCMSMain().getSocialMedia() != null &&
                            appCMSPresenter.getAppCMSMain().getSocialMedia().getGooglePlus() != null &&
                            appCMSPresenter.getAppCMSMain().getSocialMedia().getGooglePlus().isSignin()) {
                        applyBorderToComponent(context, componentViewResult.componentView, component,
                                ContextCompat.getColor(context, R.color.googleRed));
                    } else if (appCMSPresenter.getAppCMSMain().getSocialMedia() == null ||
                            appCMSPresenter.getAppCMSMain().getSocialMedia().getGooglePlus() == null ||
                            !appCMSPresenter.getAppCMSMain().getSocialMedia().getGooglePlus().isSignin()) {
                        componentViewResult.componentView.setVisibility(View.GONE);
                    }
                } else if (jsonValueKeyMap.get(moduleAPI.getModuleType())
                        == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY
                        && componentKey == AppCMSUIKeyType.PAGE_DOWNLOAD_QUALITY_CANCEL_BUTTON_KEY
                        && component.getBorderWidth() != 0) {
                    applyBorderToComponent(
                            context,
                            componentViewResult.componentView,
                            component,
                            -1);
                } else {
                    if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                            .getBlockTitleColor())) {
                        componentViewResult.componentView.setBackgroundColor(Color.parseColor(
                                getColor(context, appCMSPresenter.getAppCMSMain().getBrand()
                                        .getGeneral().getBlockTitleColor())));
                    } else {
                        applyBorderToComponent(context, componentViewResult.componentView, component, -1);
                    }
                }

                switch (componentKey) {
                    case PAGE_BUTTON_SWITCH_KEY:
                        if (appCMSPresenter.isPreferedStorageLocationSDCard(context)) {
                            ((Switch) componentViewResult.componentView).setChecked(true);
                        } else {
                            ((Switch) componentViewResult.componentView).setChecked(false);
                        }

                        ((Switch) componentViewResult.componentView).setOnCheckedChangeListener((buttonView, isChecked) -> {
                            if (isChecked) {
                                if (appCMSPresenter.isRemoveableSDCardAvailable()) {
                                    appCMSPresenter.setPreferedStorageLocationSDCard(context, true);
                                } else {
                                    appCMSPresenter.showDialog(AppCMSPresenter.DialogType.SD_CARD_NOT_AVAILABLE, null, false, null);
                                    buttonView.setChecked(false);
                                }
                            } else {
                                appCMSPresenter.setPreferedStorageLocationSDCard(context, false);
                            }

                        });
                        break;

                    case PAGE_SETTINGS_EDIT_PROFILE_KEY:
                    case PAGE_SETTINGS_CHANGE_PASSWORD_KEY:
                        if (!TextUtils.isEmpty(appCMSPresenter.getFacebookAccessToken(context))) {
                            componentViewResult.componentView.setVisibility(View.GONE);
                            componentViewResult.shouldHideComponent = true;
                        }

                        if (!TextUtils.isEmpty(appCMSPresenter.getGoogleAccessToken(context))) {
                            componentViewResult.componentView.setVisibility(View.GONE);
                            componentViewResult.shouldHideComponent = true;
                        }

                        componentViewResult.componentView.setOnClickListener(v -> {
                            String[] extraData = new String[1];
                            extraData[0] = component.getKey();
                            appCMSPresenter.launchButtonSelectedAction(null,
                                    component.getAction(),
                                    null,
                                    extraData,
                                    null,
                                    false,
                                    0,
                                    null);
                        });
                        break;

                    case PAGE_AUTOPLAY_BACK_KEY:
                        componentViewResult.componentView.setVisibility(View.GONE);
                        break;

                    case PAGE_INFO_KEY:
                        componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.info_icon));
                        break;

                    case PAGE_VIDEO_DOWNLOAD_BUTTON_KEY:
                        ((ImageButton) componentViewResult.componentView).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        componentViewResult.componentView.setBackgroundResource(android.R.color.transparent);
                        if (moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null) {
                            String userId = appCMSPresenter.getLoggedInUser(context);
                            appCMSPresenter.getUserVideoDownloadStatus(
                                    moduleAPI.getContentData().get(0).getGist().getId(), new UpdateDownloadImageIconAction((ImageButton) componentViewResult.componentView, appCMSPresenter,
                                            moduleAPI.getContentData().get(0), userId), userId);
                        }
                        componentViewResult.componentView.setVisibility(View.VISIBLE);
                        break;

                    case PAGE_ADD_TO_WATCHLIST_KEY:
                        ((ImageButton) componentViewResult.componentView)
                                .setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        componentViewResult.componentView.setBackgroundResource(android.R.color.transparent);

                        if (moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null) {
                            appCMSPresenter.getUserVideoStatus(
                                    moduleAPI.getContentData().get(0).getGist().getId(),
                                    new UpdateImageIconAction((ImageButton) componentViewResult.componentView, appCMSPresenter, moduleAPI.getContentData()
                                            .get(0).getGist().getId()));
                        }
                        componentViewResult.componentView.setVisibility(View.VISIBLE);

                        componentViewResult.componentView.setVisibility(View.VISIBLE);
                        break;

                    case PAGE_VIDEO_WATCH_TRAILER_KEY:
                        if (moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getContentDetails() != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers() != null &&
                                !moduleAPI.getContentData().get(0).getContentDetails().getTrailers().isEmpty() &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0) != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink() != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getId() != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getVideoAssets() != null) {
                            componentViewResult.componentView.setOnClickListener(v -> {
                                String[] extraData = new String[3];
                                extraData[0] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink();
                                extraData[1] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getVideoAssets().getHls();
                                extraData[2] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getId();
                                if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                        component.getAction(),
                                        moduleAPI.getContentData().get(0).getGist().getTitle(),
                                        extraData,
                                        moduleAPI.getContentData().get(0),
                                        false,
                                        -1,
                                        null)) {
                                    Log.e(TAG, "Could not launch action: " +
                                            " permalink: " +
                                            moduleAPI.getContentData().get(0).getGist().getPermalink() +
                                            " action: " +
                                            component.getAction() +
                                            " hls URL: " +
                                            moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets().getHls());
                                }
                            });
                        } else {
                            componentViewResult.shouldHideComponent = true;
                            componentViewResult.componentView.setVisibility(View.GONE);
                        }
                        break;

                    case PAGE_VIDEO_PLAY_BUTTON_KEY:
                        componentViewResult.componentView.setOnClickListener(v -> {
                            if (moduleAPI.getContentData() != null &&
                                    !moduleAPI.getContentData().isEmpty() &&
                                    moduleAPI.getContentData().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getStreamingInfo() != null &&
                                    moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets() != null) {
                                VideoAssets videoAssets = moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets();
                                String videoUrl = videoAssets.getHls();
                                if (TextUtils.isEmpty(videoUrl)) {
                                    for (int i = 0; i < videoAssets.getMpeg().size() && TextUtils.isEmpty(videoUrl); i++) {
                                        videoUrl = videoAssets.getMpeg().get(i).getUrl();
                                    }
                                }
                                if (moduleAPI.getContentData() != null &&
                                        !moduleAPI.getContentData().isEmpty() &&
                                        moduleAPI.getContentData().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getGist() != null &&
                                        moduleAPI.getContentData().get(0).getGist().getId() != null &&
                                        moduleAPI.getContentData().get(0).getGist().getPermalink() != null) {
                                    String[] extraData = new String[3];
                                    extraData[0] = moduleAPI.getContentData().get(0).getGist().getPermalink();
                                    extraData[1] = videoUrl;
                                    extraData[2] = moduleAPI.getContentData().get(0).getGist().getId();
                                    if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                            component.getAction(),
                                            moduleAPI.getContentData().get(0).getGist().getTitle(),
                                            extraData,
                                            moduleAPI.getContentData().get(0),
                                            false,
                                            -1,
                                            moduleAPI.getContentData().get(0).getContentDetails().getRelatedVideoIds())) {
                                        Log.e(TAG, "Could not launch action: " +
                                                " permalink: " +
                                                moduleAPI.getContentData().get(0).getGist().getPermalink() +
                                                " action: " +
                                                component.getAction() +
                                                " video URL: " +
                                                videoUrl);
                                    }
                                }
                            }
                        });
                        componentViewResult.componentView.setBackground(ContextCompat.getDrawable(context, R.drawable.play_icon));
                        componentViewResult.componentView.getBackground().setTint(tintColor);
                        componentViewResult.componentView.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                        break;

                    case PAGE_PLAY_KEY:
                    case PAGE_PLAY_IMAGE_KEY:
                        componentViewResult.componentView.setBackground(ContextCompat.getDrawable(context, R.drawable.play_icon));
                        componentViewResult.componentView.getBackground().setTint(tintColor);
                        componentViewResult.componentView.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                        break;

                    case PAGE_VIDEO_CLOSE_KEY:
                        ((ImageButton) componentViewResult.componentView).setImageResource(R.drawable.cancel);
                        ((ImageButton) componentViewResult.componentView).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        int fillColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor());
                        ((ImageButton) componentViewResult.componentView).getDrawable().setColorFilter(new PorterDuffColorFilter(fillColor, PorterDuff.Mode.MULTIPLY));
                        componentViewResult.componentView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                        componentViewResult.componentView.setOnClickListener(v -> {
                            if (!appCMSPresenter.launchButtonSelectedAction(null,
                                    component.getAction(),
                                    null,
                                    null,
                                    null,
                                    false,
                                    0,
                                    null)) {
                                Log.e(TAG, "Could not launch action: " +
                                        " action: " +
                                        component.getAction());
                            }
                        });
                        break;

                    case PAGE_VIDEO_SHARE_KEY:
                        Drawable shareDrawable = ContextCompat.getDrawable(context, R.drawable.share);
                        componentViewResult.componentView.setBackground(shareDrawable);
                        componentViewResult.componentView.setOnClickListener(v -> {
                            AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
                            if (appCMSMain != null &&
                                    moduleAPI.getContentData() != null &&
                                    !moduleAPI.getContentData().isEmpty() &&
                                    moduleAPI.getContentData().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getGist() != null &&
                                    moduleAPI.getContentData().get(0).getGist().getTitle() != null &&
                                    moduleAPI.getContentData().get(0).getGist().getPermalink() != null) {
                                StringBuilder filmUrl = new StringBuilder();
                                filmUrl.append(appCMSMain.getDomainName());
                                filmUrl.append(moduleAPI.getContentData().get(0).getGist().getPermalink());
                                String[] extraData = new String[1];
                                extraData[0] = filmUrl.toString();
                                if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                        component.getAction(),
                                        moduleAPI.getContentData().get(0).getGist().getTitle(),
                                        extraData,
                                        moduleAPI.getContentData().get(0),
                                        false,
                                        0,
                                        null)) {
                                    Log.e(TAG, "Could not launch action: " +
                                            " permalink: " +
                                            moduleAPI.getContentData().get(0).getGist().getPermalink() +
                                            " action: " +
                                            component.getAction() +
                                            " film URL: " +
                                            filmUrl.toString());
                                }
                            }
                        });
                        break;

                    case PAGE_FORGOTPASSWORD_KEY:
                        componentViewResult.componentView.setBackgroundColor(
                                ContextCompat.getColor(context, android.R.color.transparent));
                        break;

                    case PAGE_REMOVEALL_KEY:
                        componentViewResult.addToPageView = true;

                        FrameLayout.LayoutParams removeAllLayoutParams =
                                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);

                        removeAllLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

                        componentViewResult.componentView.setLayoutParams(removeAllLayoutParams);

                        componentViewResult.onInternalEvent = new OnInternalEvent() {
                            final View removeAllButton = componentViewResult.componentView;
                            private List<OnInternalEvent> receivers = new ArrayList<>();

                            @Override
                            public void addReceiver(OnInternalEvent e) {
                                receivers.add(e);
                            }

                            @Override
                            public void sendEvent(InternalEvent<?> event) {
                                for (OnInternalEvent internalEvent : receivers) {
                                    internalEvent.receiveEvent(null);
                                }
                            }

                            @Override
                            public void receiveEvent(InternalEvent<?> event) {
                                removeAllButton.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void cancel(boolean cancel) {
                                //
                            }
                        };
                        componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                            OnInternalEvent onInternalEvent = componentViewResult.onInternalEvent;

                            @Override
                            public void onClick(final View v) {
                                switch (jsonValueKeyMap.get(viewType)) {
                                    case PAGE_HISTORY_MODULE_KEY:
                                        appCMSPresenter.clearHistory(appCMSDeleteHistoryResult -> {
                                            onInternalEvent.sendEvent(null);
                                            v.setVisibility(View.GONE);
                                        });
                                        break;

                                    case PAGE_DOWNLOAD_MODULE_KEY:
                                        appCMSPresenter.clearDownload(appCMSAddToWatchlistResult -> {
                                            onInternalEvent.sendEvent(null);
                                            v.setVisibility(View.GONE);
                                        });
                                        break;

                                    case PAGE_WATCHLIST_MODULE_KEY:
                                        appCMSPresenter.clearWatchlist(addToWatchlistResult -> {
                                            onInternalEvent.sendEvent(null);
                                            v.setVisibility(View.GONE);
                                        });
                                        break;

                                    default:
                                        break;
                                }
                            }
                        });
                        break;

                    case PAGE_AUTOPLAY_MOVIE_PLAY_BUTTON_KEY:
                        componentViewResult.componentView.setId(R.id.autoplay_play_button);
                        break;

                    case PAGE_AUTOPLAY_MOVIE_CANCEL_BUTTON_KEY:
                        componentViewResult.componentView.setOnClickListener(v -> {
                            if (!appCMSPresenter.sendCloseOthersAction(null, true)) {
                                Log.e(TAG, "Could not perform close action: " +
                                        " action: " +
                                        component.getAction());
                            }
                        });
                        break;

                    case PAGE_DOWNLOAD_QUALITY_CONTINUE_BUTTON_KEY:
                        componentViewResult.componentView.setId(R.id.download_quality_continue_button);
                        break;

                    case PAGE_DOWNLOAD_QUALITY_CANCEL_BUTTON_KEY:
                        if (jsonValueKeyMap.get(moduleAPI.getModuleType())
                                == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY) {
                            componentViewResult.componentView.setOnClickListener(v -> {
                                if (!appCMSPresenter.sendCloseOthersAction(null, true)) {
                                    Log.e(TAG, "Could not perform close action: " +
                                            " action: " +
                                            component.getAction());
                                }
                            });
                        } else {
                            componentViewResult.componentView.setId(R.id.download_quality_cancel_button);
                            applyBorderToComponent(
                                    context,
                                    componentViewResult.componentView,
                                    component,
                                    -1);
                        }
                        break;

                    default:
                        if (!appCMSPresenter.isUserSubscribed(context)) {
                            if (componentKey == AppCMSUIKeyType.PAGE_SETTINGS_UPGRADE_PLAN_PROFILE_KEY) {
                                ((TextView) componentViewResult.componentView).setText(context.getString(R.string.app_cms_page_upgrade_subscribe_button_text));
                            } else if (componentKey == AppCMSUIKeyType.PAGE_SETTINGS_CANCEL_PLAN_PROFILE_KEY) {
                                componentViewResult.componentView.setVisibility(View.GONE);
                            }
                        }

                        componentViewResult.componentView.setOnClickListener(v -> {
                            String[] extraData = new String[1];
                            extraData[0] = component.getKey();
                            appCMSPresenter.launchButtonSelectedAction(null,
                                    component.getAction(),
                                    null,
                                    extraData,
                                    null,
                                    false,
                                    0,
                                    null);
                        });
                        break;
                }

                if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_SETTINGS_KEY) {
                    componentViewResult.componentView.setBackgroundColor(
                            ContextCompat.getColor(context, android.R.color.transparent));
                    if (componentViewResult.componentView instanceof Button) {
                        ((Button) componentViewResult.componentView)
                                .setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                                        .getBrand()
                                        .getGeneral()
                                        .getBlockTitleColor()));
                    }
                }
                break;

            case PAGE_LABEL_KEY:
            case PAGE_TEXTVIEW_KEY:
                componentViewResult.componentView = new TextView(context);
                int textColor = ContextCompat.getColor(context, R.color.colorAccent);
                if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor())) {
                    textColor = Color.parseColor(getColor(context, appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()));
                } else if (component.getStyles() != null) {
                    if (!TextUtils.isEmpty(component.getStyles().getColor())) {
                        textColor = Color.parseColor(getColor(context, component.getStyles().getColor()));
                    } else if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor())) {
                        textColor =
                                Color.parseColor(getColor(context, appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()));
                    }
                }

                if (componentKey == AppCMSUIKeyType.PAGE_AUTOPLAY_FINISHED_UP_TITLE_KEY
                        || componentKey == AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_TITLE_KEY
                        || componentKey == AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_SUBHEADING_KEY
                        || componentKey == AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_DESCRIPTION_KEY
                        || componentKey == AppCMSUIKeyType.PAGE_VIDEO_AGE_LABEL_KEY) {
                    textColor = Color.parseColor(getColor(context,
                            appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()));
                    ((TextView) componentViewResult.componentView).setTextColor(textColor);
                } else if (componentKey != AppCMSUIKeyType.PAGE_TRAY_TITLE_KEY) {
                    ((TextView) componentViewResult.componentView).setTextColor(textColor);
                } else {
                    ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor(getColor(context,
                            appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor())));
                }

                if (!gridElement) {
                    switch (componentKey) {
                        case PAGE_API_TITLE:
                            if (!TextUtils.isEmpty(moduleAPI.getTitle())) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle());
                                if (component.getNumberOfLines() != 0) {
                                    ((TextView) componentViewResult.componentView).setMaxLines(component.getNumberOfLines());
                                }
                                ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                            } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_HISTORY_MODULE_KEY) {
                                ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_history_title);
                            } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_WATCHLIST_MODULE_KEY) {
                                ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_watchlist_title);
                            } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_WATCHLIST_MODULE_KEY) {
                                ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_download_title);
                            }
                            break;

                        case PAGE_API_DESCRIPTION:
                            if (!TextUtils.isEmpty(moduleAPI.getRawText())) {
                                Spannable rawHtmlSpannable = new HtmlSpanner().fromHtml(moduleAPI.getRawText());
                                ((TextView) componentViewResult.componentView).setText(rawHtmlSpannable);
                                ((TextView) componentViewResult.componentView).setMovementMethod(LinkMovementMethod.getInstance());
                            }
                            break;

                        case PAGE_TRAY_TITLE_KEY:
                            if (!TextUtils.isEmpty(component.getText())) {
                                ((TextView) componentViewResult.componentView).setText(component.getText().toUpperCase());
                            } else if (moduleAPI.getSettings() != null && !moduleAPI.getSettings().getHideTitle() &&
                                    !TextUtils.isEmpty(moduleAPI.getTitle())) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle().toUpperCase());
                            } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_WATCHLIST_MODULE_KEY) {
                                ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_watchlist_title);
                            } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY) {
                                ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_download_title);
                            } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_HISTORY_MODULE_KEY) {
                                ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_history_title);
                            }
                            break;

                        case PAGE_AUTOPLAY_MOVIE_DESCRIPTION_KEY:
                            String autoplayVideoDescription = null;
                            if (moduleAPI.getContentData() != null &&
                                    !moduleAPI.getContentData().isEmpty() &&
                                    moduleAPI.getContentData().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getGist() != null &&
                                    moduleAPI.getContentData().get(0).getGist().getDescription() != null) {
                                autoplayVideoDescription = moduleAPI.getContentData().get(0).getGist().getDescription();
                            }
                            if (autoplayVideoDescription != null) {
                                autoplayVideoDescription = autoplayVideoDescription.trim();
                            }
                            if (!TextUtils.isEmpty(autoplayVideoDescription)) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                    ((TextView) componentViewResult.componentView).setText(Html.fromHtml(autoplayVideoDescription));
                                } else {
                                    ((TextView) componentViewResult.componentView).setText(Html.fromHtml(autoplayVideoDescription, Html.FROM_HTML_MODE_COMPACT));
                                }
                            } else if (!BaseView.isLandscape(context)) {
                                componentViewResult.shouldHideComponent = true;
                            }
                            if (moduleAPI.getContentData() != null &&
                                    !moduleAPI.getContentData().isEmpty() &&
                                    moduleAPI.getContentData().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getGist() != null &&
                                    !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getTitle())) {
                                ViewTreeObserver viewTreeObserver = componentViewResult.componentView.getViewTreeObserver();
                                ViewCreatorMultiLineLayoutListener viewCreatorMultiLineLayoutListener =
                                        new ViewCreatorMultiLineLayoutListener(((TextView) componentViewResult.componentView),
                                                moduleAPI.getContentData().get(0).getGist().getTitle(),
                                                autoplayVideoDescription,
                                                appCMSPresenter,
                                                true);
                                viewTreeObserver.addOnGlobalLayoutListener(viewCreatorMultiLineLayoutListener);
                            }
                            break;

                        case PAGE_VIDEO_DESCRIPTION_KEY:
                            String videoDescription = null;
                            if (moduleAPI.getContentData() != null &&
                                    !moduleAPI.getContentData().isEmpty() &&
                                    moduleAPI.getContentData().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getGist() != null &&
                                    moduleAPI.getContentData().get(0).getGist().getDescription() != null) {
                                videoDescription = moduleAPI.getContentData().get(0).getGist().getDescription();
                            }
                            if (videoDescription != null) {
                                videoDescription = videoDescription.trim();
                            }
                            if (!TextUtils.isEmpty(videoDescription)) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                    ((TextView) componentViewResult.componentView).setText(Html.fromHtml(videoDescription));
                                } else {
                                    ((TextView) componentViewResult.componentView).setText(Html.fromHtml(videoDescription, Html.FROM_HTML_MODE_COMPACT));
                                }
                            } else if (!BaseView.isLandscape(context)) {
                                componentViewResult.shouldHideComponent = true;
                            }
                            if (moduleAPI.getContentData() != null &&
                                    !moduleAPI.getContentData().isEmpty() &&
                                    moduleAPI.getContentData().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getGist() != null &&
                                    moduleAPI.getContentData().get(0).getGist().getTitle() != null) {
                                ViewTreeObserver textVto = componentViewResult.componentView.getViewTreeObserver();
                                ViewCreatorMultiLineLayoutListener viewCreatorLayoutListener =
                                        new ViewCreatorMultiLineLayoutListener(((TextView) componentViewResult.componentView),
                                                moduleAPI.getContentData().get(0).getGist().getTitle(),
                                                videoDescription,
                                                appCMSPresenter,
                                                false);
                                textVto.addOnGlobalLayoutListener(viewCreatorLayoutListener);
                            }
                            break;

                        case PAGE_AUTOPLAY_MOVIE_TITLE_KEY:
                            if (moduleAPI.getContentData() != null &&
                                    !moduleAPI.getContentData().isEmpty() &&
                                    moduleAPI.getContentData().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getGist() != null &&
                                    !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getTitle())) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getTitle());
                            }
                            ViewTreeObserver titleTextVto = componentViewResult.componentView.getViewTreeObserver();
                            ViewCreatorTitleLayoutListener viewCreatorTitleLayoutListener =
                                    new ViewCreatorTitleLayoutListener((TextView) componentViewResult.componentView);
                            titleTextVto.addOnGlobalLayoutListener(viewCreatorTitleLayoutListener);
                            ((TextView) componentViewResult.componentView).setSingleLine(true);
                            ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            componentViewResult.componentView.setSelected(true);
                            break;

                        case PAGE_VIDEO_TITLE_KEY:
                            if (moduleAPI.getContentData() != null &&
                                    !moduleAPI.getContentData().isEmpty() &&
                                    moduleAPI.getContentData().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getGist() != null &&
                                    !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getTitle())) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getTitle());
                            }
                            titleTextVto = componentViewResult.componentView.getViewTreeObserver();
                            viewCreatorTitleLayoutListener =
                                    new ViewCreatorTitleLayoutListener((TextView) componentViewResult.componentView);
                            titleTextVto.addOnGlobalLayoutListener(viewCreatorTitleLayoutListener);
                            ((TextView) componentViewResult.componentView).setSingleLine(true);
                            ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                            break;

                        case PAGE_VIDEO_SUBTITLE_KEY:
                        case PAGE_AUTOPLAY_MOVIE_SUBHEADING_KEY:
                            if (moduleAPI.getContentData() != null &&
                                    !moduleAPI.getContentData().isEmpty() &&
                                    moduleAPI.getContentData().get(0) != null) {
                                setViewWithSubtitle(context,
                                        moduleAPI.getContentData().get(0),
                                        componentViewResult.componentView);
                            }
                            break;

                        case PAGE_VIDEO_AGE_LABEL_KEY:
                            if (moduleAPI.getContentData() != null &&
                                    !moduleAPI.getContentData().isEmpty() &&
                                    moduleAPI.getContentData().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getGist() != null &&
                                    !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getParentalRating())) {
                                String parentalRating = moduleAPI.getContentData().get(0).getParentalRating();
                                String convertedRating = context.getString(R.string.age_rating_converted_default);
                                if (parentalRating.contains(context.getString(R.string.age_rating_y7))) {
                                    convertedRating = context.getString(R.string.age_rating_converted_y7);
                                } else if (parentalRating.contains(context.getString(R.string.age_rating_y))) {
                                    convertedRating = context.getString(R.string.age_rating_converted_y);
                                } else if (parentalRating.contains(context.getString(R.string.age_rating_pg))) {
                                    convertedRating = context.getString(R.string.age_rating_converted_pg);
                                } else if (parentalRating.contains(context.getString(R.string.age_rating_g))) {
                                    convertedRating = context.getString(R.string.age_rating_converted_g);
                                } else if (parentalRating.contains(context.getString(R.string.age_rating_fourteen))) {
                                    convertedRating = context.getString(R.string.age_rating_converted_fourteen);
                                } else if (parentalRating.contains(context.getString(R.string.age_rating_converted_default))) {
                                    convertedRating = context.getString(R.string.age_rating_converted_default);
                                } else if (parentalRating.contains(context.getString(R.string.age_raging_r))) {
                                    convertedRating = context.getString(R.string.age_rating_converted_eighteen);
                                }
                                ((TextView) componentViewResult.componentView).setText(convertedRating);
                                ((TextView) componentViewResult.componentView).setGravity(Gravity.CENTER);
                                applyBorderToComponent(context,
                                        componentViewResult.componentView,
                                        component,
                                        -1);
                            }
                            break;

                        case PAGE_AUTOPLAY_MOVIE_TIMER_LABEL_KEY:
                            componentViewResult.componentView.setId(R.id.countdown_id);
                            ((TextView) componentViewResult.componentView)
                                    .setShadowLayer(
                                            20,
                                            0,
                                            0,
                                            Color.parseColor(getColor(
                                                    context,
                                                    component.getTextColor())));
                            break;

                        case PAGE_ACTIONLABEL_KEY:

                        case PAGE_SETTINGS_NAME_VALUE_KEY:
                            ((TextView) componentViewResult.componentView).setText(appCMSPresenter.getLoggedInUserName(context));
                            break;

                        case PAGE_SETTINGS_EMAIL_VALUE_KEY:
                            ((TextView) componentViewResult.componentView).setText(appCMSPresenter.getLoggedInUserEmail(context));
                            break;

                        case PAGE_SETTINGS_EMAIL_TITLE_KEY:
                            if (TextUtils.isEmpty(appCMSPresenter.getLoggedInUserEmail(context))) {
                                componentViewResult.componentView.setVisibility(View.GONE);
                                componentViewResult.shouldHideComponent = true;
                            }
                            break;

                        case PAGE_SETTINGS_PLAN_VALUE_KEY:
                            if (appCMSPresenter.isUserSubscribed(context) &&
                                    !TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionPlanName(context))) {
                                ((TextView) componentViewResult.componentView).setText(appCMSPresenter.getActiveSubscriptionPlanName(context));
                            } else if (!appCMSPresenter.isUserSubscribed(context)) {
                                ((TextView) componentViewResult.componentView).setText(context.getString(R.string.subscription_unsubscribed_plan_value));
                            }
                            break;

                        case PAGE_SETTINGS_PLAN_PROCESSOR_TITLE_KEY:
                            if (appCMSPresenter.isUserSubscribed(context) &&
                                    !TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionPlanName(context))) {
                                componentViewResult.componentView.setVisibility(View.VISIBLE);
                            } else {
                                componentViewResult.componentView.setVisibility(View.GONE);
                                componentViewResult.shouldHideComponent = true;
                            }

                            if (!TextUtils.isEmpty(component.getText())) {
                                ((TextView) componentViewResult.componentView).setText(component.getText());
                            }

                            break;

                        case PAGE_SETTINGS_PLAN_PROCESSOR_VALUE_KEY:
                            if (paymentProcessor != null) {
                                if (paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_ios_payment_processor)) ||
                                        paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_ios_payment_processor_friendly))) {
                                    ((TextView) componentViewResult.componentView).setText(context.getString(R.string.subscription_ios_payment_processor_friendly));
                                } else if (paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_web_payment_processor_friendly))) {
                                    ((TextView) componentViewResult.componentView).setText(context.getString(R.string.subscription_web_payment_processor_friendly));
                                } else if (paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_android_payment_processor)) ||
                                        paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_android_payment_processor_friendly))) {
                                    ((TextView) componentViewResult.componentView).setText(context.getString(R.string.subscription_android_payment_processor_friendly));
                                } else if (paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_ccavenue_payment_processor))) {
                                    ((TextView) componentViewResult.componentView).setText(context.getString(R.string.subscription_ccavenue_payment_processor_friendly));
                                }
                            } else {
                                ((TextView) componentViewResult.componentView).setText("");
                            }
                            break;

                        case PAGE_SETTINGS_DOWNLOAD_QUALITY_PROFILE_KEY:
                            ((TextView) componentViewResult.componentView).setText(appCMSPresenter.getUserDownloadQualityPref(context));
                            break;

                        case PAGE_SETTINGS_APP_VERSION_VALUE_KEY:
                            ((TextView) componentViewResult.componentView).setText(context.getString(R.string.app_cms_app_version));
                            break;

                        case PAGE_SETTINGS_TITLE_KEY:
                            ((TextView) componentViewResult.componentView)
                                    .setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                                            .getBrand()
                                            .getGeneral()
                                            .getBlockTitleColor()));
                            if (!TextUtils.isEmpty(component.getText())) {
                                ((TextView) componentViewResult.componentView).setText(component.getText());
                            }
                            break;

                        default:
                            if (!TextUtils.isEmpty(component.getText())) {
                                ((TextView) componentViewResult.componentView).setText(component.getText());
                            }
                            break;
                    }
                } else {
                    ((TextView) componentViewResult.componentView).setSingleLine(true);
                    ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                }

                if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.setBackgroundColor(
                            Color.parseColor(getColor(context, component.getBackgroundColor())));
                }

                if (!TextUtils.isEmpty(component.getFontFamily())) {
                    setTypeFace(context,
                            jsonValueKeyMap,
                            component,
                            (TextView) componentViewResult.componentView);
                }
                break;

            case PAGE_IMAGE_KEY:
                componentViewResult.componentView = new ImageView(context);
                switch (componentKey) {
                    case PAGE_AUTOPLAY_MOVIE_IMAGE_KEY:
                        if (moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null &&
                                !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getPosterImageUrl()) &&
                                !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())) {
                            int viewWidth = (int) BaseView.getViewWidth(context,
                                    component.getLayout(),
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            int viewHeight = (int) BaseView.getViewHeight(context,
                                    component.getLayout(),
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            if (viewHeight > 0 && viewWidth > 0 && viewHeight > viewWidth) {
                                Glide.with(context)
                                        .load(moduleAPI.getContentData().get(0).getGist().getPosterImageUrl())
                                        .override(viewWidth, viewHeight)
                                        .centerCrop()
                                        .into((ImageView) componentViewResult.componentView);
                            } else if (viewWidth > 0) {
                                Glide.with(context)
                                        .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                                        .override(viewWidth, viewHeight)
                                        .centerCrop()
                                        .into((ImageView) componentViewResult.componentView);
                            } else {
                                Glide.with(context)
                                        .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                                        .into((ImageView) componentViewResult.componentView);
                            }
                            componentViewResult.useWidthOfScreen = false;
                        }
                        break;

                    case PAGE_VIDEO_IMAGE_KEY:
                        if (moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null &&
                                !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getPosterImageUrl()) &&
                                !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())) {
                            int viewWidth = BaseView.isLandscape(context) ?
                                    ViewGroup.LayoutParams.WRAP_CONTENT :
                                    context.getResources().getDisplayMetrics().widthPixels;
                            int viewHeight = (int) BaseView.getViewHeight(context,
                                    component.getLayout(),
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            if (viewHeight > 0 && viewWidth > 0 && viewHeight > viewWidth) {
                                String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                        moduleAPI.getContentData().get(0).getGist().getPosterImageUrl(),
                                        viewWidth,
                                        viewHeight);
                                Glide.with(context)
                                        .load(imageUrl)
                                        .override(viewWidth, viewHeight)
                                        .centerCrop()
                                        .into((ImageView) componentViewResult.componentView);
                            } else if (viewWidth > 0) {
                                String videoImageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                        moduleAPI.getContentData().get(0).getGist().getVideoImageUrl(),
                                        viewWidth,
                                        viewHeight);
                                Glide.with(context)
                                        .load(videoImageUrl)
                                        .override(viewWidth, viewHeight)
                                        .centerCrop()
                                        .into((ImageView) componentViewResult.componentView);
                            } else {
                                Glide.with(context)
                                        .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                                        .fitCenter()
                                        .into((ImageView) componentViewResult.componentView);
                            }
                            componentViewResult.useWidthOfScreen = !BaseView.isLandscape(context);
                        }
                        break;

                    default:
                        if (!TextUtils.isEmpty(component.getImageName())) {
                            Glide.with(context)
                                    .load(component.getImageName())
                                    .into((ImageView) componentViewResult.componentView);
                        }
                        ((ImageView) componentViewResult.componentView).setScaleType(ImageView.ScaleType.FIT_CENTER);
                        break;
                }
                break;

            case PAGE_BACKGROUND_IMAGE_TYPE_KEY:
                componentViewResult.componentView = new ImageView(context);
                if (jsonValueKeyMap.get(component.getView()) == AppCMSUIKeyType.PAGE_BACKGROUND_IMAGE_KEY) {
                    ((ImageView) componentViewResult.componentView).setImageResource(R.drawable.logo);
                    ((ImageView) componentViewResult.componentView).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }
                break;

            case PAGE_PROGRESS_VIEW_KEY:
                componentViewResult.componentView = new ProgressBar(context,
                        null,
                        android.R.attr.progressBarStyleHorizontal);
                if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor())) {
                    int color = Color.parseColor(getColor(context, appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor()));
                    ((ProgressBar) componentViewResult.componentView).getProgressDrawable()
                            .setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }

                if (appCMSPresenter.isUserLoggedIn(context)) {
                    ((ProgressBar) componentViewResult.componentView).setMax(100);
                    ((ProgressBar) componentViewResult.componentView).setProgress(0);
                    if (moduleAPI.getContentData() != null &&
                            !moduleAPI.getContentData().isEmpty() &&
                            moduleAPI.getContentData().get(0) != null &&
                            moduleAPI.getContentData().get(0).getGist() != null) {
                        if (moduleAPI.getContentData()
                                .get(0).getGist().getWatchedPercentage() > 0) {
                            componentViewResult.componentView.setVisibility(View.VISIBLE);
                            ((ProgressBar) componentViewResult.componentView)
                                    .setProgress(moduleAPI.getContentData()
                                            .get(0).getGist().getWatchedPercentage());
                        } else {
                            long watchedTime =
                                    moduleAPI.getContentData().get(0).getGist().getWatchedTime();
                            long runTime =
                                    moduleAPI.getContentData().get(0).getGist().getRuntime();
                            if (watchedTime > 0 && runTime > 0) {
                                long percentageWatched = watchedTime / runTime;
                                ((ProgressBar) componentViewResult.componentView)
                                        .setProgress((int) percentageWatched);
                                componentViewResult.componentView.setVisibility(View.VISIBLE);
                            } else {
                                componentViewResult.componentView.setVisibility(View.INVISIBLE);
                                ((ProgressBar) componentViewResult.componentView).setProgress(0);
                            }
                        }
                    } else {
                        componentViewResult.componentView.setVisibility(View.INVISIBLE);
                        ((ProgressBar) componentViewResult.componentView).setProgress(0);
                    }
                } else {
                    componentViewResult.componentView.setVisibility(View.GONE);
                }
                break;

            case PAGE_SEPARATOR_VIEW_KEY:
            case PAGE_SEGMENTED_VIEW_KEY:
                componentViewResult.componentView = new View(context);
                if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                        .getTextColor())) {
                    componentViewResult.componentView.
                            setBackgroundColor(Color.parseColor(getColor(context,
                                    appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor())));
                }
                componentViewResult.componentView.setAlpha(0.6f);
                break;

            case PAGE_CASTVIEW_VIEW_KEY:
                String fontFamilyKey = null;
                String fontFamilyKeyTypeParsed = null;
                if (!TextUtils.isEmpty(component.getFontFamilyKey())) {
                    String[] fontFamilyKeyArr = component.getFontFamilyKey().split("-");
                    if (fontFamilyKeyArr.length == 2) {
                        fontFamilyKey = fontFamilyKeyArr[0];
                        fontFamilyKeyTypeParsed = fontFamilyKeyArr[1];
                    }
                }

                int fontFamilyKeyType = Typeface.NORMAL;
                AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(fontFamilyKeyTypeParsed);
                if (fontWeight == AppCMSUIKeyType.PAGE_TEXT_BOLD_KEY ||
                        fontWeight == AppCMSUIKeyType.PAGE_TEXT_SEMIBOLD_KEY ||
                        fontWeight == AppCMSUIKeyType.PAGE_TEXT_EXTRABOLD_KEY) {
                    fontFamilyKeyType = Typeface.BOLD;
                }

                String fontFamilyValue = null;
                String fontFamilyValueTypeParsed = null;
                if (!TextUtils.isEmpty(component.getFontFamilyValue())) {
                    String[] fontFamilyValueArr = component.getFontFamilyValue().split("-");
                    if (fontFamilyValueArr.length == 2) {
                        fontFamilyValue = fontFamilyValueArr[0];
                        fontFamilyValueTypeParsed = fontFamilyValueArr[1];
                    }
                }

                int fontFamilyValueType = Typeface.NORMAL;
                fontWeight = jsonValueKeyMap.get(fontFamilyValueTypeParsed);

                if (fontWeight == AppCMSUIKeyType.PAGE_TEXT_BOLD_KEY ||
                        fontWeight == AppCMSUIKeyType.PAGE_TEXT_SEMIBOLD_KEY ||
                        fontWeight == AppCMSUIKeyType.PAGE_TEXT_EXTRABOLD_KEY) {
                    fontFamilyValueType = Typeface.BOLD;
                }

                textColor = Color.parseColor(getColor(context, appCMSPresenter.getAppCMSMain()
                        .getBrand().getGeneral().getTextColor()));

                String directorTitle = null;
                StringBuilder directorListSb = new StringBuilder();
                String starringTitle = null;
                StringBuilder starringListSb = new StringBuilder();

                if (moduleAPI.getContentData() != null &&
                        !moduleAPI.getContentData().isEmpty() &&
                        moduleAPI.getContentData().get(0) != null &&
                        moduleAPI.getContentData().get(0).getCreditBlocks() != null) {
                    for (CreditBlock creditBlock : moduleAPI.getContentData().get(0).getCreditBlocks()) {
                        AppCMSUIKeyType creditBlockType = jsonValueKeyMap.get(creditBlock.getTitle());
                        if (creditBlockType != null &&
                                (creditBlockType == AppCMSUIKeyType.PAGE_VIDEO_CREDITS_DIRECTEDBY_KEY ||
                                        creditBlockType == AppCMSUIKeyType.PAGE_VIDEO_CREDITS_DIRECTOR_KEY ||
                                        creditBlockType == AppCMSUIKeyType.PAGE_VIDEO_CREDITS_DIRECTORS_KEY)) {
                            if (!TextUtils.isEmpty(creditBlock.getTitle())) {
                                directorTitle = creditBlock.getTitle().toUpperCase();
                            }
                            if (creditBlock != null && creditBlock.getCredits() != null) {
                                for (int i = 0; i < creditBlock.getCredits().size(); i++) {
                                    directorListSb.append(creditBlock.getCredits().get(i).getTitle());
                                    if (i < creditBlock.getCredits().size() - 1) {
                                        directorListSb.append(", ");
                                    }
                                }
                            }
                        } else if (creditBlockType != null &&
                                creditBlockType == AppCMSUIKeyType.PAGE_VIDEO_CREDITS_STARRING_KEY) {
                            if (!TextUtils.isEmpty(creditBlock.getTitle())) {
                                starringTitle = creditBlock.getTitle().toUpperCase();
                            }
                            if (creditBlock != null && creditBlock.getCredits() != null) {
                                for (int i = 0; i < creditBlock.getCredits().size(); i++) {
                                    if (!TextUtils.isEmpty(creditBlock.getCredits().get(i).getTitle())) {
                                        starringListSb.append(creditBlock.getCredits().get(i).getTitle());
                                        if (i < creditBlock.getCredits().size() - 1) {
                                            starringListSb.append(", ");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (directorListSb.length() == 0 && starringListSb.length() == 0) {
                    if (!BaseView.isLandscape(context)) {
                        componentViewResult.shouldHideComponent = true;
                    }
                }

                componentViewResult.componentView = new CreditBlocksView(context,
                        fontFamilyKey,
                        fontFamilyKeyType,
                        fontFamilyValue,
                        fontFamilyValueType,
                        directorTitle,
                        directorListSb.toString(),
                        starringTitle,
                        starringListSb.toString(),
                        textColor,
                        BaseView.getFontSizeKey(context, component.getLayout()),
                        BaseView.getFontSizeValue(context, component.getLayout()));

                if (!BaseView.isTablet(context)
                        && jsonValueKeyMap.get(moduleAPI.getModuleType())
                        == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY) {
                    componentViewResult.componentView.setVisibility(View.GONE);
                }
                break;

            case PAGE_TEXTFIELD_KEY:
                componentViewResult.componentView = new TextInputLayout(context);
                TextInputEditText textInputEditText = new TextInputEditText(context);
                switch (componentKey) {
                    case PAGE_EMAILTEXTFIELD_KEY:
                    case PAGE_EMAILTEXTFIELD2_KEY:
                        textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;

                    case PAGE_PASSWORDTEXTFIELD_KEY:
                    case PAGE_PASSWORDTEXTFIELD2_KEY:
                        textInputEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        ((TextInputLayout) componentViewResult.componentView).setPasswordVisibilityToggleEnabled(true);
                        break;

                    case PAGE_MOBILETEXTFIELD_KEY:
                        textInputEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;

                    default:
                        break;
                }

                if (!TextUtils.isEmpty(component.getText())) {
                    textInputEditText.setHint(component.getText());
                }
                textInputEditText.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                textInputEditText.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                setTypeFace(context, jsonValueKeyMap, component, textInputEditText);
                int loginInputHorizontalMargin = context.getResources().getInteger(
                        R.integer.app_cms_login_input_horizontal_margin);
                textInputEditText.setPadding(loginInputHorizontalMargin,
                        0,
                        loginInputHorizontalMargin,
                        0);
                textInputEditText.setTextSize(context.getResources().getInteger(R.integer.app_cms_login_input_textsize));
                TextInputLayout.LayoutParams textInputEditTextLayoutParams =
                        new TextInputLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
                textInputEditText.setLayoutParams(textInputEditTextLayoutParams);

                ((TextInputLayout) componentViewResult.componentView).addView(textInputEditText);

                ((TextInputLayout) componentViewResult.componentView).setHintEnabled(false);
                break;

            case PAGE_VIDEO_STARRATING_KEY:
            case PAGE_AUTOPLAY_MOVIE_STAR_RATING_KEY:
                int starColor = Color.parseColor(getColor(context, appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor()));

                float starRating = 0.0f;
                if (moduleAPI.getContentData() != null &&
                        !moduleAPI.getContentData().isEmpty() &&
                        moduleAPI.getContentData().get(0) != null &&
                        moduleAPI.getContentData().get(0).getGist() != null) {
                    if (moduleAPI.getContentData().get(0).getGist().getAverageStarRating() != 0f) {
                        starRating = moduleAPI.getContentData().get(0).getGist().getAverageStarRating();
                    }
                }
                componentViewResult.componentView = new StarRating(context,
                        starColor,
                        starColor,
                        starRating);
                break;

            case PAGE_PLAN_META_DATA_VIEW_KEY:
                componentViewResult.componentView = new SubscriptionMetaDataView(context,
                        component,
                        component.getLayout(),
                        this,
                        moduleAPI,
                        jsonValueKeyMap,
                        appCMSPresenter,
                        settings);
                break;

            case PAGE_SETTINGS_KEY:
                componentViewResult.componentView = createModuleView(context,
                        component,
                        moduleAPI,
                        pageView,
                        jsonValueKeyMap,
                        appCMSPresenter);
                break;

            case PAGE_TOGGLE_BUTTON_KEY:
                componentViewResult.componentView = new Switch(context);
                ((Switch) componentViewResult.componentView).getTrackDrawable().setTint(Color.parseColor(
                        appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()));
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    ((Switch) componentViewResult.componentView).setTrackTintMode(PorterDuff.Mode.MULTIPLY);
                }

                if (componentKey == AppCMSUIKeyType.PAGE_AUTOPLAY_TOGGLE_BUTTON_KEY) {
                    ((Switch) componentViewResult.componentView)
                            .setChecked(appCMSPresenter.getAutoplayEnabledUserPref(context));
                    ((Switch) componentViewResult.componentView)
                            .setOnCheckedChangeListener((buttonView, isChecked)
                                    -> appCMSPresenter.setAutoplayEnabledUserPref(context, isChecked));
                }

                if (componentKey == AppCMSUIKeyType.PAGE_CLOSED_CAPTIONS_TOGGLE_BUTTON_KEY) {
                    ((Switch) componentViewResult.componentView)
                            .setChecked(appCMSPresenter.getClosedCaptionPreference(context));
                    ((Switch) componentViewResult.componentView)
                            .setOnCheckedChangeListener((buttonView, isChecked)
                                    -> appCMSPresenter.setClosedCaptionPreference(context, isChecked));
                }
                break;

            default:
                break;
        }

        if (pageView != null) {
            pageView.addViewWithComponentId(new ViewWithComponentId.Builder()
                    .id(moduleAPI.getId() + component.getKey())
                    .view(componentViewResult.componentView)
                    .build());
        }
    }

    private String getColor(Context context, String color) {
        if (color.indexOf(context.getString(R.string.color_hash_prefix)) != 0) {
            return context.getString(R.string.color_hash_prefix) + color;
        }
        return color;
    }

    private Module matchModuleAPIToModuleUI(ModuleList module, AppCMSPageAPI appCMSPageAPI,
                                            Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
        if (appCMSPageAPI != null && appCMSPageAPI.getModules() != null) {
            if (jsonValueKeyMap.get(module.getView()) != null) {
                switch (jsonValueKeyMap.get(module.getView())) {
                    case PAGE_HISTORY_MODULE_KEY:
                    case PAGE_WATCHLIST_MODULE_KEY:
                    case PAGE_AUTOPLAY_MODULE_KEY:
                    case PAGE_DOWNLOAD_SETTING_MODULE_KEY:
                    case PAGE_DOWNLOAD_MODULE_KEY:
                        if (appCMSPageAPI.getModules() != null
                                && !appCMSPageAPI.getModules().isEmpty()) {
                            return appCMSPageAPI.getModules().get(0);
                        }
                        break;

                    default:
                        break;
                }
            }

            for (Module moduleAPI : appCMSPageAPI.getModules()) {
                if (module.getId().equals(moduleAPI.getId())) {

                    return moduleAPI;
                } else if (jsonValueKeyMap.get(module.getType()) != null &&
                        jsonValueKeyMap.get(moduleAPI.getModuleType()) != null &&
                        jsonValueKeyMap.get(module.getType()) ==
                                jsonValueKeyMap.get(moduleAPI.getModuleType())) {
                    return moduleAPI;
                }
            }
        }
        return null;
    }

    private void applyBorderToComponent(Context context, View view, Component component, int forcedColor) {
        if (component.getBorderWidth() != 0 && component.getBorderColor() != null) {
            if (component.getBorderWidth() > 0 && !TextUtils.isEmpty(component.getBorderColor())) {
                GradientDrawable viewBorder = new GradientDrawable();
                viewBorder.setShape(GradientDrawable.RECTANGLE);
                if (forcedColor == -1) {
                    viewBorder.setStroke(component.getBorderWidth(),
                            Color.parseColor(getColor(context, component.getBorderColor())));
                } else {
                    viewBorder.setStroke(4, forcedColor);
                }
                viewBorder.setColor(ContextCompat.getColor(context, android.R.color.transparent));
                view.setBackground(viewBorder);
            }
        }
    }

    private void setTypeFace(Context context,
                             Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                             Component component,
                             TextView textView) {
        if (jsonValueKeyMap.get(component.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY) {
            AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(component.getFontWeight());
            if (fontWeight == null) {
                fontWeight = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            Typeface face;
            switch (fontWeight) {
                case PAGE_TEXT_BOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.opensans_bold_ttf));
                    break;

                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.opensans_semibold_ttf));
                    break;

                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.opensans_extrabold_ttf));
                    break;

                default:
                    face = Typeface.createFromAsset(context.getAssets(),
                            context.getString(R.string.opensans_regular_ttf));
                    break;
            }
            textView.setTypeface(face);
        }
    }

    private enum AdjustOtherState {
        IGNORE,
        INITIATED,
        ADJUST_OTHERS
    }

    public static class ComponentViewResult {
        View componentView;
        OnInternalEvent onInternalEvent;
        boolean useMarginsAsPercentagesOverride;
        boolean useWidthOfScreen;
        boolean shouldHideModule;
        boolean addToPageView;
        boolean shouldHideComponent;
    }

    public static class UpdateImageIconAction implements Action1<UserVideoStatusResponse> {
        private final ImageButton imageButton;
        private final AppCMSPresenter appCMSPresenter;
        private final String filmId;

        private View.OnClickListener addClickListener;
        private View.OnClickListener removeClickListener;

        public UpdateImageIconAction(ImageButton imageButton, AppCMSPresenter presenter,
                                     String filmId) {
            this.imageButton = imageButton;
            this.appCMSPresenter = presenter;
            this.filmId = filmId;

            addClickListener = v -> {
                if (appCMSPresenter.isUserLoggedIn(UpdateImageIconAction.this.imageButton.getContext())) {

                    appCMSPresenter.editWatchlist(UpdateImageIconAction.this.filmId,
                            addToWatchlistResult -> {
                                UpdateImageIconAction.this.imageButton.setImageResource(
                                        R.drawable.remove_from_watchlist);
                                UpdateImageIconAction.this.imageButton.setOnClickListener(removeClickListener);
                            }, true);
                } else {
                    if (appCMSPresenter.isAppSVOD() &&
                            appCMSPresenter.isUserLoggedIn(UpdateImageIconAction.this.imageButton.getContext())) {
                        appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.SUBSCRIPTION_REQUIRED);
                    } else {
                        appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_REQUIRED);
                    }
                }
            };
            removeClickListener = v -> appCMSPresenter.editWatchlist(UpdateImageIconAction.this.filmId,
                    addToWatchlistResult -> {
                        UpdateImageIconAction.this.imageButton.setImageResource(
                                R.drawable.add_to_watchlist);
                        UpdateImageIconAction.this.imageButton.setOnClickListener(addClickListener);
                    }, false);
        }

        @Override
        public void call(final UserVideoStatusResponse userVideoStatusResponse) {
            if (userVideoStatusResponse != null) {
                if (userVideoStatusResponse.getQueued()) {
                    imageButton.setImageResource(R.drawable.remove_from_watchlist);
                    imageButton.setOnClickListener(removeClickListener);
                } else {
                    imageButton.setImageResource(R.drawable.add_to_watchlist);
                    imageButton.setOnClickListener(addClickListener);
                }
            } else {
                imageButton.setImageResource(R.drawable.add_to_watchlist);
                imageButton.setOnClickListener(addClickListener);
            }
        }
    }

    /**
     * This class has been created to updated the Download Image Action and Status
     */
    public static class UpdateDownloadImageIconAction implements Action1<UserVideoDownloadStatus> {
        private final ImageButton imageButton;
        private final AppCMSPresenter appCMSPresenter;
        private final ContentDatum contentDatum;
        private final String userId;

        private View.OnClickListener addClickListener;

        public UpdateDownloadImageIconAction(ImageButton imageButton, AppCMSPresenter presenter,
                                             ContentDatum contentDatum, String userId) {
            this.imageButton = imageButton;
            this.appCMSPresenter = presenter;
            this.contentDatum = contentDatum;
            this.userId = userId;

            addClickListener = v -> {
                if ((appCMSPresenter.isAppSVOD() && appCMSPresenter.isUserSubscribed(UpdateDownloadImageIconAction.this.imageButton.getContext())) ||
                        !appCMSPresenter.isAppSVOD() && appCMSPresenter.isUserLoggedIn(UpdateDownloadImageIconAction.this.imageButton.getContext())) {
                    if (appCMSPresenter.getUserDownloadQualityPref(UpdateDownloadImageIconAction.this.imageButton.getContext()) != null
                            && appCMSPresenter.getUserDownloadQualityPref(UpdateDownloadImageIconAction.this.imageButton.getContext()).length() > 0) {
                        appCMSPresenter.editDownload(UpdateDownloadImageIconAction.this.contentDatum, UpdateDownloadImageIconAction.this, true);
                    } else {
                        appCMSPresenter.showDownloadQualityScreen(UpdateDownloadImageIconAction.this.contentDatum, UpdateDownloadImageIconAction.this);
                    }
                } else {
                    if (appCMSPresenter.isAppSVOD()) {
                        if (appCMSPresenter.isUserLoggedIn(UpdateDownloadImageIconAction.this.imageButton.getContext())) {
                            appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.SUBSCRIPTION_REQUIRED);
                        } else {
                            appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED);
                        }
                    } else if (!(appCMSPresenter.isAppSVOD() && appCMSPresenter.isUserLoggedIn(UpdateDownloadImageIconAction.this.imageButton.getContext()))) {
                        appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_REQUIRED);
                    }
                }
            };
        }

        @Override
        public void call(UserVideoDownloadStatus userVideoDownloadStatus) {
            if (userVideoDownloadStatus != null) {

                switch (userVideoDownloadStatus.getDownloadStatus()) {
                    case STATUS_FAILED:
                        appCMSPresenter.startNextDownload();
                        break;

                    case STATUS_PAUSED:
                        //
                        break;

                    case STATUS_PENDING:
                        appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false);
                        imageButton.setOnClickListener(null);
                        break;

                    case STATUS_RUNNING:
                        appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false);
                        imageButton.setOnClickListener(null);
                        break;

                    case STATUS_SUCCESSFUL:
                        appCMSPresenter.cancelDownloadIconTimerTask(); //Fix of SVFA-1621
                        imageButton.setImageResource(R.drawable.ic_downloaded);
                        imageButton.setOnClickListener(null);
                        appCMSPresenter.startNextDownload();
                        break;

                    default:
                        Log.d(TAG, "No download Status available ");
                        break;
                }

            } else {
                appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                        UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false);
                imageButton.setImageResource(R.drawable.ic_download);
                imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                int fillColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor());
                imageButton.getDrawable().setColorFilter(new PorterDuffColorFilter(fillColor, PorterDuff.Mode.MULTIPLY));
                imageButton.setOnClickListener(addClickListener);
            }
        }
    }
}
