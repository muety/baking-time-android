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

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnRecipeStepSelectedListener, StepDetailFragment.OnRecipeStepChangeListener {
    private static final String TAG_DETAIL_FRAGMENT = "detail_fragment";
    private static final String TAG_STEP_DETAIL_FRAGMENT = "step_detail_fragment";
    private FragmentManager fragmentManager;
    private boolean isTablet;
    private Recipe mRecipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        mRecipe = bundle.getParcelable(MainActivity.KEY_RECIPE);
        fragmentManager = getSupportFragmentManager();
        isTablet = getResources().getBoolean(R.bool.is_tablet);

        if (fragmentManager.findFragmentByTag(TAG_DETAIL_FRAGMENT) == null) {
            Fragment fragment = RecipeDetailFragment.newInstance(mRecipe);
            fragmentManager.beginTransaction().replace(R.id.detail_overview_container, fragment, TAG_DETAIL_FRAGMENT).commit();
        }
        spawnStepFragment(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipe != null) {
            outState.putParcelable(MainActivity.KEY_RECIPE, mRecipe);
        }
    }

    @Override
    public void onStepSelected(Recipe recipe, int index) {
        if (isTablet) {
            spawnStepFragment(index);
        } else {
            Intent intent = new Intent(RecipeDetailActivity.this, StepDetailActivity.class);
            intent.putExtra(MainActivity.KEY_RECIPE, recipe);
            intent.putExtra(MainActivity.KEY_RECIPE_STEP_INDEX, index);
            startActivity(intent);
        }
    }

    private void spawnStepFragment(int stepIndex) {
        String tag = String.valueOf(mRecipe.getSteps().get(stepIndex).hashCode());
        if (fragmentManager.findFragmentByTag(tag) == null) {
            Fragment fragment = StepDetailFragment.newInstance(mRecipe, stepIndex);
            fragmentManager.beginTransaction().replace(R.id.detail_step_container, fragment, tag).commit();
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
