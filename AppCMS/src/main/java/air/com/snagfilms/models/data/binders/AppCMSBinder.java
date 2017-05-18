package air.com.snagfilms.models.data.binders;

import android.os.Binder;

import java.util.Map;

import air.com.snagfilms.models.data.appcms.AppCMSKeyType;
import air.com.snagfilms.models.data.appcms.page.Page;
import air.com.snagfilms.models.network.modules.AppCMSAPIModule;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSBinder extends Binder {
    private final Page page;
    private final boolean loadedFromFile;
    private final String pageName;
    private final Map<AppCMSKeyType, String> jsonValueKeyMap;

    public AppCMSBinder(Page page,
                        boolean loadedFromFile,
                        String pageName,
                        Map<AppCMSKeyType, String> jsonValueKeyMap) {
        this.page = page;
        this.loadedFromFile = loadedFromFile;
        this.pageName = pageName;
        this.jsonValueKeyMap = jsonValueKeyMap;
    }

    public Page getPage() {
        return page;
    }

    public boolean getLoadedFromFile() {
        return loadedFromFile;
    }

    public Map<AppCMSKeyType, String> getJsonValueKeyMap() {
        return jsonValueKeyMap;
    }

    public String getPageName() {
        return pageName;
    }
}
