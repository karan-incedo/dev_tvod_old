package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.viewlift.models.data.appcms.audio.AppCMSAudioDetailResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface AppCMSAudioDetailRest {
    @GET
    Call<AppCMSAudioDetailResult> get(@Url String url, @HeaderMap Map<String, String> headers);

    @GET("/content/audio")
    Observable<AppCMSAudioDetailResult> getPlayList(@Query("site") String site,@Query("id") String id, @HeaderMap Map<String, String> headers);
}
