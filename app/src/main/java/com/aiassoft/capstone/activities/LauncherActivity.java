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

/*
 * This Activity is a simple loader, it will start the finally activity and finish
 *
 * Project Preparations to run
 *    In the file build.gradle (Module: app) (in Android Viewer)
 *    search for REVIEWERS_YOUTUBE_API_KEY_GOES_HERE then replace it
 *    with your YOUTUBE API KEY, otherwise the YouTube search will
 *    not work.
 *
 * Populating with fake data
 *    In the app exists an option to insert fake data
 *    this option is for debug purpose so it is not
 *    mentioned in the app blueprint / capstone 1
 *    and by default it is hidden.
 *    To enable it go in the dramer_main_menu.xml
 *    and set nav_database_utilities visibility to true.
 *    After that you will see in the drawer the menu option
 *    Fill database with fake data.
 *    The whole procedure comes as is and is not supported
 *
 * Playing video from the YouTube Results
 *    tap in the play circle to start video playing
 *
 * Type of activities
 *    As described in the capstone 1 there are two major types of activities:
 *
 *    The main activities, i.e., Dashboard, Vehicles List, Expenses List
 *    or YouTube search are using as primary layout app_drawer.xml the
 *    activities content views are inflated in the CoordinatorLayout named layout_container
 *    which resides in the included xml file app_drawer_container.xml
 *
 *    The entities activities, i.e., Vehicle, Expenses are using as primary layout
 *    app_coordinator_container the activities content views are inflated in the
 *    NestedScrollView name layout_container
 *
 *
 */

package com.aiassoft.capstone.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
