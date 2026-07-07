package com.parteek.guardianx.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import com.parteek.guardianx.utils.NotificationHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.view.View;

import com.parteek.guardianx.R;
import com.parteek.guardianx.ble.BLECallback;
import com.parteek.guardianx.ble.BLEManager;
import com.parteek.guardianx.utils.PermissionHelper;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class MainActivity extends AppCompatActivity implements BLECallback {

    private TextView sosActionButton;

    private View locationCard;
    private View contactsCard;
    private View emergencyCard;
    private View settingsCard;

    private boolean locationDialogShowing = false;
    private TextView headerStatus;
    private TextView deviceStatusText;
    private TextView batteryValue;

    private boolean bluetoothDialogShowing = false;
    private int sosClickCount = 0;
    private long lastSosClickTime = 0;

    private static final long SOS_CLICK_WINDOW_MS = 2000;
    private static final int REQUIRED_SOS_CLICKS = 3;
    private final Runnable resetSosClickRunnable = () -> {
        sosClickCount = 0;
        lastSosClickTime = 0;
        sosActionButton.setText(R.string.hold_to_sos);
    };

    private BLEManager bleManager;

    private ActivityResultLauncher<Intent> bluetoothEnableLauncher;

    private final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Main entry point for GuardianX.
     *
     * Version: v0.2
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, filter);

        bluetoothEnableLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (PermissionHelper.hasRequiredPermissions(this)) {
                                startBleConnection();
                            }
                        }
                );

        initializeViews();

        bleManager = new BLEManager(this, this);

        PermissionHelper.requestRequiredPermissions(this);

        if (PermissionHelper.hasRequiredPermissions(this)) {
            startBleConnection();
        }
    }

    private boolean isLocationEnabled() {

        LocationManager locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager == null) {
            return false;
        }

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showLocationEnableDialog() {

        if (locationDialogShowing) {
            return;
        }

        locationDialogShowing = true;

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Location Required")
                .setMessage("GuardianX needs Location enabled to scan and connect with your Guardian device.")
                .setPositiveButton("Open Location Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    locationDialogShowing = false;
                })
                .setOnDismissListener(dialog -> {
                    locationDialogShowing = false;
                })
                .show();
    }

    private void initializeViews() {
        headerStatus = findViewById(R.id.headerStatus);
        deviceStatusText = findViewById(R.id.deviceStatusText);
        batteryValue = findViewById(R.id.batteryValue);
        sosActionButton = findViewById(R.id.sosActionButton);
        locationCard = findViewById(R.id.locationCard);
        contactsCard = findViewById(R.id.contactsCard);
        emergencyCard = findViewById(R.id.emergencyCard);
        settingsCard = findViewById(R.id.settingsCard);

        setupQuickActions();

        setupSOSButton();
    }

    private void setupQuickActions() {

        locationCard.setOnClickListener(view ->
                startActivity(new Intent(this, LocationActivity.class))
        );

        contactsCard.setOnClickListener(view ->
                startActivity(new Intent(this, ContactsActivity.class))
        );

        emergencyCard.setOnClickListener(view ->
                startActivity(new Intent(this, EmergencyActivity.class))
        );

        settingsCard.setOnClickListener(view ->
                android.widget.Toast.makeText(
                        this,
                        "Settings coming soon",
                        android.widget.Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void setupSOSButton() {

        sosActionButton.setOnClickListener(view -> {

            long currentTime = System.currentTimeMillis();

            if (currentTime - lastSosClickTime > SOS_CLICK_WINDOW_MS) {
                sosClickCount = 0;
            }

            lastSosClickTime = currentTime;
            sosClickCount++;

            int remainingClicks = REQUIRED_SOS_CLICKS - sosClickCount;

            if (remainingClicks > 0) {

                sosActionButton.setText(
                        getString(R.string.tap_sos_remaining, remainingClicks)
                );

                handler.removeCallbacks(resetSosClickRunnable);
                handler.postDelayed(resetSosClickRunnable, SOS_CLICK_WINDOW_MS);

            } else {

                handler.removeCallbacks(resetSosClickRunnable);

                sosClickCount = 0;
                lastSosClickTime = 0;

                sosActionButton.setText(R.string.hold_to_sos);

                triggerSOS(getString(R.string.app_sos_triggered));
            }
        });
    }

    private void triggerSOS(String message) {

        headerStatus.setText("SOS");
        headerStatus.setTextColor(ContextCompat.getColor(this, R.color.guardian_error));

        deviceStatusText.setText(message);
        deviceStatusText.setTextColor(ContextCompat.getColor(this, R.color.guardian_error));

        sosActionButton.setText(R.string.hold_to_sos);

        NotificationHelper.showSOSAlert(this);

        android.widget.Toast.makeText(
                this,
                message,
                android.widget.Toast.LENGTH_LONG
        ).show();
    }



    private void startBleConnection() {

        if (bleManager == null) {
            return;
        }

        if (!isLocationEnabled()) {
            headerStatus.setText(R.string.disconnected);
            headerStatus.setTextColor(ContextCompat.getColor(this, R.color.guardian_error));

            deviceStatusText.setText("Location is turned off");
            deviceStatusText.setTextColor(ContextCompat.getColor(this, R.color.guardian_error));

            showLocationEnableDialog();
            return;
        }

        headerStatus.setText(R.string.scanning);
        headerStatus.setTextColor(ContextCompat.getColor(this, R.color.guardian_warning));

        deviceStatusText.setText(R.string.scanning);
        deviceStatusText.setTextColor(ContextCompat.getColor(this, R.color.guardian_warning));

        bleManager.stop();

        handler.postDelayed(() -> bleManager.startScan(), 500);
    }

    @Override
    protected void onResume() {
        super.onResume();

        locationDialogShowing = false;

        if (PermissionHelper.hasRequiredPermissions(this)) {
            startBleConnection();
        }
    }

    @Override
    public void onScanning() {
        runOnUiThread(() -> {
            headerStatus.setText(R.string.scanning);
            headerStatus.setTextColor(ContextCompat.getColor(this, R.color.guardian_warning));

            deviceStatusText.setText(R.string.scanning);
            deviceStatusText.setTextColor(ContextCompat.getColor(this, R.color.guardian_warning));
        });
    }

    @Override
    public void onConnected() {
        runOnUiThread(() -> {
            headerStatus.setText(R.string.connected);
            headerStatus.setTextColor(ContextCompat.getColor(this, R.color.guardian_success));

            deviceStatusText.setText(R.string.connected);
            deviceStatusText.setTextColor(ContextCompat.getColor(this, R.color.guardian_success));

            batteryValue.setText(R.string.battery_unknown);
        });
    }

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {

                int state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR
                );

                if (state == BluetoothAdapter.STATE_OFF) {

                    runOnUiThread(() -> {
                        headerStatus.setText(R.string.disconnected);
                        headerStatus.setTextColor(ContextCompat.getColor(
                                MainActivity.this,
                                R.color.guardian_error
                        ));

                        deviceStatusText.setText("Bluetooth is turned off");
                        deviceStatusText.setTextColor(ContextCompat.getColor(
                                MainActivity.this,
                                R.color.guardian_error
                        ));

                        showBluetoothEnableDialog();
                    });

                } else if (state == BluetoothAdapter.STATE_ON) {

                    runOnUiThread(() -> {
                        bluetoothDialogShowing = false;

                        headerStatus.setText(R.string.scanning);
                        headerStatus.setTextColor(ContextCompat.getColor(
                                MainActivity.this,
                                R.color.guardian_warning
                        ));

                        deviceStatusText.setText(R.string.scanning);
                        deviceStatusText.setTextColor(ContextCompat.getColor(
                                MainActivity.this,
                                R.color.guardian_warning
                        ));
                    });

                    if (PermissionHelper.hasRequiredPermissions(MainActivity.this)) {
                        startBleConnection();
                    }
                }
            }
        }
    };

    @Override
    public void onDisconnected() {
        runOnUiThread(() -> {
            headerStatus.setText(R.string.disconnected);
            headerStatus.setTextColor(ContextCompat.getColor(this, R.color.guardian_error));

            deviceStatusText.setText(R.string.disconnected);
            deviceStatusText.setTextColor(ContextCompat.getColor(this, R.color.guardian_error));
        });

        handler.postDelayed(this::startBleConnection, 2000);
    }

    @Override
    public void onSOSReceived(String message) {
        runOnUiThread(() -> triggerSOS(getString(R.string.sos_received)));
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> {

            deviceStatusText.setText(error);
            deviceStatusText.setTextColor(ContextCompat.getColor(this, R.color.guardian_error));

            if (error.equals("Bluetooth is turned off")) {
                showBluetoothEnableDialog();
                return;
            }

            if (error.equals("Guardian device not found")) {

                headerStatus.setText(R.string.disconnected);
                headerStatus.setTextColor(ContextCompat.getColor(this, R.color.guardian_error));

                handler.postDelayed(() -> {
                    if (PermissionHelper.hasRequiredPermissions(this)) {
                        startBleConnection();
                    }
                }, 5000);
            }
        });
    }

    private void showBluetoothEnableDialog() {

        if (bluetoothDialogShowing) {
            return;
        }

        bluetoothDialogShowing = true;

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Bluetooth Required")
                .setMessage("GuardianX needs Bluetooth to connect with your Guardian device.")
                .setPositiveButton("Turn On Bluetooth", (dialog, which) -> {
                    Intent enableBluetoothIntent =
                            new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    bluetoothEnableLauncher.launch(enableBluetoothIntent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    bluetoothDialogShowing = false;
                })
                .setOnDismissListener(dialog -> {
                    bluetoothDialogShowing = false;
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bleManager != null) {
            bleManager.stop();
        }

        unregisterReceiver(bluetoothStateReceiver);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionHelper.REQUEST_CODE_PERMISSIONS) {
            if (PermissionHelper.hasRequiredPermissions(this)) {
                startBleConnection();
            } else {
                onError("Required permissions denied");
            }
        }
    }
}