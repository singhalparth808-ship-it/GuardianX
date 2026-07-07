package com.parteek.guardianx.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.parteek.guardianx.R;

/**
 * Shows nearby emergency locations using Google Maps.
 *
 * Version: v0.4
 */
public class LocationActivity extends AppCompatActivity {

    private Button locationPoliceButton;
    private Button locationFireButton;
    private Button locationHospitalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        locationPoliceButton = findViewById(R.id.locationPoliceButton);
        locationFireButton = findViewById(R.id.locationFireButton);
        locationHospitalButton = findViewById(R.id.locationHospitalButton);
    }

    private void setupClickListeners() {

        locationPoliceButton.setOnClickListener(view ->
                openMapsSearch("nearby police station")
        );

        locationFireButton.setOnClickListener(view ->
                openMapsSearch("nearby fire station")
        );

        locationHospitalButton.setOnClickListener(view ->
                openMapsSearch("nearby hospital")
        );
    }

    private void openMapsSearch(String query) {

        Uri uri = Uri.parse("geo:0,0?q=" + Uri.encode(query));

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            Intent fallbackIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://maps.google.com/?q=" + Uri.encode(query))
            );
            startActivity(fallbackIntent);
        }
    }
}