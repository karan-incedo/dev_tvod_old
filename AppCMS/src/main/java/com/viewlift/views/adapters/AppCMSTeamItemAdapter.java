package com.viewlift.views.adapters;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sandeep.singh on 11/7/2017.
 */

public class AppCMSTeamItemAdapter extends RecyclerView.Adapter<AppCMSTeamItemAdapter.ViewHolder> {

    private static final String TAG = "AppCMSTeamItemAdapter";

    private final Resources resources;
    private final NavigationPrimary navigationTabBar;
    private final AppCMSPresenter appCMSPresenter;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final int textColor;
    private boolean userLoggedIn;
    private boolean userSubscribed;
    private int numPrimaryItems;
    private int numUserItems;
    private int numFooterItems;
    private boolean itemSelected;
    private int numItemClickedPosition = -1;

    public AppCMSTeamItemAdapter(List<NavigationPrimary> navigationTabBarList,
                                 AppCMSPresenter appCMSPresenter,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 boolean userLoggedIn,
                                 boolean userSubscribed,
                                 int textColor) {

        this.appCMSPresenter = appCMSPresenter;
        this.navigationTabBar = appCMSPresenter.getPageTeamNavigationPage(navigationTabBarList);
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.userLoggedIn = userLoggedIn;
        this.userSubscribed = userSubscribed;
        this.textColor = textColor;
        this.resources = appCMSPresenter.getCurrentActivity().getResources();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_item, parent,
                false);
        return new AppCMSTeamItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (navigationTabBar != null) {
            RecyclerView.LayoutParams parentParams=(RecyclerView.LayoutParams) holder.parentLayout.getLayoutParams();
            parentParams.setMargins(0,
                    BaseView.dpToPx(R.dimen.app_cms_team_list_view_margin_top, holder.itemView.getContext()),
                    0, 0);
            holder.parentLayout.setLayoutParams(parentParams);
            NavigationPrimary navigationItem = navigationTabBar.getItems().get(position);
            holder.navItemLabel.setText(navigationItem.getTitle());

            if (navigationItem.getIcon().contains("http://") || navigationItem.getIcon().contains("https://")) {
                Glide.with(holder.itemView.getContext())
                        .load(navigationItem.getIcon())
                        .apply(new RequestOptions().override(100, 100).centerCrop())
                        .into((ImageView) holder.navItemLogo);
                holder.navItemLogo.setVisibility(View.VISIBLE);
            } else {

                int resID = resources.getIdentifier(navigationItem.getIcon().replace("-", "_"), "drawable", appCMSPresenter.getCurrentActivity().getPackageName());
                holder.navItemLogo.setImageDrawable(resources.getDrawable(resID));
                holder.navItemLogo.setVisibility(View.VISIBLE);
            }


            holder.itemView.setOnClickListener(v -> {
                appCMSPresenter.cancelInternalEvents();
                appCMSPresenter.navigateToTeamDetailPage("bd16d81c-4757-403b-9871-1a18b5963674", navigationItem.getTitle(), false);

//                if (!appCMSPresenter.navigateToPage(navigationItem.getPageId(),
//                        navigationItem.getTitle(),
//                        navigationItem.getUrl(),
//                        false,
//                        true,
//                        false,
//                        true,
//                        false,
//                        null)) {
//                    //Log.e(TAG, "Could not navigate to page with Title: " +
////                                        navigationFooter.getTitle() +
////                                        " Id: " +
////                                        navigationFooter.getPageId());
//                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return navigationTabBar.getItems().size();
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

    public void setUserSubscribed(boolean userSubscribed) {
        this.userSubscribed = userSubscribed;
    }

    public boolean isItemSelected() {
        return itemSelected;
    }

    public void setItemSelected(boolean itemSelected) {
        this.itemSelected = itemSelected;
    }

    public int getClickedItemPosition() {
        return numItemClickedPosition;
    }

    public void setClickedItemPosition(int itemSelectedPosition) {
        this.numItemClickedPosition = itemSelectedPosition;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.nav_item_label)
        TextView navItemLabel;

        @BindView(R.id.nav_item_selector)
        View navItemSelector;

        @BindView(R.id.nav_item_logo)
        ImageView navItemLogo;
        @BindView(R.id.parent_layout)
        LinearLayout parentLayout;

        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
            navItemLogo.setVisibility(View.VISIBLE);
        }
    }
}
