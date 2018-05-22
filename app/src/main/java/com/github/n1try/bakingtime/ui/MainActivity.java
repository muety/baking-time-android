package com.github.n1try.bakingtime.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.services.RecipeApiService;
import com.github.n1try.bakingtime.utils.BasicUtils;
import com.github.n1try.bakingtime.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.offline_container)
    View mOfflineContainer;
    @BindView(R.id.recipe_overview_gv)
    GridView mRecipeOverviewGv;

    private RecipeApiService mApiService;
    private RecipeItemAdapter mRecipeItemAdapter;
    private List<Recipe> mRecipes;
    private boolean isTablet;
    private CountingIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(BasicUtils.styleTitle(getResources().getString(R.string.app_name)));

        mApiService = RecipeApiService.getInstance(this);
        getIdlingResource();

        isTablet = getResources().getBoolean(R.bool.is_tablet);
        mRecipeItemAdapter = new RecipeItemAdapter(this, new ArrayList());
        mRecipeOverviewGv.setAdapter(mRecipeItemAdapter);
        if (isTablet) mRecipeOverviewGv.setNumColumns(getResources().getInteger(R.integer.num_cols_tablet));

        if (savedInstanceState != null) {
            mRecipes = savedInstanceState.getParcelableArrayList(Constants.KEY_RECIPE_LIST);
            populateAdapter();
        } else {
            if (BasicUtils.isNetworkAvailable(this)) {
                new FetchRecipesTask().execute();
                mIdlingResource.increment();
            } else {
                mRecipeOverviewGv.setVisibility(View.GONE);
                mOfflineContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipes != null) {
            outState.putParcelableArrayList(Constants.KEY_RECIPE_LIST, new ArrayList<>(mRecipes));
        }
    }

    @OnItemClick(R.id.recipe_overview_gv)
    void onItemClick(int position) {
        Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
        intent.putExtra(Constants.KEY_RECIPE, mRecipes.get(position));
        startActivity(intent);
    }

    private void populateAdapter() {
        mRecipeItemAdapter.clear();
        mRecipeItemAdapter.addAll(mRecipes);
        mRecipeItemAdapter.notifyDataSetChanged();
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new CountingIdlingResource(getLocalClassName());
        }
        return mIdlingResource;
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
            mIdlingResource.decrement();
        }
    }
}
