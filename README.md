# Weathervane

A native Android weather application built with Java that provides real-time weather updates and a 3-day forecast. The app dynamically adapts to the user's current location via GPS or allows for manual city searches, delivering localized weather data in a clean, responsive UI.

## Tech Stack & Architecture

* **Language:** Java
* **Platform:** Android SDK (Minimum API Level target recommended: 24+)
* **Architecture Highlights:**
  * **Asynchronous Networking:** Uses background threads (`new Thread(() -> {...})`) and `runOnUiThread` to prevent blocking the main UI thread during network requests.
  * **Native JSON Parsing:** Utilizes standard `org.json` for parsing complex nested REST API payloads from the weather provider (OpenWeatherMap).
  * **Modern Android APIs:** Implements `ActivityResultLauncher` for clean, decoupled handling of both location permissions and inter-activity data passing.

## Getting Started

### Prerequisites
* [Android Studio](https://developer.android.com/studio) (Latest version recommended)
* An active API key from your weather data provider (e.g., OpenWeatherMap).
