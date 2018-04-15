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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.oluwafemi.medmanager.R;
import com.oluwafemi.medmanager.adapter.MedicationRecyclerAdapter;
import com.oluwafemi.medmanager.adapter.MedicationSectionedAdapter;
import com.oluwafemi.medmanager.databinding.ActivityMainBinding;
import com.oluwafemi.medmanager.model.Medication;
import com.oluwafemi.medmanager.util.Utility;
import com.oluwafemi.medmanager.viewmodel.MedicationViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";

    Toolbar toolbar;

    String selectedSortCategory;

    ActivityMainBinding binding;
    MedicationRecyclerAdapter adapter;
    MedicationViewModel medicationViewModel;
    LinearLayoutManager layoutManager;
    private SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        toolbar = binding.toolbar;
        toolbar.setTitle("All Medications");
        setSupportActionBar(toolbar);

        // initialize medicationViewModel
        medicationViewModel = ViewModelProviders.of(this).get(MedicationViewModel.class);
        layoutManager = new LinearLayoutManager(this);

        fetchCurrentPreviousYearMedication();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMedication();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // start profile action
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.action_search:
                // TODO: implement search

                break;
        }
        return true;
    }

    private void addNewMedication() {
        startActivity(new Intent(MainActivity.this, AddMedicationActivity.class));
    }

    private void fetchAllMedications() {
        medicationViewModel.getMedicationList().observe(this, new Observer<List<Medication>>() {
            @Override
            public void onChanged(@Nullable List<Medication> medications) {
                adapter = new MedicationRecyclerAdapter(medications);
                binding.pageContent.rvMedication.setLayoutManager(layoutManager);
                binding.pageContent.rvMedication.setAdapter(adapter);
            }
        });
    }

    // query medications by months
    private void fetchMonthlyMedications(String monthInd) {
        medicationViewModel.getMedicationByMonthList(monthInd).observe(this, new Observer<List<Medication>>() {
            @Override
            public void onChanged(@Nullable List<Medication> medications) {
                adapter = new MedicationRecyclerAdapter(medications);
                binding.pageContent.rvMedication.setLayoutManager(layoutManager);
                binding.pageContent.rvMedication.setAdapter(adapter);
            }
        });
    }

    // get only medications for the current year and section it into months
    private void fetchCurrentPreviousYearMedication() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int prevYear = now.get(Calendar.YEAR) - 1;
        medicationViewModel.getMedicationForCurrentYear(String.valueOf(prevYear), String.valueOf(year)).observe(this, new Observer<List<Medication>>() {
            @Override
            public void onChanged(@Nullable List<Medication> medications) {
                sectionMedicationIntoMonths(medications);
            }
        });
    }

    // section medications into months
    private void sectionMedicationIntoMonths (List<Medication> medicationList) {
        sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        Calendar dateCreatedCal = Calendar.getInstance();

        for (int eachMonth = 11; eachMonth >= 0; eachMonth--) {

            // using a decrementing loop since list has been sorted based on date DESC
            List<Medication> monthlyMed = new ArrayList<>();
            for (Medication medication : medicationList) {
                // get the month each medication was created and check if it tallies with the current medication month (eachMonth)
                dateCreatedCal.setTime(new Date(medication.getDateCreated()));
                int dateCreatedMonth = dateCreatedCal.get(Calendar.MONTH);

                if (eachMonth == dateCreatedMonth) {
                    monthlyMed.add(medication);
                }
            }

            // create a title and section for each non-empty medicationList
            if (monthlyMed.size() != 0) {
                String sectionTitle = Utility.returnMonthYearFromLong(monthlyMed.get(0).getDateCreated());
                sectionedRecyclerViewAdapter.addSection(new MedicationSectionedAdapter(monthlyMed, sectionTitle));
            }

        }

        binding.pageContent.rvMedication.setLayoutManager(layoutManager);
        binding.pageContent.rvMedication.setAdapter(sectionedRecyclerViewAdapter);
//        return monthlyMed;
    }

}
