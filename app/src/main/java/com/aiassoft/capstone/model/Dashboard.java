package com.aiassoft.capstone.model;

import android.content.Context;

import com.aiassoft.capstone.R;

/**
 * Created by gvryn on 08/08/18.
 */

public class Dashboard {
    private int vehicleId;
    private String name;
    private int kmDriven;

    private float refuelTotalCost;
    private float refuelTotalQty;
    private String refuelTotalLkm;
    private String refuelTotalCkm;

    private float expenseParkingCost;
    private float expenseTollCost;
    private float expenseInsuranceCost;
    private float expenseTotalCost;
    private String expenseCkmCost;

    private float serviceBasicCost;
    private float serviceDamageCost;
    private float serviceTotalCost;
    private String serviceCkmCost;

    /**
     * No args constructor for use in serialization ¯\_(ツ)_/¯
     */
    public Dashboard() {
    }

    public Dashboard(int vehicleId, String name, int kmDriven,
                     float refuelTotalCost, float refuelTotalQty, String refuelTotalLkm,
                     String refuelTotalCkm, float expenseParkingCost, float expenseTollCost,
                     float expenseInsuranceCost, float expenseTotalCost, String expenseCkmCost,
                     float serviceBasicCost, float serviceDamageCost, float serviceTotalCost,
                     String serviceCkmCost ) {

        this.vehicleId = vehicleId;
        this.name = name;
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
                refuelTotalQty += qty;
                refuelTotalCost += amount;
                break;
            case 1:
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
            refuelTotalLkm = String.format("%.2f", (refuelTotalQty / kmDriven));
            refuelTotalCkm = String.format("%.2f", (refuelTotalCost / kmDriven));

            expenseCkmCost = String.format("%.2f", (expenseTotalCost / kmDriven));

            serviceCkmCost = String.format("%.2f", (serviceTotalCost/ kmDriven));
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

}
