package air.com.snagfilms.models.network.components;

import javax.inject.Singleton;

import air.com.snagfilms.models.network.modules.ContentModule;
import air.com.snagfilms.models.network.rest.ContentAPICall;
import dagger.Component;

/**
 * Created by viewlift on 5/9/17.
 */

@Singleton
@Component(modules={ContentModule.class})
public interface ContentAPIComponent {
    ContentAPICall contentAPICall();
}
