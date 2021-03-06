package com.oluwafemi.medmanager.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.oluwafemi.medmanager.R;
import com.oluwafemi.medmanager.databinding.ActivityMedicationDetailsBinding;
import com.oluwafemi.medmanager.model.Medication;
import com.oluwafemi.medmanager.util.Utility;
import com.oluwafemi.medmanager.viewmodel.MedicationViewModel;

import org.parceler.Parcels;

/**
 * Created by phemi-mint on 4/17/18.
 */

public class MedicationDetailsActivity extends AppCompatActivity {

    private static final String TAG = "MedicationDetailsActivi";

    ActivityMedicationDetailsBinding binding;
    MedicationViewModel medicationViewModel;
    public static final String MEDICATION_KEY = "medication_key";
    Medication medication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_medication_details);

        // init toolbar
        Utility.setToolbarTitle(this, "Medication Details", binding.includeToolbar.toolbar);

        // retrieved passed medication
        medication = (Medication) Parcels.unwrap(getIntent().getParcelableExtra(MEDICATION_KEY));
        medicationViewModel = ViewModelProviders.of(this).get(MedicationViewModel.class);

        initDetailViews();
    }

    private void initDetailViews() {
        binding.tvMedName.setText(medication.getName());
        binding.tvMedDesc.setText(medication.getDescription());
        binding.tvDetailAlarmTime.setText(Utility.convert24hrsTo12hrs(medication.getReminderTime()));
        binding.tvStartDate.setText(Utility.formatRecyclerViewDate(medication.getStartDate()));
        binding.tvEndDate.setText(Utility.formatRecyclerViewDate(medication.getEndDate()));
        binding.tvMedFreq.setText(medication.getFrequency());
        binding.tvDetailRepeatAlarm.setText(medication.getReminderRepeatFrequency());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_medication_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            showWarningDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Are you sure you want to delete this medication ?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Yes, Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                medicationViewModel.deleteMedication(medication); // delete medication from db
                Utility.deleteEvent(MedicationDetailsActivity.this, medication.getCalendarEventId()); // remove associated calendar notification
                finish();
            }
        });
        builder.show();
    }
}
