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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat

class DetailsFilmActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_film)

        recyclerView = findViewById<RecyclerView>(R.id.list_film_recommendation_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val film = intent.extras?.get("film") as? Film

        val film_poster = findViewById<ImageView>(R.id.detail_film_poster_imageView)
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500/${film?.posterPath}?api_key=79bc1a7b946265b5f9dd2e89b2a118b2")
            .into(film_poster)

        val film_title = findViewById<TextView>(R.id.detail_film_title_textView)
        film_title.text = film?.title

        val film_rating = findViewById<TextView>(R.id.detail_film_rating_textView)
        film_rating.text = DecimalFormat("#.#").format(film?.vote_average)

        val film_synopsis = findViewById<TextView>(R.id.detail_film_synopsis_textView)
        film_synopsis.text = film?.owerview

        GlobalScope.launch(Dispatchers.Main) {
            val films = tmdbService.getPopularMovies("79bc1a7b946265b5f9dd2e89b2a118b2",1).results.map {
                Film(
                    it.id,
                    it.poster_path,
                    it.title,
                    it.release_date,
                    it.runtime,
                    it.overview,
                    it.vote_average,
                    it.vote_count
                )
            }
            recyclerView.adapter = FilmListAdapter(this@DetailsFilmActivity, films)
        }

    }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tmdbService = retrofit.create(TmdbApiService::class.java)


}