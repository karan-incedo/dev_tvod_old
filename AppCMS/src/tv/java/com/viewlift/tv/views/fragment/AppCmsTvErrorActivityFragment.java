package com.viewlift.tv.views.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewlift.appcms.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class AppCmsTvErrorActivityFragment extends Fragment {

    public AppCmsTvErrorActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_cms_tv_error, container, false);
        TextView errorTextView = (TextView) view.findViewById(R.id.app_cms_error_textview);
        errorTextView.setText(Html.fromHtml(getString(R.string.error_loading_page)));
        return view;
    }
}
