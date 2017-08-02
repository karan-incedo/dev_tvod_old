package com.viewlift.views.customviews;

import android.support.v7.widget.RecyclerView;

/**
 * Created by viewlift on 7/16/17.
 */

public class ListWithAdapter {
    RecyclerView listView;
    RecyclerView.Adapter adapter;
    String id;

    public RecyclerView getListView() {
        return listView;
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public static class Builder {
        private ListWithAdapter listWithAdapter;

        public Builder() {
            listWithAdapter = new ListWithAdapter();
        }

        public Builder listview(RecyclerView listView) {
            listWithAdapter.listView = listView;
            return this;
        }

        public Builder adapter(RecyclerView.Adapter adapter) {
            listWithAdapter.adapter = adapter;
            return this;
        }

        public Builder id(String id) {
            listWithAdapter.id = id;
            return this;
        }

        public ListWithAdapter build() {
            return listWithAdapter;
        }
    }
}
