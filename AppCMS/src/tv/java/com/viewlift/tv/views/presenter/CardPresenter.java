package com.viewlift.tv.views.presenter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;

import java.util.List;
import java.util.Map;

import com.viewlift.R;

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
    String borderColor = null;
    private Typeface fontType;

    public CardPresenter(Context context , AppCMSPresenter appCMSPresenter ,
                         int height , int width , Map<String ,
                          AppCMSUIKeyType> jsonKeyValuemap){
        mContext = context;
        mAppCmsPresenter = appCMSPresenter;
        mHeight = height;
        mWidth = width;
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
        Log.d("Presenter" , " CardPresenter onCreateViewHolder******");
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
                                FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(

                                        Utils.getViewXAxisAsPerScreen(mContext,Integer.valueOf(component.getLayout().getTv().getWidth())),
                                        Utils.getViewYAxisAsPerScreen(mContext,Integer.valueOf(component.getLayout().getTv().getHeight())));

                                imageView.setLayoutParams(parms);
                                imageView.setBackground(Utils.getTrayBorder(mContext,borderColor,component));

                                int gridImagePadding = Integer.valueOf(component.getLayout().getTv().getPadding());
                                imageView.setPadding(gridImagePadding,gridImagePadding,gridImagePadding,gridImagePadding);

                                Glide.with(mContext)
                                        .load(contentData.getGist().getPosterImageUrl()+ "?impolicy=resize&w="+mWidth + "&h=" + mHeight).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.poster_image_placeholder)
                                        .error(ContextCompat.getDrawable(mContext, R.drawable.poster_image_placeholder))
                                        .into(imageView);

                                Log.d("TAG" , "Url = "+contentData.getGist().getPosterImageUrl()+ "?impolicy=resize&w="+mWidth + "&h=" + mHeight);
                                parentLayout.addView(imageView);
                                break;
                        }
                        break;

                    case PAGE_LABEL_KEY:
                        TextView tvTitle = new TextView(parentLayout.getContext());
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT ,
                                Utils.getViewYAxisAsPerScreen(mContext,Integer.valueOf(component.getLayout().getTv().getHeight())));
                         layoutParams.topMargin =  Utils.getViewYAxisAsPerScreen(mContext,Integer.valueOf(component.getLayout().getTv().getTopMargin()));
                         tvTitle.setLayoutParams(layoutParams);
                        tvTitle.setMaxLines(1);
                        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
                        tvTitle.setTextColor(Color.parseColor(component.getTextColor()));
                        if(fontType == null)
                            fontType = getFontType(component);
                        if(fontType != null){
                            tvTitle.setTypeface(fontType);
                        }
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
