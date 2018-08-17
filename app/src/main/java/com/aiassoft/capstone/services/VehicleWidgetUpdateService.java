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

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.remote_views.ListviewRemoteViewsService;

import static com.aiassoft.capstone.data.DBQueries.fetchVehiclesName;
import static com.aiassoft.capstone.utilities.PrefUtils.getWidgetVehicleId;

/**
 * Created by gvryn on 14/08/18.
 */

/** This service updates the widgets with the appropriate recipe ingredients */
public class VehicleWidgetUpdateService extends Service {

    private static final String LOG_TAG = MyApp.APP_TAG + VehicleWidgetUpdateService.class.getSimpleName();

    public static final String EXTRA_DATA = "EXTRA_DATA";

    private static final int INVALID_INT = -1;

    private static Context mContext;

    private AppWidgetManager mAppWidgetManager;

    private int[] mAllWidgetIds;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = MyApp.getContext();
        mAppWidgetManager = AppWidgetManager.getInstance(this);

        mAllWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        mAppWidgetManager.notifyAppWidgetViewDataChanged(mAllWidgetIds, R.id.lv_widget);

        for (int appWidgetId : mAllWidgetIds) {
            /** Reaches the view on widget */
            RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget_vehicle_provider);

            /** Get widget's vehicleId */
            int vehicleId = getWidgetVehicleId(appWidgetId);

            if (vehicleId == INVALID_INT) {
                // If the app doesn't start because of bug, or developer stop
                // invalidate widgets since we do not have a corrected app instance
                showDeveloperInfo(rv);
            } else {

                hideDeveloperInfo(rv);

                updateRemoteViews(rv, appWidgetId);
            }

            mAppWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     *
     * @param rv the remote view
     */
    private void showDeveloperInfo(RemoteViews rv) {
        //TODO showDeveloperInfo
//        rv.setTextViewText(R.id.tv_recipe_title, mContext.getString(R.string.appwidget_title));
//        rv.setViewVisibility(R.id.tv_ingredients_title, View.GONE);
//        rv.setViewVisibility(R.id.iv_ingredients_separator, View.GONE);

//        rv.setViewVisibility(R.id.card_view, View.GONE);
//        rv.setEmptyView(R.id.card_view, R.id.empty_view);
//        rv.setTextViewText(R.id.empty_view, mContext.getString(R.string.you_broke_up_the_app));
    }

    private void hideDeveloperInfo(RemoteViews rv) {
        //TODO hideDeveloperInfo
//        rv.setViewVisibility(R.id.tv_ingredients_title, View.VISIBLE);
//        rv.setViewVisibility(R.id.iv_ingredients_separator, View.VISIBLE);

//        rv.setViewVisibility(R.id.card_view, View.VISIBLE);
//        rv.setViewVisibility(R.id.empty_view, View.GONE);
    }

    private void updateRemoteViews(RemoteViews rv, int appWidgetId) {
        /** Get widget's vehicleId */
        int vehicleId = getWidgetVehicleId(appWidgetId);

        rv.setTextViewText(R.id.vehicle_title, String.format(
                mContext.getString(R.string.total_running_costs), fetchVehiclesName(vehicleId)));

        /** Set up the RemoteViews object to use a RemoteViews adapter.
         This adapter connects
         to a RemoteViewsService through the specified intent.
         This is how you populate the data. */
        Intent setListviewRemoteViewsIntent;

        /** Set up the intent that starts the ListViewService, which will
         provide the views for this collection. */
        setListviewRemoteViewsIntent = new Intent(mContext, ListviewRemoteViewsService.class);

        /** Add the app widget ID & RecipePosition to the intent extras. */
        setListviewRemoteViewsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        Uri uri = Uri.parse(setListviewRemoteViewsIntent.toUri(Intent.URI_INTENT_SCHEME));
        setListviewRemoteViewsIntent.setData(uri);

        /** Finally set the remote Adapter */
        rv.setRemoteAdapter(R.id.lv_widget, setListviewRemoteViewsIntent);

        //setting an empty view in case of no data
        rv.setEmptyView(R.id.lv_widget, R.id.empty_view);
    }

}
