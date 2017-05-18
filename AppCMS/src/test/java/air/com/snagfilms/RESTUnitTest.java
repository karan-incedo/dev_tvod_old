package air.com.snagfilms;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.JsonElement;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import air.com.snagfilms.models.data.appcms.android.Android;
import air.com.snagfilms.models.data.appcms.page.Page;
import air.com.snagfilms.models.network.components.AppCMSAPIComponent;
import air.com.snagfilms.models.network.components.DaggerAppCMSAPIComponent;
import air.com.snagfilms.models.network.modules.AppCMSAPIModule;
import air.com.snagfilms.models.network.rest.AppCMSAndroidCall;
import air.com.snagfilms.models.network.rest.AppCMSMainCall;
import air.com.snagfilms.models.network.rest.AppCMSPageCall;
import air.com.snagfilms.presenters.AppCMSPresenter;
import snagfilms.com.air.appcms.R;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by viewlift on 5/8/17.
 */

public class RESTUnitTest {
    private AppCMSAPIComponent appCMSAPIComponent;
    private static final String BASEURL = "https://appcms.viewlift.com";
    private static final String APP_CMS_APP_NAME = "0f11bfb4-bf6c-4702-9883-1d24c4a5ba60";
    private static final String APP_CMS_MAIN_URL = "%1$s/%2$s/main.json";
    private static final String APP_CMS_ANDROID_URL = "https://appcms.viewlift.com/0f11bfb4-bf6c-4702-9883-1d24c4a5ba60/android.json";
    private static final String APP_CMS_SPLASH_PAGE_URL = "https://s3.amazonaws.com/appcms-config/test-site-april-5/ios/SplashPage.json";

    private static final String APP_CMS_MAIN_VERSION_KEY = "version";
    private static final String APP_CMS_MAIN_OLD_VERSION_KEY = "old_version";
    private static final String APP_CMS_MAIN_ANDROID_KEY = "Android";

    private Uri mainUri = mock(Uri.class);
    private List<String> mainUriPathList = mock(List.class);
    private Uri androidUri = mock(Uri.class);
    private List<String> androidUriPathList = mock(List.class);
    private Uri splashPageUri = mock(Uri.class);
    private List<String> splashPageUriPathList = mock(List.class);

    private Context context = mock(Context.class);

    @Before
    public void initialize() {
        when(mainUri.getPathSegments()).thenReturn(mainUriPathList);
        String mainUrl = String.format(APP_CMS_MAIN_URL, BASEURL, APP_CMS_APP_NAME);
        when(mainUri.toString()).thenReturn(mainUrl);
        when(mainUriPathList.size()).thenReturn(3);
        when(mainUriPathList.get(2)).thenReturn("main.json");

        when(androidUri.getPathSegments()).thenReturn(androidUriPathList);
        when(androidUri.toString()).thenReturn(APP_CMS_ANDROID_URL);
        when(androidUriPathList.size()).thenReturn(3);
        when(androidUriPathList.get(2)).thenReturn("android.json");

        when(splashPageUri.getPathSegments()).thenReturn(splashPageUriPathList);
        when(splashPageUri.toString()).thenReturn(APP_CMS_SPLASH_PAGE_URL);
        when(splashPageUriPathList.size()).thenReturn(3);
        when(splashPageUriPathList.get(2)).thenReturn("SplashPage.json");

        when(context.getString(R.string.app_cms_main_version_key))
                .thenReturn(APP_CMS_MAIN_VERSION_KEY);
        when(context.getString(R.string.app_cms_main_old_version_key))
                .thenReturn(APP_CMS_MAIN_OLD_VERSION_KEY);
        when(context.getString(R.string.app_cms_main_android_key))
                .thenReturn(APP_CMS_MAIN_ANDROID_KEY);
        when(context.getPackageName()).thenReturn("myPackage");

        appCMSAPIComponent = DaggerAppCMSAPIComponent
                .builder()
                .appCMSAPIModule(new AppCMSAPIModule(BASEURL, new File(""), context))
                .build();
    }

    @Test
    public void test_appCMSMainCall() throws Exception {
        AppCMSMainCall appCMSMainCall = appCMSAPIComponent.appCMSMainCall();
        JsonElement main = appCMSMainCall.call(mainUri);
        assertNotNull(main);
        assertTrue(!TextUtils.isEmpty(main.getAsJsonObject().get(APP_CMS_MAIN_ANDROID_KEY).getAsString()));
    }

    @Test
    public void test_appCMSAndroidCall() throws Exception {
        AppCMSAndroidCall appCMSAndroidCall = appCMSAPIComponent.appCMSAndroidCall();
        Android android = appCMSAndroidCall.call(androidUri, false);
        assertNotNull(android);
        assertNotNull(android.getMetaPages());
        assertTrue(android.getMetaPages().size() > 0);
        for (int i = 0; i < android.getMetaPages().size(); i++) {
            assertNotNull(android.getMetaPages().get(i));
            assertTrue(!TextUtils.isEmpty(android.getMetaPages().get(i).getPageName()));
        }
    }

    @Test
    public void test_appCMSSplashPageCall() throws Exception {
        AppCMSPageCall appCMSPageCall = appCMSAPIComponent.appCMSPageCall();
        Page splashPage = appCMSPageCall.call(splashPageUri, false);
        assertNotNull(splashPage);
    }
}
