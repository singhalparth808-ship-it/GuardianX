# GuardianX

> GuardianX is a BLE-based personal safety ecosystem consisting of an ESP32-powered wearable device and an Android application.

---

## Vision

GuardianX aims to provide fast, reliable, and accessible personal safety through a wearable BLE device and an intelligent Android application.

GuardianX is being built as a minimal but expandable safety platform. The current version focuses on a reliable college-demo-ready foundation, while future versions will expand into contacts, SMS, location sharing, battery monitoring, secure pairing, and advanced safety workflows.

---

## Features

### Completed

- Hardware SOS Trigger through ESP32 BLE
- In-App Triple-Tap SOS Trigger
- BLE Auto Connection
- Emergency Notification
- Phone Vibration Feedback
- Emergency Toolkit
- Direct Emergency Calling
- Nearby Emergency Locations using Google Maps
- Emergency Contacts saved locally
- SOS SMS sent to saved emergency contacts
- Google Maps location link included in SOS SMS
- Duplicate contact prevention
- Contact delete support
- Local Alert History
- Hardware/App SOS history tracking

### Planned

- Emergency Contacts
- SMS with Location Sharing
- WhatsApp Sharing
- Safety Timer
- Check-In Feature
- Battery Monitoring
- QR Pairing
- Alert History
- Guardian Dashboard

---

## Current Version

v0.7

Status: 🟢 Active Development

---

## Project Structure

```text
activities/
ble/
database/
emergency/
managers/
models/
receivers/
ui/
    adapters/
    components/
    dialogs/
    fragments/
utils/
widgets/