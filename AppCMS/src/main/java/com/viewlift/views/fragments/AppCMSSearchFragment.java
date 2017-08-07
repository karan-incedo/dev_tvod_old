package com.viewlift.views.fragments;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.SearchSuggestionsAdapter;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.ViewCreator;

/**
 * Created by viewlift on 6/20/17.
 */

public class AppCMSSearchFragment extends DialogFragment {
    private SearchView appCMSSearchView;
    private Button appCMSGoButton;
    private AppCMSPresenter appCMSPresenter;

    public static AppCMSSearchFragment newInstance(Context context,
                                                   long bgColor,
                                                   long buttonColor,
                                                   long textColor) {
        Bundle args = new Bundle();
        args.putLong(context.getString(R.string.bg_color_key), bgColor);
        args.putLong(context.getString(R.string.button_color_key), buttonColor);
        args.putLong(context.getString(R.string.text_color_key), textColor);
        AppCMSSearchFragment appCMSSearchFragment = new AppCMSSearchFragment();
        appCMSSearchFragment.setArguments(args);
        return appCMSSearchFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Bundle args = getArguments();
        long bgColor = 0xff000000 + args.getLong(getContext().getString(R.string.bg_color_key));
        long buttonColor = args.getLong(getString(R.string.button_color_key));
        long textColor = args.getLong(getString(R.string.text_color_key));

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        appCMSSearchView = (SearchView) view.findViewById(R.id.app_cms_search_view);
        appCMSSearchView.setQueryHint(getString(R.string.search_films));
        appCMSSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        appCMSSearchView.setIconifiedByDefault(false);
        appCMSSearchView.setFocusable(true);
        appCMSSearchView.getSuggestionsAdapter();
        appCMSSearchView.requestFocus();

        SearchSuggestionsAdapter searchSuggestionsAdapter = new SearchSuggestionsAdapter(getContext(),
                null,
                searchManager.getSearchableInfo(getActivity().getComponentName()),
                true);

        appCMSSearchView.setSuggestionsAdapter(searchSuggestionsAdapter);

        appCMSGoButton = (Button) view.findViewById(R.id.app_cms_search_button);
        appCMSGoButton.setBackgroundColor(0xff000000 + (int) buttonColor);
        appCMSGoButton.setTextColor(0xff000000 + (int) ViewCreator.adjustColor1(textColor, buttonColor));

        appCMSGoButton.setOnClickListener(v ->
                appCMSPresenter.launchSearchResultsPage(appCMSSearchView.getQuery().toString()));

        setBgColor((int) bgColor, view);

        if (!BaseView.isTablet(getContext())) {
            appCMSPresenter.restrictPortraitOnly();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!BaseView.isTablet(getContext())) {
            appCMSPresenter.unrestrictPortraitOnly();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setBgColor(int bgColor, View view) {
        RelativeLayout appCMSNavigationMenuMainLayout =
                (RelativeLayout) view.findViewById(R.id.app_cms_search_fragment);
        appCMSNavigationMenuMainLayout.setBackgroundColor(bgColor);
    }
}
