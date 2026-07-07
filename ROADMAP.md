# GuardianX Roadmap

GuardianX follows milestone-based development. Each version adds one stable layer to the product.

---

## v0.1 - Initial Project

Status: ✅ Completed

- Created GuardianX Android project
- Initialized Git repository
- Created modular package structure
- Moved MainActivity to activities package

---

## v0.1.1 - Project Documentation

Status: ✅ Completed

- Added README.md
- Added CHANGELOG.md
- Added ROADMAP.md
- Documented project vision
- Documented development workflow

---

## v0.1.2 - Design System

Status: ✅ Completed

- Added global color palette
- Added global dimensions
- Added standardized string resources
- Established UI foundation

---

## v0.1.3 - Home Screen

Status: ✅ Completed

- Added GuardianX home screen
- Added header card
- Added device status card
- Added SOS button
- Added quick action cards

---

## v0.1.4 - Home Screen Padding Fix

Status: ✅ Completed

- Fixed edge padding issue
- Removed Edge-to-Edge padding override
- Restored XML-defined layout spacing

---

## v0.2 - BLE Foundation

Status: ✅ Completed

- Added Bluetooth permissions
- Added BLE constants
- Added BLEManager
- Added scan and connect logic
- Added service discovery
- Added notification subscription
- Added ESP32 SOS reception

---

## v0.3 - SOS Engine

Status: ✅ Completed

- Added emergency notification system
- Added phone vibration feedback
- Added hardware SOS handling
- Added in-app triple-tap SOS trigger
- Added shared SOS trigger flow

---

## v0.4 - Emergency Toolkit

Status: ✅ Completed

- Added Emergency Toolkit screen
- Added direct emergency calling
- Added emergency helpline buttons
- Added nearby emergency locations
- Added Google Maps integration
- Changed SOS activation to triple-tap
- Moved nearby locations to Location screen

---

## v0.5 - Emergency Contacts

Status: ✅ Completed

- Add Emergency Contacts screen
- Add contact input UI
- Save contacts locally
- Show saved emergency contacts
- Prepare contacts for SMS/call workflow

---

## v0.6 - Location + SMS Engine

Status: ✅ Completed

- Get phone location
- Generate Google Maps location link
- Send SOS SMS to saved contacts
- Prepare live location sharing workflow

---

## v0.7 - Alert History

Status: ✅ Completed

- Store SOS events locally
- Show alert history
- Save alert type, time, and source

---

## v0.8 - Safety Timer

Status: ✅ Completed

- Added Settings hub
- Added Safety Timer
- Added custom timer
- Added background timer support
- Added timer restore after app reopen
- Added automatic SOS trigger
- Added timer cancel option
- Connected Safety Timer to SMS, notification, vibration, and alert history

---

## v0.9 - Demo Polish and Stability

Status: ✅ Completed

- Cleaned hardcoded strings
- Improved permission handling
- Added live Bluetooth state response
- Added live Location state response
- Completed full feature test pass
- Prepared app for demo readiness

---

## v1.0 - First Public Demo Release

Status: Upcoming

- Stable college demo build
- Hardware SOS
- App SOS
- Emergency toolkit
- Emergency contacts
- SMS/location workflow
- Clean UI
- GitHub documentation