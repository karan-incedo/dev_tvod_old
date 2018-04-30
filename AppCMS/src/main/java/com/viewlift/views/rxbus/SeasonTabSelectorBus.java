package com.viewlift.views.rxbus;

import rx.Observable;
import rx.subjects.PublishSubject;

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

    /**
     * Pass any event down to event listeners.
     */
    public void setTab(Object object) {
        try{
            subject.onNext(object);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to this Observable. On event, do something
     * e.g. replace a fragment
     */
    public Observable<Object> getSelectedTab() {
        return subject;
    }


}
