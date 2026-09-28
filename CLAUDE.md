# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK (dev flavor)
./gradlew assembleDevDebug

# Build release APK (product flavor)
./gradlew assembleProductRelease

# Build all variants
./gradlew assemble

# Run unit tests
./gradlew testDevDebugUnitTest

# Run instrumented tests (requires emulator/device)
./gradlew connectedDevDebugAndroidTest

# Clean build
./gradlew clean

# Sync Gradle and check dependencies
./gradlew dependencies
```

## Project Structure

Multi-module Android project with two modules:
- **app**: Main application module (namespace: `com.azg.alf_002_qrscan`)
- **qr**: Custom QR code generation library (namespace: `com.create.custom.qr`)

### Product Flavors
- `dev`: Debug builds with `build_debug = true`
- `product`: Production builds with `build_debug = false`

## Architecture

**MVVM with Jetpack Compose**:
- ViewModels use `StateFlow` for reactive UI state
- Koin for dependency injection (modules defined in `app/IModule.kt`)
- Room database with `AppRepository` pattern

**Data Flow**:
```
Compose UI → ViewModel (StateFlow) → Repository → Room DAO → Entity
```

### Key Directories

| Path | Purpose |
|------|---------|
| `app/.../app/` | Application class, DI modules, preferences |
| `app/.../base/` | Base classes (BaseActivity, BaseDialog) |
| `app/.../database/` | Room database, DAO, entities |
| `app/.../model/` | Data models for QR content types |
| `app/.../ui/` | Compose screens organized by feature |
| `app/.../viewmodel/` | ViewModels for MVVM |
| `app/.../style/` | Theme, colors, extensions |
| `app/.../utils/` | QR generation/parsing utilities |
| `qr/.../vector/` | Custom QR drawable rendering with DSL |

## Key Technologies

- **Kotlin 2.2.20** with Compose compiler
- **Jetpack Compose** (Material3) for UI
- **Room 2.8.0** for local database
- **Koin 4.1.1** for dependency injection
- **ML Kit Barcode Scanning** + **ZXing** for QR scanning/generation
- **Firebase** (Analytics, Remote Config, FCM)
- **Coil 3** for image loading
- **Navigation Compose** for screen navigation

## Database

Main entity is `HistoryApp` with fields:
- `hisId`, `scanTime`, `scanType`, `content`, `scanImage`
- `type` (QrType enum), `qrName`, `createType` (CREATE/SCAN)
- `isFavorite`, `isDelete` (soft delete)

Room schema is generated to `app/schemas/` via KSP.

## Theme System

Four themes available via `ColorTheme` enum: BLUE, PINK, GREEN, DENIM. Theme preference stored in `PreferenceData` (Koin singleton with SharedPreferences).

## QR Library Module

The `qr` module provides advanced QR customization:
- Custom pixel/ball/frame shapes
- Gradient colors
- Logo embedding
- Kotlin DSL for configuration (see `qr/.../vector/dsl/`)

Usage: `QrCodeDrawable` with `QrVectorOptions` builder.
