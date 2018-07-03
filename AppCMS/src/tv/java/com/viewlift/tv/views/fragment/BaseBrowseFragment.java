package com.viewlift.tv.views.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewlift.R;
import com.viewlift.tv.utility.Utils;


/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class BaseBrowseFragment extends BrowseSupportFragment {

    private View browseFragmentView = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         browseFragmentView =  super.onCreateView(inflater, container, savedInstanceState);

        Utils.setBrowseFragmentViewParameters(browseFragmentView,
                (int) getResources().getDimension(R.dimen.browse_fragment_margin_left),
                (int) getResources().getDimension(R.dimen.browse_fragment_margin_top));

        return browseFragmentView;
    }

    public View getBrowseFragmentView(){
        return browseFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpUIElement();
    }

    private void setUpUIElement(){
        setHeadersState(HEADERS_DISABLED);
        setHeadersTransitionOnBackEnabled(true);
    }
}
