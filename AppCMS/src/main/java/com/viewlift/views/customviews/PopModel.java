package com.viewlift.views.customviews;

/**
 * Created by wishy.gupta on 11-10-2017.
 */

public class PopModel {
    int img;
    String title;

    public PopModel(int img, String title) {
        this.img = img;
        this.title = title;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
