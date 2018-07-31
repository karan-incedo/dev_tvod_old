package com.viewlift.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.playersettings.PlayerSettingsContent;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.ClosedCaptionSelectorAdapter;
import com.viewlift.views.adapters.HLSStreamingQualitySelectorAdapter;
import com.viewlift.views.adapters.StreamingQualitySelectorAdapter;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.fragments.PlayerSettingContentListFragment;

import java.util.ArrayList;
import java.util.List;

public class AppCMSPlayerSettings extends AppCompatActivity {



    private AppCMSVideoPageBinder binder;
    private AppCMSPresenter appCMSPresenter;
    private ClosedCaptionSelectorAdapter closedCaptionSelectorAdapter;
    private StreamingQualitySelectorAdapter streamingQualitySelectorAdapter;
    private HLSStreamingQualitySelectorAdapter hlsStreamingQualitySelectorAdapter;

    int SelectedStreamingQualityIndex=0;
    int SelectedClosedCaptionIndex=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appcms_player_settings);

        appCMSPresenter = ((AppCMSApplication) getApplication()).
                getAppCMSPresenterComponent().appCMSPresenter();



        Bundle bundleExtra = getIntent().getBundleExtra("ADAPTER_BUNDLE");
        binder = (AppCMSVideoPageBinder)
                bundleExtra.getBinder(getString(R.string.app_cms_video_player_binder_key));



        closedCaptionSelectorAdapter = new ClosedCaptionSelectorAdapter(this,
                appCMSPresenter,
                binder.getAvailableClosedCaptions());


        hlsStreamingQualitySelectorAdapter= new HLSStreamingQualitySelectorAdapter(this,
                appCMSPresenter,
                binder.getAvailableStreamingQualitiesHLS());

        streamingQualitySelectorAdapter= new StreamingQualitySelectorAdapter(this,
                appCMSPresenter,
                binder.getAvailableStreamingQualities());



        View recyclerView = findViewById(R.id.item_list);
        ImageButton buttonPlayerSettingSubmit = findViewById(R.id.buttonPlayerSettingSubmit);

        buttonPlayerSettingSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("SelectedStreamingQualityIndex",SelectedStreamingQualityIndex);
                returnIntent.putExtra("SelectedClosedCaptionIndex",SelectedClosedCaptionIndex);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        ArrayList<PlayerSettingsContent> settingsItem= new ArrayList<>();

        if (binder != null) {
            if (binder.getAvailableClosedCaptions() !=null &&
                    binder.getAvailableClosedCaptions().size()>1) {
                settingsItem.add(new PlayerSettingsContent("Closed Captions", closedCaptionSelectorAdapter));
            }
            if (binder.getAvailableStreamingQualities() !=null &&
                    binder.getAvailableStreamingQualities().size()>1) {
                settingsItem.add(new PlayerSettingsContent("Playback Quality", streamingQualitySelectorAdapter));
            }
            if (binder.getAvailableStreamingQualitiesHLS() !=null &&
                    binder.getAvailableStreamingQualitiesHLS().size()>1) {
                settingsItem.add(new PlayerSettingsContent("Playback Quality", hlsStreamingQualitySelectorAdapter));
            }
        }
        closedCaptionSelectorAdapter.setItemClickListener(item -> {
            SelectedClosedCaptionIndex = closedCaptionSelectorAdapter.getDownloadQualityPosition();
            closedCaptionSelectorAdapter.setSelectedIndex(SelectedClosedCaptionIndex);
        });

        streamingQualitySelectorAdapter.setItemClickListener( item -> {
            SelectedStreamingQualityIndex = streamingQualitySelectorAdapter.getDownloadQualityPosition();
            streamingQualitySelectorAdapter.setSelectedIndex(SelectedStreamingQualityIndex);
        });

        hlsStreamingQualitySelectorAdapter.setItemClickListener(item -> {
            SelectedStreamingQualityIndex = hlsStreamingQualitySelectorAdapter.getDownloadQualityPosition();
            hlsStreamingQualitySelectorAdapter.setSelectedIndex(SelectedStreamingQualityIndex);
        });


        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, settingsItem, true));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final AppCMSPlayerSettings mParentActivity;
        private final List<PlayerSettingsContent> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerSettingsContent item = (PlayerSettingsContent) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                   // arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
                    PlayerSettingContentListFragment fragment = new PlayerSettingContentListFragment(item.getPlayerSettingAdapter());
                    //fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment,item.getSettingName())
                            .commit();
                }
            }
        };

        SimpleItemRecyclerViewAdapter(AppCMSPlayerSettings parent,
                                      List<PlayerSettingsContent> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_player_setting_master_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mContentView.setText(mValues.get(position).getSettingName());
            System.out.println(mValues.get(position).getSettingName());
            if (position == 0){
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    // arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
                    PlayerSettingContentListFragment fragment = new PlayerSettingContentListFragment(mValues.get(position).getPlayerSettingAdapter());
                    //fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment,mValues.get(position).getSettingName())
                            .commit();
                }
            }

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
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
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        setFullScreenFocus();
        super.onWindowFocusChanged(hasFocus);
    }

    private void setFullScreenFocus() {
        getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
