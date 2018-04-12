package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.support.v17.leanback.widget.HeaderItem;

import com.viewlift.models.data.appcms.ui.page.Component;

/**
 * Created by nitin.tyagi on 7/2/2017.
 */

public class CustomHeaderItem extends HeaderItem {

    private int fontSize;
    private String fontWeight;
    private String fontFamily;

    private boolean mIsCarousal;

    private String itemSpacing;
    public boolean ismIsLivePlayer() {
        return mIsLivePlayer;
    }

    public void setmIsLivePlayer(boolean mIsLivePlayer) {
        this.mIsLivePlayer = mIsLivePlayer;
    }

    private boolean mIsLivePlayer;
    private int mListRowLeftMargin;
    private int mListRowRightMargin;

    private int mListRowHeight;

    public int getmListRowWidth() {
        return mListRowWidth;
    }

    public void setmListRowWidth(int mListRowWidth) {
        this.mListRowWidth = mListRowWidth;
    }

    private int mListRowWidth;
    private String mBackGroundColor;


    public String getmModuleId() {
        return mModuleId;
    }

    public void setmModuleId(String mModuleId) {
        this.mModuleId = mModuleId;
    }

    private String mModuleId;



    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }


    public int getFontSize() {
        return fontSize;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setmIsCarousal(boolean mIsCarousal) {
        this.mIsCarousal = mIsCarousal;
    }

    public int getmListRowHeight() {
        return mListRowHeight;
    }

    public void setmListRowHeight(int mListRowHeight) {
        this.mListRowHeight = mListRowHeight;
    }

    public void setmBackGroundColor(String mBackGroundColor) {
        this.mBackGroundColor = mBackGroundColor;
    }

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

    public String getItemSpacing() {
        return itemSpacing;
    }

    public void setItemSpacing(String itemSpacing) {
        this.itemSpacing = itemSpacing;
    }

    public CustomHeaderItem(Context context , long id , String name){
        super(id,name);
    }


    public Component getComponent(){
        Component component = new Component();
        component.setFontFamily(getFontFamily());
        component.setFontWeight(getFontWeight());
        component.setFontSize(getFontSize());
        return component;
    }
}
