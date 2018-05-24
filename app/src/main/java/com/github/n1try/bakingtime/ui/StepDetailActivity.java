package com.github.n1try.bakingtime.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.utils.Constants;

public class StepDetailActivity extends AppCompatActivity implements StepDetailFragment.OnRecipeStepChangeListener {
    private Recipe mRecipe;
    private int mStepIndex;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        Bundle bundle = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        mRecipe = bundle.getParcelable(Constants.KEY_RECIPE);
        mStepIndex = bundle.getInt(Constants.KEY_RECIPE_STEP_INDEX, 0);

        fragmentManager = getSupportFragmentManager();
        spawnFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.KEY_RECIPE, mRecipe);
        outState.putInt(Constants.KEY_RECIPE_STEP_INDEX, mStepIndex);
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
