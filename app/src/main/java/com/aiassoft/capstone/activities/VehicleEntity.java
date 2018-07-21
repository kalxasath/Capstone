package com.aiassoft.capstone.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
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
import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.data.VehiclesContract.VehiclesEntry;
import com.aiassoft.capstone.utilities.FileUtils;
//import com.rany.albeg.wein.springfabmenu.SpringFabMenu;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VehicleEntity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String LOG_TAG = MyApp.APP_TAG + VehicleEntity.class.getSimpleName();

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

//    TextInputLayout mNameWrapper;
//    TextInputEditText mName;
//    TextInputLayout mMakeWrapper;
//    TextInputEditText mMake;
//    TextInputLayout mModelWrapper;
//    TextInputEditText mModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.app_coordinator_container);

        // Root Content Layout
        mRootLayout = this.findViewById(R.id.root_layout);
        // Toolbar layout, to set the title
        mCollapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        // Layout Content
        mLayoutContainer = this.findViewById(R.id.layout_container);
        // Entity
        View.inflate(this, R.layout.activity_vehicle_entity, mLayoutContainer);
        // Fab
        View.inflate(this, R.layout.fab_image_menu, mRootLayout);

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

        initTextWatcher();
        initSpinners();

        setEntityTitle("Peugeot 307sw");
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vehicle_entiry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItem = item.getItemId();

        switch (selectedMenuItem) {
            case android.R.id.home :
                //TODO ask for cancel if the are data in the views
                //finish();
                //return true;
                onBackPressed();
                return true;
                //return false; // calls the onSupportNavigateUp

            case R.id.action_done :
                //TODO check data validity
                //TODO save the data
                saveVehiclesData();
                //NavUtils.navigateUpFromSameTask(this); Did you forget to add the android.support.PARENT_ACTIVITY <meta-data>  element in your manifest?
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected

    private boolean saveVehiclesData() {
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


        /**
         * Insert new Vehicle's data via a ContentResolver
         * Then we need to insert these values into our database with
         * a call to a content resolver
         */
        Uri uri = getContentResolver().insert(VehiclesEntry.CONTENT_URI, contentValues);

        if (uri == null) {
            Toast.makeText(getBaseContext(), getString(R.string.couldnt_insert_vehicle),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Vehicle added: " + uri,
                    Toast.LENGTH_SHORT).show();
        }

        return (uri != null);
    }

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
                        VehicleEntity.super.onBackPressed();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTempPhotoFile != null) {
            mTempPhotoFile.delete();
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
