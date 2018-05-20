package com.github.n1try.bakingtime.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.model.RecipeIngredient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailOverviewFragment extends Fragment {
    @BindView(R.id.ingredients_tv)
    TextView ingredientsText;
    @BindView(R.id.steps_gv)
    GridView stepsList;

    private Recipe mRecipe;

    public static DetailOverviewFragment newInstance(Recipe recipe) {
        DetailOverviewFragment fragment = new DetailOverviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainActivity.KEY_RECIPE_ID, recipe);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipe = getArguments().getParcelable(MainActivity.KEY_RECIPE_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_overview, container, false);
        ButterKnife.bind(this, view);

        ingredientsText.setText(TextUtils.concat((CharSequence[]) mRecipe.getIngredients().stream().map(RecipeIngredient::format).toArray(i -> new CharSequence[i])));

        return view;
    }
}
