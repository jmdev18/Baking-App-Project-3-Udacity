package com.example.android.bakingapp.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.android.bakingapp.Activities.DetailsActivity;
import com.example.android.bakingapp.Activities.MainActivity;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipesData.Recipe;
import com.squareup.picasso.Picasso;

import static com.example.android.bakingapp.Activities.MainActivity.mContext;

/**
 * Helper methods for widgets
 */

public class WidgetUtils {

    /*
     * Fields
     */

    public static final String SHARED_PREFERENCES_RECIPE_NAME_WIDGET_KEY = "recipeSelectedForWidgetName";
    public static final String SHARED_PREFERENCES_RECIPE_URL_WIDGET_KEY = "recipeSelectedForWidgetImageURL";
    public static final String SHARED_PREFERENCES_RECIPE_SERVINGS_WIDGET_KEY = "recipeSelectedForWidgetServings";

    /*
     * Methods
     */

    /**
     * Updates the widget's data and stores it in SharedPreferences
     *
     * @param context The context
     * @param recipeSelected The recipe selected by the user
     */
    public static void updateWidgetsData(Context context, Recipe recipeSelected) {

        // Get widget manager
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeWidgetProvider.class));

        // Toggle recipe name to shared preferences
        toggleWidgetSharedPreferences(context, recipeSelected.getRecipeName(),
                recipeSelected.getRecipeImage(),
                Integer.toString(recipeSelected.getRecipeServings()));

        RecipeWidgetProvider.updateAppWidgets(context, appWidgetManager, recipeSelected, appWidgetIds);
    }

    /**
     * Toggles the widget's data in SharedPreferences
     *
     * @param context The context
     * @param recipeName The recipe's name
     * @param recipeURL The recipe's URL
     * @param recipeServings The recipe's servings
     */
    private static void toggleWidgetSharedPreferences(Context context, String recipeName, String recipeURL, String recipeServings) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        togglePreference(sharedPreferences, editor, SHARED_PREFERENCES_RECIPE_NAME_WIDGET_KEY, recipeName);
        togglePreference(sharedPreferences, editor, SHARED_PREFERENCES_RECIPE_URL_WIDGET_KEY, recipeURL);
        togglePreference(sharedPreferences, editor, SHARED_PREFERENCES_RECIPE_SERVINGS_WIDGET_KEY, recipeServings);

        editor.apply();
    }

    /**
     * Toggles a single preference form SharedPreferences
     *
     * @param sharedPreferences An instance of SharedPreferences
     * @param editor The SharedPreferences editor
     * @param preferenceKey The preference key
     * @param preferenceValue The preference value
     */
    private static void togglePreference(SharedPreferences sharedPreferences, SharedPreferences.Editor editor,
                                         String preferenceKey, String preferenceValue) {
        if(sharedPreferences.contains(preferenceKey)) {
            editor.remove(preferenceKey);
        } else {
            editor.putString(preferenceKey, preferenceValue);
        }
    }

    /**
     * Sets the widget's User Interface
     *
     * @param context The context
     * @param views The remote views that will be updated
     * @param recipe The recipe selected by the user
     */
    public static void setWidgetUI(Context context, RemoteViews views, Recipe recipe) {

        // Data
        CharSequence widgetText = context.getString(R.string.appwidget_text);

        // UI
        views.setTextViewText(R.id.appwidget_text, recipe.getRecipeName());

        loadRecipeWidgetImage(context, views, recipe.getRecipeImage());

        views.setTextViewText(R.id.appwidget_servings, Integer.toString(recipe.getRecipeServings()));
    }

    /**
     * Adds the widget's on click listeners depending on whether the recipe object
     * is null or not. If it is null, clicking on the widget will launch the MainActivity.
     * Else, it will launch the details activity for the recipe selected
     *
     * @param context The context
     * @param views The remote views
     * @param recipeSelected The recipe selected
     */
    public static void addWidgetOnClickListeners(Context context, RemoteViews views, Recipe recipeSelected) {

        // If there is a recipe selected, launch the corresponding details activity
        if(recipeSelected != null) {
            // Create an Intent to launch DetailActivity when clicked
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("recipeObject", recipeSelected);
            intent.putExtra("tabPosition", 0);

            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    RecipeWidgetProvider.UNIQUE_INTENT_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.appwidget_main_layout, pendingIntent);
        // Else, if there is no recipe selected, launch the main activity
        } else {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.appwidget_main_layout, pendingIntent);
        }
    }

    /**
     * Displays a toast informing the user that the widget has been updated
     *
     * @param context The context
     */
    public static void displayWidgetUpdatedToast(Context context) {
        Toast.makeText(context, context.getString(R.string.widget_updated_toast_message), Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * Loads the widget's image from Picasso or from the drawables folder
     *
     * @param context The context
     * @param views The remote views that will be updated
     * @param recipeImage The recipe image URL in String format
     */
    private static void loadRecipeWidgetImage(Context context, RemoteViews views, String recipeImage) {

        if (recipeImage.substring(0, 6).equals("recipe")) {
            int resourceId = context.getResources().getIdentifier(recipeImage, "drawable", mContext.getPackageName());
            views.setImageViewResource(R.id.appwidget_image, resourceId);
        } else {

            // Get widget manager
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeWidgetProvider.class));

            Picasso.with(context).load(recipeImage)
                    .into(views, R.id.appwidget_image, appWidgetIds);
        }
    }
}