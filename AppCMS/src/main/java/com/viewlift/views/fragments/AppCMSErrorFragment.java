package com.viewlift.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewlift.R;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSErrorFragment extends Fragment {
    static String message= null;
    public static AppCMSErrorFragment newInstance(String errorMessage) {
        message = errorMessage;
        return new AppCMSErrorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error_page, container, false);
        TextView errorTextView = (TextView) view.findViewById(R.id.app_cms_error_textview);
        if (message!=null){
            message =message +"!\nPlease try again later!";
            errorTextView.setText( message);
        }else {
            errorTextView.setText(Html.fromHtml(getString(R.string.error_loading_page)));
        }

        return view;
    }
}
