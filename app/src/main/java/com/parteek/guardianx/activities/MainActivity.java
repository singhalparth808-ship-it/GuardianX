package com.parteek.guardianx.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.parteek.guardianx.R;
import com.parteek.guardianx.ble.BLECallback;
import com.parteek.guardianx.ble.BLEManager;
import com.parteek.guardianx.utils.PermissionHelper;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class MainActivity extends AppCompatActivity implements BLECallback {

    private TextView headerStatus;
    private TextView deviceStatusText;
    private TextView batteryValue;

    private boolean bluetoothDialogShowing = false;

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

    private void initializeViews() {
        headerStatus = findViewById(R.id.headerStatus);
        deviceStatusText = findViewById(R.id.deviceStatusText);
        batteryValue = findViewById(R.id.batteryValue);
    }

    private void startBleConnection() {

        if (bleManager == null) {
            return;
        }

        headerStatus.setText(R.string.scanning);
        headerStatus.setTextColor(ContextCompat.getColor(this, R.color.guardian_warning));

        deviceStatusText.setText(R.string.scanning);
        deviceStatusText.setTextColor(ContextCompat.getColor(this, R.color.guardian_warning));

        bleManager.startScan();
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
        runOnUiThread(() -> {

            headerStatus.setText(message);
            headerStatus.setTextColor(ContextCompat.getColor(this, R.color.guardian_error));

            deviceStatusText.setText("SOS received from Guardian");
            deviceStatusText.setTextColor(ContextCompat.getColor(this, R.color.guardian_error));

            android.widget.Toast.makeText(
                    this,
                    "SOS received from Guardian",
                    android.widget.Toast.LENGTH_LONG
            ).show();
        });
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