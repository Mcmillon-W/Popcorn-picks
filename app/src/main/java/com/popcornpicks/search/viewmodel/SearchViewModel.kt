
package com.popcornpicks.search.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popcornpicks.home.data.model.Movie
import com.popcornpicks.home.data.repository.MoviesRepository
import com.popcornpicks.home.data.repository.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel for managing search functionality
 * Provides search results to the UI and handles search logic with debounce
 */
class SearchViewModel(
    private val repository: MoviesRepository = MoviesRepository()
) : ViewModel() {
    
    // Job for managing search debounce
    private var searchJob: Job? = null
    
    // LiveData for search results
    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults
    
    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData for error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // LiveData for empty state
    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty
    
    /**
     * Search for movies by query with debounce
     * Cancels previous search if a new one is triggered within the debounce period
     * @param query Search query string
     * @param language Language code (default: en-US)
     * @param debounceMs Debounce delay in milliseconds (default: 500ms)
     */
    fun searchMovies(query: String, language: String = "en-US", debounceMs: Long = 500L) {
        // Cancel previous search job if exists
        searchJob?.cancel()
        
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isEmpty.value = false
            return
        }
        
        // Start new search job with debounce
        searchJob = viewModelScope.launch {
            // Wait for debounce period
            delay(debounceMs)
            
            _isLoading.value = true
            _error.value = null
            _isEmpty.value = false
            
            when (val result = repository.searchMovies(query, language)) {
                is Result.Success -> {
                    val movies = result.data.results
                    _searchResults.value = movies
                    _isEmpty.value = movies.isEmpty()
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _error.value = result.message ?: "Failed to search movies"
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }
    
    /**
     * Clear search results and cancel any ongoing search
     */
    fun clearSearch() {
        searchJob?.cancel()
        _searchResults.value = emptyList()
        _error.value = null
        _isEmpty.value = false
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
}
