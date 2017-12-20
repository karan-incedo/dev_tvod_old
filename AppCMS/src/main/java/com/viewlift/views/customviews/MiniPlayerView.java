package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;

/**
 * Created by sandeep.singh on 11/16/2017.
 */

public class MiniPlayerView extends RelativeLayout implements Animation.AnimationListener{

    private CustomVideoPlayerView videoPlayerView;
    private AppCMSPresenter appCMSPresenter;
    private Context context;
    private RelativeLayout relativeLayoutEvent;
    private int relativeLayoutEventViewId;
    private RelativeLayout.LayoutParams lpPipView;
    private RecyclerView mRecyclerView;
    private Animation animMoveRight,animMoveLeft,animMoveUp;
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

        animMoveRight = AnimationUtils.loadAnimation(context, R.anim.move_right);
        animMoveLeft = AnimationUtils.loadAnimation(context, R.anim.move_left);
        animMoveUp = AnimationUtils.loadAnimation(context, R.anim.move_up);

        animMoveRight.setAnimationListener(this);
        animMoveLeft.setAnimationListener(this);
        animMoveUp.setAnimationListener(this);

        lpPipView = new RelativeLayout.LayoutParams(BaseView.dpToPx(R.dimen.app_cms_mini_player_width, context),
                BaseView.dpToPx(R.dimen.app_cms_mini_player_height, context));
        lpPipView.rightMargin = BaseView.dpToPx(R.dimen.app_cms_mini_player_margin, context);
        lpPipView.bottomMargin = BaseView.dpToPx(R.dimen.app_cms_mini_player_margin, context);
        relativeLayoutEventViewId = View.generateViewId();
        relativeLayoutEvent.setId(relativeLayoutEventViewId);
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


        relativeLayoutEvent.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeTop() {
                //Toast.makeText(context, "top", Toast.LENGTH_SHORT).show();
                mRecyclerView.smoothScrollToPosition(0);
                relativeLayoutEvent.startAnimation(animMoveUp);

            }

            public void onSwipeRight() {
                //Toast.makeText(context, "right", Toast.LENGTH_SHORT).show();

                miniPlayerView.startAnimation(animMoveRight);

            }

            public void onSwipeLeft() {
                // Toast.makeText(context, "left", Toast.LENGTH_SHORT).show();
                miniPlayerView.startAnimation(animMoveLeft);

            }

            public void onSwipeBottom() {
                //Toast.makeText(context, "bottom", Toast.LENGTH_SHORT).show();
            }

        });

        relativeLayoutEvent.setOnClickListener(v -> {
            mRecyclerView.smoothScrollToPosition(0);
            relativeLayoutEvent.startAnimation(animMoveUp);
        });
        if (appCMSPresenter.videoPlayerView==null){
            setVisibility(GONE);
            return;
        }
        addView(appCMSPresenter.videoPlayerView);
        if (findViewById(relativeLayoutEventViewId) == null) {
            addView(relativeLayoutEvent);
        }



    }

    private void removeWithPause() {
        if (appCMSPresenter.videoPlayerView != null &&
                appCMSPresenter.videoPlayerView.getPlayerView() != null) {
            appCMSPresenter.videoPlayerView.pausePlayer();
            appCMSPresenter.dismissPopupWindowPlayer(false);
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
        if (animation != animMoveUp){
            removeWithPause();
        }

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

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }

}
