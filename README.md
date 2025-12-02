# FRED Mobile – Field Ready Employee Dashboard

## Overview

FRED (Field Ready Employee Dashboard) is an Android app prototype for field
workers who need a simple way to check in/out of sites, report safety
incidents, and review their activity history. The goal is to make day-to-day
safety and attendance tasks easier to track on a mobile device.

This project is my final Android assignment for Mobile Application Development.

---

## Current Features – Milestone 1 (UI)

- **Home dashboard**
    - Quick access to Check-In, Incident Report, Sites, History, and Settings.

- **Sites screen**
    - List of sample work sites shown as Material3 cards.
    - Each site displays name, address, and coordinates.
    - Designed to later show weather and site details.

- **Check-In screen**
    - Shows a “current site” card and status (checked in / not checked in).
    - Large Check In / Check Out buttons.
    - Weather preview section (currently using sample text, will call a real API in Milestone 2).

- **Incident Report screen**
    - Form with severity selector, description field, optional photo checkbox,
    - and a submit button with snackbar feedback.
    - For now the data is local only (no database yet).

- **History screen**
    - Two tabs: Check-ins and Incidents.
    - Each tab shows sample history entries using card lists.
    - Will later be backed by Firestore or Room.

- **Settings screen**
    - Toggles for notifications, dark mode, and “auto check-in” (placeholder).
    - Dropdown for choosing weather units (Celsius / Fahrenheit).
    - For Milestone 1 all settings are stored in local state only.

---

## Planned Features

### Milestone 2 – API Integration (Weather)

Planned external endpoints (likely from a weather API such as OpenWeatherMap):

1. **Current weather for a site** (by latitude/longitude)
2. **Short-term forecast** for the site (next hours / days)
3. **Weather alerts** for the area
4. **Air quality index (AQI)** at the site

These will be displayed on the Check-In screen and Site/Weather views to help
workers understand safety conditions before and during a shift.

### Milestone 3 – Data Persistence & Authentication

- **Firebase Authentication**
    - Email/password sign-in.
    - Second sign-in method (e.g., Google or GitHub).

- **Database persistence**
    - Store users, sites, check-ins, and incidents in Firestore and/or Room.
    - Support full CRUD where appropriate (create, read, update, delete).

- **Settings persistence**
    - Save user preferences (notifications, dark mode, weather units) using
      DataStore.

---

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material3
- **Navigation:** Navigation Compose
- **Architecture:** Single-activity with composable screens and simple ViewModels
- **Backend (planned):** Firebase Auth, Firestore, REST weather API

---

## How to Run

1. Clone the repository.
2. Open the project in Android Studio (Giraffe or newer).
3. Sync Gradle to download dependencies.
4. Run the `app` module on an emulator or physical device (API 24+).

For future milestones, additional setup (Firebase `google-services.json`, API keys)
will be noted here.

