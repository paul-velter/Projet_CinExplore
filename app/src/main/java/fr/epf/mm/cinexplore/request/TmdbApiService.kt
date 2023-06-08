package fr.epf.mm.cinexplore

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("append_to_response") appendToResponse: String = "videos",
        @Query("language") language: String = "fr-FR"
    ): GetTmdbResult

    @GET("search/movie")
    suspend fun searchMoviesByTitle(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("append_to_response") appendToResponse: String = "videos",
        @Query("language") language: String = "fr-FR"
    ): GetTmdbResult

    @GET("discover/movie")
    suspend fun searchMoviesByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int,
        @Query("append_to_response") appendToResponse: String = "videos",
        @Query("language") language: String = "fr-FR"
    ): GetTmdbResult

    @GET("movie/{movie_id}")
    suspend fun searchMoviesById(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendToResponse: String = "videos",
        @Query("language") language: String = "fr-FR"
    ): Film
}

data class GetTmdbResult(val results: List<Film>)
data class Film(
    val id: Int,
    val poster_path: String?,
    val title: String,
    val genres: List<Genre>,
    val release_date: String,
    val runtime: Int,
    val overview: String,
    val vote_average: Float,
    val vote_count: Int,
    val videos:VideoResult?
)

data class Genre(val id: Int, val name: String)

data class VideoResult(
    val results: List<Video>
)
data class Video(
    val iso_639_1: String,
    val iso_3166_1: String,
    val name: String,
    val key: String,
    val site: String,
    val size: Int,
    val type: String,
    val official: Boolean,
    val published_at: String,
    val id: String
)