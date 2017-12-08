package com.viewlift.views.customviews;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.CreditBlock;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.Mpeg;
import com.viewlift.models.data.appcms.api.Season_;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.models.data.appcms.downloads.UserVideoDownloadStatus;
import com.viewlift.models.data.appcms.history.UserVideoStatusResponse;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
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
import com.viewlift.views.adapters.AppCMSTraySeasonItemAdapter;
import com.viewlift.views.adapters.AppCMSViewAdapter;
import com.viewlift.views.customviews.exoplayerview.CustomPlaybackControlView;
import com.viewlift.views.utilities.CustomWebView;
import com.viewlift.views.utilities.ImageUtils;

import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.handlers.StyledTextHandler;
import net.nightwhistler.htmlspanner.handlers.attributes.AlignmentAttributeHandler;
import net.nightwhistler.htmlspanner.handlers.attributes.BorderAttributeHandler;
import net.nightwhistler.htmlspanner.handlers.attributes.StyleAttributeHandler;
import net.nightwhistler.htmlspanner.style.Style;

import org.htmlcleaner.TagNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

import static com.viewlift.models.network.utility.MainUtils.loadJsonFromAssets;

/*
 * Created by viewlift on 5/5/17.
 */

public class ViewCreator {
    private static final String TAG = "ViewCreator";
    private ComponentViewResult componentViewResult;

    private HtmlSpanner htmlSpanner;
    public static HashMap<String, CustomVideoPlayerView> playerViewCache = new HashMap<String, CustomVideoPlayerView>();
    CustomVideoPlayerView videoPlayerViewSingle;

    public ViewCreator() {
        htmlSpanner = new HtmlSpanner();
        htmlSpanner.unregisterHandler("p");
        Style paragraphStyle = new Style();
        TagNodeHandler pHandler = new BorderAttributeHandler(new StyleAttributeHandler
                (new AlignmentAttributeHandler(new EmptyPStyledTextHandler(paragraphStyle))));
        htmlSpanner.registerHandler("p", pHandler);
    }

    static void setViewWithShowSubtitle(Context context, ContentDatum data, View view) {
        int totalEpisodes = 0;
        List<Season_> seasons = data.getSeason();
        int numSeasons = seasons.size();
        for (int i = 0; i < numSeasons; i++) {
            if (seasons.get(i).getEpisodes() != null) {
                totalEpisodes += seasons.get(i).getEpisodes().size();
            }
        }

        StringBuilder subtitleSb = new StringBuilder(String.valueOf(totalEpisodes));
        subtitleSb.append(context.getString(R.string.blank_separator));
        subtitleSb.append(context.getResources().getQuantityString(R.plurals.episode_subtitle_text,
                totalEpisodes));

        String primaryCategory = data.getGist().getPrimaryCategory() != null ?
                data.getGist().getPrimaryCategory().getTitle() : null;

        subtitleSb.append(context.getString(R.string.text_separator));

        if (!TextUtils.isEmpty(primaryCategory)) {
            subtitleSb.append(primaryCategory.toUpperCase());
        }

        ((TextView) view).setText(subtitleSb.toString());
        view.setAlpha(0.6f);
    }

    static void setViewWithSubtitle(Context context, ContentDatum data, View view) {
        int numberOfViewsToBeSeparated = 0;
        long runtime = data.getGist().getRuntime();

        String year = data.getGist().getYear();
        String primaryCategory = data.getGist().getPrimaryCategory() != null ?
                data.getGist().getPrimaryCategory().getTitle() : null;

        if (!TextUtils.isEmpty(String.valueOf(data.getGist().getRuntime()))) {
            numberOfViewsToBeSeparated++;
        }

        if (!TextUtils.isEmpty(data.getGist().getYear())) {
            numberOfViewsToBeSeparated++;
        }

        if (data.getGist().getPrimaryCategory() != null) {
            if (!TextUtils.isEmpty(data.getGist().getPrimaryCategory().getTitle())) {
                numberOfViewsToBeSeparated++;
            }
        }
        boolean appendFirstSep = numberOfViewsToBeSeparated > 1;
        boolean appendSecondSep = numberOfViewsToBeSeparated == 3;

        StringBuilder infoText = new StringBuilder();

        if (runtime / 60L < 1) {
            infoText.append(runtime)
                    .append(" ")
                    .append(context.getString(R.string.runtime_seconds_abbreviation));
        } else if (runtime / 60L < 2) {
            infoText.append(runtime / 60L)
                    .append(" ")
                    .append(context.getString(R.string.runtime_minute_abbreviation));
        } else {
            infoText.append(runtime / 60L)
                    .append(" ")
                    .append(context.getString(R.string.runtime_minutes_abbreviation));
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

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private void refreshPageView(PageView pageView,
                                 Context context,
                                 AppCMSPageUI appCMSPageUI,
                                 AppCMSPageAPI appCMSPageAPI,
                                 AppCMSAndroidModules appCMSAndroidModules,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter,
                                 List<String> modulesToIgnore) {
        if (appCMSPageUI == null) {
            return;
        }
        for (ModuleList moduleInfo : appCMSPageUI.getModuleList()) {
            ModuleList module = null;
            try {
                module = appCMSAndroidModules.getModuleListMap().get(moduleInfo.getBlockName());
            } catch (Exception e) {

            }
            if (module == null) {
                module = moduleInfo;
            } else if (moduleInfo != null) {
                module.setId(moduleInfo.getId());
                module.setSettings(moduleInfo.getSettings());
                module.setSvod(moduleInfo.isSvod());
                module.setType(moduleInfo.getType());
                module.setView(moduleInfo.getView());
                module.setBlockName(moduleInfo.getBlockName());
            }
            boolean createModule = !modulesToIgnore.contains(module.getType()) && pageView != null;

            if (createModule && appCMSPresenter.isViewPlanPage(module.getId()) &&
                    (jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_CAROUSEL_MODULE_KEY || jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_BANNER_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_VIDEO_PLAYER_MODULE_KEY || jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_SHOW_PLAYER_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_TRAY_MODULE_KEY)) {
                createModule = false;
            }

            if (createModule) {
                ModuleView moduleView = pageView.getModuleViewWithModuleId(module.getId());
                boolean shouldHideModule = false;
                if (moduleView != null) {
                    moduleView.setVisibility(View.VISIBLE);

                    moduleView.resetHeightAdjusters();

                    Module moduleAPI = matchModuleAPIToModuleUI(module, appCMSPageAPI, jsonValueKeyMap);

                    boolean shouldHideComponent;

                    if (moduleAPI != null) {
                        updateUserHistory(appCMSPresenter,
                                moduleAPI.getContentData());

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
                                } else if (componentType == AppCMSUIKeyType.PAGE_VIDEO_PLAYER_VIEW_KEY) {

                                    String videoId = null;
                                    if (moduleAPI != null &&
                                            moduleAPI.getContentData().get(0) != null &&
                                            moduleAPI.getContentData().get(0).getGist() != null &&
                                            moduleAPI.getContentData().get(0).getGist().getId() != null) {
                                        videoId = moduleAPI.getContentData().get(0).getGist().getId();
                                        (view).setVisibility(View.VISIBLE);
                                    }
                                    if (playerViewCache.get(moduleAPI.getId() + component.getKey()) != null) {
                                        videoPlayerViewSingle = playerViewCache.get(moduleAPI.getId() + component.getKey());
                                    } else {
                                        videoPlayerViewSingle = null;
                                    }
                                    if (videoId != null) {
                                        ((FrameLayout) view).removeAllViews();
                                        if (videoPlayerViewSingle != null) {

                                            if (videoPlayerViewSingle.getParent() != null)
                                                ((ViewGroup) videoPlayerViewSingle.getParent()).removeView(videoPlayerViewSingle);
                                            videoPlayerViewSingle.resumePlayer();

//                                            if((appCMSPresenter.isconfig || appCMSPresenter.isAppBackground) && !TextUtils.isEmpty(appCMSPresenter.videoPlayerView.lastUrl)){
//                                                appCMSPresenter.videoPlayerView.resumePlayer();
//                                            }else{
//                                                appCMSPresenter.videoPlayerView.setVideoUri(videoId, R.string.loading_video_text);
//                                            }
                                            ((FrameLayout) view).addView(videoPlayerViewSingle);
                                        } else {
                                            videoPlayerViewSingle = playerView(context, videoId);
                                            ((FrameLayout) view).addView(videoPlayerViewSingle);
                                        }
                                        playerViewCache.put(moduleAPI.getId() + component.getKey(), videoPlayerViewSingle);

                                        appCMSPresenter.isconfig = false;
                                        (view).setId(R.id.video_player_id);


                                    }
                                } else if (componentType == AppCMSUIKeyType.PAGE_PROGRESS_VIEW_KEY) {
                                    if (appCMSPresenter.isUserLoggedIn()) {
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
                                                    long percentageWatched = (long) (((double) watchedTime / (double) runTime) * 100.0);
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
                                } else if (componentType == AppCMSUIKeyType.PAGE_WEB_VIEW_KEY) {

                                    String webViewUrl = "";
                                    if (moduleAPI != null && moduleAPI.getRawText() != null) {
                                        webViewUrl = moduleAPI.getRawText();
                                        view.setVisibility(View.VISIBLE);
                                    } else {
                                        view.setVisibility(View.INVISIBLE);
                                    }

                                    int height = ((int) component.getLayout().getMobile().getHeight()) - 55;
                                    int width = BaseView.getDeviceWidth();
                                    String html = "<iframe width=\"" + "100%" + "\" height=\"" + height + "px\" style=\"border: 0px solid #cccccc;\" src=\"" + webViewUrl + "\" ></iframe>";

                                    ((CustomWebView) view).setWebViewClient(new WebViewClient() {
                                        @Override
                                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                            Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                                            context.startActivity(browserIntent);
                                            return true;
                                        }

                                        @Override
                                        public void onPageFinished(WebView view, String url) {
                                            super.onPageFinished(view, url);
                                            ((CustomWebView) view).isPageLoaded = true;
                                        }
                                    });
                                    ((CustomWebView) view).loadData(html, "text/html", "UTF-8");
                                    (view).setVisibility(View.VISIBLE);
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
                                                    //Log.e(TAG, "Could not launch action: " +
//                                                            " permalink: " +
//                                                            moduleAPI.getContentData().get(0).getGist().getPermalink() +
//                                                            " action: " +
//                                                            component.getAction() +
//                                                            " hls URL: " +
//                                                            moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets().getHls());
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
                                                if (moduleAPI.getContentData() != null &&
                                                        !moduleAPI.getContentData().isEmpty() &&
                                                        moduleAPI.getContentData().get(0) != null &&
                                                        moduleAPI.getContentData().get(0).getContentDetails() != null) {

                                                    List<String> relatedVideoIds = null;
                                                    if (moduleAPI.getContentData().get(0).getContentDetails() != null &&
                                                            moduleAPI.getContentData().get(0).getContentDetails().getRelatedVideoIds() != null) {
                                                        relatedVideoIds = moduleAPI.getContentData().get(0).getContentDetails().getRelatedVideoIds();
                                                    }
                                                    int currentPlayingIndex = -1;
                                                    if (relatedVideoIds == null) {
                                                        currentPlayingIndex = 0;
                                                    }

                                                    appCMSPresenter.launchVideoPlayer(moduleAPI.getContentData().get(0),
                                                            currentPlayingIndex,
                                                            relatedVideoIds,
                                                            moduleAPI.getContentData().get(0).getGist().getWatchedTime(),
                                                            component.getAction());

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
                                                    //Log.e(TAG, "Could not launch action: " +
//                                                            " permalink: " +
//                                                            moduleAPI.getContentData().get(0).getGist().getPermalink() +
//                                                            " action: " +
//                                                            component.getAction() +
//                                                            " film URL: " +
//                                                            filmUrl.toString());
                                                }
                                            }
                                        });
                                    } else if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_DOWNLOAD_BUTTON_KEY
                                            && view != null) {
                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0).getGist() != null &&
                                                moduleAPI.getContentData().get(0).getGist().getId() != null) {
                                            String userId = appCMSPresenter.getLoggedInUser();
                                            appCMSPresenter.getUserVideoDownloadStatus(
                                                    moduleAPI.getContentData().get(0).getGist().getId(), new UpdateDownloadImageIconAction((ImageButton) view, appCMSPresenter,
                                                            moduleAPI.getContentData().get(0), userId), userId);

                                        }
                                        if (appCMSPresenter.getAppCMSMain().getFeatures() != null &&
                                                appCMSPresenter.getAppCMSMain().getFeatures().isMobileAppDownloads()) {
                                            view.setVisibility(View.VISIBLE);
                                        } else {
                                            view.setVisibility(View.GONE);
                                        }
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
                                        if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0) != null &&
                                                moduleAPI.getContentData().get(0).getSeason() != null) {

                                            setViewWithShowSubtitle(context,
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
                                            ((TextView) view).setText(parentalRating);
                                            boolean resizeText = parentalRating.length() > 2;
                                            if (component.getFontSize() > 0) {
                                                int fontSize = component.getFontSize();
                                                if (resizeText) {
                                                    if (BaseView.isTablet(context)) {
                                                        fontSize = (int) (0.6 * fontSize);
                                                    } else {
                                                        fontSize = (int) (0.8 * fontSize);
                                                    }
                                                }
                                                ((TextView) view).setTextSize(fontSize);
                                            } else if (BaseView.getFontSize(context, component.getLayout()) > 0) {
                                                int fontSize = (int) BaseView.getFontSize(context, component.getLayout());
                                                if (resizeText) {
                                                    if (BaseView.isTablet(context)) {
                                                        fontSize = (int) (0.6 * fontSize);
                                                    } else {
                                                        fontSize = (int) (0.8 * fontSize);
                                                    }
                                                }
                                                ((TextView) view).setTextSize(fontSize);
                                            }
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
                                                            false,
                                                            Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor()),
                                                            false);
                                            textVto.addOnGlobalLayoutListener(viewCreatorLayoutListener);
                                        }
                                    } else if (componentKey == AppCMSUIKeyType.PAGE_TRAY_TITLE_KEY) {
                                        if (view instanceof TextView) {
                                            if (!TextUtils.isEmpty(component.getText())) {
                                                ((TextView) view).setText(component.getText().toUpperCase());
                                            } else if (moduleAPI != null && moduleAPI.getSettings() != null && !moduleAPI.getSettings().getHideTitle() &&
                                                    !TextUtils.isEmpty(moduleAPI.getTitle())) {
                                                ((TextView) view).setText(moduleAPI.getTitle().toUpperCase());
                                            } else if (jsonValueKeyMap.get(module.getView()) == AppCMSUIKeyType.PAGE_WATCHLIST_MODULE_KEY) {
                                                ((TextView) view).setText(R.string.app_cms_page_watchlist_title);
                                            } else if (jsonValueKeyMap.get(module.getView()) == AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY) {
                                                ((TextView) view).setText(R.string.app_cms_page_download_title);
                                            } else if (jsonValueKeyMap.get(module.getView()) == AppCMSUIKeyType.PAGE_HISTORY_MODULE_KEY) {
                                                ((TextView) view).setText(R.string.app_cms_page_history_title);
                                            }
                                        }
                                    }
                                } else if (componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY) {
                                    if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY) {
                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty()) {
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
                                                        .into((ImageView) view);
                                            } else if (viewWidth > 0 && viewHeight > 0) {
                                                String videoImageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                                        moduleAPI.getContentData().get(0).getGist().getVideoImageUrl(),
                                                        viewWidth,
                                                        viewHeight);
                                                Glide.with(context)
                                                        .load(videoImageUrl)
                                                        .override(viewWidth, viewHeight)
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
                                            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                                        }
                                    }
                                } else if (componentKey == AppCMSUIKeyType.PAGE_SETTINGS_EDIT_PROFILE_KEY) {

                                    if (!TextUtils.isEmpty(appCMSPresenter.getFacebookAccessToken()) ||
                                            (!TextUtils.isEmpty(appCMSPresenter.getUserAuthProviderName()) &&
                                                    appCMSPresenter.getUserAuthProviderName().equalsIgnoreCase(context.getString(R.string.facebook_auth_provider_name_key)))) {
                                        view.setVisibility(View.GONE);
                                        shouldHideComponent = true;
                                    }

                                    if (!TextUtils.isEmpty(appCMSPresenter.getGoogleAccessToken()) ||
                                            (!TextUtils.isEmpty(appCMSPresenter.getUserAuthProviderName()) &&
                                                    appCMSPresenter.getUserAuthProviderName().equalsIgnoreCase(context.getString(R.string.google_auth_provider_name_key)))) {
                                        view.setVisibility(View.GONE);
                                        shouldHideComponent = true;
                                    }
                                } else if (componentKey == AppCMSUIKeyType.PAGE_SETTINGS_CHANGE_PASSWORD_KEY) {
                                    if (!TextUtils.isEmpty(appCMSPresenter.getFacebookAccessToken()) ||
                                            (!TextUtils.isEmpty(appCMSPresenter.getUserAuthProviderName()) &&
                                                    appCMSPresenter.getUserAuthProviderName().equalsIgnoreCase(context.getString(R.string.facebook_auth_provider_name_key)))) {
                                        view.setVisibility(View.GONE);
                                        shouldHideComponent = true;
                                    }

                                    if (!TextUtils.isEmpty(appCMSPresenter.getGoogleAccessToken()) ||
                                            (!TextUtils.isEmpty(appCMSPresenter.getUserAuthProviderName()) &&
                                                    appCMSPresenter.getUserAuthProviderName().equalsIgnoreCase(context.getString(R.string.google_auth_provider_name_key)))) {
                                        view.setVisibility(View.GONE);
                                        shouldHideComponent = true;
                                    }
                                } else if (componentType == AppCMSUIKeyType.PAGE_PAGE_CONTROL_VIEW_KEY) {
                                    if (view instanceof DotSelectorView) {
                                        ((DotSelectorView) view).select(0);
                                        int numDots = moduleAPI != null ? moduleAPI.getContentData() != null ? moduleAPI.getContentData().size() : 0 : 0;
                                        if (!((DotSelectorView) view).dotsInitialized()) {
                                            ((DotSelectorView) view).addDots(numDots);
                                            ((DotSelectorView) view).deSelectAll();
                                            ((DotSelectorView) view).select(0);
                                        }
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
                                                            if (creditBlock.getCredits().get(j).getTitle() != null && !TextUtils.isEmpty(creditBlock.getCredits().get(j).getTitle())) {
                                                                starringListSb.append(creditBlock.getCredits().get(j).getTitle());
                                                                if (j < creditBlock.getCredits().size() - 1) {
                                                                    starringListSb.append(", ");
                                                                }
                                                            }

                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (TextUtils.isEmpty(starringListSb)) {
                                            starringListSb.append("-");

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
                                        //Log.d(TAG, "checkForExistingSubscription() - 574");
                                        appCMSPresenter.checkForExistingSubscription(false);
                                        if (!appCMSPresenter.isAppSVOD() && component.isSvod()) {
                                            shouldHideComponent = true;
                                        } else {
                                            for (Component settingsComponent : component.getComponents()) {
                                                shouldHideComponent = false;

                                                AppCMSUIKeyType settingsComponentKey = jsonValueKeyMap.get(settingsComponent.getKey());

                                                if (settingsComponentKey == null) {
                                                    settingsComponentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                                                }

                                                View settingsView = pageView.findViewFromComponentId(module.getId()
                                                        + settingsComponent.getKey());

                                                String paymentProcessor = appCMSPresenter.getActiveSubscriptionProcessor();

                                                if (settingsView != null) {
                                                    if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_NAME_VALUE_KEY) {
                                                        ((TextView) settingsView).setText(appCMSPresenter.getLoggedInUserName());
                                                    } else if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_EMAIL_VALUE_KEY) {
                                                        ((TextView) settingsView).setText(appCMSPresenter.getLoggedInUserEmail());
                                                    } else if (TextUtils.isEmpty(appCMSPresenter.getLoggedInUserEmail())) {
                                                        settingsView.setVisibility(View.GONE);
                                                    } else {
                                                        if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_PLAN_PROCESSOR_TITLE_KEY) {
                                                            if (appCMSPresenter.isUserSubscribed() &&
                                                                    !TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionProcessor())) {
                                                                settingsView.setVisibility(View.VISIBLE);
                                                            } else {
                                                                settingsView.setVisibility(View.GONE);
                                                                shouldHideComponent = true;
                                                            }
                                                        } else if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_PLAN_VALUE_KEY) {
                                                            if (appCMSPresenter.isUserSubscribed() &&
                                                                    !TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionPlanName())) {
                                                                ((TextView) settingsView).setText(appCMSPresenter.getActiveSubscriptionPlanName());
                                                            } else if (!appCMSPresenter.isUserSubscribed()) {
                                                                ((TextView) settingsView).setText(context.getString(R.string.subscription_unsubscribed_plan_value));
                                                            }
                                                        } else if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_PLAN_PROCESSOR_VALUE_KEY) {
                                                            if (paymentProcessor != null && appCMSPresenter.isUserSubscribed()) {
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
                                                            ((TextView) settingsView).setText(appCMSPresenter.getUserDownloadQualityPref());
                                                        } else if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_UPGRADE_PLAN_PROFILE_KEY) {
                                                            if (!appCMSPresenter.isUserSubscribed()) {
                                                                ((TextView) settingsView).setText(context.getString(R.string.app_cms_page_upgrade_subscribe_button_text));
                                                            } else if (!TextUtils.isEmpty(component.getText())) {
                                                                ((TextView) settingsView).setText(component.getText());
                                                                if (!appCMSPresenter.upgradesAvailableForUser()) {
                                                                    settingsView.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        } else if (settingsComponentKey == AppCMSUIKeyType.PAGE_SETTINGS_CANCEL_PLAN_PROFILE_KEY) {
                                                            if (appCMSPresenter.isUserSubscribed()) {
                                                                //Log.d(TAG, "checkForExistingSubscription() - 647");
                                                                appCMSPresenter.checkForExistingSubscription(false);

                                                                if (!appCMSPresenter.isExistingGooglePlaySubscriptionSuspended() &&
                                                                        appCMSPresenter.isSubscriptionCompleted()) {
                                                                    settingsView.setVisibility(View.VISIBLE);
                                                                } else {
                                                                    settingsView.setVisibility(View.GONE);
                                                                }
                                                            } else {
                                                                settingsView.setVisibility(View.GONE);
                                                            }
                                                        }
                                                    }
                                                    settingsView.requestLayout();
                                                }
                                            }
                                        }
                                    } else if (componentType == AppCMSUIKeyType.PAGE_TOGGLE_BUTTON_KEY) {
                                        switch (componentType) {
                                            case PAGE_AUTOPLAY_TOGGLE_BUTTON_KEY:
                                                ((Switch) view).setChecked(appCMSPresenter
                                                        .getAutoplayEnabledUserPref(context));
                                                break;

                                            case PAGE_SD_CARD_FOR_DOWNLOADS_TOGGLE_BUTTON_KEY:
                                                ((Switch) view).setChecked(appCMSPresenter
                                                        .getUserDownloadLocationPref());
                                                if (appCMSPresenter.isExternalStorageAvailable()) {
                                                    componentViewResult.componentView.setEnabled(true);
                                                    appCMSPresenter.setUserDownloadLocationPref(true);
                                                } else {
                                                    componentViewResult.componentView.setEnabled(false);
                                                    ((Switch) componentViewResult.componentView).setChecked(false);
                                                    appCMSPresenter.setUserDownloadLocationPref(false);
                                                }

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
                                                    (int) (component.getLayout().getTabletLandscape().getHeight() * 0.6);
                                            heightLayoutAdjuster.topMargin =
                                                    (int) component.getLayout().getTabletLandscape().getTopMargin();
                                            heightLayoutAdjuster.yAxis =
                                                    (int) component.getLayout().getTabletLandscape().getYAxis();
                                            heightLayoutAdjuster.component = component;
                                        } else {
                                            heightLayoutAdjuster.heightAdjustment =
                                                    (int) (component.getLayout().getTabletPortrait().getHeight() * 0.8);
                                            heightLayoutAdjuster.topMargin =
                                                    (int) component.getLayout().getTabletPortrait().getTopMargin();
                                            heightLayoutAdjuster.yAxis =
                                                    (int) component.getLayout().getTabletPortrait().getYAxis();
                                            heightLayoutAdjuster.component = component;
                                        }
                                    } else {
                                        heightLayoutAdjuster.heightAdjustment =
                                                (int) (component.getLayout().getMobile().getHeight() * 0.6);
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
                    } else {
                        moduleView.setVisibility(View.GONE);
                        shouldHideModule = true;
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
                                    } else if (childComponentAndView.component.getLayout().getTabletLandscape().getTopMargin() > 0 &&
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
                                    } else if (childComponentAndView.component.getLayout().getTabletPortrait().getTopMargin() > 0 &&
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
                                } else if (childComponentAndView.component.getLayout().getMobile().getTopMargin() > 0 &&
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
        if (pageView != null) {
            forceRedrawOfAllChildren(pageView);
        }
    }

    private void forceRedrawOfAllChildren(ViewGroup viewGroup) {
        viewGroup.requestLayout();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                forceRedrawOfAllChildren((ViewGroup) v);
            } else {
                v.requestLayout();
            }
        }
    }

    public PageView generatePage(Context context,
                                 AppCMSPageUI appCMSPageUI,
                                 AppCMSPageAPI appCMSPageAPI,
                                 AppCMSAndroidModules appCMSAndroidModules,
                                 String screenName,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter,
                                 List<String> modulesToIgnore) {
        if (appCMSPageUI == null) {
            return null;
        }

        PageView pageView = null;
        try {
            if (appCMSPresenter.isPageAVideoPage(screenName)) {
                pageView = appCMSPresenter.getPageViewLruCache().get(screenName + BaseView.isLandscape(context));
            } else {
                pageView = appCMSPresenter.getPageViewLruCache().get(screenName
                        + BaseView.isLandscape(context));
            }
        } catch (Exception e) {

        }

        boolean newView = false;

        if (pageView != null) {
            String oldVersion = pageView.getAppCMSPageUI().getVersion();
            String newVersion = appCMSPageUI.getVersion();
            if (!TextUtils.isEmpty(oldVersion) && !oldVersion.equals(newVersion)) {
                pageView = null;
            }
        }

        if (pageView == null || pageView.getContext() != context) {
            pageView = new PageView(context, appCMSPageUI, appCMSPresenter);
            pageView.setUserLoggedIn(appCMSPresenter.isUserLoggedIn());
            if (appCMSPresenter.isPageAVideoPage(screenName)) {
                appCMSPresenter.getPageViewLruCache().put(screenName + BaseView.isLandscape(context), pageView);
            } else {
                appCMSPresenter.getPageViewLruCache().put(screenName
                        + BaseView.isLandscape(context), pageView);
            }
            newView = true;
        }

        if (newView ||
                (!appCMSPresenter.isPagePrimary(screenName) && !appCMSPresenter.isPageAVideoPage(screenName)) ||
                appCMSPresenter.isUserLoggedIn() != pageView.isUserLoggedIn()) {
            pageView.setUserLoggedIn(appCMSPresenter.isUserLoggedIn());
            pageView.removeAllAddOnViews();
            pageView.getChildrenContainer().removeAllViews();
            componentViewResult = new ComponentViewResult();
            createPageView(context,
                    appCMSPageUI,
                    appCMSPageAPI,
                    appCMSAndroidModules,
                    pageView,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    modulesToIgnore);
        } else {
            refreshPageView(pageView,
                    context,
                    appCMSPageUI,
                    appCMSPageAPI,
                    appCMSAndroidModules,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    modulesToIgnore);
        }
        pageView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        return pageView;
    }

    ComponentViewResult getComponentViewResult() {
        return componentViewResult;
    }

    private void createPageView(Context context,
                                AppCMSPageUI appCMSPageUI,
                                AppCMSPageAPI appCMSPageAPI,
                                AppCMSAndroidModules appCMSAndroidModules,
                                PageView pageView,
                                Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                AppCMSPresenter appCMSPresenter,
                                List<String> modulesToIgnore) {
        appCMSPresenter.clearOnInternalEvents();
        pageView.clearExistingViewLists();
        List<ModuleList> modulesList = appCMSPageUI.getModuleList();
        ViewGroup childrenContainer = pageView.getChildrenContainer();


        for (ModuleList moduleInfo : modulesList) {
            ModuleList module = null;
            try {
                module = appCMSAndroidModules.getModuleListMap().get(moduleInfo.getBlockName());
            } catch (Exception e) {

            }
            if (module == null) {
                module = moduleInfo;
            } else if (moduleInfo != null) {
                module.setId(moduleInfo.getId());
                module.setSettings(moduleInfo.getSettings());
                module.setSvod(moduleInfo.isSvod());
                module.setType(moduleInfo.getType());
                module.setView(moduleInfo.getView());
                module.setBlockName(moduleInfo.getBlockName());
            }


            boolean createModule = !modulesToIgnore.contains(module.getType());

            if (appCMSPageAPI != null && createModule && appCMSPresenter.isViewPlanPage(appCMSPageAPI.getId()) &&
                    (jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_CAROUSEL_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_TRAY_MODULE_KEY || jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_BANNER_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_VIDEO_PLAYER_MODULE_KEY || jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_SHOW_PLAYER_MODULE_KEY)) {
                createModule = false;
            }

            if (createModule) {
                if (appCMSPageAPI != null && appCMSPresenter.isViewPlanPage(appCMSPageAPI.getId()) &&
                        jsonValueKeyMap.get(module.getType()) != AppCMSUIKeyType.PAGE_CAROUSEL_MODULE_KEY &&
                        jsonValueKeyMap.get(module.getType()) != AppCMSUIKeyType.PAGE_TRAY_MODULE_KEY) {
                }

                Module moduleAPI = matchModuleAPIToModuleUI(module, appCMSPageAPI, jsonValueKeyMap);
                View childView = createModuleView(context, module, moduleAPI,
                        appCMSAndroidModules,
                        pageView,
                        jsonValueKeyMap,
                        appCMSPresenter);
            }
        }
        pageView.notifyAdapterDataSetChanged();

        List<OnInternalEvent> presenterOnInternalEvents = appCMSPresenter.getOnInternalEvents();
        if (presenterOnInternalEvents != null) {
            for (OnInternalEvent onInternalEvent : presenterOnInternalEvents) {
                for (OnInternalEvent receiverInternalEvent : presenterOnInternalEvents) {
                    if (receiverInternalEvent != onInternalEvent) {
                        if (!TextUtils.isEmpty(onInternalEvent.getModuleId()) &&
                                onInternalEvent.getModuleId().equals(receiverInternalEvent.getModuleId())) {
                            onInternalEvent.addReceiver(receiverInternalEvent);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private <T extends ModuleWithComponents> View createModuleView(final Context context,
                                                                   final T module,
                                                                   final Module moduleAPI,
                                                                   AppCMSAndroidModules appCMSAndroidModules,
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
                    this,
                    appCMSAndroidModules);
            pageView.addModuleViewWithModuleId(module.getId(), moduleView);
        } else {
            if (module.getComponents() != null) {
                moduleView = new ModuleView<>(context, module, true);
                ViewGroup childrenContainer = moduleView.getChildrenContainer();
                boolean hideModule = false;
                boolean modulesHasHiddenComponent = false;

                AdjustOtherState adjustOthers = AdjustOtherState.IGNORE;
                pageView.addModuleViewWithModuleId(module.getId(), moduleView);
                if (module.getComponents() != null) {
                    if (moduleAPI != null) {
                        updateUserHistory(appCMSPresenter,
                                moduleAPI.getContentData());
                    }
                    System.out.println("\n\n new Plan " + new Gson().toJson(moduleAPI));
                    int size = module.getComponents().size();
                    for (int i = 0; i < size; i++) {
                        Component component = module.getComponents().get(i);

                        createComponentView(context,
                                component,
                                module.getLayout(),
                                moduleAPI,
                                appCMSAndroidModules,
                                pageView,
                                module.getSettings(),
                                jsonValueKeyMap,
                                appCMSPresenter,
                                false,
                                module.getView(),
                                module.getId());

                        if (adjustOthers == AdjustOtherState.INITIATED) {
                            adjustOthers = AdjustOtherState.ADJUST_OTHERS;
                        }

                        if (!appCMSPresenter.isAppSVOD() && component.isSvod()) {
                            componentViewResult.shouldHideComponent = true;
                            if (componentViewResult.componentView != null) {
                                componentViewResult.componentView.setVisibility(View.GONE);
                            }
                            adjustOthers = AdjustOtherState.INITIATED;
                        } else if (!appCMSPresenter.isAppSVOD() && jsonValueKeyMap.get(component.getKey()) != null &&
                                jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_USER_MANAGEMENT_DOWNLOADS_MODULE_KEY
                                && appCMSPresenter.getAppCMSMain().getFeatures() != null &&
                                !appCMSPresenter.getAppCMSMain().getFeatures().isMobileAppDownloads()) {
                            componentViewResult.shouldHideComponent = true;
                            if (componentViewResult.componentView != null) {
                                componentViewResult.componentView.setVisibility(View.GONE);
                            }
                            adjustOthers = AdjustOtherState.INITIATED;
                        }

                        if (componentViewResult.shouldHideModule) {
                            hideModule = true;
                        }

                        if (componentViewResult.onInternalEvent != null) {
                            appCMSPresenter.addInternalEvent(componentViewResult.onInternalEvent);
                        }

                        if (componentViewResult.shouldHideComponent) {
                            ModuleView.HeightLayoutAdjuster heightLayoutAdjuster = new ModuleView.HeightLayoutAdjuster();
                            modulesHasHiddenComponent = true;
                            if (BaseView.isTablet(context)) {
                                if (BaseView.isLandscape(context)) {
                                    heightLayoutAdjuster.heightAdjustment =
                                            (int) (component.getLayout().getTabletLandscape().getHeight() * 0.6);
                                    heightLayoutAdjuster.topMargin =
                                            (int) component.getLayout().getTabletLandscape().getTopMargin();
                                    heightLayoutAdjuster.yAxis =
                                            (int) component.getLayout().getTabletLandscape().getYAxis();
                                    heightLayoutAdjuster.component = component;
                                } else {
                                    heightLayoutAdjuster.heightAdjustment =
                                            (int) (component.getLayout().getTabletPortrait().getHeight() * 0.8);
                                    heightLayoutAdjuster.topMargin =
                                            (int) component.getLayout().getTabletPortrait().getTopMargin();
                                    heightLayoutAdjuster.yAxis =
                                            (int) component.getLayout().getTabletPortrait().getYAxis();
                                    heightLayoutAdjuster.component = component;
                                }
                            } else {
                                heightLayoutAdjuster.heightAdjustment =
                                        (int) (component.getLayout().getMobile().getHeight() * 0.6);
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
                                if ((adjustOthers == AdjustOtherState.IGNORE &&
                                        componentViewResult.shouldHideComponent) ||
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
                        List childComponentAndViewList = moduleView.getChildComponentAndViewList();

                        int componentViewListSize = childComponentAndViewList.size();
                        for (int j = 0; j < componentViewListSize; j++) {
                            ModuleView.ChildComponentAndView childComponentAndView = (ModuleView.ChildComponentAndView) childComponentAndViewList.get(j);

                            ViewGroup.MarginLayoutParams childLayoutParams =
                                    (ViewGroup.MarginLayoutParams) childComponentAndView.childView.getLayoutParams();
                            if (BaseView.isTablet(context)) {
                                if (BaseView.isLandscape(context)) {
                                    if (childComponentAndView.component.getLayout().getTabletLandscape().getYAxis() > 0 &&
                                            heightLayoutAdjuster.yAxis <
                                                    childComponentAndView.component.getLayout().getTabletLandscape().getYAxis()) {
                                        childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                    } else if (childComponentAndView.component.getLayout().getTabletLandscape().getTopMargin() > 0 &&
                                            heightLayoutAdjuster.topMargin <
                                                    childComponentAndView.component.getLayout().getTabletLandscape().getTopMargin()) {
                                        childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                    }
                                } else {
                                    if (childComponentAndView.component.getLayout().getTabletPortrait().getYAxis() > 0 &&
                                            heightLayoutAdjuster.yAxis <
                                                    childComponentAndView.component.getLayout().getTabletPortrait().getYAxis()) {
                                        childLayoutParams.topMargin -= BaseView.convertDpToPixel(heightLayoutAdjuster.heightAdjustment, context);
                                    } else if (childComponentAndView.component.getLayout().getTabletPortrait().getTopMargin() > 0 &&
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
                                } else if (childComponentAndView.component.getLayout().getMobile().getTopMargin() > 0 &&
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
        }
        if (moduleView != null) {
            moduleView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }
        return moduleView;
    }

    private void updateUserHistory(AppCMSPresenter appCMSPresenter,
                                   List<ContentDatum> contentData) {
        try {
            int contentDatumLength = contentData.size();
            for (int i = 0; i < contentDatumLength; i++) {
                ContentDatum currentContentDatum = contentData.get(i);
                ContentDatum userHistoryContentDatum = appCMSPresenter.getUserHistoryContentDatum(contentData.get(i).getGist().getId());
                if (userHistoryContentDatum != null) {
                    currentContentDatum.getGist().setWatchedTime(userHistoryContentDatum.getGist().getWatchedTime());
                }
            }
        } catch (Exception e) {
            //
        }
    }

    public CollectionGridItemView createCollectionGridItemView(final Context context,
                                                               final Layout parentLayout,
                                                               final boolean useParentLayout,
                                                               final Component component,
                                                               final AppCMSPresenter appCMSPresenter,
                                                               final Module moduleAPI,
                                                               final AppCMSAndroidModules appCMSAndroidModules,
                                                               Settings settings,
                                                               Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                                               int defaultWidth,
                                                               int defaultHeight,
                                                               boolean useMarginsAsPercentages,
                                                               boolean gridElement,
                                                               String viewType,
                                                               boolean createMultipleContainersForChildren,
                                                               boolean createRoundedCorners) {
        CollectionGridItemView collectionGridItemView = new CollectionGridItemView(context,
                parentLayout,
                useParentLayout,
                component,
                defaultWidth,
                defaultHeight,
                createMultipleContainersForChildren,
                createRoundedCorners);

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        List<OnInternalEvent> onInternalEvents = new ArrayList<>();

        int size = component.getComponents().size();
        for (int i = 0; i < size; i++) {
            Component childComponent = component.getComponents().get(i);
            createComponentView(context,
                    childComponent,
                    parentLayout,
                    moduleAPI,
                    appCMSAndroidModules,
                    null,
                    settings,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    gridElement,
                    viewType,
                    component.getId());

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

    @SuppressWarnings({"StringBufferReplaceableByString", "ConstantConditions"})
    void createComponentView(final Context context,
                             final Component component,
                             final Layout parentLayout,
                             final Module moduleAPI,
                             final AppCMSAndroidModules appCMSAndroidModules,
                             @Nullable final PageView pageView,
                             final Settings settings,
                             Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                             final AppCMSPresenter appCMSPresenter,
                             boolean gridElement,
                             final String viewType,
                             String moduleId) {
        componentViewResult.componentView = null;
        componentViewResult.useMarginsAsPercentagesOverride = true;
        componentViewResult.useWidthOfScreen = false;
        componentViewResult.shouldHideModule = false;
        componentViewResult.addToPageView = false;
        componentViewResult.shouldHideComponent = false;
        componentViewResult.onInternalEvent = null;

        AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());

        if (componentType == null) {
            componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());

        if (componentKey == null) {
            componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        if (moduleId == null && moduleAPI != null) {
            moduleId = moduleAPI.getId();
        }

        String paymentProcessor = appCMSPresenter.getActiveSubscriptionProcessor();

        AppCMSUIKeyType moduleType = jsonValueKeyMap.get(viewType);

        if (moduleType == null) {
            moduleType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        if (moduleType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
            componentViewResult.useMarginsAsPercentagesOverride = false;
        }

        int tintColor = Color.parseColor(getColor(context,
                appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()));

        switch (componentType) {

            case PAGE_RATINGBAR:
                if (moduleAPI.getContentData() != null &&
                        !moduleAPI.getContentData().isEmpty() &&
                        moduleAPI.getContentData().get(0) != null &&
                        moduleAPI.getContentData().get(0).getGist() != null) {
                    componentViewResult.componentView = new StarRating(context, Color.parseColor(getColor(context, component.getBorderColor())),
                            Color.parseColor(getColor(context, component.getFillColor())),
                            moduleAPI.getContentData().get(0).getGist().getAverageStarRating());
                }
                break;
            case PAGE_TABLE_VIEW_KEY:
                if (moduleType == AppCMSUIKeyType.PAGE_DOWNLOAD_SETTING_MODULE_KEY) {

                    componentViewResult.componentView = new RecyclerView(context);

                    ((RecyclerView) componentViewResult.componentView)
                            .setLayoutManager(new LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL,
                                    false));

                    List<Mpeg> mpegs;
                    if (moduleAPI != null &&
                            moduleAPI.getContentData() != null &&
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

                    pageView.addListWithAdapter(new ListWithAdapter.Builder()
                            .adapter(radioAdapter)
                            .listview((RecyclerView) componentViewResult.componentView)
                            .id(moduleId + component.getKey())
                            .build());
                } else {
                    componentViewResult.componentView = new RecyclerView(context);

                    ((RecyclerView) componentViewResult.componentView)
                            .setLayoutManager(new LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL,
                                    false));

                    CollectionGridItemViewCreator collectionGridItemViewCreator =
                            new CollectionGridItemViewCreator(this,
                                    parentLayout,
                                    false,
                                    component,
                                    appCMSPresenter,
                                    moduleAPI,
                                    appCMSAndroidModules,
                                    settings,
                                    jsonValueKeyMap,
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    true,
                                    true,
                                    viewType,
                                    false,
                                    false);

                    AppCMSTrayItemAdapter appCMSTrayItemAdapter = new AppCMSTrayItemAdapter(context,
                            collectionGridItemViewCreator,
                            moduleAPI != null ? moduleAPI.getContentData() : null,
                            component.getComponents(),
                            appCMSPresenter,
                            jsonValueKeyMap,
                            viewType,
                            (RecyclerView) componentViewResult.componentView);

                    ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSTrayItemAdapter);
                    componentViewResult.onInternalEvent = appCMSTrayItemAdapter;
                    componentViewResult.onInternalEvent.setModuleId(moduleId);

                    if (pageView != null) {
                        pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                .adapter(appCMSTrayItemAdapter)
                                .listview((RecyclerView) componentViewResult.componentView)
                                .id(moduleId + component.getKey())
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

                    if (moduleType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY) {

                        if (BaseView.isTablet(context) && BaseView.isLandscape(context)) {
                            ((RecyclerView) componentViewResult.componentView)
                                    .setLayoutManager(new GridLayoutManager(context, 2,
                                            GridLayoutManager.VERTICAL, false));
                        } else {
                            ((RecyclerView) componentViewResult.componentView)
                                    .setLayoutManager(new LinearLayoutManager(context,
                                            LinearLayoutManager.VERTICAL,
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
                                viewType,
                                appCMSAndroidModules);

                        if (!BaseView.isTablet(context)) {
                            componentViewResult.useWidthOfScreen = true;
                        }

                        ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSViewAdapter);
                        if (pageView != null) {
                            pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                    .adapter(appCMSViewAdapter)
                                    .listview((RecyclerView) componentViewResult.componentView)
                                    .id(moduleId + component.getKey())
                                    .build());
                        }
                    } else if (moduleType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY) {
                        if (BaseView.isTablet(context) && BaseView.isLandscape(context)) {
                            ((RecyclerView) componentViewResult.componentView)
                                    .setLayoutManager(new GridLayoutManager(context, 2,
                                            GridLayoutManager.HORIZONTAL, false));
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
                                viewType,
                                appCMSAndroidModules);

                        if (!BaseView.isTablet(context)) {
                            componentViewResult.useWidthOfScreen = true;
                        }

                        ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSViewAdapter);
                        if (pageView != null) {
                            pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                    .adapter(appCMSViewAdapter)
                                    .listview((RecyclerView) componentViewResult.componentView)
                                    .id(moduleId + component.getKey())
                                    .build());
                        }
                    } else {
                        AppCMSUIKeyType parentViewType = jsonValueKeyMap.get(viewType);

                        if (parentViewType == AppCMSUIKeyType.PAGE_GRID_MODULE_KEY) {
                            int numCols = 1;
                            if (settings != null && settings.getColumns() != null) {
                                if (BaseView.isTablet(context)) {
                                    numCols = settings.getColumns().getTablet();
                                } else {
                                    numCols = settings.getColumns().getMobile();
                                }
                            }
                            ((RecyclerView) componentViewResult.componentView)
                                    .setLayoutManager(new GridLayoutManager(context,
                                            numCols,
                                            LinearLayoutManager.VERTICAL,
                                            false));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                componentViewResult.componentView.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
                            }
                        } else if (parentViewType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                            if (BaseView.isTablet(context)) {
                                ((RecyclerView) componentViewResult.componentView)
                                        .setLayoutManager(new GridLayoutManager(context,
                                                2,
                                                LinearLayoutManager.VERTICAL,
                                                false));
                            } else {
                                ((RecyclerView) componentViewResult.componentView)
                                        .setLayoutManager(new LinearLayoutManager(context,
                                                LinearLayoutManager.VERTICAL,
                                                false));
                            }
                        } else {
                            ((RecyclerView) componentViewResult.componentView)
                                    .setLayoutManager(new LinearLayoutManager(context,
                                            LinearLayoutManager.HORIZONTAL,
                                            false));
                        }

                        if (parentViewType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                            if (moduleAPI != null &&
                                    moduleAPI.getContentData() != null &&
                                    !moduleAPI.getContentData().isEmpty() &&
                                    moduleAPI.getContentData().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getSeason() != null &&
                                    !moduleAPI.getContentData().get(0).getSeason().isEmpty() &&
                                    moduleAPI.getContentData().get(0).getSeason().get(0) != null) {

                                CollectionGridItemViewCreator collectionGridItemViewCreator =
                                        new CollectionGridItemViewCreator(this,
                                                parentLayout,
                                                false,
                                                component,
                                                appCMSPresenter,
                                                moduleAPI,
                                                appCMSAndroidModules,
                                                settings,
                                                jsonValueKeyMap,
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                                false,
                                                true,
                                                viewType,
                                                false,
                                                false);

                                List<String> allEpisodeIds = new ArrayList<>();
                                List<Season_> seasons = moduleAPI.getContentData().get(0).getSeason();
                                int numSeasons = seasons.size();
                                for (int i = 0; i < numSeasons; i++) {
                                    Season_ season = seasons.get(i);
                                    List<ContentDatum> episodes = season.getEpisodes();
                                    int numEpisodes = episodes.size();
                                    if (season.getEpisodes() != null) {
                                        for (int j = 0; j < numEpisodes; j++) {
                                            ContentDatum episodeContentDatum = episodes.get(j);
                                            if (episodeContentDatum != null &&
                                                    episodeContentDatum.getGist() != null &&
                                                    episodeContentDatum.getGist().getId() != null) {
                                                allEpisodeIds.add(episodeContentDatum.getGist().getId());
                                            }
                                        }
                                    }
                                }

                                AppCMSTraySeasonItemAdapter appCMSTraySeasonItemAdapter =
                                        new AppCMSTraySeasonItemAdapter(context,
                                                collectionGridItemViewCreator,
                                                moduleAPI.getContentData().get(0).getSeason().get(0).getEpisodes(),
                                                component.getComponents(),
                                                allEpisodeIds,
                                                appCMSPresenter,
                                                jsonValueKeyMap,
                                                viewType);
                                ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSTraySeasonItemAdapter);
                                componentViewResult.onInternalEvent = appCMSTraySeasonItemAdapter;
                                componentViewResult.onInternalEvent.setModuleId(moduleId);
                                if (pageView != null) {
                                    pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                            .adapter(appCMSTraySeasonItemAdapter)
                                            .listview((RecyclerView) componentViewResult.componentView)
                                            .id(moduleId + component.getKey())
                                            .build());
                                }
                            }
                        } else {
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
                                    viewType,
                                    appCMSAndroidModules);
                            componentViewResult.useWidthOfScreen = true;
                            ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSViewAdapter);
                            if (pageView != null) {
                                pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                        .adapter(appCMSViewAdapter)
                                        .listview((RecyclerView) componentViewResult.componentView)
                                        .id(moduleId + component.getKey())
                                        .build());
                            }
                        }
                    }

                    if (moduleAPI != null && (moduleAPI.getContentData() == null ||
                            moduleAPI.getContentData().isEmpty())) {
                        componentViewResult.shouldHideModule = true;
                    }
                }
                break;

            case PAGE_VIDEO_PLAYER_VIEW_KEY:
                String videoId = null;
                if (moduleAPI != null &&
                        moduleAPI.getContentData().get(0) != null &&
                        moduleAPI.getContentData().get(0).getGist() != null &&
                        moduleAPI.getContentData().get(0).getGist().getId() != null) {
                    videoId = moduleAPI.getContentData().get(0).getGist().getId();
                }
                componentViewResult.componentView = new FrameLayout(context);
                if (playerViewCache.get(moduleId + component.getKey()) != null) {
                    videoPlayerViewSingle = playerViewCache.get(moduleId + component.getKey());
                } else {
                    playerViewCache.put(moduleId + component.getKey(), appCMSPresenter.videoPlayerView);

                    videoPlayerViewSingle = null;
                }
                if (videoPlayerViewSingle != null) {

                    if (videoPlayerViewSingle.getParent() != null)
                        ((ViewGroup) videoPlayerViewSingle.getParent()).removeView(videoPlayerViewSingle);
                    videoPlayerViewSingle.resumePlayer();

//                    if((appCMSPresenter.isconfig || appCMSPresenter.isAppBackground) && !TextUtils.isEmpty(appCMSPresenter.videoPlayerView.lastUrl)){
//                        appCMSPresenter.videoPlayerView.resumePlayer();
//                    }else{
//                        appCMSPresenter.videoPlayerView.setVideoUri(videoId, R.string.loading_video_text);
//                    }
                    ((FrameLayout) componentViewResult.componentView).addView(videoPlayerViewSingle);
                } else {
                    videoPlayerViewSingle = playerView(context, videoId);
                    ((FrameLayout) componentViewResult.componentView).addView(videoPlayerViewSingle);
                }
                appCMSPresenter.isconfig = false;
                appCMSPresenter.isAppBackground = false;


                componentViewResult.componentView.setId(R.id.video_player_id);


                break;
            case PAGE_WEB_VIEW_KEY:
                componentViewResult.componentView = getWebViewComponent(context, moduleAPI, component);
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
                AppCMSCarouselItemAdapter appCMSCarouselItemAdapter = new AppCMSCarouselItemAdapter(context,
                        this,
                        appCMSPresenter,
                        settings,
                        parentLayout,
                        component,
                        jsonValueKeyMap,
                        moduleAPI,
                        (RecyclerView) componentViewResult.componentView,
                        loop,
                        appCMSAndroidModules);
                ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSCarouselItemAdapter);
                if (pageView != null) {
                    pageView.addListWithAdapter(new ListWithAdapter.Builder()
                            .adapter(appCMSCarouselItemAdapter)
                            .listview((RecyclerView) componentViewResult.componentView)
                            .id(moduleId + component.getKey())
                            .build());
                }
                componentViewResult.onInternalEvent = appCMSCarouselItemAdapter;
                componentViewResult.onInternalEvent.setModuleId(moduleId);

                if (moduleAPI != null && (moduleAPI.getContentData() == null ||
                        moduleAPI.getContentData().isEmpty())) {
                    componentViewResult.shouldHideModule = true;
                }
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
                int numDots = moduleAPI != null ? moduleAPI.getContentData() != null ? moduleAPI.getContentData().size() : 0 : 0;
                ((DotSelectorView) componentViewResult.componentView).addDots(numDots);
                if (0 < numDots) {
                    ((DotSelectorView) componentViewResult.componentView).select(0);
                }
                componentViewResult.onInternalEvent = (DotSelectorView) componentViewResult.componentView;
                componentViewResult.onInternalEvent.setModuleId(moduleId);
                componentViewResult.useMarginsAsPercentagesOverride = false;
                break;

            case PAGE_BUTTON_KEY:
                if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_CLOSE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_VIDEO_DOWNLOAD_BUTTON_KEY) {
                    componentViewResult.componentView = new ResponsiveButton(context);
                } else if (componentKey != AppCMSUIKeyType.PAGE_BUTTON_SWITCH_KEY &&
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
                    } else if (moduleAPI != null && moduleAPI.getSettings() != null &&
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
                } else if (moduleAPI != null && jsonValueKeyMap.get(moduleAPI.getModuleType())
                        == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY
                        && componentKey == AppCMSUIKeyType.PAGE_DOWNLOAD_QUALITY_CANCEL_BUTTON_KEY
                        && component.getBorderWidth() != 0) {
                    applyBorderToComponent(
                            context,
                            componentViewResult.componentView,
                            component,
                            -1);
                } else {
                    if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand().getCta()
                            .getPrimary().getBackgroundColor())) {
                        componentViewResult.componentView.setBackgroundColor(Color.parseColor(
                                getColor(context, appCMSPresenter.getAppCMSMain().getBrand()
                                        .getCta().getPrimary().getBackgroundColor())));

                        applyBorderToComponent(context, componentViewResult.componentView, component,
                                Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                                        .getGeneral().getBlockTitleColor()));
                    } else {
                        applyBorderToComponent(context, componentViewResult.componentView, component, -1);
                    }
                }

                switch (componentKey) {
                    case PAGE_BUTTON_SWITCH_KEY:
                        if (appCMSPresenter.isPreferredStorageLocationSDCard()) {
                            ((Switch) componentViewResult.componentView).setChecked(true);
                        } else {
                            ((Switch) componentViewResult.componentView).setChecked(false);
                        }

                        ((Switch) componentViewResult.componentView).setOnCheckedChangeListener((buttonView, isChecked) -> {
                            if (isChecked) {
                                if (appCMSPresenter.isRemovableSDCardAvailable()) {
                                    appCMSPresenter.setPreferredStorageLocationSDCard(true);
                                } else {
                                    appCMSPresenter.showDialog(AppCMSPresenter.DialogType.SD_CARD_NOT_AVAILABLE, null, false, null, null);
                                    buttonView.setChecked(false);
                                }
                            } else {
                                appCMSPresenter.setPreferredStorageLocationSDCard(false);
                            }

                        });
                        break;

                    case PAGE_SETTINGS_EDIT_PROFILE_KEY:
                    case PAGE_SETTINGS_CHANGE_PASSWORD_KEY:
                        if (!TextUtils.isEmpty(appCMSPresenter.getFacebookAccessToken()) ||
                                (!TextUtils.isEmpty(appCMSPresenter.getUserAuthProviderName()) &&
                                        appCMSPresenter.getUserAuthProviderName().equalsIgnoreCase(context.getString(R.string.facebook_auth_provider_name_key)))) {
                            componentViewResult.componentView.setVisibility(View.GONE);
                            componentViewResult.shouldHideComponent = true;
                        }


                        if (!TextUtils.isEmpty(appCMSPresenter.getGoogleAccessToken()) ||
                                (!TextUtils.isEmpty(appCMSPresenter.getUserAuthProviderName()) &&
                                        appCMSPresenter.getUserAuthProviderName().equalsIgnoreCase(context.getString(R.string.google_auth_provider_name_key)))) {
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
                    case PAGE_DELETE_WATCHLIST_KEY:
                    case PAGE_DELETE_HISTORY_KEY:
                        componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.ic_deleteicon));
                        break;

                    case PAGE_GRID_OPTION_KEY:
                        componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.dots_more));
                        appCMSPresenter.setMoreIconAvailable();

                        componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                        component.getAction(),
                                        moduleAPI.getContentData().get(0).getGist().getTitle(),
                                        null,
                                        moduleAPI.getContentData().get(0),
                                        false,
                                        -1,
                                        null)) {
                                    //Log.e(TAG, "Could not launch action: " +
//                                                " permalink: " +
//                                                permalink +
//                                                " action: " +
//                                                action);
                                }
                            }
                        });
                        break;
                    case PAGE_BANNER_DETAIL_BUTTON:
                        componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.dots_more));
                        componentViewResult.componentView.setId(View.generateViewId());
                        componentViewResult.componentView.setOnClickListener(view -> {
                            if (settings != null) {
                                appCMSPresenter.showPopUpMenuSports(settings.getLinks(), settings.getSocialLinks());

                            }
                        });
                        break;

                    case PAGE_VIDEO_DOWNLOAD_BUTTON_KEY:
                        ((ImageButton) componentViewResult.componentView).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        componentViewResult.componentView.setBackgroundResource(android.R.color.transparent);
                        if (!gridElement &&
                                moduleAPI != null &&
                                moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null) {
                            String userId = appCMSPresenter.getLoggedInUser();
                            appCMSPresenter.getUserVideoDownloadStatus(
                                    moduleAPI.getContentData().get(0).getGist().getId(), new UpdateDownloadImageIconAction((ImageButton) componentViewResult.componentView, appCMSPresenter,
                                            moduleAPI.getContentData().get(0), userId), userId);
                        }

                        if (appCMSPresenter.getAppCMSMain().getFeatures() != null &&
                                appCMSPresenter.getAppCMSMain().getFeatures().isMobileAppDownloads()) {
                            componentViewResult.componentView.setVisibility(View.VISIBLE);
                        } else {
                            componentViewResult.componentView.setVisibility(View.GONE);
                        }
                        break;

                    case PAGE_ADD_TO_WATCHLIST_KEY:
                        ((ImageButton) componentViewResult.componentView)
                                .setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        componentViewResult.componentView.setBackgroundResource(android.R.color.transparent);

                        if (moduleAPI != null &&
                                moduleAPI.getContentData() != null &&
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
                        if (moduleAPI != null && moduleAPI.getContentData() != null &&
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
                                if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink(),
                                        component.getAction(),
                                        moduleAPI.getContentData().get(0).getGist().getTitle(),
                                        extraData,
                                        moduleAPI.getContentData().get(0),
                                        false,
                                        -1,
                                        null)) {
                                    //Log.e(TAG, "Could not launch action: " +
//                                            " permalink: " +
//                                            moduleAPI.getContentData().get(0).getGist().getPermalink() +
//                                            " action: " +
//                                            component.getAction() +
//                                            " hls URL: " +
//                                            moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets().getHls());
                                }
                            });
                        } else if (moduleAPI.getContentData().get(0).getShowDetails() != null &&
                                moduleAPI.getContentData().get(0).getShowDetails().getTrailers() != null &&
                                !moduleAPI.getContentData().get(0).getShowDetails().getTrailers().isEmpty() &&
                                moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0) != null &&
                                moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getPermalink() != null &&
                                moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getId() != null &&
                                moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getVideoAssets() != null) {
                            componentViewResult.componentView.setOnClickListener(v -> {
                                String[] extraData = new String[3];
                                extraData[0] = moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getPermalink();
                                extraData[1] = moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getVideoAssets().getHls();
                                extraData[2] = moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getId();
                                if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getPermalink(),
                                        component.getAction(),
                                        moduleAPI.getContentData().get(0).getGist().getTitle(),
                                        extraData,
                                        moduleAPI.getContentData().get(0),
                                        false,
                                        -1,
                                        null)) {
                                }
                            });

                        } else {
                            componentViewResult.shouldHideComponent = true;
                            componentViewResult.componentView.setVisibility(View.GONE);
                        }
                        break;

                    case PAGE_VIDEO_PLAY_BUTTON_KEY:
                        componentViewResult.componentView.setOnClickListener(v -> {
                            if (moduleAPI != null &&
                                    moduleAPI.getContentData() != null &&
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
                                        moduleAPI.getContentData().get(0).getContentDetails() != null) {

                                    List<String> relatedVideoIds = null;
                                    if (moduleAPI.getContentData().get(0).getContentDetails() != null &&
                                            moduleAPI.getContentData().get(0).getContentDetails().getRelatedVideoIds() != null) {
                                        relatedVideoIds = moduleAPI.getContentData().get(0).getContentDetails().getRelatedVideoIds();
                                    }
                                    int currentPlayingIndex = -1;
                                    if (relatedVideoIds == null) {
                                        currentPlayingIndex = 0;
                                    }

                                    appCMSPresenter.launchVideoPlayer(moduleAPI.getContentData().get(0),
                                            currentPlayingIndex,
                                            relatedVideoIds,
                                            moduleAPI.getContentData().get(0).getGist().getWatchedTime(),
                                            component.getAction());

                                }
                            }
                        });
                        componentViewResult.componentView.setPadding(8, 8, 8, 8);
                        componentViewResult.componentView.setBackground(ContextCompat.getDrawable(context, R.drawable.play_icon));
                        componentViewResult.componentView.getBackground().setTint(tintColor);
                        componentViewResult.componentView.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                        break;

                    case PAGE_PLAY_KEY:
                    case PAGE_PLAY_IMAGE_KEY:
                        componentViewResult.componentView.setPadding(40, 40, 40, 40);
                        componentViewResult.componentView.setBackground(ContextCompat.getDrawable(context, R.drawable.play_icon));

                        componentViewResult.componentView.getBackground().setTint(tintColor);
                        componentViewResult.componentView.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                        break;

                    case PAGE_PLAY_LIVE_IMAGE_KEY:
                        componentViewResult.componentView.setVisibility(View.GONE);

                        break;

                    case PAGE_VIDEO_CLOSE_KEY:
                        ((ImageButton) componentViewResult.componentView).setImageResource(R.drawable.cancel);
                        ((ImageButton) componentViewResult.componentView).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        componentViewResult.componentView.setPadding(8, 0, 0, 8);
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
                                //Log.e(TAG, "Could not launch action: " +
//                                        " action: " +
//                                        component.getAction());
                            }
                        });
                        break;

                    case PAGE_VIDEO_SHARE_KEY:
                        Drawable shareDrawable = ContextCompat.getDrawable(context, R.drawable.share);
                        componentViewResult.componentView.setBackground(shareDrawable);
                        componentViewResult.componentView.setOnClickListener(v -> {
                            AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
                            if (appCMSMain != null &&
                                    moduleAPI != null &&
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
                                    //Log.e(TAG, "Could not launch action: " +
//                                            " permalink: " +
//                                            moduleAPI.getContentData().get(0).getGist().getPermalink() +
//                                            " action: " +
//                                            component.getAction() +
//                                            " film URL: " +
//                                            filmUrl.toString());
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

                        componentViewResult.onInternalEvent = new OnRemoveAllInternalEvent(moduleId,
                                componentViewResult.componentView);
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
                                        appCMSPresenter.clearDownload(appCMSDownloadStatusResult -> {
                                            onInternalEvent.sendEvent(null);
                                            v.setVisibility(View.GONE);
                                        });
                                        break;

                                    case PAGE_WATCHLIST_MODULE_KEY:
                                        appCMSPresenter.clearWatchlist(appCMSAddToWatchlistResult -> {
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
                            if (!appCMSPresenter.sendCloseOthersAction(null,
                                    true,
                                    false)) {
                                //Log.e(TAG, "Could not perform close action: " +
//                                        " action: " +
//                                        component.getAction());
                            }
                        });
                        break;

                    case PAGE_DOWNLOAD_QUALITY_CONTINUE_BUTTON_KEY:
                        componentViewResult.componentView.setId(R.id.download_quality_continue_button);
                        break;

                    case PAGE_DOWNLOAD_QUALITY_CANCEL_BUTTON_KEY:
                        if (moduleAPI != null && jsonValueKeyMap.get(moduleAPI.getModuleType())
                                == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY) {
                            componentViewResult.componentView.setOnClickListener(v -> {
                                if (!appCMSPresenter.sendCloseOthersAction(null,
                                        true,
                                        false)) {
                                    //Log.e(TAG, "Could not perform close action: " +
//                                            " action: " +
//                                            component.getAction());
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
                        if (componentKey == AppCMSUIKeyType.PAGE_SETTINGS_UPGRADE_PLAN_PROFILE_KEY) {
                            if (!appCMSPresenter.isUserSubscribed()) {
                                ((TextView) componentViewResult.componentView)
                                        .setText(context.getString(R.string.app_cms_page_upgrade_subscribe_button_text));
                            } else if (!appCMSPresenter.upgradesAvailableForUser()) {
                                componentViewResult.componentView.setVisibility(View.GONE);
                            }
                        }

                        if (componentKey == AppCMSUIKeyType.PAGE_SETTINGS_CANCEL_PLAN_PROFILE_KEY) {
                            if (appCMSPresenter.isUserSubscribed() &&
                                    !appCMSPresenter.isExistingGooglePlaySubscriptionSuspended() &&
                                    appCMSPresenter.isSubscriptionCompleted()) {
                                componentViewResult.componentView.setVisibility(View.VISIBLE);
                            } else {
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
                                        .getBrand().getCta().getPrimary().getBackgroundColor()));
                        //.getGeneral().getBlockTitleColor()));
                    }
                }
                break;

            case PAGE_LABEL_KEY:
            case PAGE_TEXTVIEW_KEY:
                boolean resizeText = false;
                int textColor = ContextCompat.getColor(context, R.color.colorAccent);

                boolean showTrayLabel = false;

                if (componentKey == AppCMSUIKeyType.PAGE_TRAY_TITLE_KEY &&
                        moduleType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY &&
                        moduleAPI != null && moduleAPI.getContentData() != null &&
                        !moduleAPI.getContentData().isEmpty() &&
                        moduleAPI.getContentData().get(0) != null &&
                        moduleAPI.getContentData().get(0).getSeason() != null) {
                    int numSeasons = moduleAPI.getContentData().get(0).getSeason().size();
                    if (1 < numSeasons) {
                        showTrayLabel = true;
                    }
                }

                if (showTrayLabel) {
                    int numSeasons = moduleAPI.getContentData().get(0).getSeason().size();
                    componentViewResult.componentView = new Spinner(context, Spinner.MODE_DROPDOWN);

                    try {
                        componentViewResult.componentView.getBackground().setColorFilter(Color.parseColor(
                                getColor(context,
                                        appCMSPresenter.getAppCMSMain().getBrand()
                                                .getCta().getPrimary().getBackgroundColor())),
                                PorterDuff.Mode.SRC_ATOP);
                    } catch (Exception e) {
                        //
                    }

                    ArrayAdapter<String> seasonTrayAdapter = new SeasonsAdapterView(context,
                            appCMSPresenter,
                            component,
                            jsonValueKeyMap);

                    for (int i = 0; i < numSeasons; i++) {
                        StringBuilder seasonTitleSb = new StringBuilder(context.getString(R.string.app_cms_episodic_season_prefix));
                        seasonTitleSb.append(context.getString(R.string.blank_separator));
                        seasonTitleSb.append(i + 1);
                        seasonTrayAdapter.add(seasonTitleSb.toString());
                    }

                    componentViewResult.onInternalEvent =
                            new OnSeasonSelectedListener(moduleAPI.getContentData().get(0).getSeason());
                    componentViewResult.onInternalEvent.setModuleId(moduleId);

                    ((Spinner) componentViewResult.componentView)
                            .setOnItemSelectedListener((AdapterView.OnItemSelectedListener) componentViewResult.onInternalEvent);

                    if (numSeasons == 1) {
                        componentViewResult.componentView.setEnabled(false);
                    } else {
                        componentViewResult.componentView.setEnabled(true);
                        try {
                            ((Spinner) componentViewResult.componentView).setPopupBackgroundDrawable(new ColorDrawable(Color.parseColor(
                                    getColor(context, appCMSPresenter.getAppCMSMain().getBrand()
                                            .getGeneral()
                                            .getBackgroundColor()))));
                        } catch (Exception e) {

                        }
                    }
                    seasonTrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    ((Spinner) componentViewResult.componentView).setAdapter(seasonTrayAdapter);
                } else {
                    componentViewResult.componentView = new TextView(context);

                    if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_SD_CARD_FOR_DOWNLOADS_TEXT_KEY &&
                            !appCMSPresenter.isAppSVOD() &&
                            !appCMSPresenter.getAppCMSMain().getFeatures().isMobileAppDownloads()) {
                        componentViewResult.componentView.setVisibility(View.GONE);
                        componentViewResult.shouldHideComponent = true;
                    } else if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_USER_MANAGEMENT_AUTOPLAY_TEXT_KEY &&
                            !appCMSPresenter.isAppSVOD() &&
                            !appCMSPresenter.getAppCMSMain().getFeatures().isAutoPlay()) {
                        componentViewResult.componentView.setVisibility(View.GONE);
                        componentViewResult.shouldHideComponent = true;
                    }

                    if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor())) {
                        textColor = Color.parseColor(getColor(context, appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor()));
                    } else if (component.getStyles() != null) {
                        if (!TextUtils.isEmpty(component.getStyles().getColor())) {
                            textColor = Color.parseColor(getColor(context, component.getStyles().getColor()));
                        } else if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor())) {
                            textColor =
                                    Color.parseColor(getColor(context, appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()));
                        }
                    }
                    if (componentKey == AppCMSUIKeyType.PAGE_BANNER_DETAIL_TITLE) {
                        int textBgColor = Color.parseColor(getColor(context, appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()));

                        int textFontColor = Color.parseColor(getColor(context, appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()));
                        if (!TextUtils.isEmpty(component.getTextColor())) {
                            textFontColor = Color.parseColor(getColor(context, component.getTextColor()));
                        }
                        componentViewResult.componentView.setBackgroundColor(textBgColor);
                        ((TextView) componentViewResult.componentView).setTextColor(textFontColor);
                        ((TextView) componentViewResult.componentView).setGravity(Gravity.START);

                        if (!TextUtils.isEmpty(component.getFontFamily())) {
                            setTypeFace(context,
                                    appCMSPresenter,
                                    jsonValueKeyMap,
                                    component,
                                    (TextView) componentViewResult.componentView);
                        }

                        if (component.getFontSize() > 0) {
                            ((TextView) componentViewResult.componentView).setTextSize(component.getFontSize());
                        } else if (BaseView.getFontSize(context, component.getLayout()) > 0) {
                            ((TextView) componentViewResult.componentView).setTextSize(BaseView.getFontSize(context, component.getLayout()));
                        }
                        if (settings != null && settings.getTitle() != null)
                            ((TextView) componentViewResult.componentView).setText(settings.getTitle());
                    }
                    if (componentKey == AppCMSUIKeyType.PAGE_GRID_THUMBNAIL_INFO) {
                        int textBgColor = Color.parseColor(getColor(context, appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()));
                        if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                            textBgColor = Color.parseColor(getColorWithOpacity(context, component.getBackgroundColor(), component.getOpacity()));
                        }
                        int textFontColor = Color.parseColor(getColor(context, appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()));
                        if (!TextUtils.isEmpty(component.getTextColor())) {
                            textFontColor = Color.parseColor(getColor(context, component.getTextColor()));
                        }
                        componentViewResult.componentView.setBackgroundColor(textBgColor);
                        ((TextView) componentViewResult.componentView).setTextColor(textFontColor);
                        ((TextView) componentViewResult.componentView).setGravity(Gravity.START);

                        if (!TextUtils.isEmpty(component.getFontFamily())) {
                            setTypeFace(context,
                                    appCMSPresenter,
                                    jsonValueKeyMap,
                                    component,
                                    (TextView) componentViewResult.componentView);
                        }

                        if (component.getFontSize() > 0) {
                            ((TextView) componentViewResult.componentView).setTextSize(component.getFontSize());
                        } else if (BaseView.getFontSize(context, component.getLayout()) > 0) {
                            ((TextView) componentViewResult.componentView).setTextSize(BaseView.getFontSize(context, component.getLayout()));
                        }
                        break;
                    } else if (componentKey == AppCMSUIKeyType.PAGE_AUTOPLAY_FINISHED_UP_TITLE_KEY
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
                                if (moduleAPI != null && !TextUtils.isEmpty(moduleAPI.getTitle())) {
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
                                } else if (moduleAPI != null &&
                                        moduleAPI.getContentData() != null &&
                                        moduleAPI.getContentData().size() > 0 &&
                                        moduleAPI.getContentData().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getGist() != null &&
                                        moduleAPI.getContentData().get(0).getGist().getTitle() != null) {
                                    ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getTitle());
                                }
                                break;

                            case PAGE_API_DESCRIPTION:
                                if (moduleAPI != null && !TextUtils.isEmpty(moduleAPI.getRawText())) {
                                    Spannable rawHtmlSpannable = htmlSpanner.fromHtml(moduleAPI.getRawText());
                                    ((TextView) componentViewResult.componentView).setText(rawHtmlSpannable);
                                    ((TextView) componentViewResult.componentView).setMovementMethod(LinkMovementMethod.getInstance());
                                }
                                break;

                            case PAGE_TRAY_TITLE_KEY:
                                if (!TextUtils.isEmpty(component.getText())) {
                                    ((TextView) componentViewResult.componentView).setText(component.getText().toUpperCase());
                                } else if (moduleAPI != null && moduleAPI.getSettings() != null && !moduleAPI.getSettings().getHideTitle() &&
                                        !TextUtils.isEmpty(moduleAPI.getTitle())) {
                                    ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle().toUpperCase());
                                } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_WATCHLIST_MODULE_KEY) {
                                    ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_watchlist_title);
                                } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY) {
                                    ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_download_title);
                                } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_HISTORY_MODULE_KEY) {
                                    ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_history_title);
                                } else if (moduleType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                                    StringBuilder seasonTitleSb = new StringBuilder(context.getString(R.string.app_cms_episodic_season_prefix));
                                    seasonTitleSb.append(context.getString(R.string.blank_separator));
                                    seasonTitleSb.append(1);
                                    ((TextView) componentViewResult.componentView).setText(seasonTitleSb.toString());
                                }
                                break;

                            case PAGE_AUTOPLAY_MOVIE_DESCRIPTION_KEY:
                                String autoplayVideoDescription = null;
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
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
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
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
                                                    true,
                                                    Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor()),
                                                    false);
                                    viewTreeObserver.addOnGlobalLayoutListener(viewCreatorMultiLineLayoutListener);
                                }
                                break;

                            case PAGE_VIDEO_DESCRIPTION_KEY:
                                String videoDescription = null;
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
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
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
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
                                                    false,
                                                    Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor()),
                                                    false);
                                    textVto.addOnGlobalLayoutListener(viewCreatorLayoutListener);
                                }
                                break;

                            case PAGE_AUTOPLAY_MOVIE_TITLE_KEY:
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
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
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
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
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                        !moduleAPI.getContentData().isEmpty() &&
                                        moduleAPI.getContentData().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getSeason() != null) {

                                    setViewWithShowSubtitle(context,
                                            moduleAPI.getContentData().get(0),
                                            componentViewResult.componentView);
                                }
                                break;

                            case PAGE_AUTOPLAY_MOVIE_SUBHEADING_KEY:
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                        !moduleAPI.getContentData().isEmpty() &&
                                        moduleAPI.getContentData().get(0) != null) {
                                    setViewWithSubtitle(context,
                                            moduleAPI.getContentData().get(0),
                                            componentViewResult.componentView);
                                }
                                break;

                            case PAGE_VIDEO_PUBLISHDATE_KEY:
                                if (moduleAPI.getContentData() != null &&
                                        !moduleAPI.getContentData().isEmpty() &&
                                        moduleAPI.getContentData().get(0) != null) {
                                    long publishDateMillseconds = moduleAPI.getContentData().get(0).getGist().getPublishDate();
                                    String publishDate = context.getResources().getString(R.string.published_on) + " " + appCMSPresenter.getDateFormat(publishDateMillseconds, "MMM dd,yyyy");
                                    ((TextView) componentViewResult.componentView).setText(publishDate);

                                }

                                break;


                            case PAGE_VIDEO_AGE_LABEL_KEY:
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                        !moduleAPI.getContentData().isEmpty() &&
                                        moduleAPI.getContentData().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getGist() != null &&
                                        !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getParentalRating())) {
                                    String parentalRating = moduleAPI.getContentData().get(0).getParentalRating();
                                    ((TextView) componentViewResult.componentView).setText(parentalRating);
                                    ((TextView) componentViewResult.componentView).setGravity(Gravity.CENTER);

                                    if (parentalRating.length() > 2) {
                                        resizeText = true;
                                    }

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
                                ((TextView) componentViewResult.componentView).setText(appCMSPresenter.getLoggedInUserName());
                                break;

                            case PAGE_SETTINGS_EMAIL_VALUE_KEY:
                                ((TextView) componentViewResult.componentView).setText(appCMSPresenter.getLoggedInUserEmail());
                                break;

                            case PAGE_SETTINGS_EMAIL_TITLE_KEY:
                                if (TextUtils.isEmpty(appCMSPresenter.getLoggedInUserEmail())) {
                                    componentViewResult.componentView.setVisibility(View.GONE);
                                    componentViewResult.shouldHideComponent = true;
                                }
                                break;

                            case PAGE_SETTINGS_PLAN_VALUE_KEY:
                                if (appCMSPresenter.isUserSubscribed() &&
                                        !TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionPlanName())) {
                                    ((TextView) componentViewResult.componentView).setText(appCMSPresenter.getActiveSubscriptionPlanName());
                                } else if (!appCMSPresenter.isUserSubscribed()) {
                                    ((TextView) componentViewResult.componentView).setText(context.getString(R.string.subscription_unsubscribed_plan_value));
                                }
                                break;

                            case PAGE_SETTINGS_PLAN_PROCESSOR_TITLE_KEY:
                                if (appCMSPresenter.isUserSubscribed() &&
                                        !TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionProcessor())) {
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
                                if (paymentProcessor != null && appCMSPresenter.isUserSubscribed()) {
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
                                if (appCMSPresenter.getAppCMSMain().getFeatures() != null &&
                                        appCMSPresenter.getAppCMSMain().getFeatures().isMobileAppDownloads()) {
                                    ((TextView) componentViewResult.componentView)
                                            .setText(appCMSPresenter.getUserDownloadQualityPref());
                                }
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
                        if (moduleType != AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                            ((TextView) componentViewResult.componentView).setSingleLine(true);
                            ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                        }
                    }

                    if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                        componentViewResult.componentView.setBackgroundColor(
                                Color.parseColor(getColor(context, component.getBackgroundColor())));
                    }

                    if (!TextUtils.isEmpty(component.getFontFamily())) {
                        setTypeFace(context,
                                appCMSPresenter,
                                jsonValueKeyMap,
                                component,
                                (TextView) componentViewResult.componentView);
                    }

                    if (component.getFontSize() > 0) {
                        int fontSize = component.getFontSize();
                        if (resizeText) {
                            fontSize = (int) (0.66 * fontSize);
                        }
                        ((TextView) componentViewResult.componentView).setTextSize(fontSize);
                    } else if (BaseView.getFontSize(context, component.getLayout()) > 0) {
                        int fontSize = (int) BaseView.getFontSize(context, component.getLayout());
                        if (resizeText) {
                            fontSize = (int) (0.66 * fontSize);
                        }
                        ((TextView) componentViewResult.componentView).setTextSize(fontSize);
                    }
                }

                break;

            case PAGE_IMAGE_KEY:
                componentViewResult.componentView = ImageUtils.createImageView(context);
                if (componentViewResult.componentView == null) {
                    componentViewResult.componentView = new ImageView(context);
                }
                switch (componentKey) {
                    case PAGE_VIDEO_TYPE_KEY:
                        // TODO: have to  add condition depending upon API object
                        if (moduleAPI.getContentData() != null &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null &&
                                moduleAPI.getContentData().get(0).getGist().getContentType() != null) {
                            if (moduleAPI.getContentData().get(0).getGist().getContentType().contains(context.getString(R.string.app_cms_content_type_shows))) {
                                ((ImageView) componentViewResult.componentView).setImageResource(R.drawable.ic_shows);
                            }
                            if (moduleAPI.getContentData().get(0).getGist().getContentType().contains(context.getString(R.string.app_cms_content_type_episode))) {
                                ((ImageView) componentViewResult.componentView).setImageResource(R.drawable.ic_episode);
                            }
                        }
                        break;
                    case PAGE_AUTOPLAY_MOVIE_IMAGE_KEY:
                        if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null &&
                                !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getPosterImageUrl())) {
                            setAutoPlayImage(context, component, moduleAPI.getContentData().get(0).getGist().getPosterImageUrl());
                        } else if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null &&
                                !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())) {
                            setAutoPlayImage(context, component, moduleAPI.getContentData().get(0).getGist().getVideoImageUrl());
                        }
                        break;

                    case PAGE_BADGE_IMAGE_KEY:
                        //
                        break;

                    case PAGE_THUMBNAIL_BADGE_IMAGE:
                        componentViewResult.componentView = new ImageView(context);
                        ImageView imageView = (ImageView) componentViewResult.componentView;
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        String iconImageUrl;
                        if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                moduleAPI.getContentData().size() > 1 &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null &&
                                moduleAPI.getContentData().get(0).getGist().getBadgeImages() != null &&
                                !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getBadgeImages().toString())) {
                            iconImageUrl = moduleAPI.getContentData().get(0).getGist().getBadgeImages().toString();
                            Glide.with(context)
                                    .load(iconImageUrl)
                                    .into(imageView);
                        } /*else if (context.getDrawable(R.drawable.pro_badge_con) != null) {
                            componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.pro_badge_con));
                        }*/


                        break;
                    case PAGE_BANNER_DETAIL_ICON:
                        componentViewResult.componentView = new ImageView(context);
                        String bannerUrl = null;
                        if (settings != null && settings.getImage() != null) {
                            bannerUrl = settings.getImage();
                        }
                        Glide.with(context)
                                .load(bannerUrl)
                                .into((ImageView) componentViewResult.componentView);
                        break;
                    case PAGE_VIDEO_IMAGE_KEY:
                        if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null &&
                                (!TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getPosterImageUrl()) ||
                                        !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl()))) {
                            int viewWidth = (int) BaseView.getViewWidth(context,
                                    component.getLayout(),
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
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
                                        .into((ImageView) componentViewResult.componentView);
                            } else if (viewWidth > 0) {
                                String videoImageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                        moduleAPI.getContentData().get(0).getGist().getVideoImageUrl(),
                                        viewWidth,
                                        viewHeight);
                                Glide.with(context)
                                        .load(videoImageUrl)
                                        .override(viewWidth, viewHeight)
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
                            if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView, component.getImageName())) {
                                Glide.with(context)
                                        .load(component.getImageName())
                                        .into((ImageView) componentViewResult.componentView);
                            }
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

                if (appCMSPresenter.isUserLoggedIn()) {
                    ((ProgressBar) componentViewResult.componentView).setMax(100);
                    ((ProgressBar) componentViewResult.componentView).setProgress(0);
                    if (moduleAPI != null && moduleAPI.getContentData() != null &&
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
                                long percentageWatched = (long) (((double) watchedTime / (double) runTime) * 100.0);
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
            case PAGE_BANNER_DETAIL_BACKGROUND:
                componentViewResult.componentView = new View(context);
                if (settings != null && settings.getBackgroundColor() != null) {
                    componentViewResult.componentView.
                            setBackgroundColor(Color.parseColor(getColor(context,
                                    settings.getBackgroundColor())));
                }
                break;
            case PAGE_SEPARATOR_VIEW_KEY:
            case PAGE_SEGMENTED_VIEW_KEY:
                componentViewResult.componentView = new View(context);
                if (component.getBackgroundColor() != null && !TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.
                            setBackgroundColor(Color.parseColor(getColor(context,
                                    component.getBackgroundColor())));
                } else if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
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

                if (moduleAPI != null && moduleAPI.getContentData() != null &&
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
                        Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor()),
                        BaseView.getFontSizeKey(context, component.getLayout()),
                        BaseView.getFontSizeValue(context, component.getLayout()));

                if (moduleAPI != null && !BaseView.isTablet(context)
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
                setTypeFace(context, appCMSPresenter, jsonValueKeyMap, component, textInputEditText);
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

            case PAGE_PLAN_META_DATA_VIEW_KEY:
                if (moduleAPI != null) {
                    if (moduleType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY) {
                        componentViewResult.componentView = new ViewPlansMetaDataView(context,
                                component,
                                component.getLayout(),
                                this,
                                moduleAPI,
                                jsonValueKeyMap,
                                appCMSPresenter,
                                settings);
                    }
                    if (moduleType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY) {
                        componentViewResult.componentView = new SubscriptionMetaDataView(context,
                                component,
                                component.getLayout(),
                                this,
                                moduleAPI,
                                jsonValueKeyMap,
                                appCMSPresenter,
                                settings,
                                appCMSAndroidModules);
                    }
                }
                break;

            case PAGE_SETTINGS_KEY:
                if (moduleAPI != null) {
                    componentViewResult.componentView = createModuleView(context,
                            component,
                            moduleAPI,
                            appCMSAndroidModules,
                            pageView,
                            jsonValueKeyMap,
                            appCMSPresenter);
                }
                break;

            case PAGE_TOGGLE_BUTTON_KEY:
                componentViewResult.componentView = new Switch(context);
                ((Switch) componentViewResult.componentView).getTrackDrawable().setTint(Color.parseColor(
                        appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor()));
                int switchOnColor = Color.parseColor(
                        appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor());
                int switchOffColor = Color.parseColor(
                        appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor());
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_checked},
                                new int[]{}
                        }, new int[]{
                        switchOnColor,
                        switchOffColor
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ((Switch) componentViewResult.componentView).setTrackTintMode(PorterDuff.Mode.MULTIPLY);
                    ((Switch) componentViewResult.componentView).setThumbTintList(colorStateList);
                } else {
                    ((Switch) componentViewResult.componentView).setButtonTintList(colorStateList);
                }

                if (componentKey == AppCMSUIKeyType.PAGE_AUTOPLAY_TOGGLE_BUTTON_KEY) {
                    if (appCMSPresenter.getAppCMSMain().getFeatures() != null &&
                            appCMSPresenter.getAppCMSMain().getFeatures().isAutoPlay()) {
                        ((Switch) componentViewResult.componentView)
                                .setChecked(appCMSPresenter.getAutoplayEnabledUserPref(context));
                        ((Switch) componentViewResult.componentView)
                                .setOnCheckedChangeListener((buttonView, isChecked)
                                        -> appCMSPresenter.setAutoplayEnabledUserPref(context, isChecked));
                    } else {
                        ((Switch) componentViewResult.componentView)
                                .setChecked(false);
                        componentViewResult.componentView.setVisibility(View.GONE);
                    }
                }

                if (componentKey == AppCMSUIKeyType.PAGE_SD_CARD_FOR_DOWNLOADS_TOGGLE_BUTTON_KEY) {

                    if (appCMSPresenter.getAppCMSMain().getFeatures() != null &&
                            appCMSPresenter.getAppCMSMain().getFeatures().isMobileAppDownloads()) {
                        ((Switch) componentViewResult.componentView)
                                .setChecked(appCMSPresenter.getUserDownloadLocationPref());
                        ((Switch) componentViewResult.componentView)
                                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    if (isChecked) {
                                        if (appCMSPresenter.isRemovableSDCardAvailable()) {
                                            appCMSPresenter.setUserDownloadLocationPref(true);
                                        } else {
                                            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.SD_CARD_NOT_AVAILABLE,
                                                    null,
                                                    false,
                                                    null,
                                                    null);
                                            buttonView.setChecked(false);
                                        }
                                    } else {
                                        appCMSPresenter.setUserDownloadLocationPref(false);
                                    }
                                });
                        if (appCMSPresenter.isExternalStorageAvailable()) {
                            componentViewResult.componentView.setEnabled(true);
                        } else {
                            componentViewResult.componentView.setEnabled(false);
                            ((Switch) componentViewResult.componentView).setChecked(false);
                        }
                        componentViewResult.componentView.setVisibility(View.VISIBLE);
                    } else {
                        componentViewResult.componentView.setEnabled(false);
                        ((Switch) componentViewResult.componentView).setChecked(false);
                        componentViewResult.componentView.setVisibility(View.GONE);
                    }
                }
                break;

            default:
                if (moduleAPI != null && component.getComponents() != null &&
                        !component.getComponents().isEmpty()) {
                    componentViewResult.componentView = createModuleView(context,
                            component,
                            moduleAPI,
                            appCMSAndroidModules,
                            pageView,
                            jsonValueKeyMap,
                            appCMSPresenter);
                    componentViewResult.useWidthOfScreen = true;
                }
                break;
        }

        if (pageView != null) {
            pageView.addViewWithComponentId(new ViewWithComponentId.Builder()
                    .id(moduleId + component.getKey())
                    .view(componentViewResult.componentView)
                    .build());
        }
    }

    private static String getColor(Context context, String color) {
        if (color.indexOf(context.getString(R.string.color_hash_prefix)) != 0) {
            return context.getString(R.string.color_hash_prefix) + color;
        }
        return color;
    }

    private String getColorWithOpacity(Context context, String baseColorCode, int opacityColorCode) {
        if (baseColorCode.indexOf(context.getString(R.string.color_hash_prefix)) != 0) {
            return context.getString(R.string.color_hash_prefix) + opacityColorCode + baseColorCode;
        }
        return baseColorCode;
    }

    private Module matchModuleAPIToModuleUI(ModuleList module, AppCMSPageAPI appCMSPageAPI,
                                            Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
        if (appCMSPageAPI != null && appCMSPageAPI.getModules() != null) {
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

            if (jsonValueKeyMap.get(module.getView()) != null) {
                switch (jsonValueKeyMap.get(module.getView())) {
                    case PAGE_HISTORY_MODULE_KEY:
                    case PAGE_WATCHLIST_MODULE_KEY:
                    case PAGE_AUTOPLAY_MODULE_KEY:
                    case PAGE_DOWNLOAD_SETTING_MODULE_KEY:
                    case PAGE_DOWNLOAD_MODULE_KEY:
                    case PAGE_BANNER_MODULE_KEY:
                        if (appCMSPageAPI.getModules() != null
                                && !appCMSPageAPI.getModules().isEmpty()) {
                            return appCMSPageAPI.getModules().get(0);
                        }
                        break;

                    default:
                        break;
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

    private static void setTypeFace(Context context,
                                    AppCMSPresenter appCMSPresenter,
                                    Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                    Component component,
                                    TextView textView) {

        if (jsonValueKeyMap.get(component.getFontFamily()) != null) {
            String fontName = "";
            switch (jsonValueKeyMap.get(component.getFontFamily())) {
                case PAGE_TEXT_OPENSANS_FONTFAMILY_KEY:
                    fontName = context.getString(R.string.app_cms_page_font_family_key);
                    break;
                case PAGE_TEXT_LATO_FONTFAMILY_KEY:
                    fontName = context.getString(R.string.app_cms_page_font_lato_family_key);
                    break;
                default:
                    fontName = context.getString(R.string.app_cms_page_font_family_key);
                    break;

            }

            AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(component.getFontWeight());
            if (fontWeight == null) {
                fontWeight = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            Typeface face;
            switch (fontWeight) {
                case PAGE_TEXT_BOLD_KEY:
                    face = appCMSPresenter.getBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_bold_ttf, fontName));
                        appCMSPresenter.setBoldTypeFace(face);
                    }
                    break;

                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = appCMSPresenter.getSemiBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_semibold_ttf, fontName));
                        appCMSPresenter.setSemiBoldTypeFace(face);
                    }
                    break;

                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_extrabold_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_BLACK_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_black_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_BLACK_ITALIC_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_black_italic_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_HAIRLINE_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_hairline_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_HAIRLINE_ITALIC_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_hairline_italic_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_HEAVY_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_heavy_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_HEAVY_ITALIC_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_heavy_italic_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_LIGHT_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_light_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_LIGHT_ITALIC_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_light_italic_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_MEDIUM_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_medium_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_MEDIUM_ITALIC_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_medium_italic_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_THIN_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_thin_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_THIN_ITALIC_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_thin_italic_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                case PAGE_TEXT_SEMIBOLD_ITALIC_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_semibold_italic_ttf, fontName));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;
                default:
                    face = appCMSPresenter.getRegularFontFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.font_regular_ttf, fontName));
                        appCMSPresenter.setRegularFontFace(face);
                    }
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

    static class ComponentViewResult {
        View componentView;
        OnInternalEvent onInternalEvent;
        boolean useMarginsAsPercentagesOverride;
        boolean useWidthOfScreen;
        boolean shouldHideModule;
        boolean addToPageView;
        boolean shouldHideComponent;
    }

    private static class OnSeasonSelectedListener implements
            AdapterView.OnItemSelectedListener,
            OnInternalEvent {

        private List<Season_> seasonData;
        private List<OnInternalEvent> onInternalEvents;
        private String moduleId;

        public OnSeasonSelectedListener(List<Season_> seasonData) {
            this.seasonData = seasonData;
            this.onInternalEvents = new ArrayList<>();
        }

        @Override
        public void addReceiver(OnInternalEvent e) {
            if (onInternalEvents != null) {
                onInternalEvents.add(e);
            }
        }

        @Override
        public void sendEvent(InternalEvent<?> event) {
            int internalEventsSize = onInternalEvents != null ? onInternalEvents.size() : 0;
            for (int i = 0; i < internalEventsSize; i++) {
                onInternalEvents.get(i).receiveEvent(event);
            }
        }

        @Override
        public void receiveEvent(InternalEvent<?> event) {

        }

        @Override
        public void cancel(boolean cancel) {

        }

        @Override
        public String getModuleId() {
            return moduleId;
        }

        @Override
        public void setModuleId(String moduleId) {
            this.moduleId = moduleId;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (0 <= position && position < seasonData.size()) {
                sendEvent(new InternalEvent<>(seasonData.get(position).getEpisodes()));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private static class SeasonsAdapterView extends ArrayAdapter<String> {
        private AppCMSPresenter appCMSPresenter;
        private Component component;
        private Map<String, AppCMSUIKeyType> jsonValueKeyMap;

        public SeasonsAdapterView(Context context,
                                  AppCMSPresenter appCMSPresenter,
                                  Component component,
                                  Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
            super(context, R.layout.season_title_dropdown);
            this.appCMSPresenter = appCMSPresenter;
            this.component = component;
            this.jsonValueKeyMap = jsonValueKeyMap;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View result = null;
            if (convertView != null && convertView instanceof TextView) {
                result = convertView;
            } else if (parent != null) {
                result = LayoutInflater.from(parent.getContext()).inflate(R.layout.season_title_dropdown,
                        parent,
                        false);

                try {
                    if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand()
                            .getCta().getPrimary().getTextColor())) {
                        ((TextView) result).setTextColor(
                                Color.parseColor(
                                        getColor(parent.getContext(), appCMSPresenter.getAppCMSMain()
                                                .getBrand().getCta().getPrimary().getBackgroundColor())));
                    }
                } catch (Exception e) {
                    //
                }

                try {
                    result.setBackgroundColor(Color.parseColor(
                            getColor(parent.getContext(), appCMSPresenter.getAppCMSMain().getBrand()
                                    .getGeneral()
                                    .getBackgroundColor())));
                } catch (Exception e) {
                    //
                }

                if (component.getFontSize() > 0) {
                    ((TextView) result).setTextSize(component.getFontSize());
                } else if (BaseView.getFontSize(parent.getContext(), component.getLayout()) > 0) {
                    ((TextView) result).setTextSize(BaseView.getFontSize(parent.getContext(), component.getLayout()));
                }

                if (!TextUtils.isEmpty(component.getFontFamily())) {
                    setTypeFace(parent.getContext(),
                            appCMSPresenter,
                            jsonValueKeyMap,
                            component,
                            (TextView) result);
                }

                result.setPadding(8, 0, 8, 0);
            }

            ((TextView) result).setText(getItem(position));

            return result;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View result = null;
            if (convertView != null && convertView instanceof TextView) {
                result = convertView;
            } else if (parent != null) {
                result = LayoutInflater.from(parent.getContext()).inflate(R.layout.season_title_dropdown,
                        parent,
                        false);

                try {
                    if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand()
                            .getCta().getPrimary().getTextColor())) {
                        ((TextView) result).setTextColor(
                                Color.parseColor(
                                        getColor(parent.getContext(), appCMSPresenter.getAppCMSMain()
                                                .getBrand().getCta().getPrimary().getBackgroundColor())));
                    }
                } catch (Exception e) {
                    //
                }

                try {
                    result.setBackgroundColor(Color.parseColor(
                            getColor(parent.getContext(), appCMSPresenter.getAppCMSMain().getBrand()
                                    .getGeneral()
                                    .getBackgroundColor())));
                } catch (Exception e) {
                    //
                }

                if (component.getFontSize() > 0) {
                    ((TextView) result).setTextSize(component.getFontSize());
                } else if (BaseView.getFontSize(parent.getContext(), component.getLayout()) > 0) {
                    ((TextView) result).setTextSize(BaseView.getFontSize(parent.getContext(), component.getLayout()));
                }

                if (!TextUtils.isEmpty(component.getFontFamily())) {
                    setTypeFace(parent.getContext(),
                            appCMSPresenter,
                            jsonValueKeyMap,
                            component,
                            (TextView) result);
                }

                result.setPadding(8, 8, 8, 8);
            }

            if (result != null) {
                ((TextView) result).setText(getItem(position));
            }

            return result;
        }
    }

    public static class UpdateImageIconAction implements Action1<UserVideoStatusResponse> {
        private final ImageButton imageButton;
        private final AppCMSPresenter appCMSPresenter;
        private final String filmId;

        private View.OnClickListener addClickListener;
        private View.OnClickListener removeClickListener;

        UpdateImageIconAction(ImageButton imageButton, AppCMSPresenter presenter,
                              String filmId) {
            this.imageButton = imageButton;
            this.appCMSPresenter = presenter;
            this.filmId = filmId;

            addClickListener = v -> {
                if (appCMSPresenter.isUserLoggedIn()) {
                    appCMSPresenter.editWatchlist(UpdateImageIconAction.this.filmId,
                            addToWatchlistResult -> {
                                UpdateImageIconAction.this.imageButton.setImageResource(
                                        R.drawable.remove_from_watchlist);
                                UpdateImageIconAction.this.imageButton.setOnClickListener(removeClickListener);
                            }, true);
                } else {
                    appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_REQUIRED,
                            () -> {
                                appCMSPresenter.setAfterLoginAction(() -> {

                                });
                            });
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
        private final AppCMSPresenter appCMSPresenter;
        private final ContentDatum contentDatum;
        private final String userId;
        private ImageButton imageButton;
        private View.OnClickListener addClickListener;

        UpdateDownloadImageIconAction(ImageButton imageButton, AppCMSPresenter presenter,
                                      ContentDatum contentDatum, String userId) {
            this.imageButton = imageButton;
            this.appCMSPresenter = presenter;
            this.contentDatum = contentDatum;
            this.userId = userId;

            addClickListener = v -> {
                if (!appCMSPresenter.isNetworkConnected()) {
                    if (!appCMSPresenter.isUserLoggedIn()) {
                        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK, null, false,
                                appCMSPresenter::launchBlankPage,
                                null);
                        return;
                    }
                    appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                            appCMSPresenter.getNetworkConnectivityDownloadErrorMsg(),
                            true,
                            () -> appCMSPresenter.navigateToDownloadPage(appCMSPresenter.getDownloadPageId(),
                                    null, null, false),
                            null);
                    return;
                }
                if ((appCMSPresenter.isAppSVOD() && appCMSPresenter.isUserSubscribed()) ||
                        !appCMSPresenter.isAppSVOD() && appCMSPresenter.isUserLoggedIn()) {
                    if (appCMSPresenter.isDownloadQualityScreenShowBefore()) {
                        appCMSPresenter.editDownload(UpdateDownloadImageIconAction.this.contentDatum, UpdateDownloadImageIconAction.this, true);
                    } else {
                        appCMSPresenter.showDownloadQualityScreen(UpdateDownloadImageIconAction.this.contentDatum, UpdateDownloadImageIconAction.this);
                    }
                } else {
                    if (appCMSPresenter.isAppSVOD()) {
                        if (appCMSPresenter.isUserLoggedIn()) {
                            appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.SUBSCRIPTION_REQUIRED,
                                    () -> {
                                        appCMSPresenter.setAfterLoginAction(() -> {
                                        });
                                    });
                        } else {
                            appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED,
                                    () -> {
                                        appCMSPresenter.setAfterLoginAction(() -> {
                                        });
                                    });
                        }
                    } else if (!(appCMSPresenter.isAppSVOD() && appCMSPresenter.isUserLoggedIn())) {
                        appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_REQUIRED,
                                () -> {
                                });
                    }
                }
                imageButton.setOnClickListener(null);
            };
        }

        @Override
        public void call(UserVideoDownloadStatus userVideoDownloadStatus) {
            if (userVideoDownloadStatus != null) {

                switch (userVideoDownloadStatus.getDownloadStatus()) {
                    case STATUS_FAILED:
                        appCMSPresenter.setDownloadInProgress(false);
                        appCMSPresenter.startNextDownload();
                        break;

                    case STATUS_PAUSED:
                        //
                        break;

                    case STATUS_PENDING:
                        appCMSPresenter.setDownloadInProgress(false);
                        appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false);
                        imageButton.setOnClickListener(null);
                        break;

                    case STATUS_RUNNING:
                        appCMSPresenter.setDownloadInProgress(true);
                        appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false);
                        imageButton.setOnClickListener(null);
                        break;

                    case STATUS_SUCCESSFUL:
                        appCMSPresenter.setDownloadInProgress(false);
                        appCMSPresenter.cancelDownloadIconTimerTask();
                        imageButton.setImageResource(R.drawable.ic_downloaded);
                        imageButton.setOnClickListener(null);
                        appCMSPresenter.notifyDownloadHasCompleted();
                        break;

                    case STATUS_INTERRUPTED:
                        appCMSPresenter.setDownloadInProgress(false);
                        imageButton.setImageResource(android.R.drawable.stat_sys_warning);
                        imageButton.setOnClickListener(null);
                        break;

                    default:
                        //Log.d(TAG, "No download Status available ");
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

    public CustomVideoPlayerView playerView(Context context, String videoId) {

        CustomVideoPlayerView videoPlayerView = new CustomVideoPlayerView(context);


       /* if (appCMSPresenter.videoPlayerView != null ){
            appCMSPresenter.videoPlayerView.releasePlayer();
            appCMSPresenter.videoPlayerView= null;
        }*/
        if (videoId != null) {
            videoPlayerView.setVideoUri(videoId, R.string.loading_video_text);
//            appCMSPresenter.videoPlayerView=videoPlayerView;
        }

        return videoPlayerView;
    }

    public static WebView getWebViewComponent(Context context, Module moduleAPI, Component component) {

        CustomWebView webView = new CustomWebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setAppCacheEnabled(true);

        int height = ((int) component.getLayout().getMobile().getHeight()) - 55;
        int width = BaseView.getDeviceWidth();
        String webViewUrl = "";
        if (moduleAPI != null && moduleAPI.getRawText() != null) {
            webViewUrl = moduleAPI.getRawText();
        }

        String html = "<iframe width=\"" + "100%" + "\" height=\"" + height + "px\" style=\"border: 0px solid #cccccc;\" src=\"" + webViewUrl + "\" ></iframe>";

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                context.startActivity(browserIntent);
                return true;
            }
        });

        webView.loadData(html, "text/html", "UTF-8");

        return webView;
    }


    private static class OnRemoveAllInternalEvent implements OnInternalEvent {
        final View removeAllButton;
        private final String moduleId;
        private List<OnInternalEvent> receivers;
        private String internalEventModuleId;

        OnRemoveAllInternalEvent(String moduleId, View removeAllButton) {
            this.moduleId = moduleId;
            this.removeAllButton = removeAllButton;
            receivers = new ArrayList<>();
            internalEventModuleId = moduleId;
        }

        @Override
        public void addReceiver(OnInternalEvent e) {
            receivers.add(e);
        }

        @Override
        public void sendEvent(InternalEvent<?> event) {
            for (OnInternalEvent internalEvent : receivers) {
                internalEvent.receiveEvent(null);
            }
            removeAllButton.setVisibility(View.GONE);
        }

        @Override
        public void receiveEvent(InternalEvent<?> event) {
            if (event != null && event.getEventData() != null
                    && event.getEventData() instanceof Integer) {
                int buttonStatus = (Integer) event.getEventData();
                if (buttonStatus == View.VISIBLE) {
                    removeAllButton.setVisibility(View.VISIBLE);
                } else if (buttonStatus == View.GONE) {
                    removeAllButton.setVisibility(View.GONE);
                }

                removeAllButton.requestLayout();
            }
        }

        @Override
        public void cancel(boolean cancel) {
            //
        }

        @Override
        public String getModuleId() {
            return internalEventModuleId;
        }

        @Override
        public void setModuleId(String moduleId) {
            internalEventModuleId = moduleId;
        }
    }

    private static class EmptyPStyledTextHandler extends StyledTextHandler {
        EmptyPStyledTextHandler(Style style) {
            super(style);
        }

        @Override
        public void beforeChildren(TagNode node, SpannableStringBuilder builder, SpanStack spanStack) {
            if (builder.length() == 0 || builder.charAt(builder.length() - 1) != '\n') {
                builder.append('\n');
            }
            super.beforeChildren(node, builder, spanStack);
        }
    }

    public static class CollectionGridItemViewCreator {
        final ViewCreator viewCreator;
        final Layout parentLayout;
        final boolean useParentLayout;
        final Component component;
        final AppCMSPresenter appCMSPresenter;
        final Module moduleAPI;
        final AppCMSAndroidModules appCMSAndroidModules;
        Settings settings;
        Map<String, AppCMSUIKeyType> jsonValueKeyMap;
        int defaultWidth;
        int defaultHeight;
        boolean useMarginsAsPercentages;
        boolean gridElement;
        String viewType;
        boolean createMultipleContainersForChildren;
        boolean createRoundedCorners;

        public CollectionGridItemViewCreator(final ViewCreator viewCreator,
                                             final Layout parentLayout,
                                             final boolean useParentLayout,
                                             final Component component,
                                             final AppCMSPresenter appCMSPresenter,
                                             final Module moduleAPI,
                                             final AppCMSAndroidModules appCMSAndroidModules,
                                             Settings settings,
                                             Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                             int defaultWidth,
                                             int defaultHeight,
                                             boolean useMarginsAsPercentages,
                                             boolean gridElement,
                                             String viewType,
                                             boolean createMultipleContainersForChildren,
                                             boolean createRoundedCorners) {
            this.viewCreator = viewCreator;
            this.parentLayout = parentLayout;
            this.useParentLayout = useParentLayout;
            this.component = component;
            this.appCMSPresenter = appCMSPresenter;
            this.moduleAPI = moduleAPI;
            this.appCMSAndroidModules = appCMSAndroidModules;
            this.settings = settings;
            this.jsonValueKeyMap = jsonValueKeyMap;
            this.defaultWidth = defaultWidth;
            this.defaultHeight = defaultHeight;
            this.useMarginsAsPercentages = useMarginsAsPercentages;
            this.gridElement = gridElement;
            this.viewType = viewType;
            this.createMultipleContainersForChildren = createMultipleContainersForChildren;
            this.createRoundedCorners = createRoundedCorners;
        }

        public View createView(Context context) {
            try {
                return viewCreator.createCollectionGridItemView(context,
                        parentLayout,
                        useParentLayout,
                        component,
                        appCMSPresenter,
                        moduleAPI,
                        appCMSAndroidModules,
                        settings,
                        jsonValueKeyMap,
                        defaultWidth,
                        defaultHeight,
                        useMarginsAsPercentages,
                        gridElement,
                        viewType,
                        createMultipleContainersForChildren,
                        createRoundedCorners);
            } catch (Exception e) {

            }
            return null;
        }
    }

    void setAutoPlayImage(Context context, Component component, String imgUrl) {
        int viewWidth = (int) BaseView.getViewWidth(context,
                component.getLayout(),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int viewHeight = (int) BaseView.getViewHeight(context,
                component.getLayout(),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (viewHeight > 0 && viewWidth > 0 && viewHeight > viewWidth) {
            if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView, imgUrl)) {
                Glide.with(context)
                        .load(imgUrl)
                        .override(viewWidth, viewHeight)
                        .into((ImageView) componentViewResult.componentView);
            }
        } else if (viewWidth > 0) {
            if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView, imgUrl)) {
                Glide.with(context)
                        .load(imgUrl)
                        .override(viewWidth, viewHeight)
                        .centerCrop()
                        .into((ImageView) componentViewResult.componentView);
            }
        } else {
            if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView, imgUrl)) {
                Glide.with(context)
                        .load(imgUrl)
                        .into((ImageView) componentViewResult.componentView);
            }
        }
        componentViewResult.componentView.setBackgroundColor(ContextCompat.getColor(context,
                android.R.color.transparent));
        componentViewResult.useWidthOfScreen = false;
    }
}

