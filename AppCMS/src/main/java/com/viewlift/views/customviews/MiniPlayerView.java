package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Mobile;
import com.viewlift.models.data.appcms.ui.page.TabletLandscape;
import com.viewlift.models.data.appcms.ui.page.TabletPortrait;

/**
 * Created by sandeep.singh on 11/16/2017.
 */

public class MiniPlayerView extends RelativeLayout {

    private  VideoPlayerView videoPlayerView;
    private Context context;
    private RelativeLayout relativeLayoutEvent;
    private RelativeLayout.LayoutParams lpPipView;
    public MiniPlayerView(Context context,
                          VideoPlayerView videoPlayerView) {
        super(context);
        this.context=context;
        this.videoPlayerView= videoPlayerView;
        relativeLayoutEvent =new RelativeLayout(context);
        init();
    }


    public void init() {


        lpPipView = new RelativeLayout.LayoutParams(BaseView.dpToPx(R.dimen.app_cms_mini_player_width,context),
                BaseView.dpToPx(R.dimen.app_cms_mini_player_height,context));
        lpPipView.rightMargin =BaseView.dpToPx(R.dimen.app_cms_mini_player_margin,context);
        lpPipView.bottomMargin =BaseView.dpToPx(R.dimen.app_cms_mini_player_margin,context);


        lpPipView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpPipView.addRule(RelativeLayout.ABOVE, R.id.app_cms_tab_nav_container);



        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFF000000); //black background
        border.setStroke(1, 0xFFFFFFFF); //white border with full opacity
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(border);
        } else {
            setBackground(border);
        }


        setPadding(2,2,2,2);
        setLayoutParams(lpPipView);

        relativeLayoutEvent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        addView(videoPlayerView);
        addView(relativeLayoutEvent);


        /**
         * Just sample
         */
      /*relativeLayoutPIP = new RelativeLayout(currentActivity);// currentActivity.findViewById(R.id.appCMSPipWindow);
            relativeLayoutPIPEvent = new RelativeLayout(currentActivity);

            lpPipView = new RelativeLayout.LayoutParams(BaseView.dpToPx(R.dimen.app_cms_mini_player_width,currentActivity),
                    BaseView.dpToPx(R.dimen.app_cms_mini_player_height,currentActivity));
            if (!BaseView.isTablet(currentActivity)) {

                lpPipView.rightMargin = 20;
                lpPipView.bottomMargin = 20;
            } else {
                lpPipView.rightMargin = 30;
                lpPipView.bottomMargin = 30;
            }

            lpPipView.rightMargin =BaseView.dpToPx(R.dimen.app_cms_mini_player_margin,currentActivity);
            lpPipView.bottomMargin =BaseView.dpToPx(R.dimen.app_cms_mini_player_margin,currentActivity);
            lpPipView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lpPipView.addRule(RelativeLayout.ABOVE, R.id.app_cms_tab_nav_container);


            relativeLayoutPIP.setLayoutParams(lpPipView);
            relativeLayoutPIP.setPadding(2,2,2,2);

            GradientDrawable border = new GradientDrawable();
            border.setColor(0xFF000000); //black background
            border.setStroke(BaseView.dpToPx(R.dimen.app_cms_mini_player_border_size,currentActivity), 0xFFFFFFFF); //white border with full opacity
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                relativeLayoutPIP.setBackgroundDrawable(border);
            } else {
                relativeLayoutPIP.setBackground(border);
            }


            relativeLayoutPIPEvent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));


            relativeLayoutPIP.addView(videoPlayerViewPIP);
*/


    }


    public RelativeLayout getRelativeLayoutEvent() {
        return relativeLayoutEvent;
    }
}
