package com.aiassoft.capstone.utilities;


import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by gvryn on 06/08/18.
 */

public class DateUtils {

    private static final String LOG_TAG = MyApp.APP_TAG + DateUtils.class.getSimpleName();

    private DateUtils() {
        throw new AssertionError(R.string.no_instances_for_you);
    }


    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /// Valid functions

    public static Date getDate(String dbDate, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(dbDate);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    public static String getAppLocalizedPattern() {
        return "yyyy-MM-dd";
    }

    public static String getSystemLocalizedPattern() {
        return ((SimpleDateFormat) java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT)).toLocalizedPattern();
    }

    public static String getDisplayFormat() {
        return ((SimpleDateFormat) DateFormat.getDateInstance()).toLocalizedPattern();
    }

    public static String getDisplayDate(String dbDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getAppLocalizedPattern());
        Date date = null;
        try {
            date = simpleDateFormat.parse(dbDate);
        } catch (ParseException e) {
            return null;
        }
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT);
        return dateFormat.format(date);
    }

    public static String getDisplayDate(Date date) {
        return DateFormat.getDateInstance().format(date);
    }

    public static String getDbDate(String displayDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getSystemLocalizedPattern());
        Date date = null;
        try {
            date = simpleDateFormat.parse(displayDate);
        } catch (ParseException e) {
            return null;
        }
        java.text.DateFormat dateFormat = new SimpleDateFormat(getAppLocalizedPattern());
        return dateFormat.format(date);
    }
}
