package com.viewlift.tv.views.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
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

import static com.viewlift.models.data.appcms.ui.AppCMSUIKeyType.ANDROID_HISTORY_NAV_KEY;
import static com.viewlift.models.data.appcms.ui.AppCMSUIKeyType.ANDROID_WATCHLIST_NAV_KEY;

/**
 * Created by nitin.tyagi on 6/27/2017.
 */

public class AppCmsSubNavigationFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private int textColor = -1;
    private int bgColor = -1;
    private static OnSubNavigationVisibilityListener navigationVisibilityListener;
    private Typeface extraBoldTypeFace , semiBoldTypeFace;
    private Component extraBoldComp , semiBoldComp;
    private AppCMSBinder appCmsBinder;
    private  Navigation mNavigation;
    private boolean isUserLogin;
    private AppCMSBinder mAppCMSBinder;
    private boolean isLoginDialogPage;

    public static AppCmsSubNavigationFragment newInstance(Context context,
                                                          OnSubNavigationVisibilityListener listener
                                                         ) {
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
        mAppCMSBinder = (AppCMSBinder)bundle.getBinder("app_cms_binder");
        isLoginDialogPage = bundle.getBoolean(getString(R.string.is_login_dialog_page_key));
/*
        AppCMSBinder appCMSBinder = ((AppCMSBinder) args.getBinder(getResources().getString(R.string.fragment_page_bundle_key)));
        this.appCmsBinder = appCMSBinder;
*/
        AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        View navTopLine = view.findViewById(R.id.nav_top_line);
        AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
        textColor = Color.parseColor(appCMSMain.getBrand().getCta().getPrimary().getTextColor());/*Color.parseColor("#F6546A");*/
        bgColor = Color.parseColor(appCMSMain.getBrand().getCta().getPrimary().getBackgroundColor());//Color.parseColor("#660066");

        mNavigation = appCMSPresenter.getNavigation();
        isUserLogin = /*appCMSPresenter.isUserLoggedIn()*/ true;

        TextView navMenuTile = (TextView) view.findViewById(R.id.nav_menu_title);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.navRecylerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (appCMSPresenter.getTemplateType().equals(AppCMSPresenter.TemplateType.ENTERTAINMENT)) {
            NavigationAdapter navigationAdapter = new NavigationAdapter(getActivity(), textColor, bgColor,
                    mNavigation,
                    isUserLogin,
                    appCMSPresenter);

            mRecyclerView.setAdapter(navigationAdapter);
            setFocusable(true);
            navigationAdapter.setFocusOnSelectedPage();
            navTopLine.setVisibility(View.GONE);
        } else {
            navMenuTile.setText("Settings");
            navMenuTile.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), getActivity().getString(R.string.lato_regular)));
            navMenuTile.setVisibility(View.VISIBLE);

            STNavigationAdapter navigationAdapter = new STNavigationAdapter(
                    getActivity(),
                    textColor,
                    bgColor,
                    mNavigation,
                    isUserLogin,
                    appCMSPresenter);

            mRecyclerView.setAdapter(navigationAdapter);
            setFocusable(true);
            navigationAdapter.setFocusOnSelectedPage();
            navTopLine.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void setTypeFaceValue(AppCMSPresenter appCMSPresenter){
        if(null == extraBoldTypeFace) {
            extraBoldComp = new Component();
            extraBoldComp.setFontFamily(getResources().getString(R.string.app_cms_page_font_family_key));
            extraBoldComp.setFontWeight(getResources().getString(R.string.app_cms_page_font_extrabold_key));
            extraBoldTypeFace = Utils.getTypeFace(getActivity(), appCMSPresenter.getJsonValueKeyMap()
                    , extraBoldComp);
        }

        if(null == semiBoldTypeFace) {
            semiBoldComp = new Component();
            semiBoldComp.setFontFamily(getResources().getString(R.string.app_cms_page_font_family_key));
            semiBoldComp.setFontWeight(getResources().getString(R.string.app_cms_page_font_semibold_key));
            semiBoldTypeFace = Utils.getTypeFace(getActivity(), appCMSPresenter.getJsonValueKeyMap()
                    , semiBoldComp);
        }
    }


    public void setFocusable(boolean hasFocus) {
        if (null != mRecyclerView) {
            if(hasFocus)
              mRecyclerView.requestFocus();
            else
                mRecyclerView.clearFocus();
        }
    }

    private String mSelectedPageId = null;
    public void setSelectedPageId(String selectedPageId) {
        this.mSelectedPageId = selectedPageId;
    }

    private int getSelectedPagePosition(){
        if(null != mSelectedPageId){
            if(null != navigationSubItemList && navigationSubItemList.size() > 0){
                for(int i=0;i<navigationSubItemList.size();i++){
                    NavigationSubItem navigationSubItem = navigationSubItemList.get(i);
                    if(navigationSubItem.pageId == mSelectedPageId){
                        return i;
                    }
                }
            }
        }
        return 0;
    }

    public void notifiDataSetInvlidate() {
        if(null != mRecyclerView && null != mRecyclerView.getAdapter()){
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
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
            final NavigationSubItem subItem = (NavigationSubItem)getItem(position);
            holder.navItemView.setText(subItem.title.toString().toUpperCase());
            holder.navItemView.setTag(R.string.item_position , position);
            //Log.d("NavigationAdapter", subItem.title.toString());

            if(selectedPosition >= 0 && selectedPosition == position){
                holder.navItemlayout.setBackground(
                        Utils.getNavigationSelectedState(mContext , appCmsPresenter,true));
                holder.navItemView.setTypeface(extraBoldTypeFace);
            }else{
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
                                isLoginDialogPage
                        );
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


        class NavItemHolder extends RecyclerView.ViewHolder {
            TextView navItemView;
            RelativeLayout navItemlayout;

            public NavItemHolder(View itemView) {
                super(itemView);
                setTypeFaceValue(appCmsPresenter);
                navItemView = (TextView) itemView.findViewById(R.id.nav_item_label);
                navItemlayout = (RelativeLayout) itemView.findViewById(R.id.nav_item_layout);
                navItemView.setTextColor(Color.parseColor(Utils.getTextColor(mContext,appCmsPresenter)));

                navItemlayout.setOnFocusChangeListener((view, focus) -> {
                    if(focus)
                     mRecyclerView.setAlpha(1f);
                });

                navItemlayout.setOnKeyListener((view, i, keyEvent) -> {
                    int keyCode = keyEvent.getKeyCode();
                    int action = keyEvent.getAction();
                    if (action == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_LEFT:
                                return tryMoveSelection(mRecyclerView.getLayoutManager() , -1);
                            case KeyEvent.KEYCODE_DPAD_RIGHT:
                                return tryMoveSelection(mRecyclerView.getLayoutManager() , 1);
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

        private void setFocusOnSelectedPage() {
                int selectedPos = getSelectedPagePosition();
                notifyItemChanged(selectedPosition);
                selectedPosition = selectedPos;
                notifyItemChanged(selectedPosition);
        }
    }



    class STNavigationAdapter extends RecyclerView.Adapter<STNavigationAdapter.STNavItemHolder> {
        private Context mContext;
        private LayoutInflater inflater;
        private int textColor;
        private int bgColor;
        private Navigation navigation;
        private boolean isuserLoggedIn;
        private AppCMSPresenter appCmsPresenter;
        private int currentNavPos;

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
            createSubNavigationListForST();
        }
        private void createSubNavigationListForST() {
            if (null == navigationSubItemList) {
                navigationSubItemList = new ArrayList<>();
            }

            NavigationSubItem navigationSubItem1 = new NavigationSubItem();
            if (appCmsPresenter.getAutoplayEnabledUserPref(mContext)){
                navigationSubItem1.title = "AUTOPLAY ON";
            } else {
                navigationSubItem1.title = "AUTOPLAY OFF";
            }
            navigationSubItemList.add(navigationSubItem1);

            navigationSubItem1 = new NavigationSubItem();
            if (appCmsPresenter.getClosedCaptionPreference()){
                navigationSubItem1.title = "CLOSED CAPTION ON";
            } else {
                navigationSubItem1.title = "CLOSED CAPTION OFF";
            }
            navigationSubItemList.add(navigationSubItem1);

            for (int i = 0; i < mNavigation.getNavigationUser().size(); i++) {
                NavigationUser navigationUser = mNavigation.getNavigationUser().get(i);
                if ((isUserLogin && navigationUser.getAccessLevels().getLoggedIn())
                        || (!isUserLogin && navigationUser.getAccessLevels().getLoggedOut())) {
                    NavigationSubItem navigationSubItem = new NavigationSubItem();
                    navigationSubItem.pageId = navigationUser.getPageId();
                    navigationSubItem.title = navigationUser.getTitle();
                    navigationSubItem.url = navigationUser.getUrl();
                    navigationSubItem.accessLevels = navigationUser.getAccessLevels();
                    navigationSubItemList.add(navigationSubItem);
                }
            }
            if (!isUserLogin) {
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
                        navigationSubItemList = new ArrayList<>();
                    }
                    navigationSubItemList.add(navigationSubItem);
                }
            }
            navigationSubItem1 = new NavigationSubItem();
            if (appCmsPresenter.isUserLoggedIn()) {
                navigationSubItem1.title = "SIGN OUT";
            } else {
                navigationSubItem1.title = "SIGN IN";
            }
            navigationSubItemList.add(navigationSubItem1);
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
            final NavigationSubItem subItem = (NavigationSubItem)getItem(position);
            holder.navItemView.setText(subItem.title.toString().toUpperCase());
            holder.navItemView.setTag(R.string.item_position , position);
            //Log.d("NavigationAdapter", subItem.title.toString());


            holder.navImageView.setImageResource(getIcon(subItem.title));

            holder.navItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavigationSubItem navigationSubItem = navigationSubItemList.get(selectedPosition);

                    if(navigationSubItem.title.toUpperCase().contains("AUTOPLAY")) {
                        if(appCmsPresenter.getAutoplayEnabledUserPref(mContext)){
                            navigationSubItem.title = "Autoplay Off";
                            appCmsPresenter.setAutoplayEnabledUserPref(mContext, false);
                        } else {
                            navigationSubItem.title = "Autoplay On";
                            appCmsPresenter.setAutoplayEnabledUserPref(mContext, true);
                        }
                    } else if(navigationSubItem.title.toUpperCase().contains("CLOSED CAPTION")) {
                        if(appCmsPresenter.getClosedCaptionPreference()){
                            navigationSubItem.title = "Closed Caption Off";
                            appCmsPresenter.setClosedCaptionPreference(false);
                        } else {
                            navigationSubItem.title = "Closed Caption On";
                            appCmsPresenter.setClosedCaptionPreference(false);
                        }
                    }

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
                                isLoginDialogPage
                        );
                    }
                    STNavigationAdapter.this.notifyItemChanged(position);
                }
            });
        }

        private int getIcon(String text) {
            text = text.toUpperCase();
            System.out.println("Anas:: " + text);
            int iconResId = 0;
            if (text.contains("AUTOPLAY")) {
                iconResId = R.drawable.st_settings_icon_autoplay;
            } else if (text.contains("CLOSED CAPTION")) {
                iconResId = R.drawable.st_settings_icon_cc;
            } else if (text.contains("ACCOUNT")) {
                iconResId = R.drawable.st_settings_icon_account;
            } else if (text.contains("FAQ")) {
                iconResId = R.drawable.st_settings_icon_faq;
            } else if (text.contains("CONTACT")) {
                iconResId = R.drawable.st_settings_icon_contact;
            } else if (text.contains("ABOUT")) {
                iconResId = R.drawable.st_settings_icon_about;
            } else if (text.contains("SIGN")) {
                iconResId = R.drawable.st_settings_icon_signout;
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
//                navItemLayout.setBackground(Utils.getNavigationSelector(mContext, appCmsPresenter, false));
                navItemView.setTextColor(Color.parseColor(Utils.getTextColor(mContext, appCmsPresenter)));
                navItemView.setTypeface(semiBoldTypeFace);
                navItemLayout.setOnFocusChangeListener((view, hasFocus) -> {

                    selectedPosition = (int) navItemView.getTag(R.string.item_position);

                    if (hasFocus) {
                        navImageView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        navImageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
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

        private void setFocusOnSelectedPage() {
                int selectedPos = getSelectedPagePosition();
                notifyItemChanged(selectedPosition);
                selectedPosition = selectedPos;
                notifyItemChanged(selectedPosition);
        }
    }



    private int selectedPosition = 0;

  private boolean isEndPosition(){
        return selectedPosition == navigationSubItemList.size()-1;
    }

    private boolean isStartPosition(){
        return (selectedPosition == 0) ;
    }

    public interface OnSubNavigationVisibilityListener {
        void showSubNavigation(boolean shouldShow);
    }

    private List<NavigationSubItem> navigationSubItemList;
    private void createSubNavigationList(){
        for(int i=0;i<mNavigation.getNavigationUser().size();i++){
            NavigationUser navigationUser = mNavigation.getNavigationUser().get(i);
            if( (isUserLogin && navigationUser.getAccessLevels().getLoggedIn())
                    || (!isUserLogin && navigationUser.getAccessLevels().getLoggedOut())){
                NavigationSubItem navigationSubItem = new NavigationSubItem();
                navigationSubItem.pageId = navigationUser.getPageId();
                navigationSubItem.title = navigationUser.getTitle();
                navigationSubItem.url = navigationUser.getUrl();
                navigationSubItem.accessLevels = navigationUser.getAccessLevels();
                if(null == navigationSubItemList){
                    navigationSubItemList = new ArrayList<NavigationSubItem>();
                }
                navigationSubItemList.add(navigationSubItem);
            }
        }
        if(!isUserLogin){
            return;
        }

        for(int i=0;i<mNavigation.getNavigationFooter().size();i++) {
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

    class NavigationSubItem{
        String pageId;
        String title;
        String url;
        AccessLevels accessLevels;
    }

}




