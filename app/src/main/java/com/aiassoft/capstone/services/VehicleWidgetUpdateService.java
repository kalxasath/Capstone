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

package com.aiassoft.capstone.services;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.aiassoft.capstone.Const;
import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.data.CapstoneDBHelper;
import com.aiassoft.capstone.model.Dashboard;

import java.util.ArrayList;
import java.util.List;

import static com.aiassoft.capstone.activities.DashboardActivity.DASHBOARD_LOADER_ID;
import static com.aiassoft.capstone.utilities.PrefUtils.getWidgetVehicleId;

/**
 * Created by gvryn on 14/08/18.
 */

/** This service updates the widgets with the appropriate recipe ingredients */
public class VehicleWidgetUpdateService extends Service {

    private static final String LOG_TAG = MyApp.APP_TAG + VehicleWidgetUpdateService.class.getSimpleName();

    private static final int INVALID_INT = -1;

    private AppWidgetManager mAppWidgetManager;

    private int[] mAllWidgetIds;

    private Dashboard mVehiclesTotalRunningCosts;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAppWidgetManager = AppWidgetManager.getInstance(this);

        mAllWidgetIds = intent
                .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        mAppWidgetManager.notifyAppWidgetViewDataChanged(mAllWidgetIds, R.id.dashboard_list);

        for (int appWidgetId : mAllWidgetIds) {
            /** Reaches the view on widget */
            RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget_vehicle_provider);

            /** Get widgets vehicleId */
            int vehicleId = getWidgetVehicleId(appWidgetId);

            if (vehicleId == INVALID_INT) {
                // If the app doesn't start because of bug, or developer stop
                // invalidate widgets since we do not have acorrected app instance
                showDeveloperInfo(rv);
            } else {

                hideDeveloperInfo(rv);
                fetchVehiclesData();
                updateRemoteViews(rv, appWidgetId, vehicleId);
            }

            mAppWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    private void fetchVehiclesData() {

    }

    /**
     *
     * @param rv the remote view
     */
    private void showDeveloperInfo(RemoteViews rv) {
        //TODO showDeveloperInfo
//        rv.setTextViewText(R.id.tv_recipe_title, MyApp.getContext().getString(R.string.appwidget_title));
//        rv.setViewVisibility(R.id.tv_ingredients_title, View.GONE);
//        rv.setViewVisibility(R.id.iv_ingredients_separator, View.GONE);
        rv.setViewVisibility(R.id.dashboard_list, View.GONE);
        rv.setEmptyView(R.id.dashboard_list, R.id.broken_view);
    }

    private void hideDeveloperInfo(RemoteViews rv) {
        //TODO hideDeveloperInfo
//        rv.setViewVisibility(R.id.tv_ingredients_title, View.VISIBLE);
//        rv.setViewVisibility(R.id.iv_ingredients_separator, View.VISIBLE);
        rv.setViewVisibility(R.id.dashboard_list, View.VISIBLE);
        rv.setViewVisibility(R.id.broken_view, View.GONE);
    }

	// TODO: read and set vehicles data in async after selecting it, or here? Yes here this is a service
    private void updateRemoteViews(RemoteViews rv, int appWidgetId, int vehicleId) {
        //TODO updateRemoteViews
//TODO        rv.setTextViewText(R.id.tv_recipe_title, MyApp.mRecipesData.get(vehicleId).getName());

        /** Set up the RemoteViews object to use a RemoteViews adapter.
         This adapter connects
         to a RemoteViewsService through the specified intent.
         This is how you populate the data. */
        Intent setListviewRemoteViewsIntent;

        /** Set up the intent that starts the ListViewService, which will
         provide the views for this collection. */
//TODO        setListviewRemoteViewsIntent = new Intent(MyApp.getContext(), ListviewRemoteViewsService.class);

        /** Add the app widget ID & RecipePosition to the intent extras. */
//TODO        setListviewRemoteViewsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//TODO        setListviewRemoteViewsIntent.putExtra(Const.EXTRA_RECIPE_POS, vehicleId);
//TODO        setListviewRemoteViewsIntent.setData(Uri.parse(setListviewRemoteViewsIntent.toUri(Intent.URI_INTENT_SCHEME)));

        /** Finally set the remote Adapter */
//TODO        rv.setRemoteAdapter(R.id.dashboard_list, setListviewRemoteViewsIntent);

        //setting an empty view in case of no data
//TODO        rv.setEmptyView(R.id.dashboard_list, R.id.empty_view);
    }

    ///////// Loader



}
