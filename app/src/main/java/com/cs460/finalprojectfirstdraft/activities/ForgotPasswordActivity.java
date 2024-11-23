package com.cs460.finalprojectfirstdraft.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private com.cs460.finalprojectfirstdraft.databinding.ActivityForgotPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = com.cs460.finalprojectfirstdraft.databinding.ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.buttonBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.buttonSubmit.setOnClickListener(v -> {
            //check login in database, if it exists, update its password to the new password given
        });
    }
}