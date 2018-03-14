package com.viewlift.views.binders;

import android.os.Binder;

import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.models.data.appcms.watchlist.AppCMSAddToWatchlistResult;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by nitin.tyagi on 7/31/2017.
 */

public class RetryCallBinder extends Binder {

    private String pagePath;
    private String action;
    private String filmTitle;
    private boolean closelauncher;
    private String[] extraData;
    private ContentDatum contentDatum;
    private String pageId;
    private Action1<AppCMSAddToWatchlistResult> callback;
    private NavigationPrimary primary;
    private List<NavigationPrimary> items;

    public String getFilmId() {
        return filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    private String filmId;

    public AppCMSPresenter.RETRY_TYPE getRetry_type() {
        return retry_type;
    }

    public void setRetry_type(AppCMSPresenter.RETRY_TYPE retry_type) {
        this.retry_type = retry_type;
    }

    private AppCMSPresenter.RETRY_TYPE retry_type;

    public String getPagePath() {
        return pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFilmTitle() {
        return filmTitle;
    }

    public void setFilmTitle(String filmTitle) {
        this.filmTitle = filmTitle;
    }

    public boolean isCloselauncher() {
        return closelauncher;
    }

    public void setCloselauncher(boolean closelauncher) {
        this.closelauncher = closelauncher;
    }

    public String[] getExtraData() {
        return extraData;
    }

    public void setExtraData(String[] extraData) {
        this.extraData = extraData;
    }

    public ContentDatum getContentDatum() {
        return contentDatum;
    }

    public void setContentDatum(ContentDatum contentDatum) {
        this.contentDatum = contentDatum;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public Action1<AppCMSAddToWatchlistResult> getCallback() {
        return callback;
    }

    public void setCallback(Action1<AppCMSAddToWatchlistResult> action1) {
        this.callback = action1;
    }


    public NavigationPrimary getPrimary() {
        return primary;
    }

    public void setPrimary(NavigationPrimary primary) {
        this.primary = primary;
    }

    public List<NavigationPrimary> getItems() {
        return items;
    }

    public void setItems(List<NavigationPrimary> items) {
        this.items = items;
    }


}
