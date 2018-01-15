package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.models.data.appcms.downloads.DownloadVideoRealm;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.CollectionGridItemView;
import com.viewlift.views.customviews.InternalEvent;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.customviews.ViewCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.viewlift.models.data.appcms.downloads.DownloadStatus.STATUS_RUNNING;

/*
 * Created by viewlift on 5/5/17.
 */

public class AppCMSPlaylistAdapter extends RecyclerView.Adapter<AppCMSPlaylistAdapter.ViewHolder>
        implements AppCMSBaseAdapter, OnInternalEvent {
    private static final String TAG = AppCMSPlaylistAdapter.class.getSimpleName() + "TAG";


    protected Context mContext;
    protected Layout parentLayout;
    protected Component component;
    protected AppCMSPresenter appCMSPresenter;
    protected Settings settings;
    protected ViewCreator viewCreator;
    protected Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    Module moduleAPI;
    List<ContentDatum> adapterData;
    CollectionGridItemView.OnClickHandler onClickHandler;
    int defaultWidth;
    int defaultHeight;
    boolean useMarginsAsPercentages;
    String componentViewType;
    AppCMSAndroidModules appCMSAndroidModules;
    private boolean useParentSize;
    private AppCMSUIKeyType viewTypeKey;
    private boolean isClickable;

    private List<OnInternalEvent> receivers;

    private String moduleId;
    RecyclerView mRecyclerView;

    public AppCMSPlaylistAdapter(Context context,
                                 ViewCreator viewCreator,
                                 AppCMSPresenter appCMSPresenter,
                                 Settings settings,
                                 Layout parentLayout,
                                 boolean useParentSize,
                                 Component component,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 Module moduleAPI,
                                 int defaultWidth,
                                 int defaultHeight,
                                 String viewType,
                                 AppCMSAndroidModules appCMSAndroidModules) {
        this.mContext = context;
        this.viewCreator = viewCreator;
        this.appCMSPresenter = appCMSPresenter;
        this.parentLayout = parentLayout;
        this.useParentSize = useParentSize;
        this.component = component;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.moduleAPI = moduleAPI;
        this.receivers = new ArrayList<>();

        if (moduleAPI != null && moduleAPI.getContentData() != null) {
            this.adapterData = moduleAPI.getContentData();
        } else {
            this.adapterData = new ArrayList<>();
        }

        this.componentViewType = viewType;
        this.viewTypeKey = jsonValueKeyMap.get(componentViewType);
        if (this.viewTypeKey == null) {
            this.viewTypeKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.useMarginsAsPercentages = true;
        this.isClickable = true;
        this.setHasStableIds(false);
        this.appCMSAndroidModules = appCMSAndroidModules;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CollectionGridItemView view = viewCreator.createCollectionGridItemView(parent.getContext(),
                parentLayout,
                useParentSize,
                component,
                appCMSPresenter,
                moduleAPI,
                appCMSAndroidModules,
                settings,
                jsonValueKeyMap,
                defaultWidth,
                defaultHeight,
                useMarginsAsPercentages,
                true,
                this.componentViewType,
                false,
                false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (0 <= position && position < adapterData.size()) {
            for (int i = 0; i < holder.componentView.getNumberOfChildren(); i++) {
                if (holder.componentView.getChild(i) instanceof TextView) {
                    ((TextView) holder.componentView.getChild(i)).setText("");
                }
            }
            bindView(holder.componentView, adapterData.get(position), position);
        }
    }


    private void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(Uri.decode(url))
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return (adapterData != null ? adapterData.size() : 0);
    }

    @Override
    public void resetData(RecyclerView listView) {
        notifyDataSetChanged();
    }

    @Override
    public void updateData(RecyclerView listView, List<ContentDatum> contentData) {
        listView.setAdapter(null);
        adapterData = null;
        notifyDataSetChanged();
        adapterData = contentData;
        notifyDataSetChanged();
        listView.setAdapter(this);
        listView.invalidate();
        notifyDataSetChanged();

    }

    @SuppressLint("ClickableViewAccessibility")
    void bindView(CollectionGridItemView itemView,
                  final ContentDatum data, int position) throws IllegalArgumentException {
        if (onClickHandler == null) {

            onClickHandler = new CollectionGridItemView.OnClickHandler() {
                @Override
                public void click(CollectionGridItemView collectionGridItemView,
                                  Component childComponent,
                                  ContentDatum data, int clickPosition) {
                    if (isClickable) {
                        if (data.getGist() != null) {
                          /*get audio details on tray click item and play song*/
                            if (data.getGist() != null &&
                                    data.getGist().getMediaType() != null &&
                                    data.getGist().getMediaType().toLowerCase().contains(itemView.getContext().getString(R.string.media_type_audio).toLowerCase()) &&
                                    data.getGist().getContentType() != null &&
                                    data.getGist().getContentType().toLowerCase().contains(itemView.getContext().getString(R.string.content_type_audio).toLowerCase())) {
                                appCMSPresenter.getAudioDetail(data.getGist().getId());
                                return;
                            }

                        }
                    }
                }

                @Override
                public void play(Component childComponent, ContentDatum data) {
                }
            };

        }

        for (int i = 0; i < itemView.getNumberOfChildren(); i++) {
            itemView.bindChild(itemView.getContext(),
                    itemView.getChild(i),
                    data,
                    jsonValueKeyMap,
                    onClickHandler,
                    componentViewType,
                    Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor()), appCMSPresenter, position);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridItemView) itemView;
        }

    }

    @Override
    public void addReceiver(OnInternalEvent e) {
        receivers.add(e);
    }

    @Override
    public void sendEvent(InternalEvent<?> event) {
        for (OnInternalEvent internalEvent : receivers) {
            internalEvent.receiveEvent(event);
        }
    }

    @Override
    public void receiveEvent(InternalEvent<?> event) {
        adapterData.clear();
        notifyDataSetChanged();
    }

    @Override
    public void cancel(boolean cancel) {

    }

    @Override
    public String getModuleId() {
        return moduleId;
    }

    @Override
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    private List<String> getListOfUpcomingMovies(int position, Object downloadStatus) {
        if (position + 1 == adapterData.size()) {
            return Collections.emptyList();
        }

        List<String> contentDatumList = new ArrayList<>();
        for (int i = position + 1; i < adapterData.size(); i++) {
            ContentDatum contentDatum = adapterData.get(i);
            if (contentDatum.getGist() != null &&
                    contentDatum.getGist().getDownloadStatus().equals(downloadStatus)) {
                contentDatumList.add(contentDatum.getGist().getId());
            }
        }

        return contentDatumList;
    }


    @Override
    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }
}
