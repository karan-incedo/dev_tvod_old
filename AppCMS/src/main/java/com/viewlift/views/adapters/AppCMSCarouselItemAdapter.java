package com.viewlift.views.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;
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
    private volatile Integer updatedIndex;
    private volatile boolean cancelled;
    private volatile boolean started;
    private boolean scrolled;
    private RecyclerView.OnScrollListener scrollListener;

    private String moduleId;

    public AppCMSCarouselItemAdapter(Context context,
                                     ViewCreator viewCreator,
                                     AppCMSPresenter appCMSPresenter,
                                     Settings settings,
                                     Layout parentLayout,
                                     final Component component,
                                     Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                     Module moduleAPI,
                                     final RecyclerView listView,
                                     boolean loop,
                                     AppCMSAndroidModules appCMSAndroidModules) {
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
                "",
                appCMSAndroidModules);

        this.listView = listView;
        this.loop = loop;

        this.updatedIndex = getDefaultIndex();

        this.internalEventReceivers = new ArrayList<>();
        this.cancelled = false;
        this.started = false;
        this.scrolled = false;

        this.listView.getLayoutManager().scrollToPosition(updatedIndex);

        this.carouselHandler = new Handler();
        this.carouselUpdater = () -> {
            if (adapterData.size() > 1 && !cancelled && (loop || (!loop && updatedIndex < adapterData.size()))) {
                int firstVisibleIndex =
                        ((LinearLayoutManager) AppCMSCarouselItemAdapter.this.listView.getLayoutManager()).findFirstVisibleItemPosition();
                int lastVisibleIndex =
                        ((LinearLayoutManager) AppCMSCarouselItemAdapter.this.listView.getLayoutManager()).findLastVisibleItemPosition();
                Log.d(TAG, "firstVisibleIndex: " + firstVisibleIndex);
                Log.d(TAG, "lastVisibleIndex: " + lastVisibleIndex);
                Log.d(TAG, "updatedIndex: " + updatedIndex);
                if (updatedIndex < firstVisibleIndex) {
                    updatedIndex = firstVisibleIndex;
                }
                if (lastVisibleIndex < updatedIndex) {
                    updatedIndex = lastVisibleIndex;
                }
                if (0 <= updatedIndex) {
                    updateCarousel(updatedIndex + 1, false);
                }
                postUpdateCarousel();
            } else if (cancelled) {
                updatedIndex = getDefaultIndex();
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

                            try {
                                listView.smoothScrollToPosition(nextVisibleViewIndex);
                            } catch (Exception e) {
                                //Log.e(TAG, "Error scrolling to position: " + nextVisibleViewIndex);
                            }
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
                        if (BaseView.isLandscape(context) &&
                                childContainer instanceof LinearLayout &&
                                childContainer.getChildCount() > 1 &&
                                childContainer.getChildAt(1) instanceof ViewGroup) {
                            childContainer = (ViewGroup) childContainer.getChildAt(1);
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
                        onClickHandler.click(null,
                                component,
                                adapterData.get(childIndex % adapterData.size()),childIndex);
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
                appCMSAndroidModules,
                settings,
                jsonValueKeyMap,
                defaultWidth,
                defaultHeight,
                useMarginsAsPercentages,
                false,
                this.componentViewType,
                true,
                false);
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
            bindView(holder.componentView, adapterData.get(position % adapterData.size()),position);
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
            updatedIndex = getDefaultIndex();
        }
    }

    @Override
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public String getModuleId() {
        return moduleId;
    }

    public void postUpdateCarousel() {
        carouselHandler.postDelayed(carouselUpdater, UPDATE_CAROUSEL_TO);
    }

    public void updateCarousel(int index, boolean fromEvent) {
        synchronized (listView) {
//            index = calculateUpdateIndex(index);
            setUpdatedIndex(index);
            try {
                listView.smoothScrollToPosition(updatedIndex);
            } catch (Exception e) {
                //Log.e(TAG, "Error scrolling to position: " + updatedIndex);
            }
            if (!fromEvent) {
                sendEvent(new InternalEvent<Object>(updatedIndex));
            }
        }
    }

    @Override
    public void resetData(RecyclerView listView) {
        super.resetData(listView);
        updatedIndex = getDefaultIndex();
        sendCancelEventToReceivers(cancelled);
        sendEvent(new InternalEvent<Object>(updatedIndex));
        listView.scrollToPosition(updatedIndex);
        cancel(false);
    }

    private int getDefaultIndex() {
        if (adapterData.size() != 0 && loop) {
            return Integer.MAX_VALUE / 2 - ((Integer.MAX_VALUE / 2) % adapterData.size());
        }
        return 0;
    }

    private void setUpdatedIndex(int index) {
        this.updatedIndex = index;
    }

    private int calculateUpdateIndex(int index) {
        int firstVisibleIndex =
                ((LinearLayoutManager) listView.getLayoutManager()).findFirstVisibleItemPosition();
        int lastVisibleIndex =
                ((LinearLayoutManager) listView.getLayoutManager()).findLastVisibleItemPosition();

        if (index < firstVisibleIndex && adapterData.size() < (firstVisibleIndex - index)) {
            if ((firstVisibleIndex % adapterData.size()) < (index % adapterData.size())) {
                index = (firstVisibleIndex + (index % adapterData.size()));
            } else {
                index = (firstVisibleIndex - (index % adapterData.size()));
            }
        } else if (lastVisibleIndex < index && adapterData.size() < (index - lastVisibleIndex)) {
            if ((lastVisibleIndex % adapterData.size()) < (index % adapterData.size())) {
                index = ((index % adapterData.size()) + lastVisibleIndex);
            } else {
                index = ((index % adapterData.size()) - lastVisibleIndex);
            }
        }

        if (adapterData.size() < Math.abs(index - firstVisibleIndex) ||
                adapterData.size() < Math.abs(index - lastVisibleIndex)) {
            index = firstVisibleIndex;
        }

        return index;
    }

    private void sendCancelEventToReceivers(boolean cancel) {
        for (OnInternalEvent receiver : internalEventReceivers) {
            receiver.cancel(cancel);
        }
    }
}
