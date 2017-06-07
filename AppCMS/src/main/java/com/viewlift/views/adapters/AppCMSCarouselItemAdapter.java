package com.viewlift.views.adapters;

import android.graphics.Rect;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.CollectionGridItemView;
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

    private static String TAG = "CarouselItemAdapter";
    private static long UPDATE_CAROUSEL_TO = 5000L;

    private final RecyclerView listView;
    private final Handler carouselHandler;
    private final Runnable carouselUpdater;
    private final boolean loop;
    private List<OnInternalEvent> internalEventReceivers;
    private int updatedIndex;
    private boolean cancelled;
    private RecyclerView.OnScrollListener scrollListener;

    public AppCMSCarouselItemAdapter(ViewCreator viewCreator,
                                     AppCMSPresenter appCMSPresenter,
                                     Settings settings,
                                     Component component,
                                     Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                     Module moduleAPI,
                                     final RecyclerView listView,
                                     boolean loop) {
        super(viewCreator,
                appCMSPresenter,
                settings,
                component,
                jsonValueKeyMap,
                moduleAPI,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.listView = listView;
        this.loop = loop;
        this.updatedIndex = 0;
        this.internalEventReceivers = new ArrayList<>();
        this.cancelled = false;

        this.carouselHandler = new Handler();
        this.carouselUpdater = new Runnable() {
            @Override
            public void run() {
                if (adapterData.size() > 1) {
                    Log.d(TAG, "Updating carousel to index: " + (updatedIndex + 1));
                    updateCarousel(updatedIndex + 1, false);
                    postUpdateCarousel();
                }
            }
        };

        this.scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                synchronized(listView) {
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

                        listView.smoothScrollToPosition(nextVisibleViewIndex);
                        sendEvent(new InternalEvent<Object>(nextVisibleViewIndex));
                        updateIndex(nextVisibleViewIndex);
                    }
                }
            }
        };

        this.listView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    carouselHandler.removeCallbacks(carouselUpdater);
                    listView.setOnScrollListener(scrollListener);
                    return true;
                } else if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
                    listView.removeOnScrollListener(scrollListener);
                    postUpdateCarousel();
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });

        this.useMarginsAsPercentages = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CollectionGridItemView view = viewCreator.createCollectionGridItemView(parent.getContext(),
                component,
                appCMSPresenter,
                moduleAPI,
                settings,
                jsonValueKeyMap,
                defaultWidth,
                defaultHeight,
                useMarginsAsPercentages);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (loop) {
            for (int i = 0; i < holder.componentView.getNumberOfChildren(); i++) {
                Component childComponent =
                        holder.componentView.matchComponentToView(holder.componentView.getChild(i));
                if (childComponent != null) {
                    AppCMSUIKeyType componentType = jsonValueKeyMap.get(childComponent.getType());
                    if (componentType == null) {
                        componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                    }
                    if (componentType == AppCMSUIKeyType.PAGE_LABEL_KEY) {
                        ((TextView) holder.componentView.getChild(i)).setText("");
                    } else if (componentType == AppCMSUIKeyType.PAGE_IMAGE_KEY) {
                        ((ImageView) holder.componentView.getChild(i)).setImageResource(android.R.color.transparent);
                    }
                }
            }
        }
        bindView(holder.componentView, adapterData.get(position % adapterData.size()));
    }

    @Override
    public int getItemCount() {
        if (loop) {
            return Integer.MAX_VALUE;
        }
        return adapterData.size();
    }

    public void updateIndex(int index) {
        if (index != updatedIndex) {
            updatedIndex = index;
            Log.d(TAG, "Updated visible index: " + updatedIndex);
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
        if (!cancelled) {
            if (event.getEventData() instanceof Integer) {
                int updatedIndexInItems = (Integer) event.getEventData();
                int visibleIndexInItems = updatedIndex % adapterData.size();
                updateCarousel(updatedIndex + (updatedIndexInItems - visibleIndexInItems), true);
            }
        }
    }

    @Override
    public void cancel(boolean cancel) {
        cancelled = cancel;
        if (!cancelled) {
            Log.d(TAG, "Starting carousel updater");
            carouselHandler.removeCallbacks(carouselUpdater);
            sendEvent(new InternalEvent<Object>(updatedIndex));
            postUpdateCarousel();
        } else {
            Log.d(TAG, "Stopping carousel updater");
            carouselHandler.removeCallbacks(carouselUpdater);
        }
    }

    public void postUpdateCarousel() {
        carouselHandler.postDelayed(carouselUpdater, UPDATE_CAROUSEL_TO);
    }

    public void updateCarousel(int index, boolean fromEvent) {
        synchronized(listView) {
            listView.smoothScrollToPosition(index);
            if (!fromEvent) {
                sendEvent(new InternalEvent<Object>(index));
            }
            updateIndex(index);
        }
    }
}
