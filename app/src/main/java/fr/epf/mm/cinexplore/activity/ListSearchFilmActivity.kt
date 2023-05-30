package fr.epf.mm.cinexplore.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.epf.mm.cinexplore.adapter.FilmListAdapter
import fr.epf.mm.cinexplore.model.Film
import fr.epf.mm.cinexplore.R
import fr.epf.mm.cinexplore.TmdbApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListSearchFilmActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var qrCodeButton: Button

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data?.getStringExtra(ScanQrCodeActivity.QR_CODE_VALUE)
                Log.d("Resultat : ", data.toString())
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_search_film)

        qrCodeButton = findViewById<Button>(R.id.list_film_QrCode_button)
        initButtonClickListener()

        recyclerView = findViewById<RecyclerView>(R.id.list_film_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        var currentQuery = ""

        searchView = findViewById<SearchView>(R.id.list_film_searchBar_searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                currentQuery = newText
                searchFilmsByTitle(currentQuery)
                return true
            }
        })
    }

    private fun initButtonClickListener() {
        qrCodeButton.setOnClickListener {
            val intent = Intent(this, ScanQrCodeActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun searchFilmsByTitle(query: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val films = tmdbService.searchMoviesByTitle("79bc1a7b946265b5f9dd2e89b2a118b2", query,1).results.map {
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
            recyclerView.adapter = FilmListAdapter(this@ListSearchFilmActivity, films)
        }
    }

    private fun searchFilmsById(id: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val film = tmdbService.searchMoviesById(id, "79bc1a7b946265b5f9dd2e89b2a118b2")
            Log.d("Resultat : ", film.toString())

        }
    }
}
