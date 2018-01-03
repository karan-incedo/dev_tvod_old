package com.viewlift.tv.views.fragment;

import android.app.Activity;
import android.content.Intent;
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

import java.util.List;

/**
 * Created by anas.azeem on 12/31/2017.
 * Owned by ViewLift, NYC
 */

public class SwitchSeasonsDialogFragment extends AbsDialogFragment {
    private static List<Season_> mSeasons;
    private RecyclerView rvSwitchSeasons;
    private AppCMSPresenter appCMSPresenter;
    private Activity mContext;

    public SwitchSeasonsDialogFragment() {
        super();
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

        rvSwitchSeasons = mView.findViewById(R.id.rv_switch_seasons);
        LinearLayoutManager layout = new LinearLayoutManager(mContext);
        layout.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvSwitchSeasons.setLayoutManager(layout);
        rvSwitchSeasons.setAdapter(new SwitchSeasonsAdapter());
        return mView;
    }

    public static SwitchSeasonsDialogFragment newInstance(List<Season_> seasons) {
        mSeasons = seasons;
        return new SwitchSeasonsDialogFragment();
    }

    private class SwitchSeasonsAdapter extends RecyclerView.Adapter<SwitchSeasonsViewHolder> {
        @Override
        public SwitchSeasonsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            String textColor = Utils.getTextColor(getActivity(), appCMSPresenter);
            String backGroundColor = Utils.getBackGroundColor(getActivity(), appCMSPresenter);
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
            holder.item.setOnClickListener(v -> {
                Intent updateSeasonIntent =
                        new Intent(AppCMSPresenter.SWITCH_SEASON_ACTION);
                updateSeasonIntent.putExtra(appCMSPresenter.getCurrentActivity().getString(R.string.app_cms_selected_season_key),
                        position);
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

        public SwitchSeasonsViewHolder(View itemView) {
            super(itemView);
            item = (Button) itemView;
        }
    }
}
