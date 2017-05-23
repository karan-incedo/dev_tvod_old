package air.com.snagfilms.models.network.modules;

import com.google.gson.Gson;

import javax.inject.Singleton;

import air.com.snagfilms.models.network.rest.AppCMSPageAPI;
import air.com.snagfilms.models.network.rest.AppCMSPageAPICall;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by viewlift on 5/9/17.
 */

@Module
public class AppCMSAPIModule {
    private final String baseUrl;
    private final String apiKey;

    public AppCMSAPIModule(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofit(Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .build();
    }

    @Provides
    @Singleton
    public AppCMSPageAPI providesContentAPI(Retrofit retrofit) {
        return retrofit.create(AppCMSPageAPI.class);
    }

    @Provides
    @Singleton
    public AppCMSPageAPICall providesContentAPICall(AppCMSPageAPI appCMSPageAPI) {
        return new AppCMSPageAPICall(appCMSPageAPI, apiKey);
    }
}
