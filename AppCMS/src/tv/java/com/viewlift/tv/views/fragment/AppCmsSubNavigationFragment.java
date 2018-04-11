package com.viewlift.tv.views.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.android.AccessLevels;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationFooter;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.models.data.appcms.ui.android.NavigationUser;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.views.binders.AppCMSBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.viewlift.models.data.appcms.ui.AppCMSUIKeyType.ANDROID_HISTORY_NAV_KEY;
import static com.viewlift.models.data.appcms.ui.AppCMSUIKeyType.ANDROID_WATCHLIST_NAV_KEY;

/**
 * Created by nitin.tyagi on 6/27/2017.
 */

public class AppCmsSubNavigationFragment extends Fragment {

    private static Context mContext;
    private static OnSubNavigationVisibilityListener navigationVisibilityListener;
    TextView navMenuTile;
    private RecyclerView mRecyclerView;
    private int textColor = -1;
    private int bgColor = -1;
    private Typeface extraBoldTypeFace, semiBoldTypeFace;
    private Component extraBoldComp, semiBoldComp;
    private AppCMSBinder appCmsBinder;
    private Navigation mNavigation;
    private AppCMSBinder mAppCMSBinder;
    private boolean isLoginDialogPage;
    private AppCMSPresenter appCMSPresenter;
    private boolean mShowTeams = false;
    private String mSelectedPageId = null;
    private int selectedPosition = 0;
    private List<NavigationSubItem> navigationSubItemList;

    public static AppCmsSubNavigationFragment newInstance(Context context,
                                                          OnSubNavigationVisibilityListener listener
    ) {
        mContext = context;
        AppCmsSubNavigationFragment fragment = new AppCmsSubNavigationFragment();
      /*  Bundle args = new Bundle();
        args.putInt(context.getString(R.string.app_cms_text_color_key), textColor);
        args.putInt(context.getString(R.string.app_cms_bg_color_key), bgColor);
        fragment.setArguments(args);*/
        navigationVisibilityListener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_navigation, null);

        Bundle bundle = getArguments();
        mAppCMSBinder = (AppCMSBinder) bundle.getBinder("app_cms_binder");
        isLoginDialogPage = bundle.getBoolean(getString(R.string.is_login_dialog_page_key));
/*
        AppCMSBinder appCMSBinder = ((AppCMSBinder) args.getBinder(getResources().getString(R.string.fragment_page_bundle_key)));
        this.appCmsBinder = appCMSBinder;
*/
        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
        view.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()));

        View navTopLine = view.findViewById(R.id.nav_top_line);
        AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
        textColor = Color.parseColor(appCMSMain.getBrand().getCta().getPrimary().getTextColor());/*Color.parseColor("#F6546A");*/
        bgColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getBackgroundColor());//Color.parseColor("#660066");

        mNavigation = appCMSPresenter.getNavigation();

        navMenuTile = (TextView) view.findViewById(R.id.nav_menu_title);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.navRecylerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (appCMSPresenter.getTemplateType().equals(AppCMSPresenter.TemplateType.ENTERTAINMENT)) {
            NavigationAdapter navigationAdapter = new NavigationAdapter(getActivity(), textColor, bgColor,
                    mNavigation,
                    appCMSPresenter);

            mRecyclerView.setAdapter(navigationAdapter);
            setFocusable(true);
            navigationAdapter.setFocusOnSelectedPage();
            navTopLine.setVisibility(View.GONE);
        } else {
            navMenuTile.setText("Settings");
            navMenuTile.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), getActivity().getString(R.string.lato_regular)));
            navMenuTile.setVisibility(View.VISIBLE);
            navMenuTile.setTextColor(Color.parseColor(appCMSPresenter.getAppTextColor()));

            STNavigationAdapter navigationAdapter = new STNavigationAdapter(
                    getActivity(),
                    textColor,
                    bgColor,
                    mNavigation,
                    appCMSPresenter);

            mRecyclerView.setAdapter(navigationAdapter);
            setFocusable(true);
            navigationAdapter.setFocusOnSelectedPage();
            navTopLine.setVisibility(View.VISIBLE);
            navTopLine.setBackgroundColor(Color.parseColor(Utils.getFocusColor(getActivity(),appCMSPresenter)));
        }
        return view;
    }

    private void setTypeFaceValue(AppCMSPresenter appCMSPresenter) {
        if (null == extraBoldTypeFace) {
            extraBoldComp = new Component();
            extraBoldComp.setFontFamily(getResources().getString(R.string.app_cms_page_font_family_key));
            extraBoldComp.setFontWeight(getResources().getString(R.string.app_cms_page_font_extrabold_key));
            extraBoldTypeFace = Utils.getTypeFace(getActivity(), appCMSPresenter.getJsonValueKeyMap()
                    , extraBoldComp);
        }

        if (null == semiBoldTypeFace) {
            semiBoldComp = new Component();
            semiBoldComp.setFontFamily(getResources().getString(R.string.app_cms_page_font_family_key));
            semiBoldComp.setFontWeight(getResources().getString(R.string.app_cms_page_font_semibold_key));
            semiBoldTypeFace = Utils.getTypeFace(getActivity(), appCMSPresenter.getJsonValueKeyMap()
                    , semiBoldComp);
        }
    }

    public void setFocusable(boolean hasFocus) {
        if (null != mRecyclerView) {
            if (hasFocus)
                mRecyclerView.requestFocus();
            else
                mRecyclerView.clearFocus();
        }
    }

    public void setSelectedPageId(String selectedPageId) {
        this.mSelectedPageId = selectedPageId;
    }

    private int getSelectedPagePosition() {
        if (null != mSelectedPageId) {
            if (null != navigationSubItemList && navigationSubItemList.size() > 0) {
                for (int i = 0; i < navigationSubItemList.size(); i++) {
                    NavigationSubItem navigationSubItem = navigationSubItemList.get(i);
                    if (Objects.equals(navigationSubItem.pageId, mSelectedPageId)) {
                        return i;
                    }
                }
            }
        }
        return 0;
    }

    public void notifyDataSetInvalidate(boolean showTeams) {
        mShowTeams = showTeams;
        if (navigationSubItemList != null) {
            navigationSubItemList.clear();
            if (appCMSPresenter.getTemplateType().equals(AppCMSPresenter.TemplateType.SPORTS)) {
                if (!showTeams) {
                    createSubNavigationListForST();
                    navMenuTile.setText("Settings");
                } else {
                    createTeamsListForST();
                    navMenuTile.setText("Teams");
                }
                mRecyclerView.requestFocus();
            }
        }
        if (null != mRecyclerView && null != mRecyclerView.getAdapter()) {
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private boolean isEndPosition() {
        return selectedPosition == navigationSubItemList.size() - 1;
    }

    private boolean isStartPosition() {
        return (selectedPosition == 0);
    }

    private void createSubNavigationList() {
        for (int i = 0; i < mNavigation.getNavigationUser().size(); i++) {
            NavigationUser navigationUser = mNavigation.getNavigationUser().get(i);
            if ((appCMSPresenter.isUserLoggedIn() && navigationUser.getAccessLevels().getLoggedIn())
                    || (!appCMSPresenter.isUserLoggedIn() && navigationUser.getAccessLevels().getLoggedOut())) {
                NavigationSubItem navigationSubItem = new NavigationSubItem();
                navigationSubItem.pageId = navigationUser.getPageId();
                navigationSubItem.title = navigationUser.getTitle();
                navigationSubItem.url = navigationUser.getUrl();
                navigationSubItem.accessLevels = navigationUser.getAccessLevels();
                if (null == navigationSubItemList) {
                    navigationSubItemList = new ArrayList<NavigationSubItem>();
                }
                navigationSubItemList.add(navigationSubItem);
            }
        }
        if (!appCMSPresenter.isUserLoggedIn()) {
            return;
        }

        for (int i = 0; i < mNavigation.getNavigationFooter().size(); i++) {
            NavigationFooter navigationFooter = mNavigation.getNavigationFooter().get(i);
            {
                NavigationSubItem navigationSubItem = new NavigationSubItem();
                navigationSubItem.pageId = navigationFooter.getPageId();
                navigationSubItem.title = navigationFooter.getTitle();
                navigationSubItem.url = navigationFooter.getUrl();
                navigationSubItem.accessLevels = navigationFooter.getAccessLevels();
                if (null == navigationSubItemList) {
                    navigationSubItemList = new ArrayList<NavigationSubItem>();
                }
                navigationSubItemList.add(navigationSubItem);
            }
        }
    }

    private void createSubNavigationListForST() {
        if (null == navigationSubItemList) {
            navigationSubItemList = new ArrayList<>();
        }

        NavigationSubItem navigationSubItem1 = new NavigationSubItem();
        navigationSubItem1.icon = getString(R.string.st_autoplay_icon_key);
        if (appCMSPresenter.getAutoplayEnabledUserPref(mContext)) {
            navigationSubItem1.title = "AUTOPLAY ON";
        } else {
            navigationSubItem1.title = "AUTOPLAY OFF";
        }
        navigationSubItemList.add(navigationSubItem1);

        navigationSubItem1 = new NavigationSubItem();
        navigationSubItem1.icon = getString(R.string.st_closed_caption_icon_key);
        if (appCMSPresenter.getClosedCaptionPreference()) {
            navigationSubItem1.title = "CLOSED CAPTION ON";
        } else {
            navigationSubItem1.title = "CLOSED CAPTION OFF";
        }
        navigationSubItemList.add(navigationSubItem1);

        if (appCMSPresenter.isUserLoggedIn()) {
            navigationSubItem1 = new NavigationSubItem();
            navigationSubItem1.title = "MANAGE SUBSCRIPTION";
            navigationSubItem1.icon = getString(R.string.st_manage_subscription_icon_key);
            navigationSubItemList.add(navigationSubItem1);
        } else /*Guest User*/{
            navigationSubItem1 = new NavigationSubItem();
            navigationSubItem1.title = "SUBSCRIBE NOW";
            navigationSubItem1.icon = getString(R.string.st_manage_subscription_icon_key);
            navigationSubItemList.add(navigationSubItem1);
        }

        for (int i = 0; i < mNavigation.getNavigationUser().size(); i++) {
            NavigationUser navigationUser = mNavigation.getNavigationUser().get(i);
            if (/*(isUserLogin && navigationUser.getAccessLevels().getLoggedIn())
                        || (!isUserLogin && navigationUser.getAccessLevels().getLoggedOut())*/
                    !navigationUser.getAccessLevels().getLoggedOut()
                            && appCMSPresenter.isUserLoggedIn()) {
                NavigationSubItem navigationSubItem = new NavigationSubItem();
                navigationSubItem.pageId = navigationUser.getPageId();
                navigationSubItem.title = navigationUser.getTitle();
                navigationSubItem.url = navigationUser.getUrl();
                navigationSubItem.icon = navigationUser.getIcon();
                navigationSubItem.accessLevels = navigationUser.getAccessLevels();
                navigationSubItemList.add(navigationSubItem);
            }
        }
            /*if (!isUserLogin) {
                return;
            }*/

            if(null != mNavigation && null != mNavigation.getNavigationFooter()) {
                for (int i = 0; i < mNavigation.getNavigationFooter().size(); i++) {
                    NavigationFooter navigationFooter = mNavigation.getNavigationFooter().get(i);
                    {
                        NavigationSubItem navigationSubItem = new NavigationSubItem();
                        navigationSubItem.pageId = navigationFooter.getPageId();
                        navigationSubItem.title = navigationFooter.getTitle();
                        navigationSubItem.url = navigationFooter.getUrl();
                        navigationSubItem.icon = navigationFooter.getIcon();
                        navigationSubItem.accessLevels = navigationFooter.getAccessLevels();
                        if (null == navigationSubItemList) {
                            navigationSubItemList = new ArrayList<>();
                        }
                        navigationSubItemList.add(navigationSubItem);
                    }
                }
            }
        navigationSubItem1 = new NavigationSubItem();
        if (appCMSPresenter.isUserLoggedIn()) {
            navigationSubItem1.title = "SIGN OUT";
            navigationSubItem1.icon = getString(R.string.st_signout_icon_key);
        } else {
            navigationSubItem1.icon = getString(R.string.st_signin_icon_key);
            navigationSubItem1.title = "SIGN IN";
        }
        navigationSubItemList.add(navigationSubItem1);
    }

    private void createTeamsListForST() {
        if (null == navigationSubItemList) {
            navigationSubItemList = new ArrayList<>();
        }

        for (NavigationPrimary primary :
                mNavigation.getTabBar()) {
            if (primary.getTitle().equalsIgnoreCase("teams")) {
                if (primary.getItems() != null) {
                    for (NavigationPrimary team :
                            primary.getItems()) {
                        NavigationSubItem navigationSubItem = new NavigationSubItem();
                        navigationSubItem.pageId = team.getPageId();
                        navigationSubItem.title = team.getTitle();
                        navigationSubItem.url = team.getUrl();
                        navigationSubItem.accessLevels = team.getAccessLevels();
                        navigationSubItem.icon = team.getIcon();
                        navigationSubItemList.add(navigationSubItem);
                    }
                }
            }
        }
    }

    public boolean isTeamsShowing() {
        return mShowTeams;
    }

    public interface OnSubNavigationVisibilityListener {
        void showSubNavigation(boolean shouldShow, boolean showTeams);
    }

    class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavItemHolder> {
        private Context mContext;
        private LayoutInflater inflater;
        private int textColor;
        private int bgColor;
        private Navigation navigation;
        private boolean isuserLoggedIn;
        private AppCMSPresenter appCmsPresenter;
        private int currentNavPos;

        public NavigationAdapter(Context activity,
                                 int textColor,
                                 int bgColor,
                                 Navigation navigation,
                                 AppCMSPresenter appCMSPresenter) {
            mContext = activity;
            this.textColor = textColor;
            this.bgColor = bgColor;
            this.navigation = navigation;
            this.appCmsPresenter = appCMSPresenter;
            createSubNavigationList();
        }


        public Object getItem(int i) {
            return navigationSubItemList.get(i);
        }

        @Override
        public NavItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.navigation_sub_item, parent, false);
            NavItemHolder navItemHolder = new NavItemHolder(view);
            return navItemHolder;
        }

        @Override
        public void onBindViewHolder(NavItemHolder holder, final int position) {
            final NavigationSubItem subItem = (NavigationSubItem) getItem(position);
            holder.navItemView.setText(subItem.title.toString().toUpperCase());
            holder.navItemView.setTag(R.string.item_position, position);
            //Log.d("NavigationAdapter", subItem.title.toString());

            if (selectedPosition >= 0 && selectedPosition == position) {
                holder.navItemlayout.setBackground(
                        Utils.getNavigationSelectedState(mContext, appCmsPresenter, true , bgColor));
                holder.navItemView.setTypeface(extraBoldTypeFace);
            } else {
                holder.navItemlayout.setBackground(null);
                holder.navItemView.setTypeface(semiBoldTypeFace);
            }

            holder.navItemlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavigationSubItem navigationSubItem = navigationSubItemList.get(selectedPosition);
                    if (ANDROID_WATCHLIST_NAV_KEY.equals(mAppCMSBinder.getJsonValueKeyMap()
                            .get(navigationSubItem.title))) {

                        appCmsPresenter.showLoadingDialog(true);
                        appCmsPresenter.navigateToWatchlistPage(
                                navigationSubItem.pageId,
                                navigationSubItem.title,
                                navigationSubItem.url,
                                false);
                    } else if (ANDROID_HISTORY_NAV_KEY.equals(mAppCMSBinder.getJsonValueKeyMap()
                            .get(navigationSubItem.title))) {
                        appCmsPresenter.showLoadingDialog(true);
                        appCmsPresenter.navigateToHistoryPage(
                                navigationSubItem.pageId,
                                navigationSubItem.title,
                                navigationSubItem.url,
                                false);
                    } else {
                        appCmsPresenter.navigateToTVPage(
                                navigationSubItem.pageId,
                                navigationSubItem.title,
                                navigationSubItem.url,
                                false,
                                Uri.EMPTY,
                                false,
                                false,
                                isLoginDialogPage);
                    }
                }
            });
        }

        private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
            int tryFocusItem = selectedPosition + direction;
            if (tryFocusItem >= 0 && tryFocusItem < getItemCount()) {
                notifyItemChanged(selectedPosition);
                selectedPosition = tryFocusItem;
                notifyItemChanged(selectedPosition);
                lm.scrollToPosition(selectedPosition);
                return true;
            }
            return true;
        }

        @Override
        public int getItemCount() {
            int totalCount = 0;
            if (null != navigationSubItemList)
                totalCount = navigationSubItemList.size();
            return totalCount;
        }

        private void setFocusOnSelectedPage() {
            int selectedPos = getSelectedPagePosition();
            notifyItemChanged(selectedPosition);
            selectedPosition = selectedPos;
            notifyItemChanged(selectedPosition);
        }

        class NavItemHolder extends RecyclerView.ViewHolder {
            TextView navItemView;
            RelativeLayout navItemlayout;

            public NavItemHolder(View itemView) {
                super(itemView);
                setTypeFaceValue(appCmsPresenter);
                navItemView = (TextView) itemView.findViewById(R.id.nav_item_label);
                navItemlayout = (RelativeLayout) itemView.findViewById(R.id.nav_item_layout);
                navItemView.setTextColor(Color.parseColor(Utils.getTextColor(mContext, appCmsPresenter)));

                navItemlayout.setOnFocusChangeListener((view, focus) -> {
                    if (focus)
                        mRecyclerView.setAlpha(1f);
                });

                navItemlayout.setOnKeyListener((view, i, keyEvent) -> {
                    int keyCode = keyEvent.getKeyCode();
                    int action = keyEvent.getAction();
                    if (action == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_LEFT:
                                return tryMoveSelection(mRecyclerView.getLayoutManager(), -1);
                            case KeyEvent.KEYCODE_DPAD_RIGHT:
                                return tryMoveSelection(mRecyclerView.getLayoutManager(), 1);
                            case KeyEvent.KEYCODE_DPAD_DOWN:
                                setFocusOnSelectedPage();
                                new Handler().postDelayed(() -> mRecyclerView.setAlpha(0.52f), 50);
                                break;
                        }
                    }
                    return false;
                });
            }
        }
    }

    class STNavigationAdapter extends RecyclerView.Adapter<STNavigationAdapter.STNavItemHolder> {
        private Context mContext;
        private LayoutInflater inflater;
        private int textColor;
        private Navigation navigation;
        private AppCMSPresenter appCMSPresenter;
        private int currentNavPos;

        public STNavigationAdapter(Context activity,
                                   int textColor,
                                   int bgColor,
                                   Navigation navigation,
                                   AppCMSPresenter appCMSPresenter) {
            mContext = activity;
            this.textColor = textColor;
            this.navigation = navigation;
            this.appCMSPresenter = appCMSPresenter;
            createSubNavigationListForST();
        }

        public Object getItem(int i) {
            return navigationSubItemList.get(i);
        }

        @Override
        public STNavItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.st_navigation_item, parent, false);
            STNavItemHolder navItemHolder = new STNavItemHolder(view);
            return navItemHolder;
        }

        @Override
        public void onBindViewHolder(STNavItemHolder holder, final int position) {
            final NavigationSubItem subItem = (NavigationSubItem) getItem(holder.getAdapterPosition());
            holder.navItemView.setText(subItem.title.toUpperCase());
            holder.navItemView.setTag(R.string.item_position, holder.getAdapterPosition());


            if (!mShowTeams) {
                holder.navImageView.setPadding(0, 0, 0, 0);
                holder.navImageView.setImageResource(subItem.icon != null
                        ? getIcon(subItem.icon) : -1);
                if(subItem.icon != null) {
                    holder.navImageView.getDrawable().setTint(Utils.getComplimentColor(appCMSPresenter.getGeneralBackgroundColor()));
                    holder.navImageView.getDrawable().setTintMode(PorterDuff.Mode.MULTIPLY);
                }
            } else {
                holder.navImageView.setPadding(10, 10, 10, 10);
                Glide.with(mContext)
                        .load(subItem.icon)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(holder.navImageView);
            }


            holder.navImageView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    int keyCode = keyEvent.getKeyCode();
                    if(keyCode == KeyEvent.KEYCODE_MENU){
                        return true;
                    }
                    return false;
                }
            });
            holder.navItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavigationSubItem navigationSubItem = navigationSubItemList.get(selectedPosition);

                    if (navigationSubItem.title.toUpperCase().contains("AUTOPLAY")) {
                        if (appCMSPresenter.getAutoplayEnabledUserPref(mContext)) {
                            navigationSubItem.title = "Autoplay Off";
                            appCMSPresenter.setAutoplayEnabledUserPref(mContext, false);
                        } else {
                            navigationSubItem.title = "Autoplay On";
                            appCMSPresenter.setAutoplayEnabledUserPref(mContext, true);
                        }
                    } else if (navigationSubItem.title.toUpperCase().contains("CLOSED CAPTION")) {
                        if (appCMSPresenter.getClosedCaptionPreference()) {
                            navigationSubItem.title = "Closed Caption Off";
                            appCMSPresenter.setClosedCaptionPreference(false);
                        } else {
                            navigationSubItem.title = "Closed Caption On";
                            appCMSPresenter.setClosedCaptionPreference(true);
                        }
                    } else if (navigationSubItem.title.toUpperCase().contains("SUBSCRI")) {
                        if (appCMSPresenter.isUserLoggedIn()) {
                            mContext.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
                            appCMSPresenter.getSubscriptionData(
                                        appCMSUserSubscriptionPlanResult -> {

                                            mContext.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION));
                                            String platform;
                                            String status;
                                            String varMessage = "";
                                            if (appCMSUserSubscriptionPlanResult != null
                                                    && appCMSUserSubscriptionPlanResult.getSubscriptionInfo() != null
                                                    && appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getPlatform() != null) {
                                                platform = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getPlatform();
                                                status = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();

                                                if (status.equalsIgnoreCase("COMPLETED") ||
                                                        status.equalsIgnoreCase("DEFERRED_CANCELLATION")) {
                                                    if (platform.equalsIgnoreCase("web_browser")) {
                                                        varMessage = mContext.getString(R.string.subscription_purchased_from_web_msg);
                                                    } else if (platform.equalsIgnoreCase("android") || platform.contains("android")) {
                                                        varMessage = mContext.getString(R.string.subscription_purchased_from_android_msg);
                                                    } else if (platform.contains("iOS") || platform.contains("ios_phone") || platform.contains("ios_ipad") || platform.contains("tvos") || platform.contains("ios_apple_tv")) {
                                                        varMessage = mContext.getString(R.string.subscription_purchased_from_apple_msg);
                                                    } else {
                                                        varMessage = mContext.getString(R.string.subscription_purchased_from_unknown_msg);
                                                    }
                                                } else {
                                                    varMessage = mContext.getString(R.string.subscription_not_purchased);
                                                }
                                            } else {
                                                varMessage = mContext.getString(R.string.subscription_not_purchased);
                                            }
                                            appCMSPresenter.openTVErrorDialog(varMessage, mContext.getString(R.string.subscription), false);
                                        }
                                );
                        } else {
                            if(!appCMSPresenter.isUserLoggedIn() && appCMSPresenter.isNetworkConnected()) {
                                appCMSPresenter.setLaunchType(AppCMSPresenter.LaunchType.NAVIGATE_TO_HOME_FROM_LOGIN_DIALOG);
                                ClearDialogFragment newFragment = Utils.getClearDialogFragment(
                                        getActivity(),
                                        appCMSPresenter,
                                        getResources().getDimensionPixelSize(R.dimen.text_clear_dialog_width),
                                        getResources().getDimensionPixelSize(R.dimen.text_add_to_watchlist_sign_in_dialog_height),
                                        getString(R.string.subscription),
                                        getString(R.string.subscription_not_purchased),
                                        getString(R.string.sign_in_text),
                                        getString(android.R.string.cancel),
                                        14
                                );

                                newFragment.setOnPositiveButtonClicked(s -> {
                                    NavigationUser navigationUser = appCMSPresenter.getLoginNavigation();
                                    appCMSPresenter.navigateToTVPage(
                                            navigationUser.getPageId(),
                                            navigationUser.getTitle(),
                                            navigationUser.getUrl(),
                                            false,
                                            Uri.EMPTY,
                                            false,
                                            false,
                                            true);
                                });
                            }else{
                                appCMSPresenter.openTVErrorDialog(
                                        getActivity().getString(R.string.subscription_not_purchased),
                                        getActivity().getString(R.string.subscription), false);
                            }
                            //appCMSPresenter.openTVErrorDialog(mContext.getString(R.string.subscription_not_purchased), mContext.getString(R.string.subscription), false);
                        }
                    } else if (navigationSubItem.title.toUpperCase().contains("SIGN")) {
                        if (!appCMSPresenter.isUserLoggedIn()) {
                            appCMSPresenter.setLaunchType(AppCMSPresenter.LaunchType.NAVIGATE_TO_HOME_FROM_LOGIN_DIALOG);
                            NavigationUser navigationUser = appCMSPresenter.getLoginNavigation();
                            appCMSPresenter.navigateToTVPage(
                                    navigationUser.getPageId(),
                                    navigationUser.getTitle(),
                                    navigationUser.getUrl(),
                                    false,
                                    Uri.EMPTY,
                                    false,
                                    false,
                                    true
                            );
                        } else {
                            appCMSPresenter.logoutTV();
                        }
                      //  navigationVisibilityListener.showSubNavigation(false, false);
                    } else if (navigationSubItem.title.toUpperCase().contains("ACCOUNT")) {
                        if (appCMSPresenter.isUserLoggedIn()) {
                            // navigationVisibilityListener.showSubNavigation(false, false);
                            appCMSPresenter.navigateToTVPage(
                                    navigationSubItem.pageId,
                                    navigationSubItem.title,
                                    navigationSubItem.url,
                                    false,
                                    Uri.EMPTY,
                                    false,
                                    true,
                                    false);
                        } else {
                            ClearDialogFragment newFragment = Utils.getClearDialogFragment(
                                    mContext,
                                    appCMSPresenter,
                                    mContext.getResources().getDimensionPixelSize(R.dimen.text_clear_dialog_width),
                                    mContext.getResources().getDimensionPixelSize(R.dimen.text_add_to_watchlist_sign_in_dialog_height),
                                    mContext.getString(R.string.sign_in_text),
                                    mContext.getString(R.string.open_account_dialog_text),
                                    mContext.getString(R.string.sign_in_text),
                                    mContext.getString(android.R.string.cancel),
                                    14

                            );
                            newFragment.setOnPositiveButtonClicked(s -> {

                                NavigationUser navigationUser = appCMSPresenter.getLoginNavigation();
                                appCMSPresenter.navigateToTVPage(
                                        navigationUser.getPageId(),
                                        navigationUser.getTitle(),
                                        navigationUser.getUrl(),
                                        false,
                                        Uri.EMPTY,
                                        false,
                                        false,
                                        true);
                            });
                        }
                    } else if (navigationSubItem.title.toUpperCase().contains("CONTACT")) {
                        //navigationVisibilityListener.showSubNavigation(false, false);
                        appCMSPresenter.navigateToTVPage(
                                navigationSubItem.pageId,
                                navigationSubItem.title,
                                navigationSubItem.url,
                                false,
                                Uri.EMPTY,
                                true,
                                true,
                                false);
                    } else {
                        if (navigationSubItem.pageId != null
                                && navigationSubItem.pageId.length() > 0) {
                            if (isTeamsShowing())
                                navigationVisibilityListener.showSubNavigation(false, false);
                            appCMSPresenter.navigateToTVPage(
                                    navigationSubItem.pageId,
                                    navigationSubItem.title,
                                    navigationSubItem.url,
                                    false,
                                    Uri.EMPTY,
                                    true,
                                    !isTeamsShowing(),
                                    isLoginDialogPage);
                        } else {
                            appCMSPresenter.openTVErrorDialog("There is some error opening " + navigationSubItem.title, "", false);
                        }
                    }
                    STNavigationAdapter.this.notifyItemChanged(holder.getAdapterPosition());
                }
            });
        }

        private int getIcon(String icon) {

            int iconResId = 0;
            if (icon.equalsIgnoreCase(getString(R.string.st_autoplay_icon_key))) {
                iconResId = R.drawable.st_settings_icon_autoplay;
            } else if (icon.equalsIgnoreCase(getString(R.string.st_closed_caption_icon_key))) {
                iconResId = R.drawable.st_settings_icon_cc;
            } else if (icon.equalsIgnoreCase(getString(R.string.st_manage_subscription_icon_key))) {
                iconResId = R.drawable.st_settings_icon_manage_subscription;
            } else if (icon.equalsIgnoreCase(getString(R.string.st_account_icon_key))) {
                iconResId = R.drawable.st_settings_icon_account;
            } else if (icon.equalsIgnoreCase(getString(R.string.st_faq_icon_key))) {
                iconResId = R.drawable.st_settings_icon_faq;
            } else if (icon.equalsIgnoreCase(getString(R.string.st_contact_icon_key))) {
                iconResId = R.drawable.st_settings_icon_contact;
            } else if (icon.equalsIgnoreCase(getString(R.string.st_signin_icon_key))) {
                iconResId = R.drawable.st_settings_icon_signin;
            } else if (icon.equalsIgnoreCase(getString(R.string.st_signout_icon_key))) {
                iconResId = R.drawable.st_settings_icon_signout;
            }else if (icon.equalsIgnoreCase(getString(R.string.st_about_us_icon_key))) {
                iconResId = R.drawable.st_settings_icon_about_us;
            }else if (icon.equalsIgnoreCase(getString(R.string.st_privacy_policy_icon_key))) {
                iconResId = R.drawable.st_setting_icon_privacy_policy;
            }
            return iconResId;
        }

        private boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
            int tryFocusItem = selectedPosition + direction;
            if (tryFocusItem >= 0 && tryFocusItem < getItemCount()) {
                notifyItemChanged(selectedPosition);
                selectedPosition = tryFocusItem;
                notifyItemChanged(selectedPosition);
                lm.scrollToPosition(selectedPosition);
                return true;
            }
            return true;
        }

        @Override
        public int getItemCount() {
            int totalCount = 0;
            if (null != navigationSubItemList)
                totalCount = navigationSubItemList.size();
            return totalCount;
        }

        private void setFocusOnSelectedPage() {
            int selectedPos = getSelectedPagePosition();
            notifyItemChanged(selectedPosition);
            selectedPosition = selectedPos;
            notifyItemChanged(selectedPosition);
        }

        class STNavItemHolder extends RecyclerView.ViewHolder {
            TextView navItemView;
            ImageView navImageView;
            RelativeLayout navItemLayout;

            public STNavItemHolder(View itemView) {
                super(itemView);
                setTypeFaceValue(appCMSPresenter);
                navItemView = (TextView) itemView.findViewById(R.id.nav_item_label);
                navItemView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), getActivity().getString(R.string.lato_medium)));
                navImageView = (ImageView) itemView.findViewById(R.id.nav_item_image);
                navItemLayout = (RelativeLayout) itemView.findViewById(R.id.nav_item_layout);
//                navItemLayout.setBackground(Utils.getNavigationSelector(mContext, appCMSPresenter, false));
                navItemLayout.setBackground(Utils.getMenuSelector(mContext, appCMSPresenter.getAppCtaBackgroundColor(),
                        appCMSPresenter.getAppCMSMain().getBrand().getCta().getSecondary().getBorder().getColor()));
                navItemView.setTextColor(Color.parseColor(Utils.getTextColor(mContext, appCMSPresenter)));
                navItemView.setTypeface(semiBoldTypeFace);
                navItemLayout.setOnFocusChangeListener((view, hasFocus) -> {

                    selectedPosition = (int) navItemView.getTag(R.string.item_position);

                    /*if (hasFocus) {
                        ((ViewGroup) navImageView.getParent()).setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        ((ViewGroup) navImageView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }*/
                });

                navItemLayout.setOnKeyListener((view, i, keyEvent) -> {
                    int keyCode = keyEvent.getKeyCode();
                    int action = keyEvent.getAction();
                    if (action == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_LEFT:
                                if (isStartPosition()) {
                                    return true;
                                }
                                break;
                            case KeyEvent.KEYCODE_DPAD_RIGHT:
                                if (isEndPosition()) {
                                    return true;
                                }
                                break;
                            case KeyEvent.KEYCODE_DPAD_DOWN:
                            case KeyEvent.KEYCODE_DPAD_UP:
                            case KeyEvent.KEYCODE_MENU:
                                return true;
                        }
                    }else if(action == KeyEvent.ACTION_UP){
                        switch(keyCode){
                            case KeyEvent.KEYCODE_MENU:
                                return true;
                        }
                    }
                    return false;
                });
            }
        }
    }

    class NavigationSubItem {
        String pageId;
        String title;
        String url;
        AccessLevels accessLevels;
        String icon;
    }

}




