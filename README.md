# Siri-like Assistant (Kotlin)

This project is a minimal single-activity Android app demonstrating speech-to-text (SpeechRecognizer)
and text-to-speech (TextToSpeech). It is ready to import into Android Studio and build an APK.

## How to build an APK

1. Open Android Studio.
2. Choose "Open" and select the folder you downloaded (SiriLikeAssistant).
3. Let Gradle sync. Install/update any SDK components if prompted.
4. Build > Build Bundle(s) / APK(s) > Build APK(s).
5. The generated APK will be in `app/build/outputs/apk/debug/`.

Or from command line (if you have Gradle wrapper installed):

./gradlew assembleDebug

## Notes
- Run on a real device for microphone and STT support.
- Accept microphone permission when prompted.
- For production release, configure signingConfigs and release buildType.
