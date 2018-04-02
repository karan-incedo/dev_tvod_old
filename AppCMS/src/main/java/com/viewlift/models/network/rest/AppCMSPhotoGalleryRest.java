package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.viewlift.models.data.appcms.article.AppCMSArticleResult;
import com.viewlift.models.data.appcms.photogallery.AppCMSPhotoGalleryResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface AppCMSPhotoGalleryRest {
    @GET
    Call<AppCMSPhotoGalleryResult> get(@Url String url);
}
