package com.viewlift.tv.views.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.Primary;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
import com.viewlift.views.binders.AppCMSBinder;

import org.w3c.dom.Text;

import java.util.zip.Inflater;

import javax.inject.Inject;

import snagfilms.com.air.appcms.R;

/**
 * Created by nitin.tyagi on 6/27/2017.
 */

public class AppCmsNavigationFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private int textColor = -1;
    private int bgColor = -1;
    private static OnNavigationVisibilityListener navigationVisibilityListener;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_navigation, null);

        Bundle args = getArguments();
        textColor = args.getInt(getResources().getString(R.string.app_cms_text_color_key));
        bgColor = args.getInt(getResources().getString(R.string.app_cms_bg_color_key));

        AppCMSBinder appCMSBinder = ((AppCMSBinder) args.getBinder(getResources().getString(R.string.fragment_page_bundle_key)));

        AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.navRecylerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        NavigationAdapter navigationAdapter = new NavigationAdapter(getActivity(), textColor, bgColor,
                                                appCMSBinder.getNavigation(),
                                                appCMSBinder.isUserLoggedIn(),
                                                appCMSPresenter);

        mRecyclerView.setAdapter(navigationAdapter);
        return view;
    }

    public void setFocusable(boolean hasFocus) {
        if (null != mRecyclerView) {
            if(hasFocus)
              mRecyclerView.requestFocus();
            else
                mRecyclerView.clearFocus();
        }
    }
    public void setSelectorColor() {
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.navigation_selector);
        GradientDrawable topshape = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.navigationTopColor);
        GradientDrawable bottomShape = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.navigationBottomColor);
        if(bgColor != -1)
            topshape.setColor(bgColor);
        bottomShape.setColor(ContextCompat.getColor(getActivity(), R.color.appcms_nav_background));
    }

    private String mSelectedPageId = null;
    public void setSelectedPageId(String selectedPageId) {
        this.mSelectedPageId = selectedPageId;
        Log.d("" ,"Navigation setSelectedPageId = " + mSelectedPageId );
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
            setSelectorColor();
        }


        public Object getItem(int i) {
            return navigation.getPrimary().get(i);
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
            final Primary primary = (Primary)getItem(position);
            holder.navItemView.setText(primary.getTitle().toString());
            Log.d("NavigationAdapter", primary.getTitle().toString());


            if(null != mSelectedPageId){
                if(primary.getPageId().equalsIgnoreCase(mSelectedPageId)){
                     holder.navItemlayout.requestFocus();
                    Log.d("NavigationAd pageId = " , mSelectedPageId + " title = " + primary.getTitle() + " -----true ");
                }else{
                    holder.navItemlayout.clearFocus();
                    Log.d("NavigationAd pageId = " , mSelectedPageId + " title = " + primary.getTitle() + " -----false");
                }
            }


            holder.navItemlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    navigationVisibilityListener.showNavigation(false);
                    if (!appCmsPresenter.navigateToTVPage(primary.getPageId(),
                            primary.getTitle(),
                            primary.getUrl(),
                            false,
                            null)) {
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            int totalCount = 0;
            if (null != this.navigation && null != navigation.getPrimary())
                totalCount = this.navigation.getPrimary().size();
            return totalCount;
        }

        class NavItemHolder extends RecyclerView.ViewHolder {
            TextView navItemView;
            RelativeLayout navItemlayout;

            public NavItemHolder(View itemView) {
                super(itemView);
                navItemView = (TextView) itemView.findViewById(R.id.nav_item_label);
                navItemlayout = (RelativeLayout) itemView.findViewById(R.id.nav_item_layout);
                navItemView.setTextColor(textColor);
                navItemlayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {

                        String text = navItemView.getText().toString();
                        if (hasFocus) {
                            navItemView.setText(text);
                            navItemView.setTypeface(Typeface.DEFAULT_BOLD);

                        } else {
                            navItemView.setText(text);
                            navItemView.setTypeface(Typeface.DEFAULT);
                        }
                    }
                });

            }
        }
    }

    public interface OnNavigationVisibilityListener {
        void showNavigation(boolean shouldShow);
    }


}




