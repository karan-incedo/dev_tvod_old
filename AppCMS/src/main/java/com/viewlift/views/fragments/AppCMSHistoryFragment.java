package com.viewlift.views.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.viewlift.presenters.AppCMSPresenter;

import com.viewlift.R;

public class AppCMSHistoryFragment extends Fragment {

    private AppCMSPresenter appCMSPresenter;

    public static AppCMSHistoryFragment newInstance(Context context,
                                                    int bgColor,
                                                    int buttonColor,
                                                    int textColor) {

        Bundle args = new Bundle();
        args.putInt(context.getString(R.string.bg_color_key), bgColor);
        args.putInt(context.getString(R.string.button_color_key), buttonColor);
        args.putInt(context.getString(R.string.text_color_key), textColor);
        AppCMSHistoryFragment appCMSHistoryFragment = new AppCMSHistoryFragment();
        appCMSHistoryFragment.setArguments(args);
        return appCMSHistoryFragment;
    }

    public AppCMSHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(inflater.getContext()).inflate(R.layout.fragment_history,
                container, false);

        Toast.makeText(view.getContext(), "History Fragment!", Toast.LENGTH_SHORT).show();

        return view;
    }
}
