package com.viewlift.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewlift.models.data.appcms.search.AppCMSSearchResult;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 6/12/17.
 */

public class AppCMSSearchItemAdapter extends RecyclerView.Adapter<AppCMSSearchItemAdapter.ViewHolder> {
    private static final String TAG = "AppCMSSearchAdapter";

    private final AppCMSPresenter appCMSPresenter;
    private List<AppCMSSearchResult> appCMSSearchResults;

    public AppCMSSearchItemAdapter(AppCMSPresenter appCMSPresenter, List<AppCMSSearchResult> appCMSSearchResults) {
        this.appCMSPresenter = appCMSPresenter;
        this.appCMSSearchResults = appCMSSearchResults;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_result_item,
                viewGroup,
                false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final int adapterPosition = i;
        viewHolder.searchResultTitle.setText(appCMSSearchResults.get(adapterPosition).getTitle());
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String permalink = appCMSSearchResults.get(adapterPosition).getPermalink();
                String action = viewHolder.view.getContext().getString(R.string.app_cms_action_videopage_key);
                String title = appCMSSearchResults.get(adapterPosition).getTitle();
                Log.d(TAG, "Launching " + permalink + ":" + action);
                if (!appCMSPresenter.launchButtonSelectedAction(permalink, action, title, null)) {
                    Log.e(TAG, "Could not launch action: " +
                            " permalink: " +
                            permalink +
                            " action: " +
                            action);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appCMSSearchResults != null ? appCMSSearchResults.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView searchResultTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            this.view = view;
            this.searchResultTitle = (TextView) itemView.findViewById(R.id.search_result_title);
        }
    }

    public void setData(List<AppCMSSearchResult> results) {
        appCMSSearchResults = results;
        notifyDataSetChanged();
    }
}
