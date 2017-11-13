package com.example.android.bakingapp.Widgets;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.Activities.MainActivity;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipesData.Ingredient;
import com.example.android.bakingapp.Utils.RecipeDataUtils;

import java.util.ArrayList;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * A Remove views service to return a new remote views factory for recipe ingredients displays on the widget
 */

public class IngredientsListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientsListRemoteFactory(this.getApplicationContext());
    }
}

class IngredientsListRemoteFactory implements RemoteViewsService.RemoteViewsFactory {

    /*
     * Fields
     */

    private Context mContext;
    private ArrayList<Ingredient> mIngredientsArrayList;

    /*
     * Methods
     */

    public IngredientsListRemoteFactory(Context context) {
        mContext = context;

        if(RecipeWidgetProvider.mRecipeSelected != null) {
            mIngredientsArrayList = RecipeWidgetProvider.mRecipeSelected.getRecipeIngredients();
        }
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if(RecipeWidgetProvider.mRecipeSelected != null) {
            mIngredientsArrayList = RecipeWidgetProvider.mRecipeSelected.getRecipeIngredients();
        } else {
            mIngredientsArrayList = null;
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(mIngredientsArrayList == null) return 0;
        return mIngredientsArrayList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if(mIngredientsArrayList == null || mIngredientsArrayList.size() == 0) return null;

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_item);
        Ingredient ingredient = mIngredientsArrayList.get(i);

        views.setTextViewText(R.id.ingredient_item_number, Integer.toString(i + 1));
        views.setTextViewText(R.id.ingredient_item_name, RecipeDataUtils.capitalizeString(ingredient.getIngredientName()));
        views.setTextViewText(R.id.ingredient_item_quantity_unit, ingredient.getIngredientQuantity() + " " + ingredient.getIngredientUnit());

        if(MainActivity.mTabletLayout) {

            views.setTextViewTextSize(R.id.ingredient_item_number, COMPLEX_UNIT_SP, 12);
            views.setTextViewTextSize(R.id.ingredient_item_name, COMPLEX_UNIT_SP, 18);
            views.setTextViewTextSize(R.id.ingredient_item_quantity_unit, COMPLEX_UNIT_SP, 16);

        } else {
            views.setTextViewTextSize(R.id.ingredient_item_number, COMPLEX_UNIT_SP, 12);
            views.setTextViewTextSize(R.id.ingredient_item_name, COMPLEX_UNIT_SP, 12);
            views.setTextViewTextSize(R.id.ingredient_item_quantity_unit, COMPLEX_UNIT_SP, 10);
        }

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
