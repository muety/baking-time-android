package com.github.n1try.bakingtime.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;

public class SerializationUtils {
    private static final SerializationUtils ourInstance = new SerializationUtils();

    public GsonBuilder gsonBuilder;

    public static SerializationUtils getInstance() {
        return ourInstance;
    }

    private SerializationUtils() {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
    }
}
