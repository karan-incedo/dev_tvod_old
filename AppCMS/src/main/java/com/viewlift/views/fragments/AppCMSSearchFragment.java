package com.viewlift.views.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private static final String TAG = "SearchFragment";
    LinearLayout searchLayout;

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
        SearchSuggestionsAdapter searchSuggestionsAdapter = new SearchSuggestionsAdapter(getActivity(),
                null,
                searchManager.getSearchableInfo(getActivity().getComponentName()),
                true);

        appCMSSearchView = (SearchView) view.findViewById(R.id.app_cms_search_fragment_view);
        appCMSSearchView.setQueryHint(getString(R.string.search_films));
        appCMSSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        appCMSSearchView.setSuggestionsAdapter(searchSuggestionsAdapter);
        appCMSSearchView.setIconifiedByDefault(false);
        appCMSSearchView.requestFocus();
        appCMSPresenter.showSoftKeyboard(appCMSSearchView);
        appCMSSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) appCMSSearchView.getSuggestionsAdapter().getItem(position);
                String[] searchHintResult=cursor.getString(cursor.getColumnIndex("suggest_intent_data")).split(",");
                appCMSPresenter.openVideoPageFromSearch(searchHintResult);
                return true;
            }
        });

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
        appCMSPresenter.closeSoftKeyboard();
        appCMSSearchView.clearFocus();
    }

    @SuppressWarnings("ConstantConditions")
    private void setBgColor(int bgColor, View view) {
        RelativeLayout appCMSNavigationMenuMainLayout =
                (RelativeLayout) view.findViewById(R.id.app_cms_search_fragment);
        appCMSNavigationMenuMainLayout.setBackgroundColor(bgColor);
    }
}
