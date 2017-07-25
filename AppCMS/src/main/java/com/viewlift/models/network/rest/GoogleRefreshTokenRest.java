package com.viewlift.models.network.rest;

import com.viewlift.models.billing.appcms.authentication.GoogleRefreshTokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/25/17.
 */

public interface GoogleRefreshTokenRest {
    @FormUrlEncoded
    @POST
    Call<GoogleRefreshTokenResponse> refreshToken(@Url String url,
                                                  @Field("grant_type") String grantType,
                                                  @Field("client_id") String clientId,
                                                  @Field("client_secret") String clientSecret,
                                                  @Field("refresh_token") String refreshToken);
}
