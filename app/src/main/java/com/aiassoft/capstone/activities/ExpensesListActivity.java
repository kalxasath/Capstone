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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.adapters.ExpensesListAdapter;
import com.aiassoft.capstone.data.ExpensesContract;
import com.aiassoft.capstone.data.VehiclesContract;
import com.aiassoft.capstone.model.Expense;
import com.aiassoft.capstone.navigation.DrawerMenu;
import com.aiassoft.capstone.utilities.AppUtils;
import com.aiassoft.capstone.widgets.VehicleWidgetProvider;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gvryn on 26/07/18.
 */

public class ExpensesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        ExpensesListAdapter.ExpensesAdapterOnClickHandler,
        LoaderCallbacks<List<Expense>> {

    private static final String LOG_TAG = MyApp.APP_TAG + ExpensesListActivity.class.getSimpleName();

    public static final int EXPENSES_LOADER_ID = 0;

    // Entity state indicator, used to know entity's return state
    public static final int CHILD_INSERT = 0;
    public static final int CHILD_UPDATE = 1;

    private static final String STATE_RECYCLER = "STATE_RECYCLER";

    Context mContext;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    ViewGroup mContainer;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.nav_view) android.support.design.widget.NavigationView mNavigationView;

    /** The recycler view */
    @BindView(R.id.expenses_list) RecyclerView mExpensesList;

    /** The recycler's view adapter */
    ExpensesListAdapter mExpensesListAdapter;

    Parcelable mRecyclerState = null;

    @BindView(R.id.empty_view) TextView mEmptyView;

    @BindView(R.id.loading_indicator) ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_drawer);
        mContext = this;

        mContainer = this.findViewById(R.id.layout_container);
        View.inflate(this, R.layout.activity_expenses_list, mContainer);

        ButterKnife.bind(this);

        /* recovering the instance state */
        if (savedInstanceState != null) {
            mRecyclerState = savedInstanceState.getParcelable(STATE_RECYCLER);
        }

        setSupportActionBar(mToolbar);

        initFab();

        initDrawer();

        initNavigation();

        initExpensesListRecyclerView();

        if (mRecyclerState != null)
            mExpensesList.getLayoutManager().onRestoreInstanceState(mRecyclerState);

        fetchExpensesList(true);
    }

    private void initFab() {
        if (mFab != null) {
            mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp));
            mFab.setContentDescription(getString(R.string.add_new_expense));
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
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().findItem(R.id.nav_expenses_list).setChecked(true);
    }

    private void initExpensesListRecyclerView() {
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
        mExpensesList.setLayoutManager(gridLayoutManager);

        mExpensesList.setItemAnimator(new DefaultItemAnimator());

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mExpensesList.setHasFixedSize(true);

        /*
         * The RecipesListAdapter is responsible for linking our recipes' data with the Views that
         * will end up displaying our recipe data.
         */
        mExpensesListAdapter = new ExpensesListAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mExpensesList.setAdapter(mExpensesListAdapter);

    }

    /** invoked when the activity may be temporarily destroyed, save the instance state here */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Parcelable recyclerState = mExpensesList.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(STATE_RECYCLER, recyclerState);

        /* call superclass to save any view hierarchy */
        super.onSaveInstanceState(outState);
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
     * Fetch the expenses' data from the database
     */
    private void fetchExpensesList(Boolean invalidateCache) {
        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Expense>> theExpenseDbLoader = loaderManager.getLoader(EXPENSES_LOADER_ID);

        if (theExpenseDbLoader == null) {
            loaderManager.initLoader(EXPENSES_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(EXPENSES_LOADER_ID, null, this);
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
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<List<Expense>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<List<Expense>>(this) {

            /* This Expense array will hold and help cache our expenses list data */
            List<Expense> mCachedExpensesListData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (mCachedExpensesListData != null) {
                    deliverResult(mCachedExpensesListData);
                } else {

                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load the data
             * from the database in the background.
             *
             * @return Expenses' data from database as a List of ExpensesReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public List<Expense> loadInBackground() {

                Uri uri = ExpensesContract.ExpensesEntry.CONTENT_URI;
                uri = uri.buildUpon().build();
                Cursor cursor = getContentResolver().query(uri, null, null, null,
                        ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE + " desc");

                if (cursor != null && cursor.getCount() > 0) {
                    /* ArrayList to hold the expenses list items */
                    List<Expense> expensesListItems = new ArrayList<Expense>();
                    Expense expensesListItem;

                    while (cursor.moveToNext()) {
                        expensesListItem = new Expense();
                        expensesListItem.setId(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry._ID)));
                        expensesListItem.setVehicleId(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID)));
                        expensesListItem.setExpenseType(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE)));
                        expensesListItem.setSubtype(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE)));
                        expensesListItem.getYearMonthDay().setDate(cursor.getString(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE)));
                        expensesListItem.setOdometer(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER)));
                        expensesListItem.setAmount(cursor.getFloat(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT)));

                        String stringId = "" + expensesListItem.getVehicleId();
                        uri = VehiclesContract.VehiclesEntry.CONTENT_URI;
                        uri = uri.buildUpon().appendPath(stringId).build();
                        Cursor vcursor = getContentResolver().query(uri, null, null, null, null);

                        if (vcursor ==  null || vcursor.getCount() == 0) {
                            expensesListItem.setVehicle(getString(R.string.deleted_vehicle) + ":" + stringId);
                        } else {
                            vcursor.moveToFirst();
                            String s = vcursor.getString(vcursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME));
                            expensesListItem.setVehicle(vcursor.getString(vcursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME)));
                        }
                        vcursor.close();

                        expensesListItems.add(expensesListItem);
                    }

                    cursor.close();

                    return expensesListItems;
                }

                return null;
            } // loadInBackground

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<Expense> data) {
                mCachedExpensesListData = data;
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
    public void onLoadFinished(Loader<List<Expense>> loader, List<Expense> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (data == null) {
            showEmptyView();
        } else {
            showExpensesListView();
        }
        /* Update the adapters data with the new one */
        invalidateData();
        mExpensesListAdapter.setExpensesData(data);

        if (mRecyclerState != null) {
            mExpensesList.getLayoutManager().onRestoreInstanceState(mRecyclerState);
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
    public void onLoaderReset(Loader<List<Expense>> loader) {
        /*
         * We aren't using this method in this application, but we are required to Override
         * it to implement the LoaderCallbacks<List<ExpensesReviewsListItem>> interface
         */
    }

    /**
     * This method is used when we need to reset the data
     */
    private void invalidateData() {
        mExpensesListAdapter.invalidateData();
    }

    /**
     * This method will make the View for the expenses list visible and
     * hides the empty view.
     */
    private void showExpensesListView() {
        mEmptyView.setVisibility(View.INVISIBLE);
        mExpensesList.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the View for the expenses list visible and
     * hides the empty view.
     */
    private void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mExpensesList.setVisibility(View.INVISIBLE);
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * the ExpensesEntityActivity is called with parameter the expenseId
     * to start the entity for edit or delete
     *
     * @param expenseId the Id from the selected expense
     */
    @Override
    public void onClick(int expenseId) {
        /* Prepare to call the detail activity, to show the expense's details
          for edit or delete
         */
        Intent intent = new Intent(this, ExpensesEntityActivity.class);
        intent.putExtra(ExpensesEntityActivity.EXTRA_EXPENSES_ID, expenseId);
        startActivityForResult(intent, CHILD_UPDATE);
        overridePendingTransition(R.anim.exit, R.anim.entry);
    }

    /**
     * this method is for responding to clicks from the fab
     *
     * the ExpensesEntityActivity is called without parameters
     * to start an empty entity for new insert
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, ExpensesEntityActivity.class);
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
            getSupportLoaderManager().restartLoader(EXPENSES_LOADER_ID, null, this);

            // update the home screen widgets
            VehicleWidgetProvider.sendRefreshBroadcast(mContext);
        }
    }
}
