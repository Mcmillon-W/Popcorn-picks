
package com.popcornpicks.home.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type converters for Room Database
 * Converts complex types to primitive types that can be stored in SQLite
 */
class Converters {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, listType)
    }
}
