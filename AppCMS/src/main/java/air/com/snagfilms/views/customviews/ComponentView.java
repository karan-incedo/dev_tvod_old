package air.com.snagfilms.views.customviews;

import android.content.Context;
import android.view.View;

import com.google.gson.JsonElement;

import java.util.InputMismatchException;

import javax.inject.Inject;

import air.com.snagfilms.models.data.appcms.page.Component;

/**
 * Created by viewlift on 5/5/17.
 */

public class ComponentView extends View {
    private final Component component;

    @Inject
    public ComponentView(Context context, Component component) {
        super(context);
        this.component = component;
    }

    public void bindView(JsonElement data) throws IllegalArgumentException {
        // TODO: Parse json data and map to child elements
        throw new IllegalArgumentException(getClass().getCanonicalName() +
                "." +
                getClass().getEnclosingMethod().getName() +
                ": operation not supported.");
    }
}
