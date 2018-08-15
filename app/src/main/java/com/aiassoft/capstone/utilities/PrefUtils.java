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

package com.aiassoft.capstone.utilities;

import android.content.SharedPreferences;

import com.aiassoft.capstone.Const;
import com.aiassoft.capstone.MyApp;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gvryn on 14/08/18.
 */

/** for each widget save the vehicle id, so we can populate the data
 * with out to have to ask the user every time
 */
public final class PrefUtils {
    private static final String LOG_TAG = MyApp.APP_TAG + PrefUtils.class.getSimpleName();

    /**
     * Set the Shared Preferences File Name
     * and Key Name
     */
    public static final String PREFS_NAME = "com.aiassoft.capstone";
    public static final String WIDGET_VEHICLE_ID = "VEHICLE";
	public static final int INVALID_INT = -1;

    private PrefUtils() {
        throw new AssertionError("No Instances for you!");
    }

    /**
     * Save the vehicle id to the widget
     * @param widgetId the widget id
     * @param vehicleId the vehicle id
     */
    public static void setWidgetVehicleId(int widgetId, int vehicleId) {
        SharedPreferences sp = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(WIDGET_VEHICLE_ID + widgetId, vehicleId);
        ed.apply();
    }

    /**
     * return the vehicle id for a widget
     * @param widgetId the widget id
     * @return the vehicle id
     */
    public static int getWidgetVehicleId(int widgetId) {
        SharedPreferences sp = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sp.getInt(WIDGET_VEHICLE_ID + widgetId, INVALID_INT);
    }

    /**
     * removes from shared preferences the widget with id
     * @param widgetId the if from the widget to be removed
     */
    public static void rmWidgetVehicleId(int widgetId) {
        SharedPreferences sp = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        sp.edit().remove(WIDGET_VEHICLE_ID + widgetId).apply();
    }

    /**
     * clears the whole shared preferences memory for the PREFS_NAME
     */
    public static void clearWidgets() {
        SharedPreferences sp = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        sp.edit().clear().apply();
    }

}
