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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.oluwafemi.medmanager.R;
import com.oluwafemi.medmanager.databinding.ActivityProfileBinding;
import com.oluwafemi.medmanager.model.User;
import com.oluwafemi.medmanager.util.PrefUtils;
import com.oluwafemi.medmanager.util.Utility;
import com.oluwafemi.medmanager.viewmodel.UserViewModel;

import org.parceler.Parcels;

import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

import static com.oluwafemi.medmanager.activity.EditProfileActivity.USER_KEY;

/**
 * Created by phemi-mint on 4/11/18.
 */

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener  {

    private static final String TAG = "ProfileActivity";
    ActivityProfileBinding binding;

    User user;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

//        initToolbar();
        Utility.setToolbarTitle(this, "My Profile", binding.includeToolbar.toolbar);

        // retrieve saved user info
        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getUserList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                user = users.get(0);
                initProfileViews(user);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_profile) {
            Intent mIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            mIntent.putExtra(USER_KEY, Parcels.wrap(user));
            startActivity(mIntent);
        } else if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }

    // set user value to views
    private void initProfileViews(User user) {
        Glide.with(this).load(user.getUserImageUrl()).into(binding.civProfileImage);
        binding.tvProfileName.setText(user.getUserName());
        binding.tvProfileEmail.setText(user.getEmail());
        if (!TextUtils.isEmpty(user.getAge())) {
            binding.tvProfileAge.setText(user.getAge() + " years old");
        } else {
            binding.tvProfileAge.setText(R.string.not_set);
        }
        binding.tvProfileGender.setText(user.getGender());
        binding.tvProfileGenotype.setText(user.getGenotype());
        binding.tvProfileAddress.setText(user.getAddress());
        binding.tvProfileLastMed.setText(user.getActiveMedication());
        if (user.getActiveMedStartDate() != null && user.getActiveMedEndDate() != null) {
            binding.tvStartDate.setText(Utility.formatRecyclerViewDate(user.getActiveMedStartDate()));
            binding.tvEndDate.setText(Utility.formatRecyclerViewDate(user.getActiveMedEndDate()));
        }

        binding.btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }
    // sign out google client
    public void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            PrefUtils.setUserIsLoggedIn(this, false);
            Intent logOutIntent = new Intent(ProfileActivity.this, LoginActivity.class);
            logOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(logOutIntent);
            Bungee.swipeRight(this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
