# Kid Player

A kid-friendly Android tablet application for streaming media content from Jellyfin servers. Designed specifically for children ages 4-10, featuring large touch targets, simple navigation, and comprehensive parental controls.

## Features

### Kid-Friendly Interface
  - Large, colorful buttons optimized for small hands (56-64dp minimum touch targets)
  - Simple grid-based navigation with 3-column layout on tablets
  - Full-screen immersive video playback
  - Haptic feedback on interactions
  - Auto-hiding player controls

### Video Playback
  - Stream videos from Jellyfin media server
  - Offline playback support for downloaded content
  - Video recommendation row in player for easy discovery
  - Autoplay with countdown for continuous viewing
  - Seek controls (+/- 10 seconds)
  - Screen stays awake during playback

### Parental Controls
  - PIN-protected settings access
  - **Screen Time Limits**: Set daily viewing limits (enforced with full-screen blocking)
  - **Access Schedule**: Define allowed viewing hours
  - Time limit extensions via parent PIN (+15/30/60 minutes)
  - Automatic daily reset of screen time

### Content Organization
  - Browse by library (Movies, TV Shows, etc.)
  - Search functionality
  - Favorites collection
  - Download manager for offline viewing
  - Series/episode organization with smart recommendations

## Tech Stack

### Core
  - **Language**: Kotlin
  - **Min SDK**: 30 (Android 11)
  - **Target SDK**: 34 (Android 14)
  - **Architecture**: MVVM with Clean Architecture

### UI
  - **Jetpack Compose** - Modern declarative UI toolkit
  - **Material Design 3** - Latest Material You theming
  - **Compose Navigation** - Type-safe navigation
  - **Coil** - Image loading and caching

### Dependency Injection
  - **Hilt** - Compile-time dependency injection

### Networking
  - **Retrofit** - REST API client
  - **OkHttp** - HTTP client with logging
  - **Kotlinx Serialization** - JSON parsing

### Local Storage
  - **Room** - SQLite database with Kotlin coroutines support
  - **DataStore** - Preferences storage
  - **EncryptedSharedPreferences** - Secure storage for sensitive data (PIN, credentials)

### Media Playback
  - **Media3 ExoPlayer** - Video playback engine
  - **Media3 UI** - Player UI components

### Async Operations
  - **Kotlin Coroutines** - Asynchronous programming
  - **Kotlin Flow** - Reactive data streams

### Logging
  - **Timber** - Logging utility

## Setup

### Prerequisites
  - Android Studio Hedgehog (2023.1.1) or newer
  - JDK 17
  - Android device or emulator running Android 11+

### Building
  ```bash
  # Debug build
  ./gradlew assembleDebug

  # Release build
  ./gradlew assembleRelease

  # Run tests
  ./gradlew test
```

## Configuration

  1. Install the app on your tablet
  2. On first launch, enter your Jellyfin server URL
  3. Log in with your Jellyfin credentials
  4. Set up a parent PIN for parental controls
  5. Configure screen time limits and access schedules as needed

## Jellyfin Server Requirements

  - Jellyfin Server 10.8.0 or newer
  - Network access from the tablet to the server
  - User account with appropriate library permissions
  - (Optional) HTTPS for secure connections

## Security Features

  - Parent PIN stored with Android EncryptedSharedPreferences
  - Server credentials encrypted at rest
  - Screen time enforcement cannot be bypassed by children
  - Navigation guards prevent access to videos when time limit reached

## Acknowledgments

  - https://jellyfin.org/ - The free software media system
  - https://exoplayer.dev/ - Application level media player for Android
  - https://m3.material.io/ - Google's open-source design system
