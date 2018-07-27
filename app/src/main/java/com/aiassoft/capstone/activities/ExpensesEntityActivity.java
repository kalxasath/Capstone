package com.aiassoft.capstone.activities;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
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

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.data.ExpensesContract;
import com.aiassoft.capstone.data.VehiclesContract;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gvryn on 26/07/18.
 */

public class ExpensesEntityActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // TODO: refactor the whole activity to support the expenses
    // TODO: create a new xml layout
    // TODO: Initialize the spinners
    // TODO: add spinner for the vehicles
    // TODO: change subtype spinner according the selected type spinner
    // TODO: save the data
    private static final String LOG_TAG = MyApp.APP_TAG + ExpensesEntityActivity.class.getSimpleName();

    private static final boolean USER_IS_GOING_TO_EXIT = false;

    private static ArrayAdapter<CharSequence> adapterVehicles;
    private static ArrayAdapter<CharSequence> adapterExpenseType;
    private static ArrayAdapter<CharSequence> adapterSubtype;

    private Context mContext;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private View mTitleView;
    private TextView mTitle;
    private ViewGroup mRootLayout;
    private ViewGroup mLayoutContainer;
    private ImageView mToolbarPhoto;
    private Toast mBacktoast;

    @BindView(R.id.vehicle_spinner) Spinner mVehicleSpinner;
    @BindView(R.id.expense_type_spinner) Spinner mExpenseTypeSpinner;
    @BindView(R.id.subtype_spinner) Spinner mSubtypeSpinner;
    @BindView(R.id.date_wrapper) TextInputLayout mDateWrapper;
    @BindView(R.id.date) TextInputEditText mDate;
    @BindView(R.id.odometer_wrapper) TextInputLayout mOdometerWrapper;
    @BindView(R.id.odometer) TextInputEditText mOdometer;
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
        mCollapsingToolbarLayout = findViewById(R.id.toolbar_layout);
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

        initSpinners();

        setEntityTitle("¯\\_(ツ)_/¯");
    }

    private void initSpinners() {
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
        //mSubtypeSpinner.setOnItemSelectedListener(this);
    }

    private void setEntityTitle(String s) {
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
        /*
        contentValues.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME, mName.getText().toString());
        contentValues.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE, mMake.getText().toString());
        contentValues.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL, mModel.getText().toString());
        contentValues.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_PLATE_NO, mPlateno.getText().toString());
        contentValues.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_INITIALMILEAGE, Integer.valueOf(mInitialMileage.getText().toString()));
        contentValues.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT, mDistanceUnitSpinner.getSelectedItemId());
        contentValues.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_TANKVOLUME, Integer.valueOf(mTankVolume.getText().toString()));
        contentValues.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_VOLUME_UNIT, mVolumeUnitSpinner.getSelectedItemId());
        contentValues.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NOTES, mNotes.getText().toString());
        */

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.expense_type_spinner) {
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
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
