package com.aiassoft.capstone.model;

/**
 * Created by gvryn on 08/08/18.
 */

public class VehiclesTotalRunningCosts {
    private int vehicleId;
    private String name;
    private int distanceUnit;
    private int volumeUnit;
    public boolean hasData;
    private int kmDriven;

    public boolean hasRefuelData;
    private float refuelTotalCost;
    private float refuelTotalQty;
    private String refuelTotalLkm;
    private String refuelTotalCkm;

    public boolean hasExpensesData;
    private float expenseParkingCost;
    private float expenseTollCost;
    private float expenseInsuranceCost;
    private float expenseTotalCost;
    private String expenseCkmCost;

    public boolean hasServiceData;
    private float serviceBasicCost;
    private float serviceDamageCost;
    private float serviceTotalCost;
    private String serviceCkmCost;

    /**
     * No args constructor for use in serialization ¯\_(ツ)_/¯
     */
    public VehiclesTotalRunningCosts() {
        this.hasData = false;
        this.hasRefuelData = false;
        this.hasExpensesData = false;
        this.hasServiceData = false;
    }

    public VehiclesTotalRunningCosts(int vehicleId, String name, int distanceUnit, int volumeUnit, int kmDriven,
                                     float refuelTotalCost, float refuelTotalQty, String refuelTotalLkm,
                                     String refuelTotalCkm, float expenseParkingCost, float expenseTollCost,
                                     float expenseInsuranceCost, float expenseTotalCost, String expenseCkmCost,
                                     float serviceBasicCost, float serviceDamageCost, float serviceTotalCost,
                                     String serviceCkmCost ) {

        this.vehicleId = vehicleId;
        this.name = name;
        this.distanceUnit = distanceUnit;
        this.volumeUnit = volumeUnit;
        this.kmDriven = kmDriven;

        this.refuelTotalQty = refuelTotalQty;
        this.refuelTotalCost = refuelTotalCost;
        this.refuelTotalLkm = refuelTotalLkm;
        this.refuelTotalCkm = refuelTotalCkm;

        this.expenseParkingCost = expenseParkingCost;
        this.expenseTollCost = expenseTollCost;
        this.expenseInsuranceCost = expenseInsuranceCost;
        this.expenseTotalCost = expenseTotalCost;
        this.expenseCkmCost = expenseCkmCost;

        this.serviceBasicCost = serviceBasicCost;
        this.serviceDamageCost = serviceDamageCost;
        this.serviceTotalCost = serviceTotalCost;
        this.serviceCkmCost = serviceCkmCost;
    }

    public void addExpense(int expenseType, int subtype, float qty, float amount) {
        switch (expenseType) {
            case 0:
                this.hasRefuelData = true;
                refuelTotalQty += qty;
                refuelTotalCost += amount;
                break;
            case 1:
                this.hasExpensesData = true;
                switch (subtype) {
                    case 0:
                        expenseParkingCost += amount;
                        break;
                    case 1:
                        expenseTollCost += amount;
                        break;
                    case 2:
                        expenseInsuranceCost += amount;
                        break;
                }
                break;
            case 2:
                this.hasServiceData = true;
                switch (subtype) {
                    case 0:
                        serviceBasicCost += amount;
                        break;
                    case 1:
                        serviceDamageCost += amount;
                        break;
                }
                break;
        }
    }

    public void calcTotals() {
        expenseTotalCost = expenseParkingCost + expenseTollCost + expenseInsuranceCost;
        serviceTotalCost = serviceBasicCost + serviceDamageCost;

        if (kmDriven > 0) {
            refuelTotalLkm = String.format("%.2f", (refuelTotalQty / kmDriven * 100));
            refuelTotalCkm = String.format("%.2f", (refuelTotalCost / kmDriven * 100));

            expenseCkmCost = String.format("%.2f", (expenseTotalCost / kmDriven * 100));

            serviceCkmCost = String.format("%.2f", (serviceTotalCost/ kmDriven * 100));
        } else {
            refuelTotalLkm = "0.00";
            refuelTotalCkm = "0.00";
            expenseCkmCost = "0.00";
            serviceCkmCost = "0.00";
        }
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

    public int getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(int distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public int getVolumeUnit() {
        return volumeUnit;
    }

    public void setVolumeUnit(int volumeUnit) {
        this.volumeUnit = volumeUnit;
    }

    public int getKmDriven() {
        return kmDriven;
    }

    public void setKmDriven(int kmDriven) {
        this.kmDriven = kmDriven;
    }

    public float getRefuelTotalCost() {
        return refuelTotalCost;
    }

    public void setRefuelTotalCost(float refuelTotalCost) { this.refuelTotalCost = refuelTotalCost; }

    public float getRefuelTotalQty() {
        return refuelTotalQty;
    }

    public void setRefuelTotalQty(float refuelTotalQty) { this.refuelTotalQty = refuelTotalQty; }

    public String getRefuelTotalLkm() {
        return refuelTotalLkm;
    }

    public void setRefuelTotalLkm(String refuelTotalLkm) {
        this.refuelTotalLkm = refuelTotalLkm;
    }


    public String getRefuelTotalCkm() {
        return refuelTotalCkm;
    }

    public void setRefuelTotalCkm(String refuelTotalCkm) {
        this.refuelTotalCkm = refuelTotalCkm;
    }

    public float getExpenseParkingCost() {
        return expenseParkingCost;
    }

    public void setExpenseParkingCost(float expenseParkingCost) { this.expenseParkingCost = expenseParkingCost; }

    public float getExpenseTollCost() {
        return expenseTollCost;
    }

    public void setExpenseTollCost(float expenseTollCost) { this.expenseTollCost = expenseTollCost; }

    public float getExpenseInsuranceCost() {
        return expenseInsuranceCost;
    }

    public void setExpenseInsuranceCost(float expenseInsuranceCost) {
        this.expenseInsuranceCost = expenseInsuranceCost;
    }

    public float getExpenseTotalCost() {
        return expenseTotalCost;
    }

    public void setExpenseTotalCost(float expenseTotalCost) {
        this.expenseTotalCost = expenseTotalCost;
    }

    public String getExpenseCkmCost() {
        return expenseCkmCost;
    }

    public void setExpenseCkmCost(String expenseCkmCost) {
        this.expenseCkmCost = expenseCkmCost;
    }

    public float getServiceBasicCost() {
        return serviceBasicCost;
    }

    public void setServiceBasicCost(float serviceBasicCost) {
        this.serviceBasicCost = serviceBasicCost;
    }

    public float getServiceDamageCost() {
        return serviceDamageCost;
    }

    public void setServiceDamageCost(float serviceDamageCost) {
        this.serviceDamageCost = serviceDamageCost;
    }

    public float getServiceTotalCost() {
        return serviceTotalCost;
    }

    public void setServiceTotalCost(float serviceTotalCost) {
        this.serviceTotalCost = serviceTotalCost;
    }

    public String getServiceCkmCost() {
        return serviceCkmCost;
    }

    public void setServiceCkmCost(String serviceCkmCost) {
        this.serviceCkmCost = serviceCkmCost;
    }

    public boolean getHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public boolean getHasRefuelData() {
        return hasRefuelData;
    }

    public void setHasRefuelData(boolean hasRefuelData) {
        this.hasRefuelData = hasRefuelData;
    }

    public boolean getHasExpensesData() {
        return hasExpensesData;
    }

    public void setHasExpensesData(boolean hasExpensesData) {
        this.hasExpensesData = hasExpensesData;
    }

    public boolean getHasServiceData() {
        return hasServiceData;
    }

    public void setHasServiceData(boolean hasServiceData) {
        this.hasServiceData = hasServiceData;
    }

}
