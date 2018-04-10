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
import android.view.View;

import com.oluwafemi.medmanager.R;
import com.oluwafemi.medmanager.adapter.MedicationRecyclerAdapter;
import com.oluwafemi.medmanager.databinding.ActivityMainBinding;
import com.oluwafemi.medmanager.model.Medication;
import com.oluwafemi.medmanager.viewmodel.MedicationViewModel;

import java.util.List;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";

    Toolbar toolbar;

    ActivityMainBinding binding;
    MedicationRecyclerAdapter adapter;
    MedicationViewModel medicationViewModel;
    LinearLayoutManager layoutManager;

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

        // fetch all medications
        fetchAllMedications();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMedication();
            }
        });

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


}