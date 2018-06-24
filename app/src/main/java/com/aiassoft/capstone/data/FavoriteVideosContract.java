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

import android.net.Uri;
import android.provider.BaseColumns;

import com.aiassoft.capstone.Const;
import com.aiassoft.capstone.R;

/**
 * Created by gvryn on 23/06/2018.
 * Defining the FavoriteVideosContract class
 * In this table will be stored the youtube ids
 * from the user's favorite videos, so that he
 * can find them quickly
 */
public class FavoriteVideosContract {


    // Define the possible paths for accessing data in this contract
    // This is the path for the "FavoriteMovies" directory, that will be appended
    // to the base content URI
    public static final String PATH_FAVORITE_VIDEOS = "FavoriteMovies";

    /**
     * We never need to create an instance of the contract class
     * because the contract is simply a class filled,
     * with DB related constants that are all static.
     */
    private FavoriteVideosContract() {
        throw new AssertionError(R.string.no_instances_for_you);
    }

    /**
     * Inner class that defines the favoriteVideos table contents
     */
    public static final class FavoriteVideosEntry implements BaseColumns {
        /** This final content URI will include the scheme, the authority,
         *  and our FAVORITE_VIDEOS path.
         */
        public static final Uri CONTENT_URI = Const.BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_VIDEOS).build();

        public static final String TABLE_NAME = "favoriteVideos";

        public static final String COLUMN_NAME_YOUTOUBE_ID = "youtubeId";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_THUMBNAIL_DEFAULT = "thumbnailDefault";
        public static final String COLUMN_NAME_THUMBNAIL_MEDIUM = "thumbnailMedium";
        public static final String COLUMN_NAME_THUMBNAIL_HIGH = "thumbnailHigh";
    }

}
