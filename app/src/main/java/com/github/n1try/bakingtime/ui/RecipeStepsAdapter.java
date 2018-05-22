package com.github.n1try.bakingtime.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.n1try.bakingtime.R;
import com.github.n1try.bakingtime.model.RecipeStep;

import java.util.List;

public class RecipeStepsAdapter extends ArrayAdapter<RecipeStep> {
    private Context context;
    private List<RecipeStep> steps;
    private int activeIndex = -1;

    public RecipeStepsAdapter(@NonNull Context context, @NonNull List<RecipeStep> steps) {
        super(context, 0, steps);
        this.context = context;
        this.steps = steps;
    }

    public void setActiveIndex(int index) {
        activeIndex = index;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_step_item, parent, false);
        }

        RecipeStep step = getItem(position);
        View innerStepContainer = convertView.findViewById(R.id.inner_step_container);
        TextView stepTitle = convertView.findViewById(R.id.step_title_tv);
        TextView stepIndex = convertView.findViewById(R.id.step_index_tv);
        stepIndex.setText(String.valueOf(position + 1));
        stepTitle.setText(step.getShortDescription());

        if (position == activeIndex) {
            innerStepContainer.setBackground(context.getDrawable(R.drawable.rounded_box_filled));
        } else {
            innerStepContainer.setBackground(context.getDrawable(R.drawable.rounded_box));
        }

        return convertView;
    }
}
