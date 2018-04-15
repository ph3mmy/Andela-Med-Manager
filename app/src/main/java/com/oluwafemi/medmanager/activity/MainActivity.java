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

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.oluwafemi.medmanager.R;
import com.oluwafemi.medmanager.adapter.MedicationRecyclerAdapter;
import com.oluwafemi.medmanager.adapter.MedicationSectionedAdapter;
import com.oluwafemi.medmanager.databinding.ActivityMainBinding;
import com.oluwafemi.medmanager.model.Medication;
import com.oluwafemi.medmanager.util.FabVisibilityListener;
import com.oluwafemi.medmanager.util.Utility;
import com.oluwafemi.medmanager.viewmodel.MedicationViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    Toolbar toolbar;

    static int activeYear;

    ActivityMainBinding binding;
    MedicationRecyclerAdapter adapter;
    MedicationViewModel medicationViewModel;
    LinearLayoutManager layoutManager;
    private int currentYear;

    // helper class to help manage fab visibility
    FabVisibilityListener fabVisibilityListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        toolbar = binding.toolbar;
        toolbar.setTitle("Medication Manager");
        setSupportActionBar(toolbar);

        // initialize medicationViewModel
        medicationViewModel = ViewModelProviders.of(this).get(MedicationViewModel.class);
        layoutManager = new LinearLayoutManager(this);

        // set up year selector
        setupYearSelector();

        fabVisibilityListener = new FabVisibilityListener(binding.fab);
        binding.pageContent.rvMedication.addOnScrollListener(fabVisibilityListener);
        binding.fab.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setupSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            // start profile action
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }
        return true;
    }

    // setup search view
    private void setupSearchView (Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Enter Medication name");
        searchView.setIconifiedByDefault(true);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                searchItem.collapseActionView();
                makeSearchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() > 0) {
                    makeSearchQuery(s);
                } else {
                    fetchCurrentYearMedication(activeYear);
                }
                return false;
            }
        });
    }

    private void makeSearchQuery(String query) {
        medicationViewModel.searchYearlyMedicationWithName(query, String.valueOf(activeYear)).observe(MainActivity.this, new Observer<List<Medication>>() {
            @Override
            public void onChanged(@Nullable List<Medication> medications) {
                sectionMedicationIntoMonths(medications);
            }
        });
    }

    // launch view to add new medication
    private void addNewMedication() {
        startActivity(new Intent(MainActivity.this, AddMedicationActivity.class));
    }

    private void setupYearSelector () {
        Calendar currentYearCal = Calendar.getInstance();
        currentYear = currentYearCal.get(Calendar.YEAR);
        binding.pageContent.tvActiveYear.setText(String.valueOf(currentYear));
        activeYear = currentYear;
        fetchCurrentYearMedication(activeYear);
        binding.pageContent.ivBackArrow.setOnClickListener(this);
        binding.pageContent.ivForwardArrow.setOnClickListener(this);
    }

    // get only medications for the current year and section it into months
    private void fetchCurrentYearMedication(int activeYear) {
        medicationViewModel.getMedicationForCurrentYear(String.valueOf(activeYear)).observe(this, new Observer<List<Medication>>() {
            @Override
            public void onChanged(@Nullable List<Medication> medications) {
                sectionMedicationIntoMonths(medications);
            }
        });
    }

    // section medications into months
    private void sectionMedicationIntoMonths (List<Medication> medicationList) {
        SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        Calendar dateCreatedCal = Calendar.getInstance();
        for (int eachMonth = 11; eachMonth >= 0; eachMonth--) {

            // using a decrementing loop since list has been sorted based on date DESC
            List<Medication> monthlyMed = new ArrayList<>();
            for (Medication medication : medicationList) { // get the month each medication was created and check if it tallies with the current medication month (eachMonth)
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                addNewMedication();
                break;
            case R.id.iv_back_arrow:
                decrementActiveYear(activeYear);
                break;
            case R.id.iv_forward_arrow:
                incrementActiveYear(activeYear);
                break;
        }
    }

    // decrease active year by 1
    private void decrementActiveYear(int activeYearInt) {
        binding.pageContent.ivForwardArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_right_arrow_white_24dp));
        if (activeYearInt > 1970) {
            activeYear = activeYearInt - 1;
            binding.pageContent.tvActiveYear.setText(String.valueOf(activeYear));
            fetchCurrentYearMedication(activeYear);
        } else {
            Toast.makeText(this, "Please select a valid year", Toast.LENGTH_SHORT).show();
        }
    }

//    increase active year by 1
    private void incrementActiveYear(int activeYearInt) {
        if (activeYearInt < currentYear) {
            activeYear = activeYearInt + 1;
            binding.pageContent.tvActiveYear.setText(String.valueOf(activeYear));
            fetchCurrentYearMedication(activeYear);
        } else {
            binding.pageContent.ivForwardArrow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_right_arrow_disabled_24dp));
        }
    }
}
