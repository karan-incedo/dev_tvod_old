package air.com.snagfilms.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import air.com.snagfilms.models.data.appcms.binders.AppCMSBinder;
import air.com.snagfilms.models.data.appcms.page.Page;
import air.com.snagfilms.views.modules.PageView;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/3/17.
 */

public class AppCMSPageFragment extends Fragment {
    public static AppCMSPageFragment newInstance(Context context, Page page) {
        AppCMSPageFragment fragment = new AppCMSPageFragment();
        Bundle args = new Bundle();
        args.putBinder(context.getString(R.string.fragment_page_bundle_key), new AppCMSBinder(page));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AppCMSBinder appCMSBinder =
                ((AppCMSBinder) getArguments().getBinder(container.getContext().getString(R.string.fragment_page_bundle_key)));
        return new PageView(container.getContext(), appCMSBinder.getPage());
    }
}
