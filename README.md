# Kid Player

[![Google Play](https://img.shields.io/badge/Google%20Play-Download-green?style=for-the-badge&logo=google-play)](https://play.google.com/store/apps/details?id=com.kidplayer.app)
[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-30-orange?style=for-the-badge)](https://developer.android.com/about/versions/11)

A kid-friendly Android tablet application for streaming media content from Jellyfin servers. Designed specifically for children ages 4-10, featuring large touch targets, simple navigation, and comprehensive parental controls.

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.kidplayer.app">
    <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="80"/>
  </a>
</p>

---

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
- Automatic pause when screen turns off

### Parental Controls
- PIN-protected settings access
- **Screen Time Limits**: Set daily viewing limits (enforced with full-screen blocking)
- **Access Schedule**: Define allowed viewing hours
- Time limit extensions via parent PIN (+15/30/60 minutes)
- Automatic daily reset of screen time
- PIN-protected download deletion

### Content Organization
- Browse by library (Movies, TV Shows, etc.)
- Playlist support with easy navigation
- Search functionality
- Favorites collection
- Download manager for offline viewing
- Series/episode organization with smart recommendations

---

## Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin |
| **Min SDK** | 30 (Android 11) |
| **Target SDK** | 35 (Android 15) |
| **Architecture** | MVVM with Clean Architecture |
| **UI Framework** | Jetpack Compose |
| **Design System** | Material Design 3 |
| **DI** | Hilt |
| **Networking** | Retrofit + OkHttp |
| **Database** | Room |
| **Media Player** | Media3 ExoPlayer |
| **Async** | Kotlin Coroutines + Flow |

---

## Setup

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android device or emulator running Android 11+

### Building from Source

\`\`\`bash
# Clone the repository
git clone https://github.com/yourusername/kid-player.git
cd kid-player

# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
\`\`\`

---

## Configuration

1. Install the app on your tablet
2. On first launch, enter your Jellyfin server URL
3. Log in with your Jellyfin credentials
4. Set up a parent PIN for parental controls
5. Configure screen time limits and access schedules as needed

---

## Jellyfin Server Requirements

- Jellyfin Server 10.8.0 or newer
- Network access from the tablet to the server
- User account with appropriate library permissions
- (Optional) HTTPS for secure connections

---

## Security Features

- Parent PIN stored with Android EncryptedSharedPreferences
- Server credentials encrypted at rest
- Screen time enforcement cannot be bypassed by children
- Navigation guards prevent access to videos when time limit reached

---

## Acknowledgments

- [Jellyfin](https://jellyfin.org/) - The free software media system
- [ExoPlayer](https://exoplayer.dev/) - Application level media player for Android
- [Material Design 3](https://m3.material.io/) - Google's open-source design system

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<p align="center">
  Made with ❤️ for kids and parents
</p>
