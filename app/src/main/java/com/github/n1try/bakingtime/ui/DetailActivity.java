package com.github.n1try.bakingtime.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.utils.BasicUtils;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG_DETAIL_FRAGMENT = "detail_fragment";
    private FragmentManager fragmentManager;
    private boolean isTablet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Recipe recipe = getIntent().getParcelableExtra(MainActivity.KEY_RECIPE_ID);
        fragmentManager = getSupportFragmentManager();
        isTablet = getResources().getBoolean(R.bool.is_tablet);

        setTitle(BasicUtils.styleTitle(recipe.getName()));
        if (fragmentManager.findFragmentByTag(TAG_DETAIL_FRAGMENT) == null) {
            Fragment detailOverviewFragment = DetailOverviewFragment.newInstance(recipe);
            fragmentManager.beginTransaction().replace(R.id.detail_overview_container, detailOverviewFragment, TAG_DETAIL_FRAGMENT).commit();
        }
    }
}
