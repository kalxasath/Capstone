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

package com.aiassoft.capstone.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.aiassoft.capstone.BuildConfig;
import com.aiassoft.capstone.Const;
import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.model.VideosListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.aiassoft.capstone.utilities.AppUtils.showSnackbar;
import static com.aiassoft.capstone.utilities.AppUtils.showToast;

/**
 * These utilities will be used for all YouTube helper methods
 */
public class YoutubeUtils {

    private static final String LOG_TAG = MyApp.APP_TAG + YoutubeUtils.class.getSimpleName();

    private YoutubeUtils() {
        throw new AssertionError(R.string.no_instances_for_you);
    }

    /**
     * youtube definitions
     */
    public static final String YOUTUBE_API_KEY = BuildConfig.YOUTUBE_API_KEY;
    public static final String YOUTUBE_APP_URI = "vnd.youtube:";
    public static final String YOUTUBE_WATCH_URL = "https://www.youtube.com/watch";
    public static final String YOUTUBE_PARAM_WATCH_VIDEO = "v";

    // https://www.googleapis.com/youtube/v3/videos?id=7lCDEYXw3mM&key=??&fields=items(id,snippet(channelId,title,categoryId),statistics)&part=snippet,statistics
    public static final String GOOGLEAPIS_YOUTUBE_URL = "https://www.googleapis.com/youtube/v3";
    public static final String YOUTUBE_CMD_SEARCH = "/search";
    public static final String YOUTUBE_CMD_VIDEO = "/video";
    public static final String YOUTUBE_PARAM_KEY = "key";
    public static final String YOUTUBE_PARAM_QUERY = "q";
    public static final String YOUTUBE_PARAM_PART = "part";
    public static final String YOUTUBE_PARAM_MAX_RESULTS = "maxResults";
    public static final String YOUTUBE_PARAM_FIELDS = "fields";


    ////// Build Http Requests //////

    public static Uri buildHttpGoogleApisYoutubeSearchVideoUri(String query) {
        Uri builtUri = Uri.parse(GOOGLEAPIS_YOUTUBE_URL + YOUTUBE_CMD_SEARCH)
                .buildUpon()
                .appendQueryParameter(YOUTUBE_PARAM_QUERY, query)
                .appendQueryParameter(YOUTUBE_PARAM_PART, "snippet")
                .appendQueryParameter(YOUTUBE_PARAM_MAX_RESULTS, "10")
                .appendQueryParameter(YOUTUBE_PARAM_KEY, BuildConfig.YOUTUBE_API_KEY)
                .build();
        return builtUri;
    }

    /**
     * Builds the URL to watch the Youtube video
     * over web browser
     *
     * @param key The Video key, to be watched
     * @return    The URI to watch the video
     */
    public static Uri buildHttpYoutubeWatchVideoUri(String key) {
        Uri builtUri = Uri.parse(YOUTUBE_WATCH_URL
        ).buildUpon()
                .appendQueryParameter(YOUTUBE_PARAM_WATCH_VIDEO, key)
                .build();
        return builtUri;
    }

    /**
     * Builds the URL to watch the Youtube video
     * over the youtube app
     *
     * @param key The Video key, to be watched
     * @return    The URI to watch the video
     */
    public static Uri buildAppYoutubeWatchVideoUri(String key) {
        Uri builtUri = Uri.parse(YOUTUBE_APP_URI + key).buildUpon().build();

        return builtUri;
    }

    ////// Parse JSON Data //////

    public static ArrayList<VideosListItem> parseSearchResults(String json) {
        //Log.d(LOG_TAG, json);

        /* ArrayList to hold the search result videos */
        ArrayList<VideosListItem> videosListItems = new ArrayList<>();
        VideosListItem videosListItem;

        try {
            /* Creates a new JSONObject with name/value mappings from the json string */
            JSONObject searchResults = new JSONObject(json);
            JSONObject pageInfo =  searchResults.optJSONObject("pageInfo");

            /* Get the movie reviews' data array */
            JSONArray items =  searchResults.optJSONArray("items");

            int maxResults = items.length();
            for (int i=0; i<maxResults; i++) {
                /* Get the video's data */
                JSONObject item = items.optJSONObject(i);
                JSONObject id = item.optJSONObject("id");
                JSONObject video = item.optJSONObject("snippet");
                JSONObject thumbnails = video.optJSONObject("thumbnails");
                JSONObject high = thumbnails.optJSONObject("high");

                videosListItem = new VideosListItem();
                videosListItem.setVideoId(id.optString("videoId"));
                videosListItem.setTitle(video.optString("title"));
                videosListItem.setDescription(video.optString("description"));

//                if (i==0) videosListItem.setThumbnail("https://url"); else
                    videosListItem.setThumbnail(high.optString("url"));

                videosListItem.setPage(pageInfo.optInt("resultsPerPage"));

                videosListItems.add(videosListItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return videosListItems;
    }


    ////// Start Youtube Player //////

    /**
     * Play the video via the youtube app, if the app doesn't exists
     * it will play it via the web browser
     * @param context the context to be used to start the activity
     * @param videoId the video id
     */
    public static void watchYoutubeVideo(Context context, String videoId){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, buildAppYoutubeWatchVideoUri(videoId));
        if (appIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(appIntent);
        } else {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, buildHttpYoutubeWatchVideoUri(videoId));
            if (webIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(webIntent);
            } else {
                showToast(context.getString(R.string.youtube_player_not_found));
            }
        }
    }


}