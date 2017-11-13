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
import com.example.android.bakingapp.Fragments.StepsListFragment;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipesData.Recipe;
import com.example.android.bakingapp.Utils.FavoritesUtils;
import com.example.android.bakingapp.Widgets.WidgetUtils;

import java.util.HashSet;

import static com.example.android.bakingapp.Activities.MainActivity.mTabletLayout;

/**
 * Details activity for phone layout
 */

public class DetailsActivity extends AppCompatActivity {

    /*
     * Constants
     */

    private static final String TAG_DETAILS_FRAGMENT = "DetailsFragment";


    /*
     * Fields
     */

    private Recipe mRecipeSelected;
    private int mTabPosition;
    private DetailsFragment mDetailsFragment;
    private Menu mMenu;

    /*
     * Methods
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        // Remove action bar shadow and elevation
        getSupportActionBar().setElevation(0);

        if (getIntent().hasExtra("recipeObject")) {
            mRecipeSelected = getIntent().getExtras().getParcelable("recipeObject");
            setTitle(getString(R.string.app_name) + " - " + mRecipeSelected.getRecipeName());
        }

        // For phone layouts
        if (getIntent().hasExtra("tabPosition")) {
            mTabPosition = getIntent().getExtras().getInt("tabPosition");
        }

        if (mTabletLayout) {
            setupStepsFragmentsForTablet();
        } else {
            setupDetailsFragmentForPhone();
            showUpButton();
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
     * Creates and assigns the details fragment for a fragments layout
     */
    private void setupStepsFragmentsForTablet() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Display the list of steps
        StepsListFragment stepsListFragment = StepsListFragment.newInstance(mRecipeSelected);

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
    }

    /*
     * Displays an Up button
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
        mMenu.findItem(R.id.menu_widget).setChecked(WidgetUtils.isRecipeWidget(this, mRecipeSelected.getRecipeName()));
        setMenuFavoriteIcon();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
     * Sets the corresponding favorite icon (empty heart if the recipe is not favorite,
     * white heart if it is favorite)
     */
    private void setMenuFavoriteIcon() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.contains(FavoritesUtils.SHARED_PREFERENCES_FAV_RECIPES_KEY)) {

            HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet(FavoritesUtils.SHARED_PREFERENCES_FAV_RECIPES_KEY, null);

            if (set.contains(mRecipeSelected.getRecipeName())) {
                mMenu.findItem(R.id.favorite_button).setIcon(ContextCompat.getDrawable(this, R.drawable.favorite_selected));
            } else {
                mMenu.findItem(R.id.favorite_button).setIcon(ContextCompat.getDrawable(this, R.drawable.favorite_not_selected));
            }
        }
    }
}
