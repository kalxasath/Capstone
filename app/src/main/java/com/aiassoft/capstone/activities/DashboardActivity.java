package com.aiassoft.capstone.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aiassoft.capstone.R;
import com.aiassoft.capstone.adapters.DashboardListAdapter;
import com.aiassoft.capstone.data.CapstoneDBHelper;
import com.aiassoft.capstone.model.VehiclesTotalRunningCosts;
import com.aiassoft.capstone.navigation.DrawerMenu;
import com.aiassoft.capstone.utilities.AppUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aiassoft.capstone.data.DBQueries.fetchVehiclesTotalRunningCosts;

/**
 * Created by gvryn on 25/07/18.
 */

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DashboardListAdapter.DashboardAdapterOnClickHandler,
        LoaderCallbacks<List<VehiclesTotalRunningCosts>> {

    public static final int DASHBOARD_LOADER_ID = 0;

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
    @BindView(R.id.dashboard_list) RecyclerView mDashboardList;

    /** The recycler's view adapter */
    DashboardListAdapter mDashboardListAdapter;

    @BindView(R.id.empty_view) TextView mEmptyView;

    @BindView(R.id.loading_indicator) ProgressBar mLoadingIndicator;

    @BindView(R.id.adView) AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_drawer);
        mContext = this;

        mContainer = this.findViewById(R.id.layout_container);
        View.inflate(this, R.layout.activity_dashboard, mContainer);

        ButterKnife.bind(this);

        mNavView = this.findViewById(R.id.nav_view);

        setSupportActionBar(mToolbar);

        initFab();

        initDrawer();

        initNavigation();

        initAdView();

        initDashboardListRecyclerView();

        fetchDashboardList(true);
    }

    private void initFab() {
        if (mFab != null) {
            mFab.setVisibility(View.GONE);
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

    private void initAdView() {
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    private void initDashboardListRecyclerView() {
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
        mDashboardList.setLayoutManager(gridLayoutManager);

        mDashboardList.setItemAnimator(new DefaultItemAnimator());

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mDashboardList.setHasFixedSize(true);

        /*
         * The RecipesListAdapter is responsible for linking our recipes' data with the Views that
         * will end up displaying our recipe data.
         */
        mDashboardListAdapter = new DashboardListAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mDashboardList.setAdapter(mDashboardListAdapter);

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
        //item.setChecked(true);
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
     * Fetch the dashboard' data from the database
     */
    private void fetchDashboardList(Boolean invalidateCache) {
        /* Create a bundle to pass parameters to the loader */
        Bundle loaderArgs = new Bundle();
        loaderArgs.putBoolean(LOADER_EXTRA_IC, invalidateCache);

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<VehiclesTotalRunningCosts>> theDashboardDbLoader = loaderManager.getLoader(DASHBOARD_LOADER_ID);

        if (theDashboardDbLoader == null) {
            loaderManager.initLoader(DASHBOARD_LOADER_ID, loaderArgs, this);
        } else {
            loaderManager.restartLoader(DASHBOARD_LOADER_ID, loaderArgs, this);
        }



    } // fetchDashboardList

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs The WEB URL for fetching the dashboard' data.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<List<VehiclesTotalRunningCosts>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<List<VehiclesTotalRunningCosts>>(this) {

            /* This VehiclesTotalRunningCosts array will hold and help cache our dashboard list data */
            List<VehiclesTotalRunningCosts> mCachedDashboardListData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (loaderArgs == null) {
                    return;
                }

                Boolean invalidateCache = loaderArgs.getBoolean(LOADER_EXTRA_IC);
                if (invalidateCache)
                    mCachedDashboardListData = null;


                if (mCachedDashboardListData != null) {
                    deliverResult(mCachedDashboardListData);
                } else {

                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    forceLoad();
                }
            } // onStartLoading

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from theexpensedb.org in the background.
             *
             * @return VehiclesTotalRunningCosts' data from theexpensedb.org as a List of DashboardReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public List<VehiclesTotalRunningCosts> loadInBackground() {
                /** ArrayList to hold the dashboard list items */
                List<VehiclesTotalRunningCosts> dashboardListItems = new ArrayList<VehiclesTotalRunningCosts>();
                VehiclesTotalRunningCosts dashboardListItem;

                CapstoneDBHelper dbHelper;
                dbHelper = new CapstoneDBHelper(getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Read the available Vehicles
                String sqlVehicles =
                        "select v._id as vehicleId, v.Name, v.distanceUnit, " +
                        "       v.volumeUnit, m.minOdo, m.maxOdo " +
                        "from vehicles as v " +
                        "left outer join ( " +
                        "      select e.vehicleId, min(e.odometer) as minOdo, " +
                        "             max(e.odometer) as maxOdo " +
                        "      from expenses as e " +
                        "      where e.expenseType = 0 " +
                        "      group by e.vehicleId " +
                        ") as m on m.vehicleId = v._id ";

                Cursor cVehicles = db.rawQuery(sqlVehicles, null);

                if (cVehicles != null && cVehicles.getCount() > 0) {

                    while (cVehicles.moveToNext()) {
                        int vehicleId = cVehicles.getInt(cVehicles.getColumnIndex("vehicleId"));
                        dashboardListItem = fetchVehiclesTotalRunningCosts(vehicleId);
                        dashboardListItems.add(dashboardListItem);
                    }

                    cVehicles.close();
                }

                db.close();
                dbHelper.close();


//                Boolean invalidateCache = loaderArgs.getBoolean(LOADER_EXTRA_IC);
//                if (invalidateCache)
                    mCachedDashboardListData = null;

                return dashboardListItems;
            } // loadInBackground

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<VehiclesTotalRunningCosts> data) {
//                mCachedDashboardListData = data;
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
    public void onLoadFinished(Loader<List<VehiclesTotalRunningCosts>> loader, List<VehiclesTotalRunningCosts> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (data == null) {
            showEmptyView();
        } else {
            showDashboardListView();
        }
        /* Update the adapters data with the new one */
        invalidateData();
        mDashboardListAdapter.setDashboardData(data);
    } // onLoadFinished

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<VehiclesTotalRunningCosts>> loader) {
        /*
         * We aren't using this method in this application, but we are required to Override
         * it to implement the LoaderCallbacks<List<DashboardReviewsListItem>> interface
         */
    }

    /**
     * This method is used when we need to reset the data
     */
    private void invalidateData() {
        mDashboardListAdapter.invalidateData();
    }

    /**
     * This method will make the View for the dashboard list visible and
     * hides the empty view.
     */
    private void showDashboardListView() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mDashboardList.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the View for the dashboard list visible and
     * hides the empty view.
     */
    private void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mDashboardList.setVisibility(View.INVISIBLE);
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param expenseId the Id from the selected expense
     */
    @Override
    public void onClick(int expenseId) {
        /* Prepare to call the detail activity, to show the expense's details */
        //Intent intent = new Intent(this, DashboardEntityActivity.class);
        //intent.putExtra(DetailActivity.EXTRA_MOVIE_ID, expenseId);
        //startActivity(intent);
    }


}


