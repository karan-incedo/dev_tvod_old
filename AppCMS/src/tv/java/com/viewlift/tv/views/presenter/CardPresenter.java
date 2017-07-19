package com.viewlift.tv.views.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseCompnentModule;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.views.fragment.AppCmsBrowseFragment;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.viewlift.appcms.R;

/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class CardPresenter extends Presenter {

    private AppCMSPresenter mAppCmsPresenter = null;
    private Context mContext;
    int i = 0;
    int mHeight = -1;
    int mWidth = -1;
    private Map<String , AppCMSUIKeyType> mJsonKeyValuemap;

    public CardPresenter(Context context , AppCMSPresenter appCMSPresenter , int height , int width , Map<String , AppCMSUIKeyType> jsonKeyValuemap){
        mContext = context;
        mAppCmsPresenter = appCMSPresenter;
        mHeight = height;
        mWidth = width;
        mJsonKeyValuemap = jsonKeyValuemap;
    }

    public CardPresenter(Context context, AppCMSPresenter appCMSPresenter) {
        mContext = context;
        mAppCmsPresenter = appCMSPresenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Log.d("Presenter" , " CardPresenter onCreateViewHolder******");
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        FrameLayout.LayoutParams layoutParams;

        if(mHeight != -1 && mWidth != -1) {
            layoutParams = new FrameLayout.LayoutParams(mWidth,
                    mHeight);
        }else{
            layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
        }
        frameLayout.setLayoutParams(layoutParams);
        frameLayout.setFocusable(true);
        return new ViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Log.d("Presenter" , " CardPresenter onBindViewHolder******. viewHolder: " + viewHolder + ", item: " + item);
        BrowseFragmentRowData rowData = (BrowseFragmentRowData)item;
        ContentDatum contentData = rowData.contentData;
        List<Component> componentList = rowData.uiComponentList;
        FrameLayout cardView = (FrameLayout) viewHolder.view;
        createComponent(componentList , cardView , contentData);
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
                    case PAGE_IMAGE_KEY:
                        ImageView imageView = new ImageView(parentLayout.getContext());
                        switch(componentKey){
                            case PAGE_THUMBNAIL_IMAGE_KEY:
                                FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(Integer.valueOf(component.getLayout().getTv().getWidth()),
                                        Integer.valueOf(component.getLayout().getTv().getHeight()));
                                imageView.setLayoutParams(parms);
                                imageView.setBackgroundResource(R.drawable.gridview_cell_border);

                                int gridImagePadding = Integer.valueOf(component.getLayout().getTv().getPadding());
                                imageView.setPadding(gridImagePadding,gridImagePadding,gridImagePadding,gridImagePadding);
                                Picasso.with(mContext)
                                        .load(contentData.getGist().getPosterImageUrl())
                                        .placeholder(R.drawable.poster_image_placeholder)
                                        .into(imageView);

                                parentLayout.addView(imageView);
                                break;
                        }
                        break;

                    case PAGE_LABEL_KEY:
                        TextView tvTitle = new TextView(parentLayout.getContext());
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT ,
                                Integer.valueOf(component.getLayout().getTv().getHeight()));
                         layoutParams.topMargin = Integer.valueOf(component.getLayout().getTv().getTopMargin());
                         tvTitle.setLayoutParams(layoutParams);
                        tvTitle.setMaxLines(1);
                        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                        tvTitle.setTextColor(Color.parseColor(component.getTextColor()));
                        tvTitle.setTypeface(getFontType(component));
                        tvTitle.setText(contentData.getGist().getTitle());
                        //tvTitle.setTextSize(component.getFontSize());
                        parentLayout.addView(tvTitle);
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
