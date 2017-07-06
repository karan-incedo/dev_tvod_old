package com.viewlift.tv.views.presenter;

import android.content.Context;
import android.graphics.Color;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ListRowView;
import android.support.v17.leanback.widget.RowHeaderView;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.CustomHeaderItem;

import snagfilms.com.air.appcms.R;

/**
 * Created by nitin.tyagi on 7/2/2017.
 */

public class AppCmsListRowPresenter extends ListRowPresenter {

    private Context mContext;

    public AppCmsListRowPresenter(Context context){
        super(FocusHighlight.ZOOM_FACTOR_XSMALL);
        mContext = context;
        setShadowEnabled(false);
        setSelectEffectEnabled(false);
    }



    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);


        if(null != holder.getRow()){

            LinearLayout headerTitleContainer =  ((LinearLayout)holder.getHeaderViewHolder().view);
            final RowHeaderView headerTitle = (RowHeaderView)headerTitleContainer.findViewById(R.id.row_header);
            headerTitle.setTextColor(ContextCompat.getColor(mContext , R.color.colorAccent));

            //ListRowView and its layout Params.
            ListRowView listRowView = (ListRowView)holder.view;
            LinearLayout.LayoutParams listRowParam = ( LinearLayout.LayoutParams)listRowView.getLayoutParams();


            //Horizontal GridView and its layout Params.
            HorizontalGridView horizontalGridView = listRowView.getGridView();
            LinearLayout.LayoutParams horizontalGrLayoutParams = ( LinearLayout.LayoutParams)horizontalGridView.getLayoutParams();


            ListRow rowItem = (ListRow) item;
            CustomHeaderItem customHeaderItem = ((CustomHeaderItem)rowItem.getHeaderItem());
            int listRowLeftmargin = customHeaderItem.getmListRowLeftMargin();
            int listRowRightmargin = customHeaderItem.getmListRowRightMargin();
            int listRowHeight =  customHeaderItem.getmListRowHeight();

            Log.d("AppCmsListRowPresenter" , " Left Margin = " + listRowLeftmargin + " Right margin = "+listRowRightmargin + " Height = "+listRowHeight);
            String listRowBackgroundColor = customHeaderItem.getmBackGroundColor();

            boolean isCarousal = customHeaderItem.ismIsCarousal();

            if(isCarousal){
                headerTitleContainer.setVisibility(View.GONE);
                headerTitle.setVisibility(View.GONE);
                int horizontalSpacing = (int)mContext.getResources().getDimension(R.dimen.caurosel_grid_item_spacing);

                //set the spacing between Carousal item.
                horizontalGridView.setItemSpacing(horizontalSpacing);

                //set the HorizontalGrid Layout Params..
                horizontalGrLayoutParams.setMargins(listRowLeftmargin, 15 , listRowRightmargin,0);
                horizontalGridView.setLayoutParams(horizontalGrLayoutParams);

                //set the background color
                listRowView.setBackgroundColor(Color.parseColor(listRowBackgroundColor)/*ContextCompat.getColor(mContext,R.color.jumbotron_background_color)*/);

                //set the ListRow height and width.
                listRowParam.height = listRowHeight/*listRowHeight*/;
                listRowParam.width =  LinearLayout.LayoutParams.MATCH_PARENT;
                listRowView.setLayoutParams(listRowParam);

            }else{
                headerTitleContainer.setVisibility(View.VISIBLE);

                int paddingTop = (int)mContext.getResources().getDimension(R.dimen.tray_list_row_padding_top);
               // horizontalGrLayoutParams.setMargins(horizontalGridView.getLeft(), paddingTop , horizontalGridView.getRight(),horizontalGridView.getBottom());

                horizontalGrLayoutParams.topMargin = paddingTop;
                horizontalGridView.setLayoutParams(horizontalGrLayoutParams);
                //set the ListRow height and width.
                listRowParam.height = listRowHeight /*listRowHeight*/;
                listRowParam.width =  LinearLayout.LayoutParams.MATCH_PARENT;
               /* listRowParam.setMargins(listRowParam.leftMargin , 0 , listRowParam.rightMargin , listRowParam.bottomMargin);
                listRowView.setLayoutParams(listRowParam);*/
               // listRowView.setBackgroundColor(ContextCompat.getColor(mContext,android.R.color.holo_red_dark));

            }

        }
    }

    @Override
    protected void onUnbindRowViewHolder(RowPresenter.ViewHolder holder) {
        super.onUnbindRowViewHolder(holder);
       // ((ViewGroup)holder.view).removeAllViews();
    }
}
