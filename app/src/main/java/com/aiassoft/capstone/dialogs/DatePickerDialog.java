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

/**
 * return a date picker dialog to select a date
 */
public class DatePickerDialog extends DialogFragment implements View.OnClickListener {

    private static final String LOG_TAG = MyApp.APP_TAG + DatePickerDialog.class.getSimpleName();

    private boolean mSetDate = false;
    private int mDay;
    private int mMonth;
    private int mYear;

    OpenDatePickerDialogOnSelectedDateHandler mSelectedDateHandler;

    public interface OpenDatePickerDialogOnSelectedDateHandler {

        void OnSelectedDate(int year, int month, int day);
    }

    @BindView(R.id.date_picker) DatePicker mDatePicker;
    @BindView(R.id.btn_accept) Button mButtonAccept;

    public DatePickerDialog() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_date_picker, viewGroup);

        ButterKnife.bind(this, rootView);

        mButtonAccept.setOnClickListener(this);

        if (mSetDate) {
            mDatePicker.updateDate(mYear, mMonth-1, mDay);
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_accept:
                int day = mDatePicker.getDayOfMonth();
                int month = mDatePicker.getMonth() + 1; // months start at 0
                int year = mDatePicker.getYear();
                mSelectedDateHandler.OnSelectedDate(year, month, day);
                break;
        }
        this.dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mSelectedDateHandler = (OpenDatePickerDialogOnSelectedDateHandler) context;
    }

    public void setDate(int year, int month, int day) {
        this.mDay = day;
        this.mMonth = month;
        this.mYear = year;
        this.mSetDate = true;
    }
}
