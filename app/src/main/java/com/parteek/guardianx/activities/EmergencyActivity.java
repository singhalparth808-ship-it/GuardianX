package com.parteek.guardianx.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import com.parteek.guardianx.R;

/**
 * Emergency toolkit screen for one-tap emergency calling
 * and nearby emergency location search.
 *
 * Version: v0.4
 */
public class EmergencyActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PERMISSION = 4001;

    private String pendingPhoneNumber;
    private Button policeButton;
    private Button cyberCrimeButton;
    private Button ambulanceButton;
    private Button fireButton;
    private Button womenButton;
    private Button childButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        policeButton = findViewById(R.id.policeButton);
        ambulanceButton = findViewById(R.id.ambulanceButton);
        fireButton = findViewById(R.id.fireButton);
        womenButton = findViewById(R.id.womenButton);
        childButton = findViewById(R.id.childButton);
        cyberCrimeButton = findViewById(R.id.cyberCrimeButton);
    }

    private void setupClickListeners() {

        policeButton.setOnClickListener(view -> callNumber("8929159342"));

        ambulanceButton.setOnClickListener(view -> callNumber("8929159342"));

        fireButton.setOnClickListener(view -> callNumber("8929159342"));

        womenButton.setOnClickListener(view -> callNumber("8929159342"));

        childButton.setOnClickListener(view -> callNumber("8929159342"));

        cyberCrimeButton.setOnClickListener(view -> callNumber("8929898726"));

    }

    private void callNumber(String phoneNumber) {

        pendingPhoneNumber = phoneNumber;

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PERMISSION
            );

            return;
        }

        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);

        } catch (Exception e) {
            openDialer(phoneNumber);
        }
    }

    private void openDialer(String phoneNumber) {

        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(dialIntent);
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

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CALL_PERMISSION) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && pendingPhoneNumber != null) {

                callNumber(pendingPhoneNumber);

            } else if (pendingPhoneNumber != null) {

                openDialer(pendingPhoneNumber);
            }
        }
    }
}