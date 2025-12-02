# FRED Mobile – Field Ready Employee Dashboard

## Overview

FRED Mobile is an Android application designed for field workers who need a simple
and reliable way to:
- Check in and out of work sites
- Report safety incidents
- View site information
- Review their daily activity
- Quickly see weather and environmental conditions

The app focuses on clarity, ease of use, and mobile-first workflows. It is being
developed as part of my final project for **Mobile Application Development**.

---

## Application Description

FRED Mobile helps prevent missed check-ins, streamlines safety reporting, and
gives field workers fast access to site conditions such as temperature, forecasts,
and air quality.

In Milestone 1, all data was local.  
In Milestone 2, the app now integrates real weather data powered by the
OpenWeatherMap API — including three live endpoints:

- **Current weather** (temperature, description)
- **Short-term forecast** (next 3 hours)
- **Air quality** (AQI)

This information appears directly on the Check-In screen to help workers
understand their environment before starting a shift.

The design follows Material3 guidelines and prioritizes accessibility,
clarity, and real-world usability for workers who may be on-site, underground,
or in remote areas.

---

## Accessibility Review & Fix Plan

The Android Accessibility Scanner was run on all screens (Auth, Home, Sites,
Check-In, Incident, History, Settings). A few minor issues were detected, and
the planned fixes are listed below.

### 1. Duplicate item descriptions (Switches)
Scanner flagged repeated speakable text such as “Off.”  
**Fix (planned for M2/M3):**  
Add unique `contentDescription` values like:
- “Toggle notifications”
- “Toggle dark mode”
- “Toggle auto check-in”

### 2. Repeated “View details” buttons on the Sites screen
All site cards currently show the same placeholder text.  
**Fix (planned):**  
Replace with dynamic labels:
- “View details for North River Plant”
- “View details for West Substation”

### 3. Repeated text descriptions in the History screen
Items such as “Status: Completed” repeat without context.  
**Fix (planned):**  
Add descriptive text such as:
- “Status completed for North River Plant, November 25”

### 4. Text contrast warnings
Scanner recommended stronger contrast for certain button text.  
**Fix (planned):**  
Use Material3 semantic colors (`onPrimary`, `onSurface`) to meet WCAG AA.

---

## Current Features – Milestone 2

### Home Dashboard
- Navigation to Check-In, Sites, Incident Report, History, and Settings
- Clean Material3 layout with modern card design

### Check-In Screen
- Shows current site (name, address, coordinates)
- Local check-in / check-out state
- Integrated real-time weather information:
  - **Current temperature + conditions**
  - **Next 3-hour forecast**
  - **Air Quality Index (AQI)**
- Uses all three required PM2 API endpoints

### Incident Report Screen
- Severity selector (Low, Medium, High)
- Description text field
- Optional “Include photo” placeholder
- Snackbar confirmation on submit

### Sites Screen
- List of sample work sites (from Milestone 1)
- Each card includes name, address, and coordinates
- “View details” placeholder (to be expanded in Milestone 3)

### History Screen
- Two tabs: **Check-ins** and **Incidents**
- Preloaded sample data
- Will later load from Firestore

### Settings Screen
- Toggles for notifications, dark mode, auto check-in
- Weather unit dropdown (Celsius/Fahrenheit)
- Placeholder location for sign-out button (Milestone 3)
- Local-only persistence in PM1 and PM2

---

## Planned Features

### Milestone 3 – Authentication & Database

### Firebase Authentication
- Email/password login
- Second login method (Google or GitHub)

### Database Persistence (Firestore or Room)
- Store sites
- Store check-ins
- Store incident reports
- Store user data
- Full CRUD support

### Settings Persistence (Jetpack DataStore)
- Dark mode preference
- Notification settings
- Weather unit selection

---

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material3
- **Architecture:** Single Activity, ViewModel, State Hoisting
- **Networking:** Retrofit + Coroutines
- **Backend:** OpenWeatherMap API (current, forecast, AQI)
- **Navigation:** Navigation Compose

---

## How to Run

1. Clone the repository.
2. Open the project in **Android Studio Giraffe or newer**.
3. Sync Gradle.
4. Add your `apikey` inside the `WeatherRepository`.
5. Run the **app** module on an emulator or physical device (API 24+).

Firebase setup instructions will be added when Milestone 3 development begins.

---
