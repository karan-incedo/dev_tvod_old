package com.viewlift.views.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apptentive.android.sdk.Apptentive;
import com.viewlift.R;
import com.viewlift.Utils;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationFooter;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.models.data.appcms.ui.android.NavigationUser;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.rxbus.DownloadTabSelectorBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.viewlift.models.data.appcms.ui.AppCMSUIKeyType.ANDROID_DOWNLOAD_NAV_KEY;
import static com.viewlift.views.customviews.download.DownloadModule.VIDEO_TAB;

/**
 * Created by viewlift on 5/30/17.
 */

public class AppCMSNavItemsAdapter extends RecyclerView.Adapter<AppCMSNavItemsAdapter.ViewHolder> {
    //private static final String TAG = "AppCMSNavItemsAdapter";

    private Navigation navigation;
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

    public AppCMSNavItemsAdapter(Navigation navigation,
                                 AppCMSPresenter appCMSPresenter,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 boolean userLoggedIn,
                                 boolean userSubscribed,
                                 int textColor) {
        this.appCMSPresenter = appCMSPresenter;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.userLoggedIn = userLoggedIn;
        this.userSubscribed = userSubscribed;
        this.textColor = textColor;
//        this.navigation = navigation;
        removePlans(navigation);
    }

    void removePlans(Navigation nav) {
        navigation = new Navigation();
        for (int i = 0; i < nav.getNavigationUser().size(); i++) {
            NavigationUser nu = nav.getNavigationUser().get(i);
            AppCMSUIKeyType titleKey = jsonValueKeyMap.get(nu.getTitle());

            if (!appCMSPresenter.isDownloadable() && titleKey == ANDROID_DOWNLOAD_NAV_KEY) {
                nav.getNavigationUser().remove(i);
                break;
            }

        }
        if (userSubscribed) {
            navigation.setLeft(nav.getLeft());
            navigation.setNavigationFooter(nav.getNavigationFooter());
            navigation.setRight(nav.getRight());
            navigation.setSettings(nav.getSettings());
            navigation.setTabBar(nav.getTabBar());
            List<NavigationPrimary> npList = new ArrayList<>();
            for (int i = 0; i < nav.getNavigationPrimary().size(); i++) {
                NavigationPrimary np = nav.getNavigationPrimary().get(i);
                if (!appCMSPresenter.isViewPlanPage(np.getPageId())) {
                    npList.add(np);
                }
            }

            navigation.setNavigationPrimary(npList);
            List<NavigationUser> nuList = new ArrayList<>();
            for (int i = 0; i < nav.getNavigationUser().size(); i++) {
                NavigationUser nu = nav.getNavigationUser().get(i);
                if (!appCMSPresenter.isViewPlanPage(nu.getPageId())) {
                    nuList.add(nu);
                }

            }
            navigation.setNavigationUser(nuList);
        } else {

            navigation = nav;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nav_item, viewGroup,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        int indexOffset = 0;

        viewHolder.navItemLabel.setText("");
        viewHolder.navItemLabel.setTypeface(appCMSPresenter.getRegularFontFace());

        if (i >= numPrimaryItems) {
            indexOffset += numPrimaryItems;
        } else {
            boolean foundViewableItem = false;
            for (int j = i; j < navigation.getNavigationPrimary().size() && !foundViewableItem; j++) {
                if (navigation.getNavigationPrimary().get(j).getAccessLevels() != null) {
                    if ((userLoggedIn && !navigation.getNavigationPrimary().get(j).getAccessLevels().getLoggedIn()) ||
                            (!userLoggedIn && !navigation.getNavigationPrimary().get(j).getAccessLevels().getLoggedOut()) ||
                            (userSubscribed && !navigation.getNavigationPrimary().get(j).getAccessLevels().getSubscribed())) {
                        indexOffset++;
                    } else {
                        foundViewableItem = true;
                    }
                }
            }
        }

        if (getClickedItemPosition() == i) {
            viewHolder.navItemSelector.setVisibility(View.VISIBLE);
            viewHolder.navItemSelector.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                    .getBrand().getCta().getPrimary().getBackgroundColor()));
            viewHolder.navItemLabel.setTypeface(appCMSPresenter.getBoldTypeFace(), Typeface.BOLD);
        } else {
            viewHolder.navItemSelector.setVisibility(View.INVISIBLE);
        }

        if (navigation.getNavigationPrimary() != null &&
                (i + indexOffset) < navigation.getNavigationPrimary().size() &&
                i < numPrimaryItems) {

            final NavigationPrimary navigationPrimary = navigation.getNavigationPrimary().get(i + indexOffset);

            if (navigationPrimary.getAccessLevels() != null) {
                if ((userLoggedIn && navigationPrimary.getAccessLevels().getLoggedIn()) ||
                        !userLoggedIn && navigationPrimary.getAccessLevels().getLoggedOut() ||
                        !userSubscribed && !navigationPrimary.getAccessLevels().getSubscribed()) {
                    viewHolder.navItemLabel.setText(navigationPrimary.getTitle().toUpperCase());
                    viewHolder.navItemLabel.setTextColor(textColor);

                    // TODO: 9/8/17 Implement Expandable ListView.

                    viewHolder.itemView.setOnClickListener(v -> {
                        setClickedItemPosition(i);
                        notifyDataSetChanged();
                        //Log.d(TAG, "Navigating to page with Title position: " + i);
                        //Log.d(TAG, "Navigating to page with Title: " + navigationPrimary.getTitle());
                        AppCMSUIKeyType titleKey = jsonValueKeyMap.get(navigationPrimary.getTitle());
                        if (titleKey == null) {
                            titleKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                        }

                        if (titleKey == AppCMSUIKeyType.ANDROID_SUBSCRIPTION_SCREEN_KEY) {
                            appCMSPresenter.navigateToSubscriptionPlansPage(true);
                        } else if (titleKey == AppCMSUIKeyType.PAGE_TEAMS_KEY) {
                            appCMSPresenter.launchTeamNavPage();
                        } else if (titleKey == AppCMSUIKeyType.PAGE_SCHEDULE_SCREEN_TITLE_KEY) {
                            appCMSPresenter.navigateToSchedulePage(navigationPrimary.getPageId(),
                                    navigationPrimary.getTitle(), false);
                        } else if (titleKey == AppCMSUIKeyType.PAGE_ROSTER_SCREEN_TITLE_KEY || titleKey == AppCMSUIKeyType.PAGE_FIGHTER_SCREEN_TITLE_KEY) {
                            appCMSPresenter.navigateToRosterPage(navigationPrimary.getPageId(),
                                    navigationPrimary.getTitle(), false);
                        } else if (!appCMSPresenter.navigateToPage(navigationPrimary.getPageId(),
                                navigationPrimary.getTitle(),
                                navigationPrimary.getUrl(),
                                false,
                                true,
                                false,
                                true,
                                false,
                                null)) {
                            //Log.e(TAG, "Could not navigate to page with Title: " +
//                                    navigationPrimary.getTitle() +
//                                    " Id: " +
//                                    navigationPrimary.getPageId());
                        } else {
                            itemSelected = true;
                        }
                    });
                }
            }
        } else {
            if (userLoggedIn && navigation.getNavigationUser() != null) {
                for (int j = 0; j <= (i - indexOffset) && j < navigation.getNavigationUser().size(); j++) {
                    if (navigation.getNavigationUser().get(j).getAccessLevels() != null) {
                        if ((userLoggedIn && !navigation.getNavigationUser().get(j).getAccessLevels().getLoggedIn()) ||
                                (!userLoggedIn && !navigation.getNavigationUser().get(j).getAccessLevels().getLoggedOut())) {
                            indexOffset--;
                        }
                    }
                }
            }

            //user nav
            if (userLoggedIn && navigation.getNavigationUser() != null && 0 <= (i - indexOffset)
                    && (i - indexOffset) < navigation.getNavigationUser().size()) {
                final NavigationUser navigationUser = navigation.getNavigationUser().get(i - indexOffset);

    if (navigationUser!=null && navigationUser.getAccessLevels() != null && navigationUser.getTitle()!=null) {
                    if (userLoggedIn && navigationUser.getAccessLevels().getLoggedIn() ||
                            !userLoggedIn && navigationUser.getAccessLevels().getLoggedOut()) {
                        viewHolder.navItemLabel.setText(navigationUser.getTitle().toUpperCase());
                        viewHolder.navItemLabel.setTextColor(textColor);
                        viewHolder.itemView.setOnClickListener(v -> {
                            setClickedItemPosition(i);
                            notifyDataSetChanged();
                            //Log.d(TAG, "Navigating to page with Title position: " + i);

                            appCMSPresenter.cancelInternalEvents();
                            AppCMSUIKeyType titleKey = jsonValueKeyMap.get(navigationUser.getTitle());
                            if (titleKey == null) {
                                titleKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                            }
                            itemSelected = true;

                            switch (titleKey) {
                                case ANDROID_DOWNLOAD_NAV_KEY:
                                    appCMSPresenter.showLoadingDialog(true);
                                    try {
                                        DownloadTabSelectorBus.instanceOf().setTab(VIDEO_TAB);
                                        appCMSPresenter.setDownloadTabSelected(VIDEO_TAB);
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                    appCMSPresenter.navigateToDownloadPage(navigationUser.getPageId(),
                                            navigationUser.getTitle(), navigationUser.getUrl(), false);
                                    break;

                                case ANDROID_SCHEDULE_SCREEN_KEY:
                                case PAGE_SCHEDULE_SCREEN_TITLE_KEY:
                                    appCMSPresenter.navigateToSchedulePage(navigationUser.getPageId(),
                                            navigationUser.getTitle(), false);
                                    break;

                                case PAGE_ROSTER_SCREEN_TITLE_KEY:
                                case PAGE_FIGHTER_SCREEN_TITLE_KEY:
                                    appCMSPresenter.navigateToRosterPage(navigationUser.getPageId(),
                                            navigationUser.getTitle(), false);
                                    break;

                                case ANDROID_WATCHLIST_NAV_KEY:
                                case ANDROID_WATCHLIST_SCREEN_KEY:
                                    if (!appCMSPresenter.isNetworkConnected()) {
                                        if (!appCMSPresenter.isUserLoggedIn()) {
                                            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK, null, false,
                                                    appCMSPresenter::launchBlankPage,
                                                    null);
                                            return;
                                        }
                                        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                                appCMSPresenter.getNetworkConnectivityDownloadErrorMsg(),
                                                true,
                                                () -> appCMSPresenter.navigateToDownloadPage(appCMSPresenter.getDownloadPageId(),
                                                        null, null, false),
                                                null);
                                        return;
                                    }
                                    appCMSPresenter.showLoadingDialog(true);
                                    appCMSPresenter.navigateToWatchlistPage(navigationUser.getPageId(),
                                            navigationUser.getTitle(), navigationUser.getUrl(), false);
                                    break;

                                case ANDROID_LIBRARY_NAV_KEY:
                                case ANDROID_LIBRARY_SCREEN_KEY:
                                    if (!appCMSPresenter.isNetworkConnected()) {
                                        if (!appCMSPresenter.isUserLoggedIn()) {
                                            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK, null, false,
                                                    appCMSPresenter::launchBlankPage,
                                                    null);
                                            return;
                                        }
                                        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                                appCMSPresenter.getNetworkConnectivityDownloadErrorMsg(),
                                                true,
                                                () -> appCMSPresenter.navigateToDownloadPage(appCMSPresenter.getDownloadPageId(),
                                                        null, null, false),
                                                null);
                                        return;
                                    }
                                    appCMSPresenter.showLoadingDialog(true);
                                    appCMSPresenter.navigateToLibraryPage(navigationUser.getPageId(),
                                            navigationUser.getTitle(), false);
                                    break;



                                case ANDROID_HISTORY_NAV_KEY:
                                case ANDROID_HISTORY_SCREEN_KEY:
                                    if (!appCMSPresenter.isNetworkConnected()) {
                                        if (!appCMSPresenter.isUserLoggedIn()) {
                                            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK, null, false, null, null);
                                            return;
                                        }
                                        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                                appCMSPresenter.getNetworkConnectivityDownloadErrorMsg(),
                                                true,
                                                () -> appCMSPresenter.navigateToDownloadPage(appCMSPresenter.getDownloadPageId(),
                                                        null, null, false),
                                                null);
                                        return;
                                    }
                                    appCMSPresenter.showLoadingDialog(true);
                                    appCMSPresenter.navigateToHistoryPage(navigationUser.getPageId(),
                                            navigationUser.getTitle(), navigationUser.getUrl(), false);
                                    break;

                                default:

                                    if (!appCMSPresenter.isNetworkConnected() && titleKey != ANDROID_DOWNLOAD_NAV_KEY) {
                                        if (!appCMSPresenter.isUserLoggedIn()) {
                                            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                                    null,
                                                    false,
                                                    null,
                                                    null);
                                            return;
                                        }
                                        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                                appCMSPresenter.getNetworkConnectivityDownloadErrorMsg(),
                                                true,
                                                () -> appCMSPresenter.navigateToDownloadPage(appCMSPresenter.getDownloadPageId(),
                                                        null, null, false),
                                                null);
                                        return;
                                    }
                                    Boolean appBarPresent = false;
                                    if (appCMSPresenter.isViewPlanPage(navigationUser.getPageId())) {
                                        appBarPresent = true;
                                    }
                                    if (!appCMSPresenter.navigateToPage(navigationUser.getPageId(),
                                            navigationUser.getTitle(),
                                            navigationUser.getUrl(),
                                            false,
                                            appBarPresent,
                                            false,
                                            false,
                                            false,
                                            null)) {
                                        //Log.e(TAG, "Could not navigate to page with Title: "
//                                                + navigationUser.getTitle() + " Id: " + navigationUser.getPageId());
                                    }
                                    break;
                            }
                        });
                    }
                }
            }

            indexOffset = numPrimaryItems + numUserItems;

            if (userLoggedIn && navigation.getNavigationFooter() != null) {
                for (int j = 0; j <= (i - indexOffset) && j < navigation.getNavigationFooter().size(); j++) {
                    if (navigation.getNavigationFooter().get(j).getAccessLevels() != null) {
                        if (userLoggedIn && !navigation.getNavigationFooter().get(j).getAccessLevels().getLoggedIn() ||
                                !userLoggedIn && !navigation.getNavigationFooter().get(j).getAccessLevels().getLoggedOut()) {
                            indexOffset--;
                        }
                    }
                }
            }

            //footer
            if (navigation.getNavigationFooter() != null && 0 <= (i - indexOffset)
                    && (i - indexOffset) < navigation.getNavigationFooter().size()) {
                final NavigationFooter navigationFooter = navigation.getNavigationFooter().get(i - indexOffset);
                if (navigationFooter.getAccessLevels() != null) {
                    if ((userLoggedIn && navigationFooter.getAccessLevels().getLoggedIn()) ||
                            (!userLoggedIn && navigationFooter.getAccessLevels().getLoggedOut())) {
                        viewHolder.navItemLabel.setText(navigationFooter.getTitle().toUpperCase());
                        viewHolder.navItemLabel.setTextColor(textColor);
                        viewHolder.itemView.setOnClickListener(v -> {
                            setClickedItemPosition(i);
                            notifyDataSetChanged();
                            //Log.d(TAG, "Navigating to page with Title position: " + i);
                            if (!appCMSPresenter.isNetworkConnected()) {
                                if (!appCMSPresenter.isUserLoggedIn()) {
                                    appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                            null,
                                            false,
                                            null,
                                            null);
                                    return;
                                }
                                appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                        appCMSPresenter.getNetworkConnectivityDownloadErrorMsg(),
                                        true,
                                        () -> appCMSPresenter.navigateToDownloadPage(appCMSPresenter.getDownloadPageId(),
                                                null, null, false),
                                        null);
                                return;
                            }
                            appCMSPresenter.cancelInternalEvents();
                            itemSelected = true;
                            if (navigationFooter.getTitle().equalsIgnoreCase(viewHolder.itemView.getContext().getString(R.string.app_cms_page_shop_title)) &&
                                    !TextUtils.isEmpty(navigationFooter.getTitle())) {
                                appCMSPresenter.openChromeTab(navigationFooter.getUrl());
                            } else if (navigationFooter.getTitle().equalsIgnoreCase(viewHolder.itemView.getContext().getString(R.string.contact_us)) && !TextUtils.isEmpty(Utils.getProperty("ApptentiveApiKey", viewHolder.itemView.getContext()))) {
                                if (appCMSPresenter.isNetworkConnected()) {
                                    //Firebase Event when contact us screen is opened.
                                    appCMSPresenter.sendFireBaseContactUsEvent();
                                    if (Apptentive.canShowMessageCenter()) {
                                        Apptentive.showMessageCenter(viewHolder.itemView.getContext());
                                    }
                                } else {
                                    appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                            null,
                                            false,
                                            () -> {
                                            },
                                            () -> {
                                            });
                                }
                            } else if (!appCMSPresenter.navigateToPage(navigationFooter.getPageId(),
                                    navigationFooter.getTitle(),
                                    navigationFooter.getUrl(),
                                    false,
                                    false,
                                    false,
                                    false,
                                    false,
                                    null)) {
                                //Log.e(TAG, "Could not navigate to page with Title: " +
//                                        navigationFooter.getTitle() +
//                                        " Id: " +
//                                        navigationFooter.getPageId());
                            }
                        });
                    }
                }
            }

            indexOffset = numPrimaryItems + numUserItems + numFooterItems;

            if (0 <= (i - indexOffset) && userLoggedIn) {
                viewHolder.navItemLabel.setText(R.string.app_cms_sign_out_label);
                viewHolder.navItemLabel.setTextColor(textColor);
                viewHolder.itemView.setOnClickListener(v -> {
                    if (!appCMSPresenter.isNetworkConnected()) {
                        appCMSPresenter.showDialog(AppCMSPresenter.DialogType.NETWORK,
                                appCMSPresenter.getNetworkConnectivityDownloadErrorMsg(),
                                true,
                                () -> appCMSPresenter.navigateToDownloadPage(appCMSPresenter.getDownloadPageId(),
                                        null, null, false),
                                null);
                    } else {
                        if (appCMSPresenter.isDownloadUnfinished()) {
                            appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGOUT_WITH_RUNNING_DOWNLOAD, null);
                        } else {
                            appCMSPresenter.showDialog(AppCMSPresenter.DialogType.SIGN_OUT,
                                    appCMSPresenter.getSignOutErrorMsg(),
                                    true,
                                    () -> {
                                        appCMSPresenter.cancelInternalEvents();
                                        appCMSPresenter.logout();
                                    },
                                    null);

                        }
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
                        if ((!userSubscribed && !navigationPrimary.getAccessLevels().getSubscribed()) &&
                                (!userLoggedIn && navigationPrimary.getAccessLevels().getLoggedOut() ||
                                        userLoggedIn && navigationPrimary.getAccessLevels().getLoggedIn())) {
                            totalItemCount++;
                            numPrimaryItems++;
                        } else if ((!userLoggedIn && navigationPrimary.getAccessLevels().getLoggedOut() ||
                                userLoggedIn && navigationPrimary.getAccessLevels().getLoggedIn()) &&
                                navigationPrimary.getAccessLevels().getSubscribed()) {
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
                        if (!userLoggedIn && navigation.getNavigationUser().get(i).getAccessLevels().getLoggedOut() ||
                                userLoggedIn && navigation.getNavigationUser().get(i).getAccessLevels().getLoggedIn()) {
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
                        if ((!userLoggedIn && navigation.getNavigationFooter().get(i).getAccessLevels().getLoggedOut() ||
                                userLoggedIn && navigation.getNavigationFooter().get(i).getAccessLevels().getLoggedIn())) {
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
