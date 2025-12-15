# Software Requirements Specification (SRS)
# Kid Player - Android Tablet Application

**Version:** 1.0
**Date:** December 11, 2025
**Project:** Kid Player Android App
**Author:** Business Analyst
**Status:** Draft for Review

---

## Document Control

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2025-12-11 | Business Analyst | Initial SRS document |

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Problem Statement & Business Objectives](#2-problem-statement--business-objectives)
3. [Stakeholder Analysis](#3-stakeholder-analysis)
4. [Scope](#4-scope)
5. [Functional Requirements](#5-functional-requirements)
6. [Non-Functional Requirements](#6-non-functional-requirements)
7. [User Interface Requirements](#7-user-interface-requirements)
8. [Technical Requirements & Constraints](#8-technical-requirements--constraints)
9. [Data Requirements](#9-data-requirements)
10. [Security & Parental Control Requirements](#10-security--parental-control-requirements)
11. [Assumptions and Dependencies](#11-assumptions-and-dependencies)
12. [Risks and Mitigation Strategies](#12-risks-and-mitigation-strategies)
13. [Success Criteria & KPIs](#13-success-criteria--kpis)
14. [Future Considerations](#14-future-considerations)

---

## 1. Executive Summary

### 1.1 Project Overview

Kid Player is a custom Android tablet application designed to provide a safe, controlled, and child-friendly video consumption experience for a 5-year-old user. The application addresses the limitations of commercial platforms like YouTube Kids by connecting to a parent-curated Jellyfin media server, ensuring complete control over content quality and appropriateness.

The application combines a simplified, colorful user interface optimized for young children with robust parental controls, offline playback capabilities, and automatic content synchronization. This solution empowers parents to provide educational and entertaining content without exposure to algorithmic recommendations or inappropriate material.

### 1.2 Key Features

- **Kid-Optimized UI**: Large thumbnails, simple navigation, minimal text, bright colors
- **Jellyfin Integration**: Direct connection to parent's existing media server
- **Offline-First Design**: Automatic background downloads of 1-2 hours of content
- **Intelligent Playback**: Seamless transition between online and offline modes with autoplay
- **Parental Controls**: Screen time management, content curation, and settings protection
- **Zero Ads**: Clean, distraction-free viewing experience

### 1.3 Target Users

- **Primary User**: 5-year-old child (non-reader, limited motor skills, short attention span)
- **Secondary User**: Parent/guardian (administrator, content curator, monitor)

---

## 2. Problem Statement & Business Objectives

### 2.1 Problem Statement

**Current State:**
Parents utilizing commercial platforms like YouTube Kids face significant challenges despite age-restriction settings:
- Inconsistent content quality and appropriateness
- Algorithm-driven recommendations that may surface unsuitable content
- Advertising and in-app purchase prompts
- Lack of granular control over viewable content
- Dependency on internet connectivity for all viewing
- Interface complexity beyond young children's cognitive abilities

**Desired State:**
A completely parent-controlled viewing environment where:
- Every piece of content is pre-approved and curated by the parent
- The child can independently browse and watch content without supervision concerns
- Viewing is possible offline during travel, at bedtime, or in areas with poor connectivity
- The interface is intuitive enough for a 5-year-old to operate without assistance
- Screen time can be managed according to family rules

### 2.2 Business Objectives

| Objective | Description | Success Metric |
|-----------|-------------|----------------|
| **Content Safety** | Ensure 100% of viewable content is parent-approved | Zero instances of unapproved content access |
| **Usability for Young Child** | Enable independent operation by 5-year-old | Child can select and play content without adult help in 95%+ of sessions |
| **Offline Reliability** | Provide seamless offline viewing experience | Offline playback works 100% of time when downloads complete |
| **Parental Control** | Maintain complete administrative control | All settings changes require parental authentication |
| **Reduced Supervision Burden** | Allow safe unsupervised viewing | Parent confidence level: "comfortable leaving child alone with app" |

### 2.3 Value Proposition

- **For Parent**: Peace of mind through complete content control, reduced supervision burden, portable entertainment for travel
- **For Child**: Fun, easy-to-use interface with familiar content, consistent availability of favorites

---

## 3. Stakeholder Analysis

### 3.1 Primary Stakeholders

#### Parent/Guardian (Administrator)
- **Role**: Content curator, settings manager, primary decision-maker
- **Goals**:
  - Ensure child safety and age-appropriate content
  - Manage screen time effectively
  - Minimize setup and maintenance effort
  - Provide offline entertainment for travel/bedtime
- **Pain Points**:
  - Current platforms lack sufficient control
  - Concerned about inappropriate content exposure
  - Need reliable offline capability
- **Technical Proficiency**: Moderate (able to run Jellyfin server, configure network access)
- **Decision Authority**: Full authority over app configuration and usage

#### Child User (5 years old)
- **Role**: Primary end-user, content consumer
- **Characteristics**:
  - Pre-reading or early reading stage
  - Limited fine motor control (requires large touch targets)
  - Short attention span (3-15 minutes per video)
  - Recognizes visual patterns and familiar characters
  - Minimal technical understanding
- **Goals**:
  - Watch favorite shows and videos easily
  - Find new interesting content
  - Operate app independently
- **Pain Points**:
  - Complex interfaces are frustrating
  - Cannot read navigation labels
  - Easily confused by too many options
- **Technical Proficiency**: Minimal (can tap large buttons, swipe, basic gestures)

### 3.2 Secondary Stakeholders

#### Jellyfin Server
- **Role**: Content source and metadata provider
- **Requirements From System**:
  - API access for content listing, streaming, and download
  - User authentication
  - Metadata retrieval (thumbnails, titles, descriptions)
- **Constraints**: Must be accessible from outside local network

#### Android Tablet Device
- **Role**: Deployment platform
- **Requirements**:
  - Sufficient storage for offline content (minimum 8GB free space recommended)
  - Android OS (version to be specified in technical requirements)
  - Adequate processing power for video playback

---

## 4. Scope

### 4.1 In-Scope

#### Phase 1 - Core Functionality (MVP)
1. **Content Discovery & Playback**
   - Connect to Jellyfin media server via API
   - Browse video content with thumbnail grid view
   - Play videos in full-screen mode
   - Autoplay next video in queue/playlist
   - Basic playback controls (play/pause, skip)

2. **Offline Capabilities**
   - Automatic background download of 1-2 hours of content
   - Offline content library view
   - Automatic fallback to offline mode when network unavailable
   - Storage management (automatic cleanup of watched content when storage low)

3. **Parental Controls**
   - PIN-protected settings area
   - Screen time timer with configurable duration
   - App lock when screen time expires
   - Manual content sync trigger

4. **Kid-Friendly UI**
   - Large thumbnail grid (2x3 or 3x4 layout)
   - Bright, colorful design similar to YouTube Kids aesthetic
   - Icon-based navigation (minimal text)
   - Simple, tap-friendly controls

5. **Network Intelligence**
   - Automatic detection of network connectivity changes
   - Seamless transition between online and offline playback
   - Background sync when on WiFi and charging

### 4.2 Out-of-Scope

The following features are explicitly excluded from the initial release:

1. **Content Creation/Upload**
   - No video recording or upload functionality
   - No user-generated content

2. **Social Features**
   - No sharing capabilities
   - No comments or ratings
   - No multi-user profiles (single child profile only)

3. **Advanced Playback Features**
   - No video quality selection (automatic adaptation only)
   - No subtitle/caption support (may be future consideration)
   - No playback speed controls
   - No picture-in-picture mode

4. **Advanced Parental Features**
   - No remote monitoring/control from parent's phone (may be future consideration)
   - No viewing history analytics/reports (basic history only)
   - No content filtering by category beyond Jellyfin library structure

5. **Cross-Platform Support**
   - Android tablets only (no phone, iOS, or web versions)

6. **Advanced Download Management**
   - No manual video selection for download (automatic only)
   - No download scheduling (downloads when WiFi + charging)

7. **Jellyfin Server Management**
   - No server configuration from app
   - No library creation or content upload to Jellyfin

### 4.3 Future Considerations (Post-MVP)

Features that may be considered for future releases:
- Multiple child profiles with separate content libraries
- Remote parental monitoring via companion mobile app
- Educational content tracking and progress reporting
- Subtitle/closed caption support
- Casting to TV/Chromecast
- Reward system for limiting screen time
- Manual download selection by parent
- Gesture-based child lock bypass for emergencies

---

## 5. Functional Requirements

### 5.1 Content Discovery & Browsing

#### FR-1.1: Jellyfin Server Connection
**As a** parent
**I want** to connect the app to my Jellyfin media server
**So that** my child can access our curated content library

**Acceptance Criteria:**
```gherkin
Given I am in the parental settings area
When I enter the Jellyfin server URL, username, and password
And I tap "Connect"
Then the app should validate the connection
And display a success message if connection is established
And save the credentials securely for future use
And retrieve the available content libraries

Given the Jellyfin server is unreachable
When I attempt to connect
Then the app should display a clear error message
And suggest troubleshooting steps (check URL, network connection, server status)
```

**Priority**: MUST HAVE
**Dependencies**: Jellyfin API documentation, secure credential storage mechanism

---

#### FR-1.2: Content Library Selection
**As a** parent
**I want** to select which Jellyfin library/libraries are accessible to my child
**So that** I can ensure only age-appropriate content is visible

**Acceptance Criteria:**
```gherkin
Given I have successfully connected to Jellyfin
When I view the library selection screen
Then I should see all available libraries on my Jellyfin server
And I should be able to enable/disable each library for child access
And I should be able to save my selection

Given I have selected one or more libraries
When my child opens the app
Then they should only see content from the enabled libraries
```

**Priority**: MUST HAVE
**Dependencies**: FR-1.1

---

#### FR-1.3: Thumbnail Grid View
**As a** child user
**I want** to see videos displayed as large, colorful thumbnails
**So that** I can easily find and choose what to watch

**Acceptance Criteria:**
```gherkin
Given I have opened the app
When I view the home screen
Then I should see videos displayed in a grid of large thumbnails
And each thumbnail should show the video cover image
And thumbnails should be sized for easy tapping (minimum 150x150dp)
And the grid should scroll vertically
And there should be 2-3 columns depending on tablet orientation

Given content has official artwork/thumbnails from Jellyfin
When displaying content
Then the app should use the highest resolution thumbnail available
And thumbnails should load progressively (placeholder -> low res -> high res)
```

**Priority**: MUST HAVE
**Dependencies**: FR-1.2, UI framework selection

---

#### FR-1.4: Content Metadata Display
**As a** child user
**I want** to see the title of each video
**So that** I can recognize my favorite shows (with parent help if needed)

**Acceptance Criteria:**
```gherkin
Given I am viewing the thumbnail grid
When I look at each video tile
Then I should see the video title displayed below or on the thumbnail
And the title should be in large, easy-to-read font (minimum 16sp)
And titles should be truncated elegantly if too long
And the title should not obscure important parts of the thumbnail

Given I tap on a thumbnail
When the video detail overlay appears (if implemented)
Then I should see the full title
And optionally a brief description (if appropriate for 5-year-old)
```

**Priority**: SHOULD HAVE
**Dependencies**: FR-1.3
**Note**: Consider that 5-year-olds may have limited reading ability; visual recognition is primary

---

#### FR-1.5: Content Filtering and Sorting
**As a** parent
**I want** content to be organized in a simple, child-friendly manner
**So that** my child can find content easily without being overwhelmed

**Acceptance Criteria:**
```gherkin
Given content exists in multiple categories/collections in Jellyfin
When the child views the home screen
Then content should be organized by category (e.g., "Favorite Shows", "New Videos")
And each category should be horizontally scrollable
Or organized in separate tabs with simple icons

Given there are many videos in a category
When displaying videos
Then the default sort order should be: recently added, or parent-specified order
And pagination should load smoothly as child scrolls
```

**Priority**: SHOULD HAVE
**Dependencies**: FR-1.3, Jellyfin metadata structure

---

### 5.2 Video Playback

#### FR-2.1: Full-Screen Video Playback
**As a** child user
**I want** to watch videos in full-screen mode
**So that** I can focus on the content without distractions

**Acceptance Criteria:**
```gherkin
Given I have selected a video from the grid
When the video starts playing
Then it should play in full-screen mode
And the device orientation should lock to landscape if video is landscape
And system UI elements (status bar, navigation bar) should be hidden
And playback should begin automatically within 2 seconds

Given a video is playing
When playback encounters an error
Then the app should display a child-friendly error message ("Oops! Let's try another video")
And automatically attempt to play the next video in queue
Or return to the browsing screen
```

**Priority**: MUST HAVE
**Dependencies**: FR-1.3, video player library selection

---

#### FR-2.2: Autoplay Next Video
**As a** child user
**I want** the next video to start automatically when one finishes
**So that** I can continue watching without interruption

**Acceptance Criteria:**
```gherkin
Given a video has finished playing
When the video reaches the end
Then the app should automatically begin playing the next video within 3 seconds
And display a brief preview/thumbnail of the next video (5 seconds before current ends)
And provide a countdown timer before auto-advancing

Given there are no more videos in the current queue/playlist
When the current video ends
Then the app should return to the beginning of the playlist
Or return to the home screen browsing view (parent-configurable)

Given autoplay is about to start the next video
When a parent taps the screen
Then they should be able to cancel autoplay
And return to browsing
```

**Priority**: MUST HAVE
**Dependencies**: FR-2.1
**Note**: Autoplay is explicitly required by parent

---

#### FR-2.3: Basic Playback Controls
**As a** child user
**I want** simple controls to pause and skip videos
**So that** I can control my viewing experience

**Acceptance Criteria:**
```gherkin
Given a video is playing
When I tap the center of the screen
Then playback should pause
And a large, simple "Play" button should appear

Given playback is paused
When I tap the play button
Then playback should resume

Given a video is playing
When I perform a swipe gesture or tap skip button
Then the current video should stop
And the next video in queue should begin playing immediately

Given playback controls are visible
When no interaction occurs for 5 seconds
Then controls should fade out automatically
```

**Priority**: MUST HAVE
**Dependencies**: FR-2.1

---

#### FR-2.4: Exit Playback
**As a** child user
**I want** to easily go back to choosing videos
**So that** I can find something else to watch

**Acceptance Criteria:**
```gherkin
Given a video is playing
When I tap a visible "Home" or "Back" button in the corner
Then playback should stop
And I should return to the thumbnail grid view

Given I accidentally tap the back button
When returning to grid view
Then my previous browsing position should be maintained
And I should be able to easily resume the video I was watching
```

**Priority**: MUST HAVE
**Dependencies**: FR-2.1, FR-1.3

---

### 5.3 Offline Capabilities

#### FR-3.1: Automatic Content Download
**As a** parent
**I want** the app to automatically download 1-2 hours of content in the background
**So that** my child can watch videos offline during travel or without internet

**Acceptance Criteria:**
```gherkin
Given the tablet is connected to WiFi
And the tablet is charging (or battery > 30%)
And storage space is sufficient (> 5GB free)
When the app is idle or in background
Then the app should automatically download unwatched videos
And download approximately 60-120 minutes of content total
And prioritize recently added content or parent-specified priority

Given downloads are in progress
When WiFi connection is lost
Then downloads should pause
And resume automatically when WiFi reconnects

Given the download queue is complete
When new content is added to Jellyfin server
Then the app should detect new content within 24 hours
And add new videos to download queue
And remove oldest watched videos to make space if needed

Given downloads are occurring
When the app is actively being used
Then downloads should throttle to prevent interference with streaming
And prioritize playback quality over download speed
```

**Priority**: MUST HAVE
**Dependencies**: FR-1.1, background task framework, storage management system

---

#### FR-3.2: Offline Content Library View
**As a** child user
**I want** to see which videos are available offline
**So that** I know what I can watch without internet

**Acceptance Criteria:**
```gherkin
Given I am viewing the thumbnail grid
When some videos are downloaded and some are not
Then downloaded videos should have a small visual indicator (e.g., download icon badge)
And the indicator should not obscure the thumbnail
And indicators should use simple, recognizable iconography

Given the device has no internet connection
When I open the app
Then only downloaded videos should be displayed
And a simple banner should indicate "Watching offline" (with icon)
And attempting to play non-downloaded content should show a friendly message
```

**Priority**: MUST HAVE
**Dependencies**: FR-1.3, FR-3.1

---

#### FR-3.3: Automatic Offline Mode Detection
**As a** child user
**I want** the app to automatically switch to offline mode when internet is unavailable
**So that** I can continue watching without interruption

**Acceptance Criteria:**
```gherkin
Given I am using the app with internet connection
When internet connection is lost
Then the app should automatically switch to offline mode within 5 seconds
And display only downloaded content
And show a simple offline indicator

Given a video is streaming from Jellyfin server
When internet connection is lost during playback
Then the app should attempt to continue playback from local cache if available
And if the video is downloaded, seamlessly switch to offline playback
And if not downloaded, display a friendly error and suggest downloaded videos

Given I am in offline mode
When internet connection is restored
Then the app should automatically switch back to online mode
And refresh content library in background
And resume download queue if needed
```

**Priority**: MUST HAVE
**Dependencies**: FR-2.1, FR-3.1, network connectivity monitoring

---

#### FR-3.4: Storage Management
**As a** parent
**I want** the app to intelligently manage storage space
**So that** the tablet doesn't run out of space

**Acceptance Criteria:**
```gherkin
Given available storage space is running low (< 2GB free)
When the app checks storage
Then it should automatically delete the oldest watched videos
And maintain at least 1GB of free space buffer
And never delete unwatched downloaded videos automatically

Given I am in parental settings
When I view storage management
Then I should see total space used by app
And I should see number of downloaded videos
And I should be able to manually clear all downloads
And I should be able to set maximum storage limit (e.g., 5GB, 10GB, 20GB)

Given storage limit is reached
When new downloads are queued
Then oldest watched content should be deleted first
And if no watched content exists, downloads should queue but not execute
And parent should receive notification when downloads are blocked by storage
```

**Priority**: MUST HAVE
**Dependencies**: FR-3.1, Android storage APIs

---

#### FR-3.5: Manual Sync Trigger
**As a** parent
**I want** to manually trigger a content sync
**So that** I can ensure latest content is downloaded before travel

**Acceptance Criteria:**
```gherkin
Given I am in parental settings
When I tap "Sync Now" button
Then the app should immediately connect to Jellyfin server
And refresh content library
And begin downloading new unwatched content
And display sync progress

Given a manual sync is in progress
When I exit settings
Then sync should continue in background
And I should be able to monitor progress via notification

Given manual sync completes
When I return to settings
Then I should see timestamp of last successful sync
And summary of new content downloaded
```

**Priority**: SHOULD HAVE
**Dependencies**: FR-3.1, parental controls

---

### 5.4 Parental Controls

#### FR-4.1: PIN-Protected Settings Access
**As a** parent
**I want** all settings and configurations to be protected by a PIN
**So that** my child cannot modify app behavior or access inappropriate controls

**Acceptance Criteria:**
```gherkin
Given I am setting up the app for the first time
When I reach the PIN creation screen
Then I should be required to create a 4-6 digit PIN
And confirm the PIN by entering it twice
And the PIN should be stored securely (hashed, not plaintext)

Given settings access is PIN-protected
When I attempt to access settings from the app
Then I should be prompted to enter my PIN
And I should have 3 attempts before being locked out for 5 minutes
And incorrect attempts should be logged

Given a child attempts to access settings
When they cannot enter correct PIN
Then they should be unable to access any configuration
And should be returned to the main browsing/playback screen

Given I have forgotten my PIN
When I need to reset it
Then I should be able to answer security questions or use recovery method
Or reset app data (losing preferences but not affecting Jellyfin library)
```

**Priority**: MUST HAVE
**Dependencies**: Secure storage framework, Android keystore

---

#### FR-4.2: Screen Time Timer
**As a** parent
**I want** to set a screen time limit that locks the app when time expires
**So that** I can enforce healthy viewing habits

**Acceptance Criteria:**
```gherkin
Given I am in parental settings
When I configure screen time timer
Then I should be able to set duration in 15-minute increments (15min - 120min)
And I should be able to enable/disable the timer
And I should be able to set whether timer resets daily or requires manual reset

Given screen time timer is enabled and set to 60 minutes
When my child has watched videos for 60 minutes
Then playback should pause
And a child-friendly message should appear ("All done! Time to play something else!")
And the app should require PIN entry to continue watching

Given screen time has expired
When I enter my PIN
Then I should be able to:
  - Grant 15 more minutes
  - Reset timer for today
  - Disable timer temporarily

Given the timer is running
When the app is closed and reopened
Then the timer should persist and continue counting
And time should accumulate across sessions within the reset period
```

**Priority**: SHOULD HAVE (marked as optional by parent, but highly recommended)
**Dependencies**: FR-4.1, persistent timer state storage

---

#### FR-4.3: Viewing History
**As a** parent
**I want** to view what my child has watched
**So that** I can monitor their content consumption

**Acceptance Criteria:**
```gherkin
Given I am in parental settings
When I access viewing history
Then I should see a chronological list of watched videos
And each entry should show: video title, date/time watched, duration watched
And history should retain last 30 days of activity

Given I am reviewing viewing history
When I want to remove a video from history
Then I should be able to delete individual entries
And I should be able to clear all history

Given my child watches a video
When playback completes or > 90% of video is watched
Then it should be logged in viewing history
And marked as "watched" for download management purposes
```

**Priority**: SHOULD HAVE
**Dependencies**: FR-4.1, local database for history storage

---

#### FR-4.4: App Exit Protection
**As a** parent
**I want** to prevent accidental app exits
**So that** my child stays within the safe environment

**Acceptance Criteria:**
```gherkin
Given my child is using the app
When they press the home button or attempt to switch apps
Then the Android system behavior should work normally (this is not a kiosk mode app)

Given I want to enable app pinning (Android feature)
When I set up the app
Then I should receive instructions on how to use Android's screen pinning
To prevent child from exiting to other apps
(Note: This is an Android OS feature, not app-specific functionality)
```

**Priority**: COULD HAVE
**Dependencies**: None (relies on Android OS screen pinning feature)
**Note**: True kiosk mode requires device admin permissions and is complex; recommend using Android's built-in screen pinning instead

---

### 5.5 Error Handling & Edge Cases

#### FR-5.1: Network Error Handling
**As a** child user
**I want** to see friendly error messages when something goes wrong
**So that** I'm not confused or scared by technical errors

**Acceptance Criteria:**
```gherkin
Given a network error occurs during streaming
When playback fails
Then I should see a simple, friendly message like "Oops! Let's try another video"
And the message should include a simple graphic (sad cloud, etc.)
And automatically attempt next video after 3 seconds
Or provide a large "Try Again" button

Given the Jellyfin server is unreachable
When the app attempts to refresh content
Then the app should fail gracefully to offline mode
And display an offline indicator
And not interrupt current playback if a video is playing

Given network errors occur repeatedly
When 3 consecutive playback failures happen
Then the app should automatically switch to offline-only mode
And display a banner suggesting parent check internet connection
```

**Priority**: MUST HAVE
**Dependencies**: All playback features

---

#### FR-5.2: Startup Behavior
**As a** child user
**I want** the app to start quickly and show me content immediately
**So that** I can start watching without waiting

**Acceptance Criteria:**
```gherkin
Given I am opening the app for the first time after installation
When the app launches
Then I should see a simple splash screen (< 2 seconds)
And be guided through initial setup by parent

Given the app is already configured
When I open the app
Then I should see the content grid within 3 seconds
And the last browsing position should be restored
And downloaded content should be available immediately

Given the app is opened without internet connection
When launching
Then the app should immediately show offline content
And not hang or show loading spinners indefinitely
```

**Priority**: MUST HAVE
**Dependencies**: App architecture, caching strategy

---

#### FR-5.3: Low Storage Warnings
**As a** parent
**I want** to be notified when storage is running low
**So that** I can manage space before downloads fail

**Acceptance Criteria:**
```gherkin
Given available storage drops below 3GB
When the app checks storage (daily check)
Then I should receive a notification (when parent unlocks tablet)
And notification should indicate current usage and recommend clearing space

Given storage is critically low (< 1GB)
When downloads attempt to proceed
Then downloads should be paused
And parent should receive urgent notification
And parental settings should show storage warning banner
```

**Priority**: SHOULD HAVE
**Dependencies**: FR-3.4, Android notification system

---

## 6. Non-Functional Requirements

### 6.1 Performance Requirements

#### NFR-1.1: Application Responsiveness
- **Requirement**: The application must respond to user interactions within 300ms for all touch events
- **Rationale**: Young children have low tolerance for lag; immediate feedback is critical for usability
- **Measurement**: 95th percentile touch-to-response time < 300ms under normal conditions
- **Priority**: MUST HAVE

#### NFR-1.2: Video Playback Performance
- **Requirement**: Video playback must start within 2 seconds of selection (streaming) or 1 second (offline)
- **Rationale**: Long buffering times frustrate young users and lead to repeated tapping
- **Measurement**: Time from video selection to first frame displayed
- **Priority**: MUST HAVE

#### NFR-1.3: Thumbnail Loading Performance
- **Requirement**: Thumbnail grid must render visible thumbnails within 2 seconds of screen display
- **Rationale**: Visual browsing is primary navigation method; fast thumbnail loading essential
- **Measurement**: Time to render all above-the-fold thumbnails
- **Priority**: MUST HAVE

#### NFR-1.4: Background Download Efficiency
- **Requirement**: Background downloads must not impact foreground playback quality
- **Rationale**: Child should be able to watch while downloads occur
- **Measurement**: No dropped frames or buffering during simultaneous playback and download
- **Priority**: MUST HAVE

#### NFR-1.5: Battery Consumption
- **Requirement**: Application should consume < 15% battery per hour during active video playback
- **Rationale**: Tablet should support reasonable viewing sessions without frequent charging
- **Measurement**: Battery drain rate during continuous playback
- **Priority**: SHOULD HAVE

---

### 6.2 Usability Requirements

#### NFR-2.1: Touch Target Sizing
- **Requirement**: All interactive elements must be minimum 48x48dp (Android accessibility guidelines)
- **Rationale**: Young children have limited fine motor control; large targets prevent frustration
- **Measurement**: Automated accessibility scanning, manual review
- **Priority**: MUST HAVE

#### NFR-2.2: Visual Clarity
- **Requirement**: Minimum contrast ratio of 4.5:1 for all text and icons against backgrounds
- **Rationale**: Ensures readability in various lighting conditions, especially important for emerging readers
- **Measurement**: WCAG 2.1 Level AA compliance for contrast
- **Priority**: MUST HAVE

#### NFR-2.3: Font Size
- **Requirement**: Minimum font size of 16sp for all visible text; 20sp+ for primary labels
- **Rationale**: Early readers and children with developing vision need large, clear text
- **Measurement**: Design review, automated tooling
- **Priority**: MUST HAVE

#### NFR-2.4: Learning Curve
- **Requirement**: A 5-year-old child should be able to successfully select and play a video without instruction after 1 demonstration
- **Rationale**: Interface must be self-explanatory through visual design alone
- **Measurement**: Usability testing with target age group (if feasible)
- **Priority**: MUST HAVE

#### NFR-2.5: Error Recovery
- **Requirement**: All error states must provide clear visual feedback and single-tap recovery options
- **Rationale**: Children cannot troubleshoot complex errors; one-tap fixes prevent frustration
- **Measurement**: Error flow testing, child usability observation
- **Priority**: MUST HAVE

---

### 6.3 Reliability Requirements

#### NFR-3.1: Application Stability
- **Requirement**: Application crash rate must be < 0.1% of sessions
- **Rationale**: Crashes disrupt viewing experience and may upset young users
- **Measurement**: Crash analytics (Firebase Crashlytics or similar)
- **Priority**: MUST HAVE

#### NFR-3.2: Offline Mode Reliability
- **Requirement**: Offline playback must succeed 100% of time for fully downloaded content
- **Rationale**: Offline mode is critical for travel/no-connectivity scenarios
- **Measurement**: Automated testing in airplane mode, field testing
- **Priority**: MUST HAVE

#### NFR-3.3: Download Integrity
- **Requirement**: Downloaded videos must be validated for integrity; corrupt downloads must be automatically retried
- **Rationale**: Prevents playback failures of offline content
- **Measurement**: Checksum validation, automatic retry testing
- **Priority**: MUST HAVE

#### NFR-3.4: Network Transition Handling
- **Requirement**: Switching between WiFi, cellular data, and offline modes must not crash the app or interrupt playback
- **Rationale**: Mobile devices frequently change network states
- **Measurement**: Automated network condition simulation testing
- **Priority**: MUST HAVE

#### NFR-3.5: Data Persistence
- **Requirement**: User preferences, viewing history, and download state must persist across app restarts and device reboots
- **Rationale**: Loss of state creates poor user experience and wasted downloads
- **Measurement**: Automated persistence testing, state verification after force-stop
- **Priority**: MUST HAVE

---

### 6.4 Security Requirements

#### NFR-4.1: Credential Storage
- **Requirement**: Jellyfin server credentials must be stored using Android Keystore or equivalent secure storage
- **Rationale**: Protect parent's server credentials from unauthorized access
- **Measurement**: Security code review, penetration testing
- **Priority**: MUST HAVE

#### NFR-4.2: PIN Protection
- **Requirement**: Parental PIN must be stored as salted hash, not plaintext
- **Rationale**: Prevent PIN exposure through device backup or app data extraction
- **Measurement**: Code review, security audit
- **Priority**: MUST HAVE

#### NFR-4.3: Network Communication
- **Requirement**: All communication with Jellyfin server must use HTTPS (TLS 1.2+)
- **Rationale**: Prevent man-in-the-middle attacks and credential interception
- **Measurement**: Network traffic analysis, certificate validation testing
- **Priority**: MUST HAVE

#### NFR-4.4: Certificate Validation
- **Requirement**: App must validate SSL certificates; option to trust custom certificates for self-signed Jellyfin servers (with parent warning)
- **Rationale**: Balance security with reality that many home servers use self-signed certificates
- **Measurement**: Certificate pinning testing, custom certificate flow testing
- **Priority**: MUST HAVE

#### NFR-4.5: Data Privacy
- **Requirement**: No data should be transmitted to third parties; no analytics, advertising, or tracking SDKs
- **Rationale**: Child privacy protection, parental control
- **Measurement**: Network traffic analysis, dependency audit
- **Priority**: MUST HAVE

---

### 6.5 Compatibility Requirements

#### NFR-5.1: Android Version Support
- **Requirement**: Application must support Android 8.0 (API level 26) and higher
- **Rationale**: Balances modern API availability with reasonable device coverage (>90% of active devices)
- **Measurement**: API level declaration in build configuration
- **Priority**: MUST HAVE

#### NFR-5.2: Tablet Optimization
- **Requirement**: Application must be optimized for tablets (7-12 inch screens); phone support is out of scope
- **Rationale**: Designed specifically for tablet use case
- **Measurement**: UI testing on 7", 10", 12" tablet emulators and physical devices
- **Priority**: MUST HAVE

#### NFR-5.3: Orientation Support
- **Requirement**: Application must support both portrait and landscape orientations for browsing; lock to video orientation during playback
- **Rationale**: Children may hold tablet in either orientation
- **Measurement**: Rotation testing, UI verification in both orientations
- **Priority**: MUST HAVE

#### NFR-5.4: Jellyfin Compatibility
- **Requirement**: Application must support Jellyfin server version 10.8.0 and higher
- **Rationale**: Ensures compatibility with recent stable Jellyfin releases
- **Measurement**: Integration testing against specified Jellyfin versions
- **Priority**: MUST HAVE

#### NFR-5.5: Video Format Support
- **Requirement**: Application must support common video formats: MP4 (H.264/H.265), MKV, AVI, WebM
- **Rationale**: Covers most common video encoding formats parents may have
- **Measurement**: Playback testing with various codecs and containers
- **Priority**: MUST HAVE

---

### 6.6 Maintainability Requirements

#### NFR-6.1: Code Documentation
- **Requirement**: All public APIs and complex logic must include inline documentation (KDoc for Kotlin)
- **Rationale**: Enables future maintenance and feature additions by parent (who is also developer)
- **Measurement**: Documentation coverage analysis
- **Priority**: SHOULD HAVE

#### NFR-6.2: Logging and Diagnostics
- **Requirement**: Application must implement structured logging for debugging without excessive performance impact
- **Rationale**: Troubleshooting connectivity and playback issues
- **Measurement**: Log output review, performance profiling with logging enabled
- **Priority**: SHOULD HAVE

#### NFR-6.3: Configuration Management
- **Requirement**: Network endpoints, timeouts, and behavior constants should be configurable without code changes
- **Rationale**: Allows tuning behavior for different network conditions and server configurations
- **Measurement**: Configuration externalization review
- **Priority**: SHOULD HAVE

---

### 6.7 Scalability Requirements

#### NFR-7.1: Content Library Size
- **Requirement**: Application must perform acceptably with libraries up to 1,000 videos
- **Rationale**: Large curated libraries may grow over time
- **Measurement**: Performance testing with 1,000+ item library
- **Priority**: SHOULD HAVE

#### NFR-7.2: Offline Storage Capacity
- **Requirement**: Application must handle up to 50GB of offline content (device storage permitting)
- **Rationale**: High-quality videos consume significant space; must scale to device capacity
- **Measurement**: Large storage testing, storage management verification
- **Priority**: SHOULD HAVE

---

## 7. User Interface Requirements

### 7.1 Design Principles

The Kid Player UI must adhere to these core design principles:

1. **Simplicity Over Features**: Every UI element must serve a clear purpose for a 5-year-old user
2. **Visual-First Navigation**: Icons and images over text; recognize before reading
3. **Immediate Feedback**: Every interaction produces instant visual response
4. **Forgiving Interface**: Difficult to break, easy to recover from mistakes
5. **Consistency**: Same actions produce same results throughout the app
6. **Delight**: Playful, colorful, engaging without being distracting

---

### 7.2 Color Scheme and Visual Style

#### UI-1: Primary Color Palette
- **Requirement**: Use bright, saturated colors that appeal to young children
- **Suggested Palette**:
  - Primary: Vibrant blue (#2196F3) - main action buttons, headers
  - Secondary: Cheerful orange (#FF9800) - secondary actions, highlights
  - Success: Friendly green (#4CAF50) - confirmations, downloads complete
  - Warning: Soft yellow (#FFC107) - cautions, storage warnings
  - Error: Gentle red (#F44336) - errors, but not harsh
  - Background: Light gray (#F5F5F5) or white (#FFFFFF) - clean, not overwhelming
- **Rationale**: High contrast, engaging colors that don't cause visual fatigue
- **Priority**: MUST HAVE

#### UI-2: Typography
- **Requirement**:
  - Primary font: Rounded sans-serif (e.g., Google's Quicksand, Nunito, or Comfortaa)
  - Minimum sizes: 20sp for titles, 16sp for labels
  - Heavy/bold weights for better readability
  - Limited use of text overall
- **Rationale**: Rounded fonts feel friendly; large sizes accommodate emerging readers
- **Priority**: MUST HAVE

#### UI-3: Iconography
- **Requirement**:
  - Custom-designed or carefully selected icon set
  - Filled style (not outline) for clarity
  - Minimum 48x48dp touch targets
  - Simple, recognizable shapes (home = house, play = triangle, etc.)
  - Consistent style across all icons
- **Rationale**: Young children recognize simple, consistent visual patterns
- **Priority**: MUST HAVE

---

### 7.3 Screen Layouts

#### UI-4: Home/Browse Screen Layout

```
┌─────────────────────────────────────────────────┐
│  [Home Icon]              [Settings - Hidden]   │ ← Header (minimal)
├─────────────────────────────────────────────────┤
│                                                 │
│   ┌────────┐  ┌────────┐  ┌────────┐          │
│   │        │  │        │  │        │          │ ← Thumbnail Grid
│   │ Video  │  │ Video  │  │ Video  │          │   (2-3 columns)
│   │   1    │  │   2    │  │   3    │          │
│   │        │  │        │  │        │          │
│   └────────┘  └────────┘  └────────┘          │
│   [Title]     [Title]     [Title]              │
│                                                 │
│   ┌────────┐  ┌────────┐  ┌────────┐          │
│   │        │  │        │  │        │          │
│   │ Video  │  │ Video  │  │ Video  │          │
│   │   4    │  │   5    │  │   6    │          │
│   │        │  │        │  │        │          │
│   └────────┘  └────────┘  └────────┘          │
│   [Title]     [Title]     [Title]              │
│                                                 │
│                  [...]                          │ ← Scroll for more
│                                                 │
│   [Offline Indicator] (if applicable)          │ ← Bottom banner
└─────────────────────────────────────────────────┘
```

**Specifications**:
- **Header**: Minimal height (48dp), simple home icon, settings gear (invisible/hidden area requiring special gesture)
- **Thumbnails**:
  - Portrait mode: 2 columns, ~40% screen width each, 16:9 aspect ratio
  - Landscape mode: 3 columns, ~30% screen width each
  - Generous padding (16dp) between items
  - Rounded corners (8dp radius)
  - Subtle drop shadow for depth
- **Titles**: Below thumbnail, 2 lines max, center-aligned, 18sp
- **Offline Indicator**: Small banner at bottom when offline, friendly cloud icon + "Watching offline" text
- **Scroll**: Vertical only, smooth scrolling with momentum
- **Priority**: MUST HAVE

---

#### UI-5: Video Playback Screen Layout

```
┌─────────────────────────────────────────────────┐
│                                                 │
│                                                 │
│                 [VIDEO PLAYER]                  │ ← Full-screen video
│                                                 │
│                                                 │
│              [Tap to Pause/Play]                │ ← Center tap area
│                                                 │
│                                                 │
│  [🏠]                                           │ ← Controls overlay
│                                                 │   (auto-hide after 5s)
│                         [⏭]                    │
└─────────────────────────────────────────────────┘
```

**Specifications**:
- **Video Player**: Full-screen, aspect-correct (letterbox if needed)
- **Controls Overlay** (appears on tap, fades after 5s):
  - Home button: Top-left corner, 64x64dp, house icon
  - Skip/Next button: Bottom-right corner, 64x64dp, forward icon
  - Play/Pause: Large center button (96x96dp) appears when paused
  - Progress bar: Optional, bottom of screen, simplified (no time stamps)
- **Background**: Black for cinematic experience
- **Gestures**:
  - Single tap center: Pause/play
  - Tap home button: Exit to browse
  - Tap next button: Skip to next video
  - Swipe left/right: Optional skip gesture (future consideration)
- **Priority**: MUST HAVE

---

#### UI-6: Parental Settings Screen Layout

```
┌─────────────────────────────────────────────────┐
│  ← Back                SETTINGS                 │ ← Header
├─────────────────────────────────────────────────┤
│                                                 │
│  📡 Server Connection                           │
│    Server: https://my.jellyfin.server          │
│    Status: ✅ Connected                         │
│    [Change Server]                              │
│                                                 │
│  ─────────────────────────────────────────────  │
│                                                 │
│  ⏱ Screen Time Timer                           │
│    Duration: [60 minutes ▼]                    │
│    Status: ⚫ Disabled / ⚫ Enabled             │
│    Remaining Today: 45 minutes                  │
│    [Reset Timer]                                │
│                                                 │
│  ─────────────────────────────────────────────  │
│                                                 │
│  💾 Storage & Downloads                         │
│    Used: 8.5 GB / 50 GB limit                   │
│    Videos Downloaded: 24                        │
│    Last Sync: 2 hours ago                       │
│    [Sync Now]  [Clear Downloads]                │
│                                                 │
│  ─────────────────────────────────────────────  │
│                                                 │
│  📚 Content Libraries                           │
│    ✅ Kids Shows                                │
│    ✅ Educational Videos                        │
│    ⬜ Movies (disabled)                        │
│    [Manage Libraries]                           │
│                                                 │
│  ─────────────────────────────────────────────  │
│                                                 │
│  🔒 Security                                    │
│    [Change PIN]                                 │
│                                                 │
│  📊 Viewing History                             │
│    [View History]                               │
│                                                 │
└─────────────────────────────────────────────────┘
```

**Specifications**:
- **Access**: Requires PIN entry to access
- **Design**: Adult-focused design, can use standard Material Design components
- **Sections**: Clearly grouped with icons, dividers, and headings
- **Buttons**: Standard size (48dp height), clear labels
- **Information**: Real-time status updates for connection, storage, timer
- **Priority**: MUST HAVE

---

#### UI-7: First-Time Setup Flow

**Screen 1: Welcome**
```
┌─────────────────────────────────────────────────┐
│                                                 │
│                  [App Logo]                     │
│                                                 │
│              Welcome to Kid Player!             │
│                                                 │
│         Safe videos for your little one         │
│                                                 │
│                                                 │
│                  [Get Started]                  │
│                                                 │
└─────────────────────────────────────────────────┘
```

**Screen 2: Create PIN**
```
┌─────────────────────────────────────────────────┐
│                                                 │
│             Set Your Parental PIN               │
│                                                 │
│     This PIN protects settings and controls     │
│                                                 │
│           [_] [_] [_] [_] [_] [_]              │
│                                                 │
│              (Numeric keypad)                   │
│                                                 │
│                   [Continue]                    │
│                                                 │
└─────────────────────────────────────────────────┘
```

**Screen 3: Connect to Jellyfin**
```
┌─────────────────────────────────────────────────┐
│                                                 │
│          Connect to Your Media Server           │
│                                                 │
│  Server URL:                                    │
│  [https://jellyfin.example.com____________]    │
│                                                 │
│  Username:                                      │
│  [_______________________________________]      │
│                                                 │
│  Password:                                      │
│  [_______________________________________]      │
│                                                 │
│             [Test Connection]                   │
│                                                 │
│                   [Continue]                    │
│                                                 │
└─────────────────────────────────────────────────┘
```

**Screen 4: Select Libraries**
```
┌─────────────────────────────────────────────────┐
│                                                 │
│        Choose Content for Your Child            │
│                                                 │
│   Select which libraries your child can see:    │
│                                                 │
│   ✅ Kids Shows                                 │
│   ✅ Educational Videos                         │
│   ⬜ Movies                                     │
│   ⬜ Music Videos                               │
│                                                 │
│                                                 │
│                   [Continue]                    │
│                                                 │
└─────────────────────────────────────────────────┘
```

**Screen 5: Setup Complete**
```
┌─────────────────────────────────────────────────┐
│                                                 │
│                [Success Icon]                   │
│                                                 │
│                 All Set! 🎉                     │
│                                                 │
│         Your child can now safely browse        │
│              and watch videos!                  │
│                                                 │
│      Downloading content in background...       │
│                                                 │
│                  [Start Watching]               │
│                                                 │
└─────────────────────────────────────────────────┘
```

**Priority**: MUST HAVE

---

### 7.4 Interaction Patterns

#### UI-8: Loading States
- **Requirement**: All loading states must show friendly visual feedback
- **Implementation**:
  - Thumbnail loading: Shimmer effect or blurred placeholder
  - Video buffering: Spinning circular progress indicator (large, centered)
  - Background sync: Small persistent notification icon (non-intrusive)
  - No indeterminate loading states > 5 seconds without progress indication
- **Priority**: MUST HAVE

#### UI-9: Error States
- **Requirement**: All error messages must be child-friendly with simple graphics
- **Examples**:
  - Network error: Sad cloud icon + "Oops! Let's try another video"
  - No content: Friendly character + "Ask grown-up to add videos"
  - Storage full: Full toy box icon + "Toy box is full! (Parent notification sent)"
- **Priority**: MUST HAVE

#### UI-10: Empty States
- **Requirement**: Empty states must guide user to resolution
- **Examples**:
  - No downloaded content: "No videos yet! Connect to WiFi to download"
  - No internet + no downloads: "Ask grown-up to download videos"
- **Priority**: MUST HAVE

#### UI-11: Transitions and Animations
- **Requirement**: Smooth, playful animations between states
- **Implementation**:
  - Screen transitions: Slide or fade (300ms duration)
  - Button press: Scale down slightly (100ms) for tactile feedback
  - Video thumbnail tap: Expand to fullscreen (400ms)
  - Auto-hide controls: Fade out (300ms)
  - Avoid jarring or overly complex animations
- **Priority**: SHOULD HAVE

---

### 7.5 Accessibility Considerations

#### UI-12: Accessibility Features
- **Requirements**:
  - Support for TalkBack screen reader (for vision-impaired parents during setup)
  - Content descriptions on all interactive elements
  - Touch target minimum 48x48dp (per Android accessibility guidelines)
  - High contrast mode support (system-level)
  - Support for large system fonts in parental settings
- **Rationale**: Ensures app is usable by parents with accessibility needs
- **Priority**: SHOULD HAVE

---

## 8. Technical Requirements & Constraints

### 8.1 Development Platform

#### TECH-1: Development Environment
- **Platform**: Android (native development)
- **Language**: Kotlin (preferred) or Java
- **IDE**: Android Studio (latest stable version)
- **Build System**: Gradle
- **Development Machine**: macOS (as specified by parent)
- **Priority**: MUST HAVE

---

### 8.2 Android Technical Stack

#### TECH-2: Minimum SDK and Target SDK
- **Minimum SDK**: API Level 26 (Android 8.0 Oreo)
- **Target SDK**: API Level 34 (Android 14) or latest stable
- **Rationale**: Balances modern API availability with broad device support
- **Priority**: MUST HAVE

#### TECH-3: Architecture Pattern
- **Recommendation**: MVVM (Model-View-ViewModel) with Android Architecture Components
- **Components**:
  - **ViewModel**: Business logic and state management
  - **LiveData/StateFlow**: Reactive data observation
  - **Repository Pattern**: Data layer abstraction
  - **Room Database**: Local persistence (viewing history, download metadata)
  - **WorkManager**: Background download orchestration
- **Rationale**: Google-recommended architecture for maintainable Android apps
- **Priority**: SHOULD HAVE (best practice, but architecture choice is flexible)

#### TECH-4: UI Framework
- **Recommendation**: Jetpack Compose (modern) or XML layouts with Material Design Components
- **Jetpack Compose Pros**: Modern, declarative, easier animations
- **XML Pros**: More mature, extensive documentation, simpler for beginners
- **Decision**: Parent's preference based on familiarity
- **Priority**: MUST HAVE (one or the other)

#### TECH-5: Video Playback Library
- **Recommendation**: ExoPlayer (Google's media player library)
- **Features**:
  - Supports wide range of video formats
  - Adaptive streaming
  - Offline playback support
  - Customizable UI
  - Well-documented
- **Alternative**: Android MediaPlayer (simpler but less capable)
- **Priority**: MUST HAVE

#### TECH-6: Network Library
- **Recommendation**: Retrofit + OkHttp for Jellyfin API communication
- **Features**:
  - RESTful API client
  - Automatic JSON parsing (with Gson or Moshi)
  - Interceptor support for authentication
  - Download progress tracking
- **Priority**: MUST HAVE

#### TECH-7: Image Loading Library
- **Recommendation**: Coil (Kotlin-first) or Glide
- **Features**:
  - Async image loading
  - Caching (memory + disk)
  - Placeholder and error handling
  - Image transformations (rounded corners, etc.)
- **Priority**: MUST HAVE

#### TECH-8: Dependency Injection
- **Recommendation**: Hilt (built on Dagger, Android-optimized)
- **Rationale**: Simplifies dependency management, testability
- **Alternative**: Koin (simpler, Kotlin-native)
- **Priority**: SHOULD HAVE (beneficial but not critical for MVP)

---

### 8.3 Jellyfin Integration

#### TECH-9: Jellyfin API Version
- **Requirement**: Support Jellyfin API version 10.8.x and higher
- **API Documentation**: https://api.jellyfin.org/
- **Authentication**: Use Jellyfin's token-based authentication
- **Priority**: MUST HAVE

#### TECH-10: Jellyfin Client Library
- **Option 1**: Official Jellyfin Kotlin SDK (if available and maintained)
- **Option 2**: Custom REST client using Retrofit
- **Recommendation**: Evaluate official SDK first; fall back to custom implementation
- **Priority**: MUST HAVE

#### TECH-11: Required Jellyfin APIs
The application must integrate with the following Jellyfin API endpoints:

| API Endpoint | Purpose | Priority |
|--------------|---------|----------|
| `/Users/AuthenticateByName` | User authentication | MUST HAVE |
| `/Users/{userId}/Items` | Retrieve library content | MUST HAVE |
| `/Items/{itemId}/Images` | Fetch thumbnails/artwork | MUST HAVE |
| `/Videos/{itemId}/stream` | Stream video content | MUST HAVE |
| `/Items/{itemId}/Download` | Download for offline | MUST HAVE |
| `/Users/{userId}/Items/Latest` | Get recently added content | SHOULD HAVE |
| `/Users/{userId}/PlayedItems` | Mark as watched | SHOULD HAVE |
| `/Library/MediaFolders` | List available libraries | MUST HAVE |

#### TECH-12: Jellyfin Server Requirements
- **Network Access**: Server must be accessible outside local network (VPN, reverse proxy, or direct port forward)
- **Server Version**: Jellyfin 10.8.0 or higher
- **HTTPS**: Strongly recommended; app should support both HTTP (dev) and HTTPS (production)
- **Self-Signed Certificates**: App should allow trusting custom certificates with parent confirmation
- **Priority**: MUST HAVE

---

### 8.4 Data Storage and Persistence

#### TECH-13: Local Database
- **Technology**: Room Persistence Library (SQLite abstraction)
- **Schema Requirements**:
  - **Videos Table**: Video metadata (ID, title, thumbnail URL, Jellyfin item ID, duration, watched status, download path)
  - **Download Queue Table**: Videos queued for download (status, priority, file size, download progress)
  - **Viewing History Table**: Timestamp, video ID, duration watched, completion percentage
  - **App Settings Table**: User preferences (screen time limit, selected libraries, sync settings)
- **Priority**: MUST HAVE

#### TECH-14: Offline Video Storage
- **Location**: App-specific external storage (`Context.getExternalFilesDir()`)
- **Format**: Original format as downloaded from Jellyfin (avoid re-encoding)
- **Organization**:
  ```
  /Android/data/com.example.kidplayer/files/videos/
    ├── {jellyfin-item-id-1}.mp4
    ├── {jellyfin-item-id-2}.mkv
    └── ...
  ```
- **Metadata**: Stored in Room database, not file system
- **Cleanup**: Automatic deletion when app uninstalled (Android behavior)
- **Priority**: MUST HAVE

#### TECH-15: Thumbnail Cache
- **Management**: Handled by image loading library (Coil/Glide)
- **Location**: App cache directory
- **Size Limit**: Configurable, recommend 500MB max
- **Eviction**: LRU (Least Recently Used)
- **Priority**: MUST HAVE

#### TECH-16: Secure Credential Storage
- **Technology**: Android Keystore System + EncryptedSharedPreferences
- **Stored Data**:
  - Jellyfin server URL
  - Jellyfin username (encrypted)
  - Jellyfin authentication token (encrypted)
  - Parental PIN (hashed with salt, NOT encrypted)
- **Priority**: MUST HAVE

---

### 8.5 Background Processing

#### TECH-17: Background Download Orchestration
- **Technology**: WorkManager (Android Jetpack)
- **Work Type**: Periodic work (daily check) + One-time expedited work (manual sync)
- **Constraints**:
  - Require WiFi connection
  - Require battery not low (or device charging)
  - Require sufficient storage
- **Chaining**: Download jobs can be chained for multiple videos
- **Retry Policy**: Exponential backoff for failed downloads
- **Priority**: MUST HAVE

#### TECH-18: Download Strategy
- **Algorithm**:
  1. Fetch list of all videos in enabled libraries
  2. Exclude already downloaded videos
  3. Exclude watched videos (if unwatched preferred)
  4. Sort by: Recently added DESC (or parent-configured priority)
  5. Calculate total duration of queue
  6. Download videos until 60-120 minutes of content accumulated
  7. Monitor storage; stop if < 2GB free space remains
- **Concurrency**: Download 1 video at a time to avoid network saturation
- **Priority**: MUST HAVE

#### TECH-19: Network Monitoring
- **Technology**: ConnectivityManager + Network Callbacks
- **Implementation**:
  - Register network callback to detect connectivity changes
  - Distinguish between WiFi, cellular, and no connection
  - Pause/resume downloads based on network type
  - Notify UI layer of connectivity changes for offline mode switching
- **Priority**: MUST HAVE

---

### 8.6 Performance Optimization

#### TECH-20: Video Streaming Optimization
- **Adaptive Bitrate**: Use ExoPlayer's adaptive streaming capabilities
- **Buffer Configuration**:
  - Min buffer: 5 seconds
  - Max buffer: 30 seconds
  - Playback buffer: 2 seconds
- **Caching**: Enable ExoPlayer's cache for partial streaming cache (helps with replays)
- **Priority**: SHOULD HAVE

#### TECH-21: Image Optimization
- **Thumbnail Size**: Request appropriately sized thumbnails from Jellyfin (e.g., 400x225 for grid)
- **Lazy Loading**: Load thumbnails only when visible (RecyclerView optimization)
- **Bitmap Pooling**: Reuse bitmap memory (handled by Coil/Glide)
- **Priority**: SHOULD HAVE

#### TECH-22: Memory Management
- **Pagination**: Implement paging for large content libraries (Paging 3 library)
- **Lifecycle Awareness**: Cancel network requests when activities/fragments destroyed
- **Leak Prevention**: Use lifecycle-aware components, avoid static references to contexts
- **Priority**: SHOULD HAVE

---

### 8.7 Testing Requirements

#### TECH-23: Unit Testing
- **Framework**: JUnit 4/5 + Mockito or MockK
- **Coverage Target**: 60%+ for business logic (ViewModels, repositories)
- **Priority**: SHOULD HAVE

#### TECH-24: Instrumented Testing
- **Framework**: Espresso for UI testing
- **Critical Flows**:
  - First-time setup flow
  - Video playback
  - Offline mode switching
  - Parental controls access
- **Priority**: SHOULD HAVE

#### TECH-25: Integration Testing
- **Scope**: Test Jellyfin API integration with mock server or test server
- **Coverage**: Authentication, content retrieval, streaming, download
- **Priority**: SHOULD HAVE

---

### 8.8 Build and Deployment

#### TECH-26: Build Variants
- **Debug Build**:
  - Allows HTTP connections
  - Logging enabled
  - Debuggable
- **Release Build**:
  - HTTPS only (unless explicitly trusted)
  - Logging minimal
  - ProGuard/R8 code shrinking and obfuscation
  - Signed with release key
- **Priority**: MUST HAVE

#### TECH-27: APK Signing
- **Requirement**: Generate and securely store release signing key
- **Storage**: Keep keystore file backed up securely (not in version control)
- **Priority**: MUST HAVE (for production distribution)

#### TECH-28: Distribution
- **Method**: Direct APK installation (sideloading)
- **No Play Store**: Out of scope for MVP
- **Installation**: Enable "Install from Unknown Sources" on tablet
- **Priority**: MUST HAVE

---

### 8.9 Development Constraints

#### TECH-29: Third-Party SDK Restrictions
- **Prohibited**:
  - Analytics SDKs (Google Analytics, Firebase Analytics, etc.)
  - Advertising SDKs
  - Social media SDKs
  - Any SDK that transmits user data to third parties
- **Allowed**:
  - Crash reporting (optional, for debugging only, e.g., Firebase Crashlytics with data collection minimized)
  - Development tools (debugging, profiling)
- **Rationale**: Child privacy, parental control, no external data sharing
- **Priority**: MUST HAVE

#### TECH-30: Open Source Licensing
- **Requirement**: All dependencies must have compatible licenses (Apache 2.0, MIT, BSD preferred)
- **Avoid**: GPL/AGPL (if distributing to others)
- **Documentation**: Maintain list of dependencies and licenses
- **Priority**: SHOULD HAVE

---

## 9. Data Requirements

### 9.1 Data Entities

#### DATA-1: Video Metadata
**Source**: Jellyfin API
**Storage**: Room database (local), refreshed periodically

| Field | Type | Description | Source |
|-------|------|-------------|--------|
| `jellyfinItemId` | String (PK) | Unique Jellyfin item ID | Jellyfin API |
| `title` | String | Video title | Jellyfin metadata |
| `thumbnailUrl` | String | URL to thumbnail image | Jellyfin API |
| `duration` | Long | Video duration in seconds | Jellyfin metadata |
| `addedDate` | Date | When video was added to Jellyfin | Jellyfin metadata |
| `seriesName` | String (nullable) | Series name if part of series | Jellyfin metadata |
| `episodeNumber` | Int (nullable) | Episode number if applicable | Jellyfin metadata |
| `seasonNumber` | Int (nullable) | Season number if applicable | Jellyfin metadata |
| `libraryId` | String | Which library this belongs to | Jellyfin API |
| `downloadedPath` | String (nullable) | Local file path if downloaded | App-managed |
| `downloadedDate` | Date (nullable) | When downloaded locally | App-managed |
| `watchedStatus` | Boolean | Whether child has watched | App-managed |
| `lastWatchedDate` | Date (nullable) | Last time watched | App-managed |

**Priority**: MUST HAVE

---

#### DATA-2: Download Queue
**Source**: App-generated
**Storage**: Room database

| Field | Type | Description |
|-------|------|-------------|
| `queueId` | Long (PK) | Auto-increment ID |
| `jellyfinItemId` | String (FK) | Reference to video |
| `status` | Enum | QUEUED, DOWNLOADING, COMPLETED, FAILED |
| `priority` | Int | Download priority (lower = higher priority) |
| `fileSize` | Long | Expected file size in bytes |
| `downloadedBytes` | Long | Bytes downloaded so far |
| `downloadProgress` | Float | Percentage (0-100) |
| `queuedDate` | Date | When added to queue |
| `completedDate` | Date (nullable) | When download finished |
| `errorMessage` | String (nullable) | Error details if failed |
| `retryCount` | Int | Number of retry attempts |

**Priority**: MUST HAVE

---

#### DATA-3: Viewing History
**Source**: App-generated
**Storage**: Room database

| Field | Type | Description |
|-------|------|-------------|
| `historyId` | Long (PK) | Auto-increment ID |
| `jellyfinItemId` | String (FK) | Reference to video |
| `watchedDate` | DateTime | When viewing occurred |
| `durationWatched` | Long | Seconds watched |
| `completionPercentage` | Float | Percentage of video watched (0-100) |
| `wasOffline` | Boolean | Whether watched in offline mode |

**Priority**: SHOULD HAVE

---

#### DATA-4: App Configuration
**Source**: Parent input
**Storage**: EncryptedSharedPreferences + Room

| Setting | Type | Storage Method | Description |
|---------|------|----------------|-------------|
| `jellyfinServerUrl` | String | Encrypted SharedPreferences | Server endpoint |
| `jellyfinUsername` | String | Encrypted SharedPreferences | Username |
| `jellyfinAuthToken` | String | Encrypted SharedPreferences | Auth token |
| `parentalPin` | String | Encrypted SharedPreferences | Hashed PIN |
| `enabledLibraries` | List<String> | Room | Enabled library IDs |
| `screenTimeLimitMinutes` | Int | Room | Screen time limit |
| `screenTimerEnabled` | Boolean | Room | Whether timer is active |
| `screenTimeResetTime` | Time | Room | When timer resets (e.g., midnight) |
| `maxStorageLimitGB` | Int | Room | Max storage for downloads |
| `autoDownloadEnabled` | Boolean | Room | Whether auto-download is on |
| `downloadTargetMinutes` | Int | Room | Target download duration (60-120) |
| `lastSyncTimestamp` | DateTime | Room | Last successful sync |

**Priority**: MUST HAVE

---

### 9.2 Data Synchronization

#### DATA-5: Content Library Sync Strategy
**Frequency**:
- Automatic: Every 24 hours (when WiFi + charging)
- Manual: Parent-triggered via "Sync Now" button
- On-demand: When app detects new content (if Jellyfin supports webhooks - future)

**Process**:
1. Authenticate with Jellyfin server
2. Fetch all items from enabled libraries
3. Compare with local database (identify new, updated, deleted)
4. Update local metadata database
5. Add new unwatched items to download queue
6. Remove metadata for deleted items
7. Delete local files for removed items (if downloaded)
8. Update last sync timestamp

**Conflict Resolution**: Server is source of truth for content; local state (watched status) is preserved

**Priority**: MUST HAVE

---

#### DATA-6: Offline Data Persistence
**Requirement**: All downloaded videos and metadata must remain accessible offline indefinitely

**Implementation**:
- Videos stored in app-specific external storage (persists across app restarts)
- Metadata cached in Room database (SQLite)
- No dependency on network for playback of downloaded content
- Graceful handling of deleted Jellyfin content (keep local until manually cleared or storage needed)

**Priority**: MUST HAVE

---

#### DATA-7: Data Retention Policies

| Data Type | Retention Policy | Rationale |
|-----------|------------------|-----------|
| Downloaded Videos | Until watched + storage needed, or manual deletion | Maximize offline availability |
| Video Metadata | Until video deleted from Jellyfin + 30 days | Grace period for server issues |
| Viewing History | 90 days | Sufficient for parental review, not excessive |
| Download Queue | Clear completed after 7 days | Housekeeping |
| Thumbnails (cache) | LRU eviction when cache > 500MB | Standard caching practice |
| App Logs | 7 days rolling | Debugging without excessive storage |

**Priority**: SHOULD HAVE

---

### 9.3 Data Migration

#### DATA-8: Database Schema Versioning
**Requirement**: Implement Room database migrations for schema changes

**Strategy**:
- Use Room Migration classes for version upgrades
- Test migrations thoroughly (MigrationTest)
- Fallback: Destructive migration acceptable for early versions (pre-release)
- Production: Always provide migration path to preserve user data

**Priority**: SHOULD HAVE (critical after initial release)

---

### 9.4 Data Privacy and Compliance

#### DATA-9: Personal Data Handling
**Data Collected**:
- Jellyfin credentials (server URL, username, token)
- Viewing history (video titles, timestamps, duration)
- Parental PIN

**Data NOT Collected**:
- No personally identifiable information beyond Jellyfin credentials
- No analytics, tracking, or behavioral data
- No data transmitted to third parties
- No crash reports containing PII

**Storage**:
- All data stored locally on device
- Credentials encrypted at rest
- No cloud backup by default (user can manually backup via Android backup features)

**Compliance**:
- COPPA (Children's Online Privacy Protection Act): Not applicable as no data transmitted online about child
- GDPR: Not applicable as no EU user data transmitted to third parties
- General best practice: Minimize data collection, secure storage, no third-party sharing

**Priority**: MUST HAVE

---

## 10. Security & Parental Control Requirements

### 10.1 Authentication and Authorization

#### SEC-1: Jellyfin Authentication
**Requirement**: Securely authenticate with Jellyfin server using username/password initially, then token-based for subsequent requests

**Implementation**:
```
1. Initial authentication: POST /Users/AuthenticateByName with username/password
2. Receive authentication token
3. Store token securely in EncryptedSharedPreferences
4. Use token for all subsequent API requests via Authorization header
5. Handle token expiration gracefully (re-authenticate if 401 response)
```

**Security Measures**:
- Never log credentials or tokens
- Clear credentials from memory after use
- Use HTTPS for transmission (enforce in production)

**Priority**: MUST HAVE

---

#### SEC-2: Parental PIN System
**Requirement**: Four to six digit PIN protects all parental settings and controls

**Implementation**:
- **PIN Creation**:
  - Require PIN during first-time setup
  - Confirm PIN (enter twice)
  - Minimum 4 digits, maximum 6 digits
  - No default or common PINs allowed (1234, 0000, etc. - show warning)

- **PIN Storage**:
  - Hash PIN using PBKDF2 with salt (minimum 10,000 iterations)
  - Store hash in EncryptedSharedPreferences
  - Never store plaintext PIN

- **PIN Validation**:
  - Rate limiting: 3 attempts, then 5-minute lockout
  - Increment lockout duration for repeated failures (5 min, 15 min, 30 min)
  - Log failed attempts

- **PIN Recovery**:
  - Security question option (future consideration)
  - App data clear (nuclear option - loses all settings and downloads)

**Priority**: MUST HAVE

---

### 10.2 Network Security

#### SEC-3: HTTPS Enforcement
**Requirement**: Enforce HTTPS for all Jellyfin server communication in production

**Implementation**:
- **Debug Build**: Allow HTTP for local testing
- **Release Build**: Default to HTTPS only
- **Self-Signed Certificates**:
  - Show warning dialog when self-signed cert detected
  - Require explicit parent confirmation to trust
  - Store certificate pin for future connections
  - Display certificate details (issuer, expiration)

**Priority**: MUST HAVE

---

#### SEC-4: Certificate Pinning (Optional Enhancement)
**Requirement**: Option to pin Jellyfin server certificate to prevent MITM attacks

**Implementation**:
- On first successful connection, optionally store certificate fingerprint
- Validate certificate on subsequent connections
- Alert parent if certificate changes unexpectedly
- Provide override mechanism (in case of legitimate cert renewal)

**Priority**: COULD HAVE (nice to have for security-conscious parents)

---

### 10.3 Application Security

#### SEC-5: Secure Data Storage
**Requirements**:

| Data Type | Storage Mechanism | Security Level |
|-----------|-------------------|----------------|
| Jellyfin credentials | EncryptedSharedPreferences (AES-256) | High |
| Parental PIN | Hashed (PBKDF2) in EncryptedSharedPreferences | High |
| Video metadata | Room database (unencrypted - not sensitive) | Low |
| Viewing history | Room database (unencrypted - not sensitive) | Low |
| Downloaded videos | App-specific storage (unencrypted - already on device) | Low |

**Encryption Keys**:
- Use Android Keystore for key management
- Generate keys on device (never transmitted)
- Keys protected by device lock screen (if available)

**Priority**: MUST HAVE

---

#### SEC-6: Code Obfuscation
**Requirement**: Obfuscate release builds to deter reverse engineering

**Implementation**:
- Enable R8/ProGuard in release build
- Obfuscate all code except:
  - Jellyfin API models (need reflection)
  - Room entities
  - ExoPlayer interfaces
- Keep rules for necessary libraries
- Test release builds thoroughly after obfuscation

**Priority**: SHOULD HAVE

---

#### SEC-7: Prevent Screenshot/Screen Recording (Optional)
**Requirement**: Optionally prevent screenshots and screen recording during video playback

**Consideration**:
- **Pro**: Protects premium content from unauthorized capture
- **Con**: May frustrate parents trying to troubleshoot issues
- **Recommendation**: Make this optional in parental settings, default OFF

**Implementation**: `window.setFlags(WindowManager.LayoutParams.FLAG_SECURE)`

**Priority**: COULD HAVE

---

### 10.4 Parental Control Features

#### SEC-8: Content Filtering
**Requirement**: Parents control which Jellyfin libraries are accessible to child

**Implementation**:
- During setup, parent selects enabled libraries
- App only queries and displays content from enabled libraries
- Child cannot access library selection (behind PIN)
- Changes take effect immediately (refresh content view)

**Priority**: MUST HAVE

---

#### SEC-9: Screen Time Enforcement
**Requirement**: Track viewing time and enforce limits set by parent

**Implementation**:
- **Timer State**:
  - Store in persistent storage (survives app restart)
  - Track cumulative watch time since last reset
  - Reset at parent-configured time (e.g., midnight daily)

- **Enforcement**:
  - Check remaining time before starting playback
  - Show warning at 5 minutes remaining
  - Pause playback when time expires
  - Display child-friendly "time's up" message
  - Require PIN to grant more time or disable timer

- **Bypass**:
  - Parent can grant temporary extensions (15, 30, 60 minutes)
  - Parent can disable timer temporarily or permanently
  - All bypass actions require PIN

**Priority**: SHOULD HAVE

---

#### SEC-10: Settings Access Protection
**Requirement**: All settings, configurations, and administrative functions require PIN

**Protected Areas**:
- Jellyfin server connection settings
- Library selection
- Screen time configuration
- Download management (clear downloads, manual sync)
- Viewing history
- PIN change
- About/version info (can be unprotected for troubleshooting)

**Unprotected Areas**:
- Video browsing and playback (child's domain)
- Offline indicator display

**Priority**: MUST HAVE

---

### 10.5 Child Safety

#### SEC-11: No External Links
**Requirement**: Application must not contain any links to external websites or browsers

**Rationale**: Prevent child from navigating away from curated content to uncontrolled internet

**Implementation**:
- No WebView components
- No Intent links to external URLs
- Help/support accessed via parent's separate device

**Priority**: MUST HAVE

---

#### SEC-12: No In-App Purchases or Ads
**Requirement**: Zero advertising, in-app purchases, or payment prompts

**Implementation**:
- No ad SDKs
- No payment processing
- No links to stores
- Clean, distraction-free interface

**Priority**: MUST HAVE

---

#### SEC-13: Age-Appropriate Error Messages
**Requirement**: All user-facing messages use simple, non-technical, friendly language

**Examples**:
- ❌ "HTTP 503 Service Unavailable"
- ✅ "Oops! Can't connect right now. Let's try another video!"

- ❌ "Insufficient storage space (errno: 28)"
- ✅ "Toy box is full! Ask grown-up to make room."

**Priority**: MUST HAVE

---

### 10.6 Security Testing

#### SEC-14: Security Audit Checklist
Before release, verify:

- [ ] Credentials stored encrypted, not plaintext
- [ ] PIN stored as hash, not plaintext
- [ ] HTTPS enforced for production
- [ ] No sensitive data logged
- [ ] ProGuard/R8 enabled and tested
- [ ] No third-party analytics or tracking
- [ ] Certificate validation working
- [ ] Rate limiting on PIN attempts
- [ ] Parental controls cannot be bypassed
- [ ] No external links or WebViews
- [ ] App permissions minimized (see SEC-15)

**Priority**: MUST HAVE

---

#### SEC-15: Android Permissions
**Required Permissions**:
- `INTERNET` - Network communication with Jellyfin
- `ACCESS_NETWORK_STATE` - Detect connectivity changes
- `WRITE_EXTERNAL_STORAGE` (API < 29) - Save downloaded videos
- `READ_EXTERNAL_STORAGE` (API < 29) - Read downloaded videos
- `WAKE_LOCK` - Keep device awake during downloads
- `FOREGROUND_SERVICE` - Background download service

**NOT Required** (explicitly avoid):
- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`
- `CAMERA`
- `READ_CONTACTS`
- `RECORD_AUDIO`
- Any other invasive permissions

**Priority**: MUST HAVE

---

## 11. Assumptions and Dependencies

### 11.1 Assumptions

#### ASSUM-1: Network and Infrastructure
1. **Jellyfin Server Availability**: Parent's Jellyfin server is configured, accessible, and has appropriate content before app use
2. **External Network Access**: Jellyfin server is accessible from outside home network (via VPN, reverse proxy, or port forwarding)
3. **WiFi Availability**: Tablet has periodic access to WiFi for background downloads (at home, etc.)
4. **Bandwidth**: Sufficient network bandwidth for streaming video (minimum 3 Mbps for standard definition)

#### ASSUM-2: Content and Metadata
5. **Pre-Curated Content**: Parent has already curated age-appropriate content in Jellyfin libraries
6. **Jellyfin Metadata**: Videos have proper metadata (titles, thumbnails) configured in Jellyfin
7. **Content Format**: Videos are in formats compatible with Android MediaCodec/ExoPlayer (MP4/H.264 preferred)
8. **No DRM**: Content is not DRM-protected (app does not handle DRM)

#### ASSUM-3: Device and User
9. **Single Child User**: App designed for one child (not multiple children with separate profiles)
10. **Tablet Ownership**: Tablet is dedicated or semi-dedicated to child's use (not shared extensively with adults)
11. **Parent Technical Proficiency**: Parent can install APK, configure network settings, manage Jellyfin server
12. **Device Storage**: Tablet has minimum 8GB free storage available for app and downloads
13. **Device Performance**: Tablet has sufficient CPU/GPU for smooth video playback (mid-range tablet from last 5 years)

#### ASSUM-4: Usage Patterns
14. **Supervised Setup**: Parent performs initial setup and configuration (child cannot set up alone)
15. **Primarily Offline Use**: App will be used offline frequently (travel, bedtime), not just as streaming client
16. **Screen Time Compliance**: Child will not actively attempt to circumvent screen time limits (age-appropriate compliance)

#### ASSUM-5: Regulatory and Legal
17. **Personal Use Only**: App is for personal family use, not commercial distribution
18. **Content Licensing**: Parent has legal right to store and play content on Jellyfin server
19. **No Third-Party Services**: No dependency on external services besides Jellyfin (no cloud services, analytics, etc.)

---

### 11.2 Dependencies

#### DEP-1: External Systems
1. **Jellyfin Media Server**:
   - **Version**: 10.8.0 or higher
   - **Availability**: Must be reachable from tablet's network
   - **Impact if unavailable**: App can function in offline mode but cannot refresh content or stream new videos

2. **Network Connectivity**:
   - **Requirement**: WiFi for downloads; can tolerate offline periods
   - **Impact if unavailable**: No new content, but offline playback unaffected

#### DEP-2: Android Platform
3. **Android OS**:
   - **Version**: 8.0 (API 26) or higher
   - **Impact**: Older devices cannot run app

4. **Google Play Services**:
   - **Not required**: App does not depend on Play Services
   - **Advantage**: Works on de-Googled devices, Amazon Fire tablets, etc.

#### DEP-3: Third-Party Libraries
5. **ExoPlayer**: Google's media playback library
   - **Dependency Type**: Critical for video playback
   - **Mitigation**: Well-maintained by Google, stable API
   - **License**: Apache 2.0

6. **Retrofit + OkHttp**: Networking libraries
   - **Dependency Type**: Critical for Jellyfin communication
   - **Mitigation**: Industry-standard libraries, actively maintained
   - **License**: Apache 2.0

7. **Room**: Database library
   - **Dependency Type**: Critical for local data persistence
   - **Mitigation**: Official Android Jetpack component
   - **License**: Apache 2.0

8. **Coil or Glide**: Image loading library
   - **Dependency Type**: Important for thumbnail display
   - **Mitigation**: Both are well-maintained; can switch if needed
   - **License**: Apache 2.0

9. **WorkManager**: Background task scheduling
   - **Dependency Type**: Critical for auto-downloads
   - **Mitigation**: Official Android Jetpack component
   - **License**: Apache 2.0

#### DEP-4: Development Tools
10. **Android Studio**: Primary IDE
    - **Version**: Latest stable release recommended
    - **Platform**: Available on macOS (parent's platform)

11. **Kotlin**: Programming language
    - **Version**: Latest stable (1.9+)
    - **Alternative**: Java (if parent prefers)

#### DEP-5: External Dependencies Risks

| Dependency | Risk | Likelihood | Impact | Mitigation |
|------------|------|------------|--------|------------|
| Jellyfin API changes | Breaking changes in API | Low | High | Use versioned API, test with multiple versions |
| ExoPlayer updates | API deprecations | Medium | Medium | Pin to stable version, test before upgrading |
| Android OS changes | Behavior changes, permission model | Medium | Medium | Test on multiple Android versions |
| Library maintenance | Library abandonment | Low | Medium | Choose well-maintained libraries with large communities |

---

### 11.3 Constraints

#### CONS-1: Technical Constraints
1. **Single Platform**: Android only (no iOS, web, or desktop versions)
2. **No Backend**: No custom backend server; Jellyfin is sole backend
3. **Local Processing**: All business logic runs on-device (no cloud computing)
4. **Storage Limits**: Constrained by device storage capacity

#### CONS-2: Resource Constraints
5. **Solo Developer**: Parent is building this alone (not a team)
6. **Time Constraint**: Likely built in spare time alongside parenting and other responsibilities
7. **Budget**: Zero budget for third-party services (free/open-source only)

#### CONS-3: User Experience Constraints
8. **Target User Age**: Must be usable by 5-year-old (limits UI complexity)
9. **Reading Ability**: Cannot rely on text navigation (icons and images primary)
10. **Attention Span**: Must support quick, immediate access to content

#### CONS-4: Distribution Constraints
11. **No Play Store**: Manual APK installation only (at least for MVP)
12. **Single Device**: Designed for one tablet, not scalable deployment

---

## 12. Risks and Mitigation Strategies

### 12.1 Technical Risks

#### RISK-1: Jellyfin API Compatibility
**Risk**: Jellyfin API changes break app functionality
**Likelihood**: Low-Medium
**Impact**: High (app may stop working)
**Mitigation**:
- Use stable API endpoints (avoid experimental features)
- Test with multiple Jellyfin versions during development
- Implement graceful error handling for API failures
- Pin to minimum supported Jellyfin version in documentation
- Monitor Jellyfin release notes and changelog

---

#### RISK-2: Video Format Incompatibility
**Risk**: Some parent's videos are in formats not supported by Android/ExoPlayer
**Likelihood**: Medium
**Impact**: Medium (some videos won't play)
**Mitigation**:
- Document supported formats clearly
- Implement format detection and user-friendly error messages
- Suggest transcoding tools for incompatible formats (Jellyfin can transcode, but that's server-side configuration)
- Gracefully skip unplayable videos in autoplay

**Contingency**: Parent re-encodes videos to MP4/H.264

---

#### RISK-3: Network Reliability
**Risk**: Unstable network causes playback interruptions or failed downloads
**Likelihood**: Medium
**Impact**: Medium-High (frustrates child, defeats purpose)
**Mitigation**:
- Implement robust retry logic with exponential backoff
- Use ExoPlayer's adaptive streaming to handle bandwidth fluctuations
- Maintain offline-first architecture (prioritize offline capability)
- Resume interrupted downloads automatically
- Provide clear feedback to parent when network issues detected

---

#### RISK-4: Storage Exhaustion
**Risk**: Tablet runs out of storage, preventing new downloads
**Likelihood**: Medium
**Impact**: Medium (can't download new content)
**Mitigation**:
- Implement automatic cleanup of watched videos
- Monitor storage proactively and warn parent
- Set configurable storage limits
- Provide easy manual clear option
- Fail gracefully when storage full (don't crash)

---

#### RISK-5: Performance Issues on Older Tablets
**Risk**: App runs slowly or stutters on older/budget tablets
**Likelihood**: Medium
**Impact**: Medium (poor user experience)
**Mitigation**:
- Optimize image loading (appropriate thumbnail sizes, lazy loading)
- Profile app on low-end devices during development
- Use efficient video codecs (H.264 is widely supported)
- Limit background tasks during active use
- Provide performance settings (lower video quality, fewer thumbnails, etc.)

**Contingency**: Document minimum recommended device specifications

---

### 12.2 Usability Risks

#### RISK-6: Interface Too Complex for 5-Year-Old
**Risk**: Child cannot navigate app independently
**Likelihood**: Medium
**Impact**: High (defeats primary goal)
**Mitigation**:
- Conduct usability testing with 5-year-old (own child)
- Iterate on UI based on child's actual behavior
- Minimize options and choices
- Use large, obvious visual cues
- Observe child using app and simplify confusing areas

**Contingency**: Parent assists child initially until familiarity develops

---

#### RISK-7: Accidental Settings Access
**Risk**: Child accidentally enters settings and changes configurations
**Likelihood**: Low (with PIN protection)
**Impact**: Medium (could disable features, change libraries)
**Mitigation**:
- Hide settings access (no obvious settings button on child screens)
- Require PIN immediately when accessing settings
- Use subtle gesture or hidden button to access settings (e.g., long-press app logo)
- Confirm destructive actions (e.g., clear downloads)

---

#### RISK-8: Screen Time Circumvention
**Risk**: Child finds way to bypass screen time limits
**Likelihood**: Low-Medium (depends on child's tech-savviness)
**Impact**: Medium (undermines parental control)
**Mitigation**:
- Store screen time state persistently (survives app restart)
- Require PIN to extend time
- Make timer state tamper-resistant (check system clock manipulation)
- Log timer events for parent review

**Contingency**: Use Android's built-in Digital Wellbeing as additional layer (OS-level enforcement)

---

### 12.3 Security Risks

#### RISK-9: Credential Exposure
**Risk**: Jellyfin credentials leaked or stolen from device
**Likelihood**: Low
**Impact**: High (unauthorized server access)
**Mitigation**:
- Use Android Keystore for encryption keys
- Encrypt credentials at rest
- Never log credentials
- Clear credentials from memory after use
- Implement secure token storage

**Contingency**: Parent changes Jellyfin password, invalidates tokens

---

#### RISK-10: Child Bypasses Parental Controls
**Risk**: Child figures out PIN or finds way around restrictions
**Likelihood**: Low (5-year-old unlikely to crack PIN)
**Impact**: Medium (accesses settings, changes config)
**Mitigation**:
- Use strong PIN hashing (PBKDF2)
- Rate-limit PIN attempts
- No PIN hints visible to child
- Lockout after multiple failed attempts

**Contingency**: Parent changes PIN, reviews and resets settings

---

#### RISK-11: Man-in-the-Middle Attack
**Risk**: Network attacker intercepts Jellyfin communication
**Likelihood**: Low (home network typically safe)
**Impact**: Medium (credential theft, content interception)
**Mitigation**:
- Enforce HTTPS in production
- Validate SSL certificates
- Optional certificate pinning
- Warn parent if using HTTP or untrusted certificates

**Contingency**: Parent uses VPN or secure network

---

### 12.4 Content Risks

#### RISK-12: Inappropriate Content Accidentally Added
**Risk**: Parent accidentally adds inappropriate video to accessible library
**Likelihood**: Low-Medium
**Impact**: High (child exposure to inappropriate content)
**Mitigation**:
- Clearly separate kid-friendly libraries in Jellyfin
- Provide library enable/disable toggle (parent can quickly disable library if mistake made)
- Implement viewing history so parent can review what child watched
- Document content curation best practices for parent

**Contingency**: Parent immediately disables library, removes video, reviews history

---

#### RISK-13: Content Deletion from Jellyfin
**Risk**: Parent deletes videos from Jellyfin that child had downloaded
**Likelihood**: Medium
**Impact**: Low (confusion when child tries to play deleted video)
**Mitigation**:
- Handle deleted content gracefully (remove from local library on next sync)
- Keep downloaded videos accessible for grace period even if removed from Jellyfin
- Display clear message if video no longer available: "This video is no longer available. Let's find another!"

---

### 12.5 Operational Risks

#### RISK-14: Jellyfin Server Downtime
**Risk**: Parent's Jellyfin server goes offline (NAS failure, network issue)
**Likelihood**: Low-Medium
**Impact**: Medium-High (can't stream or download new content)
**Mitigation**:
- Design offline-first architecture (app fully functional offline)
- Gracefully degrade to offline mode when server unreachable
- Provide clear status indication (offline mode banner)
- Cache sufficient content for extended offline use (1-2 hours minimum)

**Contingency**: Child uses offline content until parent restores server

---

#### RISK-15: App Update Breaks Functionality
**Risk**: Future app update introduces bugs or breaks features
**Likelihood**: Low-Medium (depends on testing rigor)
**Impact**: Medium (app unusable until fixed)
**Mitigation**:
- Thoroughly test updates before deploying to child's tablet
- Maintain previous APK version as backup
- Implement database migrations carefully (test extensively)
- Use versioned releases (easy to rollback)
- Test on dedicated test device before deploying to child's tablet

**Contingency**: Roll back to previous APK version

---

#### RISK-16: Parent Loses PIN
**Risk**: Parent forgets parental PIN and cannot access settings
**Likelihood**: Low
**Impact**: Medium (cannot configure app)
**Mitigation**:
- Provide PIN recovery mechanism:
  - Option 1: Security question(s)
  - Option 2: Recovery email (if parent willing to configure)
  - Option 3: Documented "nuclear option" (clear app data, reconfigure from scratch)
- Document PIN in secure location (password manager)
- Suggest simple, memorable PIN (not overly complex)

**Contingency**: Clear app data and reconfigure (loses viewing history, requires re-download)

---

### 12.6 Risk Priority Matrix

| Risk ID | Risk Name | Likelihood | Impact | Priority | Mitigation Urgency |
|---------|-----------|------------|--------|----------|-------------------|
| RISK-6 | Interface too complex | Medium | High | **HIGH** | Must address in MVP |
| RISK-12 | Inappropriate content added | Low-Medium | High | **HIGH** | Must address in MVP |
| RISK-1 | Jellyfin API changes | Low-Medium | High | **MEDIUM** | Address in MVP |
| RISK-3 | Network reliability | Medium | Medium-High | **MEDIUM** | Address in MVP |
| RISK-14 | Server downtime | Low-Medium | Medium-High | **MEDIUM** | Address in MVP |
| RISK-2 | Video format issues | Medium | Medium | **MEDIUM** | Address in MVP |
| RISK-4 | Storage exhaustion | Medium | Medium | **MEDIUM** | Address in MVP |
| RISK-5 | Performance issues | Medium | Medium | **MEDIUM** | Test in MVP |
| RISK-9 | Credential exposure | Low | High | **MEDIUM** | Address in MVP |
| RISK-7 | Accidental settings access | Low | Medium | **LOW** | Address in MVP |
| RISK-8 | Screen time bypass | Low-Medium | Medium | **LOW** | Address if timer implemented |
| RISK-10 | Child bypasses controls | Low | Medium | **LOW** | Address in MVP |
| RISK-11 | MITM attack | Low | Medium | **LOW** | Address in production |
| RISK-13 | Content deletion | Medium | Low | **LOW** | Nice to have |
| RISK-15 | Update breaks app | Low-Medium | Medium | **LOW** | Process-based mitigation |
| RISK-16 | Parent loses PIN | Low | Medium | **LOW** | Documentation |

---

## 13. Success Criteria & KPIs

### 13.1 Primary Success Criteria

#### SUCCESS-1: Content Safety (MUST HAVE)
**Metric**: Zero instances of unapproved content access
**Target**: 100% of viewable content is parent-approved
**Measurement Method**:
- Code review of content filtering logic
- Manual testing: attempt to access non-enabled libraries
- Parent verification after 1 week of use

**Definition of Success**: Child can only view videos from parent-selected Jellyfin libraries

---

#### SUCCESS-2: Independent Operation (MUST HAVE)
**Metric**: Child can select and play videos without adult assistance
**Target**: 90%+ success rate after 1 demonstration
**Measurement Method**:
- Observational testing: Watch child use app after single demo
- Parent survey: "Can your child use the app independently?"

**Definition of Success**: 5-year-old can browse grid, tap video, and watch without help in majority of sessions

---

#### SUCCESS-3: Offline Functionality (MUST HAVE)
**Metric**: Offline playback success rate
**Target**: 100% playback success for downloaded content in offline mode
**Measurement Method**:
- Automated testing in airplane mode
- Real-world testing: Use app during travel/no connectivity
- Download 10 videos, disconnect WiFi, attempt to play all 10

**Definition of Success**: All downloaded videos play successfully when device is offline

---

#### SUCCESS-4: Parental Control Integrity (MUST HAVE)
**Metric**: Settings access protection
**Target**: 100% - Settings cannot be accessed without PIN
**Measurement Method**:
- Automated testing: Attempt settings access without PIN
- Manual testing: Child attempts to access settings

**Definition of Success**: All settings and administrative functions require PIN; no bypass methods exist

---

### 13.2 Secondary Success Criteria

#### SUCCESS-5: User Satisfaction (SHOULD HAVE)
**Metric**: Parent satisfaction with app
**Target**: Parent rates app 4/5 or higher for "meets needs"
**Measurement Method**: Parent self-assessment after 2 weeks of use

**Survey Questions**:
- "The app provides a safe viewing environment for my child" (1-5 scale)
- "My child can use the app without my help" (1-5 scale)
- "Offline viewing works reliably" (1-5 scale)
- "I feel confident letting my child use this app unsupervised" (1-5 scale)
- "Overall, this app meets my needs" (1-5 scale)

---

#### SUCCESS-6: Performance (SHOULD HAVE)
**Metrics**:
- App startup time: < 3 seconds from launch to content grid
- Video start time: < 2 seconds (streaming), < 1 second (offline)
- UI responsiveness: < 300ms touch-to-response

**Measurement Method**: Automated performance profiling, manual stopwatch testing

**Definition of Success**: All performance targets met on target device (parent's child's tablet)

---

#### SUCCESS-7: Stability (SHOULD HAVE)
**Metric**: Crash rate
**Target**: < 1% of sessions result in crash
**Measurement Method**: Firebase Crashlytics (if implemented) or parent observation over 2 weeks

**Definition of Success**: Child can use app for multiple sessions without encountering crashes

---

#### SUCCESS-8: Auto-Download Effectiveness (SHOULD HAVE)
**Metric**: Availability of fresh offline content
**Target**: Child always has unwatched content available offline
**Measurement Method**: Parent verification after 1 week of use

**Survey Question**: "Does your child regularly have new unwatched videos available offline?"

---

### 13.3 Key Performance Indicators (KPIs)

#### KPI-1: Usage Frequency
**Metric**: Sessions per week
**Target**: 7+ sessions per week (daily use)
**Data Source**: App analytics (local logging, not transmitted)
**Interpretation**: High usage indicates app is meeting child's entertainment needs

---

#### KPI-2: Viewing Duration
**Metric**: Average minutes watched per session
**Target**: 15-30 minutes (healthy, age-appropriate sessions)
**Data Source**: Viewing history tracking
**Interpretation**: Consistent with recommended screen time for 5-year-olds

---

#### KPI-3: Offline Usage Ratio
**Metric**: Percentage of viewing sessions that are offline
**Target**: 30%+ of sessions offline
**Data Source**: Viewing history (offline flag)
**Interpretation**: High offline usage validates offline-first design decision

---

#### KPI-4: Autoplay Engagement
**Metric**: Average videos watched per session
**Target**: 3+ videos per session (autoplay keeping child engaged)
**Data Source**: Viewing history
**Interpretation**: Autoplay feature is working as intended

---

#### KPI-5: Download Success Rate
**Metric**: Percentage of queued downloads that complete successfully
**Target**: 95%+ completion rate
**Data Source**: Download queue status
**Interpretation**: Download system is reliable

---

#### KPI-6: Settings Access Frequency
**Metric**: Number of times parent accesses settings per week
**Target**: < 2 times per week (after initial setup)
**Data Source**: Settings access logs
**Interpretation**: Low frequency indicates "set it and forget it" success; high frequency may indicate configuration problems

---

### 13.4 Acceptance Testing Checklist

Before declaring MVP complete, verify:

**Functional Acceptance**:
- [ ] Can connect to Jellyfin server and authenticate successfully
- [ ] Can browse video library with thumbnails and titles
- [ ] Can play video in full-screen mode
- [ ] Autoplay advances to next video automatically
- [ ] Can pause, play, and skip videos
- [ ] Can exit playback and return to browsing
- [ ] Downloads occur automatically in background when on WiFi + charging
- [ ] Downloaded videos play offline without internet
- [ ] App switches to offline mode automatically when network lost
- [ ] Offline indicator displays correctly
- [ ] PIN protection prevents settings access
- [ ] Can change Jellyfin server connection in settings
- [ ] Can enable/disable content libraries
- [ ] Can manually trigger sync
- [ ] Can view and clear viewing history
- [ ] Screen time timer (if implemented) enforces limits correctly

**Non-Functional Acceptance**:
- [ ] App starts within 3 seconds
- [ ] Videos start within 2 seconds (streaming)
- [ ] UI responds to touch within 300ms
- [ ] No crashes during 1-hour continuous use
- [ ] Works correctly in airplane mode
- [ ] Survives app restart without losing state
- [ ] Credentials stored encrypted (verified via security audit)
- [ ] HTTPS enforced (verified via network inspection)
- [ ] No third-party data transmission (verified via network inspection)

**Usability Acceptance**:
- [ ] 5-year-old can select and play video without help
- [ ] Error messages are child-friendly
- [ ] Thumbnails are large and easy to tap
- [ ] Text is readable at child's eye level
- [ ] App is visually appealing to child
- [ ] Parent can configure all settings easily

---

### 13.5 Go-Live Criteria

The app is ready for production use (on child's tablet) when:

1. **All MUST HAVE functional requirements implemented and tested**
2. **All HIGH priority risks mitigated**
3. **Primary success criteria met** (content safety, independent operation, offline functionality, parental controls)
4. **Acceptance testing checklist 100% complete**
5. **Parent has completed setup and configuration successfully**
6. **Child has successfully used app for at least 3 supervised sessions without major issues**
7. **Parent feels confident in app's safety and reliability**

**Post-Launch Review**: After 2 weeks of production use, conduct retrospective to assess:
- What's working well?
- What needs improvement?
- Any unexpected issues?
- Priority for next iteration?

---

## 14. Future Considerations

The following features are out of scope for the MVP but may be considered for future releases based on parent's needs and feedback from initial use.

### 14.1 Enhancements for Future Versions

#### FUTURE-1: Multiple Child Profiles
**Description**: Support multiple children using the same app with separate content libraries, viewing history, and screen time limits

**Benefits**:
- Useful if family has multiple children
- Age-appropriate content per child
- Independent screen time tracking

**Complexity**: Medium
**Dependencies**: User authentication system, profile switching UI

**Priority**: Consider if second child begins using tablet

---

#### FUTURE-2: Parental Companion Mobile App
**Description**: Android/iOS app for parents to remotely monitor viewing activity, adjust settings, and manage content

**Features**:
- View current watch status
- See viewing history remotely
- Grant screen time extensions remotely
- Add/remove content libraries
- Get notifications (e.g., screen time limit reached)

**Benefits**: Convenience for parents, don't need to use child's tablet to manage

**Complexity**: High
**Dependencies**: Backend service for sync, push notification infrastructure

**Priority**: Nice to have, but significant scope increase

---

#### FUTURE-3: Subtitle/Closed Caption Support
**Description**: Display subtitles/captions during video playback

**Benefits**:
- Supports hearing-impaired children
- Helps with reading development
- Useful for second-language content

**Complexity**: Low-Medium
**Dependencies**: Jellyfin subtitle support, ExoPlayer subtitle rendering

**Priority**: Consider if specific need arises (e.g., child has hearing difficulty)

---

#### FUTURE-4: Manual Download Selection
**Description**: Allow parent to manually select specific videos to download, rather than automatic selection

**Benefits**:
- More control over offline content
- Can ensure favorite videos always available
- Can download content before specific trip

**Complexity**: Low
**Dependencies**: UI for manual selection (checkboxes in grid view)

**Priority**: SHOULD HAVE for post-MVP; relatively easy enhancement

---

#### FUTURE-5: Content Recommendations
**Description**: Suggest videos child might like based on viewing history

**Benefits**:
- Helps child discover new content
- Mimics familiar YouTube Kids experience

**Complexity**: Medium
**Dependencies**: Viewing history analysis, recommendation algorithm (collaborative filtering or content-based)

**Concerns**: May encourage more screen time than intended

**Priority**: Consider carefully; may conflict with parent's control philosophy

---

#### FUTURE-6: Educational Content Tracking
**Description**: Tag and track educational vs. entertainment content; provide reports to parent

**Benefits**:
- Helps parent ensure balanced content consumption
- Gamification potential (earn entertainment time by watching educational content)

**Complexity**: Medium
**Dependencies**: Content tagging system, reporting UI

**Priority**: COULD HAVE; appeals to education-focused parents

---

#### FUTURE-7: Chromecast / TV Casting Support
**Description**: Cast videos to TV via Chromecast or other casting protocols

**Benefits**:
- Larger screen viewing
- Better family viewing experience
- Reduces tablet screen time (watches on TV instead)

**Complexity**: Medium
**Dependencies**: Google Cast SDK integration, ExoPlayer casting support

**Priority**: SHOULD HAVE for post-MVP; common user request

---

#### FUTURE-8: Gesture-Based Child Lock
**Description**: Require specific gesture (e.g., draw shape) to exit app, preventing accidental home button presses

**Benefits**:
- Reduces accidental app exits
- Simple for child to perform when intended

**Complexity**: Low
**Dependencies**: Custom gesture detection

**Priority**: COULD HAVE; nice polish feature

---

#### FUTURE-9: Bedtime Mode
**Description**: Automatically enter special "bedtime" mode with calming content and automatic shutdown after set time

**Features**:
- Select bedtime-specific playlist (lullabies, calm stories)
- Dim screen gradually
- Auto-pause after specified duration
- Parent-configurable bedtime schedule

**Benefits**: Supports bedtime routines

**Complexity**: Medium
**Dependencies**: Scheduled tasks, special UI mode

**Priority**: COULD HAVE; niche but potentially delightful feature

---

#### FUTURE-10: Offline Voice Search
**Description**: Voice-activated search for videos (offline, using on-device speech recognition)

**Benefits**:
- Accessibility for pre-readers
- Faster content discovery
- Fun, interactive feature

**Complexity**: High
**Dependencies**: Android Speech Recognition API, natural language processing for 5-year-old speech

**Concerns**: Requires microphone permission (privacy concern)

**Priority**: COULD HAVE; interesting but complex

---

#### FUTURE-11: Reward System for Screen Time Management
**Description**: Gamification of screen time limits with rewards for staying within limits

**Examples**:
- Earn "stars" for stopping when timer ends
- Unlock special avatar accessories
- Visual progress tracker

**Benefits**: Positive reinforcement for healthy screen time habits

**Complexity**: Medium
**Dependencies**: Gamification logic, reward UI

**Priority**: COULD HAVE; may align with parenting philosophy

---

#### FUTURE-12: Playback Speed Control (Parent Only)
**Description**: Allow parent to set playback speed (0.5x - 2x) for educational content

**Benefits**:
- Slow down fast-paced content for comprehension
- Speed up slower content to maintain engagement

**Complexity**: Low
**Dependencies**: ExoPlayer playback speed API

**Priority**: COULD HAVE; niche use case

---

#### FUTURE-13: Watch Together Mode (Video Chat Integration)
**Description**: Allow child to watch videos synchronized with remote grandparents/family via video chat

**Benefits**: Virtual shared viewing experiences with distant family

**Complexity**: Very High
**Dependencies**: Video chat SDK, synchronization protocol, signaling server

**Priority**: WON'T HAVE (too complex for personal project)

---

#### FUTURE-14: Custom Themes / Avatar Customization
**Description**: Let child personalize app appearance (color schemes, avatar, etc.)

**Benefits**:
- Ownership and engagement
- Fun personalization

**Complexity**: Medium
**Dependencies**: Theme system, avatar selection UI

**Priority**: COULD HAVE; nice polish feature

---

### 14.2 Technical Debt and Improvements

#### TECH-FUTURE-1: Analytics and Monitoring
**Description**: Implement privacy-respecting local analytics for troubleshooting and usage insights

**Recommendation**: Use local logging only; no external transmission unless parent explicitly enables crash reporting

---

#### TECH-FUTURE-2: Automated Testing Suite
**Description**: Expand test coverage to 80%+ with unit, integration, and UI tests

**Benefits**: Confidence in future changes, regression prevention

---

#### TECH-FUTURE-3: Continuous Integration/Deployment
**Description**: Set up CI/CD pipeline for automated builds and testing

**Benefits**: Faster iteration, consistent builds

**Tools**: GitHub Actions, GitLab CI, or Jenkins

---

#### TECH-FUTURE-4: Accessibility Enhancements
**Description**: Full TalkBack support, switch control support, colorblind-friendly color schemes

**Benefits**: Inclusive design for children with disabilities

---

#### TECH-FUTURE-5: Performance Optimization Round 2
**Description**: Advanced optimizations (lazy loading, code splitting, prefetching)

**Benefits**: Supports larger libraries, older devices

---

### 14.3 Feature Prioritization Framework

When evaluating future features, consider:

1. **Alignment with Core Goals**: Does it enhance content safety, ease of use, or offline capability?
2. **Complexity vs. Value**: Is the benefit worth the development effort?
3. **Maintenance Burden**: Will this feature require ongoing maintenance?
4. **Privacy Impact**: Does it require new permissions or data collection?
5. **Parental Control**: Does it maintain or enhance parental control?

**Prioritization Quadrants**:

```
High Value, Low Complexity        High Value, High Complexity
- Manual download selection       - Multiple child profiles
- Chromecast support              - Parental companion app
- Subtitle support                - Content recommendations

Low Value, Low Complexity         Low Value, High Complexity
- Custom themes                   - Watch together mode
- Gesture lock                    - Voice search
```

**Recommendation**: Focus on High Value, Low Complexity features first for post-MVP iterations.

---

## Appendices

### Appendix A: Glossary of Terms

| Term | Definition |
|------|------------|
| **Autoplay** | Automatic advancement to the next video when the current video ends |
| **Jellyfin** | Open-source media server software that organizes and streams personal media collections |
| **Kiosk Mode** | A locked-down mode that restricts device to a single application |
| **NAS** | Network Attached Storage; a dedicated file storage device accessible over a network |
| **Offline Mode** | Application state where content is accessed from local storage without internet connection |
| **Parental Controls** | Features that allow parents to restrict and monitor child's usage |
| **PIN** | Personal Identification Number; numeric password for accessing parental settings |
| **Screen Time** | Duration of time spent actively using the application |
| **Sideloading** | Installing an application directly from an APK file rather than an app store |
| **Thumbnail** | Small preview image representing a video |

### Appendix B: Reference Documents

- **Jellyfin API Documentation**: https://api.jellyfin.org/
- **Android Developer Documentation**: https://developer.android.com/
- **ExoPlayer Documentation**: https://exoplayer.dev/
- **Material Design Guidelines**: https://material.io/design
- **Android Accessibility Guidelines**: https://developer.android.com/guide/topics/ui/accessibility
- **COPPA Compliance**: https://www.ftc.gov/enforcement/rules/rulemaking-regulatory-reform-proceedings/childrens-online-privacy-protection-rule

### Appendix C: Revision History

| Version | Date | Author | Summary of Changes |
|---------|------|--------|-------------------|
| 1.0 | 2025-12-11 | Business Analyst | Initial SRS document creation |

---

## Document Approval

This document requires review and approval before implementation begins.

| Role | Name | Signature | Date |
|------|------|-----------|------|
| **Project Sponsor (Parent)** | [Parent Name] | _______________ | __________ |
| **Business Analyst** | [Your Name] | _______________ | 2025-12-11 |
| **Developer** | [Parent Name] | _______________ | __________ |

---

**End of Software Requirements Specification**

---

## Next Steps

After SRS approval:

1. **Review and Refine**: Parent reviews document, provides feedback, clarifies ambiguities
2. **Prioritization**: Confirm MVP scope vs. future enhancements
3. **Technical Design**: Create technical architecture document (system design, database schema, API contracts)
4. **UI/UX Design**: Create mockups and wireframes (low-fidelity → high-fidelity)
5. **Development Environment Setup**: Configure Android Studio, dependencies, Jellyfin test server
6. **Sprint Planning**: Break down MVP into development sprints (if using agile methodology)
7. **Implementation**: Begin coding!

**Recommendation**: Start with a "walking skeleton" - minimal end-to-end implementation (connect to Jellyfin, display one video, play it) to validate architecture and identify early technical challenges.

---

*This SRS document is a living document and may be updated as requirements evolve during development.*