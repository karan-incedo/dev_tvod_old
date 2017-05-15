package air.com.snagfilms.models.data.binders;

import android.os.Binder;

import air.com.snagfilms.models.data.appcms.page.Page;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSBinder extends Binder {
    private final Page page;
    private final boolean loadedFromFile;

    public AppCMSBinder(Page page, boolean loadedFromFile) {
        this.page = page;
        this.loadedFromFile = loadedFromFile;
    }

    public Page getPage() {
        return page;
    }

    public boolean getLoadedFromFile() {
        return loadedFromFile;
    }
}
