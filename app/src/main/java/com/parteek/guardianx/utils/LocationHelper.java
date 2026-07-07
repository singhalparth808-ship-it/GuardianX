package com.parteek.guardianx.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.content.ContextCompat;

/**
 * Provides last known phone location for SOS messages.
 *
 * Version: v0.6
 */
public final class LocationHelper {

    private LocationHelper() {
        // Utility class
    }

    @SuppressLint("MissingPermission")
    public static String getLocationLink(Context context) {

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null) {
            return null;
        }

        Location location = null;

        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location == null
                    && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

        } catch (Exception e) {
            return null;
        }

        if (location == null) {
            return null;
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        return String.format(
                java.util.Locale.US,
                "https://maps.google.com/?q=%.5f,%.5f",
                latitude,
                longitude
        );
    }
}