# 💎 AuraStore
### *Redefining Mobile Luxury Commerce*

[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-purple.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4.svg?style=for-the-badge&logo=android)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Backend-FFCA28.svg?style=for-the-badge&logo=firebase)](https://firebase.google.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

---

## ✨ Overview

**AuraStore** is a state-of-the-art Android e-commerce application designed for the modern luxury market. It combines **cutting-edge technology** with **exquisite design** to deliver a shopping experience that feels as premium as the products it sells.

Built with a **Clean Architecture** approach and the latest **Modern Android Stack**, AuraStore demonstrates how to build scalable, reactive, and beautiful applications in 2024.

---

## 🚀 World-Class Features

### 🎨 **Immersive Design (Glassmorphism)**
Experience a UI that breathes. We've utilized **frosted glass effects**, ambient lighting gradients, and smooth micro-interactions to create a visual language that stands out from the crowd.

### 🛍️ **Smart Commerce Engine**
*   **Real-Time Inventory**: Products and stock levels update instantly via **Cloud Firestore**.
*   **Unified Cart System**: Start shopping on your phone, finish on your tablet. Your cart travels with you.
*   **Wishlist & Favorites**: Save your desire list with a single tap, synced to your cloud profile.

### 🔍 **Intelligent Discovery**
*   **Live Search**: Instant results as you type.
*   **Trending Suggestions**: Discover what's hot with smart tag recommendations (Rolex, Gucci, Dior).
*   **Parallax Details**: Product pages feature cinematic parallax headers for a dramatic presentation.

### 💳 **Secure & Animated Checkout**
A simulated high-fidelity payment flow featuring:
*   **Interactive 3D Card**: Tap to flip and view details.
*   **Seamless Validation**: Instant feedback on form entry.
*   **Order Tracking**: Watch your purchase move from "Placed" to your persistent **Order History**.

---

## 🛠️ The Technology Stack

AuraStore is engineered with the best tools in the industry:

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
*   **Architecture**: MVVM + Clean Architecture + Repository Pattern
*   **Dependency Injection**: [Dagger Hilt](https://dagger.dev/hilt/)
*   **Backend & Data**:
    *   **Firebase Authentication** (Secure Login/Signup)
    *   **Cloud Firestore** (NoSQL Real-time Database)
*   **Async Operations**: Kotlin Coroutines & Flow
*   **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
*   **Navigation**: Type-Safe Compose Navigation

---

## 📂 Project Structure

```bash
com.example.aurastore
├── common          # Resource, Constants, Extension functions
├── data            # Repositories, Data Sources, Firestore impl
├── di              # Hilt Modules (AppModule, RepositoryModule)
├── domain          # Models, Interfaces, Use Cases
├── ui
│   ├── components  # Reusable UI elements (Buttons, Inputs)
│   ├── navigation  # Routes & NavHost
│   ├── screens     # Composable Screens (Home, Cart, Profile)
│   ├── theme       # AuraTheme (Colors, Type, Shapes)
│   └── viewmodel   # State Management
└── MainActivity.kt # Entry Point
```

---

## 🏁 Getting Started

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/aura-store.git
    ```
2.  **Open in Android Studio**: Ensure you are using the latest Koala or Ladybug feature drop.
3.  **Sync Gradle**: Download all modern dependencies.
4.  **Run**: Select your emulator or physical device and press **Run**.

> **Note**: This project uses a real Firebase backend. Ensure `google-services.json` is configured if you fork the project.

---

## 🌟 Acknowledgements

Crafted with precision and passion for the Android community.

*Design System inspired by modern luxury aesthetics.*
*Architecture inspired by Google's Guide to App Architecture.*

---

**© 2026 AuraStore.** All rights reserved.
