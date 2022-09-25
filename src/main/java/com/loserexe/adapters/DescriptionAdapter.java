package com.loserexe.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.loserexe.pojo.info.Description;
import com.loserexe.pojo.text.Extra;

import java.io.IOException;
import java.util.List;

public class DescriptionAdapter extends TypeAdapter<Description> {
    private final Gson gson;

    public DescriptionAdapter (Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter jsonWriter, Description description) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("extra");
        jsonWriter.beginArray();
        List<Extra> extra = description.getExtra();
        for (int i = 0; i < extra.size(); i++) {
            writeExtra(jsonWriter, extra.get(i));
        }
        jsonWriter.endArray();
        jsonWriter.name("text");
        jsonWriter.value(description.getText());
        jsonWriter.endObject();
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

    private void writeExtra(JsonWriter jsonWriter, Extra extra) throws IOException {
        jsonWriter.beginObject();

        if (extra.isBold()) {
            jsonWriter.name("bold");
            jsonWriter.value(true);
        }

        if (extra.isStrikeThrough()) {
            jsonWriter.name("strikeThrough");
            jsonWriter.value(true);
        }

        if (extra.getColor() != null) {
            jsonWriter.name("color");
            jsonWriter.value(extra.getColor());
        }

        if (extra.getExtra() != null) {
            jsonWriter.name("extra");
            jsonWriter.beginArray();
            for (int i = 0; i < extra.getExtra().size(); i++) {
                writeExtra(jsonWriter, extra.getExtra().get(i));
            }
            jsonWriter.endArray();
        }

        if (extra.getText() != null) {
            jsonWriter.name("text");
            jsonWriter.value(extra.getText());
        }

        jsonWriter.endObject();
    }
}
