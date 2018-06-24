/*
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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.aiassoft.capstone.Const;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.data.EventsContract.EventsEntry;
import com.aiassoft.capstone.data.FavoriteVideosContract.FavoriteVideosEntry;
import com.aiassoft.capstone.data.ImagesContract.ImagesEntry;
import com.aiassoft.capstone.data.VehiclesContract.VehiclesEntry;

/**
 * Created by gvryn on 23/06/2018.
 * Defining the FavoriteVideosContract class
 * In this table will be stored the youtube ids
 * from the user's favorite videos, so that he
 * can find them quickly
 */
public class CapstoneContentProvider extends ContentProvider {

    private CapstoneDBHelper mDBHelper;

    /**
     * Define final integer constants for the directory of FAVORITE_VIDEOS and a single item.
     * It's convention to use 10, 20, 30, etc for directories,
     * and related ints (11, 12, ..) for items in that directory.
     */
    public static final int VEHICLES = 10;
    public static final int VEHICLE_WITH_ID = 11;
    public static final int EVENTS = 20;
    public static final int EVENT_WITH_ID = 21;
    public static final int IMAGES = 30;
    public static final int IMAGE_WITH_ID = 31;
    public static final int FAVORITE_VIDEOS = 40;
    public static final int FAVORITE_VIDEO_WITH_ID = 41;
    public static final int FAVORITE_VIDEO_WITH_YOUTUBE_ID = 42;

    /**
     * Now let's actually build our UriMatcher and associate these constants with the correct URI.
     * Declare a static variable for the Uri matcher that we construct
     * this is a member variable but starts with a lowercase s because it's static variable.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /** We define a static buildUriMatcher method that associates URI's with their int match */
    public static UriMatcher buildUriMatcher() {
        /** first create a new UriMatcher object, by passing in the constant UriMatcher.nomatch */
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        /**
         * Then we add the matches we want, by telling it which URI structures to recognize
         * and the integer constants they'll match with.
         * Using the method addURI(String authority, String path, int code)
         * directory
         */
        // Vehicles
        uriMatcher.addURI(Const.AUTHORITY, VehiclesContract.PATH_VEHICLES, VEHICLES);
        /** single item */
        uriMatcher.addURI(Const.AUTHORITY, VehiclesContract.PATH_VEHICLES + "/#",
                VEHICLE_WITH_ID);
        // Events
        uriMatcher.addURI(Const.AUTHORITY, EventsContract.PATH_EVENTS, EVENTS);
        /** single item */
        uriMatcher.addURI(Const.AUTHORITY, EventsContract.PATH_EVENTS + "/#",
                EVENT_WITH_ID);
        // Images
        uriMatcher.addURI(Const.AUTHORITY, ImagesContract.PATH_IMAGES, IMAGES);
        /** single item */
        uriMatcher.addURI(Const.AUTHORITY, ImagesContract.PATH_IMAGES + "/#",
                IMAGE_WITH_ID);
        // Favorite Videos
        uriMatcher.addURI(Const.AUTHORITY, FavoriteVideosContract.PATH_FAVORITE_VIDEOS, FAVORITE_VIDEOS);
        /** single item */
        uriMatcher.addURI(Const.AUTHORITY, FavoriteVideosContract.PATH_FAVORITE_VIDEOS + "/#",
                FAVORITE_VIDEO_WITH_ID);

        return uriMatcher;

    } // UriMatcher buildUriMatcher()

    /**
     * initialize the Popular Movies Content Provider
     * @return true
     */
    @Override
    public boolean onCreate() {
        /** Create the DBHelper */
        mDBHelper = new CapstoneDBHelper(getContext());

        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        /** Get access to the task database, So that we can write new data to it */
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        /**
         * Write URI matching code to identify the match for the directories
         * This match will be either 10,20,30.. for all entries
         * or 11,21,31.. for a entry with ID, or an unrecognized URI
         */
        int match = sUriMatcher.match(uri);

        /**
         * Insert new values into the database
         * Set the value for the returnedUri and write the default case for unknown URI's
         */
        Uri returnUri;

        /**
         * Contract Values
         */
        String tableName;
        Uri contentUri;

        /**
         * We want to check these cases and respond to only the 10,20,30.. cases.
         * If the cases are met, we can insert a new row of data into this directory.
         * We can't insert data into just one row like in the with id case.
         * And if we receive any other type URI or an invalid one, the default behavior
         * will be to throw an UnsupportedOperationException and print out an
         * Unknown uri message.
         */
        switch(match) {
            case VEHICLES:
                tableName = VehiclesEntry.TABLE_NAME;
                contentUri = VehiclesEntry.CONTENT_URI;
                break;
            case EVENTS:
                tableName = EventsEntry.TABLE_NAME;
                contentUri = EventsEntry.CONTENT_URI;
                break;
            case IMAGES:
                tableName = ImagesEntry.TABLE_NAME;
                contentUri = ImagesEntry.CONTENT_URI;
                break;
            case FAVORITE_VIDEOS:
                tableName = FavoriteVideosEntry.TABLE_NAME;
                contentUri = FavoriteVideosEntry.CONTENT_URI;
                break;
            default:
                /** Default case throws an UnsupportedOperationException */
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);
        }

        /** Inserting values into the table */
        long id = db.insert(tableName, null, values);
        /**
         *  If the insert wasn't successful, this ID will be -1
         *  But if ths insert is successful, we want the provider's insert method to take
         *  that unique row ID and create and return a URI for that newly inserted data.
         */

        /** So first, let's write an if that checks that this insert was successful. */
        if ( id > 0 ) {
            /**
             * Success, the insert worked and we can construct the new URI
             * that will be our main content URI, which has the authority
             * and tasks path, with the id appended to it.
             */
            returnUri = ContentUris.withAppendedId(contentUri, id);
            /**
             * contentUris is an Android class that contains helper methods for
             * constructing URIs
             */
        } else {
            /** Otherwise, we'll throw a SQLiteException, because the insert failed. */
            throw new android.database.SQLException(getContext().getString(R.string.failed_to_insert_row_into) + uri);
        }


        /**
         * Notify the resolver if the uri has been changed, and return the newly inserted URI
         * To notify the resolver that a change has occurred at this particular URI,
         * we'll do this using the notify change function.
         * This is so that the resolver knows that something has changed, and
         * can update the database and any associated UI accordingly
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
                /** Query for the VEHICLES directory */
                /**
                 * This starting code will look pretty similar for all of our CRUD functions.
                 * The query for our VEHICLES case, will return all the rows in our database
                 * as a cursor.
                 */
                retCursor = db.query(VehiclesEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case VEHICLE_WITH_ID:
                /** Query for one vehicle in the VEHICLES directory */
                /**
                 * To Query a row of data by its ID, we'll use the selection and
                 * selection args parameters of the ??? method.
                 * First we'll have to get the row ID from the past URI.
                 * The URI will look similar to the directory URI.
                 * It'll start with the same scheme authority and path.
                 * But this time also with an ID as the part of the path.
                 * And we can grab this ID by Using a call to get path segments on that URI.
                 * And get with the index 1 passed in.
                 * Index 0 would be the tasks portion of the path.
                 * And index 1 is the segment right next to that.
                 *
                 * Get the id from the URI
                 */
                id = uri.getPathSegments().get(1);

                /**
                 * Selection is the Vechicle _id column = ?,
                 * and the Selection args = the row ID from the URI
                 * The question mark indicates that this is asking for
                 */
                mSelection = VehiclesEntry._ID + "=?";

                /**
                 * the rest of this equality from the selection args parameter.
                 * And the selection args should be the row ID
                 * which we just got from the past URI.
                 * And selection args has to be an array of strings.
                 */
                mSelectionArgs = new String[]{id};

                /** finally the query is constructed as normally, passing in the selection/args */
                retCursor = db.query(VehiclesEntry.TABLE_NAME,
                        projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            case EVENTS:
                /** Query for the EVENTS directory */
                retCursor = db.query(EventsEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case EVENT_WITH_ID:
                /** Query for one event in the EVENTS directory */
                id = uri.getPathSegments().get(1);

                mSelection = EventsEntry._ID + "=?";

                mSelectionArgs = new String[]{id};

                retCursor = db.query(EventsEntry.TABLE_NAME,
                        projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            case IMAGES:
                /** Query for the IMAGES directory */
                retCursor = db.query(ImagesEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case IMAGE_WITH_ID:
                /** Query for one event in the IMAGES directory */
                id = uri.getPathSegments().get(1);

                mSelection = ImagesEntry._ID + "=?";

                mSelectionArgs = new String[]{id};

                retCursor = db.query(ImagesEntry.TABLE_NAME,
                        projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            case FAVORITE_VIDEOS:
                /** Query for the FAVORITE_VIDEOS directory */
                retCursor = db.query(FavoriteVideosEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case FAVORITE_VIDEO_WITH_ID:
                /** Query for one movie in the FAVORITE_VIDEOS directory with its db id */
                id = uri.getPathSegments().get(1);

                mSelection = FavoriteVideosEntry._ID + "=?";

                mSelectionArgs = new String[]{id};

                retCursor = db.query(FavoriteVideosEntry.TABLE_NAME,
                        projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            case FAVORITE_VIDEO_WITH_YOUTUBE_ID:
                /** Query for one movie in the FAVORITE_VIDEOS directory with its youtube id */
                id = uri.getPathSegments().get(1);

                mSelection = FavoriteVideosEntry.COLUMN_NAME_YOUTOUBE_ID + "=?";

                mSelectionArgs = new String[]{id};

                retCursor = db.query(FavoriteVideosEntry.TABLE_NAME,
                        projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;

            /** default exception */
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /** Return the desired Cursor */
        return retCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        String id;
        String mSelection;
        String[] mSelectionArgs;

        /**
         * returns the number of deleted records
         * starts as 0
         */
        int deletedRecord = 0;

        switch (match) {
            case VEHICLE_WITH_ID:
                /**
                 * build the deletion selections/args as in the delete statement
                 */
                id = uri.getPathSegments().get(1);
                mSelection = VehiclesEntry._ID + "=?";
                mSelectionArgs = new String[]{id};

                /** finally the deletion is constructed as normally, passing in the selection/args */
                deletedRecord =  db.delete(VehiclesEntry.TABLE_NAME, mSelection, mSelectionArgs);

                break;

            case EVENT_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = EventsEntry._ID + "=?";
                mSelectionArgs = new String[]{id};

                deletedRecord =  db.delete(EventsEntry.TABLE_NAME, mSelection, mSelectionArgs);

                break;

            case IMAGE_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = ImagesEntry._ID + "=?";
                mSelectionArgs = new String[]{id};

                deletedRecord =  db.delete(ImagesEntry.TABLE_NAME, mSelection, mSelectionArgs);

                break;

            case FAVORITE_VIDEO_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = FavoriteVideosEntry._ID + "=?";
                mSelectionArgs = new String[]{id};

                deletedRecord =  db.delete(FavoriteVideosEntry.TABLE_NAME, mSelection, mSelectionArgs);

                break;

            case FAVORITE_VIDEO_WITH_YOUTUBE_ID:
                id = uri.getPathSegments().get(1);
                mSelection = FavoriteVideosEntry.COLUMN_NAME_YOUTOUBE_ID + "=?";
                mSelectionArgs = new String[]{id};

                deletedRecord =  db.delete(FavoriteVideosEntry.TABLE_NAME
                        , mSelection, mSelectionArgs);

                break;

            /** default exception */
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return deletedRecord;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        /**
         * We don't need to update some data, so we don't implemented it yet
         */
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}