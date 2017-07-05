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
    private final OnCloseNavAction onCloseNavAction;
    private final int textColor;
    private boolean userLoggedIn;

    public interface OnCloseNavAction {
        void closeNavAction();
    }

    public AppCMSNavItemsAdapter(OnCloseNavAction onCloseNavAction,
                                 Navigation navigation,
                                 boolean userLoggedIn,
                                 AppCMSPresenter appCMSPresenter,
                                 int textColor) {
        this.onCloseNavAction = onCloseNavAction;
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
                            null)) {
                        Log.e(TAG, "Could not navigate to page with Title: " +
                                primary.getTitle() +
                                " Id: " +
                                primary.getPageId());
                    } else if (onCloseNavAction != null) {
                        onCloseNavAction.closeNavAction();
                    }
                }
            });
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

                        setUpWatchlistInNav();
                        setUpHistoryInNav();

                        if (!appCMSPresenter.navigateToPage(user.getPageId(),
                                user.getTitle(),
                                user.getUrl(),
                                false,
                                true,
                                null)) {
                            Log.e(TAG, "Could not navigate to page with Title: " +
                                    user.getTitle() +
                                    " Id: " +
                                    user.getPageId());
                        } else if (onCloseNavAction != null) {
                            onCloseNavAction.closeNavAction();
                        }
                    }

                    // TODO: 7/5/17 Confirm this method is being called.
                    private void setUpWatchlistInNav() {
                        for (int j = 0; j < navigation.getUser().size(); j++) {
                            if (navigation.getUser().get(j).getTitle().equals("Watchlist")) {
                                appCMSPresenter.navigateToWatchlistPage(user.getPageId(),
                                        user.getTitle(), user.getUrl(), true);
                            }
                        }
                    }

                    // TODO: 7/5/17 Confirm this method will be called.
                    private void setUpHistoryInNav() {
                        for (int j = 0; j < navigation.getUser().size(); j++) {
                            if (navigation.getUser().get(j).getTitle().equals("History")) {
                                appCMSPresenter.navigateToHistoryPage(user.getPageId(), user.getTitle(),
                                        user.getUrl(), true);
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
                                null)) {
                            Log.e(TAG, "Could not navigate to page with Title: " +
                                    footer.getTitle() +
                                    " Id: " +
                                    footer.getPageId());
                        } else if (onCloseNavAction != null) {
                            onCloseNavAction.closeNavAction();
                        }
                    }
                });
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
                totalItemCount += navigation.getUser().size();
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
