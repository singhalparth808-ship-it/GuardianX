package com.parteek.guardianx.ble;

import android.bluetooth.BluetoothGattDescriptor;
import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.pm.PackageManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothGattService;

import androidx.core.content.ContextCompat;

/**
 * Handles BLE communication between GuardianX Android app
 * and Guardian Firmware v0.1.1.
 *
 * Version: v0.2
 */
public class BLEManager {

    private boolean isScanning = false;

    private static final String TAG = "BLEManager";
    private static final long SCAN_TIMEOUT_MS = 10000;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Context context;
    private final BLECallback callback;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic sosCharacteristic;

    public BLEManager(Context context, BLECallback callback) {
        this.context = context;
        this.callback = callback;

        BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();

            if (bluetoothAdapter != null) {
                bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public boolean isBluetoothReady() {

        if (bluetoothAdapter == null) {
            callback.onError("Bluetooth adapter not available");
            return false;
        }

        if (!hasConnectPermission()) {
            callback.onError("Nearby devices permission not granted");
            return false;
        }

        if (!bluetoothAdapter.isEnabled()) {
            callback.onError("Bluetooth is turned off");
            return false;
        }

        return true;
    }

    @SuppressLint("MissingPermission")
    public void startScan() {

        if (!isBluetoothReady()) {
            return;
        }

        if (!hasScanPermission()) {
            callback.onError("Bluetooth scan permission not granted");
            return;
        }

        if (bluetoothLeScanner == null) {
            callback.onError("BLE scanner unavailable");
            return;
        }

        if (isScanning) {
            return;
        }

        isScanning = true;

        callback.onScanning();

        Log.d(TAG, "BLE scan started");

        bluetoothLeScanner.startScan(scanCallback);

        handler.postDelayed(() -> {
            if (isScanning) {
                stopScan();
                callback.onError("Guardian device not found");
            }
        }, SCAN_TIMEOUT_MS);
    }

    @SuppressLint("MissingPermission")
    public void stopScan() {

        if (!isScanning) {
            return;
        }

        if (!hasScanPermission()) {
            return;
        }

        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(scanCallback);
        }

        isScanning = false;

        Log.d(TAG, "BLE scan stopped");
    }

    private final ScanCallback scanCallback = new ScanCallback() {

        @Override
        @SuppressLint("MissingPermission")
        public void onScanResult(int callbackType, ScanResult result) {

            BluetoothDevice device = result.getDevice();

            if (!hasConnectPermission()) {
                return;
            }

            String deviceName = device.getName();

            if (deviceName == null) {
                return;
            }

            Log.d(TAG, "Found BLE device: " + deviceName);

            if (BLEConstants.DEVICE_NAME.equals(deviceName)) {

                Log.d(TAG, "Guardian device found");

                stopScan();

                connectToDevice(device);
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {

        if (!hasConnectPermission()) {
            callback.onError("Bluetooth connect permission not granted");
            return;
        }

        Log.d(TAG, "Connecting to: " + device.getName());

        bluetoothGatt = device.connectGatt(
                context,
                false,
                gattCallback
        );
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        @SuppressLint("MissingPermission")
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {

                Log.d(TAG, "BLE connected");

                bluetoothGatt = gatt;

                callback.onConnected();

                if (hasConnectPermission()) {
                    gatt.discoverServices();
                }

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                Log.d(TAG, "BLE disconnected");

                callback.onDisconnected();

                closeGatt();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            if (status != BluetoothGatt.GATT_SUCCESS) {
                callback.onError("Service discovery failed");
                return;
            }

            BluetoothGattService service =
                    gatt.getService(BLEConstants.SERVICE_UUID);

            if (service == null) {
                callback.onError("Guardian service not found");
                return;
            }

            sosCharacteristic =
                    service.getCharacteristic(BLEConstants.CHARACTERISTIC_UUID);

            if (sosCharacteristic == null) {
                callback.onError("SOS characteristic not found");
                return;
            }

            enableNotifications();
            // Notifications will be enabled in the next checkpoint.
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor,
                                      int status) {

            if (!BLEConstants.CCCD_UUID.equals(descriptor.getUuid())) {
                return;
            }

            if (status == BluetoothGatt.GATT_SUCCESS) {
                android.util.Log.d(TAG, "Notifications enabled successfully");
                callback.onError("Guardian Ready");
            } else {
                callback.onError("Notification setup failed: " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

            if (!BLEConstants.CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                return;
            }

            String message = characteristic.getStringValue(0);

            handleIncomingMessage(message);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic,
                                            byte[] value) {

            android.util.Log.d(TAG, "New BLE notification callback fired");

            if (!BLEConstants.CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                return;
            }

            String message = new String(value);

            handleIncomingMessage(message);
        }
    };

    private void handleIncomingMessage(String message) {

        if (message == null) {
            android.util.Log.d(TAG, "BLE message is null");
            return;
        }

        message = message.trim();

        android.util.Log.d(TAG, "BLE message received: [" + message + "]");

        if (BLEConstants.MESSAGE_SOS.equals(message)) {
            callback.onSOSReceived(message);
        } else {
            android.util.Log.d(TAG, "Unknown BLE message: " + message);
        }
    }

    @SuppressLint("MissingPermission")
    private void closeGatt() {

        if (bluetoothGatt == null) {
            return;
        }

        if (hasConnectPermission()) {
            bluetoothGatt.close();
        }

        bluetoothGatt = null;
        sosCharacteristic = null;
    }

    protected boolean hasScanPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED;
        }

        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    protected boolean hasConnectPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @SuppressLint("MissingPermission")
    private void enableNotifications() {

        if (bluetoothGatt == null) {
            callback.onError("GATT not available");
            return;
        }

        if (sosCharacteristic == null) {
            callback.onError("SOS characteristic not available");
            return;
        }

        if (!hasConnectPermission()) {
            callback.onError("Bluetooth connect permission not granted");
            return;
        }

        boolean notificationSet =
                bluetoothGatt.setCharacteristicNotification(sosCharacteristic, true);

        if (!notificationSet) {
            callback.onError("Failed to enable local notifications");
            return;
        }

        BluetoothGattDescriptor descriptor =
                sosCharacteristic.getDescriptor(BLEConstants.CCCD_UUID);

        if (descriptor == null) {
            callback.onError("CCCD descriptor not found");
            return;
        }

        boolean writeStarted;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            int result = bluetoothGatt.writeDescriptor(
                    descriptor,
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            );

            writeStarted = result == BluetoothGatt.GATT_SUCCESS;

        } else {

            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            writeStarted = bluetoothGatt.writeDescriptor(descriptor);
        }

        if (!writeStarted) {
            callback.onError("Failed to write CCCD descriptor");
            return;
        }

        android.util.Log.d(TAG, "CCCD descriptor write started");
    }

    @SuppressLint("MissingPermission")
    public void stop() {

        stopScan();

        if (bluetoothGatt != null && hasConnectPermission()) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
        }

        bluetoothGatt = null;
        sosCharacteristic = null;
        isScanning = false;
    }
}