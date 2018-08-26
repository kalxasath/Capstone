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

package com.aiassoft.capstone.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.services.VehicleWidgetUpdateService;

import static com.aiassoft.capstone.utilities.PrefUtils.prefClearWidgets;
import static com.aiassoft.capstone.utilities.PrefUtils.rmWidgetVehicleId;

/*
  Created by gvryn on 14/08/18.
 */

/**
 * Implementation of App Widget functionality.
 */
public class VehicleWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = MyApp.APP_TAG + VehicleWidgetProvider.class.getSimpleName();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        ComponentName thisWidget = new ComponentName(context, VehicleWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // Build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(), VehicleWidgetUpdateService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        // Update the widgets via the service
        context.startService(intent);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted (Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            rmWidgetVehicleId(appWidgetId);
        }
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        prefClearWidgets();
    }

    public static void sendRefreshBroadcast(Context context) {
//todo sendRefreshBroadcast
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName thisWidget = new ComponentName(context, VehicleWidgetProvider.class);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(thisWidget);

        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        intent.setComponent(thisWidget);
        context.sendBroadcast(intent);
    }

}

