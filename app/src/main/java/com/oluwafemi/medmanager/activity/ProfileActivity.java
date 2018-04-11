package com.oluwafemi.medmanager.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.oluwafemi.medmanager.R;
import com.oluwafemi.medmanager.databinding.ActivityProfileBinding;

/**
 * Created by phemi-mint on 4/11/18.
 */

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    ActivityProfileBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
    }
}
