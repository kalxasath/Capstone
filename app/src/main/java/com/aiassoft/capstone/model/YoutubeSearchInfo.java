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

package com.aiassoft.capstone.model;

import com.aiassoft.capstone.MyApp;

/**
 * Created by gvryn on 04/08/18.
 *
 *
 * The Videos List Item Object, holds all the necessary information of a list item
 */
public class YoutubeSearchInfo {

    private static final String LOG_TAG = MyApp.APP_TAG + YoutubeSearchInfo.class.getSimpleName();

    private String nextPageToken;
    private int totalResults;
    private int resultsPerPage;
    private int currentPage;

    /**
     * No args constructor for use in serialization
     */
    public YoutubeSearchInfo() {
    }

    /**
     * Constructor to initialize all the class fields from the parameters
     * @param nextPageToken
     * @param totalResults
     * @param resultsPerPage
     */
    public YoutubeSearchInfo(String nextPageToken, int totalResults, int resultsPerPage, int currentPage) {
        this.nextPageToken = nextPageToken;
        this.totalResults = totalResults;
        this.resultsPerPage = resultsPerPage;
        this.currentPage = currentPage;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
