package air.com.snagfilms.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import air.com.snagfilms.AppCMSApplication;
import air.com.snagfilms.views.binders.AppCMSBinder;
import air.com.snagfilms.presenters.AppCMSPresenter;
import air.com.snagfilms.views.components.AppCMSViewComponent;
import air.com.snagfilms.views.components.DaggerAppCMSViewComponent;
import air.com.snagfilms.views.customviews.PageView;
import air.com.snagfilms.views.modules.AppCMSPageViewModule;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/3/17.
 */

public class AppCMSPageFragment extends Fragment {
    private static final String TAG = "AppCMSPageFragment";

    private AppCMSViewComponent appCMSViewComponent;
    private OnPageCreationError onPageCreationError;

    public interface OnPageCreationError {
        void onError();
    }

    public static AppCMSPageFragment newInstance(Context context, AppCMSBinder appCMSBinder) {
        AppCMSPageFragment fragment = new AppCMSPageFragment();
        Bundle args = new Bundle();
        args.putBinder(context.getString(R.string.fragment_page_bundle_key), appCMSBinder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnPageCreationError){
            onPageCreationError = (OnPageCreationError) context;
            AppCMSBinder appCMSBinder =
                    ((AppCMSBinder) getArguments().getBinder(context.getString(R.string.fragment_page_bundle_key)));
            AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                    .getAppCMSPresenterComponent()
                    .appCMSPresenter();
            appCMSViewComponent = DaggerAppCMSViewComponent
                    .builder()
                    .appCMSPageViewModule(new AppCMSPageViewModule(context,
                            appCMSBinder.getPage(),
                            appCMSBinder.getJsonValueKeyMap(),
                            appCMSPresenter))
                    .build();
        } else {
            throw new RuntimeException("Attached context must implement " +
                OnPageCreationError.class.getCanonicalName());
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        PageView pageView = appCMSViewComponent.appCMSPageView();
        if (pageView == null) {
            Log.e(TAG, "AppCMS page creation error");
            onPageCreationError.onError();
        }
        return pageView;
    }
}
