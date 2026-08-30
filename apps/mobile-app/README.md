# 📱 North East Region (NER) AI Logistics Mobile Applications

This repository contains the shared Flutter workspace for the North Eastern Region AI-Powered Smart Logistics & Accessibility Platform, hosting two distinct application entry points:

- **🚛 NER Driver App** (`lib/main_driver.dart`)
- **👷 NER Field Officer App** (`lib/main_field_officer.dart`)

---

## 🏗️ Architecture Overview

```text
apps/mobile-app/
├── lib/
│   ├── main_driver.dart               <-- Entry point for Driver App
│   ├── main_field_officer.dart        <-- Entry point for Field Officer App
│   ├── app/                           <-- MaterialApp containers
│   ├── core/
│   │   ├── api/                       <-- ApiClient with JWT Bearer Token Injection
│   │   ├── auth/                      <-- Encrypted Secure Storage
│   │   ├── config/                    <-- Host & Port AppConfig (Android 10.0.2.2)
│   │   ├── offline/                   <-- SQLite Offline Queue & Sync Engine
│   │   └── websocket/                 <-- STOMP Client Service for WebSockets
│   ├── shared/
│   │   └── models/                    <-- Typed Dart Models (User, Vehicle, Incident, SOS)
│   └── features/
│       ├── auth/                      <-- 1-Tap OTP & Password Login
│       ├── driver/                    <-- Driver Dashboard, Cargo, Safety Bubble, SOS
│       └── field_officer/             <-- Officer Dashboard, Geo-Incident & Road Status
```

---

## 🚀 How to Run the Applications

### 1. Install Dependencies
```bash
cd apps/mobile-app
flutter pub get
```

### 2. Run 🚛 NER Driver App
```bash
flutter run -t lib/main_driver.dart
```

### 3. Run 👷 NER Field Officer App
```bash
flutter run -t lib/main_field_officer.dart
```

---

## ⚙️ Backend Host Connection Guide

The API host is configured dynamically inside `lib/core/config/app_config.dart`:

- **Android Emulator**: Uses `http://10.0.2.2:8080` (automatically routes to host PC `localhost:8080`).
- **iOS Simulator / Web**: Uses `http://localhost:8080`.
- **Physical Mobile Device**: Update `.env` or `app_config.dart` with your PC's local Wi-Fi LAN IP (e.g. `http://192.168.1.15:8080`).

---

## 📴 Offline-First Architecture & Idempotency

1. All offline operations (GPS location fixes, disaster incident reports, road blockage reports) are assigned a unique `clientEventId` UUID upon creation.
2. Events are saved locally in SQLite (`OfflineQueueService`).
3. When network connectivity is restored (`SyncEngine`), events are automatically batch-flushed to `/api/incidents/sync` and `/api/tracking/location/batch`.
4. The Spring Boot backend checks `clientEventId` to guarantee idempotency and prevent duplicate records.

---

## 🔑 Demonstration Accounts

- **Driver Account**: Mobile OTP `+919876543213` (Demo OTP: `123456`) or `driver@sih.gov.in` / `Driver@123`.
- **Field Officer Account**: `field@sih.gov.in` / `Officer@123`.
