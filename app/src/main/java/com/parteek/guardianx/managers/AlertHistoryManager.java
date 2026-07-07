package com.parteek.guardianx.managers;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Manages local SOS alert history.
 *
 * Version: v0.7
 */
public class AlertHistoryManager {

    private static final String PREF_NAME = "guardian_alert_history";
    private static final String KEY_ALERTS = "saved_alerts";

    private final SharedPreferences sharedPreferences;

    public AlertHistoryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveAlert(String source, boolean locationIncluded) {

        String time = new SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
        ).format(new Date());

        String locationStatus = locationIncluded
                ? "Location sent"
                : "Location unavailable";

        String alertEntry =
                "SOS from " + source + "\n"
                        + time + "\n"
                        + locationStatus;

        String existingAlerts = sharedPreferences.getString(KEY_ALERTS, "");

        String updatedAlerts;

        if (existingAlerts == null || existingAlerts.isEmpty()) {
            updatedAlerts = alertEntry;
        } else {
            updatedAlerts = alertEntry + "\n\n" + existingAlerts;
        }

        sharedPreferences.edit()
                .putString(KEY_ALERTS, updatedAlerts)
                .apply();
    }

    public String getAlertHistory() {

        String alerts = sharedPreferences.getString(KEY_ALERTS, "");

        if (alerts == null || alerts.isEmpty()) {
            return "";
        }

        return alerts;
    }

    public boolean hasHistory() {
        return !getAlertHistory().isEmpty();
    }

    public void clearHistory() {
        sharedPreferences.edit()
                .remove(KEY_ALERTS)
                .apply();
    }
}