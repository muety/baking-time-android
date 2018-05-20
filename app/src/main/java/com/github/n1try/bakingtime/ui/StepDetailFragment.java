package com.github.n1try.bakingtime.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import butterknife.OnClick;

public class StepDetailFragment extends Fragment {
    @BindView(R.id.next_step_fab)
    FloatingActionButton nextFab;
    @BindView(R.id.prev_step_fab)
    FloatingActionButton prevFab;
    @BindView(R.id.step_instructions_title_tv)
    TextView stepInstructionsTitle;
    @BindView(R.id.step_instructions_tv)
    TextView stepInstructions;

    private Recipe mRecipe;
    private int mStepIndex;
    private OnRecipeStepChangeListener mOnRecipeStepChangeListener;
    private boolean isTablet;


    public static StepDetailFragment newInstance(Recipe recipe, int stepIndex) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MainActivity.KEY_RECIPE, recipe);
        bundle.putInt(MainActivity.KEY_RECIPE_STEP_INDEX, stepIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipe = getArguments().getParcelable(MainActivity.KEY_RECIPE);
        mStepIndex = getArguments().getInt(MainActivity.KEY_RECIPE_STEP_INDEX);
        isTablet = getResources().getBoolean(R.bool.is_tablet);
        getActivity().setTitle(BasicUtils.styleTitle(mRecipe.getName() + " - Step " + (mStepIndex + 1)));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, view);

        RecipeStep step = mRecipe.getSteps().get(mStepIndex);
        stepInstructions.setText(step.getDescription());
        stepInstructionsTitle.setText(step.getShortDescription());

        if (mStepIndex == 0 || isTablet) prevFab.setVisibility(View.GONE);
        if (mStepIndex == mRecipe.getSteps().size() - 1 || isTablet) nextFab.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            mOnRecipeStepChangeListener = (OnRecipeStepChangeListener) getActivity();
        } catch (ClassCastException e) {
            Log.w(getTag(), "Could not bind OnRecipeStepChangeListener to fragment");
        }
    }

    @OnClick(R.id.next_step_fab)
    void nextStep() {
        mOnRecipeStepChangeListener.onNextStep(mStepIndex);
    }

    @OnClick(R.id.prev_step_fab)
    void prevStep() {
        mOnRecipeStepChangeListener.onPreviousStep(mStepIndex);
    }

    interface OnRecipeStepChangeListener {
        void onNextStep(int currentStepIndex);
        void onPreviousStep(int currentStepIndex);
    }
}
