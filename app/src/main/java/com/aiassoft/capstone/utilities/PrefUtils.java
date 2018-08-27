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

package com.aiassoft.capstone.utilities;

import android.content.SharedPreferences;

import com.aiassoft.capstone.MyApp;

import static android.content.Context.MODE_PRIVATE;

/*
  Created by gvryn on 14/08/18.
 */

/** This class has all the methods for the app preferences
 */
public final class PrefUtils {
    private static final String LOG_TAG = MyApp.APP_TAG + PrefUtils.class.getSimpleName();

    /**
     * Set the Shared Preferences File Name
     * and Key Name
     */
    public static final String PREFS_NAME = "com.aiassoft.capstone";
    public static final int INVALID_INT = -1;

    public static final String WIDGET_VEHICLE_ID = "VEHICLE";
    public static final String READ_EXTERNAL_STORAGE_GRANTED = "READ_EXTERNAL_STORAGE_GRANTED";

    private static boolean mReadExternalStorageGranted;

    private PrefUtils() {
        throw new AssertionError("No Instances for you!");
    }

    /**
     * sets a boolean value to the preferences
     * @param key the key to save the value
     * @param value the value for that key
     */
    public static void setBoolPref(String key, boolean value) {
        SharedPreferences sp = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(key, value);
        ed.apply();
    }

    /**
     * reads a boolean value from the preferences
     * @param key the key
     * @param missingValue the value if key is missing
     * @return the save value for that key
     */
    public static boolean getBoolPref(String key, boolean missingValue) {
        SharedPreferences sp = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sp.getBoolean(key, missingValue);
    }

    /**
     * for each widget, save the vehicle id, so we can populate the data
     * without to have to ask the user every time
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
     * Saves temporary the stored preferences except the preferences for the widgets
     */
    private static void prefSaveExceptWidgets() {
        mReadExternalStorageGranted = getBoolPref(READ_EXTERNAL_STORAGE_GRANTED, true);
    }

    /**
     * writes the temporary stored preferences back to the ap  preferences file
     */
    private static void prefRestoreExceptWidgets() {
        setBoolPref(READ_EXTERNAL_STORAGE_GRANTED, mReadExternalStorageGranted);
    }

    /**
     * clears all widget stored data from the app preferences file
     * first it saves other preferences to the memory
     * then it deletes the whole preference file
     * finally stores the temporary saved data back to the app preferences file
     */
    public static void prefClearWidgets() {
        prefSaveExceptWidgets();
        SharedPreferences sp = MyApp.getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        sp.edit().clear().apply();
        prefRestoreExceptWidgets();
    }

}
