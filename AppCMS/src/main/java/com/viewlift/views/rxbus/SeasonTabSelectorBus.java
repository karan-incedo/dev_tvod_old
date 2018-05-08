package com.viewlift.views.rxbus;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by wishy.gupta on 03-04-2018.
 */

public class SeasonTabSelectorBus {
    private static SeasonTabSelectorBus instance;

    private PublishSubject<Object> subject = PublishSubject.create();

    public static SeasonTabSelectorBus instanceOf() {
        if (instance == null) {
            instance = new SeasonTabSelectorBus();
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
