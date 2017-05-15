package air.com.snagfilms.models.network.modules;

import com.google.gson.Gson;

import javax.inject.Singleton;

import air.com.snagfilms.models.network.rest.ContentAPI;
import air.com.snagfilms.models.network.rest.ContentAPICall;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by viewlift on 5/9/17.
 */

@Module
public class ContentModule {
    private final String baseUrl;

    public ContentModule(String baseUrl) {
        this.baseUrl = baseUrl;
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
    public ContentAPI providesContentAPI(Retrofit retrofit) {
        return retrofit.create(ContentAPI.class);
    }

    @Provides
    @Singleton
    public ContentAPICall providesContentAPICall(ContentAPI contentAPI) {
        return new ContentAPICall(contentAPI);
    }
}
