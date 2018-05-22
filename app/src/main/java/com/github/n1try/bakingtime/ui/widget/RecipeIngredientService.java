package com.github.n1try.bakingtime.ui.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.utils.Constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RecipeIngredientService extends IntentService {
    public RecipeIngredientService() {
        super("RecipeIngredientService");
    }

    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, RecipeIngredientService.class);
        intent.setAction(Constants.ACTION_UPDATE_INGREDIENT_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        final String action = intent.getAction();
        if (action.equals(Constants.ACTION_UPDATE_INGREDIENT_WIDGETS)) {
            Context context = getApplicationContext();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientsWidget.class));

            int id = sharedPreferences.getInt(Constants.PREF_KEY_LAST_RECIPE_ID, -1);
            String name = sharedPreferences.getString(Constants.PREF_KEY_LAST_RECIPE_NAME, context.getResources().getString(R.string.unknown_recipe));
            Optional<Set<String>> ingredients = Optional.ofNullable(sharedPreferences.getStringSet(Constants.PREF_KEY_LAST_RECIPE_INGREDIENTS, null));
            String formattedIngredients = TextUtils.join("\n\n", ingredients.orElse(new HashSet<>(Arrays.asList(context.getResources().getString(R.string.no_recipe_selected)))));
            IngredientsWidget.updateAppWidgets(context, appWidgetManager, id, name, formattedIngredients, appWidgetIds);
        }
    }
}
