
package com.popcornpicks.home.data.repository

import android.content.Context
import com.popcornpicks.details.data.model.MovieDetailsResponse
import com.popcornpicks.home.data.api.RetrofitInstance
import com.popcornpicks.home.data.local.MovieDatabase
import com.popcornpicks.home.data.local.dao.MovieDao
import com.popcornpicks.home.data.local.entity.MovieEntity
import com.popcornpicks.home.data.model.MoviesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository class to handle data operations
 * Acts as a single source of truth for movie data
 * Implements cache-first strategy with fallback to cached data on API failure
 */
class MoviesRepository(val context: Context? = null) {
    
    private val apiService = RetrofitInstance.api
    private var movieDao: MovieDao? = null
    
    companion object {
        // Cache expiry time: 1 hour
        private const val CACHE_EXPIRY_TIME = 60 * 60 * 1000L
    }

    suspend fun getTrendingMovies(language: String = "en-US", page: Int = 1): Result<MoviesResponse> {
        return withContext(Dispatchers.IO) {
            movieDao = context?.let { MovieDatabase.getDatabase(it) }?.movieDao()
            try {
                // Try to fetch from API
                val response = apiService.getTrendingMovies(
                    language = language,
                    page = page,
                    authorization = RetrofitInstance.API_TOKEN
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    
                    // Cache the successful response
                    cacheMovies(apiResponse.results)
                    
                    Result.Success(apiResponse)
                } else {
                    // API call failed, try to return cached data
                    val cachedMovies = getCachedMovies()
                    if (cachedMovies.isNullOrEmpty().not()) {
                        Result.Success(
                            MoviesResponse(
                                page = 1,
                                results = cachedMovies,
                                totalPages = 1,
                                totalResults = cachedMovies.size
                            )
                        )
                    } else {
                        Result.Error(
                            exception = Exception("API Error: ${response.code()}"),
                            message = response.message()
                        )
                    }
                }
            } catch (e: Exception) {
                // Network error or exception, try to return cached data
                val cachedMovies = getCachedMovies()
                if (cachedMovies.isNullOrEmpty().not()) {
                    Result.Success(
                        MoviesResponse(
                            page = 1,
                            results = cachedMovies,
                            totalPages = 1,
                            totalResults = cachedMovies.size
                        )
                    )
                } else {
                    Result.Error(
                        exception = e,
                        message = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }
    
    /**
     * Cache movies to local database
     */
    private suspend fun cacheMovies(movies: List<com.popcornpicks.home.data.model.Movie>) {
        val movieEntities = movies.map { MovieEntity.fromMovie(it) }
        movieDao?.insertMovies(movieEntities)
        
        // Clean up old cached data (older than 24 hours)
        val oldDataTimestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
        movieDao?.deleteOldMovies(oldDataTimestamp)
    }
    
    /**
     * Get cached movies from local database
     * Returns recent movies (cached within the last hour)
     */
    private suspend fun getCachedMovies(): List<com.popcornpicks.home.data.model.Movie>? {
        val recentTimestamp = System.currentTimeMillis() - CACHE_EXPIRY_TIME
        val cachedEntities = movieDao?.getRecentMovies(recentTimestamp)
        return cachedEntities?.map { it.toMovie() }
    }

    suspend fun getNowPlayingMovies(language: String = "en-US", page: Int = 1): Result<MoviesResponse> {
        return withContext(Dispatchers.IO) {
            movieDao = context?.let { MovieDatabase.getDatabase(it) }?.movieDao()
            try {
                // Try to fetch from API
                val response = apiService.getNowPlayingMovies(
                    language = language,
                    page = page,
                    authorization = RetrofitInstance.API_TOKEN
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    Result.Success(apiResponse)
                } else {
                    // API call failed, try to return cached data
                    val cachedMovies = getCachedMovies()
                    if (cachedMovies.isNullOrEmpty().not()) {
                        Result.Success(
                            MoviesResponse(
                                page = 1,
                                results = cachedMovies,
                                totalPages = 1,
                                totalResults = cachedMovies.size
                            )
                        )
                    } else {
                        Result.Error(
                            exception = Exception("API Error: ${response.code()}"),
                            message = response.message()
                        )
                    }
                }
            } catch (e: Exception) {
                // Network error or exception, try to return cached data
                val cachedMovies = getCachedMovies()
                if (cachedMovies.isNullOrEmpty().not()) {
                    Result.Success(
                        MoviesResponse(
                            page = 1,
                            results = cachedMovies,
                            totalPages = 1,
                            totalResults = cachedMovies.size
                        )
                    )
                } else {
                    Result.Error(
                        exception = e,
                        message = e.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    suspend fun searchMovies(
        query: String,
        language: String = "en-US",
        page: Int = 1
    ): Result<MoviesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchMovies(
                    query = query,
                    includeAdult = false,
                    language = language,
                    page = page,
                    authorization = RetrofitInstance.API_TOKEN
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(
                        exception = Exception("API Error: ${response.code()}"),
                        message = response.message()
                    )
                }
            } catch (e: Exception) {
                Result.Error(
                    exception = e,
                    message = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    suspend fun getMovieDetails(
        movieId: Int,
        language: String = "en-US"
    ): Result<MovieDetailsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMovieDetails(
                    movieId = movieId,
                    language = language,
                    authorization = RetrofitInstance.API_TOKEN
                )
                
                if (response.isSuccessful && response.body() != null) {
                    Result.Success(response.body()!!)
                } else {
                    Result.Error(
                        exception = Exception("API Error: ${response.code()}"),
                        message = response.message()
                    )
                }
            } catch (e: Exception) {
                Result.Error(
                    exception = e,
                    message = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}
