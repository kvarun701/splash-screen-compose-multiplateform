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
