package com.viewlift.casting.roku.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.media.MediaRouter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.cast.framework.CastContext;

import java.util.ArrayList;
import java.util.List;

import com.viewlift.R;


import static android.support.v7.media.MediaRouter.RouteInfo.CONNECTION_STATE_CONNECTED;
import static android.support.v7.media.MediaRouter.RouteInfo.CONNECTION_STATE_CONNECTING;

public class CastChooserDialog extends Dialog {
    private Activity activity;
    private ListView mListView;
    private RouteAdapter mAdapter;
    private List<Object> routes = new ArrayList<>();
    private String TAG = "CastChooserDialog";
    CastChooserDialogEventListener callBackCastChoose;

    public CastChooserDialog(@NonNull Activity activity, CastChooserDialogEventListener callBackCastChoose) {
        super(activity, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        this.activity = activity;
        this.callBackCastChoose = callBackCastChoose;
    }
    public void setInstance(@NonNull Activity activity) {
        this.activity = activity;
    }
    public CastChooserDialog(@NonNull Context context,
                             @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected CastChooserDialog(@NonNull Context context,
                                boolean cancelable,
                                @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setRoutes(List<Object> routes) {
        this.routes = routes;
        Log.d("", "*******************setRoutes***************");
        for (Object obj : routes) {
            Log.d(TAG, ((MediaRouter.RouteInfo) obj).getName());
        }
        if (mAdapter != null) {
            activity.runOnUiThread(new Runnable(){
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mr_chooser_dialog);
        mListView = findViewById(R.id.mr_chooser_list);
        mListView.setOnItemClickListener(chooserDialogItemClickListener);
        mAdapter = new RouteAdapter(getContext(), routes);
        mListView.setAdapter(mAdapter);
    }

    private final class RouteAdapter extends ArrayAdapter<Object> {
        private final LayoutInflater mInflater;

        public RouteAdapter(Context context, List<Object> routes) {
            super(context, 0, routes);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(R.layout.mr_chooser_list_item, parent, false);
            }


            TextView text1 = view.findViewById(R.id.mr_chooser_route_name);
            TextView text2 = view.findViewById(R.id.mr_chooser_route_desc);
            text1.setTextSize(15);
            text2.setTextSize(12);
            if (getItem(position) instanceof MediaRouter.RouteInfo) {
                MediaRouter.RouteInfo item = (MediaRouter.RouteInfo) getItem(position);
                String description = item.getDescription();
                text1.setText(item.getName());
                boolean isConnectedOrConnecting =
                        item.getConnectionState() == CONNECTION_STATE_CONNECTED
                                || item.getConnectionState() == CONNECTION_STATE_CONNECTING;
                if (isConnectedOrConnecting && !TextUtils.isEmpty(description)) {
                    text1.setGravity(Gravity.BOTTOM);
                    text2.setVisibility(View.VISIBLE);
                    text2.setText(description);
                } else {
                    text1.setGravity(Gravity.CENTER_VERTICAL);
                    text2.setVisibility(View.GONE);
                    text2.setText("");
                }

            }

            ImageView iconView = view.findViewById(R.id.mr_chooser_route_icon);
            if (iconView != null) {
                iconView.setImageDrawable(getContext().getDrawable(R.drawable.ic_tv_24dp));
            }
            return view;
        }
    }

    private AdapterView.OnItemClickListener chooserDialogItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mAdapter.getItem(position) instanceof MediaRouter.RouteInfo) {
                CastContext.getSharedInstance(activity).getSessionManager().endCurrentSession(true);

                MediaRouter.RouteInfo route = (MediaRouter.RouteInfo) mAdapter.getItem(position);
                if (route.isEnabled()) {
                    callBackCastChoose.onChromeCastDeviceSelect();

                    route.select();
                    dismiss();
                }
            }
        }
    };


    public interface CastChooserDialogEventListener {
        void onChromeCastDeviceSelect();
    }
}
