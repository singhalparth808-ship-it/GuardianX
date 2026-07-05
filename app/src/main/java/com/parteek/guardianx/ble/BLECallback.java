package com.parteek.guardianx.ble;

/**
 * Callback interface used by BLEManager to communicate BLE events
 * back to the UI layer.
 *
 * Version: v0.2
 */
public interface BLECallback {

    void onScanning();

    void onConnected();

    void onDisconnected();

    void onSOSReceived(String message);

    void onError(String error);
}