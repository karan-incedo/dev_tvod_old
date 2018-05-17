package com.viewlift.tv.views.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewlift.R;

/**
 * Created by nitin.tyagi on 7/24/2017.
 */

public abstract class BaseFragment extends Fragment implements AppCmsSubNavigationFragment.OnSubNavigationVisibilityListener {
    public abstract boolean isSubNavigationVisible();
    public abstract boolean isSubNavExist();
  //  public abstract void showSubnavigation(boolean show);
}
