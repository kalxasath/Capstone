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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.adapters.DashboardListAdapter;
import com.aiassoft.capstone.model.VehiclesTotalRunningCosts;
import com.aiassoft.capstone.navigation.DrawerMenu;
import com.aiassoft.capstone.utilities.AppUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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

    private static final String LOG_TAG = MyApp.APP_TAG + DashboardActivity.class.getSimpleName();

    public static final int DASHBOARD_LOADER_ID = 0;

    private static final String STATE_RECYCLER = "STATE_RECYCLER";

    Context mContext;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    ViewGroup mContainer;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.nav_view) android.support.design.widget.NavigationView mNavigationView;

    /** The recycler view */
    @BindView(R.id.dashboard_list) RecyclerView mDashboardList;

    /** The recycler's view adapter */
    DashboardListAdapter mDashboardListAdapter;

    Parcelable mRecyclerState = null;

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

        /* recovering the instance state */
        if (savedInstanceState != null) {
            mRecyclerState = savedInstanceState.getParcelable(STATE_RECYCLER);
        }

        setSupportActionBar(mToolbar);

        initFab();

        initDrawer();

        initNavigation();

        initAdView();

        initDashboardListRecyclerView();

        fetchDashboardList();
    }

    /** invoked when the activity may be temporarily destroyed, save the instance state here */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Parcelable recyclerState = mDashboardList.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(STATE_RECYCLER, recyclerState);

        /* call superclass to save any view hierarchy */
        super.onSaveInstanceState(outState);
    }

    private void initFab() {
        if (mFab != null) {
            mFab.setVisibility(View.GONE);
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
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().findItem(R.id.nav_dashboard).setChecked(true);
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
    protected void onStop() {
        invalidateActivity();
        super.onStop();
    }

    private void invalidateActivity() {
        // destroy the loader
        getSupportLoaderManager().destroyLoader(DASHBOARD_LOADER_ID);
        // destroy the ads
        mAdView.destroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);

        // Handle navigation view item clicks here.
        if (DrawerMenu.navigate(this, item, mNavigationView)) {
//            overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
            finish();
        }

        return true;
    }

    /**
     * Fetch the dashboard' data from the database
     */
    private void fetchDashboardList() {
        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<VehiclesTotalRunningCosts>> theDashboardDbLoader = loaderManager.getLoader(DASHBOARD_LOADER_ID);

        if (theDashboardDbLoader == null) {
            loaderManager.initLoader(DASHBOARD_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(DASHBOARD_LOADER_ID, null, this);
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
            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                mLoadingIndicator.setVisibility(View.VISIBLE);

                forceLoad();
            } // onStartLoading

            /**
             * This is the method of the AsyncTaskLoader that will load the data
             * from the database in the background.
             *
             * @return VehiclesTotalRunningCosts' data from database as a List of DashboardReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public List<VehiclesTotalRunningCosts> loadInBackground() {
                return fetchVehiclesTotalRunningCosts();
            } // loadInBackground

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<VehiclesTotalRunningCosts> data) {
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

        if (data == null || data.size() == 0) {
            showEmptyView();
        } else {
            showDashboardListView();
        }
        /* Update the adapters data with the new one */
        invalidateData();
        mDashboardListAdapter.setDashboardData(data);

        if (mRecyclerState != null) {
            mDashboardList.getLayoutManager().onRestoreInstanceState(mRecyclerState);
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
     * @param
     */
    @Override
    public void onClick(int dashboardId) {
        // We aren't using this method in this application
    }


}


