
package com.popcornpicks.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.popcornpicks.home.data.model.Movie
import com.popcornpicks.home.data.repository.MoviesRepository
import com.popcornpicks.home.data.repository.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MoviesViewModel(
    application: Application
) : AndroidViewModel(application) {
    
    private val repository: MoviesRepository = MoviesRepository(application.applicationContext)
    
    private val _result = MutableLiveData<List<Movie>>()
    val result: LiveData<List<Movie>> = _result

    private val _appendResult = MutableLiveData<List<Movie>>()
    val appendResult: LiveData<List<Movie>> = _appendResult
    var category: MOVIE_CATEGORY? = null

    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData for error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Pagination loading state
    private val _isPaginationLoading = MutableLiveData<Boolean>()
    val isPaginationLoading: LiveData<Boolean> = _isPaginationLoading

    // Pagination error state
    private val _paginationError = MutableLiveData<String?>()
    val paginationError: LiveData<String?> = _paginationError
    
    // Pagination state for trending movies
    private var currentPage = 0
    private var totalPages = 1
    private var isPaginatingNow = false

    private var dataFetcherJob: Job? = null

    fun fetchMovies(language: String = "en-US", category: MOVIE_CATEGORY) {
        _isLoading.value = true
        dataFetcherJob?.cancel()
        dataFetcherJob = viewModelScope.launch {
            val result = when (category) {
                MOVIE_CATEGORY.TRENDING -> repository.getTrendingMovies(language)
                else -> repository.getNowPlayingMovies(language)
            }
            when (result) {
                is Result.Success -> {
                    currentPage = result.data.page
                    totalPages = result.data.totalPages
                    _result.value = result.data.results
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _error.value = result.message ?: "Failed to fetch movies"
                    _isLoading.value = false
                }
                else -> {}
            }
        }
    }

    fun loadMoreMovies(language: String = "en-US", category: MOVIE_CATEGORY) {
        // Prevent multiple simultaneous pagination requests
        if (isPaginatingNow || currentPage >= totalPages) {
            return
        }

        isPaginatingNow = true
        _isPaginationLoading.value = true
        _paginationError.value = null

        dataFetcherJob?.cancel()
        dataFetcherJob = viewModelScope.launch {
            val result = when (category) {
                MOVIE_CATEGORY.TRENDING -> repository.getTrendingMovies(language, currentPage+1)
                else -> repository.getNowPlayingMovies(language, currentPage+1)
            }
            when (result) {
                is Result.Success -> {
                    currentPage = result.data.page
                    totalPages = result.data.totalPages
                    _appendResult.value = result.data.results
                    _isPaginationLoading.value = false
                }
                is Result.Error -> {
                    _paginationError.value = result.message ?: "Failed to load more movies"
                    _isPaginationLoading.value = false
                }
                else -> {}
            }
            isPaginatingNow = false
        }
    }

    fun hasMorePages(): Boolean {
        return currentPage < totalPages
    }
}

enum class MOVIE_CATEGORY {
    TRENDING,
    NOW_PLAYING
}
