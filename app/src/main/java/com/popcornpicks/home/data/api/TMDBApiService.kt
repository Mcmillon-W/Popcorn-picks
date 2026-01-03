
package com.popcornpicks.home.data.api

import com.popcornpicks.details.data.model.MovieDetailsResponse
import com.popcornpicks.home.data.model.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApiService {

    @GET("trending/movie/day")
    suspend fun getTrendingMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Header("Authorization") authorization: String,
        @Header("accept") accept: String = "application/json"
    ): Response<MoviesResponse>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Header("Authorization") authorization: String,
        @Header("accept") accept: String = "application/json"
    ): Response<MoviesResponse>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Header("Authorization") authorization: String,
        @Header("accept") accept: String = "application/json"
    ): Response<MoviesResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US",
        @Header("Authorization") authorization: String,
        @Header("accept") accept: String = "application/json"
    ): Response<MovieDetailsResponse>
}
