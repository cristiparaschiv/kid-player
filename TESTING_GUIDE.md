# Immersive Mode Testing Guide

Quick reference for testing the full-screen immersive mode implementation.

## Quick Build & Test

```bash
# Clean build
./gradlew clean

# Build and install debug APK
./gradlew installDebug

# Launch app
adb shell am start -n com.kidplayer.app/.presentation.MainActivity
```

## Test Scenarios

### Test 1: Basic Immersive Mode

**Steps:**
1. Launch Kid Player app
2. Navigate to Home screen
3. Tap any video thumbnail

**Expected Result:**
- Video starts playing
- Status bar disappears
- Navigation bar disappears
- Video fills entire screen
- Black background with video content

**Pass Criteria:**
- [ ] Status bar hidden
- [ ] Navigation bar hidden
- [ ] Video plays in full screen
- [ ] No UI clipping or overlap

---

### Test 2: System Bar Restoration

**Steps:**
1. Play any video (full-screen mode active)
2. Tap back button in player controls
3. Return to Home/Library screen

**Expected Result:**
- Player exits
- Status bar reappears at top
- Bottom navigation bar reappears
- Normal UI restored

**Pass Criteria:**
- [ ] Status bar visible after exit
- [ ] Bottom nav visible after exit
- [ ] No lingering immersive mode
- [ ] Smooth transition

---

### Test 3: Sticky Immersive Behavior

**Steps:**
1. Play any video (full-screen mode active)
2. Swipe down from top of screen
3. Wait ~3 seconds without touching screen

**Expected Result:**
- Status bar appears when you swipe
- Shows time, battery, notifications
- Status bar fades/hides after ~3 seconds
- Video continues playing

**Pass Criteria:**
- [ ] Status bar appears on swipe
- [ ] Status bar auto-hides after timeout
- [ ] Video playback unaffected
- [ ] Can repeat swipe gesture

---

### Test 4: Navigation Bar Swipe

**Steps:**
1. Play any video (full-screen mode active)
2. Swipe up from bottom of screen
3. Wait ~3 seconds without touching screen

**Expected Result:**
- Navigation bar appears when you swipe
- Shows back, home, recent apps buttons
- Navigation bar auto-hides after ~3 seconds
- Video continues playing

**Pass Criteria:**
- [ ] Nav bar appears on swipe
- [ ] Nav bar auto-hides after timeout
- [ ] Back button works when visible
- [ ] Video playback unaffected

---

### Test 5: Notification Interaction

**Steps:**
1. Play any video (full-screen mode active)
2. Send a test notification to the device
3. Swipe down from top to reveal status bar
4. Swipe down again to open notification shade
5. Tap notification or dismiss it
6. Return to video

**Expected Result:**
- Status bar appears on first swipe
- Notification shade opens on second swipe
- Notification is accessible
- Returning to video re-enters immersive mode

**Pass Criteria:**
- [ ] Notifications accessible
- [ ] Notification shade works
- [ ] Immersive mode restores after interaction
- [ ] Video continues playing

---

### Test 6: Volume Adjustment

**Steps:**
1. Play any video (full-screen mode active)
2. Swipe down from top to reveal status bar
3. Adjust volume using slider in quick settings
4. Return to video

**Expected Result:**
- Volume controls accessible via status bar
- Can adjust media volume
- Status bar auto-hides after adjustment
- Video playback unaffected

**Pass Criteria:**
- [ ] Volume controls accessible
- [ ] Volume adjustment works
- [ ] Status bar auto-hides
- [ ] Audio changes applied

---

### Test 7: Configuration Change (Rotation)

**Steps:**
1. Enable auto-rotate on device
2. Play any video (full-screen mode active)
3. Rotate device from landscape to portrait
4. Rotate back to landscape

**Expected Result:**
- Immersive mode maintained during rotation
- System bars stay hidden after rotation
- Video player adapts to new orientation
- No UI glitches

**Pass Criteria:**
- [ ] Immersive mode survives rotation
- [ ] System bars stay hidden
- [ ] Player controls adapt
- [ ] Smooth transition

---

### Test 8: App Backgrounding

**Steps:**
1. Play any video (full-screen mode active)
2. Press Home button to background app
3. Re-open Kid Player app

**Expected Result:**
- App goes to background (normal behavior)
- Re-opening returns to player screen
- Immersive mode re-applied
- Video paused/stopped per existing logic

**Pass Criteria:**
- [ ] App backgrounds correctly
- [ ] Immersive mode restores on return
- [ ] Player state preserved
- [ ] System bars hidden again

---

### Test 9: Multi-Screen Navigation

**Steps:**
1. Start on Home screen (normal UI)
2. Navigate to Search screen (normal UI)
3. Navigate to Favorites screen (normal UI)
4. Play a video from Favorites
5. Exit player back to Favorites

**Expected Result:**
- Home/Search/Favorites: Status bar visible
- Home/Search/Favorites: Bottom nav visible
- Player screen: Full immersive mode
- Return to Favorites: Normal UI restored

**Pass Criteria:**
- [ ] Non-player screens show system bars
- [ ] Player screen hides system bars
- [ ] Transitions smooth
- [ ] UI state correct on all screens

---

### Test 10: Autoplay Navigation

**Steps:**
1. Play a video with autoplay enabled
2. Wait for video to complete
3. Autoplay countdown begins
4. Next video starts automatically

**Expected Result:**
- First video plays in immersive mode
- Autoplay overlay appears (still immersive)
- Next video starts in immersive mode
- System bars stay hidden throughout

**Pass Criteria:**
- [ ] Immersive mode continuous
- [ ] Autoplay works correctly
- [ ] System bars stay hidden
- [ ] Smooth video transitions

---

## Device-Specific Testing

### Android 11 (API 30)
```bash
# Test on minimum supported version
adb -s <device_id> shell getprop ro.build.version.sdk
# Should output: 30
```

**Key Tests:**
- [ ] Immersive mode works
- [ ] WindowInsetsController API functions
- [ ] No crashes or warnings

---

### Android 12/12L (API 31/32)
```bash
# Test on Android 12
adb -s <device_id> shell getprop ro.build.version.sdk
# Should output: 31 or 32
```

**Key Tests:**
- [ ] Immersive mode works
- [ ] Splash screen compatibility
- [ ] Material You theming intact

---

### Android 13 (API 33)
```bash
# Test on Android 13
adb -s <device_id> shell getprop ro.build.version.sdk
# Should output: 33
```

**Key Tests:**
- [ ] Immersive mode works
- [ ] Notification permissions
- [ ] Per-app language settings

---

### Android 14 (API 34 - Target SDK)
```bash
# Test on target SDK version
adb -s <device_id> shell getprop ro.build.version.sdk
# Should output: 34
```

**Key Tests:**
- [ ] Immersive mode works
- [ ] Predictive back animations
- [ ] Material 3 components

---

## Tablet-Specific Testing

### Landscape Orientation (Primary)
App is configured for `userLandscape` orientation.

**Test:**
1. Launch app in landscape
2. Play video
3. Verify immersive mode

**Pass Criteria:**
- [ ] Full-screen in landscape
- [ ] System bars hidden
- [ ] Optimal viewing experience

---

### Different Screen Sizes

**Small Tablet (7-8 inches):**
- [ ] Immersive mode works
- [ ] Video fills screen
- [ ] Controls accessible

**Medium Tablet (9-10 inches):**
- [ ] Immersive mode works
- [ ] Video scales correctly
- [ ] UI proportions good

**Large Tablet (11+ inches):**
- [ ] Immersive mode works
- [ ] No letterboxing issues
- [ ] Controls well-positioned

---

## Debugging Commands

### Check System UI Visibility
```bash
# Dump window info to see system bar state
adb shell dumpsys window | grep -A 10 "systemUiVisibility"
```

### Monitor Logs for SystemUiController
```bash
# Filter logs for immersive mode events
adb logcat | grep -i "systemui\|insets\|immersive"
```

### Check Window Insets
```bash
# See current window inset values
adb shell dumpsys window displays | grep -A 20 "insets"
```

---

## Common Issues & Solutions

### Issue: System bars not hiding

**Debugging Steps:**
1. Check if PlayerScreen is using new code:
   ```bash
   grep -n "rememberSystemUiController" app/src/main/java/com/kidplayer/app/presentation/player/PlayerScreen.kt
   ```
2. Verify import present:
   ```bash
   grep -n "import com.kidplayer.app.presentation.util.rememberSystemUiController" app/src/main/java/com/kidplayer/app/presentation/player/PlayerScreen.kt
   ```
3. Check SystemUiController file exists:
   ```bash
   ls -la app/src/main/java/com/kidplayer/app/presentation/util/SystemUiController.kt
   ```

---

### Issue: System bars not restoring on exit

**Debugging Steps:**
1. Add logging to DisposableEffect:
   ```kotlin
   DisposableEffect(Unit) {
       Timber.d("PlayerScreen: Hiding system bars")
       systemUiController?.setSystemBarsVisible(visible = false, isSticky = true)
       onDispose {
           Timber.d("PlayerScreen: Restoring system bars")
           systemUiController?.setSystemBarsVisible(visible = true)
       }
   }
   ```
2. Monitor logs:
   ```bash
   adb logcat -s PlayerScreen
   ```
3. Verify navigation removes PlayerScreen from composition

---

### Issue: Compilation errors

**Solution:**
```bash
# Clean build
./gradlew clean

# Rebuild
./gradlew build

# Check for Kotlin version compatibility
./gradlew dependencies | grep kotlin
```

---

## Automated Testing (Future Enhancement)

### UI Test Example

```kotlin
@Test
fun playerScreen_hidesSystemBars() {
    // Launch app
    composeTestRule.setContent {
        PlayerScreen(
            onNavigateBack = {},
            onPlayNext = {}
        )
    }

    // Verify system bars are hidden
    // Note: Requires custom test rule to check window insets
    // Implementation depends on test setup
}
```

### Espresso Test Example

```kotlin
@Test
fun navigateToPlayer_systemBarsHidden() {
    // Navigate to player
    onView(withId(R.id.video_thumbnail))
        .perform(click())

    // Wait for player to load
    Thread.sleep(1000)

    // Check window decor view for insets
    // Custom assertion needed
}
```

---

## Performance Testing

### Frame Rate Monitoring

```bash
# Monitor frame rate during playback
adb shell dumpsys gfxinfo com.kidplayer.app
```

**Expected:**
- Consistent 60 fps (or device refresh rate)
- No dropped frames during immersive mode transitions

---

### Memory Usage

```bash
# Monitor memory during playback
adb shell dumpsys meminfo com.kidplayer.app
```

**Expected:**
- No memory leaks from system UI controller
- Stable memory usage during video playback

---

## Test Report Template

```markdown
## Immersive Mode Test Report

**Date:** YYYY-MM-DD
**Tester:** [Name]
**Device:** [Model]
**Android Version:** [API Level]

### Test Results

| Test # | Test Name | Status | Notes |
|--------|-----------|--------|-------|
| 1 | Basic Immersive Mode | ✅ PASS | - |
| 2 | System Bar Restoration | ✅ PASS | - |
| 3 | Sticky Immersive Behavior | ✅ PASS | - |
| 4 | Navigation Bar Swipe | ✅ PASS | - |
| 5 | Notification Interaction | ✅ PASS | - |
| 6 | Volume Adjustment | ✅ PASS | - |
| 7 | Configuration Change | ✅ PASS | - |
| 8 | App Backgrounding | ✅ PASS | - |
| 9 | Multi-Screen Navigation | ✅ PASS | - |
| 10 | Autoplay Navigation | ✅ PASS | - |

### Issues Found

[List any issues encountered]

### Recommendations

[Any suggestions for improvements]
```

---

## Quick Checklist

Before marking implementation as complete, verify:

- [ ] SystemUiController.kt exists and compiles
- [ ] PlayerScreen.kt uses rememberSystemUiController()
- [ ] App builds without errors
- [ ] System bars hide when entering player
- [ ] System bars restore when exiting player
- [ ] Sticky immersive mode works (swipe to reveal)
- [ ] Other screens show normal UI
- [ ] No crashes or ANRs
- [ ] Works on multiple Android versions (11-14)
- [ ] Works on different tablet sizes
- [ ] Documentation complete

---

**Testing Guide Version:** 1.0
**Last Updated:** December 15, 2025
