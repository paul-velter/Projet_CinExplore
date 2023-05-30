package fr.epf.mm.cinexplore.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.mm.cinexplore.adapter.FilmListAdapter
import fr.epf.mm.cinexplore.model.Film
import fr.epf.mm.cinexplore.R
import fr.epf.mm.cinexplore.TmdbApiService
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListPopularFilmActivity : AppCompatActivity() {

    lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_popular_film)

        val recyclerView = findViewById<RecyclerView>(R.id.list_film_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val tmdbService = retrofit.create(TmdbApiService::class.java)

        runBlocking {
            val films = tmdbService.getPopularMovies("79bc1a7b946265b5f9dd2e89b2a118b2", 1).results.map {
                    val filmDetails = tmdbService.searchMoviesById(it.id, "79bc1a7b946265b5f9dd2e89b2a118b2")
                Film(
                    filmDetails.id,
                    filmDetails.poster_path,
                    filmDetails.title,
                    filmDetails.genres.map { it.name },
                    filmDetails.genres.map { it.id },
                    filmDetails.release_date,
                    filmDetails.runtime,
                    filmDetails.overview,
                    filmDetails.vote_average,
                    filmDetails.vote_count
                )
            }
            recyclerView.adapter = FilmListAdapter(this@ListPopularFilmActivity, films)
        }
    }

}