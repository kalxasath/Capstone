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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aiassoft.capstone.Const;
import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.data.ExpensesContract.ExpensesEntry;
import com.aiassoft.capstone.data.VehiclesContract.VehiclesEntry;

/**
 * Created by gvryn on 23/06/2018.
 * Defining the CapstoneContentProvider class
 */
public class CapstoneContentProvider extends ContentProvider {

    private static final String LOG_TAG = MyApp.APP_TAG + CapstoneContentProvider.class.getSimpleName();

    private CapstoneDBHelper mDBHelper;

    /**
     * Define final integer constants
     * It's convention to use 10, 20, 30, etc for directories,
     * and related ints (11, 12, ..) for items in that directory.
     */
    public static final int VEHICLES = 10;
    public static final int VEHICLE_WITH_ID = 11;
    public static final int EXPENSES = 20;
    public static final int EXPENSES_WITH_ID = 21;


    /**
     * Now let's actually build our UriMatcher and associate these constants with the correct URI.
     * Declare a static variable for the Uri matcher that we construct
     * this is a member variable but starts with a lowercase s because it's static variable.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /** We define a static buildUriMatcher method that associates URI's with their int match */
    public static UriMatcher buildUriMatcher() {
        /* first create a new UriMatcher object, by passing in the constant UriMatcher.nomatch */
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        /*
          Then we add the matches we want, by telling it which URI structures to recognize
          and the integer constants they'll match with.
          Using the method addURI(String authority, String path, int code)
          directory
         */
        // Vehicles
        uriMatcher.addURI(Const.CONTENT_AUTHORITY, VehiclesContract.PATH_VEHICLES, VEHICLES);
        /* single item */
        uriMatcher.addURI(Const.CONTENT_AUTHORITY, VehiclesContract.PATH_VEHICLES + "/#",
                VEHICLE_WITH_ID);
        // Events
        uriMatcher.addURI(Const.CONTENT_AUTHORITY, ExpensesContract.PATH_EXPENSES, EXPENSES);
        /* single item */
        uriMatcher.addURI(Const.CONTENT_AUTHORITY, ExpensesContract.PATH_EXPENSES + "/#",
                EXPENSES_WITH_ID);


        return uriMatcher;

    } // UriMatcher buildUriMatcher()

    /**
     * initialize the Capstone Content Provider
     * @return true
     */
    @Override
    public boolean onCreate() {
        /* Create the DBHelper */
        mDBHelper  = new CapstoneDBHelper(getContext());

        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        /* Get access to the task database, So that we can write new data to it */
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        /*
          Write URI matching code to identify the match for the directories
          This match will be either 10,20,30.. for all entries
          or 11,21,31.. for a entry with ID, or an unrecognized URI
         */
        int match = sUriMatcher.match(uri);

        /*
          Insert new values into the database
          Set the value for the returnedUri and write the default case for unknown URI's
         */
        Uri returnUri;

        /*
          Contract Values
         */
        String tableName;
        Uri contentUri;

        /*
          We want to check these cases and respond to only the 10,20,30.. cases.
          If the cases are met, we can insert a new row of data into this directory.
          We can't insert data into just one row like in the with id case.
          And if we receive any other type URI or an invalid one, the default behavior
          will be to throw an UnsupportedOperationException and print out an
          Unknown uri message.
         */
        switch(match) {
            case VEHICLES:
                tableName = VehiclesEntry.TABLE_NAME;
                contentUri = VehiclesEntry.CONTENT_URI;
                break;
            case EXPENSES:
                tableName = ExpensesEntry.TABLE_NAME;
                contentUri = ExpensesEntry.CONTENT_URI;
                break;
            default:
                /* Default case throws an UnsupportedOperationException */
                // This error will be throwed in the development state
                //
                Log.d(LOG_TAG, getContext().getString(R.string.unknown_uri) + uri);
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);
        }

        /* Inserting values into the table */
        long id = db.insert(tableName, null, values);
        /*
           If the insert wasn't successful, this ID will be -1
           But if ths insert is successful, we want the provider's insert method to take
           that unique row ID and create and return a URI for that newly inserted data.
         */

        /* So first, let's write an if that checks that this insert was successful. */
        if ( id > 0 ) {
            /*
              Success, the insert worked and we can construct the new URI
              that will be our main content URI, which has the authority
              and tasks path, with the id appended to it.
             */
            returnUri = ContentUris.withAppendedId(contentUri, id);
            /*
              contentUris is an Android class that contains helper methods for
              constructing URIs
             */
        } else {
            /* Otherwise, we'll throw a SQLiteException, because the insert failed. */
            Log.d(LOG_TAG, getContext().getString(R.string.failed_to_insert_row_into) + uri);
            throw new android.database.SQLException(getContext().getString(R.string.failed_to_insert_row_into) + uri);
        }


        /*
          Notify the resolver if the uri has been changed, and return the newly inserted URI
          To notify the resolver that a change has occurred at this particular URI,
          we'll do this using the notify change function.
          This is so that the resolver knows that something has changed, and
          can update the database and any associated UI accordingly
         */
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;
        String id;
        String mSelection;
        String[] mSelectionArgs;

        switch (match) {
            case VEHICLES:
                /* Query for the VEHICLES directory */
                /*
                  This starting code will look pretty similar for all of our CRUD functions.
                  The query for our VEHICLES case, will return all the rows in our database
                  as a cursor.
                 */
                retCursor = db.query(VehiclesEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case VEHICLE_WITH_ID:
                /* Query for one vehicle in the VEHICLES directory */
                /*
                  To Query a row of data by its ID, we'll use the selection and
                  selection args parameters of the ??? method.
                  First we'll have to get the row ID from the past URI.
                  The URI will look similar to the directory URI.
                  It'll start with the same scheme authority and path.
                  But this time also with an ID as the part of the path.
                  And we can grab this ID by Using a call to get path segments on that URI.
                  And get with the index 1 passed in.
                  Index 0 would be the tasks portion of the path.
                  And index 1 is the segment right next to that.

                  Get the id from the URI
                 */
                id = uri.getPathSegments().get(1);

                /*
                  Selection is the Vechicle _id column = ?,
                  and the Selection args = the row ID from the URI
                  The question mark indicates that this is asking for
                 */
                mSelection = VehiclesEntry._ID + "=?";

                /*
                  the rest of this equality from the selection args parameter.
                  And the selection args should be the row ID
                  which we just got from the past URI.
                  And selection args has to be an array of strings.
                 */
                mSelectionArgs = new String[]{id};

                /* finally the query is constructed as normally, passing in the selection/args */
                retCursor = db.query(VehiclesEntry.TABLE_NAME,
                        projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            case EXPENSES:
                /* Query for the EXPENSES directory */
                retCursor = db.query(ExpensesEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case EXPENSES_WITH_ID:
                /* Query for one event in the EXPENSES directory */
                id = uri.getPathSegments().get(1);

                mSelection = ExpensesEntry._ID + "=?";

                mSelectionArgs = new String[]{id};

                retCursor = db.query(ExpensesEntry.TABLE_NAME,
                        projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            /* default exception */
            default:
                Log.d(LOG_TAG, getContext().getString(R.string.unknown_uri) + uri);
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);
        }

        /* Return the desired Cursor */
        return retCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        String id;
        String mSelection;
        String[] mSelectionArgs;

        /*
          returns the number of deleted records
          starts as 0
         */
        int deletedRecords = 0;

        switch (match) {
            case VEHICLE_WITH_ID:
                /*
                  build the deletion selections/args as in the delete statement
                 */
                id = uri.getPathSegments().get(1);
                mSelection = VehiclesEntry._ID + "=?";
                mSelectionArgs = new String[]{id};

                /* finally the deletion is constructed as normally, passing in the selection/args */
                deletedRecords =  db.delete(VehiclesEntry.TABLE_NAME, mSelection, mSelectionArgs);

                break;

            case EXPENSES:
                deletedRecords =  db.delete(ExpensesEntry.TABLE_NAME, selection, selectionArgs);

                break;

            case EXPENSES_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = ExpensesEntry._ID + "=?";
                mSelectionArgs = new String[]{id};

                deletedRecords =  db.delete(ExpensesEntry.TABLE_NAME, mSelection, mSelectionArgs);

                break;

            /* default exception */
            default:
                Log.d(LOG_TAG, getContext().getString(R.string.unknown_uri) + uri);
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);
        }

        return deletedRecords;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        String tableName = "";
        String id = "";
        String mSelection = "";
        String[] mSelectionArgs;

        // Keep track of if an update occurs
        int recordsUpdated = 0;

        switch (match) {
            case VEHICLE_WITH_ID:
                tableName = VehiclesEntry.TABLE_NAME;
                // update a sing;e vehicle by getting the id
                id = uri.getPathSegments().get(1);
                mSelection = VehiclesEntry._ID + "=?";

                break;
            case EXPENSES_WITH_ID:
                tableName = ExpensesEntry.TABLE_NAME;
                // update a sing;e vehicle by getting the id
                id = uri.getPathSegments().get(1);
                mSelection = ExpensesEntry._ID + "=?";

                break;
        }

        if (!tableName.isEmpty()) {
            mSelectionArgs = new String[]{id};
            recordsUpdated = db.update(tableName, values, mSelection, mSelectionArgs);

            if (recordsUpdated != 0) {
                // set notifications if a task was updated
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return recordsUpdated;
    }

    @Override
    public String getType(@NonNull Uri uri) {


        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match) {
            case VEHICLES:
                // directory
                return "vnd.android.cursor.dir" + "/" + Const.CONTENT_AUTHORITY + "/"
                        + VehiclesContract.PATH_VEHICLES;

            case VEHICLE_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + Const.CONTENT_AUTHORITY + "/"
                        + VehiclesContract.PATH_VEHICLES;

            case EXPENSES:
                // directory
                return "vnd.android.cursor.dir" + "/" + Const.CONTENT_AUTHORITY + "/"
                        + ExpensesContract.PATH_EXPENSES;

            case EXPENSES_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + Const.CONTENT_AUTHORITY + "/"
                        + ExpensesContract.PATH_EXPENSES;

            /* default exception */
            default:
                Log.d(LOG_TAG, getContext().getString(R.string.unknown_uri) + uri);
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);
        }
    }

}