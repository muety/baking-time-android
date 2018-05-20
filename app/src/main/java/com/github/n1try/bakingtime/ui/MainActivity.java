package com.github.n1try.bakingtime.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.services.RecipeApiService;
import com.github.n1try.bakingtime.utils.BasicUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_RECIPE_ID = "recipe_id";

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
        getSupportActionBar().setTitle(BasicUtils.styleTitle(getResources().getString(R.string.app_name)));

        mApiService = RecipeApiService.getInstance(this);

        isTablet = getResources().getBoolean(R.bool.is_tablet);
        mRecipeItemAdapter = new RecipeItemAdapter(this, new ArrayList());
        mRecipeOverviewGv.setAdapter(mRecipeItemAdapter);
        if (isTablet) mRecipeOverviewGv.setNumColumns(getResources().getInteger(R.integer.num_cols_tablet));

        new FetchRecipesTask().execute();
    }

    @OnItemClick(R.id.recipe_overview_gv)
    void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(KEY_RECIPE_ID, mRecipes.get(position));
        startActivity(intent);
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
