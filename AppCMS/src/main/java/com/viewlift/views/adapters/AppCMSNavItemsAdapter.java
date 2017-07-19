package com.viewlift.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationFooter;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.models.data.appcms.ui.android.NavigationUser;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.Map;

import com.viewlift.R;

/**
 * Created by viewlift on 5/30/17.
 */

public class AppCMSNavItemsAdapter extends RecyclerView.Adapter<AppCMSNavItemsAdapter.ViewHolder> {
    private static final String TAG = "AppCMSNavItemsAdapter";

    private final Navigation navigation;
    private final AppCMSPresenter appCMSPresenter;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final int textColor;
    private boolean userLoggedIn;
    private int numPrimaryItems;
    private int numUserItems;
    private int numFooterItems;
    private boolean itemSelected;

    public AppCMSNavItemsAdapter(Navigation navigation,
                                 AppCMSPresenter appCMSPresenter,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 boolean userLoggedIn,
                                 int textColor) {
        this.navigation = navigation;
        this.appCMSPresenter = appCMSPresenter;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.userLoggedIn = userLoggedIn;
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
        if (navigation.getNavigationPrimary() != null && i < navigation.getNavigationPrimary().size()) {
            final NavigationPrimary navigationPrimary = navigation.getNavigationPrimary().get(i);
            if (navigationPrimary.getAccessLevels() != null) {
                if ((userLoggedIn && navigationPrimary.getAccessLevels().getLoggedIn()) ||
                        (!userLoggedIn && navigationPrimary.getAccessLevels().getLoggedOut())) {
                    viewHolder.navItemLabel.setText(navigationPrimary.getTitle().toUpperCase());
                    viewHolder.navItemLabel.setTextColor(textColor);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "Navigating to page with Title: " + navigationPrimary.getTitle());
                            if (!appCMSPresenter.navigateToPage(navigationPrimary.getPageId(),
                                    navigationPrimary.getTitle(),
                                    navigationPrimary.getUrl(),
                                    false,
                                    true,
                                    true,
                                    null)) {
                                Log.e(TAG, "Could not navigate to page with Title: " +
                                        navigationPrimary.getTitle() +
                                        " Id: " +
                                        navigationPrimary.getPageId());
                            } else {
                                itemSelected = true;
                            }
                        }
                    });
                }
            }
        } else {
            indexOffset += numPrimaryItems;

            if (userLoggedIn && navigation.getNavigationUser() != null) {
                for (int j = 0; j <= (i - indexOffset) && j < navigation.getNavigationUser().size(); j++) {
                    if (navigation.getNavigationUser().get(j).getAccessLevels() != null) {
                        if (userLoggedIn && !navigation.getNavigationUser().get(j).getAccessLevels().getLoggedIn()) {
                            indexOffset--;
                        }
                    }
                }
            }

            //user nav
            if (userLoggedIn && navigation.getNavigationUser() != null && 0 <= (i - indexOffset)
                    && (i - indexOffset) < navigation.getNavigationUser().size()) {
                final NavigationUser navigationUser = navigation.getNavigationUser().get(i - indexOffset);

                if (navigationUser.getAccessLevels() != null && navigationUser.getAccessLevels().getLoggedIn()) {
                    viewHolder.navItemLabel.setText(navigationUser.getTitle().toUpperCase());
                    viewHolder.navItemLabel.setTextColor(textColor);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppCMSUIKeyType titleKey = jsonValueKeyMap.get(navigationUser.getTitle());
                            if (titleKey == null) {
                                titleKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                            }
                            itemSelected = true;
                            switch (titleKey) {
                                case ANDROID_WATCHLIST_NAV_KEY:
                                    appCMSPresenter.navigateToWatchlistPage(navigationUser.getPageId(),
                                            navigationUser.getTitle(), navigationUser.getUrl(), false);
                                    break;

                                case ANDROID_HISTORY_NAV_KEY:
                                    appCMSPresenter.navigateToHistoryPage(navigationUser.getPageId(),
                                            navigationUser.getTitle(), navigationUser.getUrl(), false);
                                    break;

                                case ANDROID_SETTINGS_NAV_KEY:
                                    itemSelected = false;
                                    appCMSPresenter.navigateToSettingsPage(navigationUser.getPageId());
                                    break;

                                default:
                                    if (!appCMSPresenter.navigateToPage(navigationUser.getPageId(),
                                            navigationUser.getTitle(),
                                            navigationUser.getUrl(),
                                            false,
                                            true,
                                            true,
                                            null)) {
                                        Log.e(TAG, "Could not navigate to page with Title: "
                                                + navigationUser.getTitle() + " Id: " + navigationUser.getPageId());
                                    }
                            }
                        }
                    });
                }
            }

            indexOffset = numPrimaryItems + numUserItems;

            if (userLoggedIn && navigation.getNavigationFooter() != null) {
                for (int j = 0; j <= (i - indexOffset) && j < navigation.getNavigationFooter().size(); j++) {
                    if (navigation.getNavigationFooter().get(j).getAccessLevels() != null) {
                        if (userLoggedIn && !navigation.getNavigationFooter().get(j).getAccessLevels().getLoggedIn()) {
                            indexOffset--;
                        } else if (!userLoggedIn && !navigation.getNavigationFooter().get(j).getAccessLevels().getLoggedOut()) {
                            indexOffset--;
                        }
                    }
                }
            }

            //footer
            if (navigation.getNavigationFooter() != null && 0 <= (i - indexOffset) && (i - indexOffset) < navigation.getNavigationFooter().size()) {
                final NavigationFooter navigationFooter = navigation.getNavigationFooter().get(i - indexOffset);
                if (navigationFooter.getAccessLevels() != null) {
                    if ((userLoggedIn && navigationFooter.getAccessLevels().getLoggedIn()) ||
                            (!userLoggedIn && navigationFooter.getAccessLevels().getLoggedOut())) {
                        viewHolder.navItemLabel.setText(navigationFooter.getTitle().toUpperCase());
                        viewHolder.navItemLabel.setTextColor(textColor);
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemSelected = true;
                                if (!appCMSPresenter.navigateToPage(navigationFooter.getPageId(),
                                        navigationFooter.getTitle(),
                                        navigationFooter.getUrl(),
                                        false,
                                        true,
                                        false,
                                        null)) {
                                    Log.e(TAG, "Could not navigate to page with Title: " +
                                            navigationFooter.getTitle() +
                                            " Id: " +
                                            navigationFooter.getPageId());
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
            if (navigation.getNavigationPrimary() != null) {
                for (int i = 0; i < navigation.getNavigationPrimary().size(); i++) {
                    NavigationPrimary navigationPrimary = navigation.getNavigationPrimary().get(i);
                    if (navigationPrimary.getAccessLevels() != null) {
                        if (userLoggedIn && navigationPrimary.getAccessLevels().getLoggedIn()) {
                            totalItemCount++;
                            numPrimaryItems++;
                        } else if (!userLoggedIn && navigationPrimary.getAccessLevels().getLoggedOut()) {
                            totalItemCount++;
                            numPrimaryItems++;
                        }
                    }
                }
            }

            if (userLoggedIn && navigation.getNavigationUser() != null) {
                for (int i = 0; i < navigation.getNavigationUser().size(); i++) {
                    NavigationUser navigationUser = navigation.getNavigationUser().get(i);
                    if (navigationUser.getAccessLevels() != null) {
                        if (userLoggedIn && navigationUser.getAccessLevels().getLoggedIn()) {
                            totalItemCount++;
                            numUserItems++;
                        }
                    }
                }
            }

            if (navigation.getNavigationFooter() != null) {
                for (int i = 0; i < navigation.getNavigationFooter().size(); i++) {
                    NavigationFooter navigationFooter = navigation.getNavigationFooter().get(i);
                    if (navigationFooter.getAccessLevels() != null) {
                        if (userLoggedIn && navigationFooter.getAccessLevels().getLoggedIn()) {
                            totalItemCount++;
                            numFooterItems++;
                        } else if (!userLoggedIn && navigationFooter.getAccessLevels().getLoggedOut()) {
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

    public boolean isItemSelected() {
        return itemSelected;
    }

    public void setItemSelected(boolean itemSelected) {
        this.itemSelected = itemSelected;
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
