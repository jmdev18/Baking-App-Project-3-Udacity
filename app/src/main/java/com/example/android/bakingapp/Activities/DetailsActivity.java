package com.example.android.bakingapp.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.bakingapp.Fragments.DetailsFragment;
import com.example.android.bakingapp.Fragments.IngredientsFragment;
import com.example.android.bakingapp.Fragments.StepFragment;
import com.example.android.bakingapp.Fragments.StepsListFragment;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipesData.Recipe;
import com.example.android.bakingapp.Utils.FavoritesUtils;
import com.example.android.bakingapp.Widgets.WidgetUtils;

import java.util.HashSet;

/**
 * Details activity for phone layout
 */

public class DetailsActivity extends AppCompatActivity {

    /*
     * Constants
     */

    private static final String TAG_DETAILS_FRAGMENT = "DetailsFragment";
    private static final String IS_TABLET_LAYOUT_INTENT_KEY = "isTabletLayout";
    private static final String RECIPE_OBJECT_INTENT_KEY = "recipeObject";
    private static final String TAB_POSITION_INTENT_KEY = "tabPosition";

    // Fragment
    private StepFragment mStepFragment;


    /*
     * Fields
     */


    // Recipe
    private Recipe mRecipeSelected;

    // Activity
    private int mTabPosition;
    private DetailsFragment mDetailsFragment;
    public Menu mMenu;
    private boolean mIsTabletLayout;
    private boolean mIsFavorite;

    // Favorites
    public int mFavoriteIconSelectedId;


    /*
     * Methods
     */


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        // Remove action bar shadow and elevation
        getSupportActionBar().setElevation(0);

        // Get extras
        if(getIntent().getExtras().containsKey(IS_TABLET_LAYOUT_INTENT_KEY)) {
            mIsTabletLayout = getIntent().getExtras().getBoolean(IS_TABLET_LAYOUT_INTENT_KEY);
        }

        if (getIntent().hasExtra(RECIPE_OBJECT_INTENT_KEY)) {
            mRecipeSelected = getIntent().getExtras().getParcelable(RECIPE_OBJECT_INTENT_KEY);
            setTitle(getString(R.string.app_name) + " - " + mRecipeSelected.getRecipeName());
        }

        // Up navigation
        showUpButton();

        // Set up the correct layout
        if (mIsTabletLayout) {
            setupStepsFragmentsForTablet();
        } else {

            // For phone layouts
            if (getIntent().hasExtra(TAB_POSITION_INTENT_KEY)) {
                mTabPosition = getIntent().getExtras().getInt(TAB_POSITION_INTENT_KEY);
            }
            setupDetailsFragmentForPhone();
        }
    }

    /**
     * Creates and assigns the details fragment for a phone layout
     */
    private void setupDetailsFragmentForPhone() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        mDetailsFragment = (DetailsFragment) fragmentManager.findFragmentByTag(TAG_DETAILS_FRAGMENT);

        if (mDetailsFragment == null) {

            mDetailsFragment = DetailsFragment.newInstance(mTabPosition);
            mDetailsFragment.setRecipeSelected(mRecipeSelected);

            fragmentManager.beginTransaction()
                    .add(R.id.details_fragment_container, mDetailsFragment, TAG_DETAILS_FRAGMENT)
                    .commit();
        } else {

            fragmentManager.beginTransaction()
                    .replace(R.id.details_fragment_container, mDetailsFragment, TAG_DETAILS_FRAGMENT)
                    .commit();
        }
    }

    /*
     * Creates and assigns the details fragment for a tablet layout
     */
    private void setupStepsFragmentsForTablet() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mStepFragment = (StepFragment) fragmentManager.findFragmentByTag(StepsListFragment.STEP_FRAGMENT_UNIQUE_ID);

        if(mStepFragment == null) {
            // Display the list of steps
            StepsListFragment stepsListFragment = StepsListFragment.newInstance(mRecipeSelected);
            stepsListFragment.setIsTabletLayout(mIsTabletLayout);

            fragmentManager.beginTransaction()
                    .add(R.id.steps_list_fragment_view, stepsListFragment)
                    .commit();

            // By default, display the ingredients details first
            IngredientsFragment ingredientsFragment = IngredientsFragment.newInstance(mRecipeSelected.getRecipeName(),
                    mRecipeSelected.getRecipeIngredients(),
                    mRecipeSelected.getRecipeImage());

            fragmentManager.beginTransaction()
                    .replace(R.id.step_details_frame_layout, ingredientsFragment)
                    .commit();
        } else {

            fragmentManager.beginTransaction()
                    .replace(R.id.step_details_frame_layout, mStepFragment, StepsListFragment.STEP_FRAGMENT_UNIQUE_ID)
                    .commit();
        }

    }

    /*
     * Displays an Up button on an action bar
     */
    private void showUpButton() {
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    /*
     * Menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        mMenu = menu;

        // Set checkbox if the recipe is currently the widget
        mMenu.findItem(R.id.menu_widget).setChecked(WidgetUtils.isRecipeWidget(this, mRecipeSelected.getRecipeName()));

        // Set the action bar favorite icon and drawable id
        mIsFavorite = setMenuFavoriteIcon();
        setFavoriteIconSelectedId();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // React accordingly to item selected
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        } else if (id == R.id.menu_widget) {
            item.setChecked(!item.isChecked());
            WidgetUtils.updateWidgetsData(this, mRecipeSelected);
        } else if (id == R.id.favorite_button) {
            FavoritesUtils.toggleFavoriteRecipe(this, mRecipeSelected);
            setMenuFavoriteIcon();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the corresponding favorite icon (empty heart if the recipe
     * is not selected as favorite, white heart if it is selected as favorite)
     */
    private boolean setMenuFavoriteIcon() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.contains(FavoritesUtils.SHARED_PREFERENCES_FAV_RECIPES_KEY)) {
            HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet(FavoritesUtils.SHARED_PREFERENCES_FAV_RECIPES_KEY, null);

            if (set.contains(mRecipeSelected.getRecipeName())) {
                setFavoriteIconDrawable(R.drawable.favorite_selected);
                return true;
            } else {
                setFavoriteIconDrawable(R.drawable.favorite_not_selected);
                return false;
            }
        }

        return false;
    }

    /*
     * Sets the favorite icon id depending of whether the recipe was
     * selected as favorite or not
     */
    private void setFavoriteIconSelectedId() {
        if (mIsFavorite) {
            mFavoriteIconSelectedId = R.drawable.favorite_selected;
        } else {
            mFavoriteIconSelectedId = R.drawable.favorite_not_selected;
        }
    }

    /*
     * Sets the corresponding icon drawable for the Action Bar's favorite button
     */
    private void setFavoriteIconDrawable(int drawableResourceId) {
        mMenu.findItem(R.id.favorite_button).setIcon(ContextCompat.getDrawable(this, drawableResourceId));
        mFavoriteIconSelectedId = drawableResourceId;
    }
}
