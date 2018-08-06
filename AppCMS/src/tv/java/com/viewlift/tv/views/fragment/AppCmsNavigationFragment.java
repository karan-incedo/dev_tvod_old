package com.viewlift.tv.views.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationFooter;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.models.data.appcms.ui.android.NavigationUser;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.activity.AppCmsBaseActivity;
import com.viewlift.views.binders.AppCMSBinder;

import java.util.ArrayList;
import java.util.List;

import static com.viewlift.models.data.appcms.ui.AppCMSUIKeyType.ANDROID_HISTORY_NAV_KEY;
import static com.viewlift.models.data.appcms.ui.AppCMSUIKeyType.ANDROID_WATCHLIST_NAV_KEY;
import static com.viewlift.models.data.appcms.ui.AppCMSUIKeyType.ANDROID_WATCHLIST_SCREEN_KEY;

/**
 * Created by nitin.tyagi on 6/27/2017.
 */

public class AppCmsNavigationFragment extends Fragment {

    private static OnNavigationVisibilityListener navigationVisibilityListener;
    private static AppCmsSubNavigationFragment.OnSubNavigationVisibilityListener subNavigationVisibilityListener;
    private RecyclerView mRecyclerView;
    private int textColor = -1;
    private int bgColor = -1;
    private Typeface extraBoldTypeFace, semiBoldTypeFace;
    private Component extraBoldComp, semiBoldComp;
    private AppCMSBinder appCmsBinder;
    private TextView navMenuSubscriptionModule;
    private AppCMSPresenter appCMSPresenter;
    private String mSelectedPageId = null;
    private int selectedPosition = -1;
    private ArrayList<NavigationPrimary> navigationSubItemList;

    public static AppCmsNavigationFragment newInstance(Context context,
                                                       OnNavigationVisibilityListener listener,
                                                       AppCMSBinder appCMSBinder,
                                                       int textColor,
                                                       int bgColor) {

        AppCmsNavigationFragment fragment = new AppCmsNavigationFragment();
        Bundle args = new Bundle();
        args.putBinder(context.getString(R.string.fragment_page_bundle_key), appCMSBinder);
        args.putInt(context.getString(R.string.app_cms_text_color_key), textColor);
        args.putInt(context.getString(R.string.app_cms_bg_color_key), bgColor);
        fragment.setArguments(args);
        navigationVisibilityListener = listener;
        return fragment;
    }

    public TextView getNavMenuSubscriptionModule() {
        return navMenuSubscriptionModule;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_navigation, null);

        Bundle args = getArguments();
        textColor = args.getInt(getResources().getString(R.string.app_cms_text_color_key));
        bgColor = args.getInt(getResources().getString(R.string.app_cms_bg_color_key));

        AppCMSBinder appCMSBinder = ((AppCMSBinder) args.getBinder(getResources().getString(R.string.fragment_page_bundle_key)));
        this.appCmsBinder = appCMSBinder;
        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
        if(!appCMSPresenter.isLeftNavigationEnabled())
            view.setBackgroundColor(bgColor);
        TextView navMenuTile = (TextView) view.findViewById(R.id.nav_menu_title);
        View navTopLine = view.findViewById(R.id.nav_top_line);
        navMenuSubscriptionModule = (TextView) view.findViewById(R.id.nav_menu_subscription_module);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.navRecylerView);

        if(appCMSPresenter.isLeftNavigationEnabled()){
            mRecyclerView
                    .setLayoutManager(new LinearLayoutManager(getActivity(),
                            LinearLayoutManager.VERTICAL,
                            false));
            mRecyclerView.setPadding(0,0,0,0);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();
            layoutParams.setMargins(40, 250, 0,0);
        }else{
            mRecyclerView
                    .setLayoutManager(new LinearLayoutManager(getActivity(),
                            LinearLayoutManager.HORIZONTAL,
                            false));
            ((RelativeLayout.LayoutParams)mRecyclerView.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if(appCMSPresenter.isLeftNavigationEnabled()){
            LeftNavigationAdapter navigationAdapter = new LeftNavigationAdapter(getActivity(), textColor, bgColor,
                    appCMSBinder.getNavigation(),
                    appCMSBinder.isUserLoggedIn(),
                    appCMSPresenter);

            mRecyclerView.setAdapter(navigationAdapter);
            navMenuTile.setVisibility(View.GONE);
            navTopLine.setVisibility(View.GONE);
            navMenuSubscriptionModule.setVisibility(View.GONE);
        }
        else if (appCMSPresenter.getTemplateType().equals(AppCMSPresenter.TemplateType.ENTERTAINMENT)) {
            NavigationAdapter navigationAdapter = new NavigationAdapter(getActivity(), textColor, bgColor,
                    appCMSBinder.getNavigation(),
                    appCMSBinder.isUserLoggedIn(),
                    appCMSPresenter);

            mRecyclerView.setAdapter(navigationAdapter);
            navMenuTile.setVisibility(View.GONE);
            navTopLine.setVisibility(View.GONE);
            navMenuSubscriptionModule.setVisibility(View.GONE);
            view.findViewById(R.id.left_menu_app_logo).setVisibility(View.INVISIBLE);
        } else {
            STNavigationAdapter navigationAdapter = new STNavigationAdapter(
                    getActivity(),
                    textColor,
                    bgColor,
                    appCMSBinder.getNavigation(),
                    appCMSBinder.isUserLoggedIn(),
                    appCMSPresenter);

            mRecyclerView.setAdapter(navigationAdapter);
            navMenuTile.setText("Menu");
            navMenuTile.setTextColor(Color.parseColor(appCMSPresenter.getAppTextColor()));
            navMenuTile.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), getActivity().getString(R.string.lato_regular)));
            navMenuTile.setVisibility(View.VISIBLE);
            navTopLine.setVisibility(View.VISIBLE);
            navTopLine.setBackgroundColor(Color.parseColor(Utils.getFocusColor(getActivity(),appCMSPresenter)));
            view.findViewById(R.id.left_menu_app_logo).setVisibility(View.INVISIBLE);

            if (appCMSPresenter.isAppSVOD()) {
                String message;
                if (null != appCMSPresenter && null != appCMSPresenter.getNavigation()
                        && null != appCMSPresenter.getNavigation().getSettings()
                        && null != appCMSPresenter.getNavigation().getSettings().getPrimaryCta()) {
                    message = appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getBannerText() +
                            appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getCtaText();
                } else {
                    message = getResources().getString(R.string.watch_live_text);
                }

                navMenuSubscriptionModule.setText(message);
                navMenuSubscriptionModule.setBackground(Utils.setButtonBackgroundSelector(getActivity(),
                        Color.parseColor(Utils.getFocusColor(getActivity(),appCMSPresenter))
                        ,null,appCMSPresenter ));
                navMenuSubscriptionModule.setTextColor(Color.parseColor(Utils.getTextColor(getActivity(),appCMSPresenter)));

                toggleVisibilityOfSubscriptionModule();

                navMenuSubscriptionModule.setOnClickListener(v -> {

                    if (!appCMSPresenter.isUserLoggedIn() && appCMSPresenter.isNetworkConnected()) {
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
                    } else {
                        appCMSPresenter.openTVErrorDialog(
                                getActivity().getString(R.string.subscription_not_purchased),
                                getActivity().getString(R.string.subscription), false);
                    }
                });
            }
        }
        return view;
    }

    private void toggleVisibilityOfSubscriptionModule() {
        if(appCMSPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS && !appCMSPresenter.isLeftNavigationEnabled()) {
            appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                try {
                    if (appCMSUserSubscriptionPlanResult != null) {
                        String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                        if (subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION")) {
                            navMenuSubscriptionModule.setVisibility(View.GONE);
                        } else {
                            navMenuSubscriptionModule.setVisibility(View.VISIBLE);
                        }
                    } else {
                        navMenuSubscriptionModule.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    navMenuSubscriptionModule.setVisibility(View.VISIBLE);
                }
            });
        }
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
        //Log.d("" ,"Navigation setSelectedPageId = " + mSelectedPageId );
    }

    public void notifyDataSetInvalidate() {
        if (null != mRecyclerView && null != mRecyclerView.getAdapter()) {
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
        if (appCMSPresenter.isAppSVOD()) {
            toggleVisibilityOfSubscriptionModule();
        }
    }

    private boolean isEndPosition() {
        return selectedPosition == appCmsBinder.getNavigation().getNavigationPrimary().size() - 1;
    }

    private boolean isStartPosition() {
        return (selectedPosition == 0);
    }

    public interface OnNavigationVisibilityListener {
        void showNavigation(boolean shouldShow);
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
                                 boolean userLoggedIn,
                                 AppCMSPresenter appCMSPresenter) {
            mContext = activity;
            this.textColor = textColor;
            this.bgColor = bgColor;
            this.navigation = navigation;
            this.isuserLoggedIn = userLoggedIn;
            this.appCmsPresenter = appCMSPresenter;
            //  setSelectorColor();
        }


        public Object getItem(int i) {
            return navigation.getNavigationPrimary().get(i);
        }

        @Override
        public NavItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.navigation_item, parent, false);
            NavItemHolder navItemHolder = new NavItemHolder(view);
            return navItemHolder;
        }

        @Override
        public void onBindViewHolder(NavItemHolder holder, final int position) {
            final NavigationPrimary primary = (NavigationPrimary) getItem(position);
            holder.navItemView.setText(primary.getTitle().toUpperCase());
            holder.navItemView.setTag(R.string.item_position, position);
            //Log.d("NavigationAdapter", primary.getTitle().toString());


            if (null != mSelectedPageId) {
                if (primary.getPageId().equalsIgnoreCase(mSelectedPageId)) {
                    holder.navItemlayout.requestFocus();
                 } else {
                    holder.navItemlayout.clearFocus();
                 }
            }

            holder.navItemlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigationVisibilityListener.showNavigation(false);
                    //getActivity().sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
                    Utils.pageLoading(true, getActivity());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (primary.getTitle().equalsIgnoreCase(getString(R.string.app_cms_search_label))) {
                                appCmsPresenter.openSearch(primary.getPageId(), primary.getTitle());
                                Utils.pageLoading(false, getActivity());
                            } else if (primary.getPageId().equalsIgnoreCase(getString(R.string.app_cms_my_profile_label,
                                    getString(R.string.profile_label)))) {
                                NavigationUser navigationUser = getNavigationUser();
                                if (navigationUser != null) {
                                    if (ANDROID_WATCHLIST_NAV_KEY.equals(appCmsBinder
                                            .getJsonValueKeyMap().get(navigationUser.getTitle()))
                                            || ANDROID_WATCHLIST_SCREEN_KEY.equals(appCmsBinder
                                            .getJsonValueKeyMap().get(navigationUser.getTitle()))) {
                                        appCmsPresenter.navigateToWatchlistPage(
                                                navigationUser.getPageId(),
                                                navigationUser.getTitle(),
                                                navigationUser.getUrl(),
                                                false);
                                    }  else if (ANDROID_HISTORY_NAV_KEY.equals(appCmsBinder.getJsonValueKeyMap()
                                            .get(navigationUser.getTitle()))) {
                                        // appCmsPresenter.showLoadingDialog(true);
                                        appCmsPresenter.navigateToHistoryPage(
                                                navigationUser.getPageId(),
                                                navigationUser.getTitle(),
                                                navigationUser.getUrl(),
                                                false);
                                    }else {
                                        appCmsPresenter.navigateToTVPage(
                                                navigationUser.getPageId(),
                                                navigationUser.getTitle(),
                                                navigationUser.getUrl(),
                                                false,
                                                Uri.EMPTY,
                                                false,
                                                false,
                                                false);
                                    }
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                                    Utils.pageLoading(false, getActivity());
                                }

                            } else if (!appCmsPresenter.navigateToTVPage(primary.getPageId(),
                                    primary.getTitle(),
                                    primary.getUrl(),
                                    false,
                                    null,
                                    true,
                                    false,
                                    false)) {

                            }
                        }
                    }, 500);
                }
            });
        }

        private NavigationUser getNavigationUser() {
            List<NavigationUser> navigationUserList = navigation.getNavigationUser();
            for (NavigationUser navigationUser : navigationUserList) {
                if (appCmsPresenter.isUserLoggedIn() && navigationUser.getAccessLevels().getLoggedIn()) {
                    return navigationUser;
                } else if (!appCmsPresenter.isUserLoggedIn() && navigationUser.getAccessLevels().getLoggedOut()) {
                    return navigationUser;
                }
            }
            return null;
        }

        @Override
        public int getItemCount() {
            int totalCount = 0;
            if (null != this.navigation && null != navigation.getNavigationPrimary())
                totalCount = this.navigation.getNavigationPrimary().size();
            return totalCount;
        }


        class NavItemHolder extends RecyclerView.ViewHolder {
            TextView navItemView;
            RelativeLayout navItemlayout;

            public NavItemHolder(View itemView) {
                super(itemView);
                setTypeFaceValue(appCmsPresenter);
                navItemView = (TextView) itemView.findViewById(R.id.nav_item_label);
                navItemlayout = (RelativeLayout) itemView.findViewById(R.id.nav_item_layout);
                navItemlayout.setBackground(Utils.getNavigationSelector(mContext, appCmsPresenter, false, bgColor));
                // navItemlayout.setBackgroundColor(bgColor);
                navItemView.setTextColor(Color.parseColor(Utils.getTextColor(mContext, appCmsPresenter)));
                navItemView.setTypeface(semiBoldTypeFace);
                navItemlayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {

                        String text = navItemView.getText().toString();
                        int position = (int) navItemView.getTag(R.string.item_position);
                        selectedPosition = position;

                        //Log.d("TAG","Nav position = "+position);
                        if (hasFocus) {
                            navItemView.setText(text.toUpperCase());
                            navItemView.setTypeface(extraBoldTypeFace);
                        } else {
                            navItemView.setText(text.toUpperCase());
                            navItemView.setTypeface(semiBoldTypeFace);
                        }
                    }
                });

                navItemlayout.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
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
                            }
                        }
                        return false;
                    }
                });
            }
        }
    }

    class STNavigationAdapter extends RecyclerView.Adapter<STNavigationAdapter.STNavItemHolder> {
        private Context mContext;
        private int textColor;
        private int bgColor;
        private Navigation navigation;
        private boolean isuserLoggedIn;
        private AppCMSPresenter appCmsPresenter;

        public STNavigationAdapter(Context activity,
                                   int textColor,
                                   int bgColor,
                                   Navigation navigation,
                                   boolean userLoggedIn,
                                   AppCMSPresenter appCMSPresenter) {
            mContext = activity;
            this.textColor = textColor;
            this.bgColor = bgColor;
            this.navigation = navigation;
            this.isuserLoggedIn = userLoggedIn;
            this.appCmsPresenter = appCMSPresenter;
        }


        public Object getItem(int i) {
            return navigation.getNavigationPrimary().get(i);
        }

        @Override
        public STNavItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.st_navigation_item, parent, false);
            return new STNavItemHolder(view);
        }

        @Override
        public void onBindViewHolder(STNavItemHolder holder, final int position) {
            final NavigationPrimary primary = (NavigationPrimary) getItem(position);
            holder.navItemView.setText(primary.getTitle().toUpperCase());
            holder.navItemView.setTag(R.string.item_position, position);
            holder.navItemView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), getActivity().getString(R.string.lato_medium)));
            if (primary.getIcon() != null) {
                holder.navImageView.setImageResource(Utils.getIcon(primary.getIcon(),mContext));
                if(null != holder.navImageView.getDrawable()) {
                    holder.navImageView.getDrawable().setTint(Utils.getComplimentColor(appCmsPresenter.getGeneralBackgroundColor()));
                    holder.navImageView.getDrawable().setTintMode(PorterDuff.Mode.MULTIPLY);
                }
            }else{
                holder.navImageView.setImageResource(android.R.color.transparent);
            }
            if (null != mSelectedPageId) {
                if (null != primary.getPageId() && primary.getPageId().equalsIgnoreCase(mSelectedPageId)) {
                    holder.navItemLayout.requestFocus();
                } else {
                    holder.navItemLayout.clearFocus();
                }
            }


            holder.navItemLayout.setOnClickListener(view -> {
                final boolean[] showNavigation = {false};
                Utils.pageLoading(true, getActivity());

                new Handler().postDelayed(() -> {
                    if(null == primary.getPageId()){
                        Toast.makeText(mContext, mContext.getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                        Utils.pageLoading(false, getActivity());
                        return;
                    }
                    /*Search*/
                    if (primary.getTitle().equalsIgnoreCase(getString(R.string.app_cms_search_label))) {
                        appCmsPresenter.openSearch(primary.getPageId(), primary.getTitle());
                        Utils.pageLoading(false, getActivity());
                        //navigationVisibilityListener.showNavigation(false);
                    }

                    /*Settings*/
                    else if (primary.getTitle().equalsIgnoreCase(getString(R.string.app_cms_settings_page_tag))) {
                        createSubNavigationListForST();
                        appCmsPresenter.navigateToSubNavigationPage(
                                primary.getPageId(),
                                primary.getTitle(),
                                primary.getUrl(),
                                primary,
                                navigationSubItemList,
                                false
                        );

                    }

                    /*Profile/ My snag, my hoichoi etc*/
                    else if (getString(R.string.app_cms_my_profile_label,
                            getString(R.string.profile_label)).equalsIgnoreCase(primary.getPageId())) {

                        NavigationUser navigationUser = getNavigationUser();
                        //Log.d("","Selected Title = "+navigationUser.getTitle());
                        if (ANDROID_WATCHLIST_NAV_KEY.equals(appCmsBinder
                                .getJsonValueKeyMap().get(navigationUser.getTitle()))
                        || ANDROID_WATCHLIST_SCREEN_KEY.equals(appCmsBinder
                                .getJsonValueKeyMap().get(navigationUser.getTitle()))) {
                            appCmsPresenter.navigateToWatchlistPage(
                                    navigationUser.getPageId(),
                                    navigationUser.getTitle(),
                                    navigationUser.getUrl(),
                                    false);
                        } else {
                            appCmsPresenter.navigateToTVPage(
                                    navigationUser.getPageId(),
                                    navigationUser.getTitle(),
                                    navigationUser.getUrl(),
                                    false,
                                    Uri.EMPTY,
                                    false,
                                    false,
                                    false);
                        }
                    }
                    //This code is for SubNavigation items like Teams in MSE. So we are treating here that if primary.getItems() is not null then its a subnavigation.
                    else if (!TextUtils.isEmpty(primary.getPageId())
                            && appCmsPresenter.getPageType(primary.getPageId()).contains("Sub Navigation")
                            && primary.getItems() != null && primary.getItems().size() > 0) {
                       // navigationVisibilityListener.showNavigation(false);
//                        subNavigationVisibilityListener.showSubNavigation(true, true);
                        appCmsPresenter.sendGaScreen(primary.getTitle() + " Navigation Page");
//                        Utils.pageLoading(false, getActivity());
                        if(primary.getPageId() == null){
                            primary.setPageId(primary.getItems().get(0).getPageId());
                        }
                        appCmsPresenter.navigateToSubNavigationPage(
                                primary.getPageId(),
                                primary.getTitle(),
                                primary.getUrl(),
                                primary,
                                primary.getItems(),
                                false
                        );

                    }

                    /*Watchlist*/
                    else if (primary.getTitle().equalsIgnoreCase(getString(R.string.app_cms_page_watchlist_title))
                            || primary.getTitle().contains("Watchlist")) {
                        if (appCmsPresenter.isUserLoggedIn()) {
                            //navigationVisibilityListener.showNavigation(false);
                            Utils.pageLoading(true, getActivity());
                            appCmsPresenter.navigateToWatchlistPage(
                                    primary.getPageId(),
                                    primary.getTitle(),
                                    primary.getUrl(),
                                    false);
                        } else /*user not logged in*/ {
                            Utils.pageLoading(false, getActivity());
                            ClearDialogFragment newFragment = Utils.getClearDialogFragment(
                                    mContext,
                                    appCMSPresenter,
                                    mContext.getResources().getDimensionPixelSize(R.dimen.text_clear_dialog_width),
                                    mContext.getResources().getDimensionPixelSize(R.dimen.text_add_to_watchlist_sign_in_dialog_height),
                                    mContext.getString(R.string.sign_in_text),
                                    mContext.getString(R.string.open_watchlist_dialog_text),
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
                    }

                    /*History*/
                    else if (primary.getTitle().equalsIgnoreCase(getString(R.string.app_cms_page_history_title))
                            || primary.getTitle().contains("History")) {
                        if (appCmsPresenter.isUserLoggedIn()) {
                            //navigationVisibilityListener.showNavigation(false);
                            Utils.pageLoading(true, getActivity());
                            appCmsPresenter.navigateToHistoryPage(
                                    primary.getPageId(),
                                    primary.getTitle(),
                                    primary.getUrl(),
                                    false);
                        } else /*user not logged in*/ {
                            Utils.pageLoading(false, getActivity());
                            ClearDialogFragment newFragment = Utils.getClearDialogFragment(
                                    mContext,
                                    appCMSPresenter,
                                    mContext.getResources().getDimensionPixelSize(R.dimen.text_clear_dialog_width),
                                    mContext.getResources().getDimensionPixelSize(R.dimen.text_add_to_watchlist_sign_in_dialog_height),
                                    mContext.getString(R.string.sign_in_text),
                                    mContext.getString(R.string.open_history_dialog_text),
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
                    } else {
                        appCmsPresenter.navigateToTVPage(primary.getPageId(),
                                primary.getTitle(),
                                primary.getUrl(),
                                false,
                                null,
                                true,
                                false,
                                false);
                       // navigationVisibilityListener.showNavigation(false);
                    }

                }, 500);
            });
        }

        private NavigationUser getNavigationUser() {
            List<NavigationUser> navigationUserList = navigation.getNavigationUser();
            for (NavigationUser navigationUser : navigationUserList) {
                if (appCmsPresenter.isUserLoggedIn() && navigationUser.getAccessLevels().getLoggedIn()) {
                    return navigationUser;
                } else if (!appCmsPresenter.isUserLoggedIn() && navigationUser.getAccessLevels().getLoggedOut()) {
                    return navigationUser;
                }
            }
            return null;
        }

        @Override
        public int getItemCount() {
            int totalCount = 0;
            if (null != this.navigation && null != navigation.getNavigationPrimary())
                totalCount = this.navigation.getNavigationPrimary().size();
            return totalCount;
        }


        class STNavItemHolder extends RecyclerView.ViewHolder {
            TextView navItemView;
            ImageView navImageView;
            RelativeLayout navItemLayout;

            public STNavItemHolder(View itemView) {
                super(itemView);
                setTypeFaceValue(appCmsPresenter);
                navItemView = (TextView) itemView.findViewById(R.id.nav_item_label);
                navImageView = (ImageView) itemView.findViewById(R.id.nav_item_image);
                navItemLayout = (RelativeLayout) itemView.findViewById(R.id.nav_item_layout);
                navItemLayout.setBackground(Utils.getMenuSelector(mContext, appCmsPresenter.getAppCtaBackgroundColor(),
                        /*Utils.getComplimentColor(Color.parseColor(appCmsPresenter.getAppBackgroundColor()))*/
                        appCMSPresenter.getAppCMSMain().getBrand().getCta().getSecondary().getBorder().getColor()));
                navItemView.setTextColor(Color.parseColor(Utils.getTextColor(mContext, appCmsPresenter)));
                navItemView.setTypeface(semiBoldTypeFace);
                navItemLayout.setOnFocusChangeListener((view, hasFocus) -> {

                    selectedPosition = (int) navItemView.getTag(R.string.item_position);

                   /* if (hasFocus) {
                        navImageView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        navImageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
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
                        }
                    }
                    return false;
                });
            }
        }
    }

    class LeftNavigationAdapter extends RecyclerView.Adapter<LeftNavigationAdapter.NavItemHolder> {
        private Context mContext;
        private LayoutInflater inflater;
        private int textColor;
        private int bgColor;
        private Navigation navigation;
        private boolean isuserLoggedIn;
        private AppCMSPresenter appCmsPresenter;
        private int currentNavPos;

        public LeftNavigationAdapter(Context activity,
                                 int textColor,
                                 int bgColor,
                                 Navigation navigation,
                                 boolean userLoggedIn,
                                 AppCMSPresenter appCMSPresenter) {
            mContext = activity;
            this.textColor = textColor;
            this.bgColor = bgColor;
            this.navigation = navigation;
            this.isuserLoggedIn = userLoggedIn;
            this.appCmsPresenter = appCMSPresenter;
        }


        public Object getItem(int i) {
            return navigation.getNavigationPrimary().get(i);
        }

        @Override
        public NavItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.left_navigation_item, parent, false);
            NavItemHolder navItemHolder = new NavItemHolder(view);
            return navItemHolder;
        }

        @Override
        public void onBindViewHolder(NavItemHolder holder, final int position) {
            final NavigationPrimary primary = (NavigationPrimary) getItem(position);
            holder.navItemView.setText(primary.getTitle());
            holder.navItemView.setTag(R.string.item_position, position);
            holder.navItemView.setTextSize(getResources().getDimension(R.dimen.appcms_tv_leftnavigation_textSize));

            holder.navIconView.setImageResource(Utils.getIcon(primary.getIcon(),mContext));
            if (primary.getIcon() != null) {
                holder.navIconView.setImageResource(Utils.getIcon(primary.getIcon(),mContext));
                if(null != holder.navIconView.getDrawable()) {
                    holder.navIconView.getDrawable().setTint(Utils.getComplimentColor(appCmsPresenter.getGeneralBackgroundColor()));
                    holder.navIconView.getDrawable().setTintMode(PorterDuff.Mode.MULTIPLY);
                }
            }else{
                holder.navIconView.setImageResource(android.R.color.transparent);
            }

            if (null != mSelectedPageId) {
                if (primary.getPageId().equalsIgnoreCase(mSelectedPageId)) {
                    holder.navItemlayout.requestFocus();
                } else {
                    holder.navItemlayout.clearFocus();
                }
            }


            holder.navItemlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //navigationVisibilityListener.showNavigation(false);
                    Utils.pageLoading(true, getActivity());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (primary.getTitle().equalsIgnoreCase(getString(R.string.app_cms_search_label))) {
                                appCmsPresenter.openSearch(primary.getPageId(), primary.getTitle());
                                Utils.pageLoading(false, getActivity());
                            } else if (primary.getPageId().equalsIgnoreCase(getString(R.string.app_cms_my_profile_label,
                                    getString(R.string.profile_label)))) {
                                NavigationUser navigationUser = getNavigationUser();
                                if(null != getActivity() && getActivity() instanceof AppCmsBaseActivity){
                                    ((AppCmsBaseActivity) getActivity()).setProfileFirstTime(true);
                                }
                                if (navigationUser != null) {
                                    if (ANDROID_WATCHLIST_NAV_KEY.equals(appCmsBinder
                                            .getJsonValueKeyMap().get(navigationUser.getTitle()))
                                            || ANDROID_WATCHLIST_SCREEN_KEY.equals(appCmsBinder
                                            .getJsonValueKeyMap().get(navigationUser.getTitle()))) {
                                        appCmsPresenter.navigateToWatchlistPage(
                                                navigationUser.getPageId(),
                                                navigationUser.getTitle(),
                                                navigationUser.getUrl(),
                                                false);
                                    }  else if (ANDROID_HISTORY_NAV_KEY.equals(appCmsBinder.getJsonValueKeyMap()
                                            .get(navigationUser.getTitle()))) {
                                        // appCmsPresenter.showLoadingDialog(true);
                                        appCmsPresenter.navigateToHistoryPage(
                                                navigationUser.getPageId(),
                                                navigationUser.getTitle(),
                                                navigationUser.getUrl(),
                                                false);
                                    }else {
                                        appCmsPresenter.navigateToTVPage(
                                                navigationUser.getPageId(),
                                                navigationUser.getTitle(),
                                                navigationUser.getUrl(),
                                                false,
                                                Uri.EMPTY,
                                                false,
                                                false,
                                                false);
                                    }
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                                    Utils.pageLoading(false, getActivity());
                                }

                            } //This code is for SubNavigation items like Teams in MSE. So we are treating here that if primary.getItems() is not null then its a subnavigation.
                            else if (null != appCmsPresenter.getPageType(primary.getPageId())
                                    && appCmsPresenter.getPageType(primary.getPageId()).contains("Sub Navigation")
                                    && primary.getItems() != null && primary.getItems().size() > 0) {
                                appCmsPresenter.sendGaScreen(primary.getTitle() + " Navigation Page");
                                if (primary.getPageId() == null) {
                                    primary.setPageId(primary.getItems().get(0).getPageId());
                                }
                                appCmsPresenter.navigateToSubNavigationPage(
                                        primary.getPageId(),
                                        primary.getTitle(),
                                        primary.getUrl(),
                                        primary,
                                        primary.getItems(),
                                        false
                                );
                            }
                         /*Settings*/
                    else if (primary.getTitle().equalsIgnoreCase(getString(R.string.app_cms_settings_page_tag))) {
                                createSubNavigationListForST();
                                appCmsPresenter.navigateToSubNavigationPage(
                                        primary.getPageId(),
                                        primary.getTitle(),
                                        primary.getUrl(),
                                        primary,
                                        navigationSubItemList,
                                        false
                                );

                                }
                            else if (!appCmsPresenter.navigateToTVPage(primary.getPageId(),
                                    primary.getTitle(),
                                    primary.getUrl(),
                                    false,
                                    null,
                                    true,
                                    false,
                                    false)) {

                            }
                        }
                    }, 500);
                }
            });
        }

        private NavigationUser getNavigationUser() {
            List<NavigationUser> navigationUserList = navigation.getNavigationUser();
            for (NavigationUser navigationUser : navigationUserList) {
                if (appCmsPresenter.isUserLoggedIn() && navigationUser.getAccessLevels().getLoggedIn()) {
                    return navigationUser;
                } else if (!appCmsPresenter.isUserLoggedIn() && navigationUser.getAccessLevels().getLoggedOut()) {
                    return navigationUser;
                }
            }
            return null;
        }

        @Override
        public int getItemCount() {
            int totalCount = 0;
            if (null != this.navigation && null != navigation.getNavigationPrimary())
                totalCount = this.navigation.getNavigationPrimary().size();
            return totalCount;
        }


        class NavItemHolder extends RecyclerView.ViewHolder {
            TextView navItemView;
            LinearLayout navItemlayout;
            ImageView navIconView;

            public NavItemHolder(View itemView) {
                super(itemView);
                setTypeFaceValue(appCmsPresenter);
                navItemView = (TextView) itemView.findViewById(R.id.nav_item_label);
                navItemlayout = (LinearLayout) itemView.findViewById(R.id.nav_item_layout);
                navIconView = (ImageView) itemView.findViewById(R.id.nav_item_image);
                navItemlayout.setBackground(Utils.getNavigationSelector(mContext, appCmsPresenter, false, bgColor));

                navItemView.setTextColor(Color.parseColor(Utils.getTextColor(mContext, appCmsPresenter)));
                navItemlayout.setAlpha(0.4F);
                navItemlayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {

                        String text = navItemView.getText().toString();
                        int position = (int) navItemView.getTag(R.string.item_position);
                        selectedPosition = position;

                        //Log.d("TAG","Nav position = "+position);
                        if (hasFocus) {
                            navItemlayout.setAlpha(1.0F);
                        } else {
                            navItemlayout.setAlpha(0.4F);
                        }
                    }
                });

                navItemlayout.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        int keyCode = keyEvent.getKeyCode();
                        int action = keyEvent.getAction();
                        if (action == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_UP:
                                    if (isStartPosition()) {
                                        return true;
                                    }
                                    break;
                                case KeyEvent.KEYCODE_DPAD_DOWN:
                                    if (isEndPosition()) {
                                        return true;
                                    }
                                    break;
                            }
                        }
                        return false;
                    }
                });
            }
        }
    }


    private void createSubNavigationListForST() {
        if (null == navigationSubItemList) {
            navigationSubItemList = new ArrayList<>();
        }

        navigationSubItemList.clear();
        NavigationPrimary navigationSubItem1 = new NavigationPrimary();
        navigationSubItem1.setIcon(getString(R.string.st_autoplay_icon_key));
        if (appCMSPresenter.getAutoplayEnabledUserPref(getActivity())) {
            navigationSubItem1.setTitle("AUTOPLAY ON");
        } else {
            navigationSubItem1.setTitle("AUTOPLAY OFF");
        }
        navigationSubItemList.add(navigationSubItem1);

        navigationSubItem1 = new NavigationPrimary();
        navigationSubItem1.setIcon(getString(R.string.st_closed_caption_icon_key));
        if (appCMSPresenter.getClosedCaptionPreference()) {
            navigationSubItem1.setTitle("CLOSED CAPTION ON");
        } else {
            navigationSubItem1.setTitle("CLOSED CAPTION OFF");
        }
        navigationSubItemList.add(navigationSubItem1);

        if (appCMSPresenter.isAppSVOD()) {
            if (appCMSPresenter.isUserLoggedIn()) {
                navigationSubItem1 = new NavigationPrimary();
                navigationSubItem1.setTitle("MANAGE SUBSCRIPTION");
                navigationSubItem1.setIcon(getString(R.string.st_manage_subscription_icon_key));
                navigationSubItemList.add(navigationSubItem1);
            } else /*Guest User*/{
                navigationSubItem1 = new NavigationPrimary();
                navigationSubItem1.setTitle("SUBSCRIBE NOW");
                navigationSubItem1.setIcon(getString(R.string.st_manage_subscription_icon_key));
                navigationSubItemList.add(navigationSubItem1);
            }
        }

        Navigation navigation = appCMSPresenter.getNavigation();
        for (int i = 0; i < navigation.getNavigationUser().size(); i++) {
            NavigationUser navigationUser =  navigation.getNavigationUser().get(i);
            if (/*(isUserLogin && navigationUser.getAccessLevels().getLoggedIn())
                        || (!isUserLogin && navigationUser.getAccessLevels().getLoggedOut())*/
                    !navigationUser.getAccessLevels().getLoggedOut()
                            && appCMSPresenter.isUserLoggedIn()) {
                NavigationPrimary navigationSubItem = new NavigationPrimary();
                navigationSubItem.setPageId(navigationUser.getPageId());
                navigationSubItem.setTitle(navigationUser.getTitle());
                navigationSubItem.setUrl(navigationUser.getUrl());
                navigationSubItem.setIcon(navigationUser.getIcon());
                navigationSubItem.setAccessLevels(navigationUser.getAccessLevels());
                navigationSubItemList.add(navigationSubItem);
            }
        }
            /*if (!isUserLogin) {
                return;
            }*/

        if(null != navigation && null != navigation.getNavigationFooter()) {
            for (int i = 0; i < navigation.getNavigationFooter().size(); i++) {
                NavigationFooter navigationFooter = navigation.getNavigationFooter().get(i);
                {
                    NavigationPrimary navigationSubItem = new NavigationPrimary();
                    navigationSubItem.setPageId(navigationFooter.getPageId());
                    navigationSubItem.setTitle(navigationFooter.getTitle());
                    navigationSubItem.setUrl(navigationFooter.getUrl());
                    navigationSubItem.setIcon(navigationFooter.getIcon());
                    navigationSubItem.setAccessLevels(navigationFooter.getAccessLevels());
                    if (null == navigationSubItemList) {
                        navigationSubItemList = new ArrayList<>();
                    }
                    navigationSubItemList.add(navigationSubItem);
                }
            }
        }
        navigationSubItem1 = new NavigationPrimary();
        if (appCMSPresenter.isUserLoggedIn()) {
            navigationSubItem1.setTitle("SIGN OUT");
            navigationSubItem1.setIcon(getString(R.string.st_signout_icon_key));
        } else {
            navigationSubItem1.setIcon(getString(R.string.st_signin_icon_key));
            navigationSubItem1.setTitle("SIGN IN");
        }
        navigationSubItemList.add(navigationSubItem1);
    }

}




