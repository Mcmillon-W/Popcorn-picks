
# Dependency Management - Version Catalog

## Overview
The project now uses Gradle Version Catalogs for centralized dependency management. This approach provides:
- ✅ Single source of truth for versions
- ✅ Type-safe dependency references
- ✅ Easy version updates
- ✅ Consistent dependency declarations

## Structure

### Version Catalog File: `gradle/libs.versions.toml`

The file is organized into three sections:

#### 1. **[versions]** - Version Numbers
All version numbers are centrally defined here:

```toml
[versions]
retrofit = "2.11.0"
okhttp = "4.12.0"
coroutines = "1.8.1"
lifecycle = "2.8.7"
# ... etc
```

#### 2. **[libraries]** - Library Declarations
Libraries reference versions defined above:

```toml
[libraries]
# Retrofit
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }

# OkHttp
okhttp-logging-interceptor = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }

# Coroutines
kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
```

#### 3. **[plugins]** - Plugin Declarations
```toml
[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
```

## Usage in build.gradle.kts

### Naming Convention
Hyphens in `libs.versions.toml` become dots in `build.gradle.kts`:

| libs.versions.toml | build.gradle.kts |
|-------------------|------------------|
| `retrofit-converter-gson` | `libs.retrofit.converter.gson` |
| `okhttp-logging-interceptor` | `libs.okhttp.logging.interceptor` |
| `androidx-lifecycle-runtime-ktx` | `libs.androidx.lifecycle.runtime.ktx` |
| `kotlinx-coroutines-core` | `libs.kotlinx.coroutines.core` |

### Implementation Example

**Before (Hardcoded):**
```kotlin
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}
```

**After (Version Catalog):**
```kotlin
dependencies {
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    
    // OkHttp
    implementation(libs.okhttp.logging.interceptor)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
}
```

## Complete Dependency List

### AndroidX Core
```kotlin
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.appcompat)
implementation(libs.androidx.constraintlayout)
```

### UI Components
```kotlin
implementation(libs.material)
implementation(libs.androidx.recyclerview)
```

### Navigation
```kotlin
implementation(libs.androidx.navigation.fragment.ktx)
implementation(libs.androidx.navigation.ui.ktx)
```

### Lifecycle Components
```kotlin
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.lifecycle.viewmodel.ktx)
implementation(libs.androidx.lifecycle.livedata.ktx)
```

### Networking
```kotlin
implementation(libs.retrofit)
implementation(libs.retrofit.converter.gson)
implementation(libs.okhttp.logging.interceptor)
```

### Coroutines
```kotlin
implementation(libs.kotlinx.coroutines.core)
implementation(libs.kotlinx.coroutines.android)
```

### Testing
```kotlin
testImplementation(libs.junit)
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)
```

## Benefits

### 1. Centralized Version Management
Update a version once in `libs.versions.toml` and all dependencies using it are updated:

```toml
[versions]
lifecycle = "2.8.7"  # Update once, applies to all lifecycle components
```

### 2. Type Safety
IDE provides autocomplete and compile-time validation:
```kotlin
implementation(libs.retrofit)  // ✓ Type-safe
implementation("retrofit")      // ✗ Error-prone
```

### 3. Shared Versions
Multiple libraries can share the same version:
```toml
[versions]
lifecycle = "2.8.7"

[libraries]
androidx-lifecycle-runtime-ktx = { ... version.ref = "lifecycle" }
androidx-lifecycle-viewmodel-ktx = { ... version.ref = "lifecycle" }
androidx-lifecycle-livedata-ktx = { ... version.ref = "lifecycle" }
```

### 4. Easy Updates
When updating versions, you can see all affected dependencies:
```bash
# Before update
./gradlew dependencyUpdates

# Update version in libs.versions.toml
lifecycle = "2.9.0"  # All lifecycle components updated automatically
```

### 5. Consistency Across Modules
In multi-module projects, all modules use the same versions:
```kotlin
// Module 1
implementation(libs.retrofit)

// Module 2
implementation(libs.retrofit)  // Same version automatically
```

## How to Add New Dependencies

1. **Add version to `[versions]` section:**
```toml
[versions]
glide = "4.16.0"
```

2. **Add library to `[libraries]` section:**
```toml
[libraries]
glide = { group = "com.github.bumptech.glide", name = "glide", version.ref = "glide" }
glide-compiler = { group = "com.github.bumptech.glide", name = "compiler", version.ref = "glide" }
```

3. **Use in `build.gradle.kts`:**
```kotlin
dependencies {
    implementation(libs.glide)
    kapt(libs.glide.compiler)
}
```

## Sync After Changes

After modifying `libs.versions.toml`, sync your project:
```bash
./gradlew --refresh-dependencies
```

Or in Android Studio: **File → Sync Project with Gradle Files**

## Best Practices

1. ✅ Group related libraries together (e.g., all lifecycle components)
2. ✅ Use meaningful names for library references
3. ✅ Add comments in toml file for clarity
4. ✅ Keep versions up-to-date
5. ✅ Share common versions across libraries
6. ✅ Organize dependencies by category in build.gradle.kts

## Migration Checklist

- [x] Create/update `gradle/libs.versions.toml`
- [x] Define all versions in `[versions]` section
- [x] Define all libraries in `[libraries]` section
- [x] Replace hardcoded dependencies in `build.gradle.kts`
- [x] Use `libs.` prefix for all dependencies
- [x] Group dependencies by category with comments
- [x] Sync project with Gradle files
- [x] Verify build succeeds
