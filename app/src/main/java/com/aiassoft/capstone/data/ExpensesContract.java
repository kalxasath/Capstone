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

package com.aiassoft.capstone.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.aiassoft.capstone.Const;
import com.aiassoft.capstone.R;

/**
 * Created by gvryn on 24/06/2018.
 *
 * Defining the ExpensesContract class
 * In this table will be stored the Expenses
 */
public class ExpensesContract {


    // Define the possible paths for accessing data in this contract
    // This is the path for the "Expenses" directory, that will be appended
    // to the base content URI
    public static final String PATH_EXPENSES = "Expenses";

    /**
     * We never need to create an instance of the contract class
     * because the contract is simply a class filled,
     * with DB related constants that are all static.
     */
    private ExpensesContract() {
        throw new AssertionError(R.string.no_instances_for_you);
    }

    /**
     * Inner class that defines the events table contents
     */
    public static final class ExpensesEntry implements BaseColumns {
        /** This final content URI will include the scheme, the authority,
         *  and our EXPENSES path.
         */
        public static final Uri CONTENT_URI = Const.BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EXPENSES).build();

        public static final String TABLE_NAME = "expenses";

        public static final String COLUMN_NAME_VEHICLE_ID = "vehicleId";
		/**
		 * Expenses Types
		 * Refuel, Bill, Service
		 */
        public static final String COLUMN_NAME_EXPENSE_TYPE = "expenseType";
		/**
		 * Subtypes for Expenses
		 * Refuel: Full, Partial
		 * Bill: Accessories, Insurance, Wash, Parking, Toll, Road Tax, MOT, Others
		 * Service: Basic Service, Major Service, Change Oil, Change Tires
		 */
        public static final String COLUMN_NAME_SUBTYPE = "subtype";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_ODOMETER = "odometer";
        public static final String COLUMN_NAME_FUEL_QUANTITY = "fuelQty";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_NOTES = "notes";
    }

}
