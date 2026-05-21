# SonzaiX Streaming Android App

A premium native Android application built using Kotlin and Jetpack Compose to stream short dramas from multiple providers. The app dynamically integrates with the SonzaiX API, tracks user watchlist and watch history, supports customized video player headers via ExoPlayer, and features a developer API testing tool.

---

## 🚀 Fitur Utama

- 📱 **Home Screen & Category Browser**: Menampilkan daftar drama terbaru, populer, dan rekomendasi dari berbagai provider. Dilengkapi dengan filter selector.
- 🎬 **ExoPlayer Video Player**: Pemutar media berkinerja tinggi yang mendukung stream HLS (`.m3u8`) dan MP4.
  - *Autoplay Next*: Memutar episode berikutnya secara otomatis.
  - *Resume Position*: Menyimpan progres menonton ke database lokal setiap 5 detik dan melanjutkan dari posisi terakhir.
  - *Header Kustom*: Menyuntikkan User-Agent dan header HTTP dinamis untuk otentikasi stream.
- 🔍 **Pencarian Cerdas**: Input pencarian dengan mekanisme *debounce* 500ms dan *infinite scrolling* (paginasi).
- 📂 **Watchlist & History**:
  - *Favorit*: Menandai drama favorit menggunakan composite key untuk mencegah konflik id antar-provider.
  - *Riwayat Tontonan*: Melacak progres menonton individual per episode dengan progress bar linear.
- ⚙️ **Pengaturan & Preferensi**: Mengatur provider utama, bahasa konten default (Indonesia/Inggris), memeriksa kesehatan API server, dan menghapus cache/database.
- 🛠️ **Developer API Tester**: Antarmuka bagi pengembang untuk menguji berbagai endpoint, memanipulasi parameter query, memantau latensi dan status HTTP, serta menyalin respon JSON.

---

## 🛠️ Tech Stack & Arsitektur

Aplikasi ini dibangun menggunakan arsitektur modern Android (Clean Architecture / MVVM):

- **Bahasa**: Kotlin 1.9+
- **Framework UI**: Jetpack Compose (Material 3)
- **Dependency Injection**: Hilt (Dagger Hilt)
- **Pemutar Video**: Media3 ExoPlayer
- **Database Lokal**: Room DB (Penyimpanan Watchlist & History)
- **Key-Value Store**: Jetpack DataStore (Preferensi Pengguna)
- **Networking**: Retrofit & OkHttp (Interaksi REST API)
- **Image Loading**: Coil (Asynchronous Image Loading)
- **Manajemen Utas**: Kotlin Coroutines & Flow

---

## 📦 Panduan Kompilasi & Build Terminal

Aplikasi ini dapat dibangun dan diinstal langsung melalui terminal tanpa memerlukan Android Studio secara interaktif.

### 1. Build APK (Assembling Debug APK)

Aplikasi akan dikompilasi menggunakan Gradle Daemon bawaan.

- **Windows**:
  Double-klik file `build-apk.bat` atau jalankan di PowerShell/CMD:
  ```cmd
  build-apk.bat
  ```
- **Linux / macOS**:
  Berikan izin eksekusi lalu jalankan:
  ```bash
  chmod +x build-apk.sh
  ./build-apk.sh
  ```

*Hasil APK yang telah dikompilasi akan berada di:* `app/build/outputs/apk/debug/app-debug.apk`

### 2. Instalasi APK ke Perangkat (ADB Deployment)

Pastikan USB Debugging telah aktif di perangkat Android Anda dan perangkat terdeteksi dengan perintah `adb devices`.

- **Windows**:
  Jalankan file instalasi otomatis:
  ```cmd
  install-apk.bat
  ```
- **Linux / macOS**:
  Berikan izin eksekusi lalu jalankan:
  ```bash
  chmod +x install-apk.sh
  ./install-apk.sh
  ```

---

## 🗂️ Struktur Package Utama

```text
com.sonzaix.streaming
│
├── core
│   ├── database       # AppDatabase, DAOs, & Hilt DatabaseModule
│   ├── datastore      # SettingsDataStore untuk menyimpan preferensi pengguna
│   ├── network        # Retrofit Client, API Service, Connection Observer
│   └── utils          # Utility helper classes
│
├── data
│   ├── local          # Entity Database (FavoriteEntity, HistoryEntity)
│   ├── mapper         # Mapper JSON API ke objek Domain model
│   └── repository     # Implementasi antarmuka repositori data
│
├── domain
│   ├── model          # Representasi objek data utama (DramaItem, EpisodeItem, Provider)
│   ├── repository     # Definisi antarmuka repositori bisnis
│   └── usecase        # Kasus penggunaan khusus (GetDetailUseCase)
│
└── presentation
    ├── components     # Komponen UI Compose yang dapat digunakan kembali (DramaCard, ErrorView, dll)
    ├── navigation     # Konfigurasi Navigasi (Routes, AppNavGraph)
    ├── screens        # Seluruh halaman aplikasi (Home, Player, Favorite, Settings, dll)
    └── theme          # Skema Warna & Desain Tipografi (SonzaiXTheme)
```

---

## ⚠️ Ketentuan Hukum & Keamanan

- **Keabsahan Data**: Aplikasi ini beroperasi murni dengan data yang disediakan secara resmi oleh API.
- **Tanpa Bypass**: Tidak ada fitur bypass DRM, bypass premium, scraping ilegal, maupun pengunduhan konten tidak sah yang diimplementasikan di dalam aplikasi ini sesuai dengan batasan dan kepatuhan hukum yang berlaku.
