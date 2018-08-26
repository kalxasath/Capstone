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
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.adapters.VideosListAdapter;
import com.aiassoft.capstone.model.VideosListItem;
import com.aiassoft.capstone.navigation.DrawerMenu;
import com.aiassoft.capstone.utilities.NetworkUtils;
import com.aiassoft.capstone.utilities.YoutubeUtils;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aiassoft.capstone.utilities.AppUtils.showSnackbar;
import static com.aiassoft.capstone.utilities.YoutubeUtils.watchYoutubeVideo;

/**
 * Created by gvryn on 26/07/18.
 */

public class SearchYoutubeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        VideosListAdapter.VideosAdapterOnClickHandler,
        SearchView.OnQueryTextListener,
        LoaderManager.LoaderCallbacks<List<VideosListItem>> {

    private static final String LOG_TAG = MyApp.APP_TAG + SearchYoutubeActivity.class.getSimpleName();


    /**
     * This ID will be used to identify the Loader responsible for loading our videos list.
     */
    public static final int VIDEOS_LOADER_ID = 0;

    private static final String STATE_SEARCH_QUERY = "STATE_SEARCH_QUERY";
    private static final String STATE_RECYCLER = "STATE_RECYCLER";
    /**
     * Used to identify the WEB URL that is being used in the loader.loadInBackground
     * to get the videos' data from youtube.com
     */
    private static final String LOADER_EXTRA_SEARCH_QUERY = "search_data";

    /* The Videos List Adapter */
    private VideosListAdapter mVideosListAdapter;


    Context mContext;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    ViewGroup mContainer;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.nav_view) android.support.design.widget.NavigationView mNavigationView;

    SearchView mSearchView;
    MenuItem mSearchMenuItem;
    String mSearchQuery;

    /* The views in the xml file */
    /** The recycler view */
    @BindView(R.id.videos_list) RecyclerView mVideosList;

    Parcelable mRecyclerState = null;

    /** The Error Message Block,
     * is used to display errors and will be hidden if there are no error
     */
    @BindView(R.id.error_message) LinearLayout mErrorMessageBlock;

    /** The view holding the error message */
    @BindView(R.id.error_message_text) TextView mErrorMessageText;

    /**
     * The ProgressBar that will indicate to the user that we are loading data.
     * It will be hidden when no data is loading.
     */
    @BindView(R.id.loading_indicator) ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_drawer);
        mContext = this;
        mSearchQuery = null;

        mContainer = this.findViewById(R.id.layout_container);
        View.inflate(this, R.layout.activity_search_youtube, mContainer);

        ButterKnife.bind(this);

        /* recovering the instance state */
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(STATE_SEARCH_QUERY, null);
            mRecyclerState = savedInstanceState.getParcelable(STATE_RECYCLER);
        }

        setSupportActionBar(mToolbar);

        initFab();

        initDrawer();

        initNavigation();

        initVideosListRecyclerView();

        if (mSearchQuery != null)
            fetchVideosList(mSearchQuery);
    }

    /** invoked when the activity may be temporarily destroyed, save the instance state here */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_SEARCH_QUERY, mSearchQuery);

        Parcelable recyclerState = mVideosList.getLayoutManager().onSaveInstanceState();
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
        //mDrawer = findViewById(R.id.drawer_layout);
        //mDrawer.setFocusable(true);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //This is not working properly
                mNavigationView.requestFocus();
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
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().findItem(R.id.nav_search_youtube).setChecked(true);
    }

    private void initVideosListRecyclerView() {
        /*
         * The gridLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a grid.
         */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);

        /* setLayoutManager associates the gridLayoutManager with our RecyclerView */
        mVideosList.setLayoutManager(gridLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mVideosList.setHasFixedSize(true);

        /*
         * The VideosListAdapter is responsible for linking our videos' data with the Views that
         * will end up displaying our video data.
         */
        mVideosListAdapter = new VideosListAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mVideosList.setAdapter(mVideosListAdapter);

        /* We will check if we are connected to the internet */
        if (! NetworkUtils.isOnline()) {
            /* We are not connected, inform the user
             * with the propriety error message
             */
            displayNetworkConnectivityError();
        }
    }

    @Override
    protected void onStop() {
        invalidateActivity();
        super.onStop();
    }

    private void invalidateActivity() {
        // destroy the loader
        getSupportLoaderManager().destroyLoader(VIDEOS_LOADER_ID);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);

        // Handle navigation view item clicks here.
        if (DrawerMenu.navigate(this, item, mNavigationView)) {
            overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
            finish();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        mSearchMenuItem = menu.findItem(R.id.action_search);

        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.youtube_search));

        mSearchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItem = item.getItemId();

        if (selectedMenuItem == R.id.action_search) {
            invalidateData();
            mSearchQuery = null;
            mRecyclerState = null;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String searchQuery) {
        invalidateData();

        /* We will check if we are connected to the internet */
        if (! NetworkUtils.isOnline()) {
            /* We are not connected, inform the user
             * with the propriety error message
             */
            displayNetworkConnectivityError();
        } else {
            invalidateOptionsMenu();
            fetchVideosList(searchQuery);
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // We aren't using this method in this application, but we are required to Override it
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    /**
     * Inform user about the Network Connectivity Error
     */
    private void displayNetworkConnectivityError() {
        showSnackbar(mContainer, R.string.error_check_your_network_connectivity);
    }


    ///////////////////////////////////////////////////////////////////////////////////////
    /**
     * Fetch the videos' data from youtube.com
     */
    private void fetchVideosList(String searchQuery) {
        /* Create a bundle to pass the web url to the loader */
        Bundle loaderArgs = new Bundle();
        loaderArgs.putString(LOADER_EXTRA_SEARCH_QUERY, searchQuery);

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<VideosListItem>> theVideoDbLoader = loaderManager.getLoader(VIDEOS_LOADER_ID);

        if (theVideoDbLoader == null) {
            loaderManager.initLoader(VIDEOS_LOADER_ID, loaderArgs, this);
        } else {
            loaderManager.restartLoader(VIDEOS_LOADER_ID, loaderArgs, this);
        }

    } // fetchVideosList

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs The WEB URL for fetching the videos' data.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<List<VideosListItem>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<List<VideosListItem>>(this) {
            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (loaderArgs == null) {
                    return;
                }

                String searchQuery = loaderArgs.getString(LOADER_EXTRA_SEARCH_QUERY);
                if (searchQuery == null || TextUtils.isEmpty(searchQuery)) {
                    /* If null or empty string is passed, return immediately */
                    return;
                } else {
                    if (! searchQuery.equals(mSearchQuery)) {
                        mSearchQuery = searchQuery;
                    }
                }


                /* If Error Message Block is visible, hide it */
                if (mErrorMessageBlock.getVisibility() == View.VISIBLE) {
                    showVideosListView();
                }
                mLoadingIndicator.setVisibility(View.VISIBLE);

                forceLoad();
            } // onStartLoading

            /**
             * This is the method of the AsyncTaskLoader that will load the data
             * from the database in the background.
             *
             * @return Videos' data from database as a List of VideosReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public List<VideosListItem> loadInBackground() {

                // Get the access type argument that is passed on loader initialization */
                String searchQuery = loaderArgs.getString(LOADER_EXTRA_SEARCH_QUERY);
                if (searchQuery == null || TextUtils.isEmpty(searchQuery)) {
                    /* If null or empty string is passed, return immediately */
                    return null;
                }

                String loaderWebUrlString = YoutubeUtils.buildHttpGoogleApisYoutubeSearchVideoUri(searchQuery).toString();
                /* try to fetch the data from the network */
                try {
                    URL theWebUrl = new URL(loaderWebUrlString);

                    /* if succeed returns a List of VideosListItem objects */
                    return YoutubeUtils.parseSearchResults(
                            NetworkUtils.getResponseFromHttpUrl(theWebUrl));

                } catch (Exception e) {
                    /* If fails returns null to significate the error */
                    e.printStackTrace();
                    return null;
                }

            } // loadInBackground

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<VideosListItem> data) {
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
    public void onLoadFinished(Loader<List<VideosListItem>> loader, List<VideosListItem> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        /* Check for error */
        if (null == data) {
            /* If an error has occurred, show the error message */
            showErrorMessage(R.string.no_video_results);
        } else {
            /* Else show the videos list */
            showVideosListView();
        }

        /* Update the adapters data with the new one */
        invalidateData();
        mVideosListAdapter.setVideosData(data);

        if (mRecyclerState != null) {
            mVideosList.getLayoutManager().onRestoreInstanceState(mRecyclerState);
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
    public void onLoaderReset(Loader<List<VideosListItem>> loader) {
        /*
         * We aren't using this method in this application, but we are required to Override
         * it to implement the LoaderCallbacks<List<VideosReviewsListItem>> interface
         */
    }

    /**
     * This method is used when we need to reset the data
     */
    private void invalidateData() {
        mVideosListAdapter.invalidateData();
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param videoId the Id from the selected video
     */
    @Override
    public void onClick(String videoId) {
        watchYoutubeVideo(mContext, videoId);
    }

    /**
     * This method will make the View for the videos list visible and
     * hides the error message block.
     */
    private void showVideosListView() {
        /* First, make sure the error block is invisible */
        mErrorMessageBlock.setVisibility(View.INVISIBLE);
        /* Then, make sure the videos list is visible */
        mVideosList.setVisibility(View.VISIBLE);
    } // showVideosListView

    /**
     * This method will make the error message visible,
     * populate the error message with the corresponding error message block,
     * and hides the video details.
     * @param errorId The error message string id
     */
    private void showErrorMessage(int errorId) {
        /* First, hide the currently visible videos list */
        mVideosList.setVisibility(View.INVISIBLE);
        /* Then, show the error block */
        mErrorMessageBlock.setVisibility(View.VISIBLE);
        /* Show the corresponding error message */
        mErrorMessageText.setText(getString(errorId));
    } // showErrorMessage


}
