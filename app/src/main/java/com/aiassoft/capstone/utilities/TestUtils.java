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

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import com.aiassoft.capstone.R;
import com.aiassoft.capstone.data.ExpensesContract;
import com.aiassoft.capstone.data.VehiclesContract;

import java.util.ArrayList;
import java.util.List;

import static com.aiassoft.capstone.utilities.AppUtils.showSnackbar;

/**
 * Created by gvryn on 05/08/18.
 */

/**
 * This method inserts fake data to the database
 * Menu option by default is hidden, developer should enabled by seld
 * since this method is not supported and it comes as is
 */
public final class TestUtils {

    private TestUtils() {
        throw new AssertionError(R.string.no_instances_for_you);
    }

    public static void insertFakeData(SQLiteDatabase db, View view) {

        if (db == null) {
            return;
        }

        showSnackbar(view, view.getContext().getString(R.string.insert_fake_data));

        int erroneous = insertFakeVehicles(db);

        if (erroneous == 0)
            erroneous = insertFakeExpenses(db);

        String msg;
        if (erroneous == 0)
            msg = view.getContext().getString(R.string.finished_inserting_fake_data);
        else
            msg = view.getContext().getString(R.string.inserting_fake_data_failed) +
                    ": " + view.getContext().getString(erroneous);

        showSnackbar(view, msg);
    }

    private static int insertFakeVehicles(SQLiteDatabase db) {
        int erroneous = 0;

        //create a list of fake vehicles
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME, "My Favorite Car");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE, "Peugeot");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL, "307 sw");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_PLATE_NO, "ZKN 7777");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_INITIALMILEAGE, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_TANKVOLUME, "60");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_VOLUME_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME, "My Dream Car");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE, "Ferrari");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL, "306 gts");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_PLATE_NO, "****");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_INITIALMILEAGE, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_TANKVOLUME, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_VOLUME_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME, "My First Car");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE, "Toyota");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL, "Starlet");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_PLATE_NO, "YBH 8075");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_INITIALMILEAGE, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_TANKVOLUME, "40");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_VOLUME_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME, "My Oppas Car");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE, "Mercedes");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL, "180 W 120");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_PLATE_NO, "$$$$");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_INITIALMILEAGE, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_TANKVOLUME, "25");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_VOLUME_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        //insert all Vehicles in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (VehiclesContract.VehiclesEntry.TABLE_NAME,null,null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + VehiclesContract.VehiclesEntry.TABLE_NAME + "'");

            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(VehiclesContract.VehiclesEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
            e.printStackTrace();
            erroneous = R.string.failed_to_insert_vehicles;
        }
        finally
        {
            db.endTransaction();
        }

        return erroneous;
    }

    private static int insertFakeExpenses(SQLiteDatabase db) {
        int erroneous = 0;

        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180416");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 200485);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 45.88);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 31.84);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180421");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 200811);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 33.38);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 24.00);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180429");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 201160);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 39.42);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 27.40);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180501");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 201430);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 24.52);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 19.1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180506");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 201780);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 35.25);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 24.2);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180514");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 202158);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 39.99);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 29.2);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180520");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 202513);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 38.01);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 26.8);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180528");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 202922);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 42.55);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 30.0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180605");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 203290);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 42.34);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 29.82);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180611");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 203652);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 36.38);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 26.0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180618");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 204053);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 41.13);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 29.21);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180624");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 204377);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 35.46);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 25);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180702");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 204699);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 34.04);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 24.0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180708");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 205045);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 42.72);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 32.0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180613");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 205358);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 30.69);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 21.94);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180716");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 205591);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 28.91);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 26.6);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

//        cv = new ContentValues();
//        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
//        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
//        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 1);
//        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180720");
//        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 205848);
//        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 10.87);
//        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 10.0);
//        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
//        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180722");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 205982);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 36.57);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 29.00);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180728");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 206300);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 30.7);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 21.95);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180802");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 206669);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 30.21);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 21.60);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);


        // Toll
        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180601");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 20.0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180612");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 28.0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        // Parking
        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180612");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 6.2);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        // Insurance
        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 2);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180419");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 29.46);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 2);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180519");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 29.46);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 2);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180619");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 29.46);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 2);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180719");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 29.46);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);


        // Service
        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 2);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180606");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 203310);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 60);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "Power Generator");
        list.add(cv);

        cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 2);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "20180707");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 202026);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 145);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);



        //insert all Expenses in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (ExpensesContract.ExpensesEntry.TABLE_NAME,null,null);
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + ExpensesContract.ExpensesEntry.TABLE_NAME + "'");

            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(ExpensesContract.ExpensesEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
            e.printStackTrace();
            erroneous = R.string.failed_to_insert_expenses;
        }
        finally
        {
            db.endTransaction();
        }

        return erroneous;
    }


}