package com.aiassoft.capstone.activities;

import android.Manifest;
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
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aiassoft.capstone.Const;
import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.data.ExpensesContract;
import com.aiassoft.capstone.data.VehiclesContract;
import com.aiassoft.capstone.dialogs.DatePickerDialog;
import com.aiassoft.capstone.model.Expense;
import com.aiassoft.capstone.model.Vehicle;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aiassoft.capstone.utilities.AppUtils.showSnackbar;
import static com.aiassoft.capstone.utilities.PrefUtils.READ_EXTERNAL_STORAGE_GRANTED;
import static com.aiassoft.capstone.utilities.PrefUtils.getBoolPref;
import static com.aiassoft.capstone.utilities.PrefUtils.setBoolPref;

/**
 * Created by gvryn on 26/07/18.
 */

public class ExpensesEntityActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<Expense>,
        View.OnClickListener,
        DatePickerDialog.OpenDatePickerDialogOnSelectedDateHandler {

    private static final String LOG_TAG = MyApp.APP_TAG + ExpensesEntityActivity.class.getSimpleName();

    public static final int EXPENSES_LOADER_ID = 0;

    public static final String EXTRA_EXPENSES_ID = "EXTRA_EXPENSES_ID";

    private static final String STATE_EXPENSE_ID = "STATE_EXPENSE_ID";
    private static final String STATE_SCROLL_POS = "STATE_SCROLL_POS";
    private static final String STATE_ENTITY = "STATE_ENTITY";
    private static final String STATE_ENTITY_UPDATED = "STATE_ENTITY_UPDATED";

    List<Vehicle> mVehiclesListData = null;
    private static TextWatcher mTextWatcherForUpdate = null;
    private static ArrayAdapter<CharSequence> mAdapterVehicles;
    List<CharSequence> mVehiclesNamesList = null;
    private static ArrayAdapter<CharSequence> mAdapterExpenseType;
    private static ArrayAdapter<CharSequence> mAdapterSubtype;
    private static String[] mDistanceUnits;
    private static String[] mVolumeUnits;

    private static DialogInterface.OnClickListener mDialogCancelEditingOnClickListener;
    private static DialogInterface.OnClickListener mDialogDeleteOnClickListener;

    private Context mContext;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private ViewGroup mRootLayout;
    private ViewGroup mLayoutContainer;
    private ImageView mToolbarPhoto;

    private DatePickerDialog mDatePickerDialog;

    private static boolean mEntityUpdated;

    private int mScrollViewContainerScrollToY = Const.INVALID_INT;

    // This expense structure is to hold expense's data
    private Expense mExpense;

    private int mExpenseId;
    @BindView(R.id.vehicle_spinner) Spinner mVehiclesSpinner;
    @BindView(R.id.loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.expense_type_spinner) Spinner mExpenseTypeSpinner;
    @BindView(R.id.subtype_spinner) Spinner mSubtypeSpinner;
    @BindView(R.id.date_wrapper) TextInputLayout mDateWrapper;
    @BindView(R.id.date) TextInputEditText mDate;
    @BindView(R.id.btn_open_date_picker) Button mButtonOpenDatePicker;
    @BindView(R.id.odometer_wrapper) TextInputLayout mOdometerWrapper;
    @BindView(R.id.odometer) TextInputEditText mOdometer;
    @BindView(R.id.distance_unit) TextView mDistanceUnit;
    @BindView(R.id.fuel_quantity_wrapper) TextInputLayout mFuelQuantityWrapper;
    @BindView(R.id.fuel_quantity) TextInputEditText mFuelQuantity;
    @BindView(R.id.volume_unit) TextView mVolumeUnit;
    @BindView(R.id.amount_wrapper) TextInputLayout mAmountWrapper;
    @BindView(R.id.amount) TextInputEditText mAmount;
    @BindView(R.id.notes_wrapper) TextInputLayout mNotesWrapper;
    @BindView(R.id.notes) TextInputEditText mNotes;

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
        View.inflate(this, R.layout.activity_expenses_entity, mLayoutContainer);

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

        mDistanceUnits = getResources().getStringArray(R.array.distance_unit_array);
        mVolumeUnits = getResources().getStringArray(R.array.volume_unit_array);

        mDatePickerDialog = new DatePickerDialog();

        mButtonOpenDatePicker.setOnClickListener(this);


        /** Initialize the Spinners */
        initSpinners();

        initTextWatchers();

        initDialogsOnClickListener();

        mDate.setOnClickListener(this);


        /** recovering the instance state */
        if (savedInstanceState != null) {
            mExpenseId = savedInstanceState.getInt(STATE_EXPENSE_ID, Const.INVALID_ID);
            mScrollViewContainerScrollToY = savedInstanceState.getInt(STATE_SCROLL_POS, Const.INVALID_INT); // NestedScrollView
            mExpense = savedInstanceState.getParcelable(STATE_ENTITY);
            mEntityUpdated = savedInstanceState.getBoolean(STATE_ENTITY_UPDATED, false);
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
                mExpenseId = intent.getIntExtra(EXTRA_EXPENSES_ID, Const.NEW_RECORD_ID);
            }

            fetchData();

            if (mExpenseId != Const.NEW_RECORD_ID) {
                mToolbarPhoto.setContentDescription(
                        getString(R.string.vehicle_screen_ready_for_edit_record));
            } else {
                mToolbarPhoto.setContentDescription(
                        getString(R.string.vehicle_screen_ready_for_new_record));
            }

        }

    }

    /** invoked when the activity may be temporarily destroyed, save the instance state here */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_EXPENSE_ID, mExpenseId);
        outState.putBoolean(STATE_ENTITY_UPDATED, mEntityUpdated);
        outState.putInt(STATE_SCROLL_POS, mLayoutContainer.getScrollY());

        outState.putParcelable(STATE_ENTITY, mExpense);

        /** call superclass to save any view hierarchy */
        super.onSaveInstanceState(outState);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entity_edit, menu);

        // if new entry -> hide options menu delete entry
        menu.findItem(R.id.action_delete).setVisible(mExpenseId != Const.NEW_RECORD_ID);
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
                    updateExpenseFromViews();
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
        getSupportLoaderManager().destroyLoader(EXPENSES_LOADER_ID);
    }

    private void initSpinners() {
        mVehiclesSpinner.setOnItemSelectedListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        mAdapterExpenseType = ArrayAdapter.createFromResource(this,
                R.array.expenses_types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        mAdapterExpenseType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mExpenseTypeSpinner.setAdapter(mAdapterExpenseType);
        mExpenseTypeSpinner.setOnItemSelectedListener(this);

        mAdapterSubtype = ArrayAdapter.createFromResource(this,
                R.array.refuel_expenses_subtypes, android.R.layout.simple_spinner_item);
        mAdapterSubtype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSubtypeSpinner.setAdapter(mAdapterSubtype);
        mSubtypeSpinner.setOnItemSelectedListener(this);

//        mVehiclesSpinner.postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                mEntityUpdated = false;
//            }
//        }, 500);
    }


    private void initTextWatchers() {
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

        mOdometer.addTextChangedListener(mTextWatcherForUpdate);
        mFuelQuantity.addTextChangedListener(mTextWatcherForUpdate);
        mAmount.addTextChangedListener(mTextWatcherForUpdate);
        mNotes.addTextChangedListener(mTextWatcherForUpdate);
    }

    private void setEntityTitle(String s) {
        if (s == null) {
            s = mExpenseTypeSpinner.getSelectedItem().toString();
        }
        mCollapsingToolbarLayout.setTitle(s);
    }

    private void closeOnError() {
        String err = this.getString(R.string.activity_error_message_missing_extras, ExpensesEntityActivity.class.getSimpleName());
        Log.e(LOG_TAG, err);
        Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private boolean validateData() {
        boolean validated = true;
        mOdometerWrapper.setErrorEnabled(false);
        mFuelQuantityWrapper.setErrorEnabled(false);
        mAmountWrapper.setErrorEnabled(false);

        int odometer = 0;
        try {
            odometer = Integer.parseInt(mOdometer.getText().toString());
        } catch(NumberFormatException nfe) {
            validated = false;
            mOdometerWrapper.setErrorEnabled(true);
            mOdometerWrapper.setError(getString(R.string.validation_wrong_number));
        }

        float fuelQuantity = 0;
        try {
            fuelQuantity = Float.parseFloat(mFuelQuantity.getText().toString());
        } catch(NumberFormatException nfe) {
            validated = false;
            mFuelQuantityWrapper.setErrorEnabled(true);
            mFuelQuantityWrapper.setError(getString(R.string.validation_wrong_number));
        }

        float amount = 0;
        try {
            amount = Float.parseFloat(mAmount.getText().toString());
        } catch(NumberFormatException nfe) {
            validated = false;
            mAmountWrapper.setErrorEnabled(true);
            mAmountWrapper.setError(getString(R.string.validation_wrong_number));
        }

        if (! validated)
            showSnackbar(mLayoutContainer, R.string.form_validation_failed);

        return validated;
    }

    private boolean saveEntity() {
//todo saveEntity
        /** We'll create a new ContentValues object to place data into. */
        ContentValues contentValues = new ContentValues();
        /** Put the Expenses data into the ContentValues */
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, mExpense.getVehicleId());
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, mExpense.getExpenseType());
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, mExpense.getSubtype());
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, mExpense.getYearMonthDay().getDbDate());
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, mExpense.getOdometer());
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, mExpense.getFuelQuantity());
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, mExpense.getAmount());
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, mExpense.getNotes());

        Uri uri;
        int recordsUpdated;

        if (mExpenseId == Const.NEW_RECORD_ID) {
            /**
             * Insert new Expenses data via a ContentResolver
             */
            uri = getContentResolver().insert(ExpensesContract.ExpensesEntry.CONTENT_URI, contentValues);

            if (uri == null) {
                Toast.makeText(getBaseContext(), getString(R.string.couldnt_insert_expense),
                        Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, getString(R.string.couldnt_insert_expense));
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.expense_added) + uri,
                        Toast.LENGTH_SHORT).show();
            }

            return (uri != null);
        } else {

            uri = ExpensesContract.ExpensesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(mExpenseId+"").build();

            recordsUpdated = getContentResolver().update(uri, contentValues,  null, null);

            if (recordsUpdated == 1) {
                Toast.makeText(getBaseContext(), getString(R.string.expenses_data_updated),
                        Toast.LENGTH_SHORT).show();
            } else if (recordsUpdated > 1) {
                Toast.makeText(getBaseContext(), getString(R.string.to_much_expenses_updated),
                        Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, getString(R.string.to_much_expenses_updated));
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.expenses_data_not_updated),
                        Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, getString(R.string.expenses_data_not_updated));
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
        String imagePath = mExpense.getVehicleImage();

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

    private void populateVehiclesSpinner() {
        mAdapterVehicles = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, mVehiclesNamesList);
        mAdapterVehicles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mVehiclesSpinner.setAdapter(mAdapterVehicles);
        mVehiclesSpinner.invalidate();

        // For an new entry entity
        // We need here to set the VehicleId, since the populateViews needs it
        // Title and image are populated on spinners onItemSelected
        if (mVehiclesListData.size() != 0 && mExpenseId == Const.NEW_RECORD_ID) {
            mExpense.setVehicleId(mVehiclesListData.get(0).getId());
            mExpense.setVehiclePosInSpinner(0);
        }
    }

    /**
     * Search the Vehicles ArrayList to find vehicle's position
     * in the ArrayList, this is the position for the Spinner
     * there is no way to fail since all the data are preloaded
     * and this is not a multi user enviroment
     * @return
     */
    private int findVehiclesSpinnerPosition(int id) {
        for(int i = 0; i < mVehiclesListData.size(); ++i) {
            if(mVehiclesListData.get(i).getId() == id)
                return i;
        }

        return 0;
    }

    private void populateViews() {
        setEntityTitle(null);

        mExpense.setVehiclePosInSpinner(findVehiclesSpinnerPosition(mExpense.getVehicleId()));
        mVehiclesSpinner.setSelection(mExpense.getVehiclePosInSpinner());
        mExpenseTypeSpinner.setSelection(mExpense.getExpenseType());
        mSubtypeSpinner.setSelection(mExpense.getSubtype());

        mDate.setText(mExpense.getYearMonthDay().getDisplayDate());
        mDate.setEnabled(false);

        mOdometer.setText(String.valueOf(mExpense.getOdometer()));
        mFuelQuantity.setText(String.valueOf(mExpense.getFuelQuantity()));
        mAmount.setText(String.valueOf(mExpense.getAmount()));
        mNotes.setText(mExpense.getNotes());
    }

    private void updateExpenseFromViews() {
//todo updateExpenseFromViews
        // vehicle's id is store when selecting from the spinner
        mExpense.setExpenseType((int)mExpenseTypeSpinner.getSelectedItemId());
        mExpense.setSubtype((int)mSubtypeSpinner.getSelectedItemId());
        // date is stored from date picker
        mExpense.setOdometer(Integer.parseInt(mOdometer.getText().toString()));
        mExpense.setFuelQuantity(Float.parseFloat(mFuelQuantity.getText().toString()));
        mExpense.setAmount(Float.parseFloat(mAmount.getText().toString()));
        mExpense.setNotes(mNotes.getText().toString());
    }

    private void hideOdometerControls() {
        mFuelQuantityWrapper.setVisibility(View.GONE);
        mDistanceUnit.setVisibility(View.GONE);
        mFuelQuantity.setText("0.0");
    }

    private void showOdometerControls() {
        mFuelQuantityWrapper.setVisibility(View.VISIBLE);
        mVolumeUnit.setVisibility(View.VISIBLE);
    }

    /**
     * Spinner section
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//todo SPINNET on change
        mEntityUpdated = true;
        setEntityTitle(null);
        switch (parent.getId()) {
            case R.id.expense_type_spinner:
                switch (position) {
                    case 0:
                        showOdometerControls();
                        mAdapterSubtype = ArrayAdapter.createFromResource(this,
                                R.array.refuel_expenses_subtypes, android.R.layout.simple_spinner_item);
                        break;
                    case 1:
                        hideOdometerControls();
                        mAdapterSubtype = ArrayAdapter.createFromResource(this,
                                R.array.bill_expenses_subtypes, android.R.layout.simple_spinner_item);
                        break;
                    case 2:
                        hideOdometerControls();
                        mAdapterSubtype = ArrayAdapter.createFromResource(this,
                                R.array.service_expenses_subtypes, android.R.layout.simple_spinner_item);
                        break;
                }
                mAdapterSubtype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSubtypeSpinner.setAdapter(mAdapterSubtype);
                mSubtypeSpinner.invalidate();
                break;

            case R.id.vehicle_spinner:
                int vehicleSpinnerPosition = mVehiclesSpinner.getSelectedItemPosition();

                // Set Distance & Volume metrics according vehicle's data
                int distanceUnit = mVehiclesListData.get(vehicleSpinnerPosition).getDistanceUnit();
                mDistanceUnit.setText(mDistanceUnits[distanceUnit]);
                int volumeUnit = mVehiclesListData.get(vehicleSpinnerPosition).getVolumeUnit();
                mVolumeUnit.setText(mVolumeUnits[volumeUnit]);

                // Set vehicle's data to the expense
                mExpense.setVehiclePosInSpinner(vehicleSpinnerPosition);
                mExpense.setVehicleId(mVehiclesListData.get(vehicleSpinnerPosition).getId());
                mExpense.setVehicleImage(mVehiclesListData.get(vehicleSpinnerPosition).getImage());
                mExpense.setVehicle(mVehiclesListData.get(vehicleSpinnerPosition).getTitle());
                mExpense.setVehicleId(mVehiclesListData.get(vehicleSpinnerPosition).getId());

                requestPermissions();
                displayVehicleImage();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//todo loader
    /**
     * Vehicles loader
     */
    /**
     * Fetch data from the database
     * 1. Fetch the vehicles list
     * 2. Fetch the entities data
     */
    private void fetchData() {
        /* Create a bundle to pass parameters to the loader */
        Bundle loaderArgs = new Bundle();
        loaderArgs.putInt(EXTRA_EXPENSES_ID, mExpenseId);

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Expense>> theDbLoader = loaderManager.getLoader(EXPENSES_LOADER_ID);

        if (theDbLoader == null) {
            loaderManager.initLoader(EXPENSES_LOADER_ID, loaderArgs, this);
        } else {
            loaderManager.restartLoader(EXPENSES_LOADER_ID, loaderArgs, this);
        }

    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs The WEB URL for fetching the vehicles' data.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Expense> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Expense>(this) {
            private int mExpenseId;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (loaderArgs == null) {
                    return;
                }

                mExpenseId = loaderArgs.getInt(EXTRA_EXPENSES_ID, Const.INVALID_ID);

                if (mExpenseId == Const.INVALID_ID) {
                    return;
                }

                forceLoad();
            } // onStartLoading

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from thevehicledb.org in the background.
             *
             * @return Vehicles' data from thevehicledb.org as a List of VehiclesReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public Expense loadInBackground() {
//todo loadInBackground load the data
                // Load the Vehicles for the spinner
                Uri uri = VehiclesContract.VehiclesEntry.CONTENT_URI;
                uri = uri.buildUpon().build();
                String[] projection = new String[] {
                        VehiclesContract.VehiclesEntry._ID,
                        VehiclesContract.VehiclesEntry.COLUMN_NAME_IMAGE,
                        VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME,
                        VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE,
                        VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL,
                        VehiclesContract.VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT,
                        VehiclesContract.VehiclesEntry.COLUMN_NAME_VOLUME_UNIT
                };
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

                mVehiclesListData = null;
                mVehiclesNamesList = null;

                if (cursor != null && cursor.getCount() != 0) {
                    /** ArrayList to hold the vehicles list items */
                    mVehiclesListData = new ArrayList<Vehicle>();
                    Vehicle vehiclesListItem;
                    mVehiclesNamesList = new ArrayList<CharSequence>();

                    while (cursor.moveToNext()) {
                        vehiclesListItem = new Vehicle();
                        vehiclesListItem.setId(cursor.getInt(cursor.getColumnIndex(VehiclesContract.VehiclesEntry._ID)));
                        vehiclesListItem.setImage(cursor.getString(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_IMAGE)));
                        vehiclesListItem.setName(cursor.getString(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME)));
                        vehiclesListItem.setMake(cursor.getString(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE)));
                        vehiclesListItem.setModel(cursor.getString(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL)));
                        vehiclesListItem.setDistanceUnit(cursor.getInt(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT)));
                        vehiclesListItem.setVolumeUnit(cursor.getInt(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_VOLUME_UNIT)));

                        mVehiclesListData.add(vehiclesListItem);

                        mVehiclesNamesList.add(vehiclesListItem.getName());
                    }

                    cursor.close();

                    // We initialize the expenses only if we have at least one vehicle
                    if (mExpenseId != Const.NEW_RECORD_ID) {
                        uri = ExpensesContract.ExpensesEntry.CONTENT_URI;
                        uri = uri.buildUpon().build();
                        cursor = getContentResolver().query(uri, null,
                                ExpensesContract.ExpensesEntry._ID + "= ?",
                                new String[] {mExpenseId + ""}, null);

                        Expense expense = new Expense();
                        if (cursor != null && cursor.getCount() != 0) {
                            cursor.moveToNext();
                            expense.setId(mExpenseId);
                            expense.setVehicleId(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID)));
                            expense.setExpenseType(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE)));
                            expense.setSubtype(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE)));
                            expense.getYearMonthDay().setDate(cursor.getString(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE)));
                            expense.setSubtype(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE)));
                            expense.setOdometer(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER)));
                            expense.setFuelQuantity(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY)));
                            expense.setAmount(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT)));
                            expense.setNotes(cursor.getString(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES)));
                            cursor.close();
                        }

                        return expense;
                    }

                }

                return null;
            } // loadInBackground

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(Expense data) {
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
    public void onLoadFinished(Loader<Expense> loader, Expense data) {
//todo onLoadFinished
        // we don't need any more the loader
        getSupportLoaderManager().destroyLoader(EXPENSES_LOADER_ID);

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mVehiclesSpinner.setVisibility(View.VISIBLE);

        if (mVehiclesNamesList.size() == 0) {
            showSnackbar(mRootLayout, R.string.enter_vehicles_first);
        } else {
            if (data == null) {
                mExpenseId = Const.NEW_RECORD_ID;
                mExpense = new Expense();
                mExpense.getYearMonthDay().setTodayDate();
            } else
                mExpense = data;

            populateVehiclesSpinner();

            populateViews();

            // We need a post delay so that we can set
            // that no changes are existing on entities data
            // and this because spinners are running in their world
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    mEntityUpdated = false;
                }
            }, 300);
        }
    } // onLoadFinished

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Expense> loader) {
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
                            "vehicleId=?", new String[] {mExpenseId+""});

                    uri = VehiclesContract.VehiclesEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(mExpenseId+"").build();

                    deletedRecords = getContentResolver().delete(uri, null, null);

                    setResult(Activity.RESULT_OK, null);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.zoom_out);
                }
            }
        };
    }

    @Override
    public void OnSelectedDate(int year, int month, int day) {
//        showSnackbar(mRootLayout, year+", "+month+", "+day);
        mExpense.getYearMonthDay().setDate(year, month, day);

        mDate.setText(mExpense.getYearMonthDay().getDisplayDate());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open_date_picker || v.getId() == R.id.date) {

            int year = mExpense.getYearMonthDay().getYear();
            int month = mExpense.getYearMonthDay().getMonth();
            int day = mExpense.getYearMonthDay().getDay();

//            showSnackbar(mRootLayout, year+", "+month+", "+day);
            mDatePickerDialog.setDate(year, month, day);

            mDatePickerDialog.show(getSupportFragmentManager(), "");
        }
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
        } else {
            // Otherwise, permissions were granted and we are ready to go!
            //displayVehicleImage();
        }
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
