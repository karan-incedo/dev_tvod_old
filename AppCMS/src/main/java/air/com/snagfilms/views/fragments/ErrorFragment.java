package air.com.snagfilms.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/4/17.
 */

public class ErrorFragment extends Fragment {
    public static ErrorFragment newInstance() {
        return new ErrorFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.error_landing_page_layout, container);
        return view;
    }
}
