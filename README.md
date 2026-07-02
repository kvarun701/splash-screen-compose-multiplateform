This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

---

## Kotlin Multiplatform: `expect` and `actual` Keywords

In a Kotlin Multiplatform (KMP) project like this one, we write shared business logic in `commonMain`, but we frequently need to access platform-specific APIs (such as iOS `UIKit` or Android SDK tools). Kotlin provides the `expect` and `actual` keywords as the primary mechanism to accomplish this.

### 1. What are `expect` and `actual`?

*   **`expect` (declared in `commonMain`):**
    You use the `expect` keyword to define a contract. It tells the Kotlin compiler: *"Here is a function, class, interface, or property that the common code needs, but the platform-specific directories will provide the actual implementation."*
*   **`actual` (implemented in platform-specific modules like `androidMain`, `iosMain`):**
    For every `expect` declaration in `commonMain`, each platform module must provide a matching implementation marked with the `actual` keyword. If a platform is missing its `actual` implementation, or if the signatures do not match exactly, the compiler will raise a compilation error.

### 2. Why do we use them?

*   **Platform-Specific APIs:** Common Kotlin code compiles to a subset of Kotlin that doesn't have access to platform-specific SDKs. For example, common code cannot call Android's `android.os.Build` or iOS's `platform.UIKit.UIDevice` directly. Using `expect` / `actual` bridges this gap.
*   **Maximizing Code Shareability:** You can write the majority of your application's logic (view models, business logic, networking, etc.) in `commonMain`, and abstract away only the platform-specific behaviors.
*   **Compile-Time Safety:** Unlike using reflection or dependency injection frameworks where mismatches are only caught at runtime, KMP's compiler strictly validates that every target platform implements the exact contract defined by the `expect` keyword.

### 3. Common Use Cases

*   **System and Device Information:** Getting OS versions, device names, screen sizes, or platform name (as implemented in this project).
*   **File System Access:** Accessing native application directories (e.g., Android's cache directories vs. iOS's `NSDocumentDirectory`).
*   **Secure Storage:** Interacting with native secure storage (e.g., Android's `KeyStore` vs. iOS's `Keychain`).
*   **Local Databases & Shared Preferences:** Initializing databases like SQLDelight or accessing local settings (`SharedPreferences` vs. `NSUserDefaults`).
*   **Utilities & Logging:** Generating UUIDs, formatting dates, or invoking platform logging (e.g., `Log.d` on Android vs. `NSLog` on iOS).

### 4. How it is used in this project

This project uses `expect` and `actual` to determine the current platform name and version.

#### Step A: Declare the `expect` function in `commonMain`
In [Platform.kt](file:///Users/varun/AndroidStudioProjects/splashscreen/shared/src/commonMain/kotlin/com/ganesh/splashscreen/Platform.kt):
```kotlin
package com.ganesh.splashscreen

interface Platform {
    val name: String
}

// Expect fun declaration
expect fun getPlatform(): Platform
```

#### Step B: Implement the `actual` function for Android
In [Platform.android.kt](file:///Users/varun/AndroidStudioProjects/splashscreen/shared/src/androidMain/kotlin/com/ganesh/splashscreen/Platform.android.kt):
```kotlin
package com.ganesh.splashscreen

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

// Actual Android implementation
actual fun getPlatform(): Platform = AndroidPlatform()
```

#### Step C: Implement the `actual` function for iOS
In [Platform.ios.kt](file:///Users/varun/AndroidStudioProjects/splashscreen/shared/src/iosMain/kotlin/com/ganesh/splashscreen/Platform.ios.kt):
```kotlin
package com.ganesh.splashscreen

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

// Actual iOS implementation
actual fun getPlatform(): Platform = IOSPlatform()
```

---

## Navigation 3 in Jetpack Compose & Compose Multiplatform

Navigation 3 represents a complete redesign of navigation for Jetpack Compose and Compose Multiplatform. It shifts away from the traditional, black-box `NavController` system to a fully declarative, state-driven architecture.

### What is New & Key Concepts

#### 1. User-Owned Back Stack
In previous versions of Jetpack Navigation, the library managed the back stack internally behind the scenes. In Navigation 3, the back stack is a standard `SnapshotStateList<T>` (or any Compose state of a list) that you create, observe, and mutate.

*   To navigate forward: `backStack.add(Screen)`
*   To navigate back: `backStack.removeLast()`
*   To clear the stack: `backStack.clear()`

> [!NOTE]
> **What is `SnapshotStateList`?**
> In Jetpack Compose, a standard Kotlin list (such as `mutableListOf()`) does not trigger recomposition when its items are added, removed, or updated. 
> To solve this, Compose provides `SnapshotStateList` (created using `mutableStateListOf()`). It implements the `MutableList` interface but is connected directly to Compose's Snapshot State tracking system. Any changes to the list are automatically tracked, triggering recompositions in any composable (such as `NavDisplay`) that reads from it.

#### 2. Declarative `NavDisplay` and `NavEntry`
Instead of configuring a navigation graph, you use a `NavDisplay` composable. You pass it your back stack and an `entryProvider` lambda. The `entryProvider` receives the current screen key and returns a `NavEntry` containing the screen's composable content.

#### 3. Built for Compose Multiplatform
Navigation 3 is designed from the ground up to be multiplatform-first. Because it avoids reflection-based serialization, it runs smoothly on Android, iOS, Desktop (JVM), and Web (Kotlin/Wasm). All your navigation logic can live directly in `commonMain`.

#### 4. Native Adaptive Layout Support
Because navigation state is represented as a plain list, it is easy to build adaptive layouts (like list-detail split views). Instead of trying to coordinate multiple nav controllers, you can conditionally display one or multiple items from your back stack state side-by-side depending on screen size.

### Minimal Code Example

Here is how Navigation 3 is structured in Compose Multiplatform `commonMain`:

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.NavDisplay
import androidx.navigation3.NavEntry

// 1. Define your screens/destinations using a type-safe interface or sealed class
sealed interface Screen {
    data object Home : Screen
    data class Details(val itemId: String) : Screen
}

@Composable
fun App() {
    // 2. You own and manage the back stack state as a list
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }

    // 3. Define how screen keys map to their UI contents
    val entryProvider: (Screen) -> NavEntry<out Screen> = { screen ->
        NavEntry(key = screen) {
            when (screen) {
                is Screen.Home -> HomeScreen(
                    onNavigateToDetails = { id -> backStack.add(Screen.Details(id)) }
                )
                is Screen.Details -> DetailsScreen(
                    itemId = screen.itemId,
                    onBack = { if (backStack.size > 1) backStack.removeLast() }
                )
            }
        }
    }

    // 4. Render the current top screen
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider
    )
}
```

### How This Code Works (Step-by-Step)

*   **`NavDisplay(...)`**: The core UI component in Navigation 3. It acts as the container that observes your navigation state and handles swapping screens and animations automatically.
*   **`backStack`**: The user-owned list of routes (`SnapshotStateList<Screen>`). Since Compose tracks changes to this list, mutating it (e.g., adding or removing screens) prompts `NavDisplay` to swap to the correct active screen.
*   **`entryProvider`**: A routing lambda function that receives the current screen key from the backstack and returns a `NavEntry`.
*   **`NavEntry(key = screen) { _ -> ... }`**: A container class that associates a route key with its actual Composable UI. The `_` represents the unused context parameter in the trailing Composable lambda block.
*   **`when (screen)` Router**: A type-safe conditional block mapping each route key directly to its Composable screen function (`SplashScreen`, `LoginScreen`, or `HomeScreen`).
*   **Navigation Actions**: Screen event callbacks (like `onSplashFinished` or `onLoginSuccess`) mutate the `backStack` list (e.g., `backStack.clear()` followed by `backStack.add(...)`), which instantly changes the active screen.

### Why Use a `sealed` Interface for Navigation?

Using a `sealed interface` (or `sealed class`) to model your screens provides several critical advantages:

*   **Exhaustive `when` Expressions (Compile-Time Safety)**: Since the compiler knows all possible subclasses of the `sealed` interface, you do not need an `else ->` block when switching between screens. If you add a new screen in the future (e.g., `data object Profile : Screen`), the compiler will instantly fail to build and flag your routing blocks until you explicitly handle the new screen.
*   **Restricted Hierarchy**: Implementations of a `sealed interface` can only be defined within the same package, preventing external or arbitrary classes from being passed into your backstack.
*   **Dynamic Screen Arguments**: Unlike standard Kotlin Enums, subclasses of a `sealed interface` can be `data class`es. This allows you to pass type-safe arguments (e.g., `data class Details(val itemId: String)`) directly between screens without having to parse strings or manage route arguments manually.

### Summary of Differences

| Feature | Navigation 2.x (Traditional) | Navigation 3 (New) |
| :--- | :--- | :--- |
| **Back Stack Owner** | Managed internally by `NavController` | Owned by you (`SnapshotStateList<T>`) |
| **Type Safety** | Safe Args / Serialization setup | Native type-safe Kotlin classes or objects |
| **UI Integration** | Imperative `navController.navigate()` | Declarative updates to the list state |
| **Multiplatform** | Primarily Android-focused with wrapper support | Multiplatform-first (Kotlin/Native & Kotlin/Wasm compatible) |

---

## Local Storage & Preferences with `compose-pref`

This project integrates the `compose-pref` library to achieve local key-value storage and persistence across multiple target platforms (Android and iOS). It persists configuration data (like the logged-in user's name) across application launches.

### 1. Library Integration and Dependency Configuration
The `compose-pref` library is defined in the shared module's build configuration. To ensure the platform-specific modules (like `androidApp`) can resolve the storage types transitively, it is added as an `api` dependency in [shared/build.gradle.kts](file:///Users/varun/AndroidStudioProjects/splashscreen/shared/build.gradle.kts):

```kotlin
sourceSets {
    commonMain.dependencies {
        // Expose compose-pref transitively
        api("io.github.kvarun701:compose-pref:1.0.0")
    }
}
```

### 2. Platform-Specific Initialization
`KeyValueStorage` is initialized natively on each platform during startup and passed into the shared entry point `App(storage)`:

*   **Android (in [MainActivity.kt](file:///Users/varun/AndroidStudioProjects/splashscreen/androidApp/src/main/kotlin/com/ganesh/splashscreen/MainActivity.kt)):**
    `KeyValueStorageFactory` requires the `applicationContext` to initialize the underlying Android `SharedPreferences`:
    ```kotlin
    val storage = KeyValueStorageFactory(applicationContext).create("app_preferences")
    setContent {
        App(storage = storage)
    }
    ```

*   **iOS (in [MainViewController.kt](file:///Users/varun/AndroidStudioProjects/splashscreen/shared/src/iosMain/kotlin/com/ganesh/splashscreen/MainViewController.kt)):**
    On iOS, no context parameter is needed. It wraps `NSUserDefaults` under the hood:
    ```kotlin
    val storage = KeyValueStorageFactory().create("app_preferences")
    App(storage = storage)
    ```

### 3. Usage & Flow Mechanics

#### Step A: Collecting and Writing to Storage
On the Login Screen ([Login.kt](file:///Users/varun/AndroidStudioProjects/splashscreen/shared/src/commonMain/kotlin/com/ganesh/splashscreen/Login.kt)), the user inputs their username. When they press **Continue**, the `onLoginSuccess(username)` callback is triggered:
```kotlin
Button(
    onClick = { onLoginSuccess(username) },
    ...
)
```

In [App.kt](file:///Users/varun/AndroidStudioProjects/splashscreen/shared/src/commonMain/kotlin/com/ganesh/splashscreen/App.kt), the callback receives the username and stores it inside the preferences database:
```kotlin
Screen.Login -> {
    LoginScreen(
        onLoginSuccess = { username ->
            storage.putString("username", username)
            backStack.clear()
            backStack.add(Screen.Home)
        }
    )
}
```

#### Step B: Reading and Displaying
On the Home Screen ([HomeScreen.kt](file:///Users/varun/AndroidStudioProjects/splashscreen/shared/src/commonMain/kotlin/com/ganesh/splashscreen/HomeScreen.kt)), the stored name is retrieved. We provide a default fallback of `"Eco Explorer"` in case the value is missing or blank:
```kotlin
val username = remember { storage.getString("username", defaultValue = "Eco Explorer") ?: "Eco Explorer" }
val displayName = if (username.isBlank()) "Eco Explorer" else username

HeaderSection(username = displayName)
```

This ensures that the user's name is dynamically read from local storage and displayed in the header upon succeeding through the login flow, and remains persisted even after the app is force closed and reopened.

---

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

---

# Mastering Splash Screens in Android & Jetpack Compose: A Step-by-Step Guide

Welcome to the **Splash Screen Implementation Tutorial**! This guide is designed to teach you how to implement a production-grade, highly aesthetic splash screen system in Android applications using Kotlin, Jetpack Compose, and the official AndroidX Splash Screen API.

---

## 1. Core Concepts: The Anatomy of an App Launch

Before writing code, it is crucial to understand **why** we need a splash screen and **when** it displays.

### The App Startup Phases
When a user taps your app icon, the Android operating system starts the application process. This process goes through three states:
1. **Cold Start**: The app starts from scratch. The system has to create the app process, initialize the Application class, load classes, run static initializers, and inflate the initial UI layout.
2. **Warm Start**: The process exists, but the activity is destroyed and must be recreated (e.g., system killed the activity to reclaim memory).
3. **Hot Start**: The app is already in memory. The system simply brings your activity to the foreground (very fast, no splash screen needed).

### The Blank Screen Problem
During a **Cold Start**, there is a brief delay between the tap and when the app compiles and draws its first Compose frames. If left unconfigured, the system displays a default blank window background (usually plain white or black). This is called the "Blank Screen Flash" and looks unprofessional.

### The Old Way (Anti-Pattern) 🚫
In older tutorials, you might see developers creating a separate `SplashActivity` with a layout file, using a `Handler().postDelayed()` to sleep for 2-3 seconds, and then launching `MainActivity`. 
**Why is this bad practice?**
* **Delays the User**: It forces the user to wait a fixed amount of time *after* the app is already ready, wasting their time.
* **Cold Start Visual Flashing**: It doesn't cover the cold start phase before the `SplashActivity` is created, so the blank screen flash still happens.
* **Performance Overhead**: Launching a separate Activity requires the OS to allocate resources and execute window transitions, which slows down the startup.

### The Modern Way (Two-Phase Architecture) Android standard 🏆
To solve this, we use a two-phase architecture:
* **Phase 1: Native System Splash (AndroidX core-splashscreen)**: Displays a static splash theme instantly on the app startup window using the OS window manager. It runs *while* the JVM/Kotlin process initializes.
* **Phase 2: Animated In-App Landing (Jetpack Compose)**: Once Compose initializes, we run a gorgeous animated landing sequence, fading out the system splash and transitioning into the dashboard.

---

## 2. Phase 1: Native Cold-Start Splash Screen

The native splash screen is configured using the AndroidX Splash Screen API. This ensures it displays *instantly* with zero latency.

### Step 1: Add the Dependency
First, open your dependency definition file `gradle/libs.versions.toml` and declare the library:

```toml
[versions]
androidx-core-splashscreen = "1.0.1"

[libraries]
androidx-core-splashscreen = { module = "androidx.core:core-splashscreen", version.ref = "androidx-core-splashscreen" }
```

Then, add it to your app module's dependencies in `androidApp/build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.androidx.core.splashscreen)
}
```

### Step 2: Create a Vector Logo Asset
We need a high-quality vector icon to show in the center of the startup window. 
Create a vector file at `androidApp/src/main/res/drawable/splash_icon.xml`. Here is an example of an elegant mountain/sun emblem using Android Vector XML path coding:

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="100"
    android:viewportHeight="100">

    <!-- Sun (Warm Golden Orange) -->
    <path
        android:fillColor="#E78F66"
        android:pathData="M50,45 C58.28,45 65,38.28 65,30 C65,21.72 58.28,15 50,15 C41.72,15 35,21.72 35,30 C35,38.28 41.72,45 50,45 Z" />

    <!-- Back Mountain (Sand/Beige) -->
    <path
        android:fillColor="#D9A07D"
        android:pathData="M15,85 L48,42 L80,85 Z" />

    <!-- Front Mountain (Terracotta) -->
    <path
        android:fillColor="#C26D5C"
        android:pathData="M35,85 L68,48 L100,85 Z" />

    <!-- Base Line (Sage/Brown) -->
    <path
        android:fillColor="#5A4D41"
        android:pathData="M0,83 L100,83 L100,87 L0,87 Z" />
</vector>
```

### Step 3: Define the Styles Theme
The system splash screen is drawn by the Android OS before the app processes Kotlin code. Therefore, it is styled in XML resource files. 
Create `androidApp/src/main/res/values/styles.xml` and define your splash theme:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- 1. Extend the parent theme Theme.SplashScreen from the AndroidX library -->
    <style name="Theme.App.Starting" parent="Theme.SplashScreen">
        <!-- 2. Set the window background color (Cream tint) -->
        <item name="windowSplashScreenBackground">#F4EAE1</item>
        
        <!-- 3. Provide the icon drawable -->
        <item name="windowSplashScreenAnimatedIcon">@drawable/splash_icon</item>
        
        <!-- 4. Define the post-splash theme to load once the activity starts -->
        <item name="postSplashScreenTheme">@android:style/Theme.Material.Light.NoActionBar</item>
    </style>
</resources>
```

### Step 4: Configure the AndroidManifest
Update `androidApp/src/main/AndroidManifest.xml` to tell the OS to load this starting theme for our launcher activity (`MainActivity`):

```xml
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:theme="@style/Theme.App.Starting"> <!-- Apply Starting Theme -->
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

### Step 5: Install the Splash Screen in Kotlin
Open `androidApp/src/main/kotlin/com/ganesh/splashscreen/MainActivity.kt`. You need to call `installSplashScreen()` **before** `super.onCreate()` to initialize the splash screen framework:

```kotlin
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install System Splash Screen first!
        installSplashScreen()
        
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        setContent {
            App() // Run Compose shared UI
        }
    }
}
```

---

## 3. Phase 2: In-App Animated Splash Screen (Compose)

Once Compose compiles and initializes, the native system splash screen terminates, and our shared Composable code runs. To make the entrance look premium, we will build a secondary animated splash screen directly in Jetpack Compose.

### Step 1: Set up the Earth-Tone Theme
Create `shared/src/commonMain/kotlin/com/ganesh/splashscreen/Theme.kt`. Defining cohesive color constants helps maintain a high-quality visual style matching your app design:

```kotlin
val WarmCream = Color(0xFFF4EAE1)
val Terracotta = Color(0xFFC26D5C)
val Sand = Color(0xFFD9A07D)
val SunOrange = Color(0xFFE78F66)
val DarkSageBrown = Color(0xFF3C3530)
val SoftBeige = Color(0xFFE8DFD8)

private val EarthyColorScheme = lightColorScheme(
    primary = Terracotta,
    secondary = Sand,
    background = WarmCream,
    surface = SoftBeige,
    onBackground = DarkSageBrown,
    onSurface = DarkSageBrown
)

@Composable
fun TerraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EarthyColorScheme,
        content = content
    )
}
```

### Step 2: Add the Image Asset
Copy your image resource (e.g., `landscape_bg.jpg`) into the shared Compose resources directory: `shared/src/commonMain/composeResources/drawable/`.

Compose Multiplatform automatically generates resource accessors inside the generated package. You can reference this image using `Res.drawable.landscape_bg`!

### Step 3: Implement Staggered Animations & Render the Image
We can stagger animations (starting them with offsets) to make the entrance look premium. We animate the image card scale, alpha, and Y-offset, followed by the typographic logo title fading in:

```kotlin
// 1. The artwork card fades in and scales up smoothly
val cardProgress by animateFloatAsState(
    targetValue = if (startAnim) 1f else 0f,
    animationSpec = tween(durationMillis = 1600, easing = EaseOutCubic)
)

// 2. The title and subtitle text fades in and slides up
val textProgress by animateFloatAsState(
    targetValue = if (startAnim) 1f else 0f,
    animationSpec = tween(durationMillis = 1200, delayMillis = 600, easing = EaseOutQuad)
)
```

Then in the layout, we render the image directly using the `Image` Composable and apply our animated modifiers:

```kotlin
Image(
    painter = painterResource(Res.drawable.landscape_bg),
    contentDescription = "Landscape",
    modifier = Modifier.fillMaxSize(),
    contentScale = ContentScale.Crop
)
```

### Step 4: Control Navigation States in `App.kt`
To transit from the `SplashScreen` to the `HomeScreen`, we define a navigation state engine and utilize Compose's `Crossfade` animation to crossfade between screens:

```kotlin
enum class ScreenState { Splash, Home }

@Composable
fun App() {
    TerraTheme {
        var currentScreen by remember { mutableStateOf(ScreenState.Splash) }

        Crossfade(
            targetState = currentScreen,
            animationSpec = tween(durationMillis = 800)
        ) { screen ->
            when (screen) {
                ScreenState.Splash -> {
                    SplashScreen(onSplashFinished = { currentScreen = ScreenState.Home })
                }
                ScreenState.Home -> {
                    HomeScreen()
                }
            }
        }
    }
}
```

---

## 4. Phase 3: Building Stateful Micro-interactions

Once the user arrives at the `HomeScreen`, we show interactive list items. To make UI cards feel "premium" and alive, we add **micro-interactions** (micro-animations reacting to user inputs).

Here is how the like button counts up and runs a bounce spring animation when clicked:

```kotlin
var isLiked by remember { mutableStateOf(false) }
var likesCount by remember { mutableStateOf(initialLikes) }
var buttonPressed by remember { mutableStateOf(false) }

// Spring physics scale animation for the heart icon
val heartScale by animateFloatAsState(
    targetValue = if (buttonPressed) 1.4f else 1f,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioHighBouncy, // High bounce effect
        stiffness = Spring.StiffnessMedium
    ),
    finishedListener = { buttonPressed = false } // Reset trigger
)
```

In the layout, we scale the Canvas holding the heart icon drawing using `Modifier.scale(heartScale)` and hook up `clickable { ... }`:

```kotlin
Row(
    modifier = Modifier
        .clickable {
            isLiked = !isLiked
            likesCount = if (isLiked) initialLikes + 1 else initialLikes
            buttonPressed = true // Trigger animation
        }
) {
    Canvas(modifier = Modifier.scale(heartScale)) {
        // Draw Heart Path here
    }
    Text(text = likesCount.toString())
}
```

---

## 5. Exercises for Students 🧠

Test your learning by attempting these code challenges:

### Exercise 1: Customize the Native Exit Transition
By default, the native splash screen disappears instantly when Compose draws. You can override this to run a custom exit fade-out animation. 
* *Hint*: In `MainActivity.kt`, fetch the `splashScreen` object from `installSplashScreen()`, and call `setOnExitAnimationListener { viewProvider -> ... }` to fade or scale out the system splash view!

### Exercise 2: Add a Loading Progress Spinner
Modify `SplashScreen.kt` to draw an animating circular loading indicator or a dotted progress bar near the bottom of the screen. Ensure it fades in after the title has finished sliding up.

### Exercise 3: Add Dark Mode Support
Modify `Theme.kt` and `styles.xml` to support standard Android Dark Mode. Setup a dark scheme background `#1E1B18` and test how the colors switch when system dark mode is enabled.