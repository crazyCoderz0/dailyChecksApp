# Daily Checks Android App

This workspace now contains a complete Kotlin Jetpack Compose Android app for tracking daily checks:

- Default items: `Lock Door`, `Turn Off Stove`, `Close Garage`
- Tap any item to mark it checked
- Streak logic: one check per day counts
- Add custom items
- One daily reminder for all items
- Local-only storage
- AdMob banner on the main screen
- Interstitial ad every 4 monetized actions

## Project structure

- `app/src/main/java/com/utility/dailychecks`:
  - `data/local`: Room database and DAO
  - `data/preferences`: DataStore reminder settings
  - `data/repository`: App business logic
  - `domain`: streak rules
  - `reminder`: notification channel, receiver, alarm scheduling
  - `ads`: interstitial ad loader/show helper
  - `ui`: Compose screens and `MainViewModel`
- `app/src/test`: streak logic unit tests

## Important AdMob note

The project is wired with Google's official **test ad IDs** so ads can load safely during development:

- App ID: test
- Banner ID: test
- Interstitial ID: test

Before Play Store release, replace the values in:

- `app/src/main/res/values/strings.xml`

with your real AdMob app ID and ad unit IDs.

## Build steps in Android Studio

1. Open this folder in Android Studio.
2. Let Gradle sync and install any missing Android SDK components.
3. If prompted, trust the Gradle files.
4. Run the app on a device or emulator with Google Play services.
5. Test:
   - tap each default item
   - close and reopen the app
   - add a custom item
   - enable reminder and set a time
   - watch banner ad loading
   - trigger interstitial after a few actions
6. Generate deliverables from Android Studio:
   - `Build > Build Bundle(s) / APK(s) > Build APK(s)`
   - `Build > Generate Signed Bundle / APK`

## Release checklist

1. Replace test AdMob IDs with production IDs.
2. Create a signing key.
3. Build a signed AAB for Play Console upload.
4. Verify notifications on a physical device.
5. Confirm no debug suffix is included in release output.

