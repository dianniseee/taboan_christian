package com.example.taboan_capstone.activity.driver;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taboan_capstone.databinding.ActivityCustomerProfileBinding;

public class DriverProfileActivity extends AppCompatActivity {

    private ActivityCustomerProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerProfileBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

    }
}