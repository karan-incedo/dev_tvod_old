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

import java.text.SimpleDateFormat;
import java.util.Date;
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
    private boolean consumeUpKeyEvent = false;


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
        return new ViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        //Log.d("Presenter" , " CardPresenter onBindViewHolder******. viewHolder: " + viewHolder + ", item: " + item);
        BrowseFragmentRowData rowData = (BrowseFragmentRowData)item;
        ContentDatum contentData = rowData.contentData;
        List<Component> componentList = rowData.uiComponentList;
        String blockName = rowData.blockName;
        FrameLayout cardView = (FrameLayout) viewHolder.view;
        if(null != blockName && ( blockName.equalsIgnoreCase("tray03"))){
            cardView.setBackground(Utils.getTrayBorder(mContext, Utils.getPrimaryHoverColor(mContext, mAppCmsPresenter), Utils.getSecondaryHoverColor(mContext, mAppCmsPresenter)));
        }
        createComponent(componentList, cardView, contentData,blockName);

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
        try {
            if (null != viewHolder && null != viewHolder.view) {
                ((FrameLayout) viewHolder.view).removeAllViews();
            }
        }catch (Exception e){
        }
    }

    public void createComponent(List<Component> componentList , ViewGroup parentLayout , ContentDatum contentData , String blockName){
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
                        switch(componentKey){
                            case PAGE_THUMBNAIL_IMAGE_KEY:
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
                                if (null != blockName && (blockName.equalsIgnoreCase("tray01")
                                        || blockName.equalsIgnoreCase("tray02")
                                        || blockName.equalsIgnoreCase("grid01")
                                        || blockName.equalsIgnoreCase("showDetail01")
                                        || blockName.equalsIgnoreCase("tray04")
                                        || blockName.equalsIgnoreCase("continuewatching01"))) {
                                    imageView.setBackground(Utils.getTrayBorder(mContext, borderColor, component));
                                }
                                int gridImagePadding = Integer.valueOf(component.getLayout().getTv().getPadding());
                                imageView.setPadding(gridImagePadding, gridImagePadding, gridImagePadding, gridImagePadding);
                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                                if (itemWidth > itemHeight) {
                                    Glide.with(mContext)
                                            .load(contentData.getGist().getVideoImageUrl() + "?impolicy=resize&w=" + mWidth + "&h=" + mHeight).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                            .placeholder(R.drawable.video_image_placeholder)
                                            .error(ContextCompat.getDrawable(mContext, R.drawable.video_image_placeholder))
                                            .into(imageView);
                                } else {
                                    Glide.with(mContext)
                                            .load(contentData.getGist().getPosterImageUrl() + "?impolicy=resize&w=" + mWidth + "&h=" + mHeight).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                            .placeholder(R.drawable.poster_image_placeholder)
                                            .error(ContextCompat.getDrawable(mContext, R.drawable.poster_image_placeholder))
                                            .into(imageView);
                                }

                                //Log.d("TAG" , "Url = "+contentData.getGist().getPosterImageUrl()+ "?impolicy=resize&w="+mWidth + "&h=" + mHeight);
                                parentLayout.addView(imageView);
                                break;

                            case PAGE_BEDGE_IMAGE_KEY:
                                if (null != contentData.getGist().getBadgeImages() &&
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
                            StringBuilder stringBuilder = new StringBuilder();
                            if (mAppCmsPresenter.getAppCMSMain().getBrand().getMetadata() != null) {
                                tvTitle.setBackgroundColor(Color.parseColor(component.getBackgroundColor()));
                                tvTitle.setGravity(Gravity.CENTER);
                                Integer padding = Integer.valueOf(component.getLayout().getTv().getPadding());
                                tvTitle.setPadding(6, padding, 10, 4);
                                String time = Utils.convertSecondsToTime(contentData.getGist().getRuntime());

                                Date publishedDate = new Date(contentData.getGist().getPublishDate());
                                SimpleDateFormat spf = new SimpleDateFormat("MMM dd");
                                String date = spf.format(publishedDate);
                                if (mAppCmsPresenter.getAppCMSMain().getBrand().getMetadata().isDisplayDuration()) {
                                    stringBuilder.append(time);
                                }
                                if (mAppCmsPresenter.getAppCMSMain().getBrand().getMetadata().isDisplayPublishedDate()) {
                                    if (stringBuilder.length() > 0) stringBuilder.append(" | ");
                                    stringBuilder.append(date);
                                }
                                tvTitle.setVisibility(View.VISIBLE);
                            } else /*Don't show time and date as metadata is null*/ {
                                tvTitle.setVisibility(View.INVISIBLE);
                            }
                            tvTitle.setText(stringBuilder);
                            tvTitle.setTextSize(component.getFontSize());
                        } else {
                            layoutParams = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    Utils.getViewYAxisAsPerScreen(mContext, Integer.valueOf(component.getLayout().getTv().getHeight())));
                            tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                            tvTitle.setText(contentData.getGist().getTitle());
                        }

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
                        tvTitle.setTextColor(Color.parseColor(component.getTextColor()));
                        if (component.getFontFamily() != null)
                            fontType = getFontType(component);
                        if (fontType != null) {
                            tvTitle.setTypeface(fontType);
                        }
                       // tvTitle.setTextSize(component.getFontSize());
                        parentLayout.addView(tvTitle);
                        break;

                    case PAGE_PROGRESS_VIEW_KEY:
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
                        int progress = (int) Math.ceil(Utils.getPercentage(contentData.getGist().getRuntime(), contentData.getGist().getWatchedTime()));
                        //Log.d("NITS>>>","Runtime = "+  contentData.getGist().getRuntime()
//                           + " WatchedTime = "+ contentData.getGist().getWatchedTime()
//                        +" Percentage = " + contentData.getGist().getWatchedPercentage()
//                        +" Progress = "+progress);
                        progressBar.setProgress(progress);
                        progressBar.setFocusable(false);
                        parentLayout.addView(progressBar);
                        break;
                }
            }
        }
    }


    private Typeface getFontType(Component component) {
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
        } else if (mJsonKeyValuemap.get(component.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_LATO_FONTFAMILY_KEY) {
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

}
