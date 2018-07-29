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

package com.aiassoft.capstone;

import android.net.Uri;


/**
 * Created by gvryn on 23/06/2018.
 * Defining a class of constants to use them from anywhere
 */
public class Const {
    /**
     * We never need to create an instance of the Const class
     * because the Const is simply a class filled,
     * with App related constants that are all static.
     */
    private Const() {
        throw new AssertionError(R.string.no_instances_for_you);
    }

    /**
     * YOUTUBE API KEY
     */
    public static final String YOUTUBE_API_KEY = BuildConfig.YOUTUBE_API_KEY;

    /**
     * youtube definitions
     */
    public static final String YOUTUBE_APP_URI = "vnd.youtube:";
    public static final String YOUTUBE_WATCH_URL = "https://www.youtube.com/watch";
    public static final String YOUTUBE_PARAM_WATCH_VIDEO = "v";

    /**
     * This ID will be used to identify the Loader responsible for loading our list.
     */
    public static final int MOVIES_LOADER_ID = 0;

    /**
     * Contract's & Content Provider's definitions
     */
    // The authority, which is how the code knows which Content Provider to access
    // The authority is defined in the Android Manifest.
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".contentprovider";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String FILE_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider";

}
