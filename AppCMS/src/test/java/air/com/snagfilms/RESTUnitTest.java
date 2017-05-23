package air.com.snagfilms;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.JsonElement;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import air.com.snagfilms.models.data.appcms.api.AppCMSPageAPI;
import air.com.snagfilms.models.data.appcms.ui.android.AppCMSAndroidUI;
import air.com.snagfilms.models.data.appcms.ui.page.AppCMSPageUI;
import air.com.snagfilms.models.network.components.AppCMSAPIComponent;
import air.com.snagfilms.models.network.components.AppCMSUIComponent;
import air.com.snagfilms.models.network.components.DaggerAppCMSAPIComponent;
import air.com.snagfilms.models.network.components.DaggerAppCMSUIComponent;
import air.com.snagfilms.models.network.modules.AppCMSAPIModule;
import air.com.snagfilms.models.network.modules.AppCMSUIModule;
import air.com.snagfilms.models.network.rest.AppCMSAndroidUICall;
import air.com.snagfilms.models.network.rest.AppCMSMainUICall;
import air.com.snagfilms.models.network.rest.AppCMSPageAPICall;
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
    private AppCMSAPIComponent appCMSAPIComponent;

    private static final String BASEURL = "https://appcms.viewlift.com";
    private static final String APP_CMS_APP_NAME = "49428a08-4d82-402e-9f86-0623d9a2c918";
    private static final String APP_CMS_MAIN_URL = "%1$s/%2$s/main.json";
    private static final String APP_CMS_ANDROID_URL = "https://appcms.viewlift.com/49428a08-4d82-402e-9f86-0623d9a2c918/android.json";
    private static final String APP_CMS_SPLASH_PAGE_URL = "https://appcms.viewlift.com/49428a08-4d82-402e-9f86-0623d9a2c918/android/738aa143-cf46-4c20-bc48-8e98496c5ad0.json";

    private static final String APP_CMS_HOME_PAGE_URL_DATA = "https://apisnagfilms-dev.viewlift.com/content/pages?site=servicename2&pageId=7ca0a3a4-91f4-4e84-b71c-fab50b07966b";

    private static final String APP_CMS_MAIN_VERSION_KEY = "version";
    private static final String APP_CMS_MAIN_OLD_VERSION_KEY = "old_version";
    private static final String APP_CMS_MAIN_ANDROID_KEY = "Android";

    private static final String API_KEY = "XuP7ta1loC80l4J8JBnQp9bS4TYAa60B6Tk0Ct8F";

    private Context context = mock(Context.class);

    @Before
    public void initialize() {
        when(context.getString(R.string.app_cms_main_version_key))
                .thenReturn(APP_CMS_MAIN_VERSION_KEY);
        when(context.getString(R.string.app_cms_main_old_version_key))
                .thenReturn(APP_CMS_MAIN_OLD_VERSION_KEY);
        when(context.getString(R.string.app_cms_main_android_key))
                .thenReturn(APP_CMS_MAIN_ANDROID_KEY);
        when(context.getPackageName()).thenReturn("myPackage");
        when(context.getString(R.string.app_cms_api_baseurl))
                .thenReturn("https://appcms.viewlift.com/");
        when(context.getFilesDir())
                .thenReturn(new File(""));
        when(context.getString(R.string.app_cms_main_url,
                context.getString(R.string.app_cms_api_baseurl),
                APP_CMS_APP_NAME))
                .thenReturn(String.format(APP_CMS_MAIN_URL,
                        BASEURL,
                        APP_CMS_APP_NAME));
        when(context.getString(R.string.app_cms_page_api_url,
                APP_CMS_HOME_PAGE_URL_DATA))
                .thenReturn(APP_CMS_HOME_PAGE_URL_DATA + "&includeContent=true");

        appCMSUIComponent = DaggerAppCMSUIComponent
                .builder()
                .appCMSUIModule(new AppCMSUIModule(context))
                .build();

        appCMSAPIComponent = DaggerAppCMSAPIComponent
                .builder()
                .appCMSAPIModule(new AppCMSAPIModule(API_KEY))
                .build();
    }

    @Test
    public void test_appCMSMainCall() throws Exception {
        AppCMSMainUICall appCMSMainUICall = appCMSUIComponent.appCMSMainCall();
        JsonElement main = appCMSMainUICall.call(context, APP_CMS_APP_NAME);
        assertNotNull(main);
        assertTrue(!TextUtils.isEmpty(main.getAsJsonObject().get(APP_CMS_MAIN_ANDROID_KEY).getAsString()));
    }

    @Test
    public void test_appCMSAndroidCall() throws Exception {
        AppCMSAndroidUICall appCMSAndroidUICall = appCMSUIComponent.appCMSAndroidCall();
        AppCMSAndroidUI appCMSAndroidUI = appCMSAndroidUICall.call(APP_CMS_ANDROID_URL, false);
        assertNotNull(appCMSAndroidUI);
        assertNotNull(appCMSAndroidUI.getMetaPages());
        assertTrue(appCMSAndroidUI.getMetaPages().size() > 0);
        for (int i = 0; i < appCMSAndroidUI.getMetaPages().size(); i++) {
            assertNotNull(appCMSAndroidUI.getMetaPages().get(i));
            assertTrue(!TextUtils.isEmpty(appCMSAndroidUI.getMetaPages().get(i).getPageName()));
        }
    }

    @Test
    public void test_appCMSSplashPageCall() throws Exception {
        AppCMSPageUICall appCMSPageUICall = appCMSUIComponent.appCMSPageCall();
        AppCMSPageUI splashAppCMSPageUI = appCMSPageUICall.call(APP_CMS_SPLASH_PAGE_URL, false);
        assertNotNull(splashAppCMSPageUI);
    }

    @Test
    public void test_appCMSHomePageAPICall() throws Exception {
        AppCMSPageAPICall appCMSPageAPICall = appCMSAPIComponent.appCMSPageAPICall();
        AppCMSPageAPI appCMSPageAPI = appCMSPageAPICall.call(context, APP_CMS_HOME_PAGE_URL_DATA);
        assertNotNull(appCMSPageAPI);
    }
}
