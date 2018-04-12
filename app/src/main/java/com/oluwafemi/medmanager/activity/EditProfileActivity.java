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

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.oluwafemi.medmanager.R;
import com.oluwafemi.medmanager.databinding.ActivityEditProfileBinding;
import com.oluwafemi.medmanager.model.User;
import com.oluwafemi.medmanager.util.Utility;
import com.oluwafemi.medmanager.viewmodel.UserViewModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phemi-mint on 4/11/18.
 */

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    ActivityEditProfileBinding binding;

    public static final String USER_KEY = "user_key";
    User user;
    String[] genderArr = {"Male", "Female"};
    String[] genotypeArr = {"AA", "AS", "AC", "SC", "SS", "CC", "Not Sure"};
    String selectedGender, selectedGenotype;

    List<String> genotypeList, genderList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);

        Utility.setToolbarTitle(this, "Edit Profile", binding.includeToolbar.toolbar);

        user = (User)Parcels.unwrap(getIntent().getParcelableExtra(USER_KEY));
        initEditViewsWithUser(user);
    }

//    presets user details into view
    private void initEditViewsWithUser(User user) {
        initGenderSpinner();
        initGenotypeSpinner();
        Glide.with(this).load(user.getUserImageUrl()).into(binding.ivProfileImage);
        binding.tieAge.setText(user.getAge());
        binding.tieAddress.setText(user.getAddress());
        binding.tvUserName.setText(user.getUserName());
        binding.tvEmail.setText(user.getEmail());

        binding.btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateEditedFieldsAndSave();
            }
        });

    }

    private void initGenderSpinner() {
        /*int selectedIndex = getPositionForGenderSpinner(user.getGender());
        Log.e(TAG, "initGenderSpinner: sel index == " + selectedIndex);
        binding.spinnerGender.setSelectedIndex(selectedIndex);
        binding.spinnerGender.setItems(genderArr);

        selectedGender = genderArr[selectedIndex];
        binding.spinnerGender.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                selectedGender = genderArr[position];
            }
        });*/


        genderList = new ArrayList<>();
        genderList = getListForGenderSpinner(user.getGender());
        binding.spinnerGender.setItems(genderList);

        selectedGender = genderList.get(0);
        binding.spinnerGender.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                selectedGender = genderList.get(position);
            }
        });
    }

    private void initGenotypeSpinner() {
        genotypeList = new ArrayList<>();
        genotypeList = getListForGenotypeSpinner(user.getGenotype());
        binding.spinnerGenotype.setItems(genotypeList);

        selectedGenotype = genotypeList.get(0);
        binding.spinnerGenotype.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                selectedGenotype = genotypeList.get(position);
            }
        });
    }

    // returns the position to get the pre-selected genotype
    private List<String> getListForGenotypeSpinner(String genotype) {
        List<String> generatedGenotypeList = new ArrayList<>();
        generatedGenotypeList.add(genotype);
        for (int i = 0; i < genotypeArr.length; i++) {
            String genotypeStr = genotypeArr[i];
            if (!generatedGenotypeList.contains(genotypeStr)) {
                generatedGenotypeList.add(genotypeStr);
            }
        }
        return generatedGenotypeList;
    }

    // returns the position to get the pre-selected genotype
    private List<String> getListForGenderSpinner(String gender) {
        List<String> generatedGenderList = new ArrayList<>();
        generatedGenderList.add(gender);
        for (int i = 0; i < genderArr.length; i++) {
            String genderStr = genderArr[i];
            if (!generatedGenderList.contains(genderStr)) {
                generatedGenderList.add(genderStr);
            }
        }
        return generatedGenderList;
    }

    private void validateEditedFieldsAndSave() {
        TextInputLayout[] layouts = {binding.tilAge, binding.tilAddress};
        TextInputEditText[] editTexts = {binding.tieAge, binding.tieAddress};

        boolean isFormValid = Utility.fieldValidation(editTexts, layouts);
        if (!isFormValid || TextUtils.isEmpty(selectedGender) || TextUtils.isEmpty(selectedGenotype)) {
            Snackbar.make(binding.llEditProfileLayout, "One or More Fields are empty", Snackbar.LENGTH_SHORT).show();
        } else {
            String ageStr = binding.tieAge.getText().toString();
            String addressStr = binding.tieAddress.getText().toString();

            Log.e(TAG, "validateEditedFieldsAndSave: geno = " + selectedGenotype + " sel gender = " + selectedGender );

            user.setGenotype(selectedGenotype);
            user.setGender(selectedGender);
            user.setAddress(addressStr);
            user.setAge(ageStr);

            saveEditedUser(user);
        }
    }

    // save edited user to db
    private void saveEditedUser(User user) {
        UserViewModel viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.updateExistingUser(user);
        Toast.makeText(this, "Profile successfully updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}
