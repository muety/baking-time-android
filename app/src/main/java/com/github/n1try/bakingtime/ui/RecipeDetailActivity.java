package com.github.n1try.bakingtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.services.RecipeApiService;
import com.github.n1try.bakingtime.utils.Constants;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnRecipeStepSelectedListener, StepDetailFragment.OnRecipeStepChangeListener {
    private FragmentManager mFragmentManager;
    private RecipeApiService mApiService;
    private boolean isTablet;
    private Recipe mRecipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mApiService = RecipeApiService.getInstance(getApplicationContext());

        Bundle bundle = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        if (bundle.containsKey(Constants.KEY_RECIPE)) {
            mRecipe = bundle.getParcelable(Constants.KEY_RECIPE);
        } else if (bundle.containsKey(Constants.KEY_RECIPE_ID)) {
            mRecipe = mApiService.getOrFetchById(bundle.getInt(Constants.KEY_RECIPE_ID)).get(); // handle null case
        }
        mFragmentManager = getSupportFragmentManager();
        isTablet = getResources().getBoolean(R.bool.is_tablet);

        if (mFragmentManager.findFragmentByTag(Constants.TAG_DETAIL_FRAGMENT) == null) {
            Fragment fragment = RecipeDetailFragment.newInstance(mRecipe);
            mFragmentManager.beginTransaction().replace(R.id.detail_overview_container, fragment, Constants.TAG_DETAIL_FRAGMENT).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipe != null) {
            outState.putParcelable(Constants.KEY_RECIPE, mRecipe);
        }
    }

    @Override
    public void onStepSelected(Recipe recipe, int index) {
        if (isTablet) {
            spawnStepFragment(index);
        } else {
            Intent intent = new Intent(RecipeDetailActivity.this, StepDetailActivity.class);
            intent.putExtra(Constants.KEY_RECIPE, recipe);
            intent.putExtra(Constants.KEY_RECIPE_STEP_INDEX, index);
            startActivity(intent);
        }
    }

    private void spawnStepFragment(int stepIndex) {
        String tag = String.valueOf(mRecipe.getSteps().get(stepIndex).hashCode());
        if (mFragmentManager.findFragmentByTag(tag) == null) {
            Fragment fragment = StepDetailFragment.newInstance(mRecipe, stepIndex);
            mFragmentManager.beginTransaction().replace(R.id.detail_step_container, fragment, tag).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNextStep(int currentStepIndex) {
        if (!isTablet) return;
        spawnStepFragment(++currentStepIndex);
    }

    @Override
    public void onPreviousStep(int currentStepIndex) {
        if (!isTablet) return;
        spawnStepFragment(--currentStepIndex);
    }
}
