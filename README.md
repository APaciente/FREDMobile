# FRED Mobile – Field Ready Employee Dashboard

## Overview

FRED Mobile is an Android application built for field workers who need a simple and reliable way to:

- Check in and out of work sites
- Report safety incidents
- View site information
- Review daily activity
- Monitor weather and environmental conditions in real time

The app emphasizes clarity, accessibility, and mobile-first workflows, and is being developed as part of the **Mobile Application Development Final Project**.

---

## Application Description

FRED Mobile streamlines workplace safety operations by centralizing:

- Site check-ins
- Incident reporting
- Weather and air-quality monitoring
- Activity history and logs

Originally, all data was local (Milestone 1).  
In Milestone 2, the app integrates **live weather data** using three OpenWeatherMap API endpoints:

- **Current Weather**
- **Short-term Forecast (3-hour)**
- **Air Quality Index (AQI)**

These values appear directly on the Check-In screen so workers can understand conditions before starting their shift.

The UI follows **Material3** design, ensuring an accessible and intuitive experience even in demanding environments such as industrial sites, remote locations, or underground facilities.

---

## Accessibility Review & Fix Plan

The Google Accessibility Scanner was run across key screens (Auth, Home, Check-In, Incident, Sites, History, Settings).  
Findings and planned fixes include:

### 1. Duplicate switch descriptions
Scanner warned that switches speak the same text (e.g., “Off”).  
**Planned Fix:** Add unique `contentDescription` values such as:
- “Enable notifications”
- “Enable dark mode”
- “Allow auto check-in”

### 2. Repeated ‘View details’ buttons on Sites screen
All cards currently share identical label text.  
**Planned Fix:** Make them dynamic, e.g.:
- “View details for North River Plant”

### 3. Missing context in History items
Items such as “Status: Completed” repeat without meaning.  
**Planned Fix:** Add context-rich descriptions.

### 4. Minor text contrast warnings
Scanner flagged specific UI elements.  
**Planned Fix:** Use Material3 semantic colors (`onPrimary`, `onSurface`) for WCAG AA compliance.

---

## Current Features (Milestone 2)

### Home Dashboard
- Navigation to Check-In, Sites, Incidents, History, and Settings
- Rotating safety messages (5-item cycle)
- Clean and accessible Material3 layout

### Check-In Screen
- Displays active site (name, details)
- Local check-in/check-out logic
- Integrated **live weather** via 3 API endpoints:
  - Current temperature & conditions
  - 3-hour forecast preview
  - Air Quality Index (AQI)
- Weather ViewModel handles data retrieval and UI state

### Incident Reporting
- Severity selector (Low / Medium / High)
- Description field with validation
- Optional photo picker (local only)
- Snackbar confirmation on submit

### Sites Screen
- List of sample work sites
- Placeholder detail navigation (expanded in Milestone 3)

### History Screen
- Tabs for Check-Ins and Incidents
- Displays previous activity

### Settings Screen
- Notification toggle
- Dark mode toggle
- Auto check-in toggle
- Celsius/Fahrenheit selection
- Sign-out placeholder (implemented in Milestone 3)

---

## Planned Features (Milestone 3)

### Firebase Authentication
- Email/password login
- Google Sign-In
- Auth-gated navigation

### Database Persistence (Firestore)
- Persist check-ins
- Persist incidents
- Store user roles (admin/worker)
- Full CRUD support

### Settings Persistence (DataStore)
- Save dark mode preference
- Save weather units
- Save notification and auto-check-in toggles

---

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose + Material3
- **Architecture:** Single-Activity, MVVM, State Hoisting
- **Networking:** Retrofit + Coroutines
- **Data/Models:** Kotlin data classes, UI state wrappers
- **Backend:** OpenWeatherMap API (weather, forecast, AQI)
- **Persistence:** Firestore (Milestone 3)
- **Navigation:** Navigation Compose

---

## How to Run

1. Clone the repository
2. Open the project using **Android Studio Giraffe or newer**
3. Sync Gradle
4. Add your OpenWeatherMap API key inside `WeatherRepository`
5. Run the **app** module on an emulator or physical device (Android 7.0+)

Firebase setup instructions will be added when Milestone 3 is completed.

---
