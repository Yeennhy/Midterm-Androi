# Accessibility Seminar Demo — Midterm Project

A hands-on Android demo that teaches **mobile accessibility** by letting you toggle between an accessible app and a deliberately broken one — all in real time.

Students see first-hand what happens when content descriptions vanish, touch targets shrink, and screen-readers are silenced. Then, with one button, they restore it. The goal is to *feel* why accessibility matters and to learn the MVVM + UDF patterns used to build it.

---

## Architecture Stack

| Layer | Technology | Why |
|-------|------------|-----|
| **UI** | XML Layouts + ViewBinding | Zero `findViewById` — compile-time safe view references |
| **State** | Kotlin StateFlow | Always has an initial value, works seamlessly with coroutines, testable |
| **Architecture** | MVVM + UDF | Unidirectional data flow: events go up, state comes down |
| **DI** | Manual (ServiceLocator) | No Hilt, Koin, or Dagger — dependencies are explicit and easy to trace |
| **Currency** | `Long` for VND prices | Vietnamese Dong has no decimal subunit — `100000` = 100.000₫ |

### Unidirectional Data Flow (UDF)

```
User taps button
      │
      ▼
  Activity ──event──► ViewModel ──repo──► Repository
      ▲                                      │
      │                                      ▼
      └────── StateFlow<UiState> ◄───────────┘
```

1. The user performs an action (tap, swipe, type).
2. The Activity calls a function on the ViewModel.
3. The ViewModel updates the Repository (or reads from it).
4. The Repository emits a new state via `StateFlow`.
5. The Activity collects the state and re-renders the UI.

**Data never flows backward.** This makes the app predictable, testable, and easy to debug.

---

## Project Structure

```
app/src/main/java/com/example/midterm/
├── data/
│   ├── model/              # Immutable data classes
│   │   ├── Product.kt      # id, name, price (Long), category, imageResId
│   │   ├── CartItem.kt     # product + quantity + selection state
│   │   ├── Voucher.kt      # code, type (PERCENT/SHIPPING), minSpend
│   │   ├── Address.kt      # delivery address
│   │   ├── PaymentMethod.kt
│   │   ├── SeminarTask.kt  # required product + voucher lists
│   │   └── SeminarSession.kt # global state: mode, timer, completion
│   ├── repository/         # Business logic & data access
│   │   ├── CartRepository.kt      # StateFlow-based in-memory cart
│   │   ├── ProductRepository.kt   # Product catalog
│   │   ├── VoucherRepository.kt   # Voucher CRUD + validation
│   │   └── SeminarRepository.kt   # Global session state & toggle
│   ├── source/
│   │   └── LocalMockData.kt       # Predefined products, vouchers, etc.
│   └── ServiceLocator.kt          # Singleton DI container
│
├── ui/
│   ├── base/               # Reusable base classes
│   │   ├── BaseActivity.kt      # Generic ViewBinding inflation
│   │   ├── BaseViewModel.kt     # StateFlow + updateState()
│   │   └── ViewModelFactory.kt  # Manual DI factory (no Hilt)
│   ├── common/
│   │   └── AccessibilityHelper.kt  # Break/Fix extension functions
│   ├── main/               # Seminar control panel (dashboard)
│   ├── cart/               # Shopping cart
│   ├── voucher/            # Discount & shipping vouchers
│   └── checkout/           # Order summary & payment
│
└── utils/
    ├── CurrencyFormatter.kt  # VND formatting: 100000 → "100.000₫"
    └── RandomUtils.kt        # Shuffle / random utilities
```

### Key Files Explained

| File | Role |
|------|------|
| `BaseActivity.kt` | Takes a `(LayoutInflater) -> VB` lambda. Every Activity reduces to `class FooActivity : BaseActivity<FooBinding>(FooBinding::inflate)`. |
| `BaseViewModel.kt` | Holds a `protected val _uiState: MutableStateFlow<T>` and a public `val uiState: StateFlow<T>`. Subclasses call `updateState { it.copy(...) }` to emit new states. |
| `ViewModelFactory.kt` | A single generic factory: `ViewModelProvider(this, ViewModelFactory { MyViewModel(dep1, dep2) })`. |
| `AccessibilityHelper.kt` | Extension functions on `View`: `applyAccessibilitySupport(label, targetSize)` and `removeAccessibilitySupport(shrinkTarget)`. |
| `ServiceLocator.kt` | `object` with `val` properties initialized `by lazy`. Shared across all Activities — the only DI container in the project. |
| `CurrencyFormatter.kt` | `CurrencyFormatter.format(100000)` returns `"100.000₫"`. Always use this rather than manual string concatenation. |

---

## The "Secret Sauce": Global Accessibility Toggle

`SeminarRepository` holds a `SeminarSession` containing an `AccessibilityMode` enum:

```kotlin
enum class AccessibilityMode { ACCESSIBLE, INACCESSIBLE }
```

**ACCESSIBLE mode** — the app is fully usable:
- Content descriptions are set for TalkBack.
- Touch targets are at least 48dp (Material Design minimum).
- Views are focusable and visible to accessibility services.

**INACCESSIBLE mode** — the app is deliberately broken:
- Content descriptions are stripped (`null`).
- Touch targets shrink to 24dp (hard to tap).
- `importantForAccessibility` is set to `NO` (hidden from screen-readers).

### How to toggle

In `MainActivity`, tapping "Toggle Accessibility" calls:

```kotlin
viewModel.toggleAccessibility()
```

This flips the mode in `SeminarRepository`. Every `observeState()` in every Activity picks up the change and re-applies `AccessibilityHelper` functions to the relevant views.

### Using AccessibilityHelper in a new screen

```kotlin
binding.root.applyAccessibilitySupport(label = "My screen description")
// or
binding.root.removeAccessibilitySupport()
```

Call these inside your `observeState()` when the mode changes.

---

## Development Workflow

### How to add a new feature (e.g., "Profile")

```
ui/profile/
├── ProfileUiState.kt
├── ProfileViewModel.kt
└── ProfileActivity.kt
```

**Step 1:** Create `ProfileUiState.kt`

```kotlin
data class ProfileUiState(
    val name: String = "",
    val accessibilityMode: AccessibilityMode = AccessibilityMode.ACCESSIBLE
)
```

**Step 2:** Create `ProfileViewModel.kt`

```kotlin
class ProfileViewModel(
    private val seminarRepository: SeminarRepository
) : BaseViewModel<ProfileUiState>(ProfileUiState()) {

    init {
        viewModelScope.launch {
            seminarRepository.session.collect { session ->
                updateState { it.copy(accessibilityMode = session.accessibilityMode) }
            }
        }
    }
}
```

**Step 3:** Create `ProfileActivity.kt`

```kotlin
class ProfileActivity : BaseActivity<ActivityProfileBinding>(ActivityProfileBinding::inflate) {

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory { ProfileViewModel(ServiceLocator.seminarRepository) }
        )[ProfileViewModel::class.java]

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Render UI + apply accessibility helpers
                }
            }
        }
    }
}
```

**Step 4:** Register the Activity in `AndroidManifest.xml`:

```xml
<activity android:name=".ui.profile.ProfileActivity" android:exported="false" />
```

### Naming conventions

| What | Convention | Example |
|------|-----------|---------|
| Package | `ui.<feature>` | `ui.cart`, `ui.voucher` |
| UiState | `<Feature>UiState` | `CartUiState` |
| ViewModel | `<Feature>ViewModel` | `CartViewModel` |
| Activity | `<Feature>Activity` | `CartActivity` |
| Adapter | `<Feature>Adapter` | `CartAdapter` |
| Layout | `activity_<feature>.xml` | `activity_cart.xml` |
| Item layout | `item_<feature>_<variant>.xml` | `item_cart_product.xml` |
| Repository | `<Feature>Repository` | `CartRepository` |

### VND formatting rule

All prices in models must be `Long`. To display them, use `CurrencyFormatter`:

```kotlin
// Good
textView.text = CurrencyFormatter.format(product.price)  // "120.000₫"

// Bad
textView.text = "${product.price}đ"  // "120000đ" — no separators
```

---

## Git & Collaboration

### Branch naming

```
feature/cart-ui
feature/checkout-logic
fix/crash-on-empty-cart
docs/update-readme
```

### Before pushing

1. Ensure the project builds: `./gradlew assembleDebug`
2. Run lint: `./gradlew lintDebug`
3. Check that no `.idea/` files or `desktop.ini` are staged.

### Commit messages

Use conventional commits:

```
feat: add voucher selection to checkout
fix: resolve crash when cart is empty
docs: update README with new architecture diagram
refactor: extract AccessibilityHelper into common package
```

### What NOT to commit

| File / Folder | Reason |
|---------------|--------|
| `.idea/` | IDE-specific config — varies per developer |
| `local.properties` | Contains SDK path — machine-specific |
| `desktop.ini` | Windows shell file — irrelevant to the project |
| `build/`, `.gradle/` | Generated output — always rebuildable |
| `*.iml` | IntelliJ module file — auto-generated |

These are already in `.gitignore`. If you see them in `git status`, run `git rm --cached <file>` to unstage.

### Pull request workflow

1. Create a feature branch from `main`.
2. Commit your changes with descriptive messages.
3. Push and open a PR.
4. Assign at least one teammate to review.
5. Squash-merge when approved.

---

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+ (bundled with Android Studio)
- Android SDK 35

### Running the app

**Option 1 — Android Studio**

1. Open the project folder.
2. Wait for Gradle Sync to complete.
3. Select a device or emulator running **API 26+** (Android 8.0+).
4. Click **Run**.

**Option 2 — Command line**

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

### Gradle configuration (app/build.gradle.kts)

```kotlin
minSdk = 26
targetSdk = 35
compileSdk = 35

buildFeatures {
    viewBinding = true
}
```

### Dependencies (no DI frameworks)

```kotlin
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
```

That's it. No Hilt, no Koin, no Dagger. Every dependency is injected by hand through `ViewModelFactory`.

---

## License

This project is created for educational purposes as part of the APCS Mobile Device App Development seminar.
