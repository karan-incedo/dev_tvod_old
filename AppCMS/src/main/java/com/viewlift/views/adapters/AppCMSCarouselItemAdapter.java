package com.viewlift.views.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
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

public class AppCMSCarouselItemAdapter extends AppCMSViewAdapter implements OnInternalEvent {
    private static String TAG = "CarouselItemAdapter";
    private static long UPDATE_CAROUSEL_TO = 5000L;

    private final RecyclerView listView;
    private final Handler carouselHandler;
    private final Runnable carouselUpdater;
    private final boolean loop;
    private List<OnInternalEvent> internalEventReceivers;
    private int updatedIndex;
    private boolean cancelled;
    private boolean started;
    private boolean scrolled;
    private RecyclerView.OnScrollListener scrollListener;

    public AppCMSCarouselItemAdapter(Context context,
                                     ViewCreator viewCreator,
                                     AppCMSPresenter appCMSPresenter,
                                     Settings settings,
                                     Layout parentLayout,
                                     final Component component,
                                     Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                     Module moduleAPI,
                                     final RecyclerView listView,
                                     boolean loop) {
        super(context,
                viewCreator,
                appCMSPresenter,
                settings,
                parentLayout,
                false,
                component,
                jsonValueKeyMap,
                moduleAPI,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                "");
        this.listView = listView;
        this.loop = loop;
        this.updatedIndex = getDefaultIndex();
        this.internalEventReceivers = new ArrayList<>();
        this.cancelled = false;
        this.started = false;
        this.scrolled = false;

        this.listView.getLayoutManager().scrollToPosition(updatedIndex);

        this.carouselHandler = new Handler();
        this.carouselUpdater = new Runnable() {
            @Override
            public void run() {
                if (adapterData.size() > 1 && !cancelled) {
                    updateCarousel(updatedIndex + 1, false);
                    postUpdateCarousel();
                }
            }
        };

        this.scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    synchronized (listView) {
                        int firstVisibleIndex =
                                ((LinearLayoutManager) AppCMSCarouselItemAdapter.this.listView.getLayoutManager()).findFirstVisibleItemPosition();
                        int lastVisibleIndex =
                                ((LinearLayoutManager) AppCMSCarouselItemAdapter.this.listView.getLayoutManager()).findLastVisibleItemPosition();
                        if (firstVisibleIndex != lastVisibleIndex) {
                            listView.removeOnScrollListener(this);
                            int nextVisibleViewIndex = lastVisibleIndex;
                            if (updatedIndex != firstVisibleIndex) {
                                nextVisibleViewIndex = firstVisibleIndex;
                            }

                            listView.smoothScrollToPosition(nextVisibleViewIndex);
                            sendEvent(new InternalEvent<Object>(nextVisibleViewIndex));
                            setUpdatedIndex(nextVisibleViewIndex);
                        }
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    scrolled = true;
                }
            }
        };

        this.listView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                int[] parentLocation = new int[2];
                int childIndex = -1;
                recyclerView.getLocationOnScreen(parentLocation);
                int eventX = (int) motionEvent.getX() + parentLocation[0];
                int eventY = (int) motionEvent.getY() + parentLocation[1];
                int firstVisibleIndex =
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                int lastVisibleIndex =
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                for (int i = firstVisibleIndex; i <= lastVisibleIndex; i++) {
                    View childView = recyclerView.getLayoutManager().findViewByPosition(firstVisibleIndex);
                    if (childView instanceof CollectionGridItemView) {
                        CollectionGridItemView collectionGridItemView = (CollectionGridItemView) childView;
                        ViewGroup childContainer = collectionGridItemView.getChildrenContainer();
                        int[] collectionGridLocation = new int[2];
                        collectionGridItemView.getLocationOnScreen(collectionGridLocation);
                        int collectionGridItemWidth = collectionGridItemView.getWidth();
                        int collectionGridItemHeight = collectionGridItemView.getHeight();
                        if (collectionGridLocation[0] <= eventX && eventX <= collectionGridLocation[0] + collectionGridItemWidth) {
                            if (collectionGridLocation[1] <= eventY && eventY <= collectionGridLocation[1] + collectionGridItemHeight) {
                                childIndex = i;
                            }
                        }
                        for (int j = 0; j < childContainer.getChildCount(); j++) {
                            View gridItemChildView = childContainer.getChildAt(j);
                            if (gridItemChildView instanceof Button) {
                                int[] childLocation = new int[2];
                                gridItemChildView.getLocationOnScreen(childLocation);
                                int childWidth = gridItemChildView.getWidth();
                                int childHeight = gridItemChildView.getHeight();
                                if (childLocation[0] <= eventX && eventX <= childLocation[0] + childWidth) {
                                    if (childLocation[1] <= eventY && eventY <= childLocation[1] + childHeight) {
                                        if (adapterData.size() != 0) {
                                            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                                onClickHandler.play(collectionGridItemView.matchComponentToView(gridItemChildView),
                                                        adapterData.get(i % adapterData.size()));
                                            }
                                        }
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }

                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    carouselHandler.removeCallbacks(carouselUpdater);
                    listView.setOnScrollListener(scrollListener);
                    return true;
                } else if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
                    if (!scrolled && childIndex != -1 && adapterData.size() != 0) {
                        onClickHandler.click(component,
                                adapterData.get(childIndex % adapterData.size()));
                    } else {
                        listView.removeOnScrollListener(scrollListener);
                        postUpdateCarousel();
                    }
                    scrolled = false;
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
                parentLayout,
                true,
                component,
                appCMSPresenter,
                moduleAPI,
                settings,
                jsonValueKeyMap,
                defaultWidth,
                defaultHeight,
                useMarginsAsPercentages,
                false,
                this.viewType);
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

        if (adapterData.size() != 0) {
            bindView(holder.componentView, adapterData.get(position % adapterData.size()));
        }
    }

    @Override
    public int getItemCount() {
        if (loop) {
            return Integer.MAX_VALUE;
        }
        return adapterData.size();
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
                updateCarousel(calculateUpdateIndex((Integer) event.getEventData()), true);
            }
        }
    }

    @Override
    public void cancel(boolean cancel) {
        cancelled = cancel;
        if (!cancelled && !started) {
            carouselHandler.removeCallbacks(carouselUpdater);
            updatedIndex = getDefaultIndex();
            listView.scrollToPosition(updatedIndex);
            postUpdateCarousel();
            started = true;
        } else if (cancel) {
            carouselHandler.removeCallbacks(carouselUpdater);
            started = false;
        }
    }

    public void postUpdateCarousel() {
        carouselHandler.postDelayed(carouselUpdater, UPDATE_CAROUSEL_TO);
    }

    public void updateCarousel(int index, boolean fromEvent) {
        synchronized (listView) {
            index = calculateUpdateIndex(index);
            setUpdatedIndex(index);
            listView.smoothScrollToPosition(updatedIndex);
            if (!fromEvent) {
                sendEvent(new InternalEvent<Object>(index));
            }
        }
    }

    @Override
    public void resetData(RecyclerView listView) {
        super.resetData(listView);
        updatedIndex = getDefaultIndex();
        sendEvent(new InternalEvent<Object>(updatedIndex));
        listView.scrollToPosition(updatedIndex);
        cancel(false);
    }

    private int getDefaultIndex() {
        if (adapterData.size() != 0) {
            return Integer.MAX_VALUE / 2 - ((Integer.MAX_VALUE / 2) % adapterData.size());
        }
        return 0;
    }

    private void setUpdatedIndex(int index) {
        this.updatedIndex = index;
    }

    private int calculateUpdateIndex(int index) {
        if (adapterData.size() != 0 && Math.abs(updatedIndex - index) > adapterData.size()) {
            int visibleIndexInItems = updatedIndex % adapterData.size();
            return updatedIndex + (index - visibleIndexInItems);
        }
        return index;
    }
}
