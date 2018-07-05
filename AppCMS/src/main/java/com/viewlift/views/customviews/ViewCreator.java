package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.MetricAffectingSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.Player;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.viewlift.R;
import com.viewlift.casting.CastHelper;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.CreditBlock;
import com.viewlift.models.data.appcms.api.Fights;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.Mpeg;
import com.viewlift.models.data.appcms.api.Rounds;
import com.viewlift.models.data.appcms.api.Season_;
import com.viewlift.models.data.appcms.api.Tag;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.models.data.appcms.audio.AppCMSAudioDetailResult;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.models.data.appcms.downloads.UserVideoDownloadStatus;
import com.viewlift.models.data.appcms.history.UserVideoStatusResponse;
import com.viewlift.models.data.appcms.photogallery.PhotoGalleryGridInsetDecoration;
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
import com.viewlift.presenters.AppCMSVideoPlayerPresenter;
import com.viewlift.views.adapters.AppCMSArticleFeedViewAdapter;
import com.viewlift.views.adapters.AppCMSCarouselItemAdapter;
import com.viewlift.views.adapters.AppCMSDownloadQualityAdapter;
import com.viewlift.views.adapters.AppCMSFightSelectionAdapter;
import com.viewlift.views.adapters.AppCMSPlaylistAdapter;
import com.viewlift.views.adapters.AppCMSRosterAdapter;
import com.viewlift.views.adapters.AppCMSTraySeasonItemAdapter;
import com.viewlift.views.adapters.AppCMSUserWatHisDowAdapter;
import com.viewlift.views.adapters.AppCMSViewAdapter;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.utilities.ImageLoader;
import com.viewlift.views.utilities.ImageUtils;

import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.handlers.StyledTextHandler;
import net.nightwhistler.htmlspanner.handlers.attributes.AlignmentAttributeHandler;
import net.nightwhistler.htmlspanner.handlers.attributes.BorderAttributeHandler;
import net.nightwhistler.htmlspanner.handlers.attributes.StyleAttributeHandler;
import net.nightwhistler.htmlspanner.style.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

import static android.view.ViewGroup.FOCUS_BEFORE_DESCENDANTS;
import static com.viewlift.Utils.loadJsonFromAssets;
import static com.viewlift.views.customviews.CustomWebView.mFbLiveView;

//import org.htmlcleaner.TagNode;


/*
 * Created by viewlift on 5/5/17.
 */

/**
 * This class will utilize ingested AppCMS UI JSON and API data JSON files to dynamically create the views for entire pages,
 * including the modules and component child views.  It is used to create and return a PageView and will descend through
 * to inspect all the child elements in the input AppCMS UI JSON responses.  It will currently ignore the masthead and footer
 * modules.
 */
public class ViewCreator {
    private static final String TAG = "ViewCreator";
    private static final long SECS_TO_MSECS = 1000L;
    private CustomVideoPlayerView videoPlayerView;
    private static AppCMSVideoPageBinder videoPlayerViewBinder;
    private static AppCMSVideoPlayerPresenter appCMSVideoPlayerPresenter;
    private static VideoPlayerContent videoPlayerContent = new VideoPlayerContent();
    AppCMSPlaylistAdapter appCMSPlaylistAdapter;
    private boolean ignoreBinderUpdate;
    private ComponentViewResult componentViewResult;
    private HtmlSpanner htmlSpanner;
    private boolean isCastConnected;
    PhotoGalleryNextPreviousListener photoGalleryNextPreviousListener;
    private CountDownTimer countDownTimer = null;
    private final int countDownIntervalInMillis = 1000;

    public ViewCreator() {
        htmlSpanner = new HtmlSpanner();
        htmlSpanner.unregisterHandler("p");
        Style paragraphStyle = new Style();
        TagNodeHandler pHandler = new BorderAttributeHandler(new StyleAttributeHandler
                (new AlignmentAttributeHandler(new EmptyPStyledTextHandler(paragraphStyle))));
        htmlSpanner.registerHandler("p", pHandler);
    }

    static void setViewWithShowSubtitle(Context context, ContentDatum data, View view,
                                        boolean isJumbotron) {
        StringBuilder subtitleSb;

        if (isJumbotron) {
            subtitleSb = new StringBuilder();
            String primaryCategory = data.getGist().getPrimaryCategory() != null ?
                    data.getGist().getPrimaryCategory().getTitle() : null;

            if (!TextUtils.isEmpty(primaryCategory)) {
                subtitleSb.append(primaryCategory.toUpperCase());
            }
        } else {
            int totalEpisodes = getTotalNumberOfEpisodes(data);
            subtitleSb = new StringBuilder(String.valueOf(totalEpisodes));
            subtitleSb.append(context.getString(R.string.blank_separator));
            subtitleSb.append(context.getResources().getQuantityString(R.plurals.episode_subtitle_text,
                    totalEpisodes));

            String primaryCategory = data.getGist().getPrimaryCategory() != null ?
                    data.getGist().getPrimaryCategory().getTitle() : null;

            subtitleSb.append(context.getString(R.string.text_separator));

            if (!TextUtils.isEmpty(primaryCategory)) {
                subtitleSb.append(primaryCategory.toUpperCase());
            }
        }

        ((TextView) view).setText(subtitleSb.toString());
        view.setAlpha(0.6f);
    }

    private static int getTotalNumberOfEpisodes(ContentDatum data) {
        int totalEpisodes = 0;
        List<Season_> seasons = data.getSeason();
        int numSeasons = seasons.size();
        for (int i = 0; i < numSeasons; i++) {
            if (seasons.get(i).getEpisodes() != null) {
                totalEpisodes += seasons.get(i).getEpisodes().size();
            }
        }

        return totalEpisodes;
    }

    /**
     * Fix for JM-26
     */
    static void setViewWithSubtitle(Context context, ContentDatum data, View view) {

        long durationInSeconds = data.getGist().getRuntime();

        long minutes = durationInSeconds / 60;
        long seconds = durationInSeconds % 60;

        String year = data.getGist().getYear();
        String primaryCategory =
                data.getGist().getPrimaryCategory() != null ?
                        data.getGist().getPrimaryCategory().getTitle() :
                        null;
//        boolean appendFirstSep = minutes > 0
//                && (!TextUtils.isEmpty(year) || !TextUtils.isEmpty(primaryCategory));
//        boolean appendSecondSep = (minutes > 0 || !TextUtils.isEmpty(year))
//                && !TextUtils.isEmpty(primaryCategory);

        StringBuilder infoText = new StringBuilder();

        if (minutes == 1) {
            infoText.append("0").append(minutes).append(" ").append(context.getString(R.string.min_abbreviation));
        } else if (minutes > 1 && minutes < 10) {
            infoText.append("0").append(minutes).append(" ").append(context.getString(R.string.mins_abbreviation));
        } else if (minutes >= 10) {
            infoText.append(minutes).append(" ").append(context.getString(R.string.mins_abbreviation));
        }

        if (seconds == 1) {
            infoText.append(" ").append("0").append(seconds).append(" ").append(context.getString(R.string.sec_abbreviation));
        } else if (seconds > 1 && seconds < 10) {
            infoText.append(" ").append("0").append(seconds).append(" ").append(context.getString(R.string.secs_abbreviation));
        } else if (seconds >= 10) {
            infoText.append(" ").append(seconds).append(" ").append(context.getString(R.string.secs_abbreviation));
        }

        if (!TextUtils.isEmpty(year)) {
            infoText.append(context.getString(R.string.text_separator));
            infoText.append(year);
        }

        if (!TextUtils.isEmpty(primaryCategory)) {
            infoText.append(context.getString(R.string.text_separator));
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

   /* public static void enableFullScreenMode() {
        if (videoPlayerView != null) {
            videoPlayerView.enableFullScreenMode();
        }
    }

    public static boolean shouldPlayVideoWhenReady() {
        if (videoPlayerView != null) {
            return videoPlayerView.shouldPlayWhenReady();
        }
        return false;
    }

    public static void pausePlayer() {
        if (videoPlayerView != null && videoPlayerContent != null) {
            videoPlayerView.pausePlayer();
            videoPlayerView.releasePlayer();
            videoPlayerContent.videoPlayTime = videoPlayerView.getCurrentPosition() / SECS_TO_MSECS;
        }
    }

    public static void startPlayer(AppCMSPresenter presenter) {
        if (videoPlayerView != null &&
                !CastServiceProvider.getInstance(presenter.getCurrentActivity()).isCastingConnected()) {
            videoPlayerView.startPlayer(true);
        }
    }

    public static void resumePlayer(AppCMSPresenter appCMSPresenter, Context context) {
        if (videoPlayerView != null && videoPlayerContent != null) {
            videoPlayerView.setAppCMSPresenter(appCMSPresenter);
            videoPlayerView.enableController();
            if (videoPlayerViewBinder != null) {
                if (!TextUtils.isEmpty(videoPlayerContent.ccUrl)) {
                    videoPlayerView.setClosedCaptionEnabled(appCMSPresenter.getClosedCaptionPreference());
                    videoPlayerView.getPlayerView().getSubtitleView()
                            .setVisibility(appCMSPresenter.getClosedCaptionPreference()
                                    ? View.VISIBLE
                                    : View.GONE);
                }
                videoPlayerView.setUri(Uri.parse(videoPlayerContent.videoUrl),
                        !TextUtils.isEmpty(videoPlayerContent.ccUrl) ? Uri.parse(videoPlayerContent.ccUrl) : null);
                //Log.i(TAG, "Playing video: " + title);
            }
            videoPlayerView.setCurrentPosition(videoPlayerContent.videoPlayTime * SECS_TO_MSECS);
        }
    }*/

    /*
        public static VideoPlayerView playerView(Context context,
                                                 AppCMSPresenter presenter,
                                                 String videoUrl,
                                                 String ccUrl,
                                                 String filmId,
                                                 long watchedTime) {
            if (videoPlayerContent == null) {
                videoPlayerContent = new VideoPlayerContent();
            }

        if (appCMSVideoPlayerPresenter == null) {
            appCMSVideoPlayerPresenter = new AppCMSVideoPlayerPresenter();
        }

        videoPlayerContent.videoUrl = videoUrl;
        videoPlayerContent.ccUrl = ccUrl;

        if (videoPlayerView == null) {
            videoPlayerView = new VideoPlayerView(context, presenter);
            videoPlayerView.init(context);
            videoPlayerView.applyTimeBarColor(Color.parseColor(ViewCreator.getColor(context,
                    presenter.getAppCtaBackgroundColor())));
        } else if (videoPlayerView.getParent() != null &&
                videoPlayerView.getParent() instanceof ViewGroup) {
            ((ViewGroup) videoPlayerView.getParent()).removeView(videoPlayerView);
        }

        boolean resetWatchTime = false;
        long currentWatchedTime = videoPlayerContent.videoPlayTime;
        if (filmId != null && !filmId.equals(videoPlayerView.getFilmId())) {
            resetWatchTime = true;
        } else {
            currentWatchedTime = videoPlayerView.getCurrentPosition();
        }

        if (resetWatchTime) {
            videoPlayerView.setUri(Uri.parse(videoUrl), null);
        }

        if (!CastServiceProvider.getInstance(presenter.getCurrentActivity()).isCastingConnected()) {
            videoPlayerView.startPlayer(true);
        }

        videoPlayerView.setFilmId(filmId);
        videoPlayerView.getPlayerView().setControllerAutoShow(true);
        videoPlayerView.getPlayerView().setControllerHideOnTouch(true);
        videoPlayerView.getPlayerView().setVisibility(View.VISIBLE);

        if (resetWatchTime) {
            videoPlayerView.getPlayerView().getPlayer().seekTo(watchedTime);
        }

        appCMSVideoPlayerPresenter.updateBinder(context,
                presenter,
                videoPlayerView,
                new AppCMSVideoPlayerPresenter.OnClosePlayerEvent() {
                    @Override
                    public void closePlayer() {

                    }

                    @Override
                    public void onMovieFinished() {

                    }

                    @Override
                    public void onRemotePlayback(long currentPosition, int castingMode, boolean sentBeaconPlay, Action1<CastHelper.OnApplicationEnded> onApplicationEndedAction) {

                    }
                },
                new AppCMSVideoPlayerPresenter.OnUpdateContentDatumEvent() {
                    @Override
                    public void updateContentDatum(ContentDatum contentDatum) {

                    }

                    @Override
                    public ContentDatum getCurrentContentDatum() {
                        return null;
                    }

                    @Override
                    public List<String> getCurrentRelatedVideoIds() {
                        return null;
                    }
                },
                videoPlayerViewBinder,
                0,
                false,
                null,
                resetWatchTime);

        return videoPlayerView;
    }

    public static void applyChromecastButtonToFullScreenPlayer(ImageButton chromecastButton) {
        if (videoPlayerView != null) {
            videoPlayerView.setChromecastButton(chromecastButton);
        }
    }

    public static boolean playerViewFullScreenEnabled() {
        if (videoPlayerContent != null) {
            return videoPlayerContent.fullScreenEnabled;
        }
        if (videoPlayerView != null) {
            return videoPlayerView.fullScreenModeEnabled();
        }
        return false;
    }

    public static void clearPlayerView() {
        if (videoPlayerView != null) {
            videoPlayerView.stopPlayer();
            videoPlayerView.releasePlayer();
        }
        videoPlayerView = null;
    }

    public static void resetFullPlayerMode(Context context,
                                           AppCMSPresenter appCMSPresenter) {
        if (videoPlayerContent != null) {
            videoPlayerContent.fullScreenEnabled = false;
        }
        if (BaseView.isTablet(context)) {
            appCMSPresenter.unrestrictPortraitOnly();
        }
    }
 */
    public static void cancelBeaconPing() {
        if (appCMSVideoPlayerPresenter != null) {
            appCMSVideoPlayerPresenter.stop();
        }
    }

    public static void setTypeFace(Context context,
                                   AppCMSPresenter appCMSPresenter,
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
                    face = appCMSPresenter.getBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.opensans_bold_ttf));
                        appCMSPresenter.setBoldTypeFace(face);
                    }
                    break;

                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = appCMSPresenter.getSemiBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.opensans_semibold_ttf));
                        appCMSPresenter.setSemiBoldTypeFace(face);
                    }
                    break;

                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = appCMSPresenter.getExtraBoldTypeFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.opensans_extrabold_ttf));
                        appCMSPresenter.setExtraBoldTypeFace(face);
                    }
                    break;

                case PAGE_TEXT_BLACK_ITALIC_KEY:
                    face = Typeface.defaultFromStyle(Typeface.ITALIC);
                    break;

                default:
                    face = appCMSPresenter.getRegularFontFace();
                    if (face == null) {
                        face = Typeface.createFromAsset(context.getAssets(),
                                context.getString(R.string.opensans_regular_ttf));
                        appCMSPresenter.setRegularFontFace(face);
                    }
                    break;
            }

            textView.setTypeface(face);
        }
    }

    /* public static void openFullScreenVideoPlayer(Activity activity) {
         if (videoPlayerView != null && videoPlayerView.getParent() != null
                 && videoPlayerView.getParent() instanceof ViewGroup) {
             PageView pageViewAncestor = videoPlayerView.getPageView();
             if (pageViewAncestor != null) {
                 pageViewAncestor.openViewInFullScreen(videoPlayerView,
                         (ViewGroup) videoPlayerView.getParent());
                 videoPlayerView.showChromecastLiveVideoPlayer(true);
                 if (videoPlayerView.shouldPlayOnReattach() &&
                         !CastServiceProvider.getInstance(activity).isCastingConnected()) {
                     videoPlayerView.startPlayer(true);
                 } else {
                     videoPlayerView.resumePlayer();
                 }
             }
             videoPlayerView.disableFullScreenMode();
         }

         if (videoPlayerContent != null) {
             videoPlayerContent.fullScreenEnabled = true;
         }
     }

     public static void closeFullScreenVideoPlayer(Activity activity) {
         if (videoPlayerView != null) {
             PageView pageViewAncestor = videoPlayerView.getPageView();
             if (pageViewAncestor != null) {
                 pageViewAncestor.closeViewFromFullScreen(videoPlayerView,
                         (ViewGroup) videoPlayerView.getParent());
                 videoPlayerView.showChromecastLiveVideoPlayer(false);
                 if (videoPlayerView.shouldPlayOnReattach() &&
                         !CastServiceProvider.getInstance(activity).isCastingConnected()) {
                     videoPlayerView.startPlayer(true);
                 } else {
                     videoPlayerView.resumePlayer();
                 }
             }

             videoPlayerView.enableFullScreenMode();
         }

         if (videoPlayerContent != null) {
             videoPlayerContent.fullScreenEnabled = false;
         }
     }
 */

    public static CustomWebView getWebViewComponent(Context context, Module moduleAPI, Component component, String key, AppCMSPresenter appCMSPresenter, AppCMSUIKeyType moduleType) {
        CustomWebView webView = new CustomWebView(context, appCMSPresenter);
        String webViewUrl, html;
        if (moduleType == AppCMSUIKeyType.PAGE_AC_WEB_FRAME_03_KEY) {
            webViewUrl = moduleAPI.getRawText();
            webView.loadWebVideoUrl(appCMSPresenter, webViewUrl);
        } else if (moduleAPI != null && moduleAPI.getRawText() != null) {
            int height = ((int) component.getLayout().getMobile().getHeight()) - 55;
            webViewUrl = moduleAPI.getRawText();
            html = "<iframe width=\"" + "100%" + "\" height=\"" + height + "px\" style=\"border: 0px solid #cccccc;\" src=\"" + webViewUrl + "\" ></iframe>";
            webView.loadURLData(context, appCMSPresenter, html, key);
        } else if (moduleAPI != null && moduleAPI.getContentData() != null
                && moduleAPI.getContentData().get(0).getGist() != null
                && moduleAPI.getContentData().get(0).getGist().getPermalink() != null) {
            webViewUrl = context.getString(R.string.app_cms_article_api,
                    appCMSPresenter.getAppCMSMain().getDomainName(),
                    moduleAPI.getContentData().get(0).getGist().getPermalink());
            webView.loadURL(context, appCMSPresenter, webViewUrl.replaceAll("\\s+", ""), key);
        }
        return webView;
    }

    private void enablePhotoGalleryButtons(Boolean prevButton, boolean nextButton, PageView pageView, AppCMSPresenter appCMSPresenter, String position) {
        if ((Button) pageView.findChildViewById(R.id.photo_gallery_next_button) != null) {
            ((Button) pageView.findChildViewById(R.id.photo_gallery_next_button)).setBackgroundColor(nextButton ? appCMSPresenter.getBrandPrimaryCtaColor() : Color.parseColor("#c8c8c8"));
            ((Button) pageView.findChildViewById(R.id.photo_gallery_next_button)).setEnabled(nextButton);
        }
        if ((Button) pageView.findChildViewById(R.id.photo_gallery_prev_button) != null) {
            ((Button) pageView.findChildViewById(R.id.photo_gallery_prev_button)).setBackgroundColor(prevButton ? appCMSPresenter.getBrandPrimaryCtaColor() : Color.parseColor("#c8c8c8"));
            ((Button) pageView.findChildViewById(R.id.photo_gallery_prev_button)).setEnabled(prevButton);
        }
        if ((TextView) pageView.findChildViewById(R.id.photo_gallery_image_count) != null) {
            ((TextView) pageView.findChildViewById(R.id.photo_gallery_image_count)).setText("" + position);
        }
    }

    /**
     * This will prepend a '#' character to the beginning of a color string if one is missing.
     *
     * @param context This is the context value that created UI components should use
     * @param color   This is the color string to prepend the '#' value
     * @return A string value with one prepended '#' character
     */
    public static String getColor(Context context, String color) {
        if (color != null && color.indexOf(context.getString(R.string.color_hash_prefix)) != 0) {
            return context.getString(R.string.color_hash_prefix) + color;
        }
        return color != null ? color : "#d8d8d8";
    }

    /**
     * This will prepend a '#' character and a hex value of the alpha or opacity value to a color string if the '#' character is missing
     *
     * @param context          This is the context value that created UI components should use
     * @param baseColorCode    This is the color string to prepend the '#' value and the alpha or opacity value
     * @param opacityColorCode This is the opacity value to prepend to the color value
     * @return The original color value or a the color prepended with a '#' character followed by the alpha value
     */
    public static String getColorWithOpacity(Context context, String baseColorCode, int opacityColorCode) {
        if (baseColorCode.indexOf(context.getString(R.string.color_hash_prefix)) != 0) {
            return context.getString(R.string.color_hash_prefix) + opacityColorCode + baseColorCode;
        }
        return baseColorCode;
    }

    private void updateVideoPlayerBinder(AppCMSPresenter appCMSPresenter,
                                         ContentDatum contentDatum) {
        if (!ignoreBinderUpdate ||
                (videoPlayerViewBinder != null &&
                        videoPlayerViewBinder.getContentData() != null &&
                        videoPlayerViewBinder.getContentData().getGist() != null &&
                        videoPlayerViewBinder.getContentData().getGist().getId() != null &&
                        contentDatum != null &&
                        contentDatum.getGist() != null &&
                        !videoPlayerViewBinder.getContentData().getGist().getId().equals(contentDatum.getGist().getId()))) {
            if (videoPlayerViewBinder == null) {
                videoPlayerViewBinder =
                        appCMSPresenter.getDefaultAppCMSVideoPageBinder(contentDatum,
                                -1,
                                contentDatum.getContentDetails().getRelatedVideoIds(),
                                false,
                                false,  /** TODO: Replace with a value that is true if the video is a trailer */
                                !appCMSPresenter.isAppSVOD(),
                                appCMSPresenter.getAppAdsURL(contentDatum.getGist().getPermalink()),
                                appCMSPresenter.getAppBackgroundColor());
            } else {
                int currentlyPlayingIndex = -1;
                if (videoPlayerViewBinder.getRelateVideoIds() != null &&
                        videoPlayerViewBinder.getRelateVideoIds().contains(contentDatum.getGist().getId())) {
                    currentlyPlayingIndex = videoPlayerViewBinder.getRelateVideoIds().indexOf(contentDatum.getGist().getId());
                } else {
                    videoPlayerViewBinder.setPlayerState(Player.STATE_IDLE);
                    videoPlayerViewBinder.setRelateVideoIds(contentDatum.getContentDetails().getRelatedVideoIds());
                }
                if (videoPlayerViewBinder.getContentData().getGist().getId().equals(contentDatum.getGist().getId())) {
                    currentlyPlayingIndex = videoPlayerViewBinder.getCurrentPlayingVideoIndex();
                }
                videoPlayerViewBinder.setCurrentPlayingVideoIndex(currentlyPlayingIndex);
                videoPlayerViewBinder.setContentData(contentDatum);
            }
        }
        ignoreBinderUpdate = false;
    }

    public void setIgnoreBinderUpdate(boolean ignoreBinderUpdate) {
        this.ignoreBinderUpdate = ignoreBinderUpdate;
    }

   /* @SuppressWarnings({"unchecked", "ConstantConditions"})
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
                //
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
                    (jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_CAROUSEL_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_EVENT_CAROUSEL_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_VIDEO_PLAYER_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_TRAY_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_AUDIO_TRAY_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_PHOTO_TRAY_MODULE_KEY)) {
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

                    String viewType = module.getView();

                    AppCMSUIKeyType parentViewType = jsonValueKeyMap.get(viewType);

                    if (parentViewType == null) {
                        parentViewType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                    }

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
                                    component.setBlockName(module.getBlockName());
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
                                    CustomVideoPlayerView videoPlayerViewSingle = null;
                                    if (appCMSPresenter.getVideoPlayerViewCache(moduleAPI.getId() + component.getKey()) != null) {
                                        videoPlayerViewSingle = appCMSPresenter.getVideoPlayerViewCache(moduleAPI.getId() + component.getKey());
                                    } else {
                                        videoPlayerViewSingle = null;
                                    }
                                    if (videoId != null) {
                                        ((FrameLayout) view).removeAllViews();
                                        if (videoPlayerViewSingle != null) {

                                            if (videoPlayerViewSingle.getParent() != null)
                                                ((ViewGroup) videoPlayerViewSingle.getParent()).removeView(videoPlayerViewSingle);

                                            ((FrameLayout) view).addView(videoPlayerViewSingle);
                                            videoPlayerViewSingle.resumePlayerLastState();

                                        } else {
                                            videoPlayerViewSingle = playerView(context, videoId, moduleAPI.getId() + component.getKey(), appCMSPresenter);
                                            ((FrameLayout) view).addView(videoPlayerViewSingle);
                                        }
                                        appCMSPresenter.videoPlayerView = videoPlayerViewSingle;
                                        videoPlayerViewSingle.checkVideoStatus();
                                        appCMSPresenter.setVideoPlayerViewCache(moduleAPI.getId() + component.getKey(), videoPlayerViewSingle);
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

                                    if (moduleAPI != null && moduleAPI.getRawText() != null) {
                                        view.setVisibility(View.VISIBLE);
                                    } else {
                                        view.setVisibility(View.INVISIBLE);
                                    }
                                    CustomWebView webView = null;
                                    ((FrameLayout) view).removeAllViews();
                                    if (appCMSPresenter.getWebViewCache(moduleAPI.getId() + component.getKey()) != null) {
                                        webView = appCMSPresenter.getWebViewCache(moduleAPI.getId() + component.getKey());
                                    }
                                    if (webView != null) {
                                        if (webView.getParent() != null)
                                            ((ViewGroup) webView.getParent()).removeView(webView);
                                        ((FrameLayout) view).addView(webView);
                                    } else {
                                        webView = getWebViewComponent(context, moduleAPI, component, moduleAPI.getId() + component.getKey(), appCMSPresenter);
                                        ((FrameLayout) view).addView(webView);
                                    }
                                    (view).setVisibility(View.VISIBLE);

                                } else if (componentType == AppCMSUIKeyType.PAGE_ARTICLE_WEB_VIEW_KEY) {

                                    if (moduleAPI != null && moduleAPI.getRawText() != null) {
                                        view.setVisibility(View.VISIBLE);
                                    } else {
                                        view.setVisibility(View.INVISIBLE);
                                    }
                                    CustomWebView webView = null;
                                    ((FrameLayout) view).removeAllViews();
                                    *//*if (appCMSPresenter.getWebViewCache(moduleAPI.getId() + component.getKey()) != null) {
                                        webView = appCMSPresenter.getWebViewCache(moduleAPI.getId() + component.getKey());
                                    }*//*
                                    if (webView != null) {
                                        if (webView.getParent() != null)
                                            ((ViewGroup) webView.getParent()).removeView(webView);
                                        ((FrameLayout) view).addView(webView);
                                    } else {
                                        webView = getWebViewComponent(context, moduleAPI, component, moduleAPI.getId() + component.getKey(), appCMSPresenter);
                                        ((FrameLayout) view).addView(webView);
                                    }
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

                                                final String watchVideoTrailerAction = component.getAction();

                                                if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                                        watchVideoTrailerAction,
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
                                        } else if (moduleAPI.getContentData().get(0).getShowDetails() != null &&
                                                moduleAPI.getContentData().get(0).getShowDetails().getTrailers() != null &&
                                                !moduleAPI.getContentData().get(0).getShowDetails().getTrailers().isEmpty() &&
                                                moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0) != null &&
                                                moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getPermalink() != null &&
                                                moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getId() != null &&
                                                moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getVideoAssets() != null) {
                                            view.setOnClickListener(v -> {
                                                String[] extraData = new String[3];
                                                extraData[0] = moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getPermalink();
                                                extraData[1] = moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getVideoAssets().getHls();
                                                extraData[2] = moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getId();

                                                final String watchTrailerVideoAction = component.getAction();

                                                if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getPermalink(),
                                                        watchTrailerVideoAction,
                                                        moduleAPI.getContentData().get(0).getGist().getTitle(),
                                                        extraData,
                                                        moduleAPI.getContentData().get(0),
                                                        false,
                                                        -1,
                                                        null)) {
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
                                                            moduleAPI.getContentData().get(0).getGist().getId(),
                                                            currentPlayingIndex, relatedVideoIds,
                                                            moduleAPI.getContentData().get(0).getGist().getWatchedTime(),
                                                            component.getAction());
                                                }
                                            }
                                        });
                                    } else if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_SHARE_KEY) {
                                        if (moduleAPI.getContentData().get(0).getGist().getContentType().equalsIgnoreCase("AUDIO")) {
                                            view.setVisibility(View.GONE);

                                        }

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

                                                final String shareVideoAction = component.getAction();

                                                if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                                        shareVideoAction,
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
                                            int radiusDifference = 7;
                                            if (BaseView.isTablet(context)) {
                                                radiusDifference = 4;
                                            }
                                            if (moduleAPI.getContentData().get(0).getGist().getMediaType() != null &&
                                                    moduleAPI.getContentData().get(0).getGist().getMediaType().toLowerCase().contains(context.getString(R.string.media_type_audio).toLowerCase())) {
                                                radiusDifference = 5;
                                                if (BaseView.isTablet(context)) {
                                                    radiusDifference = 3;
                                                }
                                            }
                                            String userId = appCMSPresenter.getLoggedInUser();
                                            appCMSPresenter.getUserVideoDownloadStatus(
                                                    moduleAPI.getContentData().get(0).getGist().getId(), new UpdateDownloadImageIconAction((ImageButton) view, appCMSPresenter,
                                                            moduleAPI.getContentData().get(0), userId, radiusDifference,
                                                            moduleAPI.getId()), userId);
                                            view.setTag(moduleAPI.getContentData().get(0).getGist().getId());

                                        }
                                        if (appCMSPresenter.getAppCMSMain().getFeatures() != null &&
                                                appCMSPresenter.getAppCMSMain().getFeatures().isMobileAppDownloads()) {
                                            view.setVisibility(View.VISIBLE);
                                        } else {
                                            view.setVisibility(View.GONE);
                                        }
                                    } else if (componentKey == AppCMSUIKeyType.PAGE_ADD_TO_WATCHLIST_KEY
                                            && view != null) {
                                        AppCMSUIKeyType moduleType = jsonValueKeyMap.get(moduleAPI.getModuleType());
                                        if (moduleType == null) {
                                            moduleType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                                        }

                                        List<String> filmIds = new ArrayList<>();
                                        //TODO- below is to add episodes of shows/series
                                       *//* if (parentViewType == AppCMSUIKeyType.PAGE_API_SHOWDETAIL_MODULE_KEY &&
                                                moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0) != null &&
                                                moduleAPI.getContentData().get(0).getSeason() != null &&
                                                !moduleAPI.getContentData().get(0).getSeason().isEmpty()) {
                                            List<Season_> seasons = moduleAPI.getContentData().get(0).getSeason();
                                            int numSeasons = seasons.size();
                                            for (int i = 0; i < numSeasons; i++) {
                                                if (seasons.get(i).getEpisodes() != null &&
                                                        !seasons.get(i).getEpisodes().isEmpty()) {
                                                    List<ContentDatum> episodes = seasons.get(i).getEpisodes();
                                                    int numEpisodes = episodes.size();
                                                    for (int j = 0; j < numEpisodes; j++) {
                                                        if (episodes.get(j).getGist() != null &&
                                                                episodes.get(j).getGist().getId() != null) {
                                                            filmIds.add(episodes.get(j).getGist().getId());
                                                        }
                                                    }
                                                }
                                            }
                                        } else *//*
                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0).getGist() != null &&
                                                moduleAPI.getContentData().get(0).getGist().getId() != null) {
                                            filmIds.add(moduleAPI.getContentData().get(0).getGist().getId());
                                        }
                                        UpdateImageIconAction updateImageIconAction =
                                                new UpdateImageIconAction((ImageButton) componentViewResult.componentView,
                                                        appCMSPresenter,
                                                        filmIds,
                                                        moduleAPI.getContentData().get(0));
                                        boolean filmsAdded = true;
                                        for (String filmId : filmIds) {
                                            filmsAdded &= appCMSPresenter.isFilmAddedToWatchlist(filmId);
                                        }
                                        updateImageIconAction.updateWatchlistResponse(filmsAdded);
                                        ((ImageButton) view).setScaleType(ImageView.ScaleType.FIT_CENTER);
                                        view.setVisibility(View.VISIBLE);

                                        //TODO- of now set visibility gone for wathclist button as this is on hold
                                        if (module.getBlockName().equalsIgnoreCase("playlistDetail01")) {
                                            view.setVisibility(View.GONE);

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
                                        if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0) != null &&
                                                moduleAPI.getContentData().get(0).getSeason() != null) {

                                            setViewWithShowSubtitle(context,
                                                    moduleAPI.getContentData().get(0), view, false);
                                        }
                                    } else if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_AGE_LABEL_KEY) {
                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty() &&
                                                moduleAPI.getContentData().get(0) != null &&
                                                moduleAPI.getContentData().get(0).getParentalRating() != null &&
                                                !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getParentalRating())) {
                                            String parentalRating = moduleAPI.getContentData().get(0).getParentalRating();
                                            ((TextView) view).setText(parentalRating);
                                            ((TextView) view).setSingleLine(true);
                                            componentViewResult.componentView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                            ((TextView) view).setGravity(Gravity.CENTER);
                                            view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
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
                                                            appCMSPresenter.getBrandPrimaryCtaColor(),
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
                                            } else if (jsonValueKeyMap.get(module.getView()) == AppCMSUIKeyType.PAGE_WATCHLIST_01_MODULE_KEY ||
                                                    jsonValueKeyMap.get(module.getView()) == AppCMSUIKeyType.PAGE_WATCHLIST_02_MODULE_KEY) {
                                                ((TextView) view).setText(R.string.app_cms_page_watchlist_title);
                                            }*//* else if (jsonValueKeyMap.get(module.getView()) == AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY) {
                                                ((TextView) view).setText(R.string.app_cms_page_download_title);
                                            }*//* else if (jsonValueKeyMap.get(module.getView()) == AppCMSUIKeyType.PAGE_HISTORY_01_MODULE_KEY ||
                                                    jsonValueKeyMap.get(module.getView()) == AppCMSUIKeyType.PAGE_HISTORY_02_MODULE_KEY) {
                                                ((TextView) view).setText(R.string.app_cms_page_history_title);
                                            }
                                        }
                                    }
                                } else if (componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY) {
                                    if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY) {
                                        int placeHolderImage = *//*BaseView.isLandscape(context) ? R.drawable.vid_image_placeholder_land : R.drawable.vid_image_placeholder_port;*//*R.drawable.vid_image_placeholder_land;
                                        ((ImageView) componentViewResult.componentView).setScaleType(ImageView.ScaleType.FIT_XY);
                                        ((ImageView) view).setImageResource(placeHolderImage);
                                        if (moduleAPI.getContentData() != null &&
                                                !moduleAPI.getContentData().isEmpty()) {
                                            int viewWidth = view.getWidth();
                                            int viewHeight = view.getHeight();
                                            if (viewHeight > 0 && viewWidth > 0 && viewHeight > viewWidth) {
                                                String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                                        moduleAPI.getContentData().get(0).getGist().getPosterImageUrl(),
                                                        viewWidth,
                                                        viewHeight);
                                                if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {
                                                    Glide.with(context)
                                                            .load(imageUrl)
                                                            .apply(new RequestOptions().override(viewWidth, viewHeight))
                                                            .into((ImageView) view);
                                                } else {
                                                    if (viewHeight > viewWidth) {
                                                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                                                        ((ImageView) view).setImageResource(R.drawable.vid_image_placeholder_port);

                                                    } else {
                                                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                                                        ((ImageView) view).setImageResource(R.drawable.vid_image_placeholder_land);
                                                    }
                                                }
                                            } else if (viewWidth > 0 && viewHeight > 0) {
                                                String videoImageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                                        moduleAPI.getContentData().get(0).getGist().getVideoImageUrl(),
                                                        viewWidth,
                                                        viewHeight);

                                                if (videoImageUrl != null && !TextUtils.isEmpty(videoImageUrl)) {
                                                    Glide.with(context)
                                                            .load(videoImageUrl)
                                                            .apply(new RequestOptions().override(viewWidth, viewHeight))
                                                            .into((ImageView) view);
                                                } else {
                                                    if (viewHeight > viewWidth) {
                                                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                                                        ((ImageView) view).setImageResource(R.drawable.vid_image_placeholder_port);

                                                    } else {
                                                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                                                        ((ImageView) view).setImageResource(R.drawable.vid_image_placeholder_land);
                                                    }
                                                }
                                            } else if (viewHeight > 0) {
                                                String videoImageUrl = moduleAPI.getContentData().get(0).getGist().getVideoImageUrl();

                                                if (videoImageUrl != null && !TextUtils.isEmpty(videoImageUrl)) {
                                                    Glide.with(context)
                                                            .load(videoImageUrl)
                                                            .apply(new RequestOptions().override(Target.SIZE_ORIGINAL, viewHeight).centerCrop())
                                                            .into((ImageView) view);
                                                } else {
                                                    if (viewHeight > viewWidth) {
                                                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                                                        ((ImageView) view).setImageResource(R.drawable.vid_image_placeholder_port);

                                                    } else {
                                                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                                                        ((ImageView) view).setImageResource(R.drawable.vid_image_placeholder_land);
                                                    }
                                                }
                                            } else {
                                                String videoImageUrl = moduleAPI.getContentData().get(0).getGist().getVideoImageUrl();

                                                if (videoImageUrl != null && !TextUtils.isEmpty(videoImageUrl)) {
                                                    Glide.with(context)
                                                            .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                                                            .into((ImageView) view);
                                                } else {
                                                    if (viewHeight > viewWidth) {
                                                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                                                        ((ImageView) view).setImageResource(R.drawable.vid_image_placeholder_port);

                                                    } else {
                                                        ((ImageView) view).setScaleType(ImageView.ScaleType.FIT_XY);
                                                        ((ImageView) view).setImageResource(R.drawable.vid_image_placeholder_land);
                                                    }
                                                }

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
                                        if (numDots <= 1) {
                                            view.setVisibility(View.GONE);
                                        } else {
                                            componentViewResult.componentView.setVisibility(View.VISIBLE);
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
                                                                }else if (paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_sslcommerz_payment_processor))) {
                                                                    ((TextView) settingsView).setText(context.getString(R.string.subscription_sslcommerz_payment_processor_friendly));
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
                                                            if (appCMSPresenter.shouldDisplaySubscriptionCancelButton() &&
                                                                    appCMSPresenter.isUserSubscribed()) {
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
                                        switch (componentKey) {
                                            case PAGE_AUTOPLAY_TOGGLE_BUTTON_KEY:
                                                ((Switch) view).setChecked(appCMSPresenter
                                                        .getAutoplayEnabledUserPref(context));
                                                break;

                                            case PAGE_SD_CARD_FOR_DOWNLOADS_TOGGLE_BUTTON_KEY:
                                                ((Switch) view).setChecked(appCMSPresenter
                                                        .getUserDownloadLocationPref());
                                                if (appCMSPresenter.isExternalStorageAvailable()) {
                                                    view.setEnabled(true);
                                                    appCMSPresenter.setUserDownloadLocationPref(true);
                                                } else {
                                                    view.setEnabled(false);
                                                    ((Switch) view).setChecked(false);
                                                    appCMSPresenter.setUserDownloadLocationPref(false);
                                                }

                                                break;

                                            case PAGE_DOWNLOAD_VIA_CELLULAR_NETWORK_KEY:
                                                componentViewResult.componentView.setEnabled(true);
                                                ((Switch) componentViewResult.componentView).setChecked(appCMSPresenter.getDownloadOverCellularEnabled());
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
            pageView.notifyAdapterDataSetChanged();
            forceRedrawOfAllChildren(pageView);
        }
    }*/

    private void forceRedrawOfAllChildren(ViewGroup viewGroup) {
        viewGroup.invalidate();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                forceRedrawOfAllChildren((ViewGroup) v);
            } else {
                v.invalidate();
            }
        }
    }

    /**
     * This is the entry point for the creation of a new page that can be generated by ViewCreator.
     *
     * @param context              This is the context value that created UI components should use
     * @param appCMSPageUI         This is the Page UI response used for the creation of the UI
     * @param appCMSPageAPI        This is the Page API data used to populate the UI fields specified by appCMSPageUI
     * @param appCMSAndroidModules This contains the individual block module definitions used for creating each UI element
     * @param screenName           This is the screen name used to demarcate specific page landing events as well as distinguish this page from other types of screens
     * @param jsonValueKeyMap      This is a hashmap that associates UI string values with value enumerations
     * @param appCMSPresenter      This is a reference to the presenter class which handles all UI events including click events
     * @param modulesToIgnore      This is a list of block modules that ViewCreator should ignore
     * @return Returns a reference to a PageView View that may be rendered on the screen
     */
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

        PageView pageView = new PageView(context, appCMSPageUI, appCMSPresenter);
        pageView.setUserLoggedIn(appCMSPresenter.isUserLoggedIn());
        if (appCMSPresenter.isPageAVideoPage(screenName)) {
            appCMSPresenter.getPageViewLruCache().put(screenName + BaseView.isLandscape(context), pageView);
        } else {
            appCMSPresenter.getPageViewLruCache().put(screenName
                    + BaseView.isLandscape(context), pageView);
        }
        pageView.setReparentChromecastButton(true);

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
        if (appCMSPageAPI != null) {
            try {
                CastServiceProvider.getInstance(appCMSPresenter.getCurrentActivity()).setPageName(appCMSPageAPI.getTitle());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pageView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        if (pageView.shouldReparentChromecastButton()) {
            if (appCMSPresenter.getCurrentMediaRouteButton() != null &&
                    appCMSPresenter.getCurrentMediaRouteButton().getParent() != null &&
                    appCMSPresenter.getCurrentMediaRouteButton().getParent() instanceof ViewGroup &&
                    appCMSPresenter.getCurrentMediaRouteButton().getParent() != appCMSPresenter.getCurrentMediaRouteButtonParent()) {
                if (appCMSPresenter.getCurrentMediaRouteButton().getParent() != null) {
                    ((ViewGroup) appCMSPresenter.getCurrentMediaRouteButton().getParent()).removeView(appCMSPresenter.getCurrentMediaRouteButton());
                }
                appCMSPresenter.getCurrentMediaRouteButtonParent().addView(appCMSPresenter.getCurrentMediaRouteButton());
            }
        }

        return pageView;
    }

    ComponentViewResult getComponentViewResult() {
        return componentViewResult;
    }

    private void createCompoundTopModule(Context context,
                                         List<ModuleList> modulesList,
                                         AppCMSPageAPI appCMSPageAPI,
                                         AppCMSAndroidModules appCMSAndroidModules,
                                         PageView pageView,
                                         Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                         AppCMSPresenter appCMSPresenter,
                                         List<String> modulesToIgnore) {
        List<ModuleView> topViewList = new ArrayList<>();
        float moduleMobileHeight = 0f;

        for (ModuleList moduleInfo : modulesList) {

            ModuleList module = null;
            try {
                if (moduleInfo.getSettings() != null &&
                        moduleInfo.getSettings().isHidden()) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "article_hub.json"),
                            AppCMSPageUI.class);

                    if (moduleInfo.getBlockName().contains("eventCarousel01")) {
                        module = appCMSPageUI1.getModuleList().get(7);
                    } else if (moduleInfo.getBlockName().contains("list01")) {
                        module = appCMSPageUI1.getModuleList().get(8);
                    } else if (moduleInfo.getBlockName().contains("mediumRectangleAd01")) {
                        module = appCMSPageUI1.getModuleList().get(10);
                    }
                }
                //module = appCMSAndroidModules.getModuleListMap().get(moduleInfo.getBlockName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (module != null && moduleInfo != null) {
                module.setId(moduleInfo.getId());
                module.setSettings(moduleInfo.getSettings());
                module.setSvod(moduleInfo.isSvod());
                module.setType(moduleInfo.getType());
                module.setView(moduleInfo.getView());
                module.setBlockName(moduleInfo.getBlockName());


                Module moduleAPI = matchModuleAPIToModuleUI(module, appCMSPageAPI, jsonValueKeyMap);

                if (moduleAPI != null) {
                    View childView = createModuleView(context, module, moduleAPI,
                            appCMSAndroidModules,
                            pageView,
                            jsonValueKeyMap,
                            appCMSPresenter);
                    if (childView != null && childView instanceof ModuleView) {
                        moduleMobileHeight = module.getLayout().getMobile().getHeight() + moduleMobileHeight;
                        topViewList.add((ModuleView) childView);
                    }
                }
            }
        } //End of for (ModuleList moduleInfo : modulesList)

        if (topViewList.size() > 0) {
            AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                    loadJsonFromAssets(context, "article_hub.json"),
                    AppCMSPageUI.class);
            ModuleList moduleTop = appCMSPageUI1.getModuleList().get(9);
            moduleTop.getLayout().getMobile().setHeight(moduleMobileHeight);
            ModuleView moduleView = new CompoundTopModule(context,
                    moduleTop,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    topViewList);
            pageView.addModuleViewWithModuleId("CompoundTopModule", moduleView, false);
        }


    }

    /**
     * This creates the individual UI modules that make a PageView
     *
     * @param context              This is the context value that created UI components should use
     * @param appCMSPageUI         This is the Page UI response used for the creation of the UI
     * @param appCMSPageAPI        This is the Page API data used to populate the UI fields specified by appCMSPageUI
     * @param appCMSAndroidModules This contains the individual block module definitions used for creating each UI element
     * @param pageView             The PageView to use as the parent View Group for the created block modules
     * @param jsonValueKeyMap      This is a hashmap that associates UI string values with value enumerations
     * @param appCMSPresenter      This is a reference to the presenter class which handles all UI events including click events
     * @param modulesToIgnore      This is a list of block modules that ViewCreator should ignore
     */
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
        boolean isTopModuleCreated = false;
        for (ModuleList moduleInfo : modulesList) {
            ModuleList module = null;
            try {
               /* if (moduleInfo.getBlockName().contains("articleTray01")) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "article_hub.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(5);
                }  else if (moduleInfo.getBlockName().contains("photoGalleryDetail01")) {

                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "photo_galery_grid.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(1);
                } else if (moduleInfo.getBlockName().contains("carousel01")) {

                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "video_hub.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(1);
                }else if (moduleInfo.getBlockName().equalsIgnoreCase("articleFeed01")) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "article_hub.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(6);
                }else */
                /*if (moduleInfo.getBlockName().contains("videoPlayerInfo02")) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "video_detail_new.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(1);
                } else*/
                if (moduleInfo.getBlockName().contains("gameDetail01")) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "game_detail.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(1);
                } else if (moduleInfo.getBlockName().contains("playerState01")) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "game_detail.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(2);
                } else if (moduleInfo.getBlockName().contains("fighterRoster01")) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "roster.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(1);
                } else if (moduleInfo.getBlockName().contains("articleTray01")) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "article_hub.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(5);
                } else if (moduleInfo.getBlockName().contains("tray02")) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "photo_galery.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(1);
                } else if (moduleInfo.getBlockName().contains("authentication01_activate_device")) {

                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "home.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(12);
                } else if (moduleInfo.getBlockName().contains("webFrame03")) {

                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "live_fb.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(1);
                } else if ((moduleInfo.getBlockName().contains("videoPlayerInfo01__TEMP__NO_DOWNLOADS") ||
                        moduleInfo.getBlockName().contains("videoPlayerInfo01")) &&
                        (appCMSPresenter.getAppCMSMain().getInternalName().equalsIgnoreCase("failarmy") ||
                                appCMSPresenter.getAppCMSMain().getInternalName().equalsIgnoreCase("pet-collective") ||
                                appCMSPresenter.getAppCMSMain().getInternalName().equalsIgnoreCase("people-awesome") ||
                                appCMSPresenter.getAppCMSMain().getInternalName().equalsIgnoreCase("jukin-video")
                        )) {

                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "home.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(13);
                } else if (moduleInfo.getBlockName().contains("bannerAd01")) {

                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "home.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(1);
                } else if (moduleInfo.getBlockName().contains("downloads01")) {

                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "my_watchlist.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(3);
                } else if (moduleInfo.getBlockName().contains("history01") ||
                        moduleInfo.getBlockName().contains("history02")) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "my_watchlist.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(2);
                } else if (moduleInfo.getBlockName().contains("watchlist01") ||
                        moduleInfo.getBlockName().contains("watchlist02")) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "my_watchlist.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(1);
                } else if (moduleInfo.getBlockName().contains("eventCalendar01")) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "schedule_page_module.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(1);
                } else if (moduleInfo.getBlockName().contains("personDetail02")) {
                    AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                            loadJsonFromAssets(context, "person_detail.json"),
                            AppCMSPageUI.class);
                    module = appCMSPageUI1.getModuleList().get(1);
                } else if (moduleInfo.getSettings() != null &&
                        moduleInfo.getSettings().isHidden()) { // Done for Tampabay Top Module
                    if (isTopModuleCreated) {
                        continue;
                    } else {
                        createCompoundTopModule(context,
                                modulesList,
                                appCMSPageAPI,
                                appCMSAndroidModules,
                                pageView,
                                jsonValueKeyMap,
                                appCMSPresenter,
                                modulesToIgnore);
                        isTopModuleCreated = true;
                    }
                } else {
                    module = appCMSAndroidModules.getModuleListMap().get(moduleInfo.getBlockName());
                }

            } catch (Exception e) {
                e.printStackTrace();
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
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_EVENT_CAROUSEL_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_TRAY_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_AUDIO_TRAY_MODULE_KEY ||
                            jsonValueKeyMap.get(module.getType()) == AppCMSUIKeyType.PAGE_VIDEO_PLAYER_MODULE_KEY)) {
                createModule = false;
            }

            if (createModule) {
                if (appCMSPageAPI != null && appCMSPresenter.isViewPlanPage(appCMSPageAPI.getId()) &&
                        jsonValueKeyMap.get(module.getType()) != AppCMSUIKeyType.PAGE_CAROUSEL_MODULE_KEY &&
                        jsonValueKeyMap.get(module.getType()) != AppCMSUIKeyType.PAGE_AUDIO_TRAY_MODULE_KEY &&
                        jsonValueKeyMap.get(module.getType()) != AppCMSUIKeyType.PAGE_EVENT_CAROUSEL_MODULE_KEY &&
                        jsonValueKeyMap.get(module.getType()) != AppCMSUIKeyType.PAGE_TRAY_MODULE_KEY) {
                }

                Module moduleAPI = matchModuleAPIToModuleUI(module, appCMSPageAPI, jsonValueKeyMap);

                if (moduleAPI != null) {
                    AppCMSUIKeyType viewType = jsonValueKeyMap.get(module.getView());
                    if (viewType == null) {
                        viewType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                    }
                    ArrayList<ContentDatum> allUserHistoryforContinueWatching = appCMSPresenter.getAllUserHistory();
                    if (viewType == AppCMSUIKeyType.PAGE_CONTINUE_WATCHING_MODULE_KEY &&
                            (allUserHistoryforContinueWatching != null &&
                                    !allUserHistoryforContinueWatching.isEmpty())) {
                        moduleAPI.setContentData(allUserHistoryforContinueWatching);
                    }
                }

                View childView = createModuleView(context, module, moduleAPI,
                        appCMSAndroidModules,
                        pageView,
                        jsonValueKeyMap,
                        appCMSPresenter);

                if (moduleAPI == null && childView != null) {
                    childView.setVisibility(View.GONE);
                }
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

    /**
     * This creates an individual Module View that is
     *
     * @param context              This is the context value that created UI components should use
     * @param module               This is UI definition for the module that should be created
     * @param moduleAPI            This is the API data that should be used to populate the UI fields
     * @param appCMSAndroidModules This contains the block modules definition
     * @param pageView             This is the PageView that should be used as the ViewGroup container for the created module
     * @param jsonValueKeyMap      This is a hashmap that associates UI string values with value enumerations
     * @param appCMSPresenter      This is a reference to the presenter class which handles all UI events including click events
     * @return Returns a ModuleView that will be added as a child view within the PageView ViewGroup
     */
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
            pageView.addModuleViewWithModuleId(module.getId(), moduleView, false);
            RecyclerView view = pageView.findViewById(R.id.home_nested_scroll_view);
            if (view != null) {
                view.setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
            }
        } else if (jsonValueKeyMap.get(module.getView()) == AppCMSUIKeyType.PAGE_EVENT_DETAIL_MODULE_KEY) {
            moduleView = new EventModule(context,
                    module,
                    moduleAPI,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    this,
                    appCMSAndroidModules, pageView);
            pageView.addModuleViewWithModuleId(module.getId(), moduleView, false);
            RecyclerView view = pageView.findViewById(R.id.home_nested_scroll_view);
            if (view != null) {
                view.setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
            }

        } else if (jsonValueKeyMap.get(module.getView()) == AppCMSUIKeyType.PAGE_AC_ROSTER_MODULE_KEY) {

            moduleView = new MultiTableWithSameItemsModule(context,
                    module,
                    moduleAPI,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    this,
                    appCMSAndroidModules, pageView);
            pageView.addModuleViewWithModuleId(module.getId(), moduleView, false);
            RecyclerView view = pageView.findViewById(R.id.home_nested_scroll_view);
            if (view != null) {
                view.setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
            }
        } else {
            if (module.getComponents() != null) {
                moduleView = new ModuleView<>(context, module, true);
                ViewGroup childrenContainer = moduleView.getChildrenContainer();
                boolean hideModule = false;
                boolean modulesHasHiddenComponent = false;

                AdjustOtherState adjustOthers = AdjustOtherState.IGNORE;
                if (module.getSettings() != null && !module.getSettings().isHidden()) {
                    pageView.addModuleViewWithModuleId(module.getId(), moduleView, false);
                }
                if (module.getComponents() != null) {
                    if (moduleAPI != null) {
                        updateUserHistory(appCMSPresenter,
                                moduleAPI.getContentData());

                    }

                    int size = module.getComponents().size();
                    for (int i = 0; i < size; i++) {
                        Component component = module.getComponents().get(i);

                        try {
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
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        if (adjustOthers == AdjustOtherState.INITIATED) {
                            adjustOthers = AdjustOtherState.ADJUST_OTHERS;
                        }

                        if (!appCMSPresenter.isAppSVOD() && component.isSvod() && componentViewResult.componentView != null) {
                            componentViewResult.shouldHideComponent = true;
                            if (componentViewResult.componentView != null) {
                                componentViewResult.componentView.setVisibility(View.GONE);
                            }
                            adjustOthers = AdjustOtherState.INITIATED;
                        } else if (/*!appCMSPresenter.isAppSVOD() &&*/ jsonValueKeyMap.get(component.getKey()) != null &&
                                jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_USER_MANAGEMENT_DOWNLOADS_MODULE_KEY
                                && appCMSPresenter.getAppCMSMain().getFeatures() != null &&
                                !appCMSPresenter.getAppCMSMain().getFeatures().isMobileAppDownloads() && componentViewResult.componentView != null) {
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
                            ModuleView.HeightLayoutAdjuster heightLayoutAdjuster =
                                    new ModuleView.HeightLayoutAdjuster();
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
                                if (componentView.getParent() != null)
                                    ((ViewGroup) componentView.getParent()).removeView(componentView);

                                if (component.isHeaderView()) {
                                    pageView.addToHeaderView(componentView);
                                } else {
                                    childrenContainer.addView(componentView);
                                }
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

    /**
     * This method is used by Adapters for creating a component view.
     *
     * @param context                             This is the context value that created UI components should use
     * @param parentLayout                        This is the UI JSON layout object used by the parent ViewGroup
     * @param useParentLayout                     This flag should be set to true if this component should use the parent layout
     * @param component                           This is UI JSON component object that contains the information from AppCMS needed to create the component
     * @param appCMSPresenter                     This is the Presenter class used for handling all UI events including click events
     * @param moduleAPI                           This is the API data for the module that contains the data used for this component
     * @param appCMSAndroidModules                This is the list of the Android block modules definitions used for creating each UI block type
     * @param settings                            This is the settings object from the module object that contains this object
     * @param jsonValueKeyMap                     This is a hashmap that associates UI string values with value enumerations
     * @param defaultWidth                        This is a default width for the component if the view width is not specified in the UI JSON response
     * @param defaultHeight                       This is a default height for the component if the view height is not specified in the UI JSON response
     * @param useMarginsAsPercentages             This flag should be set to true if this component should interpret the margin as percentages instead of pixel values
     * @param gridElement                         This flag should be set to true if this component is a grid component and its values from come an array of values instead of a single element
     * @param viewType                            This is the component view type of the parent view and is used to infer additional view properties
     * @param createMultipleContainersForChildren This flag should be set to true if the parent view has multiple viewgroups as child layouts that a component may be placed in
     * @param createRoundedCorners                This flag should be set to true if this component should have rounded corners (e.g. round corners in a CardView)
     * @return Returns a CollectionGridItemView that may be bound to a view in a view adapter
     */
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
                                                               boolean createRoundedCorners,
                                                               AppCMSUIKeyType viewTypeKey) {
        CollectionGridItemView collectionGridItemView = new CollectionGridItemView(context,
                parentLayout,
                useParentLayout,
                component,
                moduleAPI.getId(),
                defaultWidth,
                defaultHeight,
                createMultipleContainersForChildren,
                createRoundedCorners,
                viewTypeKey);

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
            if (viewType != null &&
                    viewType.equalsIgnoreCase(AppCMSUIKeyType.PAGE_EVENT_CAROUSEL_MODULE_KEY.toString())) {
                childComponent.setView(viewType);
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

    AppCMSUserWatHisDowAdapter appCMSUserWatHisDowAdapter = null;
    AppCMSFightSelectionAdapter appcmsFightSelectionAdapter = null;
    AppCMSRosterAdapter appcmsRosterAdapter = null;

    /**
     * This method is used to create an individual component view, which may by a recycler view,
     * text view, button, image view, etc.  The result is stored in the componentViewResult member object.
     *
     * @param context              This is the context value that created UI components should use
     * @param component            This is UI JSON component object that contains the information from AppCMS needed to create the component
     * @param parentLayout         This is the UI JSON layout object used by the parent ViewGroup
     * @param moduleAPI            This is the API data for the module that contains the data used for this component
     * @param appCMSAndroidModules This is the list of the Android block modules definitions used for creating each UI block type
     * @param pageView             This is view used by the entire page and is used as a parent viewgroup for specific components that may be positioned anywhere on the page
     * @param settings             This is the settings object from the module object that contains this object
     * @param jsonValueKeyMap      This is a hashmap that associates UI string values with value enumerations
     * @param appCMSPresenter      This is the Presenter class used for handling all UI events including click events
     * @param gridElement          This flag should be set to true if this component is a grid component and its values from come an array of values instead of a single element
     * @param viewType             This is the component view type of the parent view and is used to infer additional view properties
     * @param moduleId             This is the module ID that associates
     */
    @SuppressLint("RestrictedApi")
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

        AppCMSUIKeyType parentViewType = jsonValueKeyMap.get(viewType);

        if (parentViewType == null) {
            moduleType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        int tintColor = appCMSPresenter.getBrandPrimaryCtaColor();

        switch (componentType) {
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
                } else if (moduleType == AppCMSUIKeyType.PAGE_EVENT_DETAIL_MODULE_KEY) {
                    componentViewResult.componentView = new RecyclerView(context);

                    /*
                     * get cache of recycler view if already created . need to clear cached  when opening first time this screen using same adapter and recycler view
                     * Currenlty cleared cache on following method navigateTODownloadPAge() , navigateToWatchlistPage() , navigateToHistoryPage()
                     */
                    //if (appCMSPresenter.getDownlistScreenCache() == null) {
                    ((RecyclerView) componentViewResult.componentView)
                            .setLayoutManager(new LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL,
                                    false));

                    RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 0);
                    ((RecyclerView) componentViewResult.componentView).setLayoutParams(params);
                    ((RecyclerView) componentViewResult.componentView).setPadding(0, 0, 0, 0);
                    appcmsFightSelectionAdapter = new AppCMSFightSelectionAdapter(context,
                            this,
                            appCMSPresenter,
                            component.getLayout(),
                            false,
                            component,
                            jsonValueKeyMap,
                            moduleAPI,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            viewType,
                            appCMSAndroidModules);

                    ((RecyclerView) componentViewResult.componentView).setAdapter(appcmsFightSelectionAdapter);
                    ((RecyclerView) componentViewResult.componentView).setDescendantFocusability(RecyclerView.FOCUS_BLOCK_DESCENDANTS);

                  /*      appCMSPresenter.setDownlistScreenCache(((RecyclerView) componentViewResult.componentView));
                    } else {
                        (componentViewResult.componentView) = appCMSPresenter.getDownlistScreenCache();
                    }*/
                    componentViewResult.onInternalEvent = appcmsFightSelectionAdapter;
                    componentViewResult.onInternalEvent.setModuleId(moduleId);
                    if (pageView != null) {
                        pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                .adapter(appcmsFightSelectionAdapter)
                                .listview((RecyclerView) componentViewResult.componentView)
                                .id(moduleId + component.getKey())
                                .build());
                    }
                } else if (moduleType == AppCMSUIKeyType.PAGE_AC_ROSTER_MODULE_KEY) {
                    componentViewResult.componentView = new RecyclerView(context);

                    /*
                     * get cache of recycler view if already created . need to clear cached  when opening first time this screen using same adapter and recycler view
                     * Currenlty cleared cache on following method navigateTODownloadPAge() , navigateToWatchlistPage() , navigateToHistoryPage()
                     */
                    //if (appCMSPresenter.getDownlistScreenCache() == null) {
                    ((RecyclerView) componentViewResult.componentView)
                            .setLayoutManager(new LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL,
                                    false));

                    appcmsRosterAdapter = new AppCMSRosterAdapter(context,
                            this,
                            appCMSPresenter,
                            component.getLayout(),
                            false,
                            component,
                            jsonValueKeyMap,
                            moduleAPI,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            viewType,
                            appCMSAndroidModules);

                    ((RecyclerView) componentViewResult.componentView).setAdapter(appcmsRosterAdapter);

                  /*      appCMSPresenter.setDownlistScreenCache(((RecyclerView) componentViewResult.componentView));
                    } else {
                        (componentViewResult.componentView) = appCMSPresenter.getDownlistScreenCache();
                    }*/
                    componentViewResult.onInternalEvent = appcmsRosterAdapter;
                    componentViewResult.onInternalEvent.setModuleId(moduleId);
                    if (pageView != null) {
                        pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                .adapter(appcmsRosterAdapter)
                                .listview((RecyclerView) componentViewResult.componentView)
                                .id(moduleId + component.getKey())
                                .build());
                    }
                } else if (moduleType == AppCMSUIKeyType.PAGE_PLAYLIST_MODULE_KEY) {
                    componentViewResult.componentView = new RecyclerView(context);

                    ((RecyclerView) componentViewResult.componentView)
                            .setLayoutManager(new LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL,
                                    false));

                    appCMSPlaylistAdapter = new AppCMSPlaylistAdapter(context,
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
                            appCMSAndroidModules, ((RecyclerView) componentViewResult.componentView));


                    ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSPlaylistAdapter);
                    componentViewResult.onInternalEvent = appCMSPlaylistAdapter;
                    componentViewResult.onInternalEvent.setModuleId(moduleId);
                    if (pageView != null) {
                        pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                .adapter(appCMSPlaylistAdapter)
                                .listview((RecyclerView) componentViewResult.componentView)
                                .id(moduleId + component.getKey())
                                .build());
                    }
                } else {
                    componentViewResult.componentView = new RecyclerView(context);
                    /*
                     * get cache of recycler view if already created . need to clear cached  when opening first time this screen using same adapter and recycler view
                     * Currenlty cleared cache on following method navigateTODownloadPAge() , navigateToWatchlistPage() , navigateToHistoryPage()
                     */
                    //if (appCMSPresenter.getDownlistScreenCache() == null) {
                    ((RecyclerView) componentViewResult.componentView)
                            .setLayoutManager(new LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL,
                                    false));


                    appCMSUserWatHisDowAdapter = new AppCMSUserWatHisDowAdapter(context,
                            this,
                            appCMSPresenter,
                            component.getLayout(),
                            false,
                            component,
                            jsonValueKeyMap,
                            moduleAPI,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            viewType,
                            appCMSAndroidModules);

                    ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSUserWatHisDowAdapter);

                    //for team schedule module .scroll list to first position item in which event time is greater than currrent time
                    if (moduleType == AppCMSUIKeyType.PAGE_AC_TEAM_SCHEDULE_MODULE_KEY) {
                        for (int i = 0; i < moduleAPI.getContentData().size(); i++) {
                            long eventDate = moduleAPI.getContentData().get(i).getGist().getEventSchedule().get(0).getEventTime();

                            long remainingTime = appCMSPresenter.getTimeIntervalForEvent(eventDate * 1000L, "EEE MMM dd HH:mm:ss");
                            if (remainingTime > 0) {
                                int selectedPostion = i;
                                appCMSPresenter.setSelectedSchedulePosition(i);
                                break;
                            }
                        }
                        ((RecyclerView) componentViewResult.componentView).scrollToPosition(appCMSPresenter.getSelectedSchedulePosition());

                    }
                  /*      appCMSPresenter.setDownlistScreenCache(((RecyclerView) componentViewResult.componentView));
                    } else {
                        (componentViewResult.componentView) = appCMSPresenter.getDownlistScreenCache();
                    }*/
                    componentViewResult.onInternalEvent = appCMSUserWatHisDowAdapter;
                    componentViewResult.onInternalEvent.setModuleId(moduleId);
                    if (pageView != null) {
                        pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                .adapter(appCMSUserWatHisDowAdapter)
                                .listview((RecyclerView) componentViewResult.componentView)
                                .id(moduleId + component.getKey())
                                .build());
                    }
                }
                break;
            case PAGE_LIST_VIEW_KEY:
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

                    if (componentKey == AppCMSUIKeyType.PAGE_LIST_VIEW_KEY) {

                        ((RecyclerView) componentViewResult.componentView)
                                .setLayoutManager(new LinearLayoutManager(context,
                                        LinearLayoutManager.VERTICAL,
                                        false));


                        appCMSUserWatHisDowAdapter = new AppCMSUserWatHisDowAdapter(context,
                                this,
                                appCMSPresenter,
                                component.getLayout(),
                                false,
                                component,
                                jsonValueKeyMap,
                                moduleAPI,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                viewType,
                                appCMSAndroidModules);


                        ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSUserWatHisDowAdapter);
                        RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
                            @Override
                            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                                int action = e.getAction();
                                switch (action) {
                                    case MotionEvent.ACTION_MOVE:
                                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                                        break;
                                }
                                return false;
                            }

                            @Override
                            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                            }

                            @Override
                            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                            }
                        };

                        ((RecyclerView) componentViewResult.componentView).addOnItemTouchListener(mScrollTouchListener);

                        if (pageView != null) {
                            pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                    .adapter(appCMSUserWatHisDowAdapter)
                                    .listview((RecyclerView) componentViewResult.componentView)
                                    .id(moduleId + component.getKey())
                                    .build());
                        }
                    } else if (moduleType == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY) {

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

                        if (BaseView.isTablet(context)) {
                            ((RecyclerView) componentViewResult.componentView)
                                    .setLayoutManager(new LinearLayoutManager(context,
                                            LinearLayoutManager.HORIZONTAL,
                                            false));
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
                    } else if (componentKey == AppCMSUIKeyType.PAGE_PHOTOGALLERY_GRID_KEY) {

                        LinearLayoutManager layoutManager = null;
                        if (BaseView.isTablet(context)) {
                            layoutManager = BaseView.isLandscape(context) ?
                                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) :
                                    new GridLayoutManager(context, 6, GridLayoutManager.VERTICAL, false);
                        } else {
                            layoutManager = new GridLayoutManager(context, 3,
                                    GridLayoutManager.VERTICAL, false);
                        }

                        ((RecyclerView) componentViewResult.componentView).setLayoutManager(layoutManager);
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
                                context.getResources().getString(R.string.app_cms_photo_tray_module_key),
                                appCMSAndroidModules);

                        ((RecyclerView) componentViewResult.componentView).setId(R.id.photo_gallery_grid_recyclerview);
                        ((RecyclerView) componentViewResult.componentView).addItemDecoration(new PhotoGalleryGridInsetDecoration(5, 15));
                        photoGalleryNextPreviousListener = appCMSViewAdapter.setPhotoGalleryImageSelectionListener(photoGalleryNextPreviousListener);

                        appCMSViewAdapter.setPhotoGalleryImageSelectionListener((url, selectedPosition) -> {
                            ImageView imageView = pageView.findViewById(R.id.photo_gallery_selectedImage);
                            Glide.with(imageView.getContext()).load(url).into(imageView);
                            int photoGallerySize = moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets().size();
                            String position = photoGallerySize == 0 ? "0/0" : (selectedPosition + 1) + "/" + photoGallerySize;
                            if ((RecyclerView) pageView.findChildViewById(R.id.photo_gallery_grid_recyclerview) != null) {
                                ((RecyclerView) pageView.findChildViewById(R.id.photo_gallery_grid_recyclerview)).scrollToPosition(selectedPosition);
                            }

                            if (selectedPosition == 0) {
                                enablePhotoGalleryButtons(false, true, pageView, appCMSPresenter, position);
                            } else if (selectedPosition > 0 && selectedPosition < photoGallerySize - 1) {
                                enablePhotoGalleryButtons(true, true, pageView, appCMSPresenter, position);
                            } else {
                                enablePhotoGalleryButtons(true, false, pageView, appCMSPresenter, position);
                            }

                        });

                        if (!BaseView.isTablet(context)) {
                            componentViewResult.useWidthOfScreen = true;
                        }

                        ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSViewAdapter);
                        RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
                            @Override
                            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                                int action = e.getAction();
                                switch (action) {
                                    case MotionEvent.ACTION_MOVE:
                                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                                        break;
                                }
                                return false;
                            }

                            @Override
                            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                            }

                            @Override
                            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                            }
                        };

                        ((RecyclerView) componentViewResult.componentView).addOnItemTouchListener(mScrollTouchListener);
                        if (pageView != null) {
                            pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                    .adapter(appCMSViewAdapter)
                                    .listview((RecyclerView) componentViewResult.componentView)
                                    .id(moduleId + component.getKey())
                                    .build());
                        }
                    } else if (componentKey == AppCMSUIKeyType.PAGE_TRAY_05_MODULE_KEY) {

                        LinearLayoutManager layoutManager = null;
                        if (BaseView.isTablet(context)) {
                            layoutManager = BaseView.isLandscape(context) ?
                                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) :
                                    new GridLayoutManager(context, 6, GridLayoutManager.VERTICAL, false);
                        } else {
                            layoutManager = new GridLayoutManager(context, 3,
                                    GridLayoutManager.VERTICAL, false);
                        }

                        ((RecyclerView) componentViewResult.componentView).setLayoutManager(layoutManager);
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
                                context.getResources().getString(R.string.app_cms_photo_tray_module_key),
                                appCMSAndroidModules);

                        ((RecyclerView) componentViewResult.componentView).setId(R.id.photo_gallery_grid_recyclerview);
                        ((RecyclerView) componentViewResult.componentView).addItemDecoration(new PhotoGalleryGridInsetDecoration(5, 15));
                        photoGalleryNextPreviousListener = appCMSViewAdapter.setPhotoGalleryImageSelectionListener(photoGalleryNextPreviousListener);

                        appCMSViewAdapter.setPhotoGalleryImageSelectionListener((url, selectedPosition) -> {
                            ImageView imageView = pageView.findViewById(R.id.photo_gallery_selectedImage);
                            Glide.with(imageView.getContext()).load(url).into(imageView);
                            int photoGallerySize = moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets().size();
                            String position = photoGallerySize == 0 ? "0/0" : (selectedPosition + 1) + "/" + photoGallerySize;
                            if ((RecyclerView) pageView.findChildViewById(R.id.photo_gallery_grid_recyclerview) != null) {
                                ((RecyclerView) pageView.findChildViewById(R.id.photo_gallery_grid_recyclerview)).scrollToPosition(selectedPosition);
                            }

                            if (selectedPosition == 0) {
                                enablePhotoGalleryButtons(false, true, pageView, appCMSPresenter, position);
                            } else if (selectedPosition > 0 && selectedPosition < photoGallerySize - 1) {
                                enablePhotoGalleryButtons(true, true, pageView, appCMSPresenter, position);
                            } else {
                                enablePhotoGalleryButtons(true, false, pageView, appCMSPresenter, position);
                            }

                        });

                        if (!BaseView.isTablet(context)) {
                            componentViewResult.useWidthOfScreen = true;
                        }

                        ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSViewAdapter);
                        RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
                            @Override
                            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                                int action = e.getAction();
                                switch (action) {
                                    case MotionEvent.ACTION_MOVE:
                                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                                        break;
                                }
                                return false;
                            }

                            @Override
                            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                            }

                            @Override
                            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                            }
                        };

                        ((RecyclerView) componentViewResult.componentView).addOnItemTouchListener(mScrollTouchListener);
                        if (pageView != null) {
                            pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                    .adapter(appCMSViewAdapter)
                                    .listview((RecyclerView) componentViewResult.componentView)
                                    .id(moduleId + component.getKey())
                                    .build());
                        }
                    } else {
                        parentViewType = jsonValueKeyMap.get(viewType);

                        if (parentViewType == AppCMSUIKeyType.PAGE_GRID_MODULE_KEY ||
                                parentViewType == AppCMSUIKeyType.PAGE_LIST_MODULE_KEY ||
                                parentViewType == AppCMSUIKeyType.PAGE_ARTICLE_FEED_MODULE_KEY) {
                            appCMSPresenter.setMoreIconAvailable();
                            int numCols = 1;
                            if (settings != null && settings.getColumns() != null) {
                                if (BaseView.isTablet(context)) {
                                    numCols = settings.getColumns().getTablet();
                                } else {
                                    numCols = settings.getColumns().getMobile();
                                }
                            }
                            numCols = 1;
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
                            ((RecyclerView) componentViewResult.componentView).setDescendantFocusability(RecyclerView.FOCUS_BLOCK_DESCENDANTS);
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
                                                moduleAPI,
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
                        } else if (parentViewType == AppCMSUIKeyType.PAGE_ARTICLE_FEED_MODULE_KEY) {
                            AppCMSArticleFeedViewAdapter appCMSArticleFeedViewAdapter = new AppCMSArticleFeedViewAdapter(context,
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
                            ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSArticleFeedViewAdapter);
                            //((RecyclerView) componentViewResult.componentView).setBackgroundColor(Color.GREEN);
                            if (pageView != null) {
                                pageView.addListWithAdapter(new ListWithAdapter.Builder()
                                        .adapter(appCMSArticleFeedViewAdapter)
                                        .listview((RecyclerView) componentViewResult.componentView)
                                        .id(moduleId + component.getKey())
                                        .build());
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

            case PAGE_FIGHT_LIST_PARENT_MODULE_KEY:

                componentViewResult.componentView = new LinearLayout(context);

                LinearLayout.LayoutParams layoutParamsFightView =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                ((LinearLayout) componentViewResult.componentView).setLayoutParams(layoutParamsFightView);
                ((LinearLayout) componentViewResult.componentView).setOrientation(LinearLayout.VERTICAL);
                if (component.getBackgroundColor() != null && !TextUtils.isEmpty(component.getBackgroundColor())) {
                    ((LinearLayout) componentViewResult.componentView).setBackgroundColor(Color.parseColor(getColor(context, component.getBackgroundColor())));
                }

                break;


            case PAGE_VIDEO_PLAYER_VIEW_KEY:
                String videoId = null;
                if (moduleAPI != null &&
                        moduleAPI.getContentData() != null &&
                        !moduleAPI.getContentData().isEmpty() &&
                        moduleAPI.getContentData().get(0) != null &&
                        moduleAPI.getContentData().get(0).getGist() != null &&
                        moduleAPI.getContentData().get(0).getGist().getId() != null) {
                    videoId = moduleAPI.getContentData().get(0).getGist().getId();
                }
                CustomVideoPlayerView videoPlayerViewSingle = null;
                componentViewResult.componentView = new FrameLayout(context);
                if (appCMSPresenter.getVideoPlayerViewCache(moduleId + component.getKey()) != null) {
                    videoPlayerViewSingle = appCMSPresenter.getVideoPlayerViewCache(moduleId + component.getKey());
                    videoPlayerViewSingle.pausePlayer();
                }
                if (videoPlayerViewSingle != null) {

                    if (videoPlayerViewSingle.getParent() != null)
                        ((ViewGroup) videoPlayerViewSingle.getParent()).removeView(videoPlayerViewSingle);


                    ((FrameLayout) componentViewResult.componentView).addView(videoPlayerViewSingle);

                    if (videoId.equalsIgnoreCase(videoPlayerViewSingle.getVideoId())) {
                        videoPlayerViewSingle.resumePlayerLastState();
                    } else {
                        videoPlayerViewSingle.setVideoUri(videoId, R.string.loading_video_text);
                    }
                } else {
                    videoPlayerViewSingle = playerView(context, videoId, moduleId + component.getKey(), appCMSPresenter);
                    if (pageView != null) {
                        videoPlayerViewSingle.setPageView(pageView);
                    }
                    ((FrameLayout) componentViewResult.componentView).addView(videoPlayerViewSingle);
                }

                /*videoPlayerViewSingle.setLayoutParams(new FrameLayout.LayoutParams((int)BaseView.getViewWidth(context,component.getLayout(), ViewGroup.LayoutParams.MATCH_PARENT),
                        (int)BaseView.getViewHeight(context,component.getLayout(), ViewGroup.LayoutParams.WRAP_CONTENT)));
*/
                FrameLayout.LayoutParams videoPlayerParentLP = new FrameLayout.LayoutParams((int) BaseView.getViewWidth(context, component.getLayout(), ViewGroup.LayoutParams.MATCH_PARENT),
                        (int) BaseView.getViewHeight(context, component.getLayout(), ViewGroup.LayoutParams.WRAP_CONTENT));
                videoPlayerViewSingle.setLayoutParams(videoPlayerParentLP);
                appCMSPresenter.videoPlayerViewParent = (ViewGroup) componentViewResult.componentView;
                appCMSPresenter.videoPlayerView = videoPlayerViewSingle;
                videoPlayerViewSingle.checkVideoStatus();
                componentViewResult.componentView.setId(R.id.video_player_id);
                break;

            case PAGE_UPCOMING_TIMER_KEY:
                if (moduleAPI != null && moduleAPI.getContentData() != null &&
                        moduleAPI.getContentData().get(0) != null &&
                        moduleAPI.getContentData().get(0).getGist() != null &&
                        moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0) != null &&
                        moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0).getIsLiveEvent() != null
                        ) {

                    componentViewResult.componentView = new LinearLayout(context);
                    ((LinearLayout) componentViewResult.componentView).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    ((LinearLayout) componentViewResult.componentView).setOrientation(LinearLayout.HORIZONTAL);
                    ((LinearLayout) componentViewResult.componentView).setGravity(Gravity.CENTER);
                    ((LinearLayout) componentViewResult.componentView).setId(R.id.timer_id);

                    for (int count = 0; count < 4; count++) {
                        LinearLayout linearLayout = new LinearLayout(context);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        params.setMargins(3, 0, 3, 0);
                        linearLayout.setLayoutParams(params);

                        linearLayout.setGravity(Gravity.CENTER);
                        linearLayout.setBackgroundColor(Color.parseColor("#000000"));
                        linearLayout.setAlpha(0.9f);
                        for (int textView = 0; textView < 2; textView++) {
                            TextView text = new TextView(context);
                            text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            text.setGravity(Gravity.CENTER);
                            text.setPadding(3, 0, 3, 0);
                            text.setTextSize(26);
                            text.setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
                            linearLayout.addView(text);
                        }
                        ((LinearLayout) componentViewResult.componentView).addView(linearLayout);
                    }
                    long eventDate = moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0).getEventTime();

                    //calculate remaining time from event date and current date
                    long remainingTime = appCMSPresenter.getTimeIntervalForEvent(eventDate * 1000L, "EEE MMM dd HH:mm:ss");

                    //if event date is greater than current date then start the timer
                    if (remainingTime > 0) {
                        startTimer(context, appCMSPresenter, eventDate, remainingTime);
                    } else {
                        if (appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null) {
                            if (appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_until_face_off) != null) {
                                TextView timerTile = appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_until_face_off);
                                timerTile.setVisibility(View.GONE);
                            }
                            if (appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_id) != null) {
                                LinearLayout linearLayout = appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_id);
                                linearLayout.setVisibility(View.GONE);
                            }
                            ((LinearLayout) componentViewResult.componentView).setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null) {
                        if (appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_until_face_off) != null) {
                            TextView timerTile = appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_until_face_off);
                            timerTile.setVisibility(View.GONE);
                        }
                        if (appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_id) != null) {
                            LinearLayout linearLayout = appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_id);
                            linearLayout.setVisibility(View.GONE);
                        }
                    }
                }
                break;

            case PAGE_WEB_VIEW_KEY:
                CustomWebView webView = null;
                componentViewResult.componentView = new FrameLayout(context);

                ((FrameLayout) componentViewResult.componentView).setId(R.id.web_view_id);

                String url = "";
                if (moduleAPI.getRawText() != null) {
                    url = moduleAPI.getRawText();
                } else if (moduleAPI != null && moduleAPI.getContentData() != null
                        && moduleAPI.getContentData().get(0).getGist() != null
                        && moduleAPI.getContentData().get(0).getGist().getPermalink() != null) {
                    url = moduleAPI.getContentData().get(0).getGist().getPermalink();
                }
                if (appCMSPresenter.getWebViewCache(moduleId + component.getKey() + url) != null) {
                    webView = appCMSPresenter.getWebViewCache(moduleId + component.getKey() + url);
                }
                if (mFbLiveView != null && ((FrameLayout) componentViewResult.componentView) != null && moduleType == AppCMSUIKeyType.PAGE_AC_WEB_FRAME_03_KEY) {
                    if (mFbLiveView.getParent() != null)
                        ((ViewGroup) mFbLiveView.getParent()).removeView(mFbLiveView);
                    ((FrameLayout) componentViewResult.componentView).addView(mFbLiveView);

                } else if (webView != null) {
                    if (webView.getParent() != null)
                        ((ViewGroup) webView.getParent()).removeView(webView);
                    ((FrameLayout) componentViewResult.componentView).addView(webView);
                } else {
                    webView = getWebViewComponent(context, moduleAPI, component, moduleId + component.getKey() + url, appCMSPresenter, moduleType);
                    ((FrameLayout) componentViewResult.componentView).addView(webView);
                }
                break;
            case PAGE_ARTICLE_WEB_VIEW_KEY:
                CustomWebView articleWebView = null;
                componentViewResult.componentView = new FrameLayout(context);
                /*if (appCMSPresenter.getWebViewCache(moduleId + component.getKey()) != null) {
                    articleWebView = appCMSPresenter.getWebViewCache(moduleId + component.getKey());
                }*/
                String urlWeb = "";
                if (moduleAPI.getRawText() != null) {
                    urlWeb = moduleAPI.getRawText();
                } else if (moduleAPI != null && moduleAPI.getContentData() != null
                        && moduleAPI.getContentData().get(0).getGist() != null
                        && moduleAPI.getContentData().get(0).getGist().getPermalink() != null) {
                    urlWeb = moduleAPI.getContentData().get(0).getGist().getPermalink();
                }
                if (appCMSPresenter.getWebViewCache(moduleId + component.getKey() + urlWeb) != null) {
                    webView = appCMSPresenter.getWebViewCache(moduleId + component.getKey() + urlWeb);
                }
                if (articleWebView != null) {
                    if (articleWebView.getParent() != null)
                        ((ViewGroup) articleWebView.getParent()).removeView(articleWebView);
                    ((FrameLayout) componentViewResult.componentView).addView(articleWebView);
                } else {
                    articleWebView = getWebViewComponent(context, moduleAPI, component, moduleId + component.getKey() + urlWeb, appCMSPresenter, moduleType);
                    ((FrameLayout) componentViewResult.componentView).addView(articleWebView);
                    articleWebView.setId(R.id.article_web_view);
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
                if (viewType.equalsIgnoreCase(context.getResources().getString(R.string.app_cms_page_event_carousel_module_key))) {
                    component.setView(viewType);
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
//                selectedColor = component.getSelectedColor() != null ?
//                        Long.valueOf(component.getSelectedColor(), 16) : 0L;

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

                if (numDots <= 1) {
                    componentViewResult.componentView.setVisibility(View.GONE);
                } else {
                    componentViewResult.componentView.setVisibility(View.VISIBLE);
                }

                break;

            case PAGE_BUTTON_KEY:
                if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_CLOSE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_VIDEO_SHARE_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_AUDIO_DOWNLOAD_BUTTON_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_PLAYLIST_DOWNLOAD_BUTTON_KEY ||
                        componentKey == AppCMSUIKeyType.PAGE_VIDEO_DOWNLOAD_BUTTON_KEY
                        ) {
                    componentViewResult.componentView = new ResponsiveButton(context);
                } else if (componentKey != AppCMSUIKeyType.PAGE_BUTTON_SWITCH_KEY &&
                        componentKey != AppCMSUIKeyType.PAGE_CHECKBOX_KEY &&
                        componentKey != AppCMSUIKeyType.PAGE_ADD_TO_WATCHLIST_KEY &&
                        componentKey != AppCMSUIKeyType.PAGE_WATCHLIST_DELETE_ITEM_BUTTON &&
                        componentKey != AppCMSUIKeyType.PAGE_DELETE_HISTORY_KEY &&
                        componentKey != AppCMSUIKeyType.PAGE_DELETE_WATCHLIST_KEY &&
                        componentKey != AppCMSUIKeyType.PAGE_DELETE_DOWNLOAD_KEY) {
                    componentViewResult.componentView = new Button(context);
                } else if (componentKey == AppCMSUIKeyType.PAGE_CHECKBOX_KEY) {
                    componentViewResult.componentView = new AppCompatCheckBox(context);
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
                            componentKey != AppCMSUIKeyType.PAGE_CHECKBOX_KEY &&
                            componentKey != AppCMSUIKeyType.PAGE_VIDEO_CLOSE_KEY) {
                        ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle());
                    }
                }

                if (!TextUtils.isEmpty(appCMSPresenter.getAppTextColor())) {
                    if (componentViewResult.componentView instanceof TextView) {
                        ((TextView) componentViewResult.componentView).setTextColor(
                                Color.parseColor(getColor(context, appCMSPresenter.getAppTextColor())));
                    }
                }

                if (!TextUtils.isEmpty(appCMSPresenter.getAppBackgroundColor())) {
                    if (componentViewResult.componentView instanceof TextView) {
                        componentViewResult.componentView.setBackgroundColor(
                                Color.parseColor(getColor(context, appCMSPresenter.getAppBackgroundColor())));
                    }
                }

                if (appCMSPresenter.isActionFacebook(component.getAction())) {
                    applyBorderToComponent(context, componentViewResult.componentView, component,
                            ContextCompat.getColor(context, R.color.facebookBlue));
                    ((Button) componentViewResult.componentView).setTextColor(appCMSPresenter.getGeneralTextColor());

                } else if (appCMSPresenter.isActionGoogle(component.getAction())) {
                    if (appCMSPresenter.getAppCMSMain().getSocialMedia() != null &&
                            appCMSPresenter.getAppCMSMain().getSocialMedia().getGooglePlus() != null &&
                            appCMSPresenter.getAppCMSMain().getSocialMedia().getGooglePlus().isSignin()) {
                        applyBorderToComponent(context, componentViewResult.componentView, component,
                                ContextCompat.getColor(context, R.color.googleRed));
                        ((Button) componentViewResult.componentView).setTextColor(appCMSPresenter.getGeneralTextColor());

                    } else if (appCMSPresenter.getAppCMSMain().getSocialMedia() == null ||
                            appCMSPresenter.getAppCMSMain().getSocialMedia().getGooglePlus() == null ||
                            !appCMSPresenter.getAppCMSMain().getSocialMedia().getGooglePlus().isSignin()) {
                        componentViewResult.componentView.setVisibility(View.GONE);
                    }
                } else if (moduleAPI != null && (jsonValueKeyMap.get(moduleAPI.getModuleType())
                        == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY_01 ||
                        jsonValueKeyMap.get(moduleAPI.getModuleType()) == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY_02 ||
                        jsonValueKeyMap.get(moduleAPI.getModuleType()) == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY_03||
                        jsonValueKeyMap.get(moduleAPI.getModuleType()) == AppCMSUIKeyType.PAGE_AUTOPLAY_LANDSCAPE_MODULE_KEY)
                        && componentKey == AppCMSUIKeyType.PAGE_DOWNLOAD_QUALITY_CANCEL_BUTTON_KEY
                        && component.getBorderWidth() != 0) {
                    ((Button) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandPrimaryCtaColor());
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
                    case PAGE_WATCH_LIVE_BUTTON_KEY:
                        if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                moduleAPI.getContentData().get(0).getGist() != null && moduleAPI.getContentData().get(0).getGist().getEventSchedule() != null &&
                                moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0).getIsLiveEvent() != null) {
                            ((Button) componentViewResult.componentView).setId(R.id.watch_live_button);

                            long eventDate = moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0).getEventTime();
                            long currentTimeMillis = System.currentTimeMillis();
//                            long remainingTime = (eventDate * 1000L) - currentTimeMillis;
                            long remainingTime = appCMSPresenter.getTimeIntervalForEvent(eventDate * 1000L, "EEE MMM dd HH:mm:ss");

                            if (remainingTime > 0) {
                                (componentViewResult.componentView).setVisibility(View.GONE);
                            } else if ((moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0).getIsLiveEvent().equalsIgnoreCase("1")) || (remainingTime <= 0 && moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0).getIsLiveEvent().equalsIgnoreCase("1"))) {
                                (componentViewResult.componentView).setBackgroundResource(R.drawable.watch_live_button);
                                ((Button) componentViewResult.componentView).setGravity(Gravity.CENTER);
                            } else {
                                (componentViewResult.componentView).setVisibility(View.GONE);
                            }
//
                        } else {
                            (componentViewResult.componentView).setVisibility(View.GONE);

                        }
                        (componentViewResult.componentView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String url = "";
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                        moduleAPI.getContentData().get(0).getGist() != null &&
                                        moduleAPI.getContentData().get(0).getGist().getExternalUrl() != null) {
                                    url = moduleAPI.getContentData().get(0).getGist().getExternalUrl();
                                }
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                appCMSPresenter.getCurrentActivity().startActivity(browserIntent);
                            }
                        });

                        break;
                    case PAGE_PHOTOGALLERY_PRE_BUTTON_KEY:
                        componentViewResult.componentView.setId(R.id.photo_gallery_prev_button);
                        ((Button) componentViewResult.componentView).setBackgroundColor(appCMSPresenter.getBrandPrimaryCtaColor());
                        ((Button) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
                        ((Button) componentViewResult.componentView).setGravity(Gravity.CENTER);
                        ((Button) componentViewResult.componentView).setBackgroundColor(Color.parseColor("#c8c8c8"));
                        ((Button) componentViewResult.componentView).setEnabled(false);

                        ((Button) componentViewResult.componentView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (photoGalleryNextPreviousListener != null) {
                                    ((Button) pageView.findChildViewById(R.id.photo_gallery_next_button)).setBackgroundColor(appCMSPresenter.getBrandPrimaryCtaColor());
                                    ;
                                    ((Button) pageView.findChildViewById(R.id.photo_gallery_next_button)).setEnabled(true);
                                    photoGalleryNextPreviousListener.previousPhoto(((Button) view));
                                }
                            }
                        });
                        break;
                    case PAGE_PHOTOGALLERY_NEXT_BUTTON_KEY:
                        componentViewResult.componentView.setId(R.id.photo_gallery_next_button);
                        ((Button) componentViewResult.componentView).setBackgroundColor(appCMSPresenter.getBrandPrimaryCtaColor());
                        ((Button) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
                        ((Button) componentViewResult.componentView).setGravity(Gravity.CENTER);
                        if (moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets() == null || moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets().size() == 0) {
                            ((Button) componentViewResult.componentView).setEnabled(false);
                            ((Button) componentViewResult.componentView).setBackgroundColor(Color.parseColor("#c8c8c8"));
                        }
                        ((Button) componentViewResult.componentView).setOnClickListener(v -> {
                            if (photoGalleryNextPreviousListener != null) {
                                enablePhotoGalleryButtons(true, true, pageView, appCMSPresenter, "1");
                                photoGalleryNextPreviousListener.nextPhoto(((Button) v));

                            }
                        });
                        break;

                    case PAGE_CHECKBOX_KEY:
                        AppCompatCheckBox checkBoxTCP = ((AppCompatCheckBox) componentViewResult.componentView);
                        checkBoxTCP.setChecked(false);
                        checkBoxTCP.setId(R.id.appCMS_tcp_check);
                        if (component.getText() != null) {
                            checkBoxTCP.setText(component.getText());
                        }
                        if (component.getBackgroundColor() != null) {
                            checkBoxTCP.setBackgroundColor(Color.parseColor(component.getBackgroundColor()));
                        }
                        int switchOnColor = Color.WHITE;
                        int states[][] = {{android.R.attr.state_checked}, {}};
                        int colors[] = {appCMSPresenter.getBrandPrimaryCtaColor(), appCMSPresenter.getGeneralTextColor()};
                        CompoundButtonCompat.setButtonTintList(checkBoxTCP, new ColorStateList(states, colors));
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkBoxTCP.getButtonDrawable() != null) {
                                checkBoxTCP.getButtonDrawable().setColorFilter(switchOnColor, PorterDuff.Mode.MULTIPLY);
                            }
                        } else {

                            ColorStateList colorStateList = new ColorStateList(
                                    new int[][]{
                                            new int[]{android.R.attr.state_checked},
                                            new int[]{}
                                    }, new int[]{
                                    switchOnColor,
                                    switchOnColor
                            });

                            checkBoxTCP.setCompoundDrawableTintList(colorStateList);
                            checkBoxTCP.setCompoundDrawableTintMode(PorterDuff.Mode.MULTIPLY);
                            //checkBoxTCP.setButtonTintList(colorStateList);
                        }*/
                        break;
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

                        final String changePasswordAction = component.getAction();

                        componentViewResult.componentView.setOnClickListener(v -> {
                            String[] extraData = new String[1];
                            extraData[0] = component.getKey();
                            appCMSPresenter.launchButtonSelectedAction(null,
                                    changePasswordAction,
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
                    case PAGE_WATCHLIST_DELETE_ITEM_BUTTON:
                    case PAGE_DELETE_DOWNLOAD_KEY:
                    case PAGE_DELETE_WATCHLIST_KEY:
                    case PAGE_DELETE_HISTORY_KEY:
                        componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.ic_deleteicon));
                        componentViewResult.componentView.getBackground().setTint(tintColor);
                        componentViewResult.componentView.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                        ((ImageButton) componentViewResult.componentView).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        break;

                    case PAGE_GRID_OPTION_KEY:
                        componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.dots_more));
                        componentViewResult.componentView.getBackground().setTint(appCMSPresenter.getGeneralTextColor());
                        componentViewResult.componentView.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
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
                    case PAGE_PLAYLIST_DOWNLOAD_BUTTON_KEY:
                        // Temp removed visibility of this playlist download button
                        ((ImageButton) componentViewResult.componentView).setVisibility(View.VISIBLE);

                        ((ImageButton) componentViewResult.componentView).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        ((ImageButton) componentViewResult.componentView).setImageResource(R.drawable.ic_download_big);
                        componentViewResult.componentView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                        componentViewResult.componentView.setId(R.id.playlist_download_id);

                        if (appCMSPresenter.isAllPlaylistAudioDownloaded(moduleAPI.getContentData())) {
                            ((ImageButton) componentViewResult.componentView).setImageResource(R.drawable.ic_downloaded);
                            componentViewResult.componentView.setVisibility(View.GONE);
                        }
                        componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!appCMSPresenter.isNetworkConnected()) {
                                    appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK, null,
                                            false,
                                            null,
                                            null);
                                    return;
                                }
                                if (!appCMSPresenter.isUserLoggedIn()) {
                                    appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED_AUDIO,
                                            () -> {
                                                appCMSPresenter.setAfterLoginAction(() -> {
                                                });
                                            });
                                } else if (!appCMSPresenter.isUserSubscribed()) {
                                    appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.SUBSCRIPTION_REQUIRED_AUDIO,
                                            () -> {
                                                appCMSPresenter.setAfterLoginAction(() -> {
                                                });
                                            });
                                } else {
                                    if (!appCMSPresenter.isAllPlaylistAudioDownloaded(moduleAPI.getContentData())) {

                                        if (!appCMSPresenter.getDownloadOverCellularEnabled() && appCMSPresenter.getActiveNetworkType() == ConnectivityManager.TYPE_MOBILE) {
                                            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.DOWNLOAD_VIA_MOBILE_DISABLED,
                                                    context.getString(R.string.app_cms_download_over_cellular_disabled_error_message),
                                                    false,
                                                    null,
                                                    null);
                                            return;
                                        }
                                        appCMSPlaylistAdapter.startDownloadPlaylist();

                                    }
                                }
                            }
                        });
                        break;
                    case PAGE_AUDIO_DOWNLOAD_BUTTON_KEY:
                      /*  ((ImageButton) componentViewResult.componentView).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        ((ImageButton) componentViewResult.componentView).setImageResource(R.drawable.ic_download_big);
                        componentViewResult.componentView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                        break;*/
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
                            int radiusDifference = 7;
                            if (BaseView.isTablet(context)) {
                                radiusDifference = 4;
                            }
                            if (moduleAPI.getContentData().get(0).getGist().getMediaType() != null &&
                                    moduleAPI.getContentData().get(0).getGist().getMediaType().toLowerCase().contains(context.getString(R.string.media_type_audio).toLowerCase())) {
                                radiusDifference = 5;
                                if (BaseView.isTablet(context)) {
                                    radiusDifference = 3;
                                }
                            }


                            componentViewResult.componentView.setTag(moduleAPI.getContentData().get(0).getGist().getId());


                            if (appCMSPresenter.isDownloadEnable()) {
                                appCMSPresenter.getUserVideoDownloadStatus(
                                        moduleAPI.getContentData().get(0).getGist().getId(), new UpdateDownloadImageIconAction((ImageButton) componentViewResult.componentView, appCMSPresenter,
                                                moduleAPI.getContentData().get(0), userId, radiusDifference,
                                                moduleAPI.getId()), userId);
                                componentViewResult.componentView.setVisibility(View.VISIBLE);
                            } else {
                                componentViewResult.componentView.setVisibility(View.GONE);
                            }
                        }

                        break;

                    case PAGE_ADD_TO_WATCHLIST_KEY:

                        componentViewResult.componentView.setBackgroundResource(android.R.color.transparent);

                        List<String> filmIds = new ArrayList<>();

                        //TODO- below is to add episodes of shows/series
                        /*if (parentViewType == AppCMSUIKeyType.PAGE_API_SHOWDETAIL_MODULE_KEY &&
                                moduleAPI != null &&
                                moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getSeason() != null &&
                                !moduleAPI.getContentData().get(0).getSeason().isEmpty()) {
                            List<Season_> seasons = moduleAPI.getContentData().get(0).getSeason();
                            int numSeasons = seasons.size();
                            for (int i = 0; i < numSeasons; i++) {
                                if (seasons.get(i).getEpisodes() != null &&
                                        !seasons.get(i).getEpisodes().isEmpty()) {
                                    List<ContentDatum> episodes = seasons.get(i).getEpisodes();
                                    int numEpisodes = episodes.size();
                                    for (int j = 0; j < numEpisodes; j++) {
                                        if (episodes.get(j).getGist() != null &&
                                                episodes.get(j).getGist().getId() != null) {
                                            filmIds.add(episodes.get(j).getGist().getId());
                                        }
                                    }
                                }
                            }
                        } else */
                        if (moduleAPI != null &&
                                moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null) {

                            filmIds.add(moduleAPI.getContentData().get(0).getGist().getId());
                        }

                        boolean filmsAdded = true;
                        for (String filmId : filmIds) {
                            filmsAdded &= appCMSPresenter.isFilmAddedToWatchlist(filmId);
                        }

                        UpdateImageIconAction updateImageIconAction =
                                new UpdateImageIconAction((ImageButton) componentViewResult.componentView,
                                        appCMSPresenter,
                                        filmIds,
                                        moduleAPI.getContentData().get(0));
                        updateImageIconAction.updateWatchlistResponse(filmsAdded);
                        break;

                    case PAGE_VIDEO_WATCH_TRAILER_KEY:
                        if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null) {

                            if (moduleAPI.getContentData().get(0).getContentDetails() != null &&
                                    moduleAPI.getContentData().get(0).getContentDetails().getTrailers() != null &&
                                    !moduleAPI.getContentData().get(0).getContentDetails().getTrailers().isEmpty() &&
                                    moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink() != null &&
                                    moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getId() != null &&
                                    moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getVideoAssets() != null) {

                                final String watchTrailerAction = component.getAction();

                                componentViewResult.componentView.setOnClickListener(v -> {
                                    String[] extraData = new String[3];
                                    extraData[0] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink();
                                    extraData[1] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getVideoAssets().getHls();
                                    extraData[2] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getId();

                                    if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink(),
                                            watchTrailerAction,
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
                                final String watchTrailerAction = component.getAction();

                                componentViewResult.componentView.setOnClickListener(v -> {
                                    String[] extraData = new String[3];
                                    extraData[0] = moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getPermalink();
                                    extraData[1] = moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getVideoAssets().getHls();
                                    extraData[2] = moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getId();

                                    if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getShowDetails().getTrailers().get(0).getPermalink(),
                                            watchTrailerAction,
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
                        } else {
                            componentViewResult.shouldHideComponent = true;
                            componentViewResult.componentView.setVisibility(View.GONE);
                        }
                        break;

                    case PAGE_VIDEO_PLAY_BUTTON_KEY:

                        componentViewResult.componentView.setVisibility(View.VISIBLE);
                        componentViewResult.componentView.setOnClickListener(v -> {
                            if (moduleAPI != null &&
                                    moduleAPI.getContentData() != null &&
                                    !moduleAPI.getContentData().isEmpty() &&
                                    moduleAPI.getContentData().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getStreamingInfo() != null &&
                                    moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets() != null) {
                                VideoAssets videoAssets = moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets();
                                String vidUrl = videoAssets.getHls();
                                if (TextUtils.isEmpty(vidUrl)) {
                                    for (int i = 0; i < videoAssets.getMpeg().size() && TextUtils.isEmpty(vidUrl); i++) {
                                        vidUrl = videoAssets.getMpeg().get(i).getUrl();
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
                                            moduleAPI.getContentData().get(0).getGist().getId(),
                                            currentPlayingIndex, relatedVideoIds,
                                            moduleAPI.getContentData().get(0).getGist().getWatchedTime(),
                                            component.getAction());

                                }
                            }
                        });


                        componentViewResult.componentView.setPadding(8, 8, 8, 8);
                        componentViewResult.componentView.setBackground(ContextCompat.getDrawable(context, R.drawable.play_icon));
                        componentViewResult.componentView.getBackground().setTint(tintColor);
                        componentViewResult.componentView.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
//                        }
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
                        ((ImageButton) componentViewResult.componentView).setScaleType(ImageView.ScaleType.FIT_CENTER);
//                        componentViewResult.componentView.setPadding(8, 0, 0, 8);

                        int width = (int) context.getResources().getDimension(R.dimen.close_button_size);
                        int height = (int) context.getResources().getDimension(R.dimen.close_button_size);
                        int leftMargin = (int) context.getResources().getDimension(R.dimen.close_button_margin);
                        int topMargin = (int) context.getResources().getDimension(R.dimen.close_button_margin);
                        int rightMargin = (int) context.getResources().getDimension(R.dimen.close_button_margin);
                        int bottomMargin = (int) context.getResources().getDimension(R.dimen.close_button_margin);

//                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) componentViewResult.componentView.getLayoutParams();
                        ViewGroup.MarginLayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);//.LayoutParams(width, height);//.MarginLayoutParams(width, height);
                        layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
//                        layoutParams.height = height;
//                        layoutParams.width = width;

                        componentViewResult.componentView.setLayoutParams(layoutParams);

                        int fillColor = appCMSPresenter.getGeneralTextColor();
                        ((ImageButton) componentViewResult.componentView).getDrawable().setColorFilter(new PorterDuffColorFilter(fillColor, PorterDuff.Mode.MULTIPLY));
                        componentViewResult.componentView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

                        final String closeAction = component.getAction();

                        componentViewResult.componentView.setOnClickListener(v -> {

                            if (appCMSPresenter.getCurrentActivity() != null) {
                                appCMSPresenter.getCurrentActivity().onBackPressed();
                            } else if (!appCMSPresenter.launchButtonSelectedAction(null,
                                    closeAction,
                                    null,
                                    null,
                                    null,
                                    false,
                                    0,
                                    null)) {

                            }
                        });

                        break;

                    case PAGE_VIDEO_CAST_KEY:

                        componentViewResult.componentView = new LinearLayout(context);
                        ((LinearLayout) componentViewResult.componentView).setOrientation(LinearLayout.HORIZONTAL);

                        ImageButton mMediaRouteButton = appCMSPresenter.getCurrentMediaRouteButton();
                        if (mMediaRouteButton != null) {
                            LinearLayout.LayoutParams mMediaRouteButtonLayoutParams =
                                    new LinearLayout.LayoutParams((int) BaseView.convertDpToPixel(28, context),
                                            (int) BaseView.convertDpToPixel(28, context));
                            mMediaRouteButtonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                            mMediaRouteButton.setLayoutParams(mMediaRouteButtonLayoutParams);
                            mMediaRouteButton.setPadding(8, 8, 8, 8);
                            mMediaRouteButton.setBackgroundResource(android.R.color.transparent);
                            boolean allowFreePlay = !appCMSPresenter.isAppSVOD();
                            setCasting(allowFreePlay, //** TODO: Replace with actual value from API response *//*
                                    appCMSPresenter,
                                    mMediaRouteButton,
                                    moduleAPI.getContentData().get(0).getGist().getWatchedTime());

                            pageView.setReparentChromecastButton(false);

                            if (mMediaRouteButton.getParent() != null &&
                                    mMediaRouteButton.getParent() instanceof ViewGroup) {
                                ((ViewGroup) mMediaRouteButton.getParent()).removeView(mMediaRouteButton);
                            }

                            ((LinearLayout) componentViewResult.componentView).addView(mMediaRouteButton);
                        }

                        //  ((LinearLayout) componentViewResult.componentView).addView(shareButton);

                        componentViewResult.componentView.requestLayout();


                        break;
                    case PAGE_VIDEO_SHARE_KEY:

                        ((ImageButton) componentViewResult.componentView).setImageResource(R.drawable.ic_share);
                        ((ImageButton) componentViewResult.componentView).setScaleType(ImageView.ScaleType.FIT_CENTER);
                        ((ImageButton) componentViewResult.componentView).getDrawable().setColorFilter(new PorterDuffColorFilter(appCMSPresenter.getGeneralTextColor(), PorterDuff.Mode.MULTIPLY));
                        componentViewResult.componentView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

                        final String shareAction = component.getAction();

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
                                        shareAction,
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
                        if (moduleAPI != null &&
                                moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null &&
                                moduleAPI.getContentData().get(0).getGist().getContentType() != null &&
                                moduleAPI.getContentData().get(0).getGist().getContentType().equalsIgnoreCase("AUDIO")) {
                            componentViewResult.componentView.setVisibility(View.GONE);

                        }
                        if (appCMSPresenter != null &&
                                appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS &&
                                appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.ANDROID) {
                            componentViewResult.componentView.setVisibility(View.GONE);
                        }
                        break;

                    case PAGE_FORGOTPASSWORD_KEY:
                        componentViewResult.componentView.setBackgroundColor(
                                ContextCompat.getColor(context, android.R.color.transparent));
                        ((Button) componentViewResult.componentView)
                                .setTextColor(appCMSPresenter.getGeneralTextColor());
                        break;

                    case PAGE_REMOVEALL_KEY:
                        componentViewResult.addToPageView = true;

                        FrameLayout.LayoutParams removeAllLayoutParams =
                                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);

                        removeAllLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

                        ((TextView) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
                        componentViewResult.componentView.setBackgroundColor(appCMSPresenter.getBrandPrimaryCtaColor());

                        componentViewResult.componentView.setLayoutParams(removeAllLayoutParams);
                        componentViewResult.componentView.setId(R.id.remove_all_download_id);
                        componentViewResult.onInternalEvent = new OnRemoveAllInternalEvent(moduleId,
                                componentViewResult.componentView);
                        componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                            OnInternalEvent onInternalEvent = componentViewResult.onInternalEvent;

                            @Override
                            public void onClick(final View v) {
                                boolean deleteAllFiles = true;
                                appCMSPresenter.showLoadingDialog(true);

                                switch (jsonValueKeyMap.get(viewType)) {
                                    case PAGE_HISTORY_01_MODULE_KEY:
                                    case PAGE_HISTORY_02_MODULE_KEY:
                                        appCMSPresenter.clearHistory(appCMSDeleteHistoryResult -> {
                                            onInternalEvent.sendEvent(null);
                                            v.setVisibility(View.GONE);
                                            appCMSPresenter.showLoadingDialog(false);
                                        });
                                        break;

                                    case PAGE_DOWNLOAD_01_MODULE_KEY:
                                    case PAGE_DOWNLOAD_02_MODULE_KEY:
                                        appCMSPresenter.clearDownload(appCMSDownloadStatusResult -> {
                                            onInternalEvent.sendEvent(null);
                                            v.setVisibility(View.GONE);
                                            appCMSPresenter.showLoadingDialog(false);

                                        }, deleteAllFiles);
                                        break;

                                    case PAGE_WATCHLIST_01_MODULE_KEY:
                                    case PAGE_WATCHLIST_02_MODULE_KEY:
                                        appCMSPresenter.clearWatchlist(appCMSAddToWatchlistResult -> {
                                            onInternalEvent.sendEvent(null);
                                            v.setVisibility(View.GONE);
                                            appCMSPresenter.showLoadingDialog(false);
                                        });
                                        break;

                                    default:
                                        break;
                                }
                            }
                        });
                        break;

                    case PAGE_ARTICLE_PREVIOUS_BUTTON_KEY:
                        componentViewResult.addToPageView = true;
                        FrameLayout.LayoutParams paramsPreviousButton =
                                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsPreviousButton.setMargins(20, 0, 0, 0);
                        paramsPreviousButton.gravity = Gravity.BOTTOM | Gravity.LEFT;
                        View previousButtonView = componentViewResult.componentView;
                        previousButtonView.setLayoutParams(paramsPreviousButton);
                        previousButtonView.setPadding(20, 0, 20, 0);
                        previousButtonView.setId(R.id.article_prev_button);
                        ((Button) previousButtonView).setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());

                        if (appCMSPresenter.getCurrentArticleIndex() < 0) {
                            previousButtonView.setBackgroundColor(Color.parseColor("#c8c8c8"));
                        } else {
                            previousButtonView.setBackgroundColor(appCMSPresenter.getBrandPrimaryCtaColor());
                        }
                        previousButtonView.setOnClickListener(v -> {


                            if (moduleAPI != null &&
                                    moduleAPI.getContentData() != null &&
                                    moduleAPI.getContentData().get(0) != null &&
                                    moduleAPI.getContentData().get(0).getContentDetails() != null &&
                                    moduleAPI.getContentData().get(0).getContentDetails().getRelatedArticleIds() != null &&
                                    appCMSPresenter.getRelatedArticleIds() != null) {
                                int currentIndex = appCMSPresenter.getCurrentArticleIndex();
                                currentIndex = currentIndex - 1;
                                if (currentIndex < -1) {
                                    return;

                                }
                                appCMSPresenter.setCurrentArticleIndex(currentIndex);
                                appCMSPresenter.navigateToArticlePage(
                                        appCMSPresenter.getRelatedArticleIds().get(currentIndex + 1),
                                        moduleAPI.getContentData().get(0).getGist().getTitle(),
                                        false,
                                        () -> {
                                            if (appCMSPresenter.getCurrentArticleIndex() < 0) {
                                                previousButtonView.setBackgroundColor(Color.parseColor("#c8c8c8"));
                                                previousButtonView.setEnabled(false);
                                            } else {
                                                previousButtonView.setBackgroundColor(appCMSPresenter.getBrandPrimaryCtaColor());
                                                previousButtonView.setEnabled(true);
                                            }
                                            CustomWebView customWebView = pageView.findViewById(R.id.article_web_view);
                                            if (customWebView != null) {
                                                customWebView.loadUrl("about:blank");
                                            }
                                        }, false);
                            }
                        });

                        break;
                    case PAGE_BOTTOM_BACKGROUND_ARTICLE_KEY:
                        componentViewResult.addToPageView = true;
                        FrameLayout.LayoutParams bottomBg =
                                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        FrameLayout.LayoutParams.WRAP_CONTENT);
                        bottomBg.gravity = Gravity.BOTTOM;
                        componentViewResult.componentView.setLayoutParams(bottomBg);
                        ((Button) componentViewResult.componentView).setBackgroundColor(Color.parseColor(component.getBackgroundColor()));
                        break;
                    case PAGE_ARTICLE_NEXT_BUTTON_KEY:
                        componentViewResult.addToPageView = true;

                        FrameLayout.LayoutParams paramsNextButton =
                                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsNextButton.setMargins(0, 0, 20, 0);
                        paramsNextButton.gravity = Gravity.BOTTOM | Gravity.RIGHT;
                        componentViewResult.componentView.setLayoutParams(paramsNextButton);
                        componentViewResult.componentView.setPadding(30, 0, 30, 0);
                        componentViewResult.componentView.setId(R.id.article_next_button);
                        ((Button) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
                        ((Button) componentViewResult.componentView).setBackgroundColor(appCMSPresenter.getBrandPrimaryCtaColor());
                        if (moduleAPI != null &&
                                moduleAPI.getContentData() != null &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getContentDetails() != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getRelatedArticleIds() != null &&
                                appCMSPresenter.getRelatedArticleIds() != null) {

                            List<String> articleIDs = appCMSPresenter.getRelatedArticleIds();
                            if (appCMSPresenter.getCurrentArticleIndex() == appCMSPresenter.getRelatedArticleIds().size() - 2) {
                                ((Button) componentViewResult.componentView).setBackgroundColor(Color.parseColor("#c8c8c8"));
                                ((Button) componentViewResult.componentView).setEnabled(false);
                            }
                            if (appCMSPresenter.getCurrentArticleIndex() == -1) {
                                articleIDs.add(0, moduleAPI.getContentData().get(0).getGist().getId());
                                appCMSPresenter.setRelatedArticleIds(articleIDs);
                            }

                            componentViewResult.componentView.setOnClickListener(v -> {
                                int currentIndex = appCMSPresenter.getCurrentArticleIndex();
                                if (appCMSPresenter.getRelatedArticleIds() != null &&
                                        currentIndex < appCMSPresenter.getRelatedArticleIds().size() - 2) {
                                    currentIndex = currentIndex + 1;
                                    appCMSPresenter.setCurrentArticleIndex(currentIndex);
                                    appCMSPresenter.navigateToArticlePage(appCMSPresenter.getRelatedArticleIds().get(currentIndex + 1),
                                            moduleAPI.getContentData().get(0).getGist().getTitle(), false,
                                            () -> {
                                                if (appCMSPresenter.getCurrentArticleIndex() == appCMSPresenter.getRelatedArticleIds().size() - 2) {
                                                    ((Button) componentViewResult.componentView).setBackgroundColor(Color.parseColor("#c8c8c8"));
                                                    ((Button) componentViewResult.componentView).setEnabled(false);
                                                }

                                                if (appCMSPresenter.getCurrentArticleIndex() > -1) {
                                                    View prevButton = appCMSPresenter.getCurrentActivity().findViewById(R.id.article_prev_button);
                                                    if (prevButton != null) {
                                                        prevButton.setBackgroundColor(appCMSPresenter.getBrandPrimaryCtaColor());
                                                        prevButton.setEnabled(true);
                                                    }
                                                }
                                                CustomWebView customWebView = pageView.findViewById(R.id.article_web_view);
                                                if (customWebView != null) {
                                                    customWebView.loadUrl("about:blank");
                                                }
                                            }, false);

                                }

                            });
                        }
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
                        if (moduleAPI != null && (jsonValueKeyMap.get(moduleAPI.getModuleType())
                                == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY_01 ||
                                jsonValueKeyMap.get(moduleAPI.getModuleType())
                                        == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY_02 ||
                                jsonValueKeyMap.get(moduleAPI.getModuleType())
                                        == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY_03||
                                jsonValueKeyMap.get(moduleAPI.getModuleType())
                                        == AppCMSUIKeyType.PAGE_AUTOPLAY_LANDSCAPE_MODULE_KEY
                        )) {
                            componentViewResult.componentView.setId(R.id.autoplay_cancel_button);
                            componentViewResult.componentView.setOnClickListener(v -> {
                                if (!appCMSPresenter.sendCloseAutoplayAction(null,
                                        true,
                                        false)) {
                                    //Log.e(TAG, "Could not perform close action: " +
//                                            " action: " +
//                                            component.getAction());
                                }
                            });
                        } else {
                            componentViewResult.componentView.setId(R.id.download_quality_cancel_button);
                        }
                        applyBorderToComponent(
                                context,
                                componentViewResult.componentView,
                                component,
                                -1);
                        break;

                    case PAGE_SUBSCRIBE_EMAIL_GO_BUTTON_KEY:
                        ((Button) componentViewResult.componentView).setTextColor(ContextCompat.getColor(context, android.R.color.black));
                        ((Button) componentViewResult.componentView).setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                        ((Button) componentViewResult.componentView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
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

                    default:
                        if (componentKey == AppCMSUIKeyType.PAGE_SETTINGS_UPGRADE_PLAN_PROFILE_KEY) {
                            if (!appCMSPresenter.isUserSubscribed()) {
                                ((TextView) componentViewResult.componentView)
                                        .setText(context.getString(R.string.app_cms_page_upgrade_subscribe_button_text));
                            } else if (!appCMSPresenter.upgradesAvailableForUser()) {
                                componentViewResult.componentView.setVisibility(View.GONE);
                            }
                            if (paymentProcessor != null && paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_sslcommerz_payment_processor))) {
                                componentViewResult.componentView.setVisibility(View.GONE);
                            }
                        }

                        if (componentKey == AppCMSUIKeyType.PAGE_SETTINGS_CANCEL_PLAN_PROFILE_KEY) {
                            if (appCMSPresenter.shouldDisplaySubscriptionCancelButton() &&
                                    appCMSPresenter.isUserSubscribed() &&
                                    !appCMSPresenter.isExistingGooglePlaySubscriptionSuspended() &&
                                    appCMSPresenter.isSubscriptionCompleted()) {
                                componentViewResult.componentView.setVisibility(View.VISIBLE);
                            } else {
                                componentViewResult.componentView.setVisibility(View.GONE);
                            }
                        }

                        componentViewResult.componentView.setOnClickListener(v -> {
                            String action = component.getAction();
                            String[] extraData = new String[1];
                            extraData[0] = component.getKey();

                            appCMSPresenter.launchButtonSelectedAction(null,
                                    action,
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
                                .setTextColor(appCMSPresenter.getGeneralTextColor());
                        //.getGeneral().getBlockTitleColor()));
                    }
                }
                break;

            case PAGE_ADS_KEY:
                //todo need to work for managing Subscribed User case scanerio
                //if (!appCMSPresenter.isUserSubscribed()) {
                componentViewResult.componentView = new LinearLayout(context);
                AdView adView = new AdView(context);
                adView.setFocusable(false);
                adView.setEnabled(false);
                adView.setClickable(false);
                switch (jsonValueKeyMap.get(viewType)) {
                    case PAGE_BANNER_AD_MODULE_KEY:
                        adView.setAdSize(AdSize.BANNER);
                        break;
                    case PAGE_MEDIAM_RECTANGLE_AD_MODULE_KEY:
                        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
                        break;
                }

                if (moduleAPI != null &&
                        moduleAPI.getMetadataMap() != null &&
                        moduleAPI.getMetadataMap() instanceof LinkedTreeMap) {
                    LinkedTreeMap<String, String> admap = (LinkedTreeMap<String, String>) moduleAPI.getMetadataMap();
                    MobileAds.initialize(context, admap.get("adTag"));
                    adView.setAdUnitId(admap.get("adTag"));
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                    ((LinearLayout) componentViewResult.componentView).addView(adView);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) adView.getLayoutParams();
                    if (params != null) {
                        params.weight = 1.0f;
                        params.gravity = Gravity.CENTER;
                        adView.setLayoutParams(params);
                    }
                } else {
                    componentViewResult.componentView.setVisibility(View.GONE);
                }
                //}
                break;

            case PAGE_LABEL_KEY:
            case PAGE_TEXTVIEW_KEY:
                boolean resizeText = false;
                int textColor = ContextCompat.getColor(context, R.color.colorAccent);

                boolean showTrayLabel = false;
                int numSeasons = 0;
                if (moduleType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                    numSeasons = moduleAPI.getContentData().get(0).getSeason().size();
                }
                if (componentKey == AppCMSUIKeyType.PAGE_TRAY_TITLE_KEY &&
                        moduleType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY &&
                        moduleAPI != null && moduleAPI.getContentData() != null &&
                        !moduleAPI.getContentData().isEmpty() &&
                        moduleAPI.getContentData().get(0) != null &&
                        moduleAPI.getContentData().get(0).getSeason() != null) {
                    if (1 < numSeasons) {
                        showTrayLabel = true;
                    }
                }


                if (showTrayLabel) {
                    List<Season_> seasons = moduleAPI.getContentData().get(0).getSeason();
                    numSeasons = seasons.size();

                    componentViewResult.componentView = new Spinner(context, Spinner.MODE_DROPDOWN);


                    try {
                        componentViewResult.componentView.getBackground().setColorFilter(Color.parseColor(
                                getColor(context,
                                        appCMSPresenter.getAppCtaBackgroundColor())),
                                PorterDuff.Mode.SRC_ATOP);
                    } catch (Exception e) {
                        //
                    }

                    ArrayAdapter<String> seasonTrayAdapter = new SeasonsAdapterView(context,
                            appCMSPresenter,
                            component,
                            jsonValueKeyMap);


                    for (int i = 0; i < numSeasons; i++) {
                        if (!TextUtils.isEmpty(seasons.get(i).getTitle())) {
                            seasonTrayAdapter.add(seasons.get(i).getTitle());
                        } else {
                            StringBuilder seasonTitleSb = new StringBuilder(context.getString(R.string.app_cms_episodic_season_prefix));
                            seasonTitleSb.append(context.getString(R.string.blank_separator));
                            seasonTitleSb.append(i + 1);
                            seasonTrayAdapter.add(seasonTitleSb.toString());
                        }
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
                                    getColor(context, appCMSPresenter.getAppBackgroundColor()))));
                        } catch (Exception e) {
                            //
                        }
                    }

                    seasonTrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    ((Spinner) componentViewResult.componentView).setAdapter(seasonTrayAdapter);
                } else if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_FIGHT_SELECTION_TXT_KEY) {
                    if (moduleAPI != null && moduleAPI.getContentData() != null &&
                            moduleAPI.getContentData().get(0) != null && moduleAPI.getContentData().get(0).getLiveEvents() != null &&
                            moduleAPI.getContentData().get(0).getLiveEvents().get(0) != null && moduleAPI.getContentData().get(0).getLiveEvents().get(0).getFights() != null) {
                        List<Fights> fights = moduleAPI.getContentData().get(0).getLiveEvents().get(0).getFights();
                        componentViewResult.componentView = new LinearLayout(context);
                        LinearLayout.LayoutParams layoutParamsDetail =
                                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                        ((LinearLayout) componentViewResult.componentView).setLayoutParams(layoutParamsDetail);
                        ((LinearLayout) componentViewResult.componentView).setOrientation(LinearLayout.HORIZONTAL);
                        ((LinearLayout) componentViewResult.componentView).setGravity(Gravity.LEFT);
                        ((LinearLayout) componentViewResult.componentView).setBackgroundColor(Color.parseColor(getColor(context, "#d6202d")));

                        Spinner spinnerFight = new Spinner(context, Spinner.MODE_DROPDOWN);
                        spinnerFight.setLayoutParams(layoutParamsDetail);
                        try {
                            spinnerFight.getBackground().setColorFilter(Color.parseColor(
                                    getColor(context,
                                            "#ffffff")),
                                    PorterDuff.Mode.SRC_ATOP);
                        } catch (Exception e) {
                            //
                        }
                        try {
                            spinnerFight.setPopupBackgroundDrawable(new ColorDrawable(Color.parseColor(
                                    getColor(context, "#d6202d"))));
                        } catch (Exception e) {
                            //
                        }
                        ArrayAdapter<String> FightTrayAdapter = new FightSmmaryAdapterView(context,
                                appCMSPresenter,
                                component,
                                jsonValueKeyMap);

                        ArrayList<Fights> listFight = new ArrayList<Fights>();

                        for (int i = 0; i < fights.size(); i++) {
                            listFight.add(fights.get(0));

                            if (!TextUtils.isEmpty(fights.get(i).getFighter1_LastName())) {
                                String fighter1Name = fights.get(i).getFighter1_LastName();
                                String fighter2Name = fights.get(i).getFighter2_LastName();
                                if (fights.get(i).getWinnerId() != null && !TextUtils.isEmpty(fights.get(i).getWinnerId())) {
                                    if (fights.get(i).getWinnerId().equalsIgnoreCase(fights.get(i).getFighter1_Id())) {
                                        fighter1Name = fighter1Name + "(Won)";
                                    } else if (fights.get(i).getWinnerId().equalsIgnoreCase(fights.get(i).getFighter2Id())) {
                                        fighter2Name = fighter2Name + "(Won)";
                                    }
                                }
                                FightTrayAdapter.add(i + 1 + " " + fighter1Name + "/" + fighter2Name);
                            }
                        }

                        System.out.println("set fight selection adapter");
                        OnFightSelectedListener onItemSelectListener = new OnFightSelectedListener(listFight, appCMSPresenter, context, moduleAPI, component, jsonValueKeyMap);
                        try {
                            componentViewResult.onInternalEvent.setModuleId(moduleId);

                            spinnerFight.setEnabled(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        spinnerFight.setOnItemSelectedListener(onItemSelectListener);

                        try {
                            FightTrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spinnerFight.setAdapter(FightTrayAdapter);
                            spinnerFight.setSelection(0);
                            FightTrayAdapter.notifyDataSetChanged();
                            System.out.println("after set fight selection adapter");

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(" exception", e.toString());

                        }
                        ((LinearLayout) componentViewResult.componentView).addView(spinnerFight);
                    }
                } else if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_RECORD_TYPE_KEY) {
                    componentViewResult.componentView = new LinearLayout(context);
                    ((LinearLayout) componentViewResult.componentView).setGravity(Gravity.BOTTOM);
                    ((LinearLayout) componentViewResult.componentView).setOrientation(LinearLayout.HORIZONTAL);
                } else {
                    componentViewResult.componentView = new TextView(context);
                    if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_PHOTO_GALLERY_TITLE_TXT_KEY) {
                        ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getTitle());
                        ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor("#000000"));
                        ((TextView) componentViewResult.componentView).setGravity(Gravity.CENTER_VERTICAL);
                        ((TextView) componentViewResult.componentView).setSingleLine(true);
                        ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                    }
//                    if (moduleType == AppCMSUIKeyType.PAGE_AC_TEAM_SCHEDULE_MODULE_KEY) {
//
//
//                        if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_TRAY_TITLE_KEY) {
//                            ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle());
//                        } else if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_SCHEDULE_TRAY_TITLE_KEY) {
//                            ((TextView) componentViewResult.componentView).setText("2018 League Schedule");
//                        } else if (component.getText() != null) {
//                            ((TextView) componentViewResult.componentView).setText(component.getText());
//                        }
//                        ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor("#ffffff"));
//                        ((TextView) componentViewResult.componentView).setGravity(Gravity.CENTER_VERTICAL);
//                        ((TextView) componentViewResult.componentView).setSingleLine(true);
//                        ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
//                    }
                    /*else {
                        ((TextView) componentViewResult.componentView).setText("title");
                        ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor("#ffffff"));
                        ((TextView) componentViewResult.componentView).setGravity(Gravity.CENTER_VERTICAL);

                    }*/

                    if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_PHOTO_GALLERY_AUTH_TXT_KEY) {
                        if (moduleAPI.getContentData().get(0).getContentDetails() != null) {
                            StringBuilder authDateAndPhotoCount = new StringBuilder();
                            if (moduleAPI.getContentData().get(0).getContentDetails().getAuthor() != null && moduleAPI.getContentData().get(0).getContentDetails().getAuthor().getName() != null) {
                                authDateAndPhotoCount.append(moduleAPI.getContentData().get(0).getContentDetails().getAuthor().getName());
                            }
                            if (moduleAPI.getContentData().get(0).getGist().getPublishDate() != null) {
                                authDateAndPhotoCount.append(" | ")
                                        .append(AppCMSPresenter.getDateFormat(Long.parseLong(moduleAPI.getContentData().get(0).getGist().getPublishDate()), "MMM dd"));
                            }
                            if (moduleAPI.getContentData().get(0).getStreamingInfo() != null && moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets() != null) {
                                authDateAndPhotoCount.append(" | ")
                                        .append(moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets().size() + " Photos");
                            }

                            ((TextView) componentViewResult.componentView).setText("By " + authDateAndPhotoCount.toString());
                            ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor("#000000"));
                        }
                    }

                    if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_PHOTO_GALLERY_SUBTITLE_TXT_KEY) {

                        StringBuilder tags = new StringBuilder();
                        String tagsName = "";
                        tags.append("<b>TAGGED:</b> ");
                        if (moduleAPI.getContentData().get(0).getTags() != null && moduleAPI.getContentData().get(0).getTags().size() > 0) {
                            for (Tag tag : moduleAPI.getContentData().get(0).getTags())
                                tags.append("" + tag.getTitle().toUpperCase() + ",");
                            tagsName = tags.length() > 0 ? tags.substring(0, tags.length() - 1) : "";
                        }
                        ((TextView) componentViewResult.componentView).setText(Html.fromHtml(tagsName));
                        ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor("#000000"));
                    }

                    if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_SD_CARD_FOR_DOWNLOADS_TEXT_KEY &&
                            /*!appCMSPresenter.isAppSVOD() &&*/
                            !appCMSPresenter.getAppCMSMain().getFeatures().isMobileAppDownloads() ||
                            (moduleType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY &&
                                    numSeasons == 0)) {
                        componentViewResult.componentView.setVisibility(View.GONE);
                        componentViewResult.shouldHideComponent = true;
                    } else if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_USER_MANAGEMENT_AUTOPLAY_TEXT_KEY &&
                            !appCMSPresenter.isAppSVOD() &&
                            !appCMSPresenter.getAppCMSMain().getFeatures().isAutoPlay()) {
                        componentViewResult.componentView.setVisibility(View.GONE);
                        componentViewResult.shouldHideComponent = true;
                    }

                    if (!TextUtils.isEmpty(appCMSPresenter.getAppTextColor())) {
                        textColor = Color.parseColor(getColor(context, appCMSPresenter.getAppTextColor()));
                    } else if (component.getStyles() != null) {
                        if (!TextUtils.isEmpty(component.getStyles().getColor())) {
                            textColor = Color.parseColor(getColor(context, component.getStyles().getColor()));
                        } else if (appCMSPresenter.getGeneralTextColor() != 0) {
                            textColor =
                                    Color.parseColor(getColor(context, appCMSPresenter.getAppTextColor()));
                        }
                    }

                    if (!TextUtils.isEmpty(component.getTextAlignment()) &&
                            component.getTextAlignment().equals(context.getString(R.string.app_cms_text_alignment_right))) {
                        componentViewResult.componentView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    }

                    if (componentKey == AppCMSUIKeyType.PAGE_PLAYLIST_TITLE) {

                        int textFontColor = appCMSPresenter.getGeneralTextColor();
                        if (!TextUtils.isEmpty(component.getTextColor())) {
                            textFontColor = Color.parseColor(getColor(context, component.getTextColor()));
                        }
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
                        if (moduleAPI != null
                                && moduleAPI.getContentData() != null
                                && moduleAPI.getContentData().get(0) != null
                                && moduleAPI.getContentData().get(0).getGist() != null
                                && moduleAPI.getContentData().get(0).getGist().getTitle() != null
                                ) {
                            ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getTitle());
                        }
                    }
                    if (componentKey == AppCMSUIKeyType.PAGE_PLAYLIST_SUB_TITLE) {

                        int textFontColor = appCMSPresenter.getGeneralTextColor();
                        if (!TextUtils.isEmpty(component.getTextColor())) {
                            textFontColor = Color.parseColor(getColor(context, component.getTextColor()));
                        }
                        textFontColor = Color.parseColor(getColor(context, component.getTextColor()));

                        ((TextView) componentViewResult.componentView).setTextColor(textFontColor);
                        ((TextView) componentViewResult.componentView).setGravity(Gravity.START);

//                    if (!TextUtils.isEmpty(component.getFontFamily())) {
//                        setTypeFace(context,
//                                appCMSPresenter,
//                                jsonValueKeyMap,
//                                component,
//                                (TextView) componentViewResult.componentView);
//                    }

                        if (component.getFontSize() > 0) {
                            ((TextView) componentViewResult.componentView).setTextSize(component.getFontSize());
                        } else if (BaseView.getFontSize(context, component.getLayout()) > 0) {
                            ((TextView) componentViewResult.componentView).setTextSize(BaseView.getFontSize(context, component.getLayout()));
                        }
                        if (moduleAPI != null
                                && moduleAPI.getContentData() != null
                                && moduleAPI.getContentData().get(0) != null
                                && moduleAPI.getContentData().get(0).getAudioList() != null
                                ) {
                            int songNum = moduleAPI.getContentData().get(0).getAudioList().size();
                            ((TextView) componentViewResult.componentView).setText((moduleAPI.getContentData().get(0).getAudioList().size() > 1) ? " " + songNum + " SONGS" : " " + songNum + " SONG");
                        }
                        break;

                    }
                    if (componentKey == AppCMSUIKeyType.PAGE_BANNER_DETAIL_TITLE) {
                        int textBgColor = appCMSPresenter.getGeneralTextColor();

                        int textFontColor = appCMSPresenter.getGeneralTextColor();
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
                    if (componentKey == AppCMSUIKeyType.PAGE_GRID_THUMBNAIL_INFO
                            || componentKey == AppCMSUIKeyType.PAGE_GRID_PHOTO_GALLERY_THUMBNAIL_INFO) {
                        int textBgColor = appCMSPresenter.getGeneralTextColor();
                        if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                            textBgColor = Color.parseColor(getColorWithOpacity(context, component.getBackgroundColor(), component.getOpacity()));
                        }
                        int textFontColor = appCMSPresenter.getGeneralTextColor();
                        if (!TextUtils.isEmpty(component.getTextColor())) {
                            textFontColor = Color.parseColor(getColor(context, component.getTextColor()));
                        }
                        componentViewResult.componentView.setBackgroundColor(textBgColor);
                        ((TextView) componentViewResult.componentView).setTextColor(textFontColor);
                        ((TextView) componentViewResult.componentView).setGravity(Gravity.LEFT);

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
                        textColor = appCMSPresenter.getGeneralTextColor();
                        ((TextView) componentViewResult.componentView).setTextColor(textColor);
                    } else if (componentKey != AppCMSUIKeyType.PAGE_TRAY_TITLE_KEY) {
                        ((TextView) componentViewResult.componentView).setTextColor(textColor);
                    } else {
                        ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor(getColor(context,
                                appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor())));
                    }

                    if (BaseView.getFontSize(context, component.getLayout()) > 0) {
                        ((TextView) componentViewResult.componentView).setTextSize(BaseView.getFontSize(context, component.getLayout()));
                    }

                    if (moduleType == AppCMSUIKeyType.PAGE_PLAYER_DETAIL_MODULE_KEY) {


                        if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_WEIGHT_DIVISION_VALUE_TXT_KEY) {
                            if (moduleAPI.getContentData() != null && moduleAPI.getContentData().get(0) != null
                                    && moduleAPI.getContentData().get(0).getGist() != null && moduleAPI.getContentData().get(0).getGist().getMetadata() != null) {
                                for (int i = 0; i < moduleAPI.getContentData().get(0).getGist().getMetadata().size(); i++) {
                                    if (moduleAPI.getContentData().get(0).getGist().getMetadata().get(i).getName().equalsIgnoreCase("weight_division")) {
                                        ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getMetadata().get(i).getValue());

                                    }
                                }
                            }
                        }
                        if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_PLAYER_NAME_KEY) {
                            if (moduleAPI.getContentData() != null && moduleAPI.getContentData().get(0) != null
                                    && moduleAPI.getContentData().get(0).getGist() != null && moduleAPI.getContentData().get(0).getGist().getFirstName() != null) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getFirstName());

                            }
                        }
                        if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_PLAYER_SCORE_TEXT) {
                            if (moduleAPI.getContentData() != null && moduleAPI.getContentData().get(0) != null
                                    && moduleAPI.getContentData().get(0).getGist() != null && moduleAPI.getContentData().get(0).getGist().getFirstName() != null) {
                                for (int i = 0; i < moduleAPI.getContentData().get(0).getGist().getMetadata().size(); i++) {
                                    if (moduleAPI.getContentData().get(0).getGist().getMetadata().get(i).getName().equalsIgnoreCase("record")) {
                                        ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getMetadata().get(i).getValue());

                                    }
                                }
                            }
                        }
                        if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_WEIGHT_VALUE_TEXT) {
                            if (moduleAPI.getContentData() != null && moduleAPI.getContentData().get(0) != null
                                    && moduleAPI.getContentData().get(0).getGist() != null && moduleAPI.getContentData().get(0).getGist().getWeight() != null) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getWeight());

                            }
                        }

                        if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_HEIGHT_VALUE_TEXT) {
                            if (moduleAPI.getContentData() != null && moduleAPI.getContentData().get(0) != null
                                    && moduleAPI.getContentData().get(0).getGist() != null && moduleAPI.getContentData().get(0).getGist().getHeight() != null) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getHeight());

                            }
                        }
                        if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_HEIGHT_VALUE_TEXT) {
                            if (moduleAPI.getContentData() != null && moduleAPI.getContentData().get(0) != null
                                    && moduleAPI.getContentData().get(0).getGist() != null && moduleAPI.getContentData().get(0).getGist().getHeight() != null) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getHeight());

                            }
                        }
                        if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_BIRTHDATE_VALUE_TEXT) {
                            if (moduleAPI.getContentData() != null && moduleAPI.getContentData().get(0) != null
                                    && moduleAPI.getContentData().get(0).getGist() != null && moduleAPI.getContentData().get(0).getGist().getDob() != null) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getDob());

                            }
                        }
                        if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_HOMETOWN_VALUE_TEXT) {
                            if (moduleAPI.getContentData() != null && moduleAPI.getContentData().get(0) != null
                                    && moduleAPI.getContentData().get(0).getGist() != null && moduleAPI.getContentData().get(0).getGist().getBirthPlace() != null) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getBirthPlace());

                            }
                        }
                        ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor(getColor(context, component.getTextColor())));
                        ((TextView) componentViewResult.componentView).setGravity(Gravity.CENTER_VERTICAL);
                        ((TextView) componentViewResult.componentView).setSingleLine(true);
                        ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                    }
                    if (jsonValueKeyMap.get(component.getKey()) == AppCMSUIKeyType.PAGE_PHOTO_GALLERY_AUTH_TXT_KEY) {
                        if (moduleAPI.getContentData().get(0).getContentDetails() != null) {
                            StringBuilder authDateAndPhotoCount = new StringBuilder();
                            if (moduleAPI.getContentData().get(0).getContentDetails().getAuthor() != null && moduleAPI.getContentData().get(0).getContentDetails().getAuthor().getName() != null) {
                                authDateAndPhotoCount.append(moduleAPI.getContentData().get(0).getContentDetails().getAuthor().getName());
                            }
                            if (moduleAPI.getContentData().get(0).getGist().getPublishDate() != null) {
                                authDateAndPhotoCount.append(" | ")
                                        .append(AppCMSPresenter.getDateFormat(Long.parseLong(moduleAPI.getContentData().get(0).getGist().getPublishDate()), "MMM dd"));
                            }
                            if (moduleAPI.getContentData().get(0).getStreamingInfo() != null && moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets() != null) {
                                authDateAndPhotoCount.append(" | ")
                                        .append(moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets().size() + " Photos");
                            }

                            ((TextView) componentViewResult.componentView).setText("By " + authDateAndPhotoCount.toString());
//                        ((TextView) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandSecondaryCtaTextColor());
                            ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor("#80000000"));
                        }
                    }
                    if (componentKey == AppCMSUIKeyType.PAGE_STATE_LABEL_TXT_KEY || componentKey == AppCMSUIKeyType.PAGE_TABEL_LABEL_HEADER_TXT_KEY) {
                        int textFontColor = 0;
                        if (!TextUtils.isEmpty(component.getTextColor())) {
                            textFontColor = Color.parseColor(getColor(context, component.getTextColor()));
                        }
                        ((TextView) componentViewResult.componentView).setTextColor(textFontColor);
                        ((TextView) componentViewResult.componentView).setText(component.getText());
                    }
                    if (componentKey == AppCMSUIKeyType.PAGE_ROSTER_TITLE_TXT_KEY) {
                        int textFontColor = 0;
                        if (!TextUtils.isEmpty(component.getTextColor())) {
                            textFontColor = Color.parseColor(getColor(context, "#000000"));
                        }
                        ((TextView) componentViewResult.componentView).setTextColor(textFontColor);
                        ((TextView) componentViewResult.componentView).setText("Title");
                    }
                    if ((moduleType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY)) {
                        int textFontColor = 0;
                        if (!TextUtils.isEmpty(component.getTextColor())) {
                            textFontColor = Color.parseColor(getColor(context, "#000000"));
                        }
                        ((TextView) componentViewResult.componentView).setTextColor(textFontColor);
                        ((TextView) componentViewResult.componentView).setText("tray Title");
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
                                } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_HISTORY_01_MODULE_KEY ||
                                        jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_HISTORY_02_MODULE_KEY) {
                                    ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_history_title);
                                } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_WATCHLIST_01_MODULE_KEY ||
                                        jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_WATCHLIST_02_MODULE_KEY) {
                                    ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_watchlist_title);
                                } /*else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY) {
                                    ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_download_title);
                                }*/ else if (moduleAPI != null &&
                                        moduleAPI.getContentData() != null &&
                                        moduleAPI.getContentData().size() > 0 &&
                                        moduleAPI.getContentData().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getGist() != null &&
                                        moduleAPI.getContentData().get(0).getGist().getTitle() != null) {
                                    ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getTitle());
                                    if (component.getNumberOfLines() != 0) {
                                        ((TextView) componentViewResult.componentView).setSingleLine(false);
                                        ((TextView) componentViewResult.componentView).setMaxLines(component.getNumberOfLines());
                                    }
                                    ((TextView) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());

                                    ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                                    if (moduleAPI.getContentData().get(0).getGist().getContentType() != null &&
                                            moduleAPI.getContentData().get(0).getGist().getContentType().equalsIgnoreCase(context.getString(R.string.content_type_event))) {
                                        ((TextView) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
                                    }
                                }
                                break;

                            case PAGE_API_DESCRIPTION:
                                if (moduleAPI != null && !TextUtils.isEmpty(moduleAPI.getRawText())) {
                                    //Spannable rawHtmlSpannable = htmlSpanner.fromHtml(moduleAPI.getRawText());
                                    String htmlStyleRegex = "<style([\\s\\S]+?)</style>";
                                    ((TextView) componentViewResult.componentView).setText(Html.fromHtml(moduleAPI.getRawText().replaceAll(htmlStyleRegex, "")));
                                    ((TextView) componentViewResult.componentView).setMovementMethod(LinkMovementMethod.getInstance());
                                }
                                break;
                            case PAGE_PHOTO_GALLERY_IMAGE_COUNT_TXT_KEY:

                                if (moduleAPI.getContentData().get(0).getStreamingInfo() != null) {
                                    ((TextView) componentViewResult.componentView).setId(R.id.photo_gallery_image_count);
                                    ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor("#FFFFFF"));
                                    int photoGallerySize = moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets().size();
                                    String position = photoGallerySize == 0 ? "0/0" : 1 + "/" + photoGallerySize;
                                    ((TextView) componentViewResult.componentView).setText(position);
                                    ((TextView) componentViewResult.componentView).setGravity(Gravity.CENTER);
                                }
                                break;

                            case PAGE_TRAY_TITLE_KEY:
                                if (!TextUtils.isEmpty(component.getText())) {
                                    ((TextView) componentViewResult.componentView).setText(component.getText().toUpperCase());
                                } else if (moduleAPI != null && moduleAPI.getSettings() != null && !moduleAPI.getSettings().getHideTitle() &&
                                        !TextUtils.isEmpty(moduleAPI.getTitle())) {
                                    ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle().toUpperCase());
                                } else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_WATCHLIST_01_MODULE_KEY ||
                                        jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_WATCHLIST_02_MODULE_KEY) {
                                    ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_watchlist_title);
                                } /*else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY) {
                                    ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_download_title);
                                } */ else if (jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_HISTORY_01_MODULE_KEY ||
                                        jsonValueKeyMap.get(viewType) == AppCMSUIKeyType.PAGE_HISTORY_02_MODULE_KEY) {
                                    ((TextView) componentViewResult.componentView).setText(R.string.app_cms_page_history_title);
                                } else if (moduleType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {

                                    if (moduleAPI != null &&
                                            moduleAPI.getContentData() != null &&
                                            moduleAPI.getContentData().get(0) != null &&
                                            moduleAPI.getContentData().get(0).getSeason() != null &&
                                            !moduleAPI.getContentData().get(0).getSeason().isEmpty() &&
                                            moduleAPI.getContentData().get(0).getSeason().get(0) != null &&
                                            !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getSeason().get(0).getTitle())) {
                                        ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getSeason().get(0).getTitle());
                                    } else {
                                        StringBuilder seasonTitleSb = new StringBuilder(context.getString(R.string.app_cms_episodic_season_prefix));
                                        seasonTitleSb.append(context.getString(R.string.blank_separator));
                                        seasonTitleSb.append(1);
                                        ((TextView) componentViewResult.componentView).setText(seasonTitleSb.toString());
                                    }
                                } else if (moduleType == AppCMSUIKeyType.PAGE_AC_ROSTER_MODULE_KEY) {

                                    if (moduleAPI != null &&
                                            moduleAPI.getContentData() != null &&
                                            moduleAPI.getContentData().get(0) != null &&
                                            moduleAPI.getContentData().get(0).getTeam() != null &&
                                            moduleAPI.getContentData().get(0).getTeam().getName() != null) {
                                        ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getTeam().getName());
                                        int textFontColor = 0;

                                        if (!TextUtils.isEmpty(component.getTextColor())) {
                                            textFontColor = Color.parseColor(getColor(context, component.getTextColor()));
                                        }
                                        ((TextView) componentViewResult.componentView).setTextColor(textFontColor);
                                    }
                                }
                                break;

                            case PAGE_TRAY_TITLE_UNDERLINE_KEY:
                                if (moduleAPI != null && moduleAPI.getTitle() != null) {
                                    ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle());
                                    ((TextView) componentViewResult.componentView).setPaintFlags(((TextView) componentViewResult.componentView).getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                    ((TextView) componentViewResult.componentView).setTypeface(((TextView) componentViewResult.componentView).getTypeface(), Typeface.ITALIC);
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
                                                    appCMSPresenter.getBrandPrimaryCtaColor(),
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
                                                    appCMSPresenter.getBrandPrimaryCtaColor(),
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

                                if (component.getKey() != null &&
                                        !component.getKey().equals(context.getString(R.string.app_cms_page_show_image_video_key)) &&
                                        !BaseView.isTablet(context)) {
                                    viewCreatorTitleLayoutListener.setSpecifiedMaxWidthRatio(0.7f);
                                }
                                titleTextVto.addOnGlobalLayoutListener(viewCreatorTitleLayoutListener);
                                ((TextView) componentViewResult.componentView).setSingleLine(true);

                                ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                                break;
                            case PAGE_VIDEO_SUBTITLE_KEY:
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                        !moduleAPI.getContentData().isEmpty() &&
                                        moduleAPI.getContentData().get(0) != null) {
                                    if (moduleAPI.getContentData().get(0).getGist() != null &&
                                            !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getContentType()) &&
                                            moduleAPI.getContentData().get(0).getGist().getContentType().equalsIgnoreCase(context.getString(R.string.app_cms_video_content_type)) &&
                                            appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.ENTERTAINMENT) {
                                        setViewWithSubtitle(context, moduleAPI.getContentData().get(0),
                                                componentViewResult.componentView);
                                    } else if (moduleAPI.getContentData().get(0).getSeason() != null) {
                                        setViewWithShowSubtitle(context, moduleAPI.getContentData().get(0),
                                                componentViewResult.componentView, false);
                                    }
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
                                if (moduleAPI != null &&
                                        moduleAPI.getContentData() != null &&
                                        !moduleAPI.getContentData().isEmpty() &&
                                        moduleAPI.getContentData().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getGist() != null &&
                                        moduleAPI.getContentData().get(0).getGist().getPublishDate() != null) {
                                    long publishDateMillseconds = Long.parseLong(moduleAPI.getContentData().get(0).getGist().getPublishDate());
                                    long publishTimeMs = moduleAPI.getContentData().get(0).getGist().getRuntime();

                                    long runtime = moduleAPI.getContentData().get(0).getGist().getRuntime();
                                    String secondsToTime = AppCMSPresenter.convertSecondsToTime(runtime);

                                    StringBuilder builder = new StringBuilder(secondsToTime);

                                    if (moduleAPI.getContentData().get(0).getGist().getPublishDate() != null) {
                                        publishDateMillseconds = Long.parseLong(moduleAPI.getContentData().get(0).getGist().getPublishDate());
                                        String publishDate = context.getResources().getString(R.string.published_on) + " " + AppCMSPresenter.getDateFormat(publishDateMillseconds, "MMM dd, yyyy");
                                        builder.append(" | ");
                                        builder.append(publishDate);
                                    }

                                    ((TextView) componentViewResult.componentView).setText(builder);

                                }

                                break;

                            case PAGE_GAME_DATE_KEY:
                                if (moduleAPI != null &&
                                        moduleAPI.getContentData() != null &&
                                        !moduleAPI.getContentData().isEmpty() &&
                                        moduleAPI.getContentData().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getGist() != null &&
                                        moduleAPI.getContentData().get(0).getGist().getEventSchedule() != null) {
                                    long date = moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0).getEventTime();
                                    String eventdate = AppCMSPresenter.getDateFormatByTimeZone(date * 1000L, "MM/dd", "UTC");
                                    StringBuilder builder = new StringBuilder(eventdate);
                                    builder.append(" - ");
                                    builder.append(AppCMSPresenter.getDateFormatByTimeZone(date * 1000L, "hh:mm aa", "UTC"));
                                    builder.append(" ");
                                    builder.append(moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0).getEventTimeZone());
                                    ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                                    ((TextView) componentViewResult.componentView).setText(builder);
                                    ((TextView) componentViewResult.componentView).setSingleLine(true);

                                    ((TextView) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
                                }

                                break;

                            case PAGE_VENUE_LABEL_KEY:
                                if (moduleAPI != null &&
                                        moduleAPI.getContentData() != null &&
                                        !moduleAPI.getContentData().isEmpty() &&
                                        moduleAPI.getContentData().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getGist() != null &&
                                        moduleAPI.getContentData().get(0).getGist().getEventSchedule() != null &&
                                        moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0).getVenue() != null) {
                                    ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0)
                                            .getGist().getEventSchedule().get(0).getVenue().toString());
                                    ((TextView) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
                                }

                                break;

                            case PAGE_TIMER_TITLE_KEY:
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                        moduleAPI.getContentData().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getGist() != null &&
                                        moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getGist().getEventSchedule().get(0).getIsLiveEvent() != null) {

                                    ((TextView) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandPrimaryCtaTextColor());
                                    ((TextView) componentViewResult.componentView).setText(context.getResources().getString(R.string.timer_until_face_off));
                                    ((TextView) componentViewResult.componentView).setId(R.id.timer_until_face_off);
                                    ((TextView) componentViewResult.componentView).setShadowLayer(2, 1, 1, android.R.color.black);
                                }
                                ((TextView) componentViewResult.componentView).setVisibility(View.GONE);

                                break;


                            case PAGE_VIDEO_AGE_LABEL_KEY:
                                if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                        !moduleAPI.getContentData().isEmpty() &&
                                        moduleAPI.getContentData().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getGist() != null &&
                                        !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getParentalRating())) {
                                    String parentalRating = moduleAPI.getContentData().get(0).getParentalRating();
                                    ((TextView) componentViewResult.componentView).setText(parentalRating);
                                    ((TextView) componentViewResult.componentView).setSingleLine(true);
                                    componentViewResult.componentView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
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
                                ((TextView) componentViewResult.componentView).setSingleLine();
                                ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
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
                                    } else if (paymentProcessor.equalsIgnoreCase(context.getString(R.string.subscription_sslcommerz_payment_processor))) {
                                        ((TextView) componentViewResult.componentView).setText(context.getString(R.string.subscription_sslcommerz_payment_processor_friendly));
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

                            case PAGE_PHOTOGALLERY_PREV_GALLERY_LABEL_KEY:
                                if (!TextUtils.isEmpty(component.getText())) {
                                    ((TextView) componentViewResult.componentView).setText(component.getText());
                                }
                                componentViewResult.componentView.setId(R.id.photo_gallery_prev_label);
                                ((TextView) componentViewResult.componentView).setCompoundDrawablesWithIntrinsicBounds(R.drawable.left_arrow_black, 0, 0, 0);

                                if (appCMSPresenter.getCurrentPhotoGalleryIndex() == 0) {
                                    ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor("#c8c8c8"));
                                    ((TextView) componentViewResult.componentView).setCompoundDrawablesWithIntrinsicBounds(R.drawable.left_arrow_disable, 0, 0, 0);
                                } else {
                                    ((TextView) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandPrimaryCtaColor());
                                    ((TextView) componentViewResult.componentView).setCompoundDrawablesWithIntrinsicBounds(R.drawable.left_arrow_black, 0, 0, 0);
                                }
                                componentViewResult.componentView.setOnClickListener(v -> {
                                    int currentIndex = appCMSPresenter.getCurrentPhotoGalleryIndex();
                                    if (currentIndex == 0) {
                                        return;
                                    }
                                    if (appCMSPresenter.getRelatedPhotoGalleryIds() != null) {
                                        currentIndex--;
                                        appCMSPresenter.setCurrentPhotoGalleryIndex(currentIndex);
                                        appCMSPresenter.navigateToPhotoGalleryPage(appCMSPresenter.getRelatedPhotoGalleryIds().get(currentIndex),
                                                null, null, false);

                                    }
                                });
                                break;

                            case PAGE_PHOTOGALLERY_NEXT_GALLERY_LABEL_KEY:
                                if (!TextUtils.isEmpty(component.getText())) {
                                    ((TextView) componentViewResult.componentView).setText(component.getText());
                                }
                                componentViewResult.componentView.setId(R.id.photo_gallery_next_label);
                                ((TextView) componentViewResult.componentView).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_arrow, 0);

                                if (appCMSPresenter.getRelatedPhotoGalleryIds() != null) {
                                    if (appCMSPresenter.getCurrentPhotoGalleryIndex() == appCMSPresenter.getRelatedPhotoGalleryIds().size() - 1) {
                                        ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor("#c8c8c8"));
                                        ((TextView) componentViewResult.componentView).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_arrow_disable, 0);
                                        ((TextView) componentViewResult.componentView).setEnabled(false);
                                    } else {
                                        ((TextView) componentViewResult.componentView).setTextColor(appCMSPresenter.getBrandPrimaryCtaColor());
                                        ((TextView) componentViewResult.componentView).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_arrow, 0);
                                    }
                                    componentViewResult.componentView.setOnClickListener(v -> {
                                        int currentIndex = appCMSPresenter.getCurrentPhotoGalleryIndex();
                                        if (appCMSPresenter.getRelatedPhotoGalleryIds() != null &&
                                                currentIndex < appCMSPresenter.getRelatedPhotoGalleryIds().size()) {
                                            currentIndex = currentIndex + 1;
                                            appCMSPresenter.setCurrentPhotoGalleryIndex(currentIndex);
                                            appCMSPresenter.navigateToPhotoGalleryPage(appCMSPresenter.getRelatedPhotoGalleryIds().get(currentIndex),
                                                    null, null, false);
                                        }

                                    });
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

                    case PAGE_PHOTO_GALLERY_SELECTED_IMAGE:

                        String selectedImgUrl = "";
                        ImageView selectedImg = (ImageView) componentViewResult.componentView;
                        selectedImg.setId(R.id.photo_gallery_selectedImage);
                        selectedImg.setScaleType(ImageView.ScaleType.FIT_XY);
                        if (moduleAPI.getContentData().get(0).getStreamingInfo() != null && moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets() != null) {
                            if (moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets().size() > 0 && moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets().get(0).getUrl() != null) {
                                selectedImgUrl = moduleAPI.getContentData().get(0).getStreamingInfo().getPhotogalleryAssets().get(0).getSecureUrl();
                            }
                        }
                        Glide.with(selectedImg.getContext()).load(selectedImgUrl).into(selectedImg);
                        break;

                    case PAGE_PHOTO_PLAYER_IMAGE:

                        String playerImgUrl = "";
                        ImageView playerImageView = (ImageView) componentViewResult.componentView;
                        playerImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        if (moduleAPI.getContentData() != null && moduleAPI.getContentData().get(0).getGist() != null &&
                                moduleAPI.getContentData().get(0).getGist().getImageGist() != null) {
                            if (moduleAPI.getContentData().get(0).getGist().getImageGist().get_3x4() != null) {
                                playerImgUrl = moduleAPI.getContentData().get(0).getGist().getImageGist().get_3x4();
                            }
                        }
                        Glide.with(playerImageView.getContext()).load(playerImgUrl).into(playerImageView);
                        break;
                    case PAGE_AUTOPLAY_MOVIE_IMAGE_KEY:
                        if (moduleAPI != null && moduleAPI.getContentData() != null &&
                                !moduleAPI.getContentData().isEmpty() &&
                                moduleAPI.getContentData().get(0) != null &&
                                moduleAPI.getContentData().get(0).getGist() != null &&
                                !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())) {
                            int viewWidth = (int) BaseView.getViewWidth(context,
                                    component.getLayout(),
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            int viewHeight = (int) BaseView.getViewHeight(context,
                                    component.getLayout(),
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            String imageUrl = null;
                            if (!TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())) {
                                imageUrl = moduleAPI.getContentData().get(0).getGist().getVideoImageUrl();
                            } else if (moduleAPI.getContentData().get(0).getGist().getImageGist() != null &&
                                    !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getImageGist().get_16x9())) {
                                imageUrl = moduleAPI.getContentData().get(0).getGist().getImageGist().get_16x9();
                            }
                            if (!TextUtils.isEmpty(imageUrl)) {
                                if (viewHeight > 0 && viewWidth > 0 && viewHeight > viewWidth) {
                                    if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView,
                                            imageUrl,
                                            ImageLoader.ScaleType.START)) {
                                        Glide.with(context)
                                                .load(imageUrl)
                                                .apply(new RequestOptions().override(viewWidth, viewHeight))
                                                .into((ImageView) componentViewResult.componentView);
                                    }
                                } else if (viewWidth > 0) {
                                    if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView,
                                            imageUrl,
                                            ImageLoader.ScaleType.START)) {
                                        Glide.with(context)
                                                .load(imageUrl)
                                                .apply(new RequestOptions().override(viewWidth, viewHeight).centerCrop())
                                                .into((ImageView) componentViewResult.componentView);
                                    }
                                } else {
                                    if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView,
                                            imageUrl,
                                            ImageLoader.ScaleType.START)) {
                                        Glide.with(context)
                                                .load(imageUrl)
                                                .into((ImageView) componentViewResult.componentView);
                                    }
                                }
                            }
                            componentViewResult.componentView.setBackgroundColor(ContextCompat.getColor(context,
                                    android.R.color.transparent));
                            componentViewResult.useWidthOfScreen = false;
                        } else {
                            ((ImageView) componentViewResult.componentView).setImageResource(R.drawable.vid_image_placeholder_16x9);
                        }
                        break;

                    case PAGE_BADGE_IMAGE_KEY:
                        //
                        break;

                    case PAGE_BANNER_IMAGE:
                        ImageView imageView1 = (ImageView) componentViewResult.componentView;
                        imageView1.setImageResource(R.drawable.logo);
                        break;

                    case PAGE_THUMBNAIL_BADGE_IMAGE:
                        componentViewResult.componentView.setVisibility(View.GONE);
                        // TODO: 03 Nov. 2017 - Badges are not yet ready for Production - This should be uncommented once that is available
//                        componentViewResult.componentView = new ImageView(context);
//                        ImageView imageView = (ImageView) componentViewResult.componentView;
//                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                        String iconImageUrl;
//                        if (component.getIcon_url() != null && !TextUtils.isEmpty(component.getIcon_url())) {
//                            iconImageUrl = component.getIcon_url();
//                            Glide.with(context)
//                                    .load(iconImageUrl)
//                                    .into(imageView);
//                        } else if (context.getDrawable(R.drawable.pro_badge_con) != null) {
//                            componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.pro_badge_con));
//                        }
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
                                        !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl()) ||
                                        !TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getLandscapeImageUrl()))) {
                            int viewWidth = (int) BaseView.getViewWidth(context,
                                    component.getLayout(),
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            int viewHeight = (int) BaseView.getViewHeight(context,
                                    component.getLayout(),
                                    ViewGroup.LayoutParams.WRAP_CONTENT);

                            int placeHolderImage = R.drawable.vid_image_placeholder_land;/*BaseView.isLandscape(context) ? R.drawable.vid_image_placeholder_land : R.drawable.vid_image_placeholder_port;*/
                            ((ImageView) componentViewResult.componentView).setImageResource(placeHolderImage);
                            ((ImageView) componentViewResult.componentView).setScaleType(ImageView.ScaleType.FIT_XY);
                            if (viewHeight > 0 && viewWidth > 0 && viewHeight > viewWidth) {
                                String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                        moduleAPI.getContentData().get(0).getGist().getLandscapeImageUrl(),
                                        viewWidth,
                                        viewHeight);
                                if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView,
                                        imageUrl,
                                        ImageLoader.ScaleType.CENTER)) {
                                    Glide.with(context)
                                            .load(imageUrl)
                                            .apply(new RequestOptions().override(viewWidth, viewHeight)
                                                    .placeholder(placeHolderImage))
                                            .into((ImageView) componentViewResult.componentView);
                                }
                            } else if (viewWidth > 0) {
                                String videoImageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                        moduleAPI.getContentData().get(0).getGist().getVideoImageUrl() == null ?
                                                moduleAPI.getContentData().get(0).getGist().getLandscapeImageUrl() :
                                                moduleAPI.getContentData().get(0).getGist().getVideoImageUrl(),
                                        viewWidth,
                                        viewHeight);
                                if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView,
                                        videoImageUrl,
                                        ImageLoader.ScaleType.CENTER)) {
                                    Glide.with(context)
                                            .load(videoImageUrl)
                                            .apply(new RequestOptions().override(viewWidth, viewHeight)
                                                    .placeholder(placeHolderImage))
                                            .into((ImageView) componentViewResult.componentView);
                                }
                            } else {
                                if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView,
                                        moduleAPI.getContentData().get(0).getGist().getVideoImageUrl(),
                                        ImageLoader.ScaleType.CENTER)) {
                                    Glide.with(context)
                                            .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                                            .apply(new RequestOptions().fitCenter().placeholder(placeHolderImage))
                                            .into((ImageView) componentViewResult.componentView);
                                }
                            }

                            componentViewResult.useWidthOfScreen = !BaseView.isLandscape(context);
                        }

                        break;

//                    case PAGE_VIDEO_DETAIL_PLAYER:
//                    videoUrl = null;
//
//                    if (moduleAPI != null &&
//                            moduleAPI.getContentData() != null &&
//                            !moduleAPI.getContentData().isEmpty() &&
//                            moduleAPI.getContentData().get(0) != null &&
//                            moduleAPI.getContentData().get(0).getStreamingInfo() != null &&
//                            moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets() != null) {
//                        VideoAssets videoAssets = moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets();
//                        videoUrl = videoAssets.getHls();
//
//                        if (videoAssets.getMpeg() != null && !videoAssets.getMpeg().isEmpty()) {
//                            if (videoAssets.getMpeg().get(0) != null) {
//                                videoUrl = videoAssets.getMpeg().get(0).getUrl();
//                            }
//
//                            for (int i = 0; i < videoAssets.getMpeg().size() && TextUtils.isEmpty(videoUrl); i++) {
//                                if (videoAssets.getMpeg().get(i) != null &&
//                                        videoAssets.getMpeg().get(i).getRenditionValue() != null) {
//                                    videoUrl = videoAssets.getMpeg().get(i).getUrl();
//                                }
//                            }
//                        }
//
//                        if (moduleAPI.getContentData() != null &&
//                                !moduleAPI.getContentData().isEmpty() &&
//                                moduleAPI.getContentData().get(0) != null &&
//                                moduleAPI.getContentData().get(0).getContentDetails() != null) {
//
//                            List<String> relatedVideoIds = null;
//                            if (moduleAPI.getContentData().get(0).getContentDetails() != null &&
//                                    moduleAPI.getContentData().get(0).getContentDetails().getRelatedVideoIds() != null) {
//                                relatedVideoIds = moduleAPI.getContentData().get(0).getContentDetails().getRelatedVideoIds();
//                            }
//                            int currentPlayingIndex = -1;
//                            if (relatedVideoIds == null) {
//                                currentPlayingIndex = 0;
//                            }
//                        }
//                    }
//
//                    componentViewResult.componentView = playerView(context, videoUrl,
//                            moduleAPI.getContentData().get(0).getGist().getId());
//
//                    videoPlayerView.setPageView(pageView);
//                    appCMSPresenter.unrestrictPortraitOnly();
//                    componentViewResult.componentView.setId(R.id.video_player_id);
//                    break;

                    default:
                        if (!TextUtils.isEmpty(component.getImageName())) {
                            if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView,
                                    component.getImageName(),
                                    ImageLoader.ScaleType.CENTER)) {
                                Glide.with(context)
                                        .load(component.getImageName())
                                        .into((ImageView) componentViewResult.componentView);
                            }
                        }

                        if (componentKey == AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY) {
                            ((ImageView) componentViewResult.componentView).setScaleType(ImageView.ScaleType.FIT_CENTER);
                        } else {
                            ((ImageView) componentViewResult.componentView).setScaleType(ImageView.ScaleType.FIT_CENTER);
                        }
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
                int color = ContextCompat.getColor(context, R.color.colorAccent);

                if (!TextUtils.isEmpty(appCMSPresenter.getAppCtaBackgroundColor())) {
                    color = Color.parseColor(getColor(context, appCMSPresenter.getAppCtaBackgroundColor()));
                }

                final int progressColor = color;
                componentViewResult.componentView = new ProgressBar(context,
                        null,
                        android.R.attr.progressBarStyleHorizontal) {
                    Paint paint = new Paint();

                    @Override
                    public void onDraw(Canvas canvas) {
                        super.onDraw(canvas);
                        int count = canvas.save();
                        int toX = (int) ((float) canvas.getWidth() * ((float) getProgress() / 100.0f));

                        paint.setColor(progressColor);
                        canvas.drawRect(0, 0, toX, canvas.getHeight(), paint);
//                        canvas.clipRect(0, 0, toX, canvas.getHeight());
                        getBackground().draw(canvas);
                        canvas.restoreToCount(count);
                    }
                };

                ((ProgressBar) componentViewResult.componentView).getProgressDrawable()
                        .setColorFilter(0x00000000, PorterDuff.Mode.SRC_IN);
                componentViewResult.componentView.setBackgroundColor(color & 0x44ffffff);

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
                if (component.getBackgroundColor() != null && !TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.
                            setBackgroundColor(Color.parseColor(getColor(context,
                                    component.getBackgroundColor())));
                }
                break;

            case PAGE_SEPARATOR_VIEW_KEY:
            case PAGE_SEGMENTED_VIEW_KEY:
                componentViewResult.componentView = new View(context);
//                if (moduleType == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY &&
//                        moduleAPI != null && moduleAPI.getContentData() != null &&
//                        !moduleAPI.getContentData().isEmpty() &&
//                        moduleAPI.getContentData().get(0) != null &&
//                        moduleAPI.getContentData().get(0).getSeason() != null &&
//                        moduleAPI.getContentData().get(0).getSeason().size() == 0) {
//                    componentViewResult.componentView.setVisibility(View.GONE);
//                    componentViewResult.shouldHideComponent = true;
//                }
                if (component.getBackgroundColor() != null && !TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.
                            setBackgroundColor(Color.parseColor(getColor(context,
                                    component.getBackgroundColor())));
                } else if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                        .getTextColor())) {
                    componentViewResult.componentView.
                            setBackgroundColor(appCMSPresenter.getGeneralTextColor());
                }
                if (moduleType != AppCMSUIKeyType.PAGE_EVENT_DETAIL_MODULE_KEY && moduleType != AppCMSUIKeyType.PAGE_AC_TEAM_SCHEDULE_MODULE_KEY) {

                    componentViewResult.componentView.setAlpha(0.6f);

                }
                break;

            case PAGE_MULTICOLUMN_TABLE_KEY:
                componentViewResult.componentView = new LinearLayout(context);
//                View TableView = setFightSummaryModule(context, moduleAPI, component, jsonValueKeyMap, fights);
//                ((LinearLayout) componentViewResult.componentView).addView(TableView);
//                ((LinearLayout) componentViewResult.componentView ).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//                Fights fights = moduleAPI.getContentData().get(appCMSPresenter.getSelectedFightId()).getFights();
                ((LinearLayout) componentViewResult.componentView).setId(R.id.fight_stats_id);

//                createFightStateRecorsView(context, appCMSPresenter, moduleAPI, component, jsonValueKeyMap, fights);

                break;

            case PAGE_CASTVIEW_VIEW_KEY:
                if (appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.ENTERTAINMENT) {
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
                            appCMSPresenter.getBrandPrimaryCtaColor(),
                            BaseView.getFontSizeKey(context, component.getLayout()),
                            BaseView.getFontSizeValue(context, component.getLayout()));

                    if (moduleAPI != null && !BaseView.isTablet(context)
                            && moduleAPI.getModuleType() != null
                            && (jsonValueKeyMap.get(moduleAPI.getModuleType())
                            == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY_01 ||
                            jsonValueKeyMap.get(moduleAPI.getModuleType())
                                    == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY_02 ||
                            jsonValueKeyMap.get(moduleAPI.getModuleType())
                                    == AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY_03 ||
                            jsonValueKeyMap.get(moduleAPI.getModuleType())
                                    == AppCMSUIKeyType.PAGE_AUTOPLAY_LANDSCAPE_MODULE_KEY
                    )) {
                        componentViewResult.componentView.setVisibility(View.GONE);
                    }
                }
                break;

            case PAGE_TEXTFIELD_KEY:
                componentViewResult.componentView = new TextInputLayout(context);
                TextInputEditText textInputEditText = new TextInputEditText(context);
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
                    case PAGE_SUBSCRIBE_EMAIL_KEY:
                        textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        textInputEditText.setId(R.id.subscribe_edit_text_id);
                        RecyclerView view = appCMSPresenter.getCurrentActivity().findViewById(R.id.home_nested_scroll_view);
                        if (view != null) {
                            view.setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
                        } else {
                            textInputEditText.requestFocus();
                        }
                        textInputEditText.setHintTextColor(ContextCompat.getColor(context, android.R.color.white));
                        textInputEditText.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                        textInputEditText.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black));

                        if (BaseView.isTablet(context)) {
                            textInputEditTextLayoutParams.setMargins(0, 0, 100, 0);
                        }
                        break;
                    default:
                        break;
                }

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
                ((Switch) componentViewResult.componentView).getTrackDrawable().setTint(
                        appCMSPresenter.getBrandPrimaryCtaTextColor());
                int switchOnColor = appCMSPresenter.getBrandPrimaryCtaColor();
                int switchOffColor = appCMSPresenter.getBrandPrimaryCtaTextColor();
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

                if (componentKey == AppCMSUIKeyType.PAGE_DOWNLOAD_VIA_CELLULAR_NETWORK_KEY) {
                    if (appCMSPresenter.getAppCMSMain().getFeatures() != null &&
                            appCMSPresenter.getAppCMSMain().getFeatures().isMobileAppDownloads()) {
                        ((Switch) componentViewResult.componentView)
                                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                                    if (isChecked) {
                                        appCMSPresenter.setDownloadOverCellularEnabled(true);
                                    } else {
                                        appCMSPresenter.setDownloadOverCellularEnabled(false);
                                    }
                                });

                        ((Switch) componentViewResult.componentView).setChecked(appCMSPresenter
                                .getDownloadOverCellularEnabled());
                        componentViewResult.componentView.setEnabled(true);
                        ((Switch) componentViewResult.componentView).setChecked(appCMSPresenter.getDownloadOverCellularEnabled());
                    }
                }


                break;

            default:
                if (moduleAPI != null && component.getComponents() != null &&
                        pageView != null &&
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

    /**
     * This will match a module from a UI JSON response to a module in a API data JSON response.
     *
     * @param module          Module from a UI JSON response
     * @param appCMSPageAPI   Module from an API data JSON response
     * @param jsonValueKeyMap This is a hashmap that associates UI string values with value enumerations
     * @return A matching module UI JSON response to the input module API data JSON response
     */
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
                } else if (jsonValueKeyMap.get(module.getType()) != null &&
                        jsonValueKeyMap.get(moduleAPI.getModuleType()) != null &&
                        module.getType().equalsIgnoreCase("AC AutoPlayLandscape 01")) {

                    return moduleAPI;
                }
            }

            if (jsonValueKeyMap.get(module.getView()) != null) {
                switch (jsonValueKeyMap.get(module.getView())) {
                    case PAGE_ARTICLE_MODULE_KEY:
                    case PAGE_PHOTO_TRAY_MODULE_KEY:
                    case PAGE_HISTORY_01_MODULE_KEY:
                    case PAGE_HISTORY_02_MODULE_KEY:
                    case PAGE_PLAYLIST_MODULE_KEY:
                    case PAGE_WATCHLIST_01_MODULE_KEY:
                    case PAGE_WATCHLIST_02_MODULE_KEY:
                    case PAGE_AUTOPLAY_MODULE_KEY_01:
                    case PAGE_AUTOPLAY_MODULE_KEY_02:
                    case PAGE_AUTOPLAY_MODULE_KEY_03:
                    case PAGE_AUTOPLAY_LANDSCAPE_MODULE_KEY:
                    case PAGE_DOWNLOAD_SETTING_MODULE_KEY:
                    case PAGE_DOWNLOAD_01_MODULE_KEY:
                    case PAGE_DOWNLOAD_02_MODULE_KEY:
                    case PAGE_AUTHENTICATION_MODULE_KEY:
                    case PAGE_AC_TEAM_SCHEDULE_MODULE_KEY:
                    case PAGE_API_TEAMDETAIL_MODULE_KEY:
                    case PAGE_GAME_DETAIL_MODULE_KEY:
                    case PAGE_PERSON_DETAIL_MODULE_KEY:
                    case PAGE_AC_ROSTER_MODULE_KEY:

                        if (appCMSPageAPI.getModules() != null
                                && !appCMSPageAPI.getModules().isEmpty()) {
                            return appCMSPageAPI.getModules().get(0);
                        }
                        break;
                    case PAGE_FIGHT_SUMMARY_MODULE_KEY:
                        if (appCMSPageAPI.getModules() != null
                                && !appCMSPageAPI.getModules().isEmpty()) {
                            return appCMSPageAPI.getModules().get(appCMSPageAPI.getModules().size() - 1);
                        }
                        break;
                    case PAGE_EVENT_DETAIL_MODULE_KEY:


                        if (appCMSPageAPI.getModules() != null
                                && !appCMSPageAPI.getModules().isEmpty()) {
                            return appCMSPageAPI.getModules().get(1);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return null;
    }


    public View setFightSummaryModule(Context context, Module moduleAPI, Component component, Map<String, AppCMSUIKeyType> jsonValueKeyMap, Fights fights, AppCMSPresenter appCMSPresenter) {


        boolean isHeader = false;
//        LinearLayout ll = new LinearLayout(context);

        HorizontalScrollView scrollView = new HorizontalScrollView(context);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
        LinearLayout tableParentLayout = new LinearLayout(context);
        tableParentLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        tableParentLayout.setOrientation(LinearLayout.VERTICAL);
        NestedScrollView nestedSCrollView = new NestedScrollView(context);
        nestedSCrollView.setLayoutParams(new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));


        TableLayout table = new TableLayout(context);
        table.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        TableLayout tableHeader = new TableLayout(context);
        tableHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        tableHeader.setGravity(Gravity.CENTER);
        RecyclerView homeRec = (RecyclerView) appCMSPresenter.getCurrentActivity().findViewById(R.id.home_nested_scroll_view);
        table.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    homeRec.requestDisallowInterceptTouchEvent(false);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    homeRec.requestDisallowInterceptTouchEvent(true);

                }
                return false;
            }
        });
        if (fights != null && fights.getRounds().size() > 0) {
            for (int i = -1; i < fights.getRounds().size(); i++) {
                TableRow row = new TableRow(context);
                row.setWeightSum(2.0f);
                row.setPadding(1, 1, 1, 1);

                TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f);

                for (EventDetailsColumnsName colTitle : EventDetailsColumnsName.values()) {
                    String cellValue = "";
                    if (i == -1) {
                        cellValue = colTitle.toString();
                        isHeader = true;
                    } else {
                        isHeader = false;

                        Rounds rounds = fights.getRounds().get(i);
                        if (colTitle.toString().equalsIgnoreCase(EventDetailsColumnsName.ROUND.toString())) {

                            if (fights != null) {
                                if (fights.getRounds().get(i).getFighterId().equalsIgnoreCase(fights.getFighter1_Id())) {
                                    cellValue = rounds.getRound();
                                } else {
                                    cellValue = "";
                                }
                            }

                        } else if (colTitle.toString().equalsIgnoreCase(EventDetailsColumnsName.TIME.toString())) {
                            if (fights.getRounds().get(i).getFighterId().equalsIgnoreCase(fights.getFighter1_Id())) {
                                cellValue = rounds.getRoundTime();
                            } else {
                                cellValue = "";
                            }
                        } else if (colTitle.toString().equalsIgnoreCase(EventDetailsColumnsName.FIGHTER.toString())) {
                            try {

                                if (fights != null) {
                                    if (fights.getRounds().get(i).getFighterId().equalsIgnoreCase(fights.getFighter1_Id())) {
                                        cellValue = fights.getFighter1_LastName();
                                    } else {
                                        cellValue = fights.getFighter2_LastName();
                                    }
                                }
                            } catch (Exception e) {
                                cellValue = " ";
                                Log.e(i + "-" + colTitle.toString(), e.toString());
                            }
                        } else if (colTitle.toString().equalsIgnoreCase(EventDetailsColumnsName.AS.toString())) {
                            try {
                                int as = Integer.parseInt(rounds.getPowerArmStrikesLanded()) + Integer.parseInt(rounds.getNonPowerArmStrikesLanded());
                                cellValue = String.valueOf(as);
                            } catch (Exception e) {
                                cellValue = "0";
                                Log.e(i + "-" + colTitle.toString(), e.toString());
                            }
                        } else if (colTitle.toString().equalsIgnoreCase(EventDetailsColumnsName.AS1.toString())) {
                            try {
                                int as = Integer.parseInt(rounds.getPowerArmStrikesLanded()) + Integer.parseInt(rounds.getNonPowerArmStrikesLanded());
                                int as1 = as * 100 / (Integer.parseInt(rounds.getTotalArmStrikesThrown()));
                                cellValue = String.valueOf(as1);
                            } catch (Exception e) {
                                cellValue = "0";
                                Log.e(i + "-" + colTitle.toString(), e.toString());
                            }
                        } else if (colTitle.toString().equalsIgnoreCase(EventDetailsColumnsName.LS.toString())) {
                            int ls = Integer.parseInt(rounds.getNonPowerLegStrikesLanded()) + Integer.parseInt(rounds.getPowerLegStrikesLanded());
                            cellValue = String.valueOf(ls);

                        } else if (colTitle.toString().equalsIgnoreCase(EventDetailsColumnsName.LS1.toString())) {
                            try {
                                int ls = Integer.parseInt(rounds.getNonPowerLegStrikesLanded()) + Integer.parseInt(rounds.getPowerLegStrikesLanded());

                                int ls1 = ls * 100 / Integer.parseInt(rounds.getTotalLegStrikesThrown());

                                cellValue = String.valueOf(ls1);
                            } catch (Exception e) {
                                cellValue = "0";

                                Log.e(i + "-" + colTitle.toString(), e.toString());
                            }
                        } else if (colTitle.toString().equalsIgnoreCase(EventDetailsColumnsName.GS.toString())) {
                            int gs = Integer.parseInt(rounds.getPowerGroundStrikesLanded()) + Integer.parseInt(rounds.getNonPowerGroundStrikesLanded());
                            cellValue = String.valueOf(gs);
                        } else if (colTitle.toString().equalsIgnoreCase(EventDetailsColumnsName.GS1.toString())) {
                            try {
                                int gs = Integer.parseInt(rounds.getPowerGroundStrikesLanded()) + Integer.parseInt(rounds.getNonPowerGroundStrikesLanded());
                                int gs1 = gs * 100 / Integer.parseInt(rounds.getGroundStrikesThrown());

                                cellValue = String.valueOf(gs1);
                            } catch (Exception e) {
                                cellValue = "0";
                                Log.e(i + "-" + colTitle.toString(), e.toString());
                            }
                        } /*else if (colTitle.toString().equalsIgnoreCase(EventDetailsColumnsName.PLS.toString())) {

                            cellValue = String.valueOf("Unknown");
                        }*/
                    }
                    addTableRowCell(context, cellValue, row, isHeader, component, appCMSPresenter, jsonValueKeyMap);
                }
                row.setLayoutParams(params);


                //if row is for header then keep it outside nested scrollview
//                if (i == -1) {
//                    tableHeader.addView(row, params);
//                    View seperatorView = new View(context);
//                    seperatorView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
//                    seperatorView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
//                    tableHeader.setStretchAllColumns(true);
//
//                    tableHeader.addView(seperatorView);
//                    tableParentLayout.addView(tableHeader);
//                } else
                {
                    table.addView(row, params);
                    table.setGravity(Gravity.CENTER);
                    View seperatorView = new View(context);
                    seperatorView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
                    seperatorView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                    table.addView(seperatorView);
                }

            }
        } else {
            TextView cell = new TextView(context);
            cell.setTextSize(15);

            cell.setTextColor(ContextCompat.getColor(context, R.color.color_grey));
            cell.setGravity(Gravity.CENTER);
            cell.setText(context.getResources().getString(R.string.no_fight_records));
            tableParentLayout.addView(cell);
            tableParentLayout.setGravity(Gravity.CENTER);
            return tableParentLayout;

        }
        table.setStretchAllColumns(true);
        nestedSCrollView.addView(table);
//        tableParentLayout.addView(nestedSCrollView);
        nestedSCrollView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

//        scrollView.setBackgroundColor(Color.parseColor(getColor(context, component.getBackgroundColor())));
        scrollView.addView(nestedSCrollView);
//        nestedSCrollViewData.addView(nestedSCrollView);
        return scrollView;
    }

    /**
     * This applies a visual border around an input view.
     *
     * @param context     This is the context value that created UI components should use
     * @param view        This is the view to apply the border
     * @param component   This is the component object from the UI JSON response associated with the input view
     * @param forcedColor If this value does not equal -1 than this color will be applied to the border instead of the color specified in the component
     */
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

    private void setCasting(boolean allowFreePlay,
                            AppCMSPresenter appCMSPresenter,
                            ImageButton mMediaRouteButton,
                            long watchedTime) {
        try {
            CastServiceProvider castProvider = CastServiceProvider.getInstance(appCMSPresenter.getCurrentActivity());
            castProvider.setAllowFreePlay(allowFreePlay);

            CastServiceProvider.ILaunchRemoteMedia callBackRemotePlayback = castingModeChromecast -> {
                CastHelper castHelper = CastHelper.getInstance(appCMSPresenter.getCurrentActivity());
                if ((castHelper.getRemoteMediaClient() != null &&
                        !castHelper.getRemoteMediaClient().isPlaying()) ||
                        (castHelper.getStartingFilmId() != null &&
                                videoPlayerViewBinder != null &&
                                videoPlayerViewBinder.getContentData() != null &&
                                videoPlayerViewBinder.getContentData().getGist() != null &&
                                videoPlayerViewBinder.getContentData().getGist().getId() != null &&
                                !castHelper.getStartingFilmId().equals(videoPlayerViewBinder.getContentData().getGist().getId()))) {

                    if (videoPlayerViewBinder != null) {
                        if (videoPlayerView != null) {
                            videoPlayerView.pausePlayer();
                        }
                        long castPlayPosition = watchedTime * 1000;
                        if (!isCastConnected) {
                            castPlayPosition = videoPlayerView.getCurrentPosition();
                        }

                        castHelper.launchRemoteMedia(appCMSPresenter,
                                videoPlayerViewBinder.getRelateVideoIds(),
                                videoPlayerViewBinder.getContentData().getGist().getId(),
                                castPlayPosition,
                                videoPlayerViewBinder,
                                true,
                                null);

                        if (!BaseView.isTablet(appCMSPresenter.getCurrentActivity())) {
                            appCMSPresenter.restrictPortraitOnly();
                        }

                        // appCMSPresenter.sendExitFullScreenAction(false);
                    }
                }
            };

            castProvider.setActivityInstance((FragmentActivity) appCMSPresenter.getCurrentActivity(),
                    mMediaRouteButton);
            castProvider.onActivityResume();

            castProvider.setRemotePlaybackCallback(callBackRemotePlayback);
            isCastConnected = castProvider.isCastingConnected();
            castProvider.playChromeCastPlaybackIfCastConnected();
            if (isCastConnected) {

            } else {
                castProvider.setActivityInstance((FragmentActivity) appCMSPresenter.getCurrentActivity(),
                        mMediaRouteButton);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e(TAG, "Error initializing cast provider: " + e.getMessage());
        }
    }

    public CustomVideoPlayerView playerView(Context context, String videoId, String key, AppCMSPresenter appCmsPresenter) {
        //CustomVideoPlayerView videoPlayerView = new CustomVideoPlayerView(context, appCmsPresenter);
        videoPlayerView = new CustomVideoPlayerView(context, appCmsPresenter);


        if (videoId != null) {
            videoPlayerView.setVideoUri(videoId, R.string.loading_video_text);
            appCmsPresenter.setVideoPlayerViewCache(key, videoPlayerView);
            videoPlayerContent = videoPlayerView.getVideoPlayerContent();
        }
        return videoPlayerView;
    }

    void setAutoPlayImage(Context context, Component component, String imgUrl) {
        int viewWidth = (int) BaseView.getViewWidth(context,
                component.getLayout(),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int viewHeight = (int) BaseView.getViewHeight(context,
                component.getLayout(),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (viewHeight > 0 && viewWidth > 0 && viewHeight > viewWidth) {
            if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView, imgUrl, ImageLoader.ScaleType.CENTER)) {
                Glide.with(context)
                        .load(imgUrl)
                        .apply(new RequestOptions().override(viewWidth, viewHeight))
                        .into((ImageView) componentViewResult.componentView);
            }
        } else if (viewWidth > 0) {
            if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView, imgUrl, ImageLoader.ScaleType.CENTER)) {
                Glide.with(context)
                        .load(imgUrl)
                        .apply(new RequestOptions().override(viewWidth, viewHeight).centerCrop())
                        .into((ImageView) componentViewResult.componentView);
            }
        } else {
            if (!ImageUtils.loadImage((ImageView) componentViewResult.componentView, imgUrl,
                    ImageLoader.ScaleType.CENTER)) {
                Glide.with(context)
                        .load(imgUrl)
                        .apply(new RequestOptions().override(viewWidth, viewHeight).centerCrop())
                        .into((ImageView) componentViewResult.componentView);
            }
        }
        componentViewResult.componentView.setBackgroundColor(ContextCompat.getColor(context,
                android.R.color.transparent));
        componentViewResult.useWidthOfScreen = false;
    }

    public enum AdjustOtherState {
        IGNORE,
        INITIATED,
        ADJUST_OTHERS
    }

    public static class VideoPlayerContent {
        long videoPlayTime = 0;
        String videoUrl;
        String ccUrl;
        boolean fullScreenEnabled;
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

    /**
     * This class is used to handle events when a season is selected from the dropdown box.  It will
     * send an event message to repopulate the view with a list of episodes from the given season.
     */
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


    private class OnFightSelectedListener implements
            AdapterView.OnItemSelectedListener,
            OnInternalEvent {

        private List<Fights> fightData;
        Fights fights;
        private List<OnInternalEvent> onInternalEvents;
        private String moduleId;
        private Context context;
        private Module moduleAPI;
        Component component;
        AppCMSPresenter appCMSPresenter;
        Map<String, AppCMSUIKeyType> jsonValueKeyMap;

        public OnFightSelectedListener(List<Fights> fightData, AppCMSPresenter appCMSPresenter, Context context, Module moduleAPI, Component component, Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
            this.fightData = fightData;
            this.onInternalEvents = new ArrayList<>();
            this.appCMSPresenter = appCMSPresenter;
            this.context = context;
            this.moduleAPI = moduleAPI;
            this.component = component;
            this.jsonValueKeyMap = jsonValueKeyMap;

        }

        @Override
        public void addReceiver(OnInternalEvent e) {

        }

        @Override
        public void sendEvent(InternalEvent<?> event) {
            int internalEventsSize = onInternalEvents != null ? onInternalEvents.size() : 0;
//            for (int i = 0; i < internalEventsSize; i++) {
//                onInternalEvents.get(i).receiveEvent(event);
//            }
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
            System.out.println("on item selection");

            if (moduleAPI.getContentData() != null && moduleAPI.getContentData().get(0) != null && moduleAPI.getContentData().get(0).getLiveEvents() != null) {

                fights = moduleAPI.getContentData().get(0).getLiveEvents().get(0).getFights().get(position);
            }

            createFightStateRecorsView(context, appCMSPresenter, moduleAPI, component, jsonValueKeyMap, fights);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public void createFightStateRecorsView(Context context, AppCMSPresenter appCMSPresenter, Module moduleAPI, Component component, Map<String, AppCMSUIKeyType> jsonValueKeyMap, Fights fights) {

        LinearLayout fightStatsView = (LinearLayout) appCMSPresenter.getCurrentActivity().findViewById(R.id.fight_stats_id);
        fightStatsView.setOrientation(LinearLayout.VERTICAL);
        if (fights != null) {
            ((LinearLayout) fightStatsView).removeAllViews();

            View tableView = setFightSummaryModule(context, moduleAPI, component, jsonValueKeyMap, fights, appCMSPresenter);

            if (tableView.getParent() != null)
                ((ViewGroup) tableView.getParent()).removeAllViews();
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            ((LinearLayout) fightStatsView).addView(tableView, p);
            ((LinearLayout) fightStatsView).postInvalidate();
            ((LinearLayout) fightStatsView).bringToFront();
            fightStatsView.setVisibility(View.VISIBLE);
        }
    }

    private static class FightSmmaryAdapterView extends ArrayAdapter<String> {
        private AppCMSPresenter appCMSPresenter;
        private Component component;
        private Map<String, AppCMSUIKeyType> jsonValueKeyMap;

        public FightSmmaryAdapterView(Context context,
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
                                        getColor(parent.getContext(), appCMSPresenter.getAppCMSMain().getBrand()
                                                .getGeneral()
                                                .getBackgroundColor())));
                    }
                } catch (Exception e) {
                    //
                }

//                try {
//                    result.setBackgroundColor(Color.parseColor(
//                            getColor(parent.getContext(), appCMSPresenter.getAppCMSMain()
//                                    .getBrand().getCta().getPrimary().getBackgroundColor())));
//                } catch (Exception e) {
//                    //
//                }

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

            ((TextView) result).setText(getItem(position));
//            ((TextView) result).setTextColor(R.color.color_white);

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
                                        getColor(parent.getContext(), appCMSPresenter.getAppCMSMain().getBrand()
                                                .getGeneral()
                                                .getBackgroundColor())));
                    }
                } catch (Exception e) {
                    //
                }

//                try {
//                    result.setBackgroundColor(Color.parseColor(
//                            getColor(parent.getContext(), appCMSPresenter.getAppCMSMain()
//                                    .getBrand().getCta().getPrimary().getBackgroundColor())));
//                } catch (Exception e) {
//                    //
//                }

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

                result.setPadding(8, 30, 8, 30);
            }

            if (result != null) {
                ((TextView) result).setText(getItem(position));
                System.out.println("on selection-" + getItem(position));
//                ((TextView) result).setTextColor(R.color.color_white);
            }

            return result;
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

    /**
     * This class is used to associate the Watchlist icon with the user's watchlist status for
     * a specific video ID.  This also creates the click listener that responds with the correct
     * action for adding/removing an video from a user's watchlist.  This will also handle events
     * corresponding to the entitlement status of the user and the type of the app (AVOD or SVOD).  An
     * AVOD application will ask a user to login before adding a film to the watchlist if a user is not logged
     * in.  An SVOD application will also ask a user to subscribe first before adding a video to the watchlist
     * if the user is logged in but not subscribed.
     */
    public static class UpdateImageIconAction implements Action1<UserVideoStatusResponse> {
        private final ImageButton imageButton;
        private final AppCMSPresenter appCMSPresenter;
        private final List<String> filmIds;

        private View.OnClickListener addClickListener;
        private View.OnClickListener removeClickListener;
        private ContentDatum contentDatum;

        UpdateImageIconAction(ImageButton imageButton,
                              AppCMSPresenter presenter,
                              List<String> filmIds,
                              ContentDatum contentDatum) {
            this.imageButton = imageButton;
            this.appCMSPresenter = presenter;
            this.filmIds = filmIds;
            this.contentDatum = contentDatum;

            addClickListener = v -> {
                if (appCMSPresenter.isUserLoggedIn()) {
                    for (String filmId : UpdateImageIconAction.this.filmIds) {
                        contentDatum.getGist().setId(filmId);
                        appCMSPresenter.editWatchlist(contentDatum,
                                addToWatchlistResult -> {
                                    UpdateImageIconAction.this.imageButton.setImageResource(
                                            R.drawable.remove_from_watchlist);
                                    UpdateImageIconAction.this.imageButton.setOnClickListener(removeClickListener);
                                },
                                true,
                                UpdateImageIconAction.this.filmIds.indexOf(filmId) == UpdateImageIconAction.this.filmIds.size() - 1);
                    }
                } else {
                    appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_REQUIRED,
                            () -> {
                                appCMSPresenter.setAfterLoginAction(() -> {
                                    //
                                });
                            });
                }
            };
            removeClickListener = v -> {
                for (String filmId : UpdateImageIconAction.this.filmIds) {
                    this.contentDatum.getGist().setId(filmId);
                    appCMSPresenter.editWatchlist(this.contentDatum,
                            addToWatchlistResult -> {
                                UpdateImageIconAction.this.imageButton.setImageResource(
                                        R.drawable.add_to_watchlist);
                                UpdateImageIconAction.this.imageButton.setOnClickListener(addClickListener);
                            },
                            false,
                            UpdateImageIconAction.this.filmIds.indexOf(filmId) == UpdateImageIconAction.this.filmIds.size() - 1);
                }
            };
        }

        public void updateWatchlistResponse(boolean filmQueued) {
            if (filmQueued) {
                imageButton.setImageResource(R.drawable.remove_from_watchlist);
                imageButton.setOnClickListener(removeClickListener);
            } else {
                imageButton.setImageResource(R.drawable.add_to_watchlist);
                imageButton.setOnClickListener(addClickListener);
            }
            imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
     * This class has been created to updated the Download Image Action and Status.  This will associate
     * the download status icon with the current download status of the video.  This will also add the click
     * handler that will handle events when the user clicks on the download icon.  This will also handle events
     * corresponding to the entitlement status of the user and the type of the app (AVOD or SVOD).  An
     * AVOD application will ask a user to login before downloading a video if a user is not logged
     * in.  An SVOD application will also ask a user to subscribe first before downloading a video
     * if the user is logged in but not subscribed.
     */
    public static class UpdateDownloadImageIconAction implements Action1<UserVideoDownloadStatus> {
        private final AppCMSPresenter appCMSPresenter;
        private ContentDatum contentDatum;
        private final String userId;
        private final int radiusDifference;
        private final String id;
        private ImageButton imageButton;
        private View.OnClickListener addClickListener;

        public UpdateDownloadImageIconAction(ImageButton imageButton, AppCMSPresenter presenter,
                                             ContentDatum contentDatum, String userId, int radiusDifference,
                                             String id) {
            this.imageButton = imageButton;
            this.appCMSPresenter = presenter;
            this.contentDatum = contentDatum;
            this.userId = userId;
            this.radiusDifference = radiusDifference;
            this.id = id;

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
                    imageButton.setOnClickListener(null);
                    /**
                     * Handling Quality screen for Audio media type
                     */
                    if (contentDatum.getGist() != null &&
                            contentDatum.getGist().getMediaType() != null &&
                            contentDatum.getGist().getMediaType().toLowerCase().contains(imageButton.getContext().getString(R.string.media_type_audio).toLowerCase()) &&
                            contentDatum.getGist().getContentType() != null &&
                            contentDatum.getGist().getContentType().toLowerCase().contains(imageButton.getContext().getString(R.string.content_type_audio).toLowerCase())) {
                        if (contentDatum.getStreamingInfo() == null ||
                                contentDatum.getStreamingInfo().getAudioAssets() == null ||
                                contentDatum.getStreamingInfo().getAudioAssets().getMp3() == null ||
                                contentDatum.getStreamingInfo().getAudioAssets().getMp3().getUrl() == null ||
                                TextUtils.isEmpty(contentDatum.getStreamingInfo().getAudioAssets().getMp3().getUrl())) {
                            appCMSPresenter.getAudioDetail(UpdateDownloadImageIconAction.this.contentDatum.getGist().getId(),
                                    0, null, false, false, 0,
                                    new AppCMSPresenter.AppCMSAudioDetailAPIAction(false,
                                            false,
                                            false,
                                            null,
                                            UpdateDownloadImageIconAction.this.contentDatum.getGist().getId(),
                                            UpdateDownloadImageIconAction.this.contentDatum.getGist().getId(),
                                            null,
                                            UpdateDownloadImageIconAction.this.contentDatum.getGist().getId(),
                                            false, null) {
                                        @Override
                                        public void call(AppCMSAudioDetailResult appCMSAudioDetailResult) {
                                            AppCMSPageAPI audioApiDetail = appCMSAudioDetailResult.convertToAppCMSPageAPI(UpdateDownloadImageIconAction.this.contentDatum.getGist().getId());
                                            if (audioApiDetail.getModules().get(0).getContentData().get(0) != null) {

                                                appCMSPresenter.editDownload(audioApiDetail.getModules().get(0).getContentData().get(0), UpdateDownloadImageIconAction.this, true);
                                            }
                                        }
                                    });
                        } else {
                            appCMSPresenter.editDownload(UpdateDownloadImageIconAction.this.contentDatum, UpdateDownloadImageIconAction.this, true);
                        }
                    } else {
                        if (appCMSPresenter.isDownloadQualityScreenShowBefore()) {
                            appCMSPresenter.editDownload(UpdateDownloadImageIconAction.this.contentDatum, UpdateDownloadImageIconAction.this, true);
                        } else {
                            appCMSPresenter.showDownloadQualityScreen(UpdateDownloadImageIconAction.this.contentDatum, UpdateDownloadImageIconAction.this);
                        }
                    }
                } else {
                    if (appCMSPresenter.isAppSVOD()) {
                        if (appCMSPresenter.isUserLoggedIn()) {
                            appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.SUBSCRIPTION_PREMIUM_CONTENT_REQUIRED,
                                    () -> {
                                        appCMSPresenter.setAfterLoginAction(() -> {
                                            //
                                        });
                                    });
                        } else {
                            appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_AND_SUBSCRIPTION_PREMIUM_CONTENT_REQUIRED,
                                    () -> {
                                        appCMSPresenter.setAfterLoginAction(() -> {
                                            //
                                        });
                                    });
                        }
                    } else if (!(appCMSPresenter.isAppSVOD() && appCMSPresenter.isUserLoggedIn())) {
                        appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_REQUIRED,
                                () -> {
                                    //
                                });
                    }
                }
                imageButton.setOnClickListener(null);
            };

//            imageButton.setOnClickListener(addClickListener);

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
                        imageButton.setImageResource(R.drawable.ic_download_queued);
                        // Uncomment to allow for Pause/Resume functionality
//                        imageButton.setOnClickListener(addClickListener);
                        imageButton.setOnClickListener(null);
                        break;

                    case STATUS_PENDING:
                        appCMSPresenter.setDownloadInProgress(false);
                        imageButton.setImageResource(R.drawable.ic_download_queued);
                        appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false,
                                radiusDifference,
                                id);
                        imageButton.setOnClickListener(null);
                        break;

                    case STATUS_RUNNING:
                        appCMSPresenter.setDownloadInProgress(true);
//                        imageButton.setImageResource(0);
                        appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                                UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false,
                                radiusDifference,
                                id);
                        // Uncomment to allow for Pause/Resume functionality
//                        imageButton.setOnClickListener(addClickListener);
                        imageButton.setOnClickListener(null);
                        break;

                    case STATUS_SUCCESSFUL:
                        imageButton.setImageResource(R.drawable.ic_downloaded_big);
                        imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageButton.setOnClickListener(null);
                        if (appCMSPresenter.downloadTaskRunning(contentDatum.getGist().getId())) {
                            appCMSPresenter.setDownloadInProgress(false);
                            appCMSPresenter.cancelDownloadIconTimerTask(contentDatum.getGist().getId());
                            contentDatum.getGist().setDownloadStatus(DownloadStatus.STATUS_COMPLETED);
                            appCMSPresenter.notifyDownloadHasCompleted();
                            notifyDataChange();
                        }
                        break;

                    case STATUS_INTERRUPTED:
                        appCMSPresenter.setDownloadInProgress(false);
                        imageButton.setImageResource(android.R.drawable.stat_sys_warning);
                        imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageButton.setOnClickListener(null);
                        break;

                    default:
                        //Log.d(TAG, "No download Status available ");
                        break;
                }

            } else {
                appCMSPresenter.updateDownloadingStatus(contentDatum.getGist().getId(),
                        UpdateDownloadImageIconAction.this.imageButton, appCMSPresenter, this, userId, false,
                        radiusDifference,
                        id);

                if (appCMSPresenter.isVideoDownloading(contentDatum.getGist().getId())) {
                    imageButton.setImageResource(R.drawable.ic_download_queued);

                } else if (appCMSPresenter.isVideoDownloaded(contentDatum.getGist().getId())) {
                    imageButton.setImageResource(R.drawable.ic_downloaded_big);

                } else {
                    imageButton.setImageResource(R.drawable.ic_download_big);
                }
                imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                int fillColor = appCMSPresenter.getGeneralTextColor();
                imageButton.getDrawable().setColorFilter(new PorterDuffColorFilter(fillColor, PorterDuff.Mode.MULTIPLY));
                imageButton.requestLayout();
                imageButton.setOnClickListener(addClickListener);
            }
        }

        public void updateDownloadImageButton(ImageButton imageButton) {
            this.imageButton = imageButton;
        }

        public void updateContentData(final ContentDatum data) {
            this.contentDatum = data;
        }

        public View.OnClickListener getAddClickListener() {
            return addClickListener;
        }
    }

    public static void notifyDataChange() {

      /*  if (appCMSUserWatHisDowAdapter != null)
            appCMSUserWatHisDowAdapter.notifyDataSetChanged();*/
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

       /* @Override
        public void beforeChildren(TagNode node, SpannableStringBuilder builder, SpanStack spanStack) {
            if (builder.length() == 0 || builder.charAt(builder.length() - 1) != '\n') {
                builder.append('\n');
            }
            super.beforeChildren(node, builder, spanStack);
        }*/
    }

    /**
     * This class encapsulates the data necessary for a tray item to create all the required UI components.
     * This may be used for tray adapters so that each individual item in a RecyclerView may be created
     * by the ViewCreate.createComponentView() method.
     */
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
                        createRoundedCorners,
                        null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void addTableRowCell(Context context, String colValue, TableRow row, boolean isHeader, Component component, AppCMSPresenter appCMSPresenter, Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
        TableRow.LayoutParams textViewParams = new TableRow.LayoutParams();
        TextView cell = new TextView(context);

        if (isHeader) {
            cell.setPadding(20, 4, 20, 4);
            cell.setTypeface(cell.getTypeface(), Typeface.ITALIC);
            cell.setTextSize(17);
            cell.setText(colValue + "  ");

        } else {
            cell.setPadding(20, 20, 20, 20);
            cell.setTextSize(15);
            cell.setText(colValue + "  ");
            if (!TextUtils.isEmpty(component.getFontFamily())) {
                setTypeFace(context,
                        appCMSPresenter,
                        jsonValueKeyMap,
                        component,
                        (TextView) cell);
            }

        }
        cell.setTextColor(ContextCompat.getColor(context, R.color.color_grey));
        cell.setGravity(Gravity.CENTER_VERTICAL);
        cell.setLayoutParams(textViewParams);
        row.addView(cell);
    }

    private void startTimer(Context context, AppCMSPresenter appCMSPresenter, long eventDate, long remainingTime) {

        countDownTimer = new CountDownTimer(remainingTime, countDownIntervalInMillis) {
            public void onTick(long millisUntilFinished) {
                long different = appCMSPresenter.getTimeIntervalForEvent(eventDate * 1000L, "EEE MMM dd HH:mm:ss");

                if (different < 0) {

                    onFinish();
                } else {
                    String[] scheduleTime = AppCMSPresenter.geTimeFormat(different).split(":");
                    String[] timerText = context.getResources().getStringArray(R.array.timer_text);
                    if (appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_until_face_off) != null) {
                        TextView timerTile = appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_until_face_off);
                        timerTile.setVisibility(View.VISIBLE);
                    }

                    if (appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null &&
                            appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_id) != null) {
                        LinearLayout linearLayout = appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_id);
                        for (int i = 0; i < linearLayout.getChildCount(); i++) {
                            LinearLayout childLinearLayout = (LinearLayout) linearLayout.getChildAt(i);
                            TextView time = ((TextView) childLinearLayout.getChildAt(0));
                            TextView timeFormat = ((TextView) childLinearLayout.getChildAt(1));

                            time.setText(scheduleTime[i]);
                            time.setTypeface(time.getTypeface(), Typeface.BOLD);

                            timeFormat.setText(timerText[i]);
                            timeFormat.setTextSize(14);
                        }
                    } else {
                        if (countDownTimer != null)
                            countDownTimer.onFinish();
                    }
                }
            }

            public void onFinish() {
                if (appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null &&
                        appCMSPresenter.getCurrentActivity().findViewById(R.id.watch_live_button) != null) {
                    Button watchLive = appCMSPresenter.getCurrentActivity().findViewById(R.id.watch_live_button);
                    watchLive.setVisibility(View.VISIBLE);
                }
                if (appCMSPresenter.getCurrentActivity().findViewById(R.id.fight_summary_module_id) != null) {
                    appCMSPresenter.getCurrentActivity().findViewById(R.id.fight_summary_module_id).setVisibility(View.VISIBLE);
                }
                if (appCMSPresenter != null && appCMSPresenter.getCurrentActivity() != null &&
                        appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_until_face_off) != null) {
                    TextView timerTile = appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_until_face_off);
                    LinearLayout linearLayout = appCMSPresenter.getCurrentActivity().findViewById(R.id.timer_id);
                    timerTile.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                }
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                appCMSPresenter.sendRefreshPageAction();

            }
        }.start();
    }

    public enum EventDetailsColumnsName {
        ROUND("Round"),
        TIME("Time"),
        FIGHTER("Fighter"),
        AS("AS"),
        AS1("AS%"),
        LS("LS"),
        LS1("LS%"),
        GS("GS"),
        GS1("GS%");
//        PLS("PLS");

//        PLS("PLS");

        private final String text;

        /**
         * @param text
         */
        EventDetailsColumnsName(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }


    }

    public static class CustomCharacterSpan extends MetricAffectingSpan {
        double ratio = 0.3;

        public CustomCharacterSpan() {
        }

        public CustomCharacterSpan(double ratio) {
            this.ratio = ratio;
        }

        @Override
        public void updateDrawState(TextPaint paint) {
            paint.baselineShift += (int) (paint.ascent() * ratio);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            paint.baselineShift += (int) (paint.ascent() * ratio);
        }
    }
}


