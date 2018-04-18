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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.WRITE_CALENDAR;

/**
 * Created by phemi-mint on 4/4/18.
 */

public class AddMedicationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddMedicationActivity";
    private static int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 0;
    ActivityAddMedicationBinding binding;
    String[] dailyFreq = {"Once Daily", "2 Times Daily", "3 Times Daily"};
    String[] repeatOptions = {"No repeat", "Repeat"};
    static Map<String, Integer> reminderRepeatMap;
    List<String> reminderRepeatList;
    User user;
    UserViewModel userViewModel;
    private Date startDate, endDate;
    private String startDateStr, endDateStr, selectedDailyFreq, selectedReminderRepeat, selectedReminderTime = null;
    private boolean hasReminder = false;

    int reminderSelectedHour, reminderSelectedMins;
    private MedicationViewModel medicationViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_medication);

        // init spinners and switch
        initFrequencySpinner();
        initRepeatMap();

        // get the user object to update with latest medication info
        getExistingUser();

        askForPermission(WRITE_CALENDAR, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);

        // set onclicklistener
        binding.ivClose.setOnClickListener(this);
        binding.ivDone.setOnClickListener(this);
        binding.tieMedStartDate.setOnClickListener(this);
        binding.tieMedEndDate.setOnClickListener(this);
        binding.tieReminderTime.setOnClickListener(this);
    }

    private void initRepeatMap() {
        reminderRepeatMap = new HashMap<>();
        reminderRepeatMap.put("Next 4 hours", 4);
        reminderRepeatMap.put("Next 6 hours", 6);
        reminderRepeatMap.put("Next 8 hours", 8);
        reminderRepeatMap.put("Next 12 hours", 12);
        reminderRepeatMap.put("No Repeat", 0);
    }

    // add items and listener to reminder repeat spinner
    private void initRepeatSpinner() {
        selectedReminderRepeat = reminderRepeatList.get(0);
        binding.spinnerReminderFreq.setItems(reminderRepeatList);
        binding.spinnerReminderFreq.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                selectedReminderRepeat = reminderRepeatList.get(position);
//                selectedReminderRepeat = repeatOptions[position];

            }
        });
    }

    // auto generate list for reminder repeat spinner based on medication frequency
    private List<String> generateRepeatList(int position) {
        List<String> repeatList = new ArrayList<>();
        switch (position) {
            case 0:
                // drug to be taken once daily: therefore there's no reminder repeat in the same day
                repeatList.add("No Repeat");
                break;
            case 1:
                // 2 times daily: reminder needed for the second dosage (6, 8 or 12 hours from the first
                repeatList.add("Next 6 hours");
                repeatList.add("Next 8 hours");
                repeatList.add("Next 12 hours");
                repeatList.add("No Repeat");
                break;
            case 2:  // 3 times daily: next two dosage can be 4-4hours, 6-6hours, 8-8hours from the first
                repeatList.add("Next 4 hours");
                repeatList.add("Next 6 hours");
                repeatList.add("Next 8 hours");
                repeatList.add("No Repeat");

                break;
        }
        return repeatList;
    }

    // add items and listener to frequency spinner
    private void initFrequencySpinner() {
        selectedDailyFreq = dailyFreq[0];
        reminderRepeatList = generateRepeatList(0);
        initRepeatSpinner();
        binding.spinnerMedFreq.setItems(dailyFreq);
        binding.spinnerMedFreq.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                selectedDailyFreq = dailyFreq[position];
                reminderRepeatList = generateRepeatList(position);
                initRepeatSpinner();
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
//                selectedReminderTime = selectedHour + ":" + selectedMinute;
                reminderSelectedHour = selectedHour;
                reminderSelectedMins = selectedMinute;

                // format time
                String curTime = String.format(Locale.getDefault(),"%02d:%02d", selectedHour, selectedMinute);
                binding.tieReminderTime.setError(null);
                selectedReminderTime = curTime;
                binding.tieReminderTime.setText(curTime);
            }
        }, hour, minute, false);
        dlg.setTitle("Select Reminder Time");
        dlg.show();
    }

    // validate form fields and save valid medication to bd
    private void validateAndSaveMedication() {
        String name, description;

        name = binding.tieMedName.getText().toString();
        description = binding.tieMedDesc.getText().toString();
        boolean isFormValid;

        TextInputLayout[] allLayouts;
        TextInputEditText[] allEditTexts;

            allLayouts = new TextInputLayout[]{binding.tilMedName, binding.tilMedDesc, binding.tilMedStartDate, binding.tilMedEndDate, binding.tilReminderTime};
            allEditTexts = new TextInputEditText[]{binding.tieMedName, binding.tieMedDesc, binding.tieMedStartDate, binding.tieMedEndDate, binding.tieReminderTime};

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
        medicationViewModel = ViewModelProviders.of(this).get(MedicationViewModel.class);

        // set reminder
        addReminder(medication);
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
                        Log.e(TAG, "onDateSetListener: mot true");
                    }
                } else {
                    if (startDate == null) {
                        setEndDateToView(date, dateStr);
                    } else if (!isStartDateGreater(startDate, date)) {
                        setEndDateToView(date, dateStr);
                    } else {
                        Toast.makeText(AddMedicationActivity.this, "Start date cannot be greater than end date", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onDateSetListener: mot true");
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

    // helper to help validate end and start date
    private boolean isStartDateGreater(Date startDate, Date endDate) {
        return startDate.getTime() >= endDate.getTime();
    }

    private void addReminder(Medication medication) {

        Calendar startEndCal = Calendar.getInstance();
        startEndCal.setTime(medication.getStartDate());
        int year = startEndCal.get(Calendar.YEAR);
        int month = startEndCal.get(Calendar.MONTH);
        int day = startEndCal.get(Calendar.DAY_OF_MONTH);

        // End Date Calendar
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(medication.getEndDate());
        int endYear = endCal.get(Calendar.YEAR);
        int endMonth = endCal.get(Calendar.MONTH);
        int endDay = endCal.get(Calendar.DAY_OF_MONTH);

        if (medication.getFrequency().equalsIgnoreCase(dailyFreq[0])) { // once daily
            startEndCal.set(year, month, day, reminderSelectedHour, reminderSelectedMins, 0);
            endCal.set(endYear, endMonth, endDay, reminderSelectedHour, reminderSelectedMins, 0);

        } else if (medication.getFrequency().equalsIgnoreCase(dailyFreq[1])) { // twice daily
            // get the number of hours to add to the HH param to create a new calendar
            int newHH = reminderSelectedHour + reminderRepeatMap.get(medication.getReminderRepeatFrequency());
            startEndCal.set(year, month, day, newHH, reminderSelectedMins, 0);
            endCal.set(endYear, endMonth, endDay, newHH, reminderSelectedMins, 0);

        } else if (medication.getFrequency().equalsIgnoreCase(dailyFreq[2])) { // three times daily
            // get the number of hours to add to the HH param to create a new calendar
            int newHH = reminderSelectedHour + (2 * reminderRepeatMap.get(medication.getReminderRepeatFrequency()));
            startEndCal.set(year, month, day, newHH, reminderSelectedMins, 0);
            endCal.set(endYear, endMonth, endDay, newHH, reminderSelectedMins, 0);
        }

        // create a new event with the new calendar
        ContentResolver cr = getContentResolver();
        ContentValues calEvent = new ContentValues();
        calEvent.put(CalendarContract.Events.CALENDAR_ID, 1); // XXX pick)
        calEvent.put(CalendarContract.Events.TITLE, "Time to take your medication (" + medication.getName() + ")");
        calEvent.put(CalendarContract.Events.DTSTART, startEndCal.getTimeInMillis());
        calEvent.put(CalendarContract.Events.DTEND, endCal.getTimeInMillis());
        calEvent.put(CalendarContract.Events.HAS_ALARM, 1);
        calEvent.put(CalendarContract.Events.EVENT_TIMEZONE, CalendarContract.Calendars.CALENDAR_TIME_ZONE);

        //save an event
        @SuppressLint("MissingPermission") final Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, calEvent);

        int dbId = Integer.parseInt(uri.getLastPathSegment());
        medication.setCalendarEventId(dbId);

        if (medication.getFrequency().equalsIgnoreCase(dailyFreq[0])) { // is once, set one-off reminder
            setReminder(cr, dbId, 5);
        } else if (medication.getFrequency().equalsIgnoreCase(dailyFreq[1])) { // is twice, set reminder twice
            setReminder(cr, dbId, 5); // the final reminder goes off 5 mins before the due date

            // calculate hours to set the first reminder
            int firstHour = reminderRepeatMap.get(medication.getReminderRepeatFrequency()) * 60;
            setReminder(cr, dbId, firstHour);
        } else if (medication.getFrequency().equalsIgnoreCase(dailyFreq[2])) { // is twice, set reminder twice

            // final reminder
            setReminder(cr, dbId, 5);

            // second reminder
            int secondDosage = reminderRepeatMap.get(medication.getReminderRepeatFrequency()) * 60;
            setReminder(cr, dbId, secondDosage);

            // 3rd reminder
            int firstReminder = 2 * secondDosage;
            setReminder(cr, dbId, firstReminder); // 3rd reminder
        }

        medicationViewModel.insertMedication(medication);
        updateUserProfileWithMedication(medication);
    }

    // routine to add reminders with the event
    public void setReminder(ContentResolver cr, long eventID, int timeBefore) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            askForPermission(WRITE_CALENDAR, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
        }
        try {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, timeBefore);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);

            int added = Integer.parseInt(uri.getLastPathSegment());
            if (added > 0) {
                Intent view = new Intent(Intent.ACTION_VIEW);
                view.setData(uri); // enter the uri of the event not the reminder

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                    view.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                } else {
                    view.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                }
                //view the event in calendar
                startActivity(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_CALENDAR) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                validateAndSaveMedication();
            } else {
                Toast.makeText(this, R.string.permission_rationale, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(AddMedicationActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddMedicationActivity.this, permission)) {
                ActivityCompat.requestPermissions(AddMedicationActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(AddMedicationActivity.this, new String[]{permission}, requestCode);
            }
        }
    }

}
