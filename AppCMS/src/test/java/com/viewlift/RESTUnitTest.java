package com.viewlift;

import android.content.Context;
import android.text.TextUtils;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidUI;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.network.components.AppCMSAPIComponent;
import com.viewlift.models.network.components.AppCMSUIComponent;
import com.viewlift.models.network.components.DaggerAppCMSUIComponent;
import com.viewlift.models.network.modules.AppCMSUIModule;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;
import com.viewlift.models.network.rest.AppCMSMainUICall;
import com.viewlift.models.network.rest.AppCMSPageAPICall;
import com.viewlift.models.network.rest.AppCMSPageUICall;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by viewlift on 5/8/17.
 */

public class RESTUnitTest {
    private static final String BASEURL = "https://appcms.viewlift.com";
    private static final String API_BASEURL = "https://apisnagfilms-dev.viewlift.com";
    private static final String PAGE_API_PATH = "/content/pages";
    private static final String SITE_ID = "demo5";
    private static final String PAGE_ID = "d6c8599a-07ce-4b51-a201-51a93300f221";
    private static final String APP_CMS_APP_NAME = "49428a08-4d82-402e-9f86-0623d9a2c918";
    private static final String APP_CMS_MAIN_URL = "%1$s/%2$s/main.json";
    private static final String APP_CMS_ANDROID_URL = "https://appcms.viewlift.com/49428a08-4d82-402e-9f86-0623d9a2c918/android.json";
    private static final String APP_CMS_SPLASH_PAGE_URL = "https://appcms.viewlift.com/49428a08-4d82-402e-9f86-0623d9a2c918/android/738aa143-cf46-4c20-bc48-8e98496c5ad0.json";
    private static final String APP_CMS_HOME_PAGE_URL_DATA = "https://apisnagfilms-dev.viewlift.com/content/pages?site=servicename2&pageId=7ca0a3a4-91f4-4e84-b71c-fab50b07966b";
    private static final String APP_CMS_MAIN_VERSION_KEY = "version";
    private static final String APP_CMS_MAIN_OLD_VERSION_KEY = "old_version";
    private static final String APP_CMS_MAIN_ANDROID_KEY = "Android";
    private static final String API_KEY = "XuP7ta1loC80l4J8JBnQp9bS4TYAa60B6Tk0Ct8F";
    private AppCMSUIComponent appCMSUIComponent;
    private AppCMSAPIComponent appCMSAPIComponent;
    private Context context = mock(Context.class);

    //    @SuppressLint("StringFormatMatches")
    @Before
    public void initialize() {
        when(context.getPackageName()).thenReturn("myPackage");
        //when(context.getString(R.string.app_cms_baseurl))
        when(Utils.getProperty("BaseUrl", context))
                .thenReturn("https://appcms.viewlift.com/");
        when(context.getFilesDir())
                .thenReturn(new File(""));
        when(context.getString(R.string.app_cms_main_url,
                //context.getString(R.string.app_cms_baseurl),

                Utils.getProperty("BaseUrl", context),
                APP_CMS_APP_NAME,
                123454321))
                .thenReturn(String.format(APP_CMS_MAIN_URL,
                        BASEURL,
                        APP_CMS_APP_NAME));

        appCMSUIComponent = DaggerAppCMSUIComponent
                .builder()
                .appCMSUIModule(new AppCMSUIModule(context))
                .build();
    }

    @Test
    public void test_appCMSMainCall() throws Exception {
        AppCMSMainUICall appCMSMainUICall = appCMSUIComponent.appCMSMainCall();
        AppCMSMain main = appCMSMainUICall.call(context, APP_CMS_APP_NAME, 0, false, false);
        assertNotNull(main);
        assertTrue(!TextUtils.isEmpty(main.getAndroid()));
    }

    @Test
    public void test_appCMSAndroidCall() throws Exception {
        AppCMSAndroidUICall appCMSAndroidUICall = appCMSUIComponent.appCMSAndroidCall();
        AppCMSAndroidUI appCMSAndroidUI = appCMSAndroidUICall.call(APP_CMS_ANDROID_URL,
                false,
                false,
                0);
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
        AppCMSPageUI splashAppCMSPageUI = appCMSPageUICall.call(APP_CMS_SPLASH_PAGE_URL, false, false);
        assertNotNull(splashAppCMSPageUI);
    }

    @Test
    public void test_appCMSHomePageAPICall() throws Exception {
        AppCMSPageAPICall appCMSPageAPICall = appCMSAPIComponent.appCMSPageAPICall();
        AppCMSPageAPI appCMSPageAPI = appCMSPageAPICall.call(null,
                null,
                null,
                null,
                false,
                0,
                null);
        assertNotNull(appCMSPageAPI);
    }
}
