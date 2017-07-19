package com.viewlift.views.fragments;

/*
 * Created by Viewlift on 6/27/2017.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.viewlift.R;

public class AppCMSWatchlistFragment extends Fragment {

//    Realm realm = null;

    @BindView(R.id.app_cms_watchlist_results)
    RecyclerView watchListRecycler;

    private AppCMSPresenter appCMSPresenter;

    public static AppCMSWatchlistFragment newInstance(Context context,
                                                      int bgColor,
                                                      int buttonColor,
                                                      int textColor) {

        Bundle args = new Bundle();
        args.putInt(context.getString(R.string.bg_color_key), bgColor);
        args.putInt(context.getString(R.string.button_color_key), buttonColor);
        args.putInt(context.getString(R.string.text_color_key), textColor);
        AppCMSWatchlistFragment appCMSWatchlistFragment = new AppCMSWatchlistFragment();
        appCMSWatchlistFragment.setArguments(args);

        return appCMSWatchlistFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(inflater.getContext()).inflate(R.layout.fragment_watchlist,
                container, false);

        ButterKnife.bind(this, view);

        final AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        if (appCMSPresenter.isUserLoggedIn(view.getContext())) {
            String loggedInUser = appCMSPresenter.getLoggedInUser(view.getContext());

            convertJsonToArrayList();
            //
        }

//        appCMSPresenter.getAppCMSMain().getBrand()

        Bundle args = getArguments();

        return view;
    }

    private void convertJsonToArrayList() {
        ArrayList<String> listData = new ArrayList<>();
        String url = "";

        saveWatchlistToDatabase(listData);
    }

    private void saveWatchlistToDatabase(ArrayList<String> listData) {
        //
    }
}
