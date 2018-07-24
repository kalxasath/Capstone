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

package com.aiassoft.capstone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aiassoft.capstone.data.EventsContract.EventsEntry;
import com.aiassoft.capstone.data.FavoriteVideosContract.FavoriteVideosEntry;
import com.aiassoft.capstone.data.ImagesContract.ImagesEntry;
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
        /**
         * At the moment we have only one version of our database
         * so we don't need to implement any version change here
         */
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteVideosEntry.TABLE_NAME);
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
                //VehiclesEntry.COLUMN_NAME_VIN + "  TEXT," +
                VehiclesEntry.COLUMN_NAME_PLATE_NO + "  TEXT, " +
                VehiclesEntry.COLUMN_NAME_INITIALMILEAGE + "  INTEGER, " +
                VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT + "  INTEGER, " +
                //VehiclesEntry.COLUMN_NAME_PURCHASEDATE + "  TEXT, " +
                //VehiclesEntry.COLUMN_NAME_PURCHASEMILEAGE + "  INTEGER ," +
                //VehiclesEntry.COLUMN_NAME_PURCHASEPRICE + "  REAL, " +
                //VehiclesEntry.COLUMN_NAME_SELLDATE + "  TEXT, " +
                //VehiclesEntry.COLUMN_NAME_SEELPRICE + "  REAL, " +
                VehiclesEntry.COLUMN_NAME_TANKVOLUME + "  INTEGER, " +
                VehiclesEntry.COLUMN_NAME_VOLUME_UNIT + "  INTEGER, " +
                //VehiclesEntry.COLUMN_NAME_FUEL_TYPE + "  INTEGER, " +
                VehiclesEntry.COLUMN_NAME_NOTES + "  TEXT " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_VEHICLES_TABLE);

        // Events
        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " +
                EventsEntry.TABLE_NAME + " (" +
                EventsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EventsEntry.COLUMN_NAME_VEHICLE_ID + "  INTEGER NOT NULL, " +
                EventsEntry.COLUMN_NAME_EVENT_TYPE + "  INTEGER NOT NULL, " +
                EventsEntry.COLUMN_NAME_SUBTYPE + "  INTEGER NOT NULL, " +
                EventsEntry.COLUMN_NAME_DATE + "  TEXT, " +
                EventsEntry.COLUMN_NAME_ODOMETER + "  INTEGER, " +
                EventsEntry.COLUMN_NAME_COST + "  REAL, " +
                EventsEntry.COLUMN_NAME_NOTES + "  TEXT " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_TABLE);

        // Images
        final String SQL_CREATE_IMAGES_TABLE = "CREATE TABLE " +
                ImagesEntry.TABLE_NAME + " (" +
                ImagesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ImagesEntry.COLUMN_NAME_ENTITY_KIND + "  INTEGER NOT NULL, " +
                ImagesEntry.COLUMN_NAME_ENTITY_ID + "  INTEGER NOT NULL, " +
                ImagesEntry.COLUMN_NAME_PATH + "  TEXT " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_IMAGES_TABLE);

        // FavoriteVideos
        final String SQL_CREATE_FAVORITEVIDEOS_TABLE = "CREATE TABLE " +
                FavoriteVideosEntry.TABLE_NAME + " (" +
                FavoriteVideosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteVideosEntry.COLUMN_NAME_YOUTOUBE_ID + " INTEGER NOT NULL, " +
                FavoriteVideosEntry.COLUMN_NAME_TITLE + "  TEXT, " +
                FavoriteVideosEntry.COLUMN_NAME_DESCRIPTION + "  TEXT, " +
                FavoriteVideosEntry.COLUMN_NAME_THUMBNAIL_DEFAULT + "  TEXT, " +
                FavoriteVideosEntry.COLUMN_NAME_THUMBNAIL_MEDIUM + "  TEXT, " +
                FavoriteVideosEntry.COLUMN_NAME_THUMBNAIL_HIGH + "  TEXT " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITEVIDEOS_TABLE);
    }
	
}
