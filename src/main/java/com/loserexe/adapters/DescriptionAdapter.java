package com.loserexe.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.loserexe.pojo.info.Description;

import java.io.IOException;

public class DescriptionAdapter extends TypeAdapter<Description> {
    private final Gson gson;

    public DescriptionAdapter (Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter jsonWriter, Description description) throws IOException {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public Description read(JsonReader jsonReader) throws IOException {
        switch (jsonReader.peek()) {
            case STRING:
                return new Description(jsonReader.nextString(), true);

            case BEGIN_OBJECT:
                return gson.fromJson(jsonReader, Description.class);

            default:
                throw new RuntimeException("Expected object or string, not " + jsonReader.peek());
        }
    }
}
