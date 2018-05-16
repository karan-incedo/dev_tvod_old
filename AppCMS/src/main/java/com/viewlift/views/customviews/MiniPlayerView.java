package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;

import static com.viewlift.views.customviews.ViewCreator.getColor;

/**
 * Created by sandeep.singh on 11/16/2017.
 */

public class MiniPlayerView extends RelativeLayout implements Animation.AnimationListener{

    ImageButton closePlayer;
    private CustomVideoPlayerView videoPlayerView;
    private AppCMSPresenter appCMSPresenter;
    private Context context;
    private RelativeLayout relativeLayoutEvent;
    private int relativeLayoutEventViewId;
    private RelativeLayout.LayoutParams lpPipView;
    private RecyclerView mRecyclerView;
    private Animation animBottomSlide;
    private MiniPlayerView miniPlayerView;

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

    public MiniPlayerView(Context context,
                          AppCMSPresenter appCMSPresenter, final View recyclerView) {
        super(context);
        mRecyclerView = (RecyclerView) recyclerView;
        miniPlayerView= this;
        this.context = context;
        this.appCMSPresenter = appCMSPresenter;
        relativeLayoutEvent = new RelativeLayout(context);
        init();
    }
    public void init(AppCMSPresenter appCMSPresenter,final View recyclerView) {
        this.appCMSPresenter=appCMSPresenter;
        mRecyclerView = (RecyclerView) recyclerView;
        miniPlayerView= this;
    }

    public void init() {

        if (BaseView.isTablet(context)){
            appCMSPresenter.unrestrictPortraitOnly();
        }else{
            appCMSPresenter.restrictPortraitOnly();
        }

        animBottomSlide = AnimationUtils.loadAnimation(context, R.anim.mini_player_slide_bottom);


        this.startAnimation(animBottomSlide);

        lpPipView = new RelativeLayout.LayoutParams(BaseView.dpToPx(R.dimen.app_cms_mini_player_width, context),
                BaseView.dpToPx(R.dimen.app_cms_mini_player_height, context));
        lpPipView.rightMargin = BaseView.dpToPx(R.dimen.app_cms_mini_player_margin, context);
        lpPipView.bottomMargin = BaseView.dpToPx(R.dimen.app_cms_mini_player_margin, context);
        lpPipView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpPipView.addRule(RelativeLayout.ABOVE, R.id.app_cms_tab_nav_container);
        relativeLayoutEventViewId = View.generateViewId();
        relativeLayoutEvent.setId(relativeLayoutEventViewId);
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

        if (appCMSPresenter != null &&
                appCMSPresenter.videoPlayerView != null &&
                appCMSPresenter.videoPlayerView.getParent() != null) {
            appCMSPresenter.videoPlayerViewParent = (ViewGroup) appCMSPresenter.videoPlayerView.getParent();
            ((ViewGroup) appCMSPresenter.videoPlayerView.getParent()).removeView(appCMSPresenter.videoPlayerView);
            appCMSPresenter.videoPlayerView.disableController();
        }


//        relativeLayoutEvent.setOnTouchListener(new OnSwipeTouchListener(context) {
//            public void onSwipeTop() {
//                //Toast.makeText(context, "top", Toast.LENGTH_SHORT).show();
//                mRecyclerView.smoothScrollToPosition(0);
//                relativeLayoutEvent.startAnimation(animMoveUp);
//
//            }
//
//            public void onSwipeRight() {
//                //Toast.makeText(context, "right", Toast.LENGTH_SHORT).show();
//
//                miniPlayerView.startAnimation(animMoveRight);
//
//            }
//
//            public void onSwipeLeft() {
//                // Toast.makeText(context, "left", Toast.LENGTH_SHORT).show();
//                miniPlayerView.startAnimation(animMoveLeft);
//
//            }
//
//            public void onSwipeBottom() {
//                //Toast.makeText(context, "bottom", Toast.LENGTH_SHORT).show();
//            }
//
//        });

//        relativeLayoutEvent.setOnClickListener(v -> {
//            mRecyclerView.smoothScrollToPosition(0);
//            relativeLayoutEvent.startAnimation(animMoveUp);
//        });
        this.removeAllViews();

        if (appCMSPresenter.videoPlayerView==null){
            setVisibility(GONE);
            return;
        }
        addCloseButton();

        appCMSPresenter.videoPlayerView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        appCMSPresenter.videoPlayerView.setClickable(false);
        addView(appCMSPresenter.videoPlayerView);
        if (findViewById(relativeLayoutEventViewId) == null) {
            addView(relativeLayoutEvent);
        }

    }

    private void addCloseButton() {
        int tintColor = appCMSPresenter.getBrandPrimaryCtaColor();
        closePlayer = new ImageButton(context);
        closePlayer.setId(View.generateViewId());
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(ALIGN_PARENT_TOP | ALIGN_PARENT_RIGHT);
        closePlayer.setBackground(context.getDrawable(R.drawable.ic_deleteicon));
        closePlayer.getBackground().setTint(tintColor);
        closePlayer.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
        relativeLayoutEvent.addView(closePlayer, buttonParams);
        closePlayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                removeWithPause();
            }
        });
    }


    public void pipClick() {
        mRecyclerView.smoothScrollToPosition(0);
        //relativeLayoutEvent.startAnimation(animMoveUp);
    }

    private void removeWithPause() {
        if (appCMSPresenter.videoPlayerView != null &&
                appCMSPresenter.videoPlayerView.getPlayerView() != null) {
            appCMSPresenter.videoPlayerView.pausePlayer();
            appCMSPresenter.unrestrictPortraitOnly();
            appCMSPresenter.dismissPopupWindowPlayer(false);
            appCMSPresenter.setMiniPLayerVisibility(false);
        }
    }

    public RelativeLayout getRelativeLayoutEvent() {
        return relativeLayoutEvent;
    }

    public void disposeRelativeLayoutEvent() {
        this.relativeLayoutEvent.setVisibility(GONE);
        this.relativeLayoutEvent.setOnClickListener(null);
        this.relativeLayoutEvent = null;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public class OnSwipeTouchListener implements OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
    }

}
