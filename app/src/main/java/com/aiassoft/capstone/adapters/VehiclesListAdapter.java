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
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.model.Vehicle;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gvryn on 22/07/18.
 */

public class VehiclesListAdapter extends RecyclerView.Adapter<VehiclesListAdapter.VehiclesAdapterViewHolder> {

    private static final String LOG_TAG = MyApp.APP_TAG + VehiclesListAdapter.class.getSimpleName();

    /* This array holds a list of vehicle objects */
    private ArrayList<Vehicle> mVehiclesData = new ArrayList<>();

    /**
     * Defining an on-click handler to make it easy for an Activity
     * to interface with the RecyclerView
     */
    private final VehiclesAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives OnClick messages
     */
    public interface VehiclesAdapterOnClickHandler {
        void onClick(int vehicleId);
    }

    /**
     * Creates a VehiclesAdapter
     *
     * @param clickHandler The on-click handler for this adapter. This single handler
     *                     is called when an item is clicked
     */
    public VehiclesListAdapter(VehiclesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a vehicle list item
     */
    public class VehiclesAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        /* The Views to display the Vehicle's Data */
        @BindView(R.id.vehicle_image) ImageView mVehicleImage;
        @BindView(R.id.vehicle_title) TextView mVehicleTitle;

        public VehiclesAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            mVehicleImage.setOnClickListener(this);
            mVehicleTitle.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            int selectedVehicle = mVehiclesData.get(adapterPosition).getId();
            mClickHandler.onClick(selectedVehicle);
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
    public VehiclesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_list_vehicle;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new VehiclesAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the vehicle
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param vehiclesAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                contents of the item at the given position in the data set.
     * @param position                The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(VehiclesAdapterViewHolder vehiclesAdapterViewHolder, int position) {
        Vehicle vehicle = mVehiclesData.get(position);

        String imagePath = vehicle.getImage();
        String vehiclesTitle = vehicle.getTitle();

        vehiclesAdapterViewHolder.mVehicleImage.setContentDescription(vehiclesTitle);

        if (imagePath == null || imagePath.isEmpty()) {
            Picasso.with(vehiclesAdapterViewHolder.mVehicleImage.getContext())
                    .load(R.drawable.jonathan_daniels_453915_unsplash_rsz)
                    .into(vehiclesAdapterViewHolder.mVehicleImage);
        } else {
            Picasso.with(vehiclesAdapterViewHolder.mVehicleImage.getContext())
                    .load(Uri.fromFile(new File(imagePath)))
                    .placeholder(R.drawable.jonathan_daniels_453915_unsplash_rsz)
                    .error(R.drawable.missing_car_image)
                    .into(vehiclesAdapterViewHolder.mVehicleImage);
        }

        vehiclesAdapterViewHolder.mVehicleTitle.setText(vehiclesTitle);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our vehicles list
     */
    @Override
    public int getItemCount() {
        if (null == mVehiclesData) return 0;
        return mVehiclesData.size();
    }

    /**
     * This method is used to set the vehicle on a VehiclesAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new VehiclesAdapter to display it.
     *
     * @param vehiclesData The new vehicles data to be displayed.
     */
    public void setVehiclesData(List<Vehicle> vehiclesData) {
        if (vehiclesData == null) return;
        mVehiclesData.addAll(vehiclesData);
        notifyDataSetChanged();
    }

    /**
     * This method is used when we are resetting data
     */
    public void invalidateData() {
        mVehiclesData = new ArrayList<Vehicle>();
        notifyDataSetChanged();
    }

}
