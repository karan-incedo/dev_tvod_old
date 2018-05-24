package com.viewlift.views.rxbus;


import com.viewlift.models.data.appcms.api.ContentDatum;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by wishy.gupta on 03-04-2018.
 */

public class SeasonTabSelectorBus {
    private static SeasonTabSelectorBus instance;

    private PublishSubject<List<ContentDatum>> subject = PublishSubject.create();

    public static SeasonTabSelectorBus instanceOf() {
        if (instance == null) {
            instance = new SeasonTabSelectorBus();
        }
        return instance;
    }

    public void setTab(List<ContentDatum> adapterData) {
        try {
            subject.onNext(adapterData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Observable<List<ContentDatum>> getSelectedTab() {
        return subject;
    }

}
