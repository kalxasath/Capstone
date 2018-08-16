/**
 * Copyright (C) 2018 by George Vrynios
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

package com.aiassoft.capstone.remote_views;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.model.VehiclesTotalRunningCosts;


import static com.aiassoft.capstone.data.DBQueries.fetchVehiclesTotalRunningCosts;
import static com.aiassoft.capstone.utilities.PrefUtils.getWidgetVehicleId;

/**
 * Created by gvryn on 15/08/18.
 */

/**
 * Populates the listview in the widget with the vehicle's costs
 */
public class ListviewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = MyApp.APP_TAG + ListviewRemoteViewsFactory.class.getSimpleName();

    private Context mContext;

    private VehiclesTotalRunningCosts mVehiclesTotalRunningCosts;


    public ListviewRemoteViewsFactory(Context applicationContext, Intent intent) {

        mContext = applicationContext;


        /** Ger widget's Id */
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

        /** Get widget's vehicleId */
        int vehicleId = getWidgetVehicleId(appWidgetId);

        /** Get Vehicle's total running costs */
        mVehiclesTotalRunningCosts = fetchVehiclesTotalRunningCosts(vehicleId);
        Toast.makeText(mContext, "Vehicle " + mVehiclesTotalRunningCosts.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {

    }

    /** called on start and when notifyAppWidgetViewDataChanged is called */
    @Override
    public void onDataSetChanged() {
//        mIngredientsData = null;
//
//        if (! MyApp.hasData() || mRecipePosition == Const.INVALID_INT) {
//            return;
//        }
//
//        mIngredientsData = MyApp.mRecipesData.get(mRecipePosition).getIngredients();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        /** we have data for one vehicle, so we always return 1 */
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Construct a RemoteViews item based on the app widget item XML file, and set the
        // text based on the position.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_list_widget);

        rv.setTextViewText(R.id.km_driven_title, String.valueOf(mVehiclesTotalRunningCosts.getKmDriven()));

        // Return the RemoteViews object.
        return rv;
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
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
