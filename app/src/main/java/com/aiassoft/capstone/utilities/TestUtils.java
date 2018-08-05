package com.aiassoft.capstone.utilities;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import com.aiassoft.capstone.R;
import com.aiassoft.capstone.data.ExpensesContract;
import com.aiassoft.capstone.data.VehiclesContract;

import java.util.ArrayList;
import java.util.List;

import static com.aiassoft.capstone.utilities.AppUtils.showSnackbar;

/**
 * Created by gvryn on 05/08/18.
 */

public final class TestUtils {

    private TestUtils() {
        throw new AssertionError(R.string.no_instances_for_you);
    }

    public static void insertFakeData(SQLiteDatabase db, View view) {

        if (db == null) {
            return;
        }

        showSnackbar(view, view.getContext().getString(R.string.insert_fake_data));

        boolean erroneous = insertFakeVehicles(db);

        if (! erroneous)
            erroneous = insertFakeExpenses(db);



        showSnackbar(view, view.getContext()
                .getString(erroneous ? R.string.inserting_fake_data_failed :
                        R.string.finished_inserting_fake_data));
    }

    private static boolean insertFakeVehicles(SQLiteDatabase db) {
        boolean erroneous = false;

        //create a list of fake vehicles
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME, "My Favorite Car");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE, "Peugeot");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL, "307 sw");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_PLATE_NO, "ZKN 7777");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_INITIALMILEAGE, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_TANKVOLUME, "60");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_VOLUME_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME, "My Dream Car");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE, "Ferrari");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL, "306 gts");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_PLATE_NO, "****");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_INITIALMILEAGE, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_TANKVOLUME, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_VOLUME_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        cv = new ContentValues();
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NAME, "My First Car");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MAKE, "Toyota");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_MODEL, "Starlet");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_PLATE_NO, "YBH 8075");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_INITIALMILEAGE, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_DINSTANCE_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_TANKVOLUME, "40");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_VOLUME_UNIT, "0");
        cv.put(VehiclesContract.VehiclesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        //insert all Vehicles in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (VehiclesContract.VehiclesEntry.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(VehiclesContract.VehiclesEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
            e.printStackTrace();
            erroneous = true;
        }
        finally
        {
            db.endTransaction();
        }

        return erroneous;
    }

    // TODO: add Fake expenses
    private static boolean insertFakeExpenses(SQLiteDatabase db) {
        boolean erroneous = false;

        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_VEHICLE_ID, 1);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_EXPENSE_TYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_SUBTYPE, 0);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_DATE, "01/07/2018");
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_ODOMETER, 100);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_FUEL_QUANTITY, 55);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_AMOUNT, 55);
        cv.put(ExpensesContract.ExpensesEntry.COLUMN_NAME_NOTES, "");
        list.add(cv);

        //insert all Expenses in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (ExpensesContract.ExpensesEntry.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(ExpensesContract.ExpensesEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
            e.printStackTrace();
            erroneous = true;
        }
        finally
        {
            db.endTransaction();
        }

        return erroneous;
    }

}