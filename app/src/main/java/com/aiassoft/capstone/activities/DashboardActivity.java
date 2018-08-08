package com.aiassoft.capstone.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
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
import com.aiassoft.capstone.adapters.DashboardListAdapter;
import com.aiassoft.capstone.data.CapstoneDBHelper;
import com.aiassoft.capstone.data.VehiclesContract;
import com.aiassoft.capstone.model.Dashboard;
import com.aiassoft.capstone.navigation.DrawerMenu;
import com.aiassoft.capstone.utilities.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aiassoft.capstone.MyApp.getContext;

/**
 * Created by gvryn on 25/07/18.
 */

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DashboardListAdapter.DashboardAdapterOnClickHandler,
        LoaderCallbacks<List<Dashboard>> {

    //TODO: in every image add the tag android:contentDescription="movie_poster_content_description"

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
    @BindView(R.id.dashboard_list)
    RecyclerView mDashboardList;

    /** The recycler's view adapter */
    DashboardListAdapter mDashboardListAdapter;

    @BindView(R.id.empty_view)
    TextView mEmptyView;

    @BindView(R.id.loading_indicator)
    ProgressBar mLoadingIndicator;

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
        Loader<List<Dashboard>> theDashboardDbLoader = loaderManager.getLoader(DASHBOARD_LOADER_ID);

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
    public Loader<List<Dashboard>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<List<Dashboard>>(this) {

            /* This Dashboard array will hold and help cache our dashboard list data */
            List<Dashboard> mCachedDashboardListData = null;

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
             * @return Dashboard' data from theexpensedb.org as a List of DashboardReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public List<Dashboard> loadInBackground() {
                CapstoneDBHelper dbHelper;

                dbHelper = new CapstoneDBHelper(getContext());

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String sql = "select e.vehicleId, v.Name, e.expenseType, e.subtype, " +
                        "m.mnOdo, m.mxOdo, sum(e.fuelQty) as sFuelQty, " +
                        "sum(e.amount) as sAmount " +
                        "from expenses as e " +
                        "inner join vehicles as v on v._id = e.vehicleId " +
                        "inner join (" +
                        "      select e.vehicleId, min(e.odometer) as mnOdo, max(e.odometer) as mxOdo " +
                        "      from expenses as e " +
                        "      where e.date between ? and ? " +
                        "        and e.expenseType = 0 " +
                        "      group by e.vehicleId " +
                        ") m on m.vehicleId = e.vehicleId " +
                        "where e.date between ? and ? " +
                        "group by e.vehicleId, v.Name, e.expenseType, e.subtype " +
                        "order by e.vehicleId ";
                //Cursor cursor = db.rawQuery(sql, null);
                Cursor cursor = db.rawQuery(sql, new String[] {"2018-01-01", "2018-12-31", "2018-01-01", "2018-12-31"});

                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex("name"));
//                        int minOdometer = cursor.getInt(cursor.getColumnIndex("mbOdo"));
//                        int maxOdometer = cursor.getInt(cursor.getColumnIndex("mxOdo"));
//                        float fuelQty = cursor.getFloat(cursor.getColumnIndex("sFuelQty"));
//                        float amount = cursor.getFloat(cursor.getColumnIndex("sAmount"));
                    }

                    cursor.close();
                }

//                Boolean invalidateCache = loaderArgs.getBoolean(LOADER_EXTRA_IC);
//                if (invalidateCache)
                    mCachedDashboardListData = null;

//                Uri uri = DashboardContract.DashboardEntry.CONTENT_URI;
//                uri = uri.buildUpon().build();
//                Cursor cursor = getContentResolver().query(uri, null, null, null,
//                        DashboardContract.DashboardEntry.COLUMN_NAME_DATE + " desc");


//                if (cursor != null && cursor.getCount() != 0) {
//                    /** ArrayList to hold the dashboard list items */
//                    List<Dashboard> dashboardListItems = new ArrayList<Dashboard>();
//                    Dashboard dashboardListItem;
//
//                    while (cursor.moveToNext()) {
//                        dashboardListItem = new Dashboard();
//                        dashboardListItem.setMinOdometer(cursor.getInt(cursor.getColumnIndex(DashboardContract.DashboardEntry._ID)));
//                        dashboardListItem.setVehicleId(cursor.getInt(cursor.getColumnIndex(DashboardContract.DashboardEntry.COLUMN_NAME_VEHICLE_ID)));
//                        dashboardListItem.setDashboardType(cursor.getInt(cursor.getColumnIndex(DashboardContract.DashboardEntry.COLUMN_NAME_EXPENSE_TYPE)));
//                        dashboardListItem.setSubtype(cursor.getInt(cursor.getColumnIndex(DashboardContract.DashboardEntry.COLUMN_NAME_SUBTYPE)));
//                        dashboardListItem.setName(cursor.getString(cursor.getColumnIndex(DashboardContract.DashboardEntry.COLUMN_NAME_DATE)));
//                        dashboardListItem.setMaxOdometer(cursor.getInt(cursor.getColumnIndex(DashboardContract.DashboardEntry.COLUMN_NAME_ODOMETER)));
//                        dashboardListItem.setAmount(cursor.getFloat(cursor.getColumnIndex(DashboardContract.DashboardEntry.COLUMN_NAME_AMOUNT)));

//                        String stringId = "" + dashboardListItem.getVehicleId();
//                        uri = VehiclesContract.VehiclesEntry.CONTENT_URI;
//                        uri = uri.buildUpon().appendPath(stringId).build();
//                        Cursor vcursor = getContentResolver().query(uri, null, null, null, null);
//
//                        if (vcursor ==  null || vcursor.getCount() == 0) {
//                            dashboardListItem.setVehicle(getString(R.string.deleted_vehicle) + ":" + stringId);
//                        } else {
//                            vcursor.moveToFirst();
//                            String s = vcursor.getString(vcursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME));
//                            dashboardListItem.setVehicle(vcursor.getString(vcursor.getColumnIndex(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME)));
//                        }
//                        vcursor.close();
//
//                        dashboardListItems.add(dashboardListItem);
//                    }
//
//                    cursor.close();
//
//                    return dashboardListItems;
//                }

                return null;
            } // loadInBackground

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<Dashboard> data) {
                mCachedDashboardListData = data;
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
    public void onLoadFinished(Loader<List<Dashboard>> loader, List<Dashboard> data) {
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
    public void onLoaderReset(Loader<List<Dashboard>> loader) {
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


