package com.aiassoft.capstone.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;
import com.aiassoft.capstone.model.Dashboard;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gvryn on 08/08/18.
 */

public class DashboardListAdapter extends RecyclerView.Adapter<DashboardListAdapter.DashboardAdapterViewHolder> {

    private static final String LOG_TAG = MyApp.APP_TAG + DashboardListAdapter.class.getSimpleName();

    /* This array holds a list of Dashboard objects */
    private ArrayList<Dashboard> mDashboardData = new ArrayList<>();

    /**
     * Defining an on-click handler to make it easy for an Activity
     * to interface with the RecyclerView
     */
    private final DashboardAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives OnClick messages
     */
    public interface DashboardAdapterOnClickHandler {
        void onClick(int DashboardId);
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
     * Cache of the children views for a Dashboard list item
     */
    public class DashboardAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        /* The Views to display the Dashboard's Data */
        @BindView(R.id.vehicle_title) TextView mVehicleTitle;
        @BindView(R.id.dashboard_date) TextView mDashboardDate;
        @BindView(R.id.dashboard_type) TextView mDashboardType;
        @BindView(R.id.dashboard_subtype) TextView mDashboardSubtype;
        @BindView(R.id.dashboard_amount) TextView mDashboardAmount;

        public DashboardAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
//            int adapterPosition = getAdapterPosition();
//            int selectedDashboard = mDashboardData.get(adapterPosition).getMinOdometer();
//            mClickHandler.onClick(selectedDashboard);
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
     * position. In this method, we update the contents of the ViewHolder to display the Dashboard
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param viewHolder The ViewHolder which should be updated to represent the
     *                                contents of the item at the given position in the data set.
     * @param position                The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(DashboardAdapterViewHolder viewHolder, int position) {
//        viewHolder.mVehicleTitle.setText(mDashboardData.get(position).getVehicle());
//        viewHolder.mDashboardDate.setText(mDashboardData.get(position).getName());
//        viewHolder.mDashboardType.setText(mDashboardData.get(position)
//                .getDashboardTypeStr(viewHolder.mDashboardType.getContext()));
//        viewHolder.mDashboardSubtype.setText(mDashboardData.get(position)
//                .getSubtypeStr(viewHolder.mDashboardSubtype.getContext()));
//        viewHolder.mDashboardAmount.setText(String.valueOf(mDashboardData.get(position).getAmount()));
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our Dashboard list
     */
    @Override
    public int getItemCount() {
        if (null == mDashboardData) return 0;
        return mDashboardData.size();
    }

    /**
     * This method is used to set the Dashboard on a DashboardAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new DashboardAdapter to display it.
     *
     * @param DashboardData The new Dashboard data to be displayed.
     */
    public void setDashboardData(List<Dashboard> DashboardData) {
        if (DashboardData == null) return;
        mDashboardData.addAll(DashboardData);
        notifyDataSetChanged();
    }

    /**
     * This method is used when we are resetting data
     */
    public void invalidateData() {
        mDashboardData = new ArrayList<Dashboard>();
        notifyDataSetChanged();
    }

}
