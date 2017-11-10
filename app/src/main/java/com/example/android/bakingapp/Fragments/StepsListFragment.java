package com.example.android.bakingapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.Activities.DetailsActivity;
import com.example.android.bakingapp.Adapters.StepsListAdapter;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipesData.Ingredient;
import com.example.android.bakingapp.RecipesData.Recipe;
import com.example.android.bakingapp.RecipesData.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.android.bakingapp.Activities.MainActivity.mTabletLayout;

/**
 *
 */

public class StepsListFragment extends Fragment implements StepsListAdapter.StepOnClickHandler,
                                                            StepsListAdapter.IngredientOnClickHandler {

    /*
     * Views
     */

    @BindView(R.id.steps_list_recycler_view) RecyclerView mStepListRecyclerView;

    /*
     * Fields
     */

    private Recipe mRecipeSelected;
    private static final String RECIPE_KEY = "recipe_key";

    private Unbinder unbinder;
    private View mRootView;

    /*
     * Methods
     */

    /**
     * Creates a new instance of a StepsListFragment
     *
     * @param recipe The recipe selected by the user
     *
     * @return An instance of StepListFragment
     */
    public static StepsListFragment newInstance(Recipe recipe) {

        StepsListFragment fragment = new StepsListFragment();

        Bundle args = new Bundle();

        // Add arguments to the fragment
        args.putParcelable(RECIPE_KEY, recipe);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Recipe recipeSelected = getArguments().getParcelable(RECIPE_KEY);
        if(recipeSelected != null) {
            mRecipeSelected = recipeSelected;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.steps_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, mRootView);

        setStepsAdapter(mStepListRecyclerView);
        return mRootView;
    }

    /**
     * Sets up the steps adapter and a divider for its items
     *
     * @param rootView The root view that will be populated
     */
    private void setStepsAdapter(RecyclerView rootView) {

        // Create and apply the layout manager
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        rootView.setLayoutManager(mLinearLayoutManager);

        rootView.setAdapter(new StepsListAdapter(getActivity(),
                mRecipeSelected.getRecipeSteps(),
                mRecipeSelected.getRecipeIngredients(),
                this,
                this));

        // Add a divider decoration
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rootView.getContext(),
                mLinearLayoutManager.getOrientation());
        rootView.addItemDecoration(mDividerItemDecoration);
    }

    @Override
    public void onClick(Step step, int position) {

        if(mTabletLayout) {
            StepFragment stepFragment = StepFragment.newInstance(step);

            getFragmentManager().beginTransaction()
                    .replace(R.id.step_details_frame_layout, stepFragment)
                    .commit();
        } else {
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra("recipeObject", mRecipeSelected);
            intent.putExtra("tabPosition", position);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(ArrayList<Ingredient> ingredient, int position) {

        if(mTabletLayout) {
            IngredientsFragment ingredientsFragment = IngredientsFragment.newInstance(mRecipeSelected.getRecipeName(),
                    mRecipeSelected.getRecipeIngredients(),
                    mRecipeSelected.getRecipeImage());

            getFragmentManager().beginTransaction()
                    .replace(R.id.step_details_frame_layout, ingredientsFragment)
                    .commit();
        } else {
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra("recipeObject", mRecipeSelected);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
