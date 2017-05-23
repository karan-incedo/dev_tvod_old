package air.com.snagfilms.models.network.modules;

import com.google.gson.Gson;

import javax.inject.Singleton;

import air.com.snagfilms.models.network.rest.AppCMSPageAPIRest;
import air.com.snagfilms.models.network.rest.AppCMSPageAPICall;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by viewlift on 5/9/17.
 */

@Module(includes={AppCMSUIModule.class})
public class AppCMSAPIModule {
    private final String apiKey;

    public AppCMSAPIModule(String apiKey) {
        this.apiKey = apiKey;
    }

    @Provides
    @Singleton
    public AppCMSPageAPIRest providesAppCMSPageAPIRest(Retrofit retrofit) {
        return retrofit.create(AppCMSPageAPIRest.class);
    }

    @Provides
    @Singleton
    public AppCMSPageAPICall providesAppCMSPageAPICall(AppCMSPageAPIRest appCMSPageAPI) {
        return new AppCMSPageAPICall(appCMSPageAPI, apiKey);
    }
}
