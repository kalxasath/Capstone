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

import android.app.Application;
import android.content.Context;

import com.aiassoft.capstone.utilities.AppUtils;

/**
 * Created by gvryn on 23/06/2018.
 *
 * This application class is used to store static resources
 * being used in static methods
 */
public class MyApp extends Application {
    // static resources
    private static MyApp mInstance;

    public static final String APP_TAG = "Capstone: ";


    /**
     * Initialize any resources here
     */
    public static void initResources() {

    }

    /**
     * @return the application instance
     */
    public static MyApp getInstance() {
        return mInstance;
    }

    /**
     * @return the application context
     */
    public static Context getContext(){
        return mInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        mInstance = this;
        MyApp.initResources();

        super.onCreate();
    }
}
