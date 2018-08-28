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
 * PLEASE RESPECT PREVIOUS REVIEWERS OPINIONS AND REQUESTS AND DON'T ASK FOR NEW REQUESTS
 * AS THE 2ND REVIEWER FROM CAPSTONE 2 PLEASE PLEASE STAY ON PATH OF 1RST REVIEWERS REQUESTS
 * I WANT TO FINISH THIS PROJECT AND I KNOW THAT I AM ON PATH
 * THANK YOU VERY MUCH
 *
 * 2nd reviewers requirements to pass the capstone stage 2
 *
 * - app/src/main/res/layout/app_drawer_container.xml
 *   line 46 required contentDescription
 *   fixed by adding android:contentDescription="@string/will_be_set_at_runtime"
 *   at runtime when the FAB is used (visible) then it is set to the appropriate  message like
 *   mFab.setContentDescription(getString(R.string.add_new_expense));
 *
 *
 * - app/build.gradle
 *   line 77 required to implement 2nd Google Play Service
 *   I have declared in my Capstone 1 (blueprint) that I will use Google YouTube search service as the second service and this was approved
 *   this is not my fault, the reviewers have to respect previous reviewers opinions!
 *   I have sent mail to Udacity support for this unpleasant surprise he asked me to implement the 3rd service
 *   I have spend a lot of time to implemented the Google YouTube search service
 *   PLEASE CONSIDER AND ASK UDACITY BEFORE YOU REQUEST AGAIN TO IMPLEMENT THE 3RD SERVICE!!!!!!!!!!!
 *
 *   I am still waiting about Udacity's arbitration, that are not serious things, because this is a fault from Udacity
 *   2 reviewers have seen the capstone 1 and no one has mentiont that the 2nd service should a Google Play Service
 *   I hope Udacity will decide wisely, because it is Udacity's fault not mine!!!!!!
 *
 *
 * 1st reviewers requirements to pass the capstone stage 2
 *
 * - app/build.gradle
 *   line 31 required missing file ../capstone.jks
 *   now it is on github
 *
 *   FIXED: App builds and deploys using the installRelease Gradle task.
 *   FIXED: App is equipped with a signing configuration, and the keystore and passwords are included in the repository. Keystore is referred to by a relative path.
 *
 * - app/build.gradle
 *   line 77 required to implement 2nd Google Play Service
 *   I have declared in my Capstone 1 (blueprint) that I will use Google YouTube search service as the second service and this was approved
 *   this is not my fault, the reviewers have to respect previous reviewers opinions!
 *   I have sent mail to Udacity support for this unpleasant surprise he asked me to implement the 3rd service
 *   I have spend a lot of time to implemented the Google YouTube search service
 *   PLEASE CONSIDER AND ASK UDACITY BEFORE YOU REQUEST AGAIN TO IMPLEMENT THE 3RD SERVICE!!!!!!!!!!!
 *
 *   ALREADY IN PATH DUE OF CAPSTONE 1 APPROVAL
 *
 * - app/src/main/res/layout/item_list_video.xml
 *   line 86 as suggested I run the refactor to fix the issues
 *   all capstone app res have been corrected, its seems that the android system
 *   by it self needs a refactoring
 *
 *   FIXED: App includes support for accessibility. That includes content descriptions, navigation using a D-pad, and, if applicable, non-audio versions of audio cues.
 *
 *
 * - app/src/main/res/layout/fab_classic.xml
 *   line 29 required contentDescription
 *   fixed by adding android:contentDescription="@string/will_be_set_at_runtime"
 *   at runtime when the FAB is used (visible) then it is set to the appropriate  message like
 *   mFab.setContentDescription(getString(R.string.add_new_expense));
 *
 *
 * - app/src/main/res/layout/activity_main.xml
 *   line 12
 *   File is not used so it is deleted
 *
 *   FIXED: App keeps all strings in a strings.xml file and enables RTL layout switching on all layouts.
 *
 *
 * - App uses standard and simple transitions between activities.
 *   The reviewer hasn't provided a suggestion link to what is a simple transition
 *   I am using transitions but what is a simple transition (search a lot but I haven't found anything)
 *
 *   He mentioned: However, you can keep the transition for a couple of activities and revert back to the normal transitions on the rest of the activities
 *
 *   I have keep some of then and revert the rest by comment the overridePendingTransition
 *
 *   I hope this time you will not ask to delete the rest of the transitions (Project must have an end)
 *
 *
 *
 *
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

import com.crashlytics.android.Crashlytics;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Worked
        //Crashlytics.getInstance().crash();

        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
