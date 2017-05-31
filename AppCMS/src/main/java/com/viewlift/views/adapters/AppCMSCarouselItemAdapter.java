package com.viewlift.views.adapters;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.InternalEvent;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.customviews.ViewCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by viewlift on 5/25/17.
 */

public class AppCMSCarouselItemAdapter extends AppCMSViewAdapter
    implements OnInternalEvent {

    private static long UPDATE_CAROUSEL_TO = 5000L;

    private final RecyclerView listView;
    private final Handler carouselHandler;
    private final Runnable carouselUpdater;
    private final boolean loop;
    private List<OnInternalEvent> internalEventReceivers;
    private int updatedVisibleIndex;
    private boolean updatingCarousel;

    public AppCMSCarouselItemAdapter(Context context,
                                     ViewCreator viewCreator,
                                     AppCMSPresenter appCMSPresenter,
                                     Settings settings,
                                     Component component,
                                     Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                                     Module moduleAPI,
                                     final RecyclerView listView,
                                     boolean loop) {
        super(context,
                viewCreator,
                appCMSPresenter,
                settings,
                component,
                jsonValueKeyMap,
                moduleAPI);
        this.listView = listView;
        this.loop = loop;
        this.updatedVisibleIndex = 0;
        this.internalEventReceivers = new ArrayList<>();
        this.updatingCarousel = false;

        this.carouselHandler = new Handler();
        this.carouselUpdater = new Runnable() {
            @Override
            public void run() {
                if (!updatingCarousel) {
                    updateCarousel(updatedVisibleIndex + 1);
                    sendEvent(new InternalEvent<Object>(updatedVisibleIndex));
                }
                postUpdateCarousel();
            }
        };

        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updatingCarousel = true;
                int firstVisibleIndex =
                        ((LinearLayoutManager) AppCMSCarouselItemAdapter.this.listView.getLayoutManager()).findFirstVisibleItemPosition();
                int lastVisibleIndex =
                        ((LinearLayoutManager) AppCMSCarouselItemAdapter.this.listView.getLayoutManager()).findLastVisibleItemPosition();
                if (firstVisibleIndex != lastVisibleIndex) {
                    View firstVisibleView = AppCMSCarouselItemAdapter.this.listView.getLayoutManager().findViewByPosition(firstVisibleIndex);
                    Rect firstVisibleBounds = new Rect();
                    firstVisibleView.getLocalVisibleRect(firstVisibleBounds);
                    int firstViewVisibleWidth = firstVisibleBounds.right - firstVisibleBounds.left;

                    View lastVisibleView = AppCMSCarouselItemAdapter.this.listView.getLayoutManager().findViewByPosition(lastVisibleIndex);
                    Rect lastVisibleBounds = new Rect();
                    lastVisibleView.getLocalVisibleRect(lastVisibleBounds);
                    int lastVisibleWidth = lastVisibleBounds.right - lastVisibleBounds.left;

                    int nextVisibleViewIndex = firstViewVisibleWidth > lastVisibleWidth ? firstVisibleIndex : lastVisibleIndex;
                    AppCMSCarouselItemAdapter.this.listView.smoothScrollToPosition(nextVisibleViewIndex);
                    updateVisibleIndex(nextVisibleViewIndex);
                    sendEvent(new InternalEvent<Object>(nextVisibleViewIndex));
                }
                updatingCarousel = false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (loop) {
            return Integer.MAX_VALUE;
        }
        return adapterData.size();
    }

    public void updateVisibleIndex(int index) {
        if (index != updatedVisibleIndex) {
            updatedVisibleIndex = index;
        }
    }

    @Override
    public void addReceiver(OnInternalEvent e) {
        internalEventReceivers.add(e);
    }

    @Override
    public void sendEvent(InternalEvent<?> event) {
        for (OnInternalEvent receiver : internalEventReceivers) {
            receiver.receiveEvent(event);
        }
    }

    @Override
    public void receiveEvent(InternalEvent<?> event) {
        if (event.getEventData() instanceof Integer) {
            int updatedIndexInItems = (Integer) event.getEventData();
            int visibleIndexInItems = updatedVisibleIndex % adapterData.size();
            updateCarousel(updatedVisibleIndex + (updatedIndexInItems - visibleIndexInItems));
        }
    }

    public void postUpdateCarousel() {
        carouselHandler.postDelayed(carouselUpdater, UPDATE_CAROUSEL_TO);
    }

    public void updateCarousel(int index) {
        AppCMSCarouselItemAdapter.this.listView.smoothScrollToPosition(index);
        updateVisibleIndex(index);
    }
}
