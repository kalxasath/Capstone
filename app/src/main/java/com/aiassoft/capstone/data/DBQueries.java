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

package com.aiassoft.capstone.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.data.VehiclesContract.VehiclesEntry;
import com.aiassoft.capstone.model.VehiclesTotalRunningCosts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gvryn on 16/08/18.
 *
 * In this class will be reside the database queries
 */

public final class DBQueries {

    private static final String LOG_TAG = MyApp.APP_TAG + DBQueries.class.getSimpleName();

    private DBQueries() {
        throw new AssertionError(R.string.no_instances_for_you);
    }

    /**
     * Returns the vehicle name of a given vehicle id
     *
     * @param vehicleId The vehicle id
     * @return the name of the vehicle
     */
    public static final String fetchVehiclesName(int vehicleId) {
        String vehicleName = MyApp.getContext().getString(R.string.deleted_vehicle);

        Uri uri = VehiclesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(vehicleId+"").build();
        Cursor cVehicle = MyApp.getContext().getContentResolver().query(uri,
                new String[] {VehiclesEntry.COLUMN_NAME_NAME}, null, null, null);

        if (cVehicle != null && cVehicle.getCount() > 0) {
            cVehicle.moveToNext();
            vehicleName = cVehicle.getString(cVehicle.getColumnIndex(VehiclesEntry.COLUMN_NAME_NAME));
        }
        cVehicle.close();

        return vehicleName;
    }

    /**
     * This method calculates the data for the dashboard activity for each vehicle
     * Each in the list item has the data from one vehicle and is from type VehiclesTotalRunningCosts
     *
     * @return a list of type VehiclesTotalRunningCosts
     */
    public static final List<VehiclesTotalRunningCosts> fetchVehiclesTotalRunningCosts() {
        /* ArrayList to hold the dashboard list items */
        List<VehiclesTotalRunningCosts> dashboardListItems = new ArrayList<VehiclesTotalRunningCosts>();
        VehiclesTotalRunningCosts dashboardListItem;

        CapstoneDBHelper dbHelper;
        dbHelper = new CapstoneDBHelper(MyApp.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Read the available Vehicles
        String sqlVehicles =
                "select v._id as vehicleId, v.Name, v.distanceUnit, " +
                        "       v.volumeUnit, m.minOdo, m.maxOdo " +
                        "from vehicles as v " +
                        "left outer join ( " +
                        "      select e.vehicleId, min(e.odometer) as minOdo, " +
                        "             max(e.odometer) as maxOdo " +
                        "      from expenses as e " +
                        "      where e.expenseType = 0 " +
                        "      group by e.vehicleId " +
                        ") as m on m.vehicleId = v._id ";

        Cursor cVehicles = db.rawQuery(sqlVehicles, null);

        if (cVehicles != null && cVehicles.getCount() > 0) {

            while (cVehicles.moveToNext()) {
                int vehicleId = cVehicles.getInt(cVehicles.getColumnIndex("vehicleId"));
                dashboardListItem = fetchVehiclesTotalRunningCosts(vehicleId);
                dashboardListItems.add(dashboardListItem);
            }

            cVehicles.close();
        }

        db.close();
        dbHelper.close();

        return dashboardListItems;
    }

    /**
     * This method calculates the data for the dashboard activity for a vehicle
     *
     * @param vehicleId The vehicle's id
     * @return the data for the given vehicle
     */
    public static final VehiclesTotalRunningCosts fetchVehiclesTotalRunningCosts(int vehicleId) {
        VehiclesTotalRunningCosts data = new VehiclesTotalRunningCosts();

        CapstoneDBHelper dbHelper;
        dbHelper = new CapstoneDBHelper(MyApp.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Read the Vehicle's data
        String sqlVehicles = "select v.Name, v.distanceUnit, " +
                "v.volumeUnit, m.minOdo, m.maxOdo " +
                "from vehicles as v " +
                "left outer join ( " +
                "      select e.vehicleId, min(e.odometer) as minOdo, " +
                "             max(e.odometer) as maxOdo " +
                "      from expenses as e " +
                "      where e.expenseType = 0 " +
                "      group by e.vehicleId " +
                ") as m on m.vehicleId = v._ID " +
                "where v._ID = ? ";

        Cursor cVehicles = db.rawQuery(sqlVehicles, new String[] {vehicleId + ""});

        if (cVehicles != null && cVehicles.getCount() > 0) {

            data.hasData = true;

            cVehicles.moveToNext();
            String name = cVehicles.getString(cVehicles.getColumnIndex("name"));
            int distanceUnit = cVehicles.getInt(cVehicles.getColumnIndex("distanceUnit"));
            int volumeUnit = cVehicles.getInt(cVehicles.getColumnIndex("volumeUnit"));
            int minOdo = cVehicles.getInt(cVehicles.getColumnIndex("minOdo"));
            int maxOdo = cVehicles.getInt(cVehicles.getColumnIndex("maxOdo"));

            data.setVehicleId(vehicleId);
            data.setName(name);
            data.setDistanceUnit(distanceUnit);
            data.setVolumeUnit(volumeUnit);
            data.setKmDriven(maxOdo - minOdo);

            // Read Vehicle's Expenses
            String sqlExpenses =
                    "select e.expenseType, e.subtype, " +
                            "       sum(e.amount) as amount, sum(e.fuelQty) as qty " +
                            "from expenses as e " +
                            "where e.vehicleId = ? " +
                            "group by e.expenseType, e.subtype ";

            Cursor cExpenses = db.rawQuery(sqlExpenses, new String[] {vehicleId + ""});

            if (cExpenses != null && cExpenses.getCount() > 0) {

                while (cExpenses.moveToNext()) {
                    int expenseType = cExpenses.getInt(cExpenses.getColumnIndex("expenseType"));
                    int subtype = cExpenses.getInt(cExpenses.getColumnIndex("subtype"));
                    float qty = cExpenses.getFloat(cExpenses.getColumnIndex("qty"));
                    float amount = cExpenses.getFloat(cExpenses.getColumnIndex("amount"));

                    data.addExpense(expenseType, subtype, qty, amount);
                }

                cExpenses.close();

                data.calcTotals();
            }

        } else {
            data.setName(MyApp.getContext().getString(R.string.deleted_vehicle));
        }

        cVehicles.close();

        db.close();
        dbHelper.close();
        return data;
    }

}
