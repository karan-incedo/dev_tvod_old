package com.viewlift.views.customviews;

/**
 * Created by viewlift on 5/29/17.
 */

public class InternalEvent<T> {
    private final T eventData;

    public InternalEvent(T eventData) {
        this.eventData = eventData;
    }

    public T getEventData() {
        return eventData;
    }
}
