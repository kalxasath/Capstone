package com.aiassoft.capstone.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.data.ExpensesContract;
import com.aiassoft.capstone.data.VehiclesContract;
import com.aiassoft.capstone.dialogs.DatePickerDialog;
import com.aiassoft.capstone.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gvryn on 26/07/18.
 */

public class ExpensesEntityActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<List<Vehicle>>,
        View.OnClickListener,
        DatePickerDialog.OpenDatePickerDialogOnSelectedDateHandler {

    private static final String LOG_TAG = MyApp.APP_TAG + ExpensesEntityActivity.class.getSimpleName();

    public static final int VEHICLES_LOADER_ID = 0;

    private static final boolean USER_IS_GOING_TO_EXIT = false;

    List<Vehicle> mVehiclesListData = null;
    List<CharSequence> mVehiclesList = null;
    private static ArrayAdapter<CharSequence> adapterVehicles;
    private static ArrayAdapter<CharSequence> adapterExpenseType;
    private static ArrayAdapter<CharSequence> adapterSubtype;
    private static String[] mVolumeUnits;

    private Context mContext;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private View mTitleView;
    private TextView mTitle;
    private ViewGroup mRootLayout;
    private ViewGroup mLayoutContainer;
    private ImageView mToolbarPhoto;
    private Toast mBacktoast;

    private DatePickerDialog mDatePickerDialog;
    private int mDay;
    private int mMonth;
    private int mYear;

    @BindView(R.id.vehicle_spinner) Spinner mVehicleSpinner;
    @BindView(R.id.loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.expense_type_spinner) Spinner mExpenseTypeSpinner;
    @BindView(R.id.subtype_spinner) Spinner mSubtypeSpinner;
    @BindView(R.id.date_wrapper) TextInputLayout mDateWrapper;
    @BindView(R.id.date) TextInputEditText mDate;
    @BindView(R.id.btn_open_date_picker) Button mButtonOpenDatePicker;
    @BindView(R.id.odometer_wrapper) TextInputLayout mOdometerWrapper;
    @BindView(R.id.odometer) TextInputEditText mOdometer;
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

        mVolumeUnits = getResources().getStringArray(R.array.volume_unit_array);

        mDatePickerDialog = new DatePickerDialog();

        mButtonOpenDatePicker.setOnClickListener(this);


        /** Fetch the Vehicles from the database */
        fetchVehiclesList();

        /** Initialize the Spinners */
        initSpinners();

        setEntityTitle("¯\\_(ツ)_/¯");
    }

    private void initSpinners() {
        mVehicleSpinner.setOnItemSelectedListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterExpenseType = ArrayAdapter.createFromResource(this,
                R.array.expenses_types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterExpenseType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mExpenseTypeSpinner.setAdapter(adapterExpenseType);
        mExpenseTypeSpinner.setOnItemSelectedListener(this);

        adapterSubtype = ArrayAdapter.createFromResource(this,
                R.array.refuel_expenses_subtypes, android.R.layout.simple_spinner_item);
        adapterSubtype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSubtypeSpinner.setAdapter(adapterSubtype);

        mSubtypeSpinner.setOnItemSelectedListener(this);
    }

    private void setEntityTitle(String s) {
        if (s == null) {
            //String v = mVehicleSpinner.getAdapter() == null ? "" : mVehicleSpinner.getSelectedItem().toString();
            String e = mExpenseTypeSpinner.getSelectedItem().toString();
            //String st = mSubtypeSpinner.getSelectedItem().toString();
            //s = e + ": " + st + " of " + v;
            s = e;
        }
        mCollapsingToolbarLayout.setTitle(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entity_edit, menu);
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
                saveData();
                //NavUtils.navigateUpFromSameTask(this); Did you forget to add the android.support.PARENT_ACTIVITY <meta-data>  element in your manifest?
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected

    private boolean saveData() {
        /** We'll create a new ContentValues object to place data into. */
        ContentValues contentValues = new ContentValues();

        /** Put the Expenses data into the ContentValues */
        int vehicleSpinnerItem = mVehicleSpinner.getSelectedItemPosition();
        int vehicleId = mVehiclesListData.get(vehicleSpinnerItem).getId();
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, vehicleId);
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, mExpenseTypeSpinner.getSelectedItemId());
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, mSubtypeSpinner.getSelectedItemId());
        // TODO: convert date to String to store to SQLite
        // TODO: create DateUtils with functions for conversions
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, mDate.getText().toString());
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, Integer.valueOf(mOdometer.getText().toString()));
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, Float.valueOf(mFuelQuantity.getText().toString()));
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, Float.valueOf(mAmount.getText().toString()));
        contentValues.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, mNotes.getText().toString());

        /**
         * Insert new Expenses data via a ContentResolver
         * Then we need to insert these values into our database with
         * a call to a content resolver
         */
        Uri uri = getContentResolver().insert(ExpensesContract.ExpensesEntry.CONTENT_URI, contentValues);

        if (uri == null) {
            Toast.makeText(getBaseContext(), getString(R.string.couldnt_insert_expense),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.expense_added) + uri,
                    Toast.LENGTH_SHORT).show();
        }

        return (uri != null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Spinner section
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setEntityTitle(null);
        switch (parent.getId()) {
            case R.id.expense_type_spinner:
                switch (position) {
                    case 0:
                        adapterSubtype = ArrayAdapter.createFromResource(this,
                                R.array.refuel_expenses_subtypes, android.R.layout.simple_spinner_item);
                        break;
                    case 1:
                        adapterSubtype = ArrayAdapter.createFromResource(this,
                                R.array.bill_expenses_subtypes, android.R.layout.simple_spinner_item);
                        break;
                    case 2:
                        adapterSubtype = ArrayAdapter.createFromResource(this,
                                R.array.service_expenses_subtypes, android.R.layout.simple_spinner_item);
                        break;
                }
                adapterSubtype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSubtypeSpinner.setAdapter(adapterSubtype);
                mSubtypeSpinner.invalidate();
                break;

            case R.id.vehicle_spinner:
                int vehicleSpinnerItem = mVehicleSpinner.getSelectedItemPosition();
                int volumeUnit = mVehiclesListData.get(vehicleSpinnerItem).getVolumeUnit();
                ((TextView) mRootLayout.findViewById(R.id.volume_unit)).setText(mVolumeUnits[volumeUnit]);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /**
     * Vehicles loader
     */
    /**
     * Fetch the vehicles list from the database
     */
    private void fetchVehiclesList() {
        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Vehicle>> theVehicleDbLoader = loaderManager.getLoader(VEHICLES_LOADER_ID);

        if (theVehicleDbLoader == null) {
            loaderManager.initLoader(VEHICLES_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(VEHICLES_LOADER_ID, null, this);
        }

    } // fetchVehiclesList

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs The WEB URL for fetching the vehicles' data.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<Vehicle>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<List<Vehicle>>(this) {

            /* This Vehicle array will hold and help cache our vehicles list data */
            List<Vehicle> mCachedVehiclesListData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (mCachedVehiclesListData != null) {
                    deliverResult(mCachedVehiclesListData);
                } else {

                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    mVehicleSpinner.setVisibility(View.INVISIBLE);

                    forceLoad();
                }
            } // onStartLoading

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from thevehicledb.org in the background.
             *
             * @return Vehicles' data from thevehicledb.org as a List of VehiclesReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public List<Vehicle> loadInBackground() {

                Uri uri = VehiclesContract.VehiclesEntry.CONTENT_URI;
                uri = uri.buildUpon().build();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                if (cursor != null && cursor.getCount() != 0) {
                    /** ArrayList to hold the vehicles list items */
                    List<Vehicle> vehiclesListItems = new ArrayList<Vehicle>();
                    Vehicle vehiclesListItem;

                    while (cursor.moveToNext()) {
                        vehiclesListItem = new Vehicle();
                        vehiclesListItem.setId(cursor.getInt(cursor.getColumnIndex(VehiclesContract.VehiclesEntry._ID)));
                        vehiclesListItem.setImage(cursor.getString(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_IMAGE)));
                        vehiclesListItem.setName(cursor.getString(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME)));
                        vehiclesListItem.setMake(cursor.getString(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE)));
                        vehiclesListItem.setModel(cursor.getString(cursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL)));

                        vehiclesListItems.add(vehiclesListItem);
                    }

                    cursor.close();

                    return vehiclesListItems;
                }

                return null;
            } // loadInBackground

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<Vehicle> data) {
                mCachedVehiclesListData = data;
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
    public void onLoadFinished(Loader<List<Vehicle>> loader, List<Vehicle> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mVehicleSpinner.setVisibility(View.VISIBLE);

        if (data == null) {
            //TODO: start activity load vehicle entity
            Intent intent = new Intent(mContext, VehicleEntityActivity.class);
            startActivity(intent);
        } else {
            mVehiclesListData = new ArrayList<Vehicle>();
            mVehiclesListData.addAll(data);

            mVehiclesList = new ArrayList<CharSequence>();
            for(Vehicle v : mVehiclesListData) {
               mVehiclesList.add(v.getName());
            }
            adapterVehicles = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, mVehiclesList);
            adapterVehicles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mVehicleSpinner.setAdapter(adapterVehicles);
            mVehicleSpinner.invalidate();
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
    public void onLoaderReset(Loader<List<Vehicle>> loader) {
        /*
         * We aren't using this method in this application, but we are required to Override
         * it to implement the LoaderCallbacks<List<VehiclesReviewsListItem>> interface
         */
    }


    @Override
    public void OnSelectedDate(int day, int month, int year) {
        mDay = day;
        mMonth = month;
        mYear = year;

        //TODO: convert date to string, put it to the date view
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_open_date_picker) {
            //TODO:mDatePickerDialog.setDate();

            mDatePickerDialog.show(getSupportFragmentManager(), "");
        }
    }
}
