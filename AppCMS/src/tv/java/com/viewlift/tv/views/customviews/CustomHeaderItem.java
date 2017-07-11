package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.support.v17.leanback.widget.HeaderItem;

/**
 * Created by nitin.tyagi on 7/2/2017.
 */

public class CustomHeaderItem extends HeaderItem {

    public void setmIsCarousal(boolean mIsCarousal) {
        this.mIsCarousal = mIsCarousal;
    }

    private boolean mIsCarousal;
    private int mListRowLeftMargin;
    private int mListRowRightMargin;

    public int getmListRowHeight() {
        return mListRowHeight;
    }

    public void setmListRowHeight(int mListRowHeight) {
        this.mListRowHeight = mListRowHeight;
    }

    private int mListRowHeight;

    public void setmBackGroundColor(String mBackGroundColor) {
        this.mBackGroundColor = mBackGroundColor;
    }

    private String mBackGroundColor;

    public int getmListRowLeftMargin() {
        return mListRowLeftMargin;
    }

    public void setmListRowLeftMargin(int mListRowLeftMargin) {
        this.mListRowLeftMargin = mListRowLeftMargin;
    }

    public int getmListRowRightMargin() {
        return mListRowRightMargin;
    }

    public void setmListRowRightMargin(int mListRowRightMargin) {
        this.mListRowRightMargin = mListRowRightMargin;
    }

    public String getmBackGroundColor() {
        return mBackGroundColor;
    }


    public boolean ismIsCarousal() {
        return mIsCarousal;
    }

    public CustomHeaderItem(Context context , long id , String name){
        super(id,name);
    }
}
