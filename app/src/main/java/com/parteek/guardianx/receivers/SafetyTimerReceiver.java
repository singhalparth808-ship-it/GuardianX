package com.parteek.guardianx.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.parteek.guardianx.R;
import com.parteek.guardianx.managers.AlertHistoryManager;
import com.parteek.guardianx.managers.SMSManager;
import com.parteek.guardianx.utils.LocationHelper;
import com.parteek.guardianx.utils.NotificationHelper;

/**
 * Receives Safety Timer alarm and triggers automatic SOS.
 *
 * Version: v0.8
 */
public class SafetyTimerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        context.getSharedPreferences("safety_timer_prefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("timer_active", false)
                .remove("timer_end_time")
                .apply();

        NotificationHelper.showSOSAlert(context);

        String locationLink = LocationHelper.getLocationLink(context);

        boolean locationIncluded = locationLink != null;

        AlertHistoryManager alertHistoryManager =
                new AlertHistoryManager(context);

        alertHistoryManager.saveAlert(
                context.getString(R.string.source_safety_timer),
                locationIncluded
        );

        SMSManager smsManager = new SMSManager(context);

        if (smsManager.hasEmergencyContacts()) {

            String smsMessage;

            if (locationLink != null) {
                smsMessage = context.getString(
                        R.string.sos_sms_message_with_location,
                        locationLink
                );
            } else {
                smsMessage = context.getString(
                        R.string.sos_sms_message_without_location
                );
            }

            smsManager.sendSOSMessage(smsMessage);
        }

        Toast.makeText(
                context,
                R.string.timer_sos_triggered,
                Toast.LENGTH_LONG
        ).show();
    }
}