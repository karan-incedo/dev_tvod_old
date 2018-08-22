package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.playersettings.PlayerSettingsContent;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.activity.AppCMSPlayVideoActivity;
import com.viewlift.views.adapters.ClosedCaptionSelectorAdapter;
import com.viewlift.views.adapters.HLSStreamingQualitySelectorAdapter;
import com.viewlift.views.adapters.StreamingQualitySelectorAdapter;
import com.viewlift.views.fragments.PlayerSettingContentListFragment;

import java.util.ArrayList;
import java.util.List;

public class PlayerSettingsView extends FrameLayout {

    private AppCMSPresenter appCMSPresenter;

    private VideoPlayerView.VideoPlayerSettingsEvent playerSettingsEvent;
    private ClosedCaptionSelectorAdapter closedCaptionSelectorAdapter;
    private StreamingQualitySelectorAdapter streamingQualitySelectorAdapter;
    private HLSStreamingQualitySelectorAdapter hlsStreamingQualitySelectorAdapter;

    int SelectedStreamingQualityIndex = 0;
    int SelectedClosedCaptionIndex = 0;

    SimpleItemRecyclerViewAdapter simpleItemRecyclerViewAdapter;

    Context mContext;

    ArrayList<PlayerSettingsContent> settingsItem = new ArrayList<>();

    public PlayerSettingsView(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public PlayerSettingsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PlayerSettingsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public PlayerSettingsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    public void initializeView(final AppCMSPresenter appCMSPresenter) {
        this.appCMSPresenter = appCMSPresenter;
        LayoutInflater.from(mContext).inflate(R.layout.player_setting_view, this);

        View recyclerView = findViewById(R.id.item_list);
        ImageButton buttonPlayerSettingSubmit = findViewById(R.id.buttonPlayerSettingSubmit);

        buttonPlayerSettingSubmit.setOnClickListener(v -> {

            if (playerSettingsEvent != null) {

                SelectedClosedCaptionIndex = closedCaptionSelectorAdapter != null ? closedCaptionSelectorAdapter.getSelectedIndex() : -1;
                SelectedStreamingQualityIndex = streamingQualitySelectorAdapter != null ? streamingQualitySelectorAdapter.getSelectedIndex() : -1;
                playerSettingsEvent.finishPlayerSetting();
            }

        });

        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {


        try {
            simpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(mContext, settingsItem, true);

            LinearLayoutManager llm = new LinearLayoutManager(mContext);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            recyclerView.setAdapter(simpleItemRecyclerViewAdapter);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSettingItems() {
        settingsItem.clear();

        if (getClosedCaptionSelectorAdapter() != null) {
            closedCaptionSelectorAdapter.setItemClickListener(item -> {
                SelectedClosedCaptionIndex = closedCaptionSelectorAdapter.getDownloadQualityPosition();
                closedCaptionSelectorAdapter.setSelectedIndex(SelectedClosedCaptionIndex);
            });
            settingsItem.add(new PlayerSettingsContent("Closed Captions", closedCaptionSelectorAdapter));
        }
        if (getStreamingQualitySelectorAdapter() != null) {
            streamingQualitySelectorAdapter.setItemClickListener(item -> {
                SelectedStreamingQualityIndex = streamingQualitySelectorAdapter.getDownloadQualityPosition();
                streamingQualitySelectorAdapter.setSelectedIndex(SelectedStreamingQualityIndex);
            });
            settingsItem.add(new PlayerSettingsContent("Playback Quality", streamingQualitySelectorAdapter));
        }

        try {
            simpleItemRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final Context mParentActivity;
        private final List<PlayerSettingsContent> mValues;
        private final boolean mTwoPane;
        private int SELECTED_ITEM_POSITION;


        SimpleItemRecyclerViewAdapter(Context parent,
                                      List<PlayerSettingsContent> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_player_setting_master_content, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mContentView.setText(mValues.get(position).getSettingName());
            System.out.println(mValues.get(position).getSettingName());

            if (position ==  SELECTED_ITEM_POSITION) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    PlayerSettingContentListFragment fragment = new PlayerSettingContentListFragment(mValues.get(position).getPlayerSettingAdapter());
                    ((AppCMSPlayVideoActivity) mParentActivity).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment, mValues.get(position).getSettingName())
                            .commit();
                    setTypeFace(mParentActivity, holder.mContentView,mParentActivity.getString(R.string.opensans_bold_ttf));
                }
            }else {
                setTypeFace(mParentActivity, holder.mContentView, mParentActivity.getString(R.string.opensans_regular_ttf));
            }

            holder.mContentView.setTag(mValues.get(position));
            holder.mContentView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SELECTED_ITEM_POSITION = position;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mContentView = (TextView) view.findViewById(R.id.content);
                mContentView.setTextColor(Color.WHITE);
                setTypeFace(mParentActivity, mContentView, mParentActivity.getString(R.string.opensans_regular_ttf));


            }
        }

        private void setTypeFace(Context context,
                                 TextView view, String fontType) {
            if (null != context && null != view && null != fontType) {
                try {
                    Typeface face = Typeface.createFromAsset(context.getAssets(), fontType);
                    view.setTypeface(face);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getSelectedStreamingQualityIndex() {
        return SelectedStreamingQualityIndex;
    }

    public void setSelectedStreamingQualityIndex(int selectedStreamingQualityIndex) {
        SelectedStreamingQualityIndex = selectedStreamingQualityIndex;
    }

    public int getSelectedClosedCaptionIndex() {
        return SelectedClosedCaptionIndex;
    }

    public void setSelectedClosedCaptionIndex(int selectedClosedCaptionIndex) {
        SelectedClosedCaptionIndex = selectedClosedCaptionIndex;
    }

    public ClosedCaptionSelectorAdapter getClosedCaptionSelectorAdapter() {
        return closedCaptionSelectorAdapter;
    }

    public void setClosedCaptionSelectorAdapter(ClosedCaptionSelectorAdapter closedCaptionSelectorAdapter) {
        this.closedCaptionSelectorAdapter = closedCaptionSelectorAdapter;
    }

    public StreamingQualitySelectorAdapter getStreamingQualitySelectorAdapter() {
        return streamingQualitySelectorAdapter;
    }

    public void setStreamingQualitySelectorAdapter(StreamingQualitySelectorAdapter streamingQualitySelectorAdapter) {
        this.streamingQualitySelectorAdapter = streamingQualitySelectorAdapter;
    }

    public HLSStreamingQualitySelectorAdapter getHlsStreamingQualitySelectorAdapter() {
        return hlsStreamingQualitySelectorAdapter;
    }

    public void setHlsStreamingQualitySelectorAdapter(HLSStreamingQualitySelectorAdapter hlsStreamingQualitySelectorAdapter) {
        this.hlsStreamingQualitySelectorAdapter = hlsStreamingQualitySelectorAdapter;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    public void setPlayerSettingsEvent(VideoPlayerView.VideoPlayerSettingsEvent playerSettingsEvent) {
        this.playerSettingsEvent = playerSettingsEvent;
    }

    public VideoPlayerView.VideoPlayerSettingsEvent getPlayerSettingsEvent() {
        return playerSettingsEvent;
    }
}
