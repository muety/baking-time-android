package com.github.n1try.bakingtime;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.github.n1try.bakingtime.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private IdlingRegistry mIdlingRegistry;
    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingRegistry = IdlingRegistry.getInstance();
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        mIdlingRegistry.register(mIdlingResource);
    }

    @Test
    public void clickRecipe_openDetails() {
        navigateToRecipe(0);
        onView(withId(R.id.ingredients_tv)).check(matches(withText(containsString("Graham Cracker"))));
        onData(anything()).inAdapterView(withId(R.id.steps_gv)).atPosition(0).onChildView(withId(R.id.step_index_tv)).check(matches(withText(is("1"))));
        onData(anything()).inAdapterView(withId(R.id.steps_gv)).atPosition(0).onChildView(withId(R.id.step_title_tv)).check(matches(withText(is("Recipe Introduction"))));
    }

    @Test
    public void clickStep_openInstructions() {
        navigateToRecipeStep(0, 1);
        onView(withId(R.id.step_thumbnail_iv)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.step_instructions_title_tv)).check(matches(withText(is("Starting prep"))));
        onView(withId(R.id.prev_step_fab)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.next_step_fab)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void clickNextStep() {
        navigateToRecipeStep(0, 0);
        onView(withId(R.id.prev_step_fab)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.next_step_fab)).perform(click());
        onView(withId(R.id.prev_step_fab)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.step_instructions_title_tv)).check(matches(withText(is("Starting prep"))));
    }

    private void navigateToRecipe(int idx) {
        onData(anything()).inAdapterView(withId(R.id.recipe_overview_gv)).atPosition(idx).perform(click());
    }

    private void navigateToRecipeStep(int recipeIdx, int stepIdx) {
        navigateToRecipe(recipeIdx);
        onData(anything()).inAdapterView(withId(R.id.steps_gv)).atPosition(stepIdx).perform(click());
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            mIdlingRegistry.unregister(mIdlingResource);
        }
    }
}
