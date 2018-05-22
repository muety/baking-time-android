package com.github.n1try.bakingtime.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
    public static final String KEY_RECIPE = "recipe";
    public static final String KEY_RECIPE_LIST = "recipe_list";
    public static final String KEY_RECIPE_STEP_INDEX = "step_index";

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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(BasicUtils.styleTitle(getResources().getString(R.string.app_name)));

        mApiService = RecipeApiService.getInstance(this);

        isTablet = getResources().getBoolean(R.bool.is_tablet);
        mRecipeItemAdapter = new RecipeItemAdapter(this, new ArrayList());
        mRecipeOverviewGv.setAdapter(mRecipeItemAdapter);
        if (isTablet) mRecipeOverviewGv.setNumColumns(getResources().getInteger(R.integer.num_cols_tablet));

        if (savedInstanceState != null) {
            mRecipes = savedInstanceState.getParcelableArrayList(KEY_RECIPE_LIST);
            populateAdapter();
        } else {
            new FetchRecipesTask().execute();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipes != null) {
            outState.putParcelableArrayList(KEY_RECIPE_LIST, new ArrayList<>(mRecipes));
        }
    }

    @OnItemClick(R.id.recipe_overview_gv)
    void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
        intent.putExtra(KEY_RECIPE, mRecipes.get(position));
        startActivity(intent);
    }

    private void populateAdapter() {
        mRecipeItemAdapter.clear();
        mRecipeItemAdapter.addAll(mRecipes);
        mRecipeItemAdapter.notifyDataSetChanged();
    }

    class FetchRecipesTask extends AsyncTask<Void, Void, List<Recipe>> {
        @Override
        protected List<Recipe> doInBackground(Void... voids) {
            return mApiService.fetchRecipes();
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            mRecipes = recipes;
            populateAdapter();
        }
    }
}
