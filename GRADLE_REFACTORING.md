
# Gradle Dependencies Refactoring

## Overview
Refactored Room Database dependencies to use Gradle Version Catalog (libs.versions.toml) for centralized dependency management.

## Changes Made

### 1. Version Catalog (`gradle/libs.versions.toml`)

#### Added Room Version
```toml
[versions]
# ... existing versions
room = "2.6.1"
```

#### Added Room Libraries
```toml
[libraries]
# ... existing libraries

# Room Database
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
```

#### Added Kapt Plugin
```toml
[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
```

### 2. App Build File (`app/build.gradle.kts`)

#### Before (Hardcoded Dependencies)
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

dependencies {
    // ... existing dependencies
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
}
```

#### After (Version Catalog)
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

dependencies {
    // ... existing dependencies
    
    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
}
```

## Benefits

### 1. **Centralized Version Management**
- All dependency versions are defined in one place
- Easy to update versions across the entire project
- Reduces version conflicts

### 2. **Type Safety**
- IDE autocomplete for dependency names
- Compile-time checking of dependency references
- Fewer typos and errors

### 3. **Consistency**
- All dependencies follow the same pattern
- Easier for team members to add new dependencies
- Better code organization

### 4. **Maintenance**
- Version updates require changes in only one file
- Dependencies are grouped logically
- Clear dependency structure

### 5. **Build Performance**
- Gradle can better optimize dependency resolution
- Shared dependency configurations across modules

## How to Use Version Catalog

### Adding a New Dependency

1. **Add version to `gradle/libs.versions.toml`:**
```toml
[versions]
newLibrary = "x.y.z"
```

2. **Add library definition:**
```toml
[libraries]
new-library = { group = "com.example", name = "library", version.ref = "newLibrary" }
```

3. **Use in `app/build.gradle.kts`:**
```kotlin
dependencies {
    implementation(libs.new.library)
}
```

### Adding a New Plugin

1. **Add to plugins section in `gradle/libs.versions.toml`:**
```toml
[plugins]
new-plugin = { id = "com.example.plugin", version.ref = "pluginVersion" }
```

2. **Use in `app/build.gradle.kts`:**
```kotlin
plugins {
    alias(libs.plugins.new.plugin)
}
```

## Next Steps

After refactoring, remember to:

1. **Sync Gradle Files**
   ```
   File → Sync Project with Gradle Files
   ```

2. **Verify Dependencies**
   - Check that all dependencies resolve correctly
   - Ensure Room annotations are recognized

3. **Clean and Rebuild**
   ```
   Build → Clean Project
   Build → Rebuild Project
   ```

## Version Catalog Naming Convention

The version catalog uses specific naming conventions:

- **Dots in library names** become **dashes** in TOML
- **Dashes in TOML** become **dots** in Kotlin DSL

Example:
```toml
# In libs.versions.toml
androidx-room-runtime = { ... }
```

```kotlin
// In build.gradle.kts
implementation(libs.androidx.room.runtime)
```

## Complete Dependency List

All project dependencies now follow the version catalog pattern:

| Category | Dependencies |
|----------|--------------|
| AndroidX Core | core-ktx, appcompat, constraintlayout |
| Material Design | material |
| Navigation | navigation-fragment-ktx, navigation-ui-ktx |
| RecyclerView | recyclerview |
| Lifecycle | runtime-ktx, viewmodel-ktx, livedata-ktx |
| Networking | retrofit, retrofit-converter-gson, okhttp-logging-interceptor |
| Coroutines | kotlinx-coroutines-core, kotlinx-coroutines-android |
| Image Loading | glide, glide-compiler |
| **Database** | **room-runtime, room-ktx, room-compiler** |
| Testing | junit, androidx-junit, androidx-espresso-core |

## Summary

The Gradle refactoring is complete. All Room Database dependencies now use the version catalog approach, providing better maintainability, type safety, and consistency across the project.
