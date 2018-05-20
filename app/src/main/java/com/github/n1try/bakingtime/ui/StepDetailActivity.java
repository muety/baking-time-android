package com.github.n1try.bakingtime.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;

public class StepDetailActivity extends AppCompatActivity {
    private static final String TAG_STEP_DETAIL_FRAGMENT = "step_detail_fragment";
    private Recipe mRecipe;
    private int mStepIndex;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        mRecipe = getIntent().getParcelableExtra(MainActivity.KEY_RECIPE_ID);
        mStepIndex = getIntent().getIntExtra(MainActivity.KEY_RECIPE_STEP_INDEX, 0);

        fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentByTag(TAG_STEP_DETAIL_FRAGMENT) == null) {
            Fragment fragment = StepDetailFragment.newInstance(mRecipe, mStepIndex);
            fragmentManager.beginTransaction().replace(R.id.detail_step_container, fragment, TAG_STEP_DETAIL_FRAGMENT).commit();
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
}
