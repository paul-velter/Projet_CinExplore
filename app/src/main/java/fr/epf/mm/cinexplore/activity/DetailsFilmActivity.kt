package fr.epf.mm.cinexplore.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.epf.mm.cinexplore.R
import fr.epf.mm.cinexplore.TmdbApiService
import fr.epf.mm.cinexplore.Video
import fr.epf.mm.cinexplore.adapter.FilmVerticalAdapter
import fr.epf.mm.cinexplore.model.Film
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.ParseException
import java.util.*

class DetailsFilmActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var filmPoster: ImageView
    private lateinit var filmTitle: TextView
    private lateinit var filmRating: TextView
    private lateinit var filmSynopsis: TextView
    private lateinit var filmTime: TextView
    private lateinit var filmVoteCount: TextView
    private lateinit var filmReleaseDate: TextView
    private lateinit var filmDisop: TextView
    private lateinit var filmGenre: TextView
    private lateinit var filmGenreRecommandation: TextView
    private lateinit var favoriteButton: ImageButton
    private lateinit var returnButton: ImageButton
    private lateinit var trailerButton: Button
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

    @SuppressLint("MissingInflatedId")
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

        film = intent?.getParcelableExtra("film")!!

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

        filmVoteCount = findViewById(R.id.detail_film_voteCount_textView)
        filmVoteCount.text = film.vote_count.toString()

        filmReleaseDate = findViewById(R.id.detail_film_releaseDate_textView)
        try {
            val releaseDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(film.release_date)
            val formattedDate = SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate)
            filmReleaseDate.text = formattedDate
        } catch (e: ParseException) {
            filmReleaseDate.text = "Date non disponible"
            e.printStackTrace()
        }

        filmGenreRecommandation = findViewById(R.id.detail_film_genre_recommandation)
        filmGenreRecommandation.text = film.genre.first().toString()

        favoriteButton = findViewById(R.id.detail_film_favorite_button)
        isFavorite = listFavoriteFilms.contains(film)
        initFavoriteButton()
        initFavoriteButtonClickListener()

        returnButton = findViewById(R.id.detail_film_return_button)
        initReturnButton()

        trailerButton = findViewById(R.id.detail_film_trailer_button)
        film.trailer_link?.let { initTailerButton(it) }

        filmDisop = findViewById(R.id.detail_film_trailerState_textView)

        if (film.trailer_link.isNullOrEmpty()) {
            initNoTrailer()
        }

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
                        getTrailerLink(filmDetails.videos?.results)
                    )
                }

            }
            recyclerView.adapter = FilmVerticalAdapter(this@DetailsFilmActivity, films)
        }

    }

    private fun initTailerButton(Url: String) {
        trailerButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Url))
            startActivity(intent)
        }
    }

    private fun initReturnButton() {
        returnButton.setOnClickListener {
            onBackPressed()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(returnButton.windowToken, 0)
            finish()
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
            updateListFavoriteFilms()
        }
    }

    private fun updateListFavoriteFilms() {
        val editor = sharedPreferences.edit()
        editor.putString("favorites", Gson().toJson(listFavoriteFilms))
        editor.apply()
    }

    private fun getTrailerLink(videosResult: List<Video>?): String? {
        val trailer = videosResult?.find { it.site == "YouTube" && it.type == "Trailer" }
        return trailer?.let { "https://www.youtube.com/watch?v=${it.key}" }
    }

    private fun initNoTrailer(){
        trailerButton.setText("Indisponible")
        trailerButton.setTextColor(ContextCompat.getColor(this, R.color.gray))
        trailerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.not_play_icon, 0, 0, 0)
        filmDisop.text = "Aucune bande-annonce disponible"
    }

}