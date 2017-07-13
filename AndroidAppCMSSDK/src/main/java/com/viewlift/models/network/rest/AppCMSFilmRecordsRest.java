package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.films.FilmRecordResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/13/17.
 */

public interface AppCMSFilmRecordsRest {
    @GET
    Call<FilmRecordResult> getFilmRecords(@Url String url);
}
