package air.com.snagfilms.models.data.appcms.binders;

import android.os.Binder;

import air.com.snagfilms.models.data.appcms.page.Page;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSBinder extends Binder {
    private final Page page;

    public AppCMSBinder(Page page) {
        this.page = page;
    }

    public Page getPage() {
        return page;
    }
}
