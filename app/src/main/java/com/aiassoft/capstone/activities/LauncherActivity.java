package com.aiassoft.capstone.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

        Intent intent = new Intent(this, VehiclesListActivity.class);
        startActivity(intent);
        finish();
    }
}
