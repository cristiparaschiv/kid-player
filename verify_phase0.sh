#!/bin/bash

# Phase 0 Verification Script
# Checks that all required files and directories are in place

echo "======================================"
echo "Kid Player - Phase 0 Verification"
echo "======================================"
echo ""

ERRORS=0
WARNINGS=0

# Function to check file exists
check_file() {
    if [ -f "$1" ]; then
        echo "✓ $1"
    else
        echo "✗ MISSING: $1"
        ((ERRORS++))
    fi
}

# Function to check directory exists
check_dir() {
    if [ -d "$1" ]; then
        echo "✓ $1/"
    else
        echo "✗ MISSING: $1/"
        ((ERRORS++))
    fi
}

echo "Checking Gradle Configuration Files..."
check_file "settings.gradle.kts"
check_file "build.gradle.kts"
check_file "gradle.properties"
check_file "gradle/libs.versions.toml"
check_file "app/build.gradle.kts"
check_file "app/proguard-rules.pro"
echo ""

echo "Checking Android Configuration..."
check_file "app/src/main/AndroidManifest.xml"
check_file "app/src/main/res/xml/network_security_config.xml"
check_file "app/src/main/res/values/strings.xml"
check_file "app/src/main/res/values/themes.xml"
echo ""

echo "Checking Application Class and DI Modules..."
check_file "app/src/main/java/com/kidplayer/app/KidPlayerApplication.kt"
check_file "app/src/main/java/com/kidplayer/app/di/AppModule.kt"
check_file "app/src/main/java/com/kidplayer/app/di/DatabaseModule.kt"
check_file "app/src/main/java/com/kidplayer/app/di/NetworkModule.kt"
echo ""

echo "Checking Theme Files..."
check_file "app/src/main/java/com/kidplayer/app/presentation/theme/Color.kt"
check_file "app/src/main/java/com/kidplayer/app/presentation/theme/Type.kt"
check_file "app/src/main/java/com/kidplayer/app/presentation/theme/Theme.kt"
echo ""

echo "Checking Navigation Files..."
check_file "app/src/main/java/com/kidplayer/app/presentation/navigation/Screen.kt"
check_file "app/src/main/java/com/kidplayer/app/presentation/navigation/NavGraph.kt"
echo ""

echo "Checking Screen Files..."
check_file "app/src/main/java/com/kidplayer/app/presentation/MainActivity.kt"
check_file "app/src/main/java/com/kidplayer/app/presentation/onboarding/SetupScreen.kt"
check_file "app/src/main/java/com/kidplayer/app/presentation/browse/BrowseScreen.kt"
check_file "app/src/main/java/com/kidplayer/app/presentation/player/PlayerScreen.kt"
check_file "app/src/main/java/com/kidplayer/app/presentation/settings/SettingsScreen.kt"
echo ""

echo "Checking Directory Structure for Future Phases..."
check_dir "app/src/main/java/com/kidplayer/app/data/local"
check_dir "app/src/main/java/com/kidplayer/app/data/remote"
check_dir "app/src/main/java/com/kidplayer/app/data/remote/dto"
check_dir "app/src/main/java/com/kidplayer/app/data/remote/mapper"
check_dir "app/src/main/java/com/kidplayer/app/data/repository"
check_dir "app/src/main/java/com/kidplayer/app/data/worker"
check_dir "app/src/main/java/com/kidplayer/app/domain/model"
check_dir "app/src/main/java/com/kidplayer/app/domain/repository"
check_dir "app/src/main/java/com/kidplayer/app/domain/usecase"
echo ""

echo "Checking Documentation..."
check_file "README.md"
check_file ".gitignore"
check_file "PHASE_0_COMPLETE.md"
echo ""

echo "======================================"
echo "Verification Summary"
echo "======================================"
echo "Errors: $ERRORS"
echo "Warnings: $WARNINGS"
echo ""

if [ $ERRORS -eq 0 ]; then
    echo "✓ Phase 0 verification PASSED!"
    echo "The project is ready to build and run."
    echo ""
    echo "Next steps:"
    echo "1. Open project in Android Studio"
    echo "2. Sync Gradle files"
    echo "3. Run on tablet emulator (API 30+)"
    exit 0
else
    echo "✗ Phase 0 verification FAILED!"
    echo "Please review missing files/directories above."
    exit 1
fi
