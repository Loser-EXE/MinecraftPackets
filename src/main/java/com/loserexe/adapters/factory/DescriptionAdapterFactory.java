package com.loserexe.adapters.factory;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.loserexe.adapters.adapter.DescriptionAdapter;

public class DescriptionAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        return (TypeAdapter<T>) new DescriptionAdapter(gson);
    }
}
