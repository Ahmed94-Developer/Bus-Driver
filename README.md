# 🚍 Bus Driver Trip Tracker

This is a modern Android application for **bus drivers** to start, track, and record trip routes using **GPS and map tracking**. The app supports both **online and offline** map views using osmdroid and stores trip data locally using **Room Database** and remotely with **Firebase Realtime Database**.

---

## ✨ Features

- 🔐 Login & Registration using Firebase Authentication
- 🗺️ Online and Offline map tracking with **osmdroid**
- 📡 Real-time location updates using **Google Play Services (FusedLocationProviderClient)**
- 🧭 Start/Stop trips and record GPS coordinates
- 🧠 Uses MVI architecture pattern
- 💾 Caches trips locally using **Room Database**
- 📤 Saves trip data to Firebase when online
- 🔄 Offline-first support (login + trip tracking)

---

## 🧰 Tech Stack

| Layer         | Technology                                      |
|---------------|--------------------------------------------------|
| UI            | **Jetpack Compose**                             |
| Architecture  | **MVI (Model-View-Intent)**                     |
| State Mgmt    | **StateFlow**, **ViewModel**, **Coroutines**    |
| Data Layer    | **Room Database** (local storage)               |
| Networking    | **Firebase Authentication**, **Firebase Realtime DB** |
| Maps          | **osmdroid**                                    |
| Location      | **Google GMS Location Services**                |
| Permissions   | Runtime permissions + background location       |

---

## 📂 Project Structure
