package com.viewlift.views.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.pedrovgs.DraggablePanel;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSBinder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppCMSDraggableFragment extends Fragment {

    @BindView(R.id.draggable_main_layout)
    ConstraintLayout appCMSDraggableMainLayout;

    @BindView(R.id.draggable_video_title)
    TextView draggableVideoTitle;

    @BindView(R.id.draggable_video_description)
    TextView draggableVideoDescription;

    @BindView(R.id.draggable_player_container)
    FrameLayout draggableVideoContainer;

    @BindView(R.id.draggable_related_videos)
    RecyclerView draggableRelatedVideos;

    private DraggablePanel draggablePanel;

    public static AppCMSDraggableFragment newInstance(Context context, AppCMSBinder appCMSBinder) {
        AppCMSDraggableFragment fragment = new AppCMSDraggableFragment();
        Bundle args = new Bundle();
        args.putBinder(context.getString(R.string.fragment_page_bundle_key), appCMSBinder);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_draggable, container, false);

        ButterKnife.bind(this, view);

        final AppCMSPresenter appCMSPresenter = ((AppCMSApplication) getActivity().getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();


        AppCMSPageFragment topFragment = new AppCMSPageFragment();
        int lastFragment = getActivity().getSupportFragmentManager().getFragments().size();
        Fragment bottomFragment = getActivity().getSupportFragmentManager().getFragments().get(lastFragment - 1);

        initializeDraggablePanel(topFragment, bottomFragment);

        Bundle args = getArguments();

        int bgColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                .getBackgroundColor());
        int textColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral()
                .getTextColor());
        int transparentColor = getResources().getColor(R.color.transparentColor);

        draggableVideoTitle.setTextColor(textColor);
        draggableVideoTitle.setBackgroundColor(transparentColor);
        draggableVideoTitle.setTypeface(appCMSPresenter.getBoldTypeFace());

        draggableVideoDescription.setTextColor(textColor);
        draggableVideoDescription.setBackgroundColor(transparentColor);

        setBgColor(bgColor);

        return view;
    }

    private void initializeDraggablePanel(Fragment topFrag, Fragment bottomFrag) {
        draggablePanel.setFragmentManager(getActivity().getSupportFragmentManager());
        draggablePanel.setTopFragment(topFrag);
        draggablePanel.setBottomFragment(bottomFrag);
        draggablePanel.setClickToMaximizeEnabled(true);
        draggablePanel.initializeView();
    }

    protected List<Fragment> getFragmentCount() {
        return getActivity().getSupportFragmentManager().getFragments();
    }

    private void setBgColor(int bgColor) {
        appCMSDraggableMainLayout.setBackgroundColor(bgColor);
    }
}
