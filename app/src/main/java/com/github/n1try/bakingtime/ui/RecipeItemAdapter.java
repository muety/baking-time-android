package com.github.n1try.bakingtime.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeItemAdapter extends ArrayAdapter<Recipe> {
    private Context context;
    private List<Drawable> defaultImages;
    private List<Recipe> recipes;

    public RecipeItemAdapter(@NonNull Context context, @NonNull List recipes) {
        super(context, 0, recipes);
        this.context = context;
        this.recipes = recipes;

        defaultImages = Arrays.stream(context.getResources().getStringArray(R.array.default_recipe_images))
                .map(i -> context.getResources().getIdentifier(i, "drawable", context.getPackageName()))
                .map(id -> context.getDrawable(id))
                .collect(Collectors.toList());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_card_item, parent, false);
        }

        final ImageView recipeImage = convertView.findViewById(R.id.recipe_item_iv);
        final TextView recipeText = convertView.findViewById(R.id.recipe_item_name_tv);

        final Recipe recipe = getItem(position);
        recipeText.setText(recipe.getName());
        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Picasso.with(getContext()).load(recipe.getImageUrl()).into(recipeImage);
        } else {
            Drawable image = defaultImages.get(position % defaultImages.size());
            recipeImage.setImageDrawable(image);
        }

        return convertView;
    }
}
