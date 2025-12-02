# FRED Mobile – Field Ready Employee Dashboard

## Overview

FRED (Field Ready Employee Dashboard) is an Android app prototype for field
workers who need a simple way to check in/out of sites, report safety
incidents, and review their activity history. The goal is to make day-to-day
safety and attendance tasks easier to track on a mobile device.

This project is my final Android assignment for Mobile Application Development.

---

## Application Description  

FRED Mobile (Field Ready Employee Dashboard) is designed for field workers who 
need a simple and reliable way to check into job sites, report safety incidents, 
and review their daily activity. The goal is to reduce missed check-ins, simplify 
safety reporting, and give workers quick access to important site conditions such 
as weather and upcoming hazards.

The app includes a clean home dashboard, a Check-In/Check-Out workflow, an 
Incident Report form, a Site list, a History log, and a full Settings page. In 
Milestone 1 all data is local, but later milestones will integrate live weather 
APIs, Firebase authentication, and a real database.

FRED focuses on a clear UI, mobile-first usability, and accessibility to support 
workers who may be on-site, underground, or in remote locations where fast and 
simple interaction is essential.

---

## Accessibility Review & Fix Plan

I ran the Android Accessibility Scanner on all core screens (Auth, Home, Sites, Check-In, Incident, History, Settings). A few items were flagged, and a plan has been documented below:

### 1. Duplicate Item Descriptions (Switches)
Scanner flagged multiple switches sharing the same speakable text (“Off”).  
**Fix (planned for M2/M3):**  
Add unique content descriptions such as:
- "Toggle notifications"
- "Toggle dark mode"
- "Toggle auto check-in"

### 2. Repeated “View details (coming soon)” Buttons
All site cards have identical button text.  
**Fix (planned):**  
Replace with dynamic labels:
- “View details for North River Plant”
- “View details for West Substation”

### 3. Repeated “Status: Completed” in History
History cards repeat the same spoken description.  
**Fix (planned):**  
Include more context like the site name or date in accessibility descriptions.

### 4. Text Contrast on Buttons
Scanner suggested increasing contrast on certain light-colored buttons.  
**Fix (planned):**  
Use Material3 `onPrimary` / `onSurface` colors to meet WCAG AA contrast.

### Summary
Most issues were minor and expected for placeholder UI.  
Fixes will be addressed as real dynamic data and site-specific screens are added in Milestones 2 and 3.


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

