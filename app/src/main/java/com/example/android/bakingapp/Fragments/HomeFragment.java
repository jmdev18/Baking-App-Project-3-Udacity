package com.example.android.bakingapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.Activities.DetailsActivity;
import com.example.android.bakingapp.Activities.MainActivity;
import com.example.android.bakingapp.Adapters.RecipesMainAdapter;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipesData.Recipe;
import com.example.android.bakingapp.Utils.NetworkUtils;

import java.io.IOException;

import static com.example.android.bakingapp.Utils.NetworkUtils.RECIPES_INTERNET_LOADER_ID;
import static com.example.android.bakingapp.Utils.RecipeDataUtils.fillRecipesArray;

/**
 * Main Fragment to display recipes
 */

public class HomeFragment extends Fragment implements RecipesMainAdapter.RecipeAdapterOnClickHandler {

    /*
     * Fields
     */

    private View mRootView;

    /*
     * Methods
     */

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.home_fragment, container, false);
        fetchRecipesFromInternet(getActivity(), getLoaderManager());

        return mRootView;
    }

    /**
     * Sets the Movie Adapter for the main layout that will contain movie posters
     */
    private void setMainActivityAdapter() {

        RecyclerView mainListRecyclerView = (RecyclerView) mRootView.findViewById(R.id.main_recipes_grid_layout);

        // Layout Manager
        setMainActivityLayoutManager(mainListRecyclerView);

        // Create and set the adapter
        RecipesMainAdapter recipesMainAdapter = new RecipesMainAdapter(getActivity(), RecipesListFragment.mRecipesArray, this);

        if (RecipesListFragment.mRecipesArray.size() > 0) {
            mainListRecyclerView.setAdapter(recipesMainAdapter);
        }
    }

    /**
     * Sets a grid layout manager
     */
    private void setMainActivityLayoutManager(RecyclerView recyclerView) {

        // Create and apply the layout manager
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);
    }

    @Override
    public void onClick(Recipe recipe) {

        if(MainActivity.mTabletLayout) {

            DetailsFragment detailsFragment = new DetailsFragment();
            detailsFragment.setRecipeSelected(recipe);

            getFragmentManager().beginTransaction()
                    .replace(R.id.recipe_details_view, detailsFragment)
                    .commit();
        } else {
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra("recipeObject", recipe);
            startActivity(intent);
        }
    }

    /**
     * Loads the recipes
     */
    public class RecipesInternetLoader implements LoaderManager.LoaderCallbacks<String> {

        private Context mContext;

        public RecipesInternetLoader(Context context) {
            mContext = context;
        }

        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<String>(mContext) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (RecipesListFragment.mRecipesArray.size() == 0) {
                        forceLoad();
                    } else {
                        setMainActivityAdapter();
                    }
                }

                @Override
                public String loadInBackground() {
                    try {
                        return NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildSearchUrl(NetworkUtils.RECIPES_SEARCH_URL));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {
            fillRecipesArray(data);
            setMainActivityAdapter();
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {

        }
    }

    /**
     * Fetched recipes from the internet by starting the corresponding loader
     *
     * @param context       The context
     * @param loaderManager The loaderManager to determine if the loader already exists
     */
    public void fetchRecipesFromInternet(Context context, LoaderManager loaderManager) {
        Loader<String> searchLoader = loaderManager.getLoader(RECIPES_INTERNET_LOADER_ID);

        if (searchLoader == null) {
            loaderManager.initLoader(RECIPES_INTERNET_LOADER_ID, null, new RecipesInternetLoader(context));
        } else {
            loaderManager.restartLoader(RECIPES_INTERNET_LOADER_ID, null, new RecipesInternetLoader(context));
        }
    }
}