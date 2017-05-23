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
import air.com.snagfilms.models.network.components.AppCMSUIComponent;
import air.com.snagfilms.models.network.components.DaggerAppCMSAPIComponent;
import air.com.snagfilms.models.network.components.DaggerAppCMSUIComponent;
import air.com.snagfilms.models.network.modules.AppCMSUIModule;
import air.com.snagfilms.models.network.rest.AppCMSAndroidUICall;
import air.com.snagfilms.models.network.rest.AppCMSMainUICall;
import air.com.snagfilms.models.network.rest.AppCMSPageUICall;
import snagfilms.com.air.appcms.R;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by viewlift on 5/8/17.
 */

public class RESTUnitTest {
    private AppCMSUIComponent appCMSUIComponent;
    private static final String BASEURL = "https://appcms.viewlift.com";
    private static final String APP_CMS_APP_NAME = "49428a08-4d82-402e-9f86-0623d9a2c918";
    private static final String APP_CMS_MAIN_URL = "%1$s/%2$s/main.json";
    private static final String APP_CMS_ANDROID_URL = "https://appcms.viewlift.com/49428a08-4d82-402e-9f86-0623d9a2c918/android.json";
    private static final String APP_CMS_SPLASH_PAGE_URL = "https://appcms.viewlift.com/49428a08-4d82-402e-9f86-0623d9a2c918/android/525e8af5-3158-4663-89c2-fd73a4a91ec6.json";

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
        when(androidUriPathList.size()).thenReturn(2);
        when(androidUriPathList.get(1)).thenReturn("android.json");

        when(splashPageUri.getPathSegments()).thenReturn(splashPageUriPathList);
        when(splashPageUri.toString()).thenReturn(APP_CMS_SPLASH_PAGE_URL);
        when(splashPageUriPathList.size()).thenReturn(1);
        when(splashPageUriPathList.get(0)).thenReturn("SplashPage.json");

        when(context.getString(R.string.app_cms_main_version_key))
                .thenReturn(APP_CMS_MAIN_VERSION_KEY);
        when(context.getString(R.string.app_cms_main_old_version_key))
                .thenReturn(APP_CMS_MAIN_OLD_VERSION_KEY);
        when(context.getString(R.string.app_cms_main_android_key))
                .thenReturn(APP_CMS_MAIN_ANDROID_KEY);
        when(context.getPackageName()).thenReturn("myPackage");

        appCMSUIComponent = DaggerAppCMSUIComponent
                .builder()
                .appCMSUIModule(new AppCMSUIModule(BASEURL, new File(""), context))
                .build();
    }

    @Test
    public void test_appCMSMainCall() throws Exception {
        AppCMSMainUICall appCMSMainUICall = appCMSUIComponent.appCMSMainCall();
        JsonElement main = appCMSMainUICall.call(mainUri);
        assertNotNull(main);
        assertTrue(!TextUtils.isEmpty(main.getAsJsonObject().get(APP_CMS_MAIN_ANDROID_KEY).getAsString()));
    }

    @Test
    public void test_appCMSAndroidCall() throws Exception {
        AppCMSAndroidUICall appCMSAndroidUICall = appCMSUIComponent.appCMSAndroidCall();
        Android android = appCMSAndroidUICall.call(androidUri, false);
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
        AppCMSPageUICall appCMSPageUICall = appCMSUIComponent.appCMSPageCall();
        Page splashPage = appCMSPageUICall.call(splashPageUri, false);
        assertNotNull(splashPage);
    }
}
