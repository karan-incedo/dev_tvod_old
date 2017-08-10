package com.viewlift.tv.views.presenter;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;

import java.util.List;

import com.viewlift.R;

/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class JumbotronPresenter extends CardPresenter {

    private Context mContext;
    private AppCMSPresenter mAppCMSPresenter;


    public JumbotronPresenter(Context context , AppCMSPresenter appCMSPresenter){
        super(context , appCMSPresenter);
        mContext = context;
        mAppCMSPresenter = appCMSPresenter;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
            Log.d("Presenter" , " CardPresenter onCreateViewHolder******");

            FrameLayout frameLayout = new FrameLayout(parent.getContext());
            FrameLayout.LayoutParams layoutParams;

            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT ,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            frameLayout.setLayoutParams(layoutParams);
            frameLayout.setFocusable(true);
            return new ViewHolder(frameLayout);
        }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
      Log.d("Presenter" , " CardPresenter onBindViewHolder******");
        BrowseFragmentRowData rowData = (BrowseFragmentRowData)item;
        ContentDatum contentData = rowData.contentData;
        List<Component> componentList = rowData.uiComponentList;
        FrameLayout cardView = (FrameLayout) viewHolder.view;
        createComponent(componentList , cardView , contentData);
    }


    public void createComponent(List<Component> componentList , ViewGroup parentLayout ,ContentDatum contentData ){
        if(null != componentList && componentList.size() > 0) {
            for (Component component : componentList) {
                AppCMSUIKeyType componentType = mAppCMSPresenter.getJsonValueKeyMap().get(component.getType());
                if (componentType == null) {
                    componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                }

                AppCMSUIKeyType componentKey = mAppCMSPresenter.getJsonValueKeyMap().get(component.getKey());
                if (componentKey == null) {
                    componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                }

                switch (componentType) {
                    case PAGE_IMAGE_KEY:
                        ImageView imageView = new ImageView(parentLayout.getContext());
                        switch(componentKey){
                            case PAGE_CAROUSEL_IMAGE_KEY:
                                FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(Integer.valueOf(component.getLayout().getTv().getWidth()),
                                        Integer.valueOf(component.getLayout().getTv().getHeight()));
                                imageView.setLayoutParams(parms);
                                imageView.setBackgroundResource(R.drawable.gridview_cell_border);

                                int gridImagePadding = Integer.valueOf(component.getLayout().getTv().getPadding());
                                imageView.setPadding(gridImagePadding,gridImagePadding,gridImagePadding,gridImagePadding);
                                Glide.with(mContext)
                                        .load(contentData.getGist().getVideoImageUrl())
                                        .into(imageView);

                                parentLayout.addView(imageView);
                                break;
                        }
                }
            }
        }
    }
}
