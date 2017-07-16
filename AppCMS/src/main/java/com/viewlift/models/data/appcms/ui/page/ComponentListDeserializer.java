package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.viewlift.models.data.appcms.ui.page.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by viewlift on 7/16/17.
 */

public class ComponentListDeserializer implements JsonDeserializer<List<Component>> {
    private Gson gson;

    public ComponentListDeserializer() {
        gson = new Gson();
    }

    @Override
    public List<Component> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Component> components = new ArrayList<>();
        if (json.isJsonArray()) {
            JsonArray componentArray = json.getAsJsonArray();
            for (int i = 0; i < componentArray.size(); i++) {
                components.add(gson.fromJson(componentArray.get(i), Component.class));
            }
        } else if (json.isJsonObject()) {
            components.add(gson.fromJson(json, Component.class));
        }
        return components;
    }
}
