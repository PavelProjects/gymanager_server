package ru.pobopo.gymanager.shared.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class ExceptionSerializer implements JsonSerializer<Exception>, JsonDeserializer<Exception> {
    @Override
    public JsonElement serialize(Exception src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("cause", new JsonPrimitive(String.valueOf(src.getCause())));
        jsonObject.add("message", new JsonPrimitive(src.getMessage()));
        return jsonObject;
    }

    @Override
    public Exception deserialize(
        JsonElement jsonElement,
        Type type,
        JsonDeserializationContext jsonDeserializationContext
    ) throws JsonParseException {
        if (jsonElement != null && jsonElement.getAsJsonObject() != null) {
            JsonObject object = jsonElement.getAsJsonObject();
            JsonElement cause = object.get("cause");
            JsonElement message = object.get("message");
            return new Exception(message != null ? message.getAsString() : "", cause != null ? new Exception(cause.getAsString()) : null);
        }
        return new Exception();
    }
}
