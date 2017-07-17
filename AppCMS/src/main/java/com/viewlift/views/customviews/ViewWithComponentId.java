package com.viewlift.views.customviews;

import android.view.View;

/**
 * Created by viewlift on 7/16/17.
 */

public class ViewWithComponentId {
    View view;
    String id;
    public static class Builder {
        private ViewWithComponentId viewWithComponentId;
        public Builder() {
            this.viewWithComponentId = new ViewWithComponentId();
        }
        public Builder view(View view) {
            viewWithComponentId.view = view;
            return this;
        }
        public Builder id(String id) {
            viewWithComponentId.id = id;
            return this;
        }
        public ViewWithComponentId build() {
            return viewWithComponentId;
        }
    }
}
