package com.parteek.guardianx.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.parteek.guardianx.R;

/**
 * Settings hub for GuardianX features.
 *
 * Version: v0.8
 */
public class SettingsActivity extends AppCompatActivity {

    private Button historyButton;
    private Button batterySettingsButton;
    private Button safetyTimerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        historyButton = findViewById(R.id.historyButton);
        safetyTimerButton = findViewById(R.id.safetyTimerButton);
        batterySettingsButton = findViewById(R.id.batterySettingsButton);
    }

    private void setupClickListeners() {

        historyButton.setOnClickListener(view ->
                startActivity(new Intent(this, HistoryActivity.class))
        );

        safetyTimerButton.setOnClickListener(view ->
                startActivity(new Intent(this, SafetyTimerActivity.class))
        );
        batterySettingsButton.setOnClickListener(view ->
                startActivity(new Intent(this, BatterySettingsActivity.class))
        );
    }
}