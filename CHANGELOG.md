# GuardianX Changelog

All notable changes to this project will be documented here.

---

## v0.6 - Location + SMS Engine

Release Date: 07 July 2026

### Added

- SMS permission support
- ContactStorageManager for centralized contact access
- SMSManager for sending SOS messages
- SOS SMS sending to saved emergency contacts
- Google Maps location link in SOS messages
- LocationHelper for last known location access
- Duplicate contact prevention
- Delete contact option with trash icon

### Changed

- ContactsActivity now uses ContactStorageManager
- SOS message shortened for faster SMS delivery
- Saved contact order now works as emergency priority order

### Status

✅ Stable  
✅ SOS SMS working  
✅ Location link working  
✅ Duplicate contact blocking working  
✅ Contact delete working

---

## v0.5 - Emergency Contacts

Release Date: 06 July 2026

### Added

- Emergency Contacts screen
- Contact name input
- Contact phone number input
- Save contact button
- Local contact storage using SharedPreferences
- Saved contacts display section
- Home screen Contacts card navigation

### Changed

- Contact input text color improved for readability
- Contact input hint color improved for readability

### Status

✅ Stable  
✅ Contacts saving working  
✅ Contacts persist after app restart

---

## v0.4 - Emergency Toolkit

Release Date: 06 July 2026

### Added

- Emergency Toolkit screen
- Direct call support for emergency helplines
- Emergency / Police call button
- Ambulance call button
- Fire emergency call button
- Women helpline call button
- Child helpline call button
- Cyber crime helpline call button
- CALL_PHONE permission handling
- Safe fallback to dialer if direct call fails
- Telephony feature declaration in AndroidManifest
- LocationActivity for nearby emergency locations
- Nearby Police Station search using Google Maps
- Nearby Fire Station search using Google Maps
- Nearby Hospital search using Google Maps

### Changed

- Home screen Location button now opens nearby emergency locations instead of current location
- Nearby emergency places moved from Emergency screen to Location screen
- Emergency Toolkit now focuses only on direct emergency calling
- Emergency buttons enlarged for better accessibility
- SOS activation changed from hold-to-SOS to triple-tap SOS activation

### Status

✅ Stable  
✅ Emergency calls working  
✅ Nearby location search working  
✅ Triple-tap app SOS working

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