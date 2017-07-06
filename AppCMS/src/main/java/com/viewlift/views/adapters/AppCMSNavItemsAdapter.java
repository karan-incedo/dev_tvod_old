package com.viewlift.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewlift.models.data.appcms.ui.android.Footer;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.Primary;
import com.viewlift.models.data.appcms.ui.android.User;
import com.viewlift.presenters.AppCMSPresenter;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/30/17.
 */

public class AppCMSNavItemsAdapter extends RecyclerView.Adapter<AppCMSNavItemsAdapter.ViewHolder> {
    private static final String TAG = "AppCMSNavItemsAdapter";

    private final Navigation navigation;
    private final AppCMSPresenter appCMSPresenter;
    private final int textColor;
    private boolean userLoggedIn;

    public AppCMSNavItemsAdapter(Navigation navigation,
                                 boolean userLoggedIn,
                                 AppCMSPresenter appCMSPresenter,
                                 int textColor) {
        this.navigation = navigation;
        this.userLoggedIn = userLoggedIn;
        this.appCMSPresenter = appCMSPresenter;
        this.textColor = textColor;
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
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        int indexOffset = 0;
        if (navigation.getPrimary() != null && i < navigation.getPrimary().size()) {
            final Primary primary = navigation.getPrimary().get(i);
            if (primary.getAccessLevels() != null) {
                if ((userLoggedIn && primary.getAccessLevels().getLoggedIn()) ||
                        (!userLoggedIn && primary.getAccessLevels().getLoggedOut())) {
                    viewHolder.navItemLabel.setText(primary.getTitle().toUpperCase());
                    viewHolder.navItemLabel.setTextColor(textColor);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "Navigating to page with Title: " + primary.getTitle());
                            if (!appCMSPresenter.navigateToPage(primary.getPageId(),
                                    primary.getTitle(),
                                    primary.getUrl(),
                                    false,
                                    true,
                                    true,
                                    null)) {
                                Log.e(TAG, "Could not navigate to page with Title: " +
                                        primary.getTitle() +
                                        " Id: " +
                                        primary.getPageId());
                            }
                        }
                    });
                }
            }
        } else {
            if (navigation.getPrimary() != null) {
                indexOffset += navigation.getPrimary().size();
            }

            //user nav
            if (userLoggedIn && navigation.getUser() != null && (i - indexOffset) < navigation.getUser().size()) {
                final User user = navigation.getUser().get(i - indexOffset);

                viewHolder.navItemLabel.setText(user.getTitle().toUpperCase());
                viewHolder.navItemLabel.setTextColor(textColor);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (user.getTitle().equals("Watchlist")) {
                            appCMSPresenter.navigateToWatchlistPage(user.getPageId(),
                                    user.getTitle(), user.getUrl(), true);
                        } else if (user.getTitle().equals("History")) {
                            appCMSPresenter.navigateToHistoryPage(user.getPageId(), user.getTitle(),
                                    user.getUrl(), true);
                        } else {
                            if (!appCMSPresenter.navigateToPage(user.getPageId(),
                                    user.getTitle(),
                                    user.getUrl(),
                                    false,
                                    true,
                                    true,
                                    null)) {
                            } else {
                                Log.e(TAG, "Could not navigate to page with Title: " +
                                        user.getTitle() +
                                        " Id: " +
                                        user.getPageId());
                            }
                        }
                    }
                });
            }

            if (userLoggedIn && navigation.getUser() != null) {
                indexOffset += navigation.getUser().size();
            }

            //footer
            if (navigation.getFooter() != null && 0 <= (i - indexOffset) && (i - indexOffset) < navigation.getFooter().size()) {

                final Footer footer = navigation.getFooter().get(i - indexOffset);
                if (footer.getAccessLevels() != null) {
                    if ((userLoggedIn && footer.getAccessLevels().getLoggedIn()) ||
                            (!userLoggedIn && footer.getAccessLevels().getLoggedOut())) {
                        viewHolder.navItemLabel.setText(footer.getTitle().toUpperCase());
                        viewHolder.navItemLabel.setTextColor(textColor);
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!appCMSPresenter.navigateToPage(footer.getPageId(),
                                        footer.getTitle(),
                                        footer.getUrl(),
                                        false,
                                        true,
                                        false,
                                        null)) {
                                    Log.e(TAG, "Could not navigate to page with Title: " +
                                            footer.getTitle() +
                                            " Id: " +
                                            footer.getPageId());
                                }
                            }
                        });
                        indexOffset += navigation.getFooter().size();
                    }
                }
            }

            if (0 <= (i - indexOffset)) {
                if (userLoggedIn) {
                    viewHolder.navItemLabel.setText(R.string.app_cms_sign_out_label);
                    viewHolder.navItemLabel.setTextColor(textColor);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            appCMSPresenter.logout();
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        int totalItemCount = 0;
        if (navigation != null) {
            if (navigation.getPrimary() != null) {
                totalItemCount += navigation.getPrimary().size();
            }

            if (navigation.getFooter() != null) {
                for (int i = 0; i < navigation.getFooter().size(); i++) {
                    totalItemCount += 1;
                }
            }

            if (userLoggedIn && navigation.getUser() != null) {
                totalItemCount += navigation.getUser().size() + 1; // + LogOut item
            }
        }

        return totalItemCount;
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
