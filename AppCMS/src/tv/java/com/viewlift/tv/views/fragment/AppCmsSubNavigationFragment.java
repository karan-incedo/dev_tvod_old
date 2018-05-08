package com.viewlift.tv.views.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.android.AccessLevels;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationFooter;
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
import static com.viewlift.models.data.appcms.ui.AppCMSUIKeyType.ANDROID_WATCHLIST_SCREEN_KEY;

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
    private Navigation mNavigation;
    private AppCMSBinder mAppCMSBinder;
    private boolean isLoginDialogPage;
    private AppCMSPresenter appCMSPresenter;
    private boolean mShowTeams = false;
    private String mSelectedPageId = null;
    private int selectedPosition = 0;
    private List<NavigationSubItem> navigationSubItemList;
    private CustomAdapter customAdapter = null;

    public static AppCmsSubNavigationFragment newInstance(Context context,
                                                          OnSubNavigationVisibilityListener listener
    ) {
        mContext = context;
        AppCmsSubNavigationFragment fragment = new AppCmsSubNavigationFragment();
        navigationVisibilityListener = listener;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        navigationVisibilityListener.setSubnavExistence(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationVisibilityListener.setSubnavExistence(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_navigation, null);

        Bundle bundle = getArguments();
        mAppCMSBinder = (AppCMSBinder) bundle.getBinder("app_cms_binder");
        isLoginDialogPage = bundle.getBoolean(getString(R.string.is_login_dialog_page_key));

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
        if (!appCMSPresenter.isLeftNavigationEnabled())
            view.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()));

        View navTopLine = view.findViewById(R.id.nav_top_line);
        AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
        textColor = Color.parseColor(appCMSMain.getBrand().getCta().getPrimary().getTextColor());/*Color.parseColor("#F6546A");*/
        bgColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getBackgroundColor());//Color.parseColor("#660066");

        mNavigation = appCMSPresenter.getNavigation();

        navMenuTile = (TextView) view.findViewById(R.id.nav_menu_title);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.navRecylerView);


        if (appCMSPresenter.isLeftNavigationEnabled()) {
            mRecyclerView
                    .setLayoutManager(new LinearLayoutManager(getActivity(),
                            LinearLayoutManager.VERTICAL,
                            false));
        } else {
            mRecyclerView
                    .setLayoutManager(new LinearLayoutManager(getActivity(),
                            LinearLayoutManager.HORIZONTAL,
                            false));
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (appCMSPresenter.isLeftNavigationEnabled()) {
            customAdapter = new LeftNavigationAdapter(getActivity(), textColor, bgColor,
                    mNavigation,
                    appCMSPresenter);

            mRecyclerView.setAdapter(customAdapter);
            setFocusable(true);
            customAdapter.setFocusOnSelectedPage();
            navMenuTile.setVisibility(View.GONE);
            navTopLine.setVisibility(View.GONE);
        } else if (appCMSPresenter.getTemplateType().equals(AppCMSPresenter.TemplateType.ENTERTAINMENT)) {
            NavigationAdapter navigationAdapter = new NavigationAdapter(getActivity(), textColor, bgColor,
                    mNavigation,
                    appCMSPresenter);

            mRecyclerView.setAdapter(navigationAdapter);
            setFocusable(true);
            navigationAdapter.setFocusOnSelectedPage();
            navTopLine.setVisibility(View.GONE);
        }
        return view;
    }

    public CustomAdapter getCustomAdapter(){
        return customAdapter;
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
            if (hasFocus) {
                mRecyclerView.requestFocus();
            } else {
                mRecyclerView.clearFocus();
            }
        }
    }

    public void setFocusonSelectedItem(){
        if(null != customAdapter){
            customAdapter.setFocusOnSelectedPage();
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
                navigationSubItem.icon = navigationUser.getIcon();
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
                navigationSubItem.icon = navigationFooter.getIcon();
                if (null == navigationSubItemList) {
                    navigationSubItemList = new ArrayList<NavigationSubItem>();
                }
                navigationSubItemList.add(navigationSubItem);
            }
        }
    }

    public interface OnSubNavigationVisibilityListener {
        void showSubNavigation(boolean shouldShow);

        void setSubnavExistence(boolean isExist);
    }

    class CustomAdapter<C extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {
        @NonNull
        @Override
        public CustomAdapter.CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
            super.onBindViewHolder(holder, position, payloads);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            int totalCount = 0;
            if (null != navigationSubItemList)
                totalCount = navigationSubItemList.size();
            return totalCount;
        }

        public Object getItem(int i) {
            return navigationSubItemList.get(i);
        }

        protected boolean tryMoveSelection(RecyclerView.LayoutManager lm, int direction) {
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

        public void setFocusOnSelectedPage() {
            int selectedPos = getSelectedPagePosition();
            notifyItemChanged(selectedPosition);
            selectedPosition = selectedPos;
            notifyItemChanged(selectedPosition);
        }

        class CustomHolder extends RecyclerView.ViewHolder {
            public CustomHolder(View itemView) {
                super(itemView);
            }
        }
    }

    class NavigationAdapter extends CustomAdapter<CustomAdapter.CustomHolder> {
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

        @Override
        public CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.navigation_sub_item, parent, false);
            NavItemHolder navItemHolder = new NavItemHolder(view);
            return navItemHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final NavigationSubItem subItem = (NavigationSubItem) getItem(position);
            NavItemHolder navItemHolder = (NavItemHolder) holder;
            navItemHolder.navItemView.setText(subItem.title.toString().toUpperCase());
            navItemHolder.navItemView.setTag(R.string.item_position, position);
            //Log.d("NavigationAdapter", subItem.title.toString());

            if (selectedPosition >= 0 && selectedPosition == position) {
                navItemHolder.navItemlayout.setBackground(
                        Utils.getNavigationSelectedState(mContext, appCmsPresenter, true, bgColor));
                navItemHolder.navItemView.setTypeface(extraBoldTypeFace);
            } else {
                navItemHolder.navItemlayout.setBackground(null);
                navItemHolder.navItemView.setTypeface(semiBoldTypeFace);
            }

            navItemHolder.navItemlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavigationSubItem navigationSubItem = navigationSubItemList.get(selectedPosition);
                    if (ANDROID_WATCHLIST_NAV_KEY.equals(mAppCMSBinder.getJsonValueKeyMap()
                            .get(navigationSubItem.title))
                            || ANDROID_WATCHLIST_SCREEN_KEY.equals(mAppCMSBinder
                            .getJsonValueKeyMap().get(navigationSubItem.title))) {

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

        class NavItemHolder extends CustomHolder {
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

    class LeftNavigationAdapter extends CustomAdapter<CustomAdapter.CustomHolder>{
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
                                     AppCMSPresenter appCMSPresenter) {
            mContext = activity;
            this.textColor = textColor;
            this.bgColor = bgColor;
            this.navigation = navigation;
            this.appCmsPresenter = appCMSPresenter;
            createSubNavigationList();
        }


        @Override
        public NavItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.left_navigation_item, parent, false);
            NavItemHolder navItemHolder = new NavItemHolder(view);
            return navItemHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final NavigationSubItem subItem = (NavigationSubItem) getItem(position);
            NavItemHolder navItemHolder = (NavItemHolder) holder;
            navItemHolder.navItemView.setText(subItem.title.toString().toUpperCase());
            navItemHolder.navItemView.setTag(R.string.item_position, position);

            navItemHolder.navIconView.setImageResource(Utils.getIcon(subItem.icon,mContext));
            if(null != navItemHolder.navIconView.getDrawable()) {
                navItemHolder.navIconView.getDrawable().setTint(Utils.getComplimentColor(appCmsPresenter.getGeneralBackgroundColor()));
                navItemHolder.navIconView.getDrawable().setTintMode(PorterDuff.Mode.MULTIPLY);
            }

            if (selectedPosition >= 0 && selectedPosition == position) {
                navItemHolder.navItemlayout.setBackground(
                        Utils.getNavigationSelectedState(mContext, appCmsPresenter, true, bgColor));
                navItemHolder.navItemView.setTypeface(extraBoldTypeFace);
            } else {
                navItemHolder.navItemlayout.setBackground(null);
                navItemHolder.navItemView.setTypeface(semiBoldTypeFace);
            }

            navItemHolder.navItemlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavigationSubItem navigationSubItem = navigationSubItemList.get(selectedPosition);
                    if (ANDROID_WATCHLIST_NAV_KEY.equals(mAppCMSBinder.getJsonValueKeyMap()
                            .get(navigationSubItem.title))
                            || ANDROID_WATCHLIST_SCREEN_KEY.equals(mAppCMSBinder
                            .getJsonValueKeyMap().get(navigationSubItem.title))) {

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

        class NavItemHolder extends CustomHolder {
            TextView navItemView;
            LinearLayout navItemlayout;
            ImageView navIconView;

            public NavItemHolder(View itemView) {
                super(itemView);
                setTypeFaceValue(appCmsPresenter);
                navItemView = (TextView) itemView.findViewById(R.id.nav_item_label);
                navItemlayout = (LinearLayout) itemView.findViewById(R.id.nav_item_layout);
                navItemView.setTextColor(Color.parseColor(Utils.getTextColor(mContext, appCmsPresenter)));

                navIconView = (ImageView) itemView.findViewById(R.id.nav_item_image);

                navItemlayout.setOnFocusChangeListener((view, focus) -> {
                    if (focus)
                        mRecyclerView.setAlpha(1f);
                });

                navItemlayout.setOnKeyListener((view, i, keyEvent) -> {
                    int keyCode = keyEvent.getKeyCode();
                    int action = keyEvent.getAction();
                    if (action == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_UP:
                                return tryMoveSelection(mRecyclerView.getLayoutManager(), -1);
                            case KeyEvent.KEYCODE_DPAD_DOWN:
                                return tryMoveSelection(mRecyclerView.getLayoutManager(), 1);
                            case KeyEvent.KEYCODE_DPAD_RIGHT:
                            case KeyEvent.KEYCODE_DPAD_LEFT:
                                setFocusOnSelectedPage();
                              // navigationVisibilityListener.showSubNavigation(false);
                                new Handler().postDelayed(() -> mRecyclerView.setAlpha(0.52f), 50);
                                break;
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




