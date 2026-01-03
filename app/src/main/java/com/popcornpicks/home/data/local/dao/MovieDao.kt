
package com.popcornpicks.home.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.popcornpicks.home.data.local.entity.MovieEntity

/**
 * Data Access Object for Movie operations
 */
@Dao
interface MovieDao {
    
    /**
     * Insert a list of movies into the database
     * If a movie with the same ID already exists, it will be replaced
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)
    
    /**
     * Get all cached movies
     */
    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    suspend fun getAllMovies(): List<MovieEntity>
    
    /**
     * Get movies cached within the last specified time (in milliseconds)
     * @param timestamp Only return movies cached after this timestamp
     */
    @Query("SELECT * FROM movies WHERE cachedAt > :timestamp ORDER BY popularity DESC")
    suspend fun getRecentMovies(timestamp: Long): List<MovieEntity>
    
    /**
     * Delete all movies from the database
     */
    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()
    
    /**
     * Delete movies older than the specified timestamp
     * @param timestamp Delete movies cached before this timestamp
     */
    @Query("DELETE FROM movies WHERE cachedAt < :timestamp")
    suspend fun deleteOldMovies(timestamp: Long)
    
    /**
     * Get the count of cached movies
     */
    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int
}
