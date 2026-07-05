package com.parteek.guardianx.ble;

import java.util.UUID;

/**
 * Stores BLE identifiers used to communicate with Guardian firmware.
 *
 * Guardian Firmware: v0.1.1
 * BLE Protocol: v0.1
 */
public final class BLEConstants {

    private BLEConstants() {
        // Utility class
    }

    public static final String DEVICE_NAME = "SOS_Device";

    public static final UUID SERVICE_UUID =
            UUID.fromString("12345678-1234-1234-1234-1234567890ab");

    public static final UUID CHARACTERISTIC_UUID =
            UUID.fromString("abcdefab-1234-5678-1234-abcdefabcdef");

    public static final UUID CCCD_UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static final String MESSAGE_SOS = "SOS";
}