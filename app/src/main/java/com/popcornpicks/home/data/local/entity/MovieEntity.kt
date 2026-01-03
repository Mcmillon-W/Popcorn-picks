
package com.popcornpicks.home.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.popcornpicks.home.data.model.Movie

/**
 * Room Entity for caching Movie data
 */
@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val adult: Boolean,
    val backdropPath: String?,
    val title: String,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val posterPath: String?,
    val mediaType: String?,
    val genreIds: List<Int>,
    val popularity: Double,
    val releaseDate: String,
    val video: Boolean,
    val voteAverage: Double,
    val voteCount: Int,
    val cachedAt: Long = System.currentTimeMillis()
) {
    /**
     * Convert MovieEntity to Movie domain model
     */
    fun toMovie(): Movie {
        return Movie(
            adult = adult,
            backdropPath = backdropPath,
            id = id,
            title = title,
            originalLanguage = originalLanguage,
            originalTitle = originalTitle,
            overview = overview,
            posterPath = posterPath,
            mediaType = mediaType,
            genreIds = genreIds,
            popularity = popularity,
            releaseDate = releaseDate,
            video = video,
            voteAverage = voteAverage,
            voteCount = voteCount
        )
    }
    
    companion object {
        /**
         * Convert Movie domain model to MovieEntity
         */
        fun fromMovie(movie: Movie): MovieEntity {
            return MovieEntity(
                id = movie.id,
                adult = movie.adult,
                backdropPath = movie.backdropPath,
                title = movie.title,
                originalLanguage = movie.originalLanguage,
                originalTitle = movie.originalTitle,
                overview = movie.overview,
                posterPath = movie.posterPath,
                mediaType = movie.mediaType,
                genreIds = movie.genreIds,
                popularity = movie.popularity,
                releaseDate = movie.releaseDate,
                video = movie.video,
                voteAverage = movie.voteAverage,
                voteCount = movie.voteCount
            )
        }
    }
}
