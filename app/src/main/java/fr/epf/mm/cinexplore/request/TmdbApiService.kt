package fr.epf.mm.cinexplore

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): GetTmdbResult

    @GET("search/movie")
    suspend fun searchMoviesByTitle(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): GetTmdbResult

    @GET("movie/{movie_id}")
    suspend fun searchMoviesById(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String
    ): Film
}
data class GetTmdbResult(val results: List<Film>)
data class Film(val id: Int, val poster_path: String?, val title: String, val release_date: String, val runtime: Int, val overview: String, val vote_average: Float, val vote_count: Int)