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
    private int numPrimaryItems;
    private int numUserItems;
    private int numFooterItems;

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
            indexOffset += numPrimaryItems;

            if (userLoggedIn && navigation.getUser() != null) {
                for (int j = 0; j <= (i - indexOffset) && j < navigation.getUser().size(); j++) {
                    if (navigation.getUser().get(j).getAccessLevels() != null) {
                        if (userLoggedIn && !navigation.getUser().get(j).getAccessLevels().getLoggedIn()) {
                            indexOffset--;
                        }
                    }
                }
            }

            //user nav
            if (userLoggedIn && navigation.getUser() != null && 0 <= (i - indexOffset)
                    && (i - indexOffset) < navigation.getUser().size()) {
                final User user = navigation.getUser().get(i - indexOffset);

                if (user.getAccessLevels() != null && user.getAccessLevels().getLoggedIn()) {
                    viewHolder.navItemLabel.setText(user.getTitle().toUpperCase());
                    viewHolder.navItemLabel.setTextColor(textColor);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (user.getTitle()) {
                                case "Watchlist":
                                    appCMSPresenter.navigateToWatchlistPage(user.getPageId(),
                                            user.getTitle(), user.getUrl(), false);
                                    break;

                                case "History":
                                    appCMSPresenter.navigateToHistoryPage(user.getPageId(),
                                            user.getTitle(), user.getUrl(), false);
                                    break;

                                case "Settings":
                                    appCMSPresenter.navigateToSettingsPage();
                                    break;

                                default:
                                    if (!appCMSPresenter.navigateToPage(user.getPageId(),
                                            user.getTitle(),
                                            user.getUrl(),
                                            false,
                                            true,
                                            true,
                                            null)) {
                                    } else {
                                        Log.e(TAG, "Could not navigate to page with Title: "
                                                + user.getTitle() + " Id: " + user.getPageId());
                                    }
                            }
                        }
                    });
                }
            }

            indexOffset = numPrimaryItems + numUserItems;

            if (userLoggedIn && navigation.getFooter() != null) {
                for (int j = 0; j <= (i - indexOffset) && j < navigation.getFooter().size(); j++) {
                    if (navigation.getFooter().get(j).getAccessLevels() != null) {
                        if (userLoggedIn && !navigation.getFooter().get(j).getAccessLevels().getLoggedIn()) {
                            indexOffset--;
                        } else if (!userLoggedIn && !navigation.getFooter().get(j).getAccessLevels().getLoggedOut()) {
                            indexOffset--;
                        }
                    }
                }
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
                    }
                }
            }

            indexOffset = numPrimaryItems + numUserItems + numFooterItems;

            if (0 <= (i - indexOffset) && userLoggedIn) {
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

    @Override
    public int getItemCount() {
        int totalItemCount = 0;
        numPrimaryItems = 0;
        numUserItems = 0;
        numFooterItems = 0;
        if (navigation != null) {
            if (navigation.getPrimary() != null) {
                for (int i = 0; i < navigation.getPrimary().size(); i++) {
                    Primary primary = navigation.getPrimary().get(i);
                    if (primary.getAccessLevels() != null) {
                        if (userLoggedIn && primary.getAccessLevels().getLoggedIn()) {
                            totalItemCount++;
                            numPrimaryItems++;
                        } else if (!userLoggedIn && primary.getAccessLevels().getLoggedOut()) {
                            totalItemCount++;
                            numPrimaryItems++;
                        }
                    }
                }
            }

            if (userLoggedIn && navigation.getUser() != null) {
                for (int i = 0; i < navigation.getUser().size(); i++) {
                    User user = navigation.getUser().get(i);
                    if (user.getAccessLevels() != null) {
                        if (userLoggedIn && user.getAccessLevels().getLoggedIn()) {
                            totalItemCount++;
                            numUserItems++;
                        }
                    }
                }
            }

            if (navigation.getFooter() != null) {
                for (int i = 0; i < navigation.getFooter().size(); i++) {
                    Footer footer = navigation.getFooter().get(i);
                    if (footer.getAccessLevels() != null) {
                        if (userLoggedIn && footer.getAccessLevels().getLoggedIn()) {
                            totalItemCount++;
                            numFooterItems++;
                        } else if (!userLoggedIn && footer.getAccessLevels().getLoggedOut()) {
                            totalItemCount++;
                            numFooterItems++;
                        }
                    }
                }
            }
        }

        if (userLoggedIn) {
            totalItemCount++;
        }

        return totalItemCount;
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
