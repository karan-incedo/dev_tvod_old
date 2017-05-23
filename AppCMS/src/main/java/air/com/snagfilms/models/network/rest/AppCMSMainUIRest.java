package air.com.snagfilms.models.network.rest;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by viewlift on 5/4/17.
 */

public interface AppCMSMainUIRest {
    @GET
    Call<JsonElement> get(@Url String url);
}
