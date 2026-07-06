package com.parteek.guardianx.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.parteek.guardianx.R;
import com.parteek.guardianx.activities.MainActivity;

/**
 * Handles GuardianX emergency notifications and phone vibration.
 *
 * Version: v0.3
 */
public final class NotificationHelper {

    private static final String CHANNEL_ID = "guardianx_emergency_channel";
    private static final int SOS_NOTIFICATION_ID = 3001;

    private NotificationHelper() {
        // Utility class
    }

    public static void showSOSAlert(Context context) {

        createNotificationChannel(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                vibratePhone(context);
                return;
            }
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle(context.getString(R.string.sos_alert_title))
                        .setContentText(context.getString(R.string.sos_alert_message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(SOS_NOTIFICATION_ID, builder.build());
        }

        vibratePhone(context);
    }

    private static void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "GuardianX Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
        );

        channel.setDescription("Emergency SOS alerts from GuardianX");
        channel.enableVibration(true);
        channel.setVibrationPattern(new long[]{0, 700});

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressWarnings("deprecation")
    private static void vibratePhone(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            VibratorManager vibratorManager =
                    (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);

            if (vibratorManager == null) {
                return;
            }

            Vibrator vibrator = vibratorManager.getDefaultVibrator();

            if (!vibrator.hasVibrator()) {
                return;
            }

            vibrator.vibrate(
                    VibrationEffect.createOneShot(
                            700,
                            VibrationEffect.DEFAULT_AMPLITUDE
                    )
            );

        } else {

            Vibrator vibrator =
                    (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            if (vibrator == null || !vibrator.hasVibrator()) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                        VibrationEffect.createOneShot(
                                700,
                                VibrationEffect.DEFAULT_AMPLITUDE
                        )
                );
            } else {
                vibrator.vibrate(700);
            }
        }
    }
}