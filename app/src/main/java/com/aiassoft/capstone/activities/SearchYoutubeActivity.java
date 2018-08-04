package com.aiassoft.capstone.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.adapters.VideosListAdapter;
import com.aiassoft.capstone.model.VideosListItem;
import com.aiassoft.capstone.navigation.DrawerMenu;
import com.aiassoft.capstone.utilities.NetworkUtils;
import com.aiassoft.capstone.utilities.YoutubeUtils;
import com.google.android.gms.common.util.JsonUtils;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aiassoft.capstone.utilities.AppUtils.showSnackbar;

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
    /**
     * Used to identify the WEB URL that is being used in the loader.loadInBackground
     * to get the videos' data from youtube.com
     */
    private static final String LOADER_EXTRA = "search_url";

    /* The Videos List Adapter */
    private VideosListAdapter mVideosListAdapter;


    Context mContext;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    ViewGroup mContainer;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.nav_view) android.support.design.widget.NavigationView mNavView;

    SearchView mSearchView;

    /** The views in the xml file */
    /** The recycler view */
    @BindView(R.id.videos_list) RecyclerView mVideosList;

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

        mContainer = this.findViewById(R.id.layout_container);
        View.inflate(this, R.layout.activity_search_youtube, mContainer);

        ButterKnife.bind(this);

        mNavView = this.findViewById(R.id.nav_view);

        setSupportActionBar(mToolbar);

        initFab();

        initDrawer();

        initNavigation();

        initVideosListRecyclerView();

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

    private void initVideosListRecyclerView() {
        /*
         * The gridLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a grid.
         */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

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
            showSnackbar(mContainer, R.string.error_check_your_network_connectivity);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);

        mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");

        mSearchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
//        Snackbar.make(mSearchView, "TextSubmit " + query, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
//        Toast.makeText(this, "TextSubmit " + query, Toast.LENGTH_LONG).show();

        invalidateData();
        fetchVideosList();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Snackbar.make(mSearchView, "TextChange " + newText, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Toast.makeText(this, "TextChange " + newText, Toast.LENGTH_LONG).show();
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////
    /**
     * Fetch the videos' data from youtube.com
     */
    private void fetchVideosList() {
        /* Create a bundle to pass the web url to the loader */
        Bundle loaderArgs = new Bundle();
        //loaderArgs.putString(LOADER_EXTRA, MyApp.videosListSortBy.getAccessType().toString());

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
    @Override
    public Loader<List<VideosListItem>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<List<VideosListItem>>(this) {

            /* This VideosListItem array will hold and help cache our videos list data */
            List<VideosListItem> mCachedVideosListData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (loaderArgs == null) {
                    return;
                }

                if (mCachedVideosListData != null) {
                    deliverResult(mCachedVideosListData);
                } else {
                    /* If Error Message Block is visible, hide it */
                    if (mErrorMessageBlock.getVisibility() == View.VISIBLE) {
                        showVideosListView();
                    }
                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    forceLoad();
                }
            } // onStartLoading

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from thevideodb.org in the background.
             *
             * @return Videos' data from thevideodb.org as a List of VideosReviewsListItem.
             *         null if an error occurs
             */
            @Override
            public List<VideosListItem> loadInBackground() {

                // Get the access type argument that is passed on loader initialization */
//                String accessType = loaderArgs.getString(LOADER_EXTRA);
//                if (accessType == null || TextUtils.isEmpty(accessType)) {
//                    /* If null or empty string is passed, return immediately */
//                    return null;
//                }

                String loaderWebUrlString = YoutubeUtils.buildHttpGoogleApisYoutubeSearchVideoUri("Peugeot 307").toString();
                /** try to fetch the data from the network */
                try {
                    URL theWebUrl = new URL(loaderWebUrlString);

                    /** if succeed returns a List of VideosListItem objects */
                    return YoutubeUtils.parseSearchResults(
                            NetworkUtils.getResponseFromHttpUrl(theWebUrl));

                } catch (Exception e) {
                    /** If fails returns null to significate the error */
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
                mCachedVideosListData = data;
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

        /* Update the adapters data with the new one */
        mVideosListAdapter.setVideosData(data);

        /* Check for error */
        if (null == data) {
            /* If an error has occurred, show the error message */
            showErrorMessage(R.string.no_video_results);
        } else {
            /* Else show the videos list */
            showVideosListView();
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
        /* Prepare to call the detail activity, to show the video's details */
        // TODO: call video player activity ? see the original code how to play video
//        Intent intent = new Intent(this, DetailActivity.class);
//        intent.putExtra(DetailActivity.EXTRA_MOVIE_ID, videoId);
//        startActivity(intent);
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

    /**
     * Called when a tap occurs in the refresh button
     * @param view The view which reacted to the click
     */
    public void onRefreshButtonClick(View view) {
        /* Again check if we are connected to the internet */
        if (NetworkUtils.isOnline()) {
            /* If the network connectivity is restored
             * show the Videos List to hide the error block, and
             * fetch videos' data from the internet
             */
            showVideosListView();
            fetchVideosList();
        }
    } // onRefreshButtonClick

}
