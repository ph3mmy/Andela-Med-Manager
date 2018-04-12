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

package com.oluwafemi.medmanager.util;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by phemi-mint on 4/10/18.
 */

public class Utility {

    public static boolean fieldValidation(TextInputEditText[] fields, TextInputLayout[] allInputLayout){

        if (fields.length != allInputLayout.length) {
            try {
                throw new Exception("Size of EditText does not equal size of InputLayout");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for ( int i=0; i < fields.length; i++) {
            TextInputEditText currentField = fields[i];
            TextInputLayout currentLayout = allInputLayout[i];

            if (currentField.getText().toString().trim().length() <= 0) {
                currentField.setError("This field is required");
                addListenerToInputField(currentField, currentLayout);
                currentField.requestFocus();
                currentLayout.setErrorEnabled(true);
                currentLayout.setError("This field is required");
                return false;
            } else {
                currentLayout.setErrorEnabled(false);
                addListenerToInputField(currentField, currentLayout);
                currentLayout.setError(null);
                currentField.setError(null);
            }
        }
        return true;
    }

    // TextChangeListener to remove TextInputLayoutError
    private static void addListenerToInputField(TextInputEditText editText, final TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    layout.setErrorEnabled(false);
                } else
                    layout.setErrorEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    // format Date
    public static String formatRecyclerViewDate(Date mDate) {
        String newDate;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        newDate = sdf.format(mDate);
        return newDate;
    }

    // format Date to return the day only e.g Mon
    public static String formatRecyclerViewDateDaysOnly(Date mDate) {
        String newDate;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());
        newDate = sdf.format(mDate);
        return newDate;
    }

    // format Date
    public static String formatModifyRecyclerViewDate(Date mDate1, Date mDate2) {
        String noOfdays;
        long dateDiff = mDate2.getTime() - mDate1.getTime();
        noOfdays = (dateDiff/(1000*60*60*24) + 1) + " " + "days";

        // get days only
        SimpleDateFormat daysOnlyFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        String day1 = daysOnlyFormat.format(mDate1);
        String day2 = daysOnlyFormat.format(mDate2);
        return noOfdays + " || " + day1 + " - " + day2;
    }

    // helper to set toolbar title
    public static void setToolbarTitle(AppCompatActivity appCompatActivity, String title, Toolbar toolbar) {
        toolbar.setTitle(title);
        appCompatActivity.setSupportActionBar(toolbar);
        if (appCompatActivity.getSupportActionBar() != null) {
            appCompatActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
