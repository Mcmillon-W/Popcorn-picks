
package com.popcornpicks.home.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.popcornpicks.home.data.local.dao.MovieDao
import com.popcornpicks.home.data.local.entity.MovieEntity

/**
 * Room Database for caching movie data
 */
@Database(
    entities = [MovieEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MovieDatabase : RoomDatabase() {
    
    abstract fun movieDao(): MovieDao
    
    companion object {
        @Volatile
        private var INSTANCE: MovieDatabase? = null
        
        /**
         * Get singleton instance of the database
         */
        fun getDatabase(context: Context): MovieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    "movie_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
