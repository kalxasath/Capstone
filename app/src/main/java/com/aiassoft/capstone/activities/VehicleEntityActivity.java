package com.aiassoft.capstone.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.FileProvider;
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
import android.widget.TextView;
import android.widget.Toast;

import com.aiassoft.capstone.BuildConfig;
import com.aiassoft.capstone.Const;
import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.data.ExpensesContract;
import com.aiassoft.capstone.data.ExpensesContract.ExpensesEntry;
import com.aiassoft.capstone.data.VehiclesContract;
import com.aiassoft.capstone.data.VehiclesContract.VehiclesEntry;
import com.aiassoft.capstone.model.Vehicle;
import com.aiassoft.capstone.utilities.AppUtils;
import com.aiassoft.capstone.utilities.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VehicleEntityActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        DialogInterface.OnClickListener,
        LoaderManager.LoaderCallbacks<Vehicle> {

    private static final String LOG_TAG = MyApp.APP_TAG + VehicleEntityActivity.class.getSimpleName();

    public static final int VEHICLES_LOADER_ID = 0;

    public static final String EXTRA_VEHICLE_ID = "EXTRA_VEHICLE_ID";

    private static final String STATE_VEHICLE_ID = "STATE_VEHICLE_ID";
    private static final String STATE_RECYCLER = "STATE_RECYCLER";

    private static final boolean USER_IS_GOING_TO_EXIT = false;
    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_PICK_PHOTO = 1;

    private static final int TYPE_OF_DEFAULT = 0;
    private static final int TYPE_OF_NAME = 1;
    private static final int TYPE_OF_MAKE = 2;
    private static final int TYPE_OF_MODEL = 3;

    private static String mTempPhotoPath = null;
    private static File mTempPhotoFile = null;

    private static TextWatcher mTextWatcher = null;
    private static ArrayAdapter<CharSequence> adapterDistanceUnit;
    private static ArrayAdapter<CharSequence> adapterVolumeUnit;

    private Context mContext;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private View mTitleView;
    private TextView mTitle;
    private ViewGroup mRootLayout;
    private ViewGroup mLayoutContainer;
    private ImageView mToolbarPhoto;
    private Toast mBacktoast;

    private int mVehicleId;
//    private boolean mReadVehiclesData;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
//        mReadVehiclesData = false;

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
        //TODO set mToolbarPhoto Content Description according state

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
/*
        SpringFabMenu sfm = (SpringFabMenu)findViewById(R.id.fab);

        sfm.setOnSpringFabMenuItemClickListener(new SpringFabMenu.OnSpringFabMenuItemClickListener() {
            @Override
            public void onSpringFabMenuItemClick(View view) {
                switch (view.getId()) {
                    case R.id.fab_camera:
                        startCamera();
                        break;
                    case R.id.fab_gallery:
                        startGallery();
                        break;
                }
            }
        });
*/
        ButterKnife.bind(this);

        initSpinners();

        initTextWatcher();

        /** recovering the instance state */
        if (savedInstanceState != null) {
            mVehicleId = savedInstanceState.getInt(STATE_VEHICLE_ID, Const.INVALID_ID);

        } else {

            /**
             * should be called from another activity. if not, show error toast and return
             */
            Intent intent = getIntent();
            if (intent == null) {
                closeOnError();
            } else {

                /** Intent parameter should be a valid vehicle id for editing / deleting
                 *  Otherwise NEW_RECORD_ID signifies a new vehicle entity
                 */
                mVehicleId = intent.getIntExtra(EXTRA_VEHICLE_ID, Const.NEW_RECORD_ID);
                // ?mReadVehiclesData = (mVehicleId != NEW_RECORD_ID);
            }
//TODO loader after init should freeze
//TODO loader after read should check for live activity, search my code
            if (mVehicleId != Const.NEW_RECORD_ID)
                fetchVehiclesData();
        }


        setEntityTitle(getString(R.string.add_new_vehicle));
    }

    /** invoked when the activity may be temporarily destroyed, save the instance state here */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_VEHICLE_ID, mVehicleId);

//        Parcelable recyclerState = mMethodStepsRecyclerView.getLayoutManager().onSaveInstanceState();
//        outState.putParcelable(Const.STATE_METHODS_STEP_RECYCLER, recyclerState);

        /** call superclass to save any view hierarchy */
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
        Intent returnIntent = new Intent();

        switch (selectedMenuItem) {
            case android.R.id.home :
                //TODO ask for cancel if the are data in the views

                setResult(Activity.RESULT_CANCELED);

                onBackPressed();
                return true;

            case R.id.action_done :
                //TODO check data validity
                //TODO save the data
                saveData();

                // TODO set returns result
                returnIntent.putExtra("my return result", 0);
                setResult(Activity.RESULT_OK, null);

                finish();
                return true;

            // TODO: implement delete method
            case R.id.action_delete :
                deleteVehicle();
                setResult(Activity.RESULT_OK, null);

                //finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTempPhotoFile != null) {
            mTempPhotoFile.delete();
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

    private void initTextWatcher() {
        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEntityTitle(s.toString());
            }
        };

        mName.addTextChangedListener(mTextWatcher);
        mMake.addTextChangedListener(mTextWatcher);
        mModel.addTextChangedListener(mTextWatcher);
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

    private boolean saveData() {
        /** We'll create a new ContentValues object to place data into. */
        ContentValues contentValues = new ContentValues();

        /** Put the Vehicle's data into the ContentValues */
        contentValues.put(VehiclesEntry.COLUMN_NAME_NAME, mName.getText().toString());
        contentValues.put(VehiclesEntry.COLUMN_NAME_MAKE, mMake.getText().toString());
        contentValues.put(VehiclesEntry.COLUMN_NAME_MODEL, mModel.getText().toString());
        contentValues.put(VehiclesEntry.COLUMN_NAME_PLATE_NO, mPlateno.getText().toString());
        contentValues.put(VehiclesEntry.COLUMN_NAME_INITIALMILEAGE, Integer.valueOf(mInitialMileage.getText().toString()));
        contentValues.put(VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT, mDistanceUnitSpinner.getSelectedItemId());
        contentValues.put(VehiclesEntry.COLUMN_NAME_TANKVOLUME, Integer.valueOf(mTankVolume.getText().toString()));
        contentValues.put(VehiclesEntry.COLUMN_NAME_VOLUME_UNIT, mVolumeUnitSpinner.getSelectedItemId());
        contentValues.put(VehiclesEntry.COLUMN_NAME_NOTES, mNotes.getText().toString());

        Uri uri;
        int recordsUpdated;

        if (mVehicleId == Const.NEW_RECORD_ID) {
            /**
             * Insert new Vehicle's data via a ContentResolver
             * insert these values into our database with
             * a call to a content resolver
             */
            uri = getContentResolver().insert(VehiclesEntry.CONTENT_URI, contentValues);

            if (uri == null) {
                Toast.makeText(getBaseContext(), getString(R.string.couldnt_insert_vehicle),
                        Toast.LENGTH_SHORT).show();
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
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.vehicles_data_not_updated),
                        Toast.LENGTH_SHORT).show();
            }

            return (recordsUpdated == 1);
        }
    }

    private boolean deleteVehicle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_confirm);
        builder.setMessage(R.string.question_delete_vehicle);
        builder.setPositiveButton(R.string.dialog_yes, this);
        builder.setNegativeButton(R.string.dialog_no, this);

        AlertDialog confirmDeletion = builder.create();
        confirmDeletion.show();

        return true;
    }

    private void populateViews(Vehicle data) {
        //TODO set vehicles Image
        mName.setText(data.getName());
        mMake.setText(data.getMake());
        mModel.setText(data.getModel());
        mPlateno.setText(data.getPlateNo());
        mInitialMileage.setText(String.valueOf(data.getInitialMileage()));
        mDistanceUnitSpinner.setSelection(data.getDistanceUnit());
        mTankVolume.setText(String.valueOf(data.getTankVolume()));
        mVolumeUnitSpinner.setSelection(data.getVolumeUnit());
        mNotes.setText(data.getNotes());
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        Intent returnIntent = new Intent();
//        // TODO set returns result
//        returnIntent.putExtra("my return result", 0);
//        setResult(Activity.RESULT_OK, returnIntent);
//        finish();
//    }

    /*
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    */


    /*
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        VehicleEntityActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public void onBackPressed() {
        if(USER_IS_GOING_TO_EXIT) {
            if(mBacktoast !=null&& mBacktoast.getView().getWindowToken()!=null) {
                finish();
            } else {
                mBacktoast = Toast.makeText(this, "Press back to exit", Toast.LENGTH_SHORT);
                mBacktoast.show();
            }
        } else {
            //other stuff...
            super.onBackPressed();
        }
    }
    */

    /**
     * The loader, is used to read the data from the database 
     * and populate the views with vehicle's data
     */
    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs The WEB URL for fetching the vehicles' data.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
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

                //TODO: freeze views until loading
                //TODO: mayne display load indicator
                //mLoadingIndicator.setVisibility(View.VISIBLE);

                forceLoad();
            } // onStartLoading

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from the database in the background.
             *
             * @return Vehicles' data from the database as a List of VehiclesReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public Vehicle loadInBackground() {

//                Boolean invalidateCache = loaderArgs.getBoolean(LOADER_EXTRA_IC);
//                if (invalidateCache)
//                    mCachedVehiclesData = null;

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

    } // Loader

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Vehicle> loader, Vehicle data) {
        populateViews(data);
        //TODO: LoadingIndicator?
        //mLoadingIndicator.setVisibility(View.INVISIBLE);

//        if (data == null) {
//            showEmptyView();
//        } else {
//            showVehiclesListView();
//        }
        /* Update the adapters data with the new one */
//        invalidateData();
//        mVehiclesListAdapter.setVehiclesData(data);
    } // onLoadFinished

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

            finish();
        }
    }




    /**
     * Camera & Gallery Handling
     */
    void startCamera() {
        try {
            dispatchTakePictureIntent();
        } catch (IOException e) {
            Log.e(LOG_TAG, getString(R.string.error_while_creating_file));
            Snackbar.make(mRootLayout, getString(R.string.error_while_creating_file), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // delete previous created temp file
            if (mTempPhotoFile != null) {
                mTempPhotoFile.delete();
            }

            // Create the Temp File where the photo should go
            try {
                mTempPhotoFile = FileUtils.createTempFile(mContext, "ve_", ".jpg");
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }

            // Continue only if the File was successfully created
            if (mTempPhotoFile != null) {
                //Uri photoURI = Uri.fromFile(photoFile);
                mTempPhotoPath = "file:" + mTempPhotoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(mContext,BuildConfig.APPLICATION_ID + ".provider", mTempPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    void startGallery() {
        try {
            dispatchPickPictureIntent();
        } catch (IOException e) {
        }
    }

    private void dispatchPickPictureIntent() throws IOException {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Ensure that there's a gallery activity to handle the intent
        if (pickPhoto.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPhoto, REQUEST_PICK_PHOTO);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent identData) {
        super.onActivityResult(requestCode, resultCode, identData);
        switch(requestCode) {
            case REQUEST_TAKE_PHOTO:
                switch(resultCode) {
                    case RESULT_OK:
                        mToolbarPhoto.setImageURI(Uri.parse(mTempPhotoPath));
                        return;
                    case RESULT_CANCELED:
                        // TODO remove temp file
                        return;
                }
                break;
            case REQUEST_PICK_PHOTO:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = identData.getData();
                    Snackbar.make(mLayoutContainer, selectedImage.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    mToolbarPhoto.setImageURI(selectedImage);
                }
                break;
        }
    }

    /**
     * AdapterView.OnItemSelectedListener methods
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}

