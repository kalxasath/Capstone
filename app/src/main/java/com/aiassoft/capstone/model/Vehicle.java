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

package com.aiassoft.capstone.model;

/**
 * Created by gvryn on 25/06/18.
 *
 * The Vehicle Object, holds all the necessary information of a Vehicle
 */
public class Vehicle {
    private int id;
    private String name;
    private String make;
    private String model;
    private String vin;
    private String plateNo;
    private int initialMileage;
    private int distanceUnit;
    private String purchaseDate;
    private int purchaseMileage;
    private float purchasePrice;
    private String sellDate;
    private float sellPrice;
    private int tankVolume;
    private int fuelType;
    private String notes;

    /**
     * No args constructor for use in serialization ¯\_(ツ)_/¯
     */
    public Vehicle() {
    }

    public Vehicle(int id, String name, String make, String model, String vin, String plateNo,
                   int initialMileage, int distanceUnit, String purchaseDate, int purchaseMileage,
                   float purchasePrice, String sellDate, float sellPrice, int tankVolume,
                   int fuelType, String notes) {
        this.id = id;
        this.name = name;
        this.make = make;
        this.model = model;
        this.vin = vin;
        this.plateNo = plateNo;
        this.initialMileage = initialMileage;
        this.distanceUnit = distanceUnit;
        this.purchaseDate = purchaseDate;
        this.purchaseMileage = purchaseMileage;
        this.purchasePrice = purchasePrice;
        this.sellDate = sellDate;
        this.sellPrice = sellPrice;
        this.tankVolume = tankVolume;
        this.fuelType = fuelType;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public int getInitialMileage() {
        return initialMileage;
    }

    public void setInitialMileage(int initialMileage) {
        this.initialMileage = initialMileage;
    }

    public int getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(int distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getPurchaseMileage() {
        return purchaseMileage;
    }

    public void setPurchaseMileage(int purchaseMileage) {
        this.purchaseMileage = purchaseMileage;
    }

    public float getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getSellDate() {
        return sellDate;
    }

    public void setSellDate(String sellDate) {
        this.sellDate = sellDate;
    }

    public float getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(float sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getTankVolume() {
        return tankVolume;
    }

    public void setTankVolume(int tankVolume) {
        this.tankVolume = tankVolume;
    }

    public int getFuelType() {
        return fuelType;
    }

    public void setFuelType(int fuelType) {
        this.fuelType = fuelType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
