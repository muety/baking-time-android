package com.github.n1try.bakingtime.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.Recipe;
import com.github.n1try.bakingtime.model.RecipeStep;
import com.github.n1try.bakingtime.utils.BasicUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepDetailFragment extends Fragment {
    @BindView(R.id.next_step_fab)
    FloatingActionButton nextFab;
    @BindView(R.id.prev_step_fab)
    FloatingActionButton prevFab;
    @BindView(R.id.step_instructions_tv)
    TextView stepInstructions;

    private Recipe mRecipe;
    private int mStepIndex;

    public static StepDetailFragment newInstance(Recipe recipe, int stepIndex) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainActivity.KEY_RECIPE_ID, recipe);
        bundle.putInt(MainActivity.KEY_RECIPE_STEP_INDEX, stepIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipe = getArguments().getParcelable(MainActivity.KEY_RECIPE_ID);
        mStepIndex = getArguments().getInt(MainActivity.KEY_RECIPE_STEP_INDEX);
        getActivity().setTitle(BasicUtils.styleTitle(mRecipe.getName() + " - Step " + mStepIndex));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, view);

        RecipeStep step = mRecipe.getSteps().get(mStepIndex);
        stepInstructions.setText(step.getDescription());

        return view;
    }
}
