package com.github.n1try.bakingtime.services;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.utils.SerializationUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RecipeApiService {
    private static RecipeApiService ourInstance;
    private static final String RECIPE_ENDPOINT = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static Type recipeListType = new TypeToken<ArrayList<Recipe>>() {
    }.getType();

    private OkHttpClient mHttpClient;
    private Gson mGson;

    public static RecipeApiService getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new RecipeApiService(context);
        }
        return ourInstance;
    }

    private RecipeApiService(Context context) {
        mHttpClient = new OkHttpClient.Builder()
                .cache(new Cache(context.getCacheDir(), 1024 * 1014 * 10))
                .build();
        mGson = SerializationUtils.getInstance().gsonBuilder.create();
    }

    public List<Recipe> fetchRecipes() {
        Uri fetchUri = Uri.parse(RECIPE_ENDPOINT);
        Request request = new Request.Builder().url(fetchUri.toString()).build();

        try {
            Response response = mHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException(response.message());
            ResponseBody body = response.body();
            List<Recipe> recipes = mGson.fromJson(body.string(), recipeListType);
            body.close();
            return recipes;
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "Could not fetch recipes.\n" + e.getMessage());
        }
        return new ArrayList<>();
    }
}
