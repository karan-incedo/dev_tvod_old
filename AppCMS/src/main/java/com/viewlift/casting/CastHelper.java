package com.viewlift.casting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.MediaRouteDiscoveryFragment;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.AppCMSVideoDetail;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.activity.AppCMSPageActivity;
import com.viewlift.views.activity.AppCMSPlayVideoActivity;
import com.viewlift.views.binders.AppCMSVideoPageBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;


public class CastHelper {
    private String TAG = "CastHelper";
    private static CastHelper objMain;
    private String beaconScreenName = "";

    private CastContext mCastContext;
    private CastSession mCastSession;
    public MediaRouter mMediaRouter;
    private SessionManagerListener<CastSession> mSessionManagerListener;
    private Callback callBackRemoteListener;
    private FragmentActivity mActivity;
    private final Context mAppContext;
    private RemoteMediaClient.Listener remoteListener;
    private RemoteMediaClient.ProgressListener progressListener;
    private List<ContentDatum> listRelatedVideosDetails;
    private List<String> listRelatedVideosId;
    private List<String> listCompareRelatedVideosId;

    private MediaRouteSelector mMediaRouteSelector;
    private MyMediaRouterCallback mMediaRouterCallback;
    private AppCMSPresenter appCMSPresenterComponenet;
    private String appName;
    public List<Object> routes = new ArrayList<>();
    public boolean isCastDeviceAvailable = false;
    public boolean isCastDeviceConnected = false;
    public boolean chromeCastConnecting = false;
    public CastDevice mSelectedDevice;
    private int currentPlayingIndex = 0;
    public int playIndexPosition = 0;
    private static long castCurrentDuration;
    private long castCurrentMediaPosition;
    private static final String DISCOVERY_FRAGMENT_TAG = "DiscoveryFragment";

    private AppCMSVideoPageBinder binderPlayScreen;
    private boolean isMainMediaId = false;
    private long currentMediaPosition = 0;
    private String startingFilmId = "";
    private boolean sentBeaconPlay;
    private boolean sentBeaconFirstFrame;
    private boolean sendBeaconPing;

    private boolean onAppDisConnectCalled = false;
    private Action1<OnApplicationEnded> onApplicationEndedAction;
    private String imageUrl = "";
    private String title = "";
    private String videoUrl = "";
    private String paramLink = "";

    private static String mStreamId;
    private long mStartBufferMilliSec = 0l;
    private long mStopBufferMilliSec;
    private static double ttfirstframe = 0d;
    private long beaconBufferingTime;


    private static boolean isVideoDownloaded;


    private CastHelper(Context mContext) {
        mAppContext = mContext.getApplicationContext();
        mCastContext = CastContext.getSharedInstance(mAppContext);
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory("com.google.android.gms.cast.CATEGORY_CAST")
                .build();
        appName = mAppContext.getResources().getString(R.string.app_name);
        beaconScreenName = mAppContext.getResources().getString(R.string.app_cms_beacon_casting_screen_name);

        mMediaRouterCallback = new MyMediaRouterCallback();

        castCurrentMediaPosition = 0L;

        setCastDiscovery();

    }

    public static class OnApplicationEnded {
        private int recommendedVideoIndex;
        private long currentWatchedTime;

        public int getRecommendedVideoIndex() {
            return recommendedVideoIndex;
        }

        public void setRecommendedVideoIndex(int recommendedVideoIndex) {
            this.recommendedVideoIndex = recommendedVideoIndex;
        }

        public long getCurrentWatchedTime() {
            return currentWatchedTime;
        }

        public void setCurrentWatchedTime(long currentWatchedTime) {
            this.currentWatchedTime = currentWatchedTime;
        }
    }


    public void setCastDiscovery() {
        if (CastingUtils.IS_CHROMECAST_ENABLE) {
            mMediaRouter = MediaRouter.getInstance(mAppContext);
            mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                    MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
            if (mActivity instanceof AppCMSPageActivity)
                addMediaRouterDiscoveryFragment();
        }

    }

    public static synchronized CastHelper getInstance(Context context) {
        if (objMain == null) {
            try {
                objMain = new CastHelper(context);
            } catch (Exception e) {

            }
        }
        return objMain;
    }

    public void initCastingObj() {
        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(mAppContext).getSessionManager()
                    .getCurrentCastSession();
        }
        mCastSession = mCastContext.getSessionManager().getCurrentCastSession();
        setupCastListener();
        initRemoteClientListeners();
        initProgressListeners();

    }

    public String getDeviceName() {
        String deviceName = "";
        mCastSession = CastContext.getSharedInstance(mAppContext).getSessionManager()
                .getCurrentCastSession();
        if (mCastSession != null && mCastSession.isConnected()) {
            deviceName = mCastSession.getCastDevice().getFriendlyName();
        }
        return deviceName;
    }


    public void setInstance(FragmentActivity mActivity) {
        this.mActivity = mActivity;
    }

    public void removeInstance() {
        this.mActivity = null;
    }

    public void setCallBackListener(Callback remoteMediaCallback) {
        callBackRemoteListener = remoteMediaCallback;
    }

    public void removeCallBackListener(Callback remoteMediaCallback) {
        callBackRemoteListener = remoteMediaCallback;
    }

    public void setCastSessionManager() {
        try {
            mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener, CastSession.class);
        } catch (NullPointerException npe) {
            //Log.e(TAG, getClass().getCanonicalName() + " " + npe.getMessage());
        } catch (Exception e) {
            //Log.e(TAG, getClass().getCanonicalName() + " " + e.getMessage());
        }
    }

    public void removeCastSessionManager() {

        mCastContext.getSessionManager().removeSessionManagerListener(mSessionManagerListener, CastSession.class);
    }

    public void removeMediaRouterRemoveCallback() {
        if (mMediaRouter != null)
            mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    private void addMediaRouterDiscoveryFragment() {
        FragmentManager fm = mActivity.getSupportFragmentManager();
        DiscoveryFragment fragment =
                (DiscoveryFragment) fm.findFragmentByTag(DISCOVERY_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new DiscoveryFragment();
            fragment.setCallback(mMediaRouterCallback);
            fragment.setRouteSelector(mMediaRouteSelector);
            fm.beginTransaction().add(fragment, DISCOVERY_FRAGMENT_TAG).commit();
        } else {
            fragment.setCallback(mMediaRouterCallback);
            fragment.setRouteSelector(mMediaRouteSelector);
        }
    }

    public static final class DiscoveryFragment extends MediaRouteDiscoveryFragment {
        private static final String TAG = "DiscoveryFragment";
        private MediaRouter.Callback mCallback;

        public DiscoveryFragment() {
            mCallback = null;
        }

        public void setCallback(MediaRouter.Callback cb) {
            mCallback = cb;
        }

        @Override
        public MediaRouter.Callback onCreateCallback() {
            return mCallback;
        }

        @Override
        public int onPrepareCallbackFlags() {
            // Add the CALLBACK_FLAG_UNFILTERED_EVENTS flag to ensure that we will
            // observe and log all route events including those that are for routes
            // that do not match our selector.  This is only for demonstration purposes
            // and should not be needed by most applications.
            return super.onPrepareCallbackFlags() | MediaRouter.CALLBACK_FLAG_UNFILTERED_EVENTS;
        }
    }

    public interface Callback {
        void onApplicationConnected();

        void onApplicationDisconnected();

        void onRouterAdded(MediaRouter mMediaRouter, MediaRouter.RouteInfo route);

        void onRouterRemoved(MediaRouter mMediaRouter, MediaRouter.RouteInfo route);

        void onRouterSelected(MediaRouter mMediaRouter, MediaRouter.RouteInfo info);

        void onRouterUnselected(MediaRouter mMediaRouter, MediaRouter.RouteInfo info);
    }

    public void finishPlayerScreenOnCastConnect() {
        if (callBackRemoteListener != null && mActivity != null & mActivity instanceof AppCMSPlayVideoActivity) {
            mActivity.finish();
        }
    }

    public boolean isRemoteDeviceConnected() {
        boolean isCastDeviceConnected = false;
        if (mMediaRouter == null)
            return false;

        if (mMediaRouter.getSelectedRoute().isDefault()) {
            isCastDeviceConnected = false;

        } else if (mMediaRouter.getSelectedRoute().getConnectionState()
                == MediaRouter.RouteInfo.CONNECTION_STATE_CONNECTED) {
            isCastDeviceConnected = true;

        } else if (mSelectedDevice != null) {
            isCastDeviceConnected = true;

        } else if (mMediaRouter.getSelectedRoute().getConnectionState()
                == MediaRouter.RouteInfo.CONNECTION_STATE_CONNECTING) {
            isCastDeviceConnected = true;
        }
        return isCastDeviceConnected;
    }


    public void launchRemoteMedia(AppCMSPresenter appCMSPresenter,
                                  List<String> relateVideoId,
                                  String filmId,
                                  long currentPosition,
                                  AppCMSVideoPageBinder binder,
                                  boolean sentBeaconPlay,
                                  Action1<OnApplicationEnded> onApplicationEndedAction) {
        this.sentBeaconPlay = sentBeaconPlay;
        this.onApplicationEndedAction = onApplicationEndedAction;
        if (mActivity != null && CastingUtils.isMediaQueueLoaded) {

            CastingUtils.isRemoteMediaControllerOpen = false;
            currentMediaPosition = currentPosition;
            binderPlayScreen = binder;
            startingFilmId = filmId;
            if (getRemoteMediaClient() == null) {
                return;
            }

            CastingUtils.isMediaQueueLoaded = false;
            getRemoteMediaClient().removeListener(remoteListener);
            getRemoteMediaClient().removeProgressListener(progressListener);
            this.appCMSPresenterComponenet = appCMSPresenter;
            listRelatedVideosDetails = new ArrayList<ContentDatum>();
            listRelatedVideosId = new ArrayList<String>();
            listCompareRelatedVideosId = new ArrayList<String>();

            if (filmId == null && relateVideoId == null) {
                return;
            }
            if (relateVideoId != null) {

                if (!relateVideoId.contains(filmId)) {
                    isMainMediaId = true;
                    listRelatedVideosId.add(filmId);
                    currentPlayingIndex = 0;
                } else {
                    currentPlayingIndex = relateVideoId.indexOf(filmId);
                }
                listRelatedVideosId.addAll(relateVideoId);
                listCompareRelatedVideosId.addAll(listRelatedVideosId);
            } else if (filmId != null) {
                currentPlayingIndex = 0;
                listRelatedVideosId.add(filmId);
                listCompareRelatedVideosId.add(filmId);
            }


            if (relateVideoId == null && binderPlayScreen != null && CastingUtils.getPlayingUrl(binderPlayScreen.getContentData()) != null && !TextUtils.isEmpty(CastingUtils.getPlayingUrl(binderPlayScreen.getContentData()))) {
                launchSingeRemoteMedia(binderPlayScreen, CastingUtils.getPlayingUrl(binderPlayScreen.getContentData()), filmId, currentPosition, false);
            } else {
                callRelatedVideoData();
            }
            Toast.makeText(mAppContext, mAppContext.getString(R.string.loading_vid_on_casting), Toast.LENGTH_SHORT).show();
            try {
                mStreamId = appCMSPresenterComponenet.getStreamingId(binder.getContentData().getGist().getTitle());
            } catch (Exception e) {
                //Log.e(TAG, e.getMessage());
                mStreamId = filmId + appCMSPresenterComponenet.getCurrentTimeStamp();
            }


        }
    }

    //launchTrailer use to launch media when auto play off
    public void launchTrailer(AppCMSPresenter appCMSPresenter, String filmId, AppCMSVideoPageBinder binder, long currentPosition) {

        Toast.makeText(mAppContext, mAppContext.getString(R.string.loading_vid_on_casting), Toast.LENGTH_SHORT).show();
        if(binder == null || binder.getContentData() == null){
            Toast.makeText(mAppContext, mAppContext.getString(R.string.app_cms_download_stream_info_error_title), Toast.LENGTH_SHORT).show();
            return;
        }
        this.appCMSPresenterComponenet = appCMSPresenter;
        if (binder.getContentData().getContentDetails() != null
                && binder.getContentData().getContentDetails().getTrailers() != null
                && binder.getContentData().getContentDetails().getTrailers().get(0) != null
                && binder.getContentData().getContentDetails().getTrailers().get(0).getVideoAssets() != null) {
            title = binder.getContentData().getContentDetails().getTrailers().get(0).getTitle();
            VideoAssets videoAssets = binder.getContentData().getContentDetails().getTrailers().get(0).getVideoAssets();
            if (videoAssets.getMpeg() != null && videoAssets.getMpeg().size() > 0) {
                videoUrl = videoAssets.getMpeg().get(videoAssets.getMpeg().size() - 1).getUrl();
            }
        } else {
            if (binder.getContentData().getGist() != null && binder.getContentData().getGist().getTitle() != null) {
                title = binder.getContentData().getGist().getTitle();
            }
            videoUrl = CastingUtils.getPlayingUrl(binder.getContentData());
        }

        if (videoUrl != null && !TextUtils.isEmpty(videoUrl)) {
            launchSingeRemoteMedia(binder, videoUrl, filmId, currentPosition, true);
        }
        try {
            mStreamId = appCMSPresenterComponenet.getStreamingId(binder.getContentData().getGist().getTitle());
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
            mStreamId = filmId + appCMSPresenterComponenet.getCurrentTimeStamp();
        }
    }


    public void launchSingeRemoteMedia(AppCMSVideoPageBinder binder, String videoPlayUrl, String filmId, long currentPosition, boolean isTrailer) {

        if (binder != null && binder.getContentData() != null && binder.getContentData().getGist() != null) {
            if (binder.getContentData().getGist().getPermalink() != null) {
                paramLink = binder.getContentData().getGist().getPermalink();
            }
            title = CastingUtils.getTitle(binder.getContentData(), isTrailer);
            if (binder.getContentData().getGist().getVideoImageUrl() != null) {
                imageUrl = binder.getContentData().getGist().getVideoImageUrl();
            }

        }
        CastingUtils.isRemoteMediaControllerOpen = false;
        JSONObject customData = new JSONObject();
        try {
            customData.put(CastingUtils.MEDIA_KEY, filmId);
        } catch (JSONException e) {
            //Log.e(TAG, "Error parsing JSON data: " + e.getMessage());
        }
        String appPackageName = mAppContext.getPackageName();

        try {
            customData.put(CastingUtils.PARAM_KEY, paramLink);
            customData.put(CastingUtils.VIDEO_TITLE, title);
            customData.put(CastingUtils.ITEM_TYPE, appPackageName + "" + CastingUtils.ITEM_TYPE_VIDEO);

        } catch (JSONException e) {
            //Log.e(TAG, "Error parsing JSON data: " + e.getMessage());
        }
        if (getRemoteMediaClient() != null) {
            getRemoteMediaClient().load(CastingUtils.buildMediaInfo(title,
                    appName,
                    imageUrl,
                    videoPlayUrl,
                    customData,
                    mAppContext), true, currentPosition);
            getRemoteMediaClient().addListener(remoteListener);
            onAppDisConnectCalled = false;
            CastingUtils.isMediaQueueLoaded = true;

        }

    }

    public void launchSingeRemoteMedia(String title, String paramLink, String imageUrl, String videoPlayUrl, String filmId, long currentPosition, boolean isTrailer) {

        this.paramLink = paramLink != null ? paramLink : "";
        this.imageUrl = imageUrl != null ? imageUrl : "";
        this.title = title != null ? title : "";
        CastingUtils.isRemoteMediaControllerOpen = false;
        JSONObject customData = new JSONObject();
        try {
            customData.put(CastingUtils.MEDIA_KEY, filmId);
        } catch (JSONException e) {
            //Log.e(TAG, "Error parsing JSON data: " + e.getMessage());
        }
        String appPackageName = mAppContext.getPackageName();

        try {
            customData.put(CastingUtils.PARAM_KEY, paramLink);
            customData.put(CastingUtils.VIDEO_TITLE, title);
            customData.put(CastingUtils.ITEM_TYPE, appPackageName + "" + CastingUtils.ITEM_TYPE_VIDEO);
        } catch (JSONException e) {
            //Log.e(TAG, "Error parsing JSON data: " + e.getMessage());
        }
        if (getRemoteMediaClient() != null) {
            getRemoteMediaClient().load(CastingUtils.buildMediaInfo(title,
                    appName,
                    imageUrl,
                    videoPlayUrl,
                    customData,
                    mAppContext), true, currentPosition);
            getRemoteMediaClient().addListener(remoteListener);
            onAppDisConnectCalled = false;
        }

    }

    public void openRemoteController() {

        if (mActivity != null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //Log.e(TAG, "Error opening remote controller: " + e.getMessage());
            }
            if (!CastingUtils.isRemoteMediaControllerOpen) {
                Intent intent = new Intent(mActivity, ExpandedControlsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
                CastingUtils.isRemoteMediaControllerOpen = true;

            }
        }
    }


    private void initRemoteClientListeners() {
        remoteListener = new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
                openRemoteController();
                if (getRemoteMediaClient() != null && getRemoteMediaClient().getMediaStatus() != null &&
                        getRemoteMediaClient().getMediaStatus().getCurrentItemId() <
                                getRemoteMediaClient().getMediaStatus().getLoadingItemId()) {
                    sentBeaconPlay = false;
                    sentBeaconFirstFrame = false;
                    try {
                        mStreamId = appCMSPresenterComponenet.getStreamingId(title);
                    } catch (Exception e) {
                        //Log.e(TAG, e.getMessage());
                        mStreamId = CastingUtils.getRemoteMediaId(mAppContext) + appCMSPresenterComponenet.getCurrentTimeStamp();
                    }
                }
                updatePlaybackState();
            }

            @Override
            public void onMetadataUpdated() {
                try {
                    JSONObject getRemoteObject = CastContext.getSharedInstance(mAppContext)
                            .getSessionManager()
                            .getCurrentCastSession()
                            .getRemoteMediaClient()
                            .getCurrentItem()
                            .getCustomData();
                    CastingUtils.castingMediaId = getRemoteObject.getString(CastingUtils.MEDIA_KEY);
                } catch (Exception e) {
                    //Log.e(TAG, e.getLocalizedMessage());
                }
                if (listCompareRelatedVideosId != null) {
                    playIndexPosition = listCompareRelatedVideosId
                            .indexOf(CastingUtils.castingMediaId);
                }

                //Log.d(TAG, "Remote Media listener-" + "onMetadataUpdated");
            }

            @Override
            public void onQueueStatusUpdated() {
                //Log.d(TAG, "Remote Media listener-" + "onQueueStatusUpdated");
            }

            @Override
            public void onPreloadStatusUpdated() {
                //Log.d(TAG, "Remote Media listener-" + "onPreloadStatusUpdated");
            }

            @Override
            public void onSendingRemoteMediaRequest() {
                //Log.d(TAG, "Remote Media listener-" + "onSendingRemoteMediaRequest");

            }

            @Override
            public void onAdBreakStatusUpdated() {
                //Log.d(TAG, "Remote Media listener-" + "onAdBreakStatusUpdated");

            }
        };

    }


    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {

                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {

                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {

                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {

                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {

            }

            @Override
            public void onSessionEnding(CastSession session) {

            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {

            }

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;
                if (callBackRemoteListener != null)
                    callBackRemoteListener.onApplicationConnected();

            }

            private void onApplicationDisconnected() {
                CastingUtils.isMediaQueueLoaded = true;

                OnApplicationEnded onApplicationEnded = new OnApplicationEnded();
                onApplicationEnded.setCurrentWatchedTime(castCurrentMediaPosition);
                onApplicationEnded.setRecommendedVideoIndex(playIndexPosition);

                if (getRemoteMediaClient() != null) {
                    getRemoteMediaClient().stop();
                    getRemoteMediaClient().removeListener(remoteListener);
                    getRemoteMediaClient().removeProgressListener(progressListener);
                }

                onAppDisConnectCalled = false;
                if (callBackRemoteListener != null && mActivity != null && mActivity instanceof AppCMSPlayVideoActivity && binderPlayScreen != null && !onAppDisConnectCalled) {
                    onAppDisConnectCalled = true;
                    //if player activity already opened than finish it
                    if (onApplicationEndedAction != null) {
                        Observable.just(onApplicationEnded).subscribe(onApplicationEndedAction);
                    }
                    //if casted from local play screen from first video than this video will not in related video list  so set -1 index position to play on local player

                    if (CastingUtils.castingMediaId == null || TextUtils.isEmpty(CastingUtils.castingMediaId)) {
                        CastingUtils.castingMediaId = startingFilmId;
                    }
                    if (isMainMediaId) {
                        playIndexPosition--;
                    } else if (listCompareRelatedVideosId != null) {
                        playIndexPosition = listCompareRelatedVideosId.indexOf(CastingUtils.castingMediaId);
                    }

                    //Log.d(TAG, "Cast Index " + playIndexPosition);
                    if (listRelatedVideosDetails != null && listRelatedVideosDetails.size() > 0) {
                        int currentVideoDetailIndex = getCurrentIndex(listRelatedVideosDetails, CastingUtils.castingMediaId);
                        if (currentVideoDetailIndex < listRelatedVideosDetails.size())
                            binderPlayScreen.setContentData(listRelatedVideosDetails.get(currentVideoDetailIndex));
                    }

                    binderPlayScreen.setCurrentPlayingVideoIndex(playIndexPosition);
                    if (listCompareRelatedVideosId != null && playIndexPosition < listCompareRelatedVideosId.size()) {
                        appCMSPresenterComponenet.playNextVideo(binderPlayScreen,
                                binderPlayScreen.getCurrentPlayingVideoIndex(),
                                castCurrentMediaPosition);
                    }

                    CastingUtils.castingMediaId = "";
                }
                if (callBackRemoteListener != null)
                    callBackRemoteListener.onApplicationDisconnected();
            }
        };
    }


    private void initProgressListeners() {

        progressListener = (remoteCastProgress, totalCastDuration) -> {
            castCurrentMediaPosition = remoteCastProgress;
            castCurrentDuration = remoteCastProgress / 1000;
            try {
                if (castCurrentDuration % 30 == 0 && sendBeaconPing) {
                    String currentRemoteMediaId = CastingUtils.getRemoteMediaId(mAppContext);
                    String currentMediaParamKey = CastingUtils.getRemoteParamKey(mAppContext);

                    System.out.println("on progress update media id- " + currentRemoteMediaId);
                    if (!TextUtils.isEmpty(currentRemoteMediaId)) {
                        appCMSPresenterComponenet.updateWatchedTime(currentRemoteMediaId,
                                castCurrentDuration);
                        appCMSPresenterComponenet.sendBeaconMessage(currentRemoteMediaId,
                                currentMediaParamKey,
                                beaconScreenName,
                                castCurrentDuration,
                                true,
                                AppCMSPresenter.BeaconEvent.PING,
                                "Video",
                                null,
                                null,
                                null,
                                mStreamId,
                                0d,
                                0,
                                isVideoDownloaded);
                        appCMSPresenterComponenet.sendGaEvent(mAppContext.getResources().getString(R.string.play_video_action),
                                mAppContext.getResources().getString(R.string.play_video_category), currentRemoteMediaId);

                        appCMSPresenterComponenet.updateWatchedTime(currentRemoteMediaId,
                                castCurrentDuration);
                    }

                }
            } catch (Exception e) {
                //Log.e(TAG, "Error initializing progress indicators: " + e.getMessage());
            }
        };
    }


    private void callRelatedVideoData() {
        CastingUtils.isMediaQueueLoaded = true;

        String filmIds = "";
        if (listRelatedVideosId != null && listRelatedVideosId.size() >= 5) {
            List<String> subList = listRelatedVideosId.subList(0, 5);

            filmIds = TextUtils.join(",", subList);
            subList.clear();

        } else if (listRelatedVideosId != null && listRelatedVideosId.size() > 0) {
            filmIds = TextUtils.join(",", listRelatedVideosId);
            listRelatedVideosId.clear();
        }

        //Log.d(TAG, "Film Ids-" + filmIds);

        appCMSPresenterComponenet.getRelatedMedia(filmIds, new Action1<AppCMSVideoDetail>() {
            @Override
            public void call(AppCMSVideoDetail relatedMediaVideoDetails) {
                if (listRelatedVideosDetails == null && relatedMediaVideoDetails != null && relatedMediaVideoDetails.getRecords() != null) {
                    listRelatedVideosDetails = relatedMediaVideoDetails.getRecords();
                } else if (relatedMediaVideoDetails != null && relatedMediaVideoDetails.getRecords() != null) {
                    listRelatedVideosDetails.addAll(relatedMediaVideoDetails.getRecords());
                }

                if (listRelatedVideosId != null && listRelatedVideosId.size() > 0) {
                    callRelatedVideoData();
                } else {
                    if (appCMSPresenterComponenet.isAppSVOD() && !appCMSPresenterComponenet.isUserSubscribed()) {
                        removeNonFreeVideos();
                    }
                    castMediaListToRemoteLocation();
                    //Log.d(TAG, "Cast Media List ");
                }
            }
        });
    }

    private void removeNonFreeVideos() {
        List<Integer> freeMovieIndices = new ArrayList<>();
        List<ContentDatum> freeMovies = new ArrayList<>();
        List<String> freeMovieIds = new ArrayList<>();
        for (int i = 0; i < listRelatedVideosDetails.size(); i++) {
            ContentDatum contentDatum = listRelatedVideosDetails.get(i);
            if (contentDatum != null &&
                    contentDatum.getGist() != null &&
                    contentDatum.getGist().getFree()) {
                freeMovieIndices.add(i);
            }
        }

        for (int i = 0; i < freeMovieIndices.size(); i++) {
            freeMovies.add(listRelatedVideosDetails.get(freeMovieIndices.get(i)));
            freeMovieIds.add(listRelatedVideosDetails.get(freeMovieIndices.get(i)).getGist().getId());
        }

        listRelatedVideosDetails = freeMovies;
        listCompareRelatedVideosId = freeMovieIds;
    }

    private void castMediaListToRemoteLocation() {
        CastingUtils.isMediaQueueLoaded = true;
        if (getRemoteMediaClient() != null && listRelatedVideosDetails != null && listRelatedVideosDetails.size() > 0) {
            MediaQueueItem[] queueItemsArray = CastingUtils.BuildCastingQueueItems(listRelatedVideosDetails,
                    appName,
                    listCompareRelatedVideosId,
                    mAppContext);
            getRemoteMediaClient().queueLoad(queueItemsArray, currentPlayingIndex,
                    MediaStatus.REPEAT_MODE_REPEAT_OFF, currentMediaPosition, null);
            getRemoteMediaClient().addListener(remoteListener);
            getRemoteMediaClient().addProgressListener(progressListener, 1000);
            onAppDisConnectCalled = false;
        } else if (binderPlayScreen != null && binderPlayScreen.getContentData() != null) {

            videoUrl = CastingUtils.getPlayingUrl(binderPlayScreen.getContentData());
            if (videoUrl != null && !TextUtils.isEmpty(videoUrl)) {
                launchSingeRemoteMedia(binderPlayScreen, videoUrl, startingFilmId, currentMediaPosition, false);
            }
        }
    }


    private class MyMediaRouterCallback extends MediaRouter.Callback {
        @Override
        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
            //Log.w(TAG, "MyMediaRouterCallback-onRouteAdded ");
            List<MediaRouter.RouteInfo> c_routes = mMediaRouter.getRoutes();
            routes.clear();
            routes.addAll(c_routes);
            onFilterRoutes(routes);
            isCastDeviceAvailable = routes.size() > 0;
            if (callBackRemoteListener != null)
                callBackRemoteListener.onRouterAdded(mMediaRouter, route);
        }

        @Override
        public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
            //Log.w(TAG, "MyMediaRouterCallback-onRouteRemoved ");
            for (int i = 0; i < routes.size(); i++) {
                if (routes.get(i) instanceof MediaRouter.RouteInfo) {
                    MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo) routes.get(i);
                    if (routeInfo.equals(route)) {
                        routes.remove(i);
                        break;
                    }
                }
            }
            isCastDeviceAvailable = routes.size() > 0;
            if (callBackRemoteListener != null)
                callBackRemoteListener.onRouterRemoved(mMediaRouter, route);
        }

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {

            chromeCastConnecting = true;
            mSelectedDevice = CastDevice.getFromBundle(info.getExtras());
            isCastDeviceConnected = true;
            if (callBackRemoteListener != null)
                callBackRemoteListener.onRouterSelected(mMediaRouter, info);
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            mSelectedDevice = null;
            isCastDeviceConnected = false;
            if (callBackRemoteListener != null)
                callBackRemoteListener.onRouterUnselected(mMediaRouter, info);
            CastingUtils.isMediaQueueLoaded = true;
        }
    }

    public RemoteMediaClient getRemoteMediaClient() {
        CastSession castSession = CastContext.getSharedInstance(mAppContext).getSessionManager()
                .getCurrentCastSession();
        if (castSession == null || !castSession.isConnected()) {
            //Log.w(TAG, "Trying to get a RemoteMediaClient when no CastSession is started.");
            return null;
        }
        return castSession.getRemoteMediaClient();
    }


    private void updatePlaybackState() {
        boolean isFinish = false;
        mCastSession = CastContext.getSharedInstance(mAppContext).getSessionManager()
                .getCurrentCastSession();

        if (listRelatedVideosDetails != null && listRelatedVideosDetails.size() > 0) {
            int currentVideoDetailIndex = getCurrentIndex(listRelatedVideosDetails, CastingUtils.castingMediaId);
            if (currentVideoDetailIndex >= listRelatedVideosDetails.size()) {
                isFinish = true;
            }
        } else {
            isFinish = true;
        }

        if (getRemoteMediaClient() == null) {
            return;
        }
        int status = getRemoteMediaClient().getPlayerState();
        int idleReason = getRemoteMediaClient().getIdleReason();
        String currentRemoteMediaId = CastingUtils.getRemoteMediaId(mAppContext);
        String currentMediaParamKey = CastingUtils.getRemoteParamKey(mAppContext);

        if (!sentBeaconPlay) {

            if (appCMSPresenterComponenet == null)
                return;
            isVideoDownloaded = appCMSPresenterComponenet.isVideoDownloaded(currentRemoteMediaId);
            mStartBufferMilliSec = new Date().getTime();
            if (!TextUtils.isEmpty(currentRemoteMediaId)) {
                mStopBufferMilliSec = new Date().getTime();
                appCMSPresenterComponenet.sendBeaconMessage(currentRemoteMediaId,
                        currentMediaParamKey,
                        beaconScreenName,
                        castCurrentDuration,
                        true,
                        AppCMSPresenter.BeaconEvent.PLAY,
                        "Video",
                        null,
                        null,
                        null,
                        mStreamId,
                        0d,
                        0,
                        isVideoDownloaded);
                sentBeaconPlay = true;

                appCMSPresenterComponenet.sendGaEvent(mAppContext.getResources().getString(R.string.play_video_action),
                        mAppContext.getResources().getString(R.string.play_video_category), currentRemoteMediaId);
            }
        }
        switch (status) {
            case MediaStatus.PLAYER_STATE_PLAYING:
                sendBeaconPing = true;
                if (!sentBeaconFirstFrame) {

                    if (!TextUtils.isEmpty(currentRemoteMediaId)) {
                        mStopBufferMilliSec = new Date().getTime();
                        ttfirstframe = mStartBufferMilliSec == 0l ? 0d : ((mStopBufferMilliSec - mStartBufferMilliSec) / 1000d);
                        appCMSPresenterComponenet.sendBeaconMessage(currentRemoteMediaId,
                                currentMediaParamKey,
                                beaconScreenName,
                                castCurrentDuration,
                                true,
                                AppCMSPresenter.BeaconEvent.FIRST_FRAME,
                                "Video",
                                null,
                                null,
                                null,
                                mStreamId,
                                ttfirstframe,
                                0,
                                isVideoDownloaded);
                        sentBeaconFirstFrame = true;
                    }
                }
                break;

            case MediaStatus.PLAYER_STATE_PAUSED:
                sendBeaconPing = false;
                break;

            case MediaStatus.PLAYER_STATE_UNKNOWN:
                sendBeaconPing = false;
                break;

            case MediaStatus.PLAYER_STATE_BUFFERING:
                sendBeaconPing = false;
                if (((System.currentTimeMillis() - beaconBufferingTime) / 1000) >= 5) {
                    beaconBufferingTime = System.currentTimeMillis();
                    if (appCMSPresenterComponenet != null) {
                        appCMSPresenterComponenet.sendBeaconMessage(currentRemoteMediaId,
                                currentMediaParamKey,
                                beaconScreenName,
                                castCurrentDuration,
                                true,
                                AppCMSPresenter.BeaconEvent.BUFFERING,
                                "Video",
                                null,
                                null,
                                null,
                                mStreamId,
                                0d,
                                0,
                                isVideoDownloaded);


                    }


                }


                break;

            case MediaStatus.PLAYER_STATE_IDLE:
                sendBeaconPing = false;

                if (idleReason == MediaStatus.IDLE_REASON_FINISHED) {
                    //If all movies in auto play queue have been finished then finish the player activity if opened
                    if (isFinish && mActivity instanceof AppCMSPlayVideoActivity) {
                        mActivity.finish();
                    }

                }
                break;

            default: // case unknown
                sendBeaconPing = false;
                break;
        }
    }


    /**
     * Called to filter the set of routes that should be included in the list.
     * <p>
     * The default implementation iterates over all routes in the provided list and
     * removes those for which {@link #onFilterRoute} returns false.
     * </p>
     *
     * @param route The list of routes to filter in-place, never null.
     */
    public void onFilterRoutes(@NonNull List<Object> route) {
        for (int i = routes.size(); i-- > 0; ) {
            if (routes.get(i) instanceof MediaRouter.RouteInfo)
                if (!onFilterRoute((MediaRouter.RouteInfo) routes.get(i))) {
                    routes.remove(i);
                }

        }
    }

    /**
     * Returns true if the route should be included in the list.
     * <p>
     * The default implementation returns true for enabled non-default routes that
     * match the selector.  Subclasses can override this method to filter routes
     * differently.
     * </p>
     *
     * @param route The route to consider, never null.
     * @return True if the route should be included in the chooser dialog.
     */
    @SuppressLint("RestrictedApi")
    public boolean onFilterRoute(@NonNull MediaRouter.RouteInfo route) {
        return !route.isDefaultOrBluetooth() && route.isEnabled()
                && route.matchesSelector(mMediaRouteSelector);
    }

    public int getCurrentIndex(List<ContentDatum> list, String videoid) {
        int i = 0;
        for (i = 0; i < list.size(); i++) {
            if (videoid.equalsIgnoreCase(list.get(i).getGist().getId())) {
                return i;
            }
        }
        return i;
    }

    public void disconnectChromecastOnLogout() {
        if (CastContext.getSharedInstance(mAppContext).getSessionManager() != null) {

            try {
                if (CastContext.getSharedInstance(mAppContext).getSessionManager() != null) {
                    CastContext.getSharedInstance(mAppContext).getSessionManager().removeSessionManagerListener(mSessionManagerListener, CastSession.class);
                }

                CastContext.getSharedInstance(mAppContext).getSessionManager().getCurrentCastSession().getRemoteMediaClient().removeListener(remoteListener);

                mSessionManagerListener = null;
                CastContext.getSharedInstance(mAppContext).getSessionManager().endCurrentSession(true);
            } catch (Exception e) {
                //Log.e(TAG, e.getMessage()); // getting crash by e.printStackTrace()

            }
        }
    }

    public String getStartingFilmId() {
        return startingFilmId;
    }
}

