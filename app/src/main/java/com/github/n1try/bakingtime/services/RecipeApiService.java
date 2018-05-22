package com.github.n1try.bakingtime.services;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.model.RecipeStep;
import com.github.n1try.bakingtime.utils.SerializationUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RecipeApiService {
    private static RecipeApiService ourInstance;
    private static final String RECIPE_ENDPOINT = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private static final String[] SUPPORTED_VIDEO_EXTENSIONS = new String[]{"mp4"};
    private static final String[] SUPPORTED_IMAGE_EXTENSIONS = new String[]{"jpg", "jpeg", "png"};
    private static Type recipeListType = new TypeToken<ArrayList<Recipe>>() {
    }.getType();

    private OkHttpClient mHttpClient;
    private Gson mGson;

    private ArrayList<Recipe> recipesCache;

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
            List<Recipe> recipes = ((List<Recipe>) mGson.fromJson(body.string(), recipeListType))
                    .stream()
                    .map(r -> tryHealConfusedLinks(r))
                    .collect(Collectors.toList());
            body.close();
            recipesCache = new ArrayList<>(recipes);
            return recipes;
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "Could not fetch recipes.\n" + e.getMessage());
        }
        recipesCache = new ArrayList<>();
        return new ArrayList<>();
    }

    private Recipe tryHealConfusedLinks(Recipe recipe) {
        for (RecipeStep step : recipe.getSteps()) {
            String videoUrl = step.getVideoUrl();
            String thumbnailUrl = step.getThumbnailUrl();
            String newVideoUrl = videoUrl;
            String newThumbnailUrl = thumbnailUrl;

            if (!TextUtils.isEmpty(videoUrl)) {
                if (!checkExtension(videoUrl, SUPPORTED_VIDEO_EXTENSIONS)) {
                    if (checkExtension(videoUrl, SUPPORTED_IMAGE_EXTENSIONS)) {
                        newThumbnailUrl = videoUrl;
                    }
                    newVideoUrl = null;
                }
            }

            if (!TextUtils.isEmpty(thumbnailUrl)) {
                if (!checkExtension(thumbnailUrl, SUPPORTED_IMAGE_EXTENSIONS)) {
                    if (checkExtension(thumbnailUrl, SUPPORTED_VIDEO_EXTENSIONS)) {
                        newVideoUrl = thumbnailUrl;
                    }
                    newThumbnailUrl = null;
                }
            }

            step.setVideoUrl(newVideoUrl);
            step.setThumbnailUrl(newThumbnailUrl);
        }
        return recipe;
    }

    private static boolean checkExtension(String url, String[] extensions) {
        return Arrays.stream(extensions).anyMatch(e -> url.toLowerCase().endsWith(e));
    }

    public Optional<Recipe> getOrFetchById(int id) {
        if (recipesCache == null || recipesCache.isEmpty()) {
            fetchRecipes();
        }
        return recipesCache.stream().filter(r -> r.getId() == id).findFirst();
    }

    public List<Recipe> getRecipesCache() {
        return recipesCache;
    }

    public void invalidateRecipesCache() {
        recipesCache = null;
    }
}
