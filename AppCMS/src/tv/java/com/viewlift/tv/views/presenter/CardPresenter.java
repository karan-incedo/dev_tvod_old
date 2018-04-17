package com.viewlift.tv.views.presenter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Season_;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;
import com.viewlift.views.customviews.CustomTypefaceSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class CardPresenter extends Presenter {
    private static final String TAG = CardPresenter.class.getCanonicalName();
    private String trayBackground;
    private Component mComponent;
    private AppCMSPresenter mAppCmsPresenter = null;
    private Context mContext;
    int i = 0;
    int mHeight = -1;
    int mWidth = -1;
    private Map<String , AppCMSUIKeyType> mJsonKeyValuemap;
    String borderColor = null;
    private Typeface fontType;
    private boolean consumeUpKeyEvent = false;

    public CardPresenter(Context context,
                         AppCMSPresenter appCMSPresenter,
                         int height,
                         int width,
                         Component component,
                         Map<String,
                                 AppCMSUIKeyType> jsonKeyValuemap) {
        mContext = context;
        mAppCmsPresenter = appCMSPresenter;
        mHeight = height;
        mWidth = width;
        mComponent = component;
        this.trayBackground = mComponent.getTrayBackground();
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
        final CustomFrameLayout frameLayout = new CustomFrameLayout(parent.getContext());
        FrameLayout.LayoutParams layoutParams;

        if (mHeight != -1 && mWidth != -1) {
            layoutParams = new FrameLayout.LayoutParams(
                    Utils.getViewXAxisAsPerScreen(mContext, mWidth),
                    Utils.getViewXAxisAsPerScreen(mContext, mHeight));
        } else {
            layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
        }
        frameLayout.setLayoutParams(layoutParams);
        frameLayout.setFocusable(true);
        createComponentView(mComponent ,frameLayout);
        return new ViewHolder(frameLayout);
    }

    private void createComponentView(Component parentComponent , CustomFrameLayout parentLayout) {
        List<Component> componentList = parentComponent.getComponents();
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
                    case PAGE_IMAGE_KEY:
                        ImageView imageView = new ImageView(parentLayout.getContext());
                        switch (componentKey) {
                            case PAGE_THUMBNAIL_IMAGE_KEY: {
                                Integer itemWidth = Integer.valueOf(component.getLayout().getTv().getWidth());
                                Integer itemHeight = Integer.valueOf(component.getLayout().getTv().getHeight());
                                FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(
                                        Utils.getViewXAxisAsPerScreen(mContext, itemWidth),
                                        Utils.getViewYAxisAsPerScreen(mContext, itemHeight));
                                int leftMargin = 0;
                                int topMargin = 0;
                                if (component.getLayout() != null
                                        && component.getLayout().getTv() != null) {
                                    if (component.getLayout().getTv().getLeftMargin() != null) {
                                        leftMargin = Integer.valueOf(component.getLayout().getTv().getLeftMargin());
                                    }
                                    if (component.getLayout().getTv().getTopMargin() != null) {
                                        topMargin = Integer.valueOf(component.getLayout().getTv().getTopMargin());
                                    }
                                }
                                parms.setMargins(leftMargin, topMargin, 0, 0);

                                imageView.setLayoutParams(parms);
                                int gridImagePadding = Integer.valueOf(component.getLayout().getTv().getPadding());
                                imageView.setPadding(gridImagePadding, gridImagePadding, gridImagePadding, gridImagePadding);
                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                parentLayout.addView(imageView);
                                parentLayout.addChildComponentAndView(imageView, component);
                                break;
                            }

                            case PAGE_BEDGE_IMAGE_KEY: {
                                Integer badgeItemWidth = Integer.valueOf(component.getLayout().getTv().getWidth());
                                Integer badgeItemHeight = Integer.valueOf(component.getLayout().getTv().getHeight());

                                FrameLayout.LayoutParams badgeParams = new FrameLayout.LayoutParams(
                                        Utils.getViewXAxisAsPerScreen(mContext, badgeItemWidth),
                                        Utils.getViewYAxisAsPerScreen(mContext, badgeItemHeight));

                                badgeParams.setMargins(
                                        Integer.valueOf(component.getLayout().getTv().getLeftMargin()),
                                        Integer.valueOf(component.getLayout().getTv().getTopMargin()),
                                        0,
                                        0);

                                imageView.setLayoutParams(badgeParams);
                                parentLayout.addView(imageView);
                                parentLayout.addChildComponentAndView(imageView, component);
                                break;
                            }
                            case PAGE_SEPARATOR_VIEW_KEY: {
                                Integer itemWidth = Integer.valueOf(component.getLayout().getTv().getWidth());
                                Integer itemHeight = Integer.valueOf(component.getLayout().getTv().getHeight());
                                FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(
                                        Utils.getViewXAxisAsPerScreen(mContext, itemWidth),
                                        Utils.getViewYAxisAsPerScreen(mContext, itemHeight));
                                int leftMargin = 0;
                                int topMargin = 0;
                                if (component.getLayout() != null
                                        && component.getLayout().getTv() != null) {
                                    if (component.getLayout().getTv().getLeftMargin() != null) {
                                        leftMargin = Integer.valueOf(component.getLayout().getTv().getLeftMargin());
                                    }
                                    if (component.getLayout().getTv().getTopMargin() != null) {
                                        topMargin = Integer.valueOf(component.getLayout().getTv().getTopMargin());
                                    }
                                }
                                parms.setMargins(leftMargin, topMargin, 0, 0);

                                imageView.setLayoutParams(parms);
                                int gridImagePadding = Integer.valueOf(component.getLayout().getTv().getPadding());
                                imageView.setPadding(gridImagePadding, gridImagePadding, gridImagePadding, gridImagePadding);
                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                parentLayout.hoverLayout.addView(imageView);
                                parentLayout.addChildComponentAndView(imageView, component);
                                parentLayout.setHoverBackground(imageView);

                                imageView.setId(R.id.videoBackgroundOnHover);
                                imageView.setBackgroundColor(Color.parseColor(component.getBackgroundColor()));
                                imageView.setAlpha(0f);
                                break;
                            }

                        }
                        break;

                    case PAGE_LABEL_KEY:{
                        TextView tvTitle = new TextView(parentLayout.getContext());
                        FrameLayout.LayoutParams layoutParams;
                        if (componentKey.equals(AppCMSUIKeyType.PAGE_THUMBNAIL_TIME_AND_DATE_KEY)) {
                            layoutParams = new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            StringBuilder stringBuilder = new StringBuilder();
                            if (mAppCmsPresenter.getAppCMSMain().getBrand().getMetadata() != null) {
                                tvTitle.setBackgroundColor(Color.parseColor(mAppCmsPresenter.getAppBackgroundColor()));
                                tvTitle.getBackground().setAlpha(128);
                                tvTitle.setGravity(Gravity.CENTER);
                                Integer padding = Integer.valueOf(component.getLayout().getTv().getPadding());
                                tvTitle.setPadding(6, padding, 10, 4);
                                tvTitle.setVisibility(View.VISIBLE);
                            } else /*Don't show time and date as metadata is null*/ {
                                tvTitle.setVisibility(View.INVISIBLE);
                            }
                            parentLayout.addView(tvTitle);
                            parentLayout.addChildComponentAndView(tvTitle, component);
                        } else if (componentKey.equals(AppCMSUIKeyType.PAGE_EPISODE_THUMBNAIL_TITLE_KEY)) {
                            Integer height = component.getLayout().getTv().getHeight() != null
                                    ? Integer.valueOf(component.getLayout().getTv().getHeight())
                                    : 0;
                            layoutParams = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    Utils.getViewYAxisAsPerScreen(mContext, height));
                            tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                            parentLayout.addView(tvTitle);
                            parentLayout.addChildComponentAndView(tvTitle, component);
                        } else if (componentKey.equals(AppCMSUIKeyType.PAGE_VIDEO_TITLE_ON_HOVER_KEY)) {
                            tvTitle.setId(R.id.videoTitleOnHover);
                            Integer itemWidth = Integer.valueOf(component.getLayout().getTv().getWidth());
                            layoutParams = new FrameLayout.LayoutParams(
                                    itemWidth,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            tvTitle.setAlpha(0);
                            parentLayout.setHoverTitle(tvTitle);

                            parentLayout.hoverLayout.addView(tvTitle);
                            parentLayout.addChildComponentAndView(tvTitle, component);
                        } else if (componentKey.equals(AppCMSUIKeyType.PAGE_VIDEO_SUB_TITLE_ON_HOVER_KEY)) {
                            tvTitle.setId(R.id.videoSubTitleOnHover);
                            tvTitle.setAlpha(0);
                            Integer itemWidth = Integer.valueOf(component.getLayout().getTv().getWidth());
                            layoutParams = new FrameLayout.LayoutParams(
                                    itemWidth,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            parentLayout.setHoverSubTitle(tvTitle);
                            parentLayout.hoverLayout.addView(tvTitle);
                            parentLayout.addChildComponentAndView(tvTitle, component);
                        } else if (componentKey.equals(AppCMSUIKeyType.PAGE_VIDEO_DESCRIPTION_ON_HOVER_KEY)) {
                            tvTitle.setId(R.id.videoDescriptionOnHover);
                            Integer itemWidth = Integer.valueOf(component.getLayout().getTv().getWidth());
                            layoutParams = new FrameLayout.LayoutParams(
                                    itemWidth,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            tvTitle.setAlpha(0);
                            parentLayout.setHoverDescription(tvTitle);
                            parentLayout.hoverLayout.addView(tvTitle);
                            parentLayout.addChildComponentAndView(tvTitle, component);
                        } else {
                            Integer height = component.getLayout().getTv().getHeight() != null
                                    ? Integer.valueOf(component.getLayout().getTv().getHeight())
                                    : 0;
                            layoutParams = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    Utils.getViewYAxisAsPerScreen(mContext, height));
                            tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                            parentLayout.addView(tvTitle);
                            parentLayout.addChildComponentAndView(tvTitle, component);}
                        //tvTitle.setSingleLine(true);
                        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                        tvTitle.setSelected(true);

                        if (component.getLayout().getTv().getTopMargin() != null)
                            layoutParams.topMargin = Utils.getViewYAxisAsPerScreen(mContext, Integer.valueOf(component.getLayout().getTv().getTopMargin()));
                        else
                            layoutParams.topMargin = Utils.getViewYAxisAsPerScreen(mContext, 0);

                        if (component.getLayout().getTv().getLeftMargin() != null)
                            layoutParams.leftMargin = Utils.getViewYAxisAsPerScreen(mContext, Integer.valueOf(component.getLayout().getTv().getLeftMargin()));
                        else
                            layoutParams.leftMargin = Utils.getViewYAxisAsPerScreen(mContext, 0);

                        tvTitle.setLayoutParams(layoutParams);
                        tvTitle.setMaxLines(2);
                        tvTitle.setTextColor(Color.parseColor(mAppCmsPresenter.getAppCtaTextColor()));
                        if (component.getFontFamily() != null)
                            fontType = getFontType(component);
                        if (fontType != null) {
                            tvTitle.setTypeface(fontType);
                        }
                        if (component.getText() != null)
                            tvTitle.setText(component.getText());
                        if (component.getFontSize() != 0) {
                            tvTitle.setTextSize(component.getFontSize());
                        }
                        break;
                }
                    case PAGE_PROGRESS_VIEW_KEY: {
                        FrameLayout.LayoutParams progressBarParams = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                Utils.getViewYAxisAsPerScreen(mContext, Integer.valueOf(component.getLayout().getTv().getHeight())));
                        progressBarParams.topMargin = Utils.getViewYAxisAsPerScreen(mContext, Integer.valueOf(component.getLayout().getTv().getYAxis()));

                        ProgressBar progressBar = new ProgressBar(mContext,
                                null,
                                R.style.Widget_AppCompat_ProgressBar_Horizontal);
                        progressBar.setLayoutParams(progressBarParams);

                        int gridImagePadding = Integer.valueOf(component.getLayout().getTv().getPadding());
                        progressBar.setPadding(gridImagePadding, 0, gridImagePadding, 0);
                        progressBar.setProgressDrawable(Utils.getProgressDrawable(mContext, component.getUnprogressColor(), mAppCmsPresenter));
                        progressBar.setFocusable(false);
                        parentLayout.addView(progressBar);
                        parentLayout.addChildComponentAndView(progressBar, component);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        BrowseFragmentRowData rowData = (BrowseFragmentRowData)item;
        ContentDatum contentData = rowData.contentData;
        List<Component> componentList = rowData.uiComponentList;
        String blockName = rowData.blockName;
        CustomFrameLayout cardView = (CustomFrameLayout) viewHolder.view;
        if(null != blockName && ( blockName.equalsIgnoreCase("tray03"))){
            cardView.setBackground(
                    Utils.getTrayBorder(mContext, Utils.getPrimaryHoverColor(mContext, mAppCmsPresenter), mAppCmsPresenter.getAppBackgroundColor() /*Utils.getSecondaryHoverColor(mContext, mAppCmsPresenter)*/));
        }
        bindComponent(cardView , contentData , blockName);
        cardView.postInvalidate();
        //createComponent(componentList, cardView, contentData,blockName);

        cardView.setOnKeyListener((v, keyCode, event) -> {
            if(keyCode == KeyEvent.KEYCODE_DPAD_UP
                    && event.getAction() == KeyEvent.ACTION_UP){
                if (rowData.rowNumber == 0) {
                    if (consumeUpKeyEvent) {
                        cardView.clearFocus();
                        consumeUpKeyEvent = false;
                    }
                    consumeUpKeyEvent = true;
                } else {
                    consumeUpKeyEvent = false;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                consumeUpKeyEvent = false;
            }
            return false;
        });
    }



    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }

    private void bindComponent(CustomFrameLayout cardView, ContentDatum contentData, String blockName) {
        List<CustomFrameLayout.ChildComponentAndView> childComponentAndViewsList = cardView.getChildViewList();
        if(null != childComponentAndViewsList && childComponentAndViewsList.size() > 0){
            for(CustomFrameLayout.ChildComponentAndView childComponentAndView : childComponentAndViewsList){
                AppCMSUIKeyType componentType = mAppCmsPresenter.getJsonValueKeyMap().get(childComponentAndView.component.getType());
                if (componentType == null) {
                    componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                }

                AppCMSUIKeyType componentKey = mAppCmsPresenter.getJsonValueKeyMap().get(childComponentAndView.component.getKey());
                if (componentKey == null) {
                    componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                }

                switch (componentType) {
                    case PAGE_IMAGE_KEY:
                        ImageView imageView = (ImageView) childComponentAndView.childView;
                        switch(componentKey){
                            case PAGE_THUMBNAIL_IMAGE_KEY:
                                Integer itemWidth = Integer.valueOf(childComponentAndView.component.getLayout().getTv().getWidth());
                                Integer itemHeight = Integer.valueOf(childComponentAndView.component.getLayout().getTv().getHeight());
                               // imageView.setBackground(null);
                                if (itemWidth > itemHeight) {
                                    Glide.with(mContext)
                                            .load(contentData.getGist().getVideoImageUrl() + "?impolicy=resize&w=" + mWidth + "&h=" + mHeight)
                                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                                    .placeholder(R.drawable.video_image_placeholder)
                                                    .error(ContextCompat.getDrawable(mContext, R.drawable.video_image_placeholder)))
                                            .into(imageView);
                                } else {
                                    Glide.with(mContext)
                                            .load(contentData.getGist().getPosterImageUrl() + "?impolicy=resize&w=" + mWidth + "&h=" + mHeight)
                                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                                    .placeholder(R.drawable.poster_image_placeholder)
                                                    .error(ContextCompat.getDrawable(mContext, R.drawable.poster_image_placeholder)))
                                            .into(imageView);
                                }

                                if (null != blockName && (blockName.equalsIgnoreCase("tray01")
                                        || blockName.equalsIgnoreCase("tray02")
                                        || blockName.equalsIgnoreCase("grid01")
                                        || blockName.equalsIgnoreCase("showDetail01")
                                        || blockName.equalsIgnoreCase("tray04")
                                        || blockName.equalsIgnoreCase("continuewatching01"))) {
                                    imageView.setBackground(Utils.getTrayBorder(mContext, borderColor, childComponentAndView.component));
                                }
                                break;

                            case PAGE_BEDGE_IMAGE_KEY:
                                if (null != contentData.getGist().getBadgeImages()) {
                                    Integer badgeItemWidth = Integer.valueOf(childComponentAndView.component.getLayout().getTv().getWidth());
                                    Integer badgeItemHeight = Integer.valueOf(childComponentAndView.component.getLayout().getTv().getHeight());
                                    try {
                                        String imageUrl;
                                        if (badgeItemWidth > badgeItemHeight) {
                                            imageUrl = contentData.getGist().getBadgeImages()
                                                    .get_16x9() + "?impolicy=resize" +
                                                    "&w=" + badgeItemWidth +
                                                    "&h=" + badgeItemHeight;
                                        } else {
                                            imageUrl = contentData.getGist().getBadgeImages()
                                                    .get_3x4() + "?impolicy=resize" +
                                                    "&w=" + badgeItemWidth +
                                                    "&h=" + badgeItemHeight;
                                        }
                                        //      imageView.setBackground(null);
                                        Glide.with(mContext)
                                                .load(imageUrl)
                                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                                                .into(imageView);

                                    }catch (Exception e){

                                    }
                                }
                                break;

                        }
                        break;

                    case PAGE_LABEL_KEY:
                        TextView tvTitle = (TextView) childComponentAndView.childView;
                        //tvTitle.setText(mContext.getResources().getString(R.string.blank_string));
                        if (componentKey.equals(AppCMSUIKeyType.PAGE_THUMBNAIL_TIME_AND_DATE_KEY)) {
                            StringBuilder stringBuilder = new StringBuilder();
                            if (mAppCmsPresenter.getAppCMSMain().getBrand().getMetadata() != null) {
                                String time = Utils.convertSecondsToTime(contentData.getGist().getRuntime());
                                String date = null;
                                if(null != contentData
                                        && null != contentData.getGist()
                                        && null != contentData.getGist().getPublishDate()) {
                                    try {
                                        date = mAppCmsPresenter.getDateFormat(
                                                Long.parseLong(contentData.getGist().getPublishDate()),
                                                "MMMM dd");
                                    } catch (Exception e) {
                                    }
                                }
                                if (mAppCmsPresenter.getAppCMSMain().getBrand().getMetadata().isDisplayDuration()) {
                                    stringBuilder.append(time);
                                }
                                if (mAppCmsPresenter.getAppCMSMain().getBrand().getMetadata().isDisplayPublishedDate() && null != date) {
                                    if (stringBuilder.length() > 0) stringBuilder.append(" | ");
                                    stringBuilder.append(date);
                                }
                                tvTitle.setVisibility(View.VISIBLE);
                            } else /*Don't show time and date as metadata is null*/ {
                                tvTitle.setVisibility(View.INVISIBLE);
                            }
                            tvTitle.setText(stringBuilder);
                            tvTitle.setTextSize(childComponentAndView.component.getFontSize());
                        } else if (componentKey.equals(AppCMSUIKeyType.PAGE_EPISODE_THUMBNAIL_TITLE_KEY)) {
                            try {
                                tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                                int episodeNumber = getEpisodeNumber(contentData,
                                        contentData.getGist().getId());
                                String text = contentData.getGist().getTitle();
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(Integer.toString(episodeNumber));
                                spannableStringBuilder.append(" ").append(text);
                                String fontFamily = mAppCmsPresenter.getFontFamily();
                                String fontPath = null != fontFamily ? "font/" + fontFamily + "-ExtraBold.ttf" : "fonts/OpenSans-ExtraBold.ttf";
                                Typeface font = Typeface.createFromAsset(mContext.getResources().getAssets(), fontPath);
                                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#7b7b7b")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannableStringBuilder.setSpan(new CustomTypefaceSpan("", font), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                                tvTitle.setText(spannableStringBuilder);
                            }catch (Exception e){

                            }
                        } else if (componentKey.equals(AppCMSUIKeyType.PAGE_THUMBNAIL_TITLE_KEY)){
                            tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                            tvTitle.setText(contentData.getGist().getTitle());
                            tvTitle.setMaxLines(2);
                        }else if (componentKey.equals(AppCMSUIKeyType.PAGE_VIDEO_TITLE_ON_HOVER_KEY)){
                           Log.d(TAG, "ANAS: hover Title");
                           tvTitle.setMaxLines(childComponentAndView.component.getNumberOfLines());
                            tvTitle.setText(contentData.getGist().getTitle());
                            tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            tvTitle.setSelected(true);
                        }else if (componentKey.equals(AppCMSUIKeyType.PAGE_VIDEO_SUB_TITLE_ON_HOVER_KEY)){
                            tvTitle.setMaxLines(childComponentAndView.component.getNumberOfLines());
                            Log.d(TAG, "ANAS: hover sub Title");
                        }else if (componentKey.equals(AppCMSUIKeyType.PAGE_VIDEO_DESCRIPTION_ON_HOVER_KEY)){
                            tvTitle.setMaxLines(childComponentAndView.component.getNumberOfLines());
                            Log.d(TAG, "ANAS: hover description");
                            tvTitle.setText(contentData.getGist().getDescription());
                        }
                        //tvTitle.setSingleLine(true);
//                        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
//                        tvTitle.setSelected(true);

                        break;

                    case PAGE_PROGRESS_VIEW_KEY:
                        ProgressBar progressBar = (ProgressBar) childComponentAndView.childView;
                        progressBar.setProgress(0);
                        int progress = (int) Math.ceil(Utils.getPercentage(contentData.getGist().getRuntime(), contentData.getGist().getWatchedTime()));
                        progressBar.setProgress(progress);
                        break;
                }
            }
        }
    }


    private Typeface getFontType(Component component) {
        Typeface face = null;
        if (mJsonKeyValuemap.get(mAppCmsPresenter.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY) {
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
        } else if (mJsonKeyValuemap.get(mAppCmsPresenter.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_LATO_FONTFAMILY_KEY) {
            AppCMSUIKeyType fontWeight = mJsonKeyValuemap.get(component.getFontWeight());
            if (fontWeight == null) {
                fontWeight = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            switch (fontWeight) {
                case PAGE_TEXT_BOLD_KEY:
                    face = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.lato_bold));
                    break;
                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.lato_medium));
                    break;
                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.lato_bold));
                    break;
                default:
                    face = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.lato_regular));
            }
        }
        return face;
    }
    private int getEpisodeNumber(ContentDatum mainContentData, String id) {
        int returnVal = 0;
        if (mainContentData.getSeason() != null) {
            for (int seasonNumber = 0; seasonNumber < mainContentData.getSeason().size(); seasonNumber++) {
                Season_ season = mainContentData.getSeason().get(seasonNumber);
                for (int episodeNumber = 0; episodeNumber < season.getEpisodes().size(); episodeNumber++) {
                    ContentDatum contentDatum = season.getEpisodes().get(episodeNumber);
                    if (contentDatum.getGist().getId().equalsIgnoreCase(id)) {
                        returnVal = episodeNumber + 1;
                        break;
                    }
                }
                if (returnVal > 0) break;
            }
        }

        return returnVal;
    }


    class CustomFrameLayout extends FrameLayout{
        List<CustomFrameLayout.ChildComponentAndView> childView = null;

        public TextView getHoverTitle() {
            return hoverTitle;
        }

        public void setHoverTitle(TextView hoverTitle) {
            this.hoverTitle = hoverTitle;
        }

        public TextView getHoverSubTitle() {
            return hoverSubTitle;
        }

        public void setHoverSubTitle(TextView hoverSubTitle) {
            this.hoverSubTitle = hoverSubTitle;
        }

        public TextView getHoverDescription() {
            return hoverDescription;
        }

        public void setHoverDescription(TextView hoverDescription) {
            this.hoverDescription = hoverDescription;
        }

        public View getHoverBackground() {
            return hoverBackground;
        }

        public void setHoverBackground(View hoverBackground) {
            this.hoverBackground = hoverBackground;
        }

        TextView hoverTitle;// = itemViewHolder.view.findViewById(R.id.videoTitleOnHover);
        TextView hoverSubTitle;// = itemViewHolder.view.findViewById(R.id.videoSubTitleOnHover);
        TextView hoverDescription;// = itemViewHolder.view.findViewById(R.id.videoDescriptionOnHover);
        View hoverBackground;// = itemViewHolder.view.findViewById(R.id.videoBackgroundOnHover);
        FrameLayout hoverLayout;
        public CustomFrameLayout(@NonNull Context context) {
            super(context);
            childView = new ArrayList<>();
            FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            hoverLayout = new FrameLayout(context);
            hoverLayout.setLayoutParams(layoutParams);
            this.addView(hoverLayout);
        }

        public List<CustomFrameLayout.ChildComponentAndView> getChildViewList(){
            return childView;
        }

        public void addChildComponentAndView(View componentView, Component component) {
            CustomFrameLayout.ChildComponentAndView childComponentAndView = new CustomFrameLayout.ChildComponentAndView();
            childComponentAndView.childView = componentView;
            childComponentAndView.component = component;
            childView.add(childComponentAndView);
        }

        public class ChildComponentAndView {
            Component component;
            View childView;
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            if (selected) {
                Log.d(TAG, "Selected");
                if (hoverTitle != null
                        && hoverSubTitle != null
                        && hoverDescription != null
                        && hoverBackground != null) {
                    hoverLayout.bringToFront();
                    hoverLayout.setVisibility(VISIBLE);
                    startHoverAnimation(hoverTitle, hoverSubTitle, hoverDescription, hoverBackground);
                }
            } else {
                Log.d(TAG, "Deselected");
                if (hoverTitle != null
                        && hoverSubTitle != null
                        && hoverDescription != null
                        && hoverBackground != null) {
                    hoverTitle.setAlpha(0);
                    hoverSubTitle.setAlpha(0);
                    hoverDescription.setAlpha(0);
                    hoverBackground.setAlpha(0);
                    hoverLayout.setVisibility(INVISIBLE);
                }
            }
        }

        public void startHoverAnimation(TextView hoverTitle,
                                         TextView hoverSubTitle,
                                         TextView hoverDescription,
                                         View hoverBackground) {

            int translateStartPosition = 20;
            int translateEndPosition = 0;
            int duration = 500;

            float alphaStartVal = 0f;
            float alphaEndVal = 1f;
            AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

            ObjectAnimator translationY = ObjectAnimator.ofFloat(hoverTitle, "translationY", translateStartPosition, translateEndPosition);
            translationY.setDuration(duration);
            translationY.setInterpolator(interpolator);
            translationY.start();

            ObjectAnimator translationY1 = ObjectAnimator.ofFloat(hoverSubTitle, "translationY", translateStartPosition, translateEndPosition);
            translationY1.setDuration(duration);
            translationY1.setStartDelay(duration / 2);
            translationY1.setInterpolator(interpolator);
            translationY1.start();

            ObjectAnimator translationY2 = ObjectAnimator.ofFloat(hoverDescription, "translationY", translateStartPosition, translateEndPosition);
            translationY2.setDuration(duration);
            translationY2.setStartDelay(duration);
            translationY2.setInterpolator(interpolator);
            translationY.addListener(getAnimatorListener());
            translationY2.start();

            ObjectAnimator alpha = ObjectAnimator.ofFloat(hoverTitle, "alpha", alphaStartVal, alphaEndVal);
            alpha.setDuration(duration);
            alpha.setInterpolator(interpolator);
            alpha.start();

            ObjectAnimator alpha1 = ObjectAnimator.ofFloat(hoverSubTitle, "alpha", alphaStartVal, alphaEndVal);
            alpha1.setDuration(duration);
            alpha1.setStartDelay(duration / 2);
            alpha1.setInterpolator(interpolator);
            alpha1.start();

            ObjectAnimator alpha2 = ObjectAnimator.ofFloat(hoverDescription, "alpha", alphaStartVal, alphaEndVal);
            alpha2.setDuration(duration);
            alpha2.setStartDelay(duration);
            alpha2.setInterpolator(interpolator);
            alpha2.start();

            ObjectAnimator alpha3 = ObjectAnimator.ofFloat(hoverBackground, "alpha", alphaStartVal, alphaEndVal);
            alpha3.setDuration(duration);
            alpha3.setInterpolator(interpolator);
            alpha3.start();
        }

        @NonNull
        private Animator.AnimatorListener getAnimatorListener() {
            return new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            /*CustomFrameLayout.this.hoverTitle.setVisibility(VISIBLE);
                            CustomFrameLayout.this.hoverSubTitle.setVisibility(VISIBLE);
                            CustomFrameLayout.this.hoverDescription.setVisibility(VISIBLE);
                            CustomFrameLayout.this.hoverBackground.setVisibility(VISIBLE);*/
                            CustomFrameLayout.this.hoverLayout.setVisibility(VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (!CustomFrameLayout.this.isSelected()) {
                                CustomFrameLayout.this.hoverLayout.setVisibility(INVISIBLE);
                            }

                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    };
        }

    }
}