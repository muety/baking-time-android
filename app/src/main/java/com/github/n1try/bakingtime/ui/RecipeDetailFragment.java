package com.github.n1try.bakingtime.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.model.RecipeIngredient;
import com.github.n1try.bakingtime.utils.BasicUtils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class RecipeDetailFragment extends Fragment {
    @BindView(R.id.ingredients_tv)
    TextView ingredientsText;
    @BindView(R.id.steps_gv)
    GridView stepsList;

    private Recipe mRecipe;
    private ArrayAdapter<String> mStepsAdapter;
    private OnRecipeStepSelectedListener onStepSelectedListener;

    public static RecipeDetailFragment newInstance(Recipe recipe) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainActivity.KEY_RECIPE_ID, recipe);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            onStepSelectedListener = (OnRecipeStepSelectedListener) getActivity();
        } catch (ClassCastException e) {
            Log.w(getTag(), "Could not bind OnRecipeStepSelectedListener to fragment");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipe = getArguments().getParcelable(MainActivity.KEY_RECIPE_ID);
        mStepsAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                IntStream.range(0, mRecipe.getSteps().size())
                        .mapToObj(i -> Pair.create(i, mRecipe.getSteps().get(i)))
                        .map(p -> String.format("%s. %s", p.first, p.second.getShortDescription()))
                        .collect(Collectors.toList())
        );
        getActivity().setTitle(BasicUtils.styleTitle(mRecipe.getName()));
    }

    @OnItemClick(R.id.steps_gv)
    void onItemClick(int position) {
        if (onStepSelectedListener == null) return;
        onStepSelectedListener.onStepSelected(mRecipe, position);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_overview, container, false);
        ButterKnife.bind(this, view);

        ingredientsText.setText(TextUtils.concat((CharSequence[]) mRecipe.getIngredients().stream().map(RecipeIngredient::format).toArray(i -> new CharSequence[i])));
        stepsList.setAdapter(mStepsAdapter);

        return view;
    }

    interface OnRecipeStepSelectedListener {
        void onStepSelected(Recipe recipe, int index);
    }
}
