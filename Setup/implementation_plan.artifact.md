# Implementation Plan - Foundational Architecture & Environment Setup

This plan details the foundation setup of a clean, modular, scalable, and professional MVVM structure for the **Android Accessibility Seminar Demo**. It incorporates user feedback to enforce **Strict ViewBinding**, **Manual Dependency Injection** (to avoid over-engineering), **VND Long Price formatting**, and **educational comments for learning StateFlow / Unidirectional Data Flow (UDF)**.

---

## 1. Context & Architecture Decisions

The existing project is a standard XML/View-based Android project. Layout files are initialized as empty `ConstraintLayout` screens, and UI controllers (Activities, Adapters) exist as empty skeleton classes.

### Core Decisions
1. **XML Views with ViewBinding & MVVM**: We will use XML Views with ViewBinding + MVVM (Model-View-ViewModel). No `findViewById` or synthetic bindings are permitted.
2. **Generic ViewBinding BaseActivity**: `BaseActivity` will be built as an abstract class that handles generic layout inflating via ViewBinding automatically, removing boilerplate.
3. **Manual Dependency Injection (Constructor Injection)**: No complex DI frameworks (like Hilt or Koin) will be introduced. ViewModels will be instantiated via custom `ViewModelProvider.Factory` constructors to keep code easy to learn, understand, and debug.
4. **Package-By-Feature Structure**: Feature packages (`cart`, `voucher`, `checkout`, `main`) keep screen logic separate and prevent git merge conflicts for teams.
5. **Seminar State Manager**: A global state store tracking whether the active session is `ACCESSIBLE` or `INACCESSIBLE`.
6. **VND Currency Formatting**: Products prices will use `Long` values to cleanly handle Vietnamese Dong without decimals (e.g., `100000` L). `CurrencyFormatter.kt` will display this like `100.000đ`.

---

## 2. Proposed Package Structure

```text
com.example.midterm/
├── data/
│   ├── model/               # Immutable domain/data entities
│   │   ├── Product.kt       # ID, name, price (Long), category, imageResId
│   │   ├── CartItem.kt      # Product, quantity, selection state
│   │   ├── UnfriendlyVoucher.kt       # Code, type (discount/shipping), value, minSpend, color
│   │   ├── Address.kt       # Delivery address fields
│   │   ├── PaymentMethod.kt # ID, name, icon res, description
│   │   ├── SeminarTask.kt   # Required shopping list & voucher combinations
│   │   └── SeminarSession.kt# Active timer, user progress, completion state
│   ├── repository/          # Clean repository pattern
│   │   ├── CartRepository.kt
│   │   ├── ProductRepository.kt
│   │   ├── VoucherRepository.kt
│   │   └── SeminarRepository.kt
│   └── source/              # Predefined local mock data sources
│       └── LocalMockData.kt # Standard products (pens/shirts), vouchers, payment methods
├── ui/
│   ├── base/                # Abstract base helpers to minimize boilerplate
│   │   ├── BaseActivity.kt  # Boilerplate for generic ViewBinding inflation
│   │   └── BaseViewModel.kt # Boilerplate for standard StateFlow updates
│   ├── cart/                # Cart Feature
│   │   ├── CartActivity.kt
│   │   ├── CartAdapter.kt
│   │   ├── CartViewModel.kt
│   │   └── CartUiState.kt
│   ├── voucher/             # Voucher Feature
│   │   ├── VoucherActivity.kt
│   │   ├── VoucherAdapter.kt
│   │   ├── VoucherViewModel.kt
│   │   └── VoucherUiState.kt
│   ├── checkout/            # Checkout Feature
│   │   ├── CheckoutActivity.kt
│   │   ├── MethodAdapter.kt
│   │   ├── CheckoutViewModel.kt
│   │   └── CheckoutUiState.kt
│   ├── common/              # Global UI elements / customized components
│   │   └── AccessibilityHelper.kt # Break / Fix helpers to dynamically toggle attributes
│   └── main/                # MainActivity - Control panel for the seminar
│       ├── MainActivity.kt
│       ├── MainViewModel.kt
│       └── MainUiState.kt
└── utils/                   # Reusable helper files
    ├── CurrencyFormatter.kt # Standardizes currency printing (e.g., "100.000đ")
    └── RandomUtils.kt       # For randomized order of vouchers
```

---

## 3. Proposed Changes

### Configuration

#### [build.gradle.kts (app)](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/app/build.gradle.kts)
- Enable `viewBinding`.
- Add standard Lifecycle & ViewModel dependencies (no Hilt, Dagger, or external DI).

```kotlin
android {
    ...
    buildFeatures {
        viewBinding = true
    }
}
dependencies {
    ...
    // Standard Lifecycle & ViewModel components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
}
```

---

### Data Layer

#### [NEW] [Product.kt](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/app/src/main/java/com/example/midterm/data/model/Product.kt)
Immutable entity. Uses `Long` for `price` to seamlessly support Vietnamese Dong (VND) without decimals.

#### [NEW] [CartItem.kt](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/app/src/main/java/com/example/midterm/data/model/CartItem.kt)
Represents a cart row: selected state, Product details, and quantity.

#### [NEW] [Voucher.kt](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/app/src/main/java/com/example/midterm/data/model/Voucher.kt)
Includes code, type (PERCENT vs SHIPPING), minimum spend, and color codes (for color-only inaccessibility demo).

#### [NEW] [LocalMockData.kt](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/app/src/main/java/com/example/midterm/data/source/LocalMockData.kt)
Predefined mock data. E.g., Products (pens, shirts, etc.) matching the seminar challenge requirements.

#### [NEW] [Repositories](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/app/src/main/java/com/example/midterm/data/repository/)
- `ProductRepository`: Provides available catalog items.
- `CartRepository`: Thread-safe, in-memory CRUD for cart data.
- `VoucherRepository`: Provides and validates vouchers.
- `SeminarRepository`: Retains the session details (Mode, Target Task, Attempt details).

---

### UI Layer & MVVM Skeletons

#### [NEW] [BaseActivity.kt](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/app/src/main/java/com/example/midterm/ui/base/BaseActivity.kt)
An abstract base Activity that uses reflection or functional inflating to bind and initialize `ViewBinding` generically.

```kotlin
abstract class BaseActivity<VB : ViewBinding>(
    private val inflate: (LayoutInflater) -> VB
) : AppCompatActivity() {
    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
    }
}
```

#### [NEW] [BaseViewModel.kt](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/app/src/main/java/com/example/midterm/ui/base/BaseViewModel.kt)
Includes detailed, educational comments about Unidirectional Data Flow (UDF), explaining why Kotlin `StateFlow` is preferred over `LiveData` for managing screen states.

#### [NEW] [AccessibilityHelper.kt](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/app/src/main/java/com/example/midterm/ui/common/AccessibilityHelper.kt)
Contains specific dynamic extension functions to "Break" and "Fix" the UI:
- `View.applyAccessibilitySupport(contentDescription: String, touchTargetSizeDp: Int = 48)`:
  - Updates content descriptions for screen-readers.
  - Ensures correct Touch Target bounds (min 48dp).
  - Configures keyboard navigation and focus flags.
- `View.removeAccessibilitySupport()`:
  - Deliberately strips any accessibility content descriptions.
  - Shrinks touch target bounds / paddings.
  - Sets `importantForAccessibility` to `IMPORTANT_FOR_ACCESSIBILITY_NO`.

---

### Team & Git Documentation

#### [NEW] [README.md](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/README.md)
Introduction to the Seminar theme, setup, and instructions.

#### [NEW] [ARCHITECTURE.md](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/ARCHITECTURE.md)
Explains MVVM with strict ViewBinding, StateFlow design, and dynamic layout adaptation.

#### [NEW] [DEVELOPMENT_GUIDELINES.md](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/DEVELOPMENT_GUIDELINES.md)
Contains templates for creating screen flows, ViewModels, and Adapters, along with manual dependency injection examples.

#### [NEW] [CONTRIBUTING.md](file:///D:/Documents/APCS/Year 2/HKIII/Mobile Device App Dev/Seminar/Midterm-Androi/CONTRIBUTING.md)
Team workflow guidelines, PR templates, and conventional git commitments.

---

## 4. Verification Plan

### Automated Verification
1. **Compilation Check**: Run Gradle build to ensure there are no compilation or syntax issues.
   - Command: `gradlew.bat assembleDebug`
2. **Lint Validation**: Verify there are no critical static analysis warnings or errors.
   - Command: `gradlew.bat lintDebug`

### Manual Verification
1. **Inspect Package Hierarchy**: Ensure all planned folders match the target repository structure.
2. **Check Educational Comments**: Confirm all StateFlow definitions and ViewModels contain comprehensive descriptions.
