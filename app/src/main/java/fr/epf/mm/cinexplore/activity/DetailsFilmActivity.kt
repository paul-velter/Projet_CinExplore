package fr.epf.mm.cinexplore.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.mm.cinexplore.R
import fr.epf.mm.cinexplore.TmdbApiService
import fr.epf.mm.cinexplore.adapter.FilmListAdapter
import fr.epf.mm.cinexplore.model.Film
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat

class DetailsFilmActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var film_poster: ImageView
    private lateinit var film_title: TextView
    private lateinit var film_rating: TextView
    private lateinit var film_synopsis: TextView
    private lateinit var film_time: TextView
    private lateinit var film_voteCount: TextView
    private lateinit var film_genre: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_film)

        recyclerView = findViewById<RecyclerView>(R.id.list_film_recommendation_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val film = intent.extras?.get("film") as? Film

        film_poster = findViewById<ImageView>(R.id.detail_film_poster_imageView)
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500/${film?.posterPath}?api_key=79bc1a7b946265b5f9dd2e89b2a118b2")
            .into(film_poster)

        film_title = findViewById<TextView>(R.id.detail_film_title_textView)
        film_title.text = film?.title

        film_rating = findViewById<TextView>(R.id.detail_film_rating_textView)
        film_rating.text = DecimalFormat("#.#").format(film?.vote_average)

        film_synopsis = findViewById<TextView>(R.id.detail_film_synopsis_textView)
        film_synopsis.text = film?.overview

        film_genre = findViewById<TextView>(R.id.detail_film_genres_textView)
        film_genre.text = film?.genre?.map { it }?.joinToString(" / ")

        GlobalScope.launch(Dispatchers.Main) {
            val films = film?.genre_id?.first()?.let {
                tmdbService.searchMoviesByGenre("79bc1a7b946265b5f9dd2e89b2a118b2",
                    it,1).results.map {
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