package com.viewlift.views.adapters;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.Primary;
import com.viewlift.models.data.appcms.ui.android.User;
import com.viewlift.presenters.AppCMSPresenter;

import org.w3c.dom.Text;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/30/17.
 */

public class AppCMSNavItemsAdapter extends RecyclerView.Adapter<AppCMSNavItemsAdapter.ViewHolder> {
    private static final String TAG = "AppCMSNavItemsAdapter";

    private static final int NUM_NAV_ITEMS = 2;

    private final Navigation navigation;
    private final AppCMSPresenter appCMSPresenter;
    private final OnCloseNavAction onCloseNavAction;
    private boolean userLoggedIn;

    public interface OnCloseNavAction {
        void closeNavAction();
    }

    public AppCMSNavItemsAdapter(OnCloseNavAction onCloseNavAction,
                                 Navigation navigation,
                                 boolean userLoggedIn,
                                 AppCMSPresenter appCMSPresenter) {
        this.onCloseNavAction = onCloseNavAction;
        this.navigation = navigation;
        this.userLoggedIn = userLoggedIn;
        this.appCMSPresenter = appCMSPresenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nav_item,
                        viewGroup,
                        false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (i < navigation.getPrimary().size()) {
            final Primary primary = navigation.getPrimary().get(i);
            viewHolder.navItemLabel.setText(primary.getTitle());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Navigating to page with Title: " + primary.getTitle());
                    if (!appCMSPresenter.navigateToPage(primary.getPageId(),
                            primary.getTitle(),
                            true,
                            null)) {
                        Log.e(TAG, "Could not navigate to page with Title: " +
                                primary.getTitle() +
                                " Id: " +
                                primary.getPageId());
                    } else if (onCloseNavAction != null ){
                        onCloseNavAction.closeNavAction();
                    }
                }
            });
        } else if (userLoggedIn) {
            final User user = navigation.getUser().get(i - navigation.getPrimary().size());
            viewHolder.navItemLabel.setText(user.getTitle());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!appCMSPresenter.navigateToPage(user.getPageId(),
                            user.getTitle(),
                            true,
                            null)) {
                        Log.e(TAG, "Could not navigate to page with Title: " +
                                user.getTitle() +
                                " Id: " +
                                user.getPageId());
                    } else if (onCloseNavAction != null ){
                        onCloseNavAction.closeNavAction();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return NUM_NAV_ITEMS;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView navItemIcon;
        TextView navItemLabel;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.navItemLabel = (TextView) itemView.findViewById(R.id.nav_item_label);
        }
    }
}
