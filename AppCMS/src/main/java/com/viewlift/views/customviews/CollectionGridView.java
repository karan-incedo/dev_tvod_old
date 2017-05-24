package com.viewlift.views.customviews;

import android.content.Context;
import android.support.v7.widget.CardView;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.page.Component;

import javax.inject.Inject;

/**
 * Created by viewlift on 5/5/17.
 */

public class CollectionGridView extends CardView {
    private final Component component;

    @Inject
    public CollectionGridView(Context context, Component component) {
        super(context);
        this.component = component;
        init();
    }

    public void bindView(AppCMSPageAPI data) throws IllegalArgumentException {

        throw new IllegalArgumentException(getClass().getCanonicalName() +
                "." +
                getClass().getEnclosingMethod().getName() +
                ": operation not supported.");
    }

    protected void init() {
        CardView.LayoutParams layoutParams =
                new CardView.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);
    }
}
