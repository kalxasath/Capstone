package com.aiassoft.capstone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// Displayed by tab messages
// HINT: Warning:(5, 6) App is not indexable by Google Search; consider adding at least one Activity with an ACTION-VIEW intent filter. See issue explanation for more details.
// HINT: Warning:(5, 6) On SDK version 23 and up, your app data will be automatically backed up and restored on app install. Consider adding the attribute `android:fullBackupContent` to specify an `@xml` resource which configures which files to backup. More info: https://developer.android.com/training/backup/autosyncapi.html

public class Launcher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
    }
}
