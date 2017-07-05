package com.viewlift.tv.views.presenter;

import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
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
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.tv.model.BrowseCompnentModule;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.views.fragment.AppCmsBrowseFragment;

import snagfilms.com.air.appcms.R;

/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class CardPresenter extends Presenter {

    int CARD_WIDTH = 170;
    int CARD_HEIGHT = 231;

    int JUMBO_HEIGHT = 367;
    int JUMBO_WIDTH = 555;

    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;
    private Context mContext;
    int i = 0;

    public CardPresenter(Context context , int height , int width){
        mContext = context;
        CARD_WIDTH = width;
        CARD_HEIGHT = height;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        sDefaultBackgroundColor =  ContextCompat.getColor(mContext , android.R.color.transparent);
        sSelectedBackgroundColor = ContextCompat.getColor(mContext , R.color.appcms_nav_background);
        Log.d("Presenter" , " CardPresenter onCreateViewHolder******");

        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        FrameLayout.LayoutParams layoutParams;

        layoutParams = new FrameLayout.LayoutParams(CARD_WIDTH ,
                CARD_HEIGHT);

        frameLayout.setLayoutParams(layoutParams);
        frameLayout.setFocusable(true);

        return new ViewHolder(frameLayout);

    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        Log.d("Presenter" , " CardPresenter onBindViewHolder******. viewHolder: " + viewHolder + ", item: " + item);
        BrowseFragmentRowData rowData = (BrowseFragmentRowData)item;
        ContentDatum contentData = rowData.contentData;

        Log.d("" , "NITS onBindViewHolder Items ===== "+contentData.getGist().getTitle());

        FrameLayout cardView = (FrameLayout) viewHolder.view;

        LinearLayout parentLayout  = new LinearLayout(cardView.getContext());
        parentLayout.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT );
        parentLayout.setLayoutParams(parms);



        ImageView imageView = new ImageView(cardView.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                CARD_HEIGHT - 30 ); //TODO : Need to change it with server.
        imageView.setLayoutParams(layoutParams);
        imageView.setBackgroundResource(R.drawable.gridview_cell_border);

        int gridImagePadding = (int)mContext.getResources().getDimension(R.dimen.grid_image_padding);
        imageView.setPadding(gridImagePadding,gridImagePadding,gridImagePadding,gridImagePadding);


        TextView tvTitle = new TextView(cardView.getContext());
        tvTitle.setGravity(Gravity.BOTTOM);
        tvTitle.setLines(1);
        tvTitle.setTextColor(ContextCompat.getColor(mContext,android.R.color.white));
        tvTitle.setText(contentData.getGist().getTitle());

        Glide.with(viewHolder.view.getContext())
                .load(contentData.getGist().getPosterImageUrl())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(null)
                .into(imageView);



        parentLayout.addView(imageView);
        parentLayout.addView(tvTitle);

        cardView.addView(parentLayout);
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


    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }


}
