package com.parteek.guardianx.managers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles sending SOS SMS messages to saved emergency contacts.
 *
 * Version: v0.6
 */
public class SMSManager {

    private static final String TAG = "SMSManager";

    private final Context context;
    private final ContactStorageManager contactStorageManager;

    public SMSManager(Context context) {
        this.context = context;
        this.contactStorageManager = new ContactStorageManager(context);
    }

    public boolean hasSmsPermission() {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasEmergencyContacts() {
        return contactStorageManager.hasContacts();
    }

    public boolean sendSOSMessage(String message) {

        if (!hasSmsPermission()) {
            Log.e(TAG, "SMS permission not granted");
            return false;
        }

        List<String> phoneNumbers = contactStorageManager.getSavedPhoneNumbers();

        if (phoneNumbers.isEmpty()) {
            Log.e(TAG, "No emergency contacts saved");
            return false;
        }

        try {

            SmsManager smsManager;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                int subscriptionId =
                        SubscriptionManager.getDefaultSmsSubscriptionId();

                smsManager =
                        SmsManager.getSmsManagerForSubscriptionId(subscriptionId);

            } else {
                smsManager = SmsManager.getDefault();
            }

            boolean atLeastOneSent = false;

            for (String phoneNumber : phoneNumbers) {

                String cleanedNumber = phoneNumber.trim();

                if (cleanedNumber.isEmpty()) {
                    continue;
                }

                ArrayList<String> parts =
                        smsManager.divideMessage(message);

                smsManager.sendMultipartTextMessage(
                        cleanedNumber,
                        null,
                        parts,
                        null,
                        null
                );

                Log.d(TAG, "SOS SMS sent to: " + cleanedNumber);

                atLeastOneSent = true;
            }

            return atLeastOneSent;

        } catch (Exception e) {

            Log.e(TAG, "SMS sending failed", e);

            return false;
        }
    }
}