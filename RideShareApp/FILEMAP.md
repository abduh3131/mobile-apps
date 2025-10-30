# RideShareApp File Map

```
RideShareApp/
├── build.gradle
├── gradle.properties
├── settings.gradle
├── FILEMAP.md
├── README.md
└── app/
    ├── build.gradle
    ├── proguard-rules.pro
    └── src/
        └── main/
            ├── AndroidManifest.xml
            ├── java/com/example/rideshare/
            │   ├── data/
            │   │   ├── model/
            │   │   │   ├── Driver.kt
            │   │   │   ├── Trip.kt
            │   │   │   └── Vehicle.kt
            │   │   └── repository/
            │   │       ├── AuthRepository.kt
            │   │       ├── RideRepository.kt
            │   │       ├── SupportRepository.kt
            │   │       └── UserRepository.kt
            │   ├── domain/usecase/
            │   │   └── ObserveNearbyDriversUseCase.kt
            │   ├── ui/
            │   │   ├── MainActivity.kt
            │   │   ├── auth/
            │   │   │   ├── AuthFragment.kt
            │   │   │   ├── AuthState.kt
            │   │   │   └── AuthViewModel.kt
            │   │   ├── dashboard/
            │   │   │   ├── DashboardFragment.kt
            │   │   │   ├── DashboardViewModel.kt
            │   │   │   └── NearbyDriverState.kt
            │   │   ├── profile/
            │   │   │   ├── ProfileFragment.kt
            │   │   │   └── ProfileViewModel.kt
            │   │   ├── ride/
            │   │   │   ├── RideRequestFragment.kt
            │   │   │   ├── RideViewModel.kt
            │   │   │   ├── TrackingFragment.kt
            │   │   │   ├── TripState.kt
            │   │   │   └── TripSummaryFragment.kt
            │   │   ├── splash/
            │   │   │   ├── SplashFragment.kt
            │   │   │   └── SplashViewModel.kt
            │   │   └── support/
            │   │       ├── SupportFragment.kt
            │   │       └── SupportViewModel.kt
            │   └── util/
            │       └── Constants.kt
            └── res/
                ├── layout/
                │   ├── activity_main.xml
                │   ├── fragment_auth.xml
                │   ├── fragment_dashboard.xml
                │   ├── fragment_profile.xml
                │   ├── fragment_ride_request.xml
                │   ├── fragment_splash.xml
                │   ├── fragment_support.xml
                │   ├── fragment_tracking.xml
                │   └── fragment_trip_summary.xml
                ├── navigation/nav_graph.xml
                ├── values-night/themes.xml
                └── values/
                    ├── colors.xml
                    ├── strings.xml
                    └── themes.xml
```
