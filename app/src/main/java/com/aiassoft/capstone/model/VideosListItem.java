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
 * Created by gvryn on 01/08/18.
 *
 *
 * The Videos List Item Object, holds all the necessary information of a list item
 */
public class VideosListItem {

    private static final String LOG_TAG = MyApp.APP_TAG + VideosListItem.class.getSimpleName();

    private String videoId;
    private String title;
    private String description;
    private String thumbnail;
    private int page;

    /**
     * No args constructor for use in serialization
     */
    public VideosListItem() {
    }

    public VideosListItem(String videoId, String title, String description, String thumbnail, int page) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.page = page;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
