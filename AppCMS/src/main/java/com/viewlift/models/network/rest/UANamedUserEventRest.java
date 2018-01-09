package com.viewlift.models.network.rest;

import com.viewlift.models.data.urbanairship.UAAssociateNamedUserRequest;
import com.viewlift.models.data.urbanairship.UAAssociateNamedUserResponse;
import com.viewlift.models.data.urbanairship.UANamedUserRequest;
import com.viewlift.models.data.urbanairship.UANamedUserResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * Created by viewlift on 12/21/17.
 */

public interface UANamedUserEventRest {
    @POST("https://go.urbanairship.com/api/named_users/tags")
    Call<UANamedUserResponse> post(@HeaderMap Map<String, String> headers,
                                   @Body UANamedUserRequest requestBody);
    @POST("https://go.urbanairship.com/api/named_users/associate")
    Call<UAAssociateNamedUserResponse> associateLogin(@HeaderMap Map<String, String> headers,
                                                      @Body UAAssociateNamedUserRequest requestBody);
    @POST("https://go.urbanairship.com/api/named_users/disassociate")
    Call<UAAssociateNamedUserResponse> disassociateLogout(@HeaderMap Map<String, String> headers,
                                                      @Body UAAssociateNamedUserRequest requestBody);

}
