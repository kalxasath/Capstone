package com.aiassoft.capstone.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aiassoft.capstone.Const;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.adapters.VehiclesListAdapter;
import com.aiassoft.capstone.data.VehiclesContract;
import com.aiassoft.capstone.model.Vehicle;
import com.aiassoft.capstone.navigation.DrawerMenu;
import com.aiassoft.capstone.utilities.AppUtils;
import com.aiassoft.capstone.widgets.VehicleWidgetProvider;

import java.util.ArrayList;
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

public class VehiclesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        VehiclesListAdapter.VehiclesAdapterOnClickHandler,
        LoaderCallbacks<List<Vehicle>> {

    public static final int VEHICLES_LOADER_ID = 0;

    public static final int CHILD_INSERT = 0;
    public static final int CHILD_UPDATE = 1;

    private static final String STATE_RECYCLER = "STATE_RECYCLER";

    Context mContext;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    ViewGroup mContainer;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.nav_view) android.support.design.widget.NavigationView mNavView;

    /** The recycler view */
    @BindView(R.id.vehicles_list) RecyclerView mVehiclesList;

    /** The recycler's view adapter */
    VehiclesListAdapter mVehiclesListAdapter;

    Parcelable mRecyclerState = null;

    @BindView(R.id.empty_view) TextView mEmptyView;

    @BindView(R.id.loading_indicator) ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_drawer);
        mContext = this;

        mContainer = this.findViewById(R.id.layout_container);
        View.inflate(this, R.layout.activity_vehicles_list, mContainer);

        ButterKnife.bind(this);

        /** recovering the instance state */
        if (savedInstanceState != null) {
            mRecyclerState = savedInstanceState.getParcelable(STATE_RECYCLER);
        }

        requestPermissions();

        setSupportActionBar(mToolbar);

        initFab();

        initDrawer();

        initNavigation();

        initVehiclesListRecyclerView();

        if (mRecyclerState != null)
            mVehiclesList.getLayoutManager().onRestoreInstanceState(mRecyclerState);

        fetchVehiclesList();
    }

    private void initFab() {
        if (mFab != null) {
            mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp));
            mFab.setContentDescription(getString(R.string.add_new_vehicle));
            mFab.setOnClickListener(this);
        }
    }

    private void initDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mContainer.requestFocus();
            }
        };
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void initNavigation() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initVehiclesListRecyclerView() {
        /*
         * The gridLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a grid.
         */
        GridLayoutManager gridLayoutManager;
        if (AppUtils.isTablet()) {
            gridLayoutManager = new GridLayoutManager(this, 3);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 1);

        }
        /* setLayoutManager associates the gridLayoutManager with our RecyclerView */
        mVehiclesList.setLayoutManager(gridLayoutManager);

        mVehiclesList.setItemAnimator(new DefaultItemAnimator());

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mVehiclesList.setHasFixedSize(true);

        /*
         * The RecipesListAdapter is responsible for linking our recipes' data with the Views that
         * will end up displaying our recipe data.
         */
        mVehiclesListAdapter = new VehiclesListAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mVehiclesList.setAdapter(mVehiclesListAdapter);

    }


    /** invoked when the activity may be temporarily destroyed, save the instance state here */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Parcelable recyclerState = mVehiclesList.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(STATE_RECYCLER, recyclerState);

        /** call superclass to save any view hierarchy */
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);

        // Handle navigation view item clicks here.
        if (DrawerMenu.navigate(this, item.getItemId(), mNavView)) {
            overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
            finish();
        }

        return true;
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        if(keyCode== KeyEvent.KEYCODE_DPAD_RIGHT){
            mDrawerToggle.syncState();
            if(! mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            }
        }
        else if(keyCode== KeyEvent.KEYCODE_DPAD_LEFT){
            mDrawerToggle.syncState();
            if(mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
                return true;
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * Fetch the vehicles' data from the database
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

    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs
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

                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from the database in the background.
             *
             * @return Vehicles' data from the database as a List of VehiclesReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public List<Vehicle> loadInBackground() {

                Uri uri = VehiclesContract.VehiclesEntry.CONTENT_URI;
                uri = uri.buildUpon().build();
                String[] projection = new String[] {
                        VehiclesContract.VehiclesEntry._ID,
                        VehiclesContract.VehiclesEntry.COLUMN_NAME_IMAGE,
                        VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME,
                        VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE,
                        VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL,
                };
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

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

        if (data == null) {
            showEmptyView();
        } else {
            showVehiclesListView();
        }
        /* Update the adapters data with the new one */
        invalidateData();
        mVehiclesListAdapter.setVehiclesData(data);

        if (mRecyclerState != null) {
            mVehiclesList.getLayoutManager().onRestoreInstanceState(mRecyclerState);
            mRecyclerState = null;
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

    /**
     * This method is used when we need to reset the data
     */
    private void invalidateData() {
        mVehiclesListAdapter.invalidateData();
    }

    /**
     * This method will make the View for the vehicles list visible and
     * hides the empty view.
     */
    private void showVehiclesListView() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mVehiclesList.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the View for the vehicles list visible and
     * hides the empty view.
     */
    private void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mVehiclesList.setVisibility(View.INVISIBLE);
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * the VehicleEntityActivity is called with parameter the vehicleId
     * to start the entity for edit or delete
     *
     * @param vehicleId the Id from the selected vehicle
     */
    @Override
    public void onClick(int vehicleId) {
        /** Prepare to call the detail activity, to show the vehicle's details
         *  for edit or delete
         */
        Intent intent = new Intent(this, VehicleEntityActivity.class);
        intent.putExtra(VehicleEntityActivity.EXTRA_VEHICLE_ID, vehicleId);
        startActivityForResult(intent, CHILD_UPDATE);
        overridePendingTransition(R.anim.exit, R.anim.entry);
    }

    /**
     * this method is for responding to clicks from the fab
     *
     * the VehicleEntityActivity is called without parameters
     * to start an empty entity for new insert
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, VehicleEntityActivity.class);
        startActivityForResult(intent, CHILD_INSERT);
        overridePendingTransition(R.anim.exit, R.anim.entry);
    }

    /**
     * called after child activity finish
     * if resultCode == Activity.RESULT_OK we will restart the loader
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // if we have changes then we restart the loader
            // so that to we read the new data and update the UI.
            getSupportLoaderManager().restartLoader(VEHICLES_LOADER_ID, null, this);

            // update the home screen widgets
            VehicleWidgetProvider.sendRefreshBroadcast(mContext);
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
            getSupportLoaderManager().restartLoader(VEHICLES_LOADER_ID, null, this);
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
                    getSupportLoaderManager().restartLoader(VEHICLES_LOADER_ID, null, this);

                } else {
                    // The permission was denied, so we can show a message why we can't run the app
                    // and then close the app.
                    boolean permWasGranted = getBoolPref(READ_EXTERNAL_STORAGE_GRANTED, true);

                    if (permWasGranted) {
                        setBoolPref(READ_EXTERNAL_STORAGE_GRANTED, false);
                        showSnackbar(mContainer, R.string.permission_for_reading_the_external_storage_not_granted);
                    } else {
                        showSnackbar(mContainer, R.string.enable_permission_for_reading_the_external_storage);
                    }
                }
            }
        }
    }




}
