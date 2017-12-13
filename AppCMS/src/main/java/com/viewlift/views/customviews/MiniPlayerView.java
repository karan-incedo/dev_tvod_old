package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Mobile;
import com.viewlift.models.data.appcms.ui.page.TabletLandscape;
import com.viewlift.models.data.appcms.ui.page.TabletPortrait;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.exoplayerview.CustomPlaybackControlView;

/**
 * Created by sandeep.singh on 11/16/2017.
 */

public class MiniPlayerView extends RelativeLayout {

    private CustomVideoPlayerView videoPlayerView;
    private AppCMSPresenter appCMSPresenter;
    private Context context;
    private RelativeLayout relativeLayoutEvent;
    private RelativeLayout.LayoutParams lpPipView;

    public MiniPlayerView(Context context,
                          CustomVideoPlayerView videoPlayerView) {
        super(context);
        this.context = context;
        this.videoPlayerView = videoPlayerView;
        relativeLayoutEvent = new RelativeLayout(context);
        init();
    }

    public MiniPlayerView(Context context,
                          AppCMSPresenter appCMSPresenter) {
        super(context);
        this.context = context;
        this.appCMSPresenter = appCMSPresenter;
        relativeLayoutEvent = new RelativeLayout(context);
        init();
    }

    public void init() {


        lpPipView = new RelativeLayout.LayoutParams(BaseView.dpToPx(R.dimen.app_cms_mini_player_width, context),
                BaseView.dpToPx(R.dimen.app_cms_mini_player_height, context));
        lpPipView.rightMargin = BaseView.dpToPx(R.dimen.app_cms_mini_player_margin, context);
        lpPipView.bottomMargin = BaseView.dpToPx(R.dimen.app_cms_mini_player_margin, context);


        lpPipView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpPipView.addRule(RelativeLayout.ABOVE, R.id.app_cms_tab_nav_container);


        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFF000000); //black background
        border.setStroke(1, 0xFFFFFFFF); //white border with full opacity
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(border);
        } else {
            setBackground(border);
        }


        setPadding(2, 2, 2, 2);
        setLayoutParams(lpPipView);

        relativeLayoutEvent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        if (appCMSPresenter.videoPlayerView.getParent() != null) {
            appCMSPresenter.videoPlayerViewParent = (ViewGroup) appCMSPresenter.videoPlayerView.getParent();
            ((ViewGroup) appCMSPresenter.videoPlayerView.getParent()).removeView(appCMSPresenter.videoPlayerView);
        }

        appCMSPresenter.videoPlayerView.disableController();

        addView(appCMSPresenter.videoPlayerView);
        addView(relativeLayoutEvent);


    }


    public RelativeLayout getRelativeLayoutEvent() {
        return relativeLayoutEvent;
    }

    public void disposeRelativeLayoutEvent() {
        this.relativeLayoutEvent.setVisibility(GONE);
        this.relativeLayoutEvent.setOnClickListener(null);
        this.relativeLayoutEvent = null;
    }
}
