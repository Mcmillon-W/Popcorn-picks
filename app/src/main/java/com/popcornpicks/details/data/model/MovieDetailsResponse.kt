
package com.popcornpicks.details.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response model for TMDB movie details API
 */
data class MovieDetailsResponse(
    @SerializedName("adult")
    val adult: Boolean,
    
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    
    @SerializedName("belongs_to_collection")
    val belongsToCollection: Collection?,
    
    @SerializedName("budget")
    val budget: Int,
    
    @SerializedName("genres")
    val genres: List<Genre>,
    
    @SerializedName("homepage")
    val homepage: String?,
    
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("imdb_id")
    val imdbId: String?,
    
    @SerializedName("origin_country")
    val originCountry: List<String>,
    
    @SerializedName("original_language")
    val originalLanguage: String,
    
    @SerializedName("original_title")
    val originalTitle: String,
    
    @SerializedName("overview")
    val overview: String,
    
    @SerializedName("popularity")
    val popularity: Double,
    
    @SerializedName("poster_path")
    val posterPath: String?,
    
    @SerializedName("production_companies")
    val productionCompanies: List<ProductionCompany>,
    
    @SerializedName("production_countries")
    val productionCountries: List<ProductionCountry>,
    
    @SerializedName("release_date")
    val releaseDate: String,
    
    @SerializedName("revenue")
    val revenue: Long,
    
    @SerializedName("runtime")
    val runtime: Int?,
    
    @SerializedName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("tagline")
    val tagline: String?,
    
    @SerializedName("title")
    val title: String,
    
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
    
    /**
     * Helper method to get formatted runtime
     */
    fun getFormattedRuntime(): String {
        return runtime?.let {
            val hours = it / 60
            val minutes = it % 60
            if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        } ?: "N/A"
    }
    
    /**
     * Helper method to get genre names as comma-separated string
     */
    fun getGenresString(): String {
        return genres.joinToString(", ") { it.name }
    }
}

data class Collection(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("poster_path")
    val posterPath: String?,
    
    @SerializedName("backdrop_path")
    val backdropPath: String?
)

data class Genre(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String
)

data class ProductionCompany(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("logo_path")
    val logoPath: String?,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("origin_country")
    val originCountry: String
)

data class ProductionCountry(
    @SerializedName("iso_3166_1")
    val iso31661: String,
    
    @SerializedName("name")
    val name: String
)

data class SpokenLanguage(
    @SerializedName("english_name")
    val englishName: String,
    
    @SerializedName("iso_639_1")
    val iso6391: String,
    
    @SerializedName("name")
    val name: String
)
