package air.com.snagfilms.presenters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import air.com.snagfilms.views.fragments.ErrorFragment;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/3/17.
 */

public class AppCMSPresenter {
    private static AppCMSPresenter presenter;

    private static final String ERROR_TAG = "error_fragment";

    public static AppCMSPresenter getAppCMSPresenter() {
        if (presenter == null) {
            presenter = new AppCMSPresenter();
        }
        return presenter;
    }

    private AppCMSPresenter() {}

    public void launchFragment(FragmentManager fragmentManager,
                               String tag,
                               String json) {

    }

    public void launchErrorFragment(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment errorFragment = ErrorFragment.newInstance();
        fragmentTransaction.add(R.id.app_cms_fragment, errorFragment, ERROR_TAG);
        fragmentTransaction.commit();
    }
}
