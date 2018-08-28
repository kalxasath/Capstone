/*
 * Copyright (C) 2018 by George Vrynios
 *
 * Capstone final project
 *
 * This project was made under the supervision of Udacity
 * in the Android Developer Nanodegree Program
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aiassoft.capstone.navigation;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.aiassoft.capstone.R;
import com.aiassoft.capstone.activities.DashboardActivity;
import com.aiassoft.capstone.activities.ExpensesListActivity;
import com.aiassoft.capstone.activities.SearchYoutubeActivity;
import com.aiassoft.capstone.activities.VehiclesListActivity;
import com.aiassoft.capstone.data.CapstoneDBHelper;
import com.google.firebase.analytics.FirebaseAnalytics;

import static com.aiassoft.capstone.MyApp.getContext;
import static com.aiassoft.capstone.utilities.TestUtils.insertFakeData;

/**
 * Created by gvryn on 25/07/18.
 */

/**
 * is used for all drawer activities to navigate around of them
 */
public class DrawerMenu {

    private DrawerMenu() {
        throw new AssertionError(R.string.no_instances_for_you);
    }

    static FirebaseAnalytics mFirebaseAnalytics;

    /**
     * Handle navigation view item clicks here.
     * @param context
     * @param item
     * @param view
     * @return
     */
    public static boolean navigate(Context context, MenuItem item, View view) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        Bundle bundle = new Bundle();
        Intent intent;
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                // Handle the dashboard action
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, context.getString(R.string.ga_dashboard));
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                intent = new Intent(context, DashboardActivity.class);
                context.startActivity(intent);
                return true;
            case R.id.nav_vehicles_list:
                // Handle the vehicles list action
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, context.getString(R.string.ga_vehicle_list));
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                intent = new Intent(context, VehiclesListActivity.class);
                context.startActivity(intent);
                return true;
            case R.id.nav_expenses_list:
                // Handle the expenses list action
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, context.getString(R.string.ga_expenses_list));
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                intent = new Intent(context, ExpensesListActivity.class);
                context.startActivity(intent);
                return true;
            case R.id.nav_search_youtube:
                // Handle the search youtube action
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, context.getString(R.string.ga_youtube_search));
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                intent = new Intent(context, SearchYoutubeActivity.class);
                context.startActivity(intent);
                return true;
            case R.id.nav_db_fill_fake_data:
                CapstoneDBHelper mDBHelper;

                mDBHelper = new CapstoneDBHelper(getContext());

                final SQLiteDatabase db = mDBHelper.getWritableDatabase();

                insertFakeData(db, view);
                return false;
        }

        return false;
    }

}
