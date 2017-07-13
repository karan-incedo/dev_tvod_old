package com.viewlift.models.data.rest;

import android.util.Log;

import com.viewlift.models.data.appcms.films.FilmRecordResult;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 7/13/17.
 */

public class AppCMSFilmRecordsCall {
    private static final String TAG = "AppCMSFilmRecordsCall";

    private AppCMSFilmRecordsRest appCMSFilmRecordsRest;

    @Inject
    public AppCMSFilmRecordsCall(AppCMSFilmRecordsRest appCMSFilmRecordsRest) {
        this.appCMSFilmRecordsRest = appCMSFilmRecordsRest;
    }

    public void getFilmsRecords(String url, final Action1<FilmRecordResult> resultReady) {
        Log.d(TAG, "Attempting to retrieve film.json: " + url);
        appCMSFilmRecordsRest.getFilmRecords(url).enqueue(new Callback<FilmRecordResult>() {
            @Override
            public void onResponse(Call<FilmRecordResult> call, Response<FilmRecordResult> response) {
                Log.d(TAG, "Succeeded in retrieving film.json - " + response.toString());
                Observable.just(response.body()).subscribe(resultReady);
            }

            @Override
            public void onFailure(Call<FilmRecordResult> call, Throwable t) {
                Log.e(TAG, "Failed to retrieve film.json - " + t.getMessage());
                Observable.just((FilmRecordResult) null).subscribe(resultReady);
            }
        });
    }
}