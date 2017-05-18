package air.com.snagfilms.views.customviews;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.widget.FrameLayout;

import com.google.gson.JsonElement;

import java.util.InputMismatchException;

import javax.inject.Inject;

import air.com.snagfilms.models.data.appcms.page.Component;

/**
 * Created by viewlift on 5/5/17.
 */

public class ComponentView extends BaseView {
    private final Component component;

    @Inject
    public ComponentView(Context context, Component component) {
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

    @Override
    protected void init() {
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParams);
    }
}
