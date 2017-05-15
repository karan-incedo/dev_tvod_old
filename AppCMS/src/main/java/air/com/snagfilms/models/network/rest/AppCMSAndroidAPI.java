package air.com.snagfilms.models.network.rest;

import air.com.snagfilms.models.data.appcms.android.Android;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by viewlift on 5/4/17.
 */

public interface AppCMSAndroidAPI {
    @GET
    Call<Android> get(@Url String url);
}
