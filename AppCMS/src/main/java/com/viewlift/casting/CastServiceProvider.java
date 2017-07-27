package com.viewlift.casting;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.media.MediaRouter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.viewlift.casting.roku.RokuCastingOverlay;
import com.viewlift.casting.roku.RokuDevice;
import com.viewlift.casting.roku.RokuLaunchThreadParams;
import com.viewlift.casting.roku.RokuWrapper;
import com.viewlift.casting.roku.dialog.CastChooserDialog;
import com.viewlift.casting.roku.dialog.CastDisconnectDialog;
import com.viewlift.models.data.appcms.api.AppCMSVideoDetail;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.views.activity.AppCMSPlayVideoActivity;
import com.viewlift.views.binders.AppCMSVideoPageBinder;

import java.util.List;

/**
 * A singleton to manage the different casting options such as chromecast and roku , on different activities.
 * google cast and roku instances creates single time here and on activity change instance of activity and cast icon view pass here.
 * and update the cast icon view on basis of casting status from remote devices
 */

public class CastServiceProvider {
    private String TAG = "CastServiceProvider";
    static FragmentActivity mActivity;
    static ImageButton mMediaRouteButton;
    public static CastHelper mCastHelper;
    private RokuWrapper rokuWrapper;
    static CastChooserDialog castChooserDialog;
    private CastSession mCastSession;
    static AnimationDrawable castAnimDrawable;
    static CastDisconnectDialog castDisconnectDialog;
    private ILaunchRemoteMedia callRemoteMediaPlayback;
    private static CastServiceProvider objMain;
    private Context mContext;

    public CastServiceProvider(Context context) {
        this.mContext = context;
        setCasting();
    }

    public static synchronized CastServiceProvider getInstance(Context context) {
        if (objMain == null) {
            objMain = new CastServiceProvider(context);
        }
        return objMain;
    }


    public void setCasting() {
        initChromecast();
        initRoku();
    }

    private void initChromecast() {
        mCastHelper = CastHelper.getInstance(mContext);
        mCastHelper.initCastingObj();
        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(mContext).getSessionManager()
                    .getCurrentCastSession();
        }
        mCastHelper.setCallBackListener(callBackCastHelper);
        mCastHelper.setCastSessionManager();
    }

    private void initRoku() {
        rokuWrapper = RokuWrapper.getInstance();
        rokuWrapper.setListener(callBackRokuDiscoveredDevices);

    }

    /*
    set the image button for chromecastmedia view and current activity instance
    onLaunchRemotePLayback get from player screen as media need to play on player screen only
    */
    public void setActivityInstance(FragmentActivity mActivity, ImageButton mediaRouterView) {
        this.mActivity = mActivity;
        this.mMediaRouteButton = mediaRouterView;
        mCastHelper.setInstance(mActivity);
    }

    public void onActivityResume() {

        castAnimDrawable = (AnimationDrawable) mMediaRouteButton.getDrawable();
        castAnimDrawable.start();
        refreshCastMediaIcon();
        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(mActivity).getSessionManager()
                    .getCurrentCastSession();
        }

        createMediaChooserDialog();

        if (mCastHelper.mMediaRouter != null && mCastHelper.mMediaRouter.getSelectedRoute().isDefault()) {
            Log.d(this.getClass().getName(), "This is a default route");
            mCastHelper.mSelectedDevice = null;
        } else if (mCastHelper.mMediaRouter != null && mCastHelper.mMediaRouter.getSelectedRoute().getConnectionState()
                == MediaRouter.RouteInfo.CONNECTION_STATE_CONNECTED) {
            mCastHelper.isCastDeviceAvailable = true;
            mCastHelper.mSelectedDevice = CastDevice.getFromBundle(mCastHelper.mMediaRouter.getSelectedRoute().getExtras());
        }

        startRokuDiscovery();
        refreshCastMediaIcon();
    }

    //if user comes from player screen and Remote devices already connected launch remote playback
    public boolean playRemotePlaybackIfCastConnected() {
        boolean isConnected = false;
        if (mCastHelper.isRemoteDeviceConnected() && mActivity instanceof AppCMSPlayVideoActivity) {
            mCastHelper.openRemoteController();
            launchChromecastRemotePlayback();
            isConnected = true;
            stopRokuDiscovery();
        } else if (rokuWrapper.isRokuConnected() && mActivity instanceof AppCMSPlayVideoActivity) {
            launchRokuPlaybackLocation();
            setRokuPlayScreen();
            isConnected = true;
        }
        return isConnected;
    }

    public boolean isCastingConnected() {
        boolean isConnected = false;
        if (mCastHelper.isRemoteDeviceConnected() && rokuWrapper.isRokuConnected()) {
            isConnected = true;
        }
        return isConnected;
    }

    private void createMediaChooserDialog() {
        castChooserDialog = new CastChooserDialog(mActivity, callBackRokuMediaSelection);
        mCastHelper.routes.clear();
        if (mCastHelper.mMediaRouter != null)
            mCastHelper.routes.addAll(mCastHelper.mMediaRouter.getRoutes());

        mCastHelper.routes.addAll(rokuWrapper.getRokuDevices());
        castChooserDialog.setRoutes(mCastHelper.routes);
    }


    private void startRokuDiscovery() {
        if (CastingUtils.ROKU_APP_NAME != null && !TextUtils.isEmpty(CastingUtils.ROKU_APP_NAME))
            rokuWrapper.startDiscoveryTimer();
    }

    private void stopRokuDiscovery() {
        if (rokuWrapper.isRokuDiscoveryTimerRunning()) {
            rokuWrapper.stopDiscoveryTimer();
        }
    }

    public void onAppDestroy() {
        mCastHelper.removeCastSessionManager();
        mCastHelper.removeCallBackListener(null);
        mCastHelper.removeInstance();
        rokuWrapper.removeListener();
        stopRokuDiscovery();
    }


    public void launchChromecastRemotePlayback() {
        if (callRemoteMediaPlayback != null) {
            callRemoteMediaPlayback.setRemotePlayBack();
        }
    }

    /**
     * callBackCastHelper gets the calls related to chormecast devices selections
     */
    CastHelper.Callback callBackCastHelper = new CastHelper.Callback() {
        @Override
        public void onApplicationConnected() {
            if (mActivity != null && mActivity instanceof AppCMSPlayVideoActivity) {
                launchChromecastRemotePlayback();
            }
            stopRokuDiscovery();
        }

        @Override
        public void onApplicationDisconnected() {
            startRokuDiscovery();
        }

        @Override
        public void onRouterAdded(MediaRouter mMediaRouter, MediaRouter.RouteInfo route) {
            List<MediaRouter.RouteInfo> c_routes = mMediaRouter.getRoutes();
            mCastHelper.routes.clear();
            mCastHelper.routes.addAll(c_routes);
            mCastHelper.routes.addAll(rokuWrapper.getRokuDevices());
            mCastHelper.onFilterRoutes(mCastHelper.routes);
            mCastHelper.isCastDeviceAvailable = mCastHelper.routes.size() > 0;
            refreshCastMediaIcon();
            castChooserDialog.setRoutes(mCastHelper.routes);
        }

        @Override
        public void onRouterRemoved(MediaRouter mMediaRouter, MediaRouter.RouteInfo info) {
            for (int i = 0; i < mCastHelper.routes.size(); i++) {
                if (mCastHelper.routes.get(i) instanceof MediaRouter.RouteInfo) {
                    MediaRouter.RouteInfo routeInfo = (MediaRouter.RouteInfo) mCastHelper.routes.get(i);
                    if (routeInfo.equals(info)) {
                        mCastHelper.routes.remove(i);
                        refreshCastMediaIcon();
                        break;
                    }
                }
            }
            castChooserDialog.setRoutes(mCastHelper.routes);
            mCastHelper.isCastDeviceAvailable = mCastHelper.routes.size() > 0;
            refreshCastMediaIcon();
        }

        @Override
        public void onRouterSelected(MediaRouter mMediaRouter, MediaRouter.RouteInfo info) {

            mCastHelper.chromeCastConnecting = true;
            mCastHelper.mSelectedDevice = CastDevice.getFromBundle(info.getExtras());
            mCastHelper.isCastDeviceConnected = true;
            refreshCastMediaIcon();
            if (rokuWrapper.isRokuDiscoveryTimerRunning()) {
                rokuWrapper.stopDiscoveryTimer();
            }
        }

        @Override
        public void onRouterUnselected(MediaRouter mMediaRouter, MediaRouter.RouteInfo info) {

            mCastHelper.mSelectedDevice = null;
            refreshCastMediaIcon();
            if (!rokuWrapper.isRokuDiscoveryTimerRunning()) {
                startRokuDiscovery();
            }
        }
    };


    /**
     * callBackRokuMediaSelection gets the calls related to selected roku devices
     */
    CastChooserDialog.CastChooserDialogEventListener callBackRokuMediaSelection = new CastChooserDialog.CastChooserDialogEventListener() {
        @Override
        public void onRokuDeviceSelected(RokuDevice selectedRokuDevice) {
            mMediaRouteButton.setOnClickListener(null);
            castAnimDrawable.start();
            rokuWrapper.setSelectedRokuDevice(selectedRokuDevice);
            if (mActivity != null)
                launchRokuPlaybackLocation();
        }
    };


    /**
     * launchRokuPlaybackLocation launch the media files on selected Roku device
     */
    private void launchRokuPlaybackLocation() {
        String userId = null;
        String contentId = "0000015c-a2b4-d7a8-a3dc-b6f6f6ad0000";

        if (contentId != null && mActivity instanceof AppCMSPlayVideoActivity) {
            try {
                rokuWrapper.sendFilmLaunchRequest(
                        contentId,
                        RokuLaunchThreadParams.CONTENT_TYPE_FILM,
                        userId);
            } catch (Exception e) {
                rokuWrapper.sendAppLaunchRequest();
                e.printStackTrace();
            }
        } else {
            rokuWrapper.sendAppLaunchRequest();
        }
    }


    /**
     * callBackRokuDiscoveredDevices gets the calls related to roku devices discovery
     */
    RokuWrapper.RokuWrapperEventListener callBackRokuDiscoveredDevices = new RokuWrapper.RokuWrapperEventListener() {
        @Override
        public void onRokuDiscovered(List<RokuDevice> rokuDeviceList) {
            Log.w(TAG, "MyMediaRouterCallback-onRokuDiscovered  " + rokuWrapper.getRokuDevices());

            mCastHelper.routes.clear();
            if (mCastHelper.mMediaRouter != null)
                mCastHelper.routes.addAll(mCastHelper.mMediaRouter.getRoutes());
            mCastHelper.routes.addAll(rokuWrapper.getRokuDevices());
            mCastHelper.onFilterRoutes(mCastHelper.routes);
            castChooserDialog.setRoutes(mCastHelper.routes);
            mCastHelper.isCastDeviceAvailable = mCastHelper.routes.size() > 0;
            refreshCastMediaIcon();
        }


        @Override
        public void onRokuConnected(RokuDevice selectedRokuDevice) {
            rokuWrapper.setRokuConnected(true);

            refreshCastMediaIcon();
            if (rokuWrapper.isRokuDiscoveryTimerRunning()) {
                rokuWrapper.stopDiscoveryTimer();
            }
            setRokuPlayScreen();
        }

        @Override
        public void onRokuStopped() {
            rokuWrapper.setRokuConnected(false);
            refreshCastMediaIcon();

            if (!rokuWrapper.isRokuDiscoveryTimerRunning()) {
                startRokuDiscovery();
            }
        }

        @Override
        public void onRokuConnectedFailed(String obj) {
        }
    };


    private void setRokuPlayScreen() {
        if (mActivity instanceof AppCMSPlayVideoActivity)
            mActivity.startActivity(new Intent(mActivity, RokuCastingOverlay.class));
    }


    /**
     * refreshCastMediaIcon invalidate the media icon view on the basis of casting status i.e disconnected/Conntected
     */
    private void refreshCastMediaIcon() {
        if (mMediaRouteButton == null)
            return;

        mMediaRouteButton.setVisibility(mCastHelper.isCastDeviceAvailable ? View.VISIBLE : View.INVISIBLE);

        if (!mCastHelper.isCastDeviceAvailable && castChooserDialog != null && castChooserDialog.isShowing()) {
            castChooserDialog.dismiss();
        }
        if (mCastHelper.isCastDeviceAvailable) {
            if (rokuWrapper.isRokuConnected() || mCastHelper.isRemoteDeviceConnected() || (mCastHelper.mSelectedDevice != null && mCastHelper.mMediaRouter != null)) {
                castAnimDrawable.stop();
                castAnimDrawable.selectDrawable(4);
            } else {
                castAnimDrawable.stop();
                castAnimDrawable.selectDrawable(0);
            }
        }

        mMediaRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                castDisconnectDialog = new CastDisconnectDialog(mActivity, callBackRokuDiscoveredDevices);

                if (mCastHelper.mSelectedDevice == null && !rokuWrapper.isRokuConnected()) {
                    startRokuDiscovery();

                    castChooserDialog.setRoutes(mCastHelper.routes);
                    castChooserDialog.show();
                } else if (mCastHelper.mSelectedDevice != null && mCastHelper.mMediaRouter != null) {
                    castDisconnectDialog.setToBeDisconnectDevice(mCastHelper.mMediaRouter);
                    castDisconnectDialog.show();
                } else if (rokuWrapper.isRokuConnected()) {
                    castDisconnectDialog.setToBeDisconnectDevice(rokuWrapper.getSelectedRokuDevice());
                    castDisconnectDialog.show();
                }
            }
        });
    }


    public void setRemotePlaybackCallback(ILaunchRemoteMedia onLaunchRemotePLayback) {
        this.callRemoteMediaPlayback = onLaunchRemotePLayback;
    }

    public interface ILaunchRemoteMedia {

        void setRemotePlayBack();

    }

}

