package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by sandeep on 19/02/18.
 */

public class ArticleFeedModule extends LinearLayout {


    private Context context;
    private Component component;

    private int defaultWidth;
    private int defaultHeight;
    private List<View> viewsToUpdateOnClickEvent;

    private AppCMSPresenter appCMSPresenter;
    private Map<String, AppCMSUIKeyType> jsonValueKeyMap;

    private LinearLayout rootView;

    private ImageView imageViewPoster;
    private TextView titleText, subTitleText, publisherName,publishDate, summeryText, readMore;
    private LinearLayout bottomControll;
    private RelativeLayout publishInfo;
    LinearLayout.LayoutParams lpBottom, lpBottomRight;
    private View separatorView;
    int textColor = 0;


    @Inject
    public ArticleFeedModule(Context context,
                             Layout parentLayout,
                             Component component,
                             int defaultWidth,
                             int defaultHeight,
                             boolean createMultipleContainersForChildren,
                             boolean createRoundedCorners,
                             AppCMSUIKeyType viewTypeKey,
                             AppCMSPresenter appCMSPresenter,
                             Map<String, AppCMSUIKeyType> jsonValueKeyMap
    ) {
        super(context);
        this.context = context;
        this.component = component;
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.viewsToUpdateOnClickEvent = new ArrayList<>();
        this.appCMSPresenter = appCMSPresenter;
        this.jsonValueKeyMap = jsonValueKeyMap;

        init();
    }

    public ArticleFeedModule(Context context, View view ){
        super(context);
        addView(view);
    }


    public void init() {

        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(LinearLayout.HORIZONTAL);
        setWeightSum(100);

        LinearLayout rootView = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);


        int bgColor = Color.DKGRAY;

        bottomControll = new LinearLayout(context);
        bottomControll.setWeightSum(2);

        publishInfo = new RelativeLayout(context);
        //publishInfo.setWeightSum(2);



        imageViewPoster = new ImageView(context);

        textColor = appCMSPresenter.getGeneralTextColor();

        readMore = new TextView(context);

        titleText = new TextView(context);
        subTitleText = new TextView(context);
        publisherName = new TextView(context);
        publishDate = new TextView(context);
        summeryText = new TextView(context);

        publisherName.setId(R.id.article_publisher);
        publishDate.setId(R.id.article_publish_date);

        separatorView = new View(context);

        lpBottom = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpBottom.weight=1;
        lpBottomRight = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpBottomRight.weight=2;

        if (component != null) {
            bgColor = Color.parseColor(component.getBackgroundColor());
            if (BaseView.isTablet(context)) {
                if (BaseView.isLandscape(context)) {
                    layoutParams.weight = 55;
                } else {
                    layoutParams.weight = 75;
                }
                layoutParams.setMargins(50, 0, 50, 50);

            } else {
                layoutParams.weight = 100;
                layoutParams.setMargins(0, 0, 0, 50);
            }

            layoutParams.width = 0;
            for (Component childComponent : component.getComponents()) {
                AppCMSUIKeyType componentType = jsonValueKeyMap.get(childComponent.getType());

                AppCMSUIKeyType componentKey = jsonValueKeyMap.get(childComponent.getKey());

                int fontSize = (int) BaseView.getFontSize(context,
                        childComponent.getLayout());

                int viewWidth = (int) BaseView.getViewWidth(context,
                        childComponent.getLayout(),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int horizontalMargine = (int) BaseView.getHorizontalMargin(context, childComponent.getLayout());
                LinearLayout.LayoutParams viewLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                viewLp.setMargins(horizontalMargine, 5, horizontalMargine, 5);

                switch (componentType) {
                    case PAGE_LABEL_KEY:
                        switch (componentKey) {
                            case PAGE_ARTICLE_FEED_BOTTOM_TEXT_KEY:
                                publisherName.setPadding(0, horizontalMargine, 0, horizontalMargine);
                                publisherName.setTextSize(fontSize);
                                publisherName.setTextColor(Color.parseColor(childComponent.getTextColor()));
                                setTypeFace(context, jsonValueKeyMap, childComponent, publisherName);
                                publisherName.setMaxLines(1);
                                publisherName.setSingleLine(true);
                                publisherName.setEllipsize(TextUtils.TruncateAt.END);

                                publishDate.setPadding(0, horizontalMargine, horizontalMargine, horizontalMargine);
                                publishDate.setTextSize(fontSize);
                                publishDate.setTextColor(Color.parseColor(childComponent.getTextColor()));
                                setTypeFace(context, jsonValueKeyMap, childComponent, publishDate);
                                publishDate.setMaxLines(1);
                                publishDate.setSingleLine(true);
                                break;
                            case PAGE_THUMBNAIL_TITLE_KEY:
                                titleText.setLayoutParams(viewLp);
                                titleText.setTextSize(fontSize);
                                titleText.setMaxLines(childComponent.getNumberOfLines());
                                titleText.setTextColor(textColor);
                                setTypeFace(context, jsonValueKeyMap, childComponent, titleText);

                                break;
                            case PAGE_THUMBNAIL_DESCRIPTION_KEY:
                                subTitleText.setLayoutParams(viewLp);
                                subTitleText.setTextSize(fontSize);
                                subTitleText.setTextColor(textColor);
                                subTitleText.setMaxLines(childComponent.getNumberOfLines());
                                setTypeFace(context, jsonValueKeyMap, childComponent, subTitleText);
                                break;
                            case PAGE_API_SUMMARY_TEXT_KEY:
                                summeryText.setLayoutParams(viewLp);
                                summeryText.setTextSize(fontSize);
                                summeryText.setTextColor(Color.parseColor(childComponent.getTextColor()));
                                summeryText.setMaxLines(childComponent.getNumberOfLines());
                                setTypeFace(context, jsonValueKeyMap, childComponent, summeryText);
                                break;
                            case PAGE_THUMBNAIL_READ_MORE_KEY:
                                //lpReadMore.setMargins(horizontalMargine, horizontalMargine, horizontalMargine, horizontalMargine);
                                readMore.setPadding(horizontalMargine,horizontalMargine,0,horizontalMargine);
                                readMore.setText(childComponent.getText());
                                readMore.setTextSize(fontSize);
                                readMore.setTextColor(textColor);
                                readMore.setMaxLines(childComponent.getNumberOfLines());
                                setTypeFace(context, jsonValueKeyMap, childComponent, readMore);
                                break;
                        }

                        break;

                    case PAGE_SEPARATOR_VIEW_KEY:

                        LayoutParams lpSeparator = new LayoutParams(viewWidth, 3);
                        lpSeparator.setMargins(horizontalMargine, 5, horizontalMargine, 5);
                        separatorView.setLayoutParams(lpSeparator);
                        separatorView.setBackgroundColor(appCMSPresenter.getBrandPrimaryCtaColor());
                        break;


                }

            }
        }

        publisherName.setGravity(Gravity.RIGHT);
        publishDate.setGravity(Gravity.RIGHT);
        publishInfo.setGravity(Gravity.CENTER_VERTICAL);



        readMore.setGravity(Gravity.LEFT);


        RelativeLayout.LayoutParams lpPublisherDate=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lpPublisher=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lpPublisher.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lpPublisher.addRule(RelativeLayout.LEFT_OF,R.id.article_publish_date);
        lpPublisher.addRule(RelativeLayout.CENTER_VERTICAL);

        lpPublisherDate.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpPublisherDate.addRule(RelativeLayout.CENTER_VERTICAL);

        publishInfo.addView(publisherName,lpPublisher);
        publishInfo.addView(publishDate,lpPublisherDate);



        bottomControll.addView(readMore, lpBottom);
        bottomControll.addView(publishInfo, lpBottom);

        rootView.setLayoutParams(layoutParams);
        rootView.setOrientation(VERTICAL);

        rootView.setBackgroundColor(bgColor);

        rootView.addView(imageViewPoster);
        rootView.addView(titleText);
        rootView.addView(summeryText);
        rootView.addView(separatorView);
        rootView.addView(subTitleText);
        rootView.addView(bottomControll);

        setBackgroundColor(Color.TRANSPARENT);

        setGravity(Gravity.CENTER_HORIZONTAL);
        addView(rootView);
    }


    public void bindChild(Context context, View view, ContentDatum data, Map<String, AppCMSUIKeyType> jsonValueKeyMap, AppCMSPresenter appCMSPresenter, int position) {
        if (component != null) {
            for (Component childComponent : component.getComponents()) {
                AppCMSUIKeyType componentType = jsonValueKeyMap.get(childComponent.getType());

                AppCMSUIKeyType componentKey = jsonValueKeyMap.get(childComponent.getKey());


                switch (componentType) {
                    case PAGE_LABEL_KEY:
                        switch (componentKey) {
                            case PAGE_ARTICLE_FEED_BOTTOM_TEXT_KEY:
                                if (data != null && data.getContentDetails() != null &&
                                        data.getContentDetails().getAuthor() != null
                                        ) {
                                    StringBuffer publishDateVal = new StringBuffer();
                                    if (data.getGist() != null && appCMSPresenter != null &&
                                            data.getGist().getPublishDate() != null) {
                                        long publishDateMillseconds = Long.parseLong(data.getGist().getPublishDate());
                                        publishDateVal.append(" | ");
                                        publishDateVal.append(appCMSPresenter.getDateFormat(publishDateMillseconds, "MMM dd,yyyy"));
                                    }
                                    if ( publishDate.toString().trim().length() > 0) {
                                        publishDate.setText(publishDateVal);

                                    }
                                    if (data.getContentDetails().getAuthor().getName() != null) {
                                        publisherName.setText(data.getContentDetails().getAuthor().getName().toString());

                                    }


                                   /* if (publishDate.getText().toString().length()<=0){
                                        publishDate.setVisibility(GONE);
                                        publishInfo.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        publisherName.setGravity(Gravity.RIGHT);
                                        publishInfo.addView(publisherName);

                                    }else if (publisherName.getText().toString().length()<=0 && publishDateVal.length()>0){
                                        publisherName.setVisibility(GONE);
                                        publishDate.setText(publishDateVal.toString().replace(" | ",""));
                                        publishDate.setGravity(Gravity.RIGHT);
                                        publishInfo.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        publishInfo.addView(publishDate);
                                    }else{*/

                                   // }

                                }
                                break;
                            case PAGE_THUMBNAIL_TITLE_KEY:
                                if (data != null && data.getGist() != null &&
                                        data.getGist().getTitle() != null &&
                                        !TextUtils.isEmpty(data.getGist().getTitle())
                                        ) {
                                    titleText.setText(data.getGist().getTitle());
                                    titleText.setVisibility(VISIBLE);
                                } else {
                                    titleText.setVisibility(GONE);
                                }
                                break;
                            case PAGE_THUMBNAIL_DESCRIPTION_KEY:
                                if (data != null && data.getGist() != null &&
                                        data.getGist().getDescription() != null &&
                                        !TextUtils.isEmpty(data.getGist().getDescription())
                                        ) {
                                    subTitleText.setText(data.getGist().getDescription());
                                    subTitleText.setVisibility(VISIBLE);
                                } else {
                                    subTitleText.setVisibility(GONE);
                                }
                                break;
                            case PAGE_API_SUMMARY_TEXT_KEY:
                                if (data != null && data.getGist() != null &&
                                        data.getGist().getSummaryText() != null &&
                                        !TextUtils.isEmpty(data.getGist().getSummaryText())
                                        ) {
                                    summeryText.setText(data.getGist().getSummaryText());
                                    summeryText.setVisibility(VISIBLE);
                                } else {
                                    summeryText.setVisibility(GONE);
                                }
                                break;
                            case PAGE_THUMBNAIL_READ_MORE_KEY:

                                readMore.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (data != null &&
                                                appCMSPresenter != null &&
                                                data.getGist() != null &&
                                                data.getGist().getId() != null &&
                                                data.getGist().getTitle() != null)
                                            appCMSPresenter.setCurrentArticleIndex(-1);
                                            appCMSPresenter.navigateToArticlePage(data.getGist().getId(), data.getGist().getTitle(), false,null,false);
                                    }
                                });
                                break;

                        }
                        break;
                    case PAGE_IMAGE_KEY:

                        if (imageViewPoster != null &&
                                data != null && data.getGist() != null &&
                                data.getGist().getImageGist() != null &&
                                data.getGist().getImageGist().get_16x9() != null &&
                                !TextUtils.isEmpty(data.getGist().getImageGist().get_16x9())) {
                            if (BaseView.isTablet(context)) {

                                int viewWidth = (int) BaseView.getViewWidth(context,
                                        childComponent.getLayout(),
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                int viewHeight = (int) BaseView.getViewHeight(context,
                                        childComponent.getLayout(),
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                String imageUrl = context.getString(R.string.app_cms_image_with_resize_query,
                                        data.getGist().getImageGist().get_16x9(),
                                        viewWidth,
                                        viewHeight);
                                Glide.with(context)
                                        .load(imageUrl)
                                        .apply(new RequestOptions().override(viewWidth, viewHeight))
                                        .into(imageViewPoster);
                                imageViewPoster.setScaleType(ImageView.ScaleType.FIT_XY);
                            } else {

                                Glide.with(context)
                                        .load(data.getGist().getImageGist().get_16x9())
                                        .into(imageViewPoster);
                            }

                        } else {
                            imageViewPoster.setVisibility(GONE);
                        }

                        break;
                }

            }
        }
    }

    private void setTypeFace(Context context,
                             Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                             Component component,
                             TextView textView) {
        if (jsonValueKeyMap.get(component.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY) {
            AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(component.getFontWeight());
            if (fontWeight == null) {
                fontWeight = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            Typeface face = null;
            switch (fontWeight) {
                case PAGE_TEXT_BOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_bold_ttf));
                    break;
                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_semibold_ttf));
                    break;
                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_extrabold_ttf));
                    break;
                default:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_regular_ttf));
            }
            int fontStyle=0;
            if (component!= null && component.getFontWeight()!=null ) {
                switch (component.getFontWeight()) {
                    case "Italic":
                        fontStyle = Typeface.ITALIC;
                        break;
                    case "Bold":
                    case "Semibold":
                        fontStyle = Typeface.BOLD;
                        break;
                    case "Bold-Italic":
                        fontStyle = Typeface.BOLD_ITALIC;
                    case "Normal":
                        fontStyle = Typeface.NORMAL;
                        break;
                    default:
                        fontStyle = 0;
                        break;
                }
            }


            textView.setTypeface(face,fontStyle);


        }
    }

}
