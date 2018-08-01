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

    private int id;
    private String posterPath;

    /**
     * No args constructor for use in serialization
     */
    public VideosListItem() {
    }

    /**
     * Constructor to initialize all the class fields from the parameters
     * @param id         The Video Id
     * @param posterPath The Poster Path / video poster image thumbnail
     */
    public VideosListItem(int id, int page, String posterPath) {
        this.id = id;
        this.posterPath = posterPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

}
