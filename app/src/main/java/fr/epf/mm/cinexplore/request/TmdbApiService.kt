package fr.epf.mm.cinexplore

import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): GetTmdbResult
}
data class GetTmdbResult(val results: List<Film>)
data class Film(val id: Int, val title: String, val poster_path: String, val release_date: String, val vote_average: Float)