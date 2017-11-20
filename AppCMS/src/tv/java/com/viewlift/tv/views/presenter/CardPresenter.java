package com.viewlift.tv.views.presenter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;

import java.util.List;
import java.util.Map;

/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class CardPresenter extends Presenter {
    private String trayBackground;
    private AppCMSPresenter mAppCmsPresenter = null;
    private Context mContext;
    int i = 0;
    int mHeight = -1;
    int mWidth = -1;
    private Map<String , AppCMSUIKeyType> mJsonKeyValuemap;
    String borderColor = null;
    private Typeface fontType;

    public CardPresenter(Context context,
                         AppCMSPresenter appCMSPresenter,
                         int height,
                         int width,
                         String trayBackground,
                         Map<String,
            AppCMSUIKeyType> jsonKeyValuemap) {
        mContext = context;
        mAppCmsPresenter = appCMSPresenter;
        mHeight = height;
        mWidth = width;
        this.trayBackground = trayBackground;
        mJsonKeyValuemap = jsonKeyValuemap;
        borderColor = Utils.getFocusColor(mContext,appCMSPresenter);
    }

    public CardPresenter(Context context, AppCMSPresenter appCMSPresenter) {
        mContext = context;
        mAppCmsPresenter = appCMSPresenter;
        borderColor = Utils.getFocusColor(mContext,appCMSPresenter);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        //Log.d("Presenter" , " CardPresenter onCreateViewHolder******");
        final FrameLayout frameLayout = new FrameLayout(parent.getContext());
        FrameLayout.LayoutParams layoutParams;

        if(mHeight != -1 && mWidth != -1) {
            layoutParams = new FrameLayout.LayoutParams(
                    Utils.getViewXAxisAsPerScreen(mContext,mWidth),
                    Utils.getViewXAxisAsPerScreen(mContext,mHeight));
        }else{
            layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
        }
        frameLayout.setLayoutParams(layoutParams);
        frameLayout.setFocusable(true);

        frameLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_DPAD_UP
                        && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    frameLayout.clearFocus();
                }
                return false;
            }
        });

        if(mAppCmsPresenter.getTemplateType() == AppCMSPresenter.TemplateType.SPORTS){
            frameLayout.setBackground(Utils.getTrayBorder(mContext, Utils.getPrimaryHoverColor(mContext, mAppCmsPresenter), Utils.getSecondaryHoverColor(mContext, mAppCmsPresenter)));
        }
        return new ViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        //Log.d("Presenter" , " CardPresenter onBindViewHolder******. viewHolder: " + viewHolder + ", item: " + item);
        BrowseFragmentRowData rowData = (BrowseFragmentRowData)item;
        ContentDatum contentData = rowData.contentData;
        List<Component> componentList = rowData.uiComponentList;
        FrameLayout cardView = (FrameLayout) viewHolder.view;
        createComponent(componentList, cardView, contentData);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        try {
            if (null != viewHolder && null != viewHolder.view) {
                ((FrameLayout) viewHolder.view).removeAllViews();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createComponent(List<Component> componentList , ViewGroup parentLayout , ContentDatum contentData ){
        if(null != componentList && componentList.size() > 0) {
            for (Component component : componentList) {
                AppCMSUIKeyType componentType = mAppCmsPresenter.getJsonValueKeyMap().get(component.getType());
                if (componentType == null) {
                    componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                }

                AppCMSUIKeyType componentKey = mAppCmsPresenter.getJsonValueKeyMap().get(component.getKey());
                if (componentKey == null) {
                    componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                }

                switch (componentType) {
                    case MAIN_SVOD_SERVICE_TYPE:
                        break;
                    case ANDROID_AUTH_SCREEN_KEY:
                        break;
                    case ANDROID_SPLASH_SCREEN_KEY:
                        break;
                    case ANDROID_DOWNLOAD_SETTINGS_KEY:
                        break;
                    case ANDROID_DOWNLOAD_KEY:
                        break;
                    case ANDROID_HOME_SCREEN_KEY:
                        break;
                    case ANDROID_SUBSCRIPTION_SCREEN_KEY:
                        break;
                    case ANDROID_HISTORY_SCREEN_KEY:
                        break;
                    case ANDROID_WATCHLIST_SCREEN_KEY:
                        break;
                    case ANDROID_HOME_NAV_KEY:
                        break;
                    case ANDROID_MOVIES_NAV_KEY:
                        break;
                    case ANDROID_WATCHLIST_NAV_KEY:
                        break;
                    case ANDROID_DOWNLOAD_NAV_KEY:
                        break;
                    case PAGE_ACTIONLABEL_KEY:
                        break;
                    case ANDROID_HISTORY_NAV_KEY:
                        break;
                    case ANDROID_SETTINGS_NAV_KEY:
                        break;
                    case PAGE_BUTTON_SWITCH_KEY:
                        break;
                    case PAGE_BUTTON_KEY:
                        break;
                    case PAGE_IMAGE_KEY:
                        ImageView imageView = new ImageView(parentLayout.getContext());
                        switch(componentKey){
                            case PAGE_THUMBNAIL_IMAGE_KEY:
                                Integer itemWidth = Integer.valueOf(component.getLayout().getTv().getWidth());
                                Integer itemHeight = Integer.valueOf(component.getLayout().getTv().getHeight());
                                FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(

                                        Utils.getViewXAxisAsPerScreen(mContext, itemWidth),
                                        Utils.getViewYAxisAsPerScreen(mContext, itemHeight));
                                parms.setMargins(
                                        Integer.valueOf(component.getLayout().getTv().getLeftMargin()),
                                        Integer.valueOf(component.getLayout().getTv().getTopMargin()),
                                        0,
                                        0);

                                imageView.setLayoutParams(parms);
                               // parentLayout.setBackground(Utils.getTrayBorder(mContext,borderColor,component));

                                int gridImagePadding = Integer.valueOf(component.getLayout().getTv().getPadding());
                                imageView.setPadding(gridImagePadding,gridImagePadding,gridImagePadding,gridImagePadding);

                                if (itemWidth > itemHeight) {
                                    Glide.with(mContext)
                                            .load(contentData.getGist().getVideoImageUrl()+ "?impolicy=resize&w="+mWidth + "&h=" + mHeight).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                            .placeholder(R.drawable.video_image_placeholder)
                                            .error(ContextCompat.getDrawable(mContext, R.drawable.video_image_placeholder))
                                            .into(imageView);
                                } else {
                                    Glide.with(mContext)
                                            .load(contentData.getGist().getPosterImageUrl()+ "?impolicy=resize&w="+mWidth + "&h=" + mHeight).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                            .placeholder(R.drawable.poster_image_placeholder)
                                            .error(ContextCompat.getDrawable(mContext, R.drawable.poster_image_placeholder))
                                            .into(imageView);
                                }

                                //Log.d("TAG" , "Url = "+contentData.getGist().getPosterImageUrl()+ "?impolicy=resize&w="+mWidth + "&h=" + mHeight);
                                parentLayout.addView(imageView);
                                break;

                            case PAGE_BEDGE_IMAGE_KEY:
                                if(null != contentData.getGist().getBadgeImages() &&
                                        null != contentData.getGist().getBadgeImages().get_16x9()) {
                                    Integer bedgeitemWidth = Integer.valueOf(component.getLayout().getTv().getWidth());
                                    Integer bedgeitemHeight = Integer.valueOf(component.getLayout().getTv().getHeight());
                                    FrameLayout.LayoutParams bedgeParams = new FrameLayout.LayoutParams(
                                            Utils.getViewXAxisAsPerScreen(mContext, bedgeitemWidth),
                                            Utils.getViewYAxisAsPerScreen(mContext, bedgeitemHeight));

                                    bedgeParams.setMargins(
                                            Integer.valueOf(component.getLayout().getTv().getLeftMargin()),
                                            Integer.valueOf(component.getLayout().getTv().getTopMargin()),
                                            0,
                                            0);

                                    imageView.setLayoutParams(bedgeParams);

                                    Glide.with(mContext)
                                            .load(contentData.getGist().getBadgeImages().get_16x9() + "?impolicy=resize&w=" + bedgeitemWidth + "&h=" + bedgeitemHeight).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                            .into(imageView);
                                    parentLayout.addView(imageView);
                                }
                                break;

                        }
                        break;

                    case PAGE_LABEL_KEY:
                        TextView tvTitle = new TextView(parentLayout.getContext());
                        FrameLayout.LayoutParams layoutParams;
                        if (componentKey.equals(AppCMSUIKeyType.PAGE_THUMBNAIL_TIME_AND_DATE_KEY)) {
                            layoutParams = new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            tvTitle.setBackgroundColor(Color.parseColor(component.getBackgroundColor()));
                            tvTitle.setGravity(Gravity.CENTER);
                            Integer padding = Integer.valueOf(component.getLayout().getTv().getPadding());
                            tvTitle.setPadding(6, padding, 10, 4);
                            String time = Utils.convertSecondsToTime(contentData.getGist().getRuntime());

                            /*Date publishedDate = new Date(contentData.getGist().getPublishDate());
                            SimpleDateFormat spf = new SimpleDateFormat("MMM dd");
                            String date = spf.format(publishedDate);*/
                            tvTitle.setText(time/* + " | " + date*/);
                        } else {
                            layoutParams = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    Utils.getViewYAxisAsPerScreen(mContext, Integer.valueOf(component.getLayout().getTv().getHeight())));
                            tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                            tvTitle.setText(contentData.getGist().getTitle());
                        }
                        layoutParams.topMargin = Utils.getViewYAxisAsPerScreen(mContext, Integer.valueOf(component.getLayout().getTv().getTopMargin()));
                        layoutParams.leftMargin = Utils.getViewYAxisAsPerScreen(mContext, Integer.valueOf(component.getLayout().getTv().getLeftMargin()));
                        tvTitle.setLayoutParams(layoutParams);
                        tvTitle.setMaxLines(2);
                        tvTitle.setTextColor(Color.parseColor(component.getTextColor()));
                        if (fontType == null)
                            fontType = getFontType(component);
                        if (fontType != null) {
                            tvTitle.setTypeface(fontType);
                        }
                        tvTitle.setTextSize(component.getFontSize());
                        parentLayout.addView(tvTitle);
                        break;


                    case PAGE_COLLECTIONGRID_KEY:
                        break;
                    case PAGE_TABLE_VIEW_KEY:
                        break;
                    case PAGE_PROGRESS_VIEW_KEY:
                        FrameLayout.LayoutParams progressBarParams = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT ,
                                Utils.getViewYAxisAsPerScreen(mContext,Integer.valueOf(component.getLayout().getTv().getHeight())));
                        progressBarParams.topMargin =  Utils.getViewYAxisAsPerScreen(mContext,Integer.valueOf(component.getLayout().getTv().getYAxis()));

                        ProgressBar progressBar = new ProgressBar(mContext,
                                null,
                                R.style.Widget_AppCompat_ProgressBar_Horizontal);
                        progressBar.setLayoutParams(progressBarParams);

                        int gridImagePadding = Integer.valueOf(component.getLayout().getTv().getPadding());
                        progressBar.setPadding(gridImagePadding,0,gridImagePadding,0);
                        progressBar.setProgressDrawable(Utils.getProgressDrawable(mContext , component.getUnprogressColor() ,mAppCmsPresenter));
                        int progress = (int)Math.ceil(Utils.getPercentage(contentData.getGist().getRuntime() , contentData.getGist().getWatchedTime()));
                        //Log.d("NITS>>>","Runtime = "+  contentData.getGist().getRuntime()
//                           + " WatchedTime = "+ contentData.getGist().getWatchedTime()
//                        +" Percentage = " + contentData.getGist().getWatchedPercentage()
//                        +" Progress = "+progress);
                        progressBar.setProgress(progress);
                        progressBar.setFocusable(false);
                        parentLayout.addView(progressBar);
                    case PAGE_CAROUSEL_VIEW_KEY:
                        break;
                    case PAGE_VIDEO_PLAYER_VIEW_KEY:
                        break;
                    case PAGE_CAROUSEL_IMAGE_KEY:
                        break;
                    case PAGE_PAGE_CONTROL_VIEW_KEY:
                        break;
                    case PAGE_SEPARATOR_VIEW_KEY:
                        break;
                    case PAGE_SEGMENTED_VIEW_KEY:
                        break;
                    case PAGE_CASTVIEW_VIEW_KEY:
                        break;
                    case PAGE_BG_KEY:
                        break;
                    case PAGE_LOGO_KEY:
                        break;
                    case PAGE_INFO_KEY:
                        break;
                    case PAGE_PLAY_KEY:
                        break;
                    case PAGE_SHOW_KEY:
                        break;
                    case PAGE_WATCH_VIDEO_KEY:
                        break;
                    case PAGE_PLAY_IMAGE_KEY:
                        break;
                    case PAGE_TRAY_TITLE_KEY:
                        break;
                    case PAGE_THUMBNAIL_IMAGE_KEY:
                        break;
                    case PAGE_THUMBNAIL_TIME_AND_DATE_KEY:
                        break;
                    case PAGE_BADGE_IMAGE_KEY:
                        break;
                    case PAGE_THUMBNAIL_TITLE_KEY:
                        break;
                    case PAGE_TEXTALIGNMENT_CENTER_KEY:
                        break;
                    case PAGE_CAROUSEL_TITLE_KEY:
                        break;
                    case PAGE_CAROUSEL_INFO_KEY:
                        break;
                    case PAGE_CAROUSEL_ADD_TO_WATCHLIST_KEY:
                        break;
                    case PAGE_ADD_TO_WATCHLIST_KEY:
                        break;
                    case PAGE_DOWNLOAD_MODULE_KEY:
                        break;
                    case PAGE_DOWNLOAD_SETTING_MODULE_KEY:
                        break;
                    case PAGE_TEXT_BOLD_KEY:
                        break;
                    case PAGE_TEXT_MEDIUM_KEY:
                        break;
                    case PAGE_TEXT_LIGHT_KEY:
                        break;
                    case PAGE_TEXT_REGULAR_KEY:
                        break;
                    case PAGE_TEXT_SEMIBOLD_KEY:
                        break;
                    case PAGE_TEXT_EXTRABOLD_KEY:
                        break;
                    case PAGE_TEXT_OPENSANS_FONTFAMILY_KEY:
                        break;
                    case PAGE_TEXT_LATO_FONTFAMILY_KEY:
                        break;
                    case PAGE_TEXTVIEW_KEY:
                        break;
                    case PAGE_TEXTFIELD_KEY:
                        break;
                    case PAGE_EMAILTEXTFIELD_KEY:
                        break;
                    case PAGE_EMAILTEXTFIELD2_KEY:
                        break;
                    case PAGE_PASSWORDTEXTFIELD_KEY:
                        break;
                    case PAGE_PASSWORDTEXTFIELD2_KEY:
                        break;
                    case PAGE_FORGOTPASSWORD_KEY:
                        break;
                    case PAGE_RESET_PASSWORD_MODULE_KEY:
                        break;
                    case PAGE_CONTACT_US_MODULE_KEY:
                        break;
                    case PAGE_MOBILETEXTFIELD_KEY:
                        break;
                    case PAGE_AUTHENTICATION_MODULE_KEY:
                        break;
                    case PAGE_LOGIN_BUTTON_KEY:
                        break;
                    case PAGE_SIGNUP_BUTTON_KEY:
                        break;
                    case PAGE_PLAN_TITLE_KEY:
                        break;
                    case PAGE_PLAN_PRICEINFO_KEY:
                        break;
                    case PAGE_PLAN_BESTVALUE_KEY:
                        break;
                    case PAGE_PLAN_PURCHASE_BUTTON_KEY:
                        break;
                    case PAGE_PLAN_META_DATA_VIEW_KEY:
                        break;
                    case PAGE_HISTORY_MODULE_KEY:
                        break;
                    case PAGE_WATCHLIST_MODULE_KEY:
                        break;
                    case PAGE_CONTINUE_WATCHING_MODULE_KEY:
                        break;
                    case PAGE_SETTINGS_KEY:
                        break;
                    case PAGE_WATCHLIST_DURATION_KEY:
                        break;
                    case PAGE_WATCHLIST_DESCRIPTION_KEY:
                        break;
                    case PAGE_WATCHLIST_TITLE_KEY:
                        break;
                    case PAGE_API_HISTORY_MODULE_KEY:
                        break;
                    case PAGE_SUBSCRIPTION_SELECTPLAN_KEY:
                        break;
                    case PAGE_SUBSCRIPTION_IMAGEROW_KEY:
                        break;
                    case PAGE_PLANMETADATATITLE_KEY:
                        break;
                    case PAGE_PLANMETADDATAIMAGE_KEY:
                        break;
                    case PAGE_PLANMETADATADEVICECOUNT_KEY:
                        break;
                    case PAGE_SETTINGS_TITLE_KEY:
                        break;
                    case PAGE_SETTINGS_NAME_VALUE_KEY:
                        break;
                    case PAGE_SETTINGS_EMAIL_TITLE_KEY:
                        break;
                    case PAGE_SETTINGS_EMAIL_VALUE_KEY:
                        break;
                    case PAGE_SETTINGS_PLAN_VALUE_KEY:
                        break;
                    case PAGE_SETTINGS_PLAN_PROCESSOR_TITLE_KEY:
                        break;
                    case PAGE_SETTINGS_PLAN_PROCESSOR_VALUE_KEY:
                        break;
                    case PAGE_SETTINGS_EDIT_PROFILE_KEY:
                        break;
                    case PAGE_SETTINGS_CHANGE_PASSWORD_KEY:
                        break;
                    case CONTACT_US_PHONE_LABEL:
                        break;
                    case CONTACT_US_EMAIL_LABEL:
                        break;
                    case CONTACT_US_PHONE_IMAGE:
                        break;
                    case CONTACT_US_EMAIL_IMAGE:
                        break;
                    case PAGE_SETTINGS_CANCEL_PLAN_PROFILE_KEY:
                        break;
                    case PAGE_SETTINGS_UPGRADE_PLAN_PROFILE_KEY:
                        break;
                    case PAGE_SETTINGS_DOWNLOAD_QUALITY_PROFILE_KEY:
                        break;
                    case PAGE_SETTINGS_APP_VERSION_VALUE_KEY:
                        break;
                    case PAGE_USER_MANAGEMENT_DOWNLOAD_KEY:
                        break;
                    case PAGE_BACKGROUND_IMAGE_KEY:
                        break;
                    case PAGE_BACKGROUND_IMAGE_TYPE_KEY:
                        break;
                    case PAGE_TOGGLE_BUTTON_KEY:
                        break;
                    case PAGE_AUTOPLAY_TOGGLE_BUTTON_KEY:
                        break;
                    case PAGE_SD_CARD_FOR_DOWNLOADS_TOGGLE_BUTTON_KEY:
                        break;
                    case PAGE_CLOSED_CAPTIONS_TOGGLE_BUTTON_KEY:
                        break;
                    case PAGE_SD_CARD_FOR_DOWNLOADS_TEXT_KEY:
                        break;
                    case PAGE_SUBSCRIPTION_PAGE_KEY:
                        break;
                    case PAGE_VIDEO_DETAILS_KEY:
                        break;
                    case PAGE_LOGIN_COMPONENT_KEY:
                        break;
                    case PAGE_SIGNUP_COMPONENT_KEY:
                        break;
                    case PAGE_REMOVEALL_KEY:
                        break;
                    case PAGE_VIDEO_IMAGE_KEY:
                        break;
                    case PAGE_THUMBNAIL_VIDEO_IMAGE_KEY:
                        break;
                    case PAGE_START_WATCHING_BUTTON_KEY:
                        break;
                    case PAGE_VIDEO_PLAY_BUTTON_KEY:
                        break;
                    case PAGE_VIDEO_DESCRIPTION_KEY:
                        break;
                    case PAGE_VIDEO_TITLE_KEY:
                        break;
                    case PAGE_DOWNLOAD_SETTING_TITLE_KEY:
                        break;
                    case PAGE_VIDEO_SUBTITLE_KEY:
                        break;
                    case PAGE_VIDEO_SHARE_KEY:
                        break;
                    case PAGE_VIDEO_CLOSE_KEY:
                        break;
                    case PAGE_VIDEO_STARRATING_KEY:
                        break;
                    case PAGE_VIDEO_AGE_LABEL_KEY:
                        break;
                    case PAGE_VIDEO_CREDITS_DIRECTOR_KEY:
                        break;
                    case PAGE_VIDEO_CREDITS_DIRECTEDBY_KEY:
                        break;
                    case PAGE_VIDEO_CREDITS_DIRECTORS_KEY:
                        break;
                    case PAGE_VIDEO_CREDITS_STARRING_KEY:
                        break;
                    case PAGE_VIDEO_WATCH_TRAILER_KEY:
                        break;
                    case PAGE_VIDEO_DOWNLOAD_BUTTON_KEY:
                        break;
                    case PAGE_API_THUMBNAIL_URL:
                        break;
                    case PAGE_API_TITLE:
                        break;
                    case PAGE_API_DESCRIPTION:
                        break;
                    case PAGE_HEADER_KEY:
                        break;
                    case PAGE_TEXTALIGNMENT_KEY:
                        break;
                    case PAGE_TEXTALIGNMENT_LEFT_KEY:
                        break;
                    case PAGE_TEXTALIGNMENT_RIGHT_KEY:
                        break;
                    case PAGE_VIDEO_DETAIL_HEADER_KEY:
                        break;
                    case PAGE_EMPTY_KEY:
                        break;
                    case PAGE_NULL_KEY:
                        break;
                    case PAGE_AUTOPLAY_MODULE_KEY:
                        break;
                    case PAGE_AUTOPLAY_FINISHED_UP_TITLE_KEY:
                        break;
                    case PAGE_AUTOPLAY_MOVIE_TITLE_KEY:
                        break;
                    case PAGE_AUTOPLAY_MOVIE_SUBHEADING_KEY:
                        break;
                    case PAGE_AUTOPLAY_MOVIE_DESCRIPTION_KEY:
                        break;
                    case PAGE_AUTOPLAY_MOVIE_STAR_RATING_KEY:
                        break;
                    case PAGE_AUTOPLAY_MOVIE_DIRECTOR_LABEL_KEY:
                        break;
                    case PAGE_AUTOPLAY_MOVIE_SUB_DIRECTOR_LABEL_KEY:
                        break;
                    case PAGE_AUTOPLAY_MOVIE_IMAGE_KEY:
                        break;
                    case PAGE_AUTOPLAY_MOVIE_PLAY_BUTTON_KEY:
                        break;
                    case PAGE_AUTOPLAY_MOVIE_CANCEL_BUTTON_KEY:
                        break;
                    case PAGE_AUTOPLAY_MOVIE_PLAYING_IN_LABEL_KEY:
                        break;
                    case PAGE_AUTOPLAY_MOVIE_TIMER_LABEL_KEY:
                        break;
                    case PAGE_AUTOPLAY_BACK_KEY:
                        break;
                    case PAGE_CAROUSEL_MODULE_KEY:
                        break;
                    case PAGE_VIDEO_PLAYER_MODULE_KEY:
                        break;
                    case PAGE_TRAY_MODULE_KEY:
                        break;
                    case PAGE_SEASON_TRAY_MODULE_KEY:
                        break;
                    case PAGE_GRID_MODULE_KEY:
                        break;
                    case PAGE_DOWNLOAD_QUALITY_CONTINUE_BUTTON_KEY:
                        break;
                    case PAGE_DOWNLOAD_QUALITY_CANCEL_BUTTON_KEY:
                        break;
                    case PAGE_SETTING_TOGGLE_SWITCH_TYPE:
                        break;
                    case PAGE_SETTING_AUTOPLAY_TOGGLE_SWITCH_KEY:
                        break;
                    case PAGE_SETTING_CLOSED_CAPTION_TOGGLE_SWITCH_KEY:
                        break;
                    case RESET_PASSWORD_CANCEL_BUTTON_KEY:
                        break;
                    case RESET_PASSWORD_CONTINUE_BUTTON_KEY:
                        break;
                    case RESET_PASSWORD_TITLE_KEY:
                        break;
                    case PAGE_SETTING_LOGOUT_BUTTON_KEY:
                        break;
                    case PAGE_WATCHLIST_TITLE_LABEL:
                        break;
                    case PAGE_WATCHLIST_DESCRIPTION_LABEL:
                        break;
                    case PAGE_WATCHLIST_DELETE_ITEM_BUTTON:
                        break;
                    case PAGE_HISTORY_LAST_ADDED_LABEL_KEY:
                        break;
                    case PAGE_SIGNUP_FOOTER_LABEL_KEY:
                        break;
                    case PAGE_AUTOPLAY_FINISHED_MOVIE_TITLE_KEY:
                        break;
                    case PAGE_AUTOPLAY_UP_NEXT_LOADER_KEY:
                        break;
                    case PAGE_AUTOPLAY_ROTATING_LOADER_VIEW_KEY:
                        break;
                    case PAGE_SETTINGS_SUBSCRIPTION_DURATION_LABEL_KEY:
                        break;
                    case PAGE_SETTINGS_MANAGE_SUBSCRIPTION_BUTTON_KEY:
                        break;
                    case PAGE_SETTINGS_SUBSCRIPTION_LABEL_KEY:
                        break;
                    case PAGE_GRID_OPTION_KEY:
                        break;
                    case PAGE_GRID_THUMBNAIL_INFO:
                        break;
                    case PAGE_THUMBNAIL_BADGE_IMAGE:
                        break;
                    case PAGE_BANNER_IMAGE:
                        break;
                    case PAGE_BANNER_DETAIL_KEY:
                        break;
                    case PAGE_BANNER_DETAIL_BACKGROUND:
                        break;
                    case PAGE_BANNER_DETAIL_ICON:
                        break;
                    case PAGE_BANNER_DETAIL_BUTTON:
                        break;
                    case PAGE_BANNER_DETAIL_TITLE:
                        break;
                    case PAGE_PLAY_LIVE_IMAGE_KEY:
                        break;
                    case PAGE_SETTINGS_USER_EMAIL_LABEL_KEY:
                        break;
                    case START_WATCHING_KEY:
                        break;
                }
            }
        }
    }


    private Typeface getFontType(Component component){
        Typeface face = null;
        if (mJsonKeyValuemap.get(component.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY) {
            AppCMSUIKeyType fontWeight = mJsonKeyValuemap.get(component.getFontWeight());
            if (fontWeight == null) {
                fontWeight = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            switch (fontWeight) {
                case PAGE_TEXT_BOLD_KEY:
                    face = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.opensans_bold_ttf));
                    break;
                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.opensans_semibold_ttf));
                    break;
                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.opensans_extrabold_ttf));
                    break;
                default:
                    face = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.opensans_regular_ttf));
            }
        }
        return face;
    }

}
