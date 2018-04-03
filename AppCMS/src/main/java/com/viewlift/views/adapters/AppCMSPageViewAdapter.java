package com.viewlift.views.adapters;

import android.content.Context;
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
    private List<ModuleView> childViews;

    private static int TYPE_PLAYER = 0;
    private static int TYPE_STANZA = 1;

    private FrameLayout topLayout;

    public AppCMSPageViewAdapter(Context context) {
        childViews = new ArrayList<>();
        setHasStableIds(false);
        createTopLayout(context);
    }

    public void addView(ModuleView view) {
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

        
            if (isPlayerView(position ))
                return TYPE_PLAYER;
            else if (isStanzaView(position ))
                return TYPE_STANZA;
            else
                return position;
        
    }

    private boolean isPlayerView(int position){
       /* if(((ModuleView)childViews.get(position)).getModule().getType().equalsIgnoreCase("AC StandaloneVideoPlayer 01"))
            return true;
        return false;
		*/
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
            /*if (position == 0) {
                holder.parent.removeAllViews();
                holder.parent.addView(topLayout);
            } else if(!isPlayerView(position - 1)) {*/
                holder.parent.removeAllViews();
                holder.parent.addView(childViews.get(position));
            //}
        } catch (Exception e) {

        }
		if (childViews.get(position).findViewById(R.id.video_player_id) !=null){
           TYPE_PLAYER=position;
       }
    }

    @Override
    public int getItemCount() {
        return childViews != null ? childViews.size()  : 0;
    }

    public List<String> getViewIdList(int firstIndex, int lastIndex) {
        List<String> viewIdList = new ArrayList<>();
        try {
            if (childViews != null && !childViews.isEmpty()) {
                int childViewsSize = childViews.size();
                for (int i = firstIndex; i < lastIndex && i < childViewsSize; i++) {
                    if (childViews.get(i) != null &&
                            childViews.get(i).getModule() != null) {
                        String viewModuleId = childViews.get(i).getModule().getId();
                        if (!TextUtils.isEmpty(viewModuleId)) {
                            viewIdList.add(viewModuleId);
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        return viewIdList;
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {
        ViewGroup parent;
        PageViewHolder(View itemView) {
            super(itemView);
            this.parent = (ViewGroup) itemView;
        }
    }

    private void createTopLayout(Context context) {
        topLayout = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                1);
        topLayout.setLayoutParams(layoutParams);
    }
}
