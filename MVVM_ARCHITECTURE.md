
# MVVM Architecture - TMDB API Integration

## Overview
Complete MVVM (Model-View-ViewModel) architecture implementation for fetching trending movies from The Movie Database (TMDB) API.

## Architecture Components

### 1. Data Layer

#### Data Models
**File:** [`MoviesResponse.kt`](app/src/main/java/com/popcornpicks/home/data/model/MoviesResponse.kt)

```kotlin
data class TrendingMoviesResponse(
    val page: Int,
    val results: List<Movie>,
    val totalPages: Int,
    val totalResults: Int
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String,
    // ... more fields
)
```

**Features:**
- ✅ Complete JSON mapping with `@SerializedName` annotations
- ✅ Helper methods: `getPosterUrl()`, `getBackdropUrl()`
- ✅ Nullable fields for optional data

#### API Service
**File:** [`TMDBApiService.kt`](app/src/main/java/com/popcornpicks/home/data/api/TMDBApiService.kt)

```kotlin
interface TMDBApiService {
    @GET("trending/movie/day")
    suspend fun getTrendingMovies(
        @Query("language") language: String = "en-US",
        @Header("Authorization") authorization: String,
        @Header("accept") accept: String = "application/json"
    ): Response<TrendingMoviesResponse>
}
```

**Features:**
- ✅ Suspend function for coroutines support
- ✅ Bearer token authentication
- ✅ Language parameter support

#### Retrofit Instance
**File:** [`RetrofitInstance.kt`](app/src/main/java/com/popcornpicks/home/data/api/RetrofitInstance.kt)

```kotlin
object RetrofitInstance {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    const val API_TOKEN = "Bearer your_token_here"
    
    val api: TMDBApiService by lazy {
        retrofit.create(TMDBApiService::class.java)
    }
}
```

**Features:**
- ✅ Singleton pattern
- ✅ HTTP logging interceptor for debugging
- ✅ Timeout configuration (30 seconds)
- ✅ GSON converter for JSON parsing

### 2. Repository Layer

**File:** [`MoviesRepository.kt`](app/src/main/java/com/popcornpicks/home/data/repository/MoviesRepository.kt)

```kotlin
class MoviesRepository {
    suspend fun getTrendingMovies(language: String = "en-US"): Result<TrendingMoviesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTrendingMovies(language, API_TOKEN)
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.Error(e, e.message ?: "Unknown error")
            }
        }
    }
}
```

**Features:**
- ✅ Single source of truth for data
- ✅ Error handling with Result wrapper
- ✅ Coroutines with Dispatchers.IO
- ✅ Network response validation

#### Result Wrapper
**File:** [`Result.kt`](app/src/main/java/com/popcornpicks/home/data/repository/Result.kt)

```kotlin
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String?) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

**Purpose:** Type-safe response handling

### 3. ViewModel Layer

**File:** [`MoviesViewModel.kt`](app/src/main/java/com/popcornpicks/home/viewmodel/MoviesViewModel.kt)

```kotlin
class MoviesViewModel(
    private val repository: MoviesRepository = MoviesRepository()
) : ViewModel() {
    
    private val _trendingMovies = MutableLiveData<List<Movie>>()
    val trendingMovies: LiveData<List<Movie>> = _trendingMovies
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun fetchTrendingMovies(language: String = "en-US") {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getTrendingMovies(language)) {
                is Result.Success -> {
                    _trendingMovies.value = result.data.results
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _error.value = result.message
                    _isLoading.value = false
                }
            }
        }
    }
}
```

**Features:**
- ✅ LiveData for reactive UI updates
- ✅ ViewModelScope for lifecycle-aware coroutines
- ✅ Separation of mutable and immutable LiveData
- ✅ Loading and error state management

### 4. View Layer

**File:** [`HomeActivity.kt`](app/src/main/java/com/popcornpicks/home/HomeActivity.kt)

```kotlin
class HomeActivity : AppCompatActivity() {
    private lateinit var viewModel: MoviesViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]
        observeViewModel()
    }
    
    private fun observeViewModel() {
        viewModel.trendingMovies.observe(this) { movies ->
            // Update UI with movies
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            // Show/hide loading indicator
        }
        
        viewModel.error.observe(this) { error ->
            // Show error message
        }
    }
}
```

## Data Flow

```
┌─────────────┐
│    View     │ HomeActivity observes LiveData
│ (Activity)  │
└──────┬──────┘
       │ observes
       ▼
┌─────────────┐
│  ViewModel  │ Manages UI data & state
│   (MVVM)    │
└──────┬──────┘
       │ calls
       ▼
┌─────────────┐
│ Repository  │ Single source of truth
│   (Data)    │
└──────┬──────┘
       │ uses
       ▼
┌─────────────┐
│ API Service │ Retrofit interface
│  (Network)  │
└──────┬──────┘
       │ fetches
       ▼
┌─────────────┐
│  TMDB API   │ External API
└─────────────┘
```

## Configuration

### Required Dependencies
```kotlin
// Retrofit
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
```

### Required Permissions
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## API Details

**Base URL:** `https://api.themoviedb.org/3/`

**Endpoint:** `GET /trending/movie/day`

**Headers:**
- `Authorization: Bearer {your_token}`
- `accept: application/json`

**Response:** JSON with trending movies list

## Usage Example

```kotlin
// In Activity
val viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]

// Observe data
viewModel.trendingMovies.observe(this) { movies ->
    movies.forEach { movie ->
        println("${movie.title} - Rating: ${movie.voteAverage}")
    }
}

// Fetch movies
viewModel.fetchTrendingMovies() // Automatically called in init

// Retry on error
viewModel.retry()
```

## Benefits of This Architecture

1. **Separation of Concerns**
   - Data layer handles networking
   - Repository manages data operations
   - ViewModel prepares data for UI
   - View displays data

2. **Testability**
   - Each layer can be tested independently
   - Repository can be mocked for ViewModel tests
   - API service can be mocked for Repository tests

3. **Lifecycle Awareness**
   - ViewModel survives configuration changes
   - LiveData respects lifecycle
   - No memory leaks

4. **Reactive UI**
   - LiveData automatically updates UI
   - No manual update calls needed
   - Type-safe observations

5. **Error Handling**
   - Centralized error handling in Repository
   - Result wrapper for type-safe responses
   - Error propagation to UI

## Integration with RecyclerView

The fetched movies are automatically converted to CardItems and displayed in the horizontal RecyclerView setup:

```kotlin
viewModel.trendingMovies.observe(this) { movies ->
    val cardItems = movies.map { movie ->
        CardItem(
            title = movie.title,
            imageUrl = movie.getPosterUrl()
        )
    }
    
    // Display in horizontal list
    val items = listOf(
        ViewItem(ViewTypes.HORIZONTAL_LIST,
            HorizontalListSection("Trending", cardItems))
    )
    adapter.setItems(items)
}
```

## Future Enhancements

- [ ] Add image loading library (Glide/Coil)
- [ ] Implement caching with Room database
- [ ] Add pagination for large datasets
- [ ] Implement pull-to-refresh
- [ ] Add search functionality
- [ ] Implement movie details screen
- [ ] Add favorites feature
- [ ] Implement offline mode
