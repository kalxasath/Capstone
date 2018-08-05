package com.aiassoft.capstone.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.aiassoft.capstone.MyApp;
import com.aiassoft.capstone.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gvryn on 05/08/18.
 */

public class DatePickerDialog extends DialogFragment implements View.OnClickListener {

    private static final String LOG_TAG = MyApp.APP_TAG + DatePickerDialog.class.getSimpleName();

    private boolean mSetDate = false;
    private int mDay;
    private int mMonth;
    private int mYear;

    OpenDatePickerDialogOnSelectedDateHandler mSelectedDateHandler;

    public interface OpenDatePickerDialogOnSelectedDateHandler {

        void OnSelectedDate(int day, int month, int year);
    }

    @BindView(R.id.date_picker) DatePicker mDatePicker;
    @BindView(R.id.btn_accept) Button mButtonAccept;

    public DatePickerDialog() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_date_picker, viewGroup);

        getDialog().setTitle(getResources().getString(R.string.date_picker_title));

        ButterKnife.bind(this, rootView);

        mButtonAccept.setOnClickListener(this);

        if (mSetDate) {
            mDatePicker.updateDate(mYear, mMonth, mDay);
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_accept:
                int day = mDatePicker.getDayOfMonth();
                int month = mDatePicker.getMonth() + 1; // months start in 0
                int year = mDatePicker.getYear();
                mSelectedDateHandler.OnSelectedDate(day, month, year);
                break;
        }
        this.dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mSelectedDateHandler = (OpenDatePickerDialogOnSelectedDateHandler) context;
    }

    public void setDate(int day, int month, int year) {
        this.mDay = day;
        this.mMonth = month;
        this.mYear = year;
        this.mSetDate = true;
    }
}
