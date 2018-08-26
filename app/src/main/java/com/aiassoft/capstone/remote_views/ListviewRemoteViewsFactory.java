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

package com.aiassoft.capstone.remote_views;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.model.VehiclesTotalRunningCosts;


import static com.aiassoft.capstone.data.DBQueries.fetchVehiclesTotalRunningCosts;
import static com.aiassoft.capstone.utilities.PrefUtils.getWidgetVehicleId;

/*
  Created by gvryn on 15/08/18.
 */

/**
 * Populates the listview in the widget with the vehicle's costs
 */
public class ListviewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = MyApp.APP_TAG + ListviewRemoteViewsFactory.class.getSimpleName();

    private Context mContext;

    private int mVehicleId;
    private VehiclesTotalRunningCosts mData;


    public ListviewRemoteViewsFactory(Context applicationContext, Intent intent) {

        mContext = applicationContext;


        /* Ger widget's Id */
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

        /* Get widget's vehicleId */
        mVehicleId = getWidgetVehicleId(appWidgetId);

        /* Get Vehicle's total running costs */
        mData = fetchVehiclesTotalRunningCosts(mVehicleId);
//        Toast.makeText(mContext, "Vehicle " + mData.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {

    }

    /** called on start and when notifyAppWidgetViewDataChanged is called */
    @Override
    public void onDataSetChanged() {
        /* Get Vehicle's total running costs */
        mData = fetchVehiclesTotalRunningCosts(mVehicleId);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        /* we have mData for one vehicle, so we always return 1 */
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Construct a RemoteViews item based on the app widget item XML file, and set the
        // text based on the position.
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_list_widget);

        Resources res = mContext.getResources();

        rv.setTextViewText(R.id.km_driven_title, String.valueOf(mData.getKmDriven()));

        if (! mData.hasData) {
            rv.setViewVisibility(R.id.ll_refuel_data, View.GONE);
            rv.setViewVisibility(R.id.ll_expenses_data, View.GONE);
            rv.setViewVisibility(R.id.ll_service_data, View.GONE);

            rv.setViewVisibility(R.id.km_driven_title, View.GONE);
            rv.setTextViewText(R.id.km_driven, mContext.getString(R.string.no_dashboard_data_available));
        } else {
            rv.setTextViewText(R.id.km_driven_title, String.format(
                    mContext.getString(R.string.dashboard_driven_title),
                    res.getStringArray(R.array.short_distance_unit_array)[mData.getDistanceUnit()]
            ));
            rv.setTextViewText(R.id.km_driven, String.valueOf(mData.getKmDriven()));

            if (! mData.hasRefuelData) {
                rv.setViewVisibility(R.id.ll_refuel_data, View.GONE);
            } else {
                rv.setTextViewText(R.id.refuel_total_cost, String.format("%.2f", mData.getRefuelTotalCost()));
                rv.setTextViewText(R.id.refuel_total_qty, String.format("%.2f", mData.getRefuelTotalQty()));
                rv.setTextViewText(R.id.refuel_total_lkm_title, String.format(
                        mContext.getString(R.string.dashboard_fuel_per100_title),
                        res.getStringArray(R.array.short_volume_unit_array)[mData.getVolumeUnit()],
                        res.getStringArray(R.array.short_distance_unit_array)[mData.getDistanceUnit()]
                ));
                rv.setTextViewText(R.id.refuel_total_lkm, mData.getRefuelTotalLkm());
                rv.setTextViewText(R.id.refuel_total_ckm_title, String.format(
                        mContext.getString(R.string.dashboard_cost_per100_title),
                        res.getStringArray(R.array.short_distance_unit_array)[mData.getDistanceUnit()]
                ));
                rv.setTextViewText(R.id.refuel_total_ckm, mData.getRefuelTotalCkm());
            }

            if (! mData.hasExpensesData) {
                rv.setViewVisibility(R.id.ll_expenses_data, View.GONE);
            } else {
                rv.setTextViewText(R.id.expense_parking_cost, String.format("%.2f", mData.getExpenseParkingCost()));
                rv.setTextViewText(R.id.expense_toll_cost, String.format("%.2f", mData.getExpenseTollCost()));
                rv.setTextViewText(R.id.expense_insurane_cost, String.format("%.2f", mData.getExpenseInsuranceCost()));
                rv.setTextViewText(R.id.expense_total_cost, String.format("%.2f", mData.getExpenseTotalCost()));
                rv.setTextViewText(R.id.expense_ckm_title, String.format(
                        mContext.getString(R.string.dashboard_cost_per100_title),
                        res.getStringArray(R.array.short_distance_unit_array)[mData.getDistanceUnit()]
                ));
                rv.setTextViewText(R.id.expense_ckm_cost, mData.getExpenseCkmCost());
            }

            if (! mData.hasServiceData) {
                rv.setViewVisibility(R.id.ll_service_data, View.GONE);
            } else {
                rv.setTextViewText(R.id.service_basic_cost, String.format("%.2f", mData.getServiceBasicCost()));
                rv.setTextViewText(R.id.service_damage_cost, String.format("%.2f", mData.getServiceDamageCost()));
                rv.setTextViewText(R.id.service_total_cost, String.format("%.2f", mData.getServiceTotalCost()));
                rv.setTextViewText(R.id.service_ckm_title, String.format(
                        mContext.getString(R.string.dashboard_cost_per100_title),
                        res.getStringArray(R.array.short_distance_unit_array)[mData.getDistanceUnit()]
                ));
                rv.setTextViewText(R.id.service_ckm_cost, mData.getServiceCkmCost());
            }
        }

        // Return the RemoteViews object.
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
