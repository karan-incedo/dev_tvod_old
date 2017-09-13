package com.viewlift.casting.roku.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.MediaRouteControllerDialog;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.viewlift.casting.CastHelper;


public class CastDisconnectDialog extends MediaRouteControllerDialog implements View.OnClickListener {


    static final int BUTTON_DISCONNECT_RES_ID = android.R.id.button2;
    static final int BUTTON_STOP_RES_ID = android.R.id.button1;
    public static final String CHROMECAST = "chromecast";
    public static final String ROKU = "roku";
    private static final String TAG = CastDisconnectDialog.class.getSimpleName();
    private final Context mContext;
    private String selectedDeviceType = CHROMECAST;
    private Button mDisconnectButton;
    private Button mStopCastingButton;
    private TextView mTitleView;
    private TextView mRouteNameTextView;
    private String type;
    private Object toBeDisconnectDevice;

    public CastDisconnectDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public CastDisconnectDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitleView = findViewById(android.support.v7.mediarouter.R.id.mr_control_title);

        mRouteNameTextView = findViewById(android.support.v7.mediarouter.R.id.mr_name);

        mDisconnectButton = findViewById(BUTTON_DISCONNECT_RES_ID);
        mDisconnectButton.setText(android.support.v7.mediarouter.R.string.mr_controller_disconnect);
        mDisconnectButton.setOnClickListener(this);

        mStopCastingButton = findViewById(BUTTON_STOP_RES_ID);
        mStopCastingButton.setText(android.support.v7.mediarouter.R.string.mr_controller_stop_casting);
        mStopCastingButton.setOnClickListener(this);

        if (toBeDisconnectDevice != null) {
            if (selectedDeviceType.equals(CHROMECAST)) {
                mRouteNameTextView.setText(((MediaRouter) toBeDisconnectDevice).getSelectedRoute().getName());
            }
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setToBeDisconnectDevice(Object toBeDisconnectDevice) {
        this.toBeDisconnectDevice = toBeDisconnectDevice;
        if (toBeDisconnectDevice instanceof MediaRouter) {
            selectedDeviceType = CHROMECAST;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow");
    }

    public Object getToBeDisconnectDevice() {
        return toBeDisconnectDevice;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == BUTTON_STOP_RES_ID || id == BUTTON_DISCONNECT_RES_ID) {
            if (toBeDisconnectDevice != null) {
                if (selectedDeviceType.equals(CHROMECAST)) {
                    if (((MediaRouter) toBeDisconnectDevice).getSelectedRoute().isSelected()) {

                        ((MediaRouter) toBeDisconnectDevice).unselect(id == BUTTON_STOP_RES_ID ?
                                MediaRouter.UNSELECT_REASON_STOPPED :
                                MediaRouter.UNSELECT_REASON_DISCONNECTED);
                        CastHelper.getInstance(mContext).isCastDeviceConnected = false;
                        System.out.println("unselecte");
                    }
                }
            }
            dismiss();
        }
    }
}
