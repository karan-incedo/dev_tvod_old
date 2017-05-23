package air.com.snagfilms.views.customviews;

import android.content.Context;
import android.support.v7.widget.CardView;

import com.google.gson.JsonElement;

import javax.inject.Inject;

import air.com.snagfilms.models.data.appcms.page.Component;

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

    public void bindView(JsonElement data) throws IllegalArgumentException {
        // TODO: Parse json data and map to child elements
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
