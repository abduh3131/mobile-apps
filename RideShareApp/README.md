# RideShareApp

RideShareApp is an Android blueprint for a ride-hailing experience inspired by Uber. It demonstrates a modular architecture that combines authentication, mapping, real-time ride tracking, AI-assisted safety features, and multi-channel communications.

## Features
- **User Interface**: Splash screen, authentication, map dashboard, ride request flows, live tracking, trip summary, user profile, and dedicated support/emergency view.
- **Authentication**: Firebase Authentication (email/password and Google sign-in) with extension points for driver verification via AI analysis of uploaded vehicle imagery.
- **Realtime Data**: Cloud Firestore-ready repositories for user, driver, trip, payment, and rating data.
- **Maps & Location**: Google Maps SDK integration for pickup/drop-off selection, rerouting, and GPS driven live driver tracking.
- **Sensors & AI**: Hooks for GPS, compass, accelerometer, gyroscope, camera, and microphone to power intelligent arrival estimation, unsafe driving detection, QR or license plate scanning, and natural-language ride booking.
- **Media Channels**: Chat, voice, video, and image sharing placeholders ready to connect to a provider like Twilio.

## Project Structure
```
app/
├── build.gradle        # Application module configuration and dependencies
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/example/rideshare/
    │   ├── data/        # Firestore-ready repositories & DTO models
    │   ├── domain/      # Use cases (business logic)
    │   ├── ui/          # Activities, Fragments, and ViewModels per feature
    │   └── util/        # Shared constants/utilities
    └── res/             # XML resources (layouts, strings, navigation)
```

## Getting Started
1. **Open in Android Studio** (Giraffe+ recommended).
2. When prompted, let Android Studio install/update the required **Android Gradle Plugin**, **Kotlin**, and **Google Play services** dependencies.
3. Add your Firebase `google-services.json` under `app/` and configure Firebase project settings.
4. Enable the Maps SDK, Places API, and Directions API in the Google Cloud console. Supply your API key via the `local.properties` file (`MAPS_API_KEY=...`) or secure secrets manager.
5. Provide credentials for chat/voice/video providers (e.g., Twilio) and update `SupportRepository` with initialization logic.
6. Implement AI verification services by connecting `AuthRepository` and `RideRepository` to Cloud Functions, Vertex AI, or custom inference endpoints.

## Key Components
- **Navigation Graph (`res/navigation/nav_graph.xml`)** orchestrates transitions between onboarding, ride flows, and post-trip experiences.
- **ViewModels** encapsulate UI state and interact with repositories to simplify unit testing and state restoration.
- **Repositories** isolate Firebase, REST, and realtime communication logic from UI layers.
- **Use Cases** capture domain behavior such as streaming nearby drivers, performing ride requests, and analyzing sensor inputs.

## Extending the Blueprint
- Replace mock repositories with Firestore/Realtime Database implementations.
- Wire up Google Sign-In and driver onboarding flows with document uploads, AI verification, and admin approval.
- Connect ML Kit models to enable QR/license scanning and unsafe driving detection via device sensors.
- Integrate WebRTC/Twilio for bi-directional voice/video calling and encrypted messaging.
- Add WorkManager jobs for background location syncing, ride history archiving, and payment reconciliation.

## Testing
- Add instrumented UI tests under `app/src/androidTest/java` for navigation and Firebase flows.
- Add unit tests under `app/src/test/java` to validate ViewModel and repository logic with mocks/fakes.

## License
This project is provided as a scaffold. Integrate production-grade authentication, storage, and safety measures before deploying to end users.
