package com.github.n1try.bakingtime.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.ui.RecipeDetailActivity;
import com.github.n1try.bakingtime.utils.Constants;

public class IngredientsWidget extends AppWidgetProvider {
    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int recipeId, String recipeName, String recipeIngredients, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeId, recipeName, recipeIngredients, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int recipeId, String recipeName, String recipeIngredients, int appWidgetId) {
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtra(Constants.KEY_RECIPE_ID, recipeId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);
        views.setTextViewText(R.id.widget_title_tv, recipeName);
        views.setTextViewText(R.id.widget_ingredients_tv, recipeIngredients);
        if (recipeId != -1) views.setOnClickPendingIntent(R.id.widget_ingredients_tv, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RecipeIngredientService.startActionUpdateWidgets(context);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}

