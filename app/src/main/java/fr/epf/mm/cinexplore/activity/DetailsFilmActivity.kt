package fr.epf.mm.cinexplore.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.mm.cinexplore.R
import fr.epf.mm.cinexplore.TmdbApiService
import fr.epf.mm.cinexplore.adapter.FilmListAdapter
import fr.epf.mm.cinexplore.model.Film
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat

class DetailsFilmActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var filmPoster: ImageView
    private lateinit var filmTitle: TextView
    private lateinit var filmRating: TextView
    private lateinit var filmSynopsis: TextView
    private lateinit var filmTime: TextView
    //private lateinit var filmVoteCount: TextView
    private lateinit var filmReleaseDate: TextView
    private lateinit var filmGenre: TextView
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_film)

        recyclerView = findViewById(R.id.list_film_recommendation_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val film = intent?.extras?.get("film") as? Film

        filmPoster = findViewById(R.id.detail_film_poster_imageView)
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500/${film?.posterPath}?api_key=79bc1a7b946265b5f9dd2e89b2a118b2")
            .into(filmPoster)

        filmTitle = findViewById(R.id.detail_film_title_textView)
        filmTitle.text = film?.title

        filmRating = findViewById(R.id.detail_film_rating_textView)
        filmRating.text = DecimalFormat("#.#").format(film?.vote_average)

        filmSynopsis = findViewById(R.id.detail_film_synopsis_textView)
        filmSynopsis.text = film?.overview

        filmGenre = findViewById(R.id.detail_film_genres_textView)
        filmGenre.text = film?.genre?.joinToString(" / ")

        filmTime = findViewById(R.id.detail_film_time_textView)
        filmTime.text = resources.getString(R.string.film_runtime, film?.runtime)

        filmReleaseDate = findViewById(R.id.detail_film_releaseDate_textView)
        filmReleaseDate.text = film?.release_date

        coroutineScope.launch {
            val films = film?.genre_id?.first()?.let {genreId ->
                tmdbService.searchMoviesByGenre("79bc1a7b946265b5f9dd2e89b2a118b2",
                    genreId,1).results.map { film ->
                    val filmDetails = tmdbService.searchMoviesById(film.id, "79bc1a7b946265b5f9dd2e89b2a118b2")
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
            }
            recyclerView.adapter = films?.let { FilmListAdapter(this@DetailsFilmActivity, it) }
        }

    }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tmdbService = retrofit.create(TmdbApiService::class.java)


}