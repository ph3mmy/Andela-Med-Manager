/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oluwafemi.medmanager.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.oluwafemi.medmanager.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by phemi-mint on 4/4/18.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    OnDateSet onDateSetListener;
    OnCancelled onCancelled;

    public static final String PICKER_TITLE = "picker_title";
    boolean isMonthYear;

    public DatePickerFragment() {
        super();
    }

    public static DatePickerFragment newInstance(String pickerTitle, boolean isMonthYear) {

        Bundle args = new Bundle();
        args.putString(PICKER_TITLE, pickerTitle);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public OnDateSet getOnDateSetListener() {
        return onDateSetListener;
    }

    public void setOnDateSetListener(OnDateSet onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    public OnCancelled getOnCancelled() {
        return onCancelled;
    }

    public void setOnCancelled(OnCancelled onCancelled) {
        this.onCancelled = onCancelled;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        Bundle args = getArguments();
        String titleStr = args.getString(PICKER_TITLE);

        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), R.style.datepicker,this, year, month, day);
        datePicker.setTitle(titleStr);
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return datePicker;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        getOnCancelled().onCancelClicked();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        getOnDateSetListener().onDateSetListener(calendar.getTime());
    }

    public interface OnDateSet{
        void onDateSetListener(Date date);
    }public interface OnCancelled{
        void onCancelClicked();
    }
}
