# Step-by-step build sequence

This explains the exact implementation sequence used for the app, so you can follow every minor change from scratch to a running release build.

## 1. Create the Android project foundation

Created:

- `settings.gradle.kts`
- root `build.gradle.kts`
- `gradle.properties`
- `app/build.gradle.kts`
- `.gitignore`

Why:

- Defines one Android app module
- Enables Kotlin, Compose, and KSP for Room code generation
- Adds dependencies for Compose, Room, DataStore, and AdMob

## 2. Add Android app metadata

Created:

- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/themes.xml`
- launcher icon resources

Why:

- Declares app entry point
- Adds AdMob app ID
- Adds permissions for internet, network, notifications, and boot completed
- Registers reminder receivers

## 3. Create the application class

Created:

- `DailyChecksApplication.kt`

Why:

- Initializes Mobile Ads SDK
- Creates notification channel
- Exposes database, repository, reminder scheduler, and DataStore objects

## 4. Create local storage

Created:

- `ChecklistItemEntity.kt`
- `ChecklistItemDao.kt`
- `AppDatabase.kt`

Why:

- Stores checklist items on the device
- Persists title, timestamp, checked date, and streak count
- Keeps data after app closes

## 5. Create reminder settings storage

Created:

- `ReminderPreferences.kt`

Why:

- Stores one shared daily reminder time
- Stores reminder enabled/disabled state
- Uses DataStore so settings survive restarts

## 6. Implement streak logic

Created:

- `StreakCalculator.kt`
- `StreakCalculatorTest.kt`

Why:

- If user checks first time: streak becomes 1
- If user checks again same day: streak does not increase
- If user checks on consecutive day: streak increments
- If a day is missed: streak resets to 1

## 7. Build repository logic

Created:

- `ChecklistRepository.kt`

Why:

- Seeds default items on first launch
- Adds custom item
- Marks item checked
- Applies streak rules
- Updates reminder schedule when settings change

## 8. Build reminder engine

Created:

- `ReminderScheduler.kt`
- `NotificationHelper.kt`
- `ReminderReceiver.kt`
- `BootReceiver.kt`

Why:

- Schedules one daily repeating alarm
- Shows notification when reminder fires
- Restores reminder after reboot or app update

## 9. Add AdMob monetization

Created:

- `InterstitialAdController.kt`

Why:

- Loads interstitial ads
- Shows interstitial every 4 monetized actions
- Keeps a banner visible on the main screen

## 10. Build the Compose UI

Created:

- `MainActivity.kt`
- `MainViewModel.kt`
- `AppScreen.kt`
- theme files

Why:

- Shows checklist items
- Displays last checked time and streak
- Lets user tap an item to check it
- Lets user add custom item
- Lets user turn reminder on/off and pick a time
- Shows AdMob banner

## 11. Run and export

In Android Studio:

1. Sync Gradle.
2. Run on emulator or device.
3. Replace test AdMob IDs with production IDs for release.
4. Build APK for testing.
5. Build signed AAB for Play Store upload.

## Final note

This codebase is complete for development and app logic. APK and AAB generation require Android Studio/Gradle sync with an installed Android SDK on the machine where you build the app.
