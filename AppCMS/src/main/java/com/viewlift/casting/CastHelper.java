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
import android.util.Log;
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
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.activity.AppCMSPageActivity;
import com.viewlift.views.activity.AppCMSPlayVideoActivity;
import com.viewlift.views.binders.AppCMSVideoPageBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;


public class CastHelper {
    private String TAG = "CastHelper";
    private static CastHelper objMain;
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

    public MediaRouteSelector mMediaRouteSelector;
    private MyMediaRouterCallback mMediaRouterCallback;
    private AppCMSPresenter appCMSPresenterComponenet;
    public String appName;
    public List<Object> routes = new ArrayList<>();
    public boolean isCastDeviceAvailable = false;
    public boolean isCastDeviceConnected = false;
    public boolean chromeCastConnecting = false;
    public CastDevice mSelectedDevice;
    private int currentPlayingIndex = 0;
    public int playIndexPosition = 0;
    long castCurrentDuration;
    private static final String DISCOVERY_FRAGMENT_TAG = "DiscoveryFragment";

    private CastHelper(Context mContext) {
        mAppContext = mContext.getApplicationContext();
        mCastContext = CastContext.getSharedInstance(mAppContext);
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory("com.google.android.gms.cast.CATEGORY_CAST")
                .build();
        appName = mAppContext.getResources().getString(R.string.app_name);
        mMediaRouterCallback = new MyMediaRouterCallback();

        setCastDiscovery();

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
            objMain = new CastHelper(context);
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
        mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener, CastSession.class);
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

        } else if (mMediaRouter.getSelectedRoute().getConnectionState()
                == MediaRouter.RouteInfo.CONNECTION_STATE_CONNECTING) {
            isCastDeviceConnected = true;
        }
        return isCastDeviceConnected;
    }

    private AppCMSVideoPageBinder binderPlayScreen;
    private boolean isMainMediaId = false;
    private long currentMediaPosition = 0;
    private String startingFilmId = "";

    public void launchRemoteMedia(AppCMSPresenter appCMSPresenter, List<String> relateVideoId, String filmId, long currentPosition, AppCMSVideoPageBinder binder) {
        if (mActivity != null && CastingUtils.isMediaQueueLoaded) {
            Toast.makeText(mAppContext, mAppContext.getString(R.string.loading_vid_on_casting), Toast.LENGTH_SHORT).show();

            CastingUtils.isRemoteMediaControllerOpen = false;
            currentMediaPosition = currentPosition;
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
            binderPlayScreen = binder;
            callRelatedVideoData();
        }
    }

    //launchSingleMediaRemote use to launch media when auto play off
    public void launchSingleMediaRemote(String title, String appName, String imageUrl, String hlsUrl, String filmId) {
        CastingUtils.isRemoteMediaControllerOpen = false;
        JSONObject customData = new JSONObject();
        try {
            customData.put(CastingUtils.MEDIA_KEY, filmId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getRemoteMediaClient().load(CastingUtils.buildMediaInfo(title, appName, imageUrl, hlsUrl, customData));
    }

    public void openRemoteController() {

        if (mActivity != null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!CastingUtils.isRemoteMediaControllerOpen) {
                Intent intent = new Intent(mActivity, ExpandedControlsActivity.class);
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
            }

            @Override
            public void onMetadataUpdated() {
                try {
                    JSONObject getRemoteObject = null;

                    getRemoteObject = CastContext.getSharedInstance(mAppContext).getSessionManager().getCurrentCastSession().getRemoteMediaClient().getCurrentItem().getCustomData();
                    CastingUtils.castingMediaId = getRemoteObject.getString(CastingUtils.MEDIA_KEY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                playIndexPosition = listCompareRelatedVideosId.indexOf(CastingUtils.castingMediaId);
                System.out.println("Cast Index " + playIndexPosition);

                Log.d(TAG, "Remote Media listener-" + "onMetadataUpdated");
            }

            @Override
            public void onQueueStatusUpdated() {
                Log.d(TAG, "Remote Media listener-" + "onQueueStatusUpdated");
            }

            @Override
            public void onPreloadStatusUpdated() {
                Log.d(TAG, "Remote Media listener-" + "onPreloadStatusUpdated");
            }

            @Override
            public void onSendingRemoteMediaRequest() {
                Log.d(TAG, "Remote Media listener-" + "onSendingRemoteMediaRequest");

            }

            @Override
            public void onAdBreakStatusUpdated() {
                Log.d(TAG, "Remote Media listener-" + "onAdBreakStatusUpdated");

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
                if (getRemoteMediaClient() != null) {
                    getRemoteMediaClient().removeListener(remoteListener);
                    getRemoteMediaClient().removeProgressListener(progressListener);
                }
                CastingUtils.isMediaQueueLoaded = true;
                if (callBackRemoteListener != null && mActivity instanceof AppCMSPlayVideoActivity && binderPlayScreen != null) {
                    mActivity.finish();
                    //if casted from local play screen from first video than this video will not in related video list  so set -1 index position to play on local player

                    if (CastingUtils.castingMediaId == null || TextUtils.isEmpty(CastingUtils.castingMediaId)) {
                        CastingUtils.castingMediaId = startingFilmId;
                    }
                    if (isMainMediaId) {
                        playIndexPosition--;
                    } else {
                        playIndexPosition = listCompareRelatedVideosId.indexOf(CastingUtils.castingMediaId);
                    }

                    Log.d(TAG, "Cast Index " + playIndexPosition);
                    if (listRelatedVideosDetails != null && listRelatedVideosDetails.size() > 0) {
                        int currentVideoDetailIndex = getCurrentIndex(listRelatedVideosDetails, CastingUtils.castingMediaId);
                        if (currentVideoDetailIndex < listRelatedVideosDetails.size())
                            binderPlayScreen.setContentData(listRelatedVideosDetails.get(currentVideoDetailIndex));
                    }

                    binderPlayScreen.setCurrentPlayingVideoIndex(playIndexPosition);
                    if (playIndexPosition < listCompareRelatedVideosId.size()) {
                        appCMSPresenterComponenet.playNextVideo(binderPlayScreen,
                                binderPlayScreen.getCurrentPlayingVideoIndex());
                    }

                    CastingUtils.castingMediaId = "";

                    if (callBackRemoteListener != null)
                        callBackRemoteListener.onApplicationDisconnected();
                }

            }
        };
    }


    private void initProgressListeners() {

        progressListener = new RemoteMediaClient.ProgressListener() {
            @Override
            public void onProgressUpdated(long remoteCastProgress, long totalCastDuration) {
                castCurrentDuration = remoteCastProgress / 1000;
                try {
                    if (castCurrentDuration % 30 == 0) {
                        String currentRemoteMediaId = CastingUtils.getRemoteMediaId(mAppContext);
                        appCMSPresenterComponenet.updateWatchedTime(currentRemoteMediaId,
                                castCurrentDuration);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private void callRelatedVideoData() {
        String filmIds = "";
        if (listRelatedVideosId != null && listRelatedVideosId.size() >= 5) {
            List<String> subList = listRelatedVideosId.subList(0, 5);
            filmIds = TextUtils.join(",", subList);
            subList.clear();

        } else if (listRelatedVideosId != null && listRelatedVideosId.size() > 0) {
            filmIds = TextUtils.join(",", listRelatedVideosId);
            listRelatedVideosId.clear();
        }

        Log.d(TAG, "Film Ids-" + filmIds);

        appCMSPresenterComponenet.getRelatedMedia(filmIds, new Action1<AppCMSVideoDetail>() {
            @Override
            public void call(AppCMSVideoDetail relatedMediaVideoDetails) {
                if (listRelatedVideosDetails == null) {
                    listRelatedVideosDetails = relatedMediaVideoDetails.getRecords();
                } else {
                    listRelatedVideosDetails.addAll(relatedMediaVideoDetails.getRecords());
                }

                if (listRelatedVideosId != null && listRelatedVideosId.size() > 0) {
                    callRelatedVideoData();
                } else {
                    castMediaListToRemoteLocation();
                    Log.d(TAG, "Cast Media List ");
                }
            }
        });
    }

    private void castMediaListToRemoteLocation() {
        CastingUtils.isMediaQueueLoaded = true;
        if (getRemoteMediaClient() != null) {
            MediaQueueItem[] queueItemsArray = CastingUtils.BuildCastingQueueItems(listRelatedVideosDetails, appName, listCompareRelatedVideosId);
            getRemoteMediaClient().queueLoad(queueItemsArray, currentPlayingIndex,
                    MediaStatus.REPEAT_MODE_REPEAT_OFF, currentMediaPosition, null);
            getRemoteMediaClient().addListener(remoteListener);
            getRemoteMediaClient().addProgressListener(progressListener, 1000);
        }
    }


    private class MyMediaRouterCallback extends MediaRouter.Callback {
        @Override
        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
            Log.w(TAG, "MyMediaRouterCallback-onRouteAdded ");
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
            Log.w(TAG, "MyMediaRouterCallback-onRouteRemoved ");
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

    private RemoteMediaClient getRemoteMediaClient() {
        CastSession castSession = CastContext.getSharedInstance(mAppContext).getSessionManager()
                .getCurrentCastSession();
        if (castSession == null || !castSession.isConnected()) {
            Log.w(TAG, "Trying to get a RemoteMediaClient when no CastSession is started.");
            return null;
        }
        return castSession.getRemoteMediaClient();
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

    public void castingLogout() {
        if (CastContext.getSharedInstance(mAppContext).getSessionManager() != null) {

            try {
                if (CastContext.getSharedInstance(mAppContext).getSessionManager() != null) {
                    CastContext.getSharedInstance(mAppContext).getSessionManager().removeSessionManagerListener(mSessionManagerListener, CastSession.class);
                }

                CastContext.getSharedInstance(mAppContext).getSessionManager().getCurrentCastSession().getRemoteMediaClient().removeListener(remoteListener);

                mSessionManagerListener = null;
                CastContext.getSharedInstance(mAppContext).getSessionManager().endCurrentSession(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopPlayback() {
        if (getRemoteMediaClient() != null) {
            getRemoteMediaClient().stop();
        }
    }
}

