package air.com.snagfilms.models.network.rest;

import air.com.snagfilms.models.data.appcms.ui.android.AppCMSAndroidUI;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by viewlift on 5/4/17.
 */

public interface AppCMSAndroidUIRest {
    @GET
    Call<AppCMSAndroidUI> get(@Url String url);
}
