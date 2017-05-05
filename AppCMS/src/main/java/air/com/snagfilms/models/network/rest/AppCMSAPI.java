package air.com.snagfilms.models.network.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by viewlift on 5/4/17.
 */

public interface AppCMSAPI<R> {
    @GET
    Call<R> get(@Url String url);
}
