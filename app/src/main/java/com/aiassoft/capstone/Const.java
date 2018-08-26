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

package com.aiassoft.capstone;

import android.net.Uri;


/**
 * Created by gvryn on 23/06/2018.
 * Defining a class of constants to use them from anywhere
 */
public final class Const {
    /**
     * We never need to create an instance of the Const class
     * because the Const is simply a class filled,
     * with App related constants that are all static.
     */
    private Const() {
        throw new AssertionError(R.string.no_instances_for_you);
    }


    public static final int INVALID_INT = -1;
    public static final int INVALID_ID = -1;
    public static final int NEW_RECORD_ID = 0;

    // Permission request codes
    public static final int MY_PERMISSION_READ_EXTERNAL_STORAGE_REQUEST_CODE = 77;


    /**
     * Contract's & Content Provider's definitions
     */
    // The authority, which is how the code knows which Content Provider to access
    // The authority is defined in the Android Manifest.
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".contentprovider";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
}
