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

package com.oluwafemi.medmanager.activity;

import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.oluwafemi.medmanager.R;
import com.oluwafemi.medmanager.databinding.ActivityAddMedicationBinding;
import com.oluwafemi.medmanager.fragment.DatePickerFragment;
import com.oluwafemi.medmanager.model.Medication;
import com.oluwafemi.medmanager.model.User;
import com.oluwafemi.medmanager.util.Utility;
import com.oluwafemi.medmanager.viewmodel.MedicationViewModel;
import com.oluwafemi.medmanager.viewmodel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by phemi-mint on 4/4/18.
 */

public class AddMedicationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddMedicationActivity";
    ActivityAddMedicationBinding binding;

    String[] dailyFreq = {"Once Daily", "2 Times Daily", "3 Times Daily"};
    String[] repeatOptions = {"Does not repeat", "Repeat"};
    private Date startDate, endDate;
    private String startDateStr, endDateStr, selectedDailyFreq, selectedReminderRepeat, selectedReminderTime = null;
    private boolean hasReminder = false;

    User user;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_medication);

        // init spinners and switch
        initFrequencySpinner();
        initRepeatSpinner();
        initReminderSwitch();

        // get the user object to update with latest medication info
        getExistingUser();

        // set onclicklistener
        binding.ivClose.setOnClickListener(this);
        binding.ivDone.setOnClickListener(this);
        binding.tieMedStartDate.setOnClickListener(this);
        binding.tieMedEndDate.setOnClickListener(this);
        binding.tieReminderTime.setOnClickListener(this);
    }

    // add items and listener to reminder repeat spinner
    private void initRepeatSpinner() {
        selectedReminderRepeat = repeatOptions[0];
        binding.spinnerReminderFreq.setItems(repeatOptions);
        binding.spinnerReminderFreq.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                selectedReminderRepeat = repeatOptions[position];
            }
        });
    }

    // add items and listener to frequency spinner
    private void initFrequencySpinner() {
        selectedDailyFreq = dailyFreq[0];
        binding.spinnerMedFreq.setItems(dailyFreq);
        binding.spinnerMedFreq.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                selectedDailyFreq = dailyFreq[position];
            }
        });

    }

    private void initReminderSwitch() {
        binding.switchReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                hasReminder = b;
                if (!compoundButton.isChecked()) {
                    binding.llReminderLayout.setVisibility(View.GONE);
                } else {
                    binding.llReminderLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getExistingUser() {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUserList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                user = users.get(0);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_done:
                validateAndSaveMedication();
                break;
            case R.id.iv_close:
                finish();
                break;
            case R.id.tie_reminder_time:
                showTimePickerDialog();
                break;
            case R.id.tie_med_start_date:
                showDatePickerDialog("Select Start date", true);
                break;
            case R.id.tie_med_end_date:
                showDatePickerDialog("Select End date", false);
                break;
        }
    }

    // time picker to set reminder time
    private void showTimePickerDialog() {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);

        TimePickerDialog dlg;
        dlg = new TimePickerDialog(AddMedicationActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                selectedReminderTime = selectedHour + ":" + selectedMinute;
                binding.tieReminderTime.setText(selectedReminderTime);
            }
        }, hour, minute, false);
        dlg.setTitle("Select Reminder Time");
        dlg.show();
    }

    // validate form fields and save valid medication to bd
    private void validateAndSaveMedication() {
        String name, description, frequency, mStartDate, mEndDate, mReminderTime, reminderRepeatStatus;

        name = binding.tieMedName.getText().toString();
        description = binding.tieMedDesc.getText().toString();
//        frequency = binding.tieMedName.getText().toString();
        mStartDate = binding.tieMedStartDate.getText().toString();
        mEndDate = binding.tieMedEndDate.getText().toString();
        mReminderTime = binding.tieReminderTime.getText().toString();
        boolean isFormValid;

        TextInputLayout[] allLayouts;
        TextInputEditText[] allEditTexts;

        if (!hasReminder) {
            allLayouts = new TextInputLayout[]{binding.tilMedName, binding.tilMedDesc, binding.tilMedStartDate, binding.tilMedEndDate};
            allEditTexts = new TextInputEditText[]{binding.tieMedName, binding.tieMedDesc, binding.tieMedStartDate, binding.tieMedEndDate};
        } else {
            allLayouts = new TextInputLayout[]{binding.tilMedName, binding.tilMedDesc, binding.tilMedStartDate, binding.tilMedEndDate, binding.tilReminderTime};
            allEditTexts = new TextInputEditText[]{binding.tieMedName, binding.tieMedDesc, binding.tieMedStartDate, binding.tieMedEndDate, binding.tieReminderTime};
        }
        isFormValid = Utility.fieldValidation(allEditTexts, allLayouts);

        if (!isFormValid) {
            Snackbar.make(binding.container, "One or more fields are still empty", Snackbar.LENGTH_SHORT).show();
        } else {
            Medication medication = new Medication();
            medication.setName(name);
            medication.setDateCreated(System.currentTimeMillis());
            medication.setDescription(description);
            medication.setFrequency(selectedDailyFreq);
            medication.setStartDate(startDate);
            medication.setEndDate(endDate);
            medication.setHasReminder(hasReminder);
            medication.setReminderRepeatFrequency(selectedReminderRepeat);
            medication.setReminderTime(selectedReminderTime);

            // save medication to database
            saveMedication(medication);
        }
    }

    // save medication and close activity
    private void saveMedication(Medication medication) {
        MedicationViewModel medicationViewModel = ViewModelProviders.of(this).get(MedicationViewModel.class);
        medicationViewModel.insertMedication(medication);
        updateUserProfileWithMedication(medication);
    }

    // update user medication info
    private void updateUserProfileWithMedication(Medication medication) {
        user.setActiveMedStartDate(medication.getStartDate());
        user.setActiveMedEndDate(medication.getEndDate());
        user.setActiveMedication(medication.getName());
        userViewModel.updateExistingUser(user);
        Toast.makeText(this, "Medication successfully saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    // datepicker to set start and end date
    private void showDatePickerDialog(String title, final boolean isStartDate) {
        final DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(title, false);
        datePickerFragment.setOnDateSetListener(new DatePickerFragment.OnDateSet() {
            @Override
            public void onDateSetListener(Date date) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                String dateStr = dateFormat.format(date);
                if (isStartDate) {
                    if (endDate == null || date == endDate) {
                        setStartDateToView(date, dateStr);
                    } else if (!isStartDateGreater(date, endDate)) {
                        setStartDateToView(date, dateStr);
                    } else {
                        Toast.makeText(AddMedicationActivity.this, "Start date cannot be greater than end date", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onDateSetListener: mot true" );
                    }
                } else {
                    if (startDate == null) {
                        setEndDateToView(date, dateStr);
                    } else if (!isStartDateGreater(startDate, date)) {
                        setEndDateToView(date, dateStr);
                    } else {
                        Toast.makeText(AddMedicationActivity.this, "Start date cannot be greater than end date", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onDateSetListener: mot true" );
                    }
                }
            }
        });
        datePickerFragment.setOnCancelled(new DatePickerFragment.OnCancelled() {
            @Override
            public void onCancelClicked() {
                datePickerFragment.dismiss();
            }
        });
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void setStartDateToView(Date date, String dateStr) {
        binding.tieMedStartDate.setError(null);
        binding.tieMedStartDate.setText(dateStr);
        startDate = date;
        startDateStr = dateStr;
    }

    private void setEndDateToView(Date date, String dateStr) {
        endDate = date;
        endDateStr = dateStr;
        binding.tieMedEndDate.setError(null);
        binding.tieMedEndDate.setText(dateStr);
    }

    private boolean isStartDateGreater(Date startDate, Date endDate) {
        return startDate.getTime() >= endDate.getTime();
    }
}
