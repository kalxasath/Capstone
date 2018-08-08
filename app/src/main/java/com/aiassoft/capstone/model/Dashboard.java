package com.aiassoft.capstone.model;

import android.content.Context;

import com.aiassoft.capstone.R;

/**
 * Created by gvryn on 08/08/18.
 */

public class Dashboard {
    private int vehicleId;
    private String name;
    private int expenseType;
    private int subtype;
    private int minOdometer;
    private int maxOdometer;
    private float fuelQuantity;
    private float amount;

    /**
     * No args constructor for use in serialization ¯\_(ツ)_/¯
     */
    public Dashboard() {
    }

    public Dashboard(int vehicleId, String name, int expenseType, int subtype, int minOdometer,
                     int maxOdometer, float fuelQuantity, float amount, String notes) {
        this.vehicleId = vehicleId;
        this.name = name;
        this.expenseType = expenseType;
        this.subtype = subtype;
        this.minOdometer = minOdometer;
        this.maxOdometer = maxOdometer;
        this.fuelQuantity = fuelQuantity;
        this.amount = amount;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getMinOdometer() {
        return minOdometer;
    }

    public void setMinOdometer(int minOdometer) {
        this.minOdometer = minOdometer;
    }

    public int getMaxOdometer() {
        return maxOdometer;
    }

    public void setMaxOdometer(int maxOdometer) {
        this.maxOdometer = maxOdometer;
    }

    public float getFuelQuantity() {
        return fuelQuantity;
    }

    public void setFuelQuantity(float fuelQuantity) { this.fuelQuantity = fuelQuantity; }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) { this.amount = amount; }


}
