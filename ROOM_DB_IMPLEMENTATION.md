
# Room Database Implementation for Homepage API Caching

## Overview
This document describes the Room Database implementation for caching the Homepage API response. The implementation follows a **cache-first strategy with fallback** - when the API call fails, the app will automatically display cached data if available.

## Architecture

### 1. Database Components

#### **MovieEntity** (`app/src/main/java/com/popcornpicks/home/data/local/entity/MovieEntity.kt`)
- Room entity that represents a movie in the database
- Includes all fields from the API response
- Has a `cachedAt` timestamp to track when data was cached
- Provides conversion methods: `toMovie()` and `fromMovie()`

#### **Converters** (`app/src/main/java/com/popcornpicks/home/data/local/Converters.kt`)
- TypeConverter for List<Int> to String conversion
- Required for storing the `genreIds` list in SQLite

#### **MovieDao** (`app/src/main/java/com/popcornpicks/home/data/local/dao/MovieDao.kt`)
- Data Access Object with methods for database operations:
  - `insertMovies()`: Insert/update movies (REPLACE strategy)
  - `getAllMovies()`: Get all cached movies
  - `getRecentMovies()`: Get movies cached within a time window
  - `deleteAllMovies()`: Clear all cached data
  - `deleteOldMovies()`: Clean up old cache entries
  - `getMovieCount()`: Get total cached movie count

#### **MovieDatabase** (`app/src/main/java/com/popcornpicks/home/data/local/MovieDatabase.kt`)
- Room Database singleton class
- Manages database creation and provides DAO access
- Uses fallback to destructive migration for schema changes

### 2. Repository Layer

#### **MoviesRepository** (`app/src/main/java/com/popcornpicks/home/data/repository/MoviesRepository.kt`)
Enhanced with caching logic:

**Cache Strategy:**
1. **Try API first**: Attempt to fetch data from TMDB API
2. **Cache on success**: If API succeeds, cache the response to database
3. **Fallback on failure**: If API fails, return cached data (if available)
4. **Clean up old data**: Delete cache entries older than 24 hours

**Cache Expiry:**
- Recent cache: 1 hour (data is considered fresh)
- Old cache cleanup: 24 hours (data is deleted)

### 3. ViewModel Layer

#### **MoviesViewModel** (`app/src/main/java/com/popcornpicks/home/viewmodel/MoviesViewModel.kt`)
- Changed from `ViewModel` to `AndroidViewModel` to access application context
- Initializes Repository with context for database access
- No changes needed in existing observers

### 4. Activity Layer

#### **HomeActivity** (`app/src/main/java/com/popcornpicks/home/HomeActivity.kt`)
- Updated ViewModel initialization to use `AndroidViewModelFactory`
- No other changes needed - existing UI code works seamlessly

## How It Works

### Normal Flow (API Success)
```
1. User opens app
2. ViewModel requests data from Repository
3. Repository calls TMDB API
4. API returns movie data
5. Repository caches data to Room DB
6. Repository returns data to ViewModel
7. UI displays movies
```

### Failure Flow (API Fails)
```
1. User opens app (no internet/API error)
2. ViewModel requests data from Repository
3. Repository calls TMDB API
4. API call fails (exception or error response)
5. Repository checks Room DB for cached data
6. If cache exists and is recent (< 1 hour):
   - Repository returns cached data
   - UI displays cached movies with a toast indicating possible stale data
7. If no cache exists:
   - Repository returns error
   - UI shows error message
```

## Testing Instructions

### 1. Build the Project
Open in Android Studio and sync Gradle files:
```
File → Sync Project with Gradle Files
```

### 2. Test Normal Operation
1. Ensure device has internet connection
2. Run the app
3. Observe movies loading from API
4. Check logcat for database operations

### 3. Test Cache Fallback
1. Run app once with internet to cache data
2. Turn off device internet/airplane mode
3. Force close and reopen the app
4. Observe cached movies being displayed
5. Check toast message indicating error (but data still shows)

### 4. Test Cache Expiry
1. Cache data by running app with internet
2. Wait 1+ hours (or modify `CACHE_EXPIRY_TIME` constant to 1 minute for testing)
3. Turn off internet and reopen app
4. Observe that old cache is still used (up to 24 hours)

### 5. Verify Database Operations
Use Android Studio Database Inspector:
```
View → Tool Windows → App Inspection → Database Inspector
```
- Check `movies` table for cached entries
- Verify `cachedAt` timestamps
- Observe insertions/deletions

## Dependencies Added

Added to `app/build.gradle.kts`:
```kotlin
plugins {
    // ... existing plugins
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

## Configuration Options

### Adjust Cache Expiry Time
In `MoviesRepository.kt`:
```kotlin
companion object {
    // Change this value to adjust cache freshness
    private const val CACHE_EXPIRY_TIME = 60 * 60 * 1000L // 1 hour
}
```

### Adjust Old Data Cleanup
In `MoviesRepository.cacheMovies()`:
```kotlin
// Change this value to adjust when old data is deleted
val oldDataTimestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000L) // 24 hours
```

## Database Schema

**Table: `movies`**
```sql
CREATE TABLE movies (
    id INTEGER PRIMARY KEY NOT NULL,
    adult INTEGER NOT NULL,
    backdropPath TEXT,
    title TEXT NOT NULL,
    originalLanguage TEXT NOT NULL,
    originalTitle TEXT NOT NULL,
    overview TEXT NOT NULL,
    posterPath TEXT,
    mediaType TEXT,
    genreIds TEXT NOT NULL,  -- JSON array stored as String
    popularity REAL NOT NULL,
    releaseDate TEXT NOT NULL,
    video INTEGER NOT NULL,
    voteAverage REAL NOT NULL,
    voteCount INTEGER NOT NULL,
    cachedAt INTEGER NOT NULL
)
```

## Benefits

1. **Offline Support**: Users can view previously loaded movies without internet
2. **Better UX**: Instant data display from cache while fetching fresh data
3. **Reduced API Calls**: Less strain on TMDB API quota
4. **Resilience**: App remains functional during network issues
5. **Performance**: Faster load times from local database

## Future Enhancements

1. **Pull to Refresh**: Add swipe-to-refresh to force API fetch
2. **Cache Indicators**: Show UI indicator when displaying cached data
3. **Multiple Categories**: Cache different movie categories separately
4. **Pagination Support**: Cache paginated results with page numbers
5. **Cache Statistics**: Display cache age and freshness in settings

## Troubleshooting

### Issue: Movies not caching
- Check if database is created: Use Database Inspector
- Verify DAO methods are being called: Add logs in Repository
- Check for Room compiler errors in build logs

### Issue: Old data showing after API success
- Verify cache cleanup is working in `deleteOldMovies()`
- Check `REPLACE` strategy is working in `insertMovies()`
- Clear app data and test again

### Issue: App crashes on launch
- Ensure Room dependencies are correctly added
- Verify kapt plugin is applied
- Check for database migration issues
- Review error stack trace for specific Room errors

## Summary

The Room DB implementation provides a robust caching mechanism for the Homepage API response. The cache-first strategy with automatic fallback ensures users always see content, even when the API is unavailable. The implementation is transparent to the UI layer, requiring minimal changes to existing code.
