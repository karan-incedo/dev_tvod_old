package com.viewlift.presenters;

import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 12/15/17.
 */

public class IMAAdsPresenter implements AdErrorEvent.AdErrorListener,
        AdEvent.AdEventListener {
    private Action1<AdErrorEvent> adErrorEventAction1;
    private Action1<AdEvent> adEventAction1;

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Observable.just(adErrorEvent).onErrorResumeNext(throwable -> Observable.empty());
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {

    }
}
