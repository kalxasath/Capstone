package com.aiassoft.capstone.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This Activity is a simple loader, it will start the finally activity and finish
 *
 * Project Preparations to run
 *    in the file build.gradle (Module: app) (in Android Viewer)
 *    search for REVIEWERS_YOUTUBE_API_KEY_GOES_HERE then replace it
 *    with your YOUTUBE API KEY, otherwise the YouTube search will
 *    not work
 *
 *
 *
 */

/**
 * PLEASE if I have anything not implemented or wrong implemented
 * give me, teach me how to do it
 * give me a hint, a link
 * Thank you
 */
public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
