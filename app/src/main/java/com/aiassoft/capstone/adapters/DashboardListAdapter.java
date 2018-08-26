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

package com.aiassoft.capstone.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.model.VehiclesTotalRunningCosts;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gvryn on 08/08/18.
 */

public class DashboardListAdapter extends RecyclerView.Adapter<DashboardListAdapter.DashboardAdapterViewHolder> {

    private static final String LOG_TAG = MyApp.APP_TAG + DashboardListAdapter.class.getSimpleName();

    /* This array holds a list of VehiclesTotalRunningCosts objects */
    private ArrayList<VehiclesTotalRunningCosts> mVehiclesTotalRunningCosts = new ArrayList<>();

    /**
     * Defining an on-click handler to make it easy for an Activity
     * to interface with the RecyclerView
     */
    private final DashboardAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives OnClick messages
     */
    public interface DashboardAdapterOnClickHandler {
        void onClick(int dashboardId);
    }

    /**
     * Creates a DashboardAdapter
     *
     * @param clickHandler The on-click handler for this adapter. This single handler
     *                     is called when an item is clicked
     */
    public DashboardListAdapter(DashboardAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a VehiclesTotalRunningCosts list item
     */
    public class DashboardAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        /* The Views to display the VehiclesTotalRunningCosts's Data */
        @BindView(R.id.vehicle_title) TextView mVehicleTitle;
        @BindView(R.id.km_driven_title) TextView mKmDrivenTitle;
        @BindView(R.id.km_driven) TextView mKmDriven;

        @BindView(R.id.refuel_title) TextView mRefuelTitle;
        @BindView(R.id.refuel_total_cost_title) TextView mRefuelTotalCostTitle;
        @BindView(R.id.refuel_total_cost) TextView mRefuelTotalCost;
        @BindView(R.id.refuel_total_qty_title) TextView mRefuelTotalQtyTitle;
        @BindView(R.id.refuel_total_qty) TextView mRefuelTotalQty;
        @BindView(R.id.refuel_total_lkm_title) TextView mRefuelTotalLkmTitle;
        @BindView(R.id.refuel_total_lkm) TextView mRefuelTotalLkm;
        @BindView(R.id.refuel_total_ckm_title) TextView mRefuelTotalCkmTitle;
        @BindView(R.id.refuel_total_ckm) TextView mRefuelTotalCkm;

        @BindView(R.id.expenses_title) TextView mExpensesTitle;
        @BindView(R.id.expense_parking_title) TextView mExpenseParkingTitle;
        @BindView(R.id.expense_parking_cost) TextView mExpenseParkingCost;
        @BindView(R.id.expense_toll_title) TextView mExpenseTollTitle;
        @BindView(R.id.expense_toll_cost) TextView mExpenseTollCost;
        @BindView(R.id.expense_insurane_title) TextView mExpenseInsuraneTitle;
        @BindView(R.id.expense_insurane_cost) TextView mExpenseInsuraneCost;
        @BindView(R.id.expense_total_title) TextView mExpenseTotalTitle;
        @BindView(R.id.expense_total_cost) TextView mExpenseTotalCost;
        @BindView(R.id.expense_ckm_title) TextView mExpenseCkmTitle;
        @BindView(R.id.expense_ckm_cost) TextView mExpenseCkmCost;

        @BindView(R.id.service_title) TextView mServiceTitle;
        @BindView(R.id.service_basic_title) TextView mServiceBasicTitle;
        @BindView(R.id.service_basic_cost) TextView mServiceBasicCost;
        @BindView(R.id.service_damage_title) TextView mServiceDamageTitle;
        @BindView(R.id.service_damage_cost) TextView mServiceDamageCost;
        @BindView(R.id.service_total_title) TextView mServiceTotalTitle;
        @BindView(R.id.service_total_cost) TextView mServiceTotalCost;
        @BindView(R.id.service_ckm_title) TextView mServiceCkmTitle;
        @BindView(R.id.service_ckm_cost) TextView mServiceCkmCost;

        public DashboardAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        /**
         * This gets called by the child views during a click
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            // this method will not be used
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout.
     * @return A new MovieVideosAdapterViewHolder that holds the View for each list item
     */
    @Override
    public DashboardAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_list_dashboard;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new DashboardAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the VehiclesTotalRunningCosts
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param viewHolder The ViewHolder which should be updated to represent the
     *                                contents of the item at the given position in the data set.
     * @param position                The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(DashboardAdapterViewHolder viewHolder, int position) {
        Context context = MyApp.getContext();
        Resources res = context.getResources();
        VehiclesTotalRunningCosts data = mVehiclesTotalRunningCosts.get(position);

        viewHolder.mVehicleTitle.setText(String.format(
                context.getString(R.string.total_running_costs), data.getName()));
        if (! data.hasData) {
            vhvSetResVisibility(viewHolder, View.GONE);
            viewHolder.mKmDrivenTitle.setVisibility(View.GONE);
            viewHolder.mKmDriven.setText(R.string.no_dashboard_data_available);
        } else {
            viewHolder.mKmDrivenTitle.setVisibility(View.VISIBLE);
            viewHolder.mKmDrivenTitle.setText(String.format(
                    context.getString(R.string.dashboard_driven_title),
                    res.getStringArray(R.array.short_distance_unit_array)[data.getDistanceUnit()]
            ));
            viewHolder.mKmDriven.setText(String.valueOf(data.getKmDriven()));

            if (! data.hasRefuelData) {
                vhvSetRefuelVisibility(viewHolder, View.GONE);
            } else {
                vhvSetRefuelVisibility(viewHolder, View.VISIBLE);
                viewHolder.mRefuelTotalCost.setText(String.format("%.2f", data.getRefuelTotalCost()));
                viewHolder.mRefuelTotalQty.setText(String.format("%.2f", data.getRefuelTotalQty()));
                viewHolder.mRefuelTotalLkmTitle.setText(String.format(
                        context.getString(R.string.dashboard_fuel_per100_title),
                        res.getStringArray(R.array.short_volume_unit_array)[data.getVolumeUnit()],
                        res.getStringArray(R.array.short_distance_unit_array)[data.getDistanceUnit()]
                ));
                viewHolder.mRefuelTotalLkm.setText(data.getRefuelTotalLkm());
                viewHolder.mRefuelTotalCkmTitle.setText(String.format(
                        context.getString(R.string.dashboard_cost_per100_title),
                        res.getStringArray(R.array.short_distance_unit_array)[data.getDistanceUnit()]
                ));
                viewHolder.mRefuelTotalCkm.setText(data.getRefuelTotalCkm());
            }

            if (! data.hasExpensesData) {
                vhvSetExpensesVisibility(viewHolder, View.GONE);
            } else {
                vhvSetExpensesVisibility(viewHolder, View.VISIBLE);
                viewHolder.mExpenseParkingCost.setText(String.format("%.2f", data.getExpenseParkingCost()));
                viewHolder.mExpenseTollCost.setText(String.format("%.2f", data.getExpenseTollCost()));
                viewHolder.mExpenseInsuraneCost.setText(String.format("%.2f", data.getExpenseInsuranceCost()));
                viewHolder.mExpenseTotalCost.setText(String.format("%.2f", data.getExpenseTotalCost()));
                viewHolder.mExpenseCkmTitle.setText(String.format(
                        context.getString(R.string.dashboard_cost_per100_title),
                        res.getStringArray(R.array.short_distance_unit_array)[data.getDistanceUnit()]
                ));
                viewHolder.mExpenseCkmCost.setText(data.getExpenseCkmCost());
            }

            if (! data.hasServiceData) {
                vhvSetServiceVisibility(viewHolder, View.GONE);
            } else {
                vhvSetServiceVisibility(viewHolder, View.VISIBLE);
                viewHolder.mServiceBasicCost.setText(String.format("%.2f", data.getServiceBasicCost()));
                viewHolder.mServiceDamageCost.setText(String.format("%.2f", data.getServiceDamageCost()));
                viewHolder.mServiceTotalCost.setText(String.format("%.2f", data.getServiceTotalCost()));
                viewHolder.mServiceCkmTitle.setText(String.format(
                        context.getString(R.string.dashboard_cost_per100_title),
                        res.getStringArray(R.array.short_distance_unit_array)[data.getDistanceUnit()]
                ));
                viewHolder.mServiceCkmCost.setText(data.getServiceCkmCost());
            }
        }
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our VehiclesTotalRunningCosts list
     */
    @Override
    public int getItemCount() {
        if (null == mVehiclesTotalRunningCosts) return 0;
        return mVehiclesTotalRunningCosts.size();
    }

    /**
     * This method is used to set the VehiclesTotalRunningCosts on a DashboardAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new DashboardAdapter to display it.
     *
     * @param vehiclesTotalRunningCosts The new VehiclesTotalRunningCosts data to be displayed.
     */
    public void setDashboardData(List<VehiclesTotalRunningCosts> vehiclesTotalRunningCosts) {
        if (vehiclesTotalRunningCosts == null) return;
        mVehiclesTotalRunningCosts.addAll(vehiclesTotalRunningCosts);
        notifyDataSetChanged();
    }

    /**
     * This method is used when we are resetting data
     */
    public void invalidateData() {
        mVehiclesTotalRunningCosts = new ArrayList<VehiclesTotalRunningCosts>();
        notifyDataSetChanged();
    }

    ///////////////// ViewHolder Views (vhv) Visibility

    /**
     * Set Refuel, Expenses, Service views to gone
     */
    private void vhvSetResVisibility(DashboardAdapterViewHolder viewHolder, int visibility) {
        vhvSetRefuelVisibility(viewHolder, visibility);
        vhvSetExpensesVisibility(viewHolder, visibility);
        vhvSetServiceVisibility(viewHolder, visibility);
    }

    private void vhvSetRefuelVisibility(DashboardAdapterViewHolder viewHolder, int visibility) {
        viewHolder.mRefuelTitle.setVisibility(visibility);
        viewHolder.mRefuelTotalCostTitle.setVisibility(visibility);
        viewHolder.mRefuelTotalCost.setVisibility(visibility);
        viewHolder.mRefuelTotalQtyTitle.setVisibility(visibility);
        viewHolder.mRefuelTotalQty.setVisibility(visibility);
        viewHolder.mRefuelTotalLkmTitle.setVisibility(visibility);
        viewHolder.mRefuelTotalLkm.setVisibility(visibility);
        viewHolder.mRefuelTotalCkmTitle.setVisibility(visibility);
        viewHolder.mRefuelTotalCkm.setVisibility(visibility);
    }

    private void vhvSetExpensesVisibility(DashboardAdapterViewHolder viewHolder, int visibility) {
        viewHolder.mExpensesTitle.setVisibility(visibility);
        viewHolder.mExpenseParkingTitle.setVisibility(visibility);
        viewHolder.mExpenseParkingCost.setVisibility(visibility);
        viewHolder.mExpenseTollTitle.setVisibility(visibility);
        viewHolder.mExpenseTollCost.setVisibility(visibility);
        viewHolder.mExpenseInsuraneTitle.setVisibility(visibility);
        viewHolder.mExpenseInsuraneCost.setVisibility(visibility);
        viewHolder.mExpenseTotalTitle.setVisibility(visibility);
        viewHolder.mExpenseTotalCost.setVisibility(visibility);
        viewHolder.mExpenseCkmTitle.setVisibility(visibility);
        viewHolder.mExpenseCkmCost.setVisibility(visibility);
    }

    private void vhvSetServiceVisibility(DashboardAdapterViewHolder viewHolder, int visibility) {
        viewHolder.mServiceTitle.setVisibility(visibility);
        viewHolder.mServiceBasicTitle.setVisibility(visibility);
        viewHolder.mServiceBasicCost.setVisibility(visibility);
        viewHolder.mServiceDamageTitle.setVisibility(visibility);
        viewHolder.mServiceDamageCost.setVisibility(visibility);
        viewHolder.mServiceTotalTitle.setVisibility(visibility);
        viewHolder.mServiceTotalCost.setVisibility(visibility);
        viewHolder.mServiceCkmTitle.setVisibility(visibility);
        viewHolder.mServiceCkmCost.setVisibility(visibility);
    }

}
