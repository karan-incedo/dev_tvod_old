package air.com.snagfilms.views.binders;

import android.os.Binder;

import java.util.Map;

import air.com.snagfilms.models.data.appcms.AppCMSKeyType;
import air.com.snagfilms.models.data.appcms.android.Navigation;
import air.com.snagfilms.models.data.appcms.page.Page;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSBinder extends Binder {
    private final Page page;
    private final Navigation navigation;
    private final boolean loadedFromFile;
    private final String pageId;
    private final boolean appbarPresent;
    private final boolean fullScreen;
    private final Map<AppCMSKeyType, String> jsonValueKeyMap;

    public AppCMSBinder(Page page,
                        Navigation navigation,
                        boolean loadedFromFile,
                        String pageId,
                        boolean appbarPresent,
                        boolean fullScreen,
                        Map<AppCMSKeyType, String> jsonValueKeyMap) {
        this.page = page;
        this.navigation = navigation;
        this.loadedFromFile = loadedFromFile;
        this.pageId = pageId;
        this.appbarPresent = appbarPresent;
        this.fullScreen = fullScreen;
        this.jsonValueKeyMap = jsonValueKeyMap;
    }

    public Page getPage() {
        return page;
    }

    public boolean getLoadedFromFile() {
        return loadedFromFile;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    public Map<AppCMSKeyType, String> getJsonValueKeyMap() {
        return jsonValueKeyMap;
    }

    public String getPageId() {
        return pageId;
    }

    public boolean isLoadedFromFile() {
        return loadedFromFile;
    }

    public boolean isAppbarPresent() {
        return appbarPresent;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }
}
