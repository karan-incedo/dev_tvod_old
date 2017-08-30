package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.viewlift.models.data.appcms.ui.page.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by viewlift on 7/16/17.
 */

public class ComponentListSerializerDeserializer implements
        JsonDeserializer<ArrayList<Component>>,
        JsonSerializer<ArrayList<Component>> {
    private Gson gson;

    public ComponentListSerializerDeserializer() {
        gson = new Gson();
    }

    @Override
    public ArrayList<Component> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ArrayList<Component> components = new ArrayList<>();
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

    @Override
    public JsonElement serialize(ArrayList<Component> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray result = new JsonArray();
        for (int i = 0; i < src.size(); i++) {
            JsonElement jsonElement = gson.toJsonTree(src.get(i), Component.class);
            result.add(jsonElement);
        }
        return result;
    }
}
