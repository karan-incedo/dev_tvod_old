package com.viewlift.tv.views.presenter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ListRowView;
import android.support.v17.leanback.widget.RowHeaderView;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.customviews.CustomHeaderItem;

import com.viewlift.R;

/**
 * Created by nitin.tyagi on 7/2/2017.
 */
public class AppCmsListRowPresenter extends ListRowPresenter {

    private Context mContext;
    private AppCMSPresenter mAppCMSPresenter;
    String textColor = null;
    Typeface typeface = null;
    float headerTileLetterSpacing = 0.11f;

    public AppCmsListRowPresenter(Context context , AppCMSPresenter appCMSPresenter){
        super(FocusHighlight.ZOOM_FACTOR_NONE);
        mContext = context;
        setShadowEnabled(false);
        setSelectEffectEnabled(false);
        mAppCMSPresenter  = appCMSPresenter;
        textColor = Utils.getTitleColorForST(mContext,mAppCMSPresenter);
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);

        if(null != holder.getRow()){
            LinearLayout headerTitleContainer =  ((LinearLayout)holder.getHeaderViewHolder().view);
            final RowHeaderView headerTitle = (RowHeaderView)headerTitleContainer.findViewById(R.id.row_header);
            if(null == textColor){
                textColor = Utils.getTitleColorForST(mContext,mAppCMSPresenter); /*mAppCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor();*/
            }
            headerTitle.setTextColor(Color.parseColor(textColor));
            //set Alpha for removing any shadow.
            headerTitleContainer.setAlpha(1);
            //set the letter spacing.
            headerTitle.setLetterSpacing(headerTileLetterSpacing);

            //ListRowView and its layout Params.
            ListRowView listRowView = (ListRowView)holder.view;
            LinearLayout.LayoutParams listRowParam = ( LinearLayout.LayoutParams)listRowView.getLayoutParams();

            //Horizontal GridView and its layout Params.
            HorizontalGridView horizontalGridView = listRowView.getGridView();
            LinearLayout.LayoutParams horizontalGrLayoutParams = ( LinearLayout.LayoutParams)horizontalGridView.getLayoutParams();

            //HeaderTitle layout Params.
            LinearLayout.LayoutParams headerTitleContainerLayoutParams = ( LinearLayout.LayoutParams)headerTitleContainer.getLayoutParams();


            horizontalGridView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    return false;
                }
            });


            ListRow rowItem = (ListRow) item;
            CustomHeaderItem customHeaderItem = ((CustomHeaderItem)rowItem.getHeaderItem());
            int listRowHeight =  Utils.getViewYAxisAsPerScreen(mContext,customHeaderItem.getmListRowHeight());
            int listRowLeftmargin = Utils.getViewXAxisAsPerScreen(mContext,customHeaderItem.getmListRowLeftMargin());
            int listRowRightmargin = Utils.getViewXAxisAsPerScreen(mContext,customHeaderItem.getmListRowRightMargin());


            headerTitle.setTextSize(customHeaderItem.getFontSize());

            if(typeface == null) {
                typeface = Utils.getTypeFace(mContext, mAppCMSPresenter.getJsonValueKeyMap(),
                        customHeaderItem.getComponent());
            }
            if(null != typeface){
                headerTitle.setTypeface(typeface);
            }

            String listRowBackgroundColor = customHeaderItem.getmBackGroundColor();

            boolean isCarousal = customHeaderItem.ismIsCarousal();

            Log.d("AppCmsListRowPresenter" , "isCarousal = "+isCarousal + " Title = "+headerTitle.getText().toString()
            +" isLivePlayer = "+customHeaderItem.ismIsLivePlayer());
            if(customHeaderItem.ismIsLivePlayer()){
                headerTitleContainer.setVisibility(View.GONE);
                headerTitle.setVisibility(View.GONE);

                listRowParam.height = mContext.getResources().getDisplayMetrics().heightPixels + 100;
                listRowParam.width = LinearLayout.LayoutParams.MATCH_PARENT;
                listRowView.setLayoutParams(listRowParam);

                horizontalGrLayoutParams.setMargins(-70, 0 , 0,0);
                horizontalGridView.setLayoutParams(horizontalGrLayoutParams);

                return;
            }
            else if(isCarousal){
                headerTitleContainer.setVisibility(View.GONE);
                headerTitle.setVisibility(View.GONE);
                int horizontalSpacing = (int)mContext.getResources().getDimension(R.dimen.caurosel_grid_item_spacing);

                //set the spacing between Carousal item.
                //horizontalGridView.setItemSpacing(horizontalSpacing);

                //set the HorizontalGrid Layout Params..
                horizontalGrLayoutParams.setMargins(listRowLeftmargin, 0 , listRowRightmargin,0);
                horizontalGridView.setLayoutParams(horizontalGrLayoutParams);

                //set the background color
                if(null != listRowBackgroundColor)
                //listRowView.setBackgroundColor(Color.parseColor(listRowBackgroundColor)/*ContextCompat.getColor(mContext,R.color.jumbotron_background_color)*/);
                    listRowView.setBackgroundColor(ContextCompat.getColor(mContext , android.R.color.transparent));
                //set the ListRow height and width.
                listRowParam.height = listRowHeight/*listRowHeight*/;
                listRowParam.width =  LinearLayout.LayoutParams.MATCH_PARENT;
                listRowView.setLayoutParams(listRowParam);

            }else{
                headerTitleContainer.setVisibility(View.VISIBLE);
                headerTitle.setVisibility(View.VISIBLE);
                int paddingTop = (int)mContext.getResources().getDimension(R.dimen.tray_list_row_padding_top);
                int paddingLeft =(int)mContext.getResources().getDimension(R.dimen.tray_list_row_padding_left);
                int horizontalSpacing = (int)mContext.getResources().getDimension(R.dimen.tray_grid_item_spacing);

                horizontalGrLayoutParams.setMargins(paddingLeft, paddingTop , 0,0);
                horizontalGridView.setLayoutParams(horizontalGrLayoutParams);
                //horizontalGridView.setItemSpacing(horizontalSpacing);

                headerTitleContainerLayoutParams.setMargins(paddingLeft , 0 , 0 , 0);
                headerTitleContainer.setLayoutParams(headerTitleContainerLayoutParams);

                //set the ListRow height and width.
                listRowParam.height = listRowHeight;
                listRowParam.width =  LinearLayout.LayoutParams.MATCH_PARENT;
                listRowView.setBackgroundColor(ContextCompat.getColor(mContext , android.R.color.transparent));
            }
        }
    }

    @Override
    protected void onUnbindRowViewHolder(RowPresenter.ViewHolder holder) {
        super.onUnbindRowViewHolder(holder);
    }
}
