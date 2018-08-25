package com.aiassoft.capstone.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aiassoft.capstone.MyApp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gvryn on 24/08/18.
 */

public class YearMonthDay implements Parcelable {

    private static final String LOG_TAG = MyApp.APP_TAG + YearMonthDay.class.getSimpleName();

    /**
     * Receive and decode whatever is inside the parcel
     */
    public static final Creator<YearMonthDay> CREATOR = new Creator<YearMonthDay>() {
        @Override
        public YearMonthDay createFromParcel(Parcel in) {
            return new YearMonthDay(in);
        }

        @Override
        public YearMonthDay[] newArray(int size) {
            return new YearMonthDay[size];
        }
    };

    private int year;
    private int month;
    private int day;

    /**
     * No args constructor for use in serialization
     */
    public YearMonthDay() {
    }

    public YearMonthDay(Date date) {
        setDate(date);
    }

    /**
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     */
    public YearMonthDay(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public YearMonthDay(String date) {
        splitDate(date);
    }

    protected YearMonthDay(Parcel in) {
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
    }

    /**
     * Returns the date pattern that the app uses to store internal
     * to convert a Date date in a date String
     * @return the apps date pattern as yyyyMMdd
     */
    private static String getAppLocalizedPattern() {
        return "yyyyMMdd";
    }

    private void splitDate(String date) {
        this.year = Integer.parseInt(date.substring(0, 4));
        this.month = Integer.parseInt(date.substring(4, 6));
        this.day = Integer.parseInt(date.substring(6));
    }

    private String joinData() {
        String date = String.valueOf(this.year) +
                (this.month < 10 ? "0" : "") + String.valueOf(this.month) +
                (this.day < 10 ? "0" : "") + String.valueOf(this.day);

        return date;
    }

    public int getYear() { return year; }

    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }

    public void setMonth(int month) { this.month = month; }

    public int getDay() { return day; }

    public void setDay(int day) { this.day = day; }

    public void setTodayDate() {
        Date date = new Date();
        setDate(date);
    }

    public void setDate(String date) {
        splitDate(date);
    }

    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @SuppressWarnings("deprecation")
    public void setDate(Date date) {
        this.year = date.getYear()+1900;
        this.month = date.getMonth()+1;
        this.day = date.getDate();
    }

    public String getDbDate() {
        return joinData();
    }

    public Date getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getAppLocalizedPattern());
        Date retDate = null;
        try {
            retDate = simpleDateFormat.parse(joinData());
        } catch (ParseException e) {
            Log.d(LOG_TAG, e.getMessage());
            return null;
        }

        return retDate;
    }

    public String getDisplayDate() {
        return DateFormat.getDateInstance().format(getDate());
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
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
    }
}
