package com.github.n1try.bakingtime.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.services.RecipeApiService;

public class StepDetailActivity extends AppCompatActivity implements StepDetailFragment.OnRecipeStepChangeListener {
    private Recipe mRecipe;
    private int mStepIndex;
    private FragmentManager fragmentManager;
    private RecipeApiService mRecipeApiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        mRecipe = getIntent().getParcelableExtra(MainActivity.KEY_RECIPE);
        mStepIndex = getIntent().getIntExtra(MainActivity.KEY_RECIPE_STEP_INDEX, 0);

        fragmentManager = getSupportFragmentManager();
        mRecipeApiService = RecipeApiService.getInstance(getApplicationContext());
        spawnFragment();
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

    private void spawnFragment() {
        String tag = String.valueOf(mRecipe.getSteps().get(mStepIndex).hashCode());
        if (fragmentManager.findFragmentByTag(tag) == null) {
            Fragment fragment = StepDetailFragment.newInstance(mRecipe, mStepIndex);
            fragmentManager.beginTransaction().replace(R.id.detail_step_container, fragment, tag).commit();
        }
    }

    @Override
    public void onNextStep(int currentStepIndex) {
        mStepIndex++;
        spawnFragment();
    }

    @Override
    public void onPreviousStep(int currentStepIndex) {
        mStepIndex--;
        spawnFragment();
    }
}
