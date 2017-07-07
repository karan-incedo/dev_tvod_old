package com.viewlift.tv.views.presenter;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.tv.model.BrowseFragmentRowData;

import snagfilms.com.air.appcms.R;

/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class JumbotronPresenter extends CardPresenter {

    private Context mContext;
    public JumbotronPresenter(Context context , int height , int width){
        super(context , height , width);
        mContext = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
      Log.d("Presenter" , " CardPresenter onBindViewHolder******");
        BrowseFragmentRowData rowData = (BrowseFragmentRowData)item;
        ContentDatum contentData = rowData.contentData;

 /*
        View cardView = (View) viewHolder.view;
        ImageView imageView = (ImageView) cardView.findViewById(R.id.image);
        TextView textView = (TextView) cardView.findViewById(R.id.title);

        textView.setText(contentData.getGist().getTitle());
        Glide.with(viewHolder.view.getContext())
                .load(contentData.getGist().getPosterImageUrl())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(null)
                .into(imageView);*/



        FrameLayout cardView = (FrameLayout) viewHolder.view;
        ImageView imageView = new ImageView(cardView.getContext());

       // imageView.setBackgroundResource(R.drawable.vp_placeholder_960x480);
        FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT );
        imageView.setLayoutParams(parms);
        imageView.setBackgroundResource(R.drawable.gridview_cell_border);

        int gridImagePadding = (int)mContext.getResources().getDimension(R.dimen.grid_image_padding);
        imageView.setPadding(gridImagePadding,gridImagePadding,gridImagePadding,gridImagePadding);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(viewHolder.view.getContext())
                .load(contentData.getGist().getVideoImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(null)
                .into(imageView);

        cardView.addView(imageView);
    }
}
