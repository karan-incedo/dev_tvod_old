package com.viewlift.tv.views.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationSubMenu;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AccessLevels;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationFooter;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.models.data.appcms.ui.android.NavigationUser;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
import com.viewlift.views.binders.AppCMSBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.viewlift.models.data.appcms.ui.AppCMSUIKeyType.ANDROID_WATCHLIST_NAV_KEY;

/**
 * Created by nitin.tyagi on 6/27/2017.
 */

public class AppCmsSubNavigationFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private int textColor = -1;
    private int bgColor = -1;
    private static OnNavigationVisibilityListener navigationVisibilityListener;
    private Typeface extraBoldTypeFace , semiBoldTypeFace;
    private Component extraBoldComp , semiBoldComp;
    private AppCMSBinder appCmsBinder;
    private  Navigation mNavigation;
    private boolean isUserLogin;
    private AppCMSBinder mAppCMSBinder;

    public static AppCmsSubNavigationFragment newInstance(Context context,
                                                          OnNavigationVisibilityListener listener
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
/*
        AppCMSBinder appCMSBinder = ((AppCMSBinder) args.getBinder(getResources().getString(R.string.fragment_page_bundle_key)));
        this.appCmsBinder = appCMSBinder;
*/
        AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();


        AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
        textColor = Color.parseColor(appCMSMain.getBrand().getCta().getPrimary().getTextColor());/*Color.parseColor("#F6546A");*/
        bgColor = Color.parseColor(appCMSMain.getBrand().getCta().getPrimary().getBackgroundColor());//Color.parseColor("#660066");

        mNavigation = appCMSPresenter.getNavigation();
        isUserLogin = appCMSPresenter.isUserLoggedIn(getActivity());


        mRecyclerView = (RecyclerView) view.findViewById(R.id.navRecylerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        NavigationAdapter navigationAdapter = new NavigationAdapter(getActivity(), textColor, bgColor,
                                                mNavigation,
                                                isUserLogin,
                                                appCMSPresenter);

        mRecyclerView.setAdapter(navigationAdapter);
        setFocusable(true);
        navigationAdapter.setFocusOnSelectedPage();
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
            Log.d("NavigationAdapter", subItem.title.toString());

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
                    if (mAppCMSBinder.getJsonValueKeyMap().get(navigationSubItem.title).equals(ANDROID_WATCHLIST_NAV_KEY)) {
                        appCmsPresenter.navigateToWatchlistPage(
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
                                false
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
               // navItemView.setTypeface(semiBoldTypeFace);

                navItemlayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean focus) {
                        if(focus)
                         mRecyclerView.setAlpha(1f);
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
                                    return tryMoveSelection(mRecyclerView.getLayoutManager() , -1);
                                case KeyEvent.KEYCODE_DPAD_RIGHT:
                                    return tryMoveSelection(mRecyclerView.getLayoutManager() , 1);
                                case KeyEvent.KEYCODE_DPAD_DOWN:
                                    setFocusOnSelectedPage();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mRecyclerView.setAlpha(0.52f);
                                        }
                                    } , 50);
                                    break;
                            }
                        }
                        return false;
                    }
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

    public interface OnNavigationVisibilityListener {
        void showNavigation(boolean shouldShow);
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




