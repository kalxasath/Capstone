package com.aiassoft.capstone.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aiassoft.capstone.R;
import com.aiassoft.capstone.adapters.ExpensesListAdapter;
import com.aiassoft.capstone.adapters.ExpensesListAdapter;
import com.aiassoft.capstone.data.ExpensesContract;
import com.aiassoft.capstone.data.VehiclesContract;
import com.aiassoft.capstone.model.Expense;
import com.aiassoft.capstone.navigation.DrawerMenu;
import com.aiassoft.capstone.utilities.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gvryn on 26/07/18.
 */

public class ExpensesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ExpensesListAdapter.ExpensesAdapterOnClickHandler,
        LoaderCallbacks<List<Expense>> {

    public static final int EXPENSES_LOADER_ID = 0;

    /**
     * Used to identify if we have to invalidate the cache
     */
    private static final String LOADER_EXTRA_IC = "invalidate_the_cache";

    Context mContext;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    ViewGroup mContainer;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.nav_view) android.support.design.widget.NavigationView mNavView;

    /** The recycler view */
    @BindView(R.id.expenses_list)
    RecyclerView mExpensesList;

    /** The recycler's view adapter */
    ExpensesListAdapter mExpensesListAdapter;

    @BindView(R.id.empty_view)
    TextView mEmptyView;

    @BindView(R.id.loading_indicator)
    ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_drawer);
        mContext = this;

        // for fragment see https://stackoverflow.com/questions/2395769/how-to-programmatically-add-views-to-views
        mContainer = this.findViewById(R.id.layout_container);
        View.inflate(this, R.layout.activity_expenses_list, mContainer);

        ButterKnife.bind(this);

        mNavView = this.findViewById(R.id.nav_view);

        setSupportActionBar(mToolbar);

        initFab();

        initDrawer();

        initNavigation();

        initExpensesListRecyclerView();

        fetchExpensesList(true);
    }

    private void initFab() {
        if (mFab != null) {
            mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp));

            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ExpensesEntityActivity.class);
                    startActivity(intent);
                }
            });
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
        if (DrawerMenu.navigate(this, item.getItemId()))
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
     * Fetch the expenses' data from the database
     */
    private void fetchExpensesList(Boolean invalidateCache) {
        /* Create a bundle to pass parameters to the loader */
        Bundle loaderArgs = new Bundle();
        loaderArgs.putBoolean(LOADER_EXTRA_IC, invalidateCache);

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Expense>> theExpenseDbLoader = loaderManager.getLoader(EXPENSES_LOADER_ID);

        if (theExpenseDbLoader == null) {
            loaderManager.initLoader(EXPENSES_LOADER_ID, loaderArgs, this);
        } else {
            loaderManager.restartLoader(EXPENSES_LOADER_ID, loaderArgs, this);
        }

    } // fetchExpensesList

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs The WEB URL for fetching the expenses' data.
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
                if (loaderArgs == null) {
                    return;
                }

                Boolean invalidateCache = loaderArgs.getBoolean(LOADER_EXTRA_IC);
                if (invalidateCache)
                    mCachedExpensesListData = null;


                if (mCachedExpensesListData != null) {
                    deliverResult(mCachedExpensesListData);
                } else {

                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    forceLoad();
                }
            } // onStartLoading

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from theexpensedb.org in the background.
             *
             * @return Expenses' data from theexpensedb.org as a List of ExpensesReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public List<Expense> loadInBackground() {

                Boolean invalidateCache = loaderArgs.getBoolean(LOADER_EXTRA_IC);
                if (invalidateCache)
                    mCachedExpensesListData = null;

                Uri uri = ExpensesContract.ExpensesEntry.CONTENT_URI;
                uri = uri.buildUpon().build();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);


                if (cursor != null && cursor.getCount() != 0) {
                    /** ArrayList to hold the expenses list items */
                    List<Expense> expensesListItems = new ArrayList<Expense>();
                    Expense expensesListItem;

                    while (cursor.moveToNext()) {
                        expensesListItem = new Expense();
                        expensesListItem.setId(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry._ID)));
                        expensesListItem.setVehicleId(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID)));
                        expensesListItem.setExpenseType(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE)));
                        expensesListItem.setSubtype(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE)));
                        expensesListItem.setDate(cursor.getString(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE)));
                        expensesListItem.setOdometer(cursor.getInt(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER)));
                        expensesListItem.setAmount(cursor.getFloat(cursor.getColumnIndex(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT)));

                        String stringId = "" + expensesListItem.getVehicleId();
                        uri = VehiclesContract.VehiclesEntry.CONTENT_URI;
                        uri = uri.buildUpon().appendPath(stringId).build();
                        Cursor vcursor = getContentResolver().query(uri, null, null, null, null);

                        if (vcursor ==  null || vcursor.getCount() == 0) {
                            expensesListItem.setVehicle(getString(R.string.deleted_vehicle));
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
     * @param expenseId the Id from the selected expense
     */
    @Override
    public void onClick(int expenseId) {
        /* Prepare to call the detail activity, to show the expense's details */
        Intent intent = new Intent(this, ExpensesEntityActivity.class);
        //intent.putExtra(DetailActivity.EXTRA_MOVIE_ID, expenseId);
        startActivity(intent);
    }

}
