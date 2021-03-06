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

package com.aiassoft.capstone.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aiassoft.capstone.Const;
import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.data.ExpensesContract;
import com.aiassoft.capstone.data.VehiclesContract;
import com.aiassoft.capstone.data.VehiclesContract.VehiclesEntry;
import com.aiassoft.capstone.model.Vehicle;
import com.aiassoft.capstone.utilities.FetchPath;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aiassoft.capstone.utilities.AppUtils.showSnackbar;
import static com.aiassoft.capstone.utilities.PrefUtils.READ_EXTERNAL_STORAGE_GRANTED;
import static com.aiassoft.capstone.utilities.PrefUtils.getBoolPref;
import static com.aiassoft.capstone.utilities.PrefUtils.setBoolPref;

/**
 * Created by gvryn on 26/07/18.
 */

public class VehicleEntityActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        View.OnClickListener,
        LoaderManager.LoaderCallbacks<Vehicle> {


    private static final String LOG_TAG = MyApp.APP_TAG + VehicleEntityActivity.class.getSimpleName();

    public static final int VEHICLES_LOADER_ID = 0;

    public static final String EXTRA_VEHICLE_ID = "EXTRA_VEHICLE_ID";

    private static final String STATE_VEHICLE_ID = "STATE_VEHICLE_ID";
    private static final String STATE_SCROLL_POS = "STATE_SCROLL_POS";
    private static final String STATE_ENTITY = "STATE_ENTITY";
    private static final String STATE_ENTITY_UPDATED = "STATE_ENTITY_UPDATED";

    private static final int REQUEST_PICK_PHOTO = 1;

    private static TextWatcher mTextWatcher = null;
    private static TextWatcher mTextWatcherForUpdate = null;
    private static ArrayAdapter<CharSequence> adapterDistanceUnit;
    private static ArrayAdapter<CharSequence> adapterVolumeUnit;

    private static DialogInterface.OnClickListener mDialogCancelEditingOnClickListener;
    private static DialogInterface.OnClickListener mDialogDeleteOnClickListener;

    private static Context mContext;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private ViewGroup mRootLayout;
    private ViewGroup mLayoutContainer;
    private ImageView mToolbarPhoto;

    private static boolean mEntityUpdated;
    private static boolean mForceDisplayVehiclesImage;

    private int mScrollViewContainerScrollToY = Const.INVALID_INT;

    // This vehicle structure is to hold vehicle's data
    // Vehicle's image uri is written here direct
    private Vehicle mVehicle;

    private int mVehicleId;
    @BindView(R.id.name_wrapper) TextInputLayout mNameWrapper;
    @BindView(R.id.name) TextInputEditText mName;
    @BindView(R.id.make_wrapper) TextInputLayout mMakeWrapper;
    @BindView(R.id.make) TextInputEditText mMake;
    @BindView(R.id.model_wrapper) TextInputLayout mModelWrapper;
    @BindView(R.id.model) TextInputEditText mModel;
    @BindView(R.id.plateno_wrapper) TextInputLayout mPlatenoWrapper;
    @BindView(R.id.plateno) TextInputEditText mPlateno;
    @BindView(R.id.initial_mileage_wrapper) TextInputLayout mInitialMileageWrapper;
    @BindView(R.id.initial_mileage) TextInputEditText mInitialMileage;
    @BindView(R.id.distance_unit_spinner) Spinner mDistanceUnitSpinner;
    @BindView(R.id.tank_volume_wrapper) TextInputLayout mTankVolumeWrapper;
    @BindView(R.id.tank_volume) TextInputEditText mTankVolume;
    @BindView(R.id.volume_unit_spinner) Spinner mVolumeUnitSpinner;
    @BindView(R.id.notes_wrapper) TextInputLayout mNotesWrapper;
    @BindView(R.id.notes) TextInputEditText mNotes;
    @BindView(R.id.fab) FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mEntityUpdated = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.app_coordinator_container);

        // Root Content Layout
        mRootLayout = this.findViewById(R.id.root_layout);
        // Toolbar layout, to set the title
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        // Layout Content
        mLayoutContainer = this.findViewById(R.id.layout_container);
        // Entity
        View.inflate(this, R.layout.activity_vehicle_entity, mLayoutContainer);
        // Fab
        View.inflate(this, R.layout.fab_classic, mRootLayout);

        // toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // toolbar photo
        mToolbarPhoto = findViewById(R.id.toolbar_photo);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ButterKnife.bind(this);

        initFab();

        initSpinners();

        initTextWatchers();

        initDialogsOnClickListener();


        /* recovering the instance state */
        if (savedInstanceState != null) {
            mVehicleId = savedInstanceState.getInt(STATE_VEHICLE_ID, Const.INVALID_ID);
            mScrollViewContainerScrollToY = savedInstanceState.getInt(STATE_SCROLL_POS, Const.INVALID_INT); // NestedScrollView
            mVehicle = savedInstanceState.getParcelable(STATE_ENTITY);
            mEntityUpdated = savedInstanceState.getBoolean(STATE_ENTITY_UPDATED, false);

            mForceDisplayVehiclesImage = true;
            requestPermissions();
            displayVehicleImage();
            //todo
        } else {

            mForceDisplayVehiclesImage = false;
            /*
              should be called from another activity. if not, show error toast and return
             */
            Intent intent = getIntent();
            if (intent == null) {
                closeOnError();
            } else {

                /* Intent parameter should be a valid vehicle id for editing / deleting
                   Otherwise NEW_RECORD_ID signifies a new vehicle entity
                 */
                mVehicleId = intent.getIntExtra(EXTRA_VEHICLE_ID, Const.NEW_RECORD_ID);
            }

            if (mVehicleId != Const.NEW_RECORD_ID) {
                mToolbarPhoto.setContentDescription(
                        getString(R.string.vehicle_screen_ready_for_edit_record));
                fetchVehiclesData();
            } else {
                mToolbarPhoto.setContentDescription(
                        getString(R.string.vehicle_screen_ready_for_new_record));
                setEntityTitle(getString(R.string.add_new_vehicle));
                // Generate Entities Structure
                mVehicle = new Vehicle();

                // use a post delay to set mEntityUpdated = false;
                setEntityUpdatedStateToFalse();
            }

        }

        if (mScrollViewContainerScrollToY != Const.INVALID_INT) {
            //this is important. scrollTo doesn't work in main thread.
            mLayoutContainer.post(new Runnable()
            {
                @Override
                public void run()
                {
                    mLayoutContainer.scrollTo(0, mScrollViewContainerScrollToY);
                }
            });
        }
    }

    /** invoked when the activity may be temporarily destroyed, save the instance state here */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_VEHICLE_ID, mVehicleId);
        outState.putBoolean(STATE_ENTITY_UPDATED, mEntityUpdated);
        outState.putInt(STATE_SCROLL_POS, mLayoutContainer.getScrollY());

        outState.putParcelable(STATE_ENTITY, mVehicle);

        /* call superclass to save any view hierarchy */
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entity_edit, menu);

        // if new entry -> hide options menu delete entry
        menu.findItem(R.id.action_delete).setVisible(mVehicleId != Const.NEW_RECORD_ID);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItem = item.getItemId();

        switch (selectedMenuItem) {
            case android.R.id.home :
                onBackPressed();
                return true;

            case R.id.action_done :
                if (validateData()) {
                    updateVehicleFromViews();
                    saveEntity();

                    setResult(Activity.RESULT_OK);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                return true;

            case R.id.action_delete :
                deleteEntity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        askForCancelingEditingAndReturn();
    }

    @Override
    protected void onStop() {
        invalidateActivity();
        super.onStop();
    }

    private void invalidateActivity() {
        // destroy the loader
        getSupportLoaderManager().destroyLoader(VEHICLES_LOADER_ID);
    }

    private void initFab() {
        if (mFab != null) {
            mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_gallery_white_24dp));
            mFab.setContentDescription(getString(R.string.add_or_replace_vehicle_image));
            mFab.setOnClickListener(this);
        }
    }

    private void initSpinners() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterVolumeUnit = ArrayAdapter.createFromResource(this,
                R.array.volume_unit_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterVolumeUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mVolumeUnitSpinner.setAdapter(adapterVolumeUnit);
        mVolumeUnitSpinner.setOnItemSelectedListener(this);

        adapterDistanceUnit = ArrayAdapter.createFromResource(this,
                R.array.distance_unit_array, android.R.layout.simple_spinner_item);
        adapterDistanceUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDistanceUnitSpinner.setAdapter(adapterDistanceUnit);
        mDistanceUnitSpinner.setOnItemSelectedListener(this);
    }

    private void initTextWatchers() {
        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mEntityUpdated = true;
                setEntityTitle(s.toString());
            }
        };

        mName.addTextChangedListener(mTextWatcher);
        mMake.addTextChangedListener(mTextWatcher);
        mModel.addTextChangedListener(mTextWatcher);

        mTextWatcherForUpdate = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mEntityUpdated = true;
            }
        };

        mPlateno.addTextChangedListener(mTextWatcherForUpdate);
        mInitialMileage.addTextChangedListener(mTextWatcherForUpdate);
        mTankVolume.addTextChangedListener(mTextWatcherForUpdate);
        mNotes.addTextChangedListener(mTextWatcherForUpdate);
    }


    private void setEntityUpdatedStateToFalse() {
        // We need a post delay so that we can set
        // that no changes are existing on entities data
        // and this because spinners are running in their world
        //
        // Dear Reviewer,
        // Is there a point or method in the activity were the system
        // says hey I am done with the queue of the background tasks?
        // and this every time after ui starts new background tasks
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mEntityUpdated = false;
            }
        }, 300);
    }

    private void setEntityTitle(String s) {
        String title = "";
        String name = mName.getText().toString();
        String make = mMake.getText().toString();
        String model = mModel.getText().toString();

        if (!name.isEmpty()) {
            title = name;
        } else {

            if (make.isEmpty() && model.isEmpty()) {
                if (s.isEmpty()) {
                    title = getString(R.string.title_activity_vehicle_entity);
                } else {
                    title = s;
                }
            }

            if (title.isEmpty()) {
                if (!s.isEmpty() && name.equals(s)) {
                    title = name;
                } else {
                    if (!make.isEmpty() && model.isEmpty()) {
                        title = make;
                    } else if (make.isEmpty() && !model.isEmpty()) {
                        title = model;
                    } else {
                        title = make + " " + model;
                    }
                }
            }
        }

        mCollapsingToolbarLayout.setTitle(title);
    }

    private void closeOnError() {
        String err = this.getString(R.string.activity_error_message_missing_extras, VehicleEntityActivity.class.getSimpleName());
        Log.e(LOG_TAG, err);
        Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    /**
     * Fetch the vehicle's data from the database
     */
    private void fetchVehiclesData() {
        /* Create a bundle to pass parameters to the loader */
        Bundle loaderArgs = new Bundle();
        loaderArgs.putInt(EXTRA_VEHICLE_ID, mVehicleId);

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Vehicle> theVehicleDbLoader = loaderManager.getLoader(VEHICLES_LOADER_ID);

        if (theVehicleDbLoader == null) {
            loaderManager.initLoader(VEHICLES_LOADER_ID, loaderArgs, this);
        } else {
            loaderManager.restartLoader(VEHICLES_LOADER_ID, loaderArgs, this);
        }

    }

    private boolean validateData() {
        boolean validated = true;
        mNameWrapper.setErrorEnabled(false);
        mMakeWrapper.setErrorEnabled(false);
        mModelWrapper.setErrorEnabled(false);
        mInitialMileageWrapper.setErrorEnabled(false);
        mTankVolumeWrapper.setErrorEnabled(false);

        String name = mName.getText().toString();
        if (name.isEmpty()) {
            String make = mMake.getText().toString();
            String model = mModel.getText().toString();

            if (name.isEmpty() && make.isEmpty() && model.isEmpty()) {
                validated = false;
                mNameWrapper.setErrorEnabled(true);
                mNameWrapper.setError(getString(R.string.validation_enter_name));
            } else {
                if (make.isEmpty()) {
                    validated = false;
                    mMakeWrapper.setErrorEnabled(true);
                    mMakeWrapper.setError(getString(R.string.validation_enter_make));
                }
                if (model.isEmpty()) {
                    validated = false;
                    mModelWrapper.setErrorEnabled(true);
                    mModelWrapper.setError(getString(R.string.validation_enter_model));
                }
            }
        }

        int initialMileage = 0;
        try {
            initialMileage = Integer.parseInt(mInitialMileage.getText().toString());
        } catch(NumberFormatException nfe) {
            validated = false;
            mInitialMileageWrapper.setErrorEnabled(true);
            mInitialMileageWrapper.setError(getString(R.string.validation_wrong_number));
        }

        int tankVolume = 0;
        try {
            tankVolume = Integer.parseInt(mTankVolume.getText().toString());
        } catch(NumberFormatException nfe) {
            validated = false;
            mTankVolumeWrapper.setErrorEnabled(true);
            mTankVolumeWrapper.setError(getString(R.string.validation_wrong_number));
        }

        if (! validated)
            showSnackbar(mLayoutContainer, R.string.form_validation_failed);

        return validated;
    }

    private boolean saveEntity() {
        /* We'll create a new ContentValues object to place data into. */
        ContentValues contentValues = new ContentValues();

        /* Put the Vehicle's data into the ContentValues */
        contentValues.put(VehiclesEntry.COLUMN_NAME_IMAGE, mVehicle.getImage());
        contentValues.put(VehiclesEntry.COLUMN_NAME_NAME, mVehicle.getName());
        contentValues.put(VehiclesEntry.COLUMN_NAME_MAKE, mVehicle.getMake());
        contentValues.put(VehiclesEntry.COLUMN_NAME_MODEL, mVehicle.getModel());
        contentValues.put(VehiclesEntry.COLUMN_NAME_PLATE_NO, mVehicle.getPlateNo());
        contentValues.put(VehiclesEntry.COLUMN_NAME_INITIALMILEAGE, mVehicle.getInitialMileage());
        contentValues.put(VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT, mVehicle.getDistanceUnit());
        contentValues.put(VehiclesEntry.COLUMN_NAME_TANKVOLUME, mVehicle.getTankVolume());
        contentValues.put(VehiclesEntry.COLUMN_NAME_VOLUME_UNIT, mVehicle.getVolumeUnit());
        contentValues.put(VehiclesEntry.COLUMN_NAME_NOTES, mVehicle.getNotes());

        Uri uri;
        int recordsUpdated;

        if (mVehicleId == Const.NEW_RECORD_ID) {
            /*
              Insert new Vehicle's data via a ContentResolver
             */
            uri = getContentResolver().insert(VehiclesEntry.CONTENT_URI, contentValues);

            if (uri == null) {
                Toast.makeText(getBaseContext(), getString(R.string.couldnt_insert_vehicle),
                        Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, getString(R.string.couldnt_insert_vehicle));
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.vehicle_added) + uri,
                        Toast.LENGTH_SHORT).show();
            }

            return (uri != null);

        } else {

            uri = VehiclesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(mVehicleId+"").build();

            recordsUpdated = getContentResolver().update(uri, contentValues,  null, null);

            if (recordsUpdated == 1) {
                Toast.makeText(getBaseContext(), getString(R.string.vehicles_data_updated),
                        Toast.LENGTH_SHORT).show();
            } else if (recordsUpdated > 1) {
                Toast.makeText(getBaseContext(), getString(R.string.to_much_vehicles_updated),
                        Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, getString(R.string.to_much_vehicles_updated));
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.vehicles_data_not_updated),
                        Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, getString(R.string.vehicles_data_not_updated));
            }

            return (recordsUpdated == 1);
        }
    }

    private void askForCancelingEditingAndReturn() {
        if (mEntityUpdated) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.dialog_confirm);
            builder.setMessage(R.string.abord_editing);
            builder.setPositiveButton(R.string.dialog_yes, mDialogCancelEditingOnClickListener);
            builder.setNegativeButton(R.string.dialog_no, mDialogCancelEditingOnClickListener);

            AlertDialog confirmAbortEditing = builder.create();
            confirmAbortEditing.show();
        } else {
            setResult(Activity.RESULT_CANCELED, null);
            finish();
            overridePendingTransition(R.anim.exit, R.anim.entry);
        }
    }

    private void deleteEntity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_confirm);
        builder.setMessage(R.string.question_delete_vehicle);
        builder.setPositiveButton(R.string.dialog_yes, mDialogDeleteOnClickListener);
        builder.setNegativeButton(R.string.dialog_no, mDialogDeleteOnClickListener);

        AlertDialog confirmDeletion = builder.create();
        confirmDeletion.show();
    }

    private void displayVehicleImage() {
        if (mVehicleId != Const.NEW_RECORD_ID || mForceDisplayVehiclesImage) {
            String imagePath = mVehicle.getImage();

            if (imagePath == null || imagePath.isEmpty()) {
                Picasso.with(mContext)
                        .load(R.drawable.jonathan_daniels_453915_unsplash_rsz)
                        .into(mToolbarPhoto);
            } else {
                Picasso.with(mContext)
                        .load(Uri.fromFile(new File(imagePath)))
                        .placeholder(R.drawable.jonathan_daniels_453915_unsplash_rsz)
                        .error(R.drawable.missing_car_image)
                        .into(mToolbarPhoto);
            }
        }
    }

    private void populateViews() {
        requestPermissions();
        displayVehicleImage();

        setEntityTitle(mVehicle.getTitle());

        mName.setText(mVehicle.getName());
        mMake.setText(mVehicle.getMake());
        mModel.setText(mVehicle.getModel());
        mPlateno.setText(mVehicle.getPlateNo());
        mInitialMileage.setText(String.valueOf(mVehicle.getInitialMileage()));
        mDistanceUnitSpinner.setSelection(mVehicle.getDistanceUnit());
        mTankVolume.setText(String.valueOf(mVehicle.getTankVolume()));
        mVolumeUnitSpinner.setSelection(mVehicle.getVolumeUnit());
        mNotes.setText(mVehicle.getNotes());
    }

    private void updateVehicleFromViews() {
        mVehicle.setName(mName.getText().toString());
        mVehicle.setMake(mMake.getText().toString());
        mVehicle.setModel(mModel.getText().toString());
        mVehicle.setPlateNo(mPlateno.getText().toString());
        mVehicle.setInitialMileage(Integer.parseInt(mInitialMileage.getText().toString()));
        mVehicle.setDistanceUnit(mDistanceUnitSpinner.getSelectedItemId());
        mVehicle.setTankVolume(Integer.parseInt(mTankVolume.getText().toString()));
        mVehicle.setVolumeUnit(mVolumeUnitSpinner.getSelectedItemId());
        mVehicle.setNotes(mNotes.getText().toString());
    }

    /*
      The loader, is used to read the data from the database
      and populate the views with vehicle's data
     */
    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs The WEB URL for fetching the vehicles' data.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Vehicle> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Vehicle>(this) {
            private int mVehicleId;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (loaderArgs == null) {
                    return;
                }

                mVehicleId = loaderArgs.getInt(EXTRA_VEHICLE_ID, Const.INVALID_ID);

                forceLoad();
            } // onStartLoading

            /**
             * This is the method of the AsyncTaskLoader that will load the data
             * from the database in the background.
             *
             * @return Vehicles' data from database as a List of VehiclesReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public Vehicle loadInBackground() {

                Uri uri = VehiclesContract.VehiclesEntry.CONTENT_URI;
                uri = uri.buildUpon().build();
                Cursor cursor = getContentResolver().query(uri, null,
                        VehiclesEntry._ID + "= ?",
                        new String[] {mVehicleId + ""}, null);

                Vehicle vehicle = new Vehicle();
                if (cursor != null && cursor.getCount() != 0) {
                    cursor.moveToNext();
                    vehicle.setId(mVehicleId);
                    vehicle.setImage(cursor.getString(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_IMAGE)));
                    vehicle.setName(cursor.getString(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME)));
                    vehicle.setMake(cursor.getString(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE)));
                    vehicle.setModel(cursor.getString(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL)));
                    vehicle.setPlateNo(cursor.getString(cursor.getColumnIndex(VehiclesEntry.COLUMN_NAME_PLATE_NO)));
                    vehicle.setInitialMileage(cursor.getInt(cursor.getColumnIndex(VehiclesEntry.COLUMN_NAME_INITIALMILEAGE)));
                    vehicle.setDistanceUnit(cursor.getInt(cursor.getColumnIndex(VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT)));
                    vehicle.setTankVolume(cursor.getInt(cursor.getColumnIndex(VehiclesEntry.COLUMN_NAME_TANKVOLUME)));
                    vehicle.setVolumeUnit(cursor.getInt(cursor.getColumnIndex(VehiclesEntry.COLUMN_NAME_VOLUME_UNIT)));
                    vehicle.setNotes(cursor.getString(cursor.getColumnIndex(VehiclesEntry.COLUMN_NAME_NOTES)));
                    cursor.close();
                }

                return vehicle;
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(Vehicle data) {
                super.deliverResult(data);
            } // deliverResult

        }; // AsyncTaskLoader

    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Vehicle> loader, Vehicle data) {
        // we don't need any more the loader
        getSupportLoaderManager().destroyLoader(VEHICLES_LOADER_ID);

        if (mContext != null) {
            mVehicle = data;
            populateViews();
        }

        // use a post delay to set mEntityUpdated = false;
        setEntityUpdatedStateToFalse();
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Vehicle> loader) {
        /*
         * We aren't using this method in this application, but we are required to Override
         * it to implement the LoaderCallbacks<List<VehiclesReviewsListItem>> interface
         */
    }



    /**
     * YES / NO Dialog onClick
     */
    private void initDialogsOnClickListener() {

        mDialogCancelEditingOnClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == Dialog.BUTTON_POSITIVE) {
                    setResult(Activity.RESULT_CANCELED, null);
                    finish();
                    overridePendingTransition(R.anim.exit, R.anim.entry);
                }
            }
        };

        mDialogDeleteOnClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == Dialog.BUTTON_POSITIVE) {
                    int deletedRecords;

                    Uri uri = ExpensesContract.ExpensesEntry.CONTENT_URI;
                    deletedRecords = getContentResolver().delete(uri,
                            "vehicleId=?", new String[] {mVehicleId+""});

                    uri = VehiclesEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(mVehicleId+"").build();

                    deletedRecords = getContentResolver().delete(uri, null, null);

                    setResult(Activity.RESULT_OK, null);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.zoom_out);
                }
            }
        };
    }

    /**
     * Views onClick
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            startGallery();
        }
    }





    /**
     * Gallery Handling
     */
    void startGallery() {
        try {
            dispatchPickPictureIntent();
        } catch (IOException e) {
            Log.d(LOG_TAG, "not posible error");
            e.printStackTrace();
        }
    }

    private void dispatchPickPictureIntent() throws IOException {
        Intent pickPhoto;

        if (Build.VERSION.SDK_INT <= 19) {
            pickPhoto = new Intent();
            pickPhoto.setType("image/*");
            pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
            pickPhoto.addCategory(Intent.CATEGORY_OPENABLE);
        } else {
            pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }

        // Ensure that there's a gallery activity to handle the intent
        if (pickPhoto.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPhoto, REQUEST_PICK_PHOTO);
        } else
            Toast.makeText(mContext, R.string.gallery_not_found, Toast.LENGTH_LONG).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent identData) {
        super.onActivityResult(requestCode, resultCode, identData);
        if (requestCode == REQUEST_PICK_PHOTO) {
            if(resultCode == RESULT_OK) {
                mEntityUpdated = true;
                Uri selectedImage = identData.getData();
                String imagePath = FetchPath.getPath(mContext, selectedImage);
                mVehicle.setImage(imagePath);
                mToolbarPhoto.setImageURI(selectedImage);
            }
        }
    }

    /**
     * AdapterView.OnItemSelectedListener methods
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mEntityUpdated = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /**
     * App Permissions for read external storage
     **/
    private void requestPermissions() {
        // If we don't have the read external storage permission...
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // And if we're on SDK M or later...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Ask again, nicely, for the permissions.
                String[] permissionsWeNeed = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE };
                requestPermissions(permissionsWeNeed, Const.MY_PERMISSION_READ_EXTERNAL_STORAGE_REQUEST_CODE);
            }
        }
        // else {
            // Otherwise, permissions were granted and we are ready to go!
        // }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Const.MY_PERMISSION_READ_EXTERNAL_STORAGE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission was granted!
                    setBoolPref(READ_EXTERNAL_STORAGE_GRANTED, true);
                    displayVehicleImage();

                } else {
                    // The permission was denied, so we can show a message why we can't run the app
                    // and then close the app.
                    boolean permWasGranted = getBoolPref(READ_EXTERNAL_STORAGE_GRANTED, true);

                    if (permWasGranted) {
                        setBoolPref(READ_EXTERNAL_STORAGE_GRANTED, false);
                        showSnackbar(mLayoutContainer, R.string.permission_for_reading_the_external_storage_not_granted);
                    } else {
                        showSnackbar(mLayoutContainer, R.string.enable_permission_for_reading_the_external_storage);
                    }
                }
            }
        }
    }
}
