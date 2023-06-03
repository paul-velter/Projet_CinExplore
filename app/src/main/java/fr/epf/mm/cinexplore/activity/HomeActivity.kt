package fr.epf.mm.cinexplore.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.epf.mm.cinexplore.adapter.FilmListAdapter
import fr.epf.mm.cinexplore.model.Film
import fr.epf.mm.cinexplore.R
import fr.epf.mm.cinexplore.TmdbApiService
import fr.epf.mm.cinexplore.adapter.FilmVerticalAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var popularRecyclerView: RecyclerView
    private lateinit var favoriteRecyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var qrCodeButton: ImageButton
    private lateinit var sharedPreferences: SharedPreferences
    private var listFavoriteFilms = mutableListOf<Film>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data?.getStringExtra(ScanQrCodeActivity.QR_CODE_VALUE)
                Log.d("Result : ", data.toString())
                if (!data.isNullOrEmpty()) {
                    val id = data.toIntOrNull()
                    if (id != null) {
                        searchFilmsById(id)
                    }
                }
            }
        }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tmdbService = retrofit.create(TmdbApiService::class.java)

    private val sharedPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "favorites") {
            getFavoriteFilms()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        qrCodeButton = findViewById(R.id.list_film_QrCode_button)
        initButtonClickListener()

        searchRecyclerView = findViewById(R.id.search_film_recyclerView)
        searchRecyclerView.layoutManager = LinearLayoutManager(this)

        popularRecyclerView = findViewById(R.id.popular_film_recyclerView)
        popularRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        favoriteRecyclerView = findViewById(R.id.favorite_film_recyclerView)
        favoriteRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        sharedPreferences = getSharedPreferences("MyFavorites", Context.MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        getPopularFilms()
        getFavoriteFilms()

        searchView = findViewById(R.id.list_film_searchBar_searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchFilmsByTitle(newText)
                return true
            }
        })
    }

    private fun getFavoriteFilms() {
        listFavoriteFilms = sharedPreferences.getString("favorites", null)?.let { json ->
            Gson().fromJson<List<Film>>(
                json,
                object : TypeToken<List<Film>>() {}.type
            ) as MutableList<Film>
        } ?: mutableListOf()
        favoriteRecyclerView.adapter = FilmVerticalAdapter(this@HomeActivity, listFavoriteFilms)

    }

    private fun initButtonClickListener() {
        qrCodeButton.setOnClickListener {
            val intent = Intent(this, ScanQrCodeActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun getPopularFilms(){
        coroutineScope.launch {
            val films = tmdbService.getPopularMovies("79bc1a7b946265b5f9dd2e89b2a118b2",1).results.map { film ->
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
            popularRecyclerView.adapter = FilmVerticalAdapter(this@HomeActivity, films)
        }
    }
    private fun searchFilmsByTitle(query: String) {
        coroutineScope.launch {
            val films = tmdbService.searchMoviesByTitle("79bc1a7b946265b5f9dd2e89b2a118b2", query,1).results.map { film ->
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
            searchRecyclerView.adapter = FilmListAdapter(this@HomeActivity, films)
        }
    }

    private fun searchFilmsById(id: Int) {
        coroutineScope.launch {
            val film = tmdbService.searchMoviesById(id, "79bc1a7b946265b5f9dd2e89b2a118b2")
            val filmScanned = Film(
                film.id,
                film.poster_path,
                film.title,
                film.genres.map { it.name },
                film.genres.map { it.id },
                film.release_date,
                film.runtime,
                film.overview,
                film.vote_average,
                film.vote_count
            )
            val intent = Intent(this@HomeActivity, DetailsFilmActivity::class.java)
            intent.putExtra("film", filmScanned)
            startActivity(intent)
        }

    }
}

