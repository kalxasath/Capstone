package com.aiassoft.capstone.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.aiassoft.capstone.R;

/**
 * Created by gvryn on 29/07/18.
 */

public class Expense implements Parcelable {

    /**
     * Receive and decode whatever is inside the parcel
     */
    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    private int id;
    private int vehicleId;
    private int expenseType;
    private int subtype;
    private String date;
    private int odometer;
    private float fuelQuantity;
    private float amount;
    private String notes;

    private String vehicle;
    private String vehicleImage;

    /**
     * No args constructor for use in serialization ¯\_(ツ)_/¯
     */
    public Expense() {
    }

    public Expense(int id, int vehicleId, int expenseType, int subtype, String date, int odometer,
                   float fuelQuantity, float amount, String notes) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.expenseType = expenseType;
        this.subtype = subtype;
        this.date = date;
        this.fuelQuantity = fuelQuantity;
        this.odometer = odometer;
        this.amount = amount;
        this.notes = notes;
    }

    public Expense(int id, int vehicleId, int expenseType, int subtype, String date, int odometer,
                   float fuelQuantity, float amount, String notes, String vehicle, String vehicleImage) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.expenseType = expenseType;
        this.subtype = subtype;
        this.date = date;
        this.fuelQuantity = fuelQuantity;
        this.odometer = odometer;
        this.amount = amount;
        this.notes = notes;
        this.vehicle = vehicle;
        this.vehicleImage = vehicleImage;
    }

    protected Expense(Parcel in) {
        this.id = in.readInt();
        this.vehicleId = in.readInt();
        this.expenseType = in.readInt();
        this.subtype = in.readInt();
        this.date = in.readString();
        this.odometer = in.readInt();
        this.fuelQuantity = in.readFloat();
        this.amount = in.readFloat();
        this.notes = in.readString();

        this.vehicle = in.readString();
        this.vehicleImage = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getExpenseType() {
        return expenseType;
    }

    public String getExpenseTypeStr(Context context) {
        return context.getResources()
                .getStringArray(R.array.expenses_types)[this.expenseType];
    }

    public void setExpenseType(int expenseType) {
        this.expenseType = expenseType;
    }

    public int getSubtype() {
        return subtype;
    }

    public String getSubtypeStr(Context context) {
        switch (this.expenseType) {
            case 0:
                return context.getResources()
                        .getStringArray(R.array.refuel_expenses_subtypes)[this.subtype];
            case 1:
                return context.getResources()
                        .getStringArray(R.array.bill_expenses_subtypes)[this.subtype];
            case 2:
                return context.getResources()
                        .getStringArray(R.array.service_expenses_subtypes)[this.subtype];
        }
        return "";
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getOdometer() {
        return odometer;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }

    public float getFuelQuantity() {
        return fuelQuantity;
    }

    public void setFuelQuantity(float fuelQuantity) { this.fuelQuantity = fuelQuantity; }


    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) { this.amount = amount; }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getVehicleImage() {
        return vehicleImage;
    }

    public void setVehicleImage(String vehicleImage) {
        this.vehicleImage = vehicleImage;
    }


    /**
     * Required by Parcelable
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Puts the class fields to the parcel object
     * @param dest a Parcel object
     * @param flags
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.vehicleId);
        dest.writeInt(this.expenseType);
        dest.writeInt(this.subtype);
        dest.writeString(this.date);
        dest.writeInt(this.odometer);
        dest.writeFloat(this.fuelQuantity);
        dest.writeFloat(this.amount);
        dest.writeString(this.notes);

        dest.writeString(this.vehicle);
        dest.writeString(this.vehicleImage);
    }
}
