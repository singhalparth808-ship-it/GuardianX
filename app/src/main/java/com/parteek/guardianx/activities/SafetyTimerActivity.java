package com.parteek.guardianx.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.parteek.guardianx.receivers.SafetyTimerReceiver;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import com.parteek.guardianx.R;
import com.parteek.guardianx.managers.AlertHistoryManager;
import com.parteek.guardianx.managers.SMSManager;
import com.parteek.guardianx.utils.LocationHelper;
import com.parteek.guardianx.utils.NotificationHelper;

/**
 * Safety timer screen.
 *
 * Version: v0.8
 */
public class SafetyTimerActivity extends AppCompatActivity {

    private TextView timerStatusText;
    private static final String PREF_NAME = "safety_timer_prefs";
    private static final String KEY_TIMER_END_TIME = "timer_end_time";
    private static final String KEY_TIMER_ACTIVE = "timer_active";

    private SharedPreferences timerPreferences;
    private EditText customTimerInput;
    private static final int SAFETY_TIMER_REQUEST_CODE = 8001;
    private long timerEndTimeMillis = 0;
    private Button startCustomTimerButton;
    private Button demoTimerButton;
    private Button fiveMinuteButton;
    private Button tenMinuteButton;
    private Button thirtyMinuteButton;
    private Button cancelTimerButton;

    private CountDownTimer countDownTimer;

    private SMSManager smsManager;
    private AlertHistoryManager alertHistoryManager;

    private boolean timerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_timer);

        smsManager = new SMSManager(this);
        alertHistoryManager = new AlertHistoryManager(this);
        timerPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        initializeViews();
        setupClickListeners();
        restoreTimerIfActive();
    }

    private void initializeViews() {
        timerStatusText = findViewById(R.id.timerStatusText);

        demoTimerButton = findViewById(R.id.demoTimerButton);
        fiveMinuteButton = findViewById(R.id.fiveMinuteButton);
        tenMinuteButton = findViewById(R.id.tenMinuteButton);
        thirtyMinuteButton = findViewById(R.id.thirtyMinuteButton);
        cancelTimerButton = findViewById(R.id.cancelTimerButton);
        customTimerInput = findViewById(R.id.customTimerInput);
        startCustomTimerButton = findViewById(R.id.startCustomTimerButton);
    }

    private void setupClickListeners() {
        demoTimerButton.setOnClickListener(view -> startSafetyTimer(10_000));
        fiveMinuteButton.setOnClickListener(view -> startSafetyTimer(5 * 60_000));
        tenMinuteButton.setOnClickListener(view -> startSafetyTimer(10 * 60_000));
        thirtyMinuteButton.setOnClickListener(view -> startSafetyTimer(30 * 60_000));
        startCustomTimerButton.setOnClickListener(view -> startCustomTimer());

        cancelTimerButton.setOnClickListener(view -> cancelSafetyTimer());
    }

    private void startCustomTimer() {

        String input = customTimerInput.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(
                    this,
                    R.string.invalid_timer,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int minutes;

        try {
            minutes = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            Toast.makeText(
                    this,
                    R.string.invalid_timer,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (minutes <= 0) {
            Toast.makeText(
                    this,
                    R.string.invalid_timer,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        long durationMillis = minutes * 60_000L;

        startSafetyTimer(durationMillis);

        customTimerInput.setText("");
    }

    private void startSafetyTimer(long durationMillis) {

        timerEndTimeMillis = System.currentTimeMillis() + durationMillis;

        saveTimerState(timerEndTimeMillis);

        scheduleSafetyTimerAlarm(timerEndTimeMillis);

        startVisibleCountdown(durationMillis);
    }

    private void saveTimerState(long endTimeMillis) {
        timerPreferences.edit()
                .putBoolean(KEY_TIMER_ACTIVE, true)
                .putLong(KEY_TIMER_END_TIME, endTimeMillis)
                .apply();
    }

    private void clearTimerState() {
        timerPreferences.edit()
                .putBoolean(KEY_TIMER_ACTIVE, false)
                .remove(KEY_TIMER_END_TIME)
                .apply();
    }

    private void restoreTimerIfActive() {

        boolean timerActive = timerPreferences.getBoolean(KEY_TIMER_ACTIVE, false);

        if (!timerActive) {
            timerStatusText.setText(R.string.timer_not_running);
            return;
        }

        long savedEndTime = timerPreferences.getLong(KEY_TIMER_END_TIME, 0);
        long remainingMillis = savedEndTime - System.currentTimeMillis();

        if (remainingMillis <= 0) {
            clearTimerState();
            timerStatusText.setText(R.string.timer_not_running);
            return;
        }

        timerEndTimeMillis = savedEndTime;
        startVisibleCountdown(remainingMillis);
    }

    private void startVisibleCountdown(long durationMillis) {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        timerRunning = true;

        countDownTimer = new CountDownTimer(durationMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                long totalSeconds = millisUntilFinished / 1000;
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;

                String timeText = String.format(
                        java.util.Locale.getDefault(),
                        "%02d:%02d",
                        minutes,
                        seconds
                );

                timerStatusText.setText(
                        getString(R.string.timer_running_with_time, timeText)
                );
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                clearTimerState();
                timerStatusText.setText(R.string.timer_sos_triggered);
            }
        };

        countDownTimer.start();
    }

    private void scheduleSafetyTimerAlarm(long triggerAtMillis) {

        Intent intent = new Intent(this, SafetyTimerReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                SAFETY_TIMER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            return;
        }

        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
        );
    }

    private void cancelSafetyTimer() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        cancelSafetyTimerAlarm();

        clearTimerState();

        timerRunning = false;
        timerEndTimeMillis = 0;

        timerStatusText.setText(R.string.timer_not_running);

        Toast.makeText(
                this,
                R.string.timer_cancelled,
                Toast.LENGTH_SHORT
        ).show();
    }

    private void cancelSafetyTimerAlarm() {

        Intent intent = new Intent(this, SafetyTimerReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                SAFETY_TIMER_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager =
                (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void triggerTimerSOS() {

        timerStatusText.setText(R.string.timer_sos_triggered);

        NotificationHelper.showSOSAlert(this);

        boolean locationIncluded = LocationHelper.getLocationLink(this) != null;

        alertHistoryManager.saveAlert("Safety Timer", locationIncluded);

        sendTimerSOSMessage();

        Toast.makeText(
                this,
                R.string.timer_sos_triggered,
                Toast.LENGTH_LONG
        ).show();
    }

    private void sendTimerSOSMessage() {

        if (smsManager == null) {
            return;
        }

        if (!smsManager.hasEmergencyContacts()) {
            Toast.makeText(
                    this,
                    R.string.no_emergency_contacts,
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String locationLink = LocationHelper.getLocationLink(this);

        String smsMessage;

        if (locationLink != null) {
            smsMessage = getString(R.string.sos_sms_message_with_location, locationLink);
        } else {
            smsMessage = getString(R.string.sos_sms_message_without_location);
        }

        boolean sent = smsManager.sendSOSMessage(smsMessage);

        if (sent) {
            Toast.makeText(
                    this,
                    R.string.sos_sms_sent,
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    this,
                    R.string.sos_sms_failed,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}