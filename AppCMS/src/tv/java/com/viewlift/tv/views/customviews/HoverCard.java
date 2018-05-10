package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.views.presenter.CardPresenter;

public class HoverCard extends LinearLayout {

    private Context mContext;
    private AppCMSPresenter mAppCmsPresenter;
    private CardPresenter.CustomFrameLayout mParentLayout;
    private int cardHeight;
    private int cardWidth;
    public HoverCard(Context context ,
                     AppCMSPresenter appCMSPresenter,
                     CardPresenter.CustomFrameLayout parentLayout) {
        super(context);
        mContext = context;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
        mAppCmsPresenter = appCMSPresenter;
        mParentLayout = parentLayout;
        setBackgroundColor(Color.parseColor("#CC" + mAppCmsPresenter.getAppBackgroundColor().replace("#","")));
        getFontFamily();
    }


    public void initViews(){
        TextView tvTitle = new TextView(mContext);
        tvTitle.setId(R.id.videoTitleOnHover);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvTitle.setAlpha(0);
        tvTitle.setPadding(10,0,10,0);
        tvTitle.setLayoutParams(layoutParams);
        if(regularTypeFace != null)
        tvTitle.setTypeface(regularTypeFace);
        mParentLayout.setHoverTitle(tvTitle);

        Component titleComponent = new Component();
        titleComponent.setType(getResources().getString(R.string.app_cms_page_label_key));
        titleComponent.setKey(getResources().getString(R.string.app_cms_page_video_title_on_hover_key));
        titleComponent.setNumberOfLines(1);
         mParentLayout.addChildComponentAndView(tvTitle, titleComponent);
        tvTitle.setTextColor(Color.parseColor(mAppCmsPresenter.getAppTextColor()));
        addView(tvTitle);


        TextView tvSubTitle = new TextView(mContext);
        tvSubTitle.setId(R.id.videoSubTitleOnHover);
        tvSubTitle.setAlpha(0);
        tvSubTitle.setLayoutParams(layoutParams);
        tvSubTitle.setPadding(10,10,10,0);
        if(regularTypeFace != null)
            tvSubTitle.setTypeface(regularTypeFace);
        mParentLayout.setHoverSubTitle(tvSubTitle);

        Component subTitleComponent = new Component();
        subTitleComponent.setType(getResources().getString(R.string.app_cms_page_label_key));
        subTitleComponent.setKey(getResources().getString(R.string.app_cms_page_video_sub_title_on_hover_key));
        subTitleComponent.setNumberOfLines(1);

        mParentLayout.addChildComponentAndView(tvSubTitle, subTitleComponent);
        tvSubTitle.setTextColor(Color.parseColor(mAppCmsPresenter.getAppTextColor()));
        addView(tvSubTitle);


        TextView tvDescription = new TextView(mContext);
        tvDescription.setId(R.id.videoDescriptionOnHover);
        layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvDescription.setAlpha(0);
        tvDescription.setText(getResources().getString(R.string.sample_text));
        tvDescription.setLayoutParams(layoutParams);
        tvDescription.setPadding(10,10,10,0);
        mParentLayout.setHoverDescription(tvDescription);
        if(regularTypeFace != null)
            tvDescription.setTypeface(regularTypeFace);
        Component descriptionComponent = new Component();
        descriptionComponent.setType(getResources().getString(R.string.app_cms_page_label_key));
        descriptionComponent.setKey(getResources().getString(R.string.app_cms_page_video_description_on_hover_key));

        if(cardHeight > cardWidth){
            descriptionComponent.setNumberOfLines(4);
        }else{
            descriptionComponent.setNumberOfLines(3);
        }
        mParentLayout.addChildComponentAndView(tvDescription, descriptionComponent);
        tvDescription.setTextColor(Color.parseColor(mAppCmsPresenter.getAppTextColor()));
        addView(tvDescription);

        if(cardWidth > cardHeight){
            if(cardHeight > 440){
                tvTitle.setTextSize(20);
                tvSubTitle.setTextSize(12);
                tvDescription.setTextSize(14);
            }else{
                tvTitle.setTextSize(16);
                tvSubTitle.setTextSize(10);
                tvDescription.setTextSize(12);
            }
        }else{
            tvTitle.setTextSize(16);
            tvSubTitle.setTextSize(10);
            tvDescription.setTextSize(12);
        }
    }

    public void removeViews(){
        try{
            removeAllViews();
        }catch (Exception e){

        }
    }

    public void setCardHeight(int cardHeight) {
        this.cardHeight = cardHeight;
    }

    public void setCardWidth(int cardWidth) {
        this.cardWidth = cardWidth;
    }

    private Typeface regularTypeFace;
    private void getFontFamily(){
        try {
            if (mAppCmsPresenter.getJsonValueKeyMap().get(mAppCmsPresenter.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY) {
                regularTypeFace = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.opensans_regular_ttf));
            } else if (mAppCmsPresenter.getJsonValueKeyMap().get(mAppCmsPresenter.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_LATO_FONTFAMILY_KEY) {
                regularTypeFace = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.lato_regular));
            }
        }catch (Exception e){

        }
    }
}
