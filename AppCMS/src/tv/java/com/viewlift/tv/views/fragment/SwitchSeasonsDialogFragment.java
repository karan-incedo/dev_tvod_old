package com.viewlift.tv.views.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.Season_;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.views.binders.AppCMSSwitchSeasonBinder;

import java.util.List;

/**
 * Created by anas.azeem on 12/31/2017.
 * Owned by ViewLift, NYC
 */

public class SwitchSeasonsDialogFragment extends AbsDialogFragment {
    private List<Season_> mSeasons;
    private static AppCMSSwitchSeasonBinder mAppCMSSwitchSeasonBinder;
    private AppCMSPresenter appCMSPresenter;
    private Activity mContext;
    private BroadcastReceiver broadcastReceiver;
    private static int selectedSeasonIndex;
    private SwitchSeasonsAdapter switchSeasonsAdapter;

    public SwitchSeasonsDialogFragment() {
        super();
        mSeasons = mAppCMSSwitchSeasonBinder.getSeasonList();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(AppCMSPresenter.SWITCH_SEASON_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(AppCMSPresenter.SWITCH_SEASON_ACTION)) {
                    if (intent.getExtras() != null
                            && intent.getExtras().getBundle("app_cms_season_selector_key") != null
                            && intent.getExtras().getBundle("app_cms_season_selector_key").getBinder("app_cms_episode_data") != null
                            && intent.getExtras().getBundle("app_cms_season_selector_key").getBinder("app_cms_episode_data") instanceof AppCMSSwitchSeasonBinder) {
                        AppCMSSwitchSeasonBinder appCMSSwitchSeasonBinder = (AppCMSSwitchSeasonBinder) intent.getExtras().getBundle("app_cms_season_selector_key").getBinder("app_cms_episode_data");
                        if (appCMSSwitchSeasonBinder != null) {
                            selectedSeasonIndex = appCMSSwitchSeasonBinder.getSelectedSeasonIndex();
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
//        int width = 1920;
//        int height = 1080;
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.tv_dialog_width_key), width);
        bundle.putInt(getString(R.string.tv_dialog_height_key), height);
        super.onActivityCreated(bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.switch_seasons_overlay, container, false);

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
        mContext = appCMSPresenter.getCurrentActivity();

        RecyclerView rvSwitchSeasons = mView.findViewById(R.id.rv_switch_seasons);
        LinearLayoutManager layout = new LinearLayoutManager(mContext);
        layout.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvSwitchSeasons.setLayoutManager(layout);
        switchSeasonsAdapter = new SwitchSeasonsAdapter();
        rvSwitchSeasons.setAdapter(switchSeasonsAdapter);
        return mView;
    }

    public static SwitchSeasonsDialogFragment newInstance(AppCMSSwitchSeasonBinder appCMSSwitchSeasonBinder) {
        mAppCMSSwitchSeasonBinder = appCMSSwitchSeasonBinder;
        return new SwitchSeasonsDialogFragment();
    }

    private class SwitchSeasonsAdapter extends RecyclerView.Adapter<SwitchSeasonsViewHolder> {
        @Override
        public SwitchSeasonsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            String textColor = Utils.getTextColor(getActivity(), appCMSPresenter);
            String focusColor = Utils.getFocusColor(getActivity(), appCMSPresenter);

            Component component = new Component();
            component.setFontFamily(getString(R.string.app_cms_page_font_family_key));
            component.setFontWeight(getString(R.string.app_cms_page_font_semibold_key));
            component.setBorderColor("#ffffff");
            component.setBorderWidth(4);
            Button button = new Button(mContext);
            button.setTextColor(Color.parseColor(textColor));

            button.setBackground(Utils.setButtonBackgroundSelector(getActivity(),
                    Color.parseColor(focusColor),
                    component));

            button.setTypeface(Utils.getTypeFace(getActivity(), appCMSPresenter.getJsonValueKeyMap(), component));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(250, 60);
            layoutParams.setMargins(10, 0, 10, 0);
            button.setLayoutParams(layoutParams);
            button.setTextSize(9);
            return new SwitchSeasonsViewHolder(button);
        }

        @Override
        public void onBindViewHolder(SwitchSeasonsViewHolder holder, int position) {
            holder.item.setText("Season " + (position + 1));
            if (position == selectedSeasonIndex) {
                holder.item.requestFocus();
            }
            holder.item.setOnClickListener(v -> {
                Intent updateSeasonIntent =
                        new Intent(AppCMSPresenter.SWITCH_SEASON_ACTION);
                Bundle bundle = new Bundle();
                mAppCMSSwitchSeasonBinder.setSelectedSeasonIndex(holder.getAdapterPosition());
                bundle.putBinder("app_cms_episode_data", mAppCMSSwitchSeasonBinder);
                updateSeasonIntent.putExtra("app_cms_season_selector_key",
                        bundle);
                appCMSPresenter.getCurrentActivity().sendBroadcast(updateSeasonIntent);
                SwitchSeasonsDialogFragment.this.dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return mSeasons.size();
        }
    }

    private class SwitchSeasonsViewHolder extends RecyclerView.ViewHolder {

        Button item;

        SwitchSeasonsViewHolder(View itemView) {
            super(itemView);
            item = (Button) itemView;
        }
    }
}
