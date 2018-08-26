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

package com.aiassoft.capstone.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by gvryn on 25/06/18.
 *
 * The Vehicle Object, holds all the necessary information of a Vehicle
 */
public class Vehicle implements Parcelable {

    /**
     * Receive and decode whatever is inside the parcel
     */
    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    private int id;
    private String image;
    private String name;
    private String make;
    private String model;
    private String plateNo;
    private int initialMileage;
    private long distanceUnit;
    private int tankVolume;
    private long volumeUnit;
    private String notes;

    /**
     * No args constructor for use in serialization ¯\_(ツ)_/¯
     */
    public Vehicle() {
    }

    public Vehicle(int id, String image, String name, String make, String model, String plateNo,
                   int initialMileage, long distanceUnit, int tankVolume, long volumeUnit, String notes) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.make = make;
        this.model = model;
        this.plateNo = plateNo;
        this.initialMileage = initialMileage;
        this.distanceUnit = distanceUnit;
        this.tankVolume = tankVolume;
        this.volumeUnit = volumeUnit;
        this.notes = notes;
    }

    protected Vehicle(Parcel in) {
        this.id = in.readInt();
        this.image = in.readString();
        this.name = in.readString();
        this.make = in.readString();
        this.model = in.readString();
        this.plateNo = in.readString();
        this.initialMileage = in.readInt();
        this.distanceUnit = in.readLong();
        this.tankVolume = in.readInt();
        this.volumeUnit = in.readLong();
        this.name = in.readString();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
        return (int) distanceUnit;
    }

    public void setDistanceUnit(long distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public int getTankVolume() { return tankVolume; }

    public void setTankVolume(int tankVolume) {
        this.tankVolume = tankVolume;
    }

    public int getVolumeUnit() {
        return (int) volumeUnit;
    }

    public void setVolumeUnit(long volumeUnit) {
        this.volumeUnit = volumeUnit;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * returns the title of the vehicle
     * depend of the name, make, model content
     *
     * @return the title
     */
    public String getTitle() {
        if (!this.name.isEmpty()) {
            return this.name;
        } else {
            if (!make.isEmpty() && model.isEmpty()) {
                return make;
            } else if (make.isEmpty() && !model.isEmpty()) {
                return model;
            } else {
                return make + " " + model;
            }
        }
    }

    /**
     * returns the make & model concatenated as a string
     * @return the make & model string
     */
    public String getMakeModel() {
        if (this.name.isEmpty()) {
            return "";
        } else {
            if (!make.isEmpty() && model.isEmpty()) {
                return make;
            } else if (make.isEmpty() && !model.isEmpty()) {
                return model;
            } else {
                return make + " " + model;
            }
        }
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
        dest.writeString(this.image);
        dest.writeString(this.name);
        dest.writeString(this.make);
        dest.writeString(this.model);
        dest.writeString(this.plateNo);
        dest.writeInt(this.initialMileage);
        dest.writeLong(this.distanceUnit);
        dest.writeInt(this.tankVolume);
        dest.writeLong(this.volumeUnit);
        dest.writeString(this.notes);
    }
}
