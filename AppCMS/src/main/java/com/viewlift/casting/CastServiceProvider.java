package com.viewlift.casting;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.media.MediaRouter;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.casting.roku.dialog.CastChooserDialog;
import com.viewlift.casting.roku.dialog.CastDisconnectDialog;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.activity.AppCMSPlayVideoActivity;

import java.util.List;

/**
 * A singleton to manage the different casting options such as chromecast and roku , on different activities.
 * google cast and roku instances creates single time here and on activity change instance of activity and cast icon view pass here.
 * and update the cast icon view on basis of casting status from remote devices
 */

public class CastServiceProvider {
    private String TAG = "CastServiceProvider";
    private FragmentActivity mActivity;
    private ImageButton mMediaRouteButton;
    private CastHelper mCastHelper;
    private boolean isHomeScreen=false;
    private CastChooserDialog castChooserDialog;
    private CastSession mCastSession;
    private AnimationDrawable castAnimDrawable;
    private CastDisconnectDialog castDisconnectDialog;
    private ILaunchRemoteMedia callRemoteMediaPlayback;
    private static CastServiceProvider objMain;
    private Context mContext;
    private AppCMSPresenter appCMSPresenter;
    private ShowcaseView mShowCaseView;

    private CastServiceProvider(Activity activity) {
        this.mContext = activity;
        setCasting();

        appCMSPresenter = ((AppCMSApplication) activity.getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
    }

    public static synchronized CastServiceProvider getInstance(Activity activity) {
        if (objMain == null) {
            objMain = new CastServiceProvider(activity);
        }
        return objMain;
    }


    public void setCasting() {
        initChromecast();
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

    /*
    set the image button for chromecastmedia view and current activity instance
    onLaunchRemotePlayback get from player screen as media need to play on player screen only
    */
    public void setActivityInstance(FragmentActivity mActivity, ImageButton mediaRouterView) {
        this.mActivity = mActivity;
        this.mMediaRouteButton = mediaRouterView;
        mCastHelper.setInstance(mActivity);
        mMediaRouteButton.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.anim_cast, null));
        castAnimDrawable = (AnimationDrawable) mMediaRouteButton.getDrawable();

    }

    public void onActivityResume() {

        refreshCastMediaIcon();
        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(mActivity).getSessionManager()
                    .getCurrentCastSession();
        }
        mCastHelper.setCastSessionManager();

        createMediaChooserDialog();
        mCastHelper.setCastDiscovery();

        if (mCastHelper.mMediaRouter != null && mCastHelper.mMediaRouter.getSelectedRoute().isDefault()) {
            Log.d(TAG, "This is a default route");
            mCastHelper.mSelectedDevice = null;
        } else if (mCastHelper.mMediaRouter != null && mCastHelper.mMediaRouter.getSelectedRoute().getConnectionState()
                == MediaRouter.RouteInfo.CONNECTION_STATE_CONNECTED) {
            mCastHelper.isCastDeviceAvailable = true;
            mCastHelper.mSelectedDevice = CastDevice.getFromBundle(mCastHelper.mMediaRouter.getSelectedRoute().getExtras());
        }
    }

    //if user comes from player screen and Remote devices already connected launch remote playback
    public boolean playChromeCastPlaybackIfCastConnected() {
        boolean isConnected = false;
        if (mCastHelper.isRemoteDeviceConnected()) {
            launchChromecastRemotePlayback(CastingUtils.CASTING_MODE_CHROMECAST);
            isConnected = true;
        }
        return isConnected;
    }

    public boolean isCastingConnected() {
        boolean isConnected = false;
        if (mCastHelper.isCastDeviceAvailable && mCastHelper.isRemoteDeviceConnected() || (mCastHelper.mSelectedDevice != null && mCastHelper.mMediaRouter != null)) {
            isConnected = true;
        }
        return isConnected;
    }

    private void createMediaChooserDialog() {
        castChooserDialog = new CastChooserDialog(mActivity, callBackRokuMediaSelection);
        mCastHelper.routes.clear();
        if (mCastHelper.mMediaRouter != null) {
            mCastHelper.routes.addAll(mCastHelper.mMediaRouter.getRoutes());
        }

        mCastHelper.onFilterRoutes(mCastHelper.routes);
        castChooserDialog.setRoutes(mCastHelper.routes);
    }

    public void onAppDestroy() {
        mCastHelper.removeCastSessionManager();
        mCastHelper.removeCallBackListener(null);
        mCastHelper.removeInstance();
    }


    public void launchChromecastRemotePlayback(int castingModeChromecast) {
        if (callRemoteMediaPlayback != null) {
            callRemoteMediaPlayback.setRemotePlayBack(castingModeChromecast);
        }
    }

    /**
     * callBackCastHelper gets the calls related to chormecast devices selections
     */
    CastHelper.Callback callBackCastHelper = new CastHelper.Callback() {
        @Override
        public void onApplicationConnected() {
            if (mActivity != null && mActivity instanceof AppCMSPlayVideoActivity) {
                launchChromecastRemotePlayback(CastingUtils.CASTING_MODE_CHROMECAST);
            }
        }

        @Override
        public void onApplicationDisconnected() {
        }

        @Override
        public void onRouterAdded(MediaRouter mMediaRouter, MediaRouter.RouteInfo route) {
            List<MediaRouter.RouteInfo> c_routes = mMediaRouter.getRoutes();
            mCastHelper.routes.clear();
            mCastHelper.routes.addAll(c_routes);
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
        }

        @Override
        public void onRouterUnselected(MediaRouter mMediaRouter, MediaRouter.RouteInfo info) {
            mCastHelper.mSelectedDevice = null;
            refreshCastMediaIcon();
        }
    };


    /**
     * callBackRokuMediaSelection gets the calls related to selected roku devices
     */
    CastChooserDialog.CastChooserDialogEventListener callBackRokuMediaSelection = new CastChooserDialog.CastChooserDialogEventListener() {
        @Override
        public void onChromeCastDeviceSelect() {
            mMediaRouteButton.setOnClickListener(null);
            castAnimDrawable.start();
        }
    };
    public void isHomeScreen(boolean fromHomePage) {
        isHomeScreen=fromHomePage;
    }


    public void showIntroOverLay() {

        if (mMediaRouteButton != null && mActivity != null && isHomeScreen) {
            new Handler().postDelayed(() -> {
                int textSize = 16;
                float scaledSizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        textSize, mContext.getResources().getDisplayMetrics());

                Target target = new ViewTarget(mMediaRouteButton.getId(), mActivity);
                TextPaint textPaint = new TextPaint();
                textPaint.setColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor()));
                textPaint.setTextSize(scaledSizeInPixels);

                mShowCaseView = new ShowcaseView.Builder(mActivity)
                        .setTarget(target) //Here is where you supply the id of the action bar item you want to display
                        .setContentText(R.string.app_cast_overlay_text)
                        .setContentTextPaint(textPaint)
                        .build();

                mShowCaseView.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);
                mShowCaseView.setShowcaseColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor()));
                mShowCaseView.setEndButtonBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor()));
                mShowCaseView.setEndButtonTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor()));

                mShowCaseView.show();
                mShowCaseView.invalidate();
            }, 500);

        }
    }


    public boolean isOverlayVisible() {
        boolean isVisible = false;
        if (mShowCaseView != null && mShowCaseView.isShowing()) {
            isVisible = true;
            ((ViewGroup) mActivity.getWindow().getDecorView()).removeView(mShowCaseView);
            mShowCaseView.hide();
            mShowCaseView = null;
        }
        return isVisible;
    }

    /**
     * refreshCastMediaIcon invalidate the media icon view on the basis of casting status i.e disconnected/Conntected
     */
    private void refreshCastMediaIcon() {
        if (mMediaRouteButton == null)
            return;

        mMediaRouteButton.setVisibility(mCastHelper.isCastDeviceAvailable ? View.VISIBLE : View.INVISIBLE);

        //Setting the Casting Overlay for Casting
        if (mCastHelper.isCastDeviceAvailable)
            if (!appCMSPresenter.isCastOverLayShown()) {
                appCMSPresenter.setCastOverLay();
                showIntroOverLay();
            }

        if (!mCastHelper.isCastDeviceAvailable && castChooserDialog != null && castChooserDialog.isShowing()) {
            castChooserDialog.dismiss();
        }

        if (mCastHelper.isCastDeviceAvailable) {
            if (mCastHelper.isRemoteDeviceConnected() || (mCastHelper.mSelectedDevice != null && mCastHelper.mMediaRouter != null)) {
                castAnimDrawable.stop();
                Drawable selectedImageDrawable = mActivity.getResources().getDrawable(R.drawable.toolbar_cast_connected, null);
                int fillColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor());
                selectedImageDrawable.setColorFilter(new PorterDuffColorFilter(fillColor, PorterDuff.Mode.MULTIPLY));
                mMediaRouteButton.setImageDrawable(selectedImageDrawable);
            } else {
                castAnimDrawable.stop();
                mMediaRouteButton.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.toolbar_cast_disconnected, null));
            }
        }

        mMediaRouteButton.setOnClickListener(v -> {
            castDisconnectDialog = new CastDisconnectDialog(mActivity);

            if (mCastHelper.mSelectedDevice == null && mActivity != null) {
                castChooserDialog.setRoutes(mCastHelper.routes);
                castChooserDialog.show();
            } else if (mCastHelper.mSelectedDevice != null && mCastHelper.mMediaRouter != null && mActivity != null) {
                castDisconnectDialog.setToBeDisconnectDevice(mCastHelper.mMediaRouter);
                castDisconnectDialog.show();
            }
        });
    }


    public void setRemotePlaybackCallback(ILaunchRemoteMedia onLaunchRemotePLayback) {
        this.callRemoteMediaPlayback = onLaunchRemotePLayback;
    }

    public interface ILaunchRemoteMedia {

        void setRemotePlayBack(int castingModeChromecast);

    }

}

