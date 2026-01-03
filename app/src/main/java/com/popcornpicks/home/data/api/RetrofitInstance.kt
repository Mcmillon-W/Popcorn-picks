
package com.popcornpicks.home.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton object to provide Retrofit instance
 */
object RetrofitInstance {
    
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    
    // Bearer token for TMDB API
    const val API_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0NDcwYWY4M2UyYTdkODgwZTA4NjAxNDk2ZDJlMzJjMyIsIm5iZiI6MTc2NzIzNzkxMi41MTMsInN1YiI6IjY5NTVlOTE4ZGU1MDg4NThjOWNiZTFkNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.rmwjvrQHdHAKPd-o2xc8G0v3pPnkl-w6USXTBQpSt4Q"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val api: TMDBApiService by lazy {
        retrofit.create(TMDBApiService::class.java)
    }
}
