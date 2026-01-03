
package com.popcornpicks.details.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popcornpicks.details.data.model.MovieDetailsResponse
import com.popcornpicks.home.data.repository.MoviesRepository
import com.popcornpicks.home.data.repository.Result
import kotlinx.coroutines.launch

/**
 * ViewModel for managing movie details data
 * Provides data to the UI and handles business logic for movie details
 */
class DetailsViewModel(
    private val repository: MoviesRepository = MoviesRepository()
) : ViewModel() {
    
    // LiveData for movie details
    private val _movieDetails = MutableLiveData<MovieDetailsResponse>()
    val movieDetails: LiveData<MovieDetailsResponse> = _movieDetails
    
    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData for error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Fetch movie details from repository
     * @param movieId The movie ID to fetch details for
     * @param language Language code (default: en-US)
     */
    fun fetchMovieDetails(movieId: Int, language: String = "en-US") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            when (val result = repository.getMovieDetails(movieId, language)) {
                is Result.Success -> {
                    _movieDetails.value = result.data
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _error.value = result.message ?: "Failed to fetch movie details"
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Retry fetching movie details
     */
    fun retry(movieId: Int) {
        fetchMovieDetails(movieId)
    }
}
