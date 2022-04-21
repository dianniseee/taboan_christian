package com.example.taboan_capstone.activity.driver;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taboan_capstone.databinding.ActivityDriverHistoryBinding;

public class DriverHistoryActivity extends AppCompatActivity {

    private ActivityDriverHistoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDriverHistoryBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

    }
}