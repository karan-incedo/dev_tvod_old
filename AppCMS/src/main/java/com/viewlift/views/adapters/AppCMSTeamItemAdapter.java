package com.viewlift.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sandeep.singh on 11/7/2017.
 */

public class AppCMSTeamItemAdapter extends RecyclerView.Adapter<AppCMSTeamItemAdapter.ViewHolder> {

    private static final String TAG = "AppCMSTeamItemAdapter";

    private final NavigationPrimary navigationPrimary;
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

    public AppCMSTeamItemAdapter(NavigationPrimary navigationPrimary,
                                 AppCMSPresenter appCMSPresenter,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 boolean userLoggedIn,
                                 boolean userSubscribed,
                                 int textColor) {
        this.navigationPrimary = navigationPrimary;
        this.appCMSPresenter = appCMSPresenter;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.userLoggedIn = userLoggedIn;
        this.userSubscribed = userSubscribed;
        this.textColor = textColor;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_item, parent,
                false);
        return new AppCMSTeamItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        navigationPrimary.getItems().get(position);
    }

    @Override
    public int getItemCount() {
        return 0;
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

        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}
