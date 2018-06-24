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
import com.aiassoft.capstone.data.FavoriteVideosContract.FavoriteVideosEntry;

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
     * It's convention to use 100, 200, 300, etc for directories,
     * and related ints (101, 102, ..) for items in that directory.
     */
    public static final int FAVORITE_VIDEOS = 100;
    public static final int FAVORITE_VIDEO_WITH_ID = 101;

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
        uriMatcher.addURI(Const.AUTHORITY, FavoriteVideosContract.PATH_FAVORITE_VIDEOS, FAVORITE_VIDEOS);
        /** single item */
        uriMatcher.addURI(Const.AUTHORITY, FavoriteVideosContract.PATH_FAVORITE_VIDEOS + "/#",
                FAVORITE_VIDEO_WITH_ID);

        return uriMatcher;
    }

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
         * Write URI matching code to identify the match for the FAVORITE_VIDEOS directory
         * This match will be either 100 for all FAVORITE_VIDEOS
         * or 101 for a movie with ID, or an unrecognized URI
         */
        int match = sUriMatcher.match(uri);

        /**
         * Insert new values into the database
         * Set the value for the returnedUri and write the default case for unknown URI's
         */
        Uri returnUri;

        /**
         * We want to check these cases and respond to only the FAVORITE_VIDEOS case.
         * If the FAVORITE_VIDEOS case is met, we can insert a new row of data into this directory.
         * We can't insert data into just one row like in the FAVORITE_VIDEOS with id case.
         * And if we receive any other type URI or an invalid one, the default behavior
         * will be to throw an UnsupportedOperationException and print out an
         * Unknown uri message.
         */
        switch(match) {
            case FAVORITE_VIDEOS:
                /** Inserting values into favoriteMovies table */
                long id = db.insert(FavoriteVideosEntry.TABLE_NAME, null, values);
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
                    returnUri = ContentUris.withAppendedId(
                            FavoriteVideosEntry.CONTENT_URI, id);
                    /**
                     * contentUris is an Android class that contains helper methods for
                     * constructing URIs
                     */
                } else {
                    /** Otherwise, we'll throw a SQLiteException, because the insert failed. */
                    throw new android.database.SQLException(getContext().getString(R.string.failed_to_insert_row_into) + uri);
                }

                break;
            /** Default case throws an UnsupportedOperationException */
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);
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

        switch (match) {
            /** Query for the FAVORITE_VIDEOS directory */
            case FAVORITE_VIDEOS:
                /**
                 * This starting code will look pretty similar for all of our CRUD functions.
                 * The query for our FAVORITE_VIDEOS case, will return all the rows in our database
                 * as a cursor.
                 */
                retCursor = db.query(FavoriteVideosEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            /** Query for one movie in the FAVORITE_VIDEOS directory */
            case FAVORITE_VIDEO_WITH_ID:
                /**
                 * To Query a row of data by its ID, we'll use the selection and
                 * selection args parameters of the delete method.
                 * First we'll have to get the row ID from the past URI.
                 * The URI will look similar to the FAVORITE_VIDEOS directory URI.
                 * It'll start with the same scheme authority and FAVORITE_VIDEOS path.
                 * But this time also with an ID as the part of the path.
                 * And we can grab this ID by Using a call to get path segments on that URI.
                 * And get with the index 1 passed in.
                 * Index 0 would be the tasks portion of the path.
                 * And index 1 is the segment right next to that.
                 *
                 * Get the id from the URI
                 */
                String id = uri.getPathSegments().get(1);

                /**
                 * Selection is the theMovieDBId column = ?,
                 * and the Selection args = the row ID from the URI
                 * The question mark indicates that this is asking for
                 */
                String mSelection = FavoriteVideosEntry.COLUMN_NAME_YOUTOUBE_ID + "=?";

                /**
                 * the rest of this equality from the selection args parameter.
                 * And the selection args should be the row ID
                 * which we just got from the past URI.
                 * And selection args has to be an array of strings.
                 */
                String[] mSelectionArgs = new String[]{id};

                /** finally the query is constructed as normally, passing in the selection/args */
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

        /**
         * returns the number of deleted records
         * starts as 0
         */
        int deletedRecord = 0;

        switch (match) {
            case FAVORITE_VIDEO_WITH_ID:
                /**
                 * build the deletion selections/args as in the delete statement
                  */
                String id = uri.getPathSegments().get(1);
                String mSelection = FavoriteVideosEntry.COLUMN_NAME_YOUTOUBE_ID + "=?";
                String[] mSelectionArgs = new String[]{id};

                /** finally the deletion is constructed as normally, passing in the selection/args */
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