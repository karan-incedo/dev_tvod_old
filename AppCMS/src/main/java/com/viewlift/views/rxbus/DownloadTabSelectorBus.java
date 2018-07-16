package com.viewlift.views.rxbus;



import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by wishy.gupta on 03-04-2018.
 */

public class DownloadTabSelectorBus {
    private static DownloadTabSelectorBus instance;

    private PublishSubject<Object> subject = PublishSubject.create();

    public static DownloadTabSelectorBus instanceOf() {
        if (instance == null) {
            instance = new DownloadTabSelectorBus();
        }
        return instance;
    }

    public void setTab(Object object) {
        try {
            subject.onNext(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Observable<Object> getSelectedTab() {
        return subject;
    }


}
