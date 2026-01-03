
package com.popcornpicks.home.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response model for TMDB trending movies API
 */
data class MoviesResponse(
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("results")
    val results: List<Movie>,
    
    @SerializedName("total_pages")
    val totalPages: Int,
    
    @SerializedName("total_results")
    val totalResults: Int
)

/**
 * Movie data model
 */
data class Movie(
    @SerializedName("adult")
    val adult: Boolean,
    
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("original_language")
    val originalLanguage: String,
    
    @SerializedName("original_title")
    val originalTitle: String,
    
    @SerializedName("overview")
    val overview: String,
    
    @SerializedName("poster_path")
    val posterPath: String?,
    
    @SerializedName("media_type")
    val mediaType: String?,
    
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    
    @SerializedName("popularity")
    val popularity: Double,
    
    @SerializedName("release_date")
    val releaseDate: String,
    
    @SerializedName("video")
    val video: Boolean,
    
    @SerializedName("vote_average")
    val voteAverage: Double,
    
    @SerializedName("vote_count")
    val voteCount: Int
) {
    /**
     * Helper method to get full poster URL
     */
    fun getPosterUrl(): String {
        return if (posterPath != null) {
            "https://image.tmdb.org/t/p/w500$posterPath"
        } else {
            ""
        }
    }
    
    /**
     * Helper method to get full backdrop URL
     */
    fun getBackdropUrl(): String {
        return if (backdropPath != null) {
            "https://image.tmdb.org/t/p/w780$backdropPath"
        } else {
            ""
        }
    }
}
