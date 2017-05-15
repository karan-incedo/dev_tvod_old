package air.com.snagfilms;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import air.com.snagfilms.models.data.appcms.android.Android;
import air.com.snagfilms.models.data.appcms.main.Main;
import air.com.snagfilms.models.data.appcms.page.Page;
import air.com.snagfilms.models.network.components.AppCMSAPIComponent;
import air.com.snagfilms.models.network.components.DaggerAppCMSAPIComponent;
import air.com.snagfilms.models.network.modules.AppCMSAPIModule;
import air.com.snagfilms.models.network.rest.AppCMSAndroidCall;
import air.com.snagfilms.models.network.rest.AppCMSMainCall;
import air.com.snagfilms.models.network.rest.AppCMSPageCall;
import air.com.snagfilms.presenters.AppCMSPresenter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by viewlift on 5/8/17.
 */

public class RESTUnitTest {
    private AppCMSAPIComponent appCMSAPIComponent;
    private static final String BASEURL = "https://s3.amazonaws.com";
    private static final String APP_CMS_APP_NAME = "test-site-april-5";
    private static final String APP_CMS_MAIN_URL = "%1$s/appcms-config/%2$s/main.json";
    private static final String APP_CMS_ANDROID_URL = "https://s3.amazonaws.com/appcms-config/test-site-april-5/android.json";
    private static final String APP_CMS_SPLASH_PAGE_URL = "https://s3.amazonaws.com/appcms-config/test-site-april-5/ios/SplashPage.json";
    private static final String APP_CMS_HOME_PAGE_URL = "https://s3.amazonaws.com/appcms-config/test-site-april-5/ios/HomePage.json";

    private Uri mainUri = mock(Uri.class);
    private List<String> mainUriPathList = mock(List.class);
    private Uri androidUri = mock(Uri.class);
    private List<String> androidUriPathList = mock(List.class);
    private Uri splashPageUri = mock(Uri.class);
    private List<String> splashPageUriPathList = mock(List.class);
    private Uri homePageUri = mock(Uri.class);
    private List<String> homePageUriPathList = mock(List.class);

    @Before
    public void initialize() {
        appCMSAPIComponent = DaggerAppCMSAPIComponent
                .builder()
                .appCMSAPIModule(new AppCMSAPIModule(BASEURL, new File("")))
                .build();

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

        when(homePageUri.getPathSegments()).thenReturn(homePageUriPathList);
        when(homePageUri.toString()).thenReturn(APP_CMS_HOME_PAGE_URL);
        when(homePageUriPathList.size()).thenReturn(3);
        when(homePageUriPathList.get(2)).thenReturn("HomePage.json");
    }

    @Test
    public void test_appCMSMainCall() throws Exception {
        AppCMSMainCall appCMSMainCall = appCMSAPIComponent.appCMSMainCall();
        Main main = appCMSMainCall.call(mainUri, false);
        assertNotNull(main);
        assertNotNull(main.getMain());
        assertNotNull(main.getMain().getAndroid());
        assertTrue(!main.getMain().getAndroid().isEmpty());
    }

    @Test
    public void test_appCMSAndroidCall() throws Exception {
        AppCMSAndroidCall appCMSAndroidCall = appCMSAPIComponent.appCMSAndroidCall();
        Android android = appCMSAndroidCall.call(androidUri, false);
        assertNotNull(android);
        assertTrue(android.getMetaPages().size() > 0);
        for (int i = 0; i < android.getMetaPages().size(); i++) {
            assertNotNull(android.getMetaPages().get(i));
            assertNotNull(android.getMetaPages().get(i).getPageName());
            AppCMSPresenter.PageName pageName =
                    AppCMSPresenter.PageName.fromString(android.getMetaPages().get(i).getPageName());
            assertTrue(pageName == AppCMSPresenter.PageName.HOME_PAGE ||
                    pageName == AppCMSPresenter.PageName.SPLASH_PAGE);
        }
    }

    @Test
    public void test_appCMSSplashPageCall() throws Exception {
        AppCMSPageCall appCMSPageCall = appCMSAPIComponent.appCMSPageCall();
        Page splashPage = appCMSPageCall.call(splashPageUri, false);
        assertNotNull(splashPage);
        assertTrue(splashPage.getTitle().equals("Splash Page"));
        assertTrue(splashPage.getType().equals("Welcome Page"));
    }

    @Test
    public void test_appCMSHomePageCall() throws Exception {
        AppCMSPageCall appCMSPageCall = appCMSAPIComponent.appCMSPageCall();
        Page homePage = appCMSPageCall.call(homePageUri, false);
        assertNotNull(homePage);
        assertTrue(homePage.getTitle().equals("Featured Page"));
        assertTrue(homePage.getType().equals("Modular Page"));
    }
}
