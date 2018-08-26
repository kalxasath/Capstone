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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.data.ExpensesContract.ExpensesEntry;
import com.aiassoft.capstone.data.VehiclesContract.VehiclesEntry;

/**
 * Created by gvryn on 23/06/2018.
 * SQLite DB Helper
 * to take care of creating the database for the first time
 * and upgrading it when the schema changes.
 */
public class CapstoneDBHelper extends SQLiteOpenHelper {

    /**
     * The database name
     */
    private static final String DATABASE_NAME = "capstone.db";

    /**
     * The database version number
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * The class constructor
     * @param context the context to pass through to the parent constructor
     */
    public CapstoneDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createDatabase(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int prevVersion, int newVersion) {
        /*
          At the moment we have only one version of our database
          so we don't need to implement any version change here
         */
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VehiclesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ExpensesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
	
    private void createDatabase(SQLiteDatabase sqLiteDatabase) {
        // Vehicles
        final String SQL_CREATE_VEHICLES_TABLE = "CREATE TABLE " +
                VehiclesEntry.TABLE_NAME + " (" +
                VehiclesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VehiclesEntry.COLUMN_NAME_IMAGE + "  TEXT, " +
                VehiclesEntry.COLUMN_NAME_NAME + "  TEXT, " +
                VehiclesEntry.COLUMN_NAME_MAKE + "  TEXT, " +
                VehiclesEntry.COLUMN_NAME_MODEL + "  TEXT, " +
                VehiclesEntry.COLUMN_NAME_PLATE_NO + "  TEXT, " +
                VehiclesEntry.COLUMN_NAME_INITIALMILEAGE + "  INTEGER, " +
                VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT + "  INTEGER, " +
                VehiclesEntry.COLUMN_NAME_TANKVOLUME + "  INTEGER, " +
                VehiclesEntry.COLUMN_NAME_VOLUME_UNIT + "  INTEGER, " +
                VehiclesEntry.COLUMN_NAME_NOTES + "  TEXT " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_VEHICLES_TABLE);

        // Expenses
        final String SQL_CREATE_EXPENSES_TABLE = "CREATE TABLE " +
                ExpensesEntry.TABLE_NAME + " (" +
                ExpensesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ExpensesEntry.COLUMN_NAME_VEHICLE_ID + "  INTEGER NOT NULL, " +
                ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE+ "  INTEGER NOT NULL, " +
                ExpensesEntry.COLUMN_NAME_SUBTYPE + "  INTEGER NOT NULL, " +
                ExpensesEntry.COLUMN_NAME_DATE + "  TEXT NOT NULL, " +
                ExpensesEntry.COLUMN_NAME_ODOMETER + "  INTEGER NOT NULL, " +
                ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY + "  REAL NOT NULL, " +
                ExpensesEntry.COLUMN_NAME_AMOUNT + "  REAL NOT NULL, " +
                ExpensesEntry.COLUMN_NAME_NOTES + "  TEXT " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_EXPENSES_TABLE);

    }

}
