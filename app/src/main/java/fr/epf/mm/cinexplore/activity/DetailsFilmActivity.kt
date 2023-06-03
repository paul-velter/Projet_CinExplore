package fr.epf.mm.cinexplore.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.epf.mm.cinexplore.R
import fr.epf.mm.cinexplore.TmdbApiService
import fr.epf.mm.cinexplore.adapter.FilmVerticalAdapter
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
    private lateinit var favoriteButton: ImageButton
    private var listFavoriteFilms = mutableListOf<Film>()
    private lateinit var film: Film
    private var isFavorite = false
    private lateinit var sharedPreferences: SharedPreferences


    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tmdbService = retrofit.create(TmdbApiService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_film)

        recyclerView = findViewById(R.id.list_film_recommendation_recyclerview)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        sharedPreferences = getSharedPreferences("MyFavorites", Context.MODE_PRIVATE)
        listFavoriteFilms = sharedPreferences.getString("favorites", null)?.let { json ->
                Gson().fromJson<List<Film>>(
                    json,
                    object : TypeToken<List<Film>>() {}.type
                ) as MutableList<Film>
            } ?: mutableListOf()

        film = (intent?.extras?.get("film") as? Film)!!

        filmPoster = findViewById(R.id.detail_film_poster_imageView)
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500/${film.posterPath}?api_key=79bc1a7b946265b5f9dd2e89b2a118b2")
            .into(filmPoster)

        filmTitle = findViewById(R.id.detail_film_title_textView)
        filmTitle.text = film.title

        filmRating = findViewById(R.id.detail_film_rating_textView)
        filmRating.text = DecimalFormat("#.#").format(film.vote_average)

        filmSynopsis = findViewById(R.id.detail_film_synopsis_textView)
        filmSynopsis.text = film.overview

        filmGenre = findViewById(R.id.detail_film_genres_textView)
        filmGenre.text = film.genre.joinToString(" / ")

        filmTime = findViewById(R.id.detail_film_time_textView)
        filmTime.text = resources.getString(R.string.film_runtime, film.runtime)

        filmReleaseDate = findViewById(R.id.detail_film_releaseDate_textView)
        filmReleaseDate.text = film.release_date

        favoriteButton = findViewById(R.id.detail_film_favorite_button)
        isFavorite = listFavoriteFilms.contains(film)
        initFavoriteButton()
        initFavoriteButtonClickListener()

        coroutineScope.launch {
            val films = film.genre_id.first().let { genreId ->
                tmdbService.searchMoviesByGenre(
                    "79bc1a7b946265b5f9dd2e89b2a118b2",
                    genreId, 1
                ).results.map { film ->
                    val filmDetails =
                        tmdbService.searchMoviesById(film.id, "79bc1a7b946265b5f9dd2e89b2a118b2")
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
                        filmDetails.vote_count,
                    )
                }
            }
            recyclerView.adapter = FilmVerticalAdapter(this@DetailsFilmActivity, films)
        }

    }

    private fun initFavoriteButton() {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.favorite_pressed_icon)
        } else {
            favoriteButton.setImageResource(R.drawable.favorite_unpressed_icon)
        }
    }

    private fun initFavoriteButtonClickListener() {
        favoriteButton.setOnClickListener {
            if (isFavorite) {
                favoriteButton.setImageResource(R.drawable.favorite_unpressed_icon)
                isFavorite = false
                listFavoriteFilms.remove(film)

            } else {
                favoriteButton.setImageResource(R.drawable.favorite_pressed_icon)
                isFavorite = true
                listFavoriteFilms.add(film)
            }
            updateListFavoritFilms()
        }
    }

    private fun updateListFavoritFilms(){
        val editor = sharedPreferences.edit()
        editor.putString("favorites", Gson().toJson(listFavoriteFilms))
        editor.apply()
    }

}