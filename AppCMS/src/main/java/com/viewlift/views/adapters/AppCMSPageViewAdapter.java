package com.viewlift.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.viewlift.R;
import com.viewlift.views.customviews.ModuleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viewlift on 11/12/17.
 */

public class AppCMSPageViewAdapter extends RecyclerView.Adapter<AppCMSPageViewAdapter.PageViewHolder> {
    private List<View> childViews;
    private static int TYPE_PLAYER = 0;
    private static int TYPE_STANZA = 1;


    public AppCMSPageViewAdapter() {
        childViews = new ArrayList<>();
    }

    public void addView(View view) {
        if (childViews == null) {
            childViews = new ArrayList<>();
        }
        childViews.add(view);
    }

    public void removeAllViews() {
        if (childViews != null) {
            childViews.clear();
        }
    }

    public View findChildViewById(int id) {
        int adapterDataSize = childViews != null ? childViews.size() : 0;
        for (int i = 0; i < adapterDataSize; i++) {
            View view = childViews.get(i).findViewById(id);
            if (view != null) {
                return view;
            }
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {

        if(isPlayerView(position))
            return TYPE_PLAYER;
        else if(isStanzaView(position))
            return TYPE_STANZA;
        else
            return position;
    }

    private boolean isPlayerView(int position){
        return position == TYPE_PLAYER;
    }

    private boolean isStanzaView(int position){
        return position == TYPE_STANZA;
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout viewGroup = new FrameLayout(parent.getContext());
        FrameLayout.LayoutParams viewGroupLayoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        viewGroup.setLayoutParams(viewGroupLayoutParams);
        return new PageViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {
        try {
            holder.parent.removeAllViews();
            holder.parent.addView(childViews.get(position));
        } catch (Exception e) {

        }
       if (childViews.get(position).findViewById(R.id.video_player_id) !=null){
           TYPE_PLAYER=position;
       }
    }

    @Override
    public int getItemCount() {
        return childViews != null ? childViews.size() : 0;
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {
        ViewGroup parent;
        PageViewHolder(View itemView) {
            super(itemView);
            this.parent = (ViewGroup) itemView;
        }
    }
}
