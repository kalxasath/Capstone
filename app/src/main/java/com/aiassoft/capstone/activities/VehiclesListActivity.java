package com.aiassoft.capstone.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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

import com.aiassoft.capstone.R;
import com.aiassoft.capstone.adapters.VehiclesListAdapter;
import com.aiassoft.capstone.data.VehiclesContract;
import com.aiassoft.capstone.model.Vehicle;
import com.aiassoft.capstone.navigation.DrawerMenu;
import com.aiassoft.capstone.utilities.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aiassoft.capstone.utilities.AppUtils.showSnackbar;

public class VehiclesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        VehiclesListAdapter.VehiclesAdapterOnClickHandler,
        LoaderCallbacks<List<Vehicle>> {

    public static final int VEHICLES_LOADER_ID = 0;

    public static final int CHILD_INSERT = 0;
    public static final int CHILD_UPDATE = 1;

    /**
     * Used to identify if we have to invalidate the cache
     */
    private static final String LOADER_EXTRA_IC = "invalidate_the_cache";

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

        //mNavView = this.findViewById(R.id.nav_view);
        //mNavView.setFocusable(true);

        //mToolbar = findViewById(R.id.toolbar);
        //mToolbar.setFocusable(true);
        setSupportActionBar(mToolbar);

        initFab();

        initDrawer();

        initNavigation();

        initVehiclesListRecyclerView();

        fetchVehiclesList();
    }

    private void initFab() {
        if (mFab != null) {
            mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp));

            mFab.setOnClickListener(this);

//            mFab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(mContext, VehicleEntityActivity.class);
//                    startActivity(intent);
//                    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    //        .setAction("Action", null).show();
//                }
//            });
        }
    }

    private void initDrawer() {
        //mDrawer = findViewById(R.id.drawer_layout);
        //mDrawer.setFocusable(true);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //This is not working properly
                mNavView.requestFocus();
                /*
                if(mNavView.requestFocus()){
                    NavigationMenuView navigationMenuView = (NavigationMenuView)mNavView.getFocusedChild();
                    navigationMenuView.setPressed(true);

                    //navigationMenuView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                }
                */
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //This seems to work
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


    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //item.setChecked(true);
        mDrawer.closeDrawer(GravityCompat.START);

        // Handle navigation view item clicks here.
        if (DrawerMenu.navigate(this, item.getItemId(), mNavView))
            finish();

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
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddTaskActivity,
     * so this restarts the loader to re-query the underlying data for any changes.
     */
    @Override
    protected void onResume() {
        super.onResume();

    }


    /**
     * Fetch the vehicles' data from the database
     */
    private void fetchVehiclesList() {
        /* Create a bundle to pass parameters to the loader */
        //Bundle loaderArgs = new Bundle();
        //loaderArgs.putBoolean(LOADER_EXTRA_IC, invalidateCache);

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
//                if (loaderArgs == null) {
//                    return;
//                }

//                Boolean invalidateCache = loaderArgs.getBoolean(LOADER_EXTRA_IC);
//                if (invalidateCache)
//                    mCachedVehiclesListData = null;


                if (mCachedVehiclesListData != null) {
                    deliverResult(mCachedVehiclesListData);
                } else {

                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    forceLoad();
                }
            } // onStartLoading

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from the database in the background.
             *
             * @return Vehicles' data from the database as a List of VehiclesReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public List<Vehicle> loadInBackground() {

//                Boolean invalidateCache = loaderArgs.getBoolean(LOADER_EXTRA_IC);
//                if (invalidateCache)
//                    mCachedVehiclesListData = null;

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

                        //vehiclesListItem.setName(vehiclesListItem.getName() + ": " + String.valueOf(vehiclesListItem.getId()));

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
    }

    /**
     * his method is for responding to clicks from the fab
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
        }
    }
}
