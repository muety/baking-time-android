package com.github.n1try.bakingtime.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.widget.GridView;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.services.RecipeApiService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recipe_overview_gv)
    GridView mRecipeOverviewGv;

    private RecipeApiService mApiService;
    private RecipeItemAdapter mRecipeItemAdapter;
    private List<Recipe> mRecipes;
    private boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initActionBarStyle();

        mApiService = RecipeApiService.getInstance(this);

        isTablet = getResources().getBoolean(R.bool.is_tablet);
        mRecipeItemAdapter = new RecipeItemAdapter(this, new ArrayList());
        mRecipeOverviewGv.setAdapter(mRecipeItemAdapter);
        if (isTablet) mRecipeOverviewGv.setNumColumns(getResources().getInteger(R.integer.num_cols_tablet));

        new FetchRecipesTask().execute();
    }

    private void initActionBarStyle() {
        String appTitle = getResources().getString(R.string.app_name);
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(appTitle);
        spannableBuilder.setSpan(new TypefaceSpan("casual"), 0, appTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, appTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableBuilder.setSpan(new ForegroundColorSpan(Color.WHITE), 0, appTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(spannableBuilder);
    }

    class FetchRecipesTask extends AsyncTask<Void, Void, List<Recipe>> {
        @Override
        protected List<Recipe> doInBackground(Void... voids) {
            return mApiService.fetchRecipes();
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            mRecipes = recipes;
            mRecipeItemAdapter.clear();
            mRecipeItemAdapter.addAll(mRecipes);
            mRecipeItemAdapter.notifyDataSetChanged();
        }
    }
}
