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

package com.aiassoft.capstone.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.aiassoft.capstone.Const;
import com.aiassoft.capstone.R;

/**
 * Created by gvryn on 24/06/2018.
 * Defining the VehiclesContract class
 * In this table will be stored the Vehicles
 */
public class VehiclesContract {


    // Define the possible paths for accessing data in this contract
    // This is the path for the "Vehicles" directory, that will be appended
    // to the base content URI
    public static final String PATH_VEHICLES = "Vehicles";

    /**
     * We never need to create an instance of the contract class
     * because the contract is simply a class filled,
     * with DB related constants that are all static.
     */
    private VehiclesContract() {
        throw new AssertionError(R.string.no_instances_for_you);
    }

    /**
     * Inner class that defines the vehicles table contents
     */
    public static final class VehiclesEntry implements BaseColumns {
        /** This final content URI will include the scheme, the authority,
         *  and our VEHICLES path.
         */
        public static final Uri CONTENT_URI = Const.BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_VEHICLES).build();

        public static final String TABLE_NAME = "vehicles";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_MAKE = "make";
        public static final String COLUMN_NAME_MODEL = "model";
        public static final String COLUMN_NAME_VIN = "vin";
        public static final String COLUMN_NAME_PLATE_NO = "plateNo";
        public static final String COLUMN_NAME_INITIALMILEAGE = "initialMileage";
        public static final String COLUMN_NAME_DINSTANCE_UNIT = "distanceUnit";
        public static final String COLUMN_NAME_PURCHASEDATE = "purchaseDate";
        public static final String COLUMN_NAME_PURCHASEMILEAGE = "purchaseMileage";
        public static final String COLUMN_NAME_PURCHASEPRICE = "purchasePrice";
        public static final String COLUMN_NAME_SELLDATE = "sellDate";
        public static final String COLUMN_NAME_SEELPRICE = "sellPrice";
        public static final String COLUMN_NAME_TANKVOLUME = "tankVolume";
        public static final String COLUMN_NAME_VOLUME_UNIT = "volumeUnit";
        public static final String COLUMN_NAME_FUEL_TYPE = "fuelType";
        public static final String COLUMN_NAME_NOTES = "notes";
    }

}
