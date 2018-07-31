package com.aiassoft.capstone.navigation;

import android.content.Context;
import android.content.Intent;

import com.aiassoft.capstone.R;
import com.aiassoft.capstone.activities.DashboardActivity;
import com.aiassoft.capstone.activities.ExpensesListActivity;
import com.aiassoft.capstone.activities.SearchYoutubeActivity;
import com.aiassoft.capstone.activities.VehiclesListActivity;

/**
 * Created by gvryn on 25/07/18.
 */

public class DrawerMenu {
    /**
     * We never need to create an instance of the Const class
     * because the Const is simply a class filled,
     * with App related constants that are all static.
     */
    private DrawerMenu() {
        throw new AssertionError(R.string.no_instances_for_you);
    }

    /**
     * Handle navigation view item clicks here.
     * @param id
     */
    public static boolean navigate(Context context, int id) {
        Intent intent;

        switch (id) {
            case R.id.nav_dashboard:
                // Handle the dashboard action
                intent = new Intent(context, DashboardActivity.class);
                context.startActivity(intent);
                return true;
            case R.id.nav_vehicles_list:
                // Handle the vehicles list action
                intent = new Intent(context, VehiclesListActivity.class);
                context.startActivity(intent);
                return true;
            case R.id.nav_expenses_list:
                // Handle the expenses list action
                intent = new Intent(context, ExpensesListActivity.class);
                context.startActivity(intent);
                return true;
            case R.id.nav_search_youtube:
                // Handle the search youtube action
                intent = new Intent(context, SearchYoutubeActivity.class);
                context.startActivity(intent);
                return true;

        }

        return false;
    }

}
