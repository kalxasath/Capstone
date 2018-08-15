/**
 * Copyright (C) 2018 by George Vrynios
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

package com.aiassoft.capstone.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.adapters.VehiclesListAdapter;
import com.aiassoft.capstone.data.VehiclesContract;
import com.aiassoft.capstone.model.Vehicle;
import com.aiassoft.capstone.utilities.AppUtils;
import com.aiassoft.capstone.utilities.PrefUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gvryn on 14/08/18.
 */

/**
 * This activity is started from the widget provider every time
 * the user creates a new widget in the home screen.
 * This activity allows the users to select the vehicle
 * that the widget will show the data
 */
public class VehicleWidgetConfigureActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Vehicle>>,
        VehiclesListAdapter.VehiclesAdapterOnClickHandler
        {

    private static final String LOG_TAG = MyApp.APP_TAG + VehicleWidgetConfigureActivity.class.getSimpleName();

    public static final int VEHICLES_LOADER_ID = 0;

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private AppWidgetManager widgetManager;

    /**
     * Used to identify if we have to invalidate the cache
     */
    private static final String LOADER_EXTRA_IC = "invalidate_the_cache";

    private static Context mContext;

    /** The views in the xml file */
    /** The recycler view */
    @BindView(R.id.vehicles_list) RecyclerView mVehiclesList;

    /** The recycler's view adapter */
    VehiclesListAdapter mVehiclesListAdapter;

    @BindView(R.id.empty_view) TextView mEmptyView;

    @BindView(R.id.loading_indicator) ProgressBar mLoadingIndicator;


    @Override
    public void onCreate(Bundle bundle) {
        mContext = this;

        super.onCreate(bundle);
        setContentView(R.layout.activity_vehicle_widget_configuration);
        ButterKnife.bind(this);

        setResult(RESULT_CANCELED);

        widgetManager = AppWidgetManager.getInstance(this);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }


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
         * The VehiclesListAdapter is responsible for linking our vehicles' data with the Views that
         * will end up displaying our vehicle data.
         */
        mVehiclesListAdapter = new VehiclesListAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mVehiclesList.setAdapter(mVehiclesListAdapter);



		/* Fetch vehicles' data from the database */
        fetchVehiclesList(true);
    }


    /**
     * Fetch the vehicles' data from the database
     */
    private void fetchVehiclesList(Boolean invalidateCache) {
        /* Create a bundle to pass parameters to the loader */
        Bundle loaderArgs = new Bundle();
        loaderArgs.putBoolean(LOADER_EXTRA_IC, invalidateCache);

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Vehicle>> theVehicleDbLoader = loaderManager.getLoader(VEHICLES_LOADER_ID);

        if (theVehicleDbLoader == null) {
            loaderManager.initLoader(VEHICLES_LOADER_ID, loaderArgs, this);
        } else {
            loaderManager.restartLoader(VEHICLES_LOADER_ID, loaderArgs, this);
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
                if (loaderArgs == null) {
                    return;
                }

                Boolean invalidateCache = loaderArgs.getBoolean(LOADER_EXTRA_IC);
                if (invalidateCache)
                    mCachedVehiclesListData = null;


                if (mCachedVehiclesListData != null) {
                    deliverResult(mCachedVehiclesListData);
                } else {

                    mLoadingIndicator.setVisibility(View.VISIBLE);

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

                Boolean invalidateCache = loaderArgs.getBoolean(LOADER_EXTRA_IC);
                if (invalidateCache)
                    mCachedVehiclesListData = null;

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

                        vehiclesListItem.setName(vehiclesListItem.getName() + ": " + String.valueOf(vehiclesListItem.getId()));

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
     * @param vehicleId the Id from the selected vehicle
     */
    @Override
    public void onClick(int vehicleId) {
        PrefUtils.setWidgetVehicleId(mAppWidgetId, vehicleId);

        VehicleWidgetProvider.sendRefreshBroadcast(mContext);

        Intent resultIntent = new Intent();
        /** Set the results as expected from a 'configure activity'. */
        resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
