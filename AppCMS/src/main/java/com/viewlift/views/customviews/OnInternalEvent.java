package com.viewlift.views.customviews;

/**
 * Created by viewlift on 5/29/17.
 */

public interface OnInternalEvent {
    void addReceiver(OnInternalEvent e);
    void sendEvent(InternalEvent<?> event);
    void receiveEvent(InternalEvent<?> event);
    void cancel(boolean cancel);
}
