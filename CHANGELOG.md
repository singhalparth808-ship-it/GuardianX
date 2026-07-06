# GuardianX Changelog

All notable changes to this project will be documented here.

---

## v0.3 - SOS Engine

Release Date: 05 July 2026

### Added

- Emergency notification system
- Phone vibration feedback for SOS
- Hardware SOS handling through BLE
- In-app Hold-to-SOS button
- Shared SOS trigger flow for hardware and app SOS
- Bluetooth enable dialog
- Location enable dialog for BLE scanning reliability

### Changed

- SOS handling moved into a common triggerSOS flow
- MainActivity now responds to both hardware and app SOS events

### Status

✅ Stable
✅ Hardware SOS working
✅ In-app SOS working

---

## v0.2 - BLE Foundation

Release Date: 05 July 2026

### Added

- Bluetooth permissions for Android
- BLE constants for Guardian firmware
- BLE callback interface
- BLEManager for scanning, connecting, service discovery, and notifications
- Automatic scan on app launch
- Bluetooth enable prompt
- Bluetooth state handling while app is open
- Guardian device not found handling
- SOS notification reception from ESP32 hardware button

### Changed

- MainActivity now connects UI with BLEManager
- Guardian status card now updates based on BLE state

### Status

✅ Stable
✅ Hardware SOS received successfully

---

## v0.1.4 - Home Screen Padding Fix

Release Date: 05 July 2026

### Fixed

- Fixed home screen cards touching screen edges
- Removed Edge-to-Edge padding override from MainActivity
- Restored XML-defined layout padding

### Status

✅ Stable

---

## v0.1.3 - Home Screen

Release Date: 05 July 2026

### Added

- Component-based home screen layout
- Header card with GuardianX branding
- Device status card with battery placeholder
- Large circular Hold-to-SOS button
- Quick action cards for Location, Contacts, Emergency, and Settings
- Non-scrolling emergency-first home screen structure

### Changed

- Replaced default Android layout with GuardianX product UI foundation
- Updated layout architecture to use reusable view components

### Status

✅ Stable

---

## v0.1.2 - Design System

Release Date: 05 July 2026

### Added

- Standardized application color palette
- Global dimensions resource
- Centralized string resources
- Foundation for a consistent Material 3 design

### Changed

- Updated README to reflect current project version

### Status

✅ Stable

---

## v0.1.1 - Project Documentation

Release Date: 05 July 2026

### Added

- README.md
- CHANGELOG.md
- ROADMAP.md
- Initial project documentation
- Development workflow documentation

### Status

✅ Stable

---

## v0.1 - Initial GuardianX Project

Release Date: July 2026

### Added

- Android Studio project created
- Git repository initialized
- Modular package architecture
- MainActivity moved to activities package
- UI package structure added

### Status

✅ Stable